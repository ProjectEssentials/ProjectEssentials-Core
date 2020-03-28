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
import kotlin.reflect.full.findAnnotation

internal object CommandProcessor : IProcessor {
    private val logger = LogManager.getLogger()
    private val marker = MarkerManager.Log4jMarker("COMMAND PROCESSOR")
    private var commands = listOf<ICommand>()

    fun getCommands() = commands

    override val processorLoadIndex = COMMAND_PROCESSOR_INDEX
    override val processorName: String = "command"

    override fun initialize() = Unit

    override fun process() {
        logger.info(marker, "Finding and processing commands")

        ProviderAPI.getProvidersByType(ProviderType.COMMAND).forEach {
            val clazz = it.objectInstance as ICommand

            ModuleEventAPI.fire(
                OnCommandClassProcessing, CommandEventData(clazz)
            )

            val data = it.findAnnotation<Command>()!!

            logger.info(
                marker,
                "\n\n    *** Command taken! ${it.simpleName}".plus(
                    "\n\n  - Command name: ${data.name}"
                ).plus(
                    "\n  - Command aliases: ${data.aliases.contentToString()}"
                ).plus(
                    "\n  - Class: ${it.qualifiedName}\n\n"
                )
            )

            commands = commands + clazz

            ModuleEventAPI.fire(
                OnCommandClassProcessed, CommandEventData(clazz)
            )
        }
    }

    @PostponedInit
    override fun postProcess() {
        getCommands().forEach {
            ModuleEventAPI.fire(
                OnCommandClassPostProcessing, CommandEventData(it)
            )
            it.initialize()
            it.register(CommandAPI.getDispatcher())
        }
    }
}
