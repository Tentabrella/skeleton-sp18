import pojos.Tile;
import utils.Tiles;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

/**
 * This class provides all code necessary to take a query box and produce
 * a query result. The getMapRaster method must return a Map containing all
 * seven of the required fields, otherwise the front end code will probably
 * not draw the output correctly.
 */
public class Rasterer {
    public Rasterer() {
        // YOUR CODE HERE
    }

    /**
     * Takes a user query and finds the grid of images that best matches the query. These
     * images will be combined into one big image (rastered) by the front end. <br>
     *
     *     The grid of images must obey the following properties, where image in the
     *     grid is referred to as a "tile".
     *     <ul>
     *         <li>The tiles collected must cover the most longitudinal distance per pixel
     *         (LonDPP) possible, while still covering less than or equal to the amount of
     *         longitudinal distance per pixel in the query box for the user viewport size. </li>
     *         <li>Contains all tiles that intersect the query bounding box that fulfill the
     *         above condition.</li>
     *         <li>The tiles must be arranged in-order to reconstruct the full image.</li>
     *     </ul>
     *
     * @param params Map of the HTTP GET request's query parameters - the query box and
     *               the user viewport width and height.
     *
     * @return A map of results for the front end as specified: <br>
     * "render_grid"   : String[][], the files to display. <br>
     * "raster_ul_lon" : Number, the bounding upper left longitude of the rastered image. <br>
     * "raster_ul_lat" : Number, the bounding upper left latitude of the rastered image. <br>
     * "raster_lr_lon" : Number, the bounding lower right longitude of the rastered image. <br>
     * "raster_lr_lat" : Number, the bounding lower right latitude of the rastered image. <br>
     * "depth"         : Number, the depth of the nodes of the rastered image <br>
     * "query_success" : Boolean, whether the query was able to successfully complete; don't
     *                    forget to set this to true on success! <br>
     */
    public Map<String, Object> getMapRaster(Map<String, Double> params) {
        Map<String, Object> results = new HashMap<>();
        boolean successQuery = checkValidGrids(params);
        if (!successQuery) {
            results.put("query_success", false);
            return results;
        }

        int dimension = calcDimension(params);
        Tile ulTile = findUlImage(dimension, params);
        int[] gridSize = calcGridSize(ulTile, params);
        double[] coordinates = calcCoordinates(gridSize, ulTile);
        String[][] render_grid = assembleTiles(gridSize, ulTile);
        results.put("render_grid", render_grid);
        results.put("raster_ul_lon", coordinates[0]);
        results.put("raster_ul_lat", coordinates[1]);
        results.put("raster_lr_lon", coordinates[2]);
        results.put("raster_lr_lat", coordinates[3]);
        results.put("depth", ulTile.getDimension());
        results.put("query_success", true);
        return results;
    }

    /**
     * calculate the dimension needed for the parameters
     * @param params
     * @return
     */
    private int calcDimension(Map<String, Double> params) {
        Double lrlon = params.get("lrlon");
        Double ullon = params.get("ullon");
        Double w = params.get("w");
        Double ubLonDPP = (lrlon - ullon)/w;
        Double londpp = Tiles.D0LONDPP;
        int dimension = 0;
        while (londpp > ubLonDPP && dimension < 7) {
            londpp/=2;
            dimension++;
        }
        return dimension;
    }

    /**
     * calculate the coordinates of returned map
     * @param gridSize
     * @param ulTile
     * @return
     */
    private double[] calcCoordinates(int[] gridSize, Tile ulTile) {
        double lrLat = ulTile.ulLat() + gridSize[0] * ulTile.getLatLen();
        double lrLon = ulTile.ulLon() + gridSize[1] * ulTile.getLonLen();
        return new double[] {ulTile.ulLon(), ulTile.ulLat(), lrLon, lrLat};
    }

    /**
     * check if search grid is include in pictures we have
     * @param params
     * @return
     */
    private boolean checkValidGrids(Map<String, Double> params) {
        Double ullon = params.get("ullon");
        Double lrlon = params.get("lrlon");
        Double ullat = params.get("ullat");
        Double lrlat = params.get("lrlat");
        Boolean successQuery = !(ullon > Tiles.D0LRLON || lrlon < Tiles.D0ULLON || ullat < Tiles.D0LRLAT || lrlat > Tiles.D0ULLAT);
        return successQuery;
    }

    /**
     * using number of tile and the upper left tile to
     * return the whole needed tiles as String[]
     * @param gridSize
     * @param tile
     * @return
     */
    private String[][] assembleTiles(int[] gridSize, Tile tile) {
        int n = gridSize[0];
        int m = gridSize[1];
        String[][] render_grid = new String[n][];
        Tile startTile = tile;
        for (int i = 0; i < n; i++) {
            render_grid[i] = new String[m];
            String[] currGrids = render_grid[i];
            for (int j = 0; j < m; j++) {
                currGrids[j] = new Tile(startTile.getDimension(), startTile.getX() + j, startTile.getY()).tilePath();
            }
            startTile = new Tile(startTile.getDimension(), startTile.getX(), startTile.getY() + 1);
        }
        return render_grid;
    }

    /**
     * find the first tile which its lower right corner is in search grid
     * will test the upper left corner if no lower right is in grid
     * first for longitude, second for latitude
     * @param dimension
     * @param params
     * @return
     */
    private Tile findUlImage(int dimension, Map<String, Double> params) {
        Tile currTile = new Tile(dimension, 0, 0);
        double lon = currTile.lrLon();
        double lat = currTile.lrLat();
        Double ullon = params.get("ullon");
        Double ullat = params.get("ullat");
        int x =(int) Math.ceil((ullon - lon) / currTile.getLonLen());
        int y =(int) Math.ceil((ullat - lat) / currTile.getLatLen());
        x = Math.max(x, 0);
        y = Math.max(y, 0);
        currTile = new Tile(dimension, x, y);
        return currTile;
    }

    /**
     * return the size of pictures
     * first is row, second is column
     * @param tile
     * @param params
     * @return
     */
    private int[] calcGridSize(Tile tile, Map<String, Double> params) {
        double tilelon = tile.ulLon();
        double tilelat = tile.ulLat();
        Double lrlon = params.get("lrlon");
        Double lrlat = params.get("lrlat");
        int x = (int) Math.ceil((lrlat - tilelat) / tile.getLatLen());
        int y = (int) Math.ceil((lrlon - tilelon) / tile.getLonLen());
        x = (int) Math.min(x, Math.pow(2, tile.getDimension()) - tile.getY());
        y = (int) Math.min(y, Math.pow(2, tile.getDimension()) - tile.getX());
        return new int[]{x, y};
    }

}
