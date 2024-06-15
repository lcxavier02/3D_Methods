import java.awt.*;
import java.awt.image.BufferedImage;

public class Surface {
  private BufferedImage buffer;
  private Graphics graphicsBuffer;
  private int surfaceX = 0;
  private int surfaceY = 0;
  private int surfaceZ = 0;
  public double angleX = 0;
  public double angleY = 0;
  public double angleZ = 0;
  private double scaleX = 1.0; // Factor de escalado en el eje X
  private double scaleY = 1.0; // Factor de escalado en el eje Y

  public Surface(BufferedImage buffer) {
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

  private int[] projectVertex(int x, int y, int z) {
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

  private int[] rotateVertex(int x, int y, int z) {
    x -= surfaceX;
    y -= surfaceY;
    z -= surfaceZ;

    double[] vertex = new double[] { x, y, z };
    double[] rotatedX = rotateX(vertex, angleX);
    double[] rotatedXY = rotateY(rotatedX, angleY);
    double[] rotatedXYZ = rotateZ(rotatedXY, angleZ);

    int finalX = (int) rotatedXYZ[0] + surfaceX;
    int finalY = (int) rotatedXYZ[1] + surfaceY;
    int finalZ = (int) rotatedXYZ[2] + surfaceZ;

    return new int[] { finalX, finalY, finalZ };
  }

  public void drawSurface() {
    double uStep = 0.3;
    double vStep = 0.3;

    double R = 50.0;
    double r = 20.0;

    double uMin = 0.0;
    double uMax = 2.0 * Math.PI;
    double vMin = 0.0;
    double vMax = 2.0 * Math.PI;

    for (double u = uMin; u < uMax; u += uStep) {
      for (double v = vMin; v < vMax; v += vStep) {
        double x = surfaceX + (R + r * Math.cos(v)) * Math.cos(u) * scaleX;
        double y = surfaceY + (R + r * Math.cos(v)) * Math.sin(u) * scaleY;
        double z = surfaceZ + r * Math.sin(v);

        double x1 = surfaceX + (R + r * Math.cos(v)) * Math.cos(u + uStep) * scaleX;
        double y1 = surfaceY + (R + r * Math.cos(v)) * Math.sin(u + uStep) * scaleY;
        double z1 = surfaceZ + r * Math.sin(v);

        double x2 = surfaceX + (R + r * Math.cos(v + vStep)) * Math.cos(u) * scaleX;
        double y2 = surfaceY + (R + r * Math.cos(v + vStep)) * Math.sin(u) * scaleY;
        double z2 = surfaceZ + r * Math.sin(v + vStep);

        double x3 = surfaceX + (R + r * Math.cos(v + vStep)) * Math.cos(u + uStep) * scaleX;
        double y3 = surfaceY + (R + r * Math.cos(v + vStep)) * Math.sin(u + uStep) * scaleY;
        double z3 = surfaceZ + r * Math.sin(v + vStep);

        int[] rotatedVertex = rotateVertex((int) x, (int) y, (int) z);
        int[] rotatedVertex1 = rotateVertex((int) x1, (int) y1, (int) z1);
        int[] rotatedVertex2 = rotateVertex((int) x2, (int) y2, (int) z2);
        int[] rotatedVertex3 = rotateVertex((int) x3, (int) y3, (int) z3);

        int[] projectedVertex = projectVertex(rotatedVertex[0], rotatedVertex[1], rotatedVertex[2]);
        int[] projectedVertex1 = projectVertex(rotatedVertex1[0], rotatedVertex1[1], rotatedVertex1[2]);
        int[] projectedVertex2 = projectVertex(rotatedVertex2[0], rotatedVertex2[1], rotatedVertex2[2]);
        int[] projectedVertex3 = projectVertex(rotatedVertex3[0], rotatedVertex3[1], rotatedVertex3[2]);

        drawLine(projectedVertex[0], projectedVertex[1], projectedVertex1[0], projectedVertex1[1], Color.RED);
        drawLine(projectedVertex[0], projectedVertex[1], projectedVertex2[0], projectedVertex2[1], Color.RED);
        drawLine(projectedVertex1[0], projectedVertex1[1], projectedVertex3[0], projectedVertex3[1], Color.RED);
        drawLine(projectedVertex2[0], projectedVertex2[1], projectedVertex3[0], projectedVertex3[1], Color.RED);
      }
    }
  }

  public void drawCylinder() {
    int numPointsU = 50;
    int numPointsV = 50;
    double a = 10.0;
    double b = 10.0;
    double height = 10.0;

    double uMin = -Math.PI / 2;
    double uMax = Math.PI / 2;
    double vMin = 0.0;
    double vMax = 2.0 * Math.PI;

    for (int i = 0; i < numPointsU - 1; i++) {
      for (int j = 0; j < numPointsV; j++) {
        double u = uMin + (uMax - uMin) * i / (numPointsU - 1);
        double v = vMin + (vMax - vMin) * j / (numPointsV - 1);

        double x = surfaceX + a * Math.cosh(u) * Math.cos(v) * scaleX;
        double y = surfaceY + a * Math.cosh(u) * Math.sin(v) * scaleY;
        double z = surfaceZ + b * u * height / (uMax - uMin);

        double nextU = uMin + (uMax - uMin) * (i + 1) / (numPointsU - 1);
        double nextX = surfaceX + a * Math.cosh(nextU) * Math.cos(v) * scaleX;
        double nextY = surfaceY + a * Math.cosh(nextU) * Math.sin(v) * scaleY;
        double nextZ = surfaceZ + b * nextU * height / (uMax - uMin);

        double nextV = vMin + (vMax - vMin) * (j + 1) / (numPointsV - 1);
        double nextXV = surfaceX + a * Math.cosh(u) * Math.cos(nextV) * scaleX;
        double nextYV = surfaceY + a * Math.cosh(u) * Math.sin(nextV) * scaleY;
        double nextZV = surfaceZ + b * u * height / (uMax - uMin);

        int[] rotatedVertex = rotateVertex((int) x, (int) y, (int) z);
        int[] rotatedNextUVertex = rotateVertex((int) nextX, (int) nextY, (int) nextZ);
        int[] rotatedNextVVertex = rotateVertex((int) nextXV, (int) nextYV, (int) nextZV);

        int[] projectedVertex = projectVertex(rotatedVertex[0], rotatedVertex[1], rotatedVertex[2]);
        int[] projectedNextUVertex = projectVertex(rotatedNextUVertex[0], rotatedNextUVertex[1], rotatedNextUVertex[2]);
        int[] projectedNextVVertex = projectVertex(rotatedNextVVertex[0], rotatedNextVVertex[1], rotatedNextVVertex[2]);

        drawLine(projectedVertex[0], projectedVertex[1], projectedNextUVertex[0], projectedNextUVertex[1], Color.RED);
        drawLine(projectedVertex[0], projectedVertex[1], projectedNextVVertex[0], projectedNextVVertex[1], Color.RED);
      }
    }
  }

  public void fillRect(int x, int y, int width, int height, Color color) {
    for (int j = y; j < y + height; j++) {
      drawLine(x, j, x + width - 1, j, color);
    }
  }

  public void showSurface() {
    if (buffer != null) {
      drawSurface();
    }
  }

  public void showCylinder() {
    if (buffer != null) {
      drawCylinder();
    }
  }

  public void clearBuffer() {
    fillRect(0, 0, buffer.getWidth(), buffer.getHeight(), Color.WHITE);
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
