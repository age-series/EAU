package org.ja13.eau.sim.mna.component;

import org.ja13.eau.sim.mna.SubSystem;
import org.ja13.eau.sim.mna.SubSystem;

public interface IAbstractor {

    void dirty(Component component);

    SubSystem getAbstractorSubSystem();
}
