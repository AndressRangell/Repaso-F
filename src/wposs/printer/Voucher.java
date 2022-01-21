package wposs.printer;

import android.graphics.Bitmap;

import java.util.ArrayList;
import java.util.EnumMap;
import java.util.List;
import java.util.Map;

import wposs.printer.data.PrintData;
import wposs.printer.data.format.TextFormat;
import wposs.printer.data.format.base.FormatType;
import wposs.printer.data.format.type.Align;
import wposs.printer.data.format.type.Size;
import wposs.printer.data.type.TypeData;
import wposs.printer.factory.TextConvert;

/**
 * Created by Esneider on June 1, 2021
 */

public class Voucher extends VoucherUtils {

    private final List<PrintData> original = new ArrayList<>();
    private final List<PrintData> copy = new ArrayList<>();
    private final List<Map<FormatType, String>> formats = new ArrayList<>();

    public Bitmap getBitmap() {
        return toBitmap(original, formats);
    }

    public Bitmap getBitmapCopy() {
        return toBitmap(copy, formats);
    }

    public void clear() {
        original.clear();
        copy.clear();
        formats.clear();
    }

    // PUT BITMAP

    public void putBitmap(Bitmap bitmap) {
        put(CopyType.BOTH, TypeData.BITMAP, bitmap, new TextFormat().setAlign(Align.CENTER));
    }

    public void putBitmap(CopyType copyType, Bitmap bitmap) {
        put(copyType, TypeData.BITMAP, bitmap, new TextFormat().setAlign(Align.CENTER));
    }

    public void putLine() {
        put(CopyType.BOTH, TypeData.TEXT, "", new TextFormat().setSize(Size.SMALL));
    }

    // PUT TEXT

    public void putText(String text, TextFormat printFormat) {
        put(CopyType.BOTH, TypeData.TEXT, text, printFormat);
    }

    public void putText(CopyType copyType, String text, TextFormat printFormat) {
        put(copyType, TypeData.TEXT, text, printFormat);
    }

    public void putTextFill(String text, String fill, TextFormat format) {
        StringBuilder add = new StringBuilder();
        for (int i = 0; i < TextConvert.getLastSize(text.length(), format.getSize()); i++) {
            add.append(fill);
        }
        if (format.getAlign() != null) switch (format.getAlign()) {
            case LEFT:
                putText(text + add.toString(), format);
                break;
            case RIGHT:
                putText(add.toString() + text, format);
                break;
            case CENTER:
                if (text.isEmpty()) putText(add.toString(), format);
                break;
            default:
                putText(text, format);
                break;
        }
    }

    private void put(CopyType copyType, TypeData typeData, Object data, TextFormat format) {
        if (format == null) {
            addBuilder(copyType, new PrintData(typeData, data));
        } else {
            EnumMap<FormatType, String> map = new EnumMap<>(format.getMap());
            int index = formats.indexOf(map);
            if (index == -1) formats.add(map);
            index = formats.indexOf(map);
            addBuilder(copyType, new PrintData(typeData, data, index));
        }
    }

    private void addBuilder(CopyType copyType, PrintData data) {
        switch (copyType) {
            case JUST_ORIGINAL:
                original.add(data);
                break;
            case JUST_COPY:
                copy.add(data);
                break;
            case BOTH:
            default:
                original.add(data);
                copy.add(data);
                break;
        }
    }

    public enum CopyType {
        BOTH,
        JUST_ORIGINAL,
        JUST_COPY
    }
}
