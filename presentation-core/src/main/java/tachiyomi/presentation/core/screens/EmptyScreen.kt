package tachiyomi.presentation.core.screens

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.paddingFromBaseline
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.util.fastForEach
import dev.icerock.moko.resources.StringResource
import tachiyomi.presentation.core.components.ActionButton
import tachiyomi.presentation.core.components.material.padding
import tachiyomi.presentation.core.i18n.stringResource

data class EmptyScreenAction(
    val stringRes: StringResource,
    val icon: ImageVector,
    val onClick: () -> Unit,
)

@Composable
fun EmptyScreen(
    stringRes: StringResource,
    modifier: Modifier = Modifier,
    actions: List<EmptyScreenAction>? = null,
) {
    EmptyScreen(
        message = stringResource(stringRes),
        modifier = modifier,
        actions = actions,
    )
}

@Composable
fun EmptyScreen(
    message: String,
    modifier: Modifier = Modifier,
    actions: List<EmptyScreenAction>? = null,
) {
    Column(
        modifier = modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(horizontal = 24.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center,
    ) {
        Box(
            modifier = Modifier
                .size(132.dp)
                .clip(RoundedCornerShape(44.dp))
                .background(MaterialTheme.colorScheme.primaryContainer),
            contentAlignment = Alignment.Center,
        ) {
            EmptyStateFace(
                color = MaterialTheme.colorScheme.onPrimaryContainer,
            )
        }

        Text(
            text = message,
            modifier = Modifier
                .paddingFromBaseline(top = 24.dp),
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            style = MaterialTheme.typography.bodyLarge,
            textAlign = TextAlign.Center,
        )

        if (!actions.isNullOrEmpty()) {
            Row(
                modifier = Modifier
                    .padding(top = 24.dp)
                    .widthIn(max = 420.dp),
                horizontalArrangement = Arrangement.spacedBy(MaterialTheme.padding.small),
            ) {
                actions.fastForEach {
                    ActionButton(
                        modifier = Modifier.weight(1f),
                        title = stringResource(it.stringRes),
                        icon = it.icon,
                        onClick = it.onClick,
                    )
                }
            }
        }
    }
}

@Composable
private fun EmptyStateFace(color: Color) {
    Canvas(modifier = Modifier.size(64.dp)) {
        val eyeRadius = size.minDimension * 0.06f
        val eyeY = size.height * 0.40f
        drawCircle(color, eyeRadius, Offset(size.width * 0.32f, eyeY))
        drawCircle(color, eyeRadius, Offset(size.width * 0.68f, eyeY))
        drawArc(
            color = color,
            startAngle = 25f,
            sweepAngle = 130f,
            useCenter = false,
            topLeft = Offset(size.width * 0.28f, size.height * 0.42f),
            size = Size(size.width * 0.44f, size.height * 0.28f),
            style = Stroke(width = eyeRadius * 1.6f, cap = StrokeCap.Round),
        )
    }
}
