package org.ja13.eau.sim;

import org.ja13.eau.EAU;
import org.ja13.eau.EAU;

public class TimeRemover implements IProcess {

    ITimeRemoverObserver observer;
    double timeout = 0;

    public TimeRemover(ITimeRemoverObserver observer) {
        this.observer = observer;
    }

    public void setTimeout(double timeout) {
        if (this.timeout <= 0) {
            observer.timeRemoverAdd();
            EAU.simulator.addSlowProcess(this);
        }
        this.timeout = timeout;
    }

    @Override
    public void process(double time) {
        if (isArmed()) {
            timeout -= time;
            if (timeout <= 0) {
                shot();
            }
        }
    }

    public boolean isArmed() {
        return timeout > 0;
    }

    public void shot() {
        timeout = 0;
        observer.timeRemoverRemove();
        EAU.simulator.removeSlowProcess(this);
    }
}
