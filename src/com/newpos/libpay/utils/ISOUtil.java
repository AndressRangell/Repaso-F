/*
 * jPOS Project [http://jpos.org]
 * Copyright (C) 2000-2015 Alejandro P. Revilla
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Affero General Public License as
 * published by the Free Software Foundation, either version 3 of the
 * License, or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Affero General Public License for more details.
 *
 * You should have received a copy of the GNU Affero General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */

package com.newpos.libpay.utils;

import android.app.Activity;
import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.util.Log;
import android.widget.Toast;

import com.flota.tools.AlarmReceiver;
import com.flota.tools.RebootServiceClass;
import com.newpos.libpay.Logger;
import com.wposs.flota.R;

import org.jpos.iso.ISOMsg;

import java.io.UnsupportedEncodingException;
import java.math.BigInteger;
import java.nio.ByteBuffer;
import java.nio.charset.Charset;
import java.util.Arrays;
import java.util.BitSet;
import java.util.StringTokenizer;

import cn.desert.newpos.payui.UIUtils;

import static android.content.Context.ALARM_SERVICE;

/**
 * various functions needed to pack/unpack ISO-8583 fields
 *
 * @author apr@jpos.org
 * @author Hani S. Kirollos
 * @author Alwyn Schoeman
 * @version $Id$
 */
@SuppressWarnings("unused")
public class ISOUtil {
    /**
     * All methods in this class are static, so there's usually no need to
     * instantiate it We provide this public constructor in order to deal with
     * some legacy script integration that needs an instance of this class in a
     * rendering context.
     */
    public ISOUtil() {
        super();
    }

    public static final String[] hexStrings;
    static String clase = "ISOUtil.java";

    static {
        hexStrings = new String[256];
        for (int i = 0; i < 256; i++) {
            StringBuilder d = new StringBuilder(2);
            char ch = Character.forDigit((byte) i >> 4 & 0x0F, 16);
            d.append(Character.toUpperCase(ch));
            ch = Character.forDigit((byte) i & 0x0F, 16);
            d.append(Character.toUpperCase(ch));
            hexStrings[i] = d.toString();
        }
    }

    /**
     * Default charset for bytes transmissions over network
     */
    public static final Charset CHARSET = Charset.forName("UTF-8");

    public static final byte STX = 0x02;
    public static final byte FS = 0x1C;
    public static final byte US = 0x1F;
    public static final byte RS = 0x1D;
    public static final byte GS = 0x1E;
    public static final byte ETX = 0x03;

    /**
     * pad to the left
     *
     * @param s   - original string
     * @param len - desired len
     * @param c   - padding char
     * @return padded string
     * @throws ISOException on error
     */
    public static String padleft(String s, int len, char c) {
        s = s.trim();
        if (s.length() > len) {
            return null;
        }
        StringBuilder d = new StringBuilder(len);
        int fill = len - s.length();
        while (fill-- > 0) {
            d.append(c);
        }
        d.append(s);
        return d.toString();
    }

    /**
     * pad to the right
     *
     * @param s   - original string
     * @param len - desired len
     * @param c   - padding char
     * @return padded string
     */
    public static String padright(String s, int len, char c) {
        s = s.trim();
        StringBuilder d = new StringBuilder(len);
        int fill = len - s.length();
        d.append(s);
        while (fill-- > 0)
            d.append(c);
        return d.toString();
    }

    /**
     * trim String (if not null)
     *
     * @param s String to trim
     * @return String (may be null)
     */
    public static String trim(String s) {
        return s != null ? s.trim() : null;
    }

    /**
     * left pad with '0'
     *
     * @param s   - original string
     * @param len - desired len
     * @return zero padded string
     * @throws ISOException if string's length greater than len
     */
    public static String zeropad(String s, int len) {
        return padleft(s, len, '0');
    }

    /**
     * zeropads a long without throwing an ISOException (performs modulus
     * operation)
     *
     * @param l   the long
     * @param len the length
     * @return zeropadded value
     */
    public static String zeropad(long l, int len) {
        return padleft(Long.toString((long) (l % Math.pow(10, len))), len, '0');
    }

    /**
     * pads to the right
     *
     * @param s   - original string
     * @param len - desired len
     * @return space padded string
     */
    public static String strpad(String s, int len) {
        StringBuilder d = new StringBuilder(s);
        while (d.length() < len)
            d.append(' ');
        return d.toString();
    }

    public static String zeropadRight(String s, int len) {
        StringBuilder d = new StringBuilder(s);
        while (d.length() < len)
            d.append('0');
        return d.toString();
    }

    /**
     * converts to BCD
     *
     * @param s       - the number
     * @param padLeft - flag indicating left/right padding
     * @param d       The byte array to copy into.
     * @param offset  Where to start copying into.
     * @return BCD representation of the number
     */
    public static byte[] str2bcd(String s, boolean padLeft, byte[] d, int offset) {
        char c;
        int len = s.length();
        int start = (len & 1) == 1 && padLeft ? 1 : 0;
        for (int i = start; i < len + start; i++) {
            c = s.charAt(i - start);
            if (c >= '0' && c <= '?') // 30~3f
                c -= '0';
            else {
                c &= ~0x20;
                c -= 'A' - 10;
            }
            d[offset + (i >> 1)] |= c << ((i & 1) == 1 ? 0 : 4);
        }
        return d;
    }

    /**
     * converts to BCD
     *
     * @param s       - the number
     * @param padLeft - flag indicating left/right padding
     * @return BCD representation of the number
     */
    public static byte[] str2bcd(String s, boolean padLeft) {
        if (s == null)
            return null;
        int len = s.length();
        byte[] d = new byte[len + 1 >> 1];
        return str2bcd(s, padLeft, d, 0);
    }

