package ru.skillbranch.kotlinexample.extensions

fun <T> List<T>.dropLastUntil(predicate: (T) -> Boolean): List<T> {
    return this.filter(predicate).last().let {
        val index = this.indexOf(it)
        if (index == 0) listOf()
        else this.subList(0, index)
    }
}