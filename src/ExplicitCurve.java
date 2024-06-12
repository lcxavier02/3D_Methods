import java.awt.*;
import java.awt.image.BufferedImage;

public class ExplicitCurve {
  private BufferedImage buffer;
  private Graphics graphicsBuffer;
  private int surfaceX = 100;
  private int surfaceY = 100;
  private int surfaceZ = 0;
  public double angleX = 0;
  public double angleY = 0;
  public double angleZ = 0;
  private double scaleX = 1.0;
  private double scaleY = 1.0;

  public ExplicitCurve(BufferedImage buffer) {
    this.buffer = buffer;
    this.graphicsBuffer = buffer.createGraphics();
  }

  public void putPixel(int x, int y, Color c) {
    if (x >= 0 && x < buffer.getWidth() && y >= 0 && y < buffer.getHeight()) {
      buffer.setRGB(x, y, c.getRGB());
    }
  }

  public void drawLine(int x1, int y1, int x2, int y2, Color color) {
    int dx = Math.abs(x2 - x1);
    int dy = Math.abs(y2 - y1);

    int xi = (x2 > x1) ? 1 : -1;
    int yi = (y2 > y1) ? 1 : -1;

    int A = 2 * dy;
    int B = 2 * dy - 2 * dx;
    int C = 2 * dx;
    int D = 2 * dx - 2 * dy;

    int x = x1;
    int y = y1;

    int pk;

    putPixel(x, y, color);

    if (dx > dy) {
      pk = 2 * dy - dx;
      while (x != x2) {
        if (pk > 0) {
          x = x + xi;
          y = y + yi;
          pk = pk + B;
        } else {
          x = x + xi;
          pk = pk + A;
        }
        putPixel(x, y, color);
      }
    } else {
      pk = 2 * dx - dy;
      while (y != y2) {
        if (pk > 0) {
          x = x + xi;
          y = y + yi;
          pk = pk + D;
        } else {
          y = y + yi;
          pk = pk + C;
        }
        putPixel(x, y, color);
      }
    }
  }

  private int[] projectVertex(double x, double y, double z) {
    int x2D = (int) (x * scaleX) + 410;
    int y2D = (int) (y * scaleY) + 410;
    return new int[] { x2D, y2D };
  }

  private double[] rotateX(double[] vertex, double angle) {
    double[] rotated = new double[3];
    rotated[0] = vertex[0];
    rotated[1] = vertex[1] * Math.cos(angle) - vertex[2] * Math.sin(angle);
    rotated[2] = vertex[1] * Math.sin(angle) + vertex[2] * Math.cos(angle);
    return rotated;
  }

  private double[] rotateY(double[] vertex, double angle) {
    double[] rotated = new double[3];
    rotated[0] = vertex[0] * Math.cos(angle) + vertex[2] * Math.sin(angle);
    rotated[1] = vertex[1];
    rotated[2] = -vertex[0] * Math.sin(angle) + vertex[2] * Math.cos(angle);
    return rotated;
  }

  private double[] rotateZ(double[] vertex, double angle) {
    double[] rotated = new double[3];
    rotated[0] = vertex[0] * Math.cos(angle) - vertex[1] * Math.sin(angle);
    rotated[1] = vertex[0] * Math.sin(angle) + vertex[1] * Math.cos(angle);
    rotated[2] = vertex[2];
    return rotated;
  }

  private double[] rotateVertex(double x, double y, double z) {
    double[] vertex = new double[] { x, y, z };
    double[] rotatedX = rotateX(vertex, angleX);
    double[] rotatedXY = rotateY(rotatedX, angleY);
    double[] rotatedXYZ = rotateZ(rotatedXY, angleZ);
    return rotatedXYZ;
  }

  public void drawSpiral() {
    int numPoints = 200;
    double height = 200;
    double radius = 50;
    double coilSpacing = 10;

    double tMin = 0.0;
    double tMax = 8.0 * Math.PI;

    for (int i = 0; i < numPoints; i++) {
      double t = tMin + (tMax - tMin) * i / numPoints;

      double x = (radius + coilSpacing * t / (2 * Math.PI)) * Math.cos(t);
      double y = (radius + coilSpacing * t / (2 * Math.PI)) * Math.sin(t);
      double z = height * t / (2 * Math.PI);

      double nextT = tMin + (tMax - tMin) * (i + 1) / numPoints;
      double nextX = (radius + coilSpacing * nextT / (2 * Math.PI)) * Math.cos(nextT);
      double nextY = (radius + coilSpacing * nextT / (2 * Math.PI)) * Math.sin(nextT);
      double nextZ = height * nextT / (2 * Math.PI);

      // Rotate around the center of the spiral
      double[] rotatedVertex = rotateVertex(x, y, z);
      double[] rotatedNextVertex = rotateVertex(nextX, nextY, nextZ);

      // Translate to the surface position
      double translatedX = rotatedVertex[0] + surfaceX;
      double translatedY = rotatedVertex[1] + surfaceY;
      double translatedZ = rotatedVertex[2] + surfaceZ;

      double nextTranslatedX = rotatedNextVertex[0] + surfaceX;
      double nextTranslatedY = rotatedNextVertex[1] + surfaceY;
      double nextTranslatedZ = rotatedNextVertex[2] + surfaceZ;

      int[] projectedVertex = projectVertex(translatedX, translatedY, translatedZ);
      int[] projectedNextVertex = projectVertex(nextTranslatedX, nextTranslatedY, nextTranslatedZ);

      drawLine(projectedVertex[0], projectedVertex[1], projectedNextVertex[0], projectedNextVertex[1], Color.RED);
    }
  }

  public void fillRect(int x, int y, int width, int height, Color color) {
    for (int j = y; j < y + height; j++) {
      drawLine(x, j, x + width - 1, j, color);
    }
  }

  public void clearBuffer() {
    fillRect(0, 0, buffer.getWidth(), buffer.getHeight(), Color.WHITE);
  }

  public void showCurve() {
    if (buffer != null) {
      drawSpiral();
    }
  }

  public void rotate(double deltaX, double deltaY, double deltaZ) {
    angleY += deltaX;
    angleX += deltaY;
    angleZ += deltaZ;
  }

  public void move(int dx, int dy, int dz) {
    surfaceX += dx;
    surfaceY += dy;
    surfaceZ += dz;
  }

  public void scale(double scale) {
    scaleX *= scale;
    scaleY *= scale;
  }
}
