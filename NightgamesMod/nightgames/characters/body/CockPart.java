package nightgames.characters.body;

import com.google.gson.JsonObject;
import nightgames.characters.Attribute;
import nightgames.characters.Character;
import nightgames.characters.NPC;
import nightgames.characters.Trait;
import nightgames.combat.Combat;
import nightgames.global.Global;
import nightgames.global.Grammar;
import nightgames.global.Rng;
import nightgames.status.*;

/**
 * Cocks!
 */
public class CockPart implements BodyPart {
    private final Size size;
    private final Mod modType;
    private static final String synonyms[] = {"cock", "dick", "shaft", "phallus"};
    private static final int DEFAULT_FEMININITY = -3;

    public CockPart() {
        this(Size.normal(), Mod.normal());
    }

    public CockPart(Size size) {
        this(size, Mod.normal());
    }

    public CockPart(Mod modType) {
        this(Size.normal(), modType);
    }

    public CockPart(Size size, Mod modType) {
        this.size = size;
        this.modType = modType;
    }

    public CockPart(CockPart original, Mod newType) {
        this(original.getSize(), newType);
    }

    public enum Size implements Comparable<Size> {
        tiny("tiny", 3),
        small("smallish", 4),
        average("average-sized", 6),
        big("big", 8),
        huge("huge", 9),
        massive("massive", 10);

        public String desc;
        public int length;

        Size(String desc, int length) {
            this.desc = desc;
            this.length = length;
        }

        public static Size normal() {
            return average;
        }

        public static Size minimumSize() {
            return values()[0];
        }

        public static Size maximumSize() {
            return values()[values().length - 1];
        }

        public Size next() {
            return traverse(1);
        }

        public Size previous() {
            return traverse(-1);
        }

        private Size traverse(int distance) {
            int newIndex = this.ordinal() + distance;
            int minIndex = 0;
            int maxIndex = Size.values().length - 1;
            if (newIndex < minIndex) {
                return Size.values()[minIndex];
            } else if (newIndex > maxIndex) {
                return Size.values()[maxIndex];
            } else {
                return this;
            }
        }
    }

    public enum Mod implements BodyPartMod {
        normal(1.0, 1.0, 1.0),
        slimy(.5, 1.5, .7),
        runic(2.0, 1.0, 1.0),
        blessed(1.0, 1.0, .75),
        incubus(1.25, 1.3, .9),
        primal(1.0, 1.4, 1.2),
        bionic(.8, 1.3, .5),
        enlightened(1.0, 1.2, .8);

        private double sensitivity;
        private double pleasure;
        private double hotness;

        Mod(double hotness, double pleasure, double sensitivity) {
            this.hotness = hotness;
            this.pleasure = pleasure;
            this.sensitivity = sensitivity;
        }

        public static Mod normal() {
            return normal;
        }

        @Override public String getModType() {
            return this.name();
        }

        @Override public boolean countsAs(Character self, BodyPartMod part) {
            return effectiveType(self) == part;
        }

        public Mod effectiveType(Character self) {
            SlimeMimicry mimicry = (SlimeMimicry) self.getStatus(Stsflag.mimicry);
            if (mimicry != null) {
                return mimicry.getCockModMimicked();
            } else {
                return this;
            }
        }

        public int counterValue(BodyPart otherPart, Character self, Character other) {
            if (this == normal && !otherPart.isGeneric(other)) {
                // Don't fuck modded parts; they're dangerous!
                return -1;
            } else if (this != normal && otherPart.isGeneric(other)) {
                return 1;
            }
            if (!(otherPart instanceof PussyPart)) {
                return 0;
            }
            PussyPart pussyType = ((PussyPart) otherPart).effectiveType(other);
            switch (this.effectiveType(self)) {
                case primal:
                    return pussyType == PussyPart.fiery ? 1 : pussyType == PussyPart.arcane ? -1 : 0;
                case runic:
                    return pussyType == PussyPart.succubus ? 1 : pussyType == PussyPart.feral ? -1 : 0;
                case incubus:
                    return pussyType == PussyPart.feral ? 1 : pussyType == PussyPart.cybernetic ? -1 : 0;
                case bionic:
                    return pussyType == PussyPart.arcane ? 1 : pussyType == PussyPart.fiery ? -1 : 0;
                case enlightened:
                    return pussyType == PussyPart.cybernetic ? 1 : pussyType == PussyPart.succubus ? -1 : 0;
                default:
                    return 0;
            }
        }
    }

