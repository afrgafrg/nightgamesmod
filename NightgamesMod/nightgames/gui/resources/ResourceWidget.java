package nightgames.gui.resources;

import nightgames.characters.resources.Meter;
import nightgames.gui.GUIColors;

import javax.swing.*;
import javax.swing.border.SoftBevelBorder;
import java.awt.*;
import java.util.Observable;
import java.util.Observer;

/**
 * Represents one of the Player's Resources with a label and bar.
 */
class ResourceWidget extends JPanel implements Observer {
    private static final long serialVersionUID = 882610467798054743L;
    private final JLabel label;
    private final JProgressBar bar;
    private final Meter meter;

    ResourceWidget(Meter meter) {
        setBackground(GUIColors.bgDark.color);
        setLayout(new GridLayout(2, 0, 0, 0));
        Color fgColor = ResourceColors.fromResource(meter.resource).color;
        this.meter = meter;
        meter.addObserver(this);
        setToolTipText(meter.resource.description);
        label = new JLabel();
        add(label);
        label.setFont(new Font("Sylfaen", 1, 15));
        label.setHorizontalAlignment(SwingConstants.CENTER);
        label.setForeground(fgColor);
        bar = new JProgressBar();
        add(bar);
        bar.setBorder(new SoftBevelBorder(1, null, null, null, null));
        bar.setForeground(fgColor);
        bar.setBackground(ResourceColors.barBackground.color);
    }

    @Override public void update(Observable o, Object arg) {
        if (!meter.equals(o)) {
            return;
        }
        label.setText(getLabelString());
        bar.setMaximum(meter.max());
        bar.setValue(meter.get());
    }

    public String getText() {
        return label.getText();
    }

    private String getLabelString() {
        String format;
        if (meter.getOverflow() > 0) {
            format = "%s: (%d)/%d";
        } else {
            format = "%s: %d/%d";
        }
        return String.format(format, meter.resource.name, meter.getReal(), meter.max());
    }
}
