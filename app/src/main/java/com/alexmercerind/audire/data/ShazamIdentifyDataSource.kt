package com.alexmercerind.audire.data

import com.alexmercerind.audire.api.shazam.ShazamRetrofitInstance
import com.alexmercerind.audire.api.shazam.models.Geolocation
import com.alexmercerind.audire.api.shazam.models.ShazamRequestBody
import com.alexmercerind.audire.api.shazam.models.ShazamResponse
import com.alexmercerind.audire.api.shazam.models.Signature
import com.alexmercerind.audire.converters.toMusic
import com.alexmercerind.audire.converters.toShortArray
import com.alexmercerind.audire.models.Music
import com.alexmercerind.audire.native.ShazamSignature
import com.github.f4b6a3.uuid.UuidCreator
import com.github.f4b6a3.uuid.enums.UuidNamespace
import java.util.Calendar
import kotlin.random.Random

class ShazamIdentifyDataSource : IdentifyDataSource {
    companion object {
        val USER_AGENTS = arrayOf<String>(
            "Dalvik/2.1.0 (Linux; U; Android 5.0.2; VS980 4G Build/LRX22G)",
            "Dalvik/1.6.0 (Linux; U; Android 4.4.2; SM-T210 Build/KOT49H)",
            "Dalvik/2.1.0 (Linux; U; Android 5.1.1; SM-P905V Build/LMY47X)",
            "Dalvik/1.6.0 (Linux; U; Android 4.4.4; Vodafone Smart Tab 4G Build/KTU84P)",
            "Dalvik/1.6.0 (Linux; U; Android 4.4.4; SM-G360H Build/KTU84P)",
            "Dalvik/2.1.0 (Linux; U; Android 5.0.2; SM-S920L Build/LRX22G)",
            "Dalvik/2.1.0 (Linux; U; Android 5.0; Fire Pro Build/LRX21M)",
            "Dalvik/2.1.0 (Linux; U; Android 5.0; SM-N9005 Build/LRX21V)",
            "Dalvik/2.1.0 (Linux; U; Android 6.0.1; SM-G920F Build/MMB29K)",
            "Dalvik/1.6.0 (Linux; U; Android 4.4.2; SM-G7102 Build/KOT49H)",
            "Dalvik/2.1.0 (Linux; U; Android 5.0; SM-G900F Build/LRX21T)",
            "Dalvik/2.1.0 (Linux; U; Android 6.0.1; SM-G928F Build/MMB29K)",
            "Dalvik/2.1.0 (Linux; U; Android 5.1.1; SM-J500FN Build/LMY48B)",
            "Dalvik/2.1.0 (Linux; U; Android 5.1.1; Coolpad 3320A Build/LMY47V)",
            "Dalvik/1.6.0 (Linux; U; Android 4.4.4; SM-J110F Build/KTU84P)",
            "Dalvik/1.6.0 (Linux; U; Android 4.4.2; SAMSUNG-SGH-I747 Build/KOT49H)",
            "Dalvik/1.6.0 (Linux; U; Android 4.4.2; SAMSUNG-SM-T337A Build/KOT49H)",
            "Dalvik/1.6.0 (Linux; U; Android 4.3; SGH-T999 Build/JSS15J)",
            "Dalvik/2.1.0 (Linux; U; Android 6.0.1; D6603 Build/23.5.A.0.570)",
            "Dalvik/2.1.0 (Linux; U; Android 5.1.1; SM-J700H Build/LMY48B)",
            "Dalvik/1.6.0 (Linux; U; Android 4.4.2; HTC6600LVW Build/KOT49H)",
            "Dalvik/2.1.0 (Linux; U; Android 5.1.1; SM-N910G Build/LMY47X)",
            "Dalvik/2.1.0 (Linux; U; Android 5.1.1; SM-N910T Build/LMY47X)",
            "Dalvik/1.6.0 (Linux; U; Android 4.4.4; C6903 Build/14.4.A.0.157)",
            "Dalvik/2.1.0 (Linux; U; Android 6.0.1; SM-G920F Build/MMB29K)",
            "Dalvik/1.6.0 (Linux; U; Android 4.2.2; GT-I9105P Build/JDQ39)",
            "Dalvik/2.1.0 (Linux; U; Android 5.0; SM-G900F Build/LRX21T)",
            "Dalvik/1.6.0 (Linux; U; Android 4.4.2; GT-I9192 Build/KOT49H)",
            "Dalvik/2.1.0 (Linux; U; Android 5.1.1; SM-G531H Build/LMY48B)",
            "Dalvik/2.1.0 (Linux; U; Android 5.0; SM-N9005 Build/LRX21V)",
            "Dalvik/2.1.0 (Linux; U; Android 5.1.1; LGMS345 Build/LMY47V)",
            "Dalvik/2.1.0 (Linux; U; Android 5.0.2; HTC One Build/LRX22G)",
            "Dalvik/2.1.0 (Linux; U; Android 5.0.2; LG-D800 Build/LRX22G)",
            "Dalvik/2.1.0 (Linux; U; Android 5.1.1; SM-G531H Build/LMY48B)",
            "Dalvik/2.1.0 (Linux; U; Android 5.0; SM-N9005 Build/LRX21V)",
            "Dalvik/1.6.0 (Linux; U; Android 4.4.4; SM-T113 Build/KTU84P)",
            "Dalvik/1.6.0 (Linux; U; Android 4.2.2; AndyWin Build/JDQ39E)",
            "Dalvik/2.1.0 (Linux; U; Android 5.0; Lenovo A7000-a Build/LRX21M)",
            "Dalvik/1.6.0 (Linux; U; Android 4.4.2; LGL16C Build/KOT49I.L16CV11a)",
            "Dalvik/1.6.0 (Linux; U; Android 4.4.2; GT-I9500 Build/KOT49H)",
            "Dalvik/2.1.0 (Linux; U; Android 5.0.2; SM-A700FD Build/LRX22G)",
            "Dalvik/1.6.0 (Linux; U; Android 4.4.2; SM-G130HN Build/KOT49H)",
            "Dalvik/1.6.0 (Linux; U; Android 4.4.2; SM-N9005 Build/KOT49H)",
            "Dalvik/1.6.0 (Linux; U; Android 4.1.2; LG-E975T Build/JZO54K)",
            "Dalvik/1.6.0 (Linux; U; Android 4.4.2; E1 Build/KOT49H)",
            "Dalvik/1.6.0 (Linux; U; Android 4.4.2; GT-I9500 Build/KOT49H)",
            "Dalvik/1.6.0 (Linux; U; Android 4.4.2; GT-N5100 Build/KOT49H)",
            "Dalvik/2.1.0 (Linux; U; Android 5.1.1; SM-A310F Build/LMY47X)",
            "Dalvik/2.1.0 (Linux; U; Android 5.1.1; SM-J105H Build/LMY47V)",
            "Dalvik/1.6.0 (Linux; U; Android 4.3; GT-I9305T Build/JSS15J)",
            "Dalvik/1.6.0 (Linux; U; Android 4.4.2; android Build/JDQ39)",
            "Dalvik/1.6.0 (Linux; U; Android 4.2.1; HS-U970 Build/JOP40D)",
            "Dalvik/1.6.0 (Linux; U; Android 4.4.4; SM-T561 Build/KTU84P)",
            "Dalvik/1.6.0 (Linux; U; Android 4.2.2; GT-P3110 Build/JDQ39)",
            "Dalvik/2.1.0 (Linux; U; Android 6.0.1; SM-G925T Build/MMB29K)",
            "Dalvik/1.6.0 (Linux; U; Android 4.4.2; HUAWEI Y221-U22 Build/HUAWEIY221-U22)",
            "Dalvik/2.1.0 (Linux; U; Android 5.1.1; SM-G530T1 Build/LMY47X)",
            "Dalvik/2.1.0 (Linux; U; Android 5.1.1; SM-G920I Build/LMY47X)",
            "Dalvik/2.1.0 (Linux; U; Android 5.0; SM-G900F Build/LRX21T)",
            "Dalvik/2.1.0 (Linux; U; Android 5.1.1; Vodafone Smart ultra 6 Build/LMY47V)",
            "Dalvik/1.6.0 (Linux; U; Android 4.4.4; XT1080 Build/SU6-7.7)",
            "Dalvik/1.6.0 (Linux; U; Android 4.4.4; ASUS MeMO Pad 7 Build/KTU84P)",
            "Dalvik/1.6.0 (Linux; U; Android 4.4.2; SM-G800F Build/KOT49H)",
            "Dalvik/1.6.0 (Linux; U; Android 4.4.2; GT-N7100 Build/KOT49H)",
            "Dalvik/2.1.0 (Linux; U; Android 6.0.1; SM-G925I Build/MMB29K)",
            "Dalvik/2.1.0 (Linux; U; Android 6.0.1; A0001 Build/MMB29X)",
            "Dalvik/2.1.0 (Linux; U; Android 5.1; XT1045 Build/LPB23.13-61)",
            "Dalvik/2.1.0 (Linux; U; Android 5.1.1; LGMS330 Build/LMY47V)",
            "Dalvik/1.6.0 (Linux; U; Android 4.4.4; Z970 Build/KTU84P)",
            "Dalvik/2.1.0 (Linux; U; Android 5.0; SM-N900P Build/LRX21V)",
            "Dalvik/1.6.0 (Linux; U; Android 4.4.2; T1-701u Build/HuaweiMediaPad)",
            "Dalvik/2.1.0 (Linux; U; Android 5.1; HTCD100LVWPP Build/LMY47O)",
            "Dalvik/2.1.0 (Linux; U; Android 6.0.1; SM-G935R4 Build/MMB29M)",
            "Dalvik/2.1.0 (Linux; U; Android 6.0.1; SM-G930V Build/MMB29M)",
            "Dalvik/2.1.0 (Linux; U; Android 5.0.2; ZTE Blade Q Lux Build/LRX22G)",
            "Dalvik/1.6.0 (Linux; U; Android 4.4.4; GT-I9060I Build/KTU84P)",
            "Dalvik/2.1.0 (Linux; U; Android 6.0.1; LGUS992 Build/MMB29M)",
            "Dalvik/2.1.0 (Linux; U; Android 6.0.1; SM-G900P Build/MMB29M)",
            "Dalvik/1.6.0 (Linux; U; Android 4.1.2; SGH-T999L Build/JZO54K)",
            "Dalvik/2.1.0 (Linux; U; Android 5.1.1; SM-N910V Build/LMY47X)",
            "Dalvik/1.6.0 (Linux; U; Android 4.4.2; GT-I9500 Build/KOT49H)",
            "Dalvik/2.1.0 (Linux; U; Android 5.1.1; SM-P601 Build/LMY47X)",
            "Dalvik/1.6.0 (Linux; U; Android 4.2.2; GT-S7272 Build/JDQ39)",
            "Dalvik/2.1.0 (Linux; U; Android 5.1.1; SM-N910T Build/LMY47X)",
            "Dalvik/1.6.0 (Linux; U; Android 4.3; SAMSUNG-SGH-I747 Build/JSS15J)",
            "Dalvik/2.1.0 (Linux; U; Android 5.0.2; ZTE Blade Q Lux Build/LRX22G)",
            "Dalvik/2.1.0 (Linux; U; Android 6.0.1; SM-G930F Build/MMB29K)",
            "Dalvik/1.6.0 (Linux; U; Android 4.4.2; HTC_PO582 Build/KOT49H)",
            "Dalvik/2.1.0 (Linux; U; Android 6.0; HUAWEI MT7-TL10 Build/HuaweiMT7-TL10)",
            "Dalvik/2.1.0 (Linux; U; Android 6.0; LG-H811 Build/MRA58K)",
            "Dalvik/1.6.0 (Linux; U; Android 4.4.2; SM-N7505 Build/KOT49H)",
            "Dalvik/2.1.0 (Linux; U; Android 6.0; LG-H815 Build/MRA58K)",
            "Dalvik/1.6.0 (Linux; U; Android 4.4.2; LenovoA3300-HV Build/KOT49H)",
            "Dalvik/1.6.0 (Linux; U; Android 4.4.4; SM-G360G Build/KTU84P)",
            "Dalvik/1.6.0 (Linux; U; Android 4.4.4; GT-I9300I Build/KTU84P)",
            "Dalvik/2.1.0 (Linux; U; Android 5.0; SM-G900F Build/LRX21T)",
            "Dalvik/2.1.0 (Linux; U; Android 6.0.1; SM-J700T Build/MMB29K)",
            "Dalvik/2.1.0 (Linux; U; Android 5.1.1; SM-J500FN Build/LMY48B)",
            "Dalvik/1.6.0 (Linux; U; Android 4.2.2; SM-T217S Build/JDQ39)",
            "Dalvik/1.6.0 (Linux; U; Android 4.4.4; SAMSUNG-SM-N900A Build/KTU84P)"
        )
        val TIMEZONES = arrayOf(
            "Europe/Amsterdam",
            "Europe/Andorra",
            "Europe/Astrakhan",
            "Europe/Athens",
            "Europe/Belgrade",
            "Europe/Berlin",
            "Europe/Bratislava",
            "Europe/Brussels",
            "Europe/Bucharest",
            "Europe/Budapest",
            "Europe/Busingen",
            "Europe/Chisinau",
            "Europe/Copenhagen",
            "Europe/Dublin",
            "Europe/Gibraltar",
            "Europe/Guernsey",
            "Europe/Helsinki",
            "Europe/Isle_of_Man",
            "Europe/Istanbul",
            "Europe/Jersey",
            "Europe/Kaliningrad",
            "Europe/Kirov",
            "Europe/Kyiv",
            "Europe/Lisbon",
            "Europe/Ljubljana",
            "Europe/London",
            "Europe/Luxembourg",
            "Europe/Madrid",
            "Europe/Malta",
            "Europe/Mariehamn",
            "Europe/Minsk",
            "Europe/Monaco",
            "Europe/Moscow",
            "Europe/Oslo",
            "Europe/Paris",
            "Europe/Podgorica",
            "Europe/Prague",
            "Europe/Riga",
            "Europe/Rome",
            "Europe/Samara",
            "Europe/San_Marino",
            "Europe/Sarajevo",
            "Europe/Saratov",
            "Europe/Simferopol",
            "Europe/Skopje",
            "Europe/Sofia",
            "Europe/Stockholm",
            "Europe/Tallinn",
            "Europe/Tirane",
            "Europe/Ulyanovsk",
            "Europe/Vaduz",
            "Europe/Vatican",
            "Europe/Vienna",
            "Europe/Vilnius",
            "Europe/Volgograd",
            "Europe/Warsaw",
            "Europe/Zagreb",
            "Europe/Zurich"
        )
    }

    override suspend fun identify(data: ByteArray, duration: Int): Music? {
        val timestamp = Calendar.getInstance().time.time.toInt()
        val body = ShazamRequestBody(
            Geolocation(
                Random(timestamp).nextDouble() * 400 + 100,
                Random(timestamp).nextDouble() * 180 - 90,
                Random(timestamp).nextDouble() * 360 - 180
            ),
            Signature(
                duration * 1000,
                timestamp,
                ShazamSignature().create(data.toShortArray())
            ),
            timestamp,
            TIMEZONES.random()
        )
        val name = Random(timestamp).nextInt(1 shl 48).toString()
        val response = ShazamRetrofitInstance.api.discovery(
            body,
            UuidCreator.getNameBasedSha1(UuidNamespace.NAMESPACE_DNS, name).toString(),
            UuidCreator.getNameBasedSha1(UuidNamespace.NAMESPACE_URL, name).toString(),
            USER_AGENTS.random(),
        )

        return if (response.isSuccessful) response.body()?.toMusic() else null
    }
}