    @Override public BodyPartMod getMod(Character self) {
        SlimeMimicry mimicry = (SlimeMimicry) self.getStatus(Stsflag.mimicry);
        if (mimicry != null) {
            return mimicry.getCockModMimicked();
        } else {
            return modType;
        }
    }

    public Mod getModType() {
        return modType;
    }

    @Override public double getFemininity(Character self) {
        return -DEFAULT_FEMININITY;
    }

    @Override public void describeLong(StringBuilder b, Character c) {
        b.append("A ");
        b.append(fullDescribe(c));
        b.append(" hangs between ").append(c.nameOrPossessivePronoun()).append(" legs.");
    }

    @Override public double priority(Character c) {
        return getPleasure(c, null);
    }

    @Override public boolean isType(String type) {
        return type.equalsIgnoreCase("cock");
    }

    @Override public String getType() {
        return "cock";
    }

    @Override public String toString() {
        return size.desc;
    }

    @Override public double getHotness(Character self, Character opponent) {
        double hotness = Math.log(size.length / 4 + 1) / Math.log(2) - 1;
        if (!opponent.hasPussy()) {
            hotness /= 2;
        }
        return hotness;
    }

    private double getPleasureBase() {
        return Math.log(size.length + 2.5) / Math.log(2) - 1.8;
    }

    @Override public double getPleasure(Character self, BodyPart target) {
        double pleasureMod = getPleasureBase();
        pleasureMod += self.hasTrait(Trait.sexTraining1) ? .5 : 0;
        pleasureMod += self.hasTrait(Trait.sexTraining2) ? .7 : 0;
        pleasureMod += self.hasTrait(Trait.sexTraining3) ? .7 : 0;
        DivineCharge charge = (DivineCharge) self.getStatus(Stsflag.divinecharge);
        if (charge != null) {
            pleasureMod += charge.magnitude;
        }
        return pleasureMod * modType.pleasure;
    }

    @Override public double getSensitivity(BodyPart target) {
        return Math.log(size.length / 5 + 1) / Math.log(2) * modType.sensitivity;
    }

    @Override public boolean isReady(Character self) {
        return self.hasTrait(Trait.alwaysready) || self.getArousal().percent() >= 15 || this.modType
                        .countsAs(self, Mod.bionic);
    }

    @Override public JsonObject save() {
        JsonObject object = new JsonObject();
        object.addProperty("size", size.name());
        object.addProperty("type", modType.name());
        return object;
    }

    @Override public BodyPart upgrade() {
        return new CockPart(size.next(), this.modType);
    }

    @Override public BodyPart downgrade() {
        return new CockPart(size.previous(), this.modType);
    }

    @Override public boolean isErogenous() {
        return true;
    }

    @Override public boolean isNotable() {
        return true;
    }

    @Override public String prefix() {
        return "a ";
    }

    @Override public int compare(BodyPart other) {
        if (other instanceof CockPart) {
            return size.length - ((CockPart) other).size.length;
        }
        return 0;
    }

    @Override public boolean isVisible(Character c) {
        return c.crotchAvailable();
    }

    @Override
    public double applySubBonuses(Character self, Character opponent, BodyPart with, BodyPart target, double damage,
                    Combat c) {
        return 0;
    }

    @Override public int modifyAttribute(Attribute a, int total) {
        switch (a) {
            case Speed:
                return -Math.round(Math.max(size.length - 6, 0));
            case Seduction:
                return Math.round(Math.max(size.length - 6, 0));
            default:
                return 0;
        }
    }

