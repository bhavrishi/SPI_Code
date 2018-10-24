package com.myonic.rishibhv.tracker;


import android.content.Context;
import android.os.Bundle;
import android.support.test.InstrumentationRegistry;
import android.support.test.rule.ActivityTestRule;
import android.support.test.runner.AndroidJUnit4;
import android.content.Intent;
import android.util.Log;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.Rule;

/**
 * The type Map places renderer test.
 */
@RunWith(AndroidJUnit4.class)
public class MapPlacesRendererTest {

    @Rule
    public ActivityTestRule<MapsActivity> mActivityTestRule = new ActivityTestRule<MapsActivity>(MapsActivity.class, true) {
        @Override
        protected Intent getActivityIntent() {
            Log.d(MapsActivity.class.getCanonicalName(), "getActivityIntent() called");
            Context targetContext = InstrumentationRegistry.getInstrumentation().getTargetContext();
            Intent intent = new Intent(targetContext, MapsActivity.class);
            intent.putExtra("testingcheck", true);
            return intent;
        }
    };


    @Before
    public void initThingsHere() {
        //do stuff like database or preference or image copying here
    }

    @Test
    public void checkBlankEmailError() {
        //to check view on screen
        Bundle bundle = mActivityTestRule.getActivity().getIntent().getExtras();
        //  assertThat(bundle.getBoolean("testingcheck"), is(true));
        System.out.println("testingcheck:" + bundle.getBoolean("testingcheck"));
    }
}