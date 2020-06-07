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
@file:Suppress("BlockingMethodInNonBlockingContext")

package com.efemoney.obaranda

import com.efemoney.obaranda.model.*
import com.squareup.moshi.JsonClass
import com.squareup.moshi.JsonReader
import com.squareup.moshi.Moshi
import com.trickl.palette.Palette.from
import io.ktor.application.call
import io.ktor.http.HttpStatusCode
import io.ktor.response.respond
import kotlinx.coroutines.Dispatchers.IO
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.invoke
import okhttp3.ResponseBody
import okhttp3.internal.closeQuietly
import okio.BufferedSource
import org.apache.commons.lang3.StringEscapeUtils.unescapeHtml4
import retrofit2.http.GET
import retrofit2.http.Streaming
import retrofit2.http.Url
import java.lang.Integer.toHexString
import java.time.Instant
import javax.imageio.ImageIO

val PollSignal: RouteHandler = {
  coroutineScope {
    call.respond(HttpStatusCode.Accepted)
    updateCommentCounts()
    updateComics()
  }
}

private suspend fun RouteHandlerContext.updateComics() {

  val lastPolledAt = settings.getLastPolledTime() ?: Instant.EPOCH
  val feed = streamingApi.stream("http://obaranda.com/json").source()

  for (item in updatedFeedItems(moshi, feed, after = lastPolledAt)) {
    // Items are either added/modified or deleted
    // An item is deleted when its images.length is 0 else its added/modified

    if (item.images.isEmpty())
      comics.deleteComicByPage(item.page)
    else {
      val comic = itemToComic(item)
      comics.putComic(comic)
    }
  }

  settings.setLastPolledTime(Instant.now())
}

private suspend fun RouteHandlerContext.updateCommentCounts() {
  // Strategy is; get all the latest disqus comments after the last time we polled
  // Map comments to their respective threads ids and de-dup the list
  // Get the set of threads by id and update the comments count for all of them

  val apiSecret = config.property("disqus.apiSecret").getString()
  val lastPolledAt = settings.getLastPolledCommentTime() ?: Instant.EPOCH

  val threadIds = disqusApi.listPosts(apiSecret, lastPolledAt).map(DisqusPost::thread).distinct().ifEmpty { return }
  val threads = disqusApi.listThreads(apiSecret, threadIds).ifEmpty { return }

  for (thread in threads) {
    val page = comics.getPageByThreadId(thread.id) ?: continue
    comics.putCommentsCount(page, thread.posts)
  }

  settings.setLastPolledCommentTime(Instant.now())
}

private suspend fun RouteHandlerContext.itemToComic(item: FeedItem): Comic {

  val apiSecret = config.property("disqus.apiSecret").getString()

  val (threadId, count) = disqusApi.threadByUrl(apiSecret, buildList {
    // Disqus has urls saved as www.obaranda.com / obaranda.com depending on how the users browser accesses the page
    // We try to lookup the comment thread by both variations of the url

    add(item.url)

    val www = "www"
    val colonSlash = "://"

    val otherUrl = if (www in item.url)
      item.url.replace(www, "")
    else
      item.url.replace(colonSlash, "$colonSlash$www.")

    add(otherUrl)
  })

  val imagesMetadata = computeImagesMetadata(streamingApi, item.images)

  return Comic(
    page = item.page,
    url = item.url,
    title = unescapeHtml4(item.title),
    commentsThreadId = threadId,
    commentsCount = count,
    pubDate = item.date_published.toString(),
    images = item.images.zip(imagesMetadata) { image, metadata ->
      Image(
        image.url,
        image.alt,
        Size(metadata.width, metadata.height),
        Palette(metadata.muted, metadata.vibrant)
      )
    },
    post = item.post.let {
      Post(
        it.title?.let(::unescapeHtml4),
        it.body?.let(::unescapeHtml4),
        it.transcript?.let(::unescapeHtml4)
      )
    },
    author = Author(item.author.name, url = null)
  )
}

private suspend fun computeImagesMetadata(streamingApi: StreamingApi, images: List<FeedImage>) = IO {
  images.map { img ->
    val image = ImageIO.read(streamingApi.stream(img.url).byteStream())
    val palette = from(image).generate()
    ImageMetadata(
      width = image.width,
      height = image.height,
      muted = palette.mutedVariationAsHexOrNull(),
      vibrant = palette.vibrantVariationAsHexOrNull()
    )
  }
}

private suspend fun updatedFeedItems(moshi: Moshi, feed: BufferedSource, after: Instant) = IO {

  buildList {

    val reader = JsonReader.of(feed)
    val itemAdapter = moshi.adapter(FeedItem::class.java)

    reader.beginObject()
    while (reader.nextName() != "items") reader.skipValue()

    reader.beginArray()
    while (reader.hasNext()) {
      val item = itemAdapter.fromJson(reader)!!
      if (item.date_published.isAfter(after)) add(item) else break
    }

    reader.closeQuietly()
  }
}

private fun com.trickl.palette.Palette.mutedVariationAsHexOrNull() =
  (mutedSwatch ?: lightMutedSwatch ?: darkMutedSwatch)?.hex()

private fun com.trickl.palette.Palette.vibrantVariationAsHexOrNull() =
  (vibrantSwatch ?: lightVibrantSwatch ?: darkVibrantSwatch)?.hex()

private fun com.trickl.palette.Palette.Swatch.hex() = "#" + toHexString(rgb)


interface StreamingApi {

  @Streaming
  @GET
  suspend fun stream(@Url url: String): ResponseBody
}

@JsonClass(generateAdapter = true)
data class FeedItem(
  val page: Int,
  val url: String,
  val title: String,
  val date_published: Instant,
  val author: FeedAuthor,
  val images: List<FeedImage>,
  val post: FeedPost
)

@JsonClass(generateAdapter = true)
data class FeedAuthor(val name: String)

@JsonClass(generateAdapter = true)
data class FeedImage(val url: String, val alt: String?)

@JsonClass(generateAdapter = true)
data class FeedPost(val title: String?, val body: String?, val transcript: String?)

data class ImageMetadata(
  val width: Int,
  val height: Int,
  val muted: String?,
  val vibrant: String?
)