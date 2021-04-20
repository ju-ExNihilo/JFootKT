package com.jgdeveloppement.jg_foot.models

import java.io.Serializable
import java.util.*

data class Comment(
    val id: String = "none",
    val userId: String = "none",
    val userName: String = "none",
    val userUrlImage: String = "none",
    val fromId: String = "none",
    val comment: String = "none",
    val createdAt: Date = Date(),
    var countLike: Int = 0,
    var countComment: Int = 0,
): Serializable
