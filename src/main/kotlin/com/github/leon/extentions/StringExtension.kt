package com.github.leon.extentions

import com.github.stuxuhai.jpinyin.PinyinFormat
import com.github.stuxuhai.jpinyin.PinyinFormat.WITHOUT_TONE
import com.github.stuxuhai.jpinyin.PinyinHelper
import java.io.BufferedReader
import java.io.InputStreamReader


fun String.pingYin(separator: String = " ", format: PinyinFormat = WITHOUT_TONE): String {
    return PinyinHelper.convertToPinyinString(this, separator, format)
}

fun String.remainLastIndexOf(string: String): String {
    return this.substring(this.lastIndexOf(string).inc())
}


fun String.execCmd(): List<String> {
    val rt = Runtime.getRuntime()
    var p: Process? = null
    val out = mutableListOf<String>()
    try {
        p = rt.exec(this)
        val br = BufferedReader(InputStreamReader(p.inputStream))
        var line = br.readLine()
        val response = StringBuilder()
        while (line != null) {
            out.add(line)
            line = br.readLine()
        }
        println(response)
    } catch (e: Exception) {
        e.printStackTrace()
    } finally {
        if (p != null) {
            try {
                p.waitFor()
            } catch (e: InterruptedException) {
                e.printStackTrace()
            }
            p.destroy()
        }
    }
    return out.toList()
}