@file:Suppress("unused")

package com.mairwunnx.projectessentials.core.api.v1.events

import org.apache.logging.log4j.LogManager
import org.apache.logging.log4j.MarkerManager

/**
 * Event API class, contains all methods for interacting
 * with events.
 * @since Mod: 2.0.0-SNAPSHOT.1, API: 1.0.0
 */
object ModuleEventAPI {
    private val logger = LogManager.getLogger()
    private val marker = MarkerManager.Log4jMarker("EVENT MANAGER")

    private val events: HashMap<IModuleEventType, MutableList<(
        IModuleEventData
    ) -> Unit>> = hashMapOf()

    /**
     * Fire target event type. And send some data.
     * @param eventType event type.
     * @param eventData event data.
     * @since Mod: 2.0.0-SNAPSHOT.1, API: 1.0.0
     */
    fun fire(
        eventType: IModuleEventType,
        eventData: IModuleEventData
    ) {
        events.keys.find { it == eventType }?.let { type ->
            logger.debug(marker, "Firing all methods event types of `$type`")
            events[type]?.forEach { it.invoke(eventData) }
        }
    }

    /**
     * Subscribe on target event type.
     * @param T event data data type.
     * @param event target event type.
     * @param action action or method reference what will
     * be called on event firing. Method must return `Unit` or
     * in java it `void`.
     * @since Mod: 2.0.0-SNAPSHOT.1, API: 1.0.0
     */
    @Suppress("UNCHECKED_CAST")
    fun <T> subscribeOn(
        event: IModuleEventType,
        action: (T) -> Unit
    ) {
        val pushedAction = action as (IModuleEventData) -> Unit
        val eventCollection = events[event]

        if (eventCollection == null) {
            events[event] = mutableListOf(pushedAction)
        } else {
            eventCollection.add(pushedAction)
        }
    }

    /**
     * @return all subscribed events.
     * @since Mod: 2.0.0-SNAPSHOT.1, API: 1.0.0
     */
    fun getAllEvents() = events

    /**
     * @param eventType target event type.
     * @return mutable list with references on subscribed methods.
     * @throws EventNotFoundException when event type not found.
     * @since Mod: 2.0.0-SNAPSHOT.1, API: 1.0.0
     */
    fun getEventsByType(
        eventType: IModuleEventType
    ): MutableList<(IModuleEventData) -> Unit> =
        events.keys.find { it == eventType }?.let { type ->
            getAllEvents()[type]?.let { return it }
        } ?: throw EventNotFoundException()

    /**
     * Removing all method references for specified event type.
     * @param eventType target event type.
     * @throws EventNotFoundException when event type not found.
     * @since Mod: 2.0.0-SNAPSHOT.1, API: 1.0.0
     */
    fun killEventsByType(eventType: IModuleEventType) =
        events.keys.find { it == eventType }?.let {
            logger.debug(marker, "Removing methods references from event types of `$it`")
            getAllEvents()[it]?.clear()
        } ?: throw EventNotFoundException()

    /**
     * Removing all methods references from all event types.
     * @since Mod: 2.0.0-SNAPSHOT.1, API: 1.0.0
     */
    fun killAllEvents() = events.clear()
}
