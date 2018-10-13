package com.suushiemaniac.cubing.bld.util

object ClosureUtil {
    fun <T, U, R> ((T, U) -> R).curry(arg: T): (U) -> R {
        return { this(arg, it) }
    }

    fun <T, U, R> ((T, U) -> R).curryRight(arg: U): (T) -> R {
        return { this(it, arg) }
    }

    fun <T, R> ((T) -> R).curry(arg: T): () -> R {
        return { this(arg) }
    }

    fun <T, U, R> ((T, U) -> R).reversingArguments(): (U, T) -> R {
        return { a, b -> this(b, a) }
    }

    fun <T> T.constant(): () -> T {
        return { this }
    }

    fun <T, R> R.always(): (T) -> R {
        return { this }
    }

    fun <T, R> (() -> R).always(): (T) -> R {
        return { this() }
    }

    fun <T, R> ((T) -> R).tryCatch(): (T) -> R {
        return {
            try {
                this(it)
            } catch (e: Exception) {
                throw RuntimeException(e)
            }
        }
    }
}