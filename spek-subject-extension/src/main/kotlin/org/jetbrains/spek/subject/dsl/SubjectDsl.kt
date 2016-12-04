package org.jetbrains.spek.subject.dsl

import org.jetbrains.spek.api.dsl.RootDsl
import org.jetbrains.spek.api.lifecycle.LifecycleAware
import org.jetbrains.spek.meta.Experimental

/**
 * @author Ranie Jade Ramiso
 */
@Experimental
interface SubjectDsl<T>: RootDsl {
    val subject: T

    fun subject(): LifecycleAware<T>
}