    @Override public BodyPart load(JsonObject obj) {
        return new CockPart(Size.valueOf(obj.get("size").getAsString()),
                        Mod.valueOf(obj.get("type").getAsString()));
    }

    @Override public void tickHolding(Combat c, Character self, Character opponent, BodyPart otherOrgan) {
    }

    @Override public int counterValue(BodyPart otherPart, Character self, Character other) {
        return modType.counterValue(otherPart, self, other);
    }

    public Size getSize() {
        return size;
    }

    public CockPart applyMod(Mod mod) {
        return new CockPart(this, mod);
    }

    @Override public double applyBonuses(Character self, Character opponent, BodyPart target, double damage, Combat c) {
        double bonus = 0;
        if (self.hasTrait(Trait.polecontrol)) {
            String desc = "";
            if (self.hasTrait(Trait.polecontrol)) {
                desc += "expert ";
            }
            c.write(self, Global.global.format("{self:SUBJECT-ACTION:use|uses} {self:possessive} " + desc
                                            + "cock control to grind against {other:name-possessive} inner walls, making {other:possessive} knuckles whiten as {other:pronoun} {other:action:moan|moans} uncontrollably.",
                            self, opponent));
            bonus += self.hasTrait(Trait.polecontrol) ? 8 : 0;
        }
        if (this.modType.countsAs(self, Mod.blessed) && target.isType("cock")) {
            if (self.getStatus(Stsflag.divinecharge) != null) {
                c.write(self, Global.global.format(
                                "{self:NAME-POSSESSIVE} concentrated divine energy in {self:possessive} cock rams into {other:name-possessive} pussy, sending unimaginable pleasure directly into {other:possessive} soul.",
                                self, opponent));
            }
            // no need for any effects, the bonus is in the pleasure mod
        }
        if (this.modType.countsAs(self, Mod.runic)) {
            String message = "";
            if (target.moddedPartCountsAs(opponent, PussyPart.succubus)) {
                message +=
                                String.format("The fae energies inside %s %s radiate outward and into %s, causing %s %s to grow much more sensitve.",
                                                self.nameOrPossessivePronoun(), describe(self),
                                                opponent.nameOrPossessivePronoun(), opponent.possessivePronoun(),
                                                target.describe(opponent));
                bonus += damage * 0.5; // +50% damage
            }
            if (Rng.rng.random(8) == 0 && !opponent.wary()) {
                message += String.format("Power radiates out from %s %s, seeping into %s and subverting %s will. ",
                                self.nameOrPossessivePronoun(), describe(self), opponent.nameOrPossessivePronoun(),
                                opponent.directObject());
                opponent.add(c, new Enthralled(opponent, self, 3));
            }
            if (self.hasStatus(Stsflag.cockbound)) {
                String binding = ((CockBound) self.getStatus(Stsflag.cockbound)).binding;
                message +=
                                String.format("With the merest of thoughts, %s %s out a pulse of energy from %s %s, freeing it from %s %s. ",
                                                self.subject(), self.human() ? "send" : "sends",
                                                self.possessivePronoun(), describe(self),
                                                opponent.nameOrPossessivePronoun(), binding);
                self.removeStatus(Stsflag.cockbound);
            }
            c.write(self, message);
        } else if (this.modType.countsAs(self, Mod.incubus)) {
            String message = String.format("%s demonic appendage latches onto %s will, trying to draw it into %s.",
                            self.nameOrPossessivePronoun(), opponent.nameOrPossessivePronoun(),
                            self.reflectivePronoun());
            int amtDrained;
            if (target.moddedPartCountsAs(opponent, PussyPart.feral)) {
                message += String.format(" %s %s gladly gives it up, eager for more pleasure.",
                                opponent.possessivePronoun(), target.describe(opponent));
                amtDrained = 5;
                bonus += 2;
            } else if (target.moddedPartCountsAs(opponent, PussyPart.cybernetic)) {
                message +=
                                String.format(" %s %s does not oblige, instead sending a pulse of electricity through %s %s and up %s spine",
                                                opponent.nameOrPossessivePronoun(), target.describe(opponent),
                                                self.nameOrPossessivePronoun(), describe(self),
                                                self.possessivePronoun());
                self.pain(c, opponent, Rng.rng.random(9) + 4);
                amtDrained = 0;
            } else {
                message += String.format(" Despite %s best efforts, some of the elusive energy passes into %s.",
                                opponent.nameOrPossessivePronoun(), self.nameDirectObject());
                amtDrained = 3;
            }
            if (amtDrained != 0) {
                opponent.loseWillpower(c, amtDrained);
                self.restoreWillpower(c, amtDrained);
            }
            c.write(self, message);
        } else if (this.modType.countsAs(self, Mod.bionic)) {
            String message = "";
            if (Rng.rng.random(5) == 0 && target.getType().equals("pussy")) {
                message +=
                                String.format("%s %s out inside %s %s, pressing the metallic head of %s %s tightly against %s cervix. "
                                                                + "Then, a thin tube extends from %s uthera and into %s womb, pumping in a powerful aphrodisiac that soon has %s sensitive and"
                                                                + " gasping for more.", self.subject(),
                                                self.human() ? "bottom" : "bottoms", opponent.nameOrPossessivePronoun(),
                                                target.describe(opponent), self.possessivePronoun(),
                                                describe(self), opponent.possessivePronoun(),
                                                self.possessivePronoun(), opponent.possessivePronoun(),
                                                opponent.directObject());
                opponent.add(c, new Hypersensitive(opponent));
                // Instantly addict
                opponent.add(c, new FluidAddiction(opponent, self, 1, 2));
                opponent.add(c, new FluidAddiction(opponent, self, 1, 2));
                opponent.add(c, new FluidAddiction(opponent, self, 1, 2));
                bonus -= 3; // Didn't actually move around too much
            } else if (target != PussyPart.fiery) {
                message +=
                                String.format("Sensing the flesh around it, %s %s starts spinning rapidly, vastly increasing the friction against the walls of %s %s.",
                                                self.nameOrPossessivePronoun(), describe(self),
                                                opponent.nameOrPossessivePronoun(), target.describe(opponent));
                bonus += 5;
                if (Rng.rng.random(5) == 0) {
                    message +=
                                    String.format(" The intense sensations cause %s to forget to breathe for a moment, leaving %s literally breathless.",
                                                    opponent.subject(), opponent.directObject());
                    opponent.add(c, new Winded(opponent, 1));
                }
            }
            c.write(self, message);
        } else if (this.modType.countsAs(self, Mod.enlightened)) {
            String message;
            if (target.moddedPartCountsAs(opponent, PussyPart.succubus)) {
                message =
                                String.format("Almost instinctively, %s %s entire being into %s %s. While this would normally be a good thing,"
                                                                + " whilst fucking a succubus it is very, very bad indeed.",
                                                self.subjectAction("focus", "focuses"), self.possessivePronoun(),
                                                self.possessivePronoun(), describe(self));
                c.write(self, message);
                // Actual bad effects are dealt with in PussyPart
            } else {
                message =
                                String.format("Drawing upon %s extensive training, %s %s will into %s %s, enhancing %s own abilities",
                                                self.possessivePronoun(),
                                                self.subjectAction("concentrate", "concentrates"),
                                                self.possessivePronoun(), self.possessivePronoun(), describe(self),
                                                self.possessivePronoun());
                c.write(self, message);
                for (int i = 0; i < Math.max(2, (self.get(Attribute.Ki) + 5) / 10); i++) { // +5
                    // for
                    // rounding:
                    // 24->29->20,
                    // 25->30->30
                    Attribute attr = new Attribute[] {Attribute.Power, Attribute.Cunning, Attribute.Seduction}[Rng.rng
                                    .random(3)];
                    self.add(c, new Abuff(self, attr, 1, 10));
                }
                self.buildMojo(c, 5);
                self.restoreWillpower(c, 1);
            }
        }
        return bonus;
    }

