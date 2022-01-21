package wposs.printer.factory;

import static wposs.printer.factory.TextConvert.MAXIMUM_WIDTH_PAPER;

import android.graphics.Bitmap;
import android.graphics.Canvas;

import java.util.Map;

import wposs.printer.data.PrintData;
import wposs.printer.data.format.base.FormatType;
import wposs.printer.data.format.type.Align;

/**
 * Created by Esneider on June 1, 2021
 */

public class BitmapConvert {

    private BitmapConvert() {
    }

    public static Bitmap dataToBitmap(PrintData data, Map<FormatType, String> format) {
        try {
            Bitmap rescale = rescaleToMaximumSize((Bitmap) data.getData(), TextConvert.MAXIMUM_WIDTH_PAPER);
            Align align = Align.valueOf(format.get(FormatType.ALIGN));
            return alignBitmap(rescale, align, MAXIMUM_WIDTH_PAPER);
        } catch (IllegalArgumentException e) {
            e.printStackTrace();
            return null;
        }
    }

    private static Bitmap rescaleToMaximumSize(Bitmap bitmap, int maximumWidth) {
        if (bitmap == null || bitmap.getHeight() <= maximumWidth) return bitmap;
        int height = bitmap.getHeight();
        final int width = bitmap.getWidth();
        int newHeight = (height * maximumWidth) / width;
        return Bitmap.createScaledBitmap(bitmap, maximumWidth, newHeight, false);
    }

    private static Bitmap alignBitmap(Bitmap bitmap, Align align, int maximumWidth) {
        float left = 0;
        if (bitmap == null) return null;
        if (align == Align.LEFT || bitmap.getWidth() >= maximumWidth) return bitmap;
        if (align == Align.RIGHT) {
            left = (float) maximumWidth - bitmap.getWidth();
        } else if (align == Align.CENTER) {
            left = (float) ((float) ((double) maximumWidth / 2) - ((double) bitmap.getWidth() / 2));
        }
        Bitmap resultBitmap = Bitmap.createBitmap(maximumWidth, bitmap.getHeight(), Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(resultBitmap);
        canvas.drawBitmap(bitmap, left, 0f, null);
        return resultBitmap;
    }
}
