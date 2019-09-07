package com.github.leon.files.parser


import com.github.leon.files.convert.CellConverter
import com.github.leon.files.validate.CellValidate

data class CellParser(

        var index: Int = 0,

        var attribute: String? = null,

        var convert: CellConverter? = null,

        var validate: CellValidate? = null
) {

    companion object {

        internal fun create(index: Int, attribute: String): CellParser {
            val cellParser = CellParser()
            cellParser.index = index
            cellParser.attribute = attribute
            return cellParser
        }

        internal fun create(index: Int, attribute: String, convert: CellConverter): CellParser {
            val cellParser = create(index, attribute)
            cellParser.convert = convert
            return cellParser
        }


        internal fun create(index: Int, attribute: String, convert: CellConverter, validate: CellValidate): CellParser {
            val cellParser = create(index, attribute)
            cellParser.convert = convert
            cellParser.validate = validate
            return cellParser
        }
    }

}
