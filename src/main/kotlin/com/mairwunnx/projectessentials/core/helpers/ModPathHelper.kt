@file:Suppress("unused")

package com.mairwunnx.projectessentials.core.helpers

import com.mairwunnx.projectessentials.core.enums.ForgeRootPaths
import net.minecraftforge.api.distmarker.Dist
import net.minecraftforge.fml.DistExecutor
import java.io.File

/**
 * Minecraft config folder absolutely path.
 */
val CONFIG_FOLDER = root + File.separator + "config"
/**
 * Project Essentials mod config folder.
 */
val MOD_CONFIG_FOLDER = CONFIG_FOLDER + File.separator + "ProjectEssentials"

internal val root: String
    get() {
        var rootPath = ""
        DistExecutor.runWhenOn(Dist.CLIENT) {
            Runnable {
                rootPath =
                    getRootPath(
                        ForgeRootPaths.CLIENT
                    )
            }
        }
        DistExecutor.runWhenOn(Dist.DEDICATED_SERVER) {
            Runnable {
                rootPath =
                    getRootPath(
                        ForgeRootPaths.SERVER
                    )
            }
        }
        return rootPath
    }
