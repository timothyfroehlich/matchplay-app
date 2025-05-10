package com.advacar.matchplayer.data.models

import kotlinx.serialization.Serializable

// Wrapper for API responses if needed, e.g. for pagination or metadata
// For now, we'll assume direct list/object returns.

// Example of a success response for POST, if the API returns more than just a status code
@Serializable
data class SuggestionResponse(
    val success: Boolean,
    val message: String? = null
)
