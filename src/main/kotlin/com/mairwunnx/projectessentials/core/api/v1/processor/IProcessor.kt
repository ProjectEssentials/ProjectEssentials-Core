package com.mairwunnx.projectessentials.core.api.v1.processor

/**
 * Interface for all module processors.
 * @since 2.0.0-SNAPSHOT.1.
 */
interface IProcessor {
    /**
     * Processor loading index, can't contains two and more
     * processors with same load index.
     *
     * Affects on loading order, for example:
     *
     *  > 0 - first to load. 100 - last to load.
     *
     * @since 2.0.0-SNAPSHOT.1.
     */
    val processorLoadIndex: Int

    /**
     * Processor name. E.g for ConfigurationProcessor class
     * we used `configuration` as name.
     */
    val processorName: String

    /**
     * Initialize the processor.
     * @since 2.0.0-SNAPSHOT.1.
     */
    fun initialize()

    /**
     * Processing something needed.
     * @since 2.0.0-SNAPSHOT.1.
     */
    fun process()

    /**
     * Post processing something after processing needed.
     * @since 2.0.0-SNAPSHOT.1.
     */
    fun postProcess()
}
