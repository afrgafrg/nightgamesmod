package nightgames.characters;

import com.google.gson.JsonObject;
import nightgames.actions.Action;
import nightgames.actions.Leap;
import nightgames.actions.Move;
import nightgames.actions.Shortcut;
import nightgames.areas.Area;
import nightgames.areas.Deployable;
import nightgames.characters.body.*;
import nightgames.characters.body.mods.GooeyMod;
import nightgames.combat.Combat;
import nightgames.combat.Encounter;
import nightgames.combat.Encs;
import nightgames.combat.Result;
import nightgames.ftc.FTCMatch;
import nightgames.global.*;
import nightgames.global.Formatter;
import nightgames.global.Random;
import nightgames.gui.GUI;
import nightgames.gui.LabeledValue;
import nightgames.items.Item;
import nightgames.items.clothing.Clothing;
import nightgames.skills.Stage;
import nightgames.skills.Tactics;
import nightgames.skills.damage.DamageType;
import nightgames.stance.Behind;
import nightgames.stance.Neutral;
import nightgames.stance.Position;
import nightgames.start.PlayerConfiguration;
import nightgames.status.*;
import nightgames.trap.Trap;

import java.util.*;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;
import java.util.stream.Collectors;

import static nightgames.requirements.RequirementShortcuts.item;

public class Player extends Character {
    public GUI gui;
    public int traitPoints;
    private String type;

    private Player() {
        this("Dummy");
    }

    public Player(String name) {
        this(name, CharacterSex.male, Optional.empty(), new ArrayList<>(), new HashMap<>());
    }

    // TODO(Ryplinn): This initialization pattern is very close to that of BasePersonality. I think it makes sense to make NPC the primary parent of characters instead of BasePersonality.
    public Player(String name, CharacterSex sex, Optional<PlayerConfiguration> config, List<Trait> pickedTraits,
                    Map<Attribute, Integer> selectedAttributes) {
        super(name, 1);
        initialGender = sex;
        applyBasicStats(this);
        setGrowth();

        body.makeGenitalOrgans(initialGender);

        config.ifPresent(this::applyConfigStats);
        finishCharacter(pickedTraits, selectedAttributes);
        this.type = getClass().getSimpleName();
    }

    public Player(JsonObject playerJson) {
        this();
        this.load(playerJson);
    }

    public void applyBasicStats(Character self) {
        self.getStamina().setMax(80);
        self.getArousal().setMax(80);
        self.getWillpower().setMax(self.willpower.max());
        self.availableAttributePoints = 0;
        self.setTrophy(Item.PlayerTrophy);
        if (initialGender.considersItselfFeminine()) {
            outfitPlan.addByID("bra");
            outfitPlan.addByID("panties");
        } else {
            outfitPlan.addByID("boxers");
        }
        outfitPlan.addByID("Tshirt");
        outfitPlan.addByID("jeans");
        outfitPlan.addByID("socks");
        outfitPlan.addByID("sneakers");
    }

    private void applyConfigStats(PlayerConfiguration config) {
        config.apply(this);
    }

    private void finishCharacter(List<Trait> pickedTraits, Map<Attribute, Integer> selectedAttributes) {
        pickedTraits.forEach(this::addTraitDontSaveData);
        att.putAll(selectedAttributes);
        change();
        body.finishBody(initialGender);
    }

    public void setGrowth() {
        getGrowth().stamina = 2;
        getGrowth().arousal = 6;
        getGrowth().willpower = .4f;
        getGrowth().bonusStamina = 1;
        getGrowth().bonusArousal = 2;
        getGrowth().attributes = new int[]{2, 3, 3, 3};
    }

    public String describeStatus() {
        StringBuilder b = new StringBuilder();
        if (GUI.gui.combat != null && (GUI.gui.combat.p1.human() || GUI.gui.combat.p2.human())) {
            body.describeBodyText(b, GUI.gui.combat.getOpponent(this), false);
        } else {
            body.describeBodyText(b, this, false);
        }
        if (getTraits().size() > 0) {
            b.append("<br/>Traits:<br/>");
            List<Trait> traits = new ArrayList<>(getTraits());
            traits.removeIf(t -> t.isOverridden(this));
            traits.sort(Comparator.comparing(Trait::toString));
            b.append(traits.stream()
                           .map(Object::toString)
                           .collect(Collectors.joining(", ")));
        }
        if (status.size() > 0) {
            b.append("<br/><br/>Statuses:<br/>");
            List<Status> statuses = new ArrayList<>(status);
            statuses.sort((first, second) -> first.name.compareTo(second.name));
            b.append(statuses.stream()
                             .map(s -> s.name)
                             .collect(Collectors.joining(", ")));
        }
        return b.toString();
    }

