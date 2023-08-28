package com.example.unitestexample

import android.content.Context
import com.example.unitestexample.utils.Const.GOOD
import com.example.unitestexample.utils.Const.NOT_BAD
import com.example.unitestexample.utils.Const.NOT_GOOD
import com.example.unitestexample.utils.Const.PERFECT
import com.example.unitestexample.shadow.ShadowElectricController
import com.example.unitestexample.shadow.ShadowGasController
import com.example.unitestexample.utils.Const.FULL_BATTERY
import com.example.unitestexample.utils.Const.LOW_BATTERY
import com.example.unitestexample.utils.ElectricController
import com.example.unitestexample.utils.ElectricController.OnElectricStateChangeListener
import com.example.unitestexample.utils.GasController
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.ArgumentCaptor
import org.mockito.Mockito.anyInt
import org.mockito.Mockito.doNothing
import org.mockito.Mockito.doThrow
import org.mockito.Mockito.mock
import org.mockito.Mockito.times
import org.mockito.Mockito.verify
import org.mockito.Mockito.`when`
import org.robolectric.RobolectricTestRunner
import org.robolectric.RuntimeEnvironment
import org.robolectric.annotation.Config
import org.robolectric.shadows.ShadowToast
import org.robolectric.util.ReflectionHelpers
import java.lang.RuntimeException

@RunWith(RobolectricTestRunner::class)
class BasicTest {
    private lateinit var mBasic: Basic
    private lateinit var mContext: Context

    @Before
    fun setup() {
        /**
         * Have two ways two create mBasic here
         *      1. Create with real context
         *
         *      2. Create with mockContext
         */
        mBasic = Basic(RuntimeEnvironment.getApplication()) /*Real Context*/

//        mContext = mock(Context::class.java)
//        mBasic = Basic(mContext) /*mock Context*/

    }

    @After
    fun tearDown() {
        //Destroy or reset values after a Test.
    }

    /**
     * Test public function
     */
    @Test
    fun testSetupCar() {
        mBasic.setupCar()
        /**
         * function setupCar() change mIsSetting value(s)
         * Verify this value after function called
         */
        val isSetting: Boolean = ReflectionHelpers.getField(mBasic, "mIsSetting")
        assertEquals(isSetting, true)
    }

    /**
     * Test private function
     * When you want to test a private function, you have to call it through a public function
     *
     * In case private function(s) was not called from any where (functions for future feature expansion)
     * you can use ReflectionHelpers lib.
     */
    @Test
    fun testPowerOn() {
        /**
         * powerOn() is private, we can call it through setupCar()
         */
        mBasic.setupCar()

        /**
         * function powerOn() change mIsPowerOn value(s)
         * Verify this value after function called
         */
        val mIsPowerOn: Boolean = ReflectionHelpers.getField(mBasic, "mIsPowerOn")
        assertEquals(mIsPowerOn, true)

        /*Not recommend, just use when function was not called from any where*/
        ReflectionHelpers.callInstanceMethod<Void>(mBasic, "powerOn")
    }

    /**
     * Test branching structure
     * expected cover all the cases
     */
    @Test
    fun testCheckPublicMachineState() {
        assertEquals(mBasic.checkPublicMachineState(1), NOT_BAD)
        assertEquals(mBasic.checkPublicMachineState(2), GOOD)
        assertEquals(mBasic.checkPublicMachineState(3), PERFECT)
        assertEquals(mBasic.checkPublicMachineState(-1), NOT_GOOD)
    }

    /**
     * Test a private function with private parameters
     *
     * Have two ways to test those function:
     *      1. (Recommend) interact with this parameters through a public function
     *
     *      2. (NOT recommend) use ReflectionHelper lib.
     *      (This way is not recommend, we just use this when the parameter(s) is too hard to set value)
     */
    @Test
    fun testCheckPrivateMachineState() {
        //The First way
        mBasic.changeState(0)

        mBasic.setupCar()
        var mState: String = ReflectionHelpers.getField(mBasic, "mState")
        assertEquals(mState, NOT_BAD)

        //The Second way (NOT recommend)
        ReflectionHelpers.setField(mBasic, "mState", 1)
        mBasic.setupCar()
        mState = ReflectionHelpers.getField(mBasic, "mState")
        assertEquals(mState, NOT_BAD)
    }

    /**
     * Interact with mock object
     *
     * Put mock Object into test class
     *
     *      NOTE: With Final classes, we can not create a mock by usual way(s)
     *      Solution is create a ShadowClass with target is your final class,
     *      implement this shadow into your test (Ex: Gas.kt)
     */
    @Test
    @Config(shadows = [ShadowElectricController::class, ShadowGasController::class])
    fun testUseEnergy() {
        /**
         * Gas and Electric have 2 different type of creator.
         *      + Singleton
         *      + Constructor function
         */

        val mockGasController = mock(GasController::class.java)
        val mockElectricController = mock(ElectricController::class.java)

        /**
         * Example about some useful mock apis
         * We have to declare those rules before invoked
         */

        //Handle returned value
        `when`(mockElectricController.state).thenReturn(0)

        //Ignore implementations
        doNothing().`when`(mockElectricController).shock(anyInt())

        //Cover exception case
        doThrow(RuntimeException("TestException")).`when`(mockElectricController.makeDangerThing())

        /**
         * Use Shadow to set mock
         */
        ShadowElectricController.setInstance(mockElectricController)

        /**
         * Use ReflectionHelper lib
         */
        mBasic.setupCar() // create object first
        ReflectionHelpers.setField(mBasic, "mGas", mockGasController)

        mBasic.useEnergy()
        verify(mockGasController, times(1)).burn()
        verify(mockElectricController, times(1)).shock(anyInt())

    }

    /**
     * Interface, Callback
     */
    @Test
    @Config(shadows = [ElectricController::class])
    fun testRegister() {
        /**
         * 1. Argument Captor (Recommend)
         */
        val mockElectric = mock(ElectricController::class.java)
        ShadowElectricController.setInstance(mockElectric)

        val listenerCaptor = ArgumentCaptor.forClass(OnElectricStateChangeListener::class.java)

        mBasic.setupCar() // call initEnergy
        verify(mockElectric).registerCallback(listenerCaptor.capture())

        val listener = listenerCaptor.value
        listener.handleFullElectric()
        assertEquals(ShadowToast.getTextOfLatestToast(), FULL_BATTERY)

        listener.handleLowElectric()
        assertEquals(ShadowToast.getTextOfLatestToast(), LOW_BATTERY)


        /**
         * 2. Reflection lib
         */
        val refListener: OnElectricStateChangeListener = ReflectionHelpers.getField(mBasic, "mOnElectricStateChangeListener")

        refListener.handleLowElectric()
        assertEquals(ShadowToast.getTextOfLatestToast(), FULL_BATTERY)

        refListener.handleLowElectric()
        assertEquals(ShadowToast.getTextOfLatestToast(), LOW_BATTERY)
    }

    /**
     *
     */

}