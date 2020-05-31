package hu.kts.cmetronome

import android.os.Handler
import android.os.SystemClock
import androidx.lifecycle.DefaultLifecycleObserver
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleOwner
import java.lang.ref.WeakReference

class TimeProvider(private val delayMillis: Long): DefaultLifecycleObserver {

    private var state = State.STOPPED
    private val handler = Handler()
    private var countDownStartValue = -1
    private var count: Long = 0
    var startTime: Long = 0
        private set

    private val nextTickDelayMillis: Long
        get() {
            val timestampOfDesiredNextTick = startTime + count * delayMillis + delayMillis
            val now = SystemClock.elapsedRealtime()
            return timestampOfDesiredNextTick - now
        }

    private val isCountDown: Boolean
        get() = countDownStartValue > -1

    private val isCountDownLastRound: Boolean
        get() = count == countDownStartValue.toLong()

    private var observer: WeakReference<(Long) -> Unit>? = null

    fun observeForever(observer: (Long) -> Unit) {
        this.observer = WeakReference(observer)
    }

    fun observe(lifecycle: Lifecycle, observer: (Long) -> Unit) {
        lifecycle.addObserver(this)
        observeForever(observer)
    }

    @Synchronized
    fun startUp() {
        initCount()
    }

    @Synchronized
    fun startDown(startValue: Int) {
        checkCountdownStartValue(startValue)
        countDownStartValue = startValue
        initCount()
    }

    @Synchronized
    fun stop() {
        state = State.STOPPED
    }

    @Synchronized
    fun continueSeamlesslyUp(originalStartTime: Long) {
        if (state == State.STOPPED) {
            startTime = originalStartTime
            state = State.IN_PROGRESS
            startCycle()
        }
    }

    private fun initCount() {
        startTime = SystemClock.elapsedRealtime()
        state = State.IN_PROGRESS
        startCycle()
    }

    private fun startCycle() {
        if (state == State.IN_PROGRESS) {
            val t = calcCallbackValue()
            callObserver(t)
            if (isCountDownLastRound) {
                state = State.STOPPED
            } else {
                handler.postDelayed({ this.startCycle() }, nextTickDelayMillis)
            }
        }
    }

    private fun checkCountdownStartValue(startValue: Int) {
        if (startValue < 0) {
            throw IllegalArgumentException("startValue must be greater or equal to 0")
        }
    }

    private fun calcCallbackValue(): Long {
        val elapsedTimeMillis = SystemClock.elapsedRealtime() - startTime
        count = elapsedTimeMillis / delayMillis
        //Division always rounds down and sometimes this causes that the same number comes twice. This way we compensate it.
        if (elapsedTimeMillis % delayMillis >= (delayMillis / 2)) ++count
        return if (isCountDown) countDownStartValue - count else count
    }

    private fun callObserver(value: Long) {
        observer?.get()?.invoke(value)
    }

    override fun onStart(owner: LifecycleOwner) {
        if (state == State.INACTIVE) {
            state = State.IN_PROGRESS
            startCycle()
        }
    }

    override fun onStop(owner: LifecycleOwner) {
        if (state == State.IN_PROGRESS) {
            state = State.INACTIVE
        }
    }

    /**
     * Stopped when someone called stop manually or never started.
     * Inactive when the caller activity is in background but progress
     * will continue automatically when it become to foreground again.
     */
    private enum class State {
        STOPPED, INACTIVE, IN_PROGRESS
    }
}
