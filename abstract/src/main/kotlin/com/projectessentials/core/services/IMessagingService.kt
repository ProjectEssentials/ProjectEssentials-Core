package com.projectessentials.core.services

import kotlinx.coroutines.Deferred

/**
 * Asynchronous messaging service for sending messages
 * to players.
 *
 * @since 3.0.0.
 */
interface IMessagingService {
    /**
     * Does send message to target as [T] type.
     *
     * If target is not handleable will type in the logs
     * message something like `Message not sent to <type>,
     * unsupported target type`.
     *
     * @param T reference type, generic type of target.
     * @param ref reference to target we want to sent message.
     * @param localized if value is true message string will
     * searching in localization container.
     * @param nativeLocalized if value is true message string
     * will localized via minecraft native localization system.
     * @param isActionBarMessage if value is true message will
     * displayed in action bar of player. (works only for player/players).
     * @param condition condition to send message, as first
     * argument will passed [T] value and as second will passed
     * any arguments as immutable list.
     * @param args arguments for the message string.
     * @param message lambda expression with return type string,
     * just must return a requested message string.
     * @return deferred boolean, if value is true then
     * message will delivered or at least one player received
     * message, otherwise false.
     * @since 3.0.0.
     */
    fun <T> sendAsync(
        ref: T,
        localized: Boolean = true,
        nativeLocalized: Boolean = false,
        isActionBarMessage: Boolean = false,
        condition: (T, List<Any>) -> Boolean = { _, _ -> true },
        vararg args: String,
        message: (T) -> String,
    ): Deferred<Boolean>

    /**
     * Does send message as list with pages to target as [T] type.
     *
     * If target is not handleable will type in the logs
     * message something like `List message not sent to <type>,
     * unsupported target type`.
     *
     * @param T reference type, generic type of target.
     * @param R context type, generic type of context.
     * @param ref reference to target we want to sent message.
     * @param context context of command for calculating some params.
     * @param sequence strings to display.
     * @param localized if value is true message string will
     * searching in localization container.
     * @param nativeLocalized if value is true message string
     * will localized via minecraft native localization system.
     * @param condition condition to send message, as first
     * argument will passed [T] value and as second will passed
     * context as [R] as argument.
     * @param title lambda expression with return type string,
     * just must return a requested message string.
     * @return deferred boolean, if value is true then
     * message will delivered or at least one player received
     * message, otherwise false.
     * @since 3.0.0.
     */
    fun <T, R> listAsync(
        ref: T,
        context: R,
        sequence: Sequence<String>,
        localized: Boolean = true,
        nativeLocalized: Boolean = false,
        condition: (T, R) -> Boolean = { _: T, _: R -> true },
        title: (T, R) -> String
    ): Deferred<Boolean>
}
