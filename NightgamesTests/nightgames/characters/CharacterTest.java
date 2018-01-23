package nightgames.characters;

import org.junit.Before;
import org.junit.Test;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.CoreMatchers.not;
import static org.junit.Assert.*;

/**
 * TODO: Write class-level documentation.
 */
public class CharacterTest {
    private Character testCharacter;

    @Before public void setUp() throws Exception {
        testCharacter = new TestCharacter();
    }

    @Test public void spendXPDetectLevelup() {
        assertThat(testCharacter.levelsToGain, equalTo(0));
        testCharacter.xp = 0;
        testCharacter.spendXP();
        assertThat(testCharacter.levelsToGain, equalTo(0));
        testCharacter.xp += testCharacter.getXPReqToNextLevel();
        testCharacter.spendXP();
        assertThat(testCharacter.levelsToGain, equalTo(1));
    }
    // test spend xp only when it would leave 0 or more

    @Test public void spendXPOnlyWhenEnough() {
        int XPBefore = testCharacter.getXPReqToNextLevel() - 1;
        testCharacter.xp = XPBefore;
        testCharacter.spendXP();
        assertThat(testCharacter.levelsToGain, equalTo(0));
        assertThat(testCharacter.xp, equalTo(XPBefore));
    }

    @Test public void spendMultipleLevelupXP() {
        int expectedLevelups = 100;
        testCharacter.xp = 0;
        testCharacter.levelsToGain = 0;
        int startLevel = testCharacter.level;
        for (int i = 0; i < expectedLevelups; i++) {
            testCharacter.xp += testCharacter.getXPReqToNextLevel(startLevel + i);
        }
        testCharacter.spendXP();
        assertThat(testCharacter.levelsToGain, equalTo(100));
        assertThat(testCharacter.xp, equalTo(0));
    }
}
