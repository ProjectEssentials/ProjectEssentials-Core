package com.projectessentials.core.services

import kotlinx.coroutines.Deferred

/**
 * Permissions service contract, for interacting
 * with player permissions.
 *
 * @param TVerifiable generic type of verifiable object.
 * @since 3.0.0.
 */
interface IPermissionsService<TVerifiable> {
    /**
     * Synchronously version of checking permissions, check also
     * [hasPermissionAsync] for calling this asynchronously.
     *
     * @param player verifiable generic type to check his permission.
     * @param node permission node to check permissions.
     * @param opLevel fallback operator level, used if possessor didn't
     * have permission node.
     * @return true if verifiable have requested permission node.
     * @since 3.0.0.
     * @see hasPermissionAsync
     */
    fun hasPermission(player: TVerifiable, node: String, opLevel: Int = 0): Boolean

    /**
     * Asynchronously version of checking permissions, check also
     * [hasPermission] for calling this synchronously.
     *
     * @param player verifiable generic type to check his permission.
     * @param node permission node to check permissions.
     * @param opLevel fallback operator level, used if possessor didn't
     * have permission node.
     * @return deferred true if verifiable have requested permission node.
     * @since 3.0.0.
     * @see hasPermission
     */
    suspend fun hasPermissionAsync(
        player: TVerifiable, node: String, opLevel: Int = 0
    ): Deferred<Boolean>

    /**
     * Performs action if [TVerifiable] has requested permission node.
     * Asynchronous version of this method [performIfCanAsync].
     *
     * @param player verifiable generic type to check his permission.
     * @param node permission node to check permissions.
     * @param opLevel fallback operator level, used if possessor didn't
     * have permission node.
     * @param action action to perform if verifiable has permission.
     * @return [TResult] result if it need.
     * @since 3.0.0.
     * @see performIfCanAsync
     */
    fun <TResult> performIfCan(
        player: TVerifiable,
        node: String,
        opLevel: Int = 0,
        action: (TVerifiable) -> TResult,
        restricted: ((TVerifiable) -> TResult)? = null
    ): TResult

    /**
     * Performs suspended action if [TVerifiable] has requested permission node.
     * Synchronous version of this method [performIfCan].
     *
     * @param player verifiable generic type to check his permission.
     * @param node permission node to check permissions.
     * @param opLevel fallback operator level, used if possessor didn't
     * have permission node.
     * @param action action to perform if verifiable has permission.
     * @param restricted action to perform if verifiable has no permission.
     * @return [TResult] result if it need.
     * @since 3.0.0.
     * @see performIfCan
     */
    suspend fun <TResult> performIfCanAsync(
        player: TVerifiable,
        node: String,
        opLevel: Int = 0,
        action: (TVerifiable) -> TResult,
        restricted: ((TVerifiable) -> TResult)? = null
    ): Deferred<TResult>
}
