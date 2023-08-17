package com.example.unitestexample

import android.content.Context
import android.util.Log
import android.widget.Toast
import com.example.unitestexample.utils.Const.FULL_BATTERY
import com.example.unitestexample.utils.Const.GOOD
import com.example.unitestexample.utils.Const.LOW_BATTERY
import com.example.unitestexample.utils.Const.NOT_BAD
import com.example.unitestexample.utils.Const.NOT_GOOD
import com.example.unitestexample.utils.Const.PERFECT
import com.example.unitestexample.utils.ElectricController
import com.example.unitestexample.utils.ElectricController.OnElectricStateChangeListener
import com.example.unitestexample.utils.GasController

class Basic(private val mContext: Context) {
    private var mGasController: GasController? = null
    private var mElectricController: ElectricController? = null
    private var mIsPowerOn = false
    private var mIsSetting = false
    private var mState = ""
    private var mStateValue: Int = 0
    private var mIsElectricFull = false;

    private val mOnElectricStateChangeListener: OnElectricStateChangeListener =
        object : OnElectricStateChangeListener {
            override fun handleLowElectric() {
                mIsElectricFull = false
                Toast.makeText(mContext, LOW_BATTERY, Toast.LENGTH_LONG).show()
            }
            override fun handleFullElectric() {
                mIsElectricFull = true
                Toast.makeText(mContext, FULL_BATTERY, Toast.LENGTH_LONG).show()
            }
        }

    fun setupCar() {
        mIsSetting = true
        powerOn()
        initEnergy()
        mState = checkPrivateMachineState(mStateValue)
    }

    private fun powerOn() {
        Log.d(TAG, "Turn on Power}")
        mIsPowerOn = true
    }

    private fun powerOff() {
        Log.d(TAG, "Turn off Power")
        mIsPowerOn = false
    }

    fun changeState(state: Int){
        mStateValue = state + 1
    }

    private fun checkPrivateMachineState(state: Int): String {
        return when (state) {
            1 -> NOT_BAD
            2 -> GOOD
            3 -> PERFECT
            else -> NOT_GOOD
        }
    }

    fun checkPublicMachineState(state: Int): String {
        return when (state) {
            1 -> NOT_BAD
            2 -> GOOD
            3 -> PERFECT
            else -> NOT_GOOD
        }
    }

    private fun initEnergy(){
        if (mGasController == null) {
            mGasController = GasController(mContext)
        }
        if (mElectricController == null) {
            mElectricController = ElectricController.getInstance()
        }

        mElectricController?.registerCallback(mOnElectricStateChangeListener)
    }

    fun useEnergy() {
        mGasController?.burn()
        mElectricController?.shock(100)
    }

    companion object {
        private const val TAG = "BasicClass"
    }
}