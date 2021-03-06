package nightgames.gui;

import nightgames.characters.Attribute;
import nightgames.characters.Trait;
import nightgames.global.GameState;
import org.hamcrest.core.IsCollectionContaining;
import org.junit.Before;
import org.junit.Test;

import java.util.Optional;

import static org.hamcrest.collection.IsMapContaining.hasEntry;
import static org.hamcrest.core.AllOf.allOf;
import static org.junit.Assert.assertThat;

/**
 * Tests involving the CreationGUI.
 */
public class CreationGUITest {
    TestGUI testGUI;
    @Before public void setUp() throws Exception {
        testGUI = new TestGUI();
        testGUI.showGameCreation();
    }

    @Test public void testSelectPlayerStats() throws Exception {
        CreationGUI creationGUI = testGUI.creation;
        creationGUI.namefield.setText("TestPlayer");
        creationGUI.StrengthBox.setSelectedItem(Trait.romantic);
        creationGUI.WeaknessBox.setSelectedItem(Trait.insatiable);
        creationGUI.power = 5;
        creationGUI.seduction = 11;
        creationGUI.cunning = 9;
        creationGUI.makeGame(Optional.empty());
        GameState gameState = testGUI.loadedState.take();
        assertThat(gameState.characterPool.human.att, allOf(hasEntry(Attribute.Power, 5), hasEntry(Attribute.Seduction, 11),
                        hasEntry(Attribute.Cunning, 9)));
        assertThat(gameState.characterPool.human.getTraits(), IsCollectionContaining.hasItems(Trait.romantic, Trait.insatiable));
    }
}
