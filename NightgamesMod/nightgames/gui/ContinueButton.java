package nightgames.gui;

/**
 * Button that unblocks a BlockedPrompt with no other side effects.
 *
 * Common continue button variants are included here.
 */
public class ContinueButton extends CommandButton {
    private static final long serialVersionUID = -7842752203787391615L;

    public ContinueButton(String text) {
        super(text, true);
    }

    public static class MatchButton extends ContinueButton {
        private static final long serialVersionUID = 3899760251122030064L;

        public MatchButton() {
            super("Start the match");
        }
    }


    static class NextButton extends ContinueButton {

        private static final long serialVersionUID = 6773730244369679822L;

        public NextButton() {
            super("Next");
        }
    }
}
