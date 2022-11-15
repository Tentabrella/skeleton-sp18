import org.junit.Test;
import pojos.Tile;
import static org.junit.Assert.*;

public class TestTile {
    @Test
    public void testtilePath() {
        Tile t1 = new Tile(2,0, 1);
        assertEquals("d2_x0_y1.png", t1.tilePath());
    }

    @Test
    public void testAAA() {
        System.out.println(Math.ceil(1/-1));
    }
}
