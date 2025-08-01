package dev.alejandrorosas.schedule.ui

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ScrollableTabRow
import androidx.compose.material3.Surface
import androidx.compose.material3.Tab
import androidx.compose.material3.Text
import androidx.compose.material3.pulltorefresh.PullToRefreshBox
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.snapshotFlow
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import coil.compose.AsyncImage
import dev.alejandrorosas.strings.R.string
import dev.alejandrorosas.strings.getReadableDateString
import dev.alejandrorosas.strings.getReadableTimeString

@Composable
fun Schedule(
    @Suppress("UNUSED_PARAMETER") navController: NavController,
) {
    Screen()
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun Screen(
    modifier: Modifier = Modifier,
    scheduleViewModel: ScheduleViewModel = hiltViewModel(),
) {
    val standingsUiState by scheduleViewModel.uiState.collectAsState()
    val pagerState = rememberPagerState(pageCount = { standingsUiState.tabs.size }, initialPage = standingsUiState.selectedTabIndex)

    PullToRefreshBox(
        modifier = modifier,
        isRefreshing = standingsUiState.isRefreshing,
        onRefresh = { scheduleViewModel.refresh() },
    ) {
        Column(
            modifier =
                Modifier
                    .fillMaxWidth()
                    .fillMaxHeight(),
        ) {
            if (standingsUiState.tabs.isNotEmpty()) {
                ScrollableTabRow(
                    selectedTabIndex = pagerState.currentPage,
                ) {
                    standingsUiState.tabs.forEachIndexed { index, item ->
                        Tab(
                            selected = index == pagerState.currentPage,
                            text = { Text(text = item) },
                            onClick = { scheduleViewModel.onTabSelected(index) },
                        )
                    }
                }
                HorizontalPager(
                    state = pagerState,
                ) {
                    val tab = standingsUiState.matches[standingsUiState.tabs[it]] ?: emptyList()
                    LazyColumn(
                        modifier =
                            Modifier
                                .fillMaxWidth()
                                .fillMaxHeight(),
                    ) {
                        var lastMatchDay = ""
                        tab.forEach { item ->
                            val date = item.date.toLocalDate().getReadableDateString()
                            if (lastMatchDay != date) {
                                lastMatchDay = date

                                stickyHeader {
                                    Surface(Modifier.fillParentMaxWidth()) {
                                        Text(
                                            text = date,
                                            modifier =
                                                Modifier
                                                    .fillMaxWidth()
                                                    .padding(horizontal = 16.dp, vertical = 8.dp),
                                            fontStyle = FontStyle.Italic,
                                            color = Color.LightGray,
                                        )
                                    }
                                }
                            }
                            item {
                                Row(
                                    modifier =
                                        Modifier
                                            .fillMaxWidth()
                                            .padding(horizontal = 16.dp, vertical = 8.dp),
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.Center,
                                ) {
                                    Surface(
                                        modifier = Modifier.weight(.08f),
                                        shape = RoundedCornerShape(4.dp),
                                        color = MaterialTheme.colorScheme.primaryContainer,
                                    ) {
                                        Text(
                                            text = item.date.getReadableTimeString(),
                                            style = MaterialTheme.typography.labelSmall,
                                            modifier =
                                                Modifier
                                                    .padding(4.dp)
                                                    .fillMaxWidth(),
                                            color = MaterialTheme.colorScheme.onPrimaryContainer,
                                            textAlign = TextAlign.Center,
                                        )
                                    }
                                    Spacer(modifier = Modifier.weight(.04f))
                                    Text(
                                        item.team1,
                                        textAlign = TextAlign.End,
                                        modifier =
                                            Modifier
                                                .padding(4.dp)
                                                .weight(.1f),
                                    )
                                    AsyncImage(
                                        model = item.team1Image,
                                        contentDescription = null,
                                        modifier =
                                            Modifier
                                                .weight(.1f)
                                                .height(48.dp)
                                                .padding(vertical = 4.dp)
                                                .padding(horizontal = 8.dp),
                                    )
                                    Spacer(modifier = Modifier.weight(.02f))
                                    Column(modifier = Modifier.weight(.15f)) {
                                        Spacer(
                                            modifier =
                                                Modifier
                                                    .fillMaxWidth()
                                                    .padding(4.dp)
                                                    .height(MaterialTheme.typography.labelSmall.lineHeight.value.dp),
                                        )
                                        Surface(
                                            shape = RoundedCornerShape(4.dp),
                                            color = MaterialTheme.colorScheme.surfaceContainer,
                                        ) {
                                            Text(
                                                text = item.team1VSteam2Score,
                                                modifier =
                                                    Modifier
                                                        .padding(4.dp)
                                                        .fillMaxWidth(),
                                                color = MaterialTheme.colorScheme.onPrimaryContainer,
                                                textAlign = TextAlign.Center,
                                            )
                                        }
                                        Text(
                                            text = item.bestOf?.let { stringResource(string.match_best_of, item.bestOf) } ?: "",
                                            style = MaterialTheme.typography.labelSmall,
                                            modifier =
                                                Modifier
                                                    .padding(4.dp)
                                                    .fillMaxWidth(),
                                            color = MaterialTheme.colorScheme.onPrimaryContainer,
                                            textAlign = TextAlign.Center,
                                        )
                                    }

                                    Spacer(modifier = Modifier.weight(.02f))
                                    AsyncImage(
                                        model = item.team2Image,
                                        contentDescription = null,
                                        modifier =
                                            Modifier
                                                .weight(.1f)
                                                .height(48.dp)
                                                .padding(vertical = 4.dp)
                                                .padding(horizontal = 8.dp),
                                    )
                                    Text(
                                        item.team2,
                                        textAlign = TextAlign.Start,
                                        modifier =
                                            Modifier
                                                .padding(4.dp)
                                                .weight(.1f),
                                    )
                                }
                            }
                        }
                    }
                }
            }
        }
    }
    LaunchedEffect(pagerState) {
        snapshotFlow { pagerState.currentPage }.collect { page ->
            if (page != standingsUiState.selectedTabIndex) {
                scheduleViewModel.onTabSelected(page)
            }
        }
    }
    LaunchedEffect(standingsUiState.selectedTabIndex) {
        if (pagerState.currentPage != standingsUiState.selectedTabIndex) {
            pagerState.scrollToPage(standingsUiState.selectedTabIndex)
        }
    }
}

@Preview
@Composable
private fun DefaultPreview() {
//    Screen {}
}
