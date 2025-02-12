import org.xml.sax.SAXException;
import pojos.Trie;
import pojos.Vertex;
import pojos.Way;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;

import java.util.*;

/**
 * Graph for storing all of the intersection (vertex) and road (edge) information.
 * Uses your GraphBuildingHandler to convert the XML files into a graph. Your
 * code must include the vertices, adjacent, distance, closest, lat, and lon
 * methods. You'll also need to include instance variables and methods for
 * modifying the graph (e.g. addNode and addEdge).
 *
 * @author Alan Yao, Josh Hug
 */
public class GraphDB {
    /** Your instance variables for storing the graph. You should consider
     * creating helper classes, e.g. Node, Edge, etc. */
    private Map<Long, List<Long>> edges;
    private Map<Long, Vertex> vertices;
    private Map<Long, Way> ways;
    private Map<String, List<Vertex>> locations;
    private Trie locationTrie;

    /**
     * Example constructor shows how to create and start an XML parser.
     * You do not need to modify this constructor, but you're welcome to do so.
     * @param dbPath Path to the XML file to be parsed.
     */
    public GraphDB(String dbPath) {
        try {
            File inputFile = new File(dbPath);
            FileInputStream inputStream = new FileInputStream(inputFile);
            // GZIPInputStream stream = new GZIPInputStream(inputStream);

            SAXParserFactory factory = SAXParserFactory.newInstance();
            SAXParser saxParser = factory.newSAXParser();
            GraphBuildingHandler gbh = new GraphBuildingHandler(this);
            edges = new HashMap<>();
            vertices = new HashMap<>();
            ways = new HashMap<>();
            saxParser.parse(inputStream, gbh);
        } catch (ParserConfigurationException | SAXException | IOException e) {
            e.printStackTrace();
        }
        locations = new HashMap<>();
        locationTrie = new Trie();
        buildVertexDB();
        clean();
    }

    private void buildVertexDB() {
        for (Long vertexId : vertices.keySet()) {
            String location = vertices.get(vertexId).getName();
            if (location != null && !location.equals("")) {
                location = cleanString(location);
                List<Vertex> locationIds = locations.getOrDefault(location, new LinkedList<>());
                locationIds.add(vertices.get(vertexId));
                locations.put(location, locationIds);
                locationTrie.add(location);
            }
        }
    }

    /**
     * Helper to process strings into their "cleaned" form, ignoring punctuation and capitalization.
     * @param s Input string.
     * @return Cleaned string.
     */
    static String cleanString(String s) {
        return s.replaceAll("[^a-zA-Z ]", "").toLowerCase();
    }

    /**
     *  Remove nodes with no connections from the graph.
     *  While this does not guarantee that any two nodes in the remaining graph are connected,
     *  we can reasonably assume this since typically roads are connected.
     */
    private void clean() {
        Set<Long> graphNodes = edges.keySet();
        Set<Long> oldKeys = new HashSet<>(vertices.keySet());
        for (Long vertex : oldKeys) {
            if (!graphNodes.contains(vertex)) {
                vertices.remove(vertex);
            }
        }
    }

    /**
     * Returns an iterable of all vertex IDs in the graph.
     * @return An iterable of id's of all vertices in the graph.
     */
    Iterable<Long> vertices() {
        return vertices.keySet();
    }

    /**
     * Returns ids of all vertices adjacent to v.
     * @param v The id of the vertex we are looking adjacent to.
     * @return An iterable of the ids of the neighbors of v.
     */
    Iterable<Long> adjacent(long v) {
        return edges.get(v);
    }

    /**
     * Returns the great-circle distance between vertices v and w in miles.
     * Assumes the lon/lat methods are implemented properly.
     * <a href="https://www.movable-type.co.uk/scripts/latlong.html">Source</a>.
     * @param v The id of the first vertex.
     * @param w The id of the second vertex.
     * @return The great-circle distance between the two locations from the graph.
     */
    double distance(long v, long w) {
        return distance(lon(v), lat(v), lon(w), lat(w));
    }

