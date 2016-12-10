package org.jetbrains.spek.api

import org.jetbrains.spek.api.dsl.RootDsl
import org.jetbrains.spek.meta.Experimental

/**
 * @author Ranie Jade Ramiso
 * @since 1.1
 */
@Experimental
fun RootDsl.include(spec: Spek) {
    spec.spec(this)
}
