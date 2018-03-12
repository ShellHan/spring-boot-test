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
public @interface ServiceCache {

    /**
     * 缓存key
     * @return
     */
    String  keyName() default "";

    /**
     * 缓存值
     * @return
     */
    String  keyValue() default "";

    /**
     * 是否收费 FEE-收费，NOTFEE-免费
     * @return
     */
    String  fee() default "FEE";

    /**
     * 缓存时间
     * @return
     */
    int  cacheTime()  default  3600*24*3;


}
