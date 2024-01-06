package task7;

import javax.swing.*;
import javax.swing.border.TitledBorder;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

import static java.lang.Math.*;
import static javax.imageio.ImageIO.read;

public class Task7 {
    static BufferedImage img;
    static BufferedImage img1;

    public static void main(String[] args) {
        JFrame jFrame = new JFrame("Task7");
        jFrame.setSize(1200, 700);
        jFrame.setLayout(new BorderLayout());

        JPanel imagePanel = new JPanel();
        imagePanel.setBorder(new TitledBorder("Изображение"));

        JPanel resultPanel = new JPanel();
        resultPanel.setBorder(new TitledBorder("Результат"));
        resultPanel.setLayout(new BorderLayout());

        JPanel controlPanel = new JPanel();
        controlPanel.setBorder(new TitledBorder("Контроль"));
        JButton loadButton = new JButton("Загрузить");
        loadButton.addActionListener(el -> {
            JFileChooser jFileChooser = new JFileChooser("src/main/resources/");
            jFileChooser.showOpenDialog(jFileChooser);
            File file = jFileChooser.getSelectedFile();

            try {
                img1 = img = read(file);
            } catch (IOException e) {
                e.printStackTrace();
            }

            imagePanel.removeAll();
            imagePanel.add(new JLabel(new ImageIcon(img)));
            imagePanel.updateUI();
        });

        JButton findButton = new JButton("Найти прямую");
        findButton.addActionListener(el -> {
            int width = img.getWidth();
            int height = img.getHeight();
            int type = img.getType();

            BufferedImage img2 = new BufferedImage(width, height, type);

            int rMax = (int) sqrt(pow(width, 2) + pow(height, 2));
            int[][] H = new int[rMax][360];

            for (int i = 0; i < width; i++) {
                for (int j = 0; j < height; j++) {
                    Color col = new Color(img.getRGB(i, j));
                    img2.setRGB(i, j, col.getRGB());

                    if (!(col.getRed() < 255 || col.getBlue() < 255 || col.getGreen() < 255)) {
                        continue;
                    }

                    for (int k = 0; k < 360; k++) {
                        int r = (int) (i * cos(k) + j * sin(k));

                        if (r < 0 || r > rMax - 1) {
                            continue;
                        }

                        H[r][k]++;
                    }
                }
            }

            int maxI = 0;
            int maxJ = 0;

            for (int i = 0; i < rMax; i++) {
                for (int j = 0; j < 360; j++) {
                    if (H[i][j] > H[maxI][maxJ]) {
                        maxI = i;
                        maxJ = j;
                    }
                }
            }

            double a = cos(maxJ);
            double b = sin(maxJ);
            int maxR = maxI;

            for (int i = 0; i < width; i++) {
                int y = (int) ((maxR - a * i) / b);

                if (y < 0 || y > height - 1) {
                    continue;
                }

                img2.setRGB(i, y, Color.blue.getRGB());
            }

            img1 = img2;

            resultPanel.removeAll();
            resultPanel.add(new JLabel(new ImageIcon(img1)), BorderLayout.CENTER);
            resultPanel.updateUI();
        });

        controlPanel.add(loadButton);
        controlPanel.add(findButton);

        jFrame.add(imagePanel, BorderLayout.WEST);
        jFrame.add(resultPanel, BorderLayout.CENTER);
        jFrame.add(controlPanel, BorderLayout.SOUTH);

        jFrame.setVisible(true);
    }
}
