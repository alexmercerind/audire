<img align="left" src="https://github.com/alexmercerind/audire/assets/28951144/d78cf14e-c6cc-411c-9c80-0294c6abc83a" width="64" height="64"></img>

<h1 align="left">Audire</h1>

**🎵 Identify music playing around you.**

https://github.com/alexmercerind/audire/assets/28951144/3a6f22d9-899e-4a3e-980b-a869c1b61e50

<br>

<table>
  <tr>
    <td><img src="https://github.com/alexmercerind/audire/assets/28951144/7f960ef3-5228-4fe8-a6d7-f0f3efec067c"></td>
    <td><img src="https://github.com/alexmercerind/audire/assets/28951144/050994eb-11d3-4e1d-bb4b-53eea33855e9"></td>
    <td><img src="https://github.com/alexmercerind/audire/assets/28951144/4f9574e1-937f-4714-9cef-08ce69e0ff9e"></td>
  </tr>
  <tr>
    <td><img src="https://github.com/alexmercerind/audire/assets/28951144/a05ade17-5c3f-4bcf-85a9-411f8c2ea725"></td>
    <td><img src="https://github.com/alexmercerind/audire/assets/28951144/e241a9db-ae5a-4f1e-be1d-c3cf9640c57e"></td>
    <td><img src="https://github.com/alexmercerind/audire/assets/28951144/97a4b977-4028-49e1-81b1-eb386651ae85"></td>
  </tr>
</table>

## Building

Refer to [CI](https://github.com/alexmercerind/audire/blob/main/.github/workflows/android.yml).

## Download

- [GitHub Releases](https://github.com/alexmercerind/audire/releases)
- [Google Play Store](https://play.google.com/store/apps/details?id=com.alexmercerind.audire)

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
        - More in future...
    - HistoryDataSource
  - Models & Entities
    - Music
    - HistoryItem
  - API: Retrofit
    - ShazamAPI
    - ShazamRetrofitInstance
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

## License

![](https://github.com/alexmercerind/audire/assets/28951144/5546336a-fec9-431e-92af-a4619863d818)

This project & the work under this repository is governed by GNU General Public License v3.0.
