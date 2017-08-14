package nightgames.global;

import nightgames.Resources.ResourceLoader;
import nightgames.characters.*;
import nightgames.daytime.Daytime;
import nightgames.gui.GUI;
import nightgames.items.clothing.Clothing;
import nightgames.json.JsonUtils;
import nightgames.skills.Skill;
import nightgames.start.PlayerConfiguration;
import nightgames.start.StartConfiguration;

import java.io.InputStreamReader;
import java.util.*;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;
import java.util.stream.Collectors;

/**
 * Creates, destroys, and maintains the state of a running game.
 */
public class GameState {
    private volatile static GameState gameState;
    public static double moneyRate = 1.0;
    public static double xpRate = 1.0;
    private static boolean ingame = false;
    private volatile boolean run;

    public GameState() {
        gameState = this;
    }

    public void newGame(String playerName, Optional<StartConfiguration> config, List<Trait> pickedTraits,
                    CharacterSex pickedGender, Map<Attribute, Integer> selectedAttributes) {
        Optional<PlayerConfiguration> playerConfig = config.map(c -> c.player);
        Collection<String> cfgFlags = config.map(StartConfiguration::getFlags).orElse(new ArrayList<>());
        CharacterPool.human = new Player(playerName, pickedGender, playerConfig, pickedTraits, selectedAttributes);
        if(CharacterPool.human.has(Trait.largereserves)) {
            CharacterPool.human.getWillpower().gain(20);
        }
        CharacterPool.players.add(CharacterPool.human);
        if (GUI.gui != null) {
            GUI.gui.populatePlayer(CharacterPool.human);
        }
        Skill.buildSkillPool(CharacterPool.human);
        Clothing.buildClothingTable();
        Skill.learnSkills(CharacterPool.human);
        CharacterPool.rebuildCharacterPool(config);
        // Add starting characters to players
        CharacterPool.players.addAll(CharacterPool.characterPool.values().stream().filter(npc -> npc.isStartCharacter).collect(Collectors.toList()));
        if (!cfgFlags.isEmpty()) {
            Flag.flags = cfgFlags.stream().collect(Collectors.toSet());
        }
        Map<String, Boolean> configurationFlags = JsonUtils.mapFromJson(JsonUtils.rootJson(new InputStreamReader(ResourceLoader.getFileResourceAsStream("data/globalflags.json"))).getAsJsonObject(), String.class, Boolean.class);
        configurationFlags.forEach((flag, val) -> Flag.setFlag(flag, val));
        Time.time = Time.NIGHT;
        Time.date = 1;
        Flag.setCharacterDisabledFlag(CharacterPool.getNPCByType("Yui"));
        Flag.setFlag(Flag.systemMessages, true);
    }

    /**
     * Loads game state data into static fields from SaveData object.
     *
     * @param data A SaveData object, as loaded from save files.
     */
    protected GameState loadData(SaveData data) {
        CharacterPool.players.addAll(data.players);
        CharacterPool.players.stream().filter(c -> c instanceof NPC).forEach(
                        c -> CharacterPool.characterPool.put(c.getType(), (NPC) c));
        Flag.flags.addAll(data.flags);
        Flag.counters.putAll(data.counters);
        Time.date = data.date;
        Time.time = data.time;
        GUI.gui.fontsize = data.fontsize;
        GUI.gui.populatePlayer(CharacterPool.human);
        return this;
    }

    public static GameState state() {
        return gameState;
    }

    protected static SaveData saveData() {
        SaveData data = new SaveData();
        data.players.addAll(CharacterPool.players);
        data.flags.addAll(Flag.flags);
        data.counters.putAll(Flag.counters);
        data.time = Time.time;
        data.date = Time.date;
        data.fontsize = GUI.gui.fontsize;
        return data;
    }

    protected static void resetForLoad() {
        CharacterPool.players.clear();
        Flag.flags.clear();
        GUI.gui.clearText();
        CharacterPool.human = new Player("Dummy");
        GUI.gui.purgePlayer();
        Skill.buildSkillPool(CharacterPool.human);
        Clothing.buildClothingTable();
        CharacterPool.rebuildCharacterPool(Optional.empty());
        Daytime.day = null;
    }

