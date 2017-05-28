package me.alexmaslakov.tsunagi.db

import com.orm.SugarRecord
import com.orm.query.Condition
import com.orm.query.Select

import java.io.Serializable
import android.preference.PreferenceManager
import android.content.SharedPreferences
import android.content.Context

class User(val name: String, val password: String): Serializable, SugarRecord() {
    override fun equals(obj: Any?): Boolean {
        if (this === obj) {
            return true
        }

        if (obj == null || javaClass != obj.javaClass) {
            return false
        }

        val user = obj as User?
        if (name != user!!.name) {
            return false
        }

        return password == user.password
    }

    override fun hashCode(): Int {
        return 31 * name!!.hashCode() + password!!.hashCode()
    }

    companion object {

        private val currentName = "current"

        fun getSingleByName(name: String): User {
            return Select.from(User::class.java)
                    .where(Condition.prop("name").eq(name))
                    .first()
        }

        fun exists(username: String, pwd: String): Boolean {
            val user = Select.from(User::class.java)
                    .where(Condition.prop("name").eq(username))
                    .where(Condition.prop("password").eq(pwd))
                    .first()
            return user != null
        }

        fun create(newUser: User): User? {
            if (exists(newUser.name, newUser.password)) {
                return null
            }

            newUser.save()
            return newUser
        }

        fun delete(user: User) {
            user.delete()
        }

        fun getAll(): List<User> {
            return User.listAll(User::class.java)
        }

        fun getCount(): Int {
            return User.listAll(User::class.java).size()
        }

        fun getShortName(user: User?): String {
            var currentUserName = "NaN"
            if (user != null) {
                currentUserName = user.name
            }

            if (currentUserName.startsWith("20")) {
                currentUserName = currentUserName.substring(0, currentUserName.indexOf("@"))
                if (currentUserName.length > 3)
                    currentUserName = currentUserName.substring(currentUserName.length - 3)
            } else {
                currentUserName = currentUserName.substring(0, 3)
            }

            return currentUserName
        }

        fun getCurrent(context: Context): User? {
            val preferences = PreferenceManager.getDefaultSharedPreferences(context)
            val name = preferences.getString(currentName, null) ?: return null
            return getSingleByName(name)
        }

        fun setCurrent(currentUser: User?, context: Context) {
            val pref = PreferenceManager.getDefaultSharedPreferences(context)
            val editor = pref.edit()
            if (currentUser == null) {
                editor.putString(currentName, null)
            } else {
                editor.putString(currentName, currentUser.name)
            }

            editor.apply()
            editor.commit()
        }
    }
}