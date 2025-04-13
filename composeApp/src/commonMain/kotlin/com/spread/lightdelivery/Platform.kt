package com.spread.lightdelivery

interface Platform {
    val name: String
}

expect fun getPlatform(): Platform