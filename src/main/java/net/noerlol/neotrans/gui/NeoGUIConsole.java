package net.noerlol.neotrans.gui;

import javax.swing.*;
import java.awt.*;

public class NeoGUIConsole extends JFrame {
    public NeoTextArea console = new NeoTextArea();
    private boolean canCreateNew = true;
    public NeoGUIConsole() {
        super("NeoTranspiler | Console");
        super.setSize(new Dimension(640, 360));
        super.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
        super.setLayout(new BorderLayout());

        console.setEditable(false);
        console.setFont(new Font("Monospaced", Font.PLAIN, 12));

        super.add(console, BorderLayout.CENTER);
        super.setVisible(true);

        canCreateNew = false;
    }


    public boolean isCanCreateNew() {
        return canCreateNew;
    }

    public void setCanCreateNew(boolean canCreateNew) {
        this.canCreateNew = canCreateNew;
    }
}
