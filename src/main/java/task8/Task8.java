package task8;

import javax.swing.*;
import javax.swing.border.TitledBorder;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.LinkedList;
import java.util.List;

import static java.lang.Math.*;
import static javax.imageio.ImageIO.read;

public class Task8 {
    static BufferedImage img;
    static BufferedImage img1;
    static int accuracy = 5;
    static List<List<Point>> globalPoints = new LinkedList<>();

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

            globalPoints.clear();
            boolean[][] isBorders = new boolean[width][height];

            for (int j = 0; j < height; j++) {
                for (int i = 0; i < width - 1; i++) {
                    Color c1 = new Color(img.getRGB(i, j));
                    Color c2 = new Color(img.getRGB(i + 1, j));

                    if (isEqualColor(c1, c2)) {
                        continue;
                    }

                    if (isBorders[i][j] || isBorders[i + 1][j]) {
                        continue;
                    }

                    List<Point> pointList = new LinkedList<>();
                    globalPoints.add(pointList);

                    Point l = new Point(i + 1, j);
                    Point r = new Point(i, j);
                    Point t = getTestPoint(l, r);

                    isBorders[l.x][l.y] = true;
                    isBorders[r.x][r.y] = true;
                    isBorders[t.x][t.y] = true;

                    pointList.add(l);

                    while (!t.equals(pointList.get(0))) {
                        if (isBlack(t)) {
                            l = t;
                            pointList.add(t);
                        } else {
                            r = t;
                        }

                        t = getTestPoint(l, r);
                        isBorders[t.x][t.y] = true;
                    }
                }
            }

            BufferedImage img2 = new BufferedImage(width, height, type);

            for (int i = 0; i < width; i++) {
                for (int j = 0; j < height; j++) {
                    img2.setRGB(i, j, Color.WHITE.getRGB());
                }
            }

            for (List<Point> pointList : globalPoints) {
                for (Point p : pointList) {
                    img2.setRGB(p.x, p.y, Color.red.getRGB());
                }
            }

            img1 = img2;

