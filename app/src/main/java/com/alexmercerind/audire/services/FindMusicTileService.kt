package com.alexmercerind.audire.services

import android.os.Build
import android.service.quicksettings.TileService
import androidx.annotation.RequiresApi

@RequiresApi(Build.VERSION_CODES.N)
class FindMusicTileService : TileService() {

    companion object {
        var handled = true
    }

    override fun onClick() {
        super.onClick()

        handled = false

//        val intent = Intent(applicationContext, MainActivity::class.java).apply {
//            flags = FLAG_ACTIVITY_NEW_TASK or FLAG_ACTIVITY_CLEAR_TOP
//        }
//        when {
//            Build.VERSION.SDK_INT >= Build.VERSION_CODES.UPSIDE_DOWN_CAKE -> startActivityAndCollapse(
//                PendingIntent.getActivity(
//                    applicationContext,
//                    0,
//                    intent,
//                    PendingIntent.FLAG_IMMUTABLE
//                )
//            )
//
//            else -> startActivityAndCollapse(intent)
//        }
    }
}
