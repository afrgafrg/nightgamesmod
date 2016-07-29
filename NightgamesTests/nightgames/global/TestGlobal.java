package nightgames.global;

import nightgames.gui.TestGUI;

/**
 * Creates a version of Global that has no visible GUI.
 */
public class TestGlobal extends Global {
    public TestGlobal() {
        super();
        gui = new TestGUI();
    }
}
