package org.researchstack.backboneapp

import android.view.View
import android.view.ViewGroup
import android.widget.DatePicker
import android.widget.LinearLayout
import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.action.ViewActions.click
import androidx.test.espresso.action.ViewActions.closeSoftKeyboard
import androidx.test.espresso.action.ViewActions.replaceText
import androidx.test.espresso.action.ViewActions.scrollTo
import androidx.test.espresso.action.ViewActions.swipeDown
import androidx.test.espresso.action.ViewActions.swipeRight
import androidx.test.espresso.assertion.ViewAssertions.matches
import androidx.test.espresso.contrib.PickerActions.setDate
import androidx.test.espresso.matcher.ViewMatchers.isAssignableFrom
import androidx.test.espresso.matcher.ViewMatchers.isDisplayed
import androidx.test.espresso.matcher.ViewMatchers.withClassName
import androidx.test.espresso.matcher.ViewMatchers.withId
import androidx.test.espresso.matcher.ViewMatchers.withSubstring
import androidx.test.espresso.matcher.ViewMatchers.withText
import androidx.test.filters.LargeTest
import androidx.test.rule.ActivityTestRule
import org.hamcrest.*
import org.hamcrest.Matchers.*
import org.hamcrest.core.*
import org.junit.*
import org.researchstack.backboneapp.R.id

@LargeTest
class MainActivityTest {

    @Rule
    @JvmField
    var mActivityTestRule = ActivityTestRule(MainActivity::class.java)

    @Test
    fun mainActivityTest() {

        // reset state, TODO: better way to setup/cleanup
        onView(withId(R.id.menu_clear)).perform(click())

        // Launch consent task
        onView(withId(R.id.consent_button)).perform(click())

        testConsentTask()

        // Launch survey task
        onView(withId(R.id.survey_button)).perform(click())

        testSurvey()
    }

    private fun testSurvey() {
        val appCompatTextView5 = onView(
                allOf(withId(id.bar_submit_postitive), withText("next"),
                        childAtPosition(
                                allOf(withId(id.rsb_submit_bar),
                                        childAtPosition(
                                                withId(id.rsb_current_step),
                                                1)),
                                2),
                        isDisplayed()))
        appCompatTextView5.perform(click())

        val appCompatEditText2 = onView(
                allOf(withId(id.value),
                        childAtPosition(
                                allOf(withId(id.rsb_survey_step_body),
                                        childAtPosition(
                                                withId(id.rsb_survey_content_container),
                                                2)),
                                1),
                        isDisplayed()))
        appCompatEditText2.perform(replaceText("test"), closeSoftKeyboard())

        val appCompatTextView6 = onView(
                allOf(withId(id.bar_submit_postitive), withText("next"),
                        childAtPosition(
                                allOf(withId(id.rsb_submit_bar),
                                        childAtPosition(
                                                withId(id.rsb_current_step),
                                                1)),
                                2),
                        isDisplayed()))
        appCompatTextView6.perform(click())


        onView(withId(id.value))
                .perform(click())

        onView(isAssignableFrom(DatePicker::class.java))
                .perform(setDate(2017, 6, 30));

        val appCompatButton4 = onView(
                allOf(withId(android.R.id.button1), withText("OK"),
                        childAtPosition(
                                childAtPosition(
                                        withClassName(`is`("android.widget.ScrollView")),
                                        0),
                                3)))
        appCompatButton4.perform(scrollTo(), click())

        val appCompatTextView7 = onView(
                allOf(withId(id.bar_submit_postitive), withText("next"),
                        childAtPosition(
                                allOf(withId(id.rsb_submit_bar),
                                        childAtPosition(
                                                withId(id.rsb_current_step),
                                                1)),
                                2),
                        isDisplayed()))
        appCompatTextView7.perform(click())

        val appCompatRadioButton = onView(
                allOf(withText("Yes"),
                        childAtPosition(
                                allOf(withId(id.rsb_survey_step_body),
                                        childAtPosition(
                                                withId(id.rsb_survey_content_container),
                                                2)),
                                0),
                        isDisplayed()))
        appCompatRadioButton.perform(click())

        val appCompatTextView8 = onView(
                allOf(withId(id.bar_submit_postitive), withText("next"),
                        childAtPosition(
                                allOf(withId(id.rsb_submit_bar),
                                        childAtPosition(
                                                withId(id.rsb_current_step),
                                                1)),
                                2),
                        isDisplayed()))
        appCompatTextView8.perform(click())

        val appCompatCheckBox = onView(
                allOf(withText("One"),
                        childAtPosition(
                                allOf(withId(id.rsb_survey_step_body),
                                        childAtPosition(
                                                withId(id.rsb_survey_content_container),
                                                2)),
                                1),
                        isDisplayed()))
        appCompatCheckBox.perform(click())

        val appCompatCheckBox2 = onView(
                allOf(withText("Two"),
                        childAtPosition(
                                allOf(withId(id.rsb_survey_step_body),
                                        childAtPosition(
                                                withId(id.rsb_survey_content_container),
                                                2)),
                                2),
                        isDisplayed()))
        appCompatCheckBox2.perform(click())

        val appCompatTextView9 = onView(
                allOf(withId(id.bar_submit_postitive), withText("next"),
                        childAtPosition(
                                allOf(withId(id.rsb_submit_bar),
                                        childAtPosition(
                                                withId(id.rsb_current_step),
                                                1)),
                                2),
                        isDisplayed()))
        appCompatTextView9.perform(click())

        val textView = onView(
                allOf(withId(id.survey_results),
                        childAtPosition(
                                childAtPosition(
                                        IsInstanceOf.instanceOf(LinearLayout::class.java),
                                        3),
                                1),
                        isDisplayed()))
        textView.check(
                matches(allOf(
                        withSubstring("multi_step: [1.0, 2.0]"),
                        withSubstring("nutrition: true"),
                        withSubstring("date: 1.49"),
                        withSubstring("name: test")))

        )
    }

