package org.ja13.eau.server;

import org.ja13.eau.EAU;
import org.ja13.eau.misc.Coordonate;
import org.ja13.eau.server.DelayedTaskManager.ITask;
import net.minecraft.init.Blocks;
import org.ja13.eau.EAU;
import org.ja13.eau.misc.Coordonate;

import java.util.HashSet;
import java.util.Set;

public class DelayedBlockRemove implements ITask {

    Coordonate c;

    private static final Set<Coordonate> blocks = new HashSet<Coordonate>();

    private DelayedBlockRemove(Coordonate c) {
        this.c = c;
    }

    public static void clear() {
        blocks.clear();
    }

    public static void add(Coordonate c) {
        if (blocks.contains(c)) return;
        blocks.add(c);
        EAU.delayedTask.add(new DelayedBlockRemove(c));
    }

    @Override
    public void run() {
        blocks.remove(c);
        c.setBlock(Blocks.air);
    }
}
