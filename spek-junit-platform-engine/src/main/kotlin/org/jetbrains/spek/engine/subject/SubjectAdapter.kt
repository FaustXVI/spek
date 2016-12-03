package org.jetbrains.spek.engine.subject

import org.jetbrains.spek.api.lifecycle.GroupScope
import org.jetbrains.spek.api.lifecycle.LifecycleAware
import org.jetbrains.spek.api.lifecycle.LifecycleListener
import org.jetbrains.spek.api.lifecycle.TestScope
import org.jetbrains.spek.api.memoized.CachingMode
import org.jetbrains.spek.api.memoized.Subject
import kotlin.reflect.KProperty

/**
 * @author Ranie Jade Ramiso
 */
class SubjectAdapter<T>(val mode: CachingMode, val factory: () -> T): Subject<T>, LifecycleListener {
    var cached: T? = null

    override fun getValue(thisRef: LifecycleAware<T>, property: KProperty<*>) = invoke()

    override fun invoke(): T {
        if (cached == null) {
            cached = factory()
        }
        return cached!!
    }

    override fun afterExecuteTest(test: TestScope) {
        if (!test.parent.lazy && mode == CachingMode.TEST) {
            cached = null
        }
    }

    override fun afterExecuteGroup(group: GroupScope) {
        if (mode == CachingMode.GROUP || group.lazy) {
            cached = null
        }
    }
}
