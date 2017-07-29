package nightgames.gui.useraction;

/**
 * A GUI component that can have a UserAction associated with it.
 */
public interface Actionable {

    void setUserAction(String string);

    void clearUserAction();
}
