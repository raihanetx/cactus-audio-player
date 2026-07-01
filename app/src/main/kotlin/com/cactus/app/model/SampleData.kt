package com.cactus.app.model

object SampleData {
    val tracks = listOf(
        Track(1, "Steve Jobs — Stanford Speech", "Motivational Talks", "SJ", "15:43", "12:34", true),
        Track(2, "The Office — Season 3", "TV Show Audio", "OF", "22:18", "5:20", true),
        Track(3, "BBC News Hour — Daily", "News & Current Affairs", "BN", "58:02", "0:00", false),
        Track(4, "TED: How to Speak So Powerfully", "Public Speaking", "TD", "09:48", "0:00", true),
        Track(5, "Friends — The One Where...", "TV Show Audio", "FR", "22:34", "0:00", false),
    )

    val subtitles = listOf(
        SubtitleLine("00:01", "00:02", "Hello.", "হ্যালো।", "-- হ্যালো --"),
        SubtitleLine("00:03", "00:04", "Good morning.", "শুভ সকাল।", "-- গুড-মর্নিং --"),
        SubtitleLine("00:05", "00:07", "How are you?", "তুমি কেমন আছো?", "-- হাউ-আর-ইউ? --"),
        SubtitleLine("00:08", "00:10", "I am fine, thanks.", "আমি ভালো আছি, ধন্যবাদ।", "-- আই-অ্যাম-ফাইন,-থ্যাংকস। --"),
        SubtitleLine("00:11", "00:14", "What is your name, sir?", "আপনার নাম কী, স্যার?", "-- হোয়াট-ইজ-ইয়োর-নেইম,-সার? --"),
        SubtitleLine("00:15", "00:18", "My name is John, nice to meet.", "আমার নাম জন, আপনার সাথে দেখা হয়ে ভালো লাগলো।", "-- মাই-নেইম-ইজ-জন,-নাইস-টু-মিট। --"),
    )

    val loops = listOf(
        Loop("Hard Sentence 1", "00:12", "00:16", 10),
        Loop("Greeting Practice", "00:01", "00:04", 5),
    )

    val notes = listOf(
        Note("Remember to practice the intonation for \"How are you?\" and try to stress the word \"how\" slightly more than the others in this sentence.", 14, "Today"),
        Note("The word \"fine\" is stressed here.", 5, "Yesterday"),
    )
}
