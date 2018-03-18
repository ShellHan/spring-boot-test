package com.springboot.service;

import com.dianping.cat.message.Transaction;
import com.mobanker.feature.provider.business.feature.FeatureBigDataBusiness;
import com.mobanker.feature.provider.common.constant.FeatureConstants;
import com.mobanker.feature.provider.contract.constants.ProductTypeEnum;
import com.mobanker.feature.provider.contract.req.CommonFields;
import com.mobanker.feature.provider.contract.req.FeatureReq;
import com.mobanker.feature.provider.contract.rsp.FeatureError;
import com.mobanker.feature.provider.contract.rsp.FeatureNull;
import com.mobanker.feature.provider.model.dto.CalcFeatureReq;
import com.mobanker.feature.provider.model.dto.CalcFeatureRsp;
import com.mobanker.feature.provider.model.dto.FeatureResult;
import com.mobanker.feature.provider.model.entity.FeatureEntity;
import com.mobanker.feature.provider.model.entity.FeaturePropertyEntity;
import com.mobanker.feature.provider.business.feature.FeatureBuild;
import com.mobanker.feature.provider.service.factory.FeatureFactory;
import com.mobanker.feature.provider.service.task.FeatureTask;
import com.mobanker.framework.constant.Constants;
import com.mobanker.framework.contract.dto.ResponseEntityDto;
import com.mobanker.framework.tracking.EE;
import com.mobanker.framework.tracking.EETransaction;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.*;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

/**
 * Copyright @ 2017QIANLONG. All right reserved. Class Name :
 * com.mobanker.feature.provider.service Description :指标计算的实现方法 Author :
 * liuyafei Date : 2017-8-30 16:21
 */
@Service
public class FeatureServiceImpl extends FeatureAbsService {
	private Logger logger = LoggerFactory.getLogger(getClass());

	@Resource
	private ThreadPoolTaskExecutor threadPoolTaskExecutor;
	@Resource
	private FeaturePropertyService featurePropertyService;

	@Resource
	private FeatureBigDataBusiness featureBigDataBusiness;

	/**
	 * 发送kafka开关
	 */
	private boolean SEND_FLAG = true;
	/**
	 * 总线程池的超时时间是60秒，每个dubbo接口的超时时间也不会超过60秒
	 */
	private static final int threadTimeout = 50;
	@Override
	@EETransaction(type = "service", name = "FeatureServiceImpl.obtainFeature")
	public List<CalcFeatureRsp> obtainFeature(FeatureReq featureReq, CommonFields commonFields) {
		logger.debug("enter obtainFeature method,featureReq:{},commonFields:{}", featureReq, commonFields);

		int userDataSourceFlag = 1;
		if (ProductTypeEnum.xczx.getProductType().equals(featureReq.getProductType())){
			userDataSourceFlag = 2;
		}

		// 多线程调用，拆分多个服务
		Set<String> featureFields = featureReq.getFeatureFields();
		List<String> featureList = new ArrayList<>(featureFields);
		List<FeatureResult> oriResult = featurePropertyService.getFeatureResultListByFiledName(featureList);

		//根据'用户数据来源'标识筛选指标信息
		List<FeatureResult> result = new ArrayList<>();
		for (FeatureResult f : oriResult){
			if (f.getUserDataSource() == userDataSourceFlag){
				result.add(f);
			}
		}
		List<CalcFeatureRsp> errorList = checkFeatureEntity(result, commonFields);

		if (errorList.size() > 0) {
			return errorList;
		}
		List calcList = new ArrayList();
		Map<String, List<FeatureResult>> map = result.stream()
				.filter(featureResult -> StringUtils.isNotBlank(featureResult.getClassName())
						&& featureResult.getStatus() == 1 && "已开发".equals(featureResult.getDevelopStatus()))
				.collect(Collectors.groupingBy(FeatureResult::getClassName));
		logger.info("指标 --->>> resultList:{}, Map : {}", result, map);
		// 多线程调用
		List<Future<List<CalcFeatureRsp>>> results = new ArrayList<Future<List<CalcFeatureRsp>>>();
		for (Map.Entry<String, List<FeatureResult>> entry : map.entrySet()) {
			CalcFeatureReq calcFeatureReq = new CalcFeatureReq();
			calcFeatureReq.setCommonFields(commonFields);
			calcFeatureReq.setFeatureParams(featureReq.getFeatureParams());
			calcFeatureReq.setProductType(featureReq.getProductType());
			calcFeatureReq.setClassName(entry.getKey());
			calcFeatureReq.setFeatureResult(entry.getValue());
			results.add(threadPoolTaskExecutor.submit(new FeatureTask(calcFeatureReq)));
		}

		for (Future<List<CalcFeatureRsp>> res : results) {
			try {
				calcList.addAll(res.get(threadTimeout,TimeUnit.SECONDS));
			} catch (Exception e) {
				logger.error(String.valueOf(e));
			}
		}
		logger.debug("exit obtainFeature method,list:{}", calcList);
		return calcList;
	}