    /**
     * converts to BCD
     *
     * @param s       - the number
     * @param padLeft - flag indicating left/right padding
     * @param fill    - fill value
     * @return BCD representation of the number
     */
    public static byte[] str2bcd(String s, boolean padLeft, byte fill) {
        char c;
        int len = s.length();
        byte[] d = new byte[len + 1 >> 1];
        Arrays.fill(d, fill);
        int start = (len & 1) == 1 && padLeft ? 1 : 0;
        for (int i = start; i < len + start; i++) {
            c = s.charAt(i - start);
            if (c >= '0' && c <= '?') // 30~3f
                c -= '0';
            else {
                c &= ~0x20;
                c -= 'A' - 10;
            }
            d[i >> 1] |= c << ((i & 1) == 1 ? 0 : 4);
            // d[i >> 1] |= s.charAt(i - start) - '0' << ((i & 1) == 1 ? 0 : 4);
        }
        return d;
    }

    /**
     * converts a BCD representation of a number to a String
     *
     * @param b       - BCD representation
     * @param offset  - starting offset
     * @param len     - BCD field len
     * @param padLeft - was padLeft packed?
     * @return the String representation of the number
     */
    public static String bcd2str(byte[] b, int offset, int len, boolean padLeft) {
        StringBuilder d = new StringBuilder(len);
        int start = (len & 1) == 1 && padLeft ? 1 : 0;
        for (int i = start; i < len + start; i++) {
            int shift = (i & 1) == 1 ? 0 : 4;
            char c = Character.forDigit(b[offset + (i >> 1)] >> shift & 0x0F, 16);
            if (c == 'd')
                c = '=';
            d.append(Character.toUpperCase(c));
        }
        return d.toString();
    }

    /**
     * converts a byte array to hex string (suitable for dumps and ASCII
     * packaging of Binary fields
     *
     * @param b - byte array
     * @return String representation
     */
    public static String hexString(byte[] b) {
        StringBuilder d = new StringBuilder(b.length * 2);
        for (byte aB : b) {
            d.append(hexStrings[(int) aB & 0xFF]);
        }
        return d.toString();
    }

    /**
     * converts a byte array to hex string (suitable for dumps and ASCII
     * packaging of Binary fields
     *
     * @param b      - byte array
     * @param offset - starting position
     * @param len    the length
     * @return String representation
     */
    public static String hexString(byte[] b, int offset, int len) {
        StringBuilder d = new StringBuilder(len * 2);
        len += offset;
        for (int i = offset; i < len; i++) {
            d.append(hexStrings[(int) b[i] & 0xFF]);
        }
        return d.toString();
    }

    /**
     * bit representation of a BitSet suitable for dumps and debugging
     *
     * @param b - the BitSet
     * @return string representing the bits (i.e. 011010010...)
     */
    public static String bitSet2String(BitSet b) {
        int len = b.size();
        len = len > 128 ? 128 : len;
        StringBuilder d = new StringBuilder(len);
        for (int i = 0; i < len; i++)
            d.append(b.get(i) ? '1' : '0');
        return d.toString();
    }

    /**
     * converts a BitSet into a binary field used in pack routines
     *
     * @param b - the BitSet
     * @return binary representation
     */
    public static byte[] bitSet2byte(BitSet b) {
        int len = b.length() + 62 >> 6 << 6;
        byte[] d = new byte[len >> 3];
        for (int i = 0; i < len; i++)
            if (b.get(i + 1))
                d[i >> 3] |= 0x80 >> i % 8;
        if (len > 64)
            d[0] |= 0x80;
        if (len > 128)
            d[8] |= 0x80;
        return d;
    }

    /**
     * converts a BitSet into a binary field used in pack routines
     *
     * @param b     - the BitSet
     * @param bytes - number of bytes to return
     * @return binary representation
     */
    public static byte[] bitSet2byte(BitSet b, int bytes) {
        int len = bytes * 8;
        byte[] d = new byte[bytes];
        for (int i = 0; i < len; i++)
            if (b.get(i + 1))
                d[i >> 3] |= 0x80 >> i % 8;
        // TODO: review why 2nd & 3rd bit map flags are set here???
        if (len > 64)
            d[0] |= 0x80;
        if (len > 128)
            d[8] |= 0x80;
        return d;
    }

    /*
     * Convert BitSet to int value.
     */
    public static int bitSet2Int(BitSet bs) {
        int total = 0;
        int b = bs.length() - 1;
        if (b > 0) {
            int value = (int) Math.pow(2, b);
            for (int i = 0; i <= b; i++) {
                if (bs.get(i))
                    total += value;
                value = value >> 1;
            }
        }

        return total;
    }

    /*
     * Convert int value to BitSet.
     */
    public static BitSet int2BitSet(int value) {

        return int2BitSet(value, 0);
    }

    /*
     * Convert int value to BitSet.
     */
    public static BitSet int2BitSet(int value, int offset) {

        BitSet bs = new BitSet();

        String hex = Integer.toHexString(value);
        hex2BitSet(bs, hex.getBytes(), offset);

        return bs;
    }

    /**
     * Converts a binary representation of a Bitmap field into a Java BitSet
     *
     * @param b                    - binary representation
     * @param offset               - staring offset
     * @param bitZeroMeansExtended - true for ISO-8583
     * @return java BitSet object
     */
    public static BitSet byte2BitSet(byte[] b, int offset,
                                     boolean bitZeroMeansExtended) {
        int len = bitZeroMeansExtended ? (b[offset] & 0x80) == 0x80 ? 128 : 64
                : 64;
        BitSet bmap = new BitSet(len);
        for (int i = 0; i < len; i++)
            if ((0xff & b[offset + (i >> 3)] & 0x80 >> i % 8) > 0)
                bmap.set(i + 1);
        return bmap;
    }

