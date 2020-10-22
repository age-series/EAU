package org.ja13.eau.sixnode.powerinductorsix;

import org.ja13.eau.EAU;
import org.ja13.eau.generic.GenericItemUsingDamageDescriptor;
import org.ja13.eau.i18n.I18N;
import org.ja13.eau.item.IConfigurable;
import org.ja13.eau.item.ItemMovingHelper;
import org.ja13.eau.misc.Direction;
import org.ja13.eau.misc.LRDU;
import org.ja13.eau.misc.Utils;
import org.ja13.eau.node.NodeBase;
import org.ja13.eau.node.six.SixNode;
import org.ja13.eau.node.six.SixNodeDescriptor;
import org.ja13.eau.node.six.SixNodeElement;
import org.ja13.eau.node.six.SixNodeElementInventory;
import org.ja13.eau.sim.ElectricalLoad;
import org.ja13.eau.sim.ThermalLoad;
import org.ja13.eau.sim.mna.component.Inductor;
import org.ja13.eau.sim.nbt.NbtElectricalLoad;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.Container;
import net.minecraft.inventory.IInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import org.ja13.eau.EAU;
import org.ja13.eau.generic.GenericItemUsingDamageDescriptor;
import org.ja13.eau.i18n.I18N;
import org.ja13.eau.item.IConfigurable;
import org.ja13.eau.item.ItemMovingHelper;
import org.ja13.eau.misc.Direction;
import org.ja13.eau.misc.LRDU;
import org.ja13.eau.misc.Utils;
import org.ja13.eau.node.NodeBase;
import org.ja13.eau.node.six.SixNode;
import org.ja13.eau.node.six.SixNodeDescriptor;
import org.ja13.eau.node.six.SixNodeElement;
import org.ja13.eau.node.six.SixNodeElementInventory;
import org.ja13.eau.sim.ElectricalLoad;
import org.ja13.eau.sim.ThermalLoad;
import org.ja13.eau.sim.mna.component.Inductor;
import org.ja13.eau.sim.nbt.NbtElectricalLoad;

import javax.annotation.Nullable;
import java.util.HashMap;
import java.util.Map;

public class PowerInductorSixElement extends SixNodeElement implements IConfigurable {

    PowerInductorSixDescriptor descriptor;
    NbtElectricalLoad positiveLoad = new NbtElectricalLoad("positiveLoad");
    NbtElectricalLoad negativeLoad = new NbtElectricalLoad("negativeLoad");

    Inductor inductor = new Inductor("inductor", positiveLoad, negativeLoad);

    boolean fromNbt = false;

    SixNodeElementInventory inventory = new SixNodeElementInventory(2, 64, this);

    public PowerInductorSixElement(SixNode SixNode, Direction side, SixNodeDescriptor descriptor) {
        super(SixNode, side, descriptor);
        this.descriptor = (PowerInductorSixDescriptor) descriptor;

        electricalLoadList.add(positiveLoad);
        electricalLoadList.add(negativeLoad);
        electricalComponentList.add(inductor);
        positiveLoad.setAsMustBeFarFromInterSystem();
    }

    @Override
    public ElectricalLoad getElectricalLoad(LRDU lrdu, int mask) {
        if (lrdu == front.right()) return positiveLoad;
        if (lrdu == front.left()) return negativeLoad;
        return null;
    }

    @Override
    public ThermalLoad getThermalLoad(LRDU lrdu, int mask) {
        return null;
    }

    @Override
    public int getConnectionMask(LRDU lrdu) {
        if (lrdu == front.right()) return NodeBase.MASK_ELECTRIC;
        if (lrdu == front.left()) return NodeBase.MASK_ELECTRIC;
        return 0;
    }

    @Override
    public String multiMeterString() {
        return Utils.plotVolt(Math.abs(inductor.getU()), "") + Utils.plotAmpere(inductor.getCurrent(), "");
    }

    @Nullable
    @Override
    public Map<String, String> getWaila() {
        Map<String, String> info = new HashMap<String, String>();
        info.put(I18N.tr("Inductance"), Utils.plotValue(inductor.getL(), "H", ""));
        info.put(I18N.tr("Charge"), Utils.plotEnergy(inductor.getE(), ""));
        if (EAU.wailaEasyMode) {
            info.put(I18N.tr("Voltage drop"), Utils.plotVolt(Math.abs(inductor.getU()), ""));
            info.put(I18N.tr("Current"), Utils.plotAmpere(Math.abs(inductor.getCurrent()), ""));
        }
        return info;
    }

