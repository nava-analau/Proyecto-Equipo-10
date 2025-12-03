import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.util.ArrayList;
import java.util.List;

public class SkyRunnerGame extends JPanel implements ActionListener, KeyListener {
    
    // Estados del juego
    public enum GameState { MENU, PLAYING, PAUSED, GAME_OVER, VICTORY }
    public enum Difficulty { EASY, NORMAL, HARD }
    public enum World { CLOUD_KINGDOM, CRYSTAL_CANYON, FLOATING_CITY }
    
    // Dimensiones del juego
    public static final int WIDTH = 800;
    public static final int HEIGHT = 600;
    
    // Configuración del juego
    private GameState gameState = GameState.MENU;
    private Difficulty difficulty = Difficulty.NORMAL;
    private World world = World.CLOUD_KINGDOM;
    
    // Componentes del juego
    private javax.swing.Timer gameTimer;
    private Aero player;
    private LevelManager levelManager;
    private SoundManager soundManager;
    
    // Listas de objetos del juego
    private ArrayList<Obstacle> obstacles;
    private ArrayList<Enemy> enemies;
    private ArrayList<Projectile> projectiles;
    private ArrayList<PowerUp> powerUps;
    private ArrayList<Cloud> clouds;
    private ArrayList<WorldFeature> worldFeatures;
    
    // Sistema de puntuación
    private int score;
    private int highScore;
    private int distance;
    private long gameStartTime;
    
    // Velocidad de desplazamiento
    private double scrollSpeed;
    private double baseScrollSpeed;
    
    // Sistema de generación
    private int lastObstacleX = WIDTH;
    private int lastEnemyX = WIDTH;
    private int lastPowerUpX = WIDTH;
    private int lastCloudX = WIDTH;
    
    // Configuración de sonido
    private boolean soundEnabled = true;
    private float volume = 0.7f;
    private double renderScale = 1.0;
    
    // Control de teclas
    private boolean leftPressed, rightPressed, upPressed, downPressed, spacePressed;
    
    public SkyRunnerGame() {
        setPreferredSize(new Dimension((int)(WIDTH * renderScale), (int)(HEIGHT * renderScale)));
        setBackground(new Color(135, 206, 235));
        setFocusable(true);
        addKeyListener(this);
        
        initGame();
    }

    public void setRenderScale(double scale) {
        this.renderScale = Math.max(1.0, Math.min(2.5, scale));
        setPreferredSize(new Dimension((int)(WIDTH * renderScale), (int)(HEIGHT * renderScale)));
        revalidate();
    }
    
    private void initGame() {
        // Inicializar componentes
        player = new Aero(WIDTH / 4, HEIGHT / 2);
        levelManager = new LevelManager(difficulty.name(), world.name());
        soundManager = new SoundManager();
        
        // Inicializar listas
        obstacles = new ArrayList<>();
        enemies = new ArrayList<>();
        projectiles = new ArrayList<>();
        powerUps = new ArrayList<>();
        clouds = new ArrayList<>();
        worldFeatures = new ArrayList<>();
        
        // Configurar velocidad base
        baseScrollSpeed = levelManager.getCurrentLevel().getScrollSpeed();
        scrollSpeed = baseScrollSpeed;
        
        // Inicializar puntuación
        score = 0;
        distance = 0;
        gameStartTime = System.currentTimeMillis();
        
        // Timer del juego
        gameTimer = new javax.swing.Timer(16, this); // ~60 FPS
        
        // Generar nubes iniciales
        generateInitialClouds();
        // Generar elementos emblemáticos del mundo
        generateInitialWorldFeatures();
    }
    
    private void generateInitialClouds() {
        for (int i = 0; i < 8; i++) {
            int x = (int)(Math.random() * WIDTH * 2);
            int y = (int)(Math.random() * HEIGHT * 0.7);
            clouds.add(new Cloud(x, y, 1));
        }
    }

    private void generateInitialWorldFeatures() {
        Color themeColor;
        switch (world) {
            case CLOUD_KINGDOM: themeColor = new Color(139, 90, 60, 180); break; // rocas
            case CRYSTAL_CANYON: themeColor = new Color(120, 180, 255, 200); break; // cristal
            case FLOATING_CITY: themeColor = new Color(140, 150, 180, 200); break; // edificios
            default: themeColor = new Color(150, 150, 150, 180);
        }
        for (int i = 0; i < 6; i++) {
            int x = (int)(Math.random() * WIDTH);
            int baseY = HEIGHT - 100 - (int)(Math.random() * 120);
            addWorldFeature(x, baseY, themeColor, true);
        }
    }
    
