package com.projectessentials.core.services

/**
 * Sound service contract, for interacting with
 * sound playing.
 *
 * @param TEntity generic type of target entity for
 * playing sound.
 * @since 3.0.0.
 */
interface ISoundService<TEntity> {
    /**
     * Does play sound to target entity with
     * configured sound event, volume and pitch.
     *
     * @param TEvent minecraft sound event type.
     * @param entity entity type to play sound.
     * @param event minecraft sound event. See [TEvent].
     * @param volume volume of sound between 0.0 and 1.0.
     * @param pitch pitch of sound between 0.0 and 1.0.
     * @since 3.0.0.
     */
    fun <TEvent> play(
        entity: TEntity,
        event: TEvent,
        volume: Float = 1.0f,
        pitch: Float = 1.0f
    )

    /**
     * Does play sound to target entity with
     * configured sound event, volume and pitch
     * if condition returned true value.
     *
     * @param TEvent minecraft sound event type.
     * @param entity entity type to play sound.
     * @param event minecraft sound event. See [TEvent].
     * @param volume volume of sound between 0.0 and 1.0.
     * @param pitch pitch of sound between 0.0 and 1.0.
     * @param condition condition for playing sound.
     * @since 3.0.0.
     */
    fun <TEvent> playIf(
        entity: TEntity,
        event: TEvent,
        volume: Float = 1.0f,
        pitch: Float = 1.0f,
        condition: (TEntity) -> Boolean
    )
}
