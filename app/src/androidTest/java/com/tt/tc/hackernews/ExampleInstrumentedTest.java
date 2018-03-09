package com.tt.tc.hackernews;

import android.content.Context;
import android.support.test.InstrumentationRegistry;
import android.support.test.espresso.Espresso;
import android.support.test.espresso.IdlingPolicies;
import android.support.test.espresso.contrib.RecyclerViewActions;
import android.support.test.filters.LargeTest;
import android.support.test.rule.ActivityTestRule;
import android.support.test.runner.AndroidJUnit4;
import android.support.v7.widget.RecyclerView;

import com.tt.tc.hackernews.adapter.CommentsAdapter;
import com.tt.tc.hackernews.model.Comment;

import org.junit.After;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.util.concurrent.TimeUnit;

import static android.support.test.espresso.action.ViewActions.click;
import static android.support.test.espresso.matcher.ViewMatchers.withId;
import static org.junit.Assert.assertEquals;

/**
 * Instrumentation test, which will execute on an Android device.
 *
 * @see <a href="http://d.android.com/tools/testing">Testing documentation</a>
 */
@RunWith(AndroidJUnit4.class)
@LargeTest
public class ExampleInstrumentedTest{
    @Rule
    public ActivityTestRule<MainActivity> activityTestRule = new ActivityTestRule<>(MainActivity.class);
    private LoadingIdlingResource loadingIdlingStories = new LoadingIdlingResource();
    private LoadingIdlingResource loadingIdlingComments = new LoadingIdlingResource();
    @Before
    public void yourSetUPFragment() {
        activityTestRule.getActivity()
                .getFragmentManager().beginTransaction();
        IdlingPolicies.setMasterPolicyTimeout(1, TimeUnit.MINUTES);
        IdlingPolicies.setIdlingResourceTimeout(3, TimeUnit.MINUTES);
    }

    @After
    public void unregisterIdlingResource() {

    }

    @Test
    public void useAppContext() throws Exception {
        // Context of the app under test.
        Context appContext = InstrumentationRegistry.getTargetContext();

        assertEquals("com.tt.tc.hackernews", appContext.getPackageName());
    }

    @Test
    public void testStoriesList(){
        RecyclerView recyclerView = (RecyclerView) activityTestRule.getActivity().findViewById(R.id.rvList);
        loadingIdlingStories.setRecyclerView(recyclerView, 15);
        Espresso.registerIdlingResources(loadingIdlingStories);
        Espresso.onView(withId(R.id.rvList))
                .perform(RecyclerViewActions.scrollToPosition(10));

        Espresso.onView(withId(R.id.rvList))
                .perform(RecyclerViewActions.scrollToPosition(2));
        Espresso.unregisterIdlingResources(loadingIdlingStories);
    }

    @Test
    public void testCommentsList(){
        RecyclerView recyclerView = (RecyclerView) activityTestRule.getActivity().findViewById(R.id.rvList);
        loadingIdlingStories.setRecyclerView(recyclerView, 10);
        Espresso.registerIdlingResources(loadingIdlingStories);
        Espresso.onView(withId(R.id.rvList))
                .perform(RecyclerViewActions.actionOnItemAtPosition(1, click()));
        Espresso.unregisterIdlingResources(loadingIdlingStories);

        recyclerView = (RecyclerView) activityTestRule.getActivity().findViewById(R.id.rvCommentList);
        loadingIdlingComments.setRecyclerView(recyclerView, 1);
        Espresso.registerIdlingResources(loadingIdlingComments);

        CommentsAdapter commentsAdapter = (CommentsAdapter) recyclerView.getAdapter();
        for (int i = 0; i < commentsAdapter.getItemCount(); i++){
            Comment comment = commentsAdapter.getItems(i);
            if (comment.getChilds() != null && comment.getChilds().size() > 0){
                Espresso.onView(withId(R.id.rvCommentList))
                        .perform(RecyclerViewActions.actionOnItemAtPosition(0, click()));
                break;
            }
        }

        Espresso.unregisterIdlingResources(loadingIdlingComments);
    }

}
