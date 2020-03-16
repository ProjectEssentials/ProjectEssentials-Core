package com.mairwunnx.projectessentials.core.module

@Target(AnnotationTarget.CLASS)
annotation class Module(
    val name: String,
    val version: String
)
