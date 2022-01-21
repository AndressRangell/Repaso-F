package wposs.printer.data.format;

import static wposs.printer.data.format.base.FormatType.ALIGN;
import static wposs.printer.data.format.base.FormatType.BOLD;
import static wposs.printer.data.format.base.FormatType.INVERTED_COLOR;
import static wposs.printer.data.format.base.FormatType.SIZE;
import static wposs.printer.data.format.base.FormatType.TYPE_TEXT;

import java.util.EnumMap;
import java.util.Map;

import wposs.printer.data.format.base.FormatType;
import wposs.printer.data.format.type.Align;
import wposs.printer.data.format.type.Bold;
import wposs.printer.data.format.type.InvertedColor;
import wposs.printer.data.format.type.Size;
import wposs.printer.data.format.type.TypeText;

public class TextFormat {

    private EnumMap<FormatType, String> map = new EnumMap<>(FormatType.class);

    public TextFormat() {
    }

    public TextFormat(Map<FormatType, String> format) {
        map.clear();
        map.putAll(format);
    }

    public static Map<FormatType, String> fillDefaultFormat(Map<FormatType, String> map) {
        if (map.get(ALIGN) == null)
            map.put(ALIGN, Align.LEFT.name());
        if (map.get(BOLD) == null)
            map.put(BOLD, Bold.OFF.name());
        if (map.get(SIZE) == null)
            map.put(SIZE, Size.MEDIUM.name());
        if (map.get(TYPE_TEXT) == null)
            map.put(TYPE_TEXT, TypeText.PARAGRAPH.name());
        if (map.get(INVERTED_COLOR) == null)
            map.put(INVERTED_COLOR, InvertedColor.OFF.name());
        return map;
    }

    public static TextFormat fromMap(Map<FormatType, String> format) {
        return new TextFormat(format);
    }

    public Map<FormatType, String> getMap() {
        return map;
    }

    public Align getAlign() {
        return Align.valueOf(map.get(ALIGN));
    }

    public TextFormat setAlign(Align align) {
        map.put(ALIGN, align.name());
        return this;
    }

    public TextFormat addAlign(Align align) {
        EnumMap<FormatType, String> mapNewInstance = new EnumMap<>(this.map);
        mapNewInstance.put(ALIGN, align.name());
        return new TextFormat(mapNewInstance);
    }

    public Bold getBold() {
        return Bold.valueOf(map.get(BOLD));
    }

    public TextFormat setBold(Bold bold) {
        map.put(BOLD, bold.name());
        return this;
    }

    public TextFormat addBold(Bold bold) {
        EnumMap<FormatType, String> mapNewInstance = new EnumMap<>(this.map);
        mapNewInstance.put(BOLD, bold.name());
        return new TextFormat(mapNewInstance);
    }

    public Size getSize() {
        return Size.valueOf(map.get(SIZE));
    }

    public TextFormat setSize(Size size) {
        map.put(SIZE, size.name());
        return this;
    }

    public TextFormat addSize(Size size) {
        EnumMap<FormatType, String> mapNewInstance = new EnumMap<>(this.map);
        mapNewInstance.put(SIZE, size.name());
        return new TextFormat(mapNewInstance);
    }

    public TypeText getTypeText() {
        return TypeText.valueOf(map.get(TYPE_TEXT));
    }

    public TextFormat setTypeText(TypeText typeText) {
        map.put(TYPE_TEXT, typeText.name());
        return this;
    }

    public TextFormat addTypeText(TypeText typeText) {
        EnumMap<FormatType, String> mapNewInstance = new EnumMap<>(this.map);
        mapNewInstance.put(TYPE_TEXT, typeText.name());
        return new TextFormat(mapNewInstance);
    }

    public InvertedColor getInvertedColor() {
        return InvertedColor.valueOf(map.get(INVERTED_COLOR));
    }

    public TextFormat setInvertedColor(InvertedColor invertedColor) {
        map.put(INVERTED_COLOR, invertedColor.name());
        return this;
    }

    public TextFormat addInvertedColor(InvertedColor invertedColor) {
        EnumMap<FormatType, String> mapNewInstance = new EnumMap<>(this.map);
        mapNewInstance.put(INVERTED_COLOR, invertedColor.name());
        return new TextFormat(mapNewInstance);
    }

    public void remove(FormatType type) {
        this.map.remove(type);
    }

    public void clear() {
        this.map.clear();
    }

    public void fillDefaultFormat() {
        if (map.get(ALIGN) == null)
            map.put(ALIGN, Align.LEFT.name());
        if (map.get(BOLD) == null)
            map.put(BOLD, Bold.OFF.name());
        if (map.get(SIZE) == null)
            map.put(SIZE, Size.MEDIUM.name());
        if (map.get(TYPE_TEXT) == null)
            map.put(TYPE_TEXT, TypeText.PARAGRAPH.name());
        if (map.get(INVERTED_COLOR) == null)
            map.put(INVERTED_COLOR, InvertedColor.OFF.name());
    }
}
