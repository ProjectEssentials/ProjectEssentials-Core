package com.mairwunnx.projectessentials.core.helpers

import com.mairwunnx.projectessentials.core.enums.ForgeRootPaths
import net.minecraft.client.Minecraft
import java.io.File

private val clientRootDir by lazy {
    Minecraft.getInstance().gameDir.absolutePath
}
private val serverRootDir by lazy {
    File(".").absolutePath
}

/**
 * @return absolutely path to configuration root dir.
 * @since 1.14.4-1.0.0.0
 */
fun getRootPath(pathType: ForgeRootPaths): String {
    return when (pathType) {
        ForgeRootPaths.CLIENT -> clientRootDir
        ForgeRootPaths.SERVER -> serverRootDir
    }
}
