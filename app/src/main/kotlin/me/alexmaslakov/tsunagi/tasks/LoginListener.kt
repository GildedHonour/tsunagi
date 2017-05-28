package me.alexmaslakov.tsunagi.tasks

import me.alexmaslakov.tsunagi.db.User

interface LoginListener {
    fun onPreLogin()
    fun onPostLogin(loginSuccess: Boolean, timeTaken: String, user: User)
}