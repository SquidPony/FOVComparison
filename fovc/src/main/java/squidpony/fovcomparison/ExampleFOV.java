package squidpony.fovcomparison;

/**
 * Generates a Field of View given a boolean map of open spaces and a starting point.
 *
 * @author Eben Howard - http://squidpony.com - howard@squidpony.com
 */
public interface ExampleFOV {

    public boolean[][] doFOV(boolean[][] map, int x, int y);
}