    public String getFluids(Character c) {
        return this.modType.countsAs(c, Mod.bionic) ? "artificial lubricant" : "pre-cum";
    }

    public double applyReceiveBonuses(Character self, Character opponent, BodyPart target, double damage, Combat c) {
        if (this.modType.countsAs(self, Mod.blessed) && c.getStance().inserted(self)) {
            DivineCharge charge = (DivineCharge) self.getStatus(Stsflag.divinecharge);
            if (charge == null) {
                c.write(self, Global.global.format("{self:NAME-POSSESSIVE} " + fullDescribe(self)
                                                + " radiates a golden glow as {self:subject-action:groan|groans}. "
                                                + "{other:SUBJECT-ACTION:realize|realizes} {self:subject-action:are|is} feeding on {self:possessive} own pleasure to charge up {self:possessive} divine energy.",
                                self, opponent));
                self.add(c, new DivineCharge(self, .25));
            } else {
                c.write(self, Global.global.format(
                                "{self:SUBJECT-ACTION:continue|continues} feeding on {self:possessive} own pleasure to charge up {self:possessive} divine energy.",
                                self, opponent));
                self.add(c, new DivineCharge(self, charge.magnitude));
            }
        }
        double bonus = 0;
        if (opponent.hasTrait(Trait.dickhandler) || opponent.hasTrait(Trait.anatomyknowledge)) {
            c.write(opponent, Global.global.format(
                            "{other:NAME-POSSESSIVE} expert handling of {self:name-possessive} cock causes {self:subject} to shudder uncontrollably.",
                            self, opponent));
            if (opponent.hasTrait(Trait.dickhandler)) {
                bonus += 5;
            }
            if (opponent.hasTrait(Trait.anatomyknowledge)) {
                bonus += 5;
            }
        }
        if (self.hasTrait(Trait.druglacedprecum) && !opponent.isPartProtected(target)) {
            opponent.add(c, new Sensitized(opponent, target, .2, 2.0, 20));
            c.write(self, Global.global.format(
                            "{self:NAME-POSSESSIVE} drug-laced precum is affecting {other:direct-object}.", self,
                            opponent));
        }
        return bonus;
    }

