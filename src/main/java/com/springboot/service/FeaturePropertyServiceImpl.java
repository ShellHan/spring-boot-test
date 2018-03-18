package com.springboot.service;

import com.mobanker.feature.provider.business.feature.FeatureEntityBusiness;
import com.mobanker.feature.provider.business.feature.FeaturePropertyEntityBusiness;
import com.mobanker.feature.provider.business.feature.ModuleEntityBusiness;
import com.mobanker.feature.provider.model.dto.FeatureResult;
import com.mobanker.feature.provider.model.entity.FeatureEntity;
import com.mobanker.feature.provider.model.entity.FeaturePropertyEntity;
import com.mobanker.feature.provider.model.entity.ModuleEntity;
import com.mobanker.framework.tracking.EETransaction;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.List;

/**
 * Copyright @ 2017QIANLONG.
 * All right reserved.
 * Class Name : com.mobanker.feature.provider.service
 * Description :
 * Author : liuyafei
 * Date : 2017-9-4 14:25
 */
@Service
public class FeaturePropertyServiceImpl implements  FeaturePropertyService{

    private Logger logger = LoggerFactory.getLogger(getClass());


    @Resource
    private FeatureEntityBusiness featureEntityBusiness;
    @Resource
    private FeaturePropertyEntityBusiness featurePropertyEntityBusiness;
    @Resource
    private ModuleEntityBusiness moduleEntityBusiness;

    @Override
    @EETransaction(type = "service",name="FeaturePropertyServiceImpl.getFeatureByFiledName")
    public FeatureEntity getFeatureByFiledName(String filedName) {

        return  featureEntityBusiness.getFeatureByFiledName(filedName);
    }

    @Override
    @EETransaction(type = "service",name="FeaturePropertyServiceImpl.getFeaturePropertyByFeatureId")
    public FeaturePropertyEntity getFeaturePropertyByFeatureId(Long FeatureId) {
        return featurePropertyEntityBusiness.getFeaturePropertyByFeatureId(FeatureId);
    }

    @Override
    @EETransaction(type = "service",name="FeaturePropertyServiceImpl.getModuleByFiledName")
    public ModuleEntity getModuleByFiledName(String filedName) {
        return moduleEntityBusiness.getModuleByFiledName(filedName);
    }

    @Override
    @EETransaction(type = "service",name="FeaturePropertyServiceImpl.getFeatureListByModuleId")
    public List<FeatureEntity> getFeatureListByModuleId(Long moduleId) {
        return featureEntityBusiness.getFeatureListByModuleId(moduleId);
    }
    @Override
    @EETransaction(type = "service",name="FeaturePropertyServiceImpl.getFeatureResultListByFiledName")
    public List<FeatureResult> getFeatureResultListByFiledName(List filedNameList){

        return featureEntityBusiness.getFeatureResultListByFiledName(filedNameList);
    }

}
