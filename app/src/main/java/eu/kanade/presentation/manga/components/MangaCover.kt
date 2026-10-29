package eu.kanade.presentation.manga.components

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.graphics.painter.ColorPainter
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.unit.dp
import coil3.compose.AsyncImage
import coil3.request.ImageRequest
import coil3.size.Size
import eu.kanade.presentation.util.rememberResourceBitmapPainter
import eu.kanade.tachiyomi.R

enum class MangaCover(val ratio: Float) {
    Square(1f / 1f),
    Book(2f / 3f),
    ;

    @Composable
    operator fun invoke(
        data: Any?,
        modifier: Modifier = Modifier,
        contentDescription: String = "",
        shape: Shape = MaterialTheme.shapes.medium,
        onClick: (() -> Unit)? = null,
    ) {
        // MangaInfoHeader (and similar spots) pass a fully-built ImageRequest
        // (e.g. `.data(manga).crossfade(true).build()`). Wrapping it in another
        // ImageRequest here would make Coil treat the request itself as the data
        // payload, so the Manga/MangaCover fetcher would never match and the
        // cover wouldn't load. Pass ready-made requests through untouched.
        val model: Any =
            if (data is ImageRequest) {
                data
            } else {
                // Cap the decoded resolution so grid covers aren't decoded at
                // full file size before being cropped to display size.
                val context = LocalContext.current
                val density = LocalDensity.current
                remember(data) {
                    ImageRequest.Builder(context)
                        .data(data)
                        .size(
                            Size(
                                with(density) { MaxCoverSizeDp.roundToPx() },
                                with(density) { MaxCoverSizeDp.roundToPx() },
                            ),
                        )
                        .build()
                }
            }
        AsyncImage(
            model = model,
            placeholder = ColorPainter(CoverPlaceholderColor),
            error = rememberResourceBitmapPainter(id = R.drawable.cover_error),
            contentDescription = contentDescription,
            modifier = modifier
                .aspectRatio(ratio)
                .clip(shape)
                .then(
                    if (onClick != null) {
                        Modifier.clickable(
                            role = Role.Button,
                            onClick = onClick,
                        )
                    } else {
                        Modifier
                    },
                ),
            contentScale = ContentScale.Crop,
        )
    }
}

private val CoverPlaceholderColor = Color(0x1F888888)

// 640dp @ ~3x density, i.e. ~1920px max — grid/list covers never need more.
private val MaxCoverSizeDp = 640.dp
