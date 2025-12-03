import java.awt.*;
import java.awt.geom.AffineTransform;

public class Aero extends GameObject {
    
    // Propiedades de la nave
    private static final int WIDTH = 60;
    private static final int HEIGHT = 40;
    private static final double MAX_SPEED = 9.0;
    private static final double MAX_VERTICAL_SPEED = 9.8;
    private static final double ACCELERATION = 1.2;
    private static final double VERTICAL_ACCELERATION = 1.1;
    private static final double FRICTION = 0.95;
    
    // Estado del jugador
    private int health = 100;
    private int maxHealth = 100;
    private double speedX = 0;
    private double speedY = 0;
    private boolean turboActive = false;
    private int turboDuration = 0;
    private int shootCooldown = 0;
    private boolean invulnerable = false;
    private int invulnerabilityFrames = 0;
    private int healthSegments = 3;
    private boolean shieldActive = false;
    private int lives = 3;
    private boolean slowActive = false;
    private double slowFactor = 1.0;
    private int slowFrames = 0;
    
    // Animación
    private int animationFrame = 0;
    private int animationDelay = 0;
    private double rotation = 0;
    private double targetRotation = 0;
    
    // Colores y apariencia
    private Color bodyColor = new Color(70, 130, 180);
    private Color wingColor = new Color(100, 149, 237);
    private Color engineColor = new Color(255, 69, 0);
    private Color propellerColor = new Color(192, 192, 192);
    
    public Aero(int x, int y) {
        super(x, y, WIDTH, HEIGHT);
    }
    
    @Override
    public void update() {
        // Actualizar física
        x += speedX;
        y += speedY;
        
        // Aplicar fricción
        if (speedX > 0) speedX = Math.max(0, speedX - FRICTION);
        if (speedX < 0) speedX = Math.min(0, speedX + FRICTION);
        if (speedY > 0) speedY = Math.max(0, speedY - FRICTION);
        if (speedY < 0) speedY = Math.min(0, speedY + FRICTION);
        
        // Limitar velocidad
        speedX = Math.max(-MAX_SPEED, Math.min(MAX_SPEED, speedX));
        speedY = Math.max(-MAX_VERTICAL_SPEED, Math.min(MAX_VERTICAL_SPEED, speedY));
        
        // Mantener dentro de límites
        x = Math.max(50, Math.min(1100, x));
        y = Math.max(50, Math.min(600, y));
        
        // Actualizar rotación suave
        double rotationDiff = targetRotation - rotation;
        rotation += rotationDiff * 0.1;
        
        // Actualizar turbo
        if (turboActive) {
            turboDuration--;
            if (turboDuration <= 0) {
                turboActive = false;
            }
        }
        
        if (slowActive) {
            speedX *= slowFactor;
            speedY *= slowFactor;
            slowFrames--;
            if (slowFrames <= 0) {
                slowActive = false;
                slowFactor = 1.0;
            }
        }
        
        // Actualizar invulnerabilidad
        if (invulnerable) {
            invulnerabilityFrames--;
            if (invulnerabilityFrames <= 0) {
                invulnerable = false;
            }
        }
        
        // Actualizar cooldown de disparo
        if (shootCooldown > 0) {
            shootCooldown--;
        }
        
        // Actualizar animación
        animationDelay++;
        if (animationDelay >= 3) {
            animationFrame = (animationFrame + 1) % 4;
            animationDelay = 0;
        }
    }
    
