package com.example.localloop;

import static org.junit.Assert.assertEquals;

import android.content.Context;
import android.util.Log;

import androidx.test.platform.app.InstrumentationRegistry;
import androidx.test.ext.junit.runners.AndroidJUnit4;

import org.junit.Test;
import org.junit.runner.RunWith;

import com.example.localloop.backend.DatabaseConnection;
import com.example.localloop.backend.Participant;
import com.example.localloop.exception.database.DatabaseConnectionException;
import com.example.localloop.backend.Organizer;


/**
 * Instrumented test, which will execute on an Android device.
 *
 * @see <a href="http://d.android.com/tools/testing">Testing documentation</a>
 */
@RunWith(AndroidJUnit4.class)
public class ExampleInstrumentedTest {
    @Test
    public void testParticipantsLoad() throws InterruptedException {
        Context appContext = InstrumentationRegistry.getInstrumentation().getTargetContext();
        Organizer o = new Organizer("tommer", "321", null);
        //Participant o = new Participant("umbo","456",null);
        assertEquals(DatabaseConnection.createNew(o),true);
    }
}