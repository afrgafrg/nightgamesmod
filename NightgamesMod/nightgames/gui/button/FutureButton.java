package nightgames.gui.button;

import java.util.concurrent.CompletableFuture;

/**
 * TODO: Write class-level documentation.
 */
public abstract class FutureButton<T> extends GameButton implements Completer<T> {
    private static final long serialVersionUID = -8136844106076669498L;

    protected FutureButton() {
    }

    public FutureButton(String text) {
        super(text);
    }

    public abstract void completeOrCancel(CompletableFuture<T> future);
}
