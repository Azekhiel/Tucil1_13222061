# Tucil1_13222061_William Gerald Briandelo

Algoritma yang digunakan menggunakan metode brute-force, dengan pendekatan back-tracking. Awalnya, program akan membaca input dan menyiapkan board (inisialiasasi matrix kosong sesuai dengan input) untuk menempatkan semua piece yang diberikan. Dalam setiap langkah yang dijalankan, program akan menempatkan piece (dengan semua kemungkinan transformasinya) pada board, jika piece berhasil ditempatkan pada board (jika dan hanya jika posisi piece valid, yaitu tidak ada piece yang tumpang tindih dan juga tidak keluar board), maka program akan melanjutkan langkahnya secara rekursif. Namun, jika piece  tidak maka program akan kembali ke langkah sebelumnya dan mencoba langkah alternatif. Program akan terus berjalan hingga board berhasil terisi penuh dengan semua piece dipakai ataupun ketika semua kemungkinan langkah telah dijalankan.

Requirement: 
Perlu diinstal JDK (Java Development Kit) : https://www.oracle.com/id/java/technologies/downloads/
Dan Java Platform Extension for Visual Studio Code jika menggunakan VSCode

Cara menjalankan Program: 
Pastikan file input terletak pada directory yang sama dengan program yang akan dijalankan, lalu:

Jika menggunakan terminal: 
buka terminal
arahkan ke directory tempat program java disimpan (menggunakan cd, misal: cd Kuliah/SMT 6/Strategi Algoritma)
tuliskan javac IQPuzzlePro.java dan tekan enter
lalu tuliskan java IQPuzzlePro
Maka Program akan berjalan

Jika menggunakan VSCode:
Buka program dengan VSCode dan tekan "Run"
Maka program akan berjalan
