import org.junit.Before;
import org.junit.Test;
import pojos.Tile;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Collections;
import java.util.StringJoiner;
import java.util.Arrays;

import static org.junit.Assert.assertArrayEquals;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

public class TestRasterer {
    private static final double DOUBLE_THRESHOLD = 0.000000001;
    private static DecimalFormat df2 = new DecimalFormat(".#########");
    private static final String PARAMS_FILE = "raster_params.txt";
    private static final String RESULTS_FILE = "raster_results.txt";
    private static final int NUM_TESTS = 8;
    private static Rasterer rasterer;


    @Before
    public void setUp() throws Exception {
        rasterer = new Rasterer();
    }

    @Test
    public void testGetMapRaster() throws Exception {
        List<Map<String, Double>> testParams = paramsFromFile();
        List<Map<String, Object>> expectedResults = resultsFromFile();

        for (int i = 0; i < NUM_TESTS; i++) {
            System.out.println(String.format("Running test: %d", i));
            Map<String, Double> params = testParams.get(i);
            Map<String, Object> actual = rasterer.getMapRaster(params);
            Map<String, Object> expected = expectedResults.get(i);
            String msg = "Your results did not match the expected results for input "
                         + mapToString(params) + ".\n";
            checkParamsMap(msg, expected, actual);
        }
    }

    private List<Map<String, Double>> paramsFromFile() throws Exception {
        List<String> lines = Files.readAllLines(Paths.get(PARAMS_FILE), Charset.defaultCharset());
        List<Map<String, Double>> testParams = new ArrayList<>();
        int lineIdx = 2; // ignore comment lines
        for (int i = 0; i < NUM_TESTS; i++) {
            Map<String, Double> params = new HashMap<>();
            params.put("ullon", Double.parseDouble(lines.get(lineIdx)));
            params.put("ullat", Double.parseDouble(lines.get(lineIdx + 1)));
            params.put("lrlon", Double.parseDouble(lines.get(lineIdx + 2)));
            params.put("lrlat", Double.parseDouble(lines.get(lineIdx + 3)));
            params.put("w", Double.parseDouble(lines.get(lineIdx + 4)));
            params.put("h", Double.parseDouble(lines.get(lineIdx + 5)));
            testParams.add(params);
            lineIdx += 6;
        }
        return testParams;
    }

    private List<Map<String, Object>> resultsFromFile() throws Exception {
        List<String> lines = Files.readAllLines(Paths.get(RESULTS_FILE), Charset.defaultCharset());
        List<Map<String, Object>> expected = new ArrayList<>();
        int lineIdx = 4; // ignore comment lines
        for (int i = 0; i < NUM_TESTS; i++) {
            Map<String, Object> results = new HashMap<>();
            results.put("raster_ul_lon", Double.parseDouble(lines.get(lineIdx)));
            results.put("raster_ul_lat", Double.parseDouble(lines.get(lineIdx + 1)));
            results.put("raster_lr_lon", Double.parseDouble(lines.get(lineIdx + 2)));
            results.put("raster_lr_lat", Double.parseDouble(lines.get(lineIdx + 3)));
            results.put("depth", Integer.parseInt(lines.get(lineIdx + 4)));
            results.put("query_success", Boolean.parseBoolean(lines.get(lineIdx + 5)));
            lineIdx += 6;
            String[] dimensions = lines.get(lineIdx).split(" ");
            int rows = Integer.parseInt(dimensions[0]);
            int cols = Integer.parseInt(dimensions[1]);
            lineIdx += 1;
            String[][] grid = new String[rows][cols];
            for (int r = 0; r < rows; r++) {
                for (int c = 0; c < cols; c++) {
                    grid[r][c] = lines.get(lineIdx);
                    lineIdx++;
                }
            }
            results.put("render_grid", grid);
            expected.add(results);
        }
        return expected;
    }

    private void checkParamsMap(String err, Map<String, Object> expected,
                                            Map<String, Object> actual) {
        for (String key : expected.keySet()) {
            assertTrue(err + "Your results map is missing "
                       + key, actual.containsKey(key));
            Object o1 = expected.get(key);
            Object o2 = actual.get(key);

            if (o1 instanceof Double) {
                String errMsg = genDiffErrMsg(err, expected, actual);
                assertTrue(errMsg, Math.abs((Double) o1 - (Double) o2) < DOUBLE_THRESHOLD);
            } else if (o1 instanceof String[][]) {
                String errMsg = genDiffErrMsg(err, expected, actual);
                assertArrayEquals(errMsg, (String[][]) o1, (String[][]) o2);
            } else {
                String errMsg = genDiffErrMsg(err, expected, actual);
                assertEquals(errMsg, o1, o2);
            }
        }
    }

