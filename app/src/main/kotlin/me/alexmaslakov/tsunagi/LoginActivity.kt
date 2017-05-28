package me.alexmaslakov.tsunagi

import android.app.ProgressDialog
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.support.design.widget.Snackbar
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.Toolbar
import android.view.KeyEvent
import android.view.View
import android.view.inputmethod.EditorInfo
import android.view.inputmethod.InputMethodManager
import android.widget.EditText
import android.widget.RelativeLayout
import android.widget.TextView
import butterknife.ButterKnife
import com.afollestad.materialdialogs.MaterialDialog

import me.alexmaslakov.tsunagi.tasks.Login
import me.alexmaslakov.tsunagi.tasks.LoginListener
import me.alexmaslakov.tsunagi.db.User

class LoginActivity : AppCompatActivity(), LoginListener {

    @Bind(R.id.loginUserName)
    internal var usernameField: EditText? = null

    @Bind(R.id.loginPassword)
    internal var passwordField: EditText? = null

    @Bind(R.id.login_button_container)
    internal var loginButtonContainer: RelativeLayout? = null

    @Bind(R.id.login_tool_bar)
    internal var toolbar: Toolbar? = null

    private var enteredUsername = ""
    private var enteredPassword = ""
    private var progressDialog: ProgressDialog? = null
    private var currentUser: User? = null
    private var settings: SettingsHelper? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)
        ButterKnife.bind(this)
        settings = SettingsHelper(applicationContext)
        currentUser = User.getCurrent(this)

        showUpdateDialog()
        setupToolbar()
        setupEditTexts()

        setupDatabase()

        if (currentUser == null) {
            // user not logged in
            loginButtonContainer!!.setOnClickListener { view ->
                loginButtonContainer!!.isEnabled = false
                enteredUsername = usernameField!!.text.toString().trim { it <= ' ' }
                enteredPassword = passwordField!!.text.toString()

                if (!enteredUsername.contains("@" + getString(R.string.webmail_domain))) {
                    enteredUsername = enteredUsername + "@" + getString(R.string.webmail_domain)
                }

                if (User.exists(enteredUsername, enteredPassword)) {
                    Snackbar.make(view, getString(R.string.snackbar_login_user_exist), Snackbar.LENGTH_LONG).show()
                    Handler().postDelayed({
                        User.setCurrent(User.getSingleByName(enteredUsername), applicationContext)
                        startActivity(Intent(this@LoginActivity, MainActivity::class.java))
                        finish()
                    }, 1000)
                } else {
                    val user = User(enteredUsername, enteredPassword)
                    Login(user, applicationContext, this@LoginActivity).execute()
                }
            }
        } else {
            // user already logged in and has an account enteredUsername
            startActivity(Intent(this, MainActivity::class.java))
            finish()
        }
    }

    private fun setupDatabase() {
        if (!settings!!.getBoolean(SettingsHelper.KEY_DATABASE_CREATED)) {
            startActivity(Intent(this@LoginActivity, SplashActivity::class.java))
        }
    }

    private fun setupToolbar() {
        toolbar!!.title = getString(R.string.appName)
        toolbar!!.setTitleTextColor(resources.getColor(R.color.toolbarText))
        setSupportActionBar(toolbar)
    }

    private fun setupEditTexts() {
        usernameField!!.setOnEditorActionListener(TextView.OnEditorActionListener { v, actionId, event ->
            if (actionId == EditorInfo.IME_ACTION_NEXT) {
                usernameField!!.clearFocus()
                passwordField!!.requestFocus()
                return@OnEditorActionListener true
            }
            false
        })

        passwordField!!.setOnKeyListener(View.OnKeyListener { v, keyCode, event ->
            if (event.action == KeyEvent.ACTION_DOWN && keyCode == KeyEvent.KEYCODE_ENTER) {
                loginButtonContainer!!.performClick()
                return@OnKeyListener true
            }
            false
        })

        usernameField!!.requestFocus()
    }

    override fun onBackPressed() {
        if (User.getCurrent(this) == null) {
            if (!User.getAll().isEmpty())
                User.setCurrent(User.getAll().get(0), applicationContext)
        } else {
            finish()
        }

        super.onBackPressed()
    }

    fun onPreLogin() {
        progressDialog = ProgressDialog.show(this@LoginActivity, "", getString(R.string.dialog_msg_logging_in), true)
        progressDialog!!.setCancelable(false)
        progressDialog!!.show()
        val mgr = getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
        mgr.hideSoftInputFromWindow(loginButtonContainer!!.windowToken, 0)
        Snackbar.make(loginButtonContainer!!, getString(R.string.snackbar_login_attempting), Snackbar.LENGTH_LONG).show()
    }

    fun onPostLogin(loginSuccess: Boolean, timeTaken: String, user: User) {
        var user = user
        progressDialog!!.dismiss()
        loginButtonContainer!!.isEnabled = true
        if (!loginSuccess) {
            Snackbar.make(loginButtonContainer!!, getString(R.string.snackbar_login_failed), Snackbar.LENGTH_LONG).show()
            usernameField!!.setText(enteredUsername)
            passwordField!!.setText("")
        } else {
            Snackbar.make(loginButtonContainer!!, getString(R.string.snackbar_login_successful), Snackbar.LENGTH_LONG).show()
            user = User.create(user)
            User.setCurrent(user, applicationContext)
            startActivity(Intent(this, MainActivity::class.java))
            finish()
            usernameField!!.setText("")
            passwordField!!.setText("")
        }
    }

    private fun showUpdateDialog() {
        if (!settings!!.getBoolean(SettingsHelper.KEY_ALERT_SHOWN)) {
            MaterialDialog.Builder(this@LoginActivity)
                    .title(getString(R.string.appName))
                    .content(getString(R.string.releaseNotes2))
                    .positiveText("Lets Go!")
                    .show()

            settings!!.save(SettingsHelper.KEY_ALERT_SHOWN, true)
        }
    }
}