package nightgames.characters;

import com.google.gson.JsonObject;
import nightgames.actions.*;
import nightgames.areas.Area;
import nightgames.characters.body.BodyPart;
import nightgames.characters.custom.CharacterLine;
import nightgames.characters.custom.CommentSituation;
import nightgames.characters.custom.RecruitmentData;
import nightgames.combat.*;
import nightgames.ftc.FTCMatch;
import nightgames.global.*;
import nightgames.global.Formatter;
import nightgames.global.Random;
import nightgames.gui.GUI;
import nightgames.items.Item;
import nightgames.items.clothing.Clothing;
import nightgames.items.clothing.ClothingSlot;
import nightgames.skills.Nothing;
import nightgames.skills.Skill;
import nightgames.skills.Stage;
import nightgames.skills.Tactics;
import nightgames.skills.damage.DamageType;
import nightgames.skills.strategy.CombatStrategy;
import nightgames.skills.strategy.DefaultStrategy;
import nightgames.stance.Behind;
import nightgames.stance.Neutral;
import nightgames.stance.Position;
import nightgames.status.*;
import nightgames.trap.Trap;

import java.util.*;
import java.util.stream.Collectors;

public class NPC extends Character {
    public static final NPC noneCharacter = new NPC("none", 1, null);
    public Personality ai;
    public HashMap<Emotion, Integer> emotes;
    public Emotion mood;
    public Plan plan;
    private boolean fakeHuman;
    public boolean isStartCharacter = false;
    public boolean available;   // True when the character has been unlocked, whether at the start or by fulfilling unlock requirements.
    private List<CombatStrategy> personalStrategies;
    private List<CombatScene> postCombatScenes;
    private Map<String, List<CharacterLine>> lines;

    public NPC(String name, int level, Personality ai) {
        super(name, level);
        this.ai = ai;
        this.lines = new HashMap<>();
        fakeHuman = false;
        emotes = new HashMap<>();
        for (Emotion e : Emotion.values()) {
            emotes.put(e, 0);
        }
        mood = Emotion.confident;
        initialGender = CharacterSex.female;
        personalStrategies = new ArrayList<>();
        postCombatScenes = new ArrayList<>();
    }

    public static Character noneCharacter() {
        return noneCharacter;
    }

    protected void addPersonalStrategy(CombatStrategy strategy) {
        personalStrategies.add(strategy);
    }

    protected void addCombatScene(CombatScene scene) {
        postCombatScenes.add(scene);
    }
    
    public List<CombatScene> getPostCombatScenes() {
        return Collections.unmodifiableList(postCombatScenes);
    }

    @Override
    public String describe(int per, Combat c) {
        StringBuilder description = new StringBuilder(ai.describeAll(c, this));
        boolean wroteStatus = false;
        for (Status s : status) {
            String statusDesc = s.describe(c);
            if (!statusDesc.isEmpty()) {
                description.append(statusDesc).append("<br/>");
                wroteStatus = true;
            }
        }
        if (wroteStatus) {
            description.append("<br/>");
        }
        description.append(outfit.describe(this));
        description.append(observe(per));
        description.append(c.getCombatantData(this).getManager().describe(this));
        return description.toString();
    }

