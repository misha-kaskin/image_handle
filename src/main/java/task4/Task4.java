package task4;

import javax.swing.*;
import javax.swing.border.TitledBorder;
import java.awt.*;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

import static java.lang.Math.*;
import static javax.imageio.ImageIO.read;

public class Task4 {
    static BufferedImage img;
    static BufferedImage img1;
    static int aX;
    static int aY;
    static int bX;
    static int bY;
    static int cX;
    static int cY;
    static boolean isPointSelect;

    public static void main(String[] args) {
        JFrame jFrame = new JFrame("Task4");
        jFrame.setSize(1100, 600);
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

            JLabel jLabel = new JLabel(new ImageIcon(img));
            jLabel.addMouseListener(new MouseListener() {
                int aX1;
                int aY1;

                @Override
                public void mouseClicked(MouseEvent e) {
                }

                @Override
                public void mousePressed(MouseEvent e) {
                    if (isPointSelect) {
                        cX = e.getX();
                        cY = e.getY();
                    } else {
                        aX1 = e.getX();
                        aY1 = e.getY();
                    }
                }

                @Override
                public void mouseReleased(MouseEvent e) {
                    if (!isPointSelect) {
                        int bX1 = e.getX();
                        int bY1 = e.getY();

                        aX = min(aX1, bX1);
                        aY = min(aY1, bY1);

                        bX = max(aX1, bX1);
                        bY = max(aY1, bY1);
                    }
                }

                @Override
                public void mouseEntered(MouseEvent e) {
                }

                @Override
                public void mouseExited(MouseEvent e) {
                }
            });

            imagePanel.removeAll();
            imagePanel.add(jLabel);
            imagePanel.updateUI();
        });

        JButton selectPoint = new JButton("Точка вращения");
        selectPoint.addActionListener(el -> isPointSelect = true);

        JButton cutImage = new JButton("Участок вращения");
        cutImage.addActionListener(el -> isPointSelect = false);

        //TODO
        JButton turnButton = new JButton("Автоповорот");
        turnButton.addActionListener(el -> {
            img1 = TurnImage.turnImage(img);
            resultPanel.removeAll();
            resultPanel.add(new JLabel(new ImageIcon(img1)), BorderLayout.CENTER);
            resultPanel.updateUI();
        });

        controlPanel.add(loadButton);
        controlPanel.add(selectPoint);
        controlPanel.add(cutImage);
        controlPanel.add(turnButton);

        JPanel parameterPanel = new JPanel();
        parameterPanel.setBorder(new TitledBorder("Параметры"));
        JPanel inPP = new JPanel();
        inPP.setLayout(new GridLayout(1, 2));
        SpinnerNumberModel snmAngle = new SpinnerNumberModel(0, -180, 180, 1);
        JSpinner angleSpinner = new JSpinner(snmAngle);
        angleSpinner.addChangeListener(el -> {
            int angle = (int) angleSpinner.getValue();
            int width = img.getWidth();
            int height = img.getHeight();
            int type = img.getType();
            img1 = new BufferedImage(width, height, type);

            double radAngle = angle / 180.0 * PI;

            for (int i = 0; i < width; i++) {
                for (int j = 0; j < height; j++) {
                    double tmpPY1 = (j - cY) / cos(radAngle) + cX * tan(radAngle) + cY;
                    double tmpPY2 = (i - cX + cX * cos(radAngle) - cY * sin(radAngle))
                            / pow(cos(radAngle), 2)
                            * sin(radAngle);
                    double tmpPY3 = 1 + pow(tan(radAngle), 2);

                    int pY = (int) ((tmpPY1 - tmpPY2) / tmpPY3);
                    int pX = (int) ((i - cX + cX * cos(radAngle) - cY * sin(radAngle) + pY * sin(radAngle))
                            / cos(radAngle));

                    if (pY >= aY && pY <= bY && pX >= aX && pX <= bX) {
                        img1.setRGB(i, j, img.getRGB(pX, pY));
                    }
                }
            }

            resultPanel.removeAll();
            resultPanel.add(new JLabel(new ImageIcon(img1)), BorderLayout.CENTER);
            resultPanel.updateUI();
        });

        inPP.add(new JLabel("Угол вращения"));
        inPP.add(angleSpinner);

        parameterPanel.add(inPP);

        jFrame.add(imagePanel, BorderLayout.WEST);
        jFrame.add(resultPanel, BorderLayout.CENTER);
        jFrame.add(controlPanel, BorderLayout.SOUTH);
        jFrame.add(parameterPanel, BorderLayout.EAST);

        jFrame.setVisible(true);
    }
}
