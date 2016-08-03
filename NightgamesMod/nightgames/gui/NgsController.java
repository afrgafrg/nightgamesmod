package nightgames.gui;

/**
 * A controller is the component of a game system that knows how to talk to the UI.
 *
 * Controllers let the UI focus on general methods of interacting with the user, without needing to know about system
 * specifics. A Match controller would know the sequence of prompts and messages to ask the UI to display at the end of
 * a match, while the UI would focus on actually displaying the messages and prompts and receiving input from the user.
 */
public interface NgsController {
    // Just a tag, for now.
}