    @Override
    public String describe(int per, Combat c) {
        String description = "<i>";
        for (Status s : status) {
            description = description + s.describe(c) + "<br/>";
        }
        description = description + "</i>";
        description = description + outfit.describe(this);
        if (per >= 5 && status.size() > 0) {
            description += "<br/>List of statuses:<br/><i>";
            description += status.stream().map(Status::toString).collect(Collectors.joining(", "));
            description += "</i><br/>";
        }
        description += Stage.describe(this);
        
        return description;
    }

    @Override
    public void victory(Combat c, Result flag) {
        if (has(Trait.slime)) {
            purge(c);
        }
        if (c.p1.human()) {
            c.p2.defeat(c, flag);
        } else {
            c.p1.defeat(c, flag);
        }
    }

    @Override
    public void defeat(Combat c, Result flag) {
        c.write("Bad thing");
        if (has(Trait.slime)) {
            purge(c);
        }
    }

    @Override
    public boolean chooseSkill(Combat c) {
        return chooseSkillInteractive(c);
    }

    @Override
    public boolean human() {
        return true;
    }

    @Override
    public void draw(Combat c, Result flag) {
        if (has(Trait.slime)) {
            purge(c);
        }
        if (c.p1.human()) {
            c.p2.draw(c, flag);
        } else {
            c.p1.draw(c, flag);
        }
    }

    @Override
    public String bbLiner(Combat c, Character target) {
        return null;
    }

    @Override
    public String nakedLiner(Combat c, Character target) {
        return null;
    }

    @Override
    public String stunLiner(Combat c, Character target) {
        return null;
    }

    @Override
    public String taunt(Combat c, Character target) {
        return null;
    }

    @Override
    public void detect() {
        for (Area adjacent : location.adjacent) {
            if (adjacent.ping(get(Attribute.Perception))) {
                GUI.gui
                      .message("You hear something in the <b>" + adjacent.name + "</b>.");
                adjacent.setPinged(true);
            }
        }
    }

    @Override
    public FightIntent faceOff(Character opponent, Encounter enc) {
        gui.message("You run into <b>" + opponent.nameDirectObject()
                        + "</b> and you both hesitate for a moment, deciding whether to attack or retreat.");
        assessOpponent(opponent);
        gui.message("<br/>");
        List<LabeledValue<FightIntent>> choices = new ArrayList<>();
        choices.add(new LabeledValue<>(FightIntent.fight, "Fight"));
        choices.add(new LabeledValue<>(FightIntent.flee, "Flee"));
        if (item(Item.SmokeBomb, 1).meets(null, this, null)) {
            choices.add(new LabeledValue<>(FightIntent.smoke, "Smoke Bomb"));
        }
        FightIntent choice = FightIntent.fight;
        try {
            choice = gui.promptFuture(choices).get();
        } catch (InterruptedException e) {
            e.printStackTrace();
            Thread.currentThread().interrupt();
        } catch (ExecutionException e) {
            throw new RuntimeException(e);
        }
        return choice;
    }

    private void assessOpponent(Character opponent) {
        String arousal;
        String stamina;
        if (opponent.state == State.webbed) {
            gui.message("She is naked and helpless.<br/>");
            return;
        }
        if (get(Attribute.Perception) >= 6) {
            gui.message("She is level " + opponent.getLevel());
        }
        if (get(Attribute.Perception) >= 8) {
            gui.message("Her Power is " + opponent.get(Attribute.Power) + ", her Cunning is "
                            + opponent.get(Attribute.Cunning) + ", and her Seduction is "
                            + opponent.get(Attribute.Seduction));
        }
        if (opponent.mostlyNude() || opponent.state == State.shower) {
            gui.message("She is completely naked.");
        } else {
            gui.message("She is dressed and ready to fight.");
        }
        if (get(Attribute.Perception) >= 4) {
            if (opponent.getArousal()
                        .percent() > 70) {
                arousal = "horny";
            } else if (opponent.getArousal()
                               .percent() > 30) {
                arousal = "slightly aroused";
            } else {
                arousal = "composed";
            }
            if (opponent.getStamina()
                        .percent() < 50) {
                stamina = "tired";
            } else {
                stamina = "eager";
            }
            gui.message("She looks " + stamina + " and " + arousal + ".");
        }
    }

