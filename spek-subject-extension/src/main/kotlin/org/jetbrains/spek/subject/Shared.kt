package org.jetbrains.spek.subject

import org.jetbrains.spek.api.Spek
import org.jetbrains.spek.api.dsl.RootDsl
import org.jetbrains.spek.api.includeSpec
import org.jetbrains.spek.api.lifecycle.LifecycleAware
import org.jetbrains.spek.meta.Experimental
import org.jetbrains.spek.subject.dsl.SubjectDsl
import org.jetbrains.spek.subject.dsl.SubjectProviderDsl
import kotlin.reflect.KProperty

/**
 * @author Ranie Jade Ramiso
 */
@Experimental
fun <T, K: T> SubjectDsl<K>.includeSubjectSpec(spec: SubjectSpek<T>) {
    includeSpec(wrap {
        try {
            val value: SubjectProviderDsl<T> = object: SubjectProviderDsl<T>, RootDsl by this {
                val adapter = object: LifecycleAware<T> {
                    override fun getValue(thisRef: LifecycleAware<T>, property: KProperty<*>): T {
                        return this()
                    }

                    override fun invoke(): T {
                        return this@includeSubjectSpec.subject().invoke()
                    }

                }

                override fun subject() = adapter
                override fun subject(mode: CachingMode, factory: () -> T) = adapter
                override val subject: T
                    get() = adapter()

            }
            spec.spec(value)
        } catch (e: Throwable) {
            e.printStackTrace()
        }
    })
}

/**
 * @author Ranie Jade Ramiso
 */
@Experimental
infix fun <T, K: T> SubjectDsl<K>.itBehavesLike(spec: SubjectSpek<T>) {
    includeSubjectSpec(spec)
}

private fun wrap(spec: RootDsl.() -> Unit) = object: Spek(spec) {}
