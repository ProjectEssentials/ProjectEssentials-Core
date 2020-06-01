@file:Suppress("unused")

package com.mairwunnx.projectessentials.core.impl.configurations

import com.mairwunnx.projectessentials.core.api.v1.configuration.IConfiguration
import com.mairwunnx.projectessentials.core.api.v1.extensions.empty
import com.mairwunnx.projectessentials.core.api.v1.helpers.projectConfigDirectory
import org.apache.logging.log4j.LogManager
import java.io.File
import java.io.FileInputStream
import java.io.FileOutputStream
import java.io.IOException
import java.util.*

object GeneralConfiguration : IConfiguration<Properties> {
    private val logger = LogManager.getLogger()
    private val properties = Properties()

    override val name = "general"
    override val version = 1
    override val configuration = take()
    override val path = projectConfigDirectory + File.separator + "general.properties"

    override fun load() = try {
        File(path).parentFile.mkdirs()
        File(path).createNewFile()
        FileInputStream(path).use { input ->
            properties.load(input)
        }
    } catch (ex: IOException) {
        logger.error("An error occurred while loading general configuration.", ex)
    }

    override fun save() = try {
        logger.info("Saving configuration `${name}`")
        FileOutputStream(path).use { output ->
            properties.store(output, null)
        }
    } catch (ex: IOException) {
        logger.error("An error occurred while saving general configuration.", ex)
    }

    override fun take() = properties

    @Synchronized
    fun getDoubleOrDefault(key: String, value: Double): Double {
        val property = properties[key]
        if (property == null) put(key, value.toString())
        return properties[key]?.toString()?.toDouble() ?: value
    }

    @Synchronized
    fun getDouble(key: String) = properties[key].toString().toDouble()

    @Synchronized
    fun getFloatOrDefault(key: String, value: Float): Float {
        val property = properties[key]
        if (property == null) put(key, value.toString())
        return properties[key]?.toString()?.toFloat() ?: value
    }

    @Synchronized
    fun getFloatInt(key: String) = properties[key].toString().toFloat()

    @Synchronized
    fun getIntOrDefault(key: String, value: Int): Int {
        val property = properties[key]
        if (property == null) put(key, value.toString())
        return properties[key]?.toString()?.toInt() ?: value
    }

    @Synchronized
    fun getInt(key: String) = properties[key].toString().toInt()

    @Synchronized
    fun getBoolOrDefault(key: String, value: Boolean): Boolean {
        val property = properties[key]
        if (property == null) put(key, value.toString())
        return properties[key]?.toString()?.toBoolean() ?: value
    }

    @Synchronized
    fun getBool(key: String) = properties[key].toString().toBoolean()

    @Synchronized
    fun getStringOrDefault(key: String, value: String): String {
        val property = properties[key]
        if (property == null) put(key, value)
        return properties[key]?.toString() ?: value
    }

    @Synchronized
    fun getString(key: String) = properties[key].toString()

    @Synchronized
    fun getList(
        key: String, defaultValue: ArrayList<String> = arrayListOf()
    ): List<String> {
        val rawList = getStringOrDefault(key, String.empty)
        if (rawList.isBlank()) return defaultValue.also { putList(key, defaultValue) }
        return rawList.trim('[', ']').replace("\"", "").let {
            val list = mutableListOf<String>()
            if (it.contains(',') && it.count() > 3) {
                it.split(',').forEach { value -> list.add(value.trim()) }
            } else if (it.count() >= 1) {
                list.add(it.trim())
            }
            return@let list
        }
    }

    @Synchronized
    fun putList(key: String, value: List<String>) = properties.set(key, value.toString())

    @Synchronized
    fun put(key: String, value: String) = properties.set(key, value)
}
