package com.example.unitestexample.shadow

import com.example.unitestexample.utils.Example
import org.robolectric.annotation.Implementation
import org.robolectric.annotation.Implements

@Implements(Example::class)
class ShadowExample {

    companion object {
        var sValue = 0
    }

    /**
     * In case some functions get crash or can't pass through because of using special object or call
     * other functions which are hard to handle.
     *
     * ==> Create fake implementations to substitute for native code and
     * add additional APIs to make testing possible.
     */
    @Implementation
    fun doSomethingVeryComplicated() {
        //no op
    }

    /**
     * Handle return value
     */
    @Implementation
    fun getValue() : Int{
        return sValue
    }
}