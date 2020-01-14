package com.mairwunnx.projectessentials.core

import net.minecraftforge.fml.common.Mod

@Suppress("unused")
@Mod("project_essentials_core")
internal class EntryPoint : EssBase() {
    init {
        modInstance = this
        modVersion = "1.14.4-1.0.3.1"
        logBaseInfo()
        validateForgeVersion()
    }

    internal companion object {
        internal lateinit var modInstance: EntryPoint
    }
}
