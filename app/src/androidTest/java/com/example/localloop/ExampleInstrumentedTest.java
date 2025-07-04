package com.example.localloop;

import android.content.Context;
import android.util.Log;

import androidx.test.platform.app.InstrumentationRegistry;
import androidx.test.ext.junit.runners.AndroidJUnit4;

import org.junit.Test;
import org.junit.runner.RunWith;

import com.example.localloop.backend.DatabaseConnection;
import com.example.localloop.backend.DatabaseConnectionException;
import com.example.localloop.backend.Organizer;


/**
 * Instrumented test, which will execute on an Android device.
 *
 * @see <a href="http://d.android.com/tools/testing">Testing documentation</a>
 */
@RunWith(AndroidJUnit4.class)
public class ExampleInstrumentedTest {
    @Test
    public void testParticipantsLoad() {
        Context appContext = InstrumentationRegistry.getInstrumentation().getTargetContext();
        try {
            DatabaseConnection dbConnect = new DatabaseConnection("bo", "123");
            Log.d("User",dbConnect.getUser().toString());
            Log.d("AccountType",dbConnect.getUser() instanceof Organizer ? "Organizer" : "Participant");
        } catch (DatabaseConnectionException e) {
            Log.e("DatabaseQueryException", e.toString());
        }
    }
}