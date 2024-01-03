package task4;

import java.awt.*;
import java.awt.image.BufferedImage;

import static java.lang.Math.*;

public class TurnImage {
    static BufferedImage turnImage(BufferedImage myPicture) {
        int width = myPicture.getWidth();
        int height = myPicture.getHeight();
        int imageType = myPicture.getType();

        /* Рассчет крайних точек (верхней, правой, левой, нижней) */

        Point high = new Point(Integer.MAX_VALUE, Integer.MAX_VALUE);
        Point low = new Point(-1, -1);
        Point left = new Point(Integer.MAX_VALUE, Integer.MAX_VALUE);
        Point right = new Point(-1, -1);

        for (int i = 0; i < width; i++) {
            for (int j = 0; j < height; j++) {
                Color color = new Color(myPicture.getRGB(i, j));
                if (color.getRed() < 70
                        && color.getGreen() < 70
                        && color.getBlue() < 70) {
                    if (i > right.x) {
                        right.x = i;
                        right.y = j;
                    }
                    if (j > low.y) {
                        low.x = i;
                        low.y = j;
                    }
                    if (i < left.x) {
                        left.x = i;
                        left.y = j;
                    }
                    if (j < high.y) {
                        high.x = i;
                        high.y = j;
                    }
                }
            }
        }

        double sinAlpha = (right.x - low.x)
                / sqrt(pow((double) right.x - (double) low.x, 2) + pow(right.y - low.y, 2));
        double alpha = asin(sinAlpha);
        double tg = tan(alpha);

        int dlt = 100;

        high.y -= dlt;
        left.x -= dlt;
        right.x += dlt;
        low.y += dlt;

        double xA = (left.x / tg + left.y - high.y + tg * high.x)
                / (tg + 1 / tg);
        double yA = tg * xA + high.y - tg * high.x;

        double xB = (right.x / tg + right.y - high.y + tg * high.x)
                / (tg + 1 / tg);
        double yB = tg * xB + high.y - tg * high.x;

        double xC = (-low.y + tg * low.x + right.x / tg + right.y)
                / (tg + 1 / tg);
        double yC = tg * xC + low.y - tg * low.x;

        double xD = (left.x / tg + left.y - low.y + tg * low.x)
                / (tg + 1 / tg);
        double yD = tg * xD + low.y - tg * low.x;

        Point A = new Point((int) xA, (int) yA);
        Point B = new Point((int) xB, (int) yB);
        Point C = new Point((int) xC, (int) yC);
        Point D = new Point((int) xD, (int) yD);

        high = A;
        right = B;
        low = C;
        left = D;

        /* Расчет угла поворота и размеров исходного изображения */

        int srcImgWidth = (int) sqrt(pow(right.x - high.x, 2) + pow(right.y - high.y, 2));
        int srcImgHeight = (int) sqrt(pow(left.x - high.x, 2) + pow(left.y - high.y, 2));

        BufferedImage newImg = new BufferedImage(srcImgWidth, srcImgHeight, imageType);

        /* Поворот изображения */

        for (int i = 0; i < srcImgWidth; i++) {
            int dx = (int) (sqrt(pow(i, 2) / (1 + pow(tg, 2))));
            int dy = (int) (dx * tg);


            for (int j = 0; j < srcImgHeight; j++) {
                int idxX = (int) (high.x - sqrt(pow(j, 2) / (1 + pow(1.0 / tg, 2))));
                int idxY = (int) (high.y + sqrt(pow(j, 2) / (1 + pow(tg, 2))));

                idxX += dx;
                idxY += dy;

                idxX = max(0, min(idxX, srcImgWidth - 1));
                idxY = max(0, min(idxY, srcImgHeight - 1));


                newImg.setRGB(i, j, myPicture.getRGB(idxX, idxY));
            }
        }

        return newImg;
    }

    static class Point {
        int x;
        int y;

        public Point(int x, int y) {
            this.x = x;
            this.y = y;
        }
    }
}
