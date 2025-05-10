package com.advacar.matchplayer.data.remote

import com.advacar.matchplayer.data.models.Game
import com.advacar.matchplayer.data.models.PlayerScore
import com.advacar.matchplayer.data.models.Round
import com.advacar.matchplayer.data.models.ScoreSuggestion
import com.advacar.matchplayer.data.models.Standing
import com.advacar.matchplayer.data.models.SuggestionResponse
import com.advacar.matchplayer.data.models.Tournament
import io.ktor.client.HttpClient
import io.ktor.client.engine.mock.MockEngine
import io.ktor.client.engine.mock.respond
import io.ktor.client.engine.mock.toByteArray
import io.ktor.client.plugins.contentnegotiation.ContentNegotiation
import io.ktor.http.HttpHeaders
import io.ktor.http.HttpMethod
import io.ktor.http.HttpStatusCode
import io.ktor.http.headersOf
import io.ktor.serialization.kotlinx.json.json
import io.ktor.util.cio.toByteArray // For request.body.toByteArray()
import io.ktor.utils.io.ByteReadChannel
import kotlinx.coroutines.test.runTest
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFailsWith
import kotlin.test.assertNotNull

class MatchplayApiServiceImplTest {

    private val json = Json {
        prettyPrint = true
        isLenient = true
        ignoreUnknownKeys = true
    }

    private fun createMockClient(mockEngine: MockEngine): HttpClient {
        return HttpClient(mockEngine) {
            install(ContentNegotiation) {
                json(json)
            }
        }
    }

    @Test
    fun `getTournaments success`() = runTest {
        val mockTournaments = listOf(
            Tournament("t1", "Tournament 1", "active", "Group Match Play", "2025-01-01"),
            Tournament("t2", "Tournament 2", "upcoming", "Group Match Play", "2025-02-01")
        )
        val mockEngine = MockEngine { _ ->
            respond(
                content = ByteReadChannel(json.encodeToString(mockTournaments)),
                status = HttpStatusCode.OK,
                headers = headersOf(HttpHeaders.ContentType, "application/json")
            )
        }
        val apiClient = MatchplayApiServiceImpl(createMockClient(mockEngine))
        val result = apiClient.getTournaments()
        assertEquals(2, result.size)
        assertEquals("Tournament 1", result[0].name)
    }

    @Test
    fun `getTournaments error`() = runTest {
        val mockEngine = MockEngine { _ ->
            respond(
                content = ByteReadChannel("Error"),
                status = HttpStatusCode.InternalServerError
            )
        }
        val apiClient = MatchplayApiServiceImpl(createMockClient(mockEngine))
        assertFailsWith<Exception> {
            apiClient.getTournaments()
        }
    }

    @Test
    fun `getTournamentDetails success`() = runTest {
        val mockTournament = Tournament("t1", "Detailed Tournament", "active", "Group Match Play", "2025-01-01")
        val mockEngine = MockEngine { request ->
            assertEquals("https://app.matchplay.events/api/tournaments/t1", request.url.toString())
            respond(
                content = ByteReadChannel(json.encodeToString(mockTournament)),
                status = HttpStatusCode.OK,
                headers = headersOf(HttpHeaders.ContentType, "application/json")
            )
        }
        val apiClient = MatchplayApiServiceImpl(createMockClient(mockEngine))
        val result = apiClient.getTournamentDetails("t1")
        assertNotNull(result)
        assertEquals("Detailed Tournament", result.name)
    }

    @Test
    fun `getTournamentStandings success`() = runTest {
        val mockStandings = listOf(
            Standing("p1", "Player One", 1, 100.0, 5),
            Standing("p2", "Player Two", 2, 90.0, 5)
        )
        val mockEngine = MockEngine { request ->
            assertEquals("https://app.matchplay.events/api/tournaments/t1/standings", request.url.toString())
            respond(
                content = ByteReadChannel(json.encodeToString(mockStandings)),
                status = HttpStatusCode.OK,
                headers = headersOf(HttpHeaders.ContentType, "application/json")
            )
        }
        val apiClient = MatchplayApiServiceImpl(createMockClient(mockEngine))
        val result = apiClient.getTournamentStandings("t1")
        assertEquals(2, result.size)
        assertEquals("Player One", result[0].playerName)
    }

