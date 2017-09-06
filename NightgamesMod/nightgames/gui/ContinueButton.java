package nightgames.gui;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;

/**
 * Valueless blocking button.
 */
public class ContinueButton extends ValueButton<String> {
    private static final long serialVersionUID = -7842752203787391615L;

    public ContinueButton(String label) {
        super(null, label, new CompletableFuture<>());

    }

    public void await() throws InterruptedException {
        try {
            future.get();
        } catch (ExecutionException e) {
            e.printStackTrace();
        }
    }
}
