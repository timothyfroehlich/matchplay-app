package com.matchplay.api

import com.matchplay.api.models.*
import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.request.get
import io.ktor.client.request.parameter
import io.ktor.client.request.post
import io.ktor.client.request.setBody
import io.ktor.http.ContentType
import io.ktor.http.contentType

class MatchplayApiServiceImpl(
    private val httpClient: HttpClient,
    private val baseUrl: String = "https://app.matchplay.events/api"
) : MatchplayApiService {

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

    override suspend fun suggestScore(
        tournamentId: String,
        suggestion: ScoreSuggestion
    ): SuggestionResponse {
        return httpClient
            .post("$baseUrl/tournaments/$tournamentId/games/${suggestion.gameId}/suggest") {
                contentType(ContentType.Application.Json)
                setBody(suggestion)
            }
            .body()
    }
}
