package org.jetbrains.spek.api.memoized

import org.jetbrains.spek.api.lifecycle.LifecycleAware
import org.jetbrains.spek.meta.Experimental

/**
 * @author Ranie Jade Ramiso
 * @since 1.0
 */
@Experimental
interface Subject<T>: LifecycleAware<T>
