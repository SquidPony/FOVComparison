package squidpony.fovcomparison;

import java.awt.Font;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.GridLayout;
import java.awt.Point;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import javax.swing.JFrame;
import javax.swing.JLayeredPane;
import javax.swing.JPanel;
import squidpony.SColor;
import squidpony.examples.TestMap;
import squidpony.squidgrid.Direction;
import squidpony.squidgrid.FOV;
import squidpony.squidgrid.LOS;
import squidpony.squidgrid.gui.TextCellFactory;
import squidpony.squidgrid.mapping.ClassicRogueMapGenerator;
import squidpony.squidgrid.mapping.Terrain;
import squidpony.squidgrid.mapping.styled.DungeonBoneGen;
import squidpony.squidgrid.mapping.styled.TilesetType;
import squidpony.squidmath.RNG;

/**
 * @author Eben Howard - http://squidpony.com - howard@squidpony.com
 * @author Tommy Ettinger
 */
public class Main {

    private static final int gridWidth = 41, gridHeight = 41, cellWidth = 8, cellHeight = 8;

    private RNG rng = new RNG();

    private JFrame frame;
    private ExamplePanel shadowPanel, ripplePanel, tightPanel, bresenhamPanel, loosePanel, eliasPanel;
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
        for (int x = 0; x < gridWidth; x++) {
            for (int y = 0; y < gridHeight; y++) {
                map[x][y] = true;
            }
        }

        frame = new JFrame("FOV Comparison");
        frame.getContentPane().setBackground(SColor.BLACK);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setLayout(new GridBagLayout());

        controls = new Controls();
        constraints.gridy = 0;
        frame.add(controls, constraints);

        setupControls();

        setupPanels();
        updateMap();

        frame.pack();
        frame.setLocationRelativeTo(null);
        frame.setVisible(true);

