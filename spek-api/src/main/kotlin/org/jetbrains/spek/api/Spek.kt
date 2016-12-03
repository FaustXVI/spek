package org.jetbrains.spek.api

import org.jetbrains.spek.api.dsl.RootDsl

/**
 * @author Ranie Jade Ramiso
 * @since 1.0
 */
abstract class Spek(val spec: RootDsl.() -> Unit)
