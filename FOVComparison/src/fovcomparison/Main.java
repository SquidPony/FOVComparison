package fovcomparison;

import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import javax.swing.JFrame;
import squidpony.squidgrid.gui.SquidPanel;

/**
 * @author Eben Howard - http://squidpony.com - howard@squidpony.com
 * @author Tommy Ettinger
 */
public class Main {

    private JFrame frame;
    private SquidPanel shadowPanel, miniShadowPanel, rayPanel, bresenhamPanel, fullLightPanel;
    private SquidPanel[] panels;
    private Controls controls;
    
// ------- Variables from Controls with explanations ---------
    // This section is for making map types
//    public JButton boulderButton; <-- a mostly open field with some boulders, should show off various distance single-pillar behavior
//    public JButton caveButton; <-- a larger open-ish cave complex with passages between open areas a few tiles wide (low priority)
//    public JButton classicButton; <-- old Rogue style
//    public JButton smallRoomButton; <-- a room that's 3x3 open and the rest all walls... to test best case speed (low priority)
//    public JButton modernButton; <-- a more modern dungeon with various features, I'm thinking of using my testing map from the FOV demo example
//    public JButton openButton; <-- a completely empty map... to test "worst" case speed (low priority)
    
    
//    public JSlider delaySlider; <-- how much delay in milliseconds to add to each FOV pass, to simulate user thinking input delays (not needed until motion added)
//    public JButton resetButton; <-- clears the display map of FOV (maybe shows the actual map fully lit?)
//    public JButton startButton; <-- starts a run of the FOV (at first with just one pass, we can add movement later)

    public static void main(String[] args) {
        new Main().go();
    }

    /**
     * The starting point for a run of this class.
     */
    private void go() {
        frame = new JFrame("FOV Comparison");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setLayout(new GridBagLayout());

        GridBagConstraints constraints = new GridBagConstraints();

        controls = new Controls();
        constraints.gridy = 0;
        frame.add(controls, constraints);
        // TODO - hook each button into generating (or using pregenerated) examples of the given dungeon type
        
        
        // TODO - build each panel to have the same size, font, etc.

        panels = new SquidPanel[]{shadowPanel, miniShadowPanel, rayPanel, bresenhamPanel, fullLightPanel};
        constraints.gridy = 1;
        for (SquidPanel sp : panels) {
            frame.add(sp, constraints);
        }
        
        // TODO - give each panel its own thread to run FOV on
        
        // TODO - give each panel its own FOV to run
        
        // TODO - hook start button up to fire all the panels' FOV

        frame.setVisible(true);
    }

}
