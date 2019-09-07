package com.github.leon.jaxb


import java.io.File
import java.io.StringReader
import java.io.StringWriter
import javax.xml.bind.JAXBContext
import javax.xml.bind.Marshaller

object JaxbKit {


    fun <T> unmarshal(src: String, clazz: Class<T>): T? {
        val avm = JAXBContext.newInstance(clazz).createUnmarshaller()
        var result = avm.unmarshal(StringReader(src)) as T

        return result
    }

    fun <T> unmarshal(xmlFile: File, clazz: Class<T>): T? {

        val avm = JAXBContext.newInstance(clazz).createUnmarshaller()
        var result = avm.unmarshal(xmlFile) as T
        return result
    }


    fun marshal(jaxbElement: Any): String {
        val sw = StringWriter()
        val fm = JAXBContext.newInstance(jaxbElement.javaClass).createMarshaller()
        fm.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, true)
        fm.marshal(jaxbElement, sw)


        return sw.toString()
    }
}