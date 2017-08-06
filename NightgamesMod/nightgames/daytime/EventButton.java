package nightgames.daytime;

import nightgames.gui.KeyableButton;
import nightgames.gui.RunnableButton;

public class EventButton {
    public static KeyableButton eventButton(Activity activity, String choice, String tooltip) {
        RunnableButton button = new RunnableButton(choice, () -> {
            activity.visit(choice);
        });
        if (tooltip != null) {
            button.getButton().setToolTipText(tooltip);
        }
        return button;
    }
}
