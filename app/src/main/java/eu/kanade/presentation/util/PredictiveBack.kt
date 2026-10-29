package eu.kanade.presentation.util

import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.unit.dp
import androidx.navigationevent.NavigationEvent
import androidx.navigationevent.NavigationEventInfo
import androidx.navigationevent.NavigationEventTransitionState
import androidx.navigationevent.compose.NavigationEventState

/**
 * Follows the predictive back gesture: the content scales down and fades out while the user drags
 * from the screen edge. Animated values are read in the draw phase, so a gesture never triggers
 * recomposition of the screen.
 */
fun Modifier.predictiveBackAnimation(
    state: NavigationEventState<out NavigationEventInfo>,
): Modifier = graphicsLayer {
    val transition = state.transitionState
    if (transition is NavigationEventTransitionState.InProgress) {
        val event = transition.latestEvent
        val progress = event.progress
        val direction = if (event.swipeEdge == NavigationEvent.EDGE_LEFT) 1f else -1f
        val scale = 1f - 0.06f * progress
        scaleX = scale
        scaleY = scale
        alpha = 1f - 0.25f * progress
        translationX = 10.dp.toPx() * progress * direction
    } else {
        scaleX = 1f
        scaleY = 1f
        alpha = 1f
        translationX = 0f
    }
}
