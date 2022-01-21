package wposs.printer.data;

import wposs.printer.data.type.TypeData;

public class PrintData {

    private final TypeData type;
    private final Object data;
    private final int formatIndex;

    public PrintData(TypeData type, Object data, int formatIndex) {
        this.type = type;
        this.data = data;
        this.formatIndex = formatIndex;
    }

    public PrintData(TypeData type, Object data) {
        this.type = type;
        this.data = data;
        this.formatIndex = -1;
    }

    public TypeData getType() {
        return type;
    }

    public Object getData() {
        return data;
    }

    public int getFormatIndex() {
        return formatIndex;
    }
}
