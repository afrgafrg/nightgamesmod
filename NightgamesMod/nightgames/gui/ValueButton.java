package nightgames.gui;

import com.sun.istack.internal.Nullable;

import javax.swing.text.html.Option;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;

/**
 * A button that completes a future on click.
 */
public class ValueButton<T> extends KeyableButton {
    private static final long serialVersionUID = -2698381865901846194L;
    public final T value;
    public final CompletableFuture<T> future;

    public ValueButton(LabeledValue<T> value, CompletableFuture<T> future) {
        super(formatHTMLMultiline(value.getLabel(), ""));
        this.value = value.getValue();
        this.future = future;
        getButton().addActionListener(evt -> complete());
    }

    public ValueButton(T value, String label, CompletableFuture<T> future) {
        this(new LabeledValue<>(value, label), future);
    }

    public Optional<T> get() throws InterruptedException {
        try {
            return Optional.ofNullable(future.get());
        } catch (ExecutionException e) {
            e.printStackTrace();
            return Optional.empty();
        }
    }

    private void complete() {
        future.complete(value);
    }

    @Override public String getText() {
        return getButton().getText();
    }
}
