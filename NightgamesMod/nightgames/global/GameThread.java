package nightgames.global;

/**
 * TODO: Write class-level documentation.
 */
public class GameThread extends Thread {
    private static GameThread currentGameThread;
    private final Global gameState;

    public GameThread(Global gameState) {
        this.gameState = gameState;
    }

    public void run() {
        currentGameThread = this;
        gameState.gameLoop();
    }

    public static GameThread getCurrentGameThread() {
        return currentGameThread;
    }

    public Global getGameState() {
        return gameState;
    }
}
