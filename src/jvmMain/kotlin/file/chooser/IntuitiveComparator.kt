package file.chooser

import java.io.File

internal class IntuitiveComparator : Comparator<String> {
    private lateinit var first: String
    private lateinit var second: String

    override fun compare(firstFile: String, secondFile: String): Int {
        first = firstFile
        second = secondFile
        while (first.isNotEmpty() && second.isNotEmpty()) {
            val fromFirst = first.first()
            val fromSecond = second.first()

            val result = when {
                fromFirst.isDigit() && fromSecond.isDigit() -> compareNumbers()
                fromFirst.isLetter() && fromSecond.isLetter() -> compareLetters()

                fromFirst.isDigit() && !fromSecond.isDigit() -> {
                    drop(); -1
                }
                fromFirst.isLetter() && !fromSecond.isLetter() -> {
                    drop(); 1
                }

                fromSecond.isDigit() -> {
                    drop(); 1
                }
                fromSecond.isLetter() -> {
                    drop(); -1
                }

                else -> {
                    drop(); fromFirst.compareTo(fromSecond)
                }
            }

            if (result != 0)
                return result
        }
        return first.length - second.length
    }

    private fun drop(f: Int = 1, s: Int = f) {
        first = first.substring(f)
        second = second.substring(s)
    }

    private fun compareNumbers(): Int {
        val firstDigits = first.run { substring(0, indexOfNonDigit()) }
        val secondDigits = second.run { substring(0, indexOfNonDigit()) }

        drop(firstDigits.length, secondDigits.length)

        return firstDigits.toBigInteger().compareTo(secondDigits.toBigInteger())
    }

    private fun String.indexOfNonDigit() = indexOfFirst { !it.isDigit() }.takeIf { it != -1 } ?: length

    private fun compareLetters(): Int {
        val first = first.first().lowercaseChar()
        val second = second.first().lowercaseChar()

        drop()

        return first.code - second.code
    }
}