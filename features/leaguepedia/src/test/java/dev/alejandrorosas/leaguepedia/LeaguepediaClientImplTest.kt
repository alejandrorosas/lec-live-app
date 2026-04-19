package dev.alejandrorosas.leaguepedia

import dev.alejandrorosas.leaguepedia.api.LeaguepediaServiceFactory
import kotlinx.coroutines.test.runTest
import okhttp3.mockwebserver.MockResponse
import okhttp3.mockwebserver.MockWebServer
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test
import java.io.IOException

class LeaguepediaClientImplTest {
    private lateinit var server: MockWebServer
    private lateinit var client: LeaguepediaClientImpl

    @Before
    fun setUp() {
        server = MockWebServer()
        server.start()

        val service = LeaguepediaServiceFactory.create(server.url("/").toString())
        client = LeaguepediaClientImpl(service)
    }

    @After
    fun tearDown() {
        server.shutdown()
    }

    @Test
    fun `getImageUrls returns urls for all requested images`() =
        runTest {
            server.enqueue(
                MockResponse().setBody(
                    """
                    {
                      "batchcomplete": "",
                      "query": {
                        "pages": {
                          "100": {
                            "pageid": 100,
                            "ns": 6,
                            "title": "File:Team 1logo square.png",
                            "imagerepository": "local",
                            "imageinfo": [{"url": "https://example.com/team1.png"}]
                          },
                          "200": {
                            "pageid": 200,
                            "ns": 6,
                            "title": "File:Team 2logo square.png",
                            "imagerepository": "local",
                            "imageinfo": [{"url": "https://example.com/team2.png"}]
                          }
                        }
                      }
                    }
                    """.trimIndent(),
                ),
            )

            val result =
                client.getImageUrls(
                    listOf("Team 1logo square.png", "Team 2logo square.png"),
                )

            assertEquals(2, result.size)
            assertEquals("https://example.com/team1.png", result["Team 1logo square.png"])
            assertEquals("https://example.com/team2.png", result["Team 2logo square.png"])
        }

    @Test
    fun `getImageUrls caches results and does not call service again`() =
        runTest {
            val body =
                """
                {
                  "batchcomplete": "",
                  "query": {
                    "pages": {
                      "100": {
                        "pageid": 100,
                        "ns": 6,
                        "title": "File:Team 1logo square.png",
                        "imageinfo": [{"url": "https://example.com/team1.png"}]
                      }
                    }
                  }
                }
                """.trimIndent()
            server.enqueue(MockResponse().setBody(body))

            client.getImageUrls(listOf("Team 1logo square.png"))
            client.getImageUrls(listOf("Team 1logo square.png"))

            assertEquals(1, server.requestCount)
        }

    @Test
    fun `getImageUrls skips pages without imageInfo`() =
        runTest {
            server.enqueue(
                MockResponse().setBody(
                    """
                    {
                      "batchcomplete": "",
                      "query": {
                        "pages": {
                          "-1": {
                            "ns": 6,
                            "title": "File:Team 1logo square.png",
                            "missing": "",
                            "imagerepository": ""
                          },
                          "100": {
                            "pageid": 100,
                            "ns": 6,
                            "title": "File:Team 2logo square.png",
                            "imageinfo": [{"url": "https://example.com/team2.png"}]
                          }
                        }
                      }
                    }
                    """.trimIndent(),
                ),
            )

            val result =
                client.getImageUrls(
                    listOf("Team 1logo square.png", "Team 2logo square.png"),
                )

            assertEquals(1, result.size)
            assertEquals("https://example.com/team2.png", result["Team 2logo square.png"])
        }

    @Test(expected = IOException::class)
    fun `getImageUrls throws on api error`() =
        runTest {
            server.enqueue(
                MockResponse().setBody(
                    """
                    {
                      "error": {
                        "code": "ratelimited",
                        "info": "You've exceeded your rate limit."
                      }
                    }
                    """.trimIndent(),
                ),
            )

            client.getImageUrls(listOf("Team 1logo square.png"))
        }

