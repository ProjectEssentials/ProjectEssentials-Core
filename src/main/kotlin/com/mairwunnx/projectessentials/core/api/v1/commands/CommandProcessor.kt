package com.mairwunnx.projectessentials.core.api.v1.commands

import com.mairwunnx.projectessentials.core.api.v1.COMMAND_PROCESSOR_INDEX
import com.mairwunnx.projectessentials.core.api.v1.events.ModuleEventAPI
import com.mairwunnx.projectessentials.core.api.v1.events.internal.CommandEventData
import com.mairwunnx.projectessentials.core.api.v1.events.internal.ModuleCoreEventType.*
import com.mairwunnx.projectessentials.core.api.v1.processor.IProcessor
import com.mairwunnx.projectessentials.core.api.v1.processor.PostponedInit
import com.mairwunnx.projectessentials.core.api.v1.providers.ProviderAPI
import com.mairwunnx.projectessentials.core.api.v1.providers.ProviderType
import org.apache.logging.log4j.LogManager
import org.apache.logging.log4j.MarkerManager

internal object CommandProcessor : IProcessor {
    private val logger = LogManager.getLogger()
    private val marker = MarkerManager.Log4jMarker("COMMAND PROCESSOR")
    private var commands = listOf<ICommand>()

    fun getCommands() = commands

    override val processorLoadIndex = COMMAND_PROCESSOR_INDEX
    override val processorName: String = "command"

    override fun initialize() = Unit

    override fun process() {
        logger.debug(marker, "Finding and processing commands")

        ProviderAPI.getProvidersByType(ProviderType.COMMAND).forEach {
            val clazz = it.getDeclaredField("INSTANCE").get(null) as ICommand

            ModuleEventAPI.fire(OnCommandClassProcessing, CommandEventData(clazz))

            logger.debug(
                marker,
                """

    ### Command taken! ${clazz.javaClass.simpleName}
        - Name: ${clazz.name}
        - Class: ${clazz.javaClass.canonicalName}
        - Aliases: ${clazz.aliases}

                """
            )

            commands = commands + clazz
            ModuleEventAPI.fire(OnCommandClassProcessed, CommandEventData(clazz))
        }
    }

    @PostponedInit
    override fun postProcess() {
        getCommands().forEach {
            ModuleEventAPI.fire(OnCommandClassPostProcessing, CommandEventData(it))
            it.initialize()
            it.register(CommandAPI.getDispatcher())
        }
    }
}
