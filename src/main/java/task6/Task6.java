package task6;

import javax.swing.*;
import javax.swing.border.TitledBorder;
import java.awt.*;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

import static java.lang.Math.max;
import static java.lang.Math.min;
import static javax.imageio.ImageIO.read;

public class Task6 {
    static BufferedImage img;
    static BufferedImage img1;
    static int triangleCount = 2;
    static Point a;
    static Point b;
    static Point c;
    static Point d;

    public static void main(String[] args) {
        JFrame jFrame = new JFrame("Task6");
        jFrame.setLayout(new BorderLayout());
        jFrame.setSize(1500, 900);

        JPanel imagePanel = new JPanel();
        imagePanel.setBorder(new TitledBorder("Изображение"));

        JPanel resultPanel = new JPanel();
        resultPanel.setBorder(new TitledBorder("Результат"));
        resultPanel.setLayout(new BorderLayout());
        ImageIcon imageIcon = new ImageIcon();
        JLabel jLabel = new JLabel(imageIcon);
        resultPanel.add(jLabel);

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

        JButton srcButton = new JButton("Исходное");
        srcButton.addActionListener(el -> {
            img1 = img;
            imageIcon.setImage(img1);

            int height1 = jLabel.getHeight();
            int width1 = jLabel.getWidth();

            int height = imageIcon.getIconHeight();
            int width = imageIcon.getIconWidth();

            int x1 = (width1 - width) / 2;
            int y1 = (height1 - height) / 2;

            int x2 = x1 + width;
            int y2 = y1;

            int x3 = x1;
            int y3 = y1 + height;

            int x4 = x1 + width;
            int y4 = y1 + height;

            a = new Point(x1, y1);
            b = new Point(x2, y2);
            c = new Point(x3, y3);
            d = new Point(x4, y4);

            jLabel.addMouseListener(new MouseListener() {
                boolean isPointSelected = true;
                Point p = new Point(0, 0);

                @Override
                public void mouseClicked(MouseEvent e) {

                }

                @Override
                public void mousePressed(MouseEvent e) {
                    int startX = e.getX();
                    int startY = e.getY();

                    isPointSelected = true;

                    if (isPoint(a, startX, startY)) {
                        p = a;
                    } else if (isPoint(b, startX, startY)) {
                        p = b;
                    } else if (isPoint(c, startX, startY)) {
                        p = c;
                    } else if (isPoint(d, startX, startY)) {
                        p = d;
                    } else {
                        isPointSelected = false;
                    }
                }

                @Override
                public void mouseReleased(MouseEvent e) {
                    if (isPointSelected) {
                        p.setLocation(e.getX(), e.getY());

                        img1 = getTriangleImg();

                        imageIcon.setImage(img1);
                        resultPanel.updateUI();
                    }
                }

                @Override
                public void mouseEntered(MouseEvent e) {

                }

                @Override
                public void mouseExited(MouseEvent e) {

                }
            });

            resultPanel.updateUI();
        });

        controlPanel.add(loadButton);
        controlPanel.add(srcButton);

        JPanel parameterPanel = new JPanel();
        parameterPanel.setBorder(new TitledBorder("Параметры"));
        JPanel inPP = new JPanel();
        inPP.setLayout(new GridLayout(1, 2));
        SpinnerNumberModel snmCountTriangle = new SpinnerNumberModel(2, 2, Integer.MAX_VALUE, 1);
        JSpinner countTriangleSpinner = new JSpinner(snmCountTriangle);
        countTriangleSpinner.addChangeListener(el -> triangleCount = (int) countTriangleSpinner.getValue());

        inPP.add(new JLabel("Разбиения"));
        inPP.add(countTriangleSpinner);

        parameterPanel.add(inPP);

        jFrame.add(imagePanel, BorderLayout.WEST);
        jFrame.add(resultPanel, BorderLayout.CENTER);
        jFrame.add(controlPanel, BorderLayout.SOUTH);
        jFrame.add(parameterPanel, BorderLayout.EAST);

        jFrame.setVisible(true);
    }

