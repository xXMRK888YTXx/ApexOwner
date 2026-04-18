package com.xxmrk888ytxx.android.extension

import com.xxmrk888ytxx.android.logs.Logger
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.withContext
import kotlin.coroutines.CoroutineContext

/**
 * Executes the given [block] in the specified [context], catching any [Throwable]
 * except [CancellationException].
 *
 * @param context The CoroutineContext to run the block in.
 * @param writeErrorInLog If true, logs the exception using [Logger].
 * @param onMapException A transformation function to map the caught exception.
 * @return A [Result] containing the success value or the mapped failure.
 */
suspend inline fun <R> coRunCatching(
    context: CoroutineContext,
    writeErrorInLog: Boolean = true,
    crossinline onMapException: (Throwable) -> Throwable = { it },
    noinline block: suspend CoroutineScope.() -> R
): Result<R> = withContext(context) {
    try {
        Result.success(block())
    } catch (e: CancellationException) {
        // Must propagate CancellationException to support coroutine cooperative cancellation
        throw e
    } catch (e: Throwable) {
        if (writeErrorInLog) {
            Logger.writeDebugLog(e)
        }
        Result.failure(onMapException(e))
    }
}