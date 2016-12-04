package org.jetbrains.spek.subject.core

import org.jetbrains.spek.api.dsl.RootDsl
import org.jetbrains.spek.api.lifecycle.LifecycleAware
import org.jetbrains.spek.subject.CachingMode
import org.jetbrains.spek.subject.dsl.SubjectProviderDsl
import kotlin.properties.Delegates

/**
 * @author Ranie Jade Ramiso
 */
internal class SubjectProviderDslImpl<T>(spec: RootDsl): SubjectDslImpl<T>(spec), SubjectProviderDsl<T> {
    var adapter: SubjectAdapter<T> by Delegates.notNull()
    override val subject: T
        get() = adapter()

    override fun subject(): LifecycleAware<T> = adapter

    override fun subject(mode: CachingMode, factory: () -> T): LifecycleAware<T> {
        return SubjectAdapter(mode, factory).apply {
            adapter = this
        }
    }
}
