package com.arpit.notes.util

fun randomString(n: Int = 10): String {
    val allowedChars = ('A'..'Z') + ('a'..'z') + ('0'..'9')
    return (1..n)
        .map { allowedChars.random() }
        .joinToString("")
}