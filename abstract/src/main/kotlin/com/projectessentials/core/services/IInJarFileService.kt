package com.projectessentials.core.services

import java.io.File

/**
 * In jar file service contract, for interacting with
 * files in jar contained.
 *
 * @since 3.0.0.
 */
interface IInJarFileService {
    /**
     * Returns resource as file by specified class loader
     * and file path.
     *
     * @param loader class loader of class jar owner which
     * contains [path] resource.
     * @param path path to the file in jar.
     * @return [File] class instance if class exist, otherwise
     * null.
     * @since 3.0.0.
     */
    fun getResourceAsFile(loader: ClassLoader, path: String): File?
}
