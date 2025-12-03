import java.awt.*;

public class Cloud {
    
    public int x, y;
    public int width, height;
    public int speed;
    private Color color;
    private int opacity;
    private int[] cloudPoints;
    private int numPoints;
    
    public Cloud(int x, int y, int size) {
        this.x = x;
        this.y = y;
        this.speed = size;
        this.width = 60 + size * 20;
        this.height = 30 + size * 10;
        this.opacity = 150 + size * 20;
        this.color = new Color(255, 255, 255, Math.min(opacity, 255));
        
        // Generar forma de nube orgánica
        generateCloudShape();
    }
    
    private void generateCloudShape() {
        numPoints = 5 + (int)(Math.random() * 3);
        cloudPoints = new int[numPoints * 2];
        
        for (int i = 0; i < numPoints; i++) {
            double angle = (2 * Math.PI * i) / numPoints;
            int radius = (int)(width * 0.3 + Math.random() * width * 0.2);
            
            cloudPoints[i * 2] = (int)(Math.cos(angle) * radius);
            cloudPoints[i * 2 + 1] = (int)(Math.sin(angle) * radius * 0.5);
        }
    }
    
    public void update() {
        x -= speed;
        
        // Movimiento suave hacia arriba y abajo
        y += Math.sin(x * 0.01) * 0.5;
    }
    
    public void draw(Graphics2D g2d) {
        // Guardar configuración original
        Composite originalComposite = g2d.getComposite();
        
        // Aplicar transparencia
        g2d.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, 
            color.getAlpha() / 255.0f));
        
        // Dibujar nube con forma orgánica
        g2d.setColor(color);
        
        // Centro de la nube
        int centerX = x + width / 2;
        int centerY = y + height / 2;
        
        // Dibujar círculos superpuestos para crear forma de nube
        for (int i = 0; i < numPoints; i++) {
            int circleX = centerX + cloudPoints[i * 2];
            int circleY = centerY + cloudPoints[i * 2 + 1];
            int circleRadius = (int)(width * 0.15 + Math.random() * width * 0.1);
            
            g2d.fillOval(circleX - circleRadius, circleY - circleRadius, 
                         circleRadius * 2, circleRadius * 2);
        }
        
        // Dibujar círculo central más grande
        int mainRadius = (int)(width * 0.25);
        g2d.fillOval(centerX - mainRadius, centerY - mainRadius, 
                     mainRadius * 2, mainRadius * 2);
        
        // Añadir sombra sutil
        g2d.setColor(new Color(200, 200, 200, color.getAlpha() / 2));
        g2d.fillOval(centerX - mainRadius + 3, centerY - mainRadius + 3, 
                     mainRadius * 2, mainRadius * 2);
        
        // Restaurar configuración original
        g2d.setComposite(originalComposite);
    }
    
    public boolean isOffScreen() {
        return x + width < 0;
    }
}