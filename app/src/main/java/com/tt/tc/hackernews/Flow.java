package com.tt.tc.hackernews;

import android.support.v4.app.Fragment;

/**
 * Created by smu (Chau) on 7/3/18.
 */

public interface Flow  {
    void goTo(boolean back, Fragment fragment, String tag);
}
