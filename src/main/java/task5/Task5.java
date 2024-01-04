package task5;

import javax.swing.*;
import javax.swing.border.TitledBorder;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.Arrays;

import static java.lang.Math.max;
import static java.lang.Math.min;
import static javax.imageio.ImageIO.read;

public class Task5 {
    static BufferedImage img;
    static BufferedImage img1;
    static int highFilterNum = 1;
    static int lowFilterNum = 1;
    static int gaussFilterNum = 3;
    static int medianFilterNum = 3;

    static double[][] lowFilterFirstType = {
            {1.0 / 9, 1.0 / 9, 1.0 / 9},
            {1.0 / 9, 1.0 / 9, 1.0 / 9},
            {1.0 / 9, 1.0 / 9, 1.0 / 9}
    };

    static double[][] lowFilterSecondType = {
            {0.1, 0.1, 0.1},
            {0.1, 0.2, 0.1},
            {0.1, 0.1, 0.1}
    };

    static double[][] highFilterFirstType = {
            {-1, -1, -1},
            {-1, 9, -1},
            {-1, -1, -1}
    };

    static double[][] highFilterSecondType = {
            {0, -1, 0},
            {-1, 5, -1},
            {0, -1, 0}
    };

    static double[][] highFilterThirdType = {
            {1, -2, 1},
            {-2, 5, -2},
            {1, -2, 1}
    };

