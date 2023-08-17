package com.example.unitestexample.base

import org.junit.After
import org.junit.Before
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner

@RunWith(RobolectricTestRunner::class)
class BaseServiceTest {

    @Before
    fun setup() {

    }

    @After
    fun tearDown() {
        //Destroy or reset values after a Test.
    }
}