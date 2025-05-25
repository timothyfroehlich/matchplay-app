package com.matchplay.api

import io.ktor.client.*
import io.ktor.client.plugins.*
import io.ktor.client.request.*

class MatchplayClient(
    private val httpClient: HttpClient,
    private val apiKey: String,
    private val baseUrl: String = "https://app.matchplay.events/api"
) : MatchplayApiService, AutoCloseable {

    private val authenticatedHttpClient = httpClient.config {
        defaultRequest {
            url(baseUrl)
            header("X-Api-Key", apiKey)
        }
    }

    private val service = MatchplayApiServiceImpl(authenticatedHttpClient, baseUrl)

    override suspend fun getTournaments() = service.getTournaments()

    override suspend fun getTournamentDetails(tournamentId: String) =
        service.getTournamentDetails(tournamentId)

    override suspend fun getTournamentStandings(tournamentId: String) =
        service.getTournamentStandings(tournamentId)

    override suspend fun getTournamentRounds(tournamentId: String, status: String?) =
        service.getTournamentRounds(tournamentId, status)

    override suspend fun suggestScore(tournamentId: String, suggestion: ScoreSuggestion) =
        service.suggestScore(tournamentId, suggestion)

    override fun close() {
        authenticatedHttpClient.close()
    }
}
