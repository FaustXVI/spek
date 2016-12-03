package org.jetbrains.spek.meta

import java.lang.annotation.Inherited

/**
 * @author Ranie Jade Ramiso
 */
@Target(AnnotationTarget.CLASS)
@Retention(AnnotationRetention.RUNTIME)
@Inherited
@Experimental
annotation class SpekDsl
