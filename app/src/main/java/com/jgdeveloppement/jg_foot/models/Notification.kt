package com.jgdeveloppement.jg_foot.models

import java.util.*

data class Notification(
        val id: String = "none",
        val userName: String = "none",
        val commentId: String = "none",
        val liked: Boolean = false,
        val replyCommentId: String = "none",
        val checked: Boolean = false,
        val createdAt: Date = Date()
)
