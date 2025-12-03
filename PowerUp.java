import java.awt.*;

public class PowerUp extends GameObject {
    
    public enum Type {
        TURBO, SCORE_BOOST, SHIELD, HEALTH
    }
    
    public Type type;
    private int animationFrame = 0;
    private int animationDelay = 0;
    private float glowIntensity = 0;
    private boolean glowIncreasing = true;
    
    public PowerUp(int x, int y, Type type) {
        super(x, y, 30, 30);
        this.type = type;
    }
    
    @Override
    public void update() {
        animationDelay++;
        if (animationDelay >= 3) {
            animationFrame = (animationFrame + 1) % 8;
            animationDelay = 0;
        }
        
        // Efecto de brillo pulsante
        if (glowIncreasing) {
            glowIntensity += 0.02;
            if (glowIntensity >= 1.0f) {
                glowIntensity = 1.0f;
                glowIncreasing = false;
            }
        } else {
            glowIntensity -= 0.02;
            if (glowIntensity <= 0.3f) {
                glowIntensity = 0.3f;
                glowIncreasing = true;
            }
        }
        
        // Movimiento flotante
        y += Math.sin(animationFrame * 0.5) * 0.5;
    }
    
    public void update(int scrollSpeed) {
        x -= scrollSpeed;
        update();
    }
    
    public Rectangle getBounds() {
        return new Rectangle(x, y, width, height);
    }
    
    public void applyEffect(Aero player) {
        switch (type) {
            case TURBO:
                player.activateTurbo();
                break;
            case SCORE_BOOST:
                // El boost de puntuación se maneja en el juego principal
                break;
            case SHIELD:
                player.activateShield();
                break;
            case HEALTH:
                // La vida extra se maneja en el juego principal
                break;
        }
    }
    
    @Override
    public void draw(Graphics2D g2d) {
        // Efecto de brillo exterior
        g2d.setColor(new Color(255, 255, 255, (int)(50 * glowIntensity)));
        g2d.fillOval(x - 5, y - 5, width + 10, height + 10);
        
        // Círculo exterior
        g2d.setColor(getPowerUpColor());
        g2d.fillOval(x, y, width, height);
        
        // Símbolo interior
        drawSymbol(g2d);
        
        // Efecto de rotación
        g2d.setColor(new Color(255, 255, 255, (int)(100 * glowIntensity)));
        int rotationAngle = animationFrame * 45;
        Graphics2D g2dRotated = (Graphics2D) g2d.create();
        g2dRotated.rotate(Math.toRadians(rotationAngle), x + width/2, y + height/2);
        g2dRotated.drawOval(x - 2, y - 2, width + 4, height + 4);
        g2dRotated.dispose();
        
        // Partículas brillantes
        drawParticles(g2d);
    }
    
    private Color getPowerUpColor() {
        switch (type) {
            case TURBO:
                return new Color(255, 140, 0); // Naranja
            case SCORE_BOOST:
                return new Color(255, 215, 0); // Dorado
            case SHIELD:
                return new Color(70, 130, 180); // Azul acero
            case HEALTH:
                return new Color(220, 20, 60); // Rojo
            default:
                return Color.CYAN;
        }
    }
    
    private void drawSymbol(Graphics2D g2d) {
        g2d.setColor(Color.WHITE);
        
        switch (type) {
            case TURBO:
                // Símbolo de velocidad (flecha)
                int[] arrowX = {x + 8, x + width - 8, x + width/2};
                int[] arrowY = {y + height/2, y + height/2, y + 5};
                g2d.fillPolygon(arrowX, arrowY, 3);
                break;
                
            case SCORE_BOOST:
                // Símbolo de estrella
                drawStar(g2d, x + width/2, y + height/2, 8);
                break;
            case SHIELD:
                // Símbolo de escudo
                int cx = x + width/2;
                int cy = y + height/2;
                Polygon shield = new Polygon();
                shield.addPoint(cx, y + 6);
                shield.addPoint(x + 6, cy);
                shield.addPoint(cx, y + height - 6);
                shield.addPoint(x + width - 6, cy);
                g2d.fillPolygon(shield);
                break;
            case HEALTH:
                // Símbolo de cruz
                int s = 6;
                int mx = x + width/2 - s/2;
                int my = y + height/2 - s/2;
                g2d.fillRect(mx - 8, my, 16, s);
                g2d.fillRect(mx, my - 8, s, 16);
                break;
        }
    }
    
    private void drawStar(Graphics2D g2d, int centerX, int centerY, int size) {
        int[] starX = new int[10];
        int[] starY = new int[10];
        
        for (int i = 0; i < 10; i++) {
            double angle = Math.PI * i / 5;
            int radius = (i % 2 == 0) ? size : size / 2;
            starX[i] = centerX + (int)(Math.cos(angle) * radius);
            starY[i] = centerY + (int)(Math.sin(angle) * radius);
        }
        
        g2d.fillPolygon(starX, starY, 10);
    }
    
    private void drawParticles(Graphics2D g2d) {
        g2d.setColor(new Color(255, 255, 255, (int)(150 * glowIntensity)));
        
        for (int i = 0; i < 4; i++) {
            int particleX = x + (int)(Math.cos(animationFrame * 0.5 + i * Math.PI/2) * 20) + width/2;
            int particleY = y + (int)(Math.sin(animationFrame * 0.5 + i * Math.PI/2) * 20) + height/2;
            g2d.fillOval(particleX - 2, particleY - 2, 4, 4);
        }
    }
    
    
}