    /**
     * Converts a binary representation of a Bitmap field into a Java BitSet
     *
     * @param b       - binary representation
     * @param offset  - staring offset
     * @param maxBits - max number of bits (supports 64,128 or 192)
     * @return java BitSet object
     */
    public static BitSet byte2BitSet(byte[] b, int offset, int maxBits) {
        int len = maxBits > 64 ? (b[offset] & 0x80) == 0x80 ? 128 : 64
                : maxBits;

        if (maxBits > 128 && b.length > offset + 8
                && (b[offset + 8] & 0x80) == 0x80) {
            len = 192;
        }
        BitSet bmap = new BitSet(len);
        for (int i = 0; i < len; i++)
            if ((0xff & b[offset + (i >> 3)] & 0x80 >> i % 8) > 0)
                bmap.set(i + 1);
        return bmap;
    }

    /**
     * Converts a binary representation of a Bitmap field into a Java BitSet
     *
     * @param bmap      - BitSet
     * @param b         - hex representation
     * @param bitOffset - (i.e. 0 for primary bitmap, 64 for secondary)
     * @return java BitSet object
     */
    public static BitSet byte2BitSet(BitSet bmap, byte[] b, int bitOffset) {
        int len = b.length << 3;
        for (int i = 0; i < len; i++)
            if ((0xff & b[i >> 3] & 0x80 >> i % 8) > 0)
                bmap.set(bitOffset + i + 1);
        return bmap;
    }

    /**
     * Converts an ASCII representation of a Bitmap field into a Java BitSet
     *
     * @param b                    - hex representation
     * @param offset               - starting offset
     * @param bitZeroMeansExtended - true for ISO-8583
     * @return java BitSet object
     */
    public static BitSet hex2BitSet(byte[] b, int offset,
                                    boolean bitZeroMeansExtended) {
        int len = bitZeroMeansExtended ? (Character.digit((char) b[offset], 16) & 0x08) == 8 ? 128
                : 64
                : 64;
        BitSet bmap = new BitSet(len);
        for (int i = 0; i < len; i++) {
            int digit = Character.digit((char) b[offset + (i >> 2)], 16);
            if ((digit & 0x08 >> i % 4) > 0)
                bmap.set(i + 1);
        }
        return bmap;
    }

    /**
     * Converts an ASCII representation of a Bitmap field into a Java BitSet
     *
     * @param b       - hex representation
     * @param offset  - starting offset
     * @param maxBits - max number of bits (supports 8, 16, 24, 32, 48, 52, 64,..
     *                128 or 192)
     * @return java BitSet object
     */
    public static BitSet hex2BitSet(byte[] b, int offset, int maxBits) {
        int len = maxBits > 64 ? (Character.digit((char) b[offset], 16) & 0x08) == 8 ? 128
                : 64
                : maxBits;
        if (len > 64 && maxBits > 128 && b.length > offset + 16
                && (Character.digit((char) b[offset + 16], 16) & 0x08) == 8) {
            len = 192;
        }
        BitSet bmap = new BitSet(len);
        for (int i = 0; i < len; i++) {
            int digit = Character.digit((char) b[offset + (i >> 2)], 16);
            if ((digit & 0x08 >> i % 4) > 0) {
                bmap.set(i + 1);
                if (i == 65 && maxBits > 128)
                    len = 192;
            }
        }
        return bmap;
    }

    /**
     * Converts an ASCII representation of a Bitmap field into a Java BitSet
     *
     * @param bmap      - BitSet
     * @param b         - hex representation
     * @param bitOffset - (i.e. 0 for primary bitmap, 64 for secondary)
     * @return java BitSet object
     */
    public static BitSet hex2BitSet(BitSet bmap, byte[] b, int bitOffset) {
        int len = b.length << 2;
        for (int i = 0; i < len; i++) {
            int digit = Character.digit((char) b[i >> 2], 16);
            if ((digit & 0x08 >> i % 4) > 0)
                bmap.set(bitOffset + i + 1);
        }
        return bmap;
    }

    /**
     * @param b      source byte array
     * @param offset starting offset
     * @param len    number of bytes in destination (processes len*2)
     * @return byte[len]
     */
    public static byte[] hex2byte(byte[] b, int offset, int len) {
        byte[] d = new byte[len];
        for (int i = 0; i < len * 2; i++) {
            // Buginfo when i oddness then this line won't be work
            // but in the for judge i>0 & i++ so i absolutely won't be oddness
            int shift = ((i % 2 == 1) ? 0 : 4);
            d[i >> 1] |= Character.digit((char) b[offset + i], 16) << shift;
        }
        return d;
    }

    /**
     * Converts a hex string into a byte array
     *
     * @param s source string (with Hex representation)
     * @return byte array
     */
    public static byte[] hex2byte(String s) {
        if (s.length() % 2 == 0) {
            return hex2byte(s.getBytes(), 0, s.length() >> 1);
        } else {
            // Padding left zero to make it even size #Bug raised by tommy
            return hex2byte("0" + s);
        }
    }

    /**
     * Converts a byte array into a hex string
     *
     * @param bs source byte array
     * @return hexadecimal representation of bytes
     */
    public static String byte2hex(byte[] bs) {
        return byte2hex(bs, 0, bs.length);
    }

    /**
     * Converts an integer into a byte array of hex
     *
     * @param value
     * @return bytes representation of integer
     */
    public static byte[] int2byte(int value) {
        if (value < 0) {
            return new byte[]{(byte) (value >>> 24 & 0xFF),
                    (byte) (value >>> 16 & 0xFF), (byte) (value >>> 8 & 0xFF),
                    (byte) (value & 0xFF)};
        } else if (value <= 0xFF) {
            return new byte[]{(byte) (value & 0xFF)};
        } else if (value <= 0xFFFF) {
            return new byte[]{(byte) (value >>> 8 & 0xFF),
                    (byte) (value & 0xFF)};
        } else if (value <= 0xFFFFFF) {
            return new byte[]{(byte) (value >>> 16 & 0xFF),
                    (byte) (value >>> 8 & 0xFF), (byte) (value & 0xFF)};
        } else {
            return new byte[]{(byte) (value >>> 24 & 0xFF),
                    (byte) (value >>> 16 & 0xFF), (byte) (value >>> 8 & 0xFF),
                    (byte) (value & 0xFF)};
        }
    }