    @Override
    public void actionPerformed(ActionEvent e) {
        if (gameState == GameState.PLAYING) {
            updateGame();
            repaint();
        }
    }
    
    private void updateGame() {
        // Actualizar jugador
        updatePlayer();
        player.setLives(levelManager.getLives());
        
        // Actualizar nivel
        distance += scrollSpeed;
        levelManager.update((int)scrollSpeed);
        
        // Generar objetos
        generateGameObjects();
        
        // Actualizar objetos
        updateObjects();
        
        // Verificar colisiones
        checkCollisions();
        
        // Limpiar objetos fuera de pantalla
        cleanupObjects();
        
        // Actualizar puntuación
        updateScore();
        
        // Verificar condiciones de victoria/derrota
        checkGameConditions();
        
        // Actualizar sonidos
        updateSounds();
    }
    
    private void updatePlayer() {
        // Movimiento del jugador
        if (leftPressed) player.moveLeft();
        if (rightPressed) player.moveRight();
        if (upPressed) player.moveUp();
        if (downPressed) player.moveDown();
        
        // Disparar
        if (spacePressed) {
            Projectile projectile = player.shoot();
            if (projectile != null) {
                projectiles.add(projectile);
                if (soundEnabled) {
                    soundManager.playShootSound();
                }
            }
        }
        
        // Actualizar jugador
        player.update();
        
        // Aplicar efecto de turbo
        if (player.isTurboActive()) {
            scrollSpeed = baseScrollSpeed * 1.5;
        } else {
            scrollSpeed = baseScrollSpeed;
        }
    }
    
    private void generateGameObjects() {
        Level currentLevel = levelManager.getCurrentLevel();
        double completion = currentLevel.getCompletionPercentage();
        int levelNum = currentLevel.getLevelNumber();
        
        // Generar nubes (sin multiplicador de dificultad)
        double obstacleEnemyMultiplier;
        if (difficulty == Difficulty.HARD) {
            obstacleEnemyMultiplier = completion >= 50.0 ? 14.0 : 8.0;
        } else {
            obstacleEnemyMultiplier = completion >= 50.0 ? 3.0 : 1.0;
        }
        if (Math.random() < 0.02) {
            int y = (int)(Math.random() * HEIGHT * 0.7);
            clouds.add(new Cloud(WIDTH + 100, y, 1));
        }
        
        // Generar obstáculos
        if (world == World.FLOATING_CITY) {
            // Generación constante de edificios en Ciudad Flotante
            if (lastObstacleX < WIDTH - 220) {
                int spawnX = WIDTH + 100;
                Obstacle building = (Obstacle) levelManager.generateObstacle(spawnX, 0);
                obstacles.add(building);
                lastObstacleX = spawnX;
            }
        } else {
            if (Math.random() < currentLevel.getObstacleSpawnRate() * obstacleEnemyMultiplier && 
                lastObstacleX < WIDTH - 200) {
                int spawnX = WIDTH + 100;
                int y1 = (int)(Math.random() * (HEIGHT - 150)) + 50;
                Obstacle o1 = (Obstacle) levelManager.generateObstacle(spawnX, y1);
                if (o1.getType() == Obstacle.Type.ELECTRIC_STORM) {
                    int SAFE_TOP = 120;
                    if (y1 < SAFE_TOP) {
                        y1 = SAFE_TOP;
                        o1.y = y1;
                    }
                }
                obstacles.add(o1);
                if (levelNum == 1 && completion >= 50.0) {
                    if (Math.random() < 0.4) {
                        int y2 = (int)(Math.random() * (HEIGHT - 150)) + 50;
                        if (Math.abs(y2 - y1) < 60) {
                            y2 = Math.min(HEIGHT - 100, y1 + 80);
                        }
                        Obstacle o2 = (Obstacle) levelManager.generateObstacle(spawnX, y2);
                        if (o2.getType() == Obstacle.Type.ELECTRIC_STORM) {
                            int SAFE_TOP = 120;
                            if (y2 < SAFE_TOP) {
                                y2 = SAFE_TOP;
                                o2.y = y2;
                            }
                        }
                        obstacles.add(o2);
                    }
                }
                lastObstacleX = spawnX;
            }
        }
        
        // Generar enemigos
        double enemyRate = currentLevel.getEnemySpawnRate() * obstacleEnemyMultiplier;
        int maxAllowedEnemies = Math.max(currentLevel.getMaxEnemies(), (int)(currentLevel.getMaxEnemies() * obstacleEnemyMultiplier));
        if (Math.random() < enemyRate && 
            enemies.size() < maxAllowedEnemies &&
            lastEnemyX < WIDTH - 300) {
            int y = (int)(Math.random() * (HEIGHT - 150)) + 50;
            enemies.add(levelManager.generateEnemy(WIDTH + 100, y));
            lastEnemyX = WIDTH + 100;
        }
        
        // Generar power-ups
        if (Math.random() < currentLevel.getPowerUpSpawnRate() && 
            lastPowerUpX < WIDTH - 400) {
            int y = (int)(Math.random() * (HEIGHT - 150)) + 50;
            PowerUp pu = levelManager.generatePowerUp(WIDTH + 100, y);
            if (pu != null) {
                powerUps.add(pu);
            }
            lastPowerUpX = WIDTH + 100;
        }

        // Generar elementos de fondo temáticos ocasionalmente
        if (Math.random() < 0.02) {
            Color themeColor;
            switch (world) {
                case CLOUD_KINGDOM: themeColor = new Color(139, 90, 60, 180); break;
                case CRYSTAL_CANYON: themeColor = new Color(120, 180, 255, 200); break;
                case FLOATING_CITY: themeColor = new Color(140, 150, 180, 200); break;
                default: themeColor = new Color(150, 150, 150, 180);
            }
            addWorldFeature(WIDTH + 120, HEIGHT - 120 - (int)(Math.random() * 100), themeColor, false);
        }
        
        // Actualizar posiciones de generación
        lastObstacleX -= scrollSpeed;
        lastEnemyX -= scrollSpeed;
        lastPowerUpX -= scrollSpeed;
    }
    
