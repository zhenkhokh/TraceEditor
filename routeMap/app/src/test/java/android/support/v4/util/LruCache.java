package android.support.v4.util

import java.util.*

// use for androidAStudio tests only
class LruCache<K, V> constructor(maxSize: Int) {
    private val map: LinkedHashMap<K, V>

    /** Size of this cache in units. Not necessarily the number of elements.  */
    private var size: Int = 0
    private var maxSize: Int
    private var putCount: Int = 0
    private var createCount: Int = 0
    private var evictionCount: Int = 0
    private var hitCount: Int = 0
    private var missCount: Int = 0

    /**
     * Sets the size of the cache.
     * @param maxSize The new maximum size.
     *
     * @hide
     */
    fun resize(maxSize: Int) {
        if (maxSize <= 0) {
            throw IllegalArgumentException("maxSize <= 0")
        }
        synchronized(this, { this.maxSize = maxSize })
        trimToSize(maxSize)
    }

    /**
     * Returns the value for `key` if it exists in the cache or can be
     * created by `#create`. If a value was returned, it is moved to the
     * head of the queue. This returns null if a value is not cached and cannot
     * be created.
     */
    operator fun get(key: K?): V? {
        if (key == null) {
            throw NullPointerException("key == null")
        }
        var mapValue: V?
        synchronized(this, {
            mapValue = map.get(key)
            if (mapValue != null) {
                hitCount++
                return mapValue
            }
            missCount++
        })

        /*
         * Attempt to create a value. This may take a long time, and the map
         * may be different when create() returns. If a conflicting value was
         * added to the map while create() was working, we leave that value in
         * the map and release the created value.
         */
        val createdValue: V? = create(key)
        if (createdValue == null) {
            return null
        }
        synchronized(this, {
            createCount++
            mapValue = map.put(key, createdValue)
            if (mapValue != null) {
                // There was a conflict so undo that last put
                map.put(key, mapValue)
            } else {
                size += safeSizeOf(key, createdValue)
            }
        })
        if (mapValue != null) {
            entryRemoved(false, key, createdValue, mapValue)
            return mapValue
        } else {
            trimToSize(maxSize)
            return createdValue
        }
    }

    /**
     * Caches `value` for `key`. The value is moved to the head of
     * the queue.
     *
     * @return the previous value mapped by `key`.
     */
    fun put(key: K?, value: V?): V? {
        if (key == null || value == null) {
            throw NullPointerException("key == null || value == null")
        }
        var previous: V?
        synchronized(this, {
            putCount++
            size += safeSizeOf(key, value)
            previous = map.put(key, value)
            if (previous != null) {
                size -= safeSizeOf(key, previous)
            }
        })
        if (previous != null) {
            entryRemoved(false, key, previous, value)
        }
        trimToSize(maxSize)
        return previous
    }

    /**
     * @param maxSize the maximum size of the cache before returning. May be -1
     * to evict even 0-sized elements.
     */
    private fun trimToSize(maxSize: Int) {
        while (true) {
            var key: K
            var value: V
            synchronized(this, {
                if (size < 0 || (map.isEmpty() && size != 0)) {
                    throw IllegalStateException((javaClass.getName()
                            + ".sizeOf() is reporting inconsistent results!"))
                }
                if (size <= maxSize) {
                    break
                }

                // BEGIN LAYOUTLIB CHANGE
                // get the last item in the linked list.
                // This is not efficient, the goal here is to minimize the changes
                // compared to the platform version.
                var toEvict: Map.Entry<K, V>? = null
                for (entry: Map.Entry<K, V>? in map.entries) {
                    toEvict = entry
                }
                // END LAYOUTLIB CHANGE
                if (toEvict == null) {
                    break
                }
                key = toEvict.key
                value = toEvict.value
                map.remove(key)
                size -= safeSizeOf(key, value)
                evictionCount++
            })
            entryRemoved(true, key, value, null)
        }
    }

    /**
     * Removes the entry for `key` if it exists.
     *
     * @return the previous value mapped by `key`.
     */
    fun remove(key: K?): V? {
        if (key == null) {
            throw NullPointerException("key == null")
        }
        var previous: V?
        synchronized(this, {
            previous = map.remove(key)
            if (previous != null) {
                size -= safeSizeOf(key, previous)
            }
        })
        if (previous != null) {
            entryRemoved(false, key, previous, null)
        }
        return previous
    }

