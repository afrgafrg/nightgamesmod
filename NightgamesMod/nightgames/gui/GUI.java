package nightgames.gui;

import nightgames.Resources.ResourceLoader;
import nightgames.characters.Attribute;
import nightgames.characters.Character;
import nightgames.characters.Player;
import nightgames.combat.Combat;
import nightgames.daytime.Activity;
import nightgames.debug.DebugGUIPanel;
import nightgames.global.*;
import nightgames.global.time.Time;
import nightgames.gui.button.*;
import nightgames.gui.keybinds.Keybinds;
import nightgames.gui.keybinds.KeymapLoader;
import nightgames.gui.resources.ResourcesPanel;
import nightgames.gui.useraction.UserAction;
import nightgames.items.Item;
import nightgames.skills.TacticGroup;

import javax.imageio.ImageIO;
import javax.swing.*;
import javax.swing.border.CompoundBorder;
import javax.swing.filechooser.FileFilter;
import javax.swing.filechooser.FileNameExtensionFilter;
import javax.swing.text.BadLocationException;
import javax.swing.text.DefaultCaret;
import javax.swing.text.html.HTMLDocument;
import javax.swing.text.html.HTMLEditorKit;
import java.awt.*;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.*;
import java.util.List;
import java.util.stream.Collectors;

public class GUI extends JFrame implements Observer {
    public static GUI gui;

    private static final long serialVersionUID = 451431916952047183L;
    public CommandPanel commandPanel;
    public Keybinds keybinds;
    public Map<UserAction, Runnable> actionMap;
    public Combat combat;
    private Map<TacticGroup, List<SkillButton>> skills;
    private TacticGroup currentTactics;
    private JTextPane textPane;
    private JLabel lvl;
    private JLabel xp;
    private JPanel topPanel;
    private JLabel loclbl;
    private JLabel timeLabel;
    private JLabel cashLabel;
    private Panel panel0;
    CreationGUI creation;
    private JScrollPane textScroll;
    private JPanel gamePanel;
    private JToggleButton stsbtn;
    private JPanel statusPanel;
    private JPanel mainPanel;
    private JPanel clothesPanel;
    private JPanel optionsPanel;
    private JPanel portraitPanel;
    private JPanel centerPanel;
    private JLabel portrait;
    private JComponent map;
    private JPanel imgPanel;
    private JLabel imgLabel;
    private JRadioButton rdnormal;
    private JRadioButton rddumb;
    private JRadioButton rdeasy;
    private JRadioButton rdhard;
    private JRadioButton rdMsgOn;
    private JRadioButton rdMsgOff;
    private JRadioButton rdAutoNextOn;
    private JRadioButton rdAutoNextOff;
    private JRadioButton rdautosaveon;
    private JRadioButton rdautosaveoff;
    private JRadioButton rdporon;
    private JRadioButton rdporoff;
    private JRadioButton rdimgon;
    private JRadioButton rdimgoff;
    private JRadioButton rdfntnorm;
    private JRadioButton rdnfntlrg;
    private JSlider malePrefSlider;
    private int width;
    private int height;
    public int fontsize;
    private JMenuItem mntmQuitMatch;
    public NgsChooser saveFileChooser;
    private Box groupBox;
	private JFrame inventoryFrame;

    private final static String USE_PORTRAIT = "PORTRAIT";
    private final static String USE_MAP = "MAP";
    private final static String USE_NONE = "NONE";
    private static final String USE_MAIN_TEXT_UI = "MAIN_TEXT";
    private static final String USE_CLOSET_UI = "CLOSET";

    public GUI() {
        gui = this;

        // frame title
        setTitle("NightGames Mod");
        setBackground(GUIColors.bgDark.color);
        // closing operation
        setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);

        // resolution resolver

        height = (int) (Toolkit.getDefaultToolkit().getScreenSize().getHeight() * 0.85);
        width = (int) (Toolkit.getDefaultToolkit().getScreenSize().getWidth() * 0.85);

        setPreferredSize(new Dimension(width, height));

        // center the window on the monitor

        int y = (int) Toolkit.getDefaultToolkit().getScreenSize().getHeight();
        int x = (int) Toolkit.getDefaultToolkit().getScreenSize().getWidth();

        int x1 = x / 2 - width / 2;
        int y1 = y / 2 - height / 2;

        this.setLocation(x1, y1);

        // menu bar

        getContentPane().setLayout(new BoxLayout(getContentPane(), 1));

        JMenuBar menuBar = new JMenuBar();
        setJMenuBar(menuBar);

        // menu bar - new game

        JMenuItem mntmNewgame = new NewGameMenuItem();


        menuBar.add(mntmNewgame);

        // menu bar - load game - can't change because can't figure out where
        // the frame is with swing

        JMenuItem mntmLoad = new JMenuItem("Load"); // Initializer

        //mntmLoad.setForeground(Color.WHITE); // Formatting
        //mntmLoad.setBackground(GUIColors.bgGrey.color);
        mntmLoad.setHorizontalAlignment(SwingConstants.CENTER);

        mntmLoad.addActionListener(arg0 -> loadWithDialog());

        menuBar.add(mntmLoad);

        // menu bar - options

        JMenuItem mntmOptions = new JMenuItem("Options");
        //mntmOptions.setForeground(Color.WHITE);
        //mntmOptions.setBackground(GUIColors.bgGrey.color);

        menuBar.add(mntmOptions);

        // options submenu creator

        optionsPanel = new JPanel();
        optionsPanel.setLayout(new GridLayout(0, 3, 0, 0));

        // AILabel - options submenu - visible

