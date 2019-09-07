package com.github.leon.files

import java.util.*

class CellableMap : LinkedHashMap<String, Any>(), Cellable {

    val headerCellValue: Array<String>
        get() = keys.toTypedArray()

    override val cellValues: Array<String>
        get() {

            val cellValues = arrayOf<String>()
            val keys = keys
            var index = 0
            for (key in keys) {
                cellValues[index++] = get(key).toString()
            }
            return cellValues
        }

}
