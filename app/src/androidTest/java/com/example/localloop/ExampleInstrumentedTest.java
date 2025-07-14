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
import com.example.localloop.model.JoinRequest;
import com.example.localloop.model.Organizer;
import com.example.localloop.model.Participant;
import com.example.localloop.resources.datetime.Date;
import com.example.localloop.resources.datetime.Time;
import com.example.localloop.resources.exception.InvalidEventNameException;
import com.example.localloop.resources.exception.NoSuchEventException;

import java.util.ArrayList;
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
                // Search for Event by Name - Participant
                /*
                DatabaseConnection dbKB = new DatabaseConnection("kobe", "123");
                Participant kobe = (Participant)dbKB.getUser();
                Event event = kobe.eventSearch(dbKB,"New");
                Log.d("eventSearch",event.toString());
                */
                // Search for Event by Category - Participant
                /*
                DatabaseConnection dbKB = new DatabaseConnection("kobe", "123");
                Participant kobe = (Participant)dbKB.getUser();
                EventCategory category = kobe.getAllEventCategories(dbKB).getFirst();
                ArrayList<Event> events = kobe.eventSearch(dbKB,category);
                Log.d("eventSearch",events.toString());
                */
                // Creating a request - Participant
                /*
                DatabaseConnection dbKB = new DatabaseConnection("kobe", "123");
                Participant kobe = (Participant)dbKB.getUser();
                ArrayList<Event> eventstojoin = kobe.getAllEvents(dbKB);
                kobe.requestJoinEvent(dbKB,eventstojoin.getFirst());
                */
                // Fetching requests - Participant
                /*
                ArrayList<Event> requests = kobe.getJoinRequests(dbKB);
                Log.d("requests",requests.toString());
                */
                // Fetching reservations - Participant
                /*
                DatabaseConnection dbKB = new DatabaseConnection("kobe", "123");
                Participant kobe = (Participant)dbKB.getUser();
                ArrayList<Event> reservations = kobe.getReservations(dbKB);
                Log.d("Kobe's reservations",reservations.toString());
                */
                // Fetching requests - Organizer
                /*
                DatabaseConnection db = new DatabaseConnection("uOttawa", "password1");
                Organizer me = (Organizer)db.getUser();
                ArrayList<Event> events = me.getUserEvents(db);
                ArrayList<Participant> requests = me.getJoinRequests(db,events.getFirst());
                Log.d("requests",requests.toString());
                */
                // Accepting request - Organizer
                /*
                DatabaseConnection db = new DatabaseConnection("uOttawa", "password1");
                Organizer me = (Organizer)db.getUser();
                ArrayList<Event> events = me.getUserEvents(db);
                ArrayList<Participant> requests = me.getJoinRequests(db,events.getFirst());
                me.acceptJoinRequest(db,requests.getFirst(),events.getFirst());
                */
                // Fetching reservations - Organizer
                /*
                DatabaseConnection db = new DatabaseConnection("uOttawa", "password1");
                Organizer me = (Organizer)db.getUser();
                ArrayList<Event> events = me.getUserEvents(db);
                ArrayList<Participant> reservations = me.getReservations(db,events.getFirst());
                Log.d("New's reservations",reservations.toString());
                */
                Log.d("TEST", "Request created");
            } catch (Exception e) {
                Log.e("TEST", "Failed to create request", e);
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