    static double distance(double lonV, double latV, double lonW, double latW) {
        double phi1 = Math.toRadians(latV);
        double phi2 = Math.toRadians(latW);
        double dphi = Math.toRadians(latW - latV);
        double dlambda = Math.toRadians(lonW - lonV);

        double a = Math.sin(dphi / 2.0) * Math.sin(dphi / 2.0);
        a += Math.cos(phi1) * Math.cos(phi2) * Math.sin(dlambda / 2.0) * Math.sin(dlambda / 2.0);
        double c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1 - a));
        return 3963 * c;
    }

    /**
     * Returns the initial bearing (angle) between vertices v and w in degrees.
     * The initial bearing is the angle that, if followed in a straight line
     * along a great-circle arc from the starting point, would take you to the
     * end point.
     * Assumes the lon/lat methods are implemented properly.
     * <a href="https://www.movable-type.co.uk/scripts/latlong.html">Source</a>.
     * @param v The id of the first vertex.
     * @param w The id of the second vertex.
     * @return The initial bearing between the vertices.
     */
    double bearing(long v, long w) {
        return bearing(lon(v), lat(v), lon(w), lat(w));
    }

    static double bearing(double lonV, double latV, double lonW, double latW) {
        double phi1 = Math.toRadians(latV);
        double phi2 = Math.toRadians(latW);
        double lambda1 = Math.toRadians(lonV);
        double lambda2 = Math.toRadians(lonW);

        double y = Math.sin(lambda2 - lambda1) * Math.cos(phi2);
        double x = Math.cos(phi1) * Math.sin(phi2);
        x -= Math.sin(phi1) * Math.cos(phi2) * Math.cos(lambda2 - lambda1);
        return Math.toDegrees(Math.atan2(y, x));
    }

    /**
     * Returns the vertex closest to the given longitude and latitude.
     * @param lon The target longitude.
     * @param lat The target latitude.
     * @return The id of the node in the graph closest to the target.
     */
    long closest(double lon, double lat) {
        double distance = Double.MAX_VALUE;
        Long closetVertex = Long.MAX_VALUE;
        for (Long vertexId : vertices.keySet()) {
            Vertex vertex = vertices.get(vertexId);
            double currDistance = distance(lon, lat, vertex.getLon(), vertex.getLat());
            if (currDistance < distance) {
                distance = currDistance;
                closetVertex = vertexId;
            }
        }
        return closetVertex;
    }

    /**
     * Gets the longitude of a vertex.
     * @param v The id of the vertex.
     * @return The longitude of the vertex.
     */
    double lon(long v) {
        return vertices.get(v).getLon();
    }

    /**
     * Gets the latitude of a vertex.
     * @param v The id of the vertex.
     * @return The latitude of the vertex.
     */
    double lat(long v) {
        return vertices.get(v).getLat();
    }

    public Map<Long, List<Long>> getEdges() {
        return edges;
    }

    public Map<Long, Vertex> getVertices() {
        return vertices;
    }

    public Map<Long, Way> getWays() {
        return ways;
    }

    public Map<String, List<Vertex>> getLocations() {
        return locations;
    }

    public Trie getLocationTrie() {
        return locationTrie;
    }

    public List<Map<String, Object>> getLocations(String locationName) {
        locationName = cleanString(locationName);
        List<Map<String, Object>> res = new LinkedList<>();
        List<Vertex> locations = this.getLocations().get(locationName);
        for (Vertex location : locations) {
            Map<String, Object> params = new HashMap<>();
            params.put("lat", location.getLat());
            params.put("lon", location.getLon());
            params.put("name", location.getName());
            params.put("id", location.getId());
            res.add(params);
        }
        return res;
    }

    public List<String> getLocationsByPrefix(String prefix) {
        List<String> res = new LinkedList<>();
        for (String locationName : this.getLocationTrie().keysWithPrefix(prefix)) {
            List<Map<String, Object>> locations = getLocations(locationName);
            for (Map<String, Object> location : locations) {
                String name = (String) location.get("name");
                res.add(name);
            }
        }
        return res;
    }
}
