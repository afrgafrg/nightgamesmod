package nightgames.utilities;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import nightgames.characters.Character;
import nightgames.characters.NPC;
import nightgames.global.GameState;

public class ProseUtils {
    private static Map<String, String> FIRST_PERSON_TO_THIRD_PERSON = new HashMap<>();
    static {
        FIRST_PERSON_TO_THIRD_PERSON.put("can", "can");
        FIRST_PERSON_TO_THIRD_PERSON.put("could", "could");
        FIRST_PERSON_TO_THIRD_PERSON.put("may", "may");
        FIRST_PERSON_TO_THIRD_PERSON.put("might", "might");
        FIRST_PERSON_TO_THIRD_PERSON.put("shall", "shall");
        FIRST_PERSON_TO_THIRD_PERSON.put("should", "should");
        FIRST_PERSON_TO_THIRD_PERSON.put("will", "will");
        FIRST_PERSON_TO_THIRD_PERSON.put("would", "would");
        FIRST_PERSON_TO_THIRD_PERSON.put("must", "must");
        FIRST_PERSON_TO_THIRD_PERSON.put("are", "is");
        FIRST_PERSON_TO_THIRD_PERSON.put("have", "has");
    }
    private static List<String> ES_VERB_ENDINGS = Arrays.asList("ch", "sh", "s", "x", "o");
    private static String CONSONANTS = "bcdfghjklmnpqrstvwxyz";
    public static String getThirdPersonFromFirstPerson(String verb) {
        verb = verb.toLowerCase();
        if (FIRST_PERSON_TO_THIRD_PERSON.containsKey(verb)) {
            return FIRST_PERSON_TO_THIRD_PERSON.get(verb);
        }
        if (ES_VERB_ENDINGS.stream().anyMatch(verb::endsWith)) {
            return verb + "es";
        }
        String lastTwoLetters = verb.length() < 2 ? "xx" : verb.substring(verb.length() - 2, verb.length());
        if ('y' == lastTwoLetters.charAt(1) && CONSONANTS.indexOf(lastTwoLetters.charAt(0)) >= 0) {
            return verb.substring(0, verb.length() - 1) + "ies";
        }
        return verb + "s";
    }
    
    private enum GrammarGender {
        FEMALE,
        MALE,
        PLAYER;
        
        static GrammarGender determine(Character c) {
            NPC exchanged = GameState.state() == null ? null : GameState.state().exchanged;
            
            if (c.human()) {
                return PLAYER;
            }
            return c.useFemalePronouns() ? FEMALE : MALE;
        }
    }
    
    public static String pronoun(Character c) {
        switch (GrammarGender.determine(c)) {
            case FEMALE:
                return "she";
            case MALE:
                return "he";
            case PLAYER:
                return "you";
            default: throw new IllegalStateException();
        }
    }
    
    public static String subject(Character c) {
        switch (GrammarGender.determine(c)) {
            case FEMALE:
            case MALE:
                return c.getName();
            case PLAYER:
                return "you";
            default: throw new IllegalStateException();
        }
    }
    
    public static String possessiveAdjective(Character c) {
        switch (GrammarGender.determine(c)) {
            case FEMALE:
                return "her";
            case MALE:
                return "his";
            case PLAYER:
                return "your";
            default: throw new IllegalStateException();
        }
    }
    
    public static String possessivePronoun(Character c) {
        switch (GrammarGender.determine(c)) {
            case FEMALE:
                return "hers";
            case MALE:
                return "his";
            case PLAYER:
                return "your";
            default: throw new IllegalStateException();
        }
    }
    
    public static String directObject(Character c) {
        switch (GrammarGender.determine(c)) {
            case FEMALE:
                return "her";
            case MALE:
                return "him";
            case PLAYER:
                return "you";
            default: throw new IllegalStateException();
        }
    }
    
    public static String nameOrDirectObject(Character c) {
        switch (GrammarGender.determine(c)) {
            case FEMALE:
            case MALE:
                return c.getName();
            case PLAYER:
                return "you";
            default: throw new IllegalStateException();
        }
    }
    
    public static String nameOrPossessivePronoun(Character c) {
        switch (GrammarGender.determine(c)) {
            case FEMALE:
            case MALE:
                return c.getName() + "'s";
            case PLAYER:
                return "your";
            default: throw new IllegalStateException();
        }
    }
    
    public static String subjectWas(Character c) {
        switch (GrammarGender.determine(c)) {
            case FEMALE:
                return "she was";
            case MALE:
                return "he was";
            case PLAYER:
                return "you were";
            default: throw new IllegalStateException();
        }
    }
    
    public static String subjectAction(Character c, String verb, String pluralVerb) {
        switch (GrammarGender.determine(c)) {
            case FEMALE:
                return "she " + pluralVerb;
            case MALE:
                return "he " + pluralVerb;
            case PLAYER:
                return "you " + verb;
            default: throw new IllegalStateException();
        }
    }
    
    public static String reflectivePronoun(Character c) {
        switch (GrammarGender.determine(c)) {
            case FEMALE:
                return "herself";
            case MALE:
                return "himself";
            case PLAYER:
                return "yourself";
            default: throw new IllegalStateException();
        }
    }
}