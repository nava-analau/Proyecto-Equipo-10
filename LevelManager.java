import java.util.*;

public class LevelManager {
    
    private List<Level> levels;
    private int currentLevelIndex;
    private Level currentLevel;
    private Random random;
    
    // Estadísticas globales
    private int totalScore;
    private int totalEnemiesDefeated;
    private int totalObstaclesPassed;
    private int lives;
    
    public LevelManager(String difficulty, String world) {
        this.random = new Random();
        this.levels = new ArrayList<>();
        this.currentLevelIndex = 0;
        this.totalScore = 0;
        this.totalEnemiesDefeated = 0;
        this.totalObstaclesPassed = 0;
        this.lives = 3; // Vidas iniciales
        
        generateLevels(difficulty, world);
        currentLevel = levels.get(0);
    }
    
    private void generateLevels(String difficulty, String world) {
        int baseLength = 5000;
        int baseTargetScore = 1200;
        if (difficulty.equals("EASY")) { baseLength = 4000; baseTargetScore = 800; }
        else if (difficulty.equals("HARD")) { baseLength = 6000; baseTargetScore = 1500; }
        // Un solo nivel por mundo
        String levelName = getLevelName(world, 0);
        Level level = new Level(levelName, world, difficulty, 1, baseLength, baseTargetScore);
        levels.add(level);
    }
    
    private String getLevelName(String world, int levelIndex) {
        String[] levelNames;
        
        if (world.equals("CLOUD_KINGDOM")) {
            levelNames = new String[]{
                "Entrada al Reino",
                "Valle de las Nubes",
                "Torres Flotantes",
                "Cielo Tormentoso",
                "Trono Celestial"
            };
        } else if (world.equals("CRYSTAL_CANYON")) {
            levelNames = new String[]{
                "Desfiladero de Cristal",
                "Cavernas Brillantes",
                "Puentes de Luz",
                "Abismo de Gemas",
                "Corazón del Cañón"
            };
        } else if (world.equals("FLOATING_CITY")) {
            levelNames = new String[]{
                "Afueras de la Ciudad",
                "Distrito Industrial",
                "Zona de Turbinas",
                "Centro de Control",
                "Núcleo Flotante"
            };
        } else {
            levelNames = new String[]{
                "Nivel 1", "Nivel 2", "Nivel 3", "Nivel 4", "Nivel 5"
            };
        }
        
        return levelNames[levelIndex];
    }
    
    // Métodos de gestión de niveles
    public boolean advanceLevel() {
        if (currentLevelIndex < levels.size() - 1) {
            // Sumar puntuación del nivel completado
            totalScore += currentLevel.calculateScore();
            totalEnemiesDefeated += currentLevel.getEnemiesDefeated();
            totalObstaclesPassed += currentLevel.getObstaclesPassed();
            
            currentLevelIndex++;
            currentLevel = levels.get(currentLevelIndex);
            return true;
        }
        return false; // No hay más niveles
    }
    
    public boolean isGameComplete() {
        return currentLevelIndex >= levels.size() - 1 && currentLevel.isCompleted();
    }
    
    // Métodos de generación de objetos del juego
    public GameObject generateObstacle(int x, int y) {
        Level.ObstacleType levelType = currentLevel.getRandomObstacleType();
        Obstacle.Type type;
        
        switch (levelType) {
            case ROCK_TOWER:
                type = Obstacle.Type.ROCK_TOWER;
                return new Obstacle(x, y, type);
            case ELECTRIC_STORM:
                type = Obstacle.Type.ELECTRIC_STORM;
                return new Obstacle(x, y, type);
            case TURBINE:
                type = Obstacle.Type.TURBINE;
                return new Obstacle(x, y, type);
            case CLOUD_KINGDOM_TOWER:
                type = Obstacle.Type.CLOUD_KINGDOM_TOWER;
                return new Obstacle(x, y, type);
            case CRYSTAL_SPIKE:
                type = Obstacle.Type.CRYSTAL_SPIKE;
                return new Obstacle(x, y, type);
            case FLOATING_PLATFORM:
                type = Obstacle.Type.FLOATING_PLATFORM;
                return new Obstacle(x, y, type);
            case CITY_BUILDING:
                type = Obstacle.Type.CITY_BUILDING;
                // Generación especial: edificios desde arriba o abajo con alturas distintas
                int margin = 80;
                boolean fromBottom = Math.random() < 0.5;
                int width = 70;
                int maxH = SkyRunnerGame.HEIGHT - margin;
                int minH = 120;
                int height = Math.max(minH, Math.min(maxH - 60, minH + (int)(Math.random() * (maxH - minH))));
                int yy = fromBottom ? SkyRunnerGame.HEIGHT - height : 0;
                // Asegurar margen según origen
                if (!fromBottom && height > SkyRunnerGame.HEIGHT - margin) {
                    height = SkyRunnerGame.HEIGHT - margin;
                }
                if (fromBottom && yy < margin) {
                    yy = margin;
                    height = SkyRunnerGame.HEIGHT - yy;
                }
                return new Obstacle(x, width, height, yy, fromBottom, type);
            default:
                return new Obstacle(x, y, Obstacle.Type.ROCK_TOWER);
        }
    }
    
