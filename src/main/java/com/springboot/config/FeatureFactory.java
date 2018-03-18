package com.springboot.config;

import com.mobanker.feature.provider.business.feature.FeatureBuild;
import com.mobanker.feature.provider.common.utils.SpringContextsUtil;

/**
 * Copyright @ 2017QIANLONG.
 * All right reserved.
 * Class Name : com.mobanker.feature.provider.service.factory
 * Description :
 * Author : liuyafei
 * Date : 2017-9-4 16:10
 */
public class FeatureFactory {

    public FeatureBuild getFeatureBulid(int sort) {
        FeatureBuild featureBuild = null;
        switch (sort) {
            case 1:
                featureBuild = (FeatureBuild) SpringContextsUtil.getBean("featureBulidUser");
                break;
            case 2:
                featureBuild = (FeatureBuild) SpringContextsUtil.getBean("featureBulidDevice");
                break;
            case 3:
                featureBuild = (FeatureBuild) SpringContextsUtil.getBean("featureBulidExpense");
                break;
            case 4:
                featureBuild = (FeatureBuild) SpringContextsUtil.getBean("featureBulidAction");
                break;
            case 5:
                featureBuild = (FeatureBuild) SpringContextsUtil.getBean("featureBulidCredit");
                break;
            default:
                featureBuild = null;
        }
        return featureBuild;
    }
}
