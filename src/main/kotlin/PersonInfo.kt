package org.example

import kotlinx.serialization.Serializable

@Serializable
data class PersonInfo(
    val name: String,
    val age: Int?,
    val count: Int?
)
