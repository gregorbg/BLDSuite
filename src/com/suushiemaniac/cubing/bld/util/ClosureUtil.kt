package com.suushiemaniac.cubing.bld.util

object ClosureUtil {
    fun <T, U, R> curry(unBound: (T, U) -> R, arg: T): (U) -> R {
        return { unBound(arg, it) }
    }

    fun <T, U, R> curryRight(unBound: (T, U) -> R, arg: U): (T) -> R {
        return { unBound(it, arg) }
    }

    fun <T, R> curry(unBound: (T) -> R, arg: T): () -> R {
        return { unBound(arg) }
    }

    fun <T, U, R> reversingArguments(original: (T, U) -> R): (U, T) -> R {
        return { a, b -> original(b, a) }
    }

    fun <T> constant(value: T): () -> T {
        return { value }
    }

    fun <T, R> always(value: R): (T) -> R {
        return { value }
    }

    fun <T, R> always(value: () -> R): (T) -> R {
        return { value() }
    }

    fun <T, R> tryCatch(throwing: (T) -> R): (T) -> R {
        return {
            try {
                throwing(it)
            } catch (e: Exception) {
                throw RuntimeException(e)
            }
        }
    }
}