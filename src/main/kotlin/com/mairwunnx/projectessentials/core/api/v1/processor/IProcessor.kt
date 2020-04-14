package com.mairwunnx.projectessentials.core.api.v1.processor

/**
 * Interface for all module processors.
 * @since Mod: 2.0.0-SNAPSHOT.1_MC-1.14.4, API: 1.0.0
 */
@OptIn(ExperimentalUnsignedTypes::class)
interface IProcessor {
    /**
     * Processor loading index, can't contains two and more
     * processors with same load index.
     *
     * Affects on loading order, for example:
     *
     *  > 0 - first to load. 100 - last to load.
     *
     * @since Mod: 2.0.0-SNAPSHOT.1_MC-1.14.4, API: 1.0.0
     */
    val processorLoadIndex: UInt

    /**
     * Processor name. E.g for ConfigurationProcessor class
     * we used `configuration` as name.
     */
    val processorName: String

    /**
     * Initialize the processor.
     * @since Mod: 2.0.0-SNAPSHOT.1_MC-1.14.4, API: 1.0.0
     */
    fun initialize()

    /**
     * Processing something needed.
     * @since Mod: 2.0.0-SNAPSHOT.1_MC-1.14.4, API: 1.0.0
     */
    fun process()

    /**
     * Post processing something after processing needed.
     * @since Mod: 2.0.0-SNAPSHOT.1_MC-1.14.4, API: 1.0.0
     */
    fun postProcess()
}
