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

package com.efemoney.obaranda

import java.time.*
import java.time.format.DateTimeFormatter
import java.time.format.DateTimeFormatterBuilder

/**
 * Parses an instant from a time allowing for standard UTC (i.e. "Z") or UTC + offset.
 *
 * @return an Instant representation of the time
 */
@Suppress("NOTHING_TO_INLINE")
inline fun String.parsePossiblyOffsetInstant(): Instant =
  if (endsWith("Z")) Instant.parse(this) else OffsetDateTime.parse(this).toInstant()


internal val formatter = DateTimeFormatterBuilder()
  .append(DateTimeFormatter.ISO_LOCAL_DATE)
  .optionalStart()
  .appendLiteral('T')
  .optionalEnd()
  .optionalStart()
  .appendLiteral(' ')
  .optionalEnd()
  .append(DateTimeFormatter.ISO_LOCAL_TIME)
  .optionalStart()
  .appendZoneId()
  .optionalEnd()
  .toFormatter()
  .withZone(ZoneId.of("Africa/Lagos"))

internal inline fun <reified T> String.parse(): T {
  val f = when (T::class) {
    Instant::class -> Instant::from
    LocalDateTime::class -> LocalDateTime::from
    ZonedDateTime::class -> ZonedDateTime::from
    OffsetDateTime::class -> OffsetDateTime::from
    else -> error("Unsupported...yet")
  }
  return formatter.parse(this, f::invoke) as T
}