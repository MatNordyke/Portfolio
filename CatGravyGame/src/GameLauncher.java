import java.awt.*;
import java.awt.event.KeyEvent;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import javax.imageio.ImageIO;
import javax.swing.*;

public class GameLauncher {
    private JFrame frame;
    private JPanel mainMenuPanel, continuePanel, newGamePanel;
    private String saveFilePath = "my save files";
    private JLabel[] saveSlots = new JLabel[3];
    private BufferedImage backgroundImage;

    public GameLauncher() {
        loadBackgroundImage("media/gravycat.jpg");

        frame = new JFrame("Game Launcher");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setSize(800, 600);
        frame.setLocationRelativeTo(null);

        showMainMenu();
        frame.setVisible(true);
    }

    private void loadBackgroundImage(String path) {
        try {
            File imageFile = new File(path);
            System.out.println("Image file exists: " + imageFile.exists());
            backgroundImage = ImageIO.read(imageFile);
        } catch (IOException e) {
            System.out.println("ERROR: Could not load background image from " + path);
            e.printStackTrace();
        }
    }

    private void showMainMenu() {
        mainMenuPanel = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                drawBackground(g, this);
            }
        };
        mainMenuPanel.setLayout(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(10, 10, 10, 10);

        JButton newGameButton = new JButton("New Game");
        JButton continueButton = new JButton("Continue");

        newGameButton.addActionListener(e -> showNewGameMenu());
        continueButton.addActionListener(e -> showContinueMenu());

        gbc.gridx = 0;
        gbc.gridy = 0;
        mainMenuPanel.add(newGameButton, gbc);
        gbc.gridy = 1;
        mainMenuPanel.add(continueButton, gbc);

        frame.setContentPane(mainMenuPanel);
        frame.revalidate();
        frame.repaint();
    }

    private void showContinueMenu() {
        continuePanel = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                drawBackground(g, this);
            }
        };
        continuePanel.setLayout(new GridLayout(5, 1));

        JLabel title = new JLabel("Select Save File", SwingConstants.CENTER);
        continuePanel.add(title);

        loadSaveFiles();

        for (JLabel saveSlot : saveSlots) {
            continuePanel.add(saveSlot);
        }

        JButton backButton = new JButton("Back");
        backButton.addActionListener(e -> showMainMenu());
        continuePanel.add(backButton);

        frame.setContentPane(continuePanel);
        frame.revalidate();
        frame.repaint();
    }

    private void showNewGameMenu() {
        final int MAX_NAME_LENGTH = 12;
        StringBuilder nameBuilder = new StringBuilder();
        JLabel nameDisplayLabel = new JLabel();
        final boolean[] showCursor = {true};

        Runnable updateDisplay = () -> {
            StringBuilder display = new StringBuilder();
            int length = nameBuilder.length();

            for (int i = 0; i < MAX_NAME_LENGTH; i++) {
                if (i < length) {
                    display.append(nameBuilder.charAt(i));
                } else if (i == length && showCursor[0]) {
                    display.append("_");
                } else {
                    display.append(" ");
                }
            }
            nameDisplayLabel.setText(display.toString());
        };

        newGamePanel = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                drawBackground(g, this);
            }
        };

        newGamePanel.setLayout(new BorderLayout());
        newGamePanel.setBackground(Color.BLACK);

        JPanel displayPanel = new JPanel();
        displayPanel.setBackground(Color.BLACK);
        nameDisplayLabel.setFont(new Font("Monospaced", Font.BOLD, 28));
        nameDisplayLabel.setForeground(Color.WHITE);
        displayPanel.add(nameDisplayLabel);
        newGamePanel.add(displayPanel, BorderLayout.NORTH);

        Timer cursorTimer = new Timer(500, e -> {
            showCursor[0] = !showCursor[0];
            updateDisplay.run();
        });
        cursorTimer.start();

        JPanel charPanel = new JPanel(new GridLayout(6, 10, 4, 4));
        charPanel.setBackground(Color.BLACK);

        String chars =
                "ABCDEFGHIJKLMNOPQRSTUVWXYZ" +
                "abcdefghijklmnopqrstuvwxyz" +
                "1234567890" +
                "!@#$%^&*()" +
                "'\"/\\?";

        JButton[][] buttons = new JButton[6][10];
        int index = 0;
        int[] selected = {0, 0};

        for (int row = 0; row < 6; row++) {
            for (int col = 0; col < 10; col++) {
                JButton btn;
                if (index < chars.length()) {
                    char c = chars.charAt(index);
                    btn = new JButton(String.valueOf(c));
                    btn.setFont(new Font("Monospaced", Font.PLAIN, 14));
                    btn.setBackground(Color.BLACK);
                    btn.setForeground(Color.WHITE);
                    btn.setFocusPainted(false);
                    btn.setBorder(BorderFactory.createLineBorder(Color.DARK_GRAY));

                    btn.addActionListener(e -> {
                        if (nameBuilder.length() < MAX_NAME_LENGTH) {
                            nameBuilder.append(c);
                            updateDisplay.run();
                        } else {
                            JOptionPane.showMessageDialog(frame, "Name cannot exceed 12 characters.");
                        }
                    });

                    buttons[row][col] = btn;
                    charPanel.add(btn);
                    index++;
                } else {
                    JButton empty = new JButton("");
                    empty.setEnabled(false);
                    empty.setVisible(false);
                    buttons[row][col] = empty;
                    charPanel.add(empty);
                }
            }
        }

        charPanel.setFocusable(true);