    @Override
    public Encs spy(Character opponent, Encounter enc) {
        gui.message("You spot <b>" + opponent.nameDirectObject()
                        + "</b> but she hasn't seen you yet. You could probably catch her off guard, or you could remain hidden and hope she doesn't notice you.");
        assessOpponent(opponent);
        gui.message("<br/>");

        List<LabeledValue<Encs>> choices = new ArrayList<>();
        choices.add(new LabeledValue<>(Encs.ambush, "Attack " + opponent.getName()));
        choices.add(new LabeledValue<>(Encs.wait, "Wait"));
        choices.add(new LabeledValue<>(Encs.fleehidden, "Flee"));
        Encs choice = Encs.wait;
        try {
            choice = gui.promptFuture(choices).get();
        } catch (InterruptedException e) {
            e.printStackTrace();
            Thread.currentThread().interrupt();
        } catch (ExecutionException e) {
            throw new RuntimeException(e);
        }
        return choice;
    }

    @Override
    public Optional<Action> move() throws InterruptedException {
        gui.clearCommand();
        List<Action> actionChoices = new ArrayList<>();
        assert (state != State.combat);
        if (busy > 0) {
            busy--;
        } else if (this.is(Stsflag.enthralled)) {
            Character master;
            master = ((Enthralled) getStatus(Stsflag.enthralled)).master;
            if (master != null) {
                Move compelled = findPath(master.location());
                gui.message("You feel an irresistible compulsion to head to the <b>" + master.location().name + "</b>");
                if (compelled != null) {
                    actionChoices.add(compelled);
                }
            }
        } else if (state == State.shower || state == State.lostclothes) {
            bathe();
        } else if (state == State.crafting) {
            craft();
        } else if (state == State.searching) {
            search();
        } else if (state == State.resupplying) {
            resupply();
        } else if (state == State.webbed) {
            gui.message("You eventually manage to get an arm free, which you then use to extract yourself from the trap.");
            state = State.ready;
        } else if (state == State.masturbating) {
            masturbate();
        } else {
            if (Flag.checkFlag(Flag.FTC)) {
                Character holder = ((FTCMatch) Match.getMatch()).getFlagHolder();
                if (holder != null && !holder.human()) {
                    gui.message("<b>" + holder.subject() + " currently holds the Flag.</b></br>");
                }
            }
            gui.message(location.description + "<br/><br/>");
            for (Deployable trap : location.env) {
                if (trap.owner() == this) {
                    gui.message("You've set a " + trap.toString() + " here.");
                }
            }
            if (state == State.inTree)
                gui.message("You are hiding in a tree, waiting to drop down on an unwitting foe.");
            else if (state == State.inBushes)
                gui.message("You are hiding in dense bushes, waiting for someone to pass by.");
            else if (state == State.inPass)
                gui.message("You are hiding in an alcove in the pass.");
            else if (state == State.hidden)
                gui.message("You have found a hiding spot and are waiting for someone to pounce upon.");
            detect();
            if (!allowedActions().isEmpty()) {
                actionChoices.addAll(allowedActions());
            } else {
                List<Action> possibleActions = new ArrayList<>();
                for (Area path : location.adjacent) {
                    possibleActions.add(new Move(path));
                }
                if (getPure(Attribute.Cunning) >= 28) {
                    for (Area path : location.shortcut) {
                        possibleActions.add(new Shortcut(path));
                    }
                }

                if (getPure(Attribute.Ninjutsu) >= 5) {
                    for (Area path : location.jump) {
                        possibleActions.add(new Leap(path));
                    }
                }
                possibleActions.addAll(Action.getActions());
                for (Action act : possibleActions) {
                    if (act.usable(this) && Match.getMatch().condition.allowAction(act, this, Match.getMatch())) {
                        actionChoices.add(act);
                    }
                }
                CompletableFuture<Action> moveFuture = gui.promptFuture(actionChoices, Action::toString);
                try {
                    return Optional.of(moveFuture.get());
                } catch (ExecutionException e) {
                    e.printStackTrace();
                }
            }
        }
        return Optional.empty();
    }

    @Override public void doAction(Action action) {
        gui.clearText();
        action.execute(this);
    }

