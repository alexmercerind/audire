package com.alexmercerind.audire.models

import java.io.Serializable

data class Music(
    val id: Int?,
    val title: String,
    val artists: String,
    val cover: String,
    val album: String?,
    val label: String?,
    val year: String?,
    val lyrics: String?,
) : Serializable
