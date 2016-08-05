package nightgames.daytime;

import nightgames.characters.Character;
import nightgames.characters.Player;
import nightgames.global.Flag;
import nightgames.global.Global;
import nightgames.status.addiction.Addiction;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.Stream;

class AddictionRemoval extends Activity {

    AddictionRemoval(Player player) {
        super("Addiction Removal", player);
    }

    private enum TreatmentOption {
        UNSAFE_OPT("Overload it", 5000,
                        "\n\nA quick look at your finances reveal that only the risky option is affordable for you right now. That may be a problem.",
                        "Nervously, you hand over the money for the overload treatment. You don't"
                                        + " remember what happened next, but you do know that now your addiction is far"
                                        + " stronger than before. Let's hope this works."),
        SAFE_OPT("Safe but expensive", 15000, "",
                        "You dole out the mountain of cash and are taken to the back for your treatment. When you emerge, you are completely free of your addiction."),
        LEAVE("Leave", 0, "\n\nUnfortunately, you don't have the cash for either option right now.", ""),;


        String description;
        int cost;
        String financialCommentary;
        String purchaseDescription;

        TreatmentOption(String description, int cost, String financialCommentary, String purchaseDescription) {
            this.description = description;
            this.cost = cost;
            this.financialCommentary = financialCommentary;
            this.purchaseDescription = purchaseDescription;
        }

        String buttonDescription() {
            switch (this) {
                case LEAVE:
                    return description;
                default:
                    return String.format("%s: $%d", description, cost);
            }
        }

        String purchase(Player player) {
            player.modMoney(-cost);
            switch (this) {
                case UNSAFE_OPT:
                    player.getStrongestAddiction().ifPresent(Addiction::overload);
                case SAFE_OPT:
                    player.getStrongestAddiction().ifPresent(player.getAddictions()::remove);
            }
            return purchaseDescription;
        }

        boolean affordable(Player player) {
            return cost >= player.money;
        }

        static List<TreatmentOption> affordableOptions(Player player) {
            return Stream.of(values()).filter(treatmentOption -> treatmentOption.affordable(player))
                            .collect(Collectors.toList());
        }

        static String financialCommentary(Player player) {
            if (SAFE_OPT.affordable(player)) {
                return SAFE_OPT.financialCommentary;
            } else if (UNSAFE_OPT.affordable(player)) {
                return UNSAFE_OPT.financialCommentary;
            } else {
                return LEAVE.financialCommentary;
            }
        }
    }


    @Override
    public boolean known() {
        return Global.global.checkFlag(Flag.AddictionAdvice) && Global.global.getPlayer()
                                                               .checkAddiction();
    }

    @Override public void start() throws InterruptedException {
        controller.newPage("You walk to the place Aesop told you about "
                        + "where you're supposed to be able to get rid of your addictions."
                        + " An imperious-looking woman in a lab coat is there to meet you, and explains"
                        + " that you have two options. The cheap options will overload the addiction,"
                        + " basically making it enormously strong for one night and then snuffing it out"
                        + " completely. The other, more expensive choice will reduce or eliminate the addiction"
                        + " without side-effects, although very strong addictions may require several treatments."
                        + "\n\n(this is a placeholder -- note that these treatments only affect your current"
                        + " strongest addiction)");
        controller.message(TreatmentOption.financialCommentary(player));
        Map<String, String> optionMap = TreatmentOption.affordableOptions(player).stream()
                        .collect(Collectors.toMap(TreatmentOption::name, TreatmentOption::buttonDescription));
        TreatmentOption option =
                        controller.getChoice(optionMap).map(TreatmentOption::valueOf).orElse(TreatmentOption.LEAVE);
        String purchaseMessage = option.purchase(player);
        controller.message(purchaseMessage);
    }

    public void shop(Character npc, int budget) {}

}
