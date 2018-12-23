package luecx.visual.basic.framework;


import javax.swing.*;
import java.awt.*;

public class Frame extends JFrame {

    public Frame(int width, int height, String title, luecx.visual.basic.framework.Panel panel) throws HeadlessException {
        super();

        this.setTitle(title);
        this.setSize(width, height);
        this.setDefaultCloseOperation(3);
        this.setLayout(new BorderLayout());
        this.add(panel, BorderLayout.CENTER);
        this.setVisible(true);
    }

    public Frame(String title, luecx.visual.basic.framework.Panel panel) throws HeadlessException {
        this(800,600,title,panel);
    }

    public Frame(luecx.visual.basic.framework.Panel panel) throws HeadlessException {
        this(800,600,panel.getClass().getSimpleName(),panel);
    }

}
