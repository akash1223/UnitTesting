package com.lysn.clinician.ui.welcome


import androidx.test.core.app.ActivityScenario
import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.action.ViewActions.click
import androidx.test.espresso.assertion.ViewAssertions.matches
import androidx.test.espresso.matcher.ViewMatchers
import androidx.test.espresso.matcher.ViewMatchers.withId
import androidx.test.ext.junit.rules.ActivityScenarioRule
import com.lysn.clinician.R
import com.lysn.clinician.ui.AuthenticateActivity
import org.junit.Rule
import org.junit.Test

class WelcomeFragmentTest {


    @get:Rule
    val activityRule = ActivityScenarioRule(AuthenticateActivity::class.java)

    @Test
    fun test_welcome_fragment_in_view() {

        onView(withId(R.id.nav_startup_fragment))
            .check(matches(ViewMatchers.isDisplayed()))
    }

    @Test
    fun test_is_Visible_login_button() {
        onView(withId(R.id.btn_log_in))
            .check(matches(ViewMatchers.isDisplayed()))
    }

    @Test
    fun test_is_Visible_register_button() {
        onView(withId(R.id.btn_register))
            .check(matches(ViewMatchers.isDisplayed()))
    }

    @Test
    fun test_is_clicked_login_button() {
        ActivityScenario.launch(AuthenticateActivity::class.java)
        onView(withId(R.id.btn_log_in))
            .perform(click())
    }

}