    @Override public String fullDescribe(Character c) {
        String description;
        if (this.modType.countsAs(c, Mod.bionic)) {
            description = "bionic robo-";
        } else if (this.modType.countsAs(c, Mod.incubus) && c.hasPussy()) {
            description = "demonic girl-";
        } else {
            description = modType.name() + (c.hasPussy() ? " girl-" : " ");
        }
        String synonym = Rng.rng.pickRandom(synonyms).get();
        return size.desc + " " + description + synonym;
    }

    @Override public String canonicalDescription() {
        return size.desc + " " + modType.name() + " cock";
    }

    @Override public String describe(Character c) {
        String description;
        if (this.modType.countsAs(c, Mod.bionic)) {
            description = "bionic robo-";
        } else if (this.modType.countsAs(c, Mod.incubus) && c.hasPussy()) {
            description = "demonic girl-";
        } else {
            description = modType.name() + (c.hasPussy() ? " girl-" : " ");
        }
        String syn = Rng.rng.pickRandom(synonyms).get();
        return Rng.rng.maybeString(size.desc + " ") + description + syn;
    }

    @Override public void onOrgasmWith(Combat c, Character self, Character opponent, BodyPart target, boolean selfCame) {
        if (this.modType.countsAs(self, Mod.incubus) && c.getStance().inserted(self)) {
            if (selfCame) {
                if (target.moddedPartCountsAs(opponent, PussyPart.cybernetic)) {
                    c.write(self,
                                    String.format("%s demonic seed splashes pointlessly against the walls of %s %s, failing even in %s moment of defeat.",
                                                    self.nameOrPossessivePronoun(), opponent.nameOrPossessivePronoun(),
                                                    target.describe(opponent), self.possessivePronoun()));
                } else {
                    int duration = Rng.rng.random(3) + 2;
                    String message =
                                    String.format("The moment %s erupts inside %s, %s mind goes completely blank, leaving %s pliant and ready.",
                                                    self.subject(), opponent.subject(), opponent.possessivePronoun(),
                                                    opponent.directObject());
                    if (target.moddedPartCountsAs(opponent, PussyPart.feral)) {
                        message += String.format(" %s no resistance to the subversive seed.",
                                        Grammar.capitalizeFirstLetter(opponent.subjectAction("offer", "offers")));
                        duration += 2;
                    }
                    opponent.add(c, new Enthralled(opponent, self, duration));
                    c.write(self, message);
                }
            } else {
                if (target != PussyPart.cybernetic) {
                    c.write(self,
                                    String.format("Sensing %s moment of passion, %s %s greedily draws upon the rampant flows of orgasmic energy within %s, transferring the power back into %s.",
                                                    opponent.nameOrPossessivePronoun(), self.nameOrPossessivePronoun(),
                                                    describe(self), opponent.directObject(), self.directObject()));
                    int attDamage = target.moddedPartCountsAs(opponent, PussyPart.feral) ? 10 : 5;
                    int willDamage = target.moddedPartCountsAs(opponent, PussyPart.feral) ? 40 : 20;
                    opponent.add(c, new Abuff(opponent, Attribute.Power, -attDamage, 20));
                    opponent.add(c, new Abuff(opponent, Attribute.Cunning, -attDamage, 20));
                    opponent.add(c, new Abuff(opponent, Attribute.Seduction, -attDamage, 20));
                    self.add(c, new Abuff(self, Attribute.Power, attDamage, 20));
                    self.add(c, new Abuff(self, Attribute.Cunning, attDamage, 20));
                    self.add(c, new Abuff(self, Attribute.Seduction, attDamage, 20));
                    opponent.loseWillpower(c, willDamage);
                    self.restoreWillpower(c, willDamage);

                }
            }
        }
    }

