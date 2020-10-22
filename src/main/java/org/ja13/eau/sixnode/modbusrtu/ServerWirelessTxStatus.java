package org.ja13.eau.sixnode.modbusrtu;

import org.ja13.eau.EAU;
import org.ja13.eau.misc.Coordonate;
import org.ja13.eau.misc.Utils;
import org.ja13.eau.sixnode.wirelesssignal.IWirelessSignalTx;
import org.ja13.eau.sixnode.wirelesssignal.tx.WirelessSignalTxElement;
import net.minecraft.nbt.NBTTagCompound;
import org.ja13.eau.EAU;
import org.ja13.eau.misc.Coordonate;
import org.ja13.eau.misc.Utils;

public class ServerWirelessTxStatus extends WirelessTxStatus implements IWirelessSignalTx, IModbusSlot {

    private final ModbusRtuElement rtu;

    Coordonate coordonate;

    short getHoldingRegister_1;
    short setHoldingRegister_0;

    public ServerWirelessTxStatus(String name, int id, double value, Coordonate coordonate, int uuid, ModbusRtuElement rtu) {
        super(name, id, value, uuid);
        this.coordonate = coordonate;
        WirelessSignalTxElement.channelRegister(this);
        this.rtu = rtu;
        rtu.mapping.add(this);
    }

    public ServerWirelessTxStatus(NBTTagCompound nbt, String str, ModbusRtuElement rtu) {
        super();
        readFromNBT(nbt, str);
        this.coordonate = rtu.sixNode.coordonate;
        WirelessSignalTxElement.channelRegister(this);
        this.rtu = rtu;
        rtu.mapping.add(this);
    }

    public void setName(String name) {
        WirelessSignalTxElement.channelRemove(this);
        super.setName(name);
        WirelessSignalTxElement.channelRegister(this);
    }

    public void delete() {
        rtu.mapping.remove(this);
        WirelessSignalTxElement.channelRemove(this);
    }

    @Override
    public Coordonate getCoordonate() {
        return coordonate;
    }

    @Override
    public int getRange() {
        return EAU.wirelessTxRange;
    }

    @Override
    public String getChannel() {
        return name;
    }

    @Override
    public double getValue() {
        return value;
    }

    public void setValue(double value) {
        this.value = value;
    }

    @Override
    public int getOffset() {
        return id;
    }

    @Override
    public int getSize() {
        return 4;
    }

    @Override
    public boolean getCoil(int id) {
        switch (id) {
            case 1:
                return value >= 0.5;
        }
        return false;
    }

    @Override
    public short getHoldingRegister(int id) {
        switch (id) {
            case 1:
                float v = (float) getValue();
                getHoldingRegister_1 = Utils.modbusToShort(v, 1);
                return Utils.modbusToShort(v, 0);
            case 2:
                return getHoldingRegister_1;
            case 3:
                return (short) (65535.0 * getValue());
        }
        return 0;
    }

    @Override
    public boolean getInput(int id) {
        return false;
    }

    @Override
    public short getInputRegister(int id) {
        return 0;
    }

    @Override
    public void setCoil(int id, boolean value) {
        switch (id) {
            case 1:
                setValue(value ? 1.0 : 0.0);
                break;
        }
    }

    @Override
    public void setHoldingRegister(int id, short value) {
        switch (id) {
            case 1:
                setHoldingRegister_0 = value;
                break;
            case 2:
                setValue(Utils.modbusToFloat(setHoldingRegister_0, value));
                break;
            case 3:
                setValue((double) ((int) value & 0xFFFF) / 65535.0);
                break;
        }
    }

    @Override
    public void setInput(int id, boolean value) {
    }

    @Override
    public void setInputRegister(int id, short value) {
    }

    @Override
    public void writeCoil(int id, boolean value) {
        setCoil(id, value);
    }

    @Override
    public void writeHoldingRegister(int id, short value) {
        setHoldingRegister(id, value);
    }
}
