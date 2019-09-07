package com.github.leon.lock

import java.util.HashMap
import java.util.concurrent.locks.ReentrantLock

/**
 * 分段锁，系统提供一定数量的原始锁，根据传入对象的哈希值获取对应的锁并加锁
 * 注意：要锁的对象的哈希值如果发生改变，有可能导致锁无法成功释放!!!
 */
class SegmentLock<T> {
    private var segments: Int = 16
    private val lockMap = HashMap<Int, ReentrantLock>()

    constructor() {
        init(null, false)
    }

    constructor(counts: Int, fair: Boolean) {
        init(counts, fair)
    }

    private fun init(counts: Int?, fair: Boolean) {
        if (counts != null) {
            segments = counts
        }
        for (i in 0 until segments) {
            lockMap[i] = ReentrantLock(fair)
        }
    }

    fun lock(key: T) {
        val lock = lockMap[key!!.hashCode() % segments]!!
        lock.lock()
    }

    fun unlock(key: T) {
        val lock = lockMap[key!!.hashCode() % segments]!!
        lock.unlock()
    }
}