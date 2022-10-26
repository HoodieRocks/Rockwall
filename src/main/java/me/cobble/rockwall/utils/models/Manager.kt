package me.cobble.rockwall.utils.models

open class Manager<K : Any, V> {

    private val map: HashMap<K, V> = hashMapOf()

    protected fun addOrUpdate(key: K, value: V) {
        map[key] = value
    }

    protected fun remove(key: K) = map.remove(key)

    protected fun get(key: K): V? = map[key]

    protected fun containsKey(key: K): Boolean = map.containsKey(key)

    protected fun getAll(): HashMap<K, V> = map
}