    private void updateObjects() {
        // Actualizar nubes
        for (Cloud cloud : clouds) {
            cloud.update();
            cloud.x -= scrollSpeed * 0.3; // Las nubes se mueven más lento
        }

        // Actualizar elementos del mundo (parallax)
        for (WorldFeature wf : worldFeatures) {
            wf.update(scrollSpeed);
        }
        
        // Actualizar obstáculos
        for (Obstacle obstacle : obstacles) {
            obstacle.update();
            obstacle.x -= scrollSpeed;
        }
        for (Obstacle obstacle : obstacles) {
            if (obstacle.getType() == Obstacle.Type.TURBINE) {
                Rectangle pb = player.getBounds();
                Rectangle ob = obstacle.getBounds();
                int px = pb.x + pb.width / 2;
                int py = pb.y + pb.height / 2;
                int ox = ob.x + ob.width / 2;
                int oy = ob.y + ob.height / 2;
                int dx = px - ox;
                int dy = py - oy;
                int r = 120;
                if (dx * dx + dy * dy <= r * r) {
                    player.applySlow(0.35, 20);
                }
            }
        }
        
        // Actualizar enemigos
        for (Enemy enemy : enemies) {
            enemy.update();
            enemy.x -= scrollSpeed;
            
            if (enemy.canShoot()) {
                double shootChance = 0.02;
                double completionPct = levelManager.getCurrentLevel().getCompletionPercentage();
                if (difficulty == Difficulty.HARD) {
                    shootChance = completionPct >= 50.0 ? 0.04 : 0.03;
                } else if (levelManager.getCurrentLevel().getLevelNumber() == 1 && completionPct >= 50.0) {
                    shootChance = 0.025;
                }
                if (Math.random() < shootChance) {
                    Projectile enemyProjectile = enemy.shoot();
                    if (enemyProjectile != null) {
                        projectiles.add(enemyProjectile);
                        if (difficulty == Difficulty.HARD && completionPct >= 50.0) {
                            projectiles.add(new Projectile(enemy.getX() - 10, enemy.getY() + enemy.getHeight()/2 - 6, -8, true));
                            projectiles.add(new Projectile(enemy.getX() - 10, enemy.getY() + enemy.getHeight()/2 + 6, -9, true));
                        }
                    }
                }
            }
        }
        
        // Actualizar proyectiles
        for (Projectile projectile : projectiles) {
            projectile.update();
            if (projectile.isEnemyProjectile()) {
                projectile.x -= scrollSpeed - 3; // Proyectiles enemigos
            } else {
                projectile.x += 5; // Proyectiles del jugador
            }
        }
        
        // Actualizar power-ups
        for (PowerUp powerUp : powerUps) {
            powerUp.update();
            powerUp.x -= scrollSpeed;
        }
    }
    
