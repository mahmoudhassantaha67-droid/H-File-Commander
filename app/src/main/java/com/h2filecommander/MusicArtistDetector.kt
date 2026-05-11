package com.h2filecommander

import java.util.regex.Pattern

object MusicArtistDetector {
    
    private val musicPatterns = listOf(
        Pattern.compile("(?i)^(.+?)\\s*-\\s*(.+)$"),
        Pattern.compile("(?i)^(.+?)_\\s*(.+)$"),
        Pattern.compile("(?i)^(.+?)\\s*-\\s*.+\\s*-\\s*(.+)$"),
        Pattern.compile("(?i)^(.+?)\\s+\\u2013\\s*(.+)$")
    )
    
    fun detectArtist(filename: String): String? {
        val nameWithoutExt = filename.substringBeforeLast(".")
        
        for (pattern in musicPatterns) {
            val matcher = pattern.matcher(nameWithoutExt)
            if (matcher.find()) {
                val artist = matcher.group(1)?.trim() ?: return null
                
                val cleanArtist = artist
                    .replace(Regex("[_\\-]+"), " ")
                    .replace(Regex("\\s+"), " ")
                    .trim()
                
                if (cleanArtist.length > 1 && !cleanArtist.all { it.isDigit() || it == ' ' }) {
                    return cleanArtist
                }
            }
        }
        
        return null
    }
}
