package nightgames.gui.resources;

import nightgames.characters.resources.Meter;
import nightgames.characters.resources.Resource;
import org.junit.Before;
import org.junit.Test;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.junit.Assert.assertThat;

/**
 * TODO: Write class-level documentation.
 */
public class ResourceWidgetTest {
    private Meter meter;
    private ResourceWidget widget;

    @Before public void setUp() throws Exception {
        meter = new Meter(Resource.WILLPOWER, 100);
        widget = new ResourceWidget(meter);
    }

    @Test public void updateNoOverflow() throws Exception {
        meter.set(50);
        assertThat(widget.getText(), equalTo("Willpower: 50/100"));
    }

    @Test public void updateOverflow() throws Exception {
        meter.set(150);
        assertThat(widget.getText(), equalTo("Willpower: (150)/100"));
    }
}
