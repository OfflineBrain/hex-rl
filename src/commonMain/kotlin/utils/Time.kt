package utils

import kotlinx.datetime.Clock

inline fun measureTimeMillis(block: () -> Unit): Long {
    val start = Clock.System.now()
    block()
    val end = Clock.System.now()
    return (end - start).inWholeMilliseconds
}
