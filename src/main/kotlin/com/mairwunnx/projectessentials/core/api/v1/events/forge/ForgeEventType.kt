package com.mairwunnx.projectessentials.core.api.v1.events.forge

import com.mairwunnx.projectessentials.core.api.v1.events.IModuleEventType

/**
 * Some event types of forge, EventAPI.
 * @since 2.0.0-SNAPSHOT.1.
 */
enum class ForgeEventType : IModuleEventType {
    /**
     * `setup` method for modloading.
     * @since 2.0.0-SNAPSHOT.1.
     */
    SetupEvent,

    /**
     * `enqueueIMC` method for modloading.
     * @since 2.0.0-SNAPSHOT.1.
     */
    EnqueueIMCEvent,

    /**
     * `processIMC` method for modloading.
     * @since 2.0.0-SNAPSHOT.1.
     */
    ProcessIMCEvent,

    /**
     * `doClientStuff` method for modloading.
     * @since 2.0.0-SNAPSHOT.1.
     */
    DoClientStuffEvent,

    /**
     * `complete` method for modloading.
     * @since 2.0.1.
     */
    LoadComplete,
}
