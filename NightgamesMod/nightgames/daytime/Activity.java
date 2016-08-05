package nightgames.daytime;

import nightgames.characters.Character;
import nightgames.characters.Player;
import nightgames.global.Global;
import nightgames.global.SceneController;
import nightgames.requirements.Requirement;

public abstract class Activity {
    protected final String name;
    protected final int duration;
    protected final Player player;
    final SceneController controller;
    protected int page;

    public Activity(String name, Player player) {
        this.name = name;
        this.player = player;
        duration = 1;
        page = 0;
        controller = new SceneController(Global.global.gui());
    }

    public abstract boolean available();

    public abstract void start() throws InterruptedException;

    public int duration() {
        return duration;
    }

    public void next() {
        page++;
    }


    @Override
    public String toString() {
        return name;
    }

    public abstract void shop(Character npc, int budget);
}