    private void checkCollisions() {
        Rectangle playerBounds = player.getBounds();
        
        for (Obstacle obstacle : obstacles) {
            if (obstacle.getType() == Obstacle.Type.ELECTRIC_STORM) {
                Rectangle col = obstacle.getLightningColumnBounds(HEIGHT);
                if (col != null && col.intersects(playerBounds) && !player.isInvulnerable()) {
                    if (player.applyDamage(34)) {
                        levelManager.loseLife();
                        player.setLives(levelManager.getLives());
                        player.healToFull();
                    }
                    player.setInvulnerable(60);
                    if (soundEnabled) {
                        soundManager.playExplosionSound();
                    }
                }
            }
        }
        
        // Colisiones con obstáculos
        for (int i = obstacles.size() - 1; i >= 0; i--) {
            Obstacle obstacle = obstacles.get(i);
            if (obstacle.getBounds().intersects(playerBounds)) {
                if (obstacle.isHarmful() && !player.isInvulnerable()) {
                    if (player.applyDamage(34)) {
                        levelManager.loseLife();
                        player.setLives(levelManager.getLives());
                        player.healToFull();
                    }
                    
                    if (soundEnabled) {
                        soundManager.playExplosionSound();
                    }
                    
                    // Aplicar efecto del obstáculo
                    obstacle.applyEffect(player);
                    
                    // Hacer invulnerable temporalmente
                    player.setInvulnerable(60); // 60 frames
                    
                    // Romper el obstáculo si es destructible
                    if (obstacle.getType() == Obstacle.Type.ROCK_TOWER) {
                        obstacles.remove(i);
                        score += 50; // Bonus por destruir obstáculo
                    }
                }
            }
        }
        
        // Colisiones con enemigos
        for (int i = enemies.size() - 1; i >= 0; i--) {
            Enemy enemy = enemies.get(i);
            if (enemy.getBounds().intersects(playerBounds) && !player.isInvulnerable()) {
                if (player.applyDamage(34)) {
                    levelManager.loseLife();
                    player.setLives(levelManager.getLives());
                    player.healToFull();
                }
                
                if (soundEnabled) {
                    soundManager.playExplosionSound();
                }
            }
            
        }
        
        // Colisiones con proyectiles enemigos
        for (int i = projectiles.size() - 1; i >= 0; i--) {
            Projectile projectile = projectiles.get(i);
            if (projectile.isEnemyProjectile() && 
                projectile.getBounds().intersects(playerBounds) && 
                !player.isInvulnerable()) {
                if (player.applyDamage(34)) {
                    levelManager.loseLife();
                    player.setLives(levelManager.getLives());
                    player.healToFull();
                }
                projectiles.remove(i);
                
                if (soundEnabled) {
                    soundManager.playExplosionSound();
                }
            }
        }
        
        // Colisiones de proyectiles del jugador con enemigos
        for (int i = projectiles.size() - 1; i >= 0; i--) {
            Projectile projectile = projectiles.get(i);
            if (!projectile.isEnemyProjectile()) {
                for (int j = enemies.size() - 1; j >= 0; j--) {
                    Enemy enemy = enemies.get(j);
                    if (projectile.getBounds().intersects(enemy.getBounds())) {
                        enemy.takeDamage(50);
                        projectiles.remove(i);
                        
                        if (enemy.getHealth() <= 0) {
                            enemies.remove(j);
                            score += 100;
                            levelManager.enemyDefeated();
                            
                            if (soundEnabled) {
                                soundManager.playExplosionSound();
                            }
                        }
                        break;
                    }
                }
            }
        }
        
        // Colisiones con power-ups
        for (int i = powerUps.size() - 1; i >= 0; i--) {
            PowerUp powerUp = powerUps.get(i);
            if (powerUp.getBounds().intersects(playerBounds)) {
                powerUp.applyEffect(player);
                if (powerUp.type == PowerUp.Type.HEALTH) {
                    levelManager.addLife();
                    player.setLives(levelManager.getLives());
                    player.healToFull();
                }
                score += 50;
                powerUps.remove(i);
                
                if (soundEnabled) {
                    soundManager.playPowerUpSound();
                }
            }
        }
    }
    
