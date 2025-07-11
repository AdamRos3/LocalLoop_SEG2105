package com.example.localloop;

import static org.junit.Assert.assertEquals;

import android.content.Context;
import android.util.Log;

import androidx.test.platform.app.InstrumentationRegistry;
import androidx.test.ext.junit.runners.AndroidJUnit4;

import org.junit.Test;
import org.junit.runner.RunWith;

import com.example.localloop.model.DatabaseConnection;
import com.example.localloop.model.Event;
import com.example.localloop.model.EventCategory;
import com.example.localloop.model.Organizer;
import com.example.localloop.resources.datetime.Date;
import com.example.localloop.resources.datetime.Time;
import com.example.localloop.resources.exception.InvalidEventNameException;
import com.example.localloop.resources.exception.NoSuchEventException;

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
    public void testParticipantsLoad() throws InvalidEventNameException, NoSuchEventException, InterruptedException {
        Context appContext = InstrumentationRegistry.getInstrumentation().getTargetContext();
        CountDownLatch testLatch = new CountDownLatch(1);

        new Thread(() -> {
            try {
                DatabaseConnection db = new DatabaseConnection("uOttawa", "password1");
                Time time = new Time(12,30);
                Date date = new Date(2026,5,12);
                EventCategory category = new EventCategory("Musics","All Music Events","-OUVRRtcizfCAM0lPhoZ");
                Organizer organizer = (Organizer) db.getUser();
                Event e = new Event("Drake Concert","at uOttawa campus",category.getCategoryID(),0,date, time, organizer.getUserID(),null);
                organizer.createEvent(db, e);
                Log.d("TEST", "Event created");
            } catch (Exception e) {
                Log.e("TEST", "Failed to create event", e);
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