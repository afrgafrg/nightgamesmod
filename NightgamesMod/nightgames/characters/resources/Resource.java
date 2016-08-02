package nightgames.characters.resources;

/**
 * In Nightgames, a Resource is a Character stat that can be depleted and refilled.
 */
public enum Resource {
    STAMINA("Stamina",
                    "Stamina represents your endurance and ability to keep fighting. If it drops to zero, you'll be temporarily stunned."), AROUSAL(
                    "Arousal",
                    "Arousal is raised when your opponent pleasures or seduces you. If it hits your max, you'll orgasm and lose a lot of Willpower."), MOJO(
                    "Mojo",
                    "Mojo is the abstract representation of your momentum and style. Basic moves will build mojo, while special moves will consume it."), WILLPOWER(
                    "Willpower",
                    "Willpower is a representation of your ability to perservere. When you run out of Willpower, you lose the fight.");

    Resource(String name, String description) {
        this.name = name;
        this.description = description;
    }

    public String name;
    public String description;

}
