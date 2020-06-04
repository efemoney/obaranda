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

package com.efemoney.obaranda.data.internal

import com.efemoney.obaranda.data.CommentRepo
import com.efemoney.obaranda.data.CommentsRequest
import com.efemoney.obaranda.data.CommentsResult
import com.efemoney.obaranda.data.comments.DisqusCommentsApi
import com.efemoney.obaranda.data.comments.model.Includes.approved
import com.efemoney.obaranda.dispatchers.Dispatchers
import kotlinx.coroutines.invoke
import javax.inject.Inject

internal class RealCommentRepo @Inject constructor(
  private val api: DisqusCommentsApi,
  private val mapper: CommentMapper,
  private val dispatchers: Dispatchers
) : CommentRepo {

  override suspend fun getComments(request: CommentsRequest): CommentsResult = dispatchers.network {
    runCatching {
      CommentsResult(api.listPosts(request.commentThreadId, listOf(approved)).let(mapper::mapJsonList))
    }.getOrElse {
      CommentsResult(emptyList(), error = it.unwrapHttpMessage())
    }
  }
}