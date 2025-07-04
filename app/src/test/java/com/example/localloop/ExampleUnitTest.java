package com.example.localloop;

import org.junit.Test;

import static org.junit.Assert.*;

import com.example.localloop.backend.DatabaseConnection;

/**
 * Example local unit test, which will execute on the development machine (host).
 *
 * @see <a href="http://d.android.com/tools/testing">Testing documentation</a>
 */
public class ExampleUnitTest {
    @Test
    public void addition_isCorrect() {
        DatabaseConnection db = new DatabaseConnection("bob", "123");
        System.out.println(db.getAllParticipants());
        System.out.println(db.getAllOrganizers());
    }
}