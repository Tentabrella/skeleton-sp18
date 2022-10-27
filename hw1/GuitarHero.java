import edu.princeton.cs.algs4.StdAudio;
import edu.princeton.cs.algs4.StdDraw;
import synthesizer.GuitarString;

import java.util.Arrays;

public class GuitarHero {
    private static final String KEYBOARD = "q2we4r5ty7u8i9op-[=zxdcfvgbnjmk,.;/' ";

    public static void main(String[] args) {
        GuitarString[] keyboardArray = new GuitarString[37];
        for (int i = 0; i < 37; i++) {
            keyboardArray[i] = new GuitarString(440.0 * Math.pow(2, (i - 24) / 12.0));
        }
        while (true) {
            /* check if the user has typed a key; if so, process it */
            if (StdDraw.hasNextKeyTyped()) {
                char key = StdDraw.nextKeyTyped();
                int someString = KEYBOARD.indexOf(key);
                if (someString != -1) {
                    keyboardArray[someString].pluck();
                }
            }

            /* compute the superposition of samples */
            double sample = Arrays.stream(keyboardArray).mapToDouble(GuitarString::sample).sum();

            /* play the sample on standard audio */
            StdAudio.play(sample);

            /* advance the simulation of each guitar string by one step */
            Arrays.stream(keyboardArray).forEach(GuitarString::tic);
        }
    }
}
