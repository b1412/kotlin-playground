package com.github.leon.files.filter

@FunctionalInterface
interface RowFilter {
    fun doFilter(rowNum: Int, list: List<String>): Boolean
}
