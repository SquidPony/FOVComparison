package squidpony.fovcomparison;

import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import javax.swing.JFrame;
import javax.swing.JLayeredPane;
import squidpony.SColor;
import squidpony.examples.ClassicRogueMapGeneratorTest;
import squidpony.squidgrid.Direction;
import squidpony.squidgrid.gui.SquidPanel;
import squidpony.squidgrid.gui.TextCellFactory;
import squidpony.squidgrid.mapping.ClassicRogueMapGenerator;
import squidpony.squidgrid.mapping.Terrain;

/**
 * Contains all the bits need to show off FOV comparisons.
 *
 * @author Eben Howard - http://squidpony.com - howard@squidpony.com
 */
public class ExamplePanel {
    
    private SquidPanel back, front;
    private int width, height;

    public ExamplePanel(int width, int height, TextCellFactory tcf, JLayeredPane layer) {
        this.width = width;
        this.height = height;
        back = new SquidPanel(width, height, tcf, null);
        front = new SquidPanel(width, height, tcf, null);
        
        layer.setLayer(back, JLayeredPane.DEFAULT_LAYER);
        layer.setLayer(front, JLayeredPane.PALETTE_LAYER);
        layer.add(back);
        layer.add(front);
        layer.setPreferredSize(back.getPreferredSize());
        layer.setSize(back.getPreferredSize());

        paint();
    }

    private void paint() {
        back.erase();
        front.erase();
        
        // lighting logic goes here

        back.refresh();
        front.refresh();
    }
}
