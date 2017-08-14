package nightgames.gui;

import com.sun.istack.internal.Nullable;

/**
 * Bundles a returnable value with a label, suitable for using with a ValueButton.
 */
public class LabeledValue<T> {
    private final T value;
    private final String label;

    /**
     * Creates a LabeledValue.
     * @param value Value to store. May be null.
     * @param label The label associated with value.
     */
    public LabeledValue(T value, String label) {
        this.value = value;
        this.label = label;
    }

    public String getLabel() {
        return label;
    }

    @Nullable
    public T getValue() {
        return value;
    }
}
