package org.ja13.eau.transparentnode.heatfurnace;

import org.ja13.eau.generic.GenericItemUsingDamageSlot;
import org.ja13.eau.gui.IItemStackFilter;
import org.ja13.eau.gui.ISlotSkin.SlotSkin;
import org.ja13.eau.gui.SlotFilter;
import org.ja13.eau.item.CombustionChamber;
import org.ja13.eau.item.ThermalIsolatorElement;
import org.ja13.eau.item.regulator.IRegulatorDescriptor;
import org.ja13.eau.misc.BasicContainer;
import org.ja13.eau.misc.Utils;
import org.ja13.eau.node.INodeContainer;
import org.ja13.eau.node.NodeBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.IInventory;
import net.minecraft.inventory.Slot;
import net.minecraft.item.ItemStack;
import org.ja13.eau.generic.GenericItemUsingDamageSlot;
import org.ja13.eau.gui.IItemStackFilter;
import org.ja13.eau.gui.ISlotSkin;
import org.ja13.eau.gui.SlotFilter;
import org.ja13.eau.i18n.I18N;
import org.ja13.eau.item.CombustionChamber;
import org.ja13.eau.item.ThermalIsolatorElement;
import org.ja13.eau.item.regulator.IRegulatorDescriptor;
import org.ja13.eau.misc.BasicContainer;
import org.ja13.eau.misc.Utils;
import org.ja13.eau.node.INodeContainer;
import org.ja13.eau.node.NodeBase;

import static org.ja13.eau.i18n.I18N.tr;

public class HeatFurnaceContainer extends BasicContainer implements INodeContainer {

    public static final int combustibleId = 0;
    public static final int regulatorId = 1;
    public static final int isolatorId = 2;
    public static final int combustrionChamberId = 3;

    NodeBase node;

    public HeatFurnaceContainer(NodeBase node, EntityPlayer player, IInventory inventory, HeatFurnaceDescriptor descriptor) {
        super(player, inventory, new Slot[]{
            new SlotFilter(inventory, combustibleId, 70, 58, 64, filters(), ISlotSkin.SlotSkin.medium,
                new String[]{I18N.tr("Fuel slot")}),
            //	new RegulatorSlot(inventory, regulatorId,62 + 0, 17 + 18, 1, new RegulatorType[]{),
            new GenericItemUsingDamageSlot(inventory, regulatorId, 8, 58, 1, IRegulatorDescriptor.class,
                ISlotSkin.SlotSkin.medium,
                new String[]{I18N.tr("Regulator slot")}),
            new GenericItemUsingDamageSlot(inventory, isolatorId, 8 + 18, -2000, 1,
                ThermalIsolatorElement.class, ISlotSkin.SlotSkin.medium,
                new String[]{I18N.tr("Thermal isolator slot")}),
            new GenericItemUsingDamageSlot(inventory, combustrionChamberId, 8 + 18, 58,
                descriptor.combustionChamberMax, CombustionChamber.class, ISlotSkin.SlotSkin.medium,
                new String[]{I18N.tr("Combustion chamber slot")}),
        });
        this.node = node;
    }

    private static IItemStackFilter[] filters() {
        IItemStackFilter[] filters = new IItemStackFilter[1];
        filters[0] = new IItemStackFilter() {
            @Override
            public boolean tryItemStack(ItemStack itemStack) {
                return Utils.getItemEnergy(itemStack) > 0;
            }
        };
        return filters;
    }

    @Override
    public NodeBase getNode() {
        return node;
    }

    @Override
    public int getRefreshRateDivider() {
        return 0;
    }
}
