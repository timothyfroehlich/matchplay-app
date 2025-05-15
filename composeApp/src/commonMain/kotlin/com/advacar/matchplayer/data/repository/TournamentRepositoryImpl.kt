package com.advacar.matchplayer.data.repository

import com.advacar.matchplayer.data.models.Round
import com.advacar.matchplayer.data.models.ScoreSuggestion
import com.advacar.matchplayer.data.models.Standing
import com.advacar.matchplayer.data.models.SuggestionResponse
import com.advacar.matchplayer.data.models.Tournament
import com.advacar.matchplayer.data.remote.MatchplayApiService

class TournamentRepositoryImpl(private val apiService: MatchplayApiService) : TournamentRepository {

    override suspend fun getTournaments(): Result<List<Tournament>> {
        return try {
            Result.success(apiService.getTournaments())
        } catch (e: Exception) {
            // Log error e
            Result.failure(e)
        }
    }

    override suspend fun getTournamentDetails(tournamentId: String): Result<Tournament> {
        return try {
            Result.success(apiService.getTournamentDetails(tournamentId))
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun getTournamentStandings(tournamentId: String): Result<List<Standing>> {
        return try {
            Result.success(apiService.getTournamentStandings(tournamentId))
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun getTournamentRounds(
        tournamentId: String,
        status: String?,
    ): Result<List<Round>> {
        return try {
            Result.success(apiService.getTournamentRounds(tournamentId, status))
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}
