package com.springboot.task;

import com.dianping.cat.message.Transaction;
import com.mobanker.feature.provider.common.utils.SpringContextsUtil;
import com.mobanker.feature.provider.model.dto.CalcFeatureReq;
import com.mobanker.feature.provider.model.dto.CalcFeatureRsp;
import com.mobanker.feature.provider.service.FeatureService;
import com.mobanker.feature.provider.service.FeatureServiceImpl;
import com.mobanker.framework.contract.dto.ResponseEntityDto;
import com.mobanker.framework.tracking.EE;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.Callable;

/**
 * Copyright @ 2017QIANLONG.
 * All right reserved.
 * Class Name : com.mobanker.feature.provider.service.task
 * Description :指标任务调用
 * Author : liuyafei
 * Date : 2017-8-30 21:24
 */
public class FeatureTask implements Callable<List<CalcFeatureRsp>> {
    private Logger logger = LoggerFactory.getLogger(getClass());

    /**
     * 指标请求信息
     */
    private CalcFeatureReq calcFeatureReq;


    public FeatureTask(CalcFeatureReq calcFeatureReq) {
       this.calcFeatureReq=calcFeatureReq;
    }
    public FeatureTask() {
        super();
        // TODO Auto-generated constructor stub
    }
    @Override
    public List<CalcFeatureRsp> call() throws Exception {
    	Transaction trans = EE.newTransaction("Thread", calcFeatureReq.getClassName());
    	List<CalcFeatureRsp> list = new ArrayList();
    	try {
    		FeatureService featureService = (FeatureService) SpringContextsUtil.getBean("featureServiceImpl");		
    		trans.setStatus(Transaction.SUCCESS);
    		list= featureService.calcFeature(calcFeatureReq);
		} catch (Exception e) {
			logger.error(e.getMessage(), e);
			trans.setStatus(e);
			EE.logError(e);
		} finally {
			trans.complete();
		}
    	
        
        return list;
    }
}
