package org.jetbrains.spek.subject.dsl

import org.jetbrains.spek.api.lifecycle.LifecycleAware
import org.jetbrains.spek.meta.Experimental
import org.jetbrains.spek.subject.CachingMode

/**
 * @author Ranie Jade Ramiso
 */
@Experimental
interface SubjectProviderDsl<T>: SubjectDsl<T> {
    fun subject(mode: CachingMode = CachingMode.TEST, factory: () -> T): LifecycleAware<T>
}
