package org.ja13.eau.sim.process.destruct

import org.ja13.eau.EAU
import org.ja13.eau.sim.IProcess

class DelayedDestruction(val dest: org.ja13.eau.sim.process.destruct.IDestructable, var tmout: Double): org.ja13.eau.sim.IProcess {
    init {
        org.ja13.eau.EAU.simulator.addSlowProcess(this)
    }

    override fun process(time: Double) {
        tmout -= time
        if(tmout <= 0.0) {
            dest.destructImpl()
            org.ja13.eau.EAU.simulator.removeSlowProcess(this)
        }
    }
}