    private void cleanupObjects() {
        // Limpiar nubes
        clouds.removeIf(cloud -> cloud.x < -200);

        // Limpiar elementos del mundo
        worldFeatures.removeIf(WorldFeature::isOffScreen);
        
        // Limpiar obstáculos
        obstacles.removeIf(obstacle -> obstacle.x < -200);
        
        // Limpiar enemigos
        enemies.removeIf(enemy -> enemy.x < -200 || enemy.getHealth() <= 0);
        
        // Limpiar proyectiles
        projectiles.removeIf(projectile -> projectile.x < -50 || projectile.x > WIDTH + 50);
        
        // Limpiar power-ups
        powerUps.removeIf(powerUp -> powerUp.x < -100);
    }
    
    private void updateScore() {
        // Puntuación por distancia recorrida
        score += (int)(scrollSpeed * 0.5);
        
        // Puntuación por esquivar obstáculos
        for (GameObject obstacle : obstacles) {
            if (obstacle.x < -100 && obstacle.isActive()) {
                score += 10;
                levelManager.obstaclePassed();
                obstacle.setActive(false);
            }
        }
    }
    
    private void checkGameConditions() {
        // Verificar Game Over
        if (player.getHealth() <= 0 || levelManager.isGameOver()) {
            gameState = GameState.GAME_OVER;
            gameTimer.stop();
            
            try { soundManager.stopBackgroundMusic(); } catch (Exception ignored) {}
            
            if (soundEnabled) {
                soundManager.playGameOverSound();
            }
        }
        
        // Verificar avance de nivel o fin del juego
        Level cur = levelManager.getCurrentLevel();
        if (cur.isCompleted()) {
            boolean advanced = levelManager.advanceLevel();
            if (advanced) {
                obstacles.clear();
                enemies.clear();
                projectiles.clear();
                powerUps.clear();
                clouds.clear();
                worldFeatures.clear();
                lastObstacleX = WIDTH;
                lastEnemyX = WIDTH;
                lastPowerUpX = WIDTH;
                baseScrollSpeed = levelManager.getCurrentLevel().getScrollSpeed();
                scrollSpeed = baseScrollSpeed;
                generateInitialWorldFeatures();
                // Música: solo nivel 1 tiene "ultimate battle"; no reiniciamos aquí
            } else if (levelManager.isGameComplete()) {
                gameTimer.stop();
                
                try { soundManager.stopBackgroundMusic(); } catch (Exception ignored) {}
                java.awt.Window w = javax.swing.SwingUtilities.getWindowAncestor(this);
                if (w != null) w.dispose();
                new MainMenu().setVisible(true);
                return;
            }
        }
        
        // Avanzar al siguiente nivel
        
    }
    
    private void updateSounds() {
        // Actualizar sonido del motor
        if (soundEnabled && gameState == GameState.PLAYING) {
            soundManager.updateEngineSound(player.isTurboActive());
        }
    }
    
    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        Graphics2D g2d = (Graphics2D) g.create();
        g2d.scale(renderScale, renderScale);
        
        // Dibujar fondo según el mundo
        drawBackground(g2d);
        
