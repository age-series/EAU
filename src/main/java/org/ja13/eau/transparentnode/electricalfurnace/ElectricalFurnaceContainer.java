package org.ja13.eau.transparentnode.electricalfurnace;

import org.ja13.eau.generic.GenericItemUsingDamageSlot;
import org.ja13.eau.gui.ISlotSkin.SlotSkin;
import org.ja13.eau.gui.SlotWithSkin;
import org.ja13.eau.item.HeatingCorpElement;
import org.ja13.eau.item.ThermalIsolatorElement;
import org.ja13.eau.item.regulator.IRegulatorDescriptor.RegulatorType;
import org.ja13.eau.item.regulator.RegulatorSlot;
import org.ja13.eau.misc.BasicContainer;
import org.ja13.eau.node.INodeContainer;
import org.ja13.eau.node.NodeBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.IInventory;
import net.minecraft.inventory.Slot;
import org.ja13.eau.generic.GenericItemUsingDamageSlot;
import org.ja13.eau.gui.ISlotSkin;
import org.ja13.eau.gui.SlotWithSkin;
import org.ja13.eau.i18n.I18N;
import org.ja13.eau.item.HeatingCorpElement;
import org.ja13.eau.item.ThermalIsolatorElement;
import org.ja13.eau.item.regulator.IRegulatorDescriptor;
import org.ja13.eau.item.regulator.RegulatorSlot;
import org.ja13.eau.misc.BasicContainer;
import org.ja13.eau.node.INodeContainer;
import org.ja13.eau.node.NodeBase;

import static org.ja13.eau.i18n.I18N.tr;

public class ElectricalFurnaceContainer extends BasicContainer implements INodeContainer {

    NodeBase node = null;

    public ElectricalFurnaceContainer(NodeBase node, EntityPlayer player, IInventory inventory) {
        super(player, inventory, new Slot[]{
            new SlotWithSkin(inventory, ElectricalFurnaceElement.outSlotId, 84, 58, ISlotSkin.SlotSkin.big),
            new SlotWithSkin(inventory, ElectricalFurnaceElement.inSlotId, 7, 58, ISlotSkin.SlotSkin.medium),

            new GenericItemUsingDamageSlot(inventory, 2, 70, 6 + 20 + 6, 1, HeatingCorpElement.class, ISlotSkin.SlotSkin.medium,
                new String[]{I18N.tr("Heating corp slot")}),
            new GenericItemUsingDamageSlot(inventory, 3, 80 + 18, -2000, 1, ThermalIsolatorElement.class, ISlotSkin.SlotSkin
                .medium, new String[]{I18N.tr("Thermal isolator slot")}),
            new RegulatorSlot(inventory, 4, 70 + 18, 6 + 20 + 6, 1, new IRegulatorDescriptor.RegulatorType[]{IRegulatorDescriptor.RegulatorType.OnOff,
                IRegulatorDescriptor.RegulatorType.Analog}, ISlotSkin.SlotSkin.medium)
        });
        this.node = node;
    }

    @Override
    public NodeBase getNode() {
        return node;
    }

    @Override
    public int getRefreshRateDivider() {
        return 1;
    }
}
