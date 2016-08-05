package nightgames.daytime;

import nightgames.gui.GUI;
import nightgames.gui.NgsController;
import nightgames.gui.Prompt;
import nightgames.gui.button.ActivityButton;

import java.util.List;
import java.util.stream.Collectors;

/**
 * TODO: Write class-level documentation.
 */
class DaytimeController implements NgsController {
    private final GUI gui;

    DaytimeController(GUI gui) {
        this.gui = gui;
    }

    void morningMessage(String message) {
        gui.message(message);
    }

    void planningMessage(String message) {
        gui.message(message);
    }

    Activity getActivity(List<Activity> activities) throws InterruptedException {
        List<ActivityButton> buttons = activities.stream().map(ActivityButton::new).collect(Collectors.toList());
        Prompt<Activity> prompt = new Prompt<>(buttons);
        gui.setChoices(buttons);
        return prompt.response().orElseThrow(() -> new RuntimeException("No activity selected"));
    }
}
