package ru.skillbranch.kotlinexample.extensions

fun String.clearPhone() = replace("[^+\\d]".toRegex(), "")

