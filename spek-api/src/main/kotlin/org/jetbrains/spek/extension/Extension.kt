package org.jetbrains.spek.extension

import org.jetbrains.spek.meta.Experimental
import kotlin.reflect.KClass

/**
 * @author Ranie Jade Ramiso
 */
@Experimental
interface Extension {
    fun init(spec: KClass<*>)
}