    /** Generates an actual/expected message from a base message, an actual map,
     *  and an expected map.
     */
    private String genDiffErrMsg(String basemsg, Map<String, Object> expected,
                                 Map<String, Object> actual) {
        return basemsg + "Expected: " + mapToString(expected) + ", but got\n"
                       + "Actual  : " + mapToString(actual);
    }

    /** Converts a Rasterer input or output map to its string representation. */
    private String mapToString(Map<String, ?> m) {
        StringJoiner sj = new StringJoiner(", ", "{", "}");

        List<String> keys = new ArrayList<>();
        keys.addAll(m.keySet());
        Collections.sort(keys);

        for (String k : keys) {

            StringBuilder sb = new StringBuilder();
            sb.append(k);
            sb.append("=");
            Object v = m.get(k);

            if (v instanceof String[][]) {
                sb.append(Arrays.deepToString((String[][]) v));
            } else if (v instanceof Double) {
                sb.append(df2.format(v));
            } else {
                sb.append(v.toString());
            }
            String thisEntry = sb.toString();

            sj.add(thisEntry);
        }

        return sj.toString();
    }

    @Test
    public void testAssembleTiles() throws IllegalAccessException, InstantiationException, NoSuchMethodException, InvocationTargetException {
        String[][] render_grid = null;
        Class<Rasterer> clazz = Rasterer.class;
        Rasterer instance = clazz.newInstance();
        Method method = clazz.getDeclaredMethod("assembleTiles", int[].class, Tile.class);
        method.setAccessible(true);

        render_grid =(String[][]) method.invoke(instance, new Object[]{new int[]{3, 4}, new Tile(2, 0, 1)});
        assertArrayEquals(new String[]{"d2_x0_y1.png", "d2_x1_y1.png", "d2_x2_y1.png", "d2_x3_y1.png"} , render_grid[0]);
    }

    @Test
    public void testCalcDimension() throws IllegalAccessException, InstantiationException, NoSuchMethodException, InvocationTargetException {
        int dimension;
        Class<Rasterer> clazz = Rasterer.class;
        Rasterer instance = clazz.newInstance();
        Method method = clazz.getDeclaredMethod("calcDimension", Map.class);
        method.setAccessible(true);

        Map<String, Double> param = new HashMap<>();
        param.put("lrlon", -122.2104604264636);
        param.put("ullon", -122.30410170759153);
        param.put("w", 1091.0);
        param.put("h", 566.0);
        param.put("lrlat", 37.8318576119893);
        param.put("ullat", 37.870213571328854);
        dimension =(int) method.invoke(instance, param);
        assertEquals(2, dimension);
    }

    @Test
    public void testCalcGridSize() throws IllegalAccessException, InstantiationException, NoSuchMethodException, InvocationTargetException {
        int[] gridSize;
        Class<Rasterer> clazz = Rasterer.class;
        Rasterer instance = clazz.newInstance();
        Method method = clazz.getDeclaredMethod("calcGridSize", Tile.class, Map.class);
        method.setAccessible(true);

        Map<String, Double> param = new HashMap<>();
        param.put("lrlon", -122.2104604264636);
        param.put("ullon", -122.30410170759153);
        param.put("w", 1091.0);
        param.put("h", 566.0);
        param.put("lrlat", 37.8318576119893);
        param.put("ullat", 37.870213571328854);
        gridSize =(int[]) method.invoke(instance, new Object[]{new Tile(2, 0, 1), param});
        assertArrayEquals(new int[]{3, 4}, gridSize);
    }

    @Test
    public void testFindUlImage() throws IllegalAccessException, InstantiationException, NoSuchMethodException, InvocationTargetException {
        Tile tile;
        Class<Rasterer> clazz = Rasterer.class;
        Rasterer instance = clazz.newInstance();
        Method method = clazz.getDeclaredMethod("findUlImage", int.class, Map.class);
        method.setAccessible(true);

        Map<String, Double> param = new HashMap<>();
        param.put("lrlon", -122.2104604264636);
        param.put("ullon", -122.30410170759153);
        param.put("w", 1091.0);
        param.put("h", 566.0);
        param.put("lrlat", 37.8318576119893);
        param.put("ullat", 37.870213571328854);
        int dimension = 2;
        tile =(Tile) method.invoke(instance, new Object[]{dimension, param});
        assertEquals(new Tile(2, 0, 1), tile);
    }
}
