package dev.alejandrorosas.leaguepedia

import dev.alejandrorosas.leaguepedia.api.LeaguepediaService
import dev.alejandrorosas.leaguepedia.contract.LeaguepediaClient
import dev.alejandrorosas.leaguepedia.contract.LeaguepediaClient.Match
import dev.alejandrorosas.leaguepedia.contract.LeaguepediaClient.Standings
import java.time.LocalDateTime
import java.time.ZoneId
import java.time.format.DateTimeFormatter
import java.util.concurrent.ConcurrentHashMap

class LeaguepediaClientImpl(
    private val leaguepediaService: LeaguepediaService,
) : LeaguepediaClient {
    private val imagesCache = ConcurrentHashMap<String, String>()

    override suspend fun getImageUrl(image: String): String? =
        imagesCache.getOrPut(image) {
            leaguepediaService
                .getImageInfo("File:$image")
                .query.pages.values
                .firstOrNull()
                ?.imageInfo
                ?.firstOrNull()
                ?.url
        }

    // https://lol.fandom.com/wiki/Module:Team
    private suspend fun getImageUrl(
        teamName: String,
        shape: String,
    ): String? {
        val image = "${teamName}logo $shape.png"
        return imagesCache.getOrPut(image) {
            leaguepediaService
                .getImageInfo("File:$image")
                .query.pages.values
                .firstOrNull()
                ?.imageInfo
                ?.firstOrNull()
                ?.url
        }
    }

    // https://lol.fandom.com/wiki/Template:MatchSchedule/CargoDec
    // https://lol.fandom.com/wiki/Template:Teams/CargoDec
    @Suppress("ktlint:standard:max-line-length")
    override suspend fun getMatches(): List<LeaguepediaClient.Match> =
        leaguepediaService
            .getCargoQuery(
                tables = "Tournaments,MatchSchedule,Teams=T1,Teams=T2,Leagues",
                fields = "T1.Short=Team1Name,T2.Short=Team2Name,Team1,Team2,Team1Score,Team2Score,DateTime_UTC,Winner,Tab,Patch,Casters,Stream,N_Page,N_TabInPage,N_MatchInTab,N_MatchInPage,MatchDay,BestOf,Tournaments.StandardName=TournamentName",
                where = "Tournaments.Region='EMEA' AND Leagues.League_Short='LEC'",
                joinOn = "MatchSchedule.OverviewPage=Tournaments.OverviewPage, MatchSchedule.Team1=T1.OverviewPage, MatchSchedule.Team2=T2.OverviewPage, Tournaments.League=Leagues.League",
                orderBy = "DateTime_UTC DESC",
            ).cargoquery
            .map {
                Match(
                    team1 = it.content["Team1Name"].orEmpty(),
                    team2 = it.content["Team2Name"].orEmpty(),
                    team1Score = it.content["Team1Score"],
                    team2Score = it.content["Team2Score"],
                    team1Image = it.content["Team1"]?.let { image -> getImageUrl(image, "square") },
                    team2Image = it.content["Team2"]?.let { image -> getImageUrl(image, "square") },
                    date = LocalDateTime.parse(it.content["DateTime UTC"], dateTimeFormatter).toZonedLocalDateTime(),
                    winner = it.content["Winner"]?.toInt(),
                    tab = it.content["TournamentName"]!! + " " + it.content["Tab"] as String,
                    patch = it.content["Patch"],
                    casters = it.content["Casters"],
                    stream = it.content["Stream"],
                    matchDay = (it.content["MatchDay"] as String).toInt(),
                    bestOf = it.content["BestOf"]?.toInt(),
                    tournamentName = it.content["TournamentName"]!!,
                )
            }

    @Suppress("ktlint:standard:max-line-length")
    override suspend fun getGames(): List<LeaguepediaClient.Game> {
        login()
        return leaguepediaService
            .getCargoQuery(
                tables = "Leagues,Tournaments,ScoreboardGames=SG,Teams=T1,Teams=T2",
                fields = "T1.Short=Team1Name,SG.Team1=Team1,SG.Team1Kills=Team1Kills,T2.Short=Team2Name, SG.Team2=Team2, SG.Team2Kills=Team2Kills, SG.Winner=Winner, SG.Gamelength=Gamelength, SG.Patch=Patch, SG.DateTime_UTC=DateTime_UTC",
                where = "Tournaments.Region='EMEA' AND Leagues.League_Short='LEC'",
                joinOn = "Tournaments.League=Leagues.League, Tournaments.OverviewPage=SG.OverviewPage, SG.Team1=T1.OverviewPage, SG.Team2=T2.OverviewPage",
                orderBy = "SG.DateTime_UTC DESC",
            ).cargoquery
            .map {
                LeaguepediaClient.Game(
                    team1 = it.content["Team1Name"]!!,
                    team1Image = it.content["Team1"]?.let { image -> getImageUrl(image, "square") },
                    team1Kills = it.content["Team1Kills"]!!.toInt(),
                    team2 = it.content["Team2Name"]!!,
                    team2Image = it.content["Team2"]?.let { image -> getImageUrl(image, "square") },
                    team2Kills = it.content["Team2Kills"]!!.toInt(),
                    winner = it.content["Winner"]!!.toInt(),
                    gameLength = it.content["Gamelength"]!!,
                    patch = it.content["Patch"]!!,
                    date = LocalDateTime.parse(it.content["DateTime_UTC"], dateTimeFormatter).toZonedLocalDateTime(),
                )
            }
    }

    @Suppress("ktlint:standard:max-line-length")
    override suspend fun getStandings(): List<Standings> {
        return leaguepediaService
            .getCargoQuery(
                tables = "Tournaments,Standings,Teams",
                fields = "Standings.OverviewPage,Standings.Team,Place,Standings.WinGames,Standings.LossGames,Image,Tournaments.IsPlayoffs",
                where = "Tournaments.League='LVP Superliga' AND SplitNumber=1 AND Year='2025' AND NOT Tournaments.IsPlayoffs",
                joinOn = "Standings.Team=Teams.OverviewPage,Tournaments.OverviewPage=Standings.OverviewPage",
                orderBy = "N",
            ).cargoquery
            .mapNotNull {
                if (it.content["Team"] == null) {
                    return@mapNotNull null
                }
                Standings(
                    team = it.content["Team"]!!,
                    place = it.content["Place"]!!,
                    winGames = it.content["WinGames"]!!,
                    lossGames = it.content["LossGames"]!!,
                    image = it.content["Image"]?.let { image -> getImageUrl(image) },
                )
            }
    }

    override suspend fun login() {
        leaguepediaService.login(
            name = "",
            password = "",
            token = leaguepediaService.getToken().query.tokens.logintoken,
        )
    }

    companion object {
        private val dateTimeFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")

        private fun LocalDateTime.toZonedLocalDateTime(): LocalDateTime =
            this
                .atZone(ZoneId.of("UTC"))
                .withZoneSameInstant(ZoneId.systemDefault())
                .toLocalDateTime()
    }
}
