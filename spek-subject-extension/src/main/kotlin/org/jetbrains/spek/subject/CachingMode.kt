package org.jetbrains.spek.subject

import org.jetbrains.spek.meta.Experimental

/**
 * Specifies how [subjects][Subject] are cached.
 *
 * @author Ranie Jade Ramiso
 */
@Experimental
enum class CachingMode {
    /**
     * Subjects will be shared throughout the group which it was declared.
     */
    GROUP,
    /**
     * Each test will get their own unique instance.
     */
    TEST
}
