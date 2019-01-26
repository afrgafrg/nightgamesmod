package nightgames.global;

import nightgames.characters.Player;
import nightgames.characters.Character;
import nightgames.characters.NPC;
import nightgames.gui.ContinueButton;
import nightgames.gui.GUI;
import nightgames.gui.LabeledValue;
import nightgames.gui.SaveButton;
import nightgames.modifier.Modifier;
import nightgames.modifier.ModifierPool;
import nightgames.modifier.standard.MayaModifier;
import nightgames.modifier.standard.NoModifier;
import nightgames.modifier.standard.UnderwearOnlyModifier;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;

/**
 * Setup for the standard match type.
 */
public class PreMatchSchool extends Prematch {
    private Modifier type;

    public PreMatchSchool(CompletableFuture<Match> match) {
        super(match);
    }

    private enum Response {
        start("Start the Match"),
        accept("Do it"),
        refuse("Not interested"),
        perspectiveSwitch("Perspective Switch");

        String label;

        Response(String label) {
            this.label = label;
        }

        LabeledValue<Response> value() {
            return new LabeledValue<>(this, this.label);
        }
    }

    public void prompt(Player player, boolean allowPerspectiveSwitch) throws InterruptedException, ExecutionException {
        List<LabeledValue<Response>> choices = new ArrayList<>();
        String message = "";
        if (type != null) {
            message += String.format("You have switched places with %s for the night.<br/>",
                            player.getTrueName());
            if (!type.name().equals(new NoModifier().name())) {
                message += "Now, you must reconsider Lilly's offer:<br/><br/>" + type.intro();
                choices.add(Response.accept.value());
                choices.add(Response.refuse.value());
            } else {
                choices.add(Response.start.value());
            }
        } else {
            if (player.getLevel() < 5) {
                message += "You arrive at the student union a few minutes before the start of the match. "
                                + "You have enough time to check in and make idle chat with your opponents before "
                                + "you head to your assigned starting point and wait. At exactly 10:00, the match is on.";
                type = new NoModifier();
                choices.add(Response.start.value());
            } else if (!Flag.checkFlag(Flag.metLilly)) {
                message += "You get to the student union a little earlier than usual. Cassie and Jewel are there already and you spend a few minutes talking with them while "
                                + "you wait for the other girls to show up. A few people are still rushing around making preparations, but it's not clear exactly what they're doing. "
                                + "Other than making sure there are enough spare clothes to change into, there shouldn't be too much setup required. Maybe they're responsible for "
                                + "making sure the game area is clear of normal students, but you can't imagine how a couple students could pull that off.<br/><br/>A girl who appears to be "
                                + "in charge calls you over. She has straight, red hair, split into two simple pigtails. Her features are unremarkable except for the freckles lightly "
                                + "spotting her face, but you could reasonably describe her as cute. She mentioned her name once. It was a kind of flower... Lilly? Yeah, "
                                + "that sounds right, Lilly Quinn.<br/><br/>Lilly gives you a thin smile that's somewhere between polite and friendly. <i>\"You seem to be doing alright for a beginner. Not "
                                + "everyone takes to this sort of competition so naturally. In fact, if you think you can handle a bit of a handicap, I've been authorized to offer "
                                + "a small bonus.\"</i> Well, you're not going to complain about some extra spending money. It's worth at least hearing her out. <i>\"Sometimes our Benefactor "
                                + "offers some extra prize money, but I can't just give it away for free.\"</i> You think you see a touch of malice enter her smile. <i>\"I came up with some "
                                + "additional rules to make the game a little more interesting. If you accept my rule, then the extra money will be added as a bonus to each point you "
                                + "score tonight.\"</i><br/><br/>It's an interesting offer, but it begs the question of why she's extending it to you specifically. Lilly smirks and brushes one of "
                                + "her pigtails over her shoulder. <i>\"Don't worry, I'm not giving you preferential treatment. You're very much not my type. On the other hand, I do like "
                                + "watching your opponents win, and by giving you a handicap I make that more likely to happen. I don't intend to unfairly pick on you though. Fortunately, "
                                + "you'll make more money for every fight you do win, "
                                + "so it's better for everyone.\"</i> That's.... You're not entirely sure how to respond to that. <i>\"For the first rule, I'll start with something simple: for "
                                + "tonight's match, you're only allowed to wear your underwear. Even when you come back here for a change of clothes, you'll only get your underwear. If you "
                                + "agree to this, I'll throw an extra $" + new UnderwearOnlyModifier().bonus()
                                + " on top of your normal prize for each point you score. Interested?\"</i>";
                type = new UnderwearOnlyModifier();
                Flag.flag(Flag.metLilly);
                choices.add(Response.accept.value());
                choices.add(Response.refuse.value());
            } else if (player.getRank() > 0 && Time.getDate() % 30 == 0) {
                type = new MayaModifier();
                message += type.intro();
                choices.add(Response.start.value());
            } else {
                message += "You arrive at the student union with about 10 minutes to spare before the start of the match. You greet each of the girls and make some idle chatter with "
                                + "them before you check in with Lilly to see if she has any custom rules for you.<br/><br/>";
                type = offer();
                message += type.intro();
                if (!type.name()
                         .equals("normal")) {
                    choices.add(Response.accept.value());
                    choices.add(Response.refuse.value());
                } else {
                    choices.add(Response.start.value());
                }
            }
        }

        GUI.gui.clearText();
        GUI.gui.message(message);
        if (allowPerspectiveSwitch) {
            choices.add(Response.perspectiveSwitch.value());
        }
        CompletableFuture<Response> sceneChoice = GUI.gui.promptFuture(choices);
        GUI.gui.addButton(new SaveButton());
        Response response = sceneChoice.get();
        switch (response) {
            case accept:
                GUI.gui.clearText();
                GUI.gui.clearCommand();
                GUI.gui.message(type.acceptance());
                GUI.gui.next(Response.start.label)
                       .await();
                break;
            case refuse:
                type = new NoModifier();
                break;
            case perspectiveSwitch:
                GUI.gui.clearText();
                GUI.gui.clearCommand();
                promptPerspective(player);
                return;
            case start:
        }
        setUpMatch(type);
    }

