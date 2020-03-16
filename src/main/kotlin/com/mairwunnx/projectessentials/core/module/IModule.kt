package com.mairwunnx.projectessentials.core.module

interface IModule {
    fun init()
    fun reload() = Unit
}
