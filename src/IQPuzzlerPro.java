import java.io.*;
import java.util.*;

public class IQPuzzlerPro {
    static int N, M;
    static char[][] board;
    static ArrayList<Piece> pieces = new ArrayList<>();
    static long iterationCount = 0;
    
    public static void main(String[] args) throws Exception {
        Scanner sc = new Scanner(System.in);
        System.out.print("Masukkan nama file test case: ");
        String filename = sc.nextLine();
        readInput(filename);

        long start = System.currentTimeMillis();
        boolean solved = solve(0);
        long end = System.currentTimeMillis();

        if (solved) {
            System.out.println("\nSolusi ditemukan:");
            printBoard();
        } else {
            System.out.println("\nTidak ada solusi yang ditemukan.");
        }
        System.out.println("Waktu pencarian: " + (end - start) + " ms");
        System.out.println("Banyak kasus yang ditinjau: " + iterationCount);
        
        System.out.print("Apakah anda ingin menyimpan solusi? (ya/tidak): ");
        String resp = sc.nextLine();
        if(resp.equalsIgnoreCase("ya")) {
            saveSolution("solusi_"+filename);
            System.out.println("Solusi telah disimpan ke solusi_"+filename);
        }
    }
    
    static void readInput(String filename) throws Exception {
        BufferedReader br = new BufferedReader(new FileReader(filename));
        String header = br.readLine();
        String[] parts = header.trim().split("\\s+");
        N = Integer.parseInt(parts[0]);
        M = Integer.parseInt(parts[1]);

        // Inisialisasi board sel kosong dengan '.'
        board = new char[N][M];
        for (int i = 0; i < N; i++) {
            Arrays.fill(board[i], '.');
        }
        
        br.readLine();
        
        ArrayList<String> allLines = new ArrayList<>();
        String line;
        while ((line = br.readLine()) != null) {
            if (!line.trim().isEmpty()) {
                allLines.add(line.trim());
            }
        }
        br.close();
        
        int i = 0;
        while (i < allLines.size()) {
            String current = allLines.get(i);
            char letter = current.charAt(0);
            ArrayList<String> shapeLines = new ArrayList<>();
            shapeLines.add(current);
            i++;
            while (i < allLines.size() && allLines.get(i).charAt(0) == letter) {
                shapeLines.add(allLines.get(i));
                i++;
            }
            
            int maxCols = 0;
            for (String s : shapeLines) {
                if (s.length() > maxCols) maxCols = s.length();
            }
            int rows = shapeLines.size();
            char[][] shape = new char[rows][maxCols];
            for (int r = 0; r < rows; r++) {
                String s = shapeLines.get(r);
                for (int c = 0; c < maxCols; c++) {
                    if (c < s.length()) {
                        shape[r][c] = s.charAt(c);
                    } else {
                        shape[r][c] = '.';
                    }
                }
            }
            pieces.add(new Piece(letter, shape));
        }
    }
    
    // Fungsi rekursif backtracking
    // Basis: jika semua piece telah ditempatkan, periksa apakah board telah terisi penuh
    static boolean solve(int index) {
        if (index == pieces.size()) {
            return isBoardFull();
        }
        Piece current = pieces.get(index);
        ArrayList<char[][]> forms = current.getTransformations();
        for (char[][] shape : forms) {
            for (int r = 0; r < N; r++) {
                for (int c = 0; c < M; c++) {
                    iterationCount++;
                    if (canPlace(shape, r, c)) {
                        place(shape, r, c, current.letter);
                        if (solve(index + 1)) {
                            return true;
                        }
                        remove(shape, r, c);
                    }
                }
            }
        }
        return false;
    }
    
    static boolean isBoardFull() {
        for (int i = 0; i < N; i++) {
            for (int j = 0; j < M; j++) {
                if (board[i][j] == '.') {
                    return false;
                }
            }
        }
        return true;
    }
    
    static boolean canPlace(char[][] shape, int r, int c) {
        int rows = shape.length;
        int cols = shape[0].length;
        if (r + rows > N || c + cols > M) return false;
        for (int i = 0; i < rows; i++) {
            for (int j = 0; j < cols; j++) {
                if (shape[i][j] != '.' && board[r + i][c + j] != '.') {
                    return false;
                }
            }
        }
        return true;
    }
    
    static void place(char[][] shape, int r, int c, char letter) {
        int rows = shape.length;
        int cols = shape[0].length;
        for (int i = 0; i < rows; i++) {
            for (int j = 0; j < cols; j++) {
                if (shape[i][j] != '.') {
                    board[r + i][c + j] = letter;
                }
            }
        }
    }
    
    static void remove(char[][] shape, int r, int c) {
        int rows = shape.length;
        int cols = shape[0].length;
        for (int i = 0; i < rows; i++) {
            for (int j = 0; j < cols; j++) {
                if (shape[i][j] != '.') {
                    board[r + i][c + j] = '.';
                }
            }
        }
    }
    
    static void printBoard() {
        for (int i = 0; i < N; i++) {
            StringBuilder sb = new StringBuilder();
            for (int j = 0; j < M; j++) {
                sb.append(board[i][j]);
            }
            System.out.println(sb.toString());
        }
    }
    
    static void saveSolution(String filename) throws Exception {
        PrintWriter pw = new PrintWriter(new FileWriter(filename));
        for (int i = 0; i < N; i++) {
            StringBuilder sb = new StringBuilder();
            for (int j = 0; j < M; j++) {
                sb.append(board[i][j]);
            }
            pw.println(sb.toString());
        }
        pw.close();
    }
    
    // Kelas untuk piece blok puzzle
    static class Piece {
        char letter;
        char[][] shape;
        
        Piece(char letter, char[][] shape) {
            this.letter = letter;
            this.shape = shape;
        }
        
        // Transformasi blok original, rotasi 90, rotasi 180, rotasi 270, dan mirror horizontal
        ArrayList<char[][]> getTransformations() {
            ArrayList<char[][]> list = new ArrayList<>();
            list.add(shape);

            char[][] r90 = rotate(shape);
            list.add(r90);

            char[][] r180 = rotate(r90);
            list.add(r180);

            char[][] r270 = rotate(r180);
            list.add(r270);
            
            char[][] mirrorOriginal = mirror(shape);
            list.add(mirrorOriginal);
            return list;
        }
        
        // Rotasi 90 derajat searah jarum jam
        char[][] rotate(char[][] mat) {
            int rows = mat.length;
            int cols = mat[0].length;
            char[][] rotated = new char[cols][rows];
            for (int i = 0; i < rows; i++) {
                for (int j = 0; j < cols; j++) {
                    rotated[j][rows - 1 - i] = mat[i][j];
                }
            }
            return rotated;
        }
        
        // Mirror horizontal
        char[][] mirror(char[][] mat) {
            int rows = mat.length;
            int cols = mat[0].length;
            char[][] mirrored = new char[rows][cols];
            for (int i = 0; i < rows; i++) {
                for (int j = 0; j < cols; j++) {
                    mirrored[i][cols - 1 - j] = mat[i][j];
                }
            }
            return mirrored;
        }
    }
}
