package squidpony.fovcomparison;

import javax.swing.JLayeredPane;
import squidpony.squidgrid.gui.SquidPanel;
import squidpony.squidgrid.gui.TextCellFactory;

/**
 * Contains all the bits need to show off FOV comparisons.
 *
 * @author Eben Howard - http://squidpony.com - howard@squidpony.com
 */
public class ExamplePanel {

    private SquidPanel back, front;
    private ExampleFOV fov;
    private int width, height;
    private boolean[][] fovMap;
    private boolean[][] map;
    private int x, y;

    public ExamplePanel(int width, int height, TextCellFactory tcf, ExampleFOV fov) {
        this.width = width;
        this.height = height;
        this.fov = fov;
        back = new SquidPanel(width, height, tcf, null);
        front = new SquidPanel(width, height, tcf, null);
        fovMap = new boolean[width][height];
        map = new boolean[width][height];
        for (int x = 0; x < width; x++) {
            for (int y = 0; y < height; y++) {
                map[x][y] = true;
                fovMap[x][y] = true;
            }
        }
        x = width / 2;
        y = height / 2;
    }

    /**
     * Attaches this panel to the given pane.
     *
     * @param layer
     */
    public void attach(JLayeredPane layer) {
        layer.setLayer(back, JLayeredPane.DEFAULT_LAYER);
        layer.setLayer(front, JLayeredPane.PALETTE_LAYER);
        layer.add(back);
        layer.add(front);
        layer.setPreferredSize(back.getPreferredSize());
        layer.setSize(back.getPreferredSize());

        refresh();
    }

    /**
     * Updates the FOV for this panel using the given start position.
     *
     * @param x
     * @param y
     */
    public void updateFOV(int x, int y) {
        fovMap = fov.doFOV(map, x, y);
        this.x = x;
        this.y = y;
    }

    /**
     * Updates the map to the given one and sets the current FOV to all seeing.
     *
     * @param map
     */
    public void updateMap(boolean[][] map) {
        this.map = map;
        for (int x = 0; x < width; x++) {
            for (int y = 0; y < height; y++) {
                fovMap[x][y] = true;
            }
        }
    }

    /**
     * Refreshes the view with the previously provided map and FOV information.
     */
    public void refresh() {
        back.erase();
        front.erase();

        for (int x = 0; x < width; x++) {
            for (int y = 0; y < height; y++) {
                if (fovMap[x][y]) {
                    front.put(x, y, map[x][y] ? '.' : '#');
                } else {
                    front.clear(x, y);
                }
            }
        }
        front.put(x, y, '@');

        back.refresh();
        front.refresh();
    }
}