        JLabel AILabel = new JLabel("AI Mode");
        ButtonGroup ai = new ButtonGroup();
        rdnormal = new JRadioButton("Normal");
        rddumb = new JRadioButton("Easier");
        ai.add(rdnormal);
        ai.add(rddumb);
        optionsPanel.add(AILabel);
        optionsPanel.add(rdnormal);
        optionsPanel.add(rddumb);

        // difficultyLabel - options submenu - visible

        JLabel difficultyLabel = new JLabel("NPC Bonuses (Mainly XP)");
        ButtonGroup diff = new ButtonGroup();
        rdeasy = new JRadioButton("Off");
        rdhard = new JRadioButton("On");
        diff.add(rdeasy);
        diff.add(rdhard);
        optionsPanel.add(difficultyLabel);
        optionsPanel.add(rdeasy);
        optionsPanel.add(rdhard);

        // systemMessageLabel - options submenu - visible

        JLabel systemMessageLabel = new JLabel("System Messages");
        ButtonGroup sysMsgG = new ButtonGroup();
        rdMsgOn = new JRadioButton("On");
        rdMsgOff = new JRadioButton("Off");
        sysMsgG.add(rdMsgOn);
        sysMsgG.add(rdMsgOff);
        optionsPanel.add(systemMessageLabel);
        optionsPanel.add(rdMsgOn);
        optionsPanel.add(rdMsgOff);

        JLabel autoNextLabel = new JLabel("Fast Combat Display");
        ButtonGroup autoNextG = new ButtonGroup();
        rdAutoNextOn = new JRadioButton("On");
        rdAutoNextOff = new JRadioButton("Off");
        autoNextG.add(rdAutoNextOn);
        autoNextG.add(rdAutoNextOff);
        optionsPanel.add(autoNextLabel);
        optionsPanel.add(rdAutoNextOn);
        optionsPanel.add(rdAutoNextOff);

        // autosave - options submenu - visible -(not currently working?)

        JLabel lblauto = new JLabel("Autosave (saves to auto.sav)");
        ButtonGroup auto = new ButtonGroup();
        rdautosaveon = new JRadioButton("on");
        rdautosaveoff = new JRadioButton("off");
        auto.add(rdautosaveon);
        auto.add(rdautosaveoff);
        optionsPanel.add(lblauto);
        optionsPanel.add(rdautosaveon);
        optionsPanel.add(rdautosaveoff);

        // portraitsLabel - options submenu - visible

        JLabel portraitsLabel = new JLabel("Portraits");

        // portraits - options submenu - visible

        ButtonGroup portraitsButton = new ButtonGroup();

        // rdpron / rdporoff - options submenu - visible

        rdporon = new JRadioButton("on");
        rdporoff = new JRadioButton("off");
        portraitsButton.add(rdporon);
        portraitsButton.add(rdporoff);
        optionsPanel.add(portraitsLabel);
        optionsPanel.add(rdporon);
        optionsPanel.add(rdporoff);

        // imageLabel - options submenu - visible
        JLabel imageLabel = new JLabel("Images");
        ButtonGroup image = new ButtonGroup();
        rdimgon = new JRadioButton("on");
        rdimgoff = new JRadioButton("off");
        image.add(rdimgon);
        image.add(rdimgoff);
        optionsPanel.add(imageLabel);
        optionsPanel.add(rdimgon);
        optionsPanel.add(rdimgoff);

        // fontSizeLabel - options submenu - visible
        JLabel fontSizeLabel = new JLabel("Font Size");
        ButtonGroup size = new ButtonGroup();
        rdfntnorm = new JRadioButton("normal");
        rdnfntlrg = new JRadioButton("large");
        size.add(rdfntnorm);
        size.add(rdnfntlrg);

        optionsPanel.add(fontSizeLabel);
        optionsPanel.add(rdfntnorm);
        optionsPanel.add(rdnfntlrg);

        JLabel pronounLabel = new JLabel("Pronoun Usage");
        ButtonGroup pronoun = new ButtonGroup();
        JRadioButton rdPronounBody = new JRadioButton("Based on Anatomy");
        JRadioButton rdPronounFemale = new JRadioButton("Always Female");
        pronoun.add(rdPronounBody);
        pronoun.add(rdPronounFemale);
        optionsPanel.add(pronounLabel);
        optionsPanel.add(rdPronounBody);
        optionsPanel.add(rdPronounFemale);

        // m/f preference (no (other) males in the games yet... good for
        // modders?)

        // malePrefLabel - options submenu - visible
        JLabel malePrefLabel = new JLabel("Female vs. Male Preference");
        optionsPanel.add(malePrefLabel);
        malePrefSlider = new JSlider(SwingConstants.HORIZONTAL, 0, 10, 1);
        malePrefSlider.setMajorTickSpacing(5);
        malePrefSlider.setMinorTickSpacing(1);
        malePrefSlider.setPaintTicks(true);
        malePrefSlider.setPaintLabels(true);
        malePrefSlider.setLabelTable(new Hashtable<Integer, JLabel>() {
            private static final long serialVersionUID = -4212836698571224221L;
            {
                put(0, new JLabel("Female"));
                put(5, new JLabel("Mixed"));
                put(10, new JLabel("Male"));
            }
        });
        malePrefSlider.setValue(Math.round(Global.global.getValue(Flag.malePref)));
        malePrefSlider.setToolTipText("This setting affects the gender your opponents will gravitate towards once that"
                        + " option becomes available.");
        malePrefSlider.addChangeListener(e -> Global.global.setCounter(Flag.malePref, malePrefSlider.getValue()));

