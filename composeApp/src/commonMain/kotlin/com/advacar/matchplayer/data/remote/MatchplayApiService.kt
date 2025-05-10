package com.advacar.matchplayer.data.remote

import com.advacar.matchplayer.data.models.Round
import com.advacar.matchplayer.data.models.ScoreSuggestion
import com.advacar.matchplayer.data.models.Standing
import com.advacar.matchplayer.data.models.SuggestionResponse
import com.advacar.matchplayer.data.models.Tournament

interface MatchplayApiService {
    /** Fetches a list of tournaments. Parameters like 'nearby', 'status' can be added as needed. */
    suspend fun getTournaments(
        // Example parameters, adjust based on actual API capabilities
        // status: String? = "active",
        // registeredPlayerId: String? = null,
        // latitude: Double? = null,
        // longitude: Double? = null
    ): List<Tournament>

    /** Fetches detailed information for a specific tournament. */
    suspend fun getTournamentDetails(
        tournamentId: String
    ): Tournament // Assuming the details endpoint returns a full Tournament object

    /** Fetches the standings for a specific tournament. */
    suspend fun getTournamentStandings(tournamentId: String): List<Standing>

    /**
     * Fetches the rounds for a specific tournament. May include parameters to filter by status
     * (e.g., "active", "completed").
     */
    suspend fun getTournamentRounds(tournamentId: String, status: String? = null): List<Round>

    /**
     * Fetches details for a specific round. This might be needed if getTournamentRounds doesn't
     * provide enough detail for active games.
     */
    suspend fun getRoundDetails(roundId: String): Round

    /** Submits a score suggestion for a game. Requires authentication. */
    suspend fun suggestScore(roundId: String, scoreSuggestion: ScoreSuggestion): SuggestionResponse
}
