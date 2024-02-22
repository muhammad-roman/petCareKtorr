package com.example.plugins

import com.example.route.authRouting
import com.example.route.postRoute
import io.ktor.server.application.*
import io.ktor.server.routing.*
import com.example.dao.post.dao

fun Application.configureRouting() {
    routing {
        authRouting()
        postRoute(dao)
    }
}