    /**
     * Converts a byte array of hex into an integer
     *
     * @param bytes
     * @return integer representation of bytes
     */
    public static int byte2int(byte[] bytes) {
        if (bytes == null || bytes.length == 0) {
            return 0;
        }
        ByteBuffer byteBuffer = ByteBuffer.allocate(4);
        for (int i = 0; i < 4 - bytes.length; i++) {
            byteBuffer.put((byte) 0);
        }
        for (int i = 0; i < bytes.length; i++) {
            byteBuffer.put(bytes[i]);
        }
        byteBuffer.position(0);
        return byteBuffer.getInt();
    }

    /**
     * Converts a byte array into a string of lower case hex chars.
     *
     * @param bs     A byte array
     * @param off    The index of the first byte to read
     * @param length The number of bytes to read.
     * @return the string of hex chars.
     */
    public static String byte2hex(byte[] bs, int off, int length) {
        if (bs.length <= off || bs.length < off + length)
            throw new IllegalArgumentException();
        StringBuilder sb = new StringBuilder(length * 2);
        byte2hexAppend(bs, off, length, sb);
        return sb.toString().toUpperCase();
    }

    private static void byte2hexAppend(byte[] bs, int off, int length,
                                       StringBuilder sb) {
        if (bs.length <= off || bs.length < off + length)
            throw new IllegalArgumentException();
        sb.ensureCapacity(sb.length() + length * 2);
        for (int i = off; i < off + length; i++) {
            sb.append(Character.forDigit(bs[i] >>> 4 & 0xf, 16));
            sb.append(Character.forDigit(bs[i] & 0xf, 16));
        }
    }

    /**
     * prepare long value used as amount for display (implicit 2 decimals)
     *
     * @param l   value
     * @param len display len
     * @return formated field
     * @throws ISOException
     */
    public static String formatAmount(long l, int len) throws ISOException {
        String buf = Long.toString(l);
        if (l < 100)
            buf = zeropad(buf, 3);
        if (buf != null) {
            StringBuilder s = new StringBuilder(padleft(buf, len - 1, ' '));
            s.insert(len - 3, '.');
            return s.toString();
        }
        return null;
    }

    /**
     * XML normalizer
     *
     * @param s         source String
     * @param canonical true if we want to normalize \r and \n as well
     * @return normalized string suitable for XML Output
     */
    public static String normalize(String s, boolean canonical) {
        StringBuilder str = new StringBuilder();

        int len = s != null ? s.length() : 0;
        for (int i = 0; i < len; i++) {
            char ch = s.charAt(i);
            switch (ch) {
                case '<':
                    str.append("&lt;");
                    break;
                case '>':
                    str.append("&gt;");
                    break;
                case '&':
                    str.append("&amp;");
                    break;
                case '"':
                    str.append("&quot;");
                    break;
                case '\r':
                case '\n':
                    if (canonical) {
                        str.append("&#");
                        str.append(Integer.toString(ch & 0xFF));
                        str.append(';');
                        break;
                    }
                    // else, default append char
                default:
                    if (ch < 0x20) {
                        str.append("&#");
                        str.append(Integer.toString(ch & 0xFF));
                        str.append(';');
                    } else if (ch > 0xff00) {
                        str.append((char) (ch & 0xFF));
                    } else
                        str.append(ch);
            }
        }
        return str.toString();
    }

    /**
     * XML normalizer (default canonical)
     *
     * @param s source String
     * @return normalized string suitable for XML Output
     */
    public static String normalize(String s) {
        return normalize(s, true);
    }

    public static int[] toIntArray(String s) {
        StringTokenizer st = new StringTokenizer(s);
        int[] array = new int[st.countTokens()];
        for (int i = 0; st.hasMoreTokens(); i++)
            array[i] = Integer.parseInt(st.nextToken());
        return array;
    }

    public static String[] toStringArray(String s) {
        StringTokenizer st = new StringTokenizer(s);
        String[] array = new String[st.countTokens()];
        for (int i = 0; st.hasMoreTokens(); i++)
            array[i] = st.nextToken();
        return array;
    }

    /**
     * Bitwise XOR between corresponding bytes
     *
     * @param op1 byteArray1
     * @param op2 byteArray2
     * @return an array of length = the smallest between op1 and op2
     */
    public static byte[] xor(byte[] op1, byte[] op2) {
        byte[] result;
        // Use the smallest array
        int length;
        if (op2.length > op1.length) {
            result = new byte[op2.length];
            length = op1.length;
            System.arraycopy(op2, 0, result, 0, op2.length);
        } else {
            result = new byte[op1.length];
            length = op2.length;
            System.arraycopy(op1, 0, result, 0, op1.length);
        }

        for (int i = 0; i < length; i++) {
            result[i] = (byte) (op1[i] ^ op2[i]);
        }

        return result;
    }

    /**
     * Bitwise XOR between corresponding byte arrays represented in hex
     *
     * @param op1 hexstring 1
     * @param op2 hexstring 2
     * @return an array of length = the smallest between op1 and op2
     */
    public static String hexor(String op1, String op2) {
        byte[] xor = xor(hex2byte(op1), hex2byte(op2));
        return hexString(xor);
    }

    /**
     * Trims a byte[] to a certain length
     *
     * @param array  the byte[] to be trimmed
     * @param length the wanted length
     * @return the trimmed byte[]
     */
    public static byte[] trim(byte[] array, int length) {
        byte[] trimmedArray = new byte[length];
        System.arraycopy(array, 0, trimmedArray, 0, length);
        return trimmedArray;
    }

    /**
     * Concatenates two byte arrays (array1 and array2)
     *
     * @param array1 first part
     * @param array2 last part
     * @return the concatenated array
     */
    public static byte[] concat(byte[] array1, byte[] array2) {
        byte[] concatArray = new byte[array1.length + array2.length];
        System.arraycopy(array1, 0, concatArray, 0, array1.length);
        System.arraycopy(array2, 0, concatArray, array1.length, array2.length);
        return concatArray;
    }

