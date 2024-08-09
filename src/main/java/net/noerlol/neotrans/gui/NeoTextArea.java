package net.noerlol.neotrans.gui;

import javax.swing.*;
import javax.swing.border.Border;
import java.awt.*;

public class NeoTextArea extends JTextField {
    private final Insets MARGIN = new Insets(5, 5, 5, 5);
    @Override
    public Border getBorder() {
        return null;
    }

    @Override
    public Dimension getSize() {
        return new Dimension(30, 15);
    }

    /**
     * Returns the margin between the text component's border and
     * its text.
     *
     * @return the margin
     */
    @Override
    public Insets getMargin() {
        return MARGIN;
    }

    /**
     * Sets margin space between the text component's border
     * and its text.  The text component's default <code>Border</code>
     * object will use this value to create the proper margin.
     * However, if a non-default border is set on the text component,
     * it is that <code>Border</code> object's responsibility to create the
     * appropriate margin space (else this property will effectively
     * be ignored).  This causes a redraw of the component.
     * A PropertyChange event ("margin") is sent to all listeners.
     *
     * @param ignored will be ignored
     */
    @Override
    public void setMargin(Insets ignored) {
        super.setMargin(MARGIN);
    }

    public void appendText(String text) {
        this.setText(this.getText() + text);
    }
}
