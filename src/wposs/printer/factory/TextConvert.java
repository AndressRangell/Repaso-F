package wposs.printer.factory;

import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Typeface;
import android.text.Layout;
import android.text.StaticLayout;
import android.text.TextPaint;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import wposs.printer.data.PrintData;
import wposs.printer.data.format.TextFormat;
import wposs.printer.data.format.base.FormatType;
import wposs.printer.data.format.type.Align;
import wposs.printer.data.format.type.Bold;
import wposs.printer.data.format.type.InvertedColor;
import wposs.printer.data.format.type.Size;

/**
 * Created by Esneider on June 1, 2021
 */

public class TextConvert {

    public static final int MAXIMUM_WIDTH_PAPER = 375;

    private static final int S_SMALL = 15;
    private static final int S_MEDIUM = 23;
    private static final int S_BIG = 29;
    private static final int MAX_CHAR_SMALL = 41;
    private static final int MAX_CHAR_MEDIUM = 27;
    private static final int MAX_CHAR_BIG = 21;

    private TextConvert() {
    }

    public static List<Bitmap> dataToBitmap(PrintData section, Map<FormatType, String> mapFormat) {
        List<Bitmap> bitmaps = new ArrayList<>();
        try {
            String text = String.valueOf(section.getData());
            TextFormat format = new TextFormat(mapFormat);
            format.fillDefaultFormat();
            switch (format.getTypeText()) {
                case JUSTIFIED:
                    bitmaps.addAll(textJustified(text, format));
                    break;
                case PARAGRAPH:
                    bitmaps.addAll(textParagraph(text, format));
                    break;
                case IN_A_LINE:
                default:
                    bitmaps.add(textInALine(text, format));
                    break;
            }
            return bitmaps;
        } catch (Exception e) {
            return new ArrayList<>();
        }
    }

    private static Bitmap textInALine(String text, TextFormat format) {
        int maxChar = getMaxChar(format.getSize());
        if (maxChar > text.length()) maxChar = text.length();
        return drawText(text.substring(0, maxChar), format);
    }

    private static List<Bitmap> textJustified(String text, TextFormat format) {
        List<Bitmap> bitmaps = new ArrayList<>();
        for (String line : getJustified(text, getMaxChar(format.getSize())))
            bitmaps.add(drawText(line, format));
        return bitmaps;
    }

    private static List<Bitmap> textParagraph(String text, TextFormat format) {
        List<Bitmap> bitmaps = new ArrayList<>();
        for (String line : getParagraph(text, getMaxChar(format.getSize())))
            bitmaps.add(drawText(line, format));
        return bitmaps;
    }

    // Tools String

    public static String setTextColumn(String column1, String column2, Size size) {
        String aux = "";
        String auxText = column2;
        auxText = setRightText(auxText, getSizeInt(size));
        String auxText2 = column1;
        if (auxText2.length() < auxText.length())
            aux = auxText.substring(auxText2.length());
        auxText2 += aux;
        return auxText2;
    }

    private static String setRightText(String data, int size) {
        StringBuilder dataFinal = new StringBuilder("");
        int len1 = 0;
        switch (size) {
            case S_SMALL:
                len1 = MAX_CHAR_SMALL - data.length();
                break;
            case S_MEDIUM:
                len1 = MAX_CHAR_MEDIUM - data.length();
                break;
            case S_BIG:
                len1 = MAX_CHAR_BIG - data.length();
                break;
            default:
                break;
        }
        for (int i = 0; i < len1; i++) {
            dataFinal.append(" ");
        }
        dataFinal.append(data);
        return dataFinal.toString();
    }

    public static int getLastSize(int length, Size size) {
        int maxChar = getMaxChar(size);
        return maxChar - length;
    }

