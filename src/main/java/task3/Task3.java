package task3;

import javax.swing.*;
import javax.swing.border.TitledBorder;
import javax.swing.filechooser.FileFilter;
import java.awt.*;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

import static java.lang.Math.*;
import static javax.imageio.ImageIO.read;

public class Task3 {
    static BufferedImage img;
    static BufferedImage img1;
    static BufferedImage img2;
    static BufferedImage qubImg;
    static BufferedImage linImg;
    static File file;
    static double scope;

    public static void main(String[] args) {
        JFrame jFrame = new JFrame("Task3");
        jFrame.setSize(1600, 600);
        jFrame.setLayout(new BorderLayout());

        JPanel imagePanel = new JPanel();
        imagePanel.setBorder(new TitledBorder("Изображение"));

        JPanel centerPanel = new JPanel();
        centerPanel.setBorder(new TitledBorder("Результат"));
        centerPanel.setLayout(new BorderLayout());

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

            JLabel jLabel = new JLabel(new ImageIcon(file.getAbsolutePath()));
            jLabel.addMouseListener(new MouseListener() {
                int fromX;
                int fromY;

                @Override
                public void mouseClicked(MouseEvent e) {

                }

                @Override
                public void mousePressed(MouseEvent e) {
                    fromX = e.getX();
                    fromY = e.getY();
                }

                @Override
                public void mouseReleased(MouseEvent e) {
                    centerPanel.removeAll();

                    int toX = e.getX();
                    int toY = e.getY();

                    int fromX1 = min(fromX, toX);
                    int toX1 = max(fromX, toX);

                    int fromY1 = min(fromY, toY);
                    int toY1 = max(fromY, toY);

                    int width = toX1 - fromX1;
                    int height = toY1 - fromY1;
                    int type = img.getType();
                    img1 = new BufferedImage(width, height, type);

                    for (int i = fromX1; i < toX1; i++) {
                        for (int j = fromY1; j < toY1; j++) {
                            img1.setRGB(i - fromX1, j - fromY1, img.getRGB(i, j));
                        }
                    }

                    centerPanel.add(new JLabel(new ImageIcon(img1)), BorderLayout.CENTER);
                    centerPanel.updateUI();
                }

                @Override
                public void mouseEntered(MouseEvent e) {

                }

                @Override
                public void mouseExited(MouseEvent e) {

                }
            });

            imagePanel.add(jLabel);
            imagePanel.updateUI();
        });

        controlPanel.add(loadButton);

        JPanel parameterPanel = new JPanel();
        parameterPanel.setBorder(new TitledBorder("Параметры"));
        JPanel inPP = new JPanel();
        inPP.setLayout(new GridLayout(3, 2));

        SpinnerNumberModel snmScope = new SpinnerNumberModel(1.0, 0.1, 5.0, 0.1);
        JSpinner scopeSpinner = new JSpinner(snmScope);
        scopeSpinner.addChangeListener(e -> {
            scope = (double) scopeSpinner.getValue();

            defaultScope();

            centerPanel.removeAll();
            centerPanel.add(new JLabel(new ImageIcon(img2)), BorderLayout.CENTER);
            centerPanel.updateUI();
        });

        JCheckBox biLinear = new JCheckBox();
        JCheckBox biQub = new JCheckBox();

        biLinear.addActionListener(e -> {
            boolean selected = biLinear.isSelected();

            if (!selected) {
                if (biQub.isSelected()) {
                    img2 = qubImg;
                } else {
                    defaultScope();
                }
            } else {
                int width = (int) (img1.getWidth() * scope);
                int height = (int) (img1.getHeight() * scope);
                int type = img1.getType();

                img2 = new BufferedImage(width, height, type);

                for (int i = 0; i < width; i++) {
                    for (int j = 0; j < height; j++) {
                        double x = i / scope;
                        double y = j / scope;

                        int ax = (int) x;
                        int ay = (int) y;

                        double dltX = x - ax;
                        double dltY = y - ay;

                        int width1 = img1.getWidth() - 1;
                        int height1 = img1.getHeight() - 1;

                        Color a = new Color(img1.getRGB(ax, ay));
                        Color b = new Color(img1.getRGB(min(ax + 1, width1), ay));
                        Color c = new Color(img1.getRGB(min(ax + 1, width1), min(ay + 1, height1)));
                        Color d = new Color(img1.getRGB(ax, min(ay + 1, height1)));

                        int redA = a.getRed();
                        int greenA = a.getGreen();
                        int blueA = a.getBlue();

                        int redB = b.getRed();
                        int greenB = b.getGreen();
                        int blueB = b.getBlue();

                        int redC = c.getRed();
                        int greenC = c.getGreen();
                        int blueC = c.getBlue();

                        int redD = d.getRed();
                        int greenD = d.getGreen();
                        int blueD = d.getBlue();

                        int redM = (int) (redA * (1 - dltX) + redB * dltX);
                        int greenM = (int) (greenA * (1 - dltX) + greenB * dltX);
                        int blueM = (int) (blueA * (1 - dltX) + blueB * dltX);

                        int redN = (int) (redD * (1 - dltX) + redC * dltX);
                        int greenN = (int) (greenD * (1 - dltX) + greenC * dltX);
                        int blueN = (int) (blueD * (1 - dltX) + blueC * dltX);

                        int redRes = (int) (redM * (1 - dltY) + redN * dltY);
                        int greenRes = (int) (greenM * (1 - dltY) + greenN * dltY);
                        int blueRes = (int) (blueM * (1 - dltY) + blueN * dltY);

                        Color res = new Color(redRes, greenRes, blueRes);

                        img2.setRGB(i, j, res.getRGB());
                    }
                }

                linImg = img2;
            }

            centerPanel.removeAll();
            centerPanel.add(new JLabel(new ImageIcon(img2)), BorderLayout.CENTER);
            centerPanel.updateUI();
        });

        biQub.addActionListener(el -> {
            boolean selected = biQub.isSelected();

            if (!selected) {
                if (biLinear.isSelected()) {
                    img2 = linImg;
                } else {
                    defaultScope();
                }
            } else {
                int width = (int) (img1.getWidth() * scope);
                int height = (int) (img1.getHeight() * scope);
                int type = img1.getType();

                img2 = new BufferedImage(width, height, type);

                for (int i = 0; i < width; i++) {
                    for (int j = 0; j < height; j++) {
                        double x = i / scope;
                        double y = j / scope;

                        int ax = (int) x;
                        int ay = (int) y;

                        double u = x - ax;
                        double v = y - ay;

                        Color[] colors = new Color[4];

                        for (int k = -1; k < 3; k++) {
                            int width1 = img1.getWidth() - 1;
                            int height1 = img1.getHeight() - 1;

                            int idxY = min(height1, max(0, ay + k));

                            Color c1 = new Color(img1.getRGB(max(0, ax - 1), idxY));
                            Color c2 = new Color(img1.getRGB(ax, idxY));
                            Color c3 = new Color(img1.getRGB(min(width1, ax + 1), idxY));
                            Color c4 = new Color(img1.getRGB(min(width1, ax + 2), idxY));

                            Color resColor = qubColor(c1, c2, c3, c4, u);
                            colors[k + 1] = resColor;
                        }

                        Color resColor = qubColor(colors[0], colors[1], colors[2], colors[3], v);
                        img2.setRGB(i, j, resColor.getRGB());
                    }
                }

                qubImg = img2;
            }

            centerPanel.removeAll();
            centerPanel.add(new JLabel(new ImageIcon(img2)), BorderLayout.CENTER);
            centerPanel.updateUI();
        });

        inPP.add(new JLabel("Масштаб"));
        inPP.add(scopeSpinner);
        inPP.add(new JLabel("Билинейная интерполяция"));
        inPP.add(biLinear);
        inPP.add(new JLabel("Бикубическая интерполяция"));
        inPP.add(biQub);

        parameterPanel.add(inPP);

        jFrame.add(imagePanel, BorderLayout.WEST);
        jFrame.add(centerPanel, BorderLayout.CENTER);
        jFrame.add(controlPanel, BorderLayout.SOUTH);
        jFrame.add(parameterPanel, BorderLayout.EAST);

        jFrame.setVisible(true);
    }

    static void defaultScope() {
        int width = (int) (img1.getWidth() * scope);
        int height = (int) (img1.getHeight() * scope);
        int type = img1.getType();

        img2 = new BufferedImage(width, height, type);

        for (int i = 0; i < width; i++) {
            for (int j = 0; j < height; j++) {
                int x = (int) (i / scope);
                int y = (int) (j / scope);
                img2.setRGB(i, j, img1.getRGB(x, y));
            }
        }
    }

    static Color qubColor(Color c1, Color c2, Color c3, Color c4, double u) {
        int resRed = qubColorTmp(c1.getRed(), c2.getRed(), c3.getRed(), c4.getRed(), u);
        int resGreen = qubColorTmp(c1.getGreen(), c2.getGreen(), c3.getGreen(), c4.getGreen(), u);
        int resBlue = qubColorTmp(c1.getBlue(), c2.getBlue(), c3.getBlue(), c4.getBlue(), u);

        return new Color(resRed, resGreen, resBlue);
    }

    static int qubColorTmp(int c1, int c2, int c3, int c4, double u) {
        double redB = (double) (c1 + c3 - 2 * c2) / 2;
        double redC = (8 * c3 - c4 - 4 * redB - 7 * (double) c2) / 6;
        double redA = c3 - redB - redC - (double) c2;

        int res = (int) (redA * Math.pow(u, 3) + redB * pow(u, 2) + redC * u + (double) c2);


        return min(255, max(0, res));
    }
}
