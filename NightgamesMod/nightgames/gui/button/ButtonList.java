package nightgames.gui.button;

import nightgames.gui.Prompt;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class ButtonList<T> implements Serializable {
    private static final long serialVersionUID = -5429455076689631217L;
    private final List<FutureButton<T>> buttons;

    private static final int MAX_BUTTONS_PER_PAGE = 25;

    public ButtonList(List<FutureButton<T>> buttons) {
        this.buttons = buttons;
        page(0);
    }

    public List<FutureButton<T>> getButtons() {
        return buttons;
    }

    public Prompt<T> makePrompt() {
        return new Prompt<>(buttons);
    }

    public List<GameButton> page(int page) {
        List<GameButton> newButtons = new ArrayList<>();
        newButtons.addAll(buttons.subList(startIndex(page), endIndex(page)));
        return newButtons;
    }

    private int startIndex(int page) {
        return page * MAX_BUTTONS_PER_PAGE;
    }

    private int endIndex(int page) {
        return Math.min(buttons.size(), startIndex(page + 1));
    }

    public boolean isFirstPage(int page) {
        return page == 0;
    }

    public boolean isLastPage(int page) {
        return startIndex(page + 1) > buttons.size();
    }
}
