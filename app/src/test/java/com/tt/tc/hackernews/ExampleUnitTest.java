package com.tt.tc.hackernews;

import com.tt.tc.hackernews.service.RestClient;

import org.junit.Test;

import java.util.List;

import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.annotations.NonNull;
import io.reactivex.functions.Consumer;
import io.reactivex.schedulers.Schedulers;

import static org.junit.Assert.*;

/**
 * Example local unit test, which will execute on the development machine (host).
 *
 * @see <a href="http://d.android.com/tools/testing">Testing documentation</a>
 */
public class ExampleUnitTest {
    @Test
    public void loadStories() throws Exception {
        RestClient.getInstance()
                .getTopItemLoader()
                .loadItems()
                .subscribeOn(Schedulers.io())
                .observeOn(Schedulers.newThread())
                .subscribe(new Consumer<List<Long>>() {
                    @Override
                    public void accept(@NonNull List<Long> longs) throws Exception {
                        assertTrue(longs.size() > 0);
                    }
                });
        assertEquals(4, 2 + 2);
    }
}