    public static void main(String[] args) {
        JFrame jFrame = new JFrame("Task5");
        jFrame.setLayout(new BorderLayout());
        jFrame.setSize(1200, 600);

        JPanel imagePanel = new JPanel();
        imagePanel.setBorder(new TitledBorder("Изображение"));

        JPanel resultPanel = new JPanel();
        resultPanel.setBorder(new TitledBorder("Результат"));
        resultPanel.setLayout(new BorderLayout());

        JPanel controlPanel = new JPanel();
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

            JLabel jLabel = new JLabel(new ImageIcon(img));

            imagePanel.removeAll();
            imagePanel.add(jLabel);
            imagePanel.updateUI();
        });

        JButton srcImgButton = new JButton("Исходное");
        srcImgButton.addActionListener(el -> {
            img1 = img;

            resultPanel.removeAll();
            resultPanel.add(new JLabel(new ImageIcon(img1)), BorderLayout.CENTER);
            resultPanel.updateUI();
        });

        JButton highFilterButton = new JButton("Высокочастнотный");
        highFilterButton.addActionListener(el -> {
            double[][] filter;

            if (highFilterNum == 1) {
                filter = highFilterFirstType;
            } else if (highFilterNum == 2) {
                filter = highFilterSecondType;
            } else {
                filter = highFilterThirdType;
            }

            img1 = getFilteredImg(filter);

            resultPanel.removeAll();
            resultPanel.add(new JLabel(new ImageIcon(img1)), BorderLayout.CENTER);
            resultPanel.updateUI();
        });

        JButton lowFilterButton = new JButton("Низкочастотный");
        lowFilterButton.addActionListener(el -> {
            double[][] filter;

            if (lowFilterNum == 1) {
                filter = lowFilterFirstType;
            } else {
                filter = lowFilterSecondType;
            }

            img1 = getFilteredImg(filter);

            resultPanel.removeAll();
            resultPanel.add(new JLabel(new ImageIcon(img1)), BorderLayout.CENTER);
            resultPanel.updateUI();
        });

        JButton gaussFilterButton = new JButton("Гауссов");
        gaussFilterButton.addActionListener(el -> {
            double[] pascalVector = getPascalVector(gaussFilterNum);
            int len = pascalVector.length;
            double[][] gaussMatrix = new double[len][len];
            double amount = 0;

            for (int i = 0; i < len; i++) {
                for (int j = 0; j < len; j++) {
                    gaussMatrix[i][j] = pascalVector[i] * pascalVector[j];
                    amount += gaussMatrix[i][j];
                }
            }

            for (int i = 0; i < len; i++) {
                for (int j = 0; j < len; j++) {
                    gaussMatrix[i][j] /= amount;
                }
            }

            img1 = getFilteredImg(gaussMatrix);

            resultPanel.removeAll();
            resultPanel.add(new JLabel(new ImageIcon(img1)), BorderLayout.CENTER);
            resultPanel.updateUI();
        });

        JButton medianFilterButton = new JButton("Медианный");
        medianFilterButton.addActionListener(el -> {
            int len = medianFilterNum;
            int size = len * len;
            int[] resRedVector = new int[size];
            int[] resGreenVector = new int[size];
            int[] resBlueVector = new int[size];

            int left = -len / 2;
            int right = len / 2 + 1;
            int delta = -left;

            int width = img.getWidth();
            int height = img.getHeight();
            int type = img.getType();

            BufferedImage img2 = new BufferedImage(width, height, type);

            for (int i = 0; i < width; i++) {
                for (int j = 0; j < height; j++) {
                    int idx = 0;

                    for (int k = left; k < right; k++) {
                        for (int l = left; l < right; l++) {
                            int idxX = max(0, min(i + k, width - 1));
                            int idxY = max(0, min(j + l, height - 1));

                            Color curColor = new Color(img1.getRGB(idxX, idxY));
                            int curRed = curColor.getRed();
                            int curGreen = curColor.getGreen();
                            int curBlue = curColor.getBlue();

                            resRedVector[idx] = curRed;
                            resGreenVector[idx] = curGreen;
                            resBlueVector[idx] = curBlue;

                            ++idx;
                        }
                    }

                    Arrays.sort(resRedVector);
                    Arrays.sort(resGreenVector);
                    Arrays.sort(resBlueVector);

                    int resRed = resRedVector[delta];
                    int resGreen = resGreenVector[delta];
                    int resBlue = resBlueVector[delta];

                    Color resColor = new Color(resRed, resGreen, resBlue);
                    img2.setRGB(i, j, resColor.getRGB());
                }
            }

            img1 = img2;

            resultPanel.removeAll();
            resultPanel.add(new JLabel(new ImageIcon(img1)), BorderLayout.CENTER);
            resultPanel.updateUI();
        });

        controlPanel.add(loadButton);
        controlPanel.add(srcImgButton);
        controlPanel.add(highFilterButton);
        controlPanel.add(lowFilterButton);
        controlPanel.add(gaussFilterButton);
        controlPanel.add(medianFilterButton);

        JPanel parameterPanel = new JPanel();
        parameterPanel.setBorder(new TitledBorder("Параметры"));
        JPanel inPP = new JPanel();
        inPP.setLayout(new GridLayout(4, 2));

        SpinnerNumberModel snmHighFilter = new SpinnerNumberModel(1, 1, 3, 1);
        SpinnerNumberModel snmLowFilter = new SpinnerNumberModel(1, 1, 2, 1);
        SpinnerNumberModel snmGaussFilter = new SpinnerNumberModel(3, 3, Integer.MAX_VALUE, 2);
        SpinnerNumberModel snmMedianFilter = new SpinnerNumberModel(3, 3, Integer.MAX_VALUE, 2);

        JSpinner highFilterSpinner = new JSpinner(snmHighFilter);
        highFilterSpinner.addChangeListener(el -> highFilterNum = (int) highFilterSpinner.getValue());

        JSpinner lowFilterSpinner = new JSpinner(snmLowFilter);
        lowFilterSpinner.addChangeListener(el -> lowFilterNum = (int) lowFilterSpinner.getValue());

        JSpinner gaussFilterSpinner = new JSpinner(snmGaussFilter);
        gaussFilterSpinner.addChangeListener(el -> gaussFilterNum = (int) gaussFilterSpinner.getValue());

        JSpinner medianFilterSpinner = new JSpinner(snmMedianFilter);
        medianFilterSpinner.addChangeListener(el -> medianFilterNum = (int) medianFilterSpinner.getValue());

        inPP.add(new JLabel("Тип высокочастотного"));
        inPP.add(highFilterSpinner);
        inPP.add(new JLabel("Тип низкочастного"));
        inPP.add(lowFilterSpinner);
        inPP.add(new JLabel("Степень Гауссового"));
        inPP.add(gaussFilterSpinner);
        inPP.add(new JLabel("Степень медианного"));
        inPP.add(medianFilterSpinner);

        parameterPanel.add(inPP);

        jFrame.add(imagePanel, BorderLayout.WEST);
        jFrame.add(resultPanel, BorderLayout.CENTER);
        jFrame.add(controlPanel, BorderLayout.SOUTH);
        jFrame.add(parameterPanel, BorderLayout.EAST);

        jFrame.setVisible(true);
    }

    static BufferedImage getFilteredImg(double[][] filter) {
        int width = img.getWidth();
        int height = img.getHeight();
        int type = img.getType();

        BufferedImage img2 = new BufferedImage(width, height, type);
        int left = -filter.length / 2;
        int right = filter.length / 2 + 1;
        int delta = -left;

        for (int i = 0; i < width; i++) {
            for (int j = 0; j < height; j++) {
                int resRed = 0;
                int resGreen = 0;
                int resBlue = 0;

                for (int k = left; k < right; k++) {
                    for (int l = left; l < right; l++) {
                        int idxX = max(0, min(i + k, width - 1));
                        int idxY = max(0, min(j + l, height - 1));

                        double multi = filter[k + delta][l + delta];
                        Color curColor = new Color(img1.getRGB(idxX, idxY));
                        int curRed = curColor.getRed();
                        int curGreen = curColor.getGreen();
                        int curBlue = curColor.getBlue();

                        resRed += curRed * multi;
                        resGreen += curGreen * multi;
                        resBlue += curBlue * multi;
                    }
                }

                resRed = max(0, min(255, resRed));
                resGreen = max(0, min(255, resGreen));
                resBlue = max(0, min(255, resBlue));

                Color resColor = new Color(resRed, resGreen, resBlue);
                img2.setRGB(i, j, resColor.getRGB());
            }
        }

        return img2;
    }

    static double[] getPascalVector(int num) {
        if (num == 3) {
            return new double[]{1, 2, 1};
        }

        double[] resVector = new double[num];
        resVector[0] = 1;
        resVector[resVector.length - 1] = 1;

        double[] prevVector = getPascalVector(num - 1);

        for (int i = 1; i < resVector.length - 1; i++) {
            resVector[i] = prevVector[i - 1] + prevVector[i];
        }

        return resVector;
    }
}
