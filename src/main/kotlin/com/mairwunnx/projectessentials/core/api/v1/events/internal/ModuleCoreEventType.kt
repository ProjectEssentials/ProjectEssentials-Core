package com.mairwunnx.projectessentials.core.api.v1.events.internal

import com.mairwunnx.projectessentials.core.api.v1.events.IModuleEventType

/**
 * Event types for core module, EventAPI.
 * @since 2.0.0-SNAPSHOT.1.
 */
enum class ModuleCoreEventType : IModuleEventType {
    /**
     * This event type will be fired on
     * processor registering.
     * @since 2.0.0-SNAPSHOT.1.
     */
    OnProcessorRegister,

    /**
     * This event type will be fired after
     * processor registering.
     * @since 2.0.0-SNAPSHOT.1.
     */
    OnProcessorAfterRegister,

    /**
     * This event type will be fired on
     * processor initializing.
     * @since 2.0.0-SNAPSHOT.1.
     */
    OnProcessorInitializing,

    /**
     * This event type will be fired after
     * processor initializing.
     * @since 2.0.0-SNAPSHOT.1.
     */
    OnProcessorAfterInitializing,

    /**
     * This event type will be fired on
     * processor processing.
     * @since 2.0.0-SNAPSHOT.1.
     */
    OnProcessorProcessing,

    /**
     * This event type will be fired after
     * processor processing.
     * @since 2.0.0-SNAPSHOT.1.
     */
    OnProcessorAfterProcessing,

    /**
     * This event type will be fired on
     * processor post processing.
     * @since 2.0.0-SNAPSHOT.1.
     */
    OnProcessorPostProcessing,

    /**
     * This event type will be fired after
     * processor post processing.
     * @since 2.0.0-SNAPSHOT.1.
     */
    OnProcessorAfterPostProcessing,

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
     * module class post processing.
     * @since 2.0.0-SNAPSHOT.1.
     */
    OnModuleClassPostProcessing,

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
     * configuration class post processing.
     * @since 2.0.0-SNAPSHOT.1.
     */
    OnConfigurationClassPostProcessing,

    /**
     * This event type will be fired on
     * localization initializing.
     * @since 2.0.0-SNAPSHOT.1.
     */
    OnLocalizationInitializing,

    /**
     * This event type will be fired on
     * localization processing.
     * @since 2.0.0-SNAPSHOT.1.
     */
    OnLocalizationProcessing,

    /**
     * This event type will be fired after
     * localization processing.
     * @since 2.0.0-SNAPSHOT.1.
     */
    OnLocalizationProcessed,

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
    OnCommandClassProcessed,

    /**
     * This event type will be fired on
     * command class post processing.
     * @since 2.0.0-SNAPSHOT.1.
     */
    OnCommandClassPostProcessing
}