    private String observe(int per) {
        String visible = "";
        if (is(Stsflag.unreadable)) {
            return visible;
        }
        if (per >= 9) {
            visible += "{self:POSSESSIVE} arousal is at " + arousal.percent() + "%<br/>";
        }
        if (per >= 8) {
            visible += "{self:POSSESSIVE} stamina is at " + stamina.percent() + "%<br/>";
        }
        if (per >= 9) {
            visible += "{self:POSSESSIVE} willpower is at " + willpower.percent() + "%<br/>";
        }
        if (per >= 7) {
            visible += "{self:PRONOUN} looks " + mood.name() + "<br/>";
        }
        if (per >= 7 && per < 9) {
            if (arousal.percent() >= 75) {
                visible = visible
                                + "{self:PRONOUN}'s dripping with arousal and breathing heavily. {self:PRONOUN}'s at least 3/4 of the way to orgasm<br/>";
            } else if (arousal.percent() >= 50) {
                visible = visible + "{self:PRONOUN}'s showing signs of arousal. {self:PRONOUN}'s at least halfway to orgasm<br/>";
            } else if (arousal.percent() >= 25) {
                visible = visible + "{self:PRONOUN}'s starting to look noticeably arousal, maybe a quarter of {self:possessive} limit<br/>";
            }
            if (willpower.percent() <= 75) {
                visible = visible + "{self:PRONOUN} still seems ready to fight.<br/>";
            } else if (willpower.percent() <= 50) {
                visible = visible + "{self:PRONOUN} seems a bit unsettled, but {self:pronoun} still has some spirit left in {self:direct-object}.<br/>";
            } else if (willpower.percent() <= 25) {
                visible = visible + "{self:POSSESSIVE} eyes seem glazed over and ready to give in.<br/>";
            }
        }
        if (per >= 6 && per < 8) {
            if (stamina.percent() <= 33) {
                visible = visible + "{self:PRONOUN} looks a bit unsteady on {self:possessive} feet<br/>";
            } else if (stamina.percent() <= 66) {
                visible = visible + "{self:PRONOUN}'s starting to look tired<br/>";
            }
        }
        if (per >= 3 && per < 7) {
            if (arousal.percent() >= 50) {
                visible = visible + "{self:PRONOUN}'s showing clear sign of arousal. You're definitely getting to {self:direct-object}.<br/>";
            }
            if (willpower.percent() <= 50) {
                visible = visible + "{self:PRONOUN} seems a bit distracted and unable to look you in the eye.<br/>";
            }
        }
        if (per >= 4 && per < 6) {
            if (stamina.percent() <= 50) {
                visible = visible + "{self:PRONOUN} looks pretty tired<br/>";
            }
        }

        if (per >= 5) {
            visible += Stage.describe(this);
        }
        if (per >= 6 && status.size() > 0) {
            visible += "List of statuses:<br/><i>";
            visible += status.stream().filter(s -> !s.flags().contains(Stsflag.disguised) || per >= 9).map(Status::toString).collect(Collectors.joining(", "));
            visible += "</i><br/>";
        }
        
        return Formatter.format(visible, this, null);
    }

    @Override
    public void victory(Combat c, Result flag) {
        Character target;
        if (c.p1 == this) {
            target = c.p2;
        } else {
            target = c.p1;
        }
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
        
        String scene;
        // If the opponent is the player, and the two have switched perspective,
        // then we show the opposite scene.
        // We have to use reference equality, as the names have also switched
        if (target.human() && GameState.state().exchanged == this) { 
            scene = ai.defeat(c, flag);
        } else {
            scene = ai.victory(c, flag);
        }
        
        gainAttraction(target, 1);
        target.gainAttraction(this, 2);
    }

    // TODO: Having both winner and loser handled by one character's code is confusing. Move the common parts elsewhere (up a level?)
    @Override
    public void defeat(Combat c, Result flag) {
        Character target;
        if (c.p1 == this) {
            target = c.p2;
        } else {
            target = c.p1;
        }
        gainXP(getDefeatXP(target));
        target.gainXP(target.getVictoryXP(this));
        arousal.empty();
        if (!target.human() || !Match.getMatch().condition.name().equals("norecovery")) {
            target.arousal.empty();
        }
        if (this.has(Trait.insatiable)) {
            arousal.restore((int) (arousal.max() * .2));
        }
        if (target.has(Trait.insatiable)) {
            target.arousal.restore((int) (arousal.max() * .2));
        }
        target.dress(c);
        undress(c);
        target.gainTrophy(c, this);
        defeated(target);
        
        String scene;
        // If the opponent is the player, and the two have switched perspective,
        // then we show the opposite scene.
        // We have to use reference equality, as the names have also switched
        if (target.human() && GameState.state().exchanged == this) { 
            scene = ai.victory(c, flag);
        } else {
            scene = ai.defeat(c, flag);
        }
        
        c.write(scene);
        gainAttraction(target, 2);
        target.gainAttraction(this, 1);
    }

    @Override
    public void intervene3p(Combat c, Character target, Character assist) {
        gainXP(getAssistXP(target));
        target.defeated(this);
        String scene;
        if (target.human() && GameState.state().exchanged == this) { 
            scene = ((Player) target).getInterveneScene(this, assist);
        } else {
            scene = ai.intervene3p(c, target, assist);
        }
        c.write(scene);
        assist.gainAttraction(this, 1);
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
        c.updateAndClearMessage();
        c.write(ai.victory3p(c, target, assist));
        gainAttraction(target, 1);
    }