	public List<CalcFeatureRsp> checkFeatureEntity(List<FeatureResult> result, CommonFields commonFields) {
		List<CalcFeatureRsp> errList = new ArrayList();

		if (result == null || result.size() == 0) {
			CalcFeatureRsp rsp = new CalcFeatureRsp();
			rsp.setResult(new ResponseEntityDto<>(Constants.System.FAIL, FeatureConstants.MESSAGE.FEATURE_NO_EXIST, FeatureConstants.MESSAGE.FEATURE_NO_EXIST_MSG, null));
			rsp.setCalcTime(0L);
			rsp.setFeatureFile("error");
			errList.add(rsp);
			return errList;
		}
		for (FeatureResult featureResult : result) {
			CalcFeatureRsp rsp = new CalcFeatureRsp();
			rsp.setCalcTime(0L);
			rsp.setFeatureFile(featureResult.getFiledName());
			if (featureResult.getStatus() != 1) {
				rsp.setResult(new ResponseEntityDto<>(Constants.System.FAIL, FeatureConstants.MESSAGE.FEATURE_IS_LOCK, featureResult.getFiledName() + FeatureConstants.MESSAGE.FEATURE_IS_LOCK_MSG, null));
				errList.add(rsp);
			} else if (!"已开发".equals(featureResult.getDevelopStatus())) {
				rsp.setResult(new ResponseEntityDto<>(Constants.System.FAIL, FeatureConstants.MESSAGE.FEATURE_IS_DEVELOPING, featureResult.getFiledName() + FeatureConstants.MESSAGE.FEATURE_IS_DEVELOPING_MSG, null));
				errList.add(rsp);
			} else if (StringUtils.isBlank(featureResult.getClassName())) {
				rsp.setResult(new ResponseEntityDto<>(Constants.System.FAIL, FeatureConstants.MESSAGE.FEATURE_PROPERTY_NO_EXITS, featureResult.getFiledName() + FeatureConstants.MESSAGE.FEATURE_PROPERTY_NO_EXITS_MSG, null));
				errList.add(rsp);
			}
		}
		return errList;
	}

	@Override
	public List<CalcFeatureRsp> calcFeature(CalcFeatureReq calcFeatureReq) {
		List<CalcFeatureRsp> rspList = new ArrayList<CalcFeatureRsp>();
		logger.debug("enter calcFeature method,calcFeatureReq:{}", calcFeatureReq);
		long startTime = System.currentTimeMillis();
		FeatureFactory featureFactory = new FeatureFactory();
		FeatureBuild featureBuild = featureFactory.getFeatureBulid(calcFeatureReq.getFeatureResult().get(0).getSort());
		Map<String, String> reqParams = calcFeatureReq.getFeatureParams();
		reqParams.put("productType", calcFeatureReq.getProductType());
		ResponseEntityDto<Map<String, Object>> ret = featureBuild.callFeature(calcFeatureReq.getClassName(), reqParams);
		long endTime = System.currentTimeMillis();
		long calcTime = endTime - startTime;
		for (FeatureResult featureResult : calcFeatureReq.getFeatureResult()) {
			CalcFeatureRsp rsp = new CalcFeatureRsp();
			rsp.setFeatureFile(featureResult.getFiledName());
			rsp.setFeatureParams(calcFeatureReq.getFeatureParams());
			ResponseEntityDto<Map<String, Object>> featureRet = new ResponseEntityDto<Map<String, Object>>();
			featureRet.setStatus(Constants.System.OK);
			featureRet.setData(checkFeatureResult(ret,featureResult));
			rsp.setResult(featureRet);
			rsp.setCalcTime(calcTime);
			calcFeatureReq.setStoreType(featureResult.getStoreType());
			rspList.add(rsp);
			if (SEND_FLAG) {
				featureBigDataBusiness.sendBigData(calcFeatureReq, rsp);
			}
		}
		logger.debug("exit calcFeature method,ret:{}", rspList);

		return rspList;

	}
	
	
	private Map<String, Object> checkFeatureResult(ResponseEntityDto<Map<String, Object>> ret,FeatureResult featureResult){
		Map<String, Object> featureMap = new HashMap<>();

		if (featureResult.getIsFake().equals("是")){
			featureMap.put(featureResult.getFiledName(), featureResult.getReferValue());

		} else if (!Constants.System.OK.equals(ret.getStatus())) {
			
			FeatureError fe = new FeatureError();
			fe.setError(ret.getError());
			fe.setMsg(ret.getMsg());
			fe.setDetial(ret.getCode());
			fe.setIsArray(featureResult.getIsArray());
			fe.setReturnType(featureResult.getReturnType());
			featureMap.put(featureResult.getFiledName(), fe);

		} else if (Constants.System.OK.equals(ret.getStatus())
				&& (ret.getData().get(featureResult.getFiledName()) == null
						|| ret.getData().get(featureResult.getFiledName()).toString().equals("-9999"))) {
			FeatureNull fe = new FeatureNull();
			fe.setMsg("查询数据为空");
			fe.setIsArray(featureResult.getIsArray());
			fe.setReturnType(featureResult.getReturnType());
			featureMap.put(featureResult.getFiledName(), fe);
		}  else {
			featureMap.put(featureResult.getFiledName(), ret.getData().get(featureResult.getFiledName()));
		}
		
		return featureMap;
	}
	

}
