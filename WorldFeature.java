import java.awt.*;

public class WorldFeature {
    public enum Type { ROCK_SPIRE, CRYSTAL_SPIRE_BG, CITY_TOWER, CITY_TURBINE_BG }

    public int x, y, width, height;
    private Type type;
    private float opacity;
    private double parallax;
    private Color baseColor;

    public WorldFeature(int x, int y, int width, int height, Type type, double parallax, Color color, float opacity) {
        this.x = x;
        this.y = y;
        this.width = width;
        this.height = height;
        this.type = type;
        this.parallax = parallax; // 0.2–0.8: más pequeño = más lejos
        this.baseColor = color;
        this.opacity = opacity;
    }

    public void update(double scrollSpeed) {
        x -= scrollSpeed * parallax;
    }

    public boolean isOffScreen() {
        return x + width < -50;
    }

    public void draw(Graphics2D g2d) {
        Composite prev = g2d.getComposite();
        g2d.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, opacity));
        g2d.setColor(baseColor);
        switch (type) {
            case ROCK_SPIRE:
                drawRockSpire(g2d);
                break;
            case CRYSTAL_SPIRE_BG:
                drawCrystalSpire(g2d);
                break;
            case CITY_TOWER:
                drawCityTower(g2d);
                break;
            case CITY_TURBINE_BG:
                drawCityTurbine(g2d);
                break;
        }
        g2d.setComposite(prev);
    }

    private void drawRockSpire(Graphics2D g) {
        int baseW = width;
        int h = height;
        int[] xs = { x, x + baseW/2, x + baseW };
        int[] ys = { y + h, y, y + h };
        g.fillPolygon(xs, ys, 3);
        g.setColor(new Color(105, 75, 50, (int)(opacity*255)));
        g.drawLine(x + baseW/3, y + h - 10, x + baseW/2, y + 10);
        g.drawLine(x + baseW*2/3, y + h - 15, x + baseW/2, y + 10);
    }

    private void drawCrystalSpire(Graphics2D g) {
        int h = height;
        Polygon p = new Polygon();
        p.addPoint(x + width/2, y);
        p.addPoint(x + width, y + h/3);
        p.addPoint(x + width*3/4, y + h);
        p.addPoint(x + width/4, y + h);
        p.addPoint(x, y + h/3);
        g.fillPolygon(p);
        // brillo
        g.setColor(new Color(255, 255, 255, (int)(opacity*180)));
        g.drawLine(x + width/2, y + 5, x + width*3/4, y + h/3);
        g.drawLine(x + width/2, y + 5, x + width/4, y + h/3);
    }

    private void drawCityTower(Graphics2D g) {
        g.fillRect(x, y, width, height);
        g.setColor(new Color(220, 220, 240, (int)(opacity*180)));
        for (int i = 0; i < height; i += 18) {
            g.drawLine(x + 6, y + i, x + width - 6, y + i);
        }
        // antena
        g.setColor(new Color(255, 200, 0, (int)(opacity*200)));
        g.fillRect(x + width/2 - 2, y - 10, 4, 10);
    }

    private void drawCityTurbine(Graphics2D g) {
        int cx = x + width/2;
        int cy = y + height/2;
        g.fillOval(cx - 14, cy - 14, 28, 28);
        g.setColor(new Color(200, 210, 230, (int)(opacity*160)));
        g.fillRect(cx - 4, cy - 26, 8, 52);
        g.fillRect(cx - 26, cy - 4, 52, 8);
    }
}