/*
 * Copyright 2026 Proify, Tomakino
 * Licensed under the Apache License, Version 2.0
 * http://www.apache.org/licenses/LICENSE-2.0
 */

package io.github.proify.lyricon.cmprovider.xposed

import android.media.MediaMetadata
import android.util.Log
import kotlinx.serialization.Serializable
import org.json.JSONObject
import java.util.concurrent.ConcurrentHashMap

object MediaMetadataCache {
    private val map = ConcurrentHashMap<Long, Metadata>()

    fun save(metadata: MediaMetadata): Metadata? {
        val id =
            metadata.getString(MediaMetadata.METADATA_KEY_MEDIA_ID)?.toLongOrNull() ?: return null

        if (map.containsKey(id)) return map[id]

        val title = metadata.getString(MediaMetadata.METADATA_KEY_TITLE)
        val artist = metadata.getString(MediaMetadata.METADATA_KEY_ARTIST)
        val duration = metadata.getLong(MediaMetadata.METADATA_KEY_DURATION)
        val lyricInfo = metadata.getString("lyricInfo")

        metadata.keySet().forEach {
            runCatching {
                Log.i(CloudMusic.TAG, "key:" + it + " value:" + metadata.getString(it))
            }
        }


        Log.i(CloudMusic.TAG, "lyricInfo:" + lyricInfo.orEmpty())
        val lyric = runCatching {
            if (lyricInfo != null) {
                val jo = JSONObject(lyricInfo)
                jo.getString("lyric")
            } else null
        }.getOrNull()

        val data = Metadata(id, title, artist, duration, lyric)
        map[id] = data
        return data
    }

    fun get(id: Long): Metadata? = map[id]
}

@Serializable
data class Metadata(
    val id: Long,
    val title: String?,
    val artist: String?,
    val duration: Long,
    val lyric: String?
)