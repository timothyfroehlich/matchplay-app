package com.advacar.matchplayer.data.models

import kotlinx.serialization.Serializable

@Serializable
data class Tournament(
    val tournamentId: String,
    val name: String,
    val status: String, // e.g., "active", "upcoming", "completed"
    val format: String, // e.g., "Group Match Play"
    val dateStart: String? = null // ISO 8601 or similar
)

@Serializable
data class Player(
    val playerId: String,
    val firstName: String,
    val lastName: String
)

@Serializable
data class Standing(
    val playerId: String,
    val playerName: String,
    val rank: Int,
    val points: Double,
    val gamesPlayed: Int
)

@Serializable
data class Round(
    val roundId: String,
    val name: String,
    val status: String, // e.g., "active", "completed", "upcoming"
    val games: List<Game> = emptyList()
)

@Serializable
data class Game(
    val gameId: String,
    val arenaName: String,
    val arenaId: String? = null,
    val playerIds: List<String> = emptyList(),
    val playerScores: List<PlayerScore> = emptyList(),
    val status: String // e.g., "pending", "active", "completed"
)

@Serializable
data class PlayerScore(
    val playerId: String,
    val score: Long
)

@Serializable
data class ScoreSuggestion(
    val gameId: String,
    val playerId: String, // Player for whom the score is being suggested
    val score: Long
)
