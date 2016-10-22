package org.jetbrains.spek.engine.extension

import org.jetbrains.spek.extension.Extension
import org.jetbrains.spek.extension.ExtensionRegistry
import java.util.HashMap
import kotlin.reflect.KClass

/**
 * @author Ranie Jade Ramiso
 */
class ExtensionRegistryImpl: ExtensionRegistry {
    private val extensionMap: MutableMap<KClass<*>, Extension> = HashMap()

    override fun <T: Extension> getExtension(extension: KClass<T>): T? {
        return extensionMap[extension] as T?
    }

    fun <T: Extension> registerExtension(extension: T) {
        if (!extensionMap.containsKey(extension.javaClass.kotlin)) {
            extensionMap.put(extension.javaClass.kotlin, extension)
        }
    }

    fun extensions(): List<Extension> = extensionMap.values.toList()
}
