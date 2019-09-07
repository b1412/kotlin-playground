package com.github.leon.extentions

import arrow.core.getOrElse
import arrow.core.toOption

fun Any?.println() {
    println(this)
}

fun Any?.print() {
    print(this)
}

fun <T> T?.orElse(default: T): T {
    return this.toOption().getOrElse { default }
}

