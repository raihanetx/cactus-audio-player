package com.cactus.app.data

import com.cactus.app.model.*
import com.cactus.app.ui.theme.primary
import com.cactus.app.ui.theme.secondary
import com.cactus.app.ui.theme.tertiary
import com.cactus.app.ui.theme.primaryContainer

/* In a real build, video detection (PRD §1.2 / §8.1) scans the device's
 * standard media directories via MediaStore. For this prototype we return a
 * fixed, representative library and simulate background "detection" through
 * the notification system. */
object SampleData {

    private val rawUri = "android.resource://com.cactus.app/raw/sample_tone"

    private val day = 24 * 60 * 60 * 1000L
    private val now = System.currentTimeMillis()

    fun videos(): List<VideoItem> = listOf(
        VideoItem("v1", "The Silent Patient — Ch. 1", "নীরব রোগী — অধ্যায় ১",
            "the_silent_patient_ch1.m4a", 245_000, now - day, "EN",
            primary, primaryContainer, rawUri, lastPlayedMs = now - 2 * 3_600_000L),
        VideoItem("v2", "TED: How to Learn Anything Fast", "টেড: যেকোনো কিছু দ্রুত শেখার উপায়",
            "ted_learn_fast.mp4", 748_000, now - 2 * day, "EN",
            secondary, primaryContainer, rawUri, lastPlayedMs = now - 30 * 60_000L),
        VideoItem("v3", "Friends — S03E12 (Audio)", null,
            "friends_s03e12_audio.mkv", 1_320_000, now - 3 * day, "EN",
            tertiary, primaryContainer, rawUri),
        VideoItem("v4", "BBC 6 Minute English — AI", "বিবিসি ৬ মিনিট ইংরেজি — এআই",
            "bbc_6min_ai.mp3", 362_000, now - 5 * day, "Mixed",
            primary, secondary, rawUri, lastPlayedMs = now - 4 * 3_600_000L),
        VideoItem("v5", "Harry Potter & the Sorcerer's Stone", "হ্যারি পটার ও দ্য সরসেরার্স স্টোন",
            "hp_sorcerer_stone.m4b", 2_010_000, now - 6 * day, "EN",
            secondary, tertiary, rawUri),
        VideoItem("v6", "Joe Rogan #1920 — Calm Mind", null,
            "jre_1920_calm.mp3", 5_400_000, now - 8 * day, "EN",
            primaryContainer, primary, rawUri),
        VideoItem("v7", "English Pronunciation Drills", "ইংরেজি উচ্চারণ অনুশীলন",
            "pronunciation_drills.mp3", 540_000, now - 10 * day, "EN",
            tertiary, secondary, rawUri),
        VideoItem("v8", "The Economist — Audio Edition", null,
            "economist_audio.mp3", 2_880_000, now - 12 * day, "EN",
            primary, primaryContainer, rawUri),
    )

    fun playlists(): List<Playlist> = listOf(
        Playlist("p1", "Commute Mix", listOf("v1", "v4", "v7"), primary),
        Playlist("p2", "Before Sleep", listOf("v3", "v5"), tertiary),
        Playlist("p3", "Pronunciation", listOf("v7", "v2"), secondary),
    )

    /* Sample bilingual subtitles for the first video, so the Subtitles sheet
     * (PRD §8.4) has real content to display. */
    fun subtitlesFor(videoId: String): List<SubtitleCue> = if (videoId == "v1") listOf(
        SubtitleCue("s1", 0, 4_000,
            "She did not speak for a very long time.",
            "সে খুব দীর্ঘ সময় কোনো কথা বলেনি।",
            "/ʃi dɪd nɒt spiːk fɔːr ə ˈvɛri lɒŋ taɪm/"),
        SubtitleCue("s2", 4_000, 9_000,
            "The room was quiet except for the rain.",
            "বৃষ্টি ছাড়া ঘরটা নিস্তব্ধ ছিল।",
            "/ðə ruːm wɒz ˈkwaɪət ɪkˈsɛpt fɔːr ðə reɪn/"),
        SubtitleCue("s3", 9_000, 14_000,
            "He watched her from the doorway.",
            "সে দরজার কাছ থেকে তাকে দেখছিল।",
            "/hi wɒtʃt hɜː frɒm ðə ˈdɔːweɪ/"),
        SubtitleCue("s4", 14_000, 19_000,
            "A year had passed since the accident.",
            "দুর্ঘটনার পর এক বছর কেটে গিয়েছিল।",
            "/ə jɪər hæd pɑːst sɪns ði ˈæksɪdənt/"),
        SubtitleCue("s5", 19_000, 24_000,
            "Nobody understood why she stayed silent.",
            "সে কেন চুপ করে ছিল তা কেউ বুঝতে পারেনি।",
            "/ˈnəʊbɒdi ˌʌndərˈstʊd waɪ ʃi steɪd ˈsaɪlənt/"),
    ) else emptyList()

    fun initialNotifications(): List<AppNotification> = listOf(
        AppNotification("n1", "3 new videos detected",
            "Found new audio in your Downloads folder.", now - 3 * 3_600_000L),
        AppNotification("n2", "Scan complete",
            "Library is up to date.", now - 26 * 3_600_000L, read = true),
    )

    /* Simulates a background scan discovering a new file (PRD §12.4). */
    fun newlyDetected(): AppNotification =
        AppNotification("n${System.currentTimeMillis()}", "1 new video detected",
            "Found \"podcast_draft.mp3\" in Music.", System.currentTimeMillis())
}