        // malePrefPanel - options submenu - visible
        optionsPanel.add(malePrefSlider);
        mntmOptions.addActionListener(arg0 -> {
            if (Global.global.checkFlag(Flag.systemMessages)) {
                rdMsgOn.setSelected(true);
            } else {
                rdMsgOff.setSelected(true);
            }

            if (Global.global.checkFlag(Flag.AutoNext)) {
                rdAutoNextOn.setSelected(true);
            } else {
                rdAutoNextOff.setSelected(true);
            }

            if (Global.global.checkFlag(Flag.hardmode)) {
                rdhard.setSelected(true);
            } else {
                rdeasy.setSelected(true);
            }

            if (Global.global.checkFlag(Flag.dumbmode)) {
                rddumb.setSelected(true);
            } else {
                rdnormal.setSelected(true);
            }
            if (Global.global.checkFlag(Flag.autosave)) {
                rdautosaveon.setSelected(true);
            } else {
                rdautosaveoff.setSelected(true);
            }
            if (Global.global.checkFlag(Flag.noportraits)) {
                rdporoff.setSelected(true);
            } else {
                rdporon.setSelected(true);
            }
            if (Global.global.checkFlag(Flag.noimage)) {
                rdimgoff.setSelected(true);
            } else {
                rdimgon.setSelected(true);
            }
            if (Global.global.checkFlag(Flag.largefonts)) {
                rdnfntlrg.setSelected(true);
            } else {
                rdfntnorm.setSelected(true);
            }
            if (Global.global.checkFlag(Flag.FemalePronounsOnly)) {
                rdPronounFemale.setSelected(true);
            } else {
                rdPronounBody.setSelected(true);
            }
            malePrefSlider.setValue(Math.round(Global.global.getValue(Flag.malePref)));

            int result = JOptionPane.showConfirmDialog(GUI.this, optionsPanel, "Options", JOptionPane.OK_CANCEL_OPTION,
                            JOptionPane.INFORMATION_MESSAGE);
            if (result == JOptionPane.OK_OPTION) {
                Global.global.setFlag(Flag.systemMessages, rdMsgOn.isSelected());
                Global.global.setFlag(Flag.AutoNext, rdAutoNextOn.isSelected());
                Global.global.setFlag(Flag.dumbmode, !rdnormal.isSelected());
                Global.global.setFlag(Flag.hardmode, rdhard.isSelected());
                Global.global.setFlag(Flag.autosave, rdautosaveon.isSelected());
                Global.global.setFlag(Flag.noportraits, rdporoff.isSelected());
                Global.global.setFlag(Flag.FemalePronounsOnly, rdPronounFemale.isSelected());
                if (!rdporon.isSelected()) {
                    showNone();
                }
                if (rdimgon.isSelected()) {
                    Global.global.unflag(Flag.noimage);
                } else {
                    Global.global.flag(Flag.noimage);
                    if (imgLabel != null) {
                        imgPanel.remove(imgLabel);
                    }
                    imgPanel.repaint();
                }
                if (rdnfntlrg.isSelected()) {
                    Global.global.flag(Flag.largefonts);
                    fontsize = 6;
                } else {
                    Global.global.unflag(Flag.largefonts);
                    fontsize = 5;
                }
            }
        });

        // menu bar - credits

        JMenuItem mntmCredits = new JMenuItem("Credits");
        //mntmCredits.setForeground(Color.WHITE);
        //mntmCredits.setBackground(GUIColors.bgGrey.color);
        menuBar.add(mntmCredits);

        // menu bar - quit match

        mntmQuitMatch = new JMenuItem("Quit Match");
        mntmQuitMatch.setEnabled(false);
        //mntmQuitMatch.setForeground(Color.WHITE);
        //mntmQuitMatch.setBackground(GUIColors.bgGrey.color);
        mntmQuitMatch.addActionListener(arg0 -> {
            int result = JOptionPane.showConfirmDialog(GUI.this,
                            "Do you want to quit for the night? Your opponents will continue to fight and gain exp.",
                            "Retire early?", JOptionPane.OK_CANCEL_OPTION, JOptionPane.INFORMATION_MESSAGE);
            if (result == JOptionPane.OK_OPTION) {
                Global.global.getMatch().quit();
            }
        });
        menuBar.add(mntmQuitMatch);
        mntmCredits.addActionListener(arg0 -> {
            JPanel panel = new JPanel();
            panel.add(new JLabel("<html>Night Games created by The Silver Bard<br/>"
                            + "Reyka and Samantha and a whole lot of stuff created by DNDW<br/>" + "Upgraded Strapon created by MotoKuchoma<br/>"
                            + "Strapon victory scenes created by Legion<br/>" + "Advanced AI by Jos<br/>"
                            + "Magic Training scenes by Legion<br/>" + "Jewel 2nd Victory scene by Legion<br/>"
                            + "Video Games scenes 1-9 by Onyxdime<br/>"
                            + "Kat Penetration Victory and Defeat scenes by Onyxdime<br/>"
                            + "Kat Non-Penetration Draw scene by Onyxdime<br/>"
                            + "Mara/Angel threesome scene by Onyxdime<br/>"
                            + "Footfetish expansion scenes by Sakruff<br/>"
                            + "Mod by Nergantre<br/>"
                            + "A ton of testing by Bronzechair</html>"));
            Object[] options = {"OK", "DEBUG"};
            Object[] okOnly = {"OK"};
            int results = JOptionPane.showOptionDialog(GUI.this, panel, "Credits", JOptionPane.DEFAULT_OPTION,
                            JOptionPane.INFORMATION_MESSAGE, null, options, options[0]);
            if (results == 1 && Global.global.inGame()) {
                JPanel debugPanel = new DebugGUIPanel();
                JOptionPane.showOptionDialog(GUI.this, debugPanel, "Debug", JOptionPane.PLAIN_MESSAGE,
                                JOptionPane.INFORMATION_MESSAGE, null, okOnly, okOnly[0]);
            } else if (results == 1) {
                JOptionPane.showOptionDialog(GUI.this, "Not in game", "Debug", JOptionPane.PLAIN_MESSAGE,
                                JOptionPane.INFORMATION_MESSAGE, null, okOnly, okOnly[0]);
            }
        });