    private static ArrayList<String> getJustified(String justified, int maxChar) {
        ArrayList<String> lines = new ArrayList<>();
        while (true) {
            if (justified.length() > maxChar) {
                lines.add(justified.substring(0, maxChar));
                justified = justified.substring(maxChar);
            } else {
                lines.add(justified);
                break;
            }
        }
        return lines;
    }

    private static ArrayList<String> getParagraph(String paragraph, int maxChar) {
        String[] words = paragraph.split(" ");
        ArrayList<String> lines = new ArrayList<>();
        StringBuilder line = new StringBuilder();
        for (int i = 0; i < words.length; i++) {
            while (words[i].length() > maxChar) { // While the word exceeds the maximum character
                if (line.length() != 0) { // If the line is not empty
                    lines.add(line.toString().trim());
                    line = new StringBuilder();
                }
                lines.add(words[i].substring(0, maxChar - 1) + "-");
                words[i] = words[i].substring(maxChar - 1);
            }
            if ((line.length() + words[i].length() + 1) <= maxChar - 1) line.append(" ");
            else {
                lines.add(line.toString().trim());
                line = new StringBuilder();
            }
            line.append(words[i]);
            if (i == words.length - 1) // If it's the last word
                lines.add(line.toString().trim());
        }
        return lines;
    }

    public static int getMaxChar(Size size) {
        switch (getSizeInt(size)) {
            case S_MEDIUM:
                return MAX_CHAR_MEDIUM;
            case S_BIG:
                return MAX_CHAR_BIG;
            case S_SMALL:
            default:
                return MAX_CHAR_SMALL;
        }
    }

    private static int getSizeInt(Size size) {
        switch (size) {
            case MEDIUM:
                return S_MEDIUM;
            case BIG:
                return S_BIG;
            case SMALL:
            default:
                return S_SMALL;
        }
    }

    private static Layout.Alignment getAlignment(Align align) {
        switch (align) {
            case CENTER:
                return Layout.Alignment.ALIGN_CENTER;
            case RIGHT:
                return Layout.Alignment.ALIGN_OPPOSITE;
            case LEFT:
            default:
                return Layout.Alignment.ALIGN_NORMAL;
        }
    }

    /**
     * Returns bitmap generated according to the text format.
     *
     * @param text   Text
     * @param format Text Format
     * @return Bitmap
     */
    private static Bitmap drawText(String text, TextFormat format) {
        boolean boldText = Bold.ON == format.getBold();
        boolean invertedColor = InvertedColor.ON == format.getInvertedColor();
        float sizeText = getSizeInt(format.getSize());
        Typeface typeface = Typeface.MONOSPACE;
        Layout.Alignment alignmentText = getAlignment(format.getAlign());

        // Get text dimensions
        TextPaint textPaint = new TextPaint(Paint.ANTI_ALIAS_FLAG | Paint.LINEAR_TEXT_FLAG);
        textPaint.setColor(invertedColor ? Color.WHITE : Color.BLACK);
        textPaint.setTextSize(sizeText);
        textPaint.setTypeface(typeface);
        textPaint.setFakeBoldText(boldText);
        StaticLayout textLayout = new StaticLayout(text, textPaint, MAXIMUM_WIDTH_PAPER,
                alignmentText, 0f, 0f, false);

        // Create bitmap and canvas to draw to
        Bitmap b = Bitmap.createBitmap(MAXIMUM_WIDTH_PAPER, textLayout.getHeight(), Bitmap.Config.RGB_565);
        Canvas c = new Canvas(b);

        // Draw background
        Paint paint = new Paint(Paint.ANTI_ALIAS_FLAG | Paint.LINEAR_TEXT_FLAG);
        paint.setStyle(Paint.Style.FILL);
        paint.setColor(invertedColor ? Color.BLACK : Color.WHITE);
        paint.setTextSize(sizeText);
        c.drawPaint(paint);

        // Draw text
        c.save();
        c.translate(0f, 0f);
        textLayout.draw(c);
        c.restore();
        return b;
    }
}
