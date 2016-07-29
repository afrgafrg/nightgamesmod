package nightgames.gui;

import java.util.List;

/**
 * A BlockingPrompt's block() method will wait for the user to click one of the unblocking CommandButton choices.
 * <p>
 * This is intended to translate Swing's event-based execution model into something suitable for use within a game loop,
 * by providing an object with a blocking method separate from the class containing the game loop.
 * <p>
 * Instead of this:
 * // Game behavior package
 * void singleLoopIteration() {
 *   // do stuff
 *   // make and display a button
 *   // returning implicitly pauses the loop until the button has been clicked
 * }
 * // GUI package; good luck keeping track of this
 * button.addActionListener(event -> {
 *   // do more stuff
 *   singleLoopIteration()    // and here the loop continues
 * })
 * <p>
 * Do this:
 * void loop() {
 *   while (loopShouldContinue) {
 *     // do stuff
 *     // make and display a button
 *     new BlockingPrompt.block(button) // will wait until the user clicks the button
 *     // do more stuff
 *   }
 * }
 * <p>
 * This also makes handling situations like what happens at the end of the match, when the user has a "Go to sleep"
 * button that activates the daytime cycle, and also a "Save" button, which activates the game save dialog. We only want
 * to advance the game state when the user chooses to go to sleep. Simply set CommandButton.canUnblock to true on the
 * sleep button and false on the save button, and pass both buttons to BlockingPrompt.block().
 */
class BlockingPrompt implements Prompt {
    private volatile boolean blocking;

    BlockingPrompt() {
        blocking = true;
    }

    @Override public synchronized void prompt(List<CommandButton> choices) {
        // Allows things like unblocking on "Start the Match" but not "Save"
        choices.stream().filter(choice -> choice.canUnblock)
                        .forEach(choice -> choice.addActionListener(e -> unblock()));
        while (blocking) {
            try {
                wait();
            } catch (InterruptedException ignored) {
                Thread.currentThread().interrupt();
            }
        }
    }

    private synchronized void unblock() {
        blocking = false;
        notifyAll();
    }
}
