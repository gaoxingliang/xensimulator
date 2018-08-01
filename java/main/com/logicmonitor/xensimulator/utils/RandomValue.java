package com.logicmonitor.xensimulator.utils;

import java.lang.annotation.*;

/**
 * indicates this method will return some random value
 */
@Documented
@Target({ElementType.METHOD})
@Retention(RetentionPolicy.RUNTIME)
public @interface RandomValue {

    /**
     * those attrs will be random
     * @return
     */
    String attrs();
}
