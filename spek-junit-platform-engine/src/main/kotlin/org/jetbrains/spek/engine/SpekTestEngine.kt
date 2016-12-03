package org.jetbrains.spek.engine

import org.jetbrains.spek.api.Spek
import org.jetbrains.spek.api.SubjectSpek
import org.jetbrains.spek.api.dsl.Dsl
import org.jetbrains.spek.api.dsl.Pending
import org.jetbrains.spek.api.dsl.RootDsl
import org.jetbrains.spek.api.dsl.SubjectDsl
import org.jetbrains.spek.api.lifecycle.LifecycleListener
import org.jetbrains.spek.api.memoized.CachingMode
import org.jetbrains.spek.api.memoized.Subject
import org.jetbrains.spek.engine.lifecycle.LifecycleManager
import org.jetbrains.spek.engine.subject.SubjectAdapter
import org.junit.platform.commons.util.ReflectionUtils
import org.junit.platform.engine.EngineDiscoveryRequest
import org.junit.platform.engine.ExecutionRequest
import org.junit.platform.engine.TestDescriptor
import org.junit.platform.engine.TestSource
import org.junit.platform.engine.UniqueId
import org.junit.platform.engine.discovery.ClassSelector
import org.junit.platform.engine.discovery.ClasspathRootSelector
import org.junit.platform.engine.discovery.PackageSelector
import org.junit.platform.engine.discovery.UniqueIdSelector
import org.junit.platform.engine.support.descriptor.ClassSource
import org.junit.platform.engine.support.descriptor.EngineDescriptor
import org.junit.platform.engine.support.hierarchical.HierarchicalTestEngine
import java.nio.file.Paths
import java.util.LinkedList
import java.util.function.Consumer
import kotlin.reflect.KClass
import kotlin.reflect.primaryConstructor

/**
 * @author Ranie Jade Ramiso
 */
class SpekTestEngine: HierarchicalTestEngine<SpekExecutionContext>() {
    override fun discover(discoveryRequest: EngineDiscoveryRequest, uniqueId: UniqueId): TestDescriptor {
        val engineDescriptor = SpekEngineDescriptor(uniqueId)
        resolveSpecs(discoveryRequest, engineDescriptor)
        return engineDescriptor
    }

    override fun getId(): String = "spek"

    override fun createExecutionContext(request: ExecutionRequest)
        = SpekExecutionContext(request)

    private fun resolveSpecs(discoveryRequest: EngineDiscoveryRequest, engineDescriptor: EngineDescriptor) {
        val isSpec = java.util.function.Predicate<Class<*>> {
            Spek::class.java.isAssignableFrom(it) || SubjectSpek::class.java.isAssignableFrom(it)
        }
        val isSpecClass = java.util.function.Predicate<String>(String::isNotEmpty)
        discoveryRequest.getSelectorsByType(ClasspathRootSelector::class.java).forEach {
            ReflectionUtils.findAllClassesInClasspathRoot(Paths.get(it.classpathRoot), isSpec, isSpecClass).forEach {
                resolveSpec(engineDescriptor, it)
            }
        }

        discoveryRequest.getSelectorsByType(PackageSelector::class.java).forEach {
            ReflectionUtils.findAllClassesInPackage(it.packageName, isSpec, isSpecClass).forEach {
                resolveSpec(engineDescriptor, it)
            }
        }

        discoveryRequest.getSelectorsByType(ClassSelector::class.java).forEach {
            if (isSpec.test(it.javaClass)) {
                resolveSpec(engineDescriptor, it.javaClass as Class<Spek>)
            }
        }

        discoveryRequest.getSelectorsByType(UniqueIdSelector::class.java).forEach {
            engineDescriptor.findByUniqueId(it.uniqueId).ifPresent(Consumer {
                filterOutUniqueId(it, engineDescriptor)
            })
        }
    }

    private fun filterOutUniqueId(target: TestDescriptor, root: TestDescriptor) {
        if (target != root) {
            if (root.descendants.contains(target)) {
                val descriptors = LinkedList<TestDescriptor>()
                root.children.forEach {
                    descriptors.add(it)
                }

                descriptors.forEach { filterOutUniqueId(target, it) }
            } else {
                root.removeFromHierarchy()
            }
        }
    }

