package com.github.leon.files

import arrow.core.Option
import arrow.core.getOrElse
import com.google.common.base.Preconditions
import kt.times.times
import org.apache.poi.hssf.usermodel.HSSFWorkbook
import org.apache.poi.ss.usermodel.Cell
import org.apache.poi.ss.usermodel.Row
import org.apache.poi.ss.usermodel.Workbook
import org.apache.poi.xssf.streaming.SXSSFWorkbook
import org.springframework.expression.spel.standard.SpelExpressionParser
import org.springframework.expression.spel.support.StandardEvaluationContext
import java.io.File
import java.io.FileOutputStream
import java.io.OutputStream
import java.time.Instant
import javax.servlet.http.HttpServletResponse

class PoiExporter(vararg data: List<*>) {
    private var version: String? = null
    private var sheetNames = listOf("sheet")
    private var cellWidth = 8000
    private var headerRow: Int = 0
    private var headers: List<List<String>> = emptyList()
    private var columns: List<List<String>> = emptyList()
    private var data: List<List<*>> = emptyList()
    private var fileName: String? = null

    init {
        this.data = data.toList()
    }

    fun render(response: HttpServletResponse) {

        val workbook = this.export()
        response.setHeader("Content-disposition", "attachment; filename=" + Option.fromNullable(fileName)
                .getOrElse { Instant.now().epochSecond.toString() + ".xls" })
        response.contentType = CONTENT_TYPE
        var os: OutputStream = response.outputStream
        workbook.write(os)


    }

    fun export(): Workbook {
        Preconditions.checkNotNull(data, "data can not be null")
        Preconditions.checkNotNull(headers, "headers can not be null")
        Preconditions.checkNotNull(columns, "columns can not be null")
        Preconditions.checkArgument(
                data.size == sheetNames.size
                        && sheetNames.size == headers.size
                        && headers.size == columns.size,
                "data,sheetNames,headers and columns'length should be the same.(data:${data.size},sheetNames:${sheetNames.size},headers:${headers.size},columns:${columns.size})")
        Preconditions.checkArgument(cellWidth >= 0, "cellWidth can not be less than 0")
        val wb: Workbook
        if (VERSION_2003 == version) {
            wb = HSSFWorkbook()
            if (data.size > 1) {
                for (i in data.indices) {
                    val item = data[i]
                    Preconditions.checkArgument(
                            item.size < MAX_ROWS,
                            "Data ["
                                    + i
                                    + "] is invalid:invalid data size ("
                                    + item.size
                                    + ") outside allowable range (0..65535)")
                }
            } else if (data.size == 1 && data[0].size > MAX_ROWS) {
                data = dice(data[0], MAX_ROWS)
                val sheetName = sheetNames[0]
                for (i in data.indices) {
                    sheetNames += sheetName + if (i == 0) "" else i + 1
                }
                val header: List<String> = headers.first()
                val h: MutableList<List<String>> = mutableListOf()
                data.size.times {
                    h.add(header)
                }
                headers = h.toList()

                val column = columns.first()
                val c: MutableList<List<String>> = mutableListOf()
                data.size.times {
                    c.add(column)
                }
                columns = c.toList()
            }
        } else {
            wb = SXSSFWorkbook(1000)
        }
        if (data.isEmpty()) {
            return wb
        }
        for (i in data.indices) {
            val sheet = wb.createSheet(sheetNames[i])
            var row: Row
            var cell: Cell
            if (headers[i].isNotEmpty()) {
                row = sheet.createRow(0)
                if (headerRow <= 0) {
                    headerRow = HEADER_ROW
                }
                headerRow = Math.min(headerRow, MAX_ROWS)
                var h = 0
                val lenH = headers[i].size
                while (h < lenH) {
                    if (cellWidth > 0) {
                        sheet.setColumnWidth(h, cellWidth)
                    }
                    cell = row.createCell(h)
                    cell.setCellValue(headers[i][h])
                    h++
                }
            }

            var j = 0
            val len = data[i].size
            while (j < len) {
                row = sheet.createRow(j + headerRow)
                val obj = data[i][j]
                if (obj == null) {
                    j++
                    continue
                }

                processAsMap(columns[i], row, obj)
                j++
            }
        }
        return wb
    }

    fun export(file: File) {
        export().write(FileOutputStream(file))
    }

    fun version(version: String): PoiExporter {
        this.version = version
        return this
    }

    fun sheetName(sheetName: String): PoiExporter {
        this.sheetNames = listOf(sheetName)
        return this
    }

    fun sheetNames(vararg sheetName: String): PoiExporter {
        this.sheetNames = sheetName.toList()
        return this
    }

    fun cellWidth(cellWidth: Int): PoiExporter {
        this.cellWidth = cellWidth
        return this
    }

    fun headerRow(headerRow: Int): PoiExporter {
        this.headerRow = headerRow
        return this
    }

    fun header(vararg header: String): PoiExporter {
        this.headers = listOf(header.toList())
        return this
    }

    fun headers(vararg headers: List<String>): PoiExporter {
        this.headers = headers.toList()
        return this
    }

    fun column(vararg column: String): PoiExporter {
        this.columns = listOf(column.toList())
        return this
    }

    fun columns(vararg columns: List<String>): PoiExporter {
        this.columns = columns.toList()
        return this
    }

    fun fileName(fileName: String): PoiExporter {
        this.fileName = fileName
        return this
    }

    companion object {

        val VERSION_2003 = "2003"
        private val CONTENT_TYPE = "application/msexcel;charset=UTF-8"
        private val HEADER_ROW = 1
        private val MAX_ROWS = 65535

        fun data(vararg data: List<*>): PoiExporter {
            return PoiExporter(*data)
        }


        fun dice(num: List<*>, chunkSize: Int): List<List<*>> {
            val size = num.size
            val chunkNum = size / chunkSize + if (size % chunkSize == 0) 0 else 1
            val result: MutableList<List<*>> = mutableListOf()
            for (i in 0 until chunkNum) {
                result.add(num.subList(i * chunkSize, if (i == chunkNum - 1) size else (i + 1) * chunkSize))
            }
            return result.toList()
        }

        private fun processAsMap(columns: List<String>, row: Row, obj: Any) {
            val parser = SpelExpressionParser()
            val context = StandardEvaluationContext(obj)
            var cell: Cell
            if (columns.isEmpty()) {
                obj::class.java.declaredFields.map { it.name }
            } else {
                columns
            }.forEachIndexed { index, column ->
                val v = parser.parseExpression(column).getValue(context, String::class.java)
                cell = row.createCell(index)
                cell.setCellValue(v)
            }
        }
    }
}