SwingUtilities.invokeLater(() -> {
    charPanel.requestFocusInWindow();
});


        charPanel.addKeyListener(new java.awt.event.KeyAdapter() {
            @Override
            public void keyPressed(java.awt.event.KeyEvent e) {
                buttons[selected[0]][selected[1]].setBorder(BorderFactory.createLineBorder(Color.DARK_GRAY));
                int r = selected[0];
                int c = selected[1];

                switch (e.getKeyCode()) {
                    case KeyEvent.VK_LEFT -> { do { c = (c + 9) % 10; } while (!buttons[r][c].isEnabled()); }
                    case KeyEvent.VK_RIGHT -> { do { c = (c + 1) % 10; } while (!buttons[r][c].isEnabled()); }
                    case KeyEvent.VK_UP -> { do { r = (r + 5) % 6; } while (!buttons[r][c].isEnabled()); }
                    case KeyEvent.VK_DOWN -> { do { r = (r + 1) % 6; } while (!buttons[r][c].isEnabled()); }
                    case KeyEvent.VK_ENTER -> {
                        JButton b = buttons[r][c];
                        if (b.isEnabled()) b.doClick();
                        return;
                    }
                    default -> { return; }
                }

                selected[0] = r;
                selected[1] = c;
                buttons[r][c].setBorder(BorderFactory.createLineBorder(Color.ORANGE, 2));
            }
        });

        buttons[0][0].setBorder(BorderFactory.createLineBorder(Color.ORANGE, 2));
        newGamePanel.add(charPanel, BorderLayout.CENTER);

        JPanel rightPanel = new JPanel();
        rightPanel.setLayout(new BoxLayout(rightPanel, BoxLayout.Y_AXIS));
        rightPanel.setBackground(Color.BLACK);

        JButton backButton = new JButton("Back");
        backButton.setAlignmentX(Component.CENTER_ALIGNMENT);
        backButton.addActionListener(e -> {
            if (nameBuilder.length() > 0) {
                nameBuilder.deleteCharAt(nameBuilder.length() - 1);
                updateDisplay.run();
            }
        });

        JButton doneButton = new JButton("Done");
        doneButton.setAlignmentX(Component.CENTER_ALIGNMENT);
        doneButton.addActionListener(e -> {
            String name = nameBuilder.toString().trim();
            if (name.isEmpty()) {
                JOptionPane.showMessageDialog(frame, "Please enter a name before continuing.");
                return;
            }

            String[] options = { "File 1", "File 2", "File 3" };
            int choice = JOptionPane.showOptionDialog(
                    frame,
                    "Select a save slot for " + name + ":",
                    "Save Game",
                    JOptionPane.DEFAULT_OPTION,
                    JOptionPane.QUESTION_MESSAGE,
                    null,
                    options,
                    options[0]
            );

            if (choice >= 0 && choice < 3) {
                File saveFolder = new File(saveFilePath);
                if (!saveFolder.exists()) saveFolder.mkdir();

                String filename = "file" + (choice + 1) + "_" + name + ".txt";
                File saveFile = new File(saveFolder, filename);

                try {
                    if (saveFile.createNewFile()) {
                        JOptionPane.showMessageDialog(frame, "Saved '" + name + "' to slot " + (choice + 1));
                    } else {
                        JOptionPane.showMessageDialog(frame, "File already exists. Overwriting.");
                    }
                } catch (IOException ex) {
                    JOptionPane.showMessageDialog(frame, "Error saving file: " + ex.getMessage());
                }

                cursorTimer.stop();
                showMainMenu();
            }
        });

        rightPanel.add(Box.createVerticalStrut(20));
        rightPanel.add(backButton);
        rightPanel.add(Box.createVerticalStrut(10));
        rightPanel.add(doneButton);
        newGamePanel.add(rightPanel, BorderLayout.EAST);

        frame.setContentPane(newGamePanel);
        frame.revalidate();
        frame.repaint();
    }

    private void loadSaveFiles() {
        File saveFolder = new File(saveFilePath);
        if (!saveFolder.exists()) {
            saveFolder.mkdir();
        }

        for (int i = 0; i < 3; i++) {
            File saveFile = new File(saveFolder, "file" + (i + 1) + ".txt");
            if (saveFile.exists()) {
                saveSlots[i] = new JLabel("File " + (i + 1) + ": " + getLastModified(saveFile), SwingConstants.CENTER);
            } else {
                saveSlots[i] = new JLabel("File " + (i + 1) + ": -------", SwingConstants.CENTER);
            }
        }
    }

    private String getLastModified(File file) {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        return sdf.format(new Date(file.lastModified()));
    }

    private void drawBackground(Graphics g, JComponent component) {
        if (backgroundImage != null) {
            g.drawImage(backgroundImage, 0, 0, component.getWidth(), component.getHeight(), null);
        } else {
            System.out.println("Background image is null.");
        }
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(GameLauncher::new);
    }
}
