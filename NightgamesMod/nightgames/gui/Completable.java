package nightgames.gui;

/**
 * An object with a task that can be completed asynchronously.
 */
public interface Completable {
    /**
     * Completes the task.
     */
    void complete();

    /**
     * Waits for task to be completed from another thread.
     */
    void await();
}