        frame.addKeyListener(keys);
    }

    /**
     * Updates the FOV maps in each panel.
     */
    private void updateMap() {

        // Make sure center is clear
        map[at.x][at.y] = true;

        for (ExamplePanel panel : panels) {
            panel.updateMap(map);
            panel.refresh();
        }

        frame.requestFocus();
    }

    /**
     * Updates the FOV using the current @ position.
     */
    private void updateFOV() {
        for (ExamplePanel panel : panels) {
            panel.updateFOV(at.x, at.y);
            panel.refresh();
        }

        frame.requestFocus();
    }

    /**
     * Attaches the control panel controls.
     */
    private void setupControls() {
        controls.boulderButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                for (int x = 0; x < gridWidth; x++) {
                    for (int y = 0; y < gridHeight; y++) {
                        map[x][y] = rng.nextDouble() > 0.07;
                    }
                }
                updateMap();
            }
        });

        controls.caveButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                DungeonBoneGen gen = new DungeonBoneGen();
                char[][] result = gen.generate(TilesetType.CAVES_LIMIT_CONNECTIVITY, gridWidth, gridWidth);
                for (int x = 0; x < gridWidth; x++) {
                    for (int y = 0; y < gridHeight; y++) {
                        map[x][y] = result[x][y] == '.';
                    }
                }
                updateMap();
            }
        });

        controls.classicButton.addActionListener(new ActionListener() {
            ClassicRogueMapGenerator gen = new ClassicRogueMapGenerator(3, 3, gridWidth, gridHeight, 5, 9, 5, 9);

            public void actionPerformed(ActionEvent e) {
                Terrain[][] result = gen.create();
                for (int x = 0; x < gridWidth; x++) {
                    for (int y = 0; y < gridHeight; y++) {
                        map[x][y] = !result[x][y].equals(Terrain.WALL);
                    }
                }
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
            TestMap test = new TestMap();

            public void actionPerformed(ActionEvent e) {
                for (int x = 0; x < gridWidth; x++) {
                    for (int y = 0; y < gridHeight; y++) {
                        map[x][y] = test.symbol(x + 30, y + 1) != '#';
                    }
                }
                updateMap();
            }
        });

        controls.randomButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                DungeonBoneGen gen = new DungeonBoneGen();
                char[][] result = gen.generate(rng.getRandomElement(TilesetType.values()), gridWidth, gridWidth);
                for (int x = 0; x < gridWidth; x++) {
                    for (int y = 0; y < gridHeight; y++) {
                        map[x][y] = result[x][y] == '.';
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
        TextCellFactory tcf = new TextCellFactory().fit("#.@").font(new Font("Ariel", Font.PLAIN, 14)).width(cellWidth).height(cellHeight).initBySize();

        shadowPanel = new ExamplePanel("SHADOW", gridWidth, gridHeight, tcf, new ExampleFOV() {
            final FOV fov = new FOV(FOV.SHADOW);

            public boolean[][] doFOV(boolean[][] map, int startx, int starty) {
                double[][] resist = new double[gridWidth][gridHeight];
                for (int x = 0; x < gridWidth; x++) {
                    for (int y = 0; y < gridHeight; y++) {
                        resist[x][y] = map[x][y] ? 0 : 1;
                    }
                }
                resist = fov.calculateFOV(resist, startx, starty, gridWidth / 2);
                boolean[][] result = new boolean[gridWidth][gridHeight];
                for (int x = 0; x < gridWidth; x++) {
                    for (int y = 0; y < gridHeight; y++) {
                        result[x][y] = resist[x][y] > 0;
                    }
                }
                return result;
            }
        });

        ripplePanel = new ExamplePanel("RIPPLE", gridWidth, gridHeight, tcf, new ExampleFOV() {
            final FOV fov = new FOV(FOV.RIPPLE);

            public boolean[][] doFOV(boolean[][] map, int startx, int starty) {
                double[][] resist = new double[gridWidth][gridHeight];
                for (int x = 0; x < gridWidth; x++) {
                    for (int y = 0; y < gridHeight; y++) {
                        resist[x][y] = map[x][y] ? 0 : 1;
                    }
                }
                resist = fov.calculateFOV(resist, startx, starty, gridWidth / 2);
                boolean[][] result = new boolean[gridWidth][gridHeight];
                for (int x = 0; x < gridWidth; x++) {
                    for (int y = 0; y < gridHeight; y++) {
                        result[x][y] = resist[x][y] > 0;
                    }
                }
                return result;
            }
        });

        tightPanel = new ExamplePanel("R TIGHT", gridWidth, gridHeight, tcf, new ExampleFOV() {
            final FOV fov = new FOV(FOV.RIPPLE_TIGHT);

            public boolean[][] doFOV(boolean[][] map, int startx, int starty) {
                double[][] resist = new double[gridWidth][gridHeight];
                for (int x = 0; x < gridWidth; x++) {
                    for (int y = 0; y < gridHeight; y++) {
                        resist[x][y] = map[x][y] ? 0 : 1;
                    }
                }
                resist = fov.calculateFOV(resist, startx, starty, gridWidth / 2);
                boolean[][] result = new boolean[gridWidth][gridHeight];
                for (int x = 0; x < gridWidth; x++) {
                    for (int y = 0; y < gridHeight; y++) {
                        result[x][y] = resist[x][y] > 0;
                    }
                }
                return result;
            }
        });

        bresenhamPanel = new ExamplePanel("BRES", gridWidth, gridHeight, tcf, new ExampleFOV() {
            final LOS los = new LOS(LOS.BRESENHAM);

            public boolean[][] doFOV(boolean[][] map, int startx, int starty) {
                double[][] resist = new double[gridWidth][gridHeight];
                for (int x = 0; x < gridWidth; x++) {
                    for (int y = 0; y < gridHeight; y++) {
                        resist[x][y] = map[x][y] ? 0 : 1;
                    }
                }
                boolean[][] mapping = new boolean[gridWidth][gridHeight];
                for (int x = 0; x < gridWidth; x++) {
                    for (int y = 0; y < gridHeight; y++) {
                        mapping[x][y] = los.isReachable(resist, startx, starty, x, y);
                    }
                }
                return mapping;
            }
        });

        loosePanel = new ExamplePanel("R V LOOSE", gridWidth, gridHeight, tcf, new ExampleFOV() {
            final FOV fov = new FOV(FOV.RIPPLE_VERY_LOOSE);

            public boolean[][] doFOV(boolean[][] map, int startx, int starty) {
                double[][] resist = new double[gridWidth][gridHeight];
                for (int x = 0; x < gridWidth; x++) {
                    for (int y = 0; y < gridHeight; y++) {
                        resist[x][y] = map[x][y] ? 0 : 1;
                    }
                }
                resist = fov.calculateFOV(resist, startx, starty, gridWidth / 2);
                boolean[][] result = new boolean[gridWidth][gridHeight];
                for (int x = 0; x < gridWidth; x++) {
                    for (int y = 0; y < gridHeight; y++) {
                        result[x][y] = resist[x][y] > 0;
                    }
                }
                return result;
            }
        });

        eliasPanel = new ExamplePanel("ELIAS", gridWidth, gridHeight, tcf, new ExampleFOV() {
            final LOS los = new LOS(LOS.ELIAS);

            public boolean[][] doFOV(boolean[][] map, int startx, int starty) {
                double[][] resist = new double[gridWidth][gridHeight];
                for (int x = 0; x < gridWidth; x++) {
                    for (int y = 0; y < gridHeight; y++) {
                        resist[x][y] = map[x][y] ? 0 : 1;
                    }
                }
                boolean[][] mapping = new boolean[gridWidth][gridHeight];
                for (int x = 0; x < gridWidth; x++) {
                    for (int y = 0; y < gridHeight; y++) {
                        mapping[x][y] = los.isReachable(resist, startx, starty, x, y);
                    }
                }
                return mapping;
            }
        });

        panels = new ExamplePanel[]{shadowPanel, bresenhamPanel, eliasPanel, tightPanel, ripplePanel, loosePanel};
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

    private KeyAdapter keys = new KeyAdapter() {

        @Override
        public void keyPressed(KeyEvent ke) {
            Direction dir = Direction.NONE;
            switch (ke.getKeyCode()) {
                case KeyEvent.VK_UP:
                    dir = Direction.UP;
                    break;
                case KeyEvent.VK_DOWN:
                    dir = Direction.DOWN;
                    break;
                case KeyEvent.VK_LEFT:
                    dir = Direction.LEFT;
                    break;
                case KeyEvent.VK_RIGHT:
                    dir = Direction.RIGHT;
                    break;
            }

            at.x += dir.deltaX;
            at.y += dir.deltaY;
            updateFOV();
        }

    };
}
