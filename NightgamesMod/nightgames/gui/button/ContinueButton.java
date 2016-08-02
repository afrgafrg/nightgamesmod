package nightgames.gui.button;

import java.util.concurrent.CompletableFuture;

/**
 * Button that unblocks a BlockedPrompt with no other side effects.
 *
 * Common continue button variants are included here.
 */
public class ContinueButton extends CompleteButton<Void> {
    private static final long serialVersionUID = -7842752203787391615L;

    public ContinueButton(String text) {
        super(text);
    }

    @Override protected void complete(CompletableFuture<Void> future) {
        future.complete(null);
    }

    public static class MatchButton extends ContinueButton {
        private static final long serialVersionUID = 3899760251122030064L;

        public MatchButton() {
            super("Start the match");
        }
    }


    public static class NextButton extends ContinueButton {
        private static final long serialVersionUID = 6773730244369679822L;

        public NextButton() {
            super("Next");
        }
    }


    public static class SleepButton extends ContinueButton {
        private static final long serialVersionUID = 1669023447753258615L;

        public SleepButton() {
            super("Go to sleep");
        }
    }
}
