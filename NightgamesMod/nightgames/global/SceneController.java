package nightgames.global;

import nightgames.gui.GUI;
import nightgames.gui.NgsController;
import nightgames.gui.Prompt;
import nightgames.gui.button.SceneButton;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * Controller for Scenes.
 */
public class SceneController implements NgsController {
    private final GUI gui;

    public SceneController(GUI gui) {
        this.gui = gui;
    }

    public GUI gui() {
        return gui;
    }

    /**
     * Makes a prompt and displays buttons for choices relating to a Scene.
     * @param sceneToLabel Map of String semantic values to button labels.
     * @return Blocks until the prompt is resolved, then returns the semantic value that the user chose.
     * @throws InterruptedException when the prompt is interrupted.
     */
    public Optional<String> getChoice(Map<String, String> sceneToLabel) throws InterruptedException {
        List<SceneButton> buttons =
                        sceneToLabel.entrySet().stream().map(e -> new SceneButton(e.getKey(), e.getValue()))
                                        .collect(Collectors.toList());
        Prompt<String> prompt = new Prompt<>(buttons);
        gui.setChoices(buttons);
        return prompt.response();
    }
}
