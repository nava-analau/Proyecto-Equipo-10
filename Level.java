import java.awt.Color;

public class Level {
    
    // Tipos de obstáculos y enemigos
    public enum ObstacleType {
        ROCK_TOWER, ELECTRIC_STORM, TURBINE, CLOUD_KINGDOM_TOWER, CRYSTAL_SPIKE, FLOATING_PLATFORM, CITY_BUILDING
    }
    
    public enum EnemyType {
        BASIC_FIGHTER, ADVANCED_FIGHTER, BOSS_FIGHTER
    }
    
    // Propiedades del nivel
    private String name;
    private String world;
    private String difficulty;
    private int levelNumber;
    private int length; // Longitud del nivel en píxeles
    private int targetScore; // Puntuación objetivo
    private double scrollSpeed;
    private Color backgroundColor;
    private Color cloudColor;
    
    // Configuración de obstáculos
    private double obstacleSpawnRate;
    private double[] obstacleTypeWeights; // Probabilidad de cada tipo
    
    // Configuración de enemigos
    private double enemySpawnRate;
    private double[] enemyTypeWeights;
    private int maxEnemies;
    
    // Configuración de power-ups
    private double powerUpSpawnRate;
    private double turboSpawnRate;
    private double scoreBoostSpawnRate;
    
    // Estado del nivel
    private int currentProgress;
    private boolean completed;
    private int enemiesDefeated;
    private int obstaclesPassed;
    
    public Level(String name, String world, String difficulty, 
                 int levelNumber, int length, int targetScore) {
        this.name = name;
        this.world = world;
        this.difficulty = difficulty;
        this.levelNumber = levelNumber;
        this.length = length;
        this.targetScore = targetScore;
        this.currentProgress = 0;
        this.completed = false;
        this.enemiesDefeated = 0;
        this.obstaclesPassed = 0;
        
        configureLevel();
    }
    
    private void configureLevel() {
        // Configurar velocidad de desplazamiento según dificultad
        if (difficulty.equals("EASY")) {
            scrollSpeed = 1.4;
            obstacleSpawnRate = 0.008;
            enemySpawnRate = 0.003;
            powerUpSpawnRate = 0.004;
            turboSpawnRate = 0.002;
            scoreBoostSpawnRate = 0.002;
            maxEnemies = 8;
        } else if (difficulty.equals("NORMAL")) {
            scrollSpeed = 1.85;
            obstacleSpawnRate = 0.012;
            enemySpawnRate = 0.005;
            powerUpSpawnRate = 0.003;
            turboSpawnRate = 0.0015;
            scoreBoostSpawnRate = 0.0015;
            maxEnemies = 12;
        } else if (difficulty.equals("HARD")) {
            scrollSpeed = 2.35;
            obstacleSpawnRate = 0.018;
            enemySpawnRate = 0.012;
            powerUpSpawnRate = 0.006;
            turboSpawnRate = 0.001;
            scoreBoostSpawnRate = 0.001;
            maxEnemies = 24;
        }
        
        // Configurar colores y pesos según el mundo
        if (world.equals("CLOUD_KINGDOM")) {
            backgroundColor = new Color(135, 206, 235); // Azul cielo
            cloudColor = new Color(255, 255, 255, 180);
            configureCloudKingdomObstacles();
            configureCloudKingdomEnemies();
        } else if (world.equals("CRYSTAL_CANYON")) {
            backgroundColor = new Color(70, 130, 180); // Azul acero
            cloudColor = new Color(200, 200, 255, 150);
            configureCrystalCanyonObstacles();
            configureCrystalCanyonEnemies();
        } else if (world.equals("FLOATING_CITY")) {
            backgroundColor = new Color(100, 100, 150); // Gris azulado
            cloudColor = new Color(150, 150, 200, 120);
            configureFloatingCityObstacles();
            configureFloatingCityEnemies();
        }
    }
    
    private void configureCloudKingdomObstacles() {
        // Reino de las Nubes: torres de roca, tormentas eléctricas, turbinas
        obstacleTypeWeights = new double[]{0.4, 0.3, 0.3, 0.0, 0.0, 0.0};
    }
    