        // panel layouts

        // gamePanel - everything is contained within it

        gamePanel = new JPanel();
        getContentPane().add(gamePanel);
        gamePanel.setLayout(new BoxLayout(gamePanel, 1));

        // panel0 - invisible, only handles topPanel

        panel0 = new Panel();
        gamePanel.add(panel0);
        panel0.setLayout(new BoxLayout(panel0, 0));

        // topPanel - invisible, menus

        topPanel = new JPanel();
        panel0.add(topPanel);
        topPanel.setLayout(new GridLayout(0, 1, 0, 0));

        // mainPanel - body of GUI (not including the top bar and such)

        mainPanel = new JPanel();
        gamePanel.add(mainPanel);
        mainPanel.setLayout(new BorderLayout(0, 0));


        // statusPanel - visible, character status

        statusPanel = new JPanel();
        statusPanel.setLayout(new BoxLayout(statusPanel, 1));

        // portraitPanel - invisible, contains imgPanel, west panel

        portraitPanel = new JPanel();
        mainPanel.add(portraitPanel, BorderLayout.WEST);

        portraitPanel.setLayout(new ShrinkingCardLayout());

        portraitPanel.setBackground(GUIColors.bgDark.color);
        portrait = new JLabel("");
        portrait.setVerticalAlignment(SwingConstants.TOP);
        portraitPanel.add(portrait, USE_PORTRAIT);

        map = new MapComponent();
        portraitPanel.add(map, USE_MAP);
        portraitPanel.add(Box.createGlue(), USE_NONE);

        // centerPanel, a CardLayout that will flip between the main text and different UIs
        centerPanel = new JPanel(new ShrinkingCardLayout());
        mainPanel.add(centerPanel, BorderLayout.CENTER);

        // textScroll
        textScroll = new JScrollPane();

        // textPane
        textPane = new JTextPane();
        DefaultCaret caret = (DefaultCaret) textPane.getCaret();
        caret.setUpdatePolicy(DefaultCaret.ALWAYS_UPDATE);
        textPane.setForeground(GUIColors.textColorLight.color);
        textPane.setBackground(GUIColors.bgLight.color);
        textPane.setPreferredSize(new Dimension(width, 400));
        textPane.setEditable(false);
        textPane.setContentType("text/html");
        textScroll.setViewportView(textPane);
        fontsize = 5;

        // imgPanel - visible, contains imgLabel
        imgPanel = new JPanel();

        // imgLabel - probably contains the in-battle images
        imgLabel = new JLabel();
        imgPanel.add(imgLabel, BorderLayout.NORTH);
        imgLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        // textAreaPanel - the area with the main text window and the in-battle stance image if active.
        JPanel textAreaPanel = new JPanel();
        textAreaPanel.setLayout(new BoxLayout(textAreaPanel, BoxLayout.PAGE_AXIS));
        textAreaPanel.add(imgLabel);
        textAreaPanel.add(textScroll);
        textAreaPanel.setBackground(GUIColors.bgDark.color);

        centerPanel.add(textAreaPanel, USE_MAIN_TEXT_UI);

        // clothesPanel - used for closet ui
        clothesPanel = new JPanel();
        clothesPanel.setLayout(new GridLayout(0, 1));
        clothesPanel.setBackground(new Color(25, 25, 50));
        centerPanel.add(clothesPanel, USE_CLOSET_UI);

        GameButton debug = new GameButton("Debug");

        // commandPanel - visible, contains the player's command buttons
        // TODO: reconcile the two CommandPanel implementations
        groupBox = Box.createHorizontalBox();
        groupBox.setBackground(GUIColors.bgDark.color);
        groupBox.setBorder(new CompoundBorder());
        JPanel groupPanel = new JPanel();
        gamePanel.add(groupPanel);

        commandPanel = new CommandPanel(120, width);
        gamePanel.add(commandPanel);

        Path keymapData = Paths.get("data/Keybinds.json");
        keybinds = new Keybinds(keymapData);
        KeymapLoader.saveKeybinds(keymapData, keybinds);

        // TODO: wire up UserActions to this KeyListener
        actionMap = Collections.synchronizedMap(new EnumMap<UserAction, Runnable>(UserAction.class));
        this.addKeyListener(new KeyListener() {
            /**
             * Space bar will select the first option, unless they are in the default actions list.
             */
            @Override
            public void keyReleased(KeyEvent e) {
                Optional<UserAction> actionOptional = keybinds.actionFromHotkey(String.valueOf(e.getKeyChar()));
                actionOptional.ifPresent(action -> actionMap.);
                if (buttonOptional.isPresent()) {
                    buttonOptional.get().keyActivated();
                }
            }

            @Override
            public void keyTyped(KeyEvent e) {}

            @Override
            public void keyPressed(KeyEvent e) {}
        });

        skills = new HashMap<>();
        clearCommand();
        currentTactics = TacticGroup.all;
        createCharacter();
        setVisible(true);
        pack();
        JPanel panel = (JPanel) getContentPane();
        panel.setFocusable(false);

