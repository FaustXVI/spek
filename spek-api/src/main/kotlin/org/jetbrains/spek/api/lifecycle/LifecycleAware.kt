package org.jetbrains.spek.api.lifecycle

import org.jetbrains.spek.meta.Experimental
import kotlin.properties.ReadOnlyProperty

/**
 * @author Ranie Jade Ramiso
 */
@Experimental
interface LifecycleAware<T>: ReadOnlyProperty<LifecycleAware<T>, T> {
    operator fun invoke(): T
}
