import javax.sound.sampled.*;
import java.io.File;
import java.io.IOException;

public class WavLoopPlayer implements MusicPlayer {
    private Clip clip;
    private float volume = 0.5f;

    @Override
    public void playLoop(String filePath) {
        stop();
        try {
            File soundFile = new File(filePath);
            if (!soundFile.exists()) return;

            AudioInputStream audioStream = AudioSystem.getAudioInputStream(soundFile);
            clip = AudioSystem.getClip();
            clip.open(audioStream);
            clip.loop(Clip.LOOP_CONTINUOUSLY);
            setVolume(volume);
        } catch (UnsupportedAudioFileException | IOException | LineUnavailableException e) {
            System.out.println("Error WAV: " + e.getMessage());
        }
    }

    @Override
    public void stop() {
        if (clip != null) {
            clip.stop();
            clip.close();
            clip = null;
        }
    }

    @Override
    public void setVolume(float volume) {
        this.volume = Math.max(0.0f, Math.min(1.0f, volume));
        if (clip != null && clip.isControlSupported(FloatControl.Type.MASTER_GAIN)) {
            FloatControl gainControl = (FloatControl) clip.getControl(FloatControl.Type.MASTER_GAIN);
            float dB = (float)(Math.log(this.volume) / Math.log(10.0) * 20.0);
            gainControl.setValue(Math.max(gainControl.getMinimum(), Math.min(dB, gainControl.getMaximum())));
        }
    }
}