        // Use this for making save dialogs
        saveFileChooser = new NgsChooser(this);
    }

    public Optional<File> askForSaveFile() {
        return saveFileChooser.askForSaveFile();
    }

    // combat GUI

    public Combat beginCombat(Character player, Character enemy) {
        showPortrait();
        combat = new Combat(player, enemy, player.location());
        combat.addObserver(this);
        combat.setBeingObserved(true);
        loadPortrait(combat, player, enemy);
        showPortrait();
        return combat;
    }

    // image loader

    public void displayImage(String path, String artist) {
        if (Global.global.checkFlag(Flag.noimage)){
            return;
        }
        if (Global.global.isDebugOn(DebugFlags.DEBUG_GUI)) {
            System.out.println("Display image: " + path);
        }
        if (!(new File("assets/"+path).canRead())) {
            return;
        }
        BufferedImage pic = null;
        try {
            pic = ImageIO.read(ResourceLoader.getFileResourceAsStream("assets/" + path));
        } catch (IOException | IllegalArgumentException e) {
            e.printStackTrace();
        }
        clearImage();
        if (pic != null) {
            imgLabel.setIcon(new ImageIcon(pic));
            imgLabel.setHorizontalAlignment(SwingConstants.CENTER);
            imgLabel.setToolTipText(artist);
        }
    }

    // image unloader

    public void clearImage() {
        if (Global.global.isDebugOn(DebugFlags.DEBUG_GUI)) {
            System.out.println("Reset image");
        }
        imgLabel.setIcon(null);
    }
    public void clearPortrait() {
        portrait.setIcon(null);
    }
    public void loadPortrait(String imagepath) {
        if (imagepath != null && new File("assets/"+imagepath).canRead()) {
            BufferedImage face = null;
            try {
                face = ImageIO.read(ResourceLoader.getFileResourceAsStream("assets/" + imagepath));
            } catch (IOException | IllegalArgumentException e) {
                e.printStackTrace();
            }
            if (face != null) {
                if (Global.global.isDebugOn(DebugFlags.DEBUG_IMAGES)) {
                    System.out.println("Loading Portrait " + imagepath + " \n");
                }
                portrait.setIcon(null);

                if (width > 720) {
                    portrait.setIcon(new ImageIcon(face));
                    portrait.setVerticalAlignment(SwingConstants.TOP);
                } else {
                    Image scaledFace = face.getScaledInstance(width / 6, height / 4, Image.SCALE_SMOOTH);
                    portrait.setIcon(new ImageIcon(scaledFace));
                    portrait.setVerticalAlignment(SwingConstants.TOP);
                    System.out.println("Portrait resizing active.");
                }
            }
        }
    }

    // portrait loader
    public void loadPortrait(Combat c, Character player, Character enemy) {
        if (!Global.global.checkFlag(Flag.noportraits)) {
            if (Global.global.isDebugOn(DebugFlags.DEBUG_GUI)) {
                System.out.println("Load portraits");
            }
            String imagepath = null;
            if (!player.human()) {
                imagepath = player.getPortrait(c);
            } else if (!enemy.human()) {
                imagepath = enemy.getPortrait(c);
            }
            loadPortrait(imagepath);
        } else {
            clearPortrait();
            if (Global.global.isDebugOn(DebugFlags.DEBUG_GUI)) {
                System.out.println("No portraits");
            }
        }
    }

    public void showMap() {
        if (Global.global.isDebugOn(DebugFlags.DEBUG_GUI)) {
            System.out.println("Show map");
        }
        map.setPreferredSize(new Dimension(300, 385));
        CardLayout portraitLayout = (CardLayout) (portraitPanel.getLayout());
        portraitLayout.show(portraitPanel, USE_MAP);
    }

    public void showPortrait() {
        if (Global.global.isDebugOn(DebugFlags.DEBUG_GUI)) {
            System.out.println("Show portrait");
        }
        CardLayout portraitLayout = (CardLayout) (portraitPanel.getLayout());
        portraitLayout.show(portraitPanel, USE_PORTRAIT);
    }

    public void showNone() {
        if (Global.global.isDebugOn(DebugFlags.DEBUG_GUI)) {
            System.out.println("Show none");
        }
        CardLayout portraitLayout = (CardLayout) (portraitPanel.getLayout());
        portraitLayout.show(portraitPanel, USE_NONE);
    }

    public void populatePlayer(Player player) {
        if (Global.global.checkFlag(Flag.largefonts)) {
            fontsize = 6;
        } else {
            fontsize = 5;
        }
        getContentPane().remove(creation);
        getContentPane().add(gamePanel);
        getContentPane().validate();
        player.addObserver(this);

        // TODO: Reconcile these implementations
        // the stamina, arousal, etc. bars and labels
        topPanel.add(new ResourcesPanel(player));
        player.getStamina().fill();
        player.getArousal().empty();
        player.getMojo().empty();
        player.getWillpower().fill();

        /*
        stamina = new JLabel("Stamina: " + getLabelString(player.getStamina()));
        stamina.setFont(new Font("Sylfaen", 1, 15));
        stamina.setHorizontalAlignment(SwingConstants.CENTER);
        stamina.setForeground(new Color(164, 8, 2));
        stamina.setToolTipText(
                        "Stamina represents your endurance and ability to keep fighting. If it drops to zero, you'll be temporarily stunned.");
        meter.add(stamina);

        arousal = new JLabel("Arousal: " + getLabelString(player.getArousal()));
        arousal.setFont(new Font("Sylfaen", 1, 15));
        arousal.setHorizontalAlignment(SwingConstants.CENTER);
        arousal.setForeground(new Color(254, 1, 107));
        arousal.setToolTipText(
                        "Arousal is raised when your opponent pleasures or seduces you. If it hits your max, you'll orgasm and lose the fight.");
        meter.add(arousal);

        mojo = new JLabel("Mojo: " + getLabelString(player.getMojo()));
        mojo.setFont(new Font("Sylfaen", 1, 15));
        mojo.setHorizontalAlignment(SwingConstants.CENTER);
        mojo.setForeground(new Color(51, 153, 255));
        mojo.setToolTipText(
                        "Mojo is the abstract representation of your momentum and style. It increases with normal techniques and is used to power special moves");
        meter.add(mojo);

        willpower = new JLabel("Willpower: " + getLabelString(player.getWillpower()));
        willpower.setFont(new Font("Sylfaen", 1, 15));
        willpower.setHorizontalAlignment(SwingConstants.CENTER);
        willpower.setForeground(new Color(68, 170, 85));
        willpower.setToolTipText("Willpower is a representation of your will to fight. When this reaches 0, you lose.");
        meter.add(willpower);
        try {
            // on macs, the aqua look and feel does not have colored progress bars.
            UIManager.setLookAndFeel(UIManager.getCrossPlatformLookAndFeelClassName());
        } catch (ClassNotFoundException | InstantiationException | IllegalAccessException
                        | UnsupportedLookAndFeelException e) {
            e.printStackTrace();
        }
        staminaBar = new JProgressBar();
        staminaBar.setBorder(new SoftBevelBorder(1, null, null, null, null));
        staminaBar.setForeground(new Color(164, 8, 2));
        staminaBar.setBackground(new Color(50, 50, 50));
        meter.add(staminaBar);
        staminaBar.setMaximum(player.getStamina().max());
        staminaBar.setValue(player.getStamina().get());

        arousalBar = new JProgressBar();
        arousalBar.setBorder(new SoftBevelBorder(1, null, null, null, null));
        arousalBar.setForeground(new Color(254, 1, 107));
        arousalBar.setBackground(new Color(50, 50, 50));
        meter.add(arousalBar);
        arousalBar.setMaximum(player.getArousal().max());
        arousalBar.setValue(player.getArousal().get());

        mojoBar = new JProgressBar();
        mojoBar.setBorder(new SoftBevelBorder(1, null, null, null, null));
        mojoBar.setForeground(new Color(51, 153, 255));
        mojoBar.setBackground(new Color(50, 50, 50));
        meter.add(mojoBar);
        mojoBar.setMaximum(player.getMojo().max());
        mojoBar.setValue(player.getMojo().get());

        willpowerBar = new JProgressBar();
        willpowerBar.setBorder(new SoftBevelBorder(1, null, null, null, null));
        willpowerBar.setForeground(new Color(68, 170, 85));
        willpowerBar.setBackground(new Color(50, 50, 50));
        meter.add(willpowerBar);
        willpowerBar.setMaximum(player.getWillpower().max());
        willpowerBar.setValue(player.getWillpower().get());
        try {
            UIManager.setLookAndFeel(UIManager.getCrossPlatformLookAndFeelClassName());
        } catch (ClassNotFoundException | InstantiationException | IllegalAccessException
                        | UnsupportedLookAndFeelException e) {
            e.printStackTrace();
        }
        */
        JPanel bio = new JPanel();
        topPanel.add(bio);
        bio.setLayout(new GridLayout(2, 0, 0, 0));
        bio.setBackground(GUIColors.bgDark.color);

        JLabel name = new JLabel(player.name());
        name.setHorizontalAlignment(SwingConstants.LEFT);
        name.setFont(new Font("Sylfaen", 1, 15));
        name.setForeground(GUIColors.textColorLight.color);
        bio.add(name);
        lvl = new JLabel("Lvl: " + player.getLevel());
        lvl.setFont(new Font("Sylfaen", 1, 15));
        lvl.setForeground(GUIColors.textColorLight.color);

        bio.add(lvl);
        xp = new JLabel("XP: " + player.getXP());
        xp.setFont(new Font("Sylfaen", 1, 15));
        xp.setForeground(GUIColors.textColorLight.color);
        bio.add(xp);

        UIManager.put("ToggleButton.select", new Color(75, 88, 102));
        stsbtn = new JToggleButton("Status");
        stsbtn.addActionListener(arg0 -> {
            if (stsbtn.isSelected()) {
                mainPanel.add(statusPanel, BorderLayout.EAST);
            } else {
                mainPanel.remove(statusPanel);
            }
            GUI.this.refresh();
            mainPanel.validate();
        });
        bio.add(stsbtn);

    	inventoryFrame = new JFrame("Inventory");
        inventoryFrame.setDefaultCloseOperation(JFrame.HIDE_ON_CLOSE);
        inventoryFrame.setVisible(false);
        inventoryFrame.setLocationByPlatform(true);
        inventoryFrame.setResizable(true);
        inventoryFrame.setMinimumSize(new Dimension(800, 100));

        JButton inventoryButton = new JButton("Inventory");
        inventoryButton.addActionListener(arg0 -> {
            toggleInventory();
        });
        loclbl = new JLabel();
        loclbl.setFont(new Font("Sylfaen", 1, 16));
        loclbl.setForeground(GUIColors.textColorLight.color);

        //stsbtn.setBackground(new Color(85, 98, 112));
        //stsbtn.setForeground(GUIColors.textColorLight.color);
        bio.add(loclbl);

        timeLabel = new JLabel();
        timeLabel.setFont(new Font("Sylfaen", 1, 16));
        timeLabel.setForeground(GUIColors.textColorLight.color);
        bio.add(timeLabel);
        cashLabel = new JLabel();
        cashLabel.setFont(new Font("Sylfaen", 1, 16));
        cashLabel.setForeground(new Color(33, 180, 42));
        bio.add(cashLabel);
        bio.add(inventoryButton);
        removeClosetGUI();
        topPanel.validate();
        showNone();
    }

    public void createCharacter() {
        getContentPane().remove(gamePanel);
        creation = new CreationGUI();
        getContentPane().add(creation);
        getContentPane().validate();
    }

    public void purgePlayer() {
        getContentPane().remove(gamePanel);
        clearText();
        gamePanel.remove(commandPanel);
        showNone();
        clearImage();
        mntmQuitMatch.setEnabled(false);
        combat = null;
        topPanel.removeAll();
    }

    public void clearText() {
        textPane.setText("");
    }

    protected void clearTextIfNeeded() {
        textPane.getCaretPosition();
        textPane.setCaretPosition(textPane.getDocument().getLength());
        textPane.selectAll();
        int x = textPane.getSelectionEnd();
        textPane.select(x, x);
    }

    public void message(String text) {
        message(null, null, text);
    }

    public void message(Combat c, Character character, String text) {
        if (c != null) {
            if (character != null) {
                c.write(character, text);
            } else {
                c.write(text);
            }
        }
        if (text.trim().length() == 0) {
            return;
        }

        HTMLDocument doc = (HTMLDocument) textPane.getDocument();
        HTMLEditorKit editorKit = (HTMLEditorKit) textPane.getEditorKit();
        try {
            editorKit.insertHTML(doc, doc.getLength(),
                            "<font face='Georgia'><font color='white'><font size='" + fontsize + "'>" + text + "<br/>",
                            0, 0, null);
        } catch (BadLocationException | IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }

    public void combatMessage(String text) {

        HTMLDocument doc = (HTMLDocument) textPane.getDocument();
        HTMLEditorKit editorKit = (HTMLEditorKit) textPane.getEditorKit();
        try {
            editorKit.insertHTML(doc, doc.getLength(),
                            "<font face='Georgia'><font color='white'><font size='" + fontsize + "'>" + text + "<br/>",
                            0, 0, null);
        } catch (BadLocationException | IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }

    public void setChoices(List<? extends GameButton> buttons) {
        commandPanel.setButtons(buttons);
        commandPanel.showButtons(0);
    }

    public void next() throws InterruptedException {
        ArrayList<FutureButton<Void>> buttons = new ArrayList<>();
        buttons.add(new ContinueButton.NextButton());
        setChoices(new ArrayList<>(buttons));
        Prompt<Void> prompt = new Prompt<>(buttons);
        prompt.response().orElseThrow(() -> new RuntimeException(
                        "It should not be possible to cancel a \"next\" prompt."));
    }

    // Night match initializer

    public void startMatch() {
        mntmQuitMatch.setEnabled(true);
        showMap();
    }

    public void endNight() {
        if (Global.global.isDebugOn(DebugFlags.DEBUG_GUI)) {
            System.out.println("Night End");
        }
        showNone();
        mntmQuitMatch.setEnabled(false);
        List<FutureButton<Void>> buttons = new ArrayList<>();
        buttons.add(new ContinueButton.SleepButton());
        List<GameButton> allButtons = new ArrayList<>(buttons);
        allButtons.add(new SaveButton());
        setChoices(allButtons);
    }

    public void refresh() {
        Player player = Global.global.human;
        lvl.setText("Lvl: " + player.getLevel());
        xp.setText("XP: " + player.getXP());
        loclbl.setText(player.location().name);
        cashLabel.setText("$" + player.money);
        if (map != null) {
            map.repaint();
        }
        timeLabel.setText(Global.global.getClock().clockString());
        if (Global.global.getTime() == Time.NIGHT) {
            timeLabel.setForeground(GUIColors.clockLabelNight.color);
        } else if (Global.global.getTime() == Time.DAY) {
            timeLabel.setForeground(GUIColors.clockLabelDay.color);
        } else {
            throw new RuntimeException("Unknown time of day: " + Global.global.getTime());
        }
        displayStatus();
        List<Item> availItems = player.getInventory().entrySet().stream().filter(entry -> (entry.getValue() > 0))
                .map(Map.Entry::getKey).collect(Collectors.toList());

	    JPanel inventoryPane = new JPanel();
	    inventoryPane.setLayout(new GridLayout(0, 5));
	    inventoryPane.setSize(new Dimension(400, 800));
	    inventoryPane.setBackground(GUIColors.bgDark.color);

	    Map<Item, Integer> items = player.getInventory();
	    int count = 0;

	    for (Item i : availItems) {
	        JLabel label = new JLabel(i.getName() + ": " + items.get(i) + "\n");
	        label.setForeground(GUIColors.textColorLight.color);
	        label.setToolTipText(i.getDesc());
	        inventoryPane.add(label);
	        count++;
	    }
	    inventoryFrame.getContentPane().removeAll();
        inventoryFrame.getContentPane().add(BorderLayout.CENTER, inventoryPane);
        inventoryFrame.pack();
    }

    private void toggleInventory() {
    	EventQueue.invokeLater(() -> {
    		if (!inventoryFrame.isVisible()) {
	    		refresh();
		        inventoryFrame.setVisible(true);
    		} else {
    			inventoryFrame.setVisible(false);
    		}
    	});
    }

    public void displayStatus() {
        statusPanel.removeAll();
        statusPanel.repaint();
        //statusPanel.setPreferredSize(new Dimension(400, mainPanel.getHeight()));
        statusPanel.setPreferredSize(new Dimension(width/4, mainPanel.getHeight()));

        if (width < 720) {
            statusPanel.setMaximumSize(new Dimension(height, width / 6));
            System.out.println("STATUS PANEL");
        }
        JPanel statsPanel = new JPanel(new GridLayout(0, 3));

        Player player = Global.global.human;

        statusPanel.add(statsPanel);
        //statsPanel.setPreferredSize(new Dimension(400, 200));
        statsPanel.setPreferredSize(new Dimension(width/4, 200));

        JSeparator sep = new JSeparator();
        sep.setMaximumSize(new Dimension(statusPanel.getWidth(), 2));
        statusPanel.add(sep);
        int count = 0;
        statsPanel.setBackground(GUIColors.bgLight.color);
        ArrayList<JLabel> attlbls = new ArrayList<>();
        for (Attribute a : Attribute.values()) {
            int amt = player.get(a);
            if (amt > 0) {
                JLabel dirtyTrick = new JLabel(a.name() + ": " + amt);

                dirtyTrick.setForeground(GUIColors.textColorLight.color);

                attlbls.add(count, dirtyTrick);

                statsPanel.add(attlbls.get(count));
                count++;
            }
        }

        // statusText - body, clothing and status description
        JTextPane statusText = new JTextPane();
        DefaultCaret caret = (DefaultCaret) statusText.getCaret();
        caret.setUpdatePolicy(DefaultCaret.NEVER_UPDATE);
        statusText.setBackground(GUIColors.bgLight.color);
        statusText.setEditable(false);
        statusText.setContentType("text/html");
        HTMLDocument doc = (HTMLDocument) statusText.getDocument();
        HTMLEditorKit editorKit = (HTMLEditorKit) statusText.getEditorKit();
        try {
            editorKit.insertHTML(doc, doc.getLength(),
                            "<font face='Georgia'><font color='white'><font size='3'>"
                                            + player.getOutfit().describe(player) + "<br/>" + player.describeStatus()
                                            + "<br/>",
                            0, 0, null);
        } catch (BadLocationException | IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        JScrollPane scrollPane = new JScrollPane(statusText);
        scrollPane.setBackground(GUIColors.bgLight.color);
        scrollPane.setOpaque(false);
        scrollPane.getViewport().setOpaque(false);
        JPanel currentStatusPanel = new JPanel(new GridLayout());
//        statusPanel.setPreferredSize(new Dimension(400, height));
//        currentStatusPanel.setMaximumSize(new Dimension(400, 2000));
//        currentStatusPanel.setPreferredSize(new Dimension(400, 2000));
        statusPanel.setPreferredSize(new Dimension(width/4, height));
        currentStatusPanel.setMaximumSize(new Dimension(width/4, 2000));
        currentStatusPanel.setPreferredSize(new Dimension(width/4, 2000));

        currentStatusPanel.setBackground(GUIColors.bgLight.color);
        statusPanel.add(currentStatusPanel);
        currentStatusPanel.add(scrollPane);
        statusPanel.setBackground(GUIColors.bgLight.color);
        if (width < 720) {
            currentStatusPanel.setSize(new Dimension(height, width / 6));
            System.out.println("Oh god so tiny");
        }
        mainPanel.revalidate();
        statusPanel.revalidate();
        statusPanel.repaint();
    }

    @Override
    public void update(Observable arg0, Object arg1) {
        refresh();
    }

    private void loadWithDialog() {
        JFileChooser dialog = new JFileChooser("./");
        FileFilter savesFilter = new FileNameExtensionFilter("Nightgame Saves", "ngs");
        dialog.addChoosableFileFilter(savesFilter);
        dialog.setFileFilter(savesFilter);
        dialog.setMultiSelectionEnabled(false);
        int rv = dialog.showOpenDialog(this);
        if (rv != JFileChooser.APPROVE_OPTION) {
            return;
        }
        File file = dialog.getSelectedFile();
        if (!file.isFile()) {
            file = new File(dialog.getSelectedFile().getAbsolutePath() + ".ngs");
            if (!file.isFile()) {
                // not a valid save, abort
                JOptionPane.showMessageDialog(this, "Nightgames save file not found", "File not found",
                                JOptionPane.ERROR_MESSAGE);
                return;
            }
        }
        if (Global.global != null) {
            Global.global.exit();
        }
        Global global = new Global();
        global.load(file);
        new GameThread(global).start();
    }


    public void changeClothes(Character player, Activity event, String backOption) {
        clothesPanel.removeAll();
        clothesPanel.add(new ClothesChangeGUI(player, event, backOption));
        CardLayout layout = (CardLayout) centerPanel.getLayout();
        layout.show(centerPanel, USE_CLOSET_UI);
    }

    public void removeClosetGUI() {
        if (Global.global.isDebugOn(DebugFlags.DEBUG_GUI)) {
            System.out.println("remove closet gui");
        }
        clothesPanel.removeAll();
        CardLayout layout = (CardLayout) centerPanel.getLayout();
        layout.show(centerPanel, USE_MAIN_TEXT_UI);
    }

    public void systemMessage(String string) {
        if (Global.global.checkFlag(Flag.systemMessages)) {
            message(string);
        }
    }

    private class NewGameMenuItem extends JMenuItem {
        private static final long serialVersionUID = 8037073855752178280L;

        NewGameMenuItem() {
            super("New Game");
            this.setForeground(Color.WHITE);
            this.setBackground(GUIColors.bgGrey.color);
            this.setHorizontalAlignment(SwingConstants.CENTER);

            this.addActionListener(arg0 -> {
                if (Global.global.inGame()) {
                    int result = JOptionPane.showConfirmDialog(GUI.this,
                                    "Do you want to start a new game? You'll lose any unsaved progress.",
                                    "Start new game?", JOptionPane.OK_CANCEL_OPTION, JOptionPane.INFORMATION_MESSAGE);
                    if (result == JOptionPane.OK_OPTION) {
                        Global.global.exit();
                        new Global();
                        createCharacter();
                    }
                }
            });
        }
    }

    // TODO: move to CommandPanel or more suitable place
    public int nSkillsForGroup(TacticGroup group) {
        return skills.get(group).size();
    }

    public void switchTactics(TacticGroup group) {
        groupBox.removeAll();
        currentTactics = group;
        Global.global.gui().showSkills();
    }
}
