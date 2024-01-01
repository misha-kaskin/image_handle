package task1;

import javax.swing.*;
import javax.swing.border.TitledBorder;
import javax.swing.filechooser.FileFilter;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.Map;
import java.util.TreeMap;

import static javax.imageio.ImageIO.read;

public class Task1 {
    static BufferedImage img;
    static BufferedImage img1;
    static File file;

    public static void main(String[] args) {
        JFrame jFrame = new JFrame("Task1");
        jFrame.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
        jFrame.setSize(1400, 800);
        jFrame.setLayout(new BorderLayout());

        JPanel imagePanel = new JPanel();
        imagePanel.setVisible(true);
        imagePanel.setBorder(new TitledBorder("Изображение"));

        JPanel resultPanel = new JPanel();
        resultPanel.setVisible(true);
        resultPanel.setBorder(new TitledBorder("Результат"));

        JPanel controlPanel = new JPanel();
        controlPanel.setVisible(true);
        controlPanel.setBorder(new TitledBorder("Управление"));

        JButton histButton = new JButton("Гистограмма");
        histButton.addActionListener((el) -> {
            BufferedImage myPicture = img1;
            int width = myPicture.getWidth();
            int height = myPicture.getHeight();

            Map<Integer, Double> map = new TreeMap<>();

            for (int i = 0; i < width; i++) {
                for (int j = 0; j < height; j++) {
                    Color color = new Color(myPicture.getRGB(i, j));
                    int red = color.getRed();

                    if (map.containsKey(red)) {
                        Double aDouble = map.get(red);
                        aDouble += 1;
                        map.put(red, aDouble);
                    } else {
                        map.put(red, 1d);
                    }
                }
            }

            for (Map.Entry<Integer, Double> integerDoubleEntry : map.entrySet()) {
                Double value = integerDoubleEntry.getValue();
                integerDoubleEntry.setValue(value / (width * height));
            }

            resultPanel.removeAll();
            resultPanel.add(BarChart.getBarChart(map));
            resultPanel.updateUI();
        });

        JButton loadButton = new JButton("Загрузить");
        loadButton.addActionListener((el) -> {
            JFileChooser jFileChooser = new JFileChooser("src/main/java/task1/");
            jFileChooser.setDialogTitle("Выбор директории");

            jFileChooser.setFileFilter(new FileFilter() {
                @Override
                public boolean accept(File f) {
                    if (f.isDirectory()) {
                        return true;
                    }

                    return f.getName().endsWith(".png") || f.getName().endsWith(".jpg");
                }

                @Override
                public String getDescription() {
                    return "Image";
                }
            });

            jFileChooser.showOpenDialog(jFileChooser);
            file = jFileChooser.getSelectedFile();
            try {
                img1 = img = read(file);
            } catch (IOException e) {
                e.printStackTrace();
            }

            histButton.doClick();
            imagePanel.removeAll();
            imagePanel.add(new JLabel(new ImageIcon(file.getAbsolutePath())));
            imagePanel.updateUI();
        });

        JButton grayScale = new JButton("GrayScale");
        grayScale.addActionListener((el) -> {
            int width = img.getWidth();
            int height = img.getHeight();
            int type = img.getType();
            img1 = new BufferedImage(width, height, type);

            for (int i = 0; i < width; i++) {
                for (int j = 0; j < height; j++) {
                    Color color = new Color(img.getRGB(i, j));
                    int red = color.getRed();
                    int blue = color.getBlue();
                    int green = color.getGreen();

                    int rgb = (int) (0.3 * red + 0.59 * green + 0.11 * blue);
                    img1.setRGB(i, j, new Color(rgb, rgb, rgb).getRGB());
                }
            }

            img = img1;
            resultPanel.removeAll();
            histButton.doClick();
            imagePanel.removeAll();
            imagePanel.add(new JLabel(new ImageIcon(img1)));
            imagePanel.updateUI();
        });

        controlPanel.add(loadButton);
        controlPanel.add(grayScale);

        JPanel parameterPanel = new JPanel();
        parameterPanel.setVisible(true);
        parameterPanel.setBorder(new TitledBorder("Параметры"));

        JPanel inPP = new JPanel();
        inPP.setVisible(true);
        inPP.setSize(100, 30);
        inPP.setLayout(new GridLayout(6, 2));

        SpinnerNumberModel snm1 = new SpinnerNumberModel(0, -255, 255, 1);
        SpinnerNumberModel snm2 = new SpinnerNumberModel(100, 0, 255, 1);
        SpinnerNumberModel snm3 = new SpinnerNumberModel(150, 0, 255, 1);
        SpinnerNumberModel snmNegative = new SpinnerNumberModel(0, 0, 255, 1);
        SpinnerNumberModel snmBinary = new SpinnerNumberModel(128, 0, 255, 1);

        JSpinner negativeSpinner = new JSpinner(snmNegative);
        JButton negativeButton = new JButton("Негатив");
        negativeButton.addActionListener((el) -> {
            int width = img.getWidth();
            int height = img.getHeight();
            int type = img.getType();
            img1 = new BufferedImage(width, height, type);
            int delta = (int) negativeSpinner.getValue();

            for (int i = 0; i < width; i++) {
                for (int j = 0; j < height; j++) {
                    Color color = new Color(img.getRGB(i, j));
                    int red = color.getRed();
                    int green = color.getGreen();
                    int blue = color.getBlue();

                    if (red >= delta) {
                        red = 255 - red;
                    }

                    if (green >= delta) {
                        green = 255 - green;
                    }

                    if (blue >= delta) {
                        blue = 255 - blue;
                    }

                    img1.setRGB(i, j, new Color(red, green, blue).getRGB());
                }
            }

            histButton.doClick();
            imagePanel.removeAll();
            imagePanel.add(new JLabel(new ImageIcon(img1)));
            imagePanel.updateUI();
        });

        controlPanel.add(negativeButton);

        JSpinner binarySpinner = new JSpinner(snmBinary);
        JButton binaryButton = new JButton("Бинаризация");
        binaryButton.addActionListener((el) -> {
            int width = img.getWidth();
            int height = img.getHeight();
            int type = img.getType();
            img1 = new BufferedImage(width, height, type);
            int delta = (int) binarySpinner.getValue();

            for (int i = 0; i < width; i++) {
                for (int j = 0; j < height; j++) {
                    Color color = new Color(img.getRGB(i, j));
                    int red = color.getRed();
                    int blue = color.getBlue();
                    int green = color.getGreen();

                    if (red < delta) {
                        red = 0;
                    } else {
                        red = 255;
                    }

                    if (green < delta) {
                        green = 0;
                    } else {
                        green = 255;
                    }

                    if (blue < delta) {
                        blue = 0;
                    } else {
                        blue = 255;
                    }

                    img1.setRGB(i, j, new Color(red, green, blue).getRGB());
                }
            }

            histButton.doClick();
            imagePanel.removeAll();
            imagePanel.add(new JLabel(new ImageIcon(img1)));
            imagePanel.updateUI();
        });

        controlPanel.add(binaryButton);

        JSpinner jSpinner1 = new JSpinner(snm1);
        jSpinner1.addChangeListener((e -> {
            int width = img.getWidth();
            int height = img.getHeight();
            int type = img.getType();
            img1 = new BufferedImage(width, height, type);
            int delta = (int) jSpinner1.getValue();

            for (int i = 0; i < width; i++) {
                for (int j = 0; j < height; j++) {
                    Color color = new Color(img.getRGB(i, j));
                    int red = color.getRed();
                    int blue = color.getBlue();
                    int green = color.getGreen();

                    if (red + delta > 255) {
                        red = 255;
                    } else if (red + delta < 0) {
                        red = 0;
                    } else {
                        red += delta;
                    }

                    if (blue + delta > 255) {
                        blue = 255;
                    } else if (blue + delta < 0) {
                        blue = 0;
                    } else {
                        blue += delta;
                    }

                    if (green + delta > 255) {
                        green = 255;
                    } else if (green + delta < 0) {
                        green = 0;
                    } else {
                        green += delta;
                    }

                    img1.setRGB(i, j, new Color(red, green, blue).getRGB());
                }
            }

            histButton.doClick();
            imagePanel.removeAll();
            imagePanel.add(new JLabel(new ImageIcon(img1)));
            imagePanel.updateUI();
        }));

        JSpinner jSpinner2 = new JSpinner(snm2);
        JSpinner jSpinner3 = new JSpinner(snm3);
        JButton increaseContrast = new JButton("Увеличение контраста");
        increaseContrast.addActionListener((el) -> {
            int width = img.getWidth();
            int height = img.getHeight();
            int type = img.getType();
            img1 = new BufferedImage(width, height, type);
            int q1 = (int) jSpinner2.getValue();
            int q2 = (int) jSpinner3.getValue();

            if (q1 > q2) {
                int tmp = q1;
                q1 = q2;
                q2 = tmp;
            }

            for (int i = 0; i < width; i++) {
                for (int j = 0; j < height; j++) {
                    Color color = new Color(img.getRGB(i, j));
                    int red = color.getRed();
                    int blue = color.getBlue();
                    int green = color.getGreen();

                    if (red < q1) {
                        red = 0;
                    } else if (red > q2) {
                        red = 255;
                    } else {
                        red = 255 * (red - q1) / (q2 - q1);
                    }

                    if (blue < q1) {
                        blue = 0;
                    } else if (blue > q2) {
                        blue = 255;
                    } else {
                        blue = 255 * (blue - q1) / (q2 - q1);
                    }

                    if (green < q1) {
                        green = 0;
                    } else if (green > q2) {
                        green = 255;
                    } else {
                        green = 255 * (green - q1) / (q2 - q1);
                    }

                    img1.setRGB(i, j, new Color(red, green, blue).getRGB());
                }
            }

            histButton.doClick();
            imagePanel.removeAll();
            imagePanel.add(new JLabel(new ImageIcon(img1)));
            imagePanel.updateUI();
        });

        controlPanel.add(increaseContrast);

        JButton decreaseContrast = new JButton("Умеьшение контраста");
        decreaseContrast.addActionListener((el) -> {
            int width = img.getWidth();
            int height = img.getHeight();
            int type = img.getType();
            img1 = new BufferedImage(width, height, type);
            int q1 = (int) jSpinner2.getValue();
            int q2 = (int) jSpinner3.getValue();

            if (q1 > q2) {
                int tmp = q1;
                q1 = q2;
                q2 = tmp;
            }

            for (int i = 0; i < width; i++) {
                for (int j = 0; j < height; j++) {
                    Color color = new Color(img.getRGB(i, j));
                    int red = color.getRed();
                    int blue = color.getBlue();
                    int green = color.getGreen();

                    red = (int) (q1 + (float) red * (q2 - q1) / 255);
                    blue = (int) (q1 + (float) blue * (q2 - q1) / 255);
                    green = (int) (q1 + (float) green * (q2 - q1) / 255);

                    img1.setRGB(i, j, new Color(red, green, blue).getRGB());
                }
            }

            histButton.doClick();
            imagePanel.removeAll();
            imagePanel.add(new JLabel(new ImageIcon(img1)));
            imagePanel.updateUI();
        });

        controlPanel.add(decreaseContrast);

        inPP.add(new JLabel("Яркость"));
        inPP.add(jSpinner1);
        inPP.add(new JLabel("Негатив"));
        inPP.add(negativeSpinner);
        inPP.add(new JLabel("Бинаризация"));
        inPP.add(binarySpinner);
        inPP.add(new JLabel("Q1"));
        inPP.add(jSpinner2);
        inPP.add(new JLabel("Q2"));
        inPP.add(jSpinner3);

        SpinnerNumberModel snmGamma = new SpinnerNumberModel(2, 0, 255, 1);
        JSpinner gammaSpinner = new JSpinner(snmGamma);
        JButton gammaButton = new JButton("Гамма");
        gammaButton.addActionListener((el) -> {
            int width = img.getWidth();
            int height = img.getHeight();
            int type = img.getType();
            img1 = new BufferedImage(width, height, type);
            int gamma = (int) gammaSpinner.getValue();

            for (int i = 0; i < width; i++) {
                for (int j = 0; j < height; j++) {
                    Color color = new Color(img.getRGB(i, j));
                    int red = color.getRed();
                    int blue = color.getBlue();
                    int green = color.getGreen();

                    red = (int) (255 * Math.pow((float) red / 255, gamma));
                    blue = (int) (255 * Math.pow((float) blue / 255, gamma));
                    green = (int) (255 * Math.pow((float) green / 255, gamma));

                    img1.setRGB(i, j, new Color(red, green, blue).getRGB());
                }
            }

            histButton.doClick();
            imagePanel.removeAll();
            imagePanel.add(new JLabel(new ImageIcon(img1)));
            imagePanel.updateUI();
        });

        controlPanel.add(gammaButton);
        inPP.add(new JLabel("Гамма"));
        inPP.add(gammaSpinner);

        parameterPanel.add(inPP);


        jFrame.add(controlPanel, BorderLayout.SOUTH);
        jFrame.add(imagePanel, BorderLayout.WEST);
        jFrame.add(parameterPanel, BorderLayout.EAST);
        jFrame.add(resultPanel, BorderLayout.CENTER);
        jFrame.setVisible(true);
    }
}
