package org.ja13.eau.node;

import org.ja13.eau.EAU;
import org.ja13.eau.EAU;

public abstract class GhostNode extends NodeBase {
    @Override
    public boolean mustBeSaved() {
        return false;
    }

    @Override
    public String getNodeUuid() {
        return EAU.ghostBlock.getNodeUuid();
    }
}