    @Override
    public boolean resist3p(Combat combat, Character intruder, Character assist) {
        if (has(Trait.cursed)) {
            GUI.gui.message(ai.resist3p(combat, intruder, assist));
            return true;
        }
        return false;
    }

    @Override
    public boolean chooseSkill(Combat c) throws InterruptedException {
        Character target = c.getOpponent(this);
        if (target.human() && DebugFlags.isDebugOn(DebugFlags.DEBUG_SKILL_CHOICES)) {
            return chooseSkillInteractive(c);
        } else {
            return chooseSkill(c, target);
        }
    }

    private boolean chooseSkill(Combat c, Character target) {
        // if there's no strategy, try getting a new one.
        if (!c.getCombatantData(this).getStrategy().isPresent()) {
            c.getCombatantData(this).setStrategy(c, this, pickStrategy(c));
        }
        // if the strategy is out of moves, try getting a new one.
        Collection<Skill> possibleSkills = c.getCombatantData(this).getStrategy().get().nextSkills(c, this);
        if (possibleSkills.isEmpty()) {
            if (DebugFlags.isDebugOn(DebugFlags.DEBUG_STRATEGIES)) {
                System.out.printf("%s has no moves available for strategy %s, picking a new one\n", this.getTrueName(), c.getCombatantData(this).getStrategy().get().getClass().getSimpleName());
            }
            c.getCombatantData(this).setStrategy(c, this, pickStrategy(c));
            possibleSkills = c.getCombatantData(this).getStrategy().get().nextSkills(c, this);
        }
        if (DebugFlags.isDebugOn(DebugFlags.DEBUG_STRATEGIES)) {
            System.out.println("next skills: " +  possibleSkills);
        }
        // if there are still no moves, just use all available skills for this turn and try again next turn.
        if (possibleSkills.isEmpty()) {
            if (DebugFlags.isDebugOn(DebugFlags.DEBUG_STRATEGIES)) {
                System.out.printf("%s has no moves available for strategy %s\n", this.getTrueName(), c.getCombatantData(this).getStrategy().get().getClass().getSimpleName());
            }
            possibleSkills = getSkills();
        } else {
            if (DebugFlags.isDebugOn(DebugFlags.DEBUG_STRATEGIES)) {
                System.out.printf("%s is using strategy %s\n", this.getTrueName(), c.getCombatantData(this).getStrategy().get().getClass().getSimpleName());
            }
        }
        HashSet<Skill> available = new HashSet<>();
        for (Skill act : possibleSkills) {
            if (Skill.skillIsUsable(c, act) && cooldownAvailable(act)) {
                available.add(act);
            }
        }
        Skill.filterAllowedSkills(c, available, this, target);
        if (available.size() == 0) {
            available.add(new Nothing(this));
        }
        c.chooseSkill(this, ai.chooseSkill(available, c));
        return false;
    }

    private CombatStrategy pickStrategy(Combat c) {
        if (Random.random(100) < 60 ) {
            if (DebugFlags.isDebugOn(DebugFlags.DEBUG_STRATEGIES)) {
                System.out.println("Using default strategy");
            }
            // most of the time don't bother using a strategy.
            return new DefaultStrategy();
        }

        Map<Double, CombatStrategy> stratsWithCumulativeWeights = new HashMap<>();
        DefaultStrategy defaultStrat = new DefaultStrategy();
        double lastWeight = defaultStrat.weight(c, this);
        stratsWithCumulativeWeights.put(lastWeight, defaultStrat);
        List<CombatStrategy> allStrategies = new ArrayList<>(CombatStrategy.availableStrategies);
        allStrategies.addAll(personalStrategies);
        for (CombatStrategy strat: allStrategies) {
            if (strat.weight(c, this) < .01 || strat.nextSkills(c, this).isEmpty()) {
                continue;
            }
            lastWeight += strat.weight(c, this);
            stratsWithCumulativeWeights.put(lastWeight, strat);
        }
        if (DebugFlags.isDebugOn(DebugFlags.DEBUG_STRATEGIES)) {
            System.out.println("Available strategies: "+ stratsWithCumulativeWeights);
        }
        double random = Random.randomdouble() * lastWeight;
        for (Map.Entry<Double, CombatStrategy> entry: stratsWithCumulativeWeights.entrySet()) {
            if (random < entry.getKey()) {
                return entry.getValue();
            }
        }
        // we should have picked something, but w/e just return the default if we need to
        return defaultStrat;
    }

