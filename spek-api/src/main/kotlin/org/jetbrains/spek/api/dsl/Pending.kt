package org.jetbrains.spek.api.dsl

/**
 * @author Ranie Jade Ramiso
 */
sealed class Pending constructor(val pending: Boolean) {
    class Yes(val reason: String? = null): Pending(true)
    object No: Pending(false)
}
