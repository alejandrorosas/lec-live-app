package dev.alejandrorosas.schedule.ui

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import dev.alejandrorosas.leaguepedia.contract.LeaguepediaClient
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import java.time.LocalDate
import java.time.LocalDateTime
import javax.inject.Inject

@HiltViewModel
class ScheduleViewModel @Inject constructor(
    private val leaguepediaClient: LeaguepediaClient,
) : ViewModel() {
    data class HomeUiState(
        val isRefreshing: Boolean = true,
        val selectedTabIndex: Int = 0,
        val tabs: List<String> = emptyList(),
        val matches: Map<String, List<Match>> = emptyMap(),
    )

    data class Match(
        val team1: String,
        val team2: String,
        val team1VSteam2Score: String,
        val team1Image: String?,
        val team2Image: String?,
        val date: LocalDateTime,
        val winner: Int?,
        val bestOf: Int?,
        val tab: String,
    )

    // Game UI state
    private val _uiState = MutableStateFlow(HomeUiState())
    val uiState: StateFlow<HomeUiState> = _uiState.asStateFlow()

    init {
        updateState()
    }

    fun refresh() {
        _uiState.value = _uiState.value.copy(isRefreshing = true)
        updateState()
    }

    private fun updateState() {
        viewModelScope.launch {
            try {
                val matches = leaguepediaClient.getMatches()
                val tabs = matches.map { it.tab }.distinct()
                val currentMatch = matches.firstOrNull { it.date.toLocalDate() <= LocalDate.now() }
                val selectedTabIndex = if (currentMatch != null) tabs.indexOf(currentMatch.tab) else 0

                _uiState.value =
                    HomeUiState(
                        isRefreshing = false,
                        selectedTabIndex = selectedTabIndex,
                        tabs = matches.map { it.tab }.distinct(),
                        matches =
                            matches.groupBy { it.tab }.mapValues {
                                it.value.map { match ->
                                    Match(
                                        team1 = match.team1,
                                        team2 = match.team2,
                                        team1VSteam2Score =
                                            if (match.team1Score.isNullOrBlank() || match.team2Score.isNullOrBlank()) {
                                                "-"
                                            } else {
                                                "${match.team1Score} - ${match.team2Score}"
                                            },
                                        date = match.date,
                                        winner = match.winner,
                                        bestOf = match.bestOf,
                                        tab = match.tab,
                                        team1Image = match.team1Image,
                                        team2Image = match.team2Image,
                                    )
                                }
                            },
                    )
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }

    fun onTabSelected(page: Int) {
        _uiState.value = _uiState.value.copy(selectedTabIndex = page)
    }
}
