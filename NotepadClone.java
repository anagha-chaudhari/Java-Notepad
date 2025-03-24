import javax.swing.*;
import javax.swing.filechooser.FileNameExtensionFilter;
import java.awt.*;
import java.awt.event.*;
import java.io.*;

public class NotepadClone extends JFrame implements ActionListener {
    JTextArea textArea;
    JLabel statusLabel;
    JFileChooser fileChooser;
    boolean isDarkMode = false;
    Timer animationTimer;
    Color currentBgColor;
    JMenuItem toggleMode;
    JComboBox<String> fontSelector;
    JComboBox<Integer> fontSizeSelector;
    JButton colorButton;

    public NotepadClone() {
        setTitle("Notepad Clone");
        setSize(600, 500);
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setLocationRelativeTo(null);

        textArea = new JTextArea();
        textArea.setFont(new Font("Arial", Font.PLAIN, 16));
        textArea.addKeyListener(new KeyAdapter() {
            public void keyReleased(KeyEvent e) {
                updateWordCount();
            }
        });

        add(new JScrollPane(textArea), BorderLayout.CENTER);

        statusLabel = new JLabel("Words: 0 | Characters: 0");
        add(statusLabel, BorderLayout.SOUTH);

        JMenuBar menuBar = new JMenuBar();
        JMenu fileMenu = new JMenu("File");
        JMenuItem newFile = new JMenuItem("New");
        JMenuItem openFile = new JMenuItem("Open");
        JMenuItem saveFile = new JMenuItem("Save");
        JMenuItem exitApp = new JMenuItem("Exit");

        toggleMode = new JMenuItem("🌙 Night Mode");
        toggleMode.addActionListener(e -> animateModeChange());

        
        newFile.addActionListener(this);
        openFile.addActionListener(this);
        saveFile.addActionListener(this);
        exitApp.addActionListener(this);

       
        fileMenu.add(newFile);
        fileMenu.add(openFile);
        fileMenu.add(saveFile);
        fileMenu.addSeparator();
        fileMenu.add(toggleMode);
        fileMenu.addSeparator();
        fileMenu.add(exitApp);
        menuBar.add(fileMenu);

        JPanel fontPanel = new JPanel();
        
        
        String[] fonts = GraphicsEnvironment.getLocalGraphicsEnvironment().getAvailableFontFamilyNames();
        fontSelector = new JComboBox<>(fonts);
        fontSelector.setSelectedItem("Arial");
        fontSelector.addActionListener(e -> updateFont());

        
        Integer[] fontSizes = {12, 14, 16, 18, 20, 24, 28, 32};
        fontSizeSelector = new JComboBox<>(fontSizes);
        fontSizeSelector.setSelectedItem(16);
        fontSizeSelector.addActionListener(e -> updateFont());

       
        colorButton = new JButton("Text Color 🎨");
        colorButton.addActionListener(e -> changeTextColor());

        fontPanel.add(new JLabel("Font: "));
        fontPanel.add(fontSelector);
        fontPanel.add(new JLabel("Size: "));
        fontPanel.add(fontSizeSelector);
        fontPanel.add(colorButton);

        menuBar.add(fontPanel);
        setJMenuBar(menuBar);

        fileChooser = new JFileChooser();
        fileChooser.setFileFilter(new FileNameExtensionFilter("Text Files (*.txt)", "txt"));

        
        currentBgColor = Color.WHITE;
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        String command = e.getActionCommand();

        if (command.equals("New")) {
            textArea.setText("");  // Clear text area
            updateWordCount();
        } else if (command.equals("Open")) {
            openFile();
        } else if (command.equals("Save")) {
            saveFile();
        } else if (command.equals("Exit")) {
            System.exit(0);
        }
    }

    private void openFile() {
        int returnValue = fileChooser.showOpenDialog(this);
        if (returnValue == JFileChooser.APPROVE_OPTION) {
            File file = fileChooser.getSelectedFile();
            try (BufferedReader reader = new BufferedReader(new FileReader(file))) {
                textArea.read(reader, null);
                updateWordCount();
            } catch (IOException ex) {
                JOptionPane.showMessageDialog(this, "Error opening file!", "Error", JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    private void saveFile() {
        int returnValue = fileChooser.showSaveDialog(this);
        if (returnValue == JFileChooser.APPROVE_OPTION) {
            File file = fileChooser.getSelectedFile();
            if (!file.getName().endsWith(".txt")) {
                file = new File(file.getAbsolutePath() + ".txt");
            }
            try (BufferedWriter writer = new BufferedWriter(new FileWriter(file))) {
                textArea.write(writer);
            } catch (IOException ex) {
                JOptionPane.showMessageDialog(this, "Error saving file!", "Error", JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    private void updateWordCount() {
        String text = textArea.getText();
        int words = text.isEmpty() ? 0 : text.trim().split("\\s+").length;
        int characters = text.length();
        statusLabel.setText("Words: " + words + " | Characters: " + characters);
    }

    private void updateFont() {
        String selectedFont = (String) fontSelector.getSelectedItem();
        int selectedSize = (Integer) fontSizeSelector.getSelectedItem();
        textArea.setFont(new Font(selectedFont, Font.PLAIN, selectedSize));
    }

    private void changeTextColor() {
        Color newColor = JColorChooser.showDialog(this, "Choose Text Color", textArea.getForeground());
        if (newColor != null) {
            textArea.setForeground(newColor);
        }
    }

    private void animateModeChange() {
        Color startColor = currentBgColor;
        Color endColor = isDarkMode ? Color.WHITE : new Color(45, 45, 45);  // Dark gray for night mode

        animationTimer = new Timer(50, new ActionListener() {
            float progress = 0;

            @Override
            public void actionPerformed(ActionEvent e) {
                progress += 0.1f;  
                if (progress >= 1) {
                    progress = 1;
                    animationTimer.stop();
                    isDarkMode = !isDarkMode;
                    toggleMode.setText(isDarkMode ? "🌞 Day Mode" : "🌙 Night Mode");
                }

                int r = (int) ((1 - progress) * startColor.getRed() + progress * endColor.getRed());
                int g = (int) ((1 - progress) * startColor.getGreen() + progress * endColor.getGreen());
                int b = (int) ((1 - progress) * startColor.getBlue() + progress * endColor.getBlue());

                currentBgColor = new Color(r, g, b);
                textArea.setBackground(currentBgColor);
                textArea.setForeground(isDarkMode ? Color.WHITE : Color.BLACK);
            }
        });

        animationTimer.start();
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new NotepadClone().setVisible(true));
    }
}
