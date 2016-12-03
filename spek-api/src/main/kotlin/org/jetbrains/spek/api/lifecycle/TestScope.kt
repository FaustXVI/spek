package org.jetbrains.spek.api.lifecycle

/**
 * @author Ranie Jade Ramiso
 */
interface TestScope: Scope {
    val parent: GroupScope
}