    /**
     * Concatenates two byte arrays (array1 and array2)
     *
     * @param array1      first part
     * @param beginIndex1 initial index
     * @param length1     length
     * @param array2      last part
     * @param beginIndex2 last part index
     * @param length2     last part length
     * @return the concatenated array
     */
    public static byte[] concat(byte[] array1, int beginIndex1, int length1,
                                byte[] array2, int beginIndex2, int length2) {
        byte[] concatArray = new byte[length1 + length2];
        System.arraycopy(array1, beginIndex1, concatArray, 0, length1);
        System.arraycopy(array2, beginIndex2, concatArray, length1, length2);
        return concatArray;
    }

    /**
     * Left unPad with '0' ��璇蹭���锟� * @param s - original string
     *
     * @return zero unPadded string
     */
    public static String zeroUnPad(String s) {
        return unPadLeft(s, '0');
    }

    /**
     * Right unPad with ' ' ���も����锟� * @param s - original string
     *
     * @return blank unPadded string
     */
    public static String blankUnPad(String s) {
        return unPadRight(s, ' ');
    }

    /**
     * Unpad from right. ��璇插礁char
     *
     * @param s - original string
     * @param c - padding char
     * @return unPadded string.
     */
    public static String unPadRight(String s, char c) {
        int end = s.length();
        if (end == 0)
            return s;
        while (0 < end && s.charAt(end - 1) == c)
            end--;
        return 0 < end ? s.substring(0, end) : s.substring(0, 1);
    }

    /**
     * Unpad from left. ��璇蹭�char
     *
     * @param s - original string
     * @param c - padding char
     * @return unPadded string.
     */
    public static String unPadLeft(String s, char c) {
        int fill = 0, end = s.length();
        if (end == 0)
            return s;
        while (fill < end && s.charAt(fill) == c)
            fill++;
        return fill < end ? s.substring(fill, end) : s.substring(fill - 1, end);
    }

    /**
     * @return true if the string is zero-filled ( 0 char filled )
     **/
    public static boolean isZero(String s) {
        int i = 0, len = s.length();
        while (i < len && s.charAt(i) == '0') {
            i++;
        }
        return i >= len;
    }

    /**
     * @return true if the string is blank filled (space char filled)
     */
    public static boolean isBlank(String s) {
        return s.trim().length() == 0;
    }

    /**
     * Return true if the string is alphanum.
     * <code>{letter digit (.) (_) (-) ( ) (?) }</code> A-Z . _ - ?
     **/
    public static boolean isAlphaNumeric(String s) {
        int i = 0, len = s.length();
        while (i < len
                && (Character.isLetterOrDigit(s.charAt(i))
                || s.charAt(i) == ' ' || s.charAt(i) == '.'
                || s.charAt(i) == '-' || s.charAt(i) == '_')
                || s.charAt(i) == '?') {
            i++;
        }
        return i >= len;
    }

    /**
     * Return true if the string represent a number in the specified radix. <br>
     * <br>
     * 1-9
     **/
    public static boolean isNumeric(String s, int radix) {
        int i = 0, len = s.length();
        while (i < len && Character.digit(s.charAt(i), radix) > -1) {
            i++;
        }
        return i >= len && len > 0;
    }

    /**
     * Converts a BitSet into an extended binary field used in pack routines.
     * The result is always in the extended format: (16 bytes of length) <br>
     * <br>
     *
     * @param b the BitSet
     * @return binary representation 128�╋拷
     */
    public static byte[] bitSet2extendedByte(BitSet b) {
        int len = 128;
        byte[] d = new byte[len >> 3];
        for (int i = 0; i < len; i++)
            if (b.get(i + 1))
                d[i >> 3] |= 0x80 >> i % 8;
        d[0] |= 0x80;
        return d;
    }

    /**
     * Converts a String to an integer of base radix. <br>
     * <br>
     * String constraints are: <li>Number must be less than 10 digits</li> <li>
     * Number must be positive</li>
     *
     * @param s     String representation of number
     * @param radix Number base to use
     * @return integer value of number
     * @throws NumberFormatException
     */
    public static int parseInt(String s, int radix)
            throws NumberFormatException {
        int length = s.length();
        if (length > 9)
            throw new NumberFormatException("Number can have maximum 9 digits");
        int result;
        int index = 0;
        int digit = Character.digit(s.charAt(index++), radix);
        if (digit == -1)
            throw new NumberFormatException("String contains non-digit");
        result = digit;
        while (index < length) {
            result *= radix;
            digit = Character.digit(s.charAt(index++), radix);
            if (digit == -1)
                throw new NumberFormatException("String contains non-digit");
            result += digit;
        }
        return result;
    }

    /**
     * Converts a String to an integer of radix 10. <br>
     * <br>
     * String constraints are: <li>Number must be less than 10 digits</li> <li>
     * Number must be positive</li>
     *
     * @param s String representation of number
     * @return integer value of number
     * @throws NumberFormatException
     */
    public static int parseInt(String s) throws NumberFormatException {
        return parseInt(s, 10);
    }

    /**
     * Converts a character array to an integer of base radix. <br>
     * <br>
     * Array constraints are: <li>Number must be less than 10 digits</li> <li>
     * Number must be positive</li>
     *
     * @param cArray Character Array representation of number
     * @param radix  Number base to use
     * @return integer value of number
     * @throws NumberFormatException
     */
    public static int parseInt(char[] cArray, int radix)
            throws NumberFormatException {
        int length = cArray.length;
        if (length > 9)
            throw new NumberFormatException("Number can have maximum 9 digits");
        int result;
        int index = 0;
        int digit = Character.digit(cArray[index++], radix);
        if (digit == -1)
            throw new NumberFormatException("Char array contains non-digit");
        result = digit;
        while (index < length) {
            result *= radix;
            digit = Character.digit(cArray[index++], radix);
            if (digit == -1)
                throw new NumberFormatException("Char array contains non-digit");
            result += digit;
        }
        return result;
    }

