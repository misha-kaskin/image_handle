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
    static int countLines = 1;

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

        JButton findLineButton = new JButton("Найти прямую");
        findLineButton.addActionListener(el -> {
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

            Point[] maxValues = getKMax(H);

            for (int k = 0; k < countLines; k++) {
                int maxJ = maxValues[k].y;
                int maxI = maxValues[k].x;

                double a = cos(maxJ);
                double b = sin(maxJ);
                int maxR = maxI;

                for (int i = 0; i < width; i++) {
                    for (int j = 0; j < height; j++) {
                        int res = (int) ceil(a * i + b * j);

                        if (res == maxR) {
                            img2.setRGB(i, j, Color.blue.getRGB());
                        }
                    }
                }
            }

            img1 = img2;

            resultPanel.removeAll();
            resultPanel.add(new JLabel(new ImageIcon(img1)), BorderLayout.CENTER);
            resultPanel.updateUI();
        });

        JButton findCircleButton = new JButton("Найти окружность");
        findCircleButton.addActionListener(el -> {
            int width = img.getWidth();
            int height = img.getHeight();
            int type = img.getType();

            BufferedImage img2 = new BufferedImage(width, height, type);

            int rMax = min(width, height) / 2;
            int[][][] H = new int[rMax][width][height];

            for (int i = 0; i < width; i++) {
                for (int j = 0; j < height; j++) {
                    Color col = new Color(img.getRGB(i, j));
                    img2.setRGB(i, j, col.getRGB());

                    if (!(col.getRed() < 255 || col.getGreen() < 255 || col.getBlue() < 255)) {
                        continue;
                    }

                    for (int k = 0; k < width; k++) {
                        for (int l = 0; l < height; l++) {
                            int r = (int) ceil(sqrt(pow(i - k, 2) + pow(j - l, 2)));

                            if (k + r > width || l + r > height || l - r < 0 || k - r < 0) {
                                continue;
                            } else if (r < 0 || r > rMax - 1) {
                                continue;
                            }

                            H[r][k][l]++;
                        }
                    }
                }
            }

            Triple[] res = getKMax(H);

            for (int k = 0; k < countLines; k++) {
                Triple t = res[k];

                for (int i = 0; i < width; i++) {
                    for (int j = 0; j < height; j++) {
                        int radius = (int) ceil(sqrt(pow(t.a - i, 2) + pow(t.b - j, 2)));

                        if (radius == t.r) {
                            img2.setRGB(i, j, Color.blue.getRGB());
                        }
                    }
                }
            }

            img1 = img2;

            resultPanel.removeAll();
            resultPanel.add(new JLabel(new ImageIcon(img1)), BorderLayout.CENTER);
            resultPanel.updateUI();
        });

        controlPanel.add(loadButton);
        controlPanel.add(findLineButton);
        controlPanel.add(findCircleButton);

        JPanel parameterPanel = new JPanel();
        parameterPanel.setBorder(new TitledBorder("Параметры"));
        JPanel inPP = new JPanel();
        inPP.setLayout(new GridLayout(1, 2));
        SpinnerNumberModel snmCountLines = new SpinnerNumberModel(1, 1, 200, 1);
        JSpinner countLinesSpinner = new JSpinner(snmCountLines);
        countLinesSpinner.addChangeListener(el -> countLines = (int) countLinesSpinner.getValue());

        inPP.add(new JLabel("Количество"));
        inPP.add(countLinesSpinner);
        parameterPanel.add(inPP);

        jFrame.add(imagePanel, BorderLayout.WEST);
        jFrame.add(resultPanel, BorderLayout.CENTER);
        jFrame.add(controlPanel, BorderLayout.SOUTH);
        jFrame.add(parameterPanel, BorderLayout.EAST);

        jFrame.setVisible(true);
    }

    static Triple[] getKMax(int[][][] h) {
        Triple[] res = new Triple[countLines];

        int[][][] tmpH = new int[h.length][h[0].length][h[0][0].length];

        for (int i = 0; i < h.length; i++) {
            for (int j = 0; j < h[0].length; j++) {
                System.arraycopy(h[i][j], 0, tmpH[i][j], 0, h[0][0].length);
            }
        }

        for (int k = 0; k < countLines; k++) {
            int maxR = 0;
            int maxA = 0;
            int maxB = 0;

            for (int i = 0; i < tmpH.length; i++) {
                for (int j = 0; j < tmpH[0].length; j++) {
                    for (int l = 0; l < tmpH[0][0].length; l++) {
                        if (tmpH[i][j][l] > tmpH[maxR][maxA][maxB]) {
                            maxR = i;
                            maxA = j;
                            maxB = l;
                        }
                    }
                }
            }

            res[k] = new Triple(maxR, maxA, maxB);
            tmpH[maxR][maxA][maxB] = -1;
        }

        return res;
    }

    static Point[] getKMax(int[][] H) {
        Point[] res = new Point[countLines];

        int[][] tmpH = new int[H.length][H[0].length];

        for (int i = 0; i < H.length; i++) {
            System.arraycopy(H[i], 0, tmpH[i], 0, H[0].length);
        }

        for (int k = 0; k < countLines; k++) {
            int maxI = 0;
            int maxJ = 0;

            for (int i = 0; i < tmpH.length; i++) {
                for (int j = 0; j < tmpH[0].length; j++) {
                    if (tmpH[i][j] > tmpH[maxI][maxJ]) {
                        maxI = i;
                        maxJ = j;
                    }
                }
            }

            res[k] = new Point(maxI, maxJ);
            tmpH[maxI][maxJ] = -1;
        }

        return res;
    }

    static class Triple {
        int r;
        int a;
        int b;

        Triple(int r, int a, int b) {
            this.r = r;
            this.a = a;
            this.b = b;
        }
    }
}
