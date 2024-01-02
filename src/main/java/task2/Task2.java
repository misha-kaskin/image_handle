package task2;

import javax.swing.*;
import javax.swing.border.TitledBorder;
import javax.swing.filechooser.FileFilter;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import static java.lang.Math.pow;
import static javax.imageio.ImageIO.read;

public class Task2 {
    static BufferedImage img;
    static BufferedImage img1;
    static File file;

    static List<Interval> intervalList = new ArrayList<>();

    public static void main(String[] args) {
        JFrame jFrame = new JFrame("Task2");
        jFrame.setSize(1000, 600);
        jFrame.setLayout(new BorderLayout());

        JPanel imagePanel = new JPanel();
        imagePanel.setBorder(new TitledBorder("Изображение"));

        jFrame.add(imagePanel, BorderLayout.WEST);

        JPanel controlPanel = new JPanel();
        controlPanel.setBorder(new TitledBorder("Контроль"));
        JButton loadButton = new JButton("Загрузить");
        loadButton.addActionListener((el) -> {
            JFileChooser jFileChooser = new JFileChooser("src/main/resources/");
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

            imagePanel.removeAll();
            imagePanel.add(new JLabel(new ImageIcon(file.getAbsolutePath())));
            imagePanel.updateUI();
        });

        controlPanel.add(loadButton);

        jFrame.add(controlPanel, BorderLayout.SOUTH);

        JPanel parameterPanel = new JPanel();
        parameterPanel.setBorder(new TitledBorder("Параметры"));

        JPanel inPP = new JPanel();
        inPP.setLayout(new GridLayout(2, 2));

        SpinnerNumberModel snmQuantum = new SpinnerNumberModel(8, 0, 8, 1);
        JSpinner quantumSpinner = new JSpinner(snmQuantum);
        quantumSpinner.addChangeListener(e -> {
            int width = img.getWidth();
            int height = img.getHeight();
            int type = img.getType();
            img1 = new BufferedImage(width, height, type);

            int count = (int) quantumSpinner.getValue();

            for (int i = 0; i < width; i++) {
                for (int j = 0; j < height; j++) {
                    Color color = new Color(img.getRGB(i, j));
                    int red = color.getRed();
                    int blue = color.getBlue();
                    int green = color.getGreen();

                    int rgb = (int) (0.3 * red + 0.59 * green + 0.11 * blue);

                    int step = (int) pow(2, 8 - count);
                    int tmpRgb = rgb / step;
                    rgb = (step * tmpRgb + step * (tmpRgb + 1)) / 2;

                    img1.setRGB(i, j, new Color(rgb, rgb, rgb).getRGB());
                }
            }

            imagePanel.removeAll();
            imagePanel.add(new JLabel(new ImageIcon(img1)));
            imagePanel.updateUI();
        });

        inPP.add(new JLabel("Цвета"));
        inPP.add(quantumSpinner);

        JCheckBox solarization = new JCheckBox();
        solarization.addItemListener(e -> {
            boolean selected = solarization.isSelected();

            if (selected) {
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

                        rgb = (int) ((-255f / 16256) * rgb * rgb + 65025f / 16256 * rgb);
                        img1.setRGB(i, j, new Color(rgb, rgb, rgb).getRGB());
                    }
                }
            } else {
                img1 = img;
            }

            imagePanel.removeAll();
            imagePanel.add(new JLabel(new ImageIcon(img1)));
            imagePanel.updateUI();
        });
        inPP.add(new JLabel("Соляризация"));
        inPP.add(solarization);

        parameterPanel.add(inPP);

        JPanel centerPanel = new JPanel();
        centerPanel.setBorder(new TitledBorder("Псевдораскрашивание"));
        centerPanel.setLayout(new BorderLayout());

        JPanel inCP = new JPanel();
        JPanel midCP = new JPanel();
        JPanel upCP = new JPanel();

        SpinnerNumberModel snmNumOfIntervals = new SpinnerNumberModel(5, 1, 254, 1);
        JSpinner numOfIntervalsSpinner = new JSpinner(snmNumOfIntervals);
        numOfIntervalsSpinner.addChangeListener(e -> {
            int countOfIntervals = (int) numOfIntervalsSpinner.getValue();
            midCP.removeAll();
            midCP.setLayout(new GridLayout(countOfIntervals + 1, 5));
            midCP.add(new JLabel("от          "));
            midCP.add(new JLabel("до          "));
            midCP.add(new JLabel("красный     "));
            midCP.add(new JLabel("зеленый     "));
            midCP.add(new JLabel("синий       "));

            intervalList.clear();

            for (int i = 0; i < countOfIntervals; i++) {
                Interval inter = new Interval();
                intervalList.add(inter);

                SpinnerNumberModel snmFrom = new SpinnerNumberModel(0, 0, 256, 1);
                SpinnerNumberModel snmTo = new SpinnerNumberModel(0, 0, 256, 1);
                SpinnerNumberModel snmRed = new SpinnerNumberModel(0, 0, 256, 1);
                SpinnerNumberModel snmGreen = new SpinnerNumberModel(0, 0, 256, 1);
                SpinnerNumberModel snmBlue = new SpinnerNumberModel(0, 0, 256, 1);

                JSpinner fromSpinner = new JSpinner(snmFrom);
                JSpinner toSpinner = new JSpinner(snmTo);
                JSpinner redSpinner = new JSpinner(snmRed);
                JSpinner greenSpinner = new JSpinner(snmGreen);
                JSpinner blueSpinner = new JSpinner(snmBlue);

                fromSpinner.addChangeListener(el -> inter.from = (int) fromSpinner.getValue());
                toSpinner.addChangeListener(el -> inter.to = (int) toSpinner.getValue());
                redSpinner.addChangeListener(el -> inter.r = (int) redSpinner.getValue());
                greenSpinner.addChangeListener(el -> inter.g = (int) greenSpinner.getValue());
                blueSpinner.addChangeListener(el -> inter.b = (int) blueSpinner.getValue());

                midCP.add(fromSpinner);
                midCP.add(toSpinner);
                midCP.add(redSpinner);
                midCP.add(greenSpinner);
                midCP.add(blueSpinner);
            }

            midCP.setBorder(new TitledBorder(""));
            midCP.updateUI();
        });

        inCP.add(midCP);
        upCP.add(new JLabel("Количество отрезков"));
        upCP.add(numOfIntervalsSpinner);
        centerPanel.add(upCP, BorderLayout.NORTH);
        centerPanel.add(inCP, BorderLayout.CENTER);
        JButton apply = new JButton("Применить");
        apply.addActionListener(el -> {
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
                    Color c = new Color(rgb, rgb, rgb);

                    for (Interval inter : intervalList) {
                        if (inter.from <= rgb && rgb <= inter.to) {
                            c = new Color(inter.r, inter.g, inter.b);
                        }
                    }

                    img1.setRGB(i, j, c.getRGB());
                }
            }

            imagePanel.removeAll();
            imagePanel.add(new JLabel(new ImageIcon(img1)));
            imagePanel.updateUI();
        });

        centerPanel.add(apply, BorderLayout.SOUTH);

        jFrame.add(centerPanel, BorderLayout.CENTER);
        jFrame.add(parameterPanel, BorderLayout.EAST);

        jFrame.setVisible(true);
    }

    static class Interval {
        int from;
        int to;
        int r;
        int g;
        int b;
    }
}
