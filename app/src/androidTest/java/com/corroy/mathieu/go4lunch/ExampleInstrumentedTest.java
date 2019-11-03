package com.corroy.mathieu.go4lunch;

import androidx.recyclerview.widget.RecyclerView;
import androidx.test.espresso.Espresso;
import androidx.test.espresso.ViewAction;
import androidx.test.espresso.action.ViewActions;
import androidx.test.espresso.contrib.DrawerActions;
import androidx.test.espresso.contrib.RecyclerViewActions;
import androidx.test.espresso.intent.rule.IntentsTestRule;
import androidx.test.ext.junit.runners.AndroidJUnit4;

import com.corroy.mathieu.go4lunch.Controller.MainScreenActivity;
import com.corroy.mathieu.go4lunch.Models.Helper.UserHelper;

import org.hamcrest.CoreMatchers;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import javax.inject.Inject;

import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.Espresso.openActionBarOverflowOrOptionsMenu;
import static androidx.test.espresso.action.ViewActions.click;
import static androidx.test.espresso.action.ViewActions.pressBack;
import static androidx.test.espresso.assertion.ViewAssertions.matches;
import static androidx.test.espresso.contrib.DrawerMatchers.isOpen;
import static androidx.test.espresso.contrib.NavigationViewActions.navigateTo;
import static androidx.test.espresso.matcher.ViewMatchers.isCompletelyDisplayed;
import static androidx.test.espresso.matcher.ViewMatchers.isDisplayed;
import static androidx.test.espresso.matcher.ViewMatchers.isRoot;
import static androidx.test.espresso.matcher.ViewMatchers.withHint;
import static androidx.test.espresso.matcher.ViewMatchers.withId;
import static androidx.test.espresso.matcher.ViewMatchers.withResourceName;
import static androidx.test.espresso.matcher.ViewMatchers.withTagValue;
import static androidx.test.espresso.matcher.ViewMatchers.withText;
import static androidx.test.platform.app.InstrumentationRegistry.getInstrumentation;

/**
 * Instrumented test, which will execute on an Android device.
 *
 * @see <a href="http://d.android.com/tools/testing">Testing documentation</a>
 */
@RunWith(AndroidJUnit4.class)
public class ExampleInstrumentedTest {

    @Rule
    public IntentsTestRule<MainScreenActivity> mActivityRule = new IntentsTestRule<>(
            MainScreenActivity.class);

    @Test
    public void testBottomNavigationViewItemClick() {
        onView(withId(R.id.map_view)).perform(click())
                .check(matches(isCompletelyDisplayed()));
        onView(withId(R.id.list_view)).perform(click())
                .check(matches(isCompletelyDisplayed()));
        onView(withId(R.id.workmates)).perform(click())
                .check(matches(isCompletelyDisplayed()));
    }

    @Test
    public void testNavigationMenuItemClick() {
        onView(withId(R.id.first_screen_drawerlayout))
                .perform(DrawerActions.open());
        onView(withId(R.id.first_screen_drawerlayout))
                .check(matches(isOpen()));
        onView(withId(R.id.first_screen_navigation_view))
                .perform(navigateTo(R.id.settings));

        onView(isRoot()).perform(pressBack());

        onView(withId(R.id.first_screen_drawerlayout))
                .perform(DrawerActions.open());
        onView(withId(R.id.first_screen_drawerlayout))
                .check(matches(isOpen()));
        onView(withId(R.id.first_screen_navigation_view))
                .perform(navigateTo(R.id.logout));
    }

    @Test
    public void testRecyclerViewScroll() throws InterruptedException {
        onView(withId(R.id.list_view)).perform(click());
        Thread.sleep(1000);
        onView(withId(R.id.listview_recyclerview)).perform(RecyclerViewActions.scrollToPosition(10));
    }

    @Test
    public void testSearchButtonBar(){
        onView(withId(R.drawable.searchicon)).perform(click());
    }
}
