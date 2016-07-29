package nightgames.gui;

import nightgames.characters.Attribute;
import nightgames.characters.CharacterSex;
import nightgames.characters.Trait;
import nightgames.global.Global;
import org.junit.Test;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;

/**
 * Tests involving the CreationGUI.
 */
public class CreationGUITest {
    @Test public void testSelectPlayerStats() throws Exception {
        Global.global = mock(Global.class);
        CreationGUI creationGUI = new CreationGUI();
        creationGUI.namefield.setText("TestPlayer");
        creationGUI.StrengthBox.setSelectedItem(Trait.romantic);
        creationGUI.WeaknessBox.setSelectedItem(Trait.insatiable);
        creationGUI.power = 5;
        creationGUI.seduction = 11;
        creationGUI.cunning = 9;
        creationGUI.makeGame(Optional.empty());
        Map<Attribute, Integer> expectedAttributes = new HashMap<>();
        expectedAttributes.put(Attribute.Power, 5);
        expectedAttributes.put(Attribute.Seduction, 11);
        expectedAttributes.put(Attribute.Cunning, 9);
        verify(Global.global).newGame("TestPlayer", Optional.empty(), Arrays.asList(Trait.romantic, Trait.insatiable),
                        CharacterSex.male, expectedAttributes);
    }
}