    public void tickHolding(Combat c, Character self, Character opponent, BodyPart otherOrgan, CockPart part) {
        if (this.modType.countsAs(self, Mod.primal)) {
            c.write(self, String.format("Raw sexual energy flows from %s %s into %s %s, enflaming %s lust",
                            self.nameOrPossessivePronoun(), part.describe(self), opponent.nameOrPossessivePronoun(),
                            otherOrgan.describe(opponent), opponent.possessivePronoun()));
            opponent.add(c, new Horny(opponent, Rng.rng.random(3) + 1, 3,
                            self.nameOrPossessivePronoun() + " primal passion"));
        }
    }


    @Override public void onStartPenetration(Combat c, Character self, Character opponent, BodyPart target) {
        if (this.modType.countsAs(self, Mod.blessed) && target.isErogenous()) {
            if (!self.human()) {
                c.write(self, Global.global.format(
                                "As soon as {self:subject} penetrates you, you realize you're screwed. Both literally and figuratively. While it looks innocuous enough, {self:name-possessive} {self:body-part:cock} "
                                                + "feels like pure ecstasy. {self:SUBJECT} hasn't even begun moving yet, but {self:possessive} cock simply sitting within you radiates a heat that has you squirming uncontrollably.",
                                self, opponent));
            }
        }
    }

    @Override public void onEndPenetration(Combat c, Character self, Character opponent, BodyPart target) {
        if (this.modType.countsAs(self, Mod.slimy)) {
            c.write(self, Global.global.format(
                            "As {self:possessive} {self:body-part:cock} leaves {other:possessive} " + target
                                            .describe(opponent)
                                            + ", a small bit of slime stays behind, vibrating inside of {other:direct-object}.",
                            self, opponent));
            opponent.add(c, new Horny(opponent, 4f, 10, self.nameOrPossessivePronoun() + " slimy residue"));
        }
    }



    public PussyPart getEquivalentPussy() {
        for (PussyPart pussy : PussyPart.values()) {
            CockPart.Mod equivalentMod = pussy.getEquivalentCockMod();
            if (equivalentMod != CockPart.Mod.normal && equivalentMod.equals(getMod(NPC.NONE_CHARACTER))) {
                return pussy;
            }
        }
        return PussyPart.normal;
    }
}
