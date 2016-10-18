package org.jetbrains.spek.extension

import org.jetbrains.spek.api.dsl.Dsl
import org.jetbrains.spek.extension.execution.BeforeExecuteTest
import kotlin.properties.ReadOnlyProperty

class MockitoExtension: BeforeExecuteTest {
    override fun beforeExecuteTest(test: TestExtensionContext) {
        // reset mocks
    }

    // TODO: call me maybe
    override fun init(annotations: Array<Annotation>) {
        // ...
    }

    inline fun <reified T> mock(): ReadOnlyProperty<*, T> {
        TODO()
    }
}

inline fun <reified T> Dsl.mock(): ReadOnlyProperty<*, T> {
    return extensionRegistry.getExtension(MockitoExtension::class)!!
        .mock()
}
