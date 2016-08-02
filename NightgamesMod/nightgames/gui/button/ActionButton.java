package nightgames.gui.button;

import nightgames.actions.Action;

/**
 * A button that executes an Action with a Character on click.
 * <p>
 * Actions are used during matches while not in combat, e.g., Move or Scavenge.
 */
public class ActionButton extends ValueButton<Action> {
    private static final long serialVersionUID = 2822534455509003521L;

    /**
     * @param action The action to run on click.
     */
    public ActionButton(Action action) {
        super(action);
    }
}
