package wposs.printer;

import android.graphics.Bitmap;

import wposs.printer.data.PrintData;
import wposs.printer.data.format.base.FormatType;
import wposs.printer.factory.BitmapConvert;
import wposs.printer.factory.TextConvert;
import wposs.printer.utils.BitmapUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * Created by Esneider on June 1, 2021
 */

public class VoucherUtils {

    Bitmap toBitmap(List<PrintData> builder, List<Map<FormatType, String>> formats) {
        try {
            List<Bitmap> bitmaps = new ArrayList<>();
            if (builder.isEmpty()) return null;
            for (PrintData section : builder) {
                Map<FormatType, String> format = formats.get(section.getFormatIndex());
                switch (section.getType()) {
                    case TEXT:
                        bitmaps.addAll(TextConvert.dataToBitmap(section, format));
                        break;
                    case BITMAP:
                        bitmaps.add(BitmapConvert.dataToBitmap(section, format));
                        break;
                    default:
                        break;
                }
            }
            return BitmapUtils.joinBitmapsVertical(bitmaps);
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }
}
