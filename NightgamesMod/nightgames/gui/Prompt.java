package nightgames.gui;

import nightgames.gui.button.FutureButton;

import java.util.List;
import java.util.Optional;
import java.util.concurrent.CancellationException;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;

/**
 * TODO: Write class-level documentation.
 */
public class Prompt<T> {
    private CompletableFuture<T> future;

    public Prompt(List<? extends FutureButton<T>> choices) {
        this.future = new CompletableFuture<>();
        choices.forEach(choice -> choice.addActionListener(l -> choice.completeOrCancel(future)));
    }

    public Optional<T> response() throws InterruptedException, ExecutionException {
        try {
            return Optional.of(future.get());
        } catch (CancellationException e) {
            return Optional.empty();
        }
    }
}