    /**
     * Called for entries that have been evicted or removed. This method is
     * invoked when a value is evicted to make space, removed by a call to
     * [.remove], or replaced by a call to [.put]. The default
     * implementation does nothing.
     *
     *
     * The method is called without synchronization: other threads may
     * access the cache while this method is executing.
     *
     * @param evicted true if the entry is being removed to make space, false
     * if the removal was caused by a [.put] or [.remove].
     * @param newValue the new value for `key`, if it exists. If non-null,
     * this removal was caused by a [.put]. Otherwise it was caused by
     * an eviction or a [.remove].
     */
    protected fun entryRemoved(evicted: Boolean, key: K, oldValue: V, newValue: V?) {}

    /**
     * Called after a cache miss to compute a value for the corresponding key.
     * Returns the computed value or null if no value can be computed. The
     * default implementation returns null.
     *
     *
     * The method is called without synchronization: other threads may
     * access the cache while this method is executing.
     *
     *
     * If a value for `key` exists in the cache when this method
     * returns, the created value will be released with [.entryRemoved]
     * and discarded. This can occur when multiple threads request the same key
     * at the same time (causing multiple values to be created), or when one
     * thread calls [.put] while another is creating a value for the same
     * key.
     */
    protected fun create(key: K): V? {
        return null
    }

    private fun safeSizeOf(key: K, value: V): Int {
        val result: Int = sizeOf(key, value)
        if (result < 0) {
            throw IllegalStateException("Negative size: " + key + "=" + value)
        }
        return result
    }

    /**
     * Returns the size of the entry for `key` and `value` in
     * user-defined units.  The default implementation returns 1 so that size
     * is the number of entries and max size is the maximum number of entries.
     *
     *
     * An entry's size must not change while it is in the cache.
     */
    protected fun sizeOf(key: K, value: V): Int {
        return 1
    }

    /**
     * Clear the cache, calling [.entryRemoved] on each removed entry.
     */
    fun evictAll() {
        trimToSize(-1) // -1 will evict 0-sized elements
    }

    /**
     * For caches that do not override [.sizeOf], this returns the number
     * of entries in the cache. For all other caches, this returns the sum of
     * the sizes of the entries in this cache.
     */
    @Synchronized
    fun size(): Int {
        return size
    }

    /**
     * For caches that do not override [.sizeOf], this returns the maximum
     * number of entries in the cache. For all other caches, this returns the
     * maximum sum of the sizes of the entries in this cache.
     */
    @Synchronized
    fun maxSize(): Int {
        return maxSize
    }

    /**
     * Returns the number of times [.get] returned a value that was
     * already present in the cache.
     */
    @Synchronized
    fun hitCount(): Int {
        return hitCount
    }

    /**
     * Returns the number of times [.get] returned null or required a new
     * value to be created.
     */
    @Synchronized
    fun missCount(): Int {
        return missCount
    }

    /**
     * Returns the number of times [.create] returned a value.
     */
    @Synchronized
    fun createCount(): Int {
        return createCount
    }

    /**
     * Returns the number of times [.put] was called.
     */
    @Synchronized
    fun putCount(): Int {
        return putCount
    }

    /**
     * Returns the number of values that have been evicted.
     */
    @Synchronized
    fun evictionCount(): Int {
        return evictionCount
    }

    /**
     * Returns a copy of the current contents of the cache, ordered from least
     * recently accessed to most recently accessed.
     */
    @Synchronized
    fun snapshot(): Map<K, V> {
        return LinkedHashMap(map)
    }

    @Synchronized
    public override fun toString(): String {
        val accesses: Int = hitCount + missCount
        val hitPercent: Int = if (accesses != 0) (100 * hitCount / accesses) else 0
        return String.format("LruCache[maxSize=%d,hits=%d,misses=%d,hitRate=%d%%]",
                maxSize, hitCount, missCount, hitPercent)
    }

    /**
     * @param maxSize for caches that do not override [.sizeOf], this is
     * the maximum number of entries in the cache. For all other caches,
     * this is the maximum sum of the sizes of the entries in this cache.
     */
    init {
        if (maxSize <= 0) {
            throw IllegalArgumentException("maxSize <= 0")
        }
        this.maxSize = maxSize
        map = LinkedHashMap(0, 0.75f, true)
    }
}