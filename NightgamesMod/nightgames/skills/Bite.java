package nightgames.skills;

import nightgames.characters.Character;
import nightgames.characters.Trait;
import nightgames.combat.Combat;
import nightgames.combat.Result;
import nightgames.global.Formatter;
import nightgames.status.addiction.Addiction;
import nightgames.status.addiction.AddictionType;

@SuppressWarnings("unused")
public class Bite extends Skill {

    public Bite(Character self) {
        super("Bite", self, 5);
    }

    @Override
    public boolean requirements(Combat c, Character user, Character target) {
        return user.has(Trait.breeder);
    }

    @Override
    public boolean usable(Combat c, Character target) {
        return c.getStance().penetratedBy(c, getSelf(), target) && c.getStance().kiss(getSelf(), target);
    }

    @Override
    public String describe(Combat c) {
        return "Instill a lasting need to fuck";
    }

    @Override
    public boolean resolve(Combat c, Character target) {
        boolean katOnTop = c.getStance().dom(getSelf());
        if (katOnTop) {
            c.write(getSelf(), Formatter.format("{self:SUBJECT-ACTION:lean} in close, grinding {self:possessive} {self:body-part:breasts}"
                            + " against {other:name-do} and biting {other:possessive} neck!"
                            + " {other:PRONOUN} briefly {other:action:panic}, but {other:pronoun-action:know} {self:name}"
                            + " wouldn't seriously hurt {other:direct-object}. {self:PRONOUN} quickly {self:action:sit}"
                            + " back up, riding {other:direct-object} with a fierce intensity. An unnatural warmth spreads from where"
                            + " {self:pronoun-action:have} bitten {other:direct-object}, "
                            + "and {self:possessive} movements suddenly feel even better than before.",
                            getSelf(), target));
        } else {
            c.write(getSelf(), Formatter.format("{self:SUBJECT-ACTION:grab} {other:name-possessive} head"
                            + " and pulls it down beside {self:poss-pronoun}, then {self:pronoun-action:twist}"
                            + " and {self:action:bite} {other:direct-object}! {other:PRONOUN-ACTION:think} "
                            + "{self:pronoun-action:have} broken {other:possessive} skin, but {other:pronoun-action:are} "
                            + "not bleeding. A warmth spreads down from {other:possessive} neck as {self:subject-action:smile}"
                            + " at {other:direct-object} coyly. <i>\"It, ah, feels so much better with a "
                            + "little bit of animal instinct, nya?\"</i> {other:SUBJECT-ACTION:are} not sure "
                            + "what {self:pronoun-action:mean}, but {other:pronoun-action:do} realize "
                            + "{other:pronoun-action:have} sped up {other:possessive} thrusting and it "
                            + "does seem to feel even better than before.", getSelf(), target));            
        }
        target.addict(c, AddictionType.BREEDER, getSelf(), Addiction.MED_INCREASE);
        
        return true;
    }
    
    public float priorityMod(Combat c) {
        return 10.f;
    }

    @Override
    public Skill copy(Character user) {
        return new Bite(user);
    }

    @Override
    public Tactics type(Combat c) {
        return Tactics.fucking;
    }

    @Override
    public String deal(Combat c, int damage, Result modifier, Character target) {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public String receive(Combat c, int damage, Result modifier, Character target) {
        // TODO Auto-generated method stub
        return null;
    }

}
