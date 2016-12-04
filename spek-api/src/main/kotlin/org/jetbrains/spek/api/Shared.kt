package org.jetbrains.spek.api

import org.jetbrains.spek.api.dsl.RootDsl
import org.jetbrains.spek.meta.Experimental

/**
 * @author Ranie Jade Ramiso
 */
@Experimental
fun RootDsl.includeSpec(spec: Spek) {
    spec.spec(this)
}
