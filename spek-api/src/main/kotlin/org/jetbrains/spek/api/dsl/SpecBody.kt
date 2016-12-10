package org.jetbrains.spek.api.dsl

import org.jetbrains.spek.meta.SpekDsl

/**
 * @author Ranie Jade Ramiso
 * @since 1.0
 */
@SpekDsl
interface SpecBody: TestContainer {
    fun group(description: String, pending: Pending = Pending.No, body: SpecBody.() -> Unit)
    fun action(description: String, pending: Pending = Pending.No, body: ActionBody.() -> Unit)

    fun beforeEachTest(callback: () -> Unit)
    fun afterEachTest(callback: () -> Unit)
}
