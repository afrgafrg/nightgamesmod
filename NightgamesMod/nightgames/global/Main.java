package nightgames.global;

import nightgames.Resources.ResourceLoader;
import nightgames.characters.TraitTree;
import nightgames.gui.GUI;
import nightgames.gui.HeadlessGui;
import nightgames.json.JsonUtils;
import nightgames.requirements.TraitRequirement;

import java.awt.event.WindowEvent;
import java.awt.event.WindowListener;
import java.io.InputStreamReader;
import java.util.Date;
import java.util.Map;
import java.util.concurrent.ExecutionException;

/**
 * Program entry point
 */
public class Main {
    public static volatile boolean exit = false;

    public static void main(String[] args) throws InterruptedException, ExecutionException {
        new Logwriter();
        Logwriter.makeLogger(new Date());
        parseDebugFlags(args);
        initialize();
        makeGUI();
        run();
    }

    private static void run() throws ExecutionException, InterruptedException {
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

    private static class CloseListener implements WindowListener {

        @Override public void windowOpened(WindowEvent e) {

        }

        @Override public void windowClosing(WindowEvent e) {
            exit = true;
        }

        @Override public void windowClosed(WindowEvent e) {

        }

        @Override public void windowIconified(WindowEvent e) {

        }

        @Override public void windowDeiconified(WindowEvent e) {

        }

        @Override public void windowActivated(WindowEvent e) {

        }

        @Override public void windowDeactivated(WindowEvent e) {

        }
    }

}