    @Override
    public void ding(Combat c) {
        level += 1;
        getStamina().gain(getGrowth().stamina);
        getArousal().gain(getGrowth().arousal);
        availableAttributePoints += getGrowth().attributes[Math.min(rank, getGrowth().attributes.length - 1)];
        if (getLevel() % 3 == 0 && level < 10 || (getLevel() + 1) % 2 == 0 && level > 10) {
            traitPoints += 1;
        }
    }

    @Override public void spendLevels(Combat c) {
        if (levelsToGain < 1) {
            return;
        }
        int levelsGained = levelsToGain;
        super.spendLevels(c);
        if (cloned > 0) {
            return;
        }
        Formatter.writeIfCombatUpdateImmediately(c, this,
                        String.format("You've leveled up %d times!<br/>Select which attributes to increase.", levelsGained));
        PlayerLevelUp levelUp = new PlayerLevelUp(this, this.availableAttributePoints, this.traitPoints);
        try {
            levelUp.getHumanChoices(gui);
            this.applyLevelUp(levelUp);
        } catch (InterruptedException e) {
            e.printStackTrace();
            Thread.currentThread().interrupt();
        } catch (ExecutionException e) {
            e.printStackTrace();
            throw new RuntimeException(e);
        }
    }

    private void applyLevelUp(PlayerLevelUp levelUp) {
        for (Attribute att : levelUp.attributeIncreases) {
            this.mod(att, 1);
        }
        this.availableAttributePoints = levelUp.remainingAttributePoints();
        for (Trait feat : levelUp.newTraits) {
            this.add(feat);
        }
        this.traitPoints = levelUp.remainingTraitPoints();
    }

    @Override
    public int getMaxWillpowerPossible() {
        return 50 + getLevel() * 5 - get(Attribute.Submissive) * 2;
    }

    @Override
    public void flee(Area location2) {
        Area[] adjacent = location2.adjacent.toArray(new Area[location2.adjacent.size()]);
        Area destination = adjacent[Random.random(adjacent.length)];
        gui.message("You dash away and escape into the <b>" + destination.name + ".</b>");
        travel(destination);
        location2.endEncounter();
    }

    @Override
    public void bathe() {
        status.removeIf(s -> s.flags().contains(Stsflag.purgable));
        stamina.fill();
        if (location.name.equals("Showers")) {
            gui.message("You let the hot water wash away your exhaustion and soon you're back to peak condition.");
        }
        if (location.name.equals("Pool")) {
            gui.message("The hot water soothes and relaxes your muscles. You feel a bit exposed, skinny-dipping in such an open area. You decide it's time to get moving.");
        }
        if (state == State.lostclothes) {
            gui.message("Your clothes aren't where you left them. Someone must have come by and taken them.");
        }
        state = State.ready;
    }

    @Override
    public void craft() {
        int roll = Random.random(10);
        GUI.gui.message("You spend some time crafting some potions with the equipment.");
        if (checkVsDc(Attribute.Cunning, 25)) {
            if (roll == 9) {
                gain(Item.Aphrodisiac);
                gain(Item.DisSol);
            } else if (roll >= 5) {
                gain(Item.Aphrodisiac);
            } else {
                gain(Item.Lubricant);
                gain(Item.Sedative);
            }
        } else if (checkVsDc(Attribute.Cunning, 20)) {
            if (roll == 9) {
                gain(Item.Aphrodisiac);
            } else if (roll >= 7) {
                gain(Item.DisSol);
            } else if (roll >= 5) {
                gain(Item.Lubricant);
            } else if (roll >= 3) {
                gain(Item.Sedative);
            } else {
                gain(Item.EnergyDrink);
            }
        } else if (checkVsDc(Attribute.Cunning, 15)) {
            if (roll == 9) {
                gain(Item.Aphrodisiac);
            } else if (roll >= 8) {
                gain(Item.DisSol);
            } else if (roll >= 7) {
                gain(Item.Lubricant);
            } else if (roll >= 6) {
                gain(Item.EnergyDrink);
            } else {
                gui.message("Your concoction turns a sickly color and releases a foul smelling smoke. You trash it before you do any more damage.");
            }
        } else {
            if (roll >= 7) {
                gain(Item.Lubricant);
            } else if (roll >= 5) {
                gain(Item.Sedative);
            } else {
                gui.message("Your concoction turns a sickly color and releases a foul smelling smoke. You trash it before you do any more damage.");
            }
        }
        state = State.ready;
    }

