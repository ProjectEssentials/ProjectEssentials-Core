package com.mairwunnx.projectessentials.core.api.v1.localization

import com.beust.klaxon.JsonReader
import com.mairwunnx.projectessentials.core.api.v1.INITIAL_FALLBACK_LANGUAGE
import com.mairwunnx.projectessentials.core.api.v1.LOCALIZATION_PROCESSOR_INDEX
import com.mairwunnx.projectessentials.core.api.v1.SETTING_LOC_FALLBACK_LANG
import com.mairwunnx.projectessentials.core.api.v1.configuration.ConfigurationAPI
import com.mairwunnx.projectessentials.core.api.v1.events.EmptyEventData
import com.mairwunnx.projectessentials.core.api.v1.events.ModuleEventAPI
import com.mairwunnx.projectessentials.core.api.v1.events.internal.LocalizationEventData
import com.mairwunnx.projectessentials.core.api.v1.events.internal.ModuleCoreEventType.*
import com.mairwunnx.projectessentials.core.api.v1.helpers.getResourceAsFile
import com.mairwunnx.projectessentials.core.api.v1.processor.IProcessor
import com.mairwunnx.projectessentials.core.impl.configurations.GeneralConfiguration
import org.apache.logging.log4j.LogManager
import org.apache.logging.log4j.MarkerManager
import java.io.StringReader
import java.util.*
import kotlin.system.measureTimeMillis

@OptIn(ExperimentalUnsignedTypes::class)
internal object LocalizationProcessor : IProcessor {
    private val logger = LogManager.getLogger()
    private val marker = MarkerManager.Log4jMarker("LOCALIZATION PROCESSOR")

    private val generalConfiguration by lazy {
        ConfigurationAPI.getConfigurationByName<GeneralConfiguration>("general")
    }

    val localizations = mutableListOf<Localization>()
    var localizationsData: HashMap<String, MutableList<HashMap<String, String>>> =
        hashMapOf()

    var fallbackLanguage = INITIAL_FALLBACK_LANGUAGE

    override val processorLoadIndex = LOCALIZATION_PROCESSOR_INDEX
    override val processorName = "localization"

    override fun initialize() {
        ModuleEventAPI.fire(
            OnLocalizationInitializing, EmptyEventData()
        )
        logger.info(marker, "Initializing localization processor")

        fallbackLanguage = generalConfiguration.getStringOrDefault(
            SETTING_LOC_FALLBACK_LANG, "en_us"
        )
    }

    override fun process() {
        localizations.forEach { localization ->
            localization.sources.forEach { source ->
                val localizationName = source.substring(
                    source.lastIndexOf("/")
                ).drop(1).dropLast(5)

                ModuleEventAPI.fire(
                    OnLocalizationProcessing, LocalizationEventData(localization)
                )
                logger.debug(
                    marker,
                    "Starting processing localization `$localizationName` by ${localization.sourceName} with ${localization.sourceClass.name}"
                )

                val ms = measureTimeMillis {
                    val json = getResourceAsFile(
                        localization.sourceClass.classLoader, source
                    )?.readText() ?: throw KotlinNullPointerException()

                    JsonReader(StringReader(json)).use { reader ->
                        reader.beginObject {
                            while (reader.hasNext()) {
                                val key = reader.nextName()
                                val value = reader.nextString()

                                localizationsData[localizationName]?.add(
                                    hashMapOf(Pair(key, value))
                                ) ?: localizationsData.set(
                                    localizationName, mutableListOf(hashMapOf(Pair(key, value)))
                                )
                            }
                        }
                    }
                }

                ModuleEventAPI.fire(
                    OnLocalizationProcessed, LocalizationEventData(localization)
                )
                logger.debug(
                    marker,
                    "Processing localization `$localizationName` by ${localization.sourceName} finished with ${ms}ms"
                )
            }
        }
    }

    override fun postProcess() = Unit
}
