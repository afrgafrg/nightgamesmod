package nightgames.gui;

import java.util.concurrent.CompletableFuture;

/**
 * On click, cancels the supplied CompletableFuture.
 */
public class CancelButton extends RunnableButton {
    private static final long serialVersionUID = -4059665931203912270L;

    public CancelButton(String label, CompletableFuture future) {
        super(label, () -> future.cancel(true));
    }
}
