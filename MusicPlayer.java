public interface MusicPlayer {
    void playLoop(String filePath);
    void stop();
    void setVolume(float volume);
}