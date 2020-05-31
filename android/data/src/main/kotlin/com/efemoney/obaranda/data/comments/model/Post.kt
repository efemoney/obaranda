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
data class Post(
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