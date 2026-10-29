package eu.kanade.presentation.more.settings.screen.about

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import cafe.adriel.voyager.navigator.LocalNavigator
import cafe.adriel.voyager.navigator.currentOrThrow
import eu.kanade.presentation.components.AppBar
import eu.kanade.presentation.manga.components.MarkdownRender
import eu.kanade.presentation.more.LogoHeader
import eu.kanade.presentation.util.Screen
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import mihon.app.di.appGraph
import org.intellij.markdown.flavours.gfm.GFMFlavourDescriptor
import tachiyomi.i18n.MR
import tachiyomi.presentation.core.components.ScrollbarLazyColumn
import tachiyomi.presentation.core.components.material.Scaffold
import tachiyomi.presentation.core.i18n.stringResource

/**
 * Shows the release notes of the latest version fetched from GitHub.
 */
object ChangelogScreen : Screen() {

    @Composable
    override fun Content() {
        val navigator = LocalNavigator.currentOrThrow
        val context = LocalContext.current
        var changelogInfo by remember { mutableStateOf<String?>(null) }

        LaunchedEffect(Unit) {
            changelogInfo = withContext(Dispatchers.IO) {
                context.appGraph.updateChecker.getReleaseNotes()
            }
        }

        Scaffold(
            topBar = { scrollBehavior ->
                AppBar(
                    title = stringResource(MR.strings.whats_new),
                    navigateUp = navigator::pop,
                    scrollBehavior = scrollBehavior,
                )
            },
        ) { contentPadding ->
            ScrollbarLazyColumn(contentPadding = contentPadding) {
                item {
                    LogoHeader(iconPadding = PaddingValues(vertical = 56.dp))
                }
                item {
                    val info = changelogInfo
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 16.dp, vertical = 8.dp),
                    ) {
                        when {
                            info == null -> CircularProgressIndicator()
                            info.isEmpty() -> Text(text = stringResource(MR.strings.no_changelog))
                            else -> MarkdownRender(
                                content = info,
                                flavour = remember { GFMFlavourDescriptor() },
                            )
                        }
                    }
                }
            }
        }
    }
}
