package com.springboot.service;

import com.mobanker.feature.provider.contract.req.CommonFields;
import com.mobanker.feature.provider.contract.req.FeatureReq;
import com.mobanker.feature.provider.model.dto.CalcFeatureReq;
import com.mobanker.feature.provider.model.dto.CalcFeatureRsp;
import com.mobanker.framework.contract.dto.ResponseEntityDto;

import java.util.List;
import java.util.Map;

/**
 * Copyright @ 2017QIANLONG.
 * All right reserved.
 * Class Name : com.mobanker.feature.provider.service.dubbo
 * Description :
 * Author : liuyafei
 * Date : 2017-8-30 16:10
 */
public interface FeatureService {

    /**
     * @description:计算指标
     * @author: liuyafei
     * @date 创建时间：2017-8-30 20:47
     * @version 1.0
     * @param [featureReq, commonFields]
     * @return java.util.List<java.util.Map<java.lang.String,com.mobanker.framework.contract.dto.ResponseEntityDto<java.util.Map<java.lang.String,java.lang.Object>>>>
     */
    public List<CalcFeatureRsp> getFeatures(FeatureReq featureReq, CommonFields commonFields);
    /**
     * @description:计算单个指标的值
     * @author: liuyafei
     * @date 创建时间：2017-8-31 14:21
     * @version 1.0
     * @param [calcFeatureReq]
     * @return com.mobanker.feature.provider.model.dto.CalcFeatureRsp
     */
    public List<CalcFeatureRsp> calcFeature(CalcFeatureReq calcFeatureReq);
}
