package com.endersuite.libcore.config.json;

/**
 * @author TheRealDomm
 * @since 10.05.2021
 */
public interface Configuration {

    /**
     * obtains the current config version
     * @return the current version
     */
    double getConfigVersion();

    /**
     * will set a config version
     * @param version to set
     */
    void setConfigVersion(double version);

}
