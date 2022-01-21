package com.flota.inicializacion.trans_init.trans;

/**
 * @author Elkin Beltrán
 */
public class HexEncoding {

    public HexEncoding() {
        //Método no implementado
    }

    public byte[] getBytes(String s) {
        if (s.length() % 2 == 0) {
            return hex2byte(s.getBytes(), 0, s.length() >> 1);
        } else {
            return new byte[0];
        }
    }

    public byte[] hex2byte(byte[] b, int offset, int len) {
        byte[] d = new byte[len];
        for (int i = 0; i < len * 2; i++) {
            int shift = i % 2 == 1 ? 0 : 4;
            d[i >> 1] |= Character.digit((char) b[offset + i], 16) << shift;
        }
        return d;
    }

    public String hexString(byte[] b) {
        StringBuilder valueHex = new StringBuilder();
        int hi = 0;
        int lo = 0;
        for (byte value : b) {
            hi = ((value >> 4) & 0x0F);
            lo = value & 0x0F;
            valueHex.append(Integer.toHexString(hi).toUpperCase()).append(Integer.toHexString(lo).toUpperCase());
        }
        return valueHex.toString();
    }

    public String hexString(byte[] b, int offset, int len) {
        StringBuilder valueHex = new StringBuilder();
        int hi = 0;
        int lo = 0;
        len += offset;
        for (int i = offset; i < len; i++) {
            hi = ((b[i] >> 4) & 0x0F);
            lo = b[i] & 0x0F;
            valueHex.append(Integer.toHexString(hi).toUpperCase()).append(Integer.toHexString(lo).toUpperCase());
        }
        return valueHex.toString();
    }

    public String hexDump(byte[] b) {
        return hexDump(b, 0, b.length);
    }

    public String hexDump(byte[] b, int offset, int len) {
        StringBuilder valueHex = new StringBuilder();
        int hi = 0;
        int lo = 0;
        len += offset;
        for (int i = offset; i < len; i++) {
            hi = ((b[i] >> 4) & 0x0F);
            lo = b[i] & 0x0F;
            valueHex.append(Integer.toHexString(hi).toUpperCase()).append(Integer.toHexString(lo).toUpperCase()).append(" ");
        }
        return valueHex.toString();
    }

    public String getString(byte[] b, int offset, int len) {
        StringBuilder d = new StringBuilder();
        for (int i = offset; i < (offset + len); i++) {
            char c = (char) b[i];
            d.append(c);
        }
        return d.toString();
    }

    public String padleft(String s, int len, char c) {
        s = s.trim();
        StringBuilder d = new StringBuilder();
        int fill = len - s.length();
        while (fill-- > 0)
            d.append(c);
        d.append(s);
        return d.toString();
    }

}
