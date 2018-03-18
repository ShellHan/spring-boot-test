package com.springboot.service;

import com.mobanker.feature.provider.model.dto.FeatureResult;
import com.mobanker.feature.provider.model.entity.FeatureEntity;
import com.mobanker.feature.provider.model.entity.FeaturePropertyEntity;
import com.mobanker.feature.provider.model.entity.ModuleEntity;

import java.util.List;

/**
 * Copyright @ 2017QIANLONG.
 * All right reserved.
 * Class Name : com.mobanker.feature.provider.service
 * Description :管理所有指标从数据库中查询数据
 * Author : liuyafei
 * Date : 2017-9-4 14:25
 */
public interface FeaturePropertyService {

    /**
     * @description: 根据字段名字来获取指标
     * @author: liuyafei
     * @date 创建时间：2017-9-4 14:31
     * @version 1.0
     * @param [filedName]
     * @return com.mobanker.feature.provider.model.entity.FeatureEntity
     */
    public FeatureEntity getFeatureByFiledName(String filedName);
    /**
     * @description: 根据指标id来获取指标配置
     * @author: liuyafei
     * @date 创建时间：2017-9-4 14:33
     * @version 1.0
     * @param [FeatureId]
     * @return com.mobanker.feature.provider.model.entity.FeaturePropertyEntity
     */
    public FeaturePropertyEntity getFeaturePropertyByFeatureId(Long FeatureId);
    /**
     * @description: 根据模块名字来获取模块信息
     * @author: liuyafei
     * @date 创建时间：2017-9-4 14:33
     * @version 1.0
     * @param [filedName]
     * @return com.mobanker.feature.provider.model.entity.ModuleEntity
     */
    public ModuleEntity getModuleByFiledName(String filedName);
    /**
     * @description:根据模块id来获取指标列表
     * @author: liuyafei
     * @date 创建时间：2017-9-4 14:34
     * @version 1.0
     * @param [moduleId]
     * @return List<FeatureEntity>
     */
    public List<FeatureEntity> getFeatureListByModuleId(Long moduleId);

    public List<FeatureResult> getFeatureResultListByFiledName(List filedNameList);
}
