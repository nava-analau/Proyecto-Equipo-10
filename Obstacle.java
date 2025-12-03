import java.awt.*;

public class Obstacle extends GameObject {
    
    public enum Type {
        ROCK_TOWER, ELECTRIC_STORM, TURBINE, CLOUD_KINGDOM_TOWER, CRYSTAL_SPIKE, FLOATING_PLATFORM, CITY_BUILDING
    }
    
    private Type type;
    private int animationFrame = 0;
    private int animationDelay = 0;
    private boolean harmful = true;
    private boolean fromBottom = true;
    
    public Obstacle(int x, int y, Type type) {
        super(x, y, 60, 80);
        this.type = type;
        
        switch (type) {
            case ROCK_TOWER:
                width = 50;
                height = 100;
                break;
            case ELECTRIC_STORM:
                width = 80;
                height = 80;
                break;
            case TURBINE:
                width = 40;
                height = 60;
                harmful = true;
                break;
            case CITY_BUILDING:
                width = 70;
                height = 180;
                harmful = true;
                break;
        }
    }

    public Obstacle(int x, int width, int height, int y, boolean fromBottom, Type type) {
        super(x, y, width, height);
        this.type = type;
        this.fromBottom = fromBottom;
        this.harmful = true;
    }
    
    @Override
    public void update() {
        animationDelay++;
        if (animationDelay >= 5) {
            animationFrame = (animationFrame + 1) % 8;
            animationDelay = 0;
        }
    }
    
    public void update(int scrollSpeed) {
        x -= scrollSpeed;
        update();
    }
    
    @Override
    public void draw(Graphics2D g2d) {
        switch (type) {
            case ROCK_TOWER:
            case CLOUD_KINGDOM_TOWER:
                drawRockTower(g2d);
                break;
            case ELECTRIC_STORM:
            case CRYSTAL_SPIKE:
                drawElectricStorm(g2d);
                break;
            case TURBINE:
            case FLOATING_PLATFORM:
                drawTurbine(g2d);
                break;
            case CITY_BUILDING:
                drawCityBuilding(g2d);
                break;
        }
    }
    
    private void drawRockTower(Graphics2D g2d) {
        g2d.setColor(new Color(139, 69, 19));
        g2d.fillRect(x, y, width, height);
        
        g2d.setColor(new Color(160, 82, 45));
        g2d.fillRect(x + 5, y + 10, width - 10, 20);
        g2d.fillRect(x + 3, y + 40, width - 6, 15);
        g2d.fillRect(x + 7, y + 65, width - 14, 18);
        
        g2d.setColor(new Color(105, 105, 105));
        g2d.drawRect(x, y, width, height);
        
        if (harmful) {
            g2d.setColor(Color.RED);
            g2d.fillOval(x + width/2 - 5, y - 15, 10, 10);
        }
    }
    
    private void drawElectricStorm(Graphics2D g2d) {
        g2d.setColor(new Color(64, 64, 64));
        g2d.fillOval(x, y, width, height/2);
        g2d.fillOval(x - 10, y + 10, width + 20, height/2);
        g2d.fillOval(x + 10, y + 5, width - 20, height/2);
        
        if (animationFrame % 2 == 0) {
            g2d.setColor(new Color(255, 255, 0));
            int startX = x + width/2;
            int startY = y + height;
            int endY = SkyRunnerGame.HEIGHT;
            int segment = 20;
            int offset = 12;
            int prevX = startX;
            int prevY = startY;
            for (int yy = startY; yy < endY; yy += segment) {
                int dir = ((yy - startY) / segment) % 2 == 0 ? -offset : offset;
                int nextX = startX + dir;
                int nextY = Math.min(yy + segment, endY);
                g2d.drawLine(prevX, prevY, nextX, nextY);
                prevX = nextX;
                prevY = nextY;
            }
            
            g2d.setColor(new Color(255, 255, 255, 100));
            g2d.fillOval(x + width/2 - 15, y + height - 10, 30, 20);
        }
        
        if (harmful) {
            g2d.setColor(Color.YELLOW);
            g2d.fillOval(x + width/2 - 5, y - 15, 10, 10);
        }
    }
    
    private void drawTurbine(Graphics2D g2d) {
        g2d.setColor(new Color(105, 105, 105));
        g2d.fillRect(x, y, width, height);
        
        g2d.setColor(new Color(192, 192, 192));
        int centerX = x + width/2;
        int centerY = y + height/2;
        
        double rotation = animationFrame * Math.PI / 4;
        Graphics2D g2dRotated = (Graphics2D) g2d.create();
        g2dRotated.rotate(rotation, centerX, centerY);
        
        g2dRotated.fillRect(centerX - 3, centerY - 25, 6, 50);
        g2dRotated.fillRect(centerX - 25, centerY - 3, 50, 6);
        
        g2dRotated.dispose();
        
        g2d.setColor(new Color(64, 64, 64));
        g2d.fillOval(centerX - 8, centerY - 8, 16, 16);
        
        g2d.setColor(new Color(0, 255, 255, 150));
        for (int i = 0; i < 3; i++) {
            g2d.fillOval(x - 10 - i * 15, centerY - 5, 20, 10);
        }
    }

    private void drawCityBuilding(Graphics2D g2d) {
        // Cuerpo del edificio
        g2d.setColor(new Color(90, 90, 120));
        g2d.fillRect(x, y, width, height);
        
        // Ventanas en rejilla
        g2d.setColor(new Color(200, 220, 255, 180));
        int rows = Math.max(3, height / 30);
        int cols = Math.max(2, width / 20);
        int padX = 6, padY = 8, winW = 10, winH = 14;
        for (int r = 0; r < rows; r++) {
            int wy = y + padY + r * (winH + 6);
            if (wy + winH > y + height - padY) break;
            for (int c = 0; c < cols; c++) {
                int wx = x + padX + c * (winW + 6);
                if (wx + winW > x + width - padX) break;
                g2d.fillRect(wx, wy, winW, winH);
            }
        }
        
        // Borde
        g2d.setColor(new Color(60, 60, 80));
        g2d.drawRect(x, y, width, height);
        
        // Indicador
        g2d.setColor(Color.RED);
        g2d.fillOval(x + width/2 - 5, y - 12, 10, 10);
    }
    
    public boolean isHarmful() {
        return harmful;
    }
    
    public Type getType() {
        return type;
    }
    
    public Rectangle getLightningColumnBounds(int gameHeight) {
        if (type != Type.ELECTRIC_STORM) return null;
        int columnWidth = 24;
        int cx = x + width / 2;
        int topY = y + height;
        int h = Math.max(0, gameHeight - topY);
        return new Rectangle(cx - columnWidth / 2, topY, columnWidth, h);
    }
    
    public void applyEffect(Aero player) {
        switch (type) {
            case TURBINE:
            case FLOATING_PLATFORM:
                // El jugador es empujado hacia la derecha
                player.x += 20;
                // Asegurar que no se salga de los límites
                if (player.x > 1100) player.x = 1100;
                break;
            case ELECTRIC_STORM:
            case CRYSTAL_SPIKE:
                // El daño directo se maneja en la lógica de colisiones del juego
                break;
        }
    }
    
    public Rectangle getBounds() {
        return new Rectangle(x, y, width, height);
    }
}