    static BufferedImage getTriangleImg() {
        int minX = Integer.MAX_VALUE;
        int maxX = -1;
        int minY = Integer.MAX_VALUE;
        int maxY = -1;

        minX = (int) min(minX, min(a.getX(), min(b.getX(), min(c.getX(), d.getX()))));
        maxX = (int) max(maxX, max(a.getX(), max(b.getX(), max(c.getX(), d.getX()))));

        minY = (int) min(minY, min(a.getY(), min(b.getY(), min(c.getY(), d.getY()))));
        maxY = (int) max(maxY, max(a.getY(), max(b.getY(), max(c.getY(), d.getY()))));

        int width = maxX - minX + 1;
        int height = maxY - minY + 1;
        int type = img.getType();

        BufferedImage img2 = new BufferedImage(width, height, type);

        Point tmpA = new Point((int) a.getX() - minX, (int) a.getY() - minY);
        Point tmpB = new Point((int) b.getX() - minX, (int) b.getY() - minY);
        Point tmpC = new Point((int) c.getX() - minX, (int) c.getY() - minY);
        Point tmpD = new Point((int) d.getX() - minX, (int) d.getY() - minY);

        Point srcA = new Point(0, 0);
        Point srcB = new Point(img.getWidth() - 1, 0);
        Point srcC = new Point(0, img.getHeight() - 1);
        Point srcD = new Point(img.getWidth() - 1, img.getHeight() - 1);

        Triangle[][] srcTriangles = getTriangles(srcA, srcB, srcC, srcD);
        Triangle[][] resTriangles = getTriangles(tmpA, tmpB, tmpC, tmpD);

        int lenWidth = resTriangles.length;
        int lenHeight = resTriangles[0].length;

        for (int i = 0; i < lenWidth; i++) {
            for (int j = 0; j < lenHeight; j++) {
                Triangle res = resTriangles[i][j];

                int maxX1 = (int) max(res.a.getX(), max(res.b.getX(), res.c.getX()));
                int minX1 = (int) min(res.a.getX(), min(res.b.getX(), res.c.getX()));

                int maxY1 = (int) max(res.a.getY(), max(res.b.getY(), res.c.getY()));
                int minY1 = (int) min(res.a.getY(), min(res.b.getY(), res.c.getY()));

                double[][] matrix = {
                        {res.a.getX(), res.b.getX(), res.c.getX()},
                        {res.a.getY(), res.b.getY(), res.c.getY()},
                        {1, 1, 1}
                };
                double d = det3(matrix);

                if (d == 0) {
                    continue;
                }

                for (int k = minX1; k < maxX1; k++) {
                    for (int l = minY1; l < maxY1; l++) {
                        double[][] matrix1 = {
                                {k, res.b.getX(), res.c.getX()},
                                {l, res.b.getY(), res.c.getY()},
                                {1, 1, 1}
                        };
                        double[][] matrix2 = {
                                {res.a.getX(), k, res.c.getX()},
                                {res.a.getY(), l, res.c.getY()},
                                {1, 1, 1}
                        };
                        double[][] matrix3 = {
                                {res.a.getX(), res.b.getX(), k},
                                {res.a.getY(), res.b.getY(), l},
                                {1, 1, 1}
                        };

                        double d1 = det3(matrix1);
                        double d2 = det3(matrix2);
                        double d3 = det3(matrix3);

                        double e1 = d1 / d;
                        double e2 = d2 / d;
                        double e3 = d3 / d;

                        if (e1 >= 0 && e2 >= 0 && e3 >= 0) {
                            Triangle src = srcTriangles[i][j];

                            int x = (int) (e1 * src.a.getX() + e2 * src.b.getX() + e3 * src.c.getX());
                            int y = (int) (e1 * src.a.getY() + e2 * src.b.getY() + e3 * src.c.getY());

                            img2.setRGB(k, l, img.getRGB(x, y));
                        }
                    }
                }
            }
        }

        return img2;
    }

    static double det3(double[][] a) {
        double res = 0;

        res += a[0][0] * a[1][1] * a[2][2];
        res += a[0][1] * a[1][2] * a[2][0];
        res += a[0][2] * a[1][0] * a[2][1];

        res -= a[0][2] * a[1][1] * a[2][0];
        res -= a[0][1] * a[1][0] * a[2][2];
        res -= a[0][0] * a[1][2] * a[2][1];

        return res;
    }

    static Triangle[][] getTriangles(Point a, Point b, Point c, Point d) {
        Point[][] points = new Point[triangleCount + 1][triangleCount + 1];

        double deltaXAC = (c.getX() - a.getX()) / triangleCount;
        double deltaXBD = (d.getX() - b.getX()) / triangleCount;

        double deltaYAC = (c.getY() - a.getY()) / triangleCount;
        double deltaYBD = (d.getY() - b.getY()) / triangleCount;

        double xAC = a.getX();
        double yAC = a.getY();

        double xBD = b.getX();
        double yBD = b.getY();

        for (int i = 0; i < triangleCount + 1; i++) {
            double deltaX = (xBD - xAC) / triangleCount;
            double deltaY = (yBD - yAC) / triangleCount;

            double x = xAC;
            double y = yAC;

            for (int j = 0; j < triangleCount + 1; j++) {
                points[i][j] = new Point((int) x, (int) y);

                x += deltaX;
                y += deltaY;
            }

            xAC += deltaXAC;
            yAC += deltaYAC;

            xBD += deltaXBD;
            yBD += deltaYBD;
        }

        Triangle[][] triangles = new Triangle[2 * triangleCount][triangleCount];

        for (int i = 0; i < triangleCount; i++) {
            for (int j = 0; j < triangleCount; j++) {
                Triangle aTr = new Triangle();
                Triangle bTr = new Triangle();

                aTr.a = points[i][j];
                aTr.b = points[i + 1][j];
                aTr.c = points[i][j + 1];

                bTr.a = points[i + 1][j];
                bTr.b = points[i][j + 1];
                bTr.c = points[i + 1][j + 1];

                triangles[2 * i][j] = aTr;
                triangles[2 * i + 1][j] = bTr;
            }
        }

        return triangles;
    }

    static boolean isPoint(Point a, int x, int y) {
        boolean isPoint = false;

        int pX = (int) a.getX();
        int pY = (int) a.getY();

        if (pY < y + 20 && pY > y - 20) {
            if (pX < x + 20 && pX > x - 20) {
                isPoint = true;
            }
        }

        return isPoint;
    }

    static class Triangle {
        Point a;
        Point b;
        Point c;
    }
}
