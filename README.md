<img align="left" src="https://github.com/alexmercerind/audire/assets/28951144/d78cf14e-c6cc-411c-9c80-0294c6abc83a" width="64" height="64"></img>

<h1 align="left">Audire</h1>

**🎵 Identify music playing around you.**

https://github.com/alexmercerind/audire/assets/28951144/3a6f22d9-899e-4a3e-980b-a869c1b61e50

<br>

<table>
  <tr>
    <td><img src="https://github.com/alexmercerind/audire/assets/28951144/dacfa41a-646c-412e-8334-57969b1f15ec"></td>
    <td><img src="https://github.com/alexmercerind/audire/assets/28951144/050994eb-11d3-4e1d-bb4b-53eea33855e9"></td>
    <td><img src="https://github.com/alexmercerind/audire/assets/28951144/a4d0f8a0-2b99-4649-a97f-de2dd2e7de62"></td>
  </tr>
  <tr>
    <td><img src="https://github.com/alexmercerind/audire/assets/28951144/0a492aef-c532-497e-baa8-14528f219cc0"></td>
    <td><img src="https://github.com/alexmercerind/audire/assets/28951144/e241a9db-ae5a-4f1e-be1d-c3cf9640c57e"></td>
    <td><img src="https://github.com/alexmercerind/audire/assets/28951144/8e8907fa-c617-4421-872e-33cd9bafa5e0"></td>
  </tr>
</table>

## Download

- [GitHub Releases](https://github.com/alexmercerind/audire/releases/latest)
- [Google Play Store](https://play.google.com/store/apps/details?id=com.alexmercerind.audire)
- [IzzySoft](https://apt.izzysoft.de/fdroid/index/apk/com.alexmercerind.audire)

## Building

Refer to [CI](https://github.com/alexmercerind/audire/blob/main/.github/workflows/android.yml).

## Architecture

The project uses MVVM & [Android Architecture Components](https://developer.android.com/topic/architecture).

- UI
  - Activities & Fragments
    - MainActivity
    - MusicActivity
    - SettingsActivity
    - AboutActivity
    - IdentifyFragment
    - HistoryFragment
  - View Models
    - IdentifyViewModel
    - HistoryViewModel
    - SettingsViewModel
- Repositories
  - IdentifyRepository
  - HistoryRepository
  - SettingsRepository
- Data
  - Sources
    - IdentifyDataSource
        - ShazamIdentifyDataSource
        - AUDDIdentifyDataSource
        - ...
    - HistoryDataSource
  - Models & Entities
    - Music
    - HistoryItem
  - API: Retrofit
    - ShazamAPI
    - ShazamRetrofitInstance
    - AUDDAPI
    - AUDDRetrofitInstance
  - DB: Room
    - HistoryItemDao
    - HistoryItemDatabase

### Libraries

- [Coil](https://coil-kt.github.io/coil/)
- [Retrofit](https://square.github.io/retrofit/)
- [Room](https://developer.android.com/training/data-storage/room)
- ...

## How

#### Notes

1. ShazamIdentifyDataSource is based on:
    - https://github.com/marin-m/SongRec
    - https://github.com/alexmercerind/shazam-signature-jni
2. AUDDIdentifyDataSource is based on:
    - https://audd.io
3. ...

## License

![](https://github.com/alexmercerind/audire/assets/28951144/5546336a-fec9-431e-92af-a4619863d818)

This project & the work under this repository is governed by GNU General Public License v3.0.
