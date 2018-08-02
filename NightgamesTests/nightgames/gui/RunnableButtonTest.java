package nightgames.gui;

import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

/**
 * Tests for RunnableButton class.
 */
public class RunnableButtonTest {
    private boolean ran;

    @Before public void setUp() throws Exception {
        ran = false;
    }

    @Test public void genericRunnableButtonClick() {
        RunnableButton genericButton = RunnableButton.genericRunnableButton("run this", () -> ran = true);
        assertFalse("Supplied runnable should not have run yet", ran);
        genericButton.call();
        assertTrue("Supplied runnable should have run", ran);
    }
}