    /**
     * Converts a character array to an integer of radix 10. <br>
     * <br>
     * Array constraints are: <li>Number must be less than 10 digits</li> <li>
     * Number must be positive</li>
     *
     * @param cArray Character Array representation of number
     * @return integer value of number
     * @throws NumberFormatException
     */
    public static int parseInt(char[] cArray) throws NumberFormatException {
        return parseInt(cArray, 10);
    }

    /**
     * Converts a byte array to an integer of base radix. <br>
     * <br>
     * Array constraints are: <li>Number must be less than 10 digits</li> <li>
     * Number must be positive</li>
     *
     * @param bArray Byte Array representation of number
     * @param radix  Number base to use
     * @return integer value of number
     * @throws NumberFormatException
     */
    public static int parseInt(byte[] bArray, int radix)
            throws NumberFormatException {
        int length = bArray.length;
        if (length > 9)
            throw new NumberFormatException("Number can have maximum 9 digits");
        int result;
        int index = 0;
        int digit = Character.digit((char) bArray[index++], radix);
        if (digit == -1)
            throw new NumberFormatException("Byte array contains non-digit");
        result = digit;
        while (index < length) {
            result *= radix;
            digit = Character.digit((char) bArray[index++], radix);
            if (digit == -1)
                throw new NumberFormatException("Byte array contains non-digit");
            result += digit;
        }
        return result;
    }

    /**
     * Converts a byte array to an integer of radix 10. <br>
     * <br>
     * Array constraints are: <li>Number must be less than 10 digits</li> <li>
     * Number must be positive</li>
     *
     * @param bArray Byte Array representation of number
     * @return integer value of number
     * @throws NumberFormatException
     */
    public static int parseInt(byte[] bArray) throws NumberFormatException {
        return parseInt(bArray, 10);
    }

    private static String hexOffset(int i) {
        i = i >> 4 << 4;
        int w = i > 0xFFFF ? 8 : 4;
        return zeropad(Integer.toString(i, 16), w);
    }

    /**
     * @param b a byte[] buffer
     * @return hexdump
     */
    public static String hexdump(byte[] b) {
        return hexdump(b, 0, b.length);
    }

    /**
     * @param b      a byte[] buffer
     * @param offset starting offset
     */
    public static String hexdump(byte[] b, int offset) {
        return hexdump(b, offset, b.length - offset);
    }

    /**
     * @param b      a byte[] buffer
     * @param offset starting offset
     * @param len    the Length
     * @return hexdump
     */
    public static String hexdump(byte[] b, int offset, int len) {
        StringBuilder sb = new StringBuilder();
        StringBuilder hex = new StringBuilder();
        StringBuilder ascii = new StringBuilder();
        String sep = "  ";
        String lineSep = System.getProperty("line.separator");
        len = offset + len;

        for (int i = offset; i < len; i++) {
            hex.append(hexStrings[(int) b[i] & 0xFF]);
            hex.append(' ');
            char c = (char) b[i];
            ascii.append(c >= 32 && c < 127 ? c : '.');

            int j = i % 16;
            switch (j) {
                case 7:
                    hex.append(' ');
                    break;
                case 15:
                    sb.append(hexOffset(i));
                    sb.append(sep);
                    sb.append(hex.toString());
                    sb.append(' ');
                    sb.append(ascii.toString());
                    sb.append(lineSep);
                    hex = new StringBuilder();
                    ascii = new StringBuilder();
                    break;
            }
        }
        if (hex.length() > 0) {
            while (hex.length() < 49)
                hex.append(' ');

            sb.append(hexOffset(len));
            sb.append(sep);
            sb.append(hex.toString());
            sb.append(' ');
            sb.append(ascii.toString());
            sb.append(lineSep);
        }
        return sb.toString();
    }

    /**
     * pads a string with 'F's (useful for pinoffset management)
     *
     * @param s   an [hex]string
     * @param len desired length
     * @return string right padded with 'F's
     */
    public static String strpadf(String s, int len) {
        StringBuilder d = new StringBuilder(s);
        while (d.length() < len)
            d.append('F');
        return d.toString();
    }

    /**
     * reverse the effect of strpadf ��缁�甯���搴ㄦ桨�ㄥ��
     *
     * @param s F padded string
     * @return trimmed string
     */
    public static String trimf(String s) {
        if (s != null) {
            int l = s.length();
            if (l > 0) {
                while (--l >= 0) {
                    if (s.charAt(l) != 'F' && s.charAt(l) != 'f')
                        break;
                }
                s = l == 0 ? "" : s.substring(0, l + 1);
            }
        }
        return s;
    }

    /**
     * return the last n characters of the passed String, left padding where
     * required with 0
     *
     * @param s String to take from
     * @param n nuber of characters to take
     * @return String (may be null)
     */
    public static String takeLastN(String s, int n) throws ISOException {
        if (s.length() > n) {
            return s.substring(s.length() - n);
        } else {
            if (s.length() < n) {
                return zeropad(s, n);
            } else {
                return s;
            }
        }
    }

    /**
     * return the first n characters of the passed String, left padding where
     * required with 0
     *
     * @param s String to take from
     * @param n nuber of characters to take
     * @return String (may be null)
     */
    public static String takeFirstN(String s, int n) throws ISOException {
        if (s.length() > n) {
            return s.substring(0, n);
        } else {
            if (s.length() < n) {
                return zeropad(s, n);
            } else {
                return s;
            }
        }
    }