    @Override
    public void search() {
        int roll = Random.random(10);
        switch (roll) {
            case 9:
                gain(Item.Tripwire);
                gain(Item.Tripwire);
                break;
            case 8:
                gain(Item.ZipTie);
                gain(Item.ZipTie);
                gain(Item.ZipTie);
                break;
            case 7:
                gain(Item.Phone);
                break;
            case 6:
                gain(Item.Rope);
                break;
            case 5:
                gain(Item.Spring);
                break;
            default:
                gui.message("You don't find anything useful.");
        }
        state = State.ready;
    }

    @Override
    public void masturbate() {
        gui.message("You hurriedly stroke yourself off, eager to finish before someone catches you. After what seems like an eternity, you ejaculate into a tissue and "
                        + "throw it in the trash. Looks like you got away with it.");
        arousal.empty();
        state = State.ready;
    }

    @Override
    public Encs showerSceneResponse(Character target, Encounter encounter) {
        if (target.location().name.equals("Showers")) {
            gui.message("You hear running water coming from the first floor showers. There shouldn't be any residents on this floor right now, so it's likely one "
                            + "of your opponents. You peek inside and sure enough, <b>" + target.subject()
                            + "</b> is taking a shower and looking quite vulnerable. Do you take advantage "
                            + "of her carelessness?");
        } else if (target.location().name.equals("Pool")) {
            gui.message("You stumble upon <b>" + target.nameDirectObject()
                            + "</b> skinny dipping in the pool. She hasn't noticed you yet. It would be pretty easy to catch her off-guard.");
        }
        assessOpponent(target);
        gui.message("<br/>");

        List<LabeledValue<Encs>> choices = new ArrayList<>();
        choices.add(new LabeledValue<>(Encs.showerattack, "Surprise Her"));
        if (!target.mostlyNude()) {
            choices.add(new LabeledValue<>(Encs.stealclothes, "Steal Clothes"));
        }
        if (has(Item.Aphrodisiac)) {
            choices.add(new LabeledValue<>(Encs.aphrodisiactrick, "Use Aphrodisiac"));
        }
        choices.add(new LabeledValue<>(Encs.wait, "Do Nothing"));
        Encs choice = Encs.wait;
        try {
            choice = gui.promptFuture(choices).get();
        } catch (InterruptedException e) {
            e.printStackTrace();
            Thread.currentThread().interrupt();
        } catch (ExecutionException e) {
            throw new RuntimeException(e);
        }
        return choice;
    }

    @Override
    public void decideIntervene(Encounter enc, Character p1, Character p2) {
        gui.message("You find <b>" + p1.getName() + "</b> and <b>" + p2.getName()
                        + "</b> fighting too intensely to notice your arrival. If you intervene now, it'll essentially decide the winner.");
        gui.message("Then again, you could just wait and see which one of them comes out on top. It'd be entertaining,"
                        + " at the very least.");
        List<LabeledValue<String>> choices = Arrays.asList(new LabeledValue<>("p1", "Help " + p1.getName()),
                        new LabeledValue<>("p2", "Help " + p2.getName()),
                        new LabeledValue<>("Watch", "Watch them fight"));
        try {
            String choice = gui.promptFuture(choices).get();
            switch (choice) {
                case "p1":
                    enc.intrude(this, p1);
                    break;
                case "p2":
                    enc.intrude(this, p2);
                    break;
                case "Watch":
                    enc.watch(gui);
                    break;
                default:
                    throw new AssertionError("Unknown Intervene choice: " + choice);
            }
        } catch (InterruptedException e) {
            e.printStackTrace();
            Thread.currentThread().interrupt();
        } catch (ExecutionException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public void intervene3p(Combat c, Character target, Character assist) {
        gainXP(getAssistXP(target));
        target.defeated(this);
        assist.gainAttraction(this, 1);
        c.write("You take your time, approaching " + target.getName() + " and " + assist.getName() + " stealthily. "
                        + assist.getName() + " notices you first and before her reaction "
                        + "gives you away, you quickly lunge and grab " + target.getName()
                        + " from behind. She freezes in surprise for just a second, but that's all you need to "
                        + "restrain her arms and leave her completely helpless. Both your hands are occupied holding her, so you focus on kissing and licking the "
                        + "sensitive nape of her neck.<br/><br/>");
    }

    @Override
    public void victory3p(Combat c, Character target, Character assist) {
        gainXP(getVictoryXP(target));
        target.gainXP(target.getDefeatXP(this));
        target.arousal.empty();
        if (target.has(Trait.insatiable)) {
            target.arousal.restore((int) (arousal.max() * .2));
        }
        dress(c);
        target.undress(c);
        gainTrophy(c, target);
        target.defeated(this);
        if (target.hasDick()) {
            c.write(String.format(
                            "You position yourself between %s's legs, gently "
                                            + "forcing them open with your knees. %s dick stands erect, fully "
                                            + "exposed and ready for attention. You grip the needy member and "
                                            + "start jerking it with a practiced hand. %s moans softly, but seems"
                                            + " to be able to handle this level of stimulation. You need to turn "
                                            + "up the heat some more. Well, if you weren't prepared to suck a cock"
                                            + " or two, you may have joined the wrong competition. You take just "
                                            + "the glans into your mouth, attacking the most senstitive area with "
                                            + "your tongue. %s lets out a gasp and shudders. That's a more promising "
                                            + "reaction.<br/><br/>You continue your oral assault until you hear a breathy "
                                            + "moan, <i>\"I'm gonna cum!\"</i> You hastily remove %s dick out of "
                                            + "your mouth and pump it rapidly. %s shoots %s load into the air, barely "
                                            + "missing you.", target.getName(),
                            Formatter.capitalizeFirstLetter(target.possessiveAdjective()), target.getName(),
                            Formatter.capitalizeFirstLetter(target.pronoun()), target.possessiveAdjective(), target.getName(),
                            target.possessiveAdjective()));
        } else {
            c.write(target.nameOrPossessivePronoun()
                            + " arms are firmly pinned, so she tries to kick you ineffectually. You catch her ankles and slowly begin kissing and licking your way "
                            + "up her legs while gently, but firmly, forcing them apart. By the time you reach her inner thighs, she's given up trying to resist. Since you no "
                            + "longer need to hold her legs, you can focus on her flooded pussy. You pump two fingers in and out of her while licking and sucking her clit. In no "
                            + "time at all, she's trembling and moaning in orgasm.");
        }
        gainAttraction(target, 1);
        target.gainAttraction(this, 1);
    }

    @Override
    public void gain(Item item) {
        GUI.gui
              .message("<b>You've gained " + item.pre() + item.getName() + ".</b>");
        super.gain(item);
    }

    @Override
    public String challenge(Character other) {
        return null;
    }

    @Override
    public void promptTrap(Encounter enc, Character target, Trap trap) {
        GUI.gui
              .message("Do you want to take the opportunity to ambush <b>" + target.getName() + "</b>?");
        assessOpponent(target);
        gui.message("<br/>");

        GameState.gameState.characterPool.getPlayer().promptOpportunity(target, trap, gui, enc);
    }

    @Override
    public void afterParty() {
    }

    @Override
    public void counterattack(Character target, Tactics type, Combat c) {
        switch (type) {
            case damage:
                c.write(this, "You dodge " + target.getName()
                                + "'s slow attack and hit her sensitive tit to stagger her.");
                target.pain(c, target, 4 + Math.min(Random.random(get(Attribute.Power)), 20));
                break;
            case pleasure:
                if (!target.crotchAvailable() || !target.hasPussy()) {
                    c.write(this, "You pull " + target.getName()
                                    + " off balance and lick her sensitive ear. She trembles as you nibble on her earlobe.");
                    target.body.pleasure(this, body.getRandom("tongue"), target.body.getRandom("ears"),
                                    4 + Math.min(Random.random(get(Attribute.Seduction)), 20), c);
                } else {
                    c.write(this, "You pull " + target.getName() + " to you and rub your thigh against her girl parts.");
                    target.body.pleasure(this, body.getRandom("feet"), target.body.getRandomPussy(),
                                    4 + Math.min(Random.random(get(Attribute.Seduction)), 20), c);
                }
                break;
            case fucking:
                if (c.getStance()
                     .sub(this)) {
                    Position reverse = c.getStance()
                                        .reverse(c, true);
                    if (reverse != c.getStance() && !BodyPart.hasOnlyType(reverse.bottomParts(), "strapon")) {
                        c.setStance(reverse, this, false);
                    } else {
                        c.write(this, Formatter.format(
                                        "{self:NAME-POSSESSIVE} quick wits find a gap in {other:name-possessive} hold and {self:action:slip|slips} away.",
                                        this, target));
                        c.setStance(new Neutral(this, c.getOpponent(this)), this, true);
                    }
                } else {
                    target.body.pleasure(this, body.getRandom("hands"), target.body.getRandomBreasts(),
                                    4 + Math.min(Random.random(get(Attribute.Seduction)), 20), c);
                    c.write(this, Formatter.format(
                                    "{self:SUBJECT-ACTION:pinch|pinches} {other:possessive} nipples with {self:possessive} hands as {other:subject-action:try|tries} to fuck {self:direct-object}. "
                                                    + "While {other:subject-action:yelp|yelps} with surprise, {self:subject-action:take|takes} the chance to pleasure {other:possessive} body.",
                                    this, target));
                }
                break;
            case stripping:
                Clothing clothes = target.stripRandom(c);
                if (clothes != null) {
                    c.write(this, "You manage to catch " + target.possessiveAdjective()
                                    + " hands groping your clothing, and in a swift motion you strip off her "
                                    + clothes.getName() + " instead.");
                } else {
                    c.write(this, "You manage to dodge " + target.possessiveAdjective()
                                    + " groping hands and give a retaliating slap in return.");
                    target.pain(c, target, 4 + Math.min(Random.random(get(Attribute.Power)), 20));
                }
                break;
            case positioning:
                if (c.getStance()
                     .dom(this)) {
                    c.write(this, "You outmanuever " + target.getName() + " and you exhausted her from the struggle.");
                    target.weaken(c, (int) this.modifyDamage(DamageType.stance, target, 15));
                } else {
                    c.write(this, target.getName()
                                    + " loses her balance while grappling with you. Before she can fall to the floor, you catch her from behind and hold her up.");
                    c.setStance(new Behind(this, target));
                }
                break;
            default:
                c.write(this, "You manage to dodge " + target.possessiveAdjective()
                                + " attack and give a retaliating slap in return.");
                target.pain(c, target, 4 + Math.min(Random.random(get(Attribute.Power)), 20));
        }
    }

    @Override
    public void eot(Combat c, Character opponent) {
        super.eot(c, opponent);
        if (opponent.has(Trait.pheromones) && opponent.getArousal()
                                                      .percent() >= 20
                        && opponent.rollPheromones(c)) {
            c.write(opponent, "<br/>Whenever you're near " + opponent.getName()
                            + ", you feel your body heat up. Something in her scent is making you extremely horny.");
            add(c, Pheromones.getWith(opponent, this, opponent.getPheromonePower(), 10));
        }
        if (opponent.has(Trait.sadist) && !is(Stsflag.masochism)) {
            c.write("<br/>"+ Formatter.capitalizeFirstLetter(
                            String.format("%s seem to shudder in arousal at the thought of pain.", subject())));
            add(c, new Masochistic(this));
        }
        if (has(Trait.RawSexuality)) {
            c.write(this, Formatter.format("{self:NAME-POSSESSIVE} raw sexuality turns both of you on.", this, opponent));
            temptNoSkillNoSource(c, opponent, arousal.max() / 25);
            opponent.temptNoSkillNoSource(c, this, opponent.arousal.max() / 25);
        }
        if (has(Trait.slime)) {
            if (hasPussy() && !body.getRandomPussy().moddedPartCountsAs(this, GooeyMod.INSTANCE)) {
                body.temporaryAddOrReplacePartWithType(body.getRandomPussy().applyMod(GooeyMod.INSTANCE), 999);
                c.write(this, 
                                Formatter.format("{self:NAME-POSSESSIVE} %s turned back into a gooey pussy.",
                                                this, opponent, body.getRandomPussy()));
            }
            if (hasDick() && !body.getRandomCock().moddedPartCountsAs(this, CockMod.slimy)) {
                body.temporaryAddOrReplacePartWithType(body.getRandomCock().applyMod(CockMod.slimy), 999);
                c.write(this, 
                                Formatter.format("{self:NAME-POSSESSIVE} %s turned back into a gooey pussy.",
                                                this, opponent, body.getRandomPussy()));
            }
        }
    }


    @Override
    public void emote(Emotion emo, int amt) {

    }

    @Override
    public String getPortrait(Combat c) {
        return null;
    }

    @Override
    public String action(String firstPerson, String thirdPerson) {
        return firstPerson;
    }

    @Override
    public Meter getWillpower() {
        return willpower;
    }

    @Override
    public String getType() {
        return type;
    }

    @Override
    public boolean resist3p(Combat c, Character target, Character assist) {
        return has(Trait.cursed);
    }

    @Override
    protected void resolveOrgasm(Combat c, Character opponent, BodyPart selfPart, BodyPart opponentPart, int times,
                    int totalTimes) {
        super.resolveOrgasm(c, opponent, selfPart, opponentPart, times, totalTimes);
        if (has(Trait.slimification) && times == totalTimes && getWillpower().percent() < 60 && !has(Trait.slime)) {
            c.write(this, Formatter.format("A powerful shiver runs through your entire body. Oh boy, you know where this"
                            + " is headed... Sure enough, you look down to see your skin seemingly <i>melt</i>,"
                            + " turning a translucent blue. You legs fuse together and collapse into a puddle."
                            + " It only takes a few seconds for you to regain some semblance of control over"
                            + " your amorphous body, but you're not going to switch back to your human"
                            + " form before this fight is over...", this, opponent));
            nudify();
            purge(c);
            addTemporaryTrait(Trait.slime, 999);
            add(c, new PlayerSlimeDummy(this));
            if (hasPussy() && !body.getRandomPussy().moddedPartCountsAs(this, GooeyMod.INSTANCE)) {
                body.temporaryAddOrReplacePartWithType(body.getRandomPussy().applyMod(GooeyMod.INSTANCE), 999);
                body.temporaryAddOrReplacePartWithType(new TentaclePart("slime filaments", "pussy", "slime", 0.0, 1.0, 1.0), 999);
            }
            if (hasDick() && !body.getRandomCock().moddedPartCountsAs(this, CockMod.slimy)) {
                body.temporaryAddOrReplacePartWithType(body.getRandomCock().applyMod(CockMod.slimy), 999);
            }
            BreastsPart part = body.getBreastsBelow(BreastsPart.h.getSize());
            if (part != null && body.getRandomBreasts() != BreastsPart.flat) {
                body.temporaryAddOrReplacePartWithType(part.upgrade(), 10);
            }
            body.temporaryAddOrReplacePartWithType(new GenericBodyPart("gooey skin", .6, 1.5, .8, "skin", ""), 999);
            body.temporaryAddOrReplacePartWithType(new TentaclePart("slime pseudopod", "back", "slime", 0.0, 1.0, 1.0), 999);
            if (level >= 21) {
                addTemporaryTrait(Trait.Sneaky, 999);
            }
            if (level >= 24) {
                addTemporaryTrait(Trait.shameless, 999);
            }
            if (level >= 27) {
                addTemporaryTrait(Trait.lactating, 999);
            }
            if (level >= 30) {
                addTemporaryTrait(Trait.addictivefluids, 999);
            }
            if (level >= 33) {
                addTemporaryTrait(Trait.autonomousPussy, 999);
            }
            if (level >= 36) {
                addTemporaryTrait(Trait.enthrallingjuices, 999);
            }
            if (level >= 39) {
                addTemporaryTrait(Trait.energydrain, 999);
            }
            if (level >= 42) {
                addTemporaryTrait(Trait.desensitized, 999);
            }
            if (level >= 45) {
                addTemporaryTrait(Trait.steady, 999);
            }
            if (level >= 50) {
                addTemporaryTrait(Trait.strongwilled, 999);
            }
        }
    }

    public void promptOpportunity(Character target, Trap trap, GUI gui, Encounter encounter) {
        List<LabeledValue<String>> choices = new ArrayList<>();
        choices.add(new LabeledValue<>("Attack", "Attack" + target.getName()));
        choices.add(new LabeledValue<>("Wait", "Wait"));
        try {
            String choice = gui.promptFuture(choices).get();
            switch (choice) {
                case "Attack":
                    encounter.parse(Encs.capitalizeontrap, this, target, trap);
                    break;
                case "Wait":
                    encounter.parse(Encs.wait, this, target);
                    break;
                default:
                    throw new AssertionError("Unknown Opportunity choice: " + choice);
            }
        } catch (InterruptedException e) {
            e.printStackTrace();
            Thread.currentThread().interrupt();
        } catch (ExecutionException e) {
            throw new RuntimeException(e);
        }
    }
}
