package dev.alejandrorosas.results.ui

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Card
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Text
import androidx.compose.material3.pulltorefresh.PullToRefreshBox
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import coil.compose.AsyncImage

@Composable
fun Results(
    @Suppress("UNUSED_PARAMETER") navController: NavController,
) {
    Screen()
}

@OptIn(ExperimentalMaterial3Api::class, ExperimentalFoundationApi::class)
@Composable
fun Screen(
    modifier: Modifier = Modifier,
    resultsViewModel: ResultsViewModel = hiltViewModel(),
) {
    val homeUiState by resultsViewModel.uiState.collectAsState()

    PullToRefreshBox(
        modifier = modifier,
        isRefreshing = homeUiState.isRefreshing,
        onRefresh = { resultsViewModel.refresh() },
    ) {
        LazyColumn(
            modifier =
                Modifier
                    .fillMaxWidth()
                    .fillMaxHeight(),
        ) {
            items(homeUiState.standings) { ResultsItem(it) }
            item { Spacer(modifier = Modifier.padding(24.dp)) }
        }
    }
}

@Composable
fun ResultsItem(
    item: ResultsViewModel.Standings,
    modifier: Modifier = Modifier,
) {
    Card(
        modifier =
            modifier
                .padding(
                    horizontal = 8.dp,
                    vertical = 4.dp,
                ).fillMaxWidth(),
    ) {
        Box(
            modifier =
                Modifier.background(
                    brush =
                        Brush.horizontalGradient(
                            colors =
                                listOf(
                                    if (item.team1Win) Color(0xFF005A82) else Color.Transparent,
                                    Color.Transparent,
                                    Color.Transparent,
                                    Color.Transparent,
                                    if (item.team2Win) Color(0xFFFF5A5A) else Color.Transparent,
                                ),
                        ),
                ),
        ) {
            Row(
                modifier =
                    Modifier.padding(
                        horizontal = 8.dp,
                        vertical = 4.dp,
                    ),
                verticalAlignment = Alignment.CenterVertically,
            ) {
                Text(item.team1, modifier = Modifier.weight(.1f), fontWeight = if (item.team1Win) FontWeight.Bold else FontWeight.Normal)
                AsyncImage(
                    model = item.team1Logo,
                    contentDescription = null,
                    modifier =
                        Modifier
                            .weight(.2f)
                            .height(48.dp)
                            .padding(vertical = 4.dp)
                            .padding(end = 8.dp),
                )
                Text(
                    item.team1Kills,
                    modifier = Modifier.weight(.1f),
                    textAlign = TextAlign.Center,
                    fontWeight = if (item.team1Win) FontWeight.Bold else FontWeight.Normal,
                )
                Text("-", modifier = Modifier.weight(.1f), textAlign = TextAlign.Center)
                Text(
                    item.team2Kills,
                    modifier = Modifier.weight(.1f),
                    textAlign = TextAlign.Center,
                    fontWeight = if (item.team2Win) FontWeight.Bold else FontWeight.Normal,
                )
                AsyncImage(
                    model = item.team2Logo,
                    contentDescription = null,
                    modifier =
                        Modifier
                            .weight(.2f)
                            .height(48.dp)
                            .padding(vertical = 4.dp)
                            .padding(end = 8.dp),
                )
                Text(
                    item.team2,
                    modifier = Modifier.weight(.1f),
                    fontWeight = if (item.team2Win) FontWeight.Bold else FontWeight.Normal,
                )
            }
        }
    }
}

@Preview
@Composable
private fun DefaultPreview() {
//    Screen {}
}
