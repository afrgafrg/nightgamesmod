package nightgames.characters;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonParseException;
import nightgames.Resources.ResourceLoader;
import nightgames.characters.custom.CustomNPC;
import nightgames.characters.custom.JsonSourceNPCDataLoader;
import nightgames.characters.custom.NPCData;
import nightgames.json.JsonUtils;
import nightgames.start.NpcConfiguration;
import nightgames.start.StartConfiguration;

import java.io.IOException;
import java.io.InputStreamReader;
import java.util.*;

/**
 * Tracks Characters in current game.
 */
public class CharacterPool {
    public Map<String, NPC> characterPool;   // All starting and unlockable characters
    public Set<Character> players = new HashSet<>();           // All currently unlocked characters
    public Set<Character> debugChars = new HashSet<>();
    public Player human;

    private Optional<NpcConfiguration> findNpcConfig(String type, Optional<StartConfiguration> startConfig) {
        return startConfig.isPresent() ? startConfig.get().findNpcConfig(type) : Optional.empty();
    }

    public Set<Character> everyone() {
        return players;
    }

    public boolean newChallenger(Personality challenger) {
        if (!players.contains(challenger.getCharacter())) {
            int targetLevel = human.getLevel();
            if (challenger.getCharacter().has(Trait.leveldrainer)) {
                targetLevel -= 4;
            }
            while (challenger.getCharacter().getLevel() <= targetLevel) {
                challenger.getCharacter().ding(null);
            }
            players.add(challenger.getCharacter());
            return true;
        } else {
            return false;
        }
    }

    public NPC getNPC(String name) {
        for (Character c : allNPCs()) {
            if (c.getType().equalsIgnoreCase(name)) {
                return (NPC) c;
            }
        }
        System.err.println("NPC \"" + name + "\" is not loaded.");
        return null;
    }

    public boolean characterTypeInGame(String type) {
        return players.stream().anyMatch(c -> type.equals(c.getType()));
    }

    public Collection<NPC> allNPCs() {
        return characterPool.values();
    }

    public Character getParticipantsByName(String name) {
        return players.stream().filter(c -> c.getTrueName().equals(name)).findAny().get();
    }

    public void rebuildCharacterPool(Optional<StartConfiguration> startConfig) {
        characterPool = new HashMap<>();
        debugChars.clear();

        Optional<NpcConfiguration> commonConfig =
                        startConfig.isPresent() ? Optional.of(startConfig.get().npcCommon) : Optional.empty();

        try (InputStreamReader reader = new InputStreamReader(
                        ResourceLoader.getFileResourceAsStream("characters/included.json"))) {
            JsonArray characterSet = JsonUtils.rootJson(reader).getAsJsonArray();
            for (JsonElement element : characterSet) {
                String name = element.getAsString();
                try {
                    NPCData data = JsonSourceNPCDataLoader
                                    .load(ResourceLoader.getFileResourceAsStream("characters/" + name));
                    Optional<NpcConfiguration> npcConfig = findNpcConfig(CustomNPC.TYPE_PREFIX + data.getName(), startConfig);
                    Personality npc = new CustomNPC(data, npcConfig, commonConfig);
                    characterPool.put(npc.getCharacter().getType(), npc.getCharacter());
                    System.out.println("Loaded " + name);
                } catch (JsonParseException e1) {
                    System.err.println("Failed to load NPC " + name);
                    e1.printStackTrace();
                }
            }
        } catch (JsonParseException | IOException e1) {
            System.err.println("Failed to load character set");
            e1.printStackTrace();
        }

        // TODO: Refactor into function and unify with CustomNPC handling.
        Personality cassie = new Cassie(findNpcConfig("Cassie", startConfig), commonConfig);
        Personality angel = new Angel(findNpcConfig("Angel", startConfig), commonConfig);
        Personality reyka = new Reyka(findNpcConfig("Reyka", startConfig), commonConfig);
        Personality kat = new Kat(findNpcConfig("Kat", startConfig), commonConfig);
        Personality mara = new Mara(findNpcConfig("Mara", startConfig), commonConfig);
        Personality jewel = new Jewel(findNpcConfig("Jewel", startConfig), commonConfig);
        Personality airi = new Airi(findNpcConfig("Airi", startConfig), commonConfig);
        Personality eve = new Eve(findNpcConfig("Eve", startConfig), commonConfig);
        Personality maya = new Maya(1, findNpcConfig("Maya", startConfig), commonConfig);
        Personality yui = new Yui(findNpcConfig("Yui", startConfig), commonConfig);
        characterPool.put(cassie.getCharacter().getType(), cassie.getCharacter());
        characterPool.put(angel.getCharacter().getType(), angel.getCharacter());
        characterPool.put(reyka.getCharacter().getType(), reyka.getCharacter());
        characterPool.put(kat.getCharacter().getType(), kat.getCharacter());
        characterPool.put(mara.getCharacter().getType(), mara.getCharacter());
        characterPool.put(jewel.getCharacter().getType(), jewel.getCharacter());
        characterPool.put(airi.getCharacter().getType(), airi.getCharacter());
        characterPool.put(eve.getCharacter().getType(), eve.getCharacter());
        characterPool.put(maya.getCharacter().getType(), maya.getCharacter());
        characterPool.put(yui.getCharacter().getType(), yui.getCharacter());
        debugChars.add(reyka.getCharacter());
    }

    public Character getCharacterByType(String type) {
        if (type.equals(human.getType())) {
            return human;
        }
        return getNPCByType(type);
    }

    public NPC getNPCByType(String type) {
        NPC results = characterPool.get(type);
        if (results == null) {
            System.err.println("failed to find NPC for type " + type);
        }
        return results;
    }

    public List<Character> getInAffectionOrder(List<Character> viableList) {
        List<Character> results = new ArrayList<>(viableList);
        results.sort(Comparator.comparingInt(a -> a.getAffection(getPlayer())));
        return results;
    }

    /**
     * WARNING DO NOT USE THIS IN ANY COMBAT RELATED CODE.
     * IT DOES NOT TAKE INTO ACCOUNT THAT THE PLAYER GETS CLONED. WARNING. WARNING.
     * @return
     */
    public Player getPlayer() {
        return human;
    }
}