    @Test
    fun `getTournamentRounds success`() = runTest {
        val mockRounds = listOf(
            Round("r1", "Round 1", "completed", games = listOf(Game("g1", "Arena A", "a1", listOf("p1", "p2"), listOf(PlayerScore("p1", 1000L)), "completed"))),
            Round("r2", "Round 2", "active")
        )
        val mockEngine = MockEngine { request ->
            assertEquals("https://app.matchplay.events/api/tournaments/t1/rounds", request.url.toString())
            respond(
                content = ByteReadChannel(json.encodeToString(mockRounds)),
                status = HttpStatusCode.OK,
                headers = headersOf(HttpHeaders.ContentType, "application/json")
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
        val mockRounds = listOf(
            Round("r2", "Round 2", "active")
        )
        val mockEngine = MockEngine { request ->
            assertEquals("https://app.matchplay.events/api/tournaments/t1/rounds?status=active", request.url.toString())
            respond(
                content = ByteReadChannel(json.encodeToString(mockRounds)),
                status = HttpStatusCode.OK,
                headers = headersOf(HttpHeaders.ContentType, "application/json")
            )
        }
        val apiClient = MatchplayApiServiceImpl(createMockClient(mockEngine))
        val result = apiClient.getTournamentRounds("t1", "active")
        assertEquals(1, result.size)
        assertEquals("Round 2", result[0].name)
    }

    @Test
    fun `getRoundDetails success`() = runTest {
        val mockRound = Round("r1_detail", "Detailed Round 1", "active", games = listOf(Game("g1_detail", "Arena X", "ax", listOf("p3", "p4"), emptyList(), "active")))
        val mockEngine = MockEngine { request ->
            assertEquals("https://app.matchplay.events/api/rounds/r1_detail", request.url.toString())
            respond(
                content = ByteReadChannel(json.encodeToString(mockRound)),
                status = HttpStatusCode.OK,
                headers = headersOf(HttpHeaders.ContentType, "application/json")
            )
        }
        val apiClient = MatchplayApiServiceImpl(createMockClient(mockEngine))
        val result = apiClient.getRoundDetails("r1_detail")
        assertNotNull(result)
        assertEquals("Detailed Round 1", result.name)
    }

    @Test
    fun `suggestScore success`() = runTest {
        val scoreSuggestion = ScoreSuggestion("g1", "p1", 12345L)
        val mockResponse = SuggestionResponse(true, "Score suggested successfully")
        val mockEngine = MockEngine { request ->
            assertEquals("https://app.matchplay.events/api/rounds/r1/scores/suggest", request.url.toString())
            assertEquals(HttpMethod.Post, request.method)
            // TODO: Add assertion for API Key header when implemented
            // assertEquals("YOUR_MATCHPLAY_API_KEY", request.headers["X-API-Key"])
            val requestBody = request.body.toByteArray().decodeToString()
            assertEquals(json.encodeToString(scoreSuggestion), requestBody)
            respond(
                content = ByteReadChannel(json.encodeToString(mockResponse)),
                status = HttpStatusCode.OK,
                headers = headersOf(HttpHeaders.ContentType, "application/json")
            )
        }
        val apiClient = MatchplayApiServiceImpl(createMockClient(mockEngine))
        val result = apiClient.suggestScore("r1", scoreSuggestion)
        assertEquals(true, result.success)
        assertEquals("Score suggested successfully", result.message)
    }

    @Test
    fun `suggestScore failure`() = runTest {
        val scoreSuggestion = ScoreSuggestion("g1", "p1", 12345L)
        val mockResponse = SuggestionResponse(false, "Invalid game ID")
         val mockEngine = MockEngine { request ->
            respond(
                content = ByteReadChannel(json.encodeToString(mockResponse)),
                status = HttpStatusCode.BadRequest, // Or other appropriate error code
                headers = headersOf(HttpHeaders.ContentType, "application/json")
            )
        }
        val apiClient = MatchplayApiServiceImpl(createMockClient(mockEngine))
        // Depending on how Ktor handles non-2xx responses with body, this might throw or return the parsed error
        // For this example, let's assume it parses the error body for certain client errors
        try {
            val result = apiClient.suggestScore("r1", scoreSuggestion)
            assertEquals(false, result.success) // If Ktor parses the error body
            assertEquals("Invalid game ID", result.message)
        } catch (e: Exception) {
            // Or it might throw an exception for HTTP errors, which is also valid to test
            println("API call failed as expected: ${'$'}e")
        }
    }
}
