package squidpony.fovcomparison;

import java.awt.Font;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.GridLayout;
import java.awt.Point;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import javax.swing.JFrame;
import javax.swing.JLayeredPane;
import javax.swing.JPanel;
import squidpony.SColor;
import squidpony.squidgrid.FOV;
import squidpony.squidgrid.gui.TextCellFactory;
import squidpony.squidmath.RNG;

/**
 * @author Eben Howard - http://squidpony.com - howard@squidpony.com
 * @author Tommy Ettinger
 */
public class Main {

    private static final int gridWidth = 41, gridHeight = 41;

    private RNG rng = new RNG();

    private JFrame frame;
    private ExamplePanel shadowPanel, miniShadowPanel, rayPanel, bresenhamPanel, fullLightPanel;
    private ExamplePanel[] panels;
    private Controls controls;

    private boolean[][] map = new boolean[gridWidth][gridHeight]; // true means open floor, false means wall

    private Point at; // the location for the @
    private GridBagConstraints constraints = new GridBagConstraints();

    public static void main(String[] args) {
        new Main().go();
    }

    /**
     * The starting point for a run of this class.
     */
    private void go() {
        at = new Point(gridWidth / 2, gridHeight / 2);
        map[at.x][at.y] = true;//make sure the center is clear

        frame = new JFrame("FOV Comparison");
        frame.getContentPane().setBackground(SColor.BLACK);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setLayout(new GridBagLayout());

        controls = new Controls();
        constraints.gridy = 0;
        frame.add(controls, constraints);

        setupControls();

        setupPanels();

        // TODO - give each panel its own FOV to run
        frame.pack();
        frame.setLocationRelativeTo(null);
        frame.setVisible(true);
    }

    /**
     * Updates the FOV maps in each panel.
     */
    private void updateMap() {
        for (ExamplePanel panel : panels) {
            panel.updateMap(map);
            panel.refresh();
        }
    }

    /**
     * Updates the FOV using the current @ position.
     */
    private void updateFOV() {
        for (ExamplePanel panel : panels) {
            panel.updateFOV(at.x, at.y);
            panel.refresh();
        }
    }

    /**
     * Attaches the control panel controls.
     */
    private void setupControls() {
//    public JButton startButton; <-- starts a run of the FOV (at first with just one pass, we can add movement later)
        controls.boulderButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                // TODO make map
                updateMap();
            }
        });

        controls.caveButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                // TODO make map
                updateMap();
            }
        });

        controls.classicButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                // TODO make map
                updateMap();
            }
        });

        controls.smallRoomButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                int midX = gridWidth / 2;
                int midY = gridHeight / 2;
                for (int x = 0; x < gridWidth; x++) {
                    for (int y = 0; y < gridHeight; y++) {
                        map[x][y] = x > midX - 3 && x < midX + 3 && y > midY - 3 && y < midY + 3;
                    }
                }
                updateMap();
            }
        });

        controls.modernButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                // TODO make map
            }
        });

        controls.openButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                for (int x = 0; x < gridWidth; x++) {
                    for (int y = 0; y < gridHeight; y++) {
                        map[x][y] = true;
                    }
                }
                updateMap();
            }
        });

        controls.startButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                updateFOV();
            }
        });
    }

    private void setupPanels() {
        TextCellFactory tcf = new TextCellFactory().fit("#.@").font(new Font("Ariel", Font.PLAIN, 14)).width(6).height(6);

        shadowPanel = new ExamplePanel(gridWidth, gridHeight, tcf, new ExampleFOV() {
            final FOV fov = new FOV(FOV.SHADOW);

            public boolean[][] doFOV(boolean[][] map, int startx, int starty) {
                double[][] resist = new double[gridWidth][gridHeight];
                for (int x = 0; x < gridWidth; x++) {
                    for (int y = 0; y < gridHeight; y++) {
                        resist[x][y] = map[x][y] ? 0 : 1;
                    }
                }
                resist = fov.calculateFOV(resist, startx, starty);
                boolean[][] result = new boolean[gridWidth][gridHeight];
                for (int x = 0; x < gridWidth; x++) {
                    for (int y = 0; y < gridHeight; y++) {
                        result[x][y] = resist[x][y] > 0;
                    }
                }
                return result;
            }
        });

        miniShadowPanel = new ExamplePanel(gridWidth, gridHeight, tcf, new ExampleFOV() {
            public boolean[][] doFOV(boolean[][] map, int startx, int starty) {
                boolean[][] mapping = new boolean[gridWidth][gridHeight];
                for (int x = 0; x < gridWidth; x++) {
                    for (int y = 0; y < gridHeight; y++) {
                        mapping[x][y] = true;
                    }
                }
                return mapping;
            }
        });

        rayPanel = new ExamplePanel(gridWidth, gridHeight, tcf, new ExampleFOV() {
            public boolean[][] doFOV(boolean[][] map, int startx, int starty) {
                boolean[][] mapping = new boolean[gridWidth][gridHeight];
                for (int x = 0; x < gridWidth; x++) {
                    for (int y = 0; y < gridHeight; y++) {
                        mapping[x][y] = true;
                    }
                }
                return mapping;
            }
        });

        bresenhamPanel = new ExamplePanel(gridWidth, gridHeight, tcf, new ExampleFOV() {
            public boolean[][] doFOV(boolean[][] map, int startx, int starty) {
                boolean[][] mapping = new boolean[gridWidth][gridHeight];
                for (int x = 0; x < gridWidth; x++) {
                    for (int y = 0; y < gridHeight; y++) {
                        mapping[x][y] = true;
                    }
                }
                return mapping;
            }
        });

        fullLightPanel = new ExamplePanel(gridWidth, gridHeight, tcf, new ExampleFOV() {
            public boolean[][] doFOV(boolean[][] map, int startx, int starty) {
                boolean[][] mapping = new boolean[gridWidth][gridHeight];
                for (int x = 0; x < gridWidth; x++) {
                    for (int y = 0; y < gridHeight; y++) {
                        mapping[x][y] = true;
                    }
                }
                return mapping;
            }
        });

        panels = new ExamplePanel[]{shadowPanel, miniShadowPanel, rayPanel, bresenhamPanel, fullLightPanel};
        JPanel panel = new JPanel();
        panel.setBackground(SColor.BLACK);
        panel.setLayout(new GridLayout(0, 3, 5, 5));
        constraints.gridy = 1;
        frame.add(panel, constraints);

        for (ExamplePanel ep : panels) {
            JLayeredPane jlp = new JLayeredPane();
            ep.attach(jlp);
            panel.add(jlp);
        }
    }

}
