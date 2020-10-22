package org.ja13.eau.sixnode.electricalsource;

import org.ja13.eau.EAU;
import org.ja13.eau.generic.GenericItemUsingDamageDescriptor;
import org.ja13.eau.i18n.I18N;
import org.ja13.eau.item.BrushDescriptor;
import org.ja13.eau.item.IConfigurable;
import org.ja13.eau.misc.Direction;
import org.ja13.eau.misc.LRDU;
import org.ja13.eau.misc.Utils;
import org.ja13.eau.node.NodeBase;
import org.ja13.eau.node.six.SixNode;
import org.ja13.eau.node.six.SixNodeDescriptor;
import org.ja13.eau.node.six.SixNodeElement;
import org.ja13.eau.sim.ElectricalLoad;
import org.ja13.eau.sim.ThermalLoad;
import org.ja13.eau.sim.mna.component.VoltageSource;
import org.ja13.eau.sim.nbt.NbtElectricalLoad;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import org.ja13.eau.EAU;
import org.ja13.eau.generic.GenericItemUsingDamageDescriptor;
import org.ja13.eau.i18n.I18N;
import org.ja13.eau.item.BrushDescriptor;
import org.ja13.eau.item.IConfigurable;
import org.ja13.eau.misc.Direction;
import org.ja13.eau.misc.LRDU;
import org.ja13.eau.misc.Utils;
import org.ja13.eau.node.NodeBase;
import org.ja13.eau.node.six.SixNode;
import org.ja13.eau.node.six.SixNodeDescriptor;
import org.ja13.eau.node.six.SixNodeElement;
import org.ja13.eau.sim.ElectricalLoad;
import org.ja13.eau.sim.ThermalLoad;
import org.ja13.eau.sim.mna.component.VoltageSource;
import org.ja13.eau.sim.nbt.NbtElectricalLoad;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

public class ElectricalSourceElement extends SixNodeElement implements IConfigurable {

    NbtElectricalLoad electricalLoad = new NbtElectricalLoad("electricalLoad");
    VoltageSource voltageSource = new VoltageSource("voltSrc", electricalLoad, null);

    public static final int setVoltageId = 1;

    int color = 0;
    int colorCare = 0;

    public ElectricalSourceElement(SixNode sixNode, Direction side, SixNodeDescriptor descriptor) {
        super(sixNode, side, descriptor);
        electricalLoadList.add(electricalLoad);
        electricalComponentList.add(voltageSource);
    }

    @Override
    public void readFromNBT(NBTTagCompound nbt) {
        super.readFromNBT(nbt);
        byte b = nbt.getByte("color");
        color = b & 0xF;
        colorCare = (b >> 4) & 1;

        voltageSource.setU(nbt.getDouble("voltage"));
    }

    public static boolean canBePlacedOnSide(Direction side, int type) {
        return true;
    }

    @Override
    public void writeToNBT(NBTTagCompound nbt) {
        super.writeToNBT(nbt);
        nbt.setByte("color", (byte) (color + (colorCare << 4)));

        nbt.setDouble("voltage", voltageSource.getU());
    }

    @Override
    public ElectricalLoad getElectricalLoad(LRDU lrdu, int mask) {
        return electricalLoad;
    }

    @Override
    public ThermalLoad getThermalLoad(LRDU lrdu, int mask) {
        return null;
    }

    @Override
    public int getConnectionMask(LRDU lrdu) {
        if (((ElectricalSourceDescriptor) sixNodeElementDescriptor).isSignalSource()) {
            return NodeBase.maskElectricalGate + (color << NodeBase.maskColorShift) +
                (colorCare << NodeBase.maskColorCareShift);
        } else {
            return NodeBase.MASK_ELECTRIC + (color << NodeBase.maskColorShift) +
                (colorCare << NodeBase.maskColorCareShift);
        }
    }

    @Override
    public String multiMeterString() {
        return Utils.plotUIP(electricalLoad.getU(), voltageSource.getI());
    }

    @Override
    public Map<String, String> getWaila() {
        Map<String, String> info = new HashMap<String, String>();
        info.put(I18N.tr("Voltage"), Utils.plotVolt(electricalLoad.getU(), ""));
        info.put(I18N.tr("Current"), Utils.plotAmpere( electricalLoad.getCurrent(), ""));
        if (EAU.wailaEasyMode) {
            info.put(I18N.tr("Power"), Utils.plotPower(electricalLoad.getU() * electricalLoad.getI(), ""));
        }
        return info;
    }

    @Override
    public String thermoMeterString() {
        return "";
    }

    @Override
    public void networkSerialize(DataOutputStream stream) {
        super.networkSerialize(stream);
        try {
            stream.writeByte((color << 4));
            stream.writeFloat((float) voltageSource.getU());
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void initialize() {
        EAU.applySmallRs(electricalLoad);
    }

    @Override
    public boolean onBlockActivated(EntityPlayer entityPlayer, Direction side, float vx, float vy, float vz) {
        if (onBlockActivatedRotate(entityPlayer)) return true;
        ItemStack currentItemStack = entityPlayer.getCurrentEquippedItem();
        if (currentItemStack != null) {
            Item item = currentItemStack.getItem();

            GenericItemUsingDamageDescriptor gen = BrushDescriptor.getDescriptor(currentItemStack);
            if (gen != null && gen instanceof BrushDescriptor) {
                BrushDescriptor brush = (BrushDescriptor) gen;
                int brushColor = brush.getColor(currentItemStack);
                if (brushColor != color && brush.use(currentItemStack, entityPlayer)) {
                    color = brushColor;
                    sixNode.reconnect();
                }
            }
        }
        return false;
    }

    @Override
    public void networkUnserialize(DataInputStream stream) {
        super.networkUnserialize(stream);
        try {
            switch (stream.readByte()) {
                case setVoltageId:
                    voltageSource.setU(stream.readFloat());
                    needPublish();
                    break;
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public boolean hasGui() {
        return true;
    }

    @Override
    public void readConfigTool(NBTTagCompound compound, EntityPlayer invoker) {
        if(compound.hasKey("voltage")) {
            voltageSource.setU(compound.getDouble("voltage"));
            needPublish();
        }
    }

    @Override
    public void writeConfigTool(NBTTagCompound compound, EntityPlayer invoker) {
        compound.setDouble("voltage", voltageSource.getU());
    }
}
