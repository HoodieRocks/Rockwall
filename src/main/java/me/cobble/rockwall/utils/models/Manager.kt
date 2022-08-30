package me.cobble.rockwall.utils.models

import java.util.*
import java.util.concurrent.CompletableFuture

open class Manager<K : Any, V> {

    private val map: HashMap<K, V> = hashMapOf()

    fun addOrUpdate(key: K, value: V) {
        map[key] = value
    }

    fun remove(key: K) {
        map.remove(key)
    }

    fun get(key: K): V? {
        return map[key]
    }

    fun getByValue(value: V): CompletableFuture<Optional<K>> {
        return CompletableFuture.supplyAsync {
            Optional.ofNullable(map.keys.find {
                get(it) == value
            })
        }
    }

    fun containsValue(value: V): Boolean {
        return map.containsValue(value)
    }

    fun containsKey(key: K): Boolean {
        return map.containsKey(key)
    }

    fun getAll(): HashMap<K, V> {
        return map
    }
}