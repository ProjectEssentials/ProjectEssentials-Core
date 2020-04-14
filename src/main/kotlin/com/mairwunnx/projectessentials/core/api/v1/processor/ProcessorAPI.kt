@file:Suppress("unused")

package com.mairwunnx.projectessentials.core.api.v1.processor

import com.mairwunnx.projectessentials.core.api.v1.events.ModuleEventAPI
import com.mairwunnx.projectessentials.core.api.v1.events.internal.ModuleCoreEventType.*
import com.mairwunnx.projectessentials.core.api.v1.events.internal.ProcessorEventData
import org.apache.logging.log4j.LogManager
import org.apache.logging.log4j.MarkerManager

/**
 * Processor API, for interacting with processors.
 * @since Mod: 2.0.0-SNAPSHOT.1_MC-1.14.4, API: 1.0.0
 */
@OptIn(ExperimentalUnsignedTypes::class)
object ProcessorAPI {
    private var processed = false
    private var postProcessed = false
    private val logger = LogManager.getLogger()
    private val marker = MarkerManager.Log4jMarker("PROCESSOR")
    private val processors = mutableListOf<IProcessor>()

    /**
     * Register new processor.
     *
     * NOTE: Call it ONLY in `setup` event.
     * @param processor processor class instance.
     * @throws ProcessorIndexDuplicateException
     * @since Mod: 2.0.0-SNAPSHOT.1_MC-1.14.4, API: 1.0.0
     */
    fun register(processor: IProcessor) {
        ModuleEventAPI.fire(OnProcessorRegister, ProcessorEventData(processor))
        logger.debug(
            marker,
            "Registering processor ${processor.processorName} with load index ${processor.processorLoadIndex}"
        )
        getAllProcessors().find { it.processorLoadIndex == processor.processorLoadIndex }?.let {
            throw ProcessorIndexDuplicateException(
                "Processor with same index ${processor.processorLoadIndex} already exist."
            )
        }
        processors.add(processor)
        ModuleEventAPI.fire(OnProcessorAfterRegister, ProcessorEventData(processor))
    }

    /**
     * @return last available loading index for processor.
     * @since Mod: 2.0.0-SNAPSHOT.1_MC-1.14.4, API: 1.0.0
     */
    fun getAvailableLastIndex() = processors.map { it.processorLoadIndex }.max()?.plus(1u) ?: 0u

    /**
     * @return true if processors already processed.
     * @since Mod: 2.0.0-SNAPSHOT.1_MC-1.14.4, API: 1.0.0
     */
    fun getIsProcessed() = processed

    /**
     * @return true if processors already post processed.
     * @since Mod: 2.0.0-SNAPSHOT.1_MC-1.14.4, API: 1.0.0
     */
    fun getIsPostProcessed() = postProcessed

    /**
     * @return all registered processors.
     * @since Mod: 2.0.0-SNAPSHOT.1_MC-1.14.4, API: 1.0.0
     */
    fun getAllProcessors() = processors

    /**
     * @param name processor name.
     * @throws ProcessorNotFoundException
     * @return processor by name. If processor with
     * name not exist then throws `ProcessorNotFoundException`.
     * @since Mod: 2.0.0-SNAPSHOT.1_MC-1.14.4, API: 1.0.0
     */
    fun getProcessorByName(name: String) = getAllProcessors().find {
        it.processorName.toLowerCase() == name.toLowerCase()
    } ?: throw ProcessorNotFoundException(
        "Processor with name $name not found."
    )

    /**
     * Processing processors, initializing processors.
     * It method calling in `enqueueIMC` event.
     * @since Mod: 2.0.0-SNAPSHOT.1_MC-1.14.4, API: 1.0.0
     */
    internal fun processProcessors() {
        if (!processed) {
            processed = true

            processors.sortedWith(compareBy {
                it.processorLoadIndex
            }).forEach {
                logger.debug(
                    marker,
                    "Starting work on processor ${it.processorName}, index: ${it.processorLoadIndex}"
                )

                try {
                    if (!isPostponedInit(it, "initialize")) {
                        ModuleEventAPI.fire(OnProcessorInitializing, ProcessorEventData(it))
                        logger.debug(marker, "Initializing processor ${it.processorName}")
                        it.initialize()
                        ModuleEventAPI.fire(OnProcessorAfterInitializing, ProcessorEventData(it))
                    }
                    if (!isPostponedInit(it, "process")) {
                        ModuleEventAPI.fire(OnProcessorProcessing, ProcessorEventData(it))
                        logger.debug(marker, "Processing processor ${it.processorName}")
                        it.process()
                        ModuleEventAPI.fire(OnProcessorAfterProcessing, ProcessorEventData(it))
                    }
                } catch (_: NotImplementedError) {
                    logger.error(
                        marker,
                        "Processing or initialize method (`process` or `initialize`) for ${it.processorName} not implemented. Processing skipped."
                    )
                }
            }
        }
    }

    /**
     * Processing processors, initializing processors.
     * It method calling in `processIMC` event.
     * @since Mod: 2.0.0-SNAPSHOT.1_MC-1.14.4, API: 1.0.0
     */
    internal fun postProcessProcessors() {
        if (!postProcessed) {
            postProcessed = true

            processors.sortedWith(compareBy {
                it.processorLoadIndex
            }).forEach {
                logger.debug(
                    marker,
                    "Post processing processor ${it.processorName}, index: ${it.processorLoadIndex}"
                )

                try {
                    if (!isPostponedInit(it, "postProcess")) {
                        ModuleEventAPI.fire(OnProcessorPostProcessing, ProcessorEventData(it))
                        it.postProcess()
                        ModuleEventAPI.fire(OnProcessorAfterPostProcessing, ProcessorEventData(it))
                    }
                } catch (_: NotImplementedError) {
                    logger.error(
                        marker,
                        "Post processing method (`postProcess`) for ${it.processorName} not implemented. Post processing skipped."
                    )
                }
            }
        }
    }

    private fun isPostponedInit(
        processorClass: IProcessor, methodName: String
    ) = processorClass.javaClass.declaredMethods.find {
        it.name == methodName
    }?.let { return it.isAnnotationPresent(PostponedInit::class.java) } ?: false
}
