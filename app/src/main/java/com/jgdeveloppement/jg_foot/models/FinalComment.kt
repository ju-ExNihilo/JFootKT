package com.jgdeveloppement.jg_foot.models


import java.io.Serializable
import java.util.*

data class FinalComment(
    val id: String = "none",
    val userId: String = "none",
    val userName: String = "none",
    val userUrlImage: String = "none",
    val fromId: String = "none",
    val createdAt: Date = Date(),
    val comment: String = "none",
    var countLike: Int = 0,
    var countComment: Int = 0,
    var haveLiked: Boolean = false
): Serializable
