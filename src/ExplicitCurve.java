import java.awt.*;
import java.awt.image.BufferedImage;

public class ExplicitCurve {
  private BufferedImage buffer;
  private Graphics graphicsBuffer;
  private double d = 300;
  private int surfaceX = 100;
  private int surfaceY = 100;
  private int surfaceZ = 0; // Posición Z de la superficie
  private double angleX = 0; // Ángulo de rotación alrededor del eje X
  private double angleY = 0; // Ángulo de rotación alrededor del eje Y
  private double scaleX = 1.0; // Factor de escalado en el eje X
  private double scaleY = 1.0; // Factor de escalado en el eje Y

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

  private int[] projectVertex(int x, int y, int z) {
    int x2D = (int) (x * scaleX);
    int y2D = (int) (y * scaleY);
    return new int[] { x2D, y2D };
  }

  private int[] rotateVertex(int x, int y, int z) {
    double cosY = Math.cos(angleY);
    double sinY = Math.sin(angleY);
    double cosX = Math.cos(angleX);
    double sinX = Math.sin(angleX);

    // Rotación alrededor del eje Y
    double newX = x * cosY - z * sinY;
    double newZ = x * sinY + z * cosY;

    // Rotación alrededor del eje X
    double newY = y * cosX - newZ * sinX;
    newZ = y * sinX + newZ * cosX;

    return new int[] { (int) newX, (int) newY, (int) newZ };
  }

  public void drawSpiral() {
    int numPoints = 200; // Número de puntos en la espiral
    double height = 200; // Altura total de la espiral
    double radius = 50; // Radio de la espiral
    double coilSpacing = 10; // Espaciado entre las espiras

    // Rango para las variables t y theta
    double tMin = 0.0;
    double tMax = 8.0 * Math.PI;

    for (int i = 0; i < numPoints; i++) {
      double t = tMin + (tMax - tMin) * i / numPoints;

      // Calcular coordenadas para la espiral
      double x = surfaceX + (radius + coilSpacing * t / (2 * Math.PI)) * Math.cos(t) * scaleX;
      double y = surfaceY + (radius + coilSpacing * t / (2 * Math.PI)) * Math.sin(t) * scaleY;
      double z = surfaceZ + height * t / (2 * Math.PI);

      // Calcular coordenadas para el siguiente punto
      double nextT = tMin + (tMax - tMin) * (i + 1) / numPoints;
      double nextX = surfaceX + (radius + coilSpacing * nextT / (2 * Math.PI)) * Math.cos(nextT) * scaleX;
      double nextY = surfaceY + (radius + coilSpacing * nextT / (2 * Math.PI)) * Math.sin(nextT) * scaleY;
      double nextZ = surfaceZ + height * nextT / (2 * Math.PI);

      // Rotación
      int[] rotatedVertex = rotateVertex((int) x, (int) y, (int) z);
      int[] rotatedNextVertex = rotateVertex((int) nextX, (int) nextY, (int) nextZ);

      // Proyección
      int[] projectedVertex = projectVertex(rotatedVertex[0], rotatedVertex[1], rotatedVertex[2]);
      int[] projectedNextVertex = projectVertex(rotatedNextVertex[0], rotatedNextVertex[1], rotatedNextVertex[2]);

      // Dibujar línea entre el punto actual y el siguiente
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

  public void rotate(double deltaX, double deltaY) {
    angleY += deltaX;
    angleX += deltaY;
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
