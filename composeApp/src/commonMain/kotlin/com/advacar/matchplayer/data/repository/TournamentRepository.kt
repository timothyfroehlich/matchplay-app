package com.advacar.matchplayer.data.repository

import com.advacar.matchplayer.data.models.Round
import com.advacar.matchplayer.data.models.ScoreSuggestion
import com.advacar.matchplayer.data.models.Standing
import com.advacar.matchplayer.data.models.SuggestionResponse
import com.advacar.matchplayer.data.models.Tournament

/**
 * Repository interface for accessing tournament data. This abstracts the data source (network,
 * cache, etc.) from the ViewModels.
 */
interface TournamentRepository {
    suspend fun getTournaments(): Result<List<Tournament>>

    suspend fun getTournamentDetails(tournamentId: String): Result<Tournament>

    suspend fun getTournamentStandings(tournamentId: String): Result<List<Standing>>

    suspend fun getTournamentRounds(
        tournamentId: String,
        status: String? = null,
    ): Result<List<Round>>
}
