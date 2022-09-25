public class Planet {
    public double xxPos;
    public double yyPos;
    public double xxVel;
    public double yyVel;
    public double mass;
    public String imgFileName;
    public static double G = 6.67E-11;

    public Planet(double xP, double yP, double xV,
            double yV, double m, String img) {
        this.xxPos = xP;
        this.yyPos = yP;
        this.xxVel = xV;
        this.yyVel = yV;
        this.mass = m;
        this.imgFileName = img;
    }

    public Planet(Planet b) {
        this.xxPos = b.xxPos;
        this.yyPos = b.yyPos;
        this.xxVel = b.xxVel;
        this.yyVel = b.yyVel;
        this.mass = b.mass;
        this.imgFileName = b.imgFileName; 
    }

    public double calcDistance(Planet b) {
        double dx = this.xxPos - b.xxPos;
        double dy = this.yyPos - b.yyPos;
        return Math.sqrt(Math.pow(dx, 2) + Math.pow(dy, 2));
    }

    public double calcForceExertedBy(Planet b) {
        double force = (G*this.mass*b.mass)/(Math.pow(this.calcDistance(b), 2));
        return force;
    }

    public double calcForceExertedByX(Planet b) {
        double force = calcForceExertedBy(b);
        double dx = b.xxPos - this.xxPos;
        double r = calcDistance(b);
        return force*dx/r;
    }

    public double calcForceExertedByY(Planet b) {
        double force = calcForceExertedBy(b);
        double dy = b.yyPos - this.yyPos;
        double r = calcDistance(b);
        return force*dy/r;
    }  
    
    public double calcNetForceExertedByX(Planet[] planetArr) {
        double force = 0;
        for (Planet b: planetArr) {
            if (b.equals(this)) continue;
            force += calcForceExertedByX(b);
        }
        return force;
    }

    public double calcNetForceExertedByY(Planet[] planetArr) {
        double force = 0;
        for (Planet b: planetArr) {
            if (b.equals(this)) continue;
            force += calcForceExertedByY(b);
        }
        return force;
    }

    public void update(double dt, double fX, double fY) {
        double aX = fX/this.mass;
        double aY = fY/this.mass;
        this.xxVel += dt*aX;
        this.yyVel += dt*aY;
        this.xxPos += dt*this.xxVel;
        this.yyPos += dt*this.yyVel;
    }

    public void draw(double radius) {
        StdDraw.picture(this.xxPos/radius, this.yyPos/radius, "./images/" + imgFileName);
    }

    @Override
    public String toString() {
        return "Planet{" +
                "xxPos=" + xxPos +
                ", yyPos=" + yyPos +
                ", xxVel=" + xxVel +
                ", yyVel=" + yyVel +
                ", mass=" + mass +
                ", imgFileName='" + imgFileName + '\'' +
                '}';
    }
}
