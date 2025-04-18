package com.example.cinemood

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.FavoriteBorder
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalLayoutDirection
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.LayoutDirection
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage

@Composable
fun MovieItem(
    movie: TmdbMovie,
    isFavorite: Boolean,
    buttonText: String,
    onFavorite: () -> Unit,
    onClick: () -> Unit
) {
    CompositionLocalProvider(LocalLayoutDirection provides LayoutDirection.Rtl) {
        val cardBackgroundColor = MaterialTheme.colorScheme.surface
        val textColor = MaterialTheme.colorScheme.onSurface
        val secondaryTextColor = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f)
        val favoriteIconColor = if (isFavorite) Color.Red else MaterialTheme.colorScheme.secondary
        val buttonTextColor = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f)

        Card(
            modifier = Modifier
                .fillMaxWidth()
                .padding(8.dp)
                .clickable { onClick() },
            colors = CardDefaults.cardColors(
                containerColor = cardBackgroundColor,
                contentColor = textColor
            ),
            shape = RoundedCornerShape(16.dp),
            elevation = CardDefaults.cardElevation(8.dp)
        ) {
            Row(
                modifier = Modifier
                    .padding(12.dp)
                    .fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {

                Column(
                    modifier = Modifier
                        .weight(1f)
                        .padding(end = 12.dp)
                ) {
                    Text(
                        text = movie.title,
                        style = MaterialTheme.typography.titleMedium.copy(
                            fontSize = 20.sp,
                            fontWeight = FontWeight.Bold
                        ),
                        color = textColor
                    )

                    Spacer(modifier = Modifier.height(4.dp))

                    Text(
                        text = "تاریخ اکران: ${movie.release_date}",
                        style = MaterialTheme.typography.bodySmall.copy(color = secondaryTextColor)
                    )

                    Text(
                        text = "امتیاز: ${movie.vote_average}",
                        style = MaterialTheme.typography.bodySmall.copy(color = secondaryTextColor)
                    )

                    Spacer(modifier = Modifier.height(8.dp))

                    Row(verticalAlignment = Alignment.CenterVertically) {
                        IconButton(onClick = onFavorite) {
                            Icon(
                                imageVector = if (isFavorite) Icons.Filled.Favorite else Icons.Default.FavoriteBorder,
                                contentDescription = "علاقه‌مندی",
                                tint = favoriteIconColor
                            )
                        }

                        Spacer(modifier = Modifier.width(8.dp))

                        Text(
                            text = buttonText,
                            color = buttonTextColor,
                            fontSize = 14.sp
                        )
                    }
                }

                AsyncImage(
                    model = "https://image.tmdb.org/t/p/w500${movie.poster_path}",
                    contentDescription = "پوستر ${movie.title}",
                    modifier = Modifier
                        .width(100.dp)
                        .height(140.dp)
                        .clip(RoundedCornerShape(12.dp)),
                    contentScale = ContentScale.Crop
                )
            }
        }
    }
}
