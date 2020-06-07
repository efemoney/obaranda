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

package com.efemoney.obaranda.model

import com.efemoney.obaranda.await
import com.efemoney.obaranda.parse
import com.google.cloud.firestore.Firestore
import com.google.cloud.firestore.SetOptions
import java.time.Instant
import javax.inject.Inject

internal class Settings @Inject constructor(private val firestore: Firestore) {

  suspend fun getLastPolledTime(): Instant? {
    return firestore.collection("internal")
      .document("settings")
      .await()
      .getString("last-poll")
      ?.parse()
  }

  suspend fun setLastPolledTime(atWhen: Instant) {
    firestore.collection("internal")
      .document("settings")
      .set(mapOf("last-poll" to "$atWhen"), SetOptions.merge())
      .await()
  }

  suspend fun getLastPolledCommentTime(): Instant? {
    return firestore
      .collection("internal")
      .document("settings")
      .await()
      .getString("last-comment-poll")
      ?.parse()
  }

  suspend fun setLastPolledCommentTime(atWhen: Instant) {
    firestore.collection("internal")
      .document("settings")
      .set(mapOf("last-comment-poll" to "$atWhen"), SetOptions.merge())
      .await()
  }
}