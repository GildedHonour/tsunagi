package me.alexmaslakov.tsunagi

import me.alexmaslakov.tsunagi.R
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.Toolbar
import android.view.View
import android.widget.Switch

import me.alexmaslakov.tsunagi.SettingsHelper

class SettingsActivity : AppCompatActivity() {

    internal var switch_mobile: Switch? = null
    internal var toolbar: Toolbar? = null

    private var toggleMobileData = true
    private var settings: SettingsHelper? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_settings)
        settings = SettingsHelper(applicationContext)
        toggleMobileData = settings!!.getBoolean(SettingsHelper.KEY_MOBILE_DATA)
    }

    override fun onBackPressed() {
        super.onBackPressed()
        finish()
    }
}