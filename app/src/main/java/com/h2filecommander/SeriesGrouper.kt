package com.h2filecommander

import java.util.regex.Pattern

object SeriesGrouper {
    
    private val seriesPatterns = listOf(
        Pattern.compile("(?i)(.+?)[\\s_-]*[Ee](\\d{1,3})"),
        Pattern.compile("(?i)(.+?)[\\s_-]*(\\d{1,3})\\s*(?:قسمت|ep|episode)?"),
        Pattern.compile("(?i)(.+?)[\\s_-]*[Ss](\\d{1,2})[\\s_-]*[Ee](\\d{1,2})"),
        Pattern.compile("(?i)(.+?)\\.?[Ss](\\d{1,2})\\.?[Ee](\\d{1,2})")
    )
    
    fun detectSeries(filename: String): String? {
        val nameWithoutExt = filename.substringBeforeLast(".")
        
        for (pattern in seriesPatterns) {
            val matcher = pattern.matcher(nameWithoutExt)
            if (matcher.find()) {
                val seriesName = matcher.group(1)?.trim() ?: return null
                val cleanName = seriesName
                    .replace(Regex("[._-]+"), " ")
                    .replace(Regex("\\s+"), " ")
                    .trim()
                
                if (cleanName.length > 1) {
                    return cleanName
                }
            }
        }
        
        return null
    }
}
