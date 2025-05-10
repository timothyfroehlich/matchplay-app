package com.advacar.matchplayer.data.remote

import com.advacar.matchplayer.data.models.Round
import com.advacar.matchplayer.data.models.ScoreSuggestion
import com.advacar.matchplayer.data.models.Standing
import com.advacar.matchplayer.data.models.SuggestionResponse
import com.advacar.matchplayer.data.models.Tournament
import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.request.get
import io.ktor.client.request.parameter
import io.ktor.client.request.post
import io.ktor.client.request.setBody
import io.ktor.http.ContentType
import io.ktor.http.contentType

class MatchplayApiServiceImpl(private val httpClient: HttpClient) : MatchplayApiService {

    private val baseUrl = "https://app.matchplay.events/api"

    override suspend fun getTournaments(): List<Tournament> {
        return httpClient.get("$baseUrl/tournaments").body()
    }

    override suspend fun getTournamentDetails(tournamentId: String): Tournament {
        return httpClient.get("$baseUrl/tournaments/$tournamentId").body()
    }

    override suspend fun getTournamentStandings(tournamentId: String): List<Standing> {
        return httpClient.get("$baseUrl/tournaments/$tournamentId/standings").body()
    }

    override suspend fun getTournamentRounds(tournamentId: String, status: String?): List<Round> {
        return httpClient
            .get("$baseUrl/tournaments/$tournamentId/rounds") {
                status?.let { parameter("status", it) }
            }
            .body()
    }

    override suspend fun getRoundDetails(roundId: String): Round {
        return httpClient.get("$baseUrl/rounds/$roundId").body()
    }

    override suspend fun suggestScore(
        roundId: String,
        scoreSuggestion: ScoreSuggestion,
    ): SuggestionResponse {
        return httpClient
            .post("$baseUrl/rounds/$roundId/scores/suggest") {
                contentType(ContentType.Application.Json)
                setBody(scoreSuggestion)
                // TODO: Add API Key header for authentication if required by this endpoint
                // headers.append("X-API-Key", "YOUR_MATCHPLAY_API_KEY")
            }
            .body()
    }
}
