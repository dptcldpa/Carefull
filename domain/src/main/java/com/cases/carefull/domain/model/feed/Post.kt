package com.cases.carefull.domain.model.feed

import java.util.Date

data class Post(
    val id: String,
    val title: String,
    val content: String,
    val category: SocialCategory,
    val imageUrl: String?,
    val userId: String,
    val createdAt: Date,
    val likeCount: Int,
    val commentCount: Int
)