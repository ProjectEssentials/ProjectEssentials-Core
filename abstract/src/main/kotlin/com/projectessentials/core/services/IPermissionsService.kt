package com.projectessentials.core.services

/**
 * Permissions service contract, for interacting
 * with player permissions.
 *
 * @param TVerifiable generic type of verifiable object.
 * @since 3.0.0.
 */
interface IPermissionsService<TVerifiable> {
    /**
     * @param player verifiable generic type to check his permission.
     * @param node permission node to check permissions.
     * @param opLevel fallback operator level, used if possessor didn't
     * have permission node.
     * @return true if verifiable have requested permission node.
     * @since 3.0.0.
     */
    fun hasPermission(player: TVerifiable, node: String, opLevel: Int = 0): Boolean

    /**
     * Performs action if [TVerifiable] has requested permission node.
     *
     * @param player verifiable generic type to check his permission.
     * @param node permission node to check permissions.
     * @param opLevel fallback operator level, used if possessor didn't
     * have permission node.
     * @param action action to perform if verifiable has permission.
     * @return [TResult] result if it need.
     * @since 3.0.0.
     */
    fun <TResult> performIfCan(
        player: TVerifiable, node: String, opLevel: Int = 0, action: (TVerifiable) -> TResult
    ): TResult

    /**
     * Performs action if [TVerifiable] has requested permission node otherwise
     * performs [restricted] action if verifiable didn't have permission.
     *
     * @param player verifiable generic type to check his permission.
     * @param node permission node to check permissions.
     * @param opLevel fallback operator level, used if possessor didn't
     * have permission node.
     * @param restricted action to perform if verifiable has no permission.
     * @param action action to perform if verifiable has permission.
     * @return [TResult] result if it need.
     * @since 3.0.0.
     */
    fun <TResult> performIfCan(
        player: TVerifiable,
        node: String,
        opLevel: Int = 0,
        restricted: (TVerifiable) -> TResult,
        action: (TVerifiable) -> TResult,
    ): TResult
}
