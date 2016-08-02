package nightgames.gui.button;

import java.util.concurrent.CompletableFuture;

/**
 * TODO: Write class-level documentation.
 */
public class ValueButton<T> extends CompleteButton<T> {
    private static final long serialVersionUID = 8450297929568568020L;
    private T value;

    public ValueButton(T value) {
        this(value, value.toString());
    }

    public ValueButton(T value, String text) {
        super(text);
        this.value = value;
    }

    public T getValue() {
        return value;
    }

    @Override protected void complete(CompletableFuture<T> future) {
        future.complete(value);
    }
}