            resultPanel.removeAll();
            resultPanel.add(new JLabel(new ImageIcon(img1)), BorderLayout.CENTER);
            resultPanel.updateUI();
        });

        JButton approxButton = new JButton("Аппроксимация");
        approxButton.addActionListener(el -> {
            int width = img.getWidth();
            int height = img.getHeight();
            int type = img.getType();

            List<List<Point>> localPoints = firstTypeApprox();
            List<List<Point>> localPoints2 = secondTypeApprox(localPoints);

            BufferedImage img2 = new BufferedImage(width, height, type);

            for (int i = 0; i < width; i++) {
                for (int j = 0; j < height; j++) {
                    img2.setRGB(i, j, Color.WHITE.getRGB());
                }
            }

            fillImg(img2, localPoints2);

            img1 = img2;

            resultPanel.removeAll();
            resultPanel.add(new JLabel(new ImageIcon(img1)), BorderLayout.CENTER);
            resultPanel.updateUI();
        });

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

    static void fillImg(BufferedImage img2, List<List<Point>> localPointList) {
        for (List<Point> pl : localPointList) {
            for (int i = 0; i < pl.size() - 1; i++) {
                drawLine(pl.get(i), pl.get(i + 1), img2);
            }

            drawLine(pl.get(0), pl.get(pl.size() - 1), img2);
        }
    }

    static void drawLine(Point p1, Point p2, BufferedImage img2) {
        int dist = (int) (sqrt(pow(p1.x - p2.x, 2) + pow(p1.y - p2.y, 2)) * 10);
        double deltaX = (p2.getX() - p1.getX()) / dist;
        double deltaY = (p2.getY() - p1.getY()) / dist;
        int startX = p1.x;
        int startY = p1.y;

        for (int i = 0; i < dist; i++) {
            int x = (int) ceil(startX + deltaX * i);
            int y = (int) ceil(startY + deltaY * i);
            img2.setRGB(x, y, Color.red.getRGB());
        }
    }

    static List<List<Point>> secondTypeApprox(List<List<Point>> localPointList) {
        List<List<Point>> resList = new LinkedList<>();

        for (List<Point> pl : localPointList) {
            List<Integer> buffer = new LinkedList<>();
            buffer.add(0);
            buffer.add(pl.size() - 1);

            List<Integer> tmp = secondType(pl, buffer, 0, pl.size());
            tmp.sort(Integer::compare);
            List<Point> tmpPoints = new LinkedList<>();

            for (Integer idx : tmp) {
                Point p = pl.get(idx);
                tmpPoints.add(p);
            }

            resList.add(tmpPoints);
        }

        return resList;
    }

    static List<Integer> secondType(List<Point> pl, List<Integer> buffer, int l, int r) {
        int maxIdx = findMaxDistPoint(pl, l, r);
        buffer.add(maxIdx);

        Point a = pl.get(l);
        Point b = pl.get(r - 1);
        Point maxPoint = pl.get(maxIdx);

        if (getDistance(a, b, maxPoint) <= accuracy) {
            return buffer;
        }

        secondType(pl, buffer, l, maxIdx + 1);
        secondType(pl, buffer, maxIdx, r);

        return buffer;
    }

    static double getDistance(Point a, Point b, Point maxPoint) {
        double angle1 = atan2(b.x - a.x, b.y - a.y);
        double angle2 = atan2(maxPoint.x - a.x, maxPoint.y - a.y);

        if (angle1 < 0) {
            angle1 += 2 * PI;
        }
        if (angle2 < 0) {
            angle2 += 2 * PI;
        }

        double angle = abs(angle1 - angle2);
        if (angle > PI) {
            angle = 2 * PI - angle;
        }
        double distToA = sqrt(pow(a.x - maxPoint.x, 2) + pow(a.y - maxPoint.y, 2));

        if (angle >= PI / 2) {
            return distToA;
        }
        double distFromAToB = sqrt(pow(a.x - b.x, 2) + pow(a.y - b.y, 2));

        if (cos(angle) * distToA > distFromAToB) {
            return sqrt(pow(maxPoint.x - b.x, 2) + pow(maxPoint.y - b.y, 2));
        }

        return sin(angle) * distToA;
    }

    static int findMaxDistPoint(List<Point> pl, int l, int r) {
        if (l == r) {
            return 0;
        }

        int maxDistIdx = l;
        Point a = pl.get(l);
        Point b = pl.get(r - 1);
        Point p1 = pl.get(maxDistIdx);

        for (int i = l; i < r; i++) {
            Point p = pl.get(i);

            if (getDistance(a, b, p) > getDistance(a, b, p1)) {
                maxDistIdx = i;
                p1 = p;
            }
        }

        return maxDistIdx;
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

    static boolean isOneLine(Point p1, Point p2, Point p3) {
        int ax = p1.x - p2.x;
        int ay = p1.y - p2.y;
        int bx = p1.x - p3.x;
        int by = p1.y - p3.y;

        if (ax == 0 && bx == 0 || ay == 0 && by == 0) {
            return true;
        }

        if (abs(ax) == abs(ay) && abs(bx) == abs(by)) {
            return ax * bx > 0 && ay * by > 0;
        }

        return false;
    }

    static List<List<Point>> firstTypeApprox() {
        List<List<Point>> localPoints = new LinkedList<>();

        for (List<Point> lp : globalPoints) {
            List<Point> localPointList = new LinkedList<>();

            Point p1 = lp.get(0);
            localPointList.add(p1);

            for (int i = 0; i < lp.size() - 2; i++) {
                Point p2 = lp.get(i + 1);
                Point p3 = lp.get(i + 2);

                if (!isOneLine(p1, p2, p3)) {
                    localPointList.add(p2);
                    p1 = p2;
                }
            }

            localPointList.add(lp.get(lp.size() - 1));

            localPoints.add(localPointList);
        }

        return localPoints;
    }
}
