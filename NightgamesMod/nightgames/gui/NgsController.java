package nightgames.gui;

/**
 * A controller is the component of a game system that knows how to talk to the UI.
 *
 * Controllers let the UI focus on general methods of interacting with the user, without needing to know about system
 * specifics. A Match controller would know the sequence of prompts and messages to ask the UI to display at the end of
 * a match, while the UI would focus on actually displaying the messages and prompts and receiving input from the user.
 */
public interface NgsController {
    /**
     * A controller is associated with the GUI instance it talks to.
     * @return The GUI this controller queries and updates.
     */
    GUI gui();

    /**
     * Clears text from the main text panel and prints a message.
     * @param message The message to print to a cleared panel.
     */
    default void newPage(String message) {
        gui().clearText();
        gui().message(message);
    }

    /**
     * Appends text to the main text panel.
     * @param message The message to append.
     */
    default void message(String message) {
        gui().message(message);
    }
}
