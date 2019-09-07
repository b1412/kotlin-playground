package com.github.leon.files.parser

import com.github.leon.files.convert.CellConverter
import com.google.common.collect.Lists

data class FileParser(

        var name: String? = null,

        var sheetNo: Int = 0,

        var start: Int = 0,

        var end: Int = 0,

        var rowFilter: String? = null,

        var preExcelProcessor: String? = null,

        var postExcelProcessor: String? = null,

        var preRowProcessor: RowProcessor? = null,

        var postRowProcessor: RowProcessor? = null,

        var cellParsers: MutableList<CellParser> = Lists.newArrayList()
) {

    fun addCell(cellParser: CellParser): FileParser {
        cellParsers.add(cellParser)
        return this
    }

    fun addCell(index: Int, attribute: String): FileParser {
        cellParsers.add(CellParser.create(index, attribute))
        return this
    }

    fun addCell(index: Int, attribute: String, cellConvertor: CellConverter): FileParser {
        cellParsers.add(CellParser.create(index, attribute, cellConvertor))
        return this
    }

    /* fun addCell(index: Int, attribute: String, convert: CellConverter<Any>, validate: CellValidate): FileParser {
         cellParsers.add(CellParser.create(index, attribute, convert, validate))
         return this
     }*/

}
