package com.github.leon.files.convert

@FunctionalInterface
interface CellConverter {
    fun convert(value: String, obj: Any): Any
}
