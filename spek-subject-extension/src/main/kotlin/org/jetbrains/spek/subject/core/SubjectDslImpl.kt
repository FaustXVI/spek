package org.jetbrains.spek.subject.core

import org.jetbrains.spek.api.dsl.RootDsl
import org.jetbrains.spek.subject.dsl.SubjectDsl

/**
 * @author Ranie Jade Ramiso
 */
abstract class SubjectDslImpl<T>(val root: RootDsl): SubjectDsl<T>, RootDsl by root
