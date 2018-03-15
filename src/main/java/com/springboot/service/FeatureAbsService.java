package com.springboot.service;

import com.mobanker.feature.provider.business.feature.FeatureEntityBusiness;
import com.mobanker.feature.provider.contract.req.CommonFields;
import com.mobanker.feature.provider.contract.req.FeatureReq;
import com.mobanker.feature.provider.model.dto.CalcFeatureRsp;
import com.mobanker.framework.contract.dto.ResponseEntityDto;
import com.mobanker.framework.tracking.EETransaction;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutionException;

/**
 * Copyright @ 2017QIANLONG.
 * All right reserved.
 * Class Name : com.mobanker.feature.provider.service
 * Description :抽象类，统一处理埋cat和数据快照等一起处理的问题
 * Author : liuyafei
 * Date : 2017-8-30 16:16
 */
@Service
public abstract class FeatureAbsService implements FeatureService{
    private Logger logger = LoggerFactory.getLogger(getClass());
    /**
     * @description:中间加一层进行处理
     * @author: liuyafei
     * @date 创建时间：2017-8-31 20:26
     * @version 1.0
     * @param [featureReq, commonFields]
     * @return java.util.List<com.mobanker.feature.provider.model.dto.CalcFeatureRsp>
     */
    @Override
    @EETransaction(type = "service",name="FeatureAbsService.getFeatures")
    public List<CalcFeatureRsp> getFeatures(FeatureReq featureReq, CommonFields commonFields){
        logger.debug("enter getFeatures--->>>featureReq:{};commonFields:{}",featureReq,commonFields);
        List<CalcFeatureRsp> result = obtainFeature(featureReq,commonFields);
        logger.debug("exit getFeatures<<<---featureReq:{};commonFields:{};return:{}",featureReq,commonFields,result);
        return result;
    }
    /**
     * @description:抽象方法，在实现类中实现
     * @author: liuyafei
     * @date 创建时间：2017-8-31 20:26
     * @version 1.0
     * @param [featureReq, commonFields]
     * @return java.util.List<com.mobanker.feature.provider.model.dto.CalcFeatureRsp>
     */
    public abstract List<CalcFeatureRsp> obtainFeature(FeatureReq featureReq, CommonFields commonFields) ;

}
