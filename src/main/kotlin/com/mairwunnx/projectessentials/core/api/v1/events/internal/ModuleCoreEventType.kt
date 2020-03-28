package com.mairwunnx.projectessentials.core.api.v1.events.internal

import com.mairwunnx.projectessentials.core.api.v1.events.IModuleEventType

/**
 * Event types for core module, EventAPI.
 * @since Mod: 2.0.0-RC.1+MC-1.14.4, API: 1.0.0
 */
enum class ModuleCoreEventType : IModuleEventType {
    /**
     * This event type will be fired on
     * processor registering.
     * @since Mod: 2.0.0-RC.1+MC-1.14.4, API: 1.0.0
     */
    OnProcessorRegister,

    /**
     * This event type will be fired after
     * processor registering.
     * @since Mod: 2.0.0-RC.1+MC-1.14.4, API: 1.0.0
     */
    OnProcessorAfterRegister,

    /**
     * This event type will be fired on
     * processor initializing.
     * @since Mod: 2.0.0-RC.1+MC-1.14.4, API: 1.0.0
     */
    OnProcessorInitializing,

    /**
     * This event type will be fired after
     * processor initializing.
     * @since Mod: 2.0.0-RC.1+MC-1.14.4, API: 1.0.0
     */
    OnProcessorAfterInitializing,

    /**
     * This event type will be fired on
     * processor processing.
     * @since Mod: 2.0.0-RC.1+MC-1.14.4, API: 1.0.0
     */
    OnProcessorProcessing,

    /**
     * This event type will be fired after
     * processor processing.
     * @since Mod: 2.0.0-RC.1+MC-1.14.4, API: 1.0.0
     */
    OnProcessorAfterProcessing,

    /**
     * This event type will be fired on
     * processor post processing.
     * @since Mod: 2.0.0-RC.1+MC-1.14.4, API: 1.0.0
     */
    OnProcessorPostProcessing,

    /**
     * This event type will be fired after
     * processor post processing.
     * @since Mod: 2.0.0-RC.1+MC-1.14.4, API: 1.0.0
     */
    OnProcessorAfterPostProcessing,

    /**
     * This event type will be fired on
     * module class processing.
     * @since Mod: 2.0.0-RC.1+MC-1.14.4, API: 1.0.0
     */
    OnModuleClassProcessing,

    /**
     * This event type will be fired after
     * module class processing.
     * @since Mod: 2.0.0-RC.1+MC-1.14.4, API: 1.0.0
     */
    OnModuleClassProcessed,

    /**
     * This event type will be fired on
     * module class post processing.
     * @since Mod: 2.0.0-RC.1+MC-1.14.4, API: 1.0.0
     */
    OnModuleClassPostProcessing,

    /**
     * This event type will be fired on
     * configuration class processing.
     * @since Mod: 2.0.0-RC.1+MC-1.14.4, API: 1.0.0
     */
    OnConfigurationClassProcessing,

    /**
     * This event type will be fired after
     * configuration class processing.
     * @since Mod: 2.0.0-RC.1+MC-1.14.4, API: 1.0.0
     */
    OnConfigurationClassProcessed,

    /**
     * This event type will be fired on
     * configuration class post processing.
     * @since Mod: 2.0.0-RC.1+MC-1.14.4, API: 1.0.0
     */
    OnConfigurationClassPostProcessing,

    /**
     * This event type will be fired on
     * localization initializing.
     * @since Mod: 2.0.0-RC.1+MC-1.14.4, API: 1.0.0
     */
    OnLocalizationInitializing,

    /**
     * This event type will be fired on
     * localization processing.
     * @since Mod: 2.0.0-RC.1+MC-1.14.4, API: 1.0.0
     */
    OnLocalizationProcessing,

    /**
     * This event type will be fired after
     * localization processing.
     * @since Mod: 2.0.0-RC.1+MC-1.14.4, API: 1.0.0
     */
    OnLocalizationProcessed,

    /**
     * This event type will be fired on
     * command class processing.
     * @since Mod: 2.0.0-RC.1+MC-1.14.4, API: 1.0.0
     */
    OnCommandClassProcessing,

    /**
     * This event type will be fired after
     * command class processing.
     * @since Mod: 2.0.0-RC.1+MC-1.14.4, API: 1.0.0
     */
    OnCommandClassProcessed,

    /**
     * This event type will be fired on
     * command class post processing.
     * @since Mod: 2.0.0-RC.1+MC-1.14.4, API: 1.0.0
     */
    OnCommandClassPostProcessing
}