    @Test
    fun `getImageUrls sends pipe-separated titles`() =
        runTest {
            server.enqueue(
                MockResponse().setBody(
                    """
                    {
                      "batchcomplete": "",
                      "query": {
                        "pages": {
                          "100": {
                            "pageid": 100,
                            "title": "File:Team 1logo square.png",
                            "imageinfo": [{"url": "https://example.com/team1.png"}]
                          },
                          "200": {
                            "pageid": 200,
                            "title": "File:Team 2logo square.png",
                            "imageinfo": [{"url": "https://example.com/team2.png"}]
                          }
                        }
                      }
                    }
                    """.trimIndent(),
                ),
            )

            client.getImageUrls(listOf("Team 1logo square.png", "Team 2logo square.png"))

            val request = server.takeRequest()
            val path = request.path!!
            assertTrue(path.contains("File%3ATeam"))
            assertTrue(path.contains("%7C") || path.contains("|"))
        }

    @Test
    fun `getImageUrls returns empty map for empty input`() =
        runTest {
            val result = client.getImageUrls(emptyList())

            assertTrue(result.isEmpty())
            assertEquals(0, server.requestCount)
        }

    @Test
    fun `getMatches returns matches with images`() =
        runTest {
            server.enqueue(
                MockResponse().setBody(
                    """
                    {
                      "cargoquery": [
                        {
                          "title": {
                            "Team1Name": "T1",
                            "Team2Name": "T2",
                            "Team1": "Team 1",
                            "Team2": "Team 2",
                            "Team1Score": "2",
                            "Team2Score": "1",
                            "DateTime UTC": "2025-03-15 18:00:00",
                            "Winner": "1",
                            "Tab": "Week 1",
                            "TournamentName": "Mock League",
                            "Patch": "15.5",
                            "Casters": "Caster A",
                            "Stream": "https://example.com/stream",
                            "MatchDay": "1",
                            "BestOf": "3"
                          }
                        }
                      ]
                    }
                    """.trimIndent(),
                ),
            )

            server.enqueue(
                MockResponse().setBody(
                    """
                    {
                      "batchcomplete": "",
                      "query": {
                        "pages": {
                          "100": {
                            "pageid": 100,
                            "title": "File:Team 1logo square.png",
                            "imageinfo": [{"url": "https://example.com/team1.png"}]
                          },
                          "200": {
                            "pageid": 200,
                            "title": "File:Team 2logo square.png",
                            "imageinfo": [{"url": "https://example.com/team2.png"}]
                          }
                        }
                      }
                    }
                    """.trimIndent(),
                ),
            )

            val matches = client.getMatches()

            assertEquals(1, matches.size)
            val match = matches.first()
            assertEquals("T1", match.team1)
            assertEquals("T2", match.team2)
            assertEquals("2", match.team1Score)
            assertEquals("1", match.team2Score)
            assertEquals("https://example.com/team1.png", match.team1Image)
            assertEquals("https://example.com/team2.png", match.team2Image)
            assertEquals(1, match.winner)
            assertEquals(3, match.bestOf)
        }

    @Test(expected = IOException::class)
    fun `getMatches throws on cargo api error`() =
        runTest {
            server.enqueue(
                MockResponse().setBody(
                    """
                    {
                      "error": {
                        "code": "ratelimited",
                        "info": "You've exceeded your rate limit."
                      }
                    }
                    """.trimIndent(),
                ),
            )

            client.getMatches()
        }

