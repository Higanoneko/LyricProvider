/*
 * Copyright 2026 Proify, Tomakino
 * Licensed under the Apache License, Version 2.0
 * http://www.apache.org/licenses/LICENSE-2.0
 */

package io.github.proify.lyricon.amprovider.xposed.parser

import io.github.proify.lyricon.amprovider.xposed.MediaMetadataCache
import io.github.proify.lyricon.amprovider.xposed.model.AppleSong

object AppleSongParser {

    /** 解析 Apple Music 原生 Song 对象为 [AppleSong]，所有取值失败时安全降级。 */
    fun parse(songNative: Any): AppleSong = AppleSong().apply {
        adamId = callMethod(songNative, "getAdamId")?.toString()

        callMethod(songNative, "getAgents")?.let {
            agents = LyricsAgentParser.parse(it)
        }

        duration = callMethod(songNative, "getDuration") as? Int ?: 0

        callMethod(songNative, "getSections")?.let {
            lyrics = LyricsSectionParser.parse(it).mergeLyrics()
        }

        fillMetadataFromCache()
    }

    /** 原生对象不含名称信息，用 [MediaMetadataCache] 里的元数据回填标题和歌手。 */
    private fun AppleSong.fillMetadataFromCache() {
        val metadata = adamId?.let { MediaMetadataCache.getMetadataById(it) } ?: return
        name = metadata.title
        artist = metadata.artist
    }
}
