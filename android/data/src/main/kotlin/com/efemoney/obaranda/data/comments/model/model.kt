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

package com.efemoney.obaranda.data.comments.model

import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
internal data class Author(
  val id: String,
  val username: String,
  val name: String,
  val about: String,
  val avatar: Avatar,
  val location: String,
  val joinedAt: String,
  val url: String,

  val profileUrl: String,
  val signedUrl: String,

  val isPrivate: Boolean,
  val isPrimary: Boolean,
  val isAnonymous: Boolean,
  val isPowerContributor: Boolean,
  val disable3rdPartyTrackers: Boolean
)

@JsonClass(generateAdapter = true)
internal data class Avatar(
  override val cache: String,
  override val permalink: String,
  val isCustom: Boolean,
  val small: Image,
  val large: Image
) : Image(permalink, cache)

@JsonClass(generateAdapter = true)
internal open class Image(
  open val permalink: String,
  open val cache: String
)

@JsonClass(generateAdapter = true)
internal data class Media(
  val id: Long,
  val title: String,
  val description: String,
  val providerName: String,
  val post/* id */: String,

  val html: String,
  val forum: String,
  val thread: String,
  val type: String, // Int
  val mediaType: String, // Int
  val metadata: Metadata,

  val url: String,
  val urlRedirect: String,
  val resolvedUrl: String,
  val resolvedUrlRedirect: String,

  val htmlWidth: Int?,
  val htmlHeight: Int?,
  val thumbnailUrl: String,
  val thumbnailURL: String,
  val thumbnailWidth: Int,
  val thumbnailHeight: Int
)

@JsonClass(generateAdapter = true)
internal data class Metadata(
  val create_method: String,
  val thumbnail: String
)

@JsonClass(generateAdapter = true)
internal data class Post(
  val id: Long,
  val parent: Long?,
  val points: Int,
  val likes: Int,
  val dislikes: Int,
  val numReports: Int,
  val forum: String,
  val thread: String,
  val message: String,
  val raw_message: String,
  val createdAt: String,

  val author: Author,
  val media: List<Media>,
  val moderationLabels: List<String>,

  val sb: Boolean,
  val canVote: Boolean,
  val isSpam: Boolean,
  val isEdited: Boolean,
  val isFlagged: Boolean,
  val isHighlighted: Boolean,
  val isApproved: Boolean,
  val isDeleted: Boolean,
  val isDeletedByAuthor: Boolean
)

@Suppress("EnumEntryName")
internal enum class Includes { unapproved, approved, spam, deleted, flagged, highlighted }