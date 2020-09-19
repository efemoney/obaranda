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

package com.efemoney.obaranda.data.networking

import android.content.Context
import android.net.ConnectivityManager
import androidx.core.content.getSystemService
import dagger.hilt.android.qualifiers.ApplicationContext
import okhttp3.Interceptor
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.Protocol
import okhttp3.Response
import okhttp3.ResponseBody.Companion.toResponseBody
import javax.inject.Inject

class ConnectivityInterceptor @Inject constructor(
  @ApplicationContext private val context: Context
) : Interceptor {

  // Return empty response if no network
  override fun intercept(chain: Interceptor.Chain): Response {
    val cm = context.getSystemService<ConnectivityManager>()!!
    val network = cm.activeNetworkInfo

    if (network == null || !network.isConnectedOrConnecting) {
      return Response.Builder()
        .protocol(Protocol.HTTP_1_1)
        .code(400)
        .message("You are not online.\nPlease check your internet connection.")
        .sentRequestAtMillis(-1)
        .receivedResponseAtMillis(System.currentTimeMillis())
        .request(chain.request())
        .body("\"no dice\"".toResponseBody("application/json".toMediaType()))
        .build()
    }

    return chain.proceed(chain.request())
  }
}
