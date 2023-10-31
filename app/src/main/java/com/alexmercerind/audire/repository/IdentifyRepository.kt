package com.alexmercerind.audire.repository

import com.alexmercerind.audire.data.IdentifyDataSource

class IdentifyRepository(private val source: IdentifyDataSource) {
    suspend fun identify(data: ByteArray, duration: Int) = source.identify(data, duration)
}
