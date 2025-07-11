package com.example.localloop;

import org.junit.Test;

import com.example.localloop.model.DatabaseConnection;

/**
 * Example local unit test, which will execute on the development machine (host).
 *
 * @see <a href="http://d.android.com/tools/testing">Testing documentation</a>
 */
public class ExampleUnitTest {
    @Test
    public void addition_isCorrect() {
        DatabaseConnection db = new DatabaseConnection("bb", "123");
    }
}