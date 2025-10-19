package dev.alejandrorosas.leaguepedia.contract

import java.time.LocalDateTime

interface LeaguepediaClient {
    suspend fun getImageUrl(image: String): String?

    suspend fun getMatches(): List<Match>

    suspend fun getGames(): List<Game>

    data class Match(
        val team1: String,
        val team2: String,
        val team1Score: String?,
        val team2Score: String?,
        val team1Image: String?,
        val team2Image: String?,
        val date: LocalDateTime,
        val winner: Int?,
        val tab: String,
        val patch: String?,
        val casters: String?,
        val stream: String?,
        val matchDay: Int,
        val bestOf: Int?,
        val tournamentName: String,
    )

    data class Game(
        val team1: String,
        val team1Image: String?,
        val team1Kills: Int,
        val team2: String,
        val team2Image: String?,
        val team2Kills: Int,
        val winner: Int,
        val gameLength: String,
        val patch: String,
        val date: LocalDateTime,
    )

    suspend fun getStandings(): List<Standings>

    suspend fun login()

    class Standings(
        val team: String,
        val place: String,
        val winGames: String,
        val lossGames: String,
        val image: String?,
    )
}
