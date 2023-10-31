package com.alexmercerind.audire.data

import com.alexmercerind.audire.models.Music

interface IdentifyDataSource {
    suspend fun identify(data: ByteArray, duration: Int): Music?
}
