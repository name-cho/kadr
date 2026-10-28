package eu.kanade.presentation.more.settings

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.util.fastForEachIndexed
import eu.kanade.presentation.more.settings.screen.SearchableSettings
import eu.kanade.presentation.more.settings.widget.PreferenceGroupHeader
import eu.kanade.presentation.more.settings.widget.SettingsGroup
import eu.kanade.presentation.more.settings.widget.SettingsGroupDivider
import kotlinx.coroutines.delay
import tachiyomi.presentation.core.components.ScrollbarLazyColumn
import kotlin.time.Duration.Companion.seconds

/**
 * A block of the settings list: either a titled group or a run of consecutive standalone
 * items that are rendered inside a single card.
 */
private sealed interface SettingsEntry {
    val key: String

    data class Group(
        override val key: String,
        val preference: Preference.PreferenceGroup,
    ) : SettingsEntry

    data class ItemRun(
        override val key: String,
        val items: List<Preference.PreferenceItem<*, *>>,
    ) : SettingsEntry
}

@Composable
fun PreferenceScreen(
    items: List<Preference>,
    modifier: Modifier = Modifier,
    contentPadding: PaddingValues = PaddingValues(0.dp),
) {
    val state = rememberLazyListState()
    val highlightKey = SearchableSettings.highlightKey
    val entries = items.toEntries()

    if (highlightKey != null) {
        LaunchedEffect(Unit) {
            val i = entries.indexOfFirst { entry ->
                when (entry) {
                    is SettingsEntry.Group ->
                        entry.preference.title == highlightKey ||
                            entry.preference.preferenceItems.any { it.title == highlightKey }
                    is SettingsEntry.ItemRun -> entry.items.any { it.title == highlightKey }
                }
            }
            if (i >= 0) {
                delay(0.5.seconds)
                state.animateScrollToItem(i)
            }
            SearchableSettings.highlightKey = null
        }
    }

    ScrollbarLazyColumn(
        modifier = modifier,
        state = state,
        contentPadding = contentPadding,
    ) {
        entries.fastForEachIndexed { index, entry ->
            item(
                key = entry.key,
                contentType = entry::class.java.simpleName,
            ) {
                when (entry) {
                    is SettingsEntry.Group -> Column {
                        PreferenceGroupHeader(
                            title = entry.preference.title,
                            highlighted = entry.preference.title == highlightKey,
                        )
                        SettingsGroup {
                            entry.preference.preferenceItems
                                .filter { it.visible }
                                .fastForEachIndexed { itemIndex, item ->
                                    if (itemIndex > 0) {
                                        SettingsGroupDivider()
                                    }
                                    PreferenceItem(
                                        item = item,
                                        highlightKey = highlightKey,
                                    )
                                }
                        }
                    }

                    is SettingsEntry.ItemRun -> SettingsGroup {
                        entry.items.fastForEachIndexed { itemIndex, item ->
                            if (itemIndex > 0) {
                                SettingsGroupDivider()
                            }
                            PreferenceItem(
                                item = item,
                                highlightKey = highlightKey,
                            )
                        }
                    }
                }
            }
        }
    }
}

/**
 * Merges consecutive standalone items into a single card, so the list never looks like a stack of
 * cards glued together.
 */
private fun List<Preference>.toEntries(): List<SettingsEntry> {
    val entries = mutableListOf<SettingsEntry>()
    var run = mutableListOf<Preference.PreferenceItem<*, *>>()

    fun flushRun() {
        if (run.isNotEmpty()) {
            entries += SettingsEntry.ItemRun(
                key = "items_${entries.size}_${run.first().title}",
                items = run,
            )
            run = mutableListOf()
        }
    }

    for (preference in this) {
        when (preference) {
            is Preference.PreferenceGroup -> {
                flushRun()
                if (preference.visible) {
                    entries += SettingsEntry.Group(
                        key = "group_${entries.size}_${preference.title}",
                        preference = preference,
                    )
                }
            }
            is Preference.PreferenceItem<*, *> -> {
                if (preference.visible) {
                    run += preference
                }
            }
        }
    }
    flushRun()

    return entries
}