    private void configureCrystalCanyonObstacles() {
        // Cañón de Cristal: cristales, tormentas, plataformas flotantes
        obstacleTypeWeights = new double[]{0.2, 0.3, 0.2, 0.2, 0.1, 0.0};
    }
    
    private void configureFloatingCityObstacles() {
        // Ciudad Flotante: solo edificios
        obstacleTypeWeights = new double[]{0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 1.0};
    }
    
    private void configureCloudKingdomEnemies() {
        // Enemigos básicos en el reino de las nubes
        enemyTypeWeights = new double[]{0.7, 0.3, 0.0};
    }
    
    private void configureCrystalCanyonEnemies() {
        // Enemigos más avanzados en el cañón
        enemyTypeWeights = new double[]{0.4, 0.5, 0.1};
    }
    
    private void configureFloatingCityEnemies() {
        // Enemigos variados en la ciudad flotante
        enemyTypeWeights = new double[]{0.5, 0.4, 0.1};
    }
    
    // Métodos para obtener configuraciones
    public ObstacleType getRandomObstacleType() {
        double random = Math.random();
        double cumulative = 0.0;
        
        for (int i = 0; i < obstacleTypeWeights.length; i++) {
            cumulative += obstacleTypeWeights[i];
            if (random <= cumulative) {
                return ObstacleType.values()[i];
            }
        }
        
        return ObstacleType.ROCK_TOWER; // Valor por defecto
    }
    
    public EnemyType getRandomEnemyType() {
        double random = Math.random();
        double cumulative = 0.0;
        
        for (int i = 0; i < enemyTypeWeights.length; i++) {
            cumulative += enemyTypeWeights[i];
            if (random <= cumulative) {
                return EnemyType.values()[i];
            }
        }
        
        return EnemyType.BASIC_FIGHTER; // Valor por defecto
    }
    
    // Métodos de actualización
    public void updateProgress(int distance) {
        currentProgress += distance;
        if (currentProgress >= length) {
            completed = true;
        }
    }
    
    public void enemyDefeated() {
        enemiesDefeated++;
    }
    
    public void obstaclePassed() {
        obstaclesPassed++;
    }
    
    // Getters
    public String getName() { return name; }
    public String getWorld() { return world; }
    public String getDifficulty() { return difficulty; }
    public int getLevelNumber() { return levelNumber; }
    public int getLength() { return length; }
    public int getTargetScore() { return targetScore; }
    public double getScrollSpeed() { return scrollSpeed; }
    public Color getBackgroundColor() { return backgroundColor; }
    public Color getCloudColor() { return cloudColor; }
    public double getObstacleSpawnRate() { return obstacleSpawnRate; }
    public double getEnemySpawnRate() { return enemySpawnRate; }
    public double getPowerUpSpawnRate() { return powerUpSpawnRate; }
    public double getTurboSpawnRate() { return turboSpawnRate; }
    public double getScoreBoostSpawnRate() { return scoreBoostSpawnRate; }
    public int getMaxEnemies() { return maxEnemies; }
    public int getCurrentProgress() { return currentProgress; }
    public boolean isCompleted() { return completed; }
    public int getEnemiesDefeated() { return enemiesDefeated; }
    public int getObstaclesPassed() { return obstaclesPassed; }
    
    public double getCompletionPercentage() {
        return (double) currentProgress / length * 100.0;
    }
    
    public int calculateScore() {
        int score = 0;
        
        // Puntuación por progreso
        score += currentProgress / 10;
        
        // Puntuación por enemigos derrotados
        score += enemiesDefeated * 100;
        
        // Puntuación por obstáculos esquivados
        score += obstaclesPassed * 50;
        
        // Bonus por completar el nivel
        if (completed) {
            score += targetScore;
            
            // Bonus por dificultad
            if (difficulty.equals("NORMAL")) {
                score += 500;
            } else if (difficulty.equals("HARD")) {
                score += 1000;
            }
        }
        
        return score;
    }
}
