package nightgames.gui.button;

import java.util.concurrent.CompletableFuture;

/**
 * TODO: Write class-level documentation.
 */
public abstract class CompleteButton<T> extends FutureButton<T> {
    public CompleteButton() {
    }

    public CompleteButton(String text) {
        super(text);
    }

    @Override public void completeOrCancel(CompletableFuture<T> future) {
        complete(future);
    }

    protected abstract void complete(CompletableFuture<T> future);

}
