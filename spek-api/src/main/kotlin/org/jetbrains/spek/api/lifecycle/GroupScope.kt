package org.jetbrains.spek.api.lifecycle

/**
 * @author Ranie Jade Ramiso
 */
interface GroupScope: Scope {
    val parent: GroupScope?
    val lazy: Boolean
}
