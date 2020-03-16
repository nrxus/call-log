package com.nrxus.calllog

sealed class Result<out T, out E> {
    class Ok<T>(val v: T) : Result<T, Nothing>()
    class Err<E>(val e: E) : Result<Nothing, E>()

    fun <R> map(mapper: (T) -> R): Result<R, E> = when (this) {
        is Ok -> Ok(mapper(this.v))
        is Err -> this
    }
}

fun <T, E> Result<T?, E>.transpose(): Result<T, E>? = when (this) {
    is Result.Ok -> when (val v = this.v) {
        null -> null
        else -> Result.Ok(v)
    }
    is Result.Err -> this
}