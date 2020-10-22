package org.ja13.eau.fsm

import org.ja13.eau.sim.IProcess

open class StateMachine : org.ja13.eau.sim.IProcess {
    fun setInitialState(initialState: State?) {
        this.initialState = initialState
    }

    fun reset() {
        state = initialState
        if (state != null) state!!.enter()
    }

    protected fun stop() {
        state = null
    }

    fun setDebug(enabled: Boolean) {
        debug = enabled
    }

    override fun process(time: Double) {
        if (state == null) {
            if (debug) println("INVALID STATE!!")
            return
        }
        val nextState = state!!.state(time)
        if (nextState != null && nextState !== state) {
            if (debug) println("FSM: ${javaClass::class.java.simpleName}: ${state!!::class.java.simpleName} -> ${nextState::class.java.simpleName}")
            state!!.leave()
            state = nextState
            state!!.enter()
        }
    }

    private var initialState: State? = null
    private var state: State? = null
    private var debug = false
}