    private fun testConsentTask() {
        // ConsentVisualStep

        onView(withId(id.title))
                .check(matches(allOf(
                        withText("The title of the section goes here ..."),
                        isDisplayed())))

        onView(withId(id.summary))
                .check(matches(allOf(
                        withText("The summary about the section goes here ..."),
                        isDisplayed())))

        onView(withId(id.bar_submit_negative))
                .check(matches(not(isDisplayed())))

        onView(withId(id.bar_submit_postitive))
                .check(matches(allOf(withText("next"), isDisplayed())))
                .perform(click())

        // ConsentDocumentStep

        onView(withId(id.bar_submit_negative))
                .check(matches(allOf(
                        isDisplayed(),
                        withText("DISAGREE")
                )))

        onView(withId(id.bar_submit_postitive))
                .check(matches(allOf(
                        withText("Agree"),
                        isDisplayed())))
                .perform(click())


        onView(withId(android.R.id.button2))
                .check(matches(allOf(
                        withText("CANCEL"),
                        isDisplayed()
                )))

        onView(withId(android.R.id.button1))
                .check(matches(allOf(
                        withText("Agree"),
                        isDisplayed())))
                .perform(scrollTo(), click())

        // FormStep (for signature)

        val appCompatEditText = onView(
                allOf(withId(id.value),
                        childAtPosition(
                                childAtPosition(
                                        withId(id.rsb_survey_step_body),
                                        0),
                                1),
                        isDisplayed()))
        appCompatEditText.perform(replaceText("test"), closeSoftKeyboard())

        val appCompatTextView3 = onView(
                allOf(withId(id.bar_submit_postitive), withText("next"),
                        childAtPosition(
                                allOf(withId(id.rsb_submit_bar),
                                        childAtPosition(
                                                withId(id.rsb_current_step),
                                                1)),
                                2),
                        isDisplayed()))
        appCompatTextView3.perform(click())

        val appCompatTextView4 = onView(
                allOf(withId(id.bar_submit_postitive), withText("next"),
                        childAtPosition(
                                allOf(withId(id.submit_bar),
                                        childAtPosition(
                                                withId(id.rsb_current_step),
                                                3)),
                                2),
                        isDisplayed()))

        // ConsentSignatureStep
        onView(withId(id.layout_consent_review_signature))
                .perform(swipeRight())
                .perform(swipeDown())

        appCompatTextView4.perform(click())
    }

    private fun childAtPosition(
            parentMatcher: Matcher<View>, position: Int): Matcher<View> {

        return object : TypeSafeMatcher<View>() {
            override fun describeTo(description: Description) {
                description.appendText("Child at position $position in parent ")
                parentMatcher.describeTo(description)
            }

            public override fun matchesSafely(view: View): Boolean {
                val parent = view.parent
                return parent is ViewGroup && parentMatcher.matches(parent)
                        && view == parent.getChildAt(position)
            }
        }
    }
}
