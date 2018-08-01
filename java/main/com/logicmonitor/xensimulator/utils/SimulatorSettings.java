package com.logicmonitor.xensimulator.utils;

public class SimulatorSettings {
    private SimulatorSettings(){}

    // whether we will ignore ssl error for a client
    public static boolean ignoreSSL = true;

    /**
     * where the keystore file is.
     * Which will be relative with the class loader root path
     */
    public static String keystoreFile = "/xensim.keystore";

    /**
     * keystore password
     */
    public static char[] keystorePass = "123456".toCharArray();

}