    private fun resolveSpec(engineDescriptor: EngineDescriptor, klass: Class<*>) {
        val lifecycleManager = LifecycleManager()

        val kotlinClass = klass.kotlin
        val instance = kotlinClass.primaryConstructor!!.call()
        val root = Scope.Group(
            engineDescriptor.uniqueId.append(SPEC_SEGMENT_TYPE, klass.name),
            Pending.No,
            ClassSource(klass), false, lifecycleManager, {}
        )
        engineDescriptor.addChild(root)

        when(instance) {
            is SubjectSpek<*> -> (instance as SubjectSpek<Any>).spec.invoke(
                SubjectCollector<Any>(root, lifecycleManager)
            )
            is Spek -> instance.spec.invoke(Collector(root, lifecycleManager))
        }

    }

    open class Collector(val root: Scope.Group,
                         val lifecycleManager: LifecycleManager): RootDsl {

        val fixtures = FixturesAdapter().apply {
            lifecycleManager.addListener(this)
        }

        override fun registerListener(listener: LifecycleListener) {
            lifecycleManager.addListener(listener)
        }

        override fun group(description: String, pending: Pending, lazy: Boolean, body: Dsl.() -> Unit) {
            val action: Scope.Group.(SpekExecutionContext) -> Unit = if (lazy) {
                {
                    body.invoke(LazyGroupCollector(this, lifecycleManager, it))
                }
            } else {
                { }
            }

            val group = Scope.Group(
                root.uniqueId.append(GROUP_SEGMENT_TYPE, description),
                pending, getSource(), lazy, lifecycleManager, action
            )

            root.addChild(group)

            if (!lazy) {
                body.invoke(Collector(group, lifecycleManager))
            }
        }

        override fun test(description: String, pending: Pending, body: () -> Unit) {
            val test = Scope.Test(
                root.uniqueId.append(TEST_SEGMENT_TYPE, description),
                pending, getSource(), lifecycleManager, body
            )
            root.addChild(test)
        }

        override fun beforeEachTest(callback: () -> Unit) {
            fixtures.registerBeforeEach(root, callback)
        }

        override fun afterEachTest(callback: () -> Unit) {
            fixtures.registerAfterEach(root, callback)
        }
    }

    class LazyGroupCollector(root: Scope.Group, lifecycleManager: LifecycleManager,
                             val context: SpekExecutionContext): Collector(root, lifecycleManager) {
        override fun group(description: String, pending: Pending, lazy: Boolean, body: Dsl.() -> Unit) {
            fail()
        }

        override fun beforeEachTest(callback: () -> Unit) {
            fail()
        }

        override fun afterEachTest(callback: () -> Unit) {
            fail()
        }

        override fun test(description: String, pending: Pending, body: () -> Unit) {
            val test = Scope.Test(
                root.uniqueId.append(TEST_SEGMENT_TYPE, description), pending, getSource(), lifecycleManager, body
            )
            root.addChild(test)
            context.engineExecutionListener.dynamicTestRegistered(test)
        }

        private inline fun fail() {
            throw SpekException("You're not allowed to do this")
        }
    }

    open class SubjectCollector<T>(root: Scope.Group, lifecycleManager: LifecycleManager)
        : Collector(root, lifecycleManager), SubjectDsl<T> {
        lateinit var _subject: Subject<T>

        override fun subject(mode: CachingMode, factory: () -> T): Subject<T> {
            return SubjectAdapter(mode, factory).apply {
                _subject = this
                lifecycleManager.addListener(this)
            }
        }

        override val subject: T
            get() {
                try {
                    return _subject()
                } catch (e: Throwable) {
                    throw SpekException("Subject not configured.")
                }

            }

        override fun <T, K : SubjectSpek<T>> includeSubjectSpec(spec: KClass<K>) {
            val instance = spec.primaryConstructor!!.call()

            val scope = Scope.Group(
                root.uniqueId.append(SPEC_SEGMENT_TYPE, spec.java.name),
                Pending.No, ClassSource(spec.java), false, lifecycleManager, {}
            )
            root.addChild(scope)
            instance.spec.invoke(
                NestedSubjectCollector(scope, lifecycleManager, this as SubjectCollector<T>)
            )
        }
    }

    class NestedSubjectCollector<T>(root: Scope.Group, lifecycleManager: LifecycleManager,
                                    val parent: SubjectCollector<T>)
        : SubjectCollector<T>(root, lifecycleManager) {
        override fun subject(mode: CachingMode, factory: () -> T): Subject<T> {
            return parent._subject
        }

        override val subject: T
            get() = parent.subject
    }

    companion object {
        const val SPEC_SEGMENT_TYPE = "spec";
        const val GROUP_SEGMENT_TYPE = "group";
        const val TEST_SEGMENT_TYPE = "test";

        // TODO: fix me
        fun getSource(): TestSource? = null
    }
}
