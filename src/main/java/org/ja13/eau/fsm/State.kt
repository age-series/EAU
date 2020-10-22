package org.ja13.eau.fsm

interface State {
    fun enter()
    fun state(time: Double): State?
    fun leave()
}