    public Skill actFast(Combat c) {
        HashSet<Skill> available = new HashSet<>();
        Character target;
        if (c.p1 == this) {
            target = c.p2;
        } else {
            target = c.p1;
        }
        for (Skill act : getSkills()) {
            if (Skill.skillIsUsable(c, act) && cooldownAvailable(act)) {
                available.add(act);
            }
        }
        Skill.filterAllowedSkills(c, available, this, target);
        if (available.size() == 0) {
            available.add(new Nothing(this));
        }
        return ai.chooseSkill(available, c);
    }

    @Override
    public boolean human() {
        return fakeHuman;
    }

    public void setFakeHuman(boolean val) {
        fakeHuman = val;
    }

    @Override
    public void draw(Combat c, Result flag) {
        Character target;
        if (c.p1 == this) {
            target = c.p2;
        } else {
            target = c.p1;
        }
        gainXP(getVictoryXP(target));
        target.gainXP(getVictoryXP(this));
        arousal.empty();
        target.arousal.empty();
        if (this.has(Trait.insatiable)) {
            arousal.restore((int) (arousal.max() * .2));
        }
        if (target.has(Trait.insatiable)) {
            target.arousal.restore((int) (arousal.max() * .2));
        }
        target.undress(c);
        undress(c);
        target.gainTrophy(c, this);
        gainTrophy(c, target);
        target.defeated(this);
        defeated(target);
        c.write(ai.draw(c, flag));
        gainAttraction(target, 4);
        target.gainAttraction(this, 4);
        if (getAffection(target) > 0) {
            gainAffection(target, 1);
            target.gainAffection(this, 1);
        }
    }

    public String getRandomLineFor(String lineType, Combat c, Character other) {
        Map<String, List<CharacterLine>> lines = this.lines;
        Disguised disguised = (Disguised) getStatus(Stsflag.disguised);
        if (disguised != null) {
            lines = disguised.getTarget().getLines();
        }
        return Formatter.format(Random.pickRandom(lines.get(lineType)).orElse((cb, sf, ot) -> "").getLine(c, this, other), this, other);
    }

    @Override
    public String challenge(Character other) {
        return getRandomLineFor(CharacterLine.CHALLENGE, null, other);
    }

    @Override
    public String orgasmLiner(Combat c, Character target) {
        return getRandomLineFor(CharacterLine.ORGASM_LINER, c, target);
    }

    @Override
    public String makeOrgasmLiner(Combat c, Character target) {
        return getRandomLineFor(CharacterLine.MAKE_ORGASM_LINER, c, target);
    }

    @Override
    public String bbLiner(Combat c, Character target) {
        return getRandomLineFor(CharacterLine.BB_LINER, c, target);
    }

    @Override
    public String nakedLiner(Combat c, Character target) {
        return getRandomLineFor(CharacterLine.NAKED_LINER, c, target);
    }

    @Override
    public String stunLiner(Combat c, Character target) {
        return getRandomLineFor(CharacterLine.STUNNED_LINER, c, target);
    }

    @Override
    public String taunt(Combat c, Character target) {
        return getRandomLineFor(CharacterLine.TAUNT_LINER, c, target);
    }

    @Override
    public String temptLiner(Combat c, Character target) {
        return getRandomLineFor(CharacterLine.TEMPT_LINER, c, target);
    }

    @Override
    public void detect() {
    }

