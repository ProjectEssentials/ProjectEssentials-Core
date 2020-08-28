package com.projectessentials.core.services

import kotlinx.datetime.Instant
import kotlin.time.Duration
import kotlin.time.ExperimentalTime

/**
 * Local date service contract, for interacting
 * with date time with local time zone.
 *
 * @since 3.0.0.
 */
@OptIn(ExperimentalTime::class)
interface ILocalDateService {
    /**
     * @return date time at moment represented as
     * [Instant] class instance.
     * @since 3.0.0.
     */
    fun now(): Instant

    /**
     * @param now date time now represented as string.
     * @param past date time past represented as string.
     * @return duration between now and past date times
     * represented as [Duration] class instance.
     * @since 3.0.0.
     */
    fun between(now: String, past: String): Duration

    /**
     * @param now date time now represented as [Instant].
     * @param past date time past represented as [Instant].
     * @return duration between now and past date times
     * represented as [Duration] class instance.
     * @since 3.0.0.
     */
    fun between(now: Instant, past: Instant): Duration
}
