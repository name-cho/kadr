package eu.kanade.tachiyomi.ui.home

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.core.spring
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.scaleIn
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Badge
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.adaptive.navigationsuite.NavigationSuiteDefaults
import androidx.compose.material3.adaptive.navigationsuite.NavigationSuiteItem
import androidx.compose.material3.adaptive.navigationsuite.NavigationSuiteScaffold
import androidx.compose.material3.adaptive.navigationsuite.NavigationSuiteType
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.produceState
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.rememberUpdatedState
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.nestedscroll.NestedScrollConnection
import androidx.compose.ui.input.nestedscroll.NestedScrollSource
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.Velocity
import androidx.compose.ui.util.fastForEach
import androidx.navigationevent.NavigationEventInfo
import androidx.navigationevent.compose.NavigationBackHandler
import androidx.navigationevent.compose.rememberNavigationEventState
import cafe.adriel.voyager.navigator.LocalNavigator
import cafe.adriel.voyager.navigator.currentOrThrow
import cafe.adriel.voyager.navigator.tab.LocalTabNavigator
import cafe.adriel.voyager.navigator.tab.TabNavigator
import eu.kanade.presentation.components.ExpressiveNavigationBar
import eu.kanade.presentation.util.Screen
import eu.kanade.presentation.util.isTabletUi
import eu.kanade.tachiyomi.ui.browse.BrowseTab
import eu.kanade.tachiyomi.ui.download.DownloadQueueScreen
import eu.kanade.tachiyomi.ui.history.HistoryTab
import eu.kanade.tachiyomi.ui.library.LibraryTab
import eu.kanade.tachiyomi.ui.manga.MangaScreen
import eu.kanade.tachiyomi.ui.more.MoreTab
import eu.kanade.tachiyomi.ui.updates.UpdatesTab
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.launch
import mihon.app.di.appGraph
import tachiyomi.i18n.MR
import tachiyomi.presentation.core.i18n.pluralStringResource

object HomeScreen : Screen() {

    private val librarySearchEvent = Channel<String>()
    private val openTabEvent = Channel<Tab>()
    private val showBottomNavEvent = Channel<Boolean>()

    @Suppress("ConstPropertyName")
    private const val TabFadeDuration = 200

    @Suppress("ConstPropertyName")
    private const val TabNavigatorKey = "HomeTabs"

    private val TABS = listOf(
        LibraryTab,
        UpdatesTab,
        HistoryTab,
        BrowseTab,
        MoreTab,
    )

