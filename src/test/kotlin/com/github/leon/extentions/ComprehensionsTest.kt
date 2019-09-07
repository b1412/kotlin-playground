package com.github.leon.extentions
import org.junit.Test

class ComprehensionsTest {
    @Test
    fun hey() {
        val sequence = from(
                listOf(1, 2),
                { listOf("a", "b", "c") }
        ) where { a, b -> Pair(a, b) != Pair(2, "b") } select { a, b -> Pair(a, b) }

        println(sequence.toList() == (listOf(Pair(1, "a"), Pair(1, "b"), Pair(1, "c"), Pair(2, "a"), Pair(2, "c"))))

    }

    @Test
    fun hey2() {
        val result = from(
                (1..10), { (5..20) }
        ) where { a, b -> a + b == 10 } select { a, b -> Pair(a, b) }
        println(result.toList())
    }
}