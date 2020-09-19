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

import com.efemoney.obaranda.data.comments.model.Post
import com.efemoney.obaranda.data.model.Comment
import com.efemoney.obaranda.data.model.Media
import com.efemoney.obaranda.data.model.MediaType
import javax.inject.Inject
import com.efemoney.obaranda.data.comments.model.Media as ApiMedia

internal class CommentMapper @Inject constructor() {

  fun mapJsonList(posts: List<Post>): List<Comment> {

    val rootList = posts
      .filter { it.parent == null }

    val parentChildrenMap = posts
      .filter { it.parent != null }
      .groupBy { it.parent!! }

    return rootList.map { it.toComment(parentChildrenMap) }
  }

  private fun Post.toComment(children: Map<Long, List<Post>>): Comment {

    return Comment(
      id = id,
      avatar = with(author.avatar) { if (isCustom) permalink else null },
      userName = author.username,
      displayName = author.name,
      votesCount = points,
      upvoteCount = likes,
      downvoteCount = dislikes,
      rawText = raw_message,
      media = media.map(::mapMedia),
      children = children[id]?.map { it.toComment(children) } ?: emptyList()
    )
  }

  private fun mapMedia(media: ApiMedia): Media {

    return Media(
      id = media.id,
      description = media.description,
      provider = media.providerName,
      url = media.url,
      type = resolveMediaType(media),
      resolvedUrl = media.resolvedUrl,
      previewUrl = "https:" + media.thumbnailUrl,
      previewWidth = media.thumbnailWidth,
      previewHeight = media.thumbnailHeight
    )
  }

  private fun resolveMediaType(media: ApiMedia): MediaType {

    // rules are arbitrary and depend on what I discover from the disqus api
    return when (media.providerName.toLowerCase()) {
      "youtube" -> MediaType.video
      else -> MediaType.image
    }
  }
}
