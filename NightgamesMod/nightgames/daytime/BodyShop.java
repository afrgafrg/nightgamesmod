package nightgames.daytime;

import nightgames.characters.Character;
import nightgames.characters.Player;
import nightgames.characters.Trait;
import nightgames.characters.body.*;
import nightgames.global.DebugFlags;
import nightgames.global.Flag;
import nightgames.global.Global;
import nightgames.global.Rng;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class BodyShop extends Activity {
    private List<ShopSelection> selection;

    public BodyShop(Player player) {
        super("Body Shop", player);
        selection = new ArrayList<>();
        populateSelection();
    }

    abstract class ShopSelection {
        String choice;
        int price;

        ShopSelection(String choice, int price) {
            this.choice = choice;
            this.price = price;
        }

        abstract void buy(Character buyer);

        abstract boolean available(Character buyer);

        double priority(Character buyer) {
            return 5;
        }

        @Override
        public String toString() {
            return choice;
        }
    }

    interface CharacterRequirement {
        boolean isSatisfied(Character character);
    }

    private void addBodyPartMod(String name, final BodyPart part, final BodyPart normal, int growPrice,
                    int removePrice) {
        addBodyPartMod(name, part, normal, growPrice, removePrice, 5, false);
    }

    private void addBodyPartMod(String name, final BodyPart part, final BodyPart normal, int growPrice, int removePrice,
                    final int priority, final boolean onlyReplace) {
        selection.add(new ShopSelection(name, growPrice) {
            @Override
            void buy(Character buyer) {
                buyer.body.addReplace(part, 1);
            }

            @Override
            boolean available(Character buyer) {
                boolean possible = true;
                if (onlyReplace) {
                    possible = buyer.body.has(part.getType());
                }
                if (normal == null) {
                    return possible && !buyer.body.has(part.getType()); // never
                                                                        // available
                } else {
                    return possible && !buyer.body.contains(part);
                }
            }

            @Override
            double priority(Character buyer) {
                return priority;
            }
        });

        selection.add(new ShopSelection("Remove " + name, removePrice) {
            @Override
            void buy(Character buyer) {
                if (normal == null) {
                    buyer.body.removeOne(part.getType());
                } else {
                    buyer.body.remove(part);
                    buyer.body.addReplace(normal, 1);
                }
            }

            @Override
            boolean available(Character buyer) {
                if (normal == null) {
                    return buyer.body.has(part.getType());
                } else {
                    return buyer.body.contains(part);
                }
            }

            @Override
            double priority(Character buyer) {
                return 1;
            }
        });
    }

    private void addTraitMod(String name, String removeName, final Trait trait, int addPrice, int removePrice,
                    final CharacterRequirement requirement) {
        selection.add(new ShopSelection(name, addPrice) {
            @Override
            void buy(Character buyer) {
                buyer.add(trait);
            }

            @Override
            boolean available(Character buyer) {
                return !buyer.hasTrait(trait) && requirement.isSatisfied(buyer);
            }
        });

        selection.add(new ShopSelection(removeName, removePrice) {
            @Override
            void buy(Character buyer) {
                buyer.remove(trait);
            }

            @Override
            boolean available(Character buyer) {
                return buyer.hasTrait(trait);
            }

            @Override
            double priority(Character buyer) {
                return 1;
            }
        });
    }

    private void populateSelection() {
        CharacterRequirement noRequirement = character -> true;

        selection.add(new ShopSelection("Breast Expansion", 1500) {
            @Override
            void buy(Character buyer) {
                BreastsPart target = buyer.body.getBreastsBelow(BreastsPart.maximumSize().size);
                assert target != null;
                buyer.body.remove(target);
                buyer.body.addReplace(target.upgrade(), 1);
            }

            @Override
            boolean available(Character buyer) {
                BreastsPart target = buyer.body.getBreastsBelow(BreastsPart.maximumSize().size);
                return target != null;
            }

            @Override
            double priority(Character buyer) {
                return 10;
            }
        });

        selection.add(new ShopSelection("Breast Reduction", 1500) {
            @Override
            void buy(Character buyer) {
                BreastsPart target = buyer.body.getBreastsAbove(BreastsPart.flat.size);
                assert target != null;
                buyer.body.remove(target);
                buyer.body.addReplace(target.downgrade(), 1);
            }

            @Override
            boolean available(Character buyer) {
                BreastsPart target = buyer.body.getBreastsAbove(BreastsPart.flat.size);
                return target != null;
            }

            @Override
            double priority(Character buyer) {
                return 5;
            }
        });

        selection.add(new ShopSelection("Grow Cock", 2500) {
            @Override
            void buy(Character buyer) {
                buyer.body.addReplace(new CockPart(CockPart.Size.minimumSize()), 1);
            }

            @Override
            boolean available(Character buyer) {
                return !buyer.hasDick();
            }

            @Override
            double priority(Character buyer) {
                return buyer.dickPreference();
            }
        });

        selection.add(new ShopSelection("Remove Cock", 2500) {
            @Override
            void buy(Character buyer) {
                buyer.body.removeAll("cock");
                buyer.body.removeAll("balls");
            }

            @Override
            boolean available(Character buyer) {
                return buyer.hasDick();
            }

            @Override
            double priority(Character buyer) {
                return Math.max(0, buyer.pussyPreference() - 7);
            }
        });

        selection.add(new ShopSelection("Remove Pussy", 2500) {
            @Override
            void buy(Character buyer) {
                buyer.body.removeAll("pussy");
            }

            @Override
            boolean available(Character buyer) {
                return buyer.hasPussy();
            }

            @Override
            double priority(Character buyer) {
                return Math.max(0, buyer.dickPreference() - 7);
            }
        });

        selection.add(new ShopSelection("Grow Balls", 1000) {
            @Override
            void buy(Character buyer) {
                buyer.body.addReplace(new GenericBodyPart("balls", 0, 1.0, 1.5, "balls", ""), 1);
            }

            @Override
            boolean available(Character buyer) {
                return !buyer.hasBalls() && buyer.hasDick();
            }

            @Override
            double priority(Character buyer) {
                return Math.max(0, 4 - buyer.dickPreference());
            }
        });

        selection.add(new ShopSelection("Remove Balls", 1000) {
            @Override
            void buy(Character buyer) {
                buyer.body.removeAll("balls");
            }

            @Override
            boolean available(Character buyer) {
                return buyer.hasBalls();
            }

            @Override
            double priority(Character buyer) {
                return Math.max(0, buyer.pussyPreference() - 5);
            }
        });

        selection.add(new ShopSelection("Remove Wings", 1000) {
            @Override
            void buy(Character buyer) {
                buyer.body.removeAll("wings");
            }

            @Override
            boolean available(Character buyer) {
                return buyer.body.has("wings");
            }

            @Override
            double priority(Character buyer) {
                return 0;
            }
        });

        selection.add(new ShopSelection("Remove Tail", 1000) {
            @Override
            void buy(Character buyer) {
                buyer.body.removeAll("tail");
            }

            @Override
            boolean available(Character buyer) {
                return buyer.body.has("tail");
            }

            @Override
            double priority(Character buyer) {
                return 0;
            }
        });

        selection.add(new ShopSelection("Restore Ears", 1000) {
            @Override
            void buy(Character buyer) {
                buyer.body.removeAll("ears");
                buyer.body.add(EarPart.normal);
            }

            @Override
            boolean available(Character buyer) {
                return buyer.body.getRandom("ears") != EarPart.normal;
            }

            @Override
            double priority(Character buyer) {
                return 0;
            }
        });
        selection.add(new ShopSelection("Grow Pussy", 2500) {
            @Override
            void buy(Character buyer) {
                buyer.body.addReplace(PussyPart.normal, 1);
            }

            @Override
            boolean available(Character buyer) {
                return !buyer.hasPussy();
            }

            @Override
            double priority(Character buyer) {
                return buyer.pussyPreference();
            }
        });

        selection.add(new ShopSelection("Cock Expansion", 1500) {
            @Override
            void buy(Character buyer) {
                CockPart target = buyer.body.getCockBelow(CockPart.Size.maximumSize().length);
                assert target != null;
                buyer.body.remove(target);
                buyer.body.addReplace(target.upgrade(), 1);
            }

            @Override
            boolean available(Character buyer) {
                CockPart target = buyer.body.getCockBelow(CockPart.Size.maximumSize().length);
                return target != null;
            }

            @Override
            double priority(Character buyer) {
                CockPart part = buyer.body.getRandomCock();
                if (part != null) {
                    return CockPart.Size.big.length > part.getSize().length ? 10 : 3;
                }
                return 0;
            }
        });

        selection.add(new ShopSelection("Cock Reduction", 1500) {
            @Override
            void buy(Character buyer) {
                CockPart target = buyer.body.getCockAbove(CockPart.Size.minimumSize().length);
                assert target != null;
                buyer.body.remove(target);
                buyer.body.addReplace(target.downgrade(), 1);
            }

            @Override
            boolean available(Character buyer) {
                CockPart target = buyer.body.getCockAbove(CockPart.Size.minimumSize().length);
                return target != null;
            }

            @Override
            double priority(Character buyer) {
                CockPart part = buyer.body.getRandomCock();
                if (part != null) {
                    return CockPart.Size.small.length < part.getSize().length ? 3 : 0;
                }
                return 0;
            }
        });

        selection.add(new ShopSelection("Restore Cock", 1500) {
            @Override
            void buy(Character buyer) {
                CockPart target = buyer.body.getRandomCock();
                assert target != null;
                buyer.body.remove(target);
                buyer.body.addReplace(new CockPart(target, CockPart.Mod.normal), 1);
            }

            @Override
            boolean available(Character buyer) {
                Optional<BodyPart> optTarget =
                                buyer.body.get("cock").stream().filter(c -> !c.isGeneric(buyer)).findAny();
                return optTarget.isPresent();
            }

            @Override
            double priority(Character buyer) {
                return 0;
            }
        });

        selection.add(new ShopSelection("Restore Pussy", 1500) {
            @Override
            void buy(Character buyer) {
                PussyPart target = buyer.body.getRandomPussy();
                assert target != null;
                buyer.body.remove(target);
                buyer.body.addReplace(PussyPart.normal, 1);
            }

            @Override
            boolean available(Character buyer) {
                Optional<BodyPart> optTarget =
                                buyer.body.get("pussy").stream().filter(c -> c != PussyPart.normal).findAny();
                return optTarget.isPresent();
            }

            @Override
            double priority(Character buyer) {
                return 0;
            }
        });

        addTraitMod("Vaginal Tongue", "Remove V.Tongue", Trait.vaginaltongue, 10000, 10000,
                        Character::hasPussy);
        addTraitMod("Laced Juices", "Remove L.Juices", Trait.lacedjuices, 1000, 1000,
                        noRequirement);
        addTraitMod("Permanent Lactation", "Stop Lactating", Trait.lactating, 1000, 1000,
                        noRequirement);
        addTraitMod("Pheromones", "Remove Pheromones", Trait.augmentedPheromones, 1500, 1500,
                        noRequirement);
        addBodyPartMod("Fused Boots",
                        new GenericBodyPart("Fused Boots",
                                        "{self:name-possessive} legs are wrapped in a shiny black material that look fused on.",
                                        .3, 1.5, .7, true, "feet", ""),
                        new GenericBodyPart("feet", 0, 1, 1, "feet", ""), 1000, 1000);
        addBodyPartMod("Anal Pussy", AnalPussyPart.generic, AssPart.generic, 2000, 2000);
        addBodyPartMod("Fused Gloves",
                        new GenericBodyPart("Fused Gloves",
                                        "{self:name-possessive} arms and hands are wrapped in a shiny black material that look fused on.",
                                        .2, 1.5, .7, true, "hands", ""),
                        new GenericBodyPart("hands", 0, 1, 1, "hands", ""), 1000, 1000);
    }

    @Override
    public boolean available() {

        return Global.global.checkFlag(Flag.bodyShop);
    }

    private void displaySelection() {
        Global.global.gui().message("You have :$" + player.money + " to spend.");
        for (ShopSelection s : selection) {
            if (s.available(player) && player.money >= s.price) {
                Global.global.gui().choose(this, s.choice, "Price: $" + s.price);
                Global.gui().message(s.choice + ": $" + s.price);
            }
        }
        Global.global.gui().choose(this, "Leave");
    }

    @Override
    public void start() {
        if (choice.equals("Start")) {
            Global.global.gui().clearText();
            Global.global.gui().commandPanel.clearCommand(Global.global.gui());
            Global.global.gui().message(
                            "While wondering why you're even here, you walk into the rundown shack named \"The Body Shop\". The proprietor looks at you strangely then mutely points to the sign.");
            displaySelection();
            return;
        }
        for (ShopSelection s : selection) {
            if (s.choice.equals(choice)) {
                Global.global.gui().message("<br/>You've selected " + s.choice
                                + ". While wondering if this was such a great idea, you follow the proprietor into the back room...");
                s.buy(player);
                player.money -= s.price;
                done(true);
                return;
            }
        }
        Global.global.gui().message(
                        "<br/>You have some second thoughts about letting some stranger play with your body. You think up some excuse and quickly leave the shack.");
        done(false);
    }

    public void shop(Character npc, int budget) {
        int chance = 100;
        while (budget > 0) {
            if (Rng.rng.random(100) > chance) {
                break;
            }
            chance /= 3;
            List<ShopSelection> avail = new ArrayList<ShopSelection>();
            for (int i = 0; i < 10; i++) {
                avail.add(new ShopSelection("none" + i, 0) {
                    @Override
                    void buy(Character buyer) {

                    }

                    @Override
                    boolean available(Character buyer) {
                        return true;
                    }
                });
            }
            for (ShopSelection s : selection) {
                if (s.available(npc) && budget >= s.price) {
                    for (int i = 0; i < s.priority(npc); i++) {
                        avail.add(s);
                    }
                }
            }

            if (avail.size() == 0) {
                return;
            }
            int randomindex = Rng.rng.random(avail.size());
            ShopSelection choice = avail.get(randomindex);
            npc.money -= choice.price;
            budget -= choice.price;
            choice.buy(npc);
            if (Global.global.isDebugOn(DebugFlags.DEBUG_PLANNING) && !choice.choice.contains("none")) {
                System.out.println(npc.name() + " purchased " + choice.choice);
            }
        }
    }
}
