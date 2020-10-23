package org.ja13.eau.misc;

public class KotlinHelper {
    public static int and(byte a, byte b) {
        return a & b;
    }
    public static int and(int a, int b) {
        return (byte)a & (byte)b;
    }
    public static int and(byte a, int b) {
        return a & (byte)b;
    }
    public static int and(int a, byte b) {
        return (byte)a & b;
    }

    public static int shr(byte a, byte b) { return a >> b; }
    public static int shr(int a, byte b) { return a >> b; }
    public static int shr(byte a, int b) { return a >> b; }
    public static int shr(int a, int b) {return a >> b; }
}
