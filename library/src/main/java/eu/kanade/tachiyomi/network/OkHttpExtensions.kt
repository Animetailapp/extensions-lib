package eu.kanade.tachiyomi.network

import kotlinx.coroutines.suspendCancellableCoroutine
import okhttp3.Call
import okhttp3.Callback
import okhttp3.Response
import rx.Observable
import java.io.IOException
import kotlin.coroutines.resume
import kotlin.coroutines.resumeWithException


fun Call.asObservable(): Observable<Response> {
    throw Exception("Stub!")
}

fun Call.asObservableSuccess(): Observable<Response> {
    throw Exception("Stub!")
}

suspend fun Call.await(): Response {
    return suspendCancellableCoroutine { continuation ->
        enqueue(
                object : Callback {
                    override fun onResponse(call: Call, response: Response) {
                        if (!response.isSuccessful) {
                            continuation.resumeWithException(Exception("HTTP error ${response.code}"))
                            return
                        }

                        continuation.resume(response)
                    }

                    override fun onFailure(call: Call, e: IOException) {
                        // Don't bother with resuming the continuation if it is already cancelled.
                        if (continuation.isCancelled) return
                        continuation.resumeWithException(e)
                    }
                }
        )

        continuation.invokeOnCancellation {
            try {
                cancel()
            } catch (ex: Throwable) {
                // Ignore cancel exception
            }
        }
    }
}