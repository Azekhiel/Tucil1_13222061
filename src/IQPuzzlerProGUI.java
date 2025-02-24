import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.io.*;

public class IQPuzzlerProGUI extends JFrame {
    private JTextArea boardArea;
    private JButton loadFileButton, solveButton, saveSolutionButton;
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
        saveSolutionButton = new JButton("Save Solution");
        
        solveButton.setEnabled(false);
        saveSolutionButton.setEnabled(false);
        
        topPanel.add(loadFileButton);
        topPanel.add(solveButton);
        topPanel.add(saveSolutionButton);
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
                JOptionPane.showMessageDialog(this, "Solusi telah disimpan.");
            } catch(IOException ex) {
                JOptionPane.showMessageDialog(this, "Error saat menyimpan solusi: " + ex.getMessage());
            }
        }
    }
    
    public static void main(String[] args) {
        SwingUtilities.invokeLater(new Runnable() {
            public void run() {
                new IQPuzzlerProGUI().setVisible(true);
            }
        });
    }
}
