package nightgames.gui.button;

import java.util.concurrent.CompletableFuture;

/**
 * TODO: Write class-level documentation.
 */
public class CancelButton<T> extends FutureButton<T> {
    private static final long serialVersionUID = -1891760139218775762L;

    public CancelButton(String label) {
        super(label);
    }

    @Override public void completeOrCancel(CompletableFuture<T> future) {
        future.cancel(true);
    }
}
