package nightgames.global;

import com.google.gson.JsonIOException;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.google.gson.stream.JsonWriter;
import nightgames.gui.GUI;
import nightgames.json.JsonUtils;

import javax.swing.*;
import javax.swing.filechooser.FileFilter;
import javax.swing.filechooser.FileNameExtensionFilter;
import java.io.*;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;

/**
 * Read and write SaveData to and from save files.
 */
public class SaveFile {
    public static void autoSave() {
        if (Flag.checkFlag(Flag.autosave)) {
            save(new File("./auto.ngs"));
        }
    }

    public static void saveWithDialog() {
        Optional<File> file = GUI.gui.askForSaveFile();
        if (file.isPresent()) {
            save(file.get());
        }
    }

    public static void save(File file) {
        SaveData data = GameState.gameState.saveData();
        JsonObject saveJson = data.toJson();

        try (JsonWriter saver = new JsonWriter(new FileWriter(file))) {
            saver.setIndent("  ");
            JsonUtils.getGson().toJson(saveJson, saver);
        } catch (IOException | JsonIOException e) {
            System.err.println("Could not save file " + file + ": " + e.getMessage());
            e.printStackTrace();
        }
    }

    public static void loadWithDialog(CompletableFuture<GameState> stateFuture) {
        JFileChooser dialog = new JFileChooser("./");
        FileFilter savesFilter = new FileNameExtensionFilter("Nightgame Saves", "ngs");
        dialog.addChoosableFileFilter(savesFilter);
        dialog.setFileFilter(savesFilter);
        dialog.setMultiSelectionEnabled(false);
        int rv = dialog.showOpenDialog(GUI.gui);
        if (rv != JFileChooser.APPROVE_OPTION) {
            return;
        }
        File file = dialog.getSelectedFile();
        if (!file.isFile()) {
            file = new File(dialog.getSelectedFile().getAbsolutePath() + ".ngs");
            if (!file.isFile()) {
                // not a valid save, abort
                JOptionPane.showMessageDialog(GUI.gui, "Nightgames save file not found", "File not found",
                                JOptionPane.ERROR_MESSAGE);
                return;
            }
        }
        try {
            stateFuture.complete(load(file));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static GameState load(File file) throws IOException {
        GameState loadedGame = new GameState();
        loadedGame.resetForLoad();

        JsonObject object;
        try (Reader loader = new InputStreamReader(new FileInputStream(file))) {
            object = new JsonParser().parse(loader).getAsJsonObject();

        } catch (IOException e) {
            // Couldn't load data; just get out
            throw e;
        }
        SaveData data = new SaveData(object, loadedGame);
        return loadedGame.loadData(data);
    }

}
