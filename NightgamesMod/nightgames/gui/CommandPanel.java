package nightgames.gui;

<<<<<<< HEAD
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;

import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JPanel;
import javax.swing.border.CompoundBorder;

public class CommandPanel {
    private static final List<Character> POSSIBLE_HOTKEYS = Arrays.asList(
                    'q', 'w', 'e', 'r', 't', 'y',
                    'a', 's', 'd', 'f' , 'g', 'h',
                    'z', 'x', 'c', 'v', 'b', 'n'); 
    private static final Set<String> DEFAULT_CHOICES = new HashSet<>(Arrays.asList("Wait", "Nothing", "Next", "Leave", "Back"));
    private static final int ROW_LIMIT = 6;

    private JPanel panel;
    private int index;
    private int page;
    private Map<Character, KeyableButton> hotkeyMapping;
    private List<KeyableButton> buttons;
    private JPanel rows[];
    public CommandPanel(int width) {
        panel = new JPanel();
        panel.setBackground(GUIColors.bgDark);
        panel.setPreferredSize(new Dimension(width, 160));
        panel.setMinimumSize(new Dimension(width, 160));
        panel.setBorder(new CompoundBorder());
        hotkeyMapping = new HashMap<>();
        rows = new JPanel[POSSIBLE_HOTKEYS.size() / ROW_LIMIT];
        rows[0] = new JPanel();
        rows[1] = new JPanel();
        rows[2] = new JPanel();
        for (JPanel row : rows) {
            FlowLayout layout;
            layout = new FlowLayout();
            layout.setVgap(0);
            layout.setHgap(4);
            row.setLayout(layout);
            row.setOpaque(false);
            row.setBorder(BorderFactory.createEmptyBorder());
            row.setPreferredSize(new Dimension(0, 20));
            panel.add(row);
        }
        BoxLayout layout = new BoxLayout(panel, BoxLayout.Y_AXIS);
        panel.setLayout(layout);
        panel.add(Box.createVerticalGlue());
        panel.add(Box.createVerticalStrut(2));
        buttons = new ArrayList<>();
        index = 0;
    }

    public JPanel getPanel() {
        return panel;
    }

    public void reset() {
        buttons.clear();
        hotkeyMapping.clear();
        clear();
        refresh();
    }

    private void clear() {
        for (JPanel row : rows) {
            row.removeAll();
        }
        POSSIBLE_HOTKEYS.forEach(hotkeyMapping::remove);
        index = 0;  
    }

    public void refresh() {
        panel.repaint();
        panel.revalidate();
    }

    public void add(KeyableButton button) {
        page = 0;
        buttons.add(button);
        use(button);
    }

    private void use(KeyableButton button) {
        int effectiveIndex = index - page * POSSIBLE_HOTKEYS.size();
        int currentPage = page;
        if (effectiveIndex >= 0 && effectiveIndex < POSSIBLE_HOTKEYS.size()) {
            int rowIndex = Math.min(rows.length - 1, effectiveIndex / ROW_LIMIT);
            JPanel row = rows[rowIndex];
            row.add(button);
            Character hotkey = POSSIBLE_HOTKEYS.get(effectiveIndex);
            register(hotkey, button);
            if (DEFAULT_CHOICES.contains(button.getText()) && !hotkeyMapping.containsKey(' ')) {
                hotkeyMapping.put(' ', button); 
            }
        } else if (effectiveIndex == -1) {
            KeyableButton leftPage = new RunnableButton("<<<", () -> setPage(currentPage - 1));
            rows[0].add(leftPage, 0);
            register('~', leftPage);
        } else if (effectiveIndex == POSSIBLE_HOTKEYS.size()){
            KeyableButton rightPage = new RunnableButton(">>>", () -> setPage(currentPage + 1));
            rows[0].add(rightPage);
            register('`', rightPage);
        }
        index += 1;
    }

    public void setPage(int page) {
        this.page = page;
        clear();
        buttons.forEach(this::use);
        refresh();
    }

    public Optional<KeyableButton> getButtonForHotkey(char keyChar) {
        return Optional.ofNullable(hotkeyMapping.get(keyChar));
    }

    public void register(Character hotkey, KeyableButton button) {
        button.setHotkeyTextTo(hotkey.toString().toUpperCase());
        hotkeyMapping.put(hotkey, button);
=======
import nightgames.gui.button.ButtonList;
import nightgames.gui.button.FutureButton;
import nightgames.gui.button.PageButton;

import javax.swing.*;
import javax.swing.border.CompoundBorder;
import java.awt.*;
import java.io.Serializable;
import java.util.List;

public class CommandPanel extends JPanel implements Serializable {
    private static final long serialVersionUID = 3539791525124851677L;
    private ButtonList<?> buttons;
    private int page;

    CommandPanel(int height, int width) {
        this.setBackground(GUIColors.bgDark);
        this.setPreferredSize(new Dimension(width, height));
        this.setMinimumSize(new Dimension(width, height));
        this.page = 0;

        this.setBorder(new CompoundBorder());
    }

    public void setPage(int page) {
        this.page = page;
        showButtons();
    }

    public <T> void setButtons(List<FutureButton<T>> buttons) {
        this.buttons = new ButtonList<>(buttons);
        setPage(0);
    }

    public void showButtons() {
        removeAll();
        if (!buttons.isFirstPage(page)) {
            add(new PageButton("<-", page - 1, this));
        }
        buttons.page(page).forEach(this::add);
        if (!buttons.isLastPage(page)) {
            add(new PageButton("->", page + 1, this));
        }
        repaint();
        revalidate();
    }

    @Override public Component add(Component comp) {
        return super.add(comp);
    }

    @Override public void removeAll() {
        super.removeAll();
    }

    public Prompt<?> prompt() {
        return buttons.makePrompt();
>>>>>>> e1fc2a8... Redefined Prompts around the notion of CompleteableFutures set by value-holding buttons. Adjusted Button definitions to accommodate. Moved Button definitions to their own subpackage.
    }
}
