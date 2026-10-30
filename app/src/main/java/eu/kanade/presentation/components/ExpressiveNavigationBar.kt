package eu.kanade.presentation.components

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.spring
import androidx.compose.animation.core.tween
import androidx.compose.animation.expandVertically
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.shrinkVertically
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsPressedAsState
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.selection.selectable
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.hapticfeedback.HapticFeedbackType
import androidx.compose.ui.platform.LocalHapticFeedback
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.util.fastForEachIndexed
import eu.kanade.presentation.util.Tab

@Composable
fun ExpressiveNavigationBar(
    tabs: List<Tab>,
    selectedTab: Tab,
    onSelect: (Tab) -> Unit,
    modifier: Modifier = Modifier,
    visible: Boolean = true,
    badge: @Composable (Tab) -> Unit = {},
) {
    AnimatedVisibility(
        visible = visible,
        modifier = modifier.navigationBarsPadding(),
        enter = fadeIn(tween(durationMillis = 160)) +
            expandVertically(expandFrom = Alignment.Bottom, animationSpec = tween(durationMillis = 240)),
        exit = fadeOut(tween(durationMillis = 120)) +
            shrinkVertically(shrinkTowards = Alignment.Bottom, animationSpec = tween(durationMillis = 200)),
    ) {
        Row(
            modifier = Modifier
                .padding(horizontal = 12.dp, vertical = 10.dp)
                .fillMaxWidth()
                .height(72.dp)
                .padding(horizontal = 6.dp, vertical = 6.dp),
            horizontalArrangement = Arrangement.spacedBy(2.dp),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            tabs.fastForEachIndexed { index, tab ->
                ExpressiveNavigationItem(
                    tab = tab,
                    selected = selectedTab::class == tab::class,
                    onClick = { onSelect(tab) },
                    badge = { badge(tab) },
                )
            }
        }
    }
}

@Composable
private fun RowScope.ExpressiveNavigationItem(
    tab: Tab,
    selected: Boolean,
    onClick: () -> Unit,
    badge: @Composable () -> Unit,
) {
    val haptic = LocalHapticFeedback.current
    val interactionSource = remember { MutableInteractionSource() }
    val pressed by interactionSource.collectIsPressedAsState()
    val options = tab.options

    val itemScale by animateFloatAsState(
        targetValue = if (pressed) 0.9f else 1f,
        animationSpec = spring(dampingRatio = 0.55f, stiffness = 900f),
        label = "navItemScale",
    )
    // One spring drives both the indicator and the icon scale. It is read in the
    // draw phase so the animation never recomposes the item.
    val activeProgress = animateFloatAsState(
        targetValue = if (selected) 1f else 0f,
        animationSpec = spring(dampingRatio = 0.62f, stiffness = 500f),
        label = "navActive",
    )

    val indicatorColor = MaterialTheme.colorScheme.secondaryContainer
    val iconTint = if (selected) {
        MaterialTheme.colorScheme.onSecondaryContainer
    } else {
        MaterialTheme.colorScheme.onSurfaceVariant
    }
    val labelColor = if (selected) {
        MaterialTheme.colorScheme.secondary
    } else {
        MaterialTheme.colorScheme.onSurfaceVariant
    }

    Column(
        modifier = Modifier
            .weight(1f)
            .height(60.dp)
            .graphicsLayer {
                scaleX = itemScale
                scaleY = itemScale
            }
            .clip(RoundedCornerShape(24.dp))
            .selectable(
                selected = selected,
                interactionSource = interactionSource,
                indication = androidx.compose.material3.ripple(bounded = true),
                role = Role.Tab,
                onClick = {
                    haptic.performHapticFeedback(HapticFeedbackType.ContextClick)
                    onClick()
                },
            )
            .padding(vertical = 4.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center,
    ) {
        Box(
            modifier = Modifier
                .height(32.dp)
                .width(56.dp)
                .drawBehind {
                    val progress = activeProgress.value
                    if (progress > 0.01f) {
                        val widthPx = 24.dp.toPx() + 32.dp.toPx() * progress
                        drawRoundRect(
                            color = indicatorColor.copy(alpha = progress),
                            size = Size(widthPx, size.height),
                            topLeft = Offset((size.width - widthPx) / 2f, 0f),
                            cornerRadius = CornerRadius(size.height / 2f),
                        )
                    }
                },
            contentAlignment = Alignment.Center,
        ) {
            Box(
                modifier = Modifier.graphicsLayer {
                    val progress = activeProgress.value
                    scaleX = 1f + 0.06f * progress
                    scaleY = 1f + 0.06f * progress
                },
            ) {
                Icon(
                    painter = options.icon!!,
                    contentDescription = options.title,
                    tint = iconTint,
                    modifier = Modifier.size(24.dp),
                )
                Box(
                    modifier = Modifier
                        .align(Alignment.TopEnd)
                        .offset(x = 7.dp, y = (-7).dp),
                ) {
                    badge()
                }
            }
        }

        Text(
            text = options.title,
            modifier = Modifier.padding(top = 2.dp),
            color = labelColor,
            style = MaterialTheme.typography.labelSmall,
            fontWeight = if (selected) FontWeight.Bold else FontWeight.Medium,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis,
        )
    }
}