    private void promptPerspective(Player player) throws InterruptedException, ExecutionException {
        List<LabeledValue<NPC>> choices = new ArrayList<>();
        GameState.state().characterPool.availableNpcs()
                                       .stream()
                                       .filter(n -> !Flag.checkCharacterDisabledFlag(n))
                                       .map(n -> new LabeledValue<>(n, n.getTrueName()))
                                       .forEach(choices::add);
        choices.add(new LabeledValue<>(null, "Actually, Nevermind"));
        
        GUI.gui.message("Here, you can switch positions with any of your"
                        + " opponents for one night. Any addictions the other character"
                        + " may have will carry over, and vice versa. Also, any permanent"
                        + " changes either character undergoes during the night will be"
                        + " transferred to the 'original' when the night is over.");
        
        CompletableFuture<NPC> choice = GUI.gui.promptFuture(choices);
        NPC chosenPerspective = choice.get();

        try {
            Character.exchange(player, chosenPerspective);
            GameState.state().exchanged = chosenPerspective;
        } catch (CloneNotSupportedException e) {
            e.printStackTrace();
        }
        GUI.gui.clearText();
        GUI.gui.clearCommand();
        prompt(player, false);
    }

    protected Modifier offer() {
        if (Random.random(10) > 4) {
            return new NoModifier();
        }
        Set<Modifier> modifiers = new HashSet<>(ModifierPool.getModifierPool());
        modifiers.removeIf(mod -> !mod.isApplicable() || mod.name()
                                                            .equals("normal"));
        return Random.pickRandom(modifiers.toArray(new Modifier[] {}))
                     .orElse(new NoModifier());
    }

}
