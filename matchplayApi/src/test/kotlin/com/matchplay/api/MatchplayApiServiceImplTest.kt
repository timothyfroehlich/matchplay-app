package com.matchplay.api

import com.matchplay.api.models.*
import io.ktor.client.HttpClient
import io.ktor.client.engine.mock.MockEngine
import io.ktor.client.engine.mock.respond
import io.ktor.client.plugins.contentnegotiation.ContentNegotiation
import io.ktor.http.HttpHeaders
import io.ktor.http.HttpStatusCode
import io.ktor.http.headersOf
import io.ktor.serialization.kotlinx.json.json
import io.ktor.utils.io.ByteReadChannel
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFailsWith
import kotlin.test.assertNotNull
import kotlinx.coroutines.test.runTest
import kotlinx.serialization.json.Json

class MatchplayApiServiceImplTest {

    private val json = Json {
        prettyPrint = true
        isLenient = true
        ignoreUnknownKeys = true
    }

    private fun createMockClient(mockEngine: MockEngine): HttpClient {
        return HttpClient(mockEngine) { install(ContentNegotiation) { json(json) } }
    }

    @Test
    fun `getTournaments success`() = runTest {
        val mockTournaments =
            listOf(
                Tournament("t1", "Tournament 1", "active", "Group Match Play", "2025-01-01"),
                Tournament("t2", "Tournament 2", "upcoming", "Group Match Play", "2025-02-01"),
            )
        val mockEngine = MockEngine { _ ->
            respond(
                content = ByteReadChannel(json.encodeToString(mockTournaments)),
                status = HttpStatusCode.OK,
                headers = headersOf(HttpHeaders.ContentType, "application/json"),
            )
        }
        val apiClient = MatchplayApiServiceImpl(createMockClient(mockEngine))
        val result = apiClient.getTournaments()
        assertEquals(2, result.size)
        assertEquals("Tournament 1", result[0].name)
    }

    @Test
    fun `getTournamentDetails success`() = runTest {
        val mockTournament =
            Tournament("t1", "Detailed Tournament", "active", "Group Match Play", "2025-01-01")
        val mockEngine = MockEngine { request ->
            assertEquals(
                "https://app.matchplay.events/api/tournaments/t1",
                request.url.toString()
            )
            respond(
                content = ByteReadChannel(json.encodeToString(mockTournament)),
                status = HttpStatusCode.OK,
                headers = headersOf(HttpHeaders.ContentType, "application/json"),
            )
        }
        val apiClient = MatchplayApiServiceImpl(createMockClient(mockEngine))
        val result = apiClient.getTournamentDetails("t1")
        assertNotNull(result)
        assertEquals("Detailed Tournament", result.name)
    }

    @Test
    fun `getTournamentStandings success`() = runTest {
        val mockStandings =
            listOf(
                Standing("p1", "Player One", 1, 100.0, 5),
                Standing("p2", "Player Two", 2, 90.0, 5),
            )
        val mockEngine = MockEngine { request ->
            assertEquals(
                "https://app.matchplay.events/api/tournaments/t1/standings",
                request.url.toString()
            )
            respond(
                content = ByteReadChannel(json.encodeToString(mockStandings)),
                status = HttpStatusCode.OK,
                headers = headersOf(HttpHeaders.ContentType, "application/json"),
            )
        }
        val apiClient = MatchplayApiServiceImpl(createMockClient(mockEngine))
        val result = apiClient.getTournamentStandings("t1")
        assertEquals(2, result.size)
        assertEquals("Player One", result[0].playerName)
    }

    @Test
    fun `getTournamentRounds success`() = runTest {
        val mockRounds =
            listOf(
                Round(
                    "r1",
                    "Round 1",
                    "completed",
                    games =
                        listOf(
                            Game(
                                "g1",
                                "Arena A",
                                "a1",
                                listOf("p1", "p2"),
                                listOf(PlayerScore("p1", 1000L)),
                                "completed",
                            )
                        ),
                ),
                Round("r2", "Round 2", "active"),
            )
        val mockEngine = MockEngine { request ->
            assertEquals(
                "https://app.matchplay.events/api/tournaments/t1/rounds",
                request.url.toString()
            )
            respond(
                content = ByteReadChannel(json.encodeToString(mockRounds)),
                status = HttpStatusCode.OK,
                headers = headersOf(HttpHeaders.ContentType, "application/json"),
            )
        }
        val apiClient = MatchplayApiServiceImpl(createMockClient(mockEngine))
        val result = apiClient.getTournamentRounds("t1")
        assertEquals(2, result.size)
        assertEquals("Round 1", result[0].name)
        assertEquals(1, result[0].games.size)
    }

    @Test
    fun `getTournamentRounds with status success`() = runTest {
        val mockRounds = listOf(Round("r2", "Round 2", "active"))
        val mockEngine = MockEngine { request ->
            assertEquals(
                "https://app.matchplay.events/api/tournaments/t1/rounds?status=active",
                request.url.toString()
            )
            respond(
                content = ByteReadChannel(json.encodeToString(mockRounds)),
                status = HttpStatusCode.OK,
                headers = headersOf(HttpHeaders.ContentType, "application/json"),
            )
        }
        val apiClient = MatchplayApiServiceImpl(createMockClient(mockEngine))
        val result = apiClient.getTournamentRounds("t1", "active")
        assertEquals(1, result.size)
        assertEquals("Round 2", result[0].name)
    }
}
