package com.h2filecommander

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import java.io.File

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            H2FileCommanderApp()
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun H2FileCommanderApp() {
    var selectedFolder by remember { mutableStateOf<File?>(null) }
    var folders by remember { mutableStateOf<List<File>>(emptyList()) }
    var isLoading by remember { mutableStateOf(true) }
    var organizeResult by remember { mutableStateOf<String?>(null) }

    LaunchedEffect(Unit) {
        val externalStorage = android.os.Environment.getExternalStorageDirectory()
        val excludedFolders = listOf("Pictures", "Android", "DCIM", "Download", "Android/data")
        
        folders = externalStorage.listFiles()?.filter { file ->
            file.isDirectory && !excludedFolders.any { excluded ->
                file.name.equals(excluded, ignoreCase = true)
            }
        }?.sortedBy { it.name.lowercase() } ?: emptyList()
        
        isLoading = false
    }

    MaterialTheme {
        Scaffold(
            topBar = {
                TopAppBar(
                    title = { Text("H² File Commander 📁") },
                    colors = TopAppBarDefaults.topAppBarColors(
                        containerColor = MaterialTheme.colorScheme.primaryContainer
                    )
                )
            }
        ) { padding ->
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(padding)
                    .padding(16.dp)
            ) {
                if (organizeResult != null) {
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        colors = CardDefaults.cardColors(
                            containerColor = MaterialTheme.colorScheme.secondaryContainer
                        )
                    ) {
                        Text(
                            text = organizeResult!!,
                            modifier = Modifier.padding(16.dp),
                            style = MaterialTheme.typography.bodyLarge
                        )
                    }
                    Spacer(modifier = Modifier.height(16.dp))
                    Button(onClick = { organizeResult = null }) {
                        Text("برگشت")
                    }
                } else if (selectedFolder == null) {
                    Text(
                        text = "یه پوشه انتخاب کن:",
                        style = MaterialTheme.typography.titleMedium
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    
                    if (isLoading) {
                        CircularProgressIndicator()
                    } else {
                        LazyColumn {
                            items(folders) { folder ->
                                Card(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(vertical = 4.dp)
                                        .clickable { selectedFolder = folder },
                                    colors = CardDefaults.cardColors(
                                        containerColor = MaterialTheme.colorScheme.surfaceVariant
             …
