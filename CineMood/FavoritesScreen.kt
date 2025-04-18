package com.example.cinemood

import android.content.Context
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalLayoutDirection
import androidx.compose.ui.unit.LayoutDirection
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.cinemood.destinations.DetailsScreenDestination
import com.ramcosta.composedestinations.annotation.Destination
import com.ramcosta.composedestinations.navigation.DestinationsNavigator
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import kotlinx.coroutines.delay

@Destination
@Composable
fun FavoritesScreen(navigator: DestinationsNavigator) {
    CompositionLocalProvider(LocalLayoutDirection provides LayoutDirection.Rtl) {
        val context = LocalContext.current
        val scope = rememberCoroutineScope()
        val dataStore = remember { FavoriteDataStore(context) }

        var allMovies by remember { mutableStateOf(emptyList<TmdbMovie>()) }
        var favoriteIds by remember { mutableStateOf(setOf<String>()) }
        var isLoading by remember { mutableStateOf(true) }
        var toastMessage by remember { mutableStateOf<String?>(null) }

        LaunchedEffect(Unit) {
            favoriteIds = dataStore.favoriteMoviesFlow.first()
            val result = withContext(Dispatchers.IO) { getUpcomingMovies() }
            allMovies = result?.results ?: emptyList()
            isLoading = false
        }

        val favoriteMovies = allMovies.filter { favoriteIds.contains(it.id.toString()) }

        Box(modifier = Modifier.fillMaxSize()) {
            Column(Modifier.padding(16.dp)) {
                Text("دوست داشتنی ها💕", color = Color(0xFF902923), style = MaterialTheme.typography.headlineMedium)

                Spacer(Modifier.height(16.dp))

                if (isLoading) {
                    Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                        CircularProgressIndicator()
                    }
                } else if (favoriteMovies.isEmpty()) {
                    Box(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(horizontal = 16.dp),
                        contentAlignment = Alignment.Center
                    ) {

                        Surface(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(horizontal = 16.dp)
                                .height(100.dp),
                            shape = RoundedCornerShape(12.dp),
                            color = MaterialTheme.colorScheme.surfaceVariant,
                            shadowElevation = 8.dp
                        ) {
                            Box(
                                modifier = Modifier
                                    .fillMaxSize()
                                    .padding(16.dp),
                                contentAlignment = Alignment.Center
                            ) {
                                Text(
                                    text = "هیچ فیلمی را دوست نداری😭",
                                    style = MaterialTheme.typography.titleLarge.copy(fontSize = 24.sp),
                                    color = MaterialTheme.colorScheme.onSurface,
                                    modifier = Modifier.align(Alignment.Center)
                                )
                            }
                        }
                    }
                } else {
                    LazyColumn {
                        items(favoriteMovies) { movie ->
                            val isFavorite = favoriteIds.contains(movie.id.toString())

                            MovieItem(
                                movie = movie,
                                isFavorite = isFavorite,
                                buttonText = "",
                                onFavorite = {
                                    scope.launch {
                                        dataStore.removeFavorite(movie.id.toString())
                                        favoriteIds = dataStore.favoriteMoviesFlow.first()
                                        toastMessage = "حذف شد"
                                        delay(2000)
                                        toastMessage = null
                                    }
                                },
                                onClick = {
                                    navigator.navigate(DetailsScreenDestination(movie))
                                }
                            )
                            Spacer(Modifier.height(12.dp))
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
