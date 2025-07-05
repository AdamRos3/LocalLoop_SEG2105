package com.example.localloop;

import static org.junit.Assert.assertEquals;

import android.content.Context;
import android.util.Log;

import androidx.test.platform.app.InstrumentationRegistry;
import androidx.test.ext.junit.runners.AndroidJUnit4;

import org.junit.Test;
import org.junit.runner.RunWith;

import com.example.localloop.backend.DatabaseConnection;
import com.example.localloop.resources.exception.NoSuchUserException;


/**
 * Instrumented test, which will execute on an Android device.
 *
 * @see <a href="http://d.android.com/tools/testing">Testing documentation</a>
 */
@RunWith(AndroidJUnit4.class)
public class ExampleInstrumentedTest {
    @Test
    public void testParticipantsLoad() throws InterruptedException, NoSuchUserException {
        Context appContext = InstrumentationRegistry.getInstrumentation().getTargetContext();
        //Organizer o = new Organizer("tommer", "321", null);
        //Participant o = new Participant("umbo","456",null);
        //DatabaseConnection db = new DatabaseConnection("admin","XPI76SZUqyCjVxgnUjm0");
        DatabaseConnection db;
        try {
            db = new DatabaseConnection("admin", "XPI76SZUqyCjVxgnUjm0");
            Log.d("Login","VALID CREDENTIALS!");
        } catch (NoSuchUserException e) {
            throw e;
        }
    }
}