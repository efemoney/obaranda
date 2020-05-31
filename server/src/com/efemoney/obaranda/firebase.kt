/*
 * Copyright 2020 Efeturi Money. All rights reserved.
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
@file:Suppress("UNCHECKED_CAST", "BlockingMethodInNonBlockingContext")

package com.efemoney.obaranda

import com.google.api.core.ApiFuture
import com.google.api.core.ApiFutureCallback
import com.google.api.core.ApiFutures
import com.google.cloud.firestore.*
import com.google.common.util.concurrent.MoreExecutors
import com.google.firebase.FirebaseApp
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.cloud.FirestoreClient
import dagger.Module
import dagger.Provides
import kotlinx.coroutines.CancellableContinuation
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.Job
import kotlinx.coroutines.suspendCancellableCoroutine
import java.util.concurrent.ExecutionException
import kotlin.coroutines.Continuation
import kotlin.coroutines.resume
import kotlin.coroutines.resumeWithException

/**
 * Copied from https://github.com/JetBrains/teamcity-google-agent/blob/master/google-cloud-server/src/main/kotlin/jetbrains/buildServer/clouds/google/connector/ApiFuture.kt
 *
 * Awaits for completion of the future without blocking a thread.
 *
 * This suspending function is cancellable.
 * If the [Job] of the current coroutine is completed while this suspending function is waiting, this function
 * stops waiting for the future and immediately resumes with [CancellationException].
 *
 * Note, that `ListenableFuture` does not support removal of installed listeners, so on cancellation of this wait
 * a few small objects will remain in the `ListenableFuture` list of listeners until the future completes. However, the
 * care is taken to clear the reference to the waiting coroutine itself, so that its memory can be released even if
 * the future never completes.
 */
suspend fun <T> ApiFuture<T>.await(): T {
  try {
    if (isDone) return get() as T
  } catch (e: ExecutionException) {
    throw e.cause ?: e // unwrap original cause from ExecutionException
  }

  return suspendCancellableCoroutine { cont: CancellableContinuation<T> ->
    val callback = ContinuationCallback(cont)
    ApiFutures.addCallback(this, callback, MoreExecutors.directExecutor())
    cont.invokeOnCancellation {
      cancel(false)
      callback.cont = null // clear the reference to continuation from the future's callback
    }
  }
}

private class ContinuationCallback<T>(@Volatile @JvmField var cont: Continuation<T>?) : ApiFutureCallback<T> {

  override fun onSuccess(result: T?) {
    cont?.resume(result as T)
  }

  override fun onFailure(t: Throwable) {
    cont?.resumeWithException(t)
  }
}


suspend inline fun Query.await(): QuerySnapshot = get().await()

suspend inline fun DocumentReference.await(): DocumentSnapshot = get().await()

inline fun <reified T : Any?> DocumentSnapshot.data(): T? = toObject(T::class.java)

inline fun <reified T : Any?> QueryDocumentSnapshot.data(): T = toObject(T::class.java)


@Module
interface FirebaseAdminModule {

  companion object {

    @Provides
    @ApplicationScope
    fun app(): FirebaseApp = FirebaseApp.initializeApp()

    @Provides
    @ApplicationScope
    fun firestore(app: FirebaseApp): Firestore = FirestoreClient.getFirestore(app)

    @Provides
    @ApplicationScope
    fun auth(app: FirebaseApp): FirebaseAuth = FirebaseAuth.getInstance(app)
  }
}