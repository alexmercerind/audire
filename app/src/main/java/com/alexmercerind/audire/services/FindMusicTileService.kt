package com.alexmercerind.audire.services

import android.os.Build
import android.service.quicksettings.TileService
import androidx.annotation.RequiresApi

@RequiresApi(Build.VERSION_CODES.N)
class FindMusicTileService : TileService() {
    override fun onClick() {
        super.onClick()
        // TODO: Missing implementation.
    }
}
