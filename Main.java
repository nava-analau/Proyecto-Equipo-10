import javax.swing.*;

public class Main {
    public static void main(String[] args) {

        JFrame frame = new JFrame("Sky Runner: Carrera en las Nubes");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setResizable(false);
        
        SkyRunnerGame game = new SkyRunnerGame();
        frame.add(game);
        
 
        frame.pack();
        frame.setLocationRelativeTo(null); 
        
        frame.setVisible(true);
        game.requestFocusInWindow();
    }
}