package com.mairwunnx.projectessentials.core.api.v1.events.internal

import com.mairwunnx.projectessentials.core.api.v1.events.IModuleEventType

/**
 * Event types for core module, EventAPI.
 * @since 2.0.0-SNAPSHOT.1.
 */
enum class ModuleCoreEventType : IModuleEventType {
    /**
     * This event type will be fired on
     * module class processing.
     * @since 2.0.0-SNAPSHOT.1.
     */
    OnModuleClassProcessing,

    /**
     * This event type will be fired after
     * module class processing.
     * @since 2.0.0-SNAPSHOT.1.
     */
    OnModuleClassProcessed,

    /**
     * This event type will be fired on
     * processor processing.
     * @since 2.0.0-SNAPSHOT.1.
     */
    OnProcessorProcessing,

    /**
     * This event type will be fired on
     * configuration class processing.
     * @since 2.0.0-SNAPSHOT.1.
     */
    OnConfigurationClassProcessing,

    /**
     * This event type will be fired after
     * configuration class processing.
     * @since 2.0.0-SNAPSHOT.1.
     */
    OnConfigurationClassProcessed,

    /**
     * This event type will be fired on
     * command class processing.
     * @since 2.0.0-SNAPSHOT.1.
     */
    OnCommandClassProcessing,

    /**
     * This event type will be fired after
     * command class processing.
     * @since 2.0.0-SNAPSHOT.1.
     */
    OnCommandClassProcessed
}
