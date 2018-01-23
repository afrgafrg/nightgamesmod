package nightgames.characters;

import nightgames.actions.Action;
import nightgames.combat.Combat;
import nightgames.combat.Encounter;
import nightgames.combat.Result;
import nightgames.skills.Tactics;
import nightgames.trap.Trap;

import java.util.Optional;

/**
 * TODO: Write class-level documentation.
 */
public class TestCharacter extends Character {
    public TestCharacter() {
        this(1);
    }

    public TestCharacter(int level) {
        super("TestCharacter", level);
    }

    @Override public void ding(Combat c) {

    }

    @Override public void detect() {

    }

    @Override public void doAction(Action action) {

    }

    @Override public void faceOff(Character opponent, Encounter enc) {

    }

    @Override public void spy(Character opponent, Encounter enc) {

    }

    @Override public String describe(int per, Combat c) {
        return null;
    }

    @Override public void victory(Combat c, Result flag) {

    }

    @Override public void defeat(Combat c, Result flag) {

    }

    @Override public void intervene3p(Combat c, Character target, Character assist) {

    }

    @Override public void victory3p(Combat c, Character target, Character assist) {

    }

    @Override public boolean resist3p(Combat c, Character target, Character assist) {
        return false;
    }

    @Override public boolean chooseSkill(Combat c) {
        return false;
    }

    @Override public Optional<Action> move() throws InterruptedException {
        return Optional.empty();
    }

    @Override public void draw(Combat c, Result flag) {

    }

    @Override public boolean human() {
        return false;
    }

    @Override public String bbLiner(Combat c, Character target) {
        return null;
    }

    @Override public String nakedLiner(Combat c, Character target) {
        return null;
    }

    @Override public String stunLiner(Combat c, Character target) {
        return null;
    }

    @Override public String taunt(Combat c, Character target) {
        return null;
    }

    @Override public void intervene(Encounter fight, Character p1, Character p2) {

    }

    @Override public void showerScene(Character target, Encounter encounter) {

    }

    @Override public String getType() {
        return "TestCharacter";
    }

    @Override public void afterParty() {

    }

    @Override public void emote(Emotion emo, int amt) {

    }

    @Override public String challenge(Character other) {
        return null;
    }

    @Override public void promptTrap(Encounter fight, Character target, Trap trap) {

    }

    @Override public void counterattack(Character target, Tactics type, Combat c) {

    }

    @Override public String getPortrait(Combat c) {
        return null;
    }
}
