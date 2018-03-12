package com.springboot.common.annoation;

import java.lang.annotation.*;

/**
* @description: 
* @author: hanbei
* @date 创建时间：2018年3月12日
* @version 1.0 
*/

@Target({ ElementType.METHOD})
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface ServiceType {

    /**
     * 征信源公司名称，如百度
     * @return
     */
    String  mainName() default "unknown";

    /**
     * 征信源母公司旗下子公司或子项目名称，如百度-磐石
     * @return
     */
    String  childName() default "unknown";
}
