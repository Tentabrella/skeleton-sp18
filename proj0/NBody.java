import java.util.*;
import java.nio.file.*;
import java.nio.charset.StandardCharsets;
import java.io.*;

public class NBody {
    public static double readRadius(String path) {
        double radius = -1;
        try {
            List<String> lines = Files.readAllLines(Paths.get(path), StandardCharsets.US_ASCII);
            radius = Double.parseDouble(lines.get(1));
        } catch (IOException e) {
            System.err.println("may imput a wrong path");
        } 
        return radius;
    }
    
    public static Planet[] readPlanets(String path) {
        Planet[] planets = null;
        try {
            List<String> lines = Files.readAllLines(Paths.get(path), StandardCharsets.US_ASCII);
            int bodyNum = Integer.parseInt(lines.get(0));
            planets = new Planet[bodyNum];
            int j = 0;
            for (int i = 2; i < 2 + bodyNum; i++) {
                String bodyRaw = lines.get(i);
                Planet planet = parsePlanet(bodyRaw);
                planets[j++] = planet; 
            } 
        } catch (IOException e) {
            System.err.println("may imput a wrong path");
        } 
        return planets;
    }

    public static Planet parsePlanet(String s) {
        String[] d = s.trim().split("\\s+");
        Planet planet = new Planet(Double.parseDouble(d[0]), 
            Double.parseDouble(d[1]), 
            Double.parseDouble(d[2]), 
            Double.parseDouble(d[3]), 
            Double.parseDouble(d[4]),
            d[5]);
        return planet;
    }

    public static void main(String[] args) {
        // readin values
        double T = Double.parseDouble(args[0]);
        double dt = Double.parseDouble(args[1]);
        String filename = args[2];

        //readin planets and init fX fY array
        double radius = readRadius(filename);
        Planet[] planets = readPlanets(filename);
       
        //init canvas
        StdDraw.setXscale(-1.0, 1.0);
        StdDraw.setYscale(-1.0, 1.0);
        StdDraw.enableDoubleBuffering();
        
        //init fX and fY array
        int n = planets.length;
        double[] fXs = new double[n];
        double[] fYs = new double[n];
 
       while (T > 0) {
            StdDraw.picture(0, 0, "./images/starfield.jpg");
            for (int i = 0; i < n; i++) {
                Planet planet = planets[i];
                double fX = planet.calcNetForceExertedByX(planets);
                double fY = planet.calcNetForceExertedByY(planets);
                fXs[i] = fX;
                fYs[i] = fY;
            }
            for (int i = 0; i < n; i++) {
                Planet planet = planets[i];
                planet.update(dt, fXs[i], fYs[i]);
                planet.draw(radius);
            }
            StdDraw.show();
            T -= dt;
            StdDraw.pause(10);
        }
        StdOut.printf("%d\n", planets.length);
        StdOut.printf("%.2e\n", radius);
        for (int i = 0; i < planets.length; i++) {
            StdOut.printf("%11.4e %11.4e %11.4e %11.4e %11.4e %12s\n",
                      planets[i].xxPos, planets[i].yyPos, planets[i].xxVel,
                      planets[i].yyVel, planets[i].mass, planets[i].imgFileName);
}
    }
}