    @Composable
    override fun Content() {
        val navigator = LocalNavigator.currentOrThrow
        TabNavigator(
            tab = LibraryTab,
            key = TabNavigatorKey,
        ) { tabNavigator ->
            // Provide usable navigator to content screen
            CompositionLocalProvider(LocalNavigator provides navigator) {
                val tabletUi = isTabletUi()
                var showBottomNav by remember { mutableStateOf(true) }
                // While a screen pins the bar (e.g. selection mode), scroll direction
                // must not override its visibility.
                var navBarPinned by remember { mutableStateOf(false) }
                val tabScope = rememberCoroutineScope()

                LaunchedEffect(tabletUi) {
                    if (tabletUi) {
                        showBottomNav = true
                        return@LaunchedEffect
                    }
                    showBottomNavEvent.receiveAsFlow().collectLatest { show ->
                        navBarPinned = !show
                        showBottomNav = show
                    }
                }

                if (tabletUi) {
                    NavigationSuiteScaffold(
                        navigationSuiteType = NavigationSuiteType.NavigationRail,
                        navigationSuiteColors = NavigationSuiteDefaults.colors(
                            navigationRailContainerColor = MaterialTheme.colorScheme
                                .surfaceContainerLow,
                        ),
                        navigationItemVerticalArrangement = Arrangement.Center,
                        navigationItems = {
                            TABS.fastForEach { NavigationSuiteItem(it, NavigationSuiteType.NavigationRail) }
                        },
                    ) {
                        TabContent(tabNavigator)
                    }
                } else {
                    androidx.compose.material3.Scaffold(
                        containerColor = Color.Transparent,
                        contentWindowInsets = WindowInsets(0, 0, 0, 0),
                        bottomBar = {
                            ExpressiveNavigationBar(
                                tabs = TABS,
                                selectedTab = tabNavigator.current as eu.kanade.presentation.util.Tab,
                                visible = showBottomNav,
                                onSelect = { tab ->
                                    if (tabNavigator.current::class != tab::class) {
                                        tabNavigator.current = tab
                                    } else {
                                        tabScope.launch { tab.onReselect(navigator) }
                                    }
                                },
                                badge = { tab -> tabBadge(tab)?.invoke() },
                            )
                        },
                    ) { contentPadding ->
                        TabContent(
                            tabNavigator = tabNavigator,
                            modifier = Modifier.padding(bottom = contentPadding.calculateBottomPadding()),
                            onScrollDirectionChanged = { scrolledDown ->
                                if (!navBarPinned) {
                                    showBottomNav = !scrolledDown
                                }
                            },
                        )
                    }
                }
            }

            val goToLibraryTab = { tabNavigator.current = LibraryTab }

            val tabBackState = rememberNavigationEventState(currentInfo = NavigationEventInfo.None)
            NavigationBackHandler(
                state = tabBackState,
                isBackEnabled = tabNavigator.current != LibraryTab,
                onBackCompleted = goToLibraryTab,
            )

            LaunchedEffect(Unit) {
                launch {
                    librarySearchEvent.receiveAsFlow().collectLatest {
                        goToLibraryTab()
                        LibraryTab.search(it)
                    }
                }
                launch {
                    openTabEvent.receiveAsFlow().collectLatest {
                        tabNavigator.current = when (it) {
                            is Tab.Library -> LibraryTab
                            Tab.Updates -> UpdatesTab
                            Tab.History -> HistoryTab
                            is Tab.Browse -> {
                                if (it.toExtensions) {
                                    BrowseTab.showExtension()
                                }
                                BrowseTab
                            }
                            is Tab.More -> MoreTab
                        }

                        if (it is Tab.Library && it.mangaIdToOpen != null) {
                            navigator.push(MangaScreen(it.mangaIdToOpen))
                        }
                        if (it is Tab.More && it.toDownloads) {
                            navigator.push(DownloadQueueScreen)
                        }
                    }
                }
            }
        }
    }

    @Composable
    private fun TabContent(
        tabNavigator: TabNavigator,
        modifier: Modifier = Modifier,
        onScrollDirectionChanged: (scrolledDown: Boolean) -> Unit = {},
    ) {
        val scrollHandler = rememberUpdatedState(onScrollDirectionChanged)
        val scrollConnection = remember {
            NavBarScrollConnection(
                onHide = { scrollHandler.value(true) },
                onShow = { scrollHandler.value(false) },
            )
        }

        // A new tab starts at the top, so the bar always comes back with it.
        LaunchedEffect(tabNavigator.current) {
            scrollConnection.reset()
            scrollHandler.value(false)
        }

        AnimatedContent(
            targetState = tabNavigator.current,
            modifier = modifier.nestedScroll(scrollConnection),
            transitionSpec = {
                (
                    fadeIn(animationSpec = tween(durationMillis = 220)) +
                        scaleIn(
                            initialScale = 0.94f,
                            animationSpec = spring(dampingRatio = 0.72f, stiffness = 380f),
                        )
                    ) togetherWith fadeOut(animationSpec = tween(durationMillis = 140))
            },
            label = "tabContent",
        ) {
            tabNavigator.saveableState(key = "currentTab", it) {
                it.Content()
            }
        }
    }

    @Composable
    private fun NavigationSuiteItem(
        tab: eu.kanade.presentation.util.Tab,
        navigationSuiteType: NavigationSuiteType,
    ) {
        val tabNavigator = LocalTabNavigator.current
        val navigator = LocalNavigator.currentOrThrow
        val scope = rememberCoroutineScope()
        val selected = tabNavigator.current::class == tab::class
        NavigationSuiteItem(
            navigationSuiteType = navigationSuiteType,
            selected = selected,
            onClick = {
                if (!selected) {
                    tabNavigator.current = tab
                } else {
                    scope.launch { tab.onReselect(navigator) }
                }
            },
            icon = {
                Icon(
                    painter = tab.options.icon!!,
                    contentDescription = tab.options.title,
                )
            },
            label = {
                Text(
                    text = tab.options.title,
                    style = MaterialTheme.typography.labelLarge,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                )
            },
            badge = tabBadge(tab),
        )
    }

