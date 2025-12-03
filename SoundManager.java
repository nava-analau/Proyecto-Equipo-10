import javax.sound.sampled.*;
import java.io.File;
import java.io.IOException;

public class SoundManager {
    // Música de menú con pista dedicada
    public void playMenuMusic() {
        if (!soundEnabled) return;
        String track = bestTrack("menu");
        startMusic(track);
    }
    private static final String SOUND_DIR = "sounds/";
    
    
    // Sonidos del juego
    private Clip engineSound;
    private Clip shootSound;
    private Clip explosionSound;
    private Clip powerUpSound;
    private Clip turboSound;
    private Clip backgroundMusic;
    // Reproductor de música (WAV por defecto, MP3 si está disponible)
    private MusicPlayer musicPlayer = new WavLoopPlayer();
    // Estilo musical preferido: "ambient" busca pistas tranquilas si existen
    private String musicStyle = "ambient";
    
    // Volumen (0.0 a 1.0)
    private float volume = 0.7f;
    private boolean soundEnabled = true;
    private boolean engineLooping = false;
    
    public SoundManager() {
        loadSounds();
    }
    
    private void loadSounds() {
        try {
            // Crear directorio de sonidos si no existe
            File soundDir = new File(SOUND_DIR);
            if (!soundDir.exists()) {
                soundDir.mkdir();
            }
            
            // Cargar sonidos (archivos de ejemplo)
            engineSound = loadSound("engine.wav");
            shootSound = loadSound("shoot.wav");
            explosionSound = loadSound("explosion.wav");
            powerUpSound = loadSound("powerup.wav");
            turboSound = loadSound("turbo.wav");
            backgroundMusic = loadSound("background.wav");
            
        } catch (Exception e) {
            System.out.println("Error al cargar sonidos: " + e.getMessage());
            createDefaultSounds();
        }
    }
    
    private Clip loadSound(String filename) {
        try {
            File soundFile = new File(SOUND_DIR + filename);
            if (!soundFile.exists()) {
                // Para motor y música de fondo, no usar sintético para evitar pitidos
                if ("engine.wav".equals(filename) || "background.wav".equals(filename)) {
                    return null;
                }
                // Para efectos, usar sintético como fallback
                return createSyntheticSound(filename);
            }
            
            AudioInputStream audioStream = AudioSystem.getAudioInputStream(soundFile);
            Clip clip = AudioSystem.getClip();
            clip.open(audioStream);
            return clip;
            
        } catch (UnsupportedAudioFileException | IOException | LineUnavailableException e) {
            System.out.println("Error al cargar " + filename + ": " + e.getMessage());
            return createSyntheticSound(filename);
        }
    }
    
    private Clip createSyntheticSound(String filename) {
        try {
            // Crear sonido sintético básico
            int sampleRate = 44100;
            int duration = 200; // milisegundos
            int numSamples = (int)(sampleRate * duration / 1000.0);
            
            byte[] audioData = new byte[numSamples * 2]; // 16-bit
            
            // Generar onda sinusoidal
            for (int i = 0; i < numSamples; i++) {
                double angle = 2.0 * Math.PI * i / (sampleRate / 440.0); // 440 Hz
                short sample = (short)(Math.sin(angle) * 32767.0 * 0.5);
                audioData[i * 2] = (byte)(sample & 0xFF);
                audioData[i * 2 + 1] = (byte)((sample >> 8) & 0xFF);
            }
            
            AudioFormat format = new AudioFormat(sampleRate, 16, 1, true, false);
            DataLine.Info info = new DataLine.Info(Clip.class, format);
            Clip clip = (Clip)AudioSystem.getLine(info);
            clip.open(format, audioData, 0, audioData.length);
            
            return clip;
            
        } catch (LineUnavailableException e) {
            System.out.println("Error al crear sonido sintético: " + e.getMessage());
            return null;
        }
    }
    
    private void createDefaultSounds() {
        // Crear sonidos por defecto si no se pueden cargar
        engineSound = null;
        shootSound = createSyntheticSound("shoot.wav");
        explosionSound = createSyntheticSound("explosion.wav");
        powerUpSound = createSyntheticSound("powerup.wav");
        turboSound = createSyntheticSound("turbo.wav");
        backgroundMusic = null;
    }
    
    public void playEngineSound() {
        if (!soundEnabled) return;
        if (engineSound == null) return;
        if (!engineLooping) {
            engineSound.stop();
            engineSound.setFramePosition(0);
            engineSound.loop(Clip.LOOP_CONTINUOUSLY);
            engineLooping = true;
        }
        setVolume(engineSound, volume * 0.25f);
    }
    
    public void stopEngineSound() {
        engineLooping = false;
        if (engineSound != null) {
            engineSound.stop();
        }
    }
    
    public void playShootSound() {
        if (soundEnabled && shootSound != null) {
            shootSound.stop();
            shootSound.setFramePosition(0);
            shootSound.start();
            setVolume(shootSound, volume);
        }
    }
    
    public void playExplosionSound() {
        if (soundEnabled && explosionSound != null) {
            explosionSound.stop();
            explosionSound.setFramePosition(0);
            explosionSound.start();
            setVolume(explosionSound, volume);
        }
    }
    
    public void playPowerUpSound() {
        if (soundEnabled && powerUpSound != null) {
            powerUpSound.stop();
            powerUpSound.setFramePosition(0);
            powerUpSound.start();
            setVolume(powerUpSound, volume);
        }
    }
    
    public void playTurboSound() {
        if (soundEnabled && turboSound != null) {
            turboSound.stop();
            turboSound.setFramePosition(0);
            turboSound.start();
            setVolume(turboSound, volume);
        }
    }
    
    public void playGameOverSound() {
        // Sonido de game over
        playTone(200, 500, 0.2);
        playTone(150, 500, 0.2);
        playTone(100, 500, 0.2);
    }
    
    public void playVictorySound() {
        // Sonido de victoria
        playTone(400, 200, 0.1);
        playTone(500, 200, 0.1);
        playTone(600, 300, 0.1);
    }
    
    public void updateEngineSound(boolean turboActive) {
        if (!soundEnabled) return;
        if (engineSound == null) return;
        if (!engineLooping) {
            playEngineSound();
        }
        float target = turboActive ? volume * 0.4f : volume * 0.25f;
        setVolume(engineSound, target);
    }
    
    public void playTone(int frequency, int duration, double volume) {
        if (!soundEnabled) return;
        
        try {
            // Crear onda sinusoidal
            byte[] buffer = new byte[(int)(duration * 44.1)]; // Duración especificada a 44.1kHz
            for (int i = 0; i < buffer.length; i++) {
                double angle = 2.0 * Math.PI * frequency * i / 44100.0;
                buffer[i] = (byte)(Math.sin(angle) * volume * 127);
            }
            
            AudioFormat format = new AudioFormat(44100, 8, 1, true, false);
            AudioInputStream stream = new AudioInputStream(
                new java.io.ByteArrayInputStream(buffer), format, buffer.length);
            
            Clip clip = AudioSystem.getClip();
            clip.open(stream);
            clip.start();
            
            // Liberar recursos después de reproducir
            clip.addLineListener(event -> {
                if (event.getType() == LineEvent.Type.STOP) {
                    clip.close();
                }
            });
            
        } catch (Exception e) {
            System.err.println("Error playing tone: " + e.getMessage());
        }
    }
    
    public void playBackgroundMusic() {
        if (soundEnabled && backgroundMusic != null) {
            backgroundMusic.stop();
            backgroundMusic.setFramePosition(0);
            backgroundMusic.loop(Clip.LOOP_CONTINUOUSLY);
            setVolume(backgroundMusic, volume * 0.5f); // Música más suave
        }
    }
    
    public void stopBackgroundMusic() {
        if (musicPlayer != null) {
            musicPlayer.stop();
        }
        if (backgroundMusic != null) {
            backgroundMusic.stop();
        }
    }

    

    // Música por mundo (Cloud, Canyon, City)
    public void playWorldMusic(String world) {
        if (!soundEnabled) return;
        String track;
        switch (world) {
            case "CLOUD_KINGDOM":
                track = bestTrackAny("Ultimate-Battle", "ultimate battle", "world_cloud", "cloud", "Cloud-Kingdom");
                break;
            case "CRYSTAL_CANYON":
                track = bestTrackAny("Marvel-vs-Capcom", "marvel vs capcom", "world_canyon", "crystal", "canyon");
                break;
            case "FLOATING_CITY":
                track = bestTrackAny("Botanic-Panic", "botanic panic", "botanic", "world_city", "city");
                break;
            default:
                track = bestTrackAny("menu");
        }
        startMusic(track);
    }

    

    // Inicia música desde archivo, usando MP3 si existe y está disponible
    private void startMusic(String trackPath) {
        stopBackgroundMusic();
        // Fallback: si no hay archivo de pista válido, usa el Clip backgroundMusic
        if (trackPath == null || !new File(trackPath).exists()) {
            if (musicPlayer != null) musicPlayer.stop();
            playBackgroundMusic();
            return;
        }
        try {
            if (trackPath.endsWith(".mp3") && isMp3PlayerAvailable()) {
                musicPlayer = (MusicPlayer) Class.forName("Mp3LoopPlayer").getDeclaredConstructor().newInstance();
            } else {
                musicPlayer = new WavLoopPlayer();
            }
        } catch (Exception e) {
            musicPlayer = new WavLoopPlayer();
        }
        musicPlayer.stop();
        musicPlayer.playLoop(trackPath);
        // Reducimos el nivel para que suene más tranquila
        musicPlayer.setVolume(volume * 0.35f);
    }

