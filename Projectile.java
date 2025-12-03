import java.awt.*;

public class Projectile extends GameObject {
    
    private int speed;
    private boolean enemyProjectile;
    private Color color;
    private int trailLength = 5;
    private int[] trailX, trailY;
    private int trailIndex = 0;
    
    public Projectile(int x, int y, int speed, boolean enemyProjectile) {
        super(x, y, 8, 4);
        this.speed = speed;
        this.enemyProjectile = enemyProjectile;
        this.color = enemyProjectile ? Color.RED : Color.YELLOW;
        
        // Inicializar trail para efecto de estela
        trailX = new int[trailLength];
        trailY = new int[trailLength];
        for (int i = 0; i < trailLength; i++) {
            trailX[i] = x;
            trailY[i] = y;
        }
    }
    
    @Override
    public void update() {
        // Actualizar trail
        trailX[trailIndex] = x;
        trailY[trailIndex] = y;
        trailIndex = (trailIndex + 1) % trailLength;
        
        // Mover proyectil
        x += speed;
        
        // Añadir algo de movimiento vertical para hacerlo más dinámico
        y += Math.sin(x * 0.1) * 0.5;
    }
    
    @Override
    public void draw(Graphics2D g2d) {
        // Dibujar estela
        for (int i = 0; i < trailLength; i++) {
            int alpha = (int)(255 * (1.0 - (double)i / trailLength));
            g2d.setColor(new Color(color.getRed(), color.getGreen(), color.getBlue(), alpha));
            int trailPos = (trailIndex - i + trailLength) % trailLength;
            g2d.fillOval(trailX[trailPos], trailY[trailPos], width - i, height - i);
        }
        
        // Dibujar proyectil principal
        g2d.setColor(color);
        g2d.fillOval(x, y, width, height);
        
        // Añadir brillo
        g2d.setColor(new Color(255, 255, 255, 200));
        g2d.fillOval(x + 1, y + 1, width - 2, height - 2);
        
        // Dibujar aura para proyectiles del jugador
        if (!enemyProjectile) {
            g2d.setColor(new Color(255, 255, 0, 100));
            g2d.fillOval(x - 2, y - 2, width + 4, height + 4);
        }
    }
    
    public boolean isEnemyProjectile() {
        return enemyProjectile;
    }
    
    public Rectangle getBounds() {
        return new Rectangle(x, y, 8, 8);
    }
    
    public int getSpeed() {
        return speed;
    }
}