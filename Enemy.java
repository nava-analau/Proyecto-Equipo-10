import java.awt.*;

public class Enemy extends GameObject {
    
    private int speed;
    private int health = 30;
    private int shootCooldown = 0;
    private int animationFrame = 0;
    private int animationDelay = 0;
    private boolean movingUp = true;
    private int verticalRange = 100;
    private int originalY;
    
    public Enemy(int x, int y, double speed, int health) {
        super(x, y, 40, 30); // Ancho 40, alto 30
        this.speed = (int)speed;
        this.health = health;
        this.shootCooldown = 0;
        this.animationFrame = 0;
        this.animationDelay = 0;
        this.originalY = y;
    }
    
    public Enemy(int x, int y, int speed) {
        super(x, y, 40, 30); // Ancho 40, alto 30
        this.speed = speed;
        this.health = 100;
        this.shootCooldown = 0;
        this.animationFrame = 0;
        this.animationDelay = 0;
        this.originalY = y;
    }
    
    @Override
    public void update() {
        animationDelay++;
        if (animationDelay >= 4) {
            animationFrame = (animationFrame + 1) % 6;
            animationDelay = 0;
        }
        
        // Movimiento vertical
        if (movingUp) {
            y -= 2;
            if (y <= originalY - verticalRange) {
                movingUp = false;
            }
        } else {
            y += 2;
            if (y >= originalY + verticalRange) {
                movingUp = true;
            }
        }
        
        // Actualizar cooldown de disparo
        if (shootCooldown > 0) {
            shootCooldown--;
        }
    }
    
    public void update(int scrollSpeed) {
        x -= scrollSpeed;
        update();
    }
    
    @Override
    public void draw(Graphics2D g2d) {
        // Cuerpo principal
        g2d.setColor(new Color(139, 0, 0));
        g2d.fillOval(x, y + 5, width, height - 10);
        
        // Alas
        g2d.setColor(new Color(178, 34, 34));
        int wingBeat = animationFrame % 2 == 0 ? 5 : -5;
        
        // Ala superior
        g2d.fillPolygon(
            new int[]{x + 10, x - 5, x + 15},
            new int[]{y + 10, y + wingBeat, y + 15},
            3
        );
        
        // Ala inferior
        g2d.fillPolygon(
            new int[]{x + 10, x + 15, x + 25},
            new int[]{y + 20, y + 25, y + 20 + wingBeat},
            3
        );
        
        // Cabina del piloto
        g2d.setColor(new Color(255, 215, 0));
        g2d.fillOval(x + width - 15, y + 10, 10, 8);
        
        // Cañón
        g2d.setColor(new Color(64, 64, 64));
        g2d.fillRect(x + width - 5, y + height/2 - 2, 15, 4);
        
        // Detalles
        g2d.setColor(new Color(105, 105, 105));
        g2d.drawOval(x, y + 5, width, height - 10);
        
        // Indicador de enemigo
        g2d.setColor(Color.RED);
        g2d.fillOval(x + width/2 - 3, y - 10, 6, 6);
    }
    
    public boolean canShoot() {
        return shootCooldown <= 0;
    }
    
    public Projectile shoot() {
        if (canShoot()) {
            shootCooldown = 60;
            return new Projectile(x - 10, y + height/2, -8, true);
        }
        return null;
    }
    
    public Rectangle getBounds() {
        return new Rectangle(x, y, width, height);
    }

    public void takeDamage(int damage) {
        health -= damage;
        if (health <= 0) {
            active = false;
        }
    }

    public int getHealth() {
        return health;
    }
}
