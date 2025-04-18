package com.example.cinemood

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalLayoutDirection
import androidx.compose.ui.unit.LayoutDirection
import androidx.compose.ui.unit.dp
import com.example.cinemood.destinations.DetailsScreenDestination
import com.ramcosta.composedestinations.annotation.Destination
import com.ramcosta.composedestinations.navigation.DestinationsNavigator
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

@Destination(start = true)
@Composable
fun HomeScreen(navigator: DestinationsNavigator) {
    CompositionLocalProvider(LocalLayoutDirection provides LayoutDirection.Rtl) {
        val context = LocalContext.current
        val scope = rememberCoroutineScope()
        val dataStore = remember { FavoriteDataStore(context) }

        var movies by remember { mutableStateOf(emptyList<TmdbMovie>()) }
        var isLoading by remember { mutableStateOf(true) }
        var searchQuery by remember { mutableStateOf("") }
        var favoriteIds by remember { mutableStateOf(setOf<String>()) }
        var toastMessage by remember { mutableStateOf<String?>(null) }

        LaunchedEffect(Unit) {
            favoriteIds = dataStore.favoriteMoviesFlow.first()
            val result = withContext(Dispatchers.IO) { getUpcomingMovies() }
            movies = result?.results ?: emptyList()
            isLoading = false
        }

        val filteredMovies = movies.filter {
            it.title.contains(searchQuery, ignoreCase = true)
        }

        Box(modifier = Modifier.fillMaxSize()) {
            if (isLoading) {
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    CircularProgressIndicator(
                        color = MaterialTheme.colorScheme.secondary,
                        strokeWidth = 4.dp,
                        modifier = Modifier.size(48.dp)
                    )
                }
            } else {
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(horizontal = 16.dp, vertical = 12.dp)
                ) {

                    SearchBox(
                        searchQuery = searchQuery,
                        onSearchQueryChanged = { searchQuery = it },
                        modifier = Modifier.fillMaxWidth().height(56.dp)
                    )

                    Spacer(modifier = Modifier.height(16.dp))

                    LazyColumn(
                        verticalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        items(filteredMovies) { movie ->

                            val isFavorite = favoriteIds.contains(movie.id.toString())

                            MovieItem(
                                movie = movie,
                                isFavorite = isFavorite,
                                buttonText = "",
                                onFavorite = {
                                    scope.launch {
                                        if (isFavorite) {
                                            dataStore.removeFavorite(movie.id.toString())
                                            toastMessage = "حذف شد"
                                        } else {
                                            dataStore.addFavorite(movie.id.toString())
                                            toastMessage = "دوست داشتنی شد"
                                        }
                                        favoriteIds = dataStore.favoriteMoviesFlow.first()
                                        delay(2000)
                                        toastMessage = null
                                    }
                                },
                                onClick = {
                                    navigator.navigate(DetailsScreenDestination(movie))
                                }
                            )
                        }
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
}