    @Override
    public String thermoMeterString() {
        return null;
    }

    @Override
    public void initialize() {
        setupPhysical();
    }

    @Override
    public void inventoryChanged() {
        super.inventoryChanged();
        setupPhysical();
    }

    public void setupPhysical() {
        double rs = descriptor.getRsValue(inventory);
        inductor.setL(descriptor.getlValue(inventory));
        positiveLoad.setRs(rs);
        negativeLoad.setRs(rs);

        if (fromNbt) {
            fromNbt = false;
        } else {
            inductor.resetStates();
        }
    }

    @Override
    public void writeToNBT(NBTTagCompound nbt) {
        super.writeToNBT(nbt);
    }

    @Override
    public void readFromNBT(NBTTagCompound nbt) {
        super.readFromNBT(nbt);
        fromNbt = true;
    }

    @Override
    public IInventory getInventory() {
        return inventory;
    }

    @Override
    public boolean hasGui() {
        return true;
    }

    @Override
    public Container newContainer(Direction side, EntityPlayer player) {
        return new PowerInductorSixContainer(player, inventory);
    }

    @Override
    public void readConfigTool(NBTTagCompound compound, EntityPlayer invoker) {
        if(compound.hasKey("indCableAmt")) {
            int desired = compound.getInteger("indCableAmt");
            (new ItemMovingHelper() {
                @Override
                public boolean acceptsStack(ItemStack stack) {
                    return EAU.copperCableDescriptor.checkSameItemStack(stack);
                }

                @Override
                public ItemStack newStackOfSize(int items) {
                    return EAU.copperCableDescriptor.newItemStack(items);
                }
            }).move(invoker.inventory, inventory, PowerInductorSixContainer.cableId, desired);
            reconnect();
        }
        if(compound.hasKey("indCore")) {
            String descName = compound.getString("indCore");
            if(descName == GenericItemUsingDamageDescriptor.INVALID_NAME) {
                ItemStack stack = inventory.getStackInSlot(PowerInductorSixContainer.coreId);
                GenericItemUsingDamageDescriptor desc = GenericItemUsingDamageDescriptor.getDescriptor(stack);
                if(desc != null) {
                    (new ItemMovingHelper() {
                        @Override
                        public boolean acceptsStack(ItemStack stack) {
                            return desc == GenericItemUsingDamageDescriptor.getDescriptor(stack);
                        }

                        @Override
                        public ItemStack newStackOfSize(int items) {
                            return desc.newItemStack(items);
                        }
                    }).move(invoker.inventory, inventory, PowerInductorSixContainer.coreId, 0);
                }
            } else {
                GenericItemUsingDamageDescriptor desc = GenericItemUsingDamageDescriptor.getByName(compound.getString("indCore"));
                (new ItemMovingHelper() {
                    @Override
                    public boolean acceptsStack(ItemStack stack) {
                        return GenericItemUsingDamageDescriptor.getDescriptor(stack) == desc;
                    }

                    @Override
                    public ItemStack newStackOfSize(int items) {
                        return desc.newItemStack(items);
                    }
                }).move(invoker.inventory, inventory, PowerInductorSixContainer.coreId, 1);
            }
            reconnect();
        }
    }

    @Override
    public void writeConfigTool(NBTTagCompound compound, EntityPlayer invoker) {
        ItemStack stack = inventory.getStackInSlot(PowerInductorSixContainer.cableId);
        if(stack == null) {
            compound.setInteger("indCableAmt", 0);
        } else {
            compound.setInteger("indCableAmt", stack.stackSize);
        }
        stack = inventory.getStackInSlot(PowerInductorSixContainer.coreId);
        GenericItemUsingDamageDescriptor desc = GenericItemUsingDamageDescriptor.getDescriptor(stack);
        if(desc == null) {
            compound.setString("indCore", GenericItemUsingDamageDescriptor.INVALID_NAME);
        } else {
            compound.setString("indCore", desc.name);
        }
    }
}
