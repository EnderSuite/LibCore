package com.endersuite.libcore.config.json;

/**
 * The standard configuration specification.
 * Every configuration holds a version used to determine whether the configuration
 * needs to be upgraded.
 *
 * @author TheRealDomm
 * @since 10.05.2021
 */
public interface IConfiguration {

    /**
     * Returns the current version of the configuration.
     *
     * @return The version
     */
    double getConfigVersion();

    /**
     * Sets the current version of the configuration.
     *
     * @param version
     *              The new version
     */
    void setConfigVersion(double version);

}