    public static String millisToString(long millis) {
        StringBuilder sb = new StringBuilder();
        if (millis < 0) {
            millis = -millis;
            sb.append('-');
        }
        int ms = (int) (millis % 1000);
        millis /= 1000;
        int dd = (int) (millis / 86400);
        millis -= dd * 86400;
        int hh = (int) (millis / 3600);
        millis -= hh * 3600;
        int mm = (int) (millis / 60);
        millis -= mm * 60;
        int ss = (int) millis;
        if (dd > 0) {
            sb.append(Long.toString(dd));
            sb.append("d ");
        }
        sb.append(zeropad(hh, 2));
        sb.append(':');
        sb.append(zeropad(mm, 2));
        sb.append(':');
        sb.append(zeropad(ss, 2));
        sb.append('.');
        sb.append(zeropad(ms, 3));
        return sb.toString();
    }

    /**
     * int convert to bcd
     *
     * @param data int
     * @param len  len
     * @return
     */
    public static byte[] int2bcd(int data, int len) {
        byte[] bb = null;
        if (len == 1) {
            data = data % 100;
            bb = new byte[1];
            bb[0] = (byte) (((data / 10) << 4) + (data % 10));
            return bb;
        } else if (len == 2) {
            bb = new byte[2];
            bb[0] = (byte) (data / 100);

            bb[1] = (byte) ((((data / 10) % 10) << 4) + (data % 10));
            return bb;
        } else
            return null;
    }

    /**
     * byte2Int by offset
     *
     * @param bb
     * @param offset
     * @param len
     * @return
     */
    public static int byte2int(byte[] bb, int offset, int len) {
        byte[] temp = new byte[len];
        System.arraycopy(bb, offset, temp, 0, len);
        return byte2int(temp);
    }

    /**
     * 杩���BCD��缂╃��int��
     *
     * @param bb
     * @return
     */
    public static int bcd2int(byte bb) {
        return ((bb >> 4) & 0x0F) * 10 + (bb & 0x0F);
    }

    /**
     * 杩���澶�涓�瀛�����缂╃��BCD��int
     *
     * @param bb
     * @param len
     * @return
     */
    public static int bcd2int(byte[] bb, int offset, int len) {
        int result = 0;
        for (int i = 0; i < len; i++) {
            result = result * 100 + bcd2int(bb[offset + i]);
        }
        return result;
    }

    /**
     *
     */
    public static boolean memcmp(byte[] b1, int offset1, byte[] b2,
                                 int offset2, int len) {
        for (int i = 0; i < len; i++) {
            if (b1[offset1 + i] != b2[offset2 + i])
                return false;
        }
        return true;
    }

    /**
     * 按字符长度分割字符串
     *
     * @param str 字符串
     * @param len 长度
     * @return 返回分割过后的数组 如果str长度小于len 或者 str长度不能整除 len 返回空
     */
    public static String[] subStrByLen(String str, int len) {
        if (str.length() < len || str.length() % len != 0)
            return null;

        String[] sb = new String[str.length() / len];
        for (int i = 0; i < str.length() / len; i++) {
            sb[i] = (str.substring(i * len, (i + 1) * len)).trim();
        }
        return sb;
    }

    /**
     * hex数组转换成ASCII字符串
     *
     * @param hex
     * @return
     */
    public static String hex2AsciiStr(String hex) {
        StringBuilder sb = new StringBuilder();
        StringBuilder temp = new StringBuilder();
        for (int i = 0; i < hex.length() - 1; i += 2) {
            String output = hex.substring(i, (i + 2));
            int decimal = Integer.parseInt(output, 16);
            sb.append((char) decimal);
            temp.append(decimal);
        }

        return sb.toString();
    }

    /**
     * @param hex
     * @return
     */
    public static String hex2AsciiStr(String hex, boolean isIP) {
        StringBuilder temp = new StringBuilder();
        for (int i = 0; i < hex.length() - 1; i += 2) {
            //grab the hex in pairs
            String output = hex.substring(i, (i + 2));
            //convert hex to decimal
            int decimal = Integer.parseInt(output, 16);
            temp.append(decimal);

            if (isIP)
                if (i < 6)
                    temp.append(".");
        }

        return temp.toString();
    }


    private static String checkRange(int octDec) {
        String ret = "";
        if (octDec >= 0 && octDec <= 15) {
            ret = "0" + Integer.toHexString(octDec);
        } else {
            ret = Integer.toHexString(octDec);
        }
        return ret;
    }

    /**
     * @param value
     * @param character
     * @return
     */
    public static String intToHex(String value, String character) {
        StringBuilder temp = new StringBuilder();
        int cont = 0;
        int pos = 0;
        int octDec = 0;
        String oct;
        String output = "";
        while (value.indexOf(character) > -1) {
            cont = value.indexOf(character);
            output = value.substring(pos, cont);
            octDec = Integer.parseInt(output);
            temp.append(checkRange(octDec));
            pos += cont;
            pos++;//incrementa el '.'
            value = value.substring(pos);
            pos = 0;
        }
        octDec = Integer.parseInt(value);
        temp.append(checkRange(octDec));//Se toma el ultimo octeto

        return temp.toString();
    }

    /**
     * ASCII字符串转hex数组
     *
     * @param str
     * @return
     */
    public static String convertStringToHex(String str) {
        char[] chars = str.toCharArray();
        StringBuffer hex = new StringBuffer();
        for (int i = 0; i < chars.length; i++) {
            hex.append(Integer.toHexString((int) chars[i]));
        }
        return hex.toString();
    }

    /**
     * convierte una representacion BCD de un numero a una cadena
     *
     * @param source - representacion BCD
     * @param offset - desplazamiento inicial
     * @param length - tama�o de los Bytes a convertir
     * @retorna una cadena con la representacion del numero
     */
    public static String bcd2str(byte[] source, int offset, int length) {
        char[] ret = new char[length * 2];
        byte car;

        int counter;
        int indexString = 0;

        for (counter = offset; counter < length + offset; counter++) {
            car = (byte) ((source[counter] & 0xF0) >> 4);
            ret[indexString] = (char) (car + ((car < 10) ? '0' : '7'));
            indexString++;
            car = (byte) (source[counter] & 0x0F);
            ret[indexString] = (char) (car + ((car < 10) ? '0' : '7'));
            indexString++;
        }

        return new String(ret);
    }

