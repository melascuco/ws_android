package com.melascuco.android.testingprocedures;

import android.test.InstrumentationTestCase;

public class ExampleTest extends InstrumentationTestCase {

    public void test() throws Exception {
        final int expected = 5;
        final int reality = 3;
        assertEquals(expected, reality);
    }
}