        if (gameState == GameState.PLAYING || gameState == GameState.PAUSED) {
            drawGame(g2d);
            drawUI(g2d);
        } else if (gameState == GameState.GAME_OVER) {
            drawGameOver(g2d);
        } else if (gameState == GameState.VICTORY) {
            drawVictory(g2d);
        }
        g2d.dispose();
    }
    
    private void drawBackground(Graphics g) {
        Level currentLevel = levelManager.getCurrentLevel();
        g.setColor(currentLevel.getBackgroundColor());
        g.fillRect(0, 0, WIDTH, HEIGHT);
        
        // Dibujar elementos emblemáticos del mundo detrás de las nubes
        Graphics2D g2d = (Graphics2D) g;
        for (WorldFeature wf : worldFeatures) {
            wf.draw(g2d);
        }
        
        // Dibujar nubes
        for (Cloud cloud : clouds) {
            cloud.draw(g2d);
        }
    }

    private void addWorldFeature(int x, int baseY, Color themeColor, boolean initial) {
        double parallax = initial ? (0.3 + Math.random() * 0.4) : (0.4 + Math.random() * 0.3);
        float opacity = initial ? 0.35f : 0.45f;
        switch (world) {
            case CLOUD_KINGDOM:
                worldFeatures.add(new WorldFeature(x, baseY - 80, 80, 160,
                        WorldFeature.Type.ROCK_SPIRE, parallax,
                        new Color(themeColor.getRed(), themeColor.getGreen(), themeColor.getBlue(), 220), opacity));
                break;
            case CRYSTAL_CANYON:
                worldFeatures.add(new WorldFeature(x, baseY - 70, 70, 150,
                        WorldFeature.Type.CRYSTAL_SPIRE_BG, parallax,
                        new Color(120, 200, 255, 210), opacity));
                break;
            case FLOATING_CITY:
                // Alternar entre torres y turbinas
                if (Math.random() < 0.6) {
                    worldFeatures.add(new WorldFeature(x, baseY - 100, 60 + (int)(Math.random()*40), 160,
                            WorldFeature.Type.CITY_TOWER, parallax,
                            new Color(130, 140, 170, 220), opacity));
                } else {
                    worldFeatures.add(new WorldFeature(x, baseY - 60, 60, 120,
                            WorldFeature.Type.CITY_TURBINE_BG, parallax,
                            new Color(170, 180, 200, 220), opacity));
                }
                break;
        }
    }
    
    private void drawGame(Graphics g) {
        // Dibujar obstáculos
        for (Obstacle obstacle : obstacles) {
            obstacle.draw((Graphics2D)g);
        }
        
        // Dibujar enemigos
        for (Enemy enemy : enemies) {
            enemy.draw((Graphics2D)g);
        }
        
        // Dibujar proyectiles
        for (Projectile projectile : projectiles) {
            projectile.draw((Graphics2D)g);
        }
        
        // Dibujar power-ups
        for (PowerUp powerUp : powerUps) {
            powerUp.draw((Graphics2D)g);
        }
        
        // Dibujar jugador
        player.draw((Graphics2D)g);
    }
    
    private void drawUI(Graphics g) {
        g.setColor(Color.WHITE);
        g.setFont(new Font("Arial", Font.BOLD, 16));
        
        // Información del nivel
        g.drawString(levelManager.getProgressInfo(), 10, 25);
        
        // Puntuación
        g.drawString("Puntuación: " + score, 10, 50);
        
        // Vidas
        g.drawString("Vidas: " + levelManager.getLives(), 10, 75);
        
        int barX = 10;
        int barY = 85;
        int barW = 200;
        int barH = 10;
        g.setColor(Color.DARK_GRAY);
        g.fillRect(barX, barY, barW, barH);
        int segments = 3;
        int gap = 4;
        int segW = (barW - (segments - 1) * gap) / segments;
        int filled = Math.max(0, Math.min(segments, levelManager.getLives()));
        g.setColor(Color.GREEN);
        for (int i = 0; i < filled; i++) {
            int sx = barX + i * (segW + gap);
            g.fillRect(sx, barY, segW, barH);
        }
        g.setColor(new Color(120, 0, 0));
        for (int i = filled; i < segments; i++) {
            int sx = barX + i * (segW + gap);
            g.fillRect(sx, barY, segW, barH);
        }
        g.setColor(Color.WHITE);
        g.drawRect(barX, barY, barW, barH);
        for (int i = 1; i < segments; i++) {
            int dx = barX + i * (segW + gap) - gap/2;
            g.drawLine(dx, barY, dx, barY + barH);
        }
        
        // Barra de turbo
        if (player.getTurboDuration() > 0) {
            g.setColor(new Color(255, 165, 0));
            g.fillRect(10, 100, (int)(200 * player.getTurboDuration() / 300.0), 5);
            g.setColor(Color.WHITE);
            g.drawRect(10, 100, 200, 5);
        }
        
        // Indicador de turbo activo
        if (player.isTurboActive()) {
            g.setColor(Color.YELLOW);
            g.setFont(new Font("Arial", Font.BOLD, 20));
            g.drawString("¡TURBO!", WIDTH - 100, 30);
        }
        
        // Pausa
        if (gameState == GameState.PAUSED) {
            g.setColor(new Color(0, 0, 0, 150));
            g.fillRect(0, 0, WIDTH, HEIGHT);
            g.setColor(Color.WHITE);
            g.setFont(new Font("Arial", Font.BOLD, 48));
            String pausedText = "PAUSADO";
            int textWidth = g.getFontMetrics().stringWidth(pausedText);
            g.drawString(pausedText, (WIDTH - textWidth) / 2, HEIGHT / 2);
            
            g.setFont(new Font("Arial", Font.PLAIN, 24));
            String continueText = "Presiona ESC para continuar";
            textWidth = g.getFontMetrics().stringWidth(continueText);
            g.drawString(continueText, (WIDTH - textWidth) / 2, HEIGHT / 2 + 50);
            String menuText = "Presiona M para volver al menú";
            textWidth = g.getFontMetrics().stringWidth(menuText);
            g.drawString(menuText, (WIDTH - textWidth) / 2, HEIGHT / 2 + 85);
        }
    }
    
    private void drawGameOver(Graphics g) {
        g.setColor(new Color(0, 0, 0, 200));
        g.fillRect(0, 0, WIDTH, HEIGHT);
        
        g.setColor(Color.RED);
        g.setFont(new Font("Arial", Font.BOLD, 48));
        String gameOverText = "GAME OVER";
        int textWidth = g.getFontMetrics().stringWidth(gameOverText);
        g.drawString(gameOverText, (WIDTH - textWidth) / 2, HEIGHT / 2 - 100);
        
        g.setColor(Color.WHITE);
        g.setFont(new Font("Arial", Font.PLAIN, 24));
        g.drawString("Puntuación Final: " + (score + levelManager.getTotalScore()), 
                    (WIDTH - 200) / 2, HEIGHT / 2 - 50);
        g.drawString("Nivel Alcanzado: " + (levelManager.getCurrentLevelIndex() + 1), 
                    (WIDTH - 200) / 2, HEIGHT / 2 - 20);
        
        g.setFont(new Font("Arial", Font.PLAIN, 18));
        g.drawString("Presiona ENTER para volver al menú", 
                    (WIDTH - 300) / 2, HEIGHT / 2 + 50);
        g.drawString("Presiona R para reiniciar", 
                    (WIDTH - 200) / 2, HEIGHT / 2 + 80);
    }
    
    private void drawVictory(Graphics g) {
        g.setColor(new Color(0, 100, 0, 200));
        g.fillRect(0, 0, WIDTH, HEIGHT);
        
        g.setColor(Color.YELLOW);
        g.setFont(new Font("Arial", Font.BOLD, 48));
        String victoryText = "¡VICTORIA!";
        int textWidth = g.getFontMetrics().stringWidth(victoryText);
        g.drawString(victoryText, (WIDTH - textWidth) / 2, HEIGHT / 2 - 100);
        
        g.setColor(Color.WHITE);
        g.setFont(new Font("Arial", Font.PLAIN, 24));
        g.drawString("¡Has completado todos los niveles!", 
                    (WIDTH - 350) / 2, HEIGHT / 2 - 50);
        g.drawString("Puntuación Total: " + levelManager.getFinalScore(), 
                    (WIDTH - 200) / 2, HEIGHT / 2 - 20);
        
        g.setFont(new Font("Arial", Font.PLAIN, 18));
        g.drawString(levelManager.getFinalStats(), 
                    (WIDTH - 400) / 2, HEIGHT / 2 + 30);
        
        g.drawString("Presiona ENTER para volver al menú", 
                    (WIDTH - 300) / 2, HEIGHT / 2 + 100);
    }
    
    // Métodos de control
    @Override
    public void keyPressed(KeyEvent e) {
        int keyCode = e.getKeyCode();
        
        switch (gameState) {
            case MENU:
                if (keyCode == KeyEvent.VK_ENTER) {
                    startGame();
                }
                break;
                
            case PLAYING:
                handleGameKeyPress(keyCode);
                break;
                
            case PAUSED:
                if (keyCode == KeyEvent.VK_ESCAPE) {
                    gameState = GameState.PLAYING;
                } else if (keyCode == KeyEvent.VK_M) {
                    returnToMenu();
                }
                break;
                
            case GAME_OVER:
            case VICTORY:
                handleEndGameKeyPress(keyCode);
                break;
        }
    }
    
    private void handleGameKeyPress(int keyCode) {
        switch (keyCode) {
            case KeyEvent.VK_LEFT:
                leftPressed = true;
                break;
            case KeyEvent.VK_RIGHT:
                rightPressed = true;
                break;
            case KeyEvent.VK_UP:
                upPressed = true;
                break;
            case KeyEvent.VK_DOWN:
                downPressed = true;
                break;
            case KeyEvent.VK_SPACE:
                spacePressed = true;
                break;
            case KeyEvent.VK_ESCAPE:
                gameState = GameState.PAUSED;
                break;
        }
    }
    
    private void handleEndGameKeyPress(int keyCode) {
        switch (keyCode) {
            case KeyEvent.VK_ENTER:
                returnToMenu();
                break;
            case KeyEvent.VK_R:
                restartGame();
                break;
        }
    }
    
    @Override
    public void keyReleased(KeyEvent e) {
        int keyCode = e.getKeyCode();
        
        switch (keyCode) {
            case KeyEvent.VK_LEFT:
                leftPressed = false;
                break;
            case KeyEvent.VK_RIGHT:
                rightPressed = false;
                break;
            case KeyEvent.VK_UP:
                upPressed = false;
                break;
            case KeyEvent.VK_DOWN:
                downPressed = false;
                break;
            case KeyEvent.VK_SPACE:
                spacePressed = false;
                break;
        }
    }
    
    @Override
    public void keyTyped(KeyEvent e) {
        // No se usa
    }
    
    // Métodos de configuración
    public void setDifficulty(String difficulty) {
        this.difficulty = Difficulty.valueOf(difficulty);
    }
    
    public void setWorld(String world) {
        this.world = World.valueOf(world);
    }
    
    public void setSoundEnabled(boolean enabled) {
        this.soundEnabled = enabled;
        soundManager.setSoundEnabled(enabled);
    }
    
    public void setVolume(float volume) {
        this.volume = volume;
        soundManager.setVolume(volume);
    }
    
    // Métodos de control del juego
    public void startGame() {
        initGame();
        gameState = GameState.PLAYING;
        gameTimer.start();
        
        if (soundEnabled) {
            try {
                Level cur = levelManager.getCurrentLevel();
                if (cur != null) {
                    soundManager.playWorldMusic(world.name());
                }
            } catch (Exception ignored) {}
        }
    }
    
    public void pauseGame() {
        if (gameState == GameState.PLAYING) {
            gameState = GameState.PAUSED;
        } else if (gameState == GameState.PAUSED) {
            gameState = GameState.PLAYING;
        }
    }
    
    public void restartGame() {
        gameTimer.stop();
        
        try { soundManager.stopBackgroundMusic(); } catch (Exception ignored) {}
        initGame();
        gameState = GameState.PLAYING;
        gameTimer.start();
        
        if (soundEnabled) {
            try {
                Level cur = levelManager.getCurrentLevel();
                if (cur != null) {
                    soundManager.playWorldMusic(world.name());
                }
            } catch (Exception ignored) {}
        }
    }
    
    public void returnToMenu() {
        gameTimer.stop();
        
        try { soundManager.stopBackgroundMusic(); } catch (Exception ignored) {}
        
        // Cerrar ventana actual
        Window window = SwingUtilities.getWindowAncestor(this);
        if (window != null) {
            window.dispose();
        }
        
        // Volver al menú principal
        SwingUtilities.invokeLater(() -> {
            new MainMenu().setVisible(true);
        });
    }
    
    // Getters
    public GameState getGameState() { return gameState; }
    public int getScore() { return score; }
    public int getDistance() { return distance; }
    public Aero getPlayer() { return player; }
    public LevelManager getLevelManager() { return levelManager; }
    public SoundManager getSoundManager() { return soundManager; }
}
