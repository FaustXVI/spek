package org.jetbrains.spek.engine

import org.jetbrains.spek.api.dsl.Pending
import org.jetbrains.spek.api.lifecycle.GroupScope
import org.jetbrains.spek.api.lifecycle.TestScope
import org.jetbrains.spek.engine.lifecycle.LifecycleManager
import org.junit.platform.engine.TestSource
import org.junit.platform.engine.UniqueId
import org.junit.platform.engine.support.descriptor.AbstractTestDescriptor
import org.junit.platform.engine.support.hierarchical.Node

/**
 * @author Ranie Jade Ramiso
 */
sealed class Scope(uniqueId: UniqueId, val pending: Pending, val source: TestSource?,
                   val lifecycleManager: LifecycleManager)
    : AbstractTestDescriptor(uniqueId, uniqueId.segments.last().value), Node<SpekExecutionContext>,
      org.jetbrains.spek.api.lifecycle.Scope {

    init {
        if (source != null) {
            setSource(source)
        }
    }

    open class Group(uniqueId: UniqueId, pending: Pending,
                     source: TestSource?,
                     override val lazy: Boolean,
                     lifecycleManager: LifecycleManager,
                     val body: Group.(SpekExecutionContext) -> Unit)
        : Scope(uniqueId, pending, source, lifecycleManager), GroupScope {
        override val parent: GroupScope? by lazy {
            return@lazy if (getParent().isPresent) {
                getParent().get() as GroupScope
            } else {
                null
            }
        }

        override fun isTest() = false
        override fun isContainer() = true

        override fun hasTests(): Boolean {
            return if (lazy) {
                true
            } else {
                super.hasTests()
            }
        }

        override fun before(context: SpekExecutionContext): SpekExecutionContext {
            lifecycleManager.beforeExecuteGroup(this@Group)
            return context
//            return super.before(context).apply {
//                context.registry.extensions()
//                    .filterIsInstance(BeforeExecuteGroup::class.java)
//                    .forEach { it.beforeExecuteGroup(this@Group) }
//
//            }
        }

        override fun execute(context: SpekExecutionContext): SpekExecutionContext {
            val collector = ThrowableCollector()

//            if (lazy) {
//                context.registry.extensions()
//                    .filterIsInstance(FixturesAdapter::class.java)
//                    .forEach {
//                        collector.executeSafely { it.beforeExecuteGroup(this) }
//                    }
//            }

            if (collector.isEmpty()) {
                collector.executeSafely { body.invoke(this, context) }
            }

//            if (lazy) {
//                context.registry.extensions()
//                    .filterIsInstance(FixturesAdapter::class.java)
//                    .forEach {
//                        collector.executeSafely { it.afterExecuteGroup(this) }
//                    }
//            }

            collector.assertEmpty()
            return context
        }

        override fun after(context: SpekExecutionContext) {
//            context.registry.extensions()
//                .filterIsInstance(AfterExecuteGroup::class.java)
//                .forEach { it.afterExecuteGroup(this@Group) }

            lifecycleManager.afterExecuteGroup(this@Group)
        }
    }

    class Test(uniqueId: UniqueId, pending: Pending, source: TestSource?, lifecycleManager: LifecycleManager, val body: () -> Unit)
        : Scope(uniqueId, pending, source, lifecycleManager), TestScope {
        override val parent: GroupScope by lazy {
            getParent().get() as GroupScope
        }

        override fun isTest() = true
        override fun isContainer() = false
        override fun isLeaf() = true

        override fun before(context: SpekExecutionContext): SpekExecutionContext {
            lifecycleManager.beforeExecuteTest(this)
            return context
        }

        override fun after(context: SpekExecutionContext) {
            lifecycleManager.afterExecuteTest(this)
        }

        override fun execute(context: SpekExecutionContext): SpekExecutionContext {
            val collector = ThrowableCollector()

//            context.registry.extensions()
//                .filterIsInstance(BeforeExecuteTest::class.java)
//                .forEach {
//                    collector.executeSafely { it.beforeExecuteTest(this@Test) }
//                }

            if (collector.isEmpty()) {
//                if (!parent.lazy) {
//                    context.registry.extensions()
//                        .filterIsInstance(FixturesAdapter::class.java)
//                        .forEach {
//                            collector.executeSafely { it.beforeExecuteTest(this@Test) }
//                        }
//                }

                if (collector.isEmpty()) {
                    collector.executeSafely { body.invoke() }
                }
            }


//            if (!parent.lazy) {
//                context.registry.extensions()
//                    .filterIsInstance(FixturesAdapter::class.java)
//                    .forEach {
//                        collector.executeSafely { it.afterExecuteTest(this@Test) }
//                    }
//            }
//
//            context.registry.extensions()
//                .filterIsInstance(AfterExecuteTest::class.java)
//                .forEach {
//                    collector.executeSafely { it.afterExecuteTest(this) }
//                }

            collector.assertEmpty()

            return context
        }
    }

    override fun shouldBeSkipped(context: SpekExecutionContext): Node.SkipResult {
        return when(pending) {
            is Pending.Yes -> Node.SkipResult.skip(pending.reason)
            else -> Node.SkipResult.doNotSkip()
        }
    }
}
