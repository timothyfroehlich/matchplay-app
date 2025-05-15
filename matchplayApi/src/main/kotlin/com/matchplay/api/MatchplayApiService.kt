package com.matchplay.api

import com.matchplay.api.models.*

interface MatchplayApiService {
    /** Fetches a list of tournaments. */
    suspend fun getTournaments(): List<Tournament>

    /** Fetches detailed information for a specific tournament. */
    suspend fun getTournamentDetails(tournamentId: String): Tournament

    /** Fetches the standings for a specific tournament. */
    suspend fun getTournamentStandings(tournamentId: String): List<Standing>

    /** Fetches the rounds for a specific tournament. */
    suspend fun getTournamentRounds(tournamentId: String, status: String? = null): List<Round>

    /** Submits a score suggestion for a player in a game. */
    suspend fun suggestScore(tournamentId: String, suggestion: ScoreSuggestion): SuggestionResponse
}
