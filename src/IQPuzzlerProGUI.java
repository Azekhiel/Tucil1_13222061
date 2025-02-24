import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.awt.image.BufferedImage;
import java.io.*;
import java.util.HashMap;
import java.util.Random;
import javax.imageio.ImageIO;

public class IQPuzzlerProGUI extends JFrame {
    private JTextArea boardArea;
    private JButton loadFileButton, solveButton, saveSolutionButton, saveImageButton;
    private JLabel statusLabel;
    private File testCaseFile;

    public IQPuzzlerProGUI() {
        setTitle("IQ Puzzler Pro GUI");
        setSize(600, 500);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        initComponents();
    }
    
    private void initComponents() {
        JPanel topPanel = new JPanel();
        loadFileButton = new JButton("Load Test Case");
        solveButton = new JButton("Solve Puzzle");
        saveSolutionButton = new JButton("Save Text Solution");
        saveImageButton = new JButton("Save as Image");
        
        solveButton.setEnabled(false);
        saveSolutionButton.setEnabled(false);
        saveImageButton.setEnabled(false);
        
        topPanel.add(loadFileButton);
        topPanel.add(solveButton);
        topPanel.add(saveSolutionButton);
        topPanel.add(saveImageButton);
        add(topPanel, BorderLayout.NORTH);
        
        boardArea = new JTextArea();
        boardArea.setEditable(false);
        boardArea.setFont(new Font("Monospaced", Font.PLAIN, 14));
        JScrollPane scrollPane = new JScrollPane(boardArea);
        add(scrollPane, BorderLayout.CENTER);
        
        statusLabel = new JLabel("Status: Menunggu file test case");
        add(statusLabel, BorderLayout.SOUTH);
        
        loadFileButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                loadTestCase();
            }
        });
        
        solveButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                solvePuzzle();
            }
        });
        
        saveSolutionButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                saveSolution();
            }
        });
        
        saveImageButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                saveSolutionAsImage();
            }
        });
    }
    
    private void loadTestCase() {
        JFileChooser fileChooser = new JFileChooser();
        int result = fileChooser.showOpenDialog(this);
        if(result == JFileChooser.APPROVE_OPTION) {
            testCaseFile = fileChooser.getSelectedFile();
            statusLabel.setText("File dipilih: " + testCaseFile.getName());
            solveButton.setEnabled(true);
        }
    }
    
    private void solvePuzzle() {
        if(testCaseFile == null) {
            JOptionPane.showMessageDialog(this, "Belum ada file test case yang dipilih.");
            return;
        }
        
        loadFileButton.setEnabled(false);
        solveButton.setEnabled(false);
        saveSolutionButton.setEnabled(false);
        saveImageButton.setEnabled(false);
        statusLabel.setText("Mencari solusi");
        
        SwingWorker<Void, Void> worker = new SwingWorker<Void, Void>() {
            private long timeTaken;
            @Override
            protected Void doInBackground() throws Exception {
                IQPuzzlerPro.readInput(testCaseFile.getAbsolutePath());
                long start = System.currentTimeMillis();
                boolean solved = IQPuzzlerPro.solve(0);
                long end = System.currentTimeMillis();
                timeTaken = end - start;
                if(!solved) {
                    boardArea.setText("Tidak ada solusi yang ditemukan.");
                } else {
                    StringBuilder sb = new StringBuilder();
                    for (int i = 0; i < IQPuzzlerPro.N; i++) {
                        for (int j = 0; j < IQPuzzlerPro.M; j++) {
                            sb.append(IQPuzzlerPro.board[i][j]);
                        }
                        sb.append("\n");
                    }
                    boardArea.setText(sb.toString());
                }
                return null;
            }
            
            @Override
            protected void done() {
                statusLabel.setText("Solusi ditemukan dalam " + timeTaken + " ms. Iterasi: " + IQPuzzlerPro.iterationCount);
                loadFileButton.setEnabled(true);
                solveButton.setEnabled(true);
                saveSolutionButton.setEnabled(true);
                saveImageButton.setEnabled(true);
            }
        };
        worker.execute();
    }
    
    private void saveSolution() {
        JFileChooser fileChooser = new JFileChooser();
        int result = fileChooser.showSaveDialog(this);
        if(result == JFileChooser.APPROVE_OPTION) {
            File saveFile = fileChooser.getSelectedFile();
            try {
                PrintWriter pw = new PrintWriter(new FileWriter(saveFile));
                pw.print(boardArea.getText());
                pw.close();
                JOptionPane.showMessageDialog(this, "Solusi telah disimpan sebagai file teks.");
            } catch(IOException ex) {
                JOptionPane.showMessageDialog(this, "Error saat menyimpan solusi: " + ex.getMessage());
            }
        }
    }
    
    private void saveSolutionAsImage() {
        int cellSize = 40;
        int rows = IQPuzzlerPro.N;
        int cols = IQPuzzlerPro.M;
        int imgWidth = cols * cellSize;
        int imgHeight = rows * cellSize;
        
        BufferedImage image = new BufferedImage(imgWidth, imgHeight, BufferedImage.TYPE_INT_RGB);
        Graphics2D g2d = image.createGraphics();
        
        HashMap<Character, Color> colorMap = new HashMap<>();
        Random rand = new Random();
        
        g2d.setColor(Color.WHITE);
        g2d.fillRect(0, 0, imgWidth, imgHeight);
        
        for (int i = 0; i < rows; i++) {
            for (int j = 0; j < cols; j++) {
                char letter = IQPuzzlerPro.board[i][j];
                int x = j * cellSize;
                int y = i * cellSize;
                if (letter != '.') {
                    if (!colorMap.containsKey(letter)) {
                        colorMap.put(letter, getRandomColor(rand));
                    }
                    g2d.setColor(colorMap.get(letter));
                    g2d.fillRect(x, y, cellSize, cellSize);
                    g2d.setColor(Color.BLACK);
                    g2d.drawRect(x, y, cellSize, cellSize);
                    g2d.setColor(Color.BLACK);
                    FontMetrics fm = g2d.getFontMetrics();
                    String s = String.valueOf(letter);
                    int textWidth = fm.stringWidth(s);
                    int textHeight = fm.getAscent();
                    g2d.drawString(s, x + (cellSize - textWidth) / 2, y + (cellSize + textHeight) / 2 - 4);
                } else {
                    g2d.setColor(Color.LIGHT_GRAY);
                    g2d.drawRect(x, y, cellSize, cellSize);
                }
            }
        }
        g2d.dispose();
        
        JFileChooser fileChooser = new JFileChooser();
        int result = fileChooser.showSaveDialog(this);
        if(result == JFileChooser.APPROVE_OPTION) {
            File saveFile = fileChooser.getSelectedFile();
            try {
                ImageIO.write(image, "png", saveFile);
                JOptionPane.showMessageDialog(this, "Solusi telah disimpan sebagai gambar.");
            } catch(IOException ex) {
                JOptionPane.showMessageDialog(this, "Error saat menyimpan gambar: " + ex.getMessage());
            }
        }
    }
    
    private Color getRandomColor(Random rand) {
        float hue = rand.nextFloat();
        float saturation = 0.7f + rand.nextFloat() * 0.3f;
        float brightness = 0.8f + rand.nextFloat() * 0.2f;
        return Color.getHSBColor(hue, saturation, brightness);
    }
    
    public static void main(String[] args) {
        SwingUtilities.invokeLater(new Runnable() {
            public void run() {
                new IQPuzzlerProGUI().setVisible(true);
            }
        });
    }
}