    public static String stringToAscii(String ascii) {
        byte[] bytes = new byte[0];
        try {
            bytes = ascii.getBytes("US-ASCII");
        } catch (UnsupportedEncodingException e) {
              Logger.exception(clase, e);
            Logger.error("Exception" + e);
        }
        return bcd2str(bytes, 0, bytes.length);

    }

    public static String decimalFormat(String s) {

        String str = s;
        String cuttedStr = str;
        for (int i = str.length() - 1; i >= 0; i--) {
            char c = str.charAt(i);
            if ('.' == c) {
                cuttedStr = str.substring(0, i) + str.substring(i + 1);
                break;
            }
        }
        int NUM = cuttedStr.length();
        int zeroIndex = -1;
        for (int i = 0; i < NUM - 2; i++) {
            char c = cuttedStr.charAt(i);
            if (c != '0') {
                zeroIndex = i;
                break;
            } else if (i == NUM - 3) {
                zeroIndex = i;
                break;
            }
        }
        if (zeroIndex != -1) {
            cuttedStr = cuttedStr.substring(zeroIndex);
        }
        if (cuttedStr.length() < 3) {
            cuttedStr = "0" + cuttedStr;
        }
        cuttedStr = cuttedStr.substring(0, cuttedStr.length() - 2)
                + "." + cuttedStr.substring(cuttedStr.length() - 2);

        return cuttedStr;

    }

    public static boolean checkNull(String strText) {
        boolean rta = false;
        if (strText != null)
            rta = true;
        return rta;
    }

    /**
     * @param value
     * @return
     */
    public static boolean stringToBoolean(String value) {
        if (value != null) {
            if (value.equals("true") || value.equals("1") || value.equals("S"))
                return true;
        }
        return false;
    }

    /**
     * Este metodo se realiza dadas las inconsistencias en la Plataforma Polaris Cloud.
     *
     * @param value
     * @return :
     * 1 : true
     * 0 : false
     */
    public static boolean validarBooleanPolaris(String value, String mensajeError, Context context) {

        if (value != null) {
            switch (value.toLowerCase()) {
                case "true":
                case "1":
                case "s":
                    return true;

                case "false":
                case "0":
                case "n":
                    return false;
            }
        }
        showMensaje(" Error validando " + mensajeError, context);
        return false;
    }

    public static void showMensaje(String mensaje, Context context) {
        if (context != null)
            UIUtils.toast((Activity) context, R.drawable.ic_redinfonet, mensaje, Toast.LENGTH_LONG);
        else
            Logger.debug("Info", "showMensaje: " + "context == null");
    }

    public static String asciiToHex(String ascii) {
        // Step-1 - Convert ASCII string to char array
        char[] ch = ascii.toCharArray();

        // Step-2 Iterate over char array and cast each element to Integer.
        StringBuilder builder = new StringBuilder();

        for (char c : ch) {
            int i = (int) c;
            // Step-3 Convert integer value to hex using toHexString() method.
            builder.append(Integer.toHexString(i).toUpperCase());
        }

        return builder.toString();
    }

    public static String toHex(String arg) {
        return String.format("%x", new BigInteger(1, arg.getBytes(/*YOUR_CHARSET?*/)));
    }

    public static String logISOMsg(ISOMsg msg) {
        String cat = "";
        Logger.info("ISOMsg", "----ISO MESSAGE-----");
        try {
            Logger.info("ISOMsg", "  MTI : " + msg.getMTI());
            cat = msg.getMTI();
            for (int i = 1; i <= msg.getMaxField(); i++) {
                if (msg.hasField(i)) {
                    cat = cat + "|" + msg.getString(i);
                    Logger.info("ISOMsg", "    Field-" + i + " : " + msg.getString(i));
                }
            }
        } catch (org.jpos.iso.ISOException e) {
            Logger.exception(clase, e);
            e.printStackTrace();
        } finally {
            Logger.info("ISOMsg", "--------------------");
        }
        return cat;
    }

    /**
     * Convert bcd to int
     *
     * @param buffer
     * @return
     */
    public static int bcdToInt(byte[] buffer) {
        return ((buffer[1] & 0x0F) + (((buffer[1] & 0xF0) >> 4) * 16) + ((buffer[0] & 0x0F) * 16 * 16) + (((buffer[0] & 0xF0) >> 4) * 16 * 16 * 16));
    }

    public static String ofuscarPAN(String pan) {
        int leng = pan.length();
        String firstPart = pan.substring(0, 4);
        String lastPart = pan.substring(leng - 4);
        return firstPart +
                "********" +
                lastPart;
    }

    public static void AlertExcepciones(String mensaje, Context context) {
        if (context != null)
            UIUtils.toast((Activity) context, R.drawable.ic_redinfonet, mensaje, Toast.LENGTH_LONG);
        else
            Logger.debug("Info", "showMensaje: " + "context == null");
    }

    public static void setAlarm(int i, Long timestamp, Context ctx) {
        AlarmManager alarmManager = (AlarmManager) ctx.getSystemService(ALARM_SERVICE);
        Intent alarmIntent = new Intent(ctx, AlarmReceiver.class);
        PendingIntent pendingIntent = PendingIntent.getBroadcast(ctx, i, alarmIntent, PendingIntent.FLAG_ONE_SHOT);
        alarmIntent.setData((Uri.parse("custom://" + System.currentTimeMillis())));
        if (alarmManager != null) {
            alarmManager.setExact(AlarmManager.RTC_WAKEUP, timestamp, pendingIntent);
            RebootServiceClass.setAlarma(true);
        }

    }

    public static int hex2int(byte[] bb,int offset)
    {
        int result = 0;
        int lenField;
        int lenField1;

        lenField=bb[offset];
        lenField1=bb[offset+1];

        if(lenField<0)
        {
            lenField+=256;
        }
        if(lenField1<0)
        {
            lenField1+=256;
        }
        result = lenField*256;
        result+=lenField1;
        return result;
    }
}
