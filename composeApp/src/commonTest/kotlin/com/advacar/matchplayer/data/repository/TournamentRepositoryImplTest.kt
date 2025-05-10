package com.advacar.matchplayer.data.repository

import com.advacar.matchplayer.data.models.Round
import com.advacar.matchplayer.data.models.ScoreSuggestion
import com.advacar.matchplayer.data.models.Standing
import com.advacar.matchplayer.data.models.SuggestionResponse
import com.advacar.matchplayer.data.models.Tournament
import com.advacar.matchplayer.data.remote.MatchplayApiService
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.mockk
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue
import kotlinx.coroutines.test.runTest

class TournamentRepositoryImplTest {

    private val mockApiService: MatchplayApiService = mockk()
    private val repository: TournamentRepository = TournamentRepositoryImpl(mockApiService)

    @Test
    fun `getTournaments success`() = runTest {
        val mockTournaments = listOf(Tournament("t1", "Tournament 1", "active", "Group Match Play"))
        coEvery { mockApiService.getTournaments() } returns mockTournaments

        val result = repository.getTournaments()

        assertTrue(result.isSuccess)
        assertEquals(mockTournaments, result.getOrNull())
        coVerify(exactly = 1) { mockApiService.getTournaments() }
    }

    @Test
    fun `getTournaments failure`() = runTest {
        val exception = RuntimeException("Network error")
        coEvery { mockApiService.getTournaments() } throws exception

        val result = repository.getTournaments()

        assertTrue(result.isFailure)
        assertEquals(exception, result.exceptionOrNull())
        coVerify(exactly = 1) { mockApiService.getTournaments() }
    }

    @Test
    fun `getTournamentDetails success`() = runTest {
        val mockTournament = Tournament("t1", "Tournament 1", "active", "Group Match Play")
        coEvery { mockApiService.getTournamentDetails("t1") } returns mockTournament

        val result = repository.getTournamentDetails("t1")

        assertTrue(result.isSuccess)
        assertEquals(mockTournament, result.getOrNull())
        coVerify(exactly = 1) { mockApiService.getTournamentDetails("t1") }
    }

    @Test
    fun `getTournamentDetails failure`() = runTest {
        val exception = RuntimeException("API error")
        coEvery { mockApiService.getTournamentDetails("t1") } throws exception

        val result = repository.getTournamentDetails("t1")

        assertTrue(result.isFailure)
        assertEquals(exception, result.exceptionOrNull())
        coVerify(exactly = 1) { mockApiService.getTournamentDetails("t1") }
    }

    @Test
    fun `getTournamentStandings success`() = runTest {
        val mockStandings = listOf(Standing("p1", "Player 1", 1, 100.0, 10))
        coEvery { mockApiService.getTournamentStandings("t1") } returns mockStandings

        val result = repository.getTournamentStandings("t1")

        assertTrue(result.isSuccess)
        assertEquals(mockStandings, result.getOrNull())
        coVerify(exactly = 1) { mockApiService.getTournamentStandings("t1") }
    }

    @Test
    fun `getTournamentRounds success`() = runTest {
        val mockRounds = listOf(Round("r1", "Round 1", "active"))
        coEvery { mockApiService.getTournamentRounds("t1", "active") } returns mockRounds

        val result = repository.getTournamentRounds("t1", "active")

        assertTrue(result.isSuccess)
        assertEquals(mockRounds, result.getOrNull())
        coVerify(exactly = 1) { mockApiService.getTournamentRounds("t1", "active") }
    }

    @Test
    fun `getRoundDetails success`() = runTest {
        val mockRound = Round("r1", "Round 1", "active")
        coEvery { mockApiService.getRoundDetails("r1") } returns mockRound

        val result = repository.getRoundDetails("r1")

        assertTrue(result.isSuccess)
        assertEquals(mockRound, result.getOrNull())
        coVerify(exactly = 1) { mockApiService.getRoundDetails("r1") }
    }

    @Test
    fun `suggestScore success`() = runTest {
        val suggestion = ScoreSuggestion("g1", "p1", 100L)
        val mockResponse = SuggestionResponse(true, "Success")
        coEvery { mockApiService.suggestScore("r1", suggestion) } returns mockResponse

        val result = repository.suggestScore("r1", suggestion)

        assertTrue(result.isSuccess)
        assertEquals(mockResponse, result.getOrNull())
        coVerify(exactly = 1) { mockApiService.suggestScore("r1", suggestion) }
    }
}