    @Composable
    private fun tabBadge(tab: eu.kanade.presentation.util.Tab): (@Composable () -> Unit)? {
        val context = LocalContext.current
        val count by produceState(initialValue = 0, tab) {
            val graph = context.appGraph
            when (tab) {
                is UpdatesTab -> {
                    combine(
                        graph.libraryPreferences.newShowUpdatesCount.changes(),
                        graph.libraryPreferences.newUpdatesCount.changes(),
                    ) { show, count ->
                        if (show) count else 0
                    }
                        .collectLatest { value = it }
                }

                is BrowseTab -> {
                    graph.sourcePreferences.extensionUpdatesCount.changes()
                        .collectLatest { value = it }
                }

                else -> value = 0
            }
        }
        if (count <= 0) return null
        return {
            Badge {
                val desc = when (tab) {
                    is UpdatesTab -> pluralStringResource(
                        MR.plurals.notification_chapters_generic,
                        count = count,
                        count,
                    )

                    is BrowseTab -> pluralStringResource(
                        MR.plurals.update_check_notification_ext_updates,
                        count = count,
                        count,
                    )

                    else -> null
                }
                Text(
                    text = count.toString(),
                    modifier = Modifier.semantics {
                        if (desc != null) contentDescription = desc
                    },
                )
            }
        }
    }

    suspend fun search(query: String) {
        librarySearchEvent.send(query)
    }

    suspend fun openTab(tab: Tab) {
        openTabEvent.send(tab)
    }

    suspend fun showBottomNav(show: Boolean) {
        showBottomNavEvent.send(show)
    }

    sealed interface Tab {
        data class Library(val mangaIdToOpen: Long? = null) : Tab
        data object Updates : Tab
        data object History : Tab
        data class Browse(val toExtensions: Boolean = false) : Tab
        data class More(val toDownloads: Boolean) : Tab
    }
}

// How far the content has to be dragged up before the bar hides. Keeps taps,
// tiny scrolls and overscroll bounces from collapsing it.
private const val NAV_BAR_HIDE_THRESHOLD_PX = 140f
private const val NAV_BAR_HIDE_FLING_VELOCITY = 1_500f

/**
 * Hides the navigation bar as soon as the content is dragged up (reading
 * further) and brings it back on the first downward drag.
 *
 * Compose reports the finger delta: dragging the content up reports a negative
 * `y`, dragging it back down reports a positive `y`.
 */
private class NavBarScrollConnection(
    private val onHide: () -> Unit,
    private val onShow: () -> Unit,
) : NestedScrollConnection {
    private var accumulatedDown = 0f
    private var hidden = false

    fun reset() {
        accumulatedDown = 0f
        hidden = false
    }

    override fun onPreScroll(available: Offset, source: NestedScrollSource): Offset {
        if (source != NestedScrollSource.UserInput) return Offset.Zero

        val dy = available.y
        when {
            dy < 0f -> {
                accumulatedDown = (accumulatedDown - dy).coerceAtMost(NAV_BAR_HIDE_THRESHOLD_PX)
                if (!hidden && accumulatedDown >= NAV_BAR_HIDE_THRESHOLD_PX) {
                    hidden = true
                    onHide()
                }
            }
            dy > 0f -> {
                accumulatedDown = 0f
                if (hidden) {
                    hidden = false
                    onShow()
                }
            }
        }
        return Offset.Zero
    }

    override suspend fun onPreFling(available: Velocity): Velocity {
        when {
            available.y <= -NAV_BAR_HIDE_FLING_VELOCITY && !hidden -> {
                hidden = true
                onHide()
            }
            available.y >= NAV_BAR_HIDE_FLING_VELOCITY && hidden -> {
                hidden = false
                onShow()
            }
        }
        return Velocity.Zero
    }
}
