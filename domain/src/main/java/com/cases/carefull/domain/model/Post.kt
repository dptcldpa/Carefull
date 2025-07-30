package com.cases.carefull.domain.model

data class Post(
    val postNumber: Int, // 글번호
    val title: String, // 글 제목
    val content: String, // 글 내용
    val createdAt: String, // 작성일 (예: "2024.05.21")
    val commentCount: Int, // 댓글 수
    val likeCount: Int, // 좋아요 수
)