    @Override
    public Optional<Action> move() {
        Action move = null;
        if (DebugFlags.isDebugOn(DebugFlags.DEBUG_SCENE)) {
            System.out.println(getTrueName() + " is moving with state " + state);
        }
        if (state == State.combat) {
            if (location != null && location.activeEncounter != null) {
                if (DebugFlags.isDebugOn(DebugFlags.DEBUG_SCENE)) {
                    System.out.println(getTrueName() + " is battling in the " + location.name);
                }
                location.activeEncounter.battle();
            } else {
                if (DebugFlags.isDebugOn(DebugFlags.DEBUG_SCENE)) {
                    System.out.println(getTrueName() + " is done battling in the " + location.name);
                }
            }
        } else if (busy > 0) {
            busy--;
        } else if (this.is(Stsflag.enthralled) && !has(Trait.immobile)) {
            Character master;
            master = ((Enthralled) getStatus(Stsflag.enthralled)).master;
            Move compelled = findPath(master.location);
            if (compelled != null) {
                move = compelled;
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
            state = State.ready;
        } else if (state == State.masturbating) {
            masturbate();
        } else {
            if (!location.hasEncounter()) {
                
                HashSet<Action> moves = new HashSet<>();
                HashSet<Movement> radar = new HashSet<>();
                FTCMatch match;
                if (Flag.checkFlag(Flag.FTC) && allowedActions().isEmpty()) {
                    match = (FTCMatch) Match.getMatch();
                    if (match.isPrey(this) && match.getFlagHolder() == null) {
                        moves.add(findPath(match.gps("Central Camp")));
                        if (DebugFlags.isDebugOn(DebugFlags.DEBUG_FTC))
                            System.out.println(getTrueName() + " moving to get flag (prey)");
                    } else if (!match.isPrey(this) && has(Item.Flag) && !match.isBase(this, location)) {
                        moves.add(findPath(match.getBase(this)));
                        if (DebugFlags.isDebugOn(DebugFlags.DEBUG_FTC))
                            System.out.println(getTrueName() + " moving to deliver flag (hunter)");
                    } else if (!match.isPrey(this) && has(Item.Flag) && match.isBase(this, location)) {
                        if (DebugFlags.isDebugOn(DebugFlags.DEBUG_FTC))
                            System.out.println(getTrueName() + " delivering flag (hunter)");
                        new Resupply().execute(this);
                    }
                }
                if (!has(Trait.immobile) && moves.isEmpty()) {
                    for (Area path : location.adjacent) {
                        moves.add(new Move(path));
                        if (path.ping(get(Attribute.Perception))) {
                            radar.add(path.id());
                        }
                    }
                    if (getPure(Attribute.Cunning) >= 28) {
                        for (Area path : location.shortcut) {
                            moves.add(new Shortcut(path));
                        }
                    }
                    if(getPure(Attribute.Ninjutsu)>=5){
                        for(Area path:location.jump){
                            moves.add(new Leap(path));
                        }
                    }
                }

                move = pickAction(allowedActions(), moves, radar);
            }
        }
        return Optional.ofNullable(move);
    }

    private Action pickAction(Collection<Action> available, Collection<Action> moves, Collection<Movement> radar) {
        if (available.isEmpty()) {
            available.addAll(Action.getActions());
            available.addAll(moves);
        }
        available.removeIf(a -> a == null || !a.usable(this));
        return ai.move(available, radar);
    }

    @Override public void doAction(Action action) {
        if (location.humanPresent()) {
            GUI.gui.message("You notice " + getName() + action.execute(this).describe());
        } else {
            action.execute(this);
        }
    }

    private void pickAndDoAction(Collection<Action> available, Collection<Action> moves, Collection<Movement> radar) {
        if (available.isEmpty()) {
            available.addAll(Action.getActions());
            available.addAll(moves);
        }
        available.removeIf(a -> a == null || !a.usable(this));
        if (location.humanPresent()) {
            GUI.gui.message("You notice " + getName() + ai.move(available, radar).execute(this).describe());
        } else {
            ai.move(available, radar).execute(this);
        }
    }

    @Override
    public FightIntent faceOff(Character opponent, Encounter enc) {
        if (ai.fightFlight(opponent)) {
            return FightIntent.fight;
        } else if (has(Item.SmokeBomb)) {
            return FightIntent.smoke;
        } else {
            return FightIntent.flee;
        }
    }

    @Override
    public Encs spy(Character opponent, Encounter enc) {
        if (ai.attack(opponent)) {
            return Encs.ambush;
        } else {
            return Encs.wait;
        }
    }

    @Override public void ding(Combat c, int levelsToGain) {
        super.ding(c, levelsToGain);
        if (c != null && c.isBeingObserved()) {
            Formatter.writeIfCombatUpdateImmediately(c, this,
                            Formatter.format("{self:subject} gained %d levels!", this, this, levelsToGain));
        }
    }

    @Override
    public void ding(Combat c) {
        level++;
        ai.ding(this);
    }

    @Override
    public Encs showerSceneResponse(Character target, Encounter encounter) {
        Encs response;
        if (this.has(Item.Aphrodisiac)) {
            // encounter.aphrodisiactrick(this, target);
            response = Encs.aphrodisiactrick;
        } else if (!target.mostlyNude() && Random.random(3) >= 2) {
            // encounter.steal(this, target);
            response = Encs.stealclothes;
        } else {
            // encounter.showerambush(this, target);
            response = Encs.showerattack;
        }
        return response;
    }

    @Override public JsonObject save() {
        JsonObject saveJson = super.save();
        saveJson.addProperty("available", available);
        return saveJson;
    }

    @Override
    public void decideIntervene(Encounter enc, Character p1, Character p2) {
        if (Random.random(20) + getAffection(p1) + (p1.has(Trait.sympathetic) ? 10 : 0) >= Random.random(20)
                        + getAffection(p2) + (p2.has(Trait.sympathetic) ? 10 : 0)) {
            enc.intrude(this, p1);
        } else {
            enc.intrude(this, p2);
        }
    }

    @Override
    public void promptTrap(Encounter enc, Character target, Trap trap) {
        if (ai.attack(target) && (!target.human() || !DebugFlags.isDebugOn(DebugFlags.DEBUG_SPECTATE))) {
            enc.trap(this, target, trap);
        } else {
            location.endEncounter();
        }
    }

    public void addLine(String lineType, CharacterLine line) {
        lines.computeIfAbsent(lineType, type -> new ArrayList<>());
        lines.get(lineType).add(line);
    }

    @Override
    public void afterParty() {
        GUI.gui.message(getRandomLineFor(CharacterLine.NIGHT_LINER, null, GameState.gameState.characterPool.getPlayer()));
    }

    public void daytime(int time) {
        ai.rest(time);
    }

    @Override
    public Emotion getMood() {
        return mood;
    }

    @Override
    public void counterattack(Character target, Tactics type, Combat c) {
        switch (type) {
            case damage:
                c.write(this, Formatter.format("{self:SUBJECT} avoids your clumsy attack and "
                                + "swings {self:possessive} fist into your nuts.", this, target));
                target.pain(c, target, 4 + Math.min(Random.random(get(Attribute.Power)), 20));
                break;
            case pleasure:
                if (target.hasDick()) {
                    if (target.crotchAvailable()) {
                        c.write(this, Formatter.format("{self:SUBJECT} catches you by the penis and "
                                        + "rubs your sensitive glans.", this, target));
                        target.body.pleasure(this, body.getRandom("hands"), target.body.getRandom("cock"),
                                        4 + Math.min(Random.random(get(Attribute.Seduction)), 20), c);
                    } else {
                        c.write(this, Formatter.format("{self:SUBJECT} catches you as you approach "
                                        + "and grinds {self:possessive} knee into the tent in your "
                                        + target.getOutfit().getTopOfSlot(ClothingSlot.bottom) +".",
                                        this, target));
                        target.body.pleasure(this, body.getRandom("legs"), target.body.getRandom("cock"),
                                        4 + Math.min(Random.random(get(Attribute.Seduction)), 20), c);
                    }
                } else {
                    c.write(this, Formatter.format("{self:SUBJECT} pulls you off balance and licks your sensitive ear. "
                                    + "You tremble as {self:pronoun} nibbles on your earlobe.",
                                    this, target));
                    target.body.pleasure(this, body.getRandom("tongue"), target.body.getRandom("ears"),
                                    4 + Math.min(Random.random(get(Attribute.Seduction)), 20), c);
                }
                break;
            case fucking:
                if (c.getStance().sub(this)) {
                    Position reverse = c.getStance().reverse(c, true);
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
                                                    + "While {other:subject-action:yelp|yelps} with surprise, {self:subject-action:take|takes} the chance to pleasure {other:possessive} body",
                                    this, target));
                }

                break;
            case stripping:
                Clothing clothes = target.stripRandom(c);
                if (clothes != null) {
                    c.write(this, Formatter.format("{self:SUBJECT} manages to catch you groping {self:possessive}"
                                    + " clothing, and in a swift motion strips off your "
                                    + clothes.getName() + ".", this, target));
                } else {
                    c.write(this, getName()
                                    + " manages to dodge your groping hands and gives a retaliating slap in return.");
                    target.pain(c, target, 4 + Math.min(Random.random(get(Attribute.Power)), 20));
                }
                break;
            case positioning:
                if (c.getStance().dom(this)) {
                    c.write(this, getName() + " outmanuevers you and you're exhausted from the struggle.");
                    target.weaken(c, (int) this.modifyDamage(DamageType.stance, target, 15));
                } else {
                    c.write(this, getName() + " outmanuevers you and catches you from behind when you stumble.");
                    c.setStance(new Behind(this, target));
                }
                break;
            default:
                c.write(this, getName() + " manages to dodge your attack and gives a retaliating slap in return.");
                target.pain(c, target, 4 + Math.min(Random.random(get(Attribute.Power)), 20));
        }
    }

    public Skill prioritize(ArrayList<WeightedSkill> plist) {
        if (plist.isEmpty()) {
            return null;
        }
        double sum = 0;
        ArrayList<WeightedSkill> wlist = new ArrayList<>();
        for (WeightedSkill wskill : plist) {
            sum += wskill.weight;
            wlist.add(new WeightedSkill(sum, wskill.skill));
            if (DebugFlags.isDebugOn(DebugFlags.DEBUG_SKILLS)) {
                System.out.printf("%.1f %s\n", sum, wskill.skill);
            }
        }

        if (wlist.isEmpty()) {
            return null;
        }
        double s = Random.randomdouble() * sum;
        for (WeightedSkill wskill : wlist) {
            if (DebugFlags.isDebugOn(DebugFlags.DEBUG_SKILLS)) {
                System.out.printf("%.1f/%.1f %s\n", wskill.weight, s, wskill.skill);
            }
            if (wskill.weight > s) {
                return wskill.skill;
            }
        }
        return plist.get(plist.size() - 1).skill;
    }

    @Override
    public void emote(Emotion emo, int amt) {
        if (DebugFlags.isDebugOn(DebugFlags.DEBUG_MOOD)) {
            System.out.printf("%s: %+d %s", getTrueName(), amt, emo.name());
        }
        if (emo == mood) {
            // if already this mood, cut gain by half
            amt = Math.max(1, amt / 2);
        }
        emotes.put(emo, emotes.get(emo) + amt);
    }

    public Emotion moodSwing(Combat c) {
        Emotion current = mood;
        for (Emotion e : emotes.keySet()) {
            if (ai.checkMood(c, e, emotes.get(e))) {
                emotes.put(e, 0);
                // cut all the other emotions by half so that the new mood
                // persists for a bit
                for (Emotion e2 : emotes.keySet()) {
                    emotes.put(e2, emotes.get(e2) / 2);
                }
                mood = e;
                if (DebugFlags.isDebugOn(DebugFlags.DEBUG_MOOD)) {
                    System.out.printf("Moodswing: %s is now %s\n", getTrueName(), mood.name());
                }
                if (c.p1.human() || c.p2.human()) {
                    GUI.gui.loadPortrait(c, c.p1, c.p2);
                }
                return e;
            }
        }
        return current;
    }

    @Override
    public void eot(Combat c, Character opponent) {
        super.eot(c, opponent);
        ai.eot(c, opponent);
        if (opponent.has(Trait.pheromones) && opponent.getArousal().percent() >= 20 && opponent.rollPheromones(c)) {
            c.write(opponent, Formatter.format("<br/>{other:SUBJECT-ACTION:see} {self:subject} swoon slightly "
                            + "as {self:pronoun-action:get} close to {other:direct-object}. "
                            + "Seems like {self:pronoun-action:are} starting to feel "
                            + "the effects of {other:possessive} musk.", this, opponent));
            add(c, Pheromones.getWith(opponent, this, opponent.getPheromonePower(), 10));
        }
        if (has(Trait.RawSexuality)) {
            c.write(this, Formatter.format("{self:NAME-POSSESSIVE} raw sexuality turns both of you on.", this, opponent));
            temptNoSkillNoSource(c, opponent, getArousal().max() / 20);
            opponent.temptNoSkillNoSource(c, this, opponent.getArousal().max() / 20);
        }
        if (c.getStance().dom(this)) {
            emote(Emotion.dominant, 20);
            emote(Emotion.confident, 10);
        } else if (c.getStance().sub(this)) {
            emote(Emotion.nervous, 15);
            emote(Emotion.desperate, 10);
        }
        if (opponent.mostlyNude()) {
            emote(Emotion.horny, 15);
            emote(Emotion.confident, 10);
        }
        if (mostlyNude()) {
            emote(Emotion.nervous, 10);
            if (has(Trait.exhibitionist)) {
                emote(Emotion.horny, 20);
            }
        }
        if (opponent.getArousal().percent() >= 75) {
            emote(Emotion.confident, 20);
        }

        if (getArousal().percent() >= 50) {
            emote(Emotion.horny, getArousal().percent() / 4);
        }

        if (getArousal().percent() >= 50) {
            emote(Emotion.desperate, 10);
        }
        if (getArousal().percent() >= 75) {
            emote(Emotion.desperate, 20);
            emote(Emotion.nervous, 10);
        }
        if (getArousal().percent() >= 90) {
            emote(Emotion.desperate, 20);
        }
        if (!canAct()) {
            emote(Emotion.desperate, 10);
        }
        if (!opponent.canAct()) {
            emote(Emotion.dominant, 20);
        }
        moodSwing(c);
    }

    @Override
    public NPC clone() throws CloneNotSupportedException {
        return (NPC) super.clone();
    }

    @Override
    protected void resolveOrgasm(Combat c, Character opponent, BodyPart selfPart, BodyPart opponentPart, int times,
                    int totalTimes) {
        super.resolveOrgasm(c, opponent, selfPart, opponentPart, times, totalTimes);
        ai.resolveOrgasm(c, this, opponent, selfPart, opponentPart, times, totalTimes);
    }

    @Override
    public String getPortrait(Combat c) {
        Disguised disguised = (Disguised) getStatus(Stsflag.disguised);
        if (disguised != null && !c.isEnded()) {
            return disguised.getTarget().ai.image(c);
        }
        return ai.image(c);
    }

    @Override
    public String getType() {
        return ai.getType();
    }

    @Override public void load(JsonObject object) {
        super.load(object);
        // Prior to adding the available field, characters appearing in save files were assumed to be unlocked.
        if (object.has("available")) {
            available = object.get("available").getAsBoolean();
        } else {
            Flag.flag(Flag.LegacyCharAvailableSave);
            available = true;
        }
    }

    public RecruitmentData getRecruitmentData() {
        return ai.getRecruitmentData();
    }

    @Override
    public double dickPreference() {
        return ai instanceof Eve ? 10.0 : super.dickPreference();
    }

    public Optional<String> getComment(Combat c) {
        // can't really talk when they're disabled
        if (!canRespond()) {
            return Optional.empty();
        }
        Set<CommentSituation> applicable = CommentSituation.getApplicableComments(c, this, c.getOpponent(this));
        Set<CommentSituation> forbidden = EnumSet.allOf(CommentSituation.class);
        forbidden.removeAll(applicable);
        Map<CommentSituation, String> comments = ai.getComments(c);
        forbidden.forEach(comments::remove);
        if (comments.isEmpty() || comments.size() == 1 && comments.containsKey(CommentSituation.NO_COMMENT))
            return Optional.empty();
        return Random.pickRandom(new ArrayList<>(comments.values()));
    }
    
    public Emotion moodSwing() {
        Emotion current = mood;
        int max=emotes.get(current);
        for(Emotion e: emotes.keySet()){
            if(emotes.get(e)>max){
                mood=e;
                max=emotes.get(e);
            }
        }
        return mood;
    }
    
    public void decayMood(){
        for(Emotion e: emotes.keySet()){
            if(mostlyNude()&&!has(Trait.exhibitionist)&&!has(Trait.shameless)&& e==Emotion.nervous){
                emotes.put(e, emotes.get(e)-((emotes.get(e)-50)/2));
            }else if(mostlyNude()&&!has(Trait.exhibitionist)&& e==Emotion.horny){
                emotes.put(e, emotes.get(e)-((emotes.get(e)-50)/2));
            }
            else if(!mostlyNude()&&e==Emotion.confident){
                emotes.put(e, emotes.get(e)-((emotes.get(e)-50)/2));
            }
            else{
                if(emotes.get(e)>=5){
                    emotes.put(e, emotes.get(e)-(emotes.get(e)/2));
                }
            }
        }
    }
    
    @Override
    public void upkeep() {
        super.upkeep();
        moodSwing();
        decayMood();
        update();
    }

    public Map<String, List<CharacterLine>> getLines() {
        return lines;
    }

}
