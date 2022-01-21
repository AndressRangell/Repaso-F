package wposs.printer.utils;

import android.graphics.Bitmap;
import android.graphics.Canvas;

import java.util.List;

/**
 * Created by Esneider on June 1, 2021
 */

public class BitmapUtils {

    private BitmapUtils() {
    }

    public static Bitmap setBorderBitmap(Bitmap bitmap, int border) {
        if (bitmap != null) {
            Bitmap resultBitmap = Bitmap.createBitmap(bitmap.getWidth() + (border * 2),
                    bitmap.getHeight() + (border * 2), Bitmap.Config.ARGB_8888);
            Canvas canvas = new Canvas(resultBitmap);
            canvas.drawBitmap(bitmap, border, border, null);

            return resultBitmap;
        }
        return null;
    }

    public static Bitmap centerBitmap(Bitmap bitmap, int width, int height) {
        if (bitmap != null) {
            float left = (float) (((double) width / 2) - ((double) bitmap.getWidth() / 2));
            float top = (float) (((double) height / 2) - ((double) bitmap.getHeight() / 2));
            Bitmap resultBitmap = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888);
            Canvas canvas = new Canvas(resultBitmap);
            canvas.drawBitmap(bitmap, left, top, null);
            return resultBitmap;
        }
        return null;
    }

    public static Bitmap joinBitmapsVertical(List<Bitmap> bitmaps) {
        int width = 0;
        int height = 0;
        int top = 0;

        if (bitmaps == null) return null;

        if (!bitmaps.isEmpty()) {
            for (Bitmap bitmap : bitmaps) {
                if (bitmap == null) continue;
                height = height + bitmap.getHeight();
                if (width < bitmap.getWidth()) width = bitmap.getWidth();
            }
            Bitmap resultBitmap = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888);
            Canvas canvas = new Canvas(resultBitmap);
            for (Bitmap bitmap : bitmaps) {
                if (bitmap == null) continue;
                canvas.drawBitmap(bitmap, 0f, top, null);
                top = top + bitmap.getHeight();
            }
            return resultBitmap;
        } else return null;
    }
}
