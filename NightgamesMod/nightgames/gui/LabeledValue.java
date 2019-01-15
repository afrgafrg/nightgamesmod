package nightgames.gui;

/**
 * Bundles a returnable value with a label, suitable for using with a ValueButton.
 */
public class LabeledValue<T> {
    private final T value;
    private final String label;
    private final String toolTipText;

    /**
     * Creates a LabeledValue.
     * @param value Value to store. May be null.
     * @param label The label associated with the value.
     * @param toolTipText The tooltip text associated with the value. May be null.
     */
    public LabeledValue(T value, String label, String toolTipText) {
        this.value = value;
        this.label = label;
        this.toolTipText = toolTipText;
    }

    public LabeledValue(T value, String label) {
        this(value, label, null);
    }

    public String getLabel() {
        return label;
    }

    public T getValue() {
        return value;
    }

    public String getToolTipText() {
        return toolTipText;
    }
}
