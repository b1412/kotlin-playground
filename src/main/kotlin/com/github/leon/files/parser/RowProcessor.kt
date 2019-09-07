package com.github.leon.files.parser

interface RowProcessor {
    fun exec(model: Any, list: List<String>, rowIndex: Int)
}