    // TODO: Make this its own scene.
    public static String getIntro() {
        return "You don't really know why you're going to the Student Union in the middle of the night."
                        + " You'd have to be insane to accept the invitation you received this afternoon."
                        + " Seriously, someone is offering you money to sexfight a bunch of other students?"
                        + " You're more likely to get mugged (though you're not carrying any money) or murdered if you show up."
                        + " Best case scenario, it's probably a prank for gullible freshmen."
                        + " You have no good reason to believe the invitation is on the level, but here you are, walking into the empty Student Union."
                        + "\n\n" + "Not quite empty, it turns out."
                        + " The same woman who approached you this afternoon greets you and brings you to a room near the back of the building."
                        + " Inside, you're surprised to find three quite attractive girls."
                        + " After comparing notes, you confirm they're all freshmen like you and received the same invitation today."
                        + " You're surprised, both that these girls would agree to such an invitation."
                        + " For the first time, you start to believe that this might actually happen."
                        + " After a few minutes of awkward small talk (though none of these girls seem self-conscious about being here), the woman walks in again leading another girl."
                        + " Embarrassingly you recognize the girl, named Cassie, who is a classmate of yours, and who you've become friends with over the past couple weeks."
                        + " She blushes when she sees you and the two of you consciously avoid eye contact while the woman explains the rules of the competition."
                        + "\n\n" + "There are a lot of specific points, but the rules basically boil down to this: "
                        + " competitors move around the empty areas of the campus and engage each other in sexfights."
                        + " When one competitor orgasms and doesn't have the will to go on, the other gets a point and can claim the loser's clothes."
                        + " Those two players are forbidden to engage again until the loser gets a replacement set of clothes at either the Student Union or the first floor of the dorm building."
                        + " It seems to be customary, but not required, for the loser to get the winner off after a fight, when it doesn't count."
                        + " After three hours, the match ends and each player is paid for each opponent they defeat, each set of clothes turned in, and a bonus for whoever scores the most points."
                        + "\n\n"
                        + "After the explanation, she confirms with each participant whether they are still interested in participating."
                        + " Everyone agrees." + " The first match starts at exactly 10:00.";
    }

    public static void reset() {
        CharacterPool.players.clear();
        Flag.flags.clear();
        Daytime.day = null;
        Match.match = null;
        CharacterPool.human = new Player("Dummy");
        GUI.gui.purgePlayer();
        xpRate = 1.0;
        GUI.gui.createCharacter();
    }

    public static boolean inGame() {
        return ingame;
    }

    public synchronized void gameLoop() {
        ingame = true;
        run = true;
        while (run) {
            try {
                if (Time.getTime() == Time.NIGHT) {
                    // do nighttime stuff
                    // choose match
                    CompletableFuture<Match> preparedMatch = new CompletableFuture<>();
                    Prematch prematch = Prematch.decideMatchType(preparedMatch);
                    // set up match
                    prematch.prompt(CharacterPool.human);
                    Match match = preparedMatch.get();
                    // start match
                    CountDownLatch matchComplete = match.matchComplete;
                    match.startMatch();
                    // end match
                    matchComplete.await();
                    Postmatch postmatch = new Postmatch(CharacterPool.getPlayer(), match.combatants);
                    postmatch.endMatch();
                    // set time to next day
                    Time.date++;
                    Time.time = Time.DAY;
                    postmatch.EndMatchGui();
                    postmatch.readyForBed.await();
                    // autosave
                    SaveFile.autoSave();
                    // sleep
                }
                if (Time.getTime() == Time.DAY) {
                    Match.match = null;
                    Daytime.day = new Daytime(CharacterPool.human);
                    // do daytime stuff
                    Daytime.day.plan();
                    Daytime.day.readyForNight.await();
                    // set time to night
                    Daytime.day = null;
                    Time.time = Time.NIGHT;
                    // autosave
                    SaveFile.autoSave();
                }
            } catch (InterruptedException e) {
                run = false;
            } catch (ExecutionException e) {
                run = false;
                e.printStackTrace();
            }
        }
        ingame = false;
    }

    public void wake() {
        this.notifyAll();
    }
}
