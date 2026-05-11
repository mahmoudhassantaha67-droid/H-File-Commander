package com.h2filecommander

import java.io.File
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

object FileOrganizer {
    
    private val videoExtensions = listOf("mp4", "mkv", "avi", "mov", "wmv", "flv", "webm")
    private val audioExtensions = listOf("mp3", "wav", "flac", "aac", "ogg", "m4a", "wma")
    private val documentExtensions = listOf("pdf", "mhtml", "mht", "doc", "docx", "txt")
    
    fun organize(folder: File): String {
        val files = folder.listFiles()?.filter { it.isFile } ?: return "هیچ فایلی پیدا نشد"
        
        if (files.isEmpty()) return "پوشه خالیه!"
        
        val videosDir = File(folder, "ویدیو‌ها")
        val moviesDir = File(folder, "فیلم‌ها")
        val musicDir = File(folder, "موزیک")
        val documentsDir = File(folder, "داکیومنت‌ها")
        val othersDir = File(folder, "سایر")
        
        listOf(videosDir, moviesDir, musicDir, documentsDir, othersDir).forEach { it.mkdirs() }
        
        var movedCount = 0
        val logEntries = mutableListOf<String>()
        
        val dateFormat = SimpleDateFormat("yyyy/MM/dd - HH:mm", Locale("fa", "IR"))
        val now = dateFormat.format(Date())

        files.forEach { file ->
            val ext = file.extension.lowercase()
            val originalPath = file.absolutePath

            when {
                ext in videoExtensions -> {
                    val seriesGroup = SeriesGrouper.detectSeries(file.name)
                    val destDir = if (seriesGroup != null) {
                        File(videosDir, seriesGroup).also { it.mkdirs() }
                    } else {
                        moviesDir
                    }
                    
                    if (file.renameTo(File(destDir, file.name))) {
                        movedCount++
                        logEntries.add("📺 ${destDir.absolutePath.replace(folder.absolutePath + "/", "")}/${file.name}  ←  $originalPath")
                    }
                }
                
                ext in audioExtensions -> {
                    val artist = MusicArtistDetector.detectArtist(file.name)
                    val destDir = if (artist != null) {
                        File(musicDir, artist).also { it.mkdirs() }
                    } else {
                        musicDir
                    }
                    
                    if (file.renameTo(File(destDir, file.name))) {
                        movedCount++
                        logEntries.add("🎵 ${destDir.absolutePath.replace(folder.absolutePath + "/", "")}/${file.name}  ←  $originalPath")
                    }
                }
                
                ext in documentExtensions -> {
                    if (file.renameTo(File(documentsDir, file.name))) {
                        movedCount++
                        logEntries.add("📄 ${documentsDir.name}/${file.name}  ←  $originalPath")
                    }
                }
                
                else -> {
                    if (file.renameTo(File(othersDir, file.name))) {
                        movedCount++
                        logEntries.add("📦 ${othersDir.name}/${file.name}  ←  $originalPath")
                    }
                }
            }
        }
        
        updateLogFile(folder, now, logEntries)
        
        return "✅ $movedCount فایل مرتب شد!\n" +
               "📺 سریال/انیمه: ${videosDir.listFiles()?.size ?: 0}\n" +
               "🎬 فیلم‌ها: ${moviesDir.listFiles()?.size ?: 0}\n" +
               "🎵 موزیک: ${musicDir.listFiles()?.size ?: 0}\n" +
               "📄 داکیومنت: ${documentsDir.listFiles()?.size ?: 0}\n" +
               "📦 سایر: ${othersDir.listFiles()?.size ?: 0}\n" +
               "📋 لاگ آپدیت شد"
    }
    
    private fun updateLogFile(folder: File, date: String, entries: List<String>) {
        val logFile = File(folder, "organize_log.txt")
        val separator = "━━━━━━━━━━━━━━━━━━━━…
