package nightgames.combat;

import nightgames.characters.Character;
import nightgames.characters.NPC;
import nightgames.gui.ContinueButton;
import nightgames.gui.GUI;
import nightgames.gui.LabeledValue;
import nightgames.gui.RunnableButton;
import nightgames.requirements.Requirement;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;

public class CombatScene {
    public interface StringProvider {
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
        c.write(message.provide(c, npc, c.getOpponent(npc)));
        Future<CombatSceneChoice> choiceFuture = GUI.gui.promptFuture(choices, CombatSceneChoice::getChoice);
        try {
            c.updateGUI();
            CombatSceneChoice choice = choiceFuture.get();
            choice.choose(c, npc);
            c.updateGUI();
        } catch (InterruptedException e) {
            e.printStackTrace();
            Thread.currentThread().interrupt();
        } catch (ExecutionException e) {
            throw new RuntimeException(e);
        }
    }

    boolean meetsRequirements(Combat c, NPC npc) {
        return requirement.meets(c, npc, c.getOpponent(npc));
    }
}
