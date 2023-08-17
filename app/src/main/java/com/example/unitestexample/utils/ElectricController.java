package com.example.unitestexample.utils;

import android.util.Log;

public class ElectricController {

    private static ElectricController sInstance;

    private OnElectricStateChangeListener mOnElectricStateChangeListener = new OnElectricStateChangeListener() {
        @Override
        public void handleLowElectric() {

        }

        @Override
        public void handleFullElectric() {

        }
    };

    public static ElectricController getInstance(){
        synchronized (ElectricController.class) {
            if(null == sInstance) {
                sInstance = new ElectricController();
            }
        }
        return  sInstance;
    }

    public void shock(int volt){

    }

    public int getState(){
        return 1;
    }

    public void makeDangerThing() throws Exception{
        try {
            //do something
        }catch (Exception e){
            Log.e("Electric", "Got exception");
        }
    }

    public void registerCallback(OnElectricStateChangeListener listener) {
        mOnElectricStateChangeListener = listener;
    }

    public void unregisterCallback(){
        mOnElectricStateChangeListener = null;
    }

    public interface OnElectricStateChangeListener {
        void handleLowElectric();

        void handleFullElectric();
    }
}
