package nightgames.gui;

public class RunnableButton extends KeyableButton {
    private static final long serialVersionUID = 5435929681634872672L;
    private String text;
    public RunnableButton(String text, Runnable runnable) {
        super(formatHTMLMultiline(text, ""));
        this.text = text;
        resetFontSize();

        getButton().addActionListener((evt) -> runnable.run());
    }

    @Override
    public String getText() {
        return text;
    }

    public void setHotkeyTextTo(String string) {
        getButton().setText(formatHTMLMultiline(text, String.format(" [%s]", string)));
        resetFontSize();
    }
}
