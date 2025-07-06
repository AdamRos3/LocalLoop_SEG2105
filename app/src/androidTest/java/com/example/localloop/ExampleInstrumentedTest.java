package com.example.localloop;

import static org.junit.Assert.assertEquals;

import android.content.Context;
import android.util.Log;

import androidx.test.platform.app.InstrumentationRegistry;
import androidx.test.ext.junit.runners.AndroidJUnit4;

import org.junit.Test;
import org.junit.runner.RunWith;

import com.example.localloop.backend.Admin;
import com.example.localloop.backend.DatabaseConnection;
import com.example.localloop.backend.EventCategory;
import com.example.localloop.resources.exception.InvalidEventCategoryNameException;
import com.example.localloop.resources.exception.NoSuchEventCategoryNameException;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;


/**
 * Instrumented test, which will execute on an Android device.
 *
 * @see <a href="http://d.android.com/tools/testing">Testing documentation</a>
 */
@RunWith(AndroidJUnit4.class)
public class ExampleInstrumentedTest {
    @Test
    public void testParticipantsLoad() throws InvalidEventCategoryNameException, NoSuchEventCategoryNameException, InterruptedException {
        Context appContext = InstrumentationRegistry.getInstrumentation().getTargetContext();
        CountDownLatch testLatch = new CountDownLatch(1);

        new Thread(() -> {
            try {
                DatabaseConnection db = new DatabaseConnection("admin", "XPI76SZUqyCjVxgnUjm0");
                EventCategory ec = new EventCategory("Soccer", "All Soccer Events", null);
                Admin admin = (Admin) db.getUser();
                admin.createEventCategory(db, ec);
                Log.d("TEST", "Category Deleted");
            } catch (Exception e) {
                Log.e("TEST", "Failed to delete event category", e);
            } finally {
                testLatch.countDown();  // Always release the latch!
            }
        }).start();

        // Wait up to 10 seconds for thread to complete
        if (!testLatch.await(10, TimeUnit.SECONDS)) {
            throw new AssertionError("Test timed out before Firebase operation completed");
        }
    }
}