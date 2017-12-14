package nightgames.global;

import nightgames.Resources.ResourceLoader;
import nightgames.characters.TraitTree;
import nightgames.gui.GUI;
import nightgames.gui.HeadlessGui;
import nightgames.json.JsonUtils;
import nightgames.requirements.TraitRequirement;

import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.InputStreamReader;
import java.util.Date;
import java.util.Map;
import java.util.concurrent.ExecutionException;

/**
 * Program entry point
 */
public class Main {
    public static volatile boolean exit = false;

    public static void main(String[] args) {
        new Logwriter();
        Logwriter.makeLogger(new Date());
        parseDebugFlags(args);
        initialize();
        makeGUI();
        // TODO: Make sure this works like I want it to. I don't think it captures anything useful on interrupts or errors.
        try {
            run();
        } catch (ExecutionException e) {
            e.printStackTrace();
        } catch (InterruptedException e) {
            System.out.println("Interrupted!");
            e.printStackTrace();
        }
    }

    private static void run() throws ExecutionException, InterruptedException {
        // TODO: test loading while waiting for a pause prompt
        while (!exit) {
            GameState state = GUI.gui.getGameState();
            state.gameLoop();
        }
    }

    private static void makeGUI() {
        GUI gui;
        if (DebugFlags.isDebugOn(DebugFlags.NO_GUI)) {
            gui = new HeadlessGui();
        } else {
            gui = new GUI();
        }
        gui.addWindowListener(new CloseListener());
    }

    public static void parseDebugFlags(String[] args) {
        for (String arg : args) {
            try {
                DebugFlags flag = DebugFlags.valueOf(arg);
                DebugFlags.debug[flag.ordinal()] = true;
            } catch (IllegalArgumentException e) {
                // pass
            }
        }
    }

    public static void initialize() {
        TraitRequirement.setTraitRequirements(new TraitTree(ResourceLoader.getFileResourceAsStream("data/TraitRequirements.xml")));
        Map<String, Boolean> configurationFlags = JsonUtils.mapFromJson(JsonUtils.rootJson(
                        new InputStreamReader(ResourceLoader.getFileResourceAsStream("data/globalflags.json")))
                        .getAsJsonObject(), String.class, Boolean.class);
        configurationFlags.forEach(Flag::setFlag);
    }

    private static class CloseListener extends WindowAdapter {
        @Override public void windowClosing(WindowEvent e) {
            exit = true;
        }
    }
}

