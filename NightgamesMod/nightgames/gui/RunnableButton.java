package nightgames.gui;

public abstract class RunnableButton extends KeyableButton {
    private static final long serialVersionUID = 5435929681634872672L;
    private String text;
    RunnableButton(String text) {
        super(formatHTMLMultiline(text, ""));
        this.text = text;
        resetFontSize();

        getButton().addActionListener((evt) -> this.run());
    }

    public static RunnableButton genericRunnableButton(String text, Runnable runnable) {
        return new RunnableButton(text) {
            private static final long serialVersionUID = -3002901673898389260L;

            @Override protected void run() {
                runnable.run();
            }
        };
    }

    protected abstract void run();

    @Override
    public String getText() {
        return text;
    }

    @Override public void setHotkeyTextTo(String string) {
        getButton().setText(formatHTMLMultiline(text, String.format(" [%s]", string)));
        resetFontSize();
    }
}
