package task8;

import javax.swing.*;
import javax.swing.border.TitledBorder;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.LinkedList;
import java.util.List;

import static javax.imageio.ImageIO.read;

public class Task8 {
    static BufferedImage img;
    static BufferedImage img1;
    static int accuracy = 5;

    public static void main(String[] args) {
        JFrame jFrame = new JFrame("Task8");
        jFrame.setSize(1200, 800);
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
                img1 = read(file);
            } catch (IOException e) {
                e.printStackTrace();
            }

            img1 = img = binary(img1);

            imagePanel.removeAll();
            imagePanel.add(new JLabel(new ImageIcon(img)));
            imagePanel.updateUI();
        });

        JButton borderButton = new JButton("Граница");
        borderButton.addActionListener(el -> {
            int width = img.getWidth();
            int height = img.getHeight();
            int type = img.getType();

            BufferedImage img2 = new BufferedImage(width, height, type);
            List<Point> pointList = new LinkedList<>();

            for (int i = 0; i < width; i++) {
                for (int j = 0; j < height; j++) {
                    img2.setRGB(i, j, Color.WHITE.getRGB());
                }
            }

            for (int j = 0; j < height; j++) {
                for (int i = 0; i < width - 1; i++) {
                    Color c1 = new Color(img.getRGB(i, j));
                    Color c2 = new Color(img.getRGB(i + 1, j));

                    if (isEqualColor(c1, c2)) {
                        continue;
                    }

                    Point l = new Point(i + 1, j);
                    Point r = new Point(i, j);
                    Point t = getTestPoint(l, r);

                    pointList.add(l);

                    while (!t.equals(pointList.get(0))) {
                        if (isBlack(t)) {
                            l = t;
                            pointList.add(t);
                        } else {
                            r = t;
                        }

                        t = getTestPoint(l, r);
                    }

                    i = width - 1;
                    j = height;
                }
            }

            for (Point p : pointList) {
                img2.setRGB(p.x, p.y, Color.red.getRGB());
            }

            img1 = img2;

            resultPanel.removeAll();
            resultPanel.add(new JLabel(new ImageIcon(img1)), BorderLayout.CENTER);
            resultPanel.updateUI();
        });

        JButton approxButton = new JButton("Аппроксимация");
        JButton figureButton = new JButton("Тип фигуры");

        controlPanel.add(loadButton);
        controlPanel.add(borderButton);
        controlPanel.add(approxButton);
        controlPanel.add(figureButton);

        JPanel parameterPanel = new JPanel();
        parameterPanel.setBorder(new TitledBorder("Параметры"));
        JPanel inPP = new JPanel();
        inPP.setLayout(new GridLayout(1, 2));
        SpinnerNumberModel snmAccuracy = new SpinnerNumberModel(5, 0, 50, 1);
        JSpinner accuracySpinner = new JSpinner(snmAccuracy);
        accuracySpinner.addChangeListener(el -> accuracy = (int) accuracySpinner.getValue());

        inPP.add(new JLabel("Точность"));
        inPP.add(accuracySpinner);

        parameterPanel.add(inPP);

        jFrame.add(imagePanel, BorderLayout.WEST);
        jFrame.add(resultPanel, BorderLayout.CENTER);
        jFrame.add(parameterPanel, BorderLayout.EAST);
        jFrame.add(controlPanel, BorderLayout.SOUTH);

        jFrame.setVisible(true);
    }

    static Point getTestPoint(Point l, Point r) {
        int tX;
        int tY;

        if (l.x == r.x || l.y == r.y) {
            tX = r.x - (r.y - l.y);
            tY = r.y + r.x - l.x;
        } else {
            tX = (l.x + r.x - r.y + l.y) / 2;
            tY = (l.y + r.y + r.x - l.x) / 2;
        }

        return new Point(tX, tY);
    }

    static boolean isEqualColor(Color c1, Color c2) {
        return c1.getRed() == c2.getRed()
                && c1.getGreen() == c2.getGreen()
                && c1.getBlue() == c2.getBlue();
    }

    static boolean isBlack(Point p) {
        Color c = new Color(img.getRGB(p.x, p.y));
        return c.getRed() == 0 && c.getGreen() == 0 && c.getBlue() == 0;
    }

    static BufferedImage binary(BufferedImage im) {
        int width = im.getWidth();
        int height = im.getHeight();
        int type = im.getType();

        BufferedImage img2 = new BufferedImage(width, height, type);

        for (int i = 0; i < width; i++) {
            for (int j = 0; j < height; j++) {
                Color c = new Color(im.getRGB(i, j));
                if (c.getRed() < 128 && c.getGreen() < 128 && c.getBlue() < 128) {
                    img2.setRGB(i, j, Color.BLACK.getRGB());
                } else {
                    img2.setRGB(i, j, Color.WHITE.getRGB());
                }
            }
        }

        return img2;
    }
}
