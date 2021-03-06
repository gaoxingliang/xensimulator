package com.logicmonitor.xensimulator.utils;

import java.lang.annotation.*;

/**
 * Indicates this is a remote api which will be used to change the
 *  simulator internal status or variables
 */
@Documented
@Target({ElementType.METHOD})
@Retention(RetentionPolicy.RUNTIME)
public @interface SimAPI {
}
