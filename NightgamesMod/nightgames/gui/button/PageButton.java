package nightgames.gui.button;

import nightgames.gui.CommandPanel;

/**
 * TODO: Write class-level documentation.
 */
public class PageButton extends GameButton {
    private static final long serialVersionUID = 1291939812301193206L;

    public PageButton(String label, int newPage, CommandPanel commandPanel) {
        super(label);
        addActionListener(arg0 -> commandPanel.showButtons(newPage));
    }
}