    @Override
    public void draw(Graphics2D g2d) {
        // Guardar transformación original
        AffineTransform originalTransform = g2d.getTransform();
        
        // Aplicar rotación y posición
        AffineTransform transform = new AffineTransform();
        transform.translate(x + width/2, y + height/2);
        transform.rotate(rotation);
        transform.translate(-width/2, -height/2);
        g2d.transform(transform);
        
        // Efecto de invulnerabilidad (parpadeo)
        if (invulnerable && (invulnerabilityFrames / 5) % 2 == 0) {
            g2d.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, 0.5f));
        }
        
        // Dibujar cuerpo principal
        g2d.setColor(bodyColor);
        g2d.fillOval(15, 10, 30, 20);
        
        // Dibujar alas
        g2d.setColor(wingColor);
        int[] wingX = {0, 15, 45, 60, 45, 15};
        int[] wingY = {20, 15, 10, 20, 30, 25};
        g2d.fillPolygon(wingX, wingY, 6);
        
        // Dibujar cabina
        g2d.setColor(new Color(135, 206, 235));
        g2d.fillOval(20, 12, 12, 8);
        
        // Dibujar motor
        g2d.setColor(engineColor);
        g2d.fillRect(5, 17, 15, 8);
        
        // Dibujar efecto de propulsión (animado)
        if (turboActive) {
            g2d.setColor(new Color(255, 255, 0));
            int flameSize = 15 + animationFrame * 5;
            g2d.fillOval(-flameSize, 18, flameSize, 6);
        } else {
            g2d.setColor(new Color(255, 140, 0));
            int flameSize = 8 + animationFrame * 2;
            g2d.fillOval(-flameSize, 19, flameSize, 4);
        }
        
        // Dibujar hélice (animada)
        g2d.setColor(propellerColor);
        int propellerX = animationFrame < 2 ? -5 : 65;
        g2d.fillRect(propellerX, 10, 10, 20);
        
        // Restaurar transformación original
        g2d.setTransform(originalTransform);
        g2d.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, 1.0f));
        
        // Dibujar barra de salud sobre la nave
        drawHealthBar(g2d);
    }
    
    private void drawHealthBar(Graphics2D g2d) {
        int barWidth = 54;
        int barHeight = 6;
        int barX = x + 3;
        int barY = y - 15;
        int segments = 3;
        int gap = 3;
        int segW = (barWidth - (segments - 1) * gap) / segments;
        
        // Fondo general
        g2d.setColor(Color.DARK_GRAY);
        g2d.fillRect(barX, barY, barWidth, barHeight);
        
        // Relleno por tercios según vidas
        int segsLeft = Math.max(0, Math.min(3, lives));
        Color activeColor = (segsLeft >= 3) ? Color.GREEN : (segsLeft == 2 ? Color.YELLOW : Color.RED);
        for (int i = 0; i < segments; i++) {
            int sx = barX + i * (segW + gap);
            if (i < segsLeft) {
                g2d.setColor(activeColor);
                g2d.fillRect(sx, barY, segW, barHeight);
            } else {
                g2d.setColor(new Color(120, 0, 0));
                g2d.fillRect(sx, barY, segW, barHeight);
            }
        }
        
        // Borde y divisiones
        g2d.setColor(Color.WHITE);
        g2d.drawRect(barX, barY, barWidth, barHeight);
        for (int i = 1; i < segments; i++) {
            int dx = barX + i * (segW + gap) - gap/2;
            g2d.drawLine(dx, barY, dx, barY + barHeight);
        }
    }
    
    private Color getHealthColor() {
        if (health > 66) return Color.GREEN;
        if (health > 33) return Color.YELLOW;
        return Color.RED;
    }
    
    // Métodos de control
    public void moveUp() {
        speedY -= VERTICAL_ACCELERATION;
        targetRotation = -0.2;
    }
    
    public void moveDown() {
        speedY += VERTICAL_ACCELERATION;
        targetRotation = 0.2;
    }
    
    public void moveLeft() {
        speedX -= ACCELERATION;
    }
    
    public void moveRight() {
        speedX += ACCELERATION;
    }
    
    public Projectile shoot() {
        if (shootCooldown <= 0) {
            shootCooldown = 15; // 15 frames de cooldown
            return new Projectile(x + width, y + height/2, 10, false);
        }
        return null;
    }
    
    public void takeDamage() {
        takeDamage(20);
    }
    
    public void takeDamage(int damage) {
        if (!invulnerable) {
            health -= damage;
            if (health < 0) health = 0;
            invulnerable = true;
            invulnerabilityFrames = 60; 
        }
    }

    public boolean applyDamage(int damage) {
        if (invulnerable) return false;
        if (shieldActive) {
            shieldActive = false;
            invulnerable = true;
            invulnerabilityFrames = 20;
            return false;
        }
        health = maxHealth;
        invulnerable = true;
        invulnerabilityFrames = 60;
        return true;
    }
    
    public void activateTurbo() {
        turboActive = true;
        turboDuration = 180; // 3 segundos a 60 FPS
    }
    
    public void reset() {
        health = maxHealth;
        speedX = 0;
        speedY = 0;
        turboActive = false;
        turboDuration = 0;
        invulnerable = false;
        invulnerabilityFrames = 0;
        rotation = 0;
        targetRotation = 0;
    }
    
    public void activateShield() { shieldActive = true; }
    public boolean isShieldActive() { return shieldActive; }
    public void setLives(int lives) { this.lives = lives; }
    public void applySlow(double factor, int frames) { slowActive = true; slowFactor = factor; slowFrames = frames; }
    public void healToFull() { this.health = this.maxHealth; }
    
    
    // Getters
    public int getHealth() { return health; }
    public boolean isTurboActive() {
        return turboActive;
    }
    
    public boolean isInvulnerable() {
        return invulnerable;
    }
    
    public Rectangle getBounds() {
        return new Rectangle(x, y, width, height);
    }
    
    public void setInvulnerable(int frames) {
        invulnerable = true;
        invulnerabilityFrames = frames;
    }
    
    public int getTurboDuration() {
        return turboDuration;
    }
    
    @Override
    public boolean collidesWith(GameObject other) {
        // Colisión más precisa considerando la forma de la nave
        Rectangle thisRect = new Rectangle(x + 10, y + 5, width - 20, height - 10);
        Rectangle otherRect = new Rectangle(other.x, other.y, other.width, other.height);
        return thisRect.intersects(otherRect);
    }
}
