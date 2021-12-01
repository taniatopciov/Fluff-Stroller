package com.example.flusffstroller.utils.observer;

import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

public class SubjectTest {

    @Test
    public void testMap() {
        final boolean[] notified = {false};
        Subject<String> subject = new Subject<>();

        subject.map(String::length)
                .subscribe(response -> {
                    if (response.hasErrors()) {
                        fail();
                    }
                    assertNotNull(response.data);
                    assertEquals(5, (int) response.data);
                    notified[0] = true;
                });

        subject.notifyObservers("12345");

        assertTrue(notified[0]);

        subject.clearAllObservers();
    }

    @Test
    public void testMapAndSubscribe() {
        final boolean[] notified = {false, false};
        Subject<String> subject = new Subject<>();

        subject.mapAndSubscribe(str -> {
            assertEquals("text", str);
            return str.length();
        }, response -> {
            if (response.hasErrors()) {
                fail();
            }

            assertNotNull(response.data);
            assertEquals(4, (int) response.data);
            notified[0] = true;
        })
                .map(Object::toString)
                .subscribe(res -> {
                    if (res.hasErrors()) {
                        fail();
                    }

                    assertEquals("4", res.data);
                    notified[1] = true;
                });


        subject.notifyObservers("text");

        assertTrue(notified[0]);
        assertTrue(notified[1]);

        subject.clearAllObservers();
    }
}