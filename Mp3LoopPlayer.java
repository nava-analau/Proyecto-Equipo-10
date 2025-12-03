import java.io.BufferedInputStream;
import java.io.FileInputStream;
import java.io.InputStream;

// Implementación basada en reflexión para evitar dependencia de compilación.
// Usa JLayer (javazoom.jl.player.Player) si está disponible en tiempo de ejecución.
public class Mp3LoopPlayer implements MusicPlayer {
    private volatile boolean stopFlag = false;
    private Thread playThread;
    private String currentPath;
    private float volume = 0.5f; // No-op con JLayer Player estándar

    @Override
    public synchronized void playLoop(String path) {
        stop();
        this.currentPath = path;
        this.stopFlag = false;
        playThread = new Thread(() -> {
            while (!stopFlag) {
                try (InputStream fis = new BufferedInputStream(new FileInputStream(currentPath))) {
                    Class<?> playerClass = Class.forName("javazoom.jl.player.Player");
                    Object player = playerClass.getConstructor(InputStream.class).newInstance(fis);
                    // player.play();
                    playerClass.getMethod("play").invoke(player);
                    // Cerrar cuando termine una pasada
                    try { playerClass.getMethod("close").invoke(player); } catch (Exception ignore) {}
                } catch (Throwable t) {
                    // Si no está disponible JLayer o error al reproducir, salimos del bucle
                    break;
                }
            }
        }, "Mp3LoopPlayerThread");
        playThread.setDaemon(true);
        playThread.start();
    }

    @Override
    public synchronized void stop() {
        stopFlag = true;
        if (playThread != null) {
            try { playThread.interrupt(); } catch (Exception ignore) {}
            playThread = null;
        }
    }

    @Override
    public void setVolume(float volume) {
        // JLayer Player estándar no expone control de volumen público.
        // Guardamos el valor por coherencia, pero es un no-op.
        this.volume = Math.max(0.0f, Math.min(1.0f, volume));
    }
}