    public Enemy generateEnemy(int x, int y) {
        Level.EnemyType levelType = currentLevel.getRandomEnemyType();
        double speed;
        int health;
        
        switch (levelType) {
            case BASIC_FIGHTER:
                speed = 1.0;
                health = 100;
                break;
            case ADVANCED_FIGHTER:
                speed = 1.5;
                health = 150;
                break;
            case BOSS_FIGHTER:
                speed = 0.8;
                health = 300;
                break;
            default:
                speed = 1.0;
                health = 100;
                break;
        }
        
        return new Enemy(x, y, speed, health);
    }
    
    public PowerUp generatePowerUp(int x, int y) {
        // Distribución por dificultad
        if (currentLevel.getDifficulty().equals("HARD")) {
            double r = Math.random();
            if (r < 0.30) return new PowerUp(x, y, PowerUp.Type.SCORE_BOOST); // 30%
            if (r < 0.55) return new PowerUp(x, y, PowerUp.Type.TURBO);       // +25% = 55%
            if (r < 0.80) return new PowerUp(x, y, PowerUp.Type.SHIELD);      // +25% = 80%
            return new PowerUp(x, y, PowerUp.Type.HEALTH);                    // 20%
        }
        // Otras dificultades: distribución previa
        double r = Math.random();
        if (r < 0.50) return new PowerUp(x, y, PowerUp.Type.SCORE_BOOST);
        if (r < 0.75) return new PowerUp(x, y, PowerUp.Type.TURBO);
        if (r < 0.85) return new PowerUp(x, y, PowerUp.Type.SHIELD);
        if (r < 0.90) return new PowerUp(x, y, PowerUp.Type.HEALTH);
        return null;
    }
    
    // Métodos de estado
    public void loseLife() {
        lives--;
    }
    public void addLife() {
        if (lives < 3) lives++;
    }
    
    public boolean isGameOver() {
        return lives <= 0;
    }
    
    // Métodos de actualización
    public void update(int distance) {
        currentLevel.updateProgress(distance);
    }
    
    public void enemyDefeated() {
        currentLevel.enemyDefeated();
    }
    
    public void obstaclePassed() {
        currentLevel.obstaclePassed();
    }
    
    // Getters
    public Level getCurrentLevel() { return currentLevel; }
    public int getCurrentLevelIndex() { return currentLevelIndex; }
    public int getTotalScore() { return totalScore; }
    public int getTotalEnemiesDefeated() { return totalEnemiesDefeated; }
    public int getTotalObstaclesPassed() { return totalObstaclesPassed; }
    public int getLives() { return lives; }
    public List<Level> getLevels() { return levels; }
    
    // Información del progreso
    public String getProgressInfo() {
        return String.format("Nivel %d: %s (%.1f%%)", 
                           currentLevelIndex + 1, 
                           currentLevel.getName(), 
                           currentLevel.getCompletionPercentage());
    }
    
    public int getFinalScore() {
        if (isGameComplete()) {
            return totalScore + currentLevel.calculateScore();
        }
        return totalScore;
    }
    
    public String getFinalStats() {
        return String.format(
            "Puntuación Final: %d\n" +
            "Enemigos Derrotados: %d\n" +
            "Obstáculos Esquivados: %d\n" +
            "Niveles Completados: %d/%d\n" +
            "Dificultad: %s",
            getFinalScore(),
            totalEnemiesDefeated,
            totalObstaclesPassed,
            currentLevelIndex + 1,
            levels.size(),
            currentLevel.getDifficulty()
        );
    }
}