    private boolean isMp3PlayerAvailable() {
        try {
            Class.forName("Mp3LoopPlayer");
            return true;
        } catch (ClassNotFoundException e) {
            return false;
        }
    }

    private String bestTrack(String baseName) {
        if (baseName == null) return null;
        if ("ambient".equalsIgnoreCase(musicStyle)) {
            File ambientMp3 = new File(SOUND_DIR + baseName + "_ambient.mp3");
            if (ambientMp3.exists()) return ambientMp3.getPath();
            File ambientWav = new File(SOUND_DIR + baseName + "_ambient.wav");
            if (ambientWav.exists()) return ambientWav.getPath();
        }
        File mp3 = new File(SOUND_DIR + baseName + ".mp3");
        if (mp3.exists()) return mp3.getPath();
        File wav = new File(SOUND_DIR + baseName + ".wav");
        if (wav.exists()) return wav.getPath();
        // Búsqueda tolerante: intenta localizar por nombre aproximado
        String loose = findTrackByLooseName(baseName);
        return loose;
    }

    private String bestTrackAny(String... baseNames) {
        if (baseNames == null) return null;
        for (String base : baseNames) {
            String t = bestTrack(base);
            if (t != null) return t;
        }
        return null;
    }

    private String findTrackByLooseName(String baseName) {
        try {
            File dir = new File(SOUND_DIR);
            if (!dir.exists()) return null;
            String norm = normalize(baseName);
            File[] files = dir.listFiles((d, name) -> name.toLowerCase().endsWith(".wav") || name.toLowerCase().endsWith(".mp3"));
            if (files == null) return null;
            for (File f : files) {
                String name = f.getName();
                String nn = normalize(name.replaceFirst("\\.wav$|\\.mp3$", ""));
                if (nn.contains(norm)) return f.getPath();
            }
        } catch (Exception ignored) {}
        return null;
    }

    private String normalize(String s) {
        if (s == null) return "";
        return s.toLowerCase().replaceAll("[\\s_-]", "");
    }

    // Permite cambiar el estilo musical en tiempo de ejecución
    public void setMusicStyleAmbient(boolean ambient) {
        this.musicStyle = ambient ? "ambient" : "default";
    }

    private void fadeIn(Clip clip, float targetVolume, int durationMs) {
        new Thread(() -> {
            try {
                int steps = 10;
                for (int i = 1; i <= steps; i++) {
                    float v = targetVolume * (i / (float) steps);
                    setVolume(clip, v);
                    Thread.sleep(durationMs / steps);
                }
            } catch (InterruptedException ignored) {}
        }, "music-fade-in").start();
    }

    private void fadeOut(Clip clip, int durationMs) {
        new Thread(() -> {
            try {
                int steps = 10;
                // Obtener volumen actual aproximado usando el campo volume
                for (int i = steps - 1; i >= 0; i--) {
                    float v = (volume * 0.5f) * (i / (float) steps);
                    setVolume(clip, v);
                    Thread.sleep(durationMs / steps);
                }
                clip.stop();
            } catch (InterruptedException ignored) {}
        }, "music-fade-out").start();
    }
    
    private void setVolume(Clip clip, float volume) {
        if (clip != null && clip.isControlSupported(FloatControl.Type.MASTER_GAIN)) {
            FloatControl gainControl = (FloatControl) clip.getControl(FloatControl.Type.MASTER_GAIN);
            float dB = (float)(Math.log(volume) / Math.log(10.0) * 20.0);
            gainControl.setValue(Math.max(gainControl.getMinimum(), Math.min(dB, gainControl.getMaximum())));
        }
    }
    
    public void setVolume(float volume) {
        this.volume = Math.max(0.0f, Math.min(1.0f, volume));
        
        // Actualizar volumen de todos los sonidos activos
        if (engineSound != null && engineSound.isActive()) {
            setVolume(engineSound, this.volume * 0.3f);
        }
        if (backgroundMusic != null && backgroundMusic.isActive()) {
            setVolume(backgroundMusic, this.volume * 0.5f);
        }
        if (musicPlayer != null) {
            musicPlayer.setVolume(this.volume * 0.5f);
        }
    }
    
    public void setSoundEnabled(boolean enabled) {
        this.soundEnabled = enabled;
        
        if (!enabled) {
            stopAllSounds();
        }
    }
    
    public void stopAllSounds() {
        stopEngineSound();
        stopBackgroundMusic();
        
        if (shootSound != null) shootSound.stop();
        if (explosionSound != null) explosionSound.stop();
        if (powerUpSound != null) powerUpSound.stop();
        if (turboSound != null) turboSound.stop();
    }

    
    
    public float getVolume() {
        return volume;
    }
    
    public boolean isSoundEnabled() {
        return soundEnabled;
    }
}
