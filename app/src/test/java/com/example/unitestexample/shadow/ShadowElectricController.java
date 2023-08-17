package com.example.unitestexample.shadow;

import com.example.unitestexample.utils.ElectricController;

import org.mockito.Mockito;
import org.robolectric.annotation.Implementation;
import org.robolectric.annotation.Implements;

@Implements(ElectricController.class)
public class ShadowElectricController {
    static ElectricController mInstance;

    public static void setInstance(ElectricController electric){
        mInstance = electric;
    }

    /**
     * When this shadow class is implemented
     * This function will be substitute for native code
     */
    @Implementation
    public static ElectricController getInstance() {
        if(mInstance == null){
            mInstance = Mockito.mock(ElectricController.class);
        }
        return mInstance;
    }
}
