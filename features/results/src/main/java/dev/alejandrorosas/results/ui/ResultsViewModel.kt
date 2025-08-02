package dev.alejandrorosas.results.ui

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import dev.alejandrorosas.leaguepedia.contract.LeaguepediaClient
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ResultsViewModel @Inject constructor(
    private val leaguepediaClient: LeaguepediaClient,
) : ViewModel() {
    data class ResultsUiState(
        val isRefreshing: Boolean = true,
        val standings: List<Standings> = emptyList(),
    )

    data class Standings(
        val team1: String,
        val team1Kills: String,
        val team1Logo: String?,
        val team1Win: Boolean,
        val team2: String,
        val team2Kills: String,
        val team2Logo: String?,
        val team2Win: Boolean,
    )

    // Game UI state
    private val _uiState = MutableStateFlow(ResultsUiState())
    val uiState: StateFlow<ResultsUiState> = _uiState.asStateFlow()

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
//                val standings = leaguepediaClient.getStandings()
                val games = leaguepediaClient.getGames()

                _uiState.value =
                    ResultsUiState(
                        isRefreshing = false,
                        standings =
                            games.map {
                                Standings(
                                    team1 = it.team1,
                                    team1Kills = it.team1Kills.toString(),
                                    team1Logo = it.team1Image,
                                    team1Win = it.winner == 1,
                                    team2 = it.team2,
                                    team2Kills = it.team2Kills.toString(),
                                    team2Logo = it.team2Image,
                                    team2Win = it.winner == 2,
                                )
                            },
                    )
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }
}
