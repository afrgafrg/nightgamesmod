package nightgames.gui.button;

import java.util.concurrent.CompletableFuture;

/**
 * TODO: Write class-level documentation.
 */
public interface Completer<T> {
    void completeOrCancel(CompletableFuture<T> future);
}
