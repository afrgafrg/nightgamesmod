package nightgames.gui;

import java.util.Optional;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CountDownLatch;

/**
 * TODO: Write class-level documentation.
 */
public class ContinueButton extends ValueButton<String> {
    private static final long serialVersionUID = -7842752203787391615L;
    private final String label;

    public ContinueButton(String label) {
        super(null, label, new CompletableFuture<>());
        this.label = label;

    }

    public void await() throws InterruptedException {
        get();
    }

    @Override public String getText() {
        return label;
    }
}
