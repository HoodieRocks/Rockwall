package me.cobble.rockwall.utils

abstract class Manager<K, V> {

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

    fun getByValue(value: V): K? {
        if (!containsValue(value)) return null
        return map.keys.find {
            map[it] == value
        }
    }

    fun containsValue(value: V): Boolean {
        if (value == null) return false
        return map.containsValue(value)
    }

    fun containsKey(key: K): Boolean {
        if (key == null) return false
        return map.containsKey(key)
    }

    fun getAll(): HashMap<K, V> {
        return map
    }
}