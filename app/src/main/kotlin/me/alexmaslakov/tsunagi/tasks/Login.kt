package me.alexmaslakov.tsunagi.tasks

import android.content.Context
import android.os.AsyncTask

import me.alexmaslakov.tsunagi.db.User

class Login(private val user: User, private val context: Context, private val loginListener: LoginListener) :
        AsyncTask<Void, Void, Void>() {

    private var initTime: Long = 0
    private var finalTime: Long = 0
    private var loggedIn = false

    override fun onPreExecute() {
        super.onPreExecute()
        loginListener.onPreLogin()
        initTime = System.currentTimeMillis()
    }

    override fun doInBackground(vararg voids: Void): Void? {
        val restAPI = RestAPI(user, context)
        loggedIn = restAPI.logIn()
        return null
    }

    override fun onPostExecute(aVoid: Void) {
        super.onPostExecute(aVoid)
        finalTime = System.currentTimeMillis()
        loginListener.onPostLogin(loggedIn, (finalTime - initTime).toString(), user)
    }
}
