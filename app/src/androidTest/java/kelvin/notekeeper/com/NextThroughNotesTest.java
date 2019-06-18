package kelvin.notekeeper.com;

import static org.junit.Assert.*;

import android.support.test.espresso.contrib.DrawerActions;
import android.support.test.espresso.contrib.NavigationViewActions;
import android.support.test.espresso.contrib.RecyclerViewActions;
import android.support.test.rule.ActivityTestRule;
import android.support.test.runner.AndroidJUnit4;

import org.junit.BeforeClass;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;


import java.util.List;

import static android.support.test.espresso.action.ViewActions.click;
import static android.support.test.espresso.action.ViewActions.closeSoftKeyboard;
import static android.support.test.espresso.action.ViewActions.pressBack;
import static android.support.test.espresso.action.ViewActions.typeText;
import static android.support.test.espresso.assertion.ViewAssertions.*;
import static org.junit.Assert.*;
import static android.support.test.espresso.Espresso.*;
import static android.support.test.espresso.matcher.ViewMatchers.*;
import static  android.support.test.espresso.ViewAction.*;
import static org.hamcrest.Matchers.*;

public class NextThroughNotesTest {
    @Rule
    public ActivityTestRule<Main> mActivityTestRule =
            new ActivityTestRule(Main.class);

    @Test
    public void NextThroughTest ()
    {
        onView(withId(R.id.drawer_layout)).perform(DrawerActions.open());
        onView(withId(R.id.nav_view)).perform(NavigationViewActions.navigateTo(R.id.nav_notes));

        onView(withId(R.id.list_items)).perform(RecyclerViewActions.actionOnItemAtPosition(0,click()));

        List<NoteInfo> notes = DataManager.getInstance().getNotes();

       for(int index = 0; index<notes.size(); index++) {

           NoteInfo note = notes.get(index);

           onView(withId(R.id.spinner_courses)).check(
                   matches(withSpinnerText(note.getCourse().getTitle()))
           );
           onView(withId(R.id.text_note_title)).check(
                   matches(withText(note.getTitle()))
           );
           onView(withId(R.id.text_note_text)).check(
                   matches(withText(note.getText()))
           );

           onView(withId(R.id.action_next)).perform(click());
       }



    }


}