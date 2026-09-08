package com.example.goindiacab

interface Platform {
    val name: String
}

expect fun getPlatform(): Platform
