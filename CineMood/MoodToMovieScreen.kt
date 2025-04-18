package com.example.cinemood

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalLayoutDirection
import androidx.compose.ui.unit.*
import com.example.cinemood.destinations.DetailsScreenDestination
import com.example.cinemood.destinations.MoodToMovieScreenDestination
import com.ramcosta.composedestinations.annotation.Destination
import com.ramcosta.composedestinations.navigation.DestinationsNavigator
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

data class Mood(val emoji: String, val title: String)

@Destination
@Composable
fun MoodToMovieScreen(navigator: DestinationsNavigator) {
    CompositionLocalProvider(LocalLayoutDirection provides LayoutDirection.Rtl) {
        val context = LocalContext.current
        val scope = rememberCoroutineScope()
        val dataStore = remember { FavoriteDataStore(context) }

        val moods = listOf(
            Mood("😊", "شاد"),
            Mood("😢", "غمگین"),
            Mood("😴", "بی‌حال"),
            Mood("🤩", "هیجان‌زده")
        )

        var selectedMood by remember { mutableStateOf<Mood?>(null) }
        var movie by remember { mutableStateOf<TmdbMovie?>(null) }
        val favoriteIds by dataStore.favoriteMoviesFlow.collectAsState(initial = emptySet())

        var toastMessage by remember { mutableStateOf<String?>(null) }

        Box(modifier = Modifier.fillMaxSize()) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(24.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    text = "حالت چطوره؟",
                    style = MaterialTheme.typography.headlineMedium,
                    color = MaterialTheme.colorScheme.onBackground,
                    modifier = Modifier.padding(bottom = 24.dp)
                )

                LazyVerticalGrid(
                    columns = GridCells.Fixed(2),
                    verticalArrangement = Arrangement.spacedBy(12.dp),
                    horizontalArrangement = Arrangement.spacedBy(12.dp),
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(300.dp)
                ) {
                    items(moods) { mood ->
                        Card(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clickable {
                                    selectedMood = mood
                                    scope.launch {
                                        val result = getUpcomingMovies()
                                        result?.results?.let { movies ->
                                            movie = movies.random()
                                            toastMessage = "فیلم برای حالت '${mood.title}' پیدا شد 🎬"
                                            delay(2000)
                                            toastMessage = null
                                        }
                                    }
                                },
                            colors = CardDefaults.cardColors(containerColor = Color.DarkGray),
                            shape = RoundedCornerShape(16.dp),
                            elevation = CardDefaults.cardElevation(defaultElevation = 6.dp)
                        ) {
                            Column(
                                modifier = Modifier
                                    .padding(24.dp)
                                    .fillMaxWidth(),
                                horizontalAlignment = Alignment.CenterHorizontally
                            ) {
                                Text(text = mood.emoji, fontSize = 36.sp)
                                Text(text = mood.title, color = Color.White)
                            }
                        }
                    }
                }

                Spacer(modifier = Modifier.height(24.dp))

                movie?.let { currentMovie ->
                    Text("فیلم پیشنهادی بر اساس حالت: ${selectedMood?.title}", fontSize = 18.sp)

                    val isFavorite = favoriteIds.contains(currentMovie.id.toString())

                    MovieItem(
                        movie = currentMovie,
                        isFavorite = isFavorite,
                        buttonText = "",
                        onFavorite = {
                            scope.launch {
                                if (!isFavorite) {
                                    dataStore.addFavorite(currentMovie.id.toString())
                                    toastMessage = "دوست داشتنی شد"
                                } else {
                                    dataStore.removeFavorite(currentMovie.id.toString())
                                    toastMessage = "حذف شد"
                                }
                                delay(2000)
                                toastMessage = null
                            }
                        },
                        onClick = {
                            navigator.navigate(DetailsScreenDestination(currentMovie))
                        }
                    )
                }

            }
            }

            toastMessage?.let { msg ->
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(bottom = 40.dp),
                    contentAlignment = Alignment.BottomCenter
                ) {
                    Surface(
                        color = MaterialTheme.colorScheme.primary.copy(alpha = 0.9f),
                        shape = MaterialTheme.shapes.medium,
                        shadowElevation = 6.dp
                    ) {
                        Text(
                            text = msg,
                            modifier = Modifier.padding(horizontal = 20.dp, vertical = 10.dp),
                            color = Color.White,
                            style = MaterialTheme.typography.bodyMedium
                        )
                    }
                }
            }
        }
    }