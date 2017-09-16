package nightgames.combat;

import nightgames.characters.Character;
import nightgames.characters.NPC;
import nightgames.gui.GUI;
import nightgames.gui.RunnableButton;
import nightgames.requirements.Requirement;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

public class CombatScene {
    public static interface StringProvider {
        String provide(Combat c, Character self, Character other);
    }
    private StringProvider message;
    private List<CombatSceneChoice> choices;
    private Requirement requirement;

    public CombatScene(Requirement requirement, StringProvider message, Collection<CombatSceneChoice> choices) {
        this.choices = new ArrayList<>(choices);
        this.message = message;
        this.requirement = requirement;
    }

    public void visit(Combat c, Character npc) {
        c.write("<br/>");
        c.write(message.provide(c, npc, c.getOpponent(npc)));
        c.updateAndClearMessage();
        choices.forEach(choice -> {
            RunnableButton button = RunnableButton.genericRunnableButton(choice.getChoice(), () -> {
                c.write("<br/>");
                choice.choose(c, npc);
                c.updateMessage();
                c.promptNext(GUI.gui);
            });
            GUI.gui.commandPanel.add(button);
            GUI.gui.commandPanel.refresh();
        });
    }

    public boolean meetsRequirements(Combat c, NPC npc) {
        return requirement.meets(c, npc, c.getOpponent(npc));
    }
}
