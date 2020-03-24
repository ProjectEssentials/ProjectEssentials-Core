package com.mairwunnx.projectessentials.core.api.v1.commands

import com.mairwunnx.projectessentials.core.api.v1.COMMAND_PROCESSOR_INDEX
import com.mairwunnx.projectessentials.core.api.v1.configuration.Configuration
import com.mairwunnx.projectessentials.core.api.v1.events.ModuleEventAPI
import com.mairwunnx.projectessentials.core.api.v1.events.internal.CommandEventData
import com.mairwunnx.projectessentials.core.api.v1.events.internal.DomainEventData
import com.mairwunnx.projectessentials.core.api.v1.events.internal.ModuleCoreEventType.*
import com.mairwunnx.projectessentials.core.api.v1.processor.IProcessor
import com.mairwunnx.projectessentials.core.api.v1.providers.createProvider
import org.apache.logging.log4j.LogManager
import org.apache.logging.log4j.MarkerManager
import org.reflections.Reflections

internal object CommandProcessor : IProcessor {
    private val logger = LogManager.getLogger()
    private val marker = MarkerManager.Log4jMarker("COMMAND PROCESSOR")
    private val provider = createProvider("command")
    private var commands = listOf<ICommand>()
    private val interfaceName = ICommand::class.java.name
    private val allowedDomains = mutableListOf(
        "com.mairwunnx"
    )

    fun getCommands() = commands
    fun getAllowedDomains() = allowedDomains

    override val processorLoadIndex = COMMAND_PROCESSOR_INDEX
    override val processorName: String = "command"

    override fun initialize() {
        logger.info(marker, "Initializing command processor")
        logger.info(marker, "Loading allowed package domains")
        loadDomains()
    }

    private fun loadDomains() {
        provider.readLines().forEach {
            ModuleEventAPI.fire(
                OnAllowedDomainLoading, DomainEventData(it)
            )
            logger.info(marker, "Loaded command domain: $it")
            allowedDomains.add(it)
        }
    }

    override fun process() {
        logger.info(marker, "Finding and processing commands")

        allowedDomains.forEach { domain ->
            ModuleEventAPI.fire(
                OnAllowedDomainProcessing, DomainEventData(domain)
            )

            val reflections = Reflections(domain)
            reflections.getTypesAnnotatedWith(
                Configuration::class.java
            ).forEach { commandClass ->
                if (isCommand(commandClass)) {
                    commandClass as ICommand

                    ModuleEventAPI.fire(
                        OnCommandClassProcessing, CommandEventData(commandClass)
                    )

                    val data = commandClass.getAnnotation(Command::class.java)

                    logger.info(
                        marker,
                        "\n    *** Command taken! ${commandClass.name}".plus(
                            "\n      ## Initializing command ##"
                        ).plus(
                            "\n  - Command name: ${data.name}"
                        ).plus(
                            "\n  - Command aliases: ${data.aliases}"
                        )
                    )
                    commandClass.initialize()
                    commands = commands + commandClass

                    ModuleEventAPI.fire(
                        OnCommandClassProcessed, CommandEventData(commandClass)
                    )
                }
            }
        }
    }

    private fun isCommand(clazz: Class<*>): Boolean {
        val interfaces = clazz.interfaces
        interfaces.forEach {
            if (it.name == interfaceName) {
                return true
            }
        }
        return false
    }

    override fun postProcess() {
        getCommands().forEach {
            ModuleEventAPI.fire(
                OnCommandClassPostProcessing, CommandEventData(it)
            )

            it as Class<*>
            val data = it.getAnnotation(Command::class.java)

            logger.info(
                marker, "Starting registering command ${data.name}"
            )
            it.register(CommandAPI.getDispatcher())
        }
    }
}
