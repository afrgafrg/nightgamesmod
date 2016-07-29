package nightgames.gui;

import nightgames.global.Global;

/**
 * TODO: Write class-level documentation.
 */
public class GameThread extends Thread {
    private static GameThread currentGameThread;

    public void run() {
        currentGameThread = this;
        Global.global.gameLoop();
    }

    public static GameThread getCurrentGameThread() {
        return currentGameThread;
    }
}
