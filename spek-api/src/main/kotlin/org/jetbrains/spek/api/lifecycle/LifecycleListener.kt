package org.jetbrains.spek.api.lifecycle

import org.jetbrains.spek.meta.Experimental

/**
 * @author Ranie Jade Ramiso
 */
@Experimental
interface LifecycleListener {
    fun beforeExecuteTest(test: TestScope) { }
    fun afterExecuteTest(test: TestScope) { }
    fun beforeExecuteGroup(group: GroupScope) { }
    fun afterExecuteGroup(group: GroupScope) { }
}
