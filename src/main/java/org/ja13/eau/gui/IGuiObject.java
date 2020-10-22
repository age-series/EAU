package org.ja13.eau.gui;

public interface IGuiObject {

    void idraw(int x, int y, float f);

    void idraw2(int x, int y);

    boolean ikeyTyped(char key, int code);

    void imouseClicked(int x, int y, int code);

    void imouseMove(int x, int y);

    void imouseMovedOrUp(int x, int y, int witch);

    interface IGuiObjectObserver {
        void guiObjectEvent(IGuiObject object);
    }

    void translate(int x, int y);

    int getYMax();
}