    @Test
    fun `getGames returns games with images`() =
        runTest {
            server.enqueue(
                MockResponse().setBody(
                    """
                    {
                      "cargoquery": [
                        {
                          "title": {
                            "Team1Name": "T1",
                            "Team1": "Team 1",
                            "Team1Kills": "15",
                            "Team2Name": "T2",
                            "Team2": "Team 2",
                            "Team2Kills": "10",
                            "Winner": "1",
                            "Gamelength": "32:15",
                            "Patch": "15.5",
                            "DateTime_UTC": "2025-03-15 18:00:00"
                          }
                        }
                      ]
                    }
                    """.trimIndent(),
                ),
            )

            server.enqueue(
                MockResponse().setBody(
                    """
                    {
                      "batchcomplete": "",
                      "query": {
                        "pages": {
                          "100": {
                            "pageid": 100,
                            "title": "File:Team 1logo square.png",
                            "imageinfo": [{"url": "https://example.com/team1.png"}]
                          },
                          "200": {
                            "pageid": 200,
                            "title": "File:Team 2logo square.png",
                            "imageinfo": [{"url": "https://example.com/team2.png"}]
                          }
                        }
                      }
                    }
                    """.trimIndent(),
                ),
            )

            val games = client.getGames()

            assertEquals(1, games.size)
            val game = games.first()
            assertEquals("T1", game.team1)
            assertEquals(15, game.team1Kills)
            assertEquals("https://example.com/team1.png", game.team1Image)
            assertEquals("T2", game.team2)
            assertEquals(10, game.team2Kills)
            assertEquals("https://example.com/team2.png", game.team2Image)
            assertEquals(1, game.winner)
            assertEquals("32:15", game.gameLength)
        }

    @Test(expected = IOException::class)
    fun `getGames throws on cargo api error`() =
        runTest {
            server.enqueue(
                MockResponse().setBody(
                    """
                    {
                      "error": {
                        "code": "ratelimited",
                        "info": "You've exceeded your rate limit."
                      }
                    }
                    """.trimIndent(),
                ),
            )

            client.getGames()
        }

    @Test
    fun `getStandings returns standings with images`() =
        runTest {
            server.enqueue(
                MockResponse().setBody(
                    """
                    {
                      "cargoquery": [
                        {
                          "title": {
                            "Team": "Team 1",
                            "Place": "1",
                            "WinGames": "10",
                            "LossGames": "2",
                            "Image": "Team 1logo square.png"
                          }
                        }
                      ]
                    }
                    """.trimIndent(),
                ),
            )

            server.enqueue(
                MockResponse().setBody(
                    """
                    {
                      "batchcomplete": "",
                      "query": {
                        "pages": {
                          "100": {
                            "pageid": 100,
                            "title": "File:Team 1logo square.png",
                            "imageinfo": [{"url": "https://example.com/team1.png"}]
                          }
                        }
                      }
                    }
                    """.trimIndent(),
                ),
            )

            val standings = client.getStandings()

            assertEquals(1, standings.size)
            val standing = standings.first()
            assertEquals("Team 1", standing.team)
            assertEquals("1", standing.place)
            assertEquals("10", standing.winGames)
            assertEquals("2", standing.lossGames)
            assertEquals("https://example.com/team1.png", standing.image)
        }

    @Test
    fun `getStandings skips entries with null team`() =
        runTest {
            server.enqueue(
                MockResponse().setBody(
                    """
                    {
                      "cargoquery": [
                        {
                          "title": {
                            "Team": null,
                            "Place": "1",
                            "WinGames": "0",
                            "LossGames": "0",
                            "Image": null
                          }
                        },
                        {
                          "title": {
                            "Team": "Team 2",
                            "Place": "2",
                            "WinGames": "8",
                            "LossGames": "4",
                            "Image": "Team 2logo square.png"
                          }
                        }
                      ]
                    }
                    """.trimIndent(),
                ),
            )

            server.enqueue(
                MockResponse().setBody(
                    """
                    {
                      "batchcomplete": "",
                      "query": {
                        "pages": {
                          "200": {
                            "pageid": 200,
                            "title": "File:Team 2logo square.png",
                            "imageinfo": [{"url": "https://example.com/team2.png"}]
                          }
                        }
                      }
                    }
                    """.trimIndent(),
                ),
            )

            val standings = client.getStandings()

            assertEquals(1, standings.size)
            assertEquals("Team 2", standings.first().team)
        }

    @Test(expected = IOException::class)
    fun `getStandings throws on cargo api error`() =
        runTest {
            server.enqueue(
                MockResponse().setBody(
                    """
                    {
                      "error": {
                        "code": "ratelimited",
                        "info": "You've exceeded your rate limit."
                      }
                    }
                    """.trimIndent(),
                ),
            )

            client.getStandings()
        }
}
