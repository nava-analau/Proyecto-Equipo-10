import javax.swing.*;
import java.awt.*;
import java.awt.event.*;

public class MainMenu extends JFrame implements ActionListener {
    
    private String selectedDifficulty = "NORMAL";
    private String selectedWorld = "CLOUD_KINGDOM";
    private boolean soundEnabled = true;
    private float volume = 0.7f;
    private boolean ambientPreferred = true;
    private double renderScale = 1.0;
    
    private SoundManager soundManager;
    private JButton startButton;
    
    public MainMenu() {
        super("Sky Runner - Menú Principal");
        
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(800, 600);
        setLocationRelativeTo(null);
        setResizable(false);
        
        initComponents(); 
        
        // Música de fondo del menú
        soundManager = new SoundManager();
        soundManager.setVolume(volume);
        soundManager.setMusicStyleAmbient(ambientPreferred);
        if (soundEnabled) {
            try { soundManager.playMenuMusic(); } catch (Exception ignored) {}
        }
    }
    
    private void initComponents() {
        JPanel mainPanel = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                drawBackground(g);
            }
        };
        
        mainPanel.setLayout(new BorderLayout());
        mainPanel.setBackground(new Color(135, 206, 235));
        
        // Panel del título
        JPanel titlePanel = new JPanel();
        titlePanel.setOpaque(false);
        titlePanel.setLayout(new BoxLayout(titlePanel, BoxLayout.Y_AXIS));
        titlePanel.setBorder(BorderFactory.createEmptyBorder(50, 0, 30, 0));
        
        JLabel titleLabel = new JLabel("SKY RUNNER");
        titleLabel.setFont(new Font("Arial", Font.BOLD, 48));
        titleLabel.setForeground(Color.WHITE);
        titleLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        
        JLabel subtitleLabel = new JLabel("Carrera en las Nubes");
        subtitleLabel.setFont(new Font("Arial", Font.ITALIC, 24));
        subtitleLabel.setForeground(Color.WHITE);
        subtitleLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        
        titlePanel.add(titleLabel);
        titlePanel.add(Box.createVerticalStrut(10));
        titlePanel.add(subtitleLabel);
        
        // Panel de opciones
        JPanel optionsPanel = new JPanel();
        optionsPanel.setOpaque(false);
        optionsPanel.setLayout(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(10, 10, 10, 10);
        gbc.fill = GridBagConstraints.HORIZONTAL;
        
        // Dificultad
        gbc.gridx = 0;
        gbc.gridy = 0;
        optionsPanel.add(createLabel("Dificultad:"), gbc);
        
        gbc.gridx = 1;
        JComboBox<String> difficultyCombo = new JComboBox<>(new String[]{"Normal", "Difícil"});
        difficultyCombo.setSelectedIndex(0);
        difficultyCombo.addActionListener(e -> {
            switch (difficultyCombo.getSelectedIndex()) {
                case 0: selectedDifficulty = "NORMAL"; break;
                case 1: selectedDifficulty = "HARD"; break;
            }
        });
        optionsPanel.add(difficultyCombo, gbc);
        
        // Mundo
        gbc.gridx = 0;
        gbc.gridy = 1;
        optionsPanel.add(createLabel("Mundo:"), gbc);
        
        gbc.gridx = 1;
        JComboBox<String> worldCombo = new JComboBox<>(new String[]{
            "Reino de las Nubes", 
            "Cañón de Cristal", 
            "Ciudad Flotante"
        });
        worldCombo.addActionListener(e -> {
            switch (worldCombo.getSelectedIndex()) {
                case 0: selectedWorld = "CLOUD_KINGDOM"; break;
                    case 1: selectedWorld = "CRYSTAL_CANYON"; break;
                    case 2: selectedWorld = "FLOATING_CITY"; break;
            }
            if (startButton != null) startButton.setEnabled(true);
        });
        optionsPanel.add(worldCombo, gbc);
        
        // (Eliminado) Opción de sonido general: dejamos solo control de música
        
        // Música: eliminamos la opción de quitar música; solo control de volumen
        gbc.gridx = 0;
        gbc.gridy = 2;
        optionsPanel.add(createLabel("Música:"), gbc);
        gbc.gridx = 1;
        optionsPanel.add(new JLabel("Siempre activa"), gbc);

        // Tamaño de ventana
        gbc.gridx = 0;
        gbc.gridy = 3;
        optionsPanel.add(createLabel("Tamaño de ventana:"), gbc);
        gbc.gridx = 1;
        JComboBox<String> sizeCombo = new JComboBox<>(new String[]{"100%","125%","150%","200%"});
        sizeCombo.setSelectedIndex(2);
        sizeCombo.addActionListener(e -> {
            int idx = sizeCombo.getSelectedIndex();
            renderScale = switch (idx) { case 0 -> 1.0; case 1 -> 1.25; case 2 -> 1.5; default -> 2.0; };
        });
        optionsPanel.add(sizeCombo, gbc);

        // Volumen
        gbc.gridx = 0;
        gbc.gridy = 4;
        optionsPanel.add(createLabel("Volumen:"), gbc);

        gbc.gridx = 1;
        JSlider volumeSlider = new JSlider(0, 100, 70);
        volumeSlider.setMajorTickSpacing(25);
        volumeSlider.setPaintTicks(true);
        volumeSlider.setPaintLabels(true);
        volumeSlider.addChangeListener(e -> {
            volume = volumeSlider.getValue() / 100.0f;
            if (soundManager != null) {
                soundManager.setVolume(volume);
            }
        });
        optionsPanel.add(volumeSlider, gbc);

        // Botón rojo "Empezar" debajo del volumen
        gbc.gridx = 0;
        gbc.gridy = 5;
        gbc.gridwidth = 2;
        JButton empezarButton = createButton("Empezar", new Color(220, 20, 60));
        empezarButton.addActionListener(e -> startGame());
        optionsPanel.add(empezarButton, gbc);
        gbc.gridwidth = 1;
        
        // Panel de botones
        JPanel buttonPanel = new JPanel();
        buttonPanel.setOpaque(false);
        buttonPanel.setLayout(new FlowLayout(FlowLayout.CENTER, 20, 20));
        
        startButton = createButton("Iniciar Juego", new Color(34, 139, 34));
        startButton.setEnabled(false);
        startButton.addActionListener(e -> startGame());
        
        JButton instructionsButton = createButton("Instrucciones", new Color(70, 130, 180));
        instructionsButton.addActionListener(e -> showInstructions());
        
        JButton exitButton = createButton("Salir", new Color(220, 20, 60));
        exitButton.addActionListener(e -> System.exit(0));
        
        buttonPanel.add(startButton);
        buttonPanel.add(instructionsButton);
        buttonPanel.add(exitButton);
        
        // Panel de información
        JPanel infoPanel = new JPanel();
        infoPanel.setOpaque(false);
        infoPanel.setBorder(BorderFactory.createEmptyBorder(20, 0, 20, 0));
        
        JLabel infoLabel = new JLabel("Usa las flechas para moverte, ESPACIO para disparar");
        infoLabel.setFont(new Font("Arial", Font.PLAIN, 14));
        infoLabel.setForeground(Color.WHITE);
        infoPanel.add(infoLabel);
        
        // Ensamblar todo
        mainPanel.add(titlePanel, BorderLayout.NORTH);
        mainPanel.add(optionsPanel, BorderLayout.CENTER);
        mainPanel.add(buttonPanel, BorderLayout.SOUTH);
        mainPanel.add(infoPanel, BorderLayout.PAGE_END);
        
        add(mainPanel);
    }
    
    private JLabel createLabel(String text) {
        JLabel label = new JLabel(text);
        label.setFont(new Font("Arial", Font.BOLD, 16));
        label.setForeground(Color.WHITE);
        return label;
    }
    
    private JButton createButton(String text, Color backgroundColor) {
        JButton button = new JButton(text);
        button.setFont(new Font("Arial", Font.BOLD, 16));
        button.setBackground(backgroundColor);
        button.setForeground(Color.WHITE);
        button.setFocusPainted(false);
        button.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(Color.WHITE, 2),
            BorderFactory.createEmptyBorder(10, 20, 10, 20)
        ));
        
        button.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseEntered(MouseEvent e) {
                button.setBackground(backgroundColor.brighter());
            }
            
            @Override
            public void mouseExited(MouseEvent e) {
                button.setBackground(backgroundColor);
            }
        });
        
        return button;
    }
    
    private void drawBackground(Graphics g) {
        // Crear efecto de cielo con degradado
        Graphics2D g2d = (Graphics2D) g;
        GradientPaint gradient = new GradientPaint(0, 0, new Color(135, 206, 235), 
                                                     0, getHeight(), new Color(70, 130, 180));
        g2d.setPaint(gradient);
        g2d.fillRect(0, 0, getWidth(), getHeight());
        
        // Dibujar nubes decorativas
        g2d.setColor(new Color(255, 255, 255, 100));
        for (int i = 0; i < 5; i++) {
            int x = (i * 200 + 50) % getWidth();
            int y = 50 + i * 80;
            int size = 40 + i * 10;
            
            // Dibujar nube simple
            g2d.fillOval(x, y, size, size/2);
            g2d.fillOval(x - size/4, y + size/4, (int)(size * 1.5f), size/2);
            g2d.fillOval(x + size/4, y + size/4, size, size/2);
        }
        
        // Dibujar aviones decorativos
        g2d.setColor(new Color(255, 255, 255, 150));
        for (int i = 0; i < 3; i++) {
            int x = (i * 300 + 100) % getWidth();
            int y = 100 + i * 120;
            
            drawDecorativePlane(g2d, x, y);
        }
    }
    
    private void drawDecorativePlane(Graphics2D g2d, int x, int y) {
        // Dibujar un avión simple decorativo
        g2d.fillOval(x, y, 40, 20);
        g2d.fillPolygon(new int[]{x - 10, x + 10, x + 20}, new int[]{y + 10, y + 5, y + 15}, 3);
        g2d.fillPolygon(new int[]{x + 20, x + 30, x + 40}, new int[]{y + 15, y + 20, y + 15}, 3);
    }
    
    private void startGame() {
        if (soundManager != null) {
            try { soundManager.stopBackgroundMusic(); } catch (Exception ignored) {}
        }
        
        dispose();
        
        // Crear y configurar el juego
        SkyRunnerGame game = new SkyRunnerGame();
        game.setDifficulty(selectedDifficulty);
        game.setWorld(selectedWorld);
        
        JFrame gameFrame = new JFrame("Sky Runner: Carrera en las Nubes");
        gameFrame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        game.setRenderScale(renderScale);
        gameFrame.add(game);
        gameFrame.pack();
        gameFrame.setLocationRelativeTo(null);
        gameFrame.setResizable(false);
        gameFrame.setVisible(true);
        // Enfocar y auto-iniciar el juego
        game.requestFocusInWindow();
        game.startGame();
        
        // Configurar sonido
        game.setSoundEnabled(soundEnabled);
        game.setVolume(volume);
        // Música siempre activa con el volumen elegido
    }
    
    private void showInstructions() {
        String instructions = 
            "CONTROLES:\n" +
            "• Flechas: Mover la nave\n" +
            "• Espacio: Disparar\n" +
            "• ESC: Pausar el juego\n\n" +
            "OBJETIVO:\n" +
            "• Llegar a la meta con la mayor puntuación\n" +
            "• Esquiva obstáculos y enemigos\n" +
            "• Recoge power-ups para mejorar\n\n" +
            "POWER-UPS:\n" +
            "• Turbo: Aumenta velocidad\n" +
            "• Estrella: Bonus de puntuación\n\n" +
            "OBSTÁCULOS:\n" +
            "• Torres de roca: Dañan tu nave\n" +
            "• Tormentas: Desestabilizan el vuelo\n" +
            "• Turbinas: Empujan tu nave";
        
        JOptionPane.showMessageDialog(this, instructions, "Instrucciones", JOptionPane.INFORMATION_MESSAGE);
    }
    
    @Override
    public void actionPerformed(ActionEvent e) {
        // Manejar eventos de acción si es necesario
    }
    
    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            new MainMenu().setVisible(true);
        });
    }

    
}
