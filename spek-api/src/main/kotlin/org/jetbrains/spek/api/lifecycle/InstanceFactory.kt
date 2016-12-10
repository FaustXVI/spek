package org.jetbrains.spek.api.lifecycle

import org.jetbrains.spek.api.Spek
import kotlin.reflect.KClass

/**
 * @author Ranie Jade Ramiso
 */
interface InstanceFactory {
    fun <T: Spek> create(spek: KClass<T>): T
}
