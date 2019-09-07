package com.github.leon.lock

import java.util.concurrent.ConcurrentHashMap
import java.util.concurrent.atomic.AtomicInteger
import java.util.concurrent.locks.ReentrantLock

class HashLock<T> {
    private var isFair = false
    private val segmentLock = SegmentLock<T>()
    private val lockMap = ConcurrentHashMap<T, LockInfo>()

    constructor() {}

    constructor(fair: Boolean) {
        isFair = fair
    }

    fun lock(key: T) {
        var lockInfo: LockInfo?
        segmentLock.lock(key)
        try {
            lockInfo = lockMap[key]
            if (lockInfo == null) {
                lockInfo = LockInfo(isFair)
                lockMap[key] = lockInfo
            } else {
                lockInfo.count.incrementAndGet()
            }
        } finally {
            segmentLock.unlock(key)
        }
        lockInfo!!.lock.lock()
    }

    fun unlock(key: T) {
        val lockInfo = lockMap[key]!!
        if (lockInfo.count.get() == 1) {
            segmentLock.lock(key)
            try {
                if (lockInfo.count.get() == 1) {
                    lockMap.remove(key)
                }
            } finally {
                segmentLock.unlock(key)
            }
        }
        lockInfo.count.decrementAndGet()
        lockInfo.unlock()
    }

    fun sync(key: T, body: () -> Unit) {
        this.lock(key)
        body()
        this.unlock(key)
    }

    class LockInfo constructor(fair: Boolean) {
        var lock: ReentrantLock = ReentrantLock(fair)
        var count = AtomicInteger(1)

        fun lock() {
            this.lock.lock()
        }

        fun unlock() {
            this.lock.unlock()
        }
    }
}