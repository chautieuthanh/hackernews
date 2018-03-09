package com.tt.tc.hackernews;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v7.app.AppCompatActivity;

import com.tt.tc.hackernews.ui.StoriesFragment;

public class MainActivity extends AppCompatActivity implements Flow{

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        FragmentManager fragmentManager = getSupportFragmentManager();
        StoriesFragment storiesFragment = (StoriesFragment) fragmentManager.findFragmentByTag(StoriesFragment.TAG);
        if(storiesFragment == null){
            storiesFragment = StoriesFragment.newInstance();
            fragmentManager.beginTransaction()
                    .add(R.id.container, storiesFragment, StoriesFragment.TAG)
                    .commit();
        }
    }

    @Override
    public void goTo(boolean back, Fragment fragment, String tag) {
        if (!isFinishing()) {
            FragmentManager fragmentManager = getSupportFragmentManager();
            if(fragmentManager.findFragmentByTag(tag) == null){
                fragmentManager.beginTransaction()
                        .add(R.id.container, fragment, tag)
                        .addToBackStack(null)
                        .commitAllowingStateLoss();
            }
        }
    }
}
