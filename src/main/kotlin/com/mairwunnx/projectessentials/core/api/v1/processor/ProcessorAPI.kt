@file:Suppress("unused")

package com.mairwunnx.projectessentials.core.api.v1.processor

import org.apache.logging.log4j.LogManager
import org.apache.logging.log4j.MarkerManager

/**
 * Processor API, for interacting with processors.
 * @since Mod: 1.14.4-2.0.0, API: 1.0.0
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
     * @since Mod: 1.14.4-2.0.0, API: 1.0.0
     */
    fun register(processor: IProcessor) {
        logger.info(
            marker,
            "Registering processor ${processor.processorName} with load index ${processor.processorLoadIndex}"
        )

        getAllProcessors().forEach {
            if (it.processorLoadIndex == processor.processorLoadIndex) {
                throw ProcessorIndexDuplicateException(
                    "Processor with same index ${processor.processorLoadIndex} already exist."
                )
            }
        }
        processors.add(processor)
    }

    /**
     * @return last available loading index for processor.
     * @since Mod: 1.14.4-2.0.0, API: 1.0.0
     */
    fun getAvailableLastIndex(): UInt {
        var lastIndex = 0u
        processors.forEach {
            val index = it.processorLoadIndex
            if (index > lastIndex) lastIndex = index
        }
        return lastIndex++
    }

    /**
     * @return true if processors already processed.
     * @since Mod: 1.14.4-2.0.0, API: 1.0.0
     */
    fun getIsProcessed() = processed

    /**
     * @return true if processors already post processed.
     * @since Mod: 1.14.4-2.0.0, API: 1.0.0
     */
    fun getIsPostProcessed() = postProcessed

    /**
     * @return all registered processors.
     * @since Mod: 1.14.4-2.0.0, API: 1.0.0
     */
    fun getAllProcessors() = processors

    /**
     * @param name processor name.
     * @throws ProcessorNotFoundException
     * @return processor by name. If processor with
     * name not exist then throws `ProcessorNotFoundException`.
     * @since Mod: 1.14.4-2.0.0, API: 1.0.0
     */
    fun getProcessorByName(name: String): IProcessor {
        return getAllProcessors().find {
            it.processorName.toLowerCase() == name.toLowerCase()
        } ?: throw ProcessorNotFoundException(
            "Processor with name $name not found."
        )
    }

    /**
     * Processing processors, initializing processors.
     * It method calling in `enqueueIMC` event.
     * @since Mod: 1.14.4-2.0.0, API: 1.0.0
     */
    internal fun processProcessors() {
        if (!processed) {
            processors.sortedWith(compareBy {
                it.processorLoadIndex
            }).forEach {
                logger.info(
                    marker,
                    "Starting work on processor ${it.processorName}, index: ${it.processorLoadIndex}"
                )

                try {
                    logger.info(marker, "Initializing processor ${it.processorName}")
                    it.initialize()
                    logger.info(marker, "Processing processor ${it.processorName}")
                    it.process()
                } catch (_: NotImplementedError) {
                    logger.error(
                        marker,
                        "Processing or initialize method (`process` or `initialize`) for ${it.processorName} not implemented. Processing skipped."
                    )
                }
            }

            processed = true
        }
    }

    /**
     * Processing processors, initializing processors.
     * It method calling in `processIMC` event.
     * @since Mod: 1.14.4-2.0.0, API: 1.0.0
     */
    internal fun postProcessProcessors() {
        if (!postProcessed) {
            processors.sortedWith(compareBy {
                it.processorLoadIndex
            }).forEach {
                logger.info(
                    marker,
                    "Post processing processor ${it.processorName}, index: ${it.processorLoadIndex}"
                )

                try {
                    it.postProcess()
                } catch (_: NotImplementedError) {
                    logger.error(
                        marker,
                        "Post processing method (`postProcess`) for ${it.processorName} not implemented. Post processing skipped."
                    )
                }
            }

            postProcessed = true
        }
    }
}
