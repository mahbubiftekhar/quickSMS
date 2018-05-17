package Util.Python

import java.io.File
import kotlin.coroutines.experimental.buildIterator
import kotlin.coroutines.experimental.buildSequence

fun File.readSeq(by : ((String) -> Iterable<String>)? = null) : Sequence<String> {
    val func = by ?: { it.lines() }
    val data = this.bufferedReader().use { it.readText() }
    return buildSequence { yieldAll(func(data)) }
}

fun <T> Sequence<Sequence<T>>.flatten() : Sequence<T> {
    return buildSequence {
        for (seq in this@flatten) {
            yieldAll(seq)
        }
    }
}

fun <T> Sequence<T>.unpack() : T = this.first()
fun <T> Sequence<T>.next() : T = this.first()

fun <T> Sequence<T>.consume() {
    this.forEach {}
}

fun <T> Sequence<T>.cache() : Iterator<T> {
    return buildIterator {
        yieldAll(this@cache)
    }
}

fun <T> Sequence<T>.tee() : Pair<Sequence<T>, Sequence<T>> {
    val withMemory = this.cache()
    return buildSequence { yieldAll(withMemory) } to buildSequence { yieldAll(withMemory) }
}

fun <T> Sequence<T>.pairwise() : Sequence<Pair<T, T>> {
    val (a, b) = this.tee()
    b.next()
    return a.zip(b)
}