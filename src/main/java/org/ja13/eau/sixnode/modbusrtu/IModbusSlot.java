package org.ja13.eau.sixnode.modbusrtu;

public interface IModbusSlot {

    int getOffset();

    int getSize();

    boolean getCoil(int id);

    short getHoldingRegister(int id);

    boolean getInput(int id);

    short getInputRegister(int id);

    void setCoil(int id, boolean value);

    void setHoldingRegister(int id, short value);

    void setInput(int id, boolean value);

    void setInputRegister(int id, short value);

    void writeCoil(int id, boolean value);

    void writeHoldingRegister(int id, short value);
}
