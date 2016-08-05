package nightgames.daytime;

import nightgames.gui.GUI;
import nightgames.gui.NgsController;
import nightgames.gui.Prompt;
import nightgames.gui.button.ActivityButton;

import java.util.List;
import java.util.stream.Collectors;

/**
 * Controller for Daytime.
 */
class DaytimeController implements NgsController {
    private final GUI gui;

    DaytimeController(GUI gui) {
        this.gui = gui;
    }

    @Override public GUI gui() {
        return gui;
    }

    void morningMessage(String message) {
        gui.message(message);
    }

    void planningMessage(String message) {
        gui.message(message);
    }

    /**
     * Prompts the user to select an Activity.
     * @param activities The available Activities.
     * @return The Activity that the user selected.
     * @throws InterruptedException when the prompt is interrupted.
     */
    Activity getActivity(List<Activity> activities) throws InterruptedException {
        List<ActivityButton> buttons = activities.stream().map(ActivityButton::new).collect(Collectors.toList());
        Prompt<Activity> prompt = new Prompt<>(buttons);
        gui.setChoices(buttons);
        return prompt.response().orElseThrow(() -> new RuntimeException("No activity selected"));
    }
}
