package com.darkn0va.timber.`fun`

import java.util.*

class Triangle(private val x: Int, private val y: Int) {
    fun createHollowTriangle() {
        for (i in 1..y) {
            for (j in 1..x) {
                if (i == y || j == 1 || j == i) {
                    print("* ")
                } else {
                    print("  ")
                }
            }
            println()
        }
    }
}

fun main() {
    val scanner = Scanner(System.`in`)
    print("Zadej počet řádků (y): ")
    val y = scanner.nextInt()
    print("Zadej počet sloupců (x): ")
    val x = scanner.nextInt()

    val triangle = Triangle(x, y)
    triangle.createHollowTriangle()
}
