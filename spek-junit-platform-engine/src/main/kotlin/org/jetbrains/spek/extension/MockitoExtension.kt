package org.jetbrains.spek.extension

import org.jetbrains.spek.api.dsl.Dsl
import org.jetbrains.spek.extension.execution.BeforeExecuteTest
import kotlin.reflect.KProperty

val Dsl.extensionRegistry: ExtensionRegistry
    get() {
        TODO()
    }

class MockitoExtension: BeforeExecuteTest {
    override fun beforeExecuteTest(test: TestExtensionContext) {
        // reset mocks
    }

    override fun init(annotations: Array<Annotation>) {
        // ...
    }

    inline fun <reified T> mock(): ObjectDelegate<T> {
        TODO()
    }
}

interface ObjectDelegate<T> {
    operator fun getValue(thisRef: Any?, property: KProperty<*>): T

}

inline fun <reified T> Dsl.mock(): ObjectDelegate<T> {
    return extensionRegistry.getExtension(MockitoExtension::class)!!
        .mock()
}
