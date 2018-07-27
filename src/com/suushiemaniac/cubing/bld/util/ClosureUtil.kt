package com.suushiemaniac.cubing.bld.util

object ClosureUtil {
    fun <T, U, R> curry(unBound: (T, U) -> R, arg: T): (U) -> R {
        return { fnArg -> unBound(arg, fnArg) }
    }

    fun <T, U, R> curryRight(unBound: (T, U) -> R, arg: U): (T) -> R {
        return { fnArg -> unBound(fnArg, arg) }
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
        return { _ -> value }
    }

    fun <T, R> always(value: () -> R): (T) -> R {
        return { _ -> value() }
    }

    fun <T, R> tryCatch(throwing: (T) -> R): (T) -> R {
        return { t ->
            try {
                throwing(t)
            } catch (e: Exception) {
                throw RuntimeException(e)
            }
        }
    }
}