import java.awt.*;
import java.awt.image.BufferedImage;

public class CubeRot {
  private BufferedImage buffer;
  private Graphics graphicsBuffer;
  public int cubeX = 100;
  public int cubeY = 100;
  public int cubeZ = 100;
  public double angleX = 0;
  public double angleY = 0;
  public double angleZ = 0;
  int size = 50;

  public CubeRot(BufferedImage buffer) {
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

  private int[] projectVertex(double[] vertex) {
    int x = (int) (vertex[0] * 2) + 410;
    int y = (int) (vertex[1] * 2) + 410;
    return new int[] { x, y };
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

  public void drawCube() {
    double[][] vertices = new double[][] {
        { -size, -size, -size }, { size, -size, -size }, { size, size, -size }, { -size, size, -size },
        { -size, -size, size }, { size, -size, size }, { size, size, size }, { -size, size, size }
    };

    double[][] rotatedVertices = new double[8][3];
    for (int i = 0; i < vertices.length; i++) {
      double[] rotatedX = rotateX(vertices[i], angleX);
      double[] rotatedXY = rotateY(rotatedX, angleY);
      double[] rotatedXYZ = rotateZ(rotatedXY, angleZ);
      rotatedVertices[i] = rotatedXYZ;
    }

    int[][] projectedVertex = new int[8][2];
    for (int i = 0; i < rotatedVertices.length; i++) {
      projectedVertex[i] = projectVertex(rotatedVertices[i]);
    }

    drawEdges(projectedVertex);
  }

  private void drawEdges(int[][] vertex) {
    int[][] edges = {
        { 0, 1 }, { 1, 2 }, { 2, 3 }, { 3, 0 },
        { 4, 5 }, { 5, 6 }, { 6, 7 }, { 7, 4 },
        { 0, 4 }, { 1, 5 }, { 2, 6 }, { 3, 7 }
    };

    Color color = Color.GREEN;
    for (int[] edge : edges) {
      int x0 = vertex[edge[0]][0];
      int y0 = vertex[edge[0]][1];
      int x1 = vertex[edge[1]][0];
      int y1 = vertex[edge[1]][1];
      drawLine(x0, y0, x1, y1, color);
    }
  }

  public void fillRect(int x, int y, int width, int height, Color color) {
    for (int j = y; j < y + height; j++) {
      drawLine(x, j, x + width - 1, j, color);
    }
  }

  public void showCube() {
    if (buffer != null) {
      drawCube();
    }
  }

  public void clearBuffer() {
    fillRect(0, 0, buffer.getWidth(), buffer.getHeight(), Color.WHITE);
  }

  public void rotateCube(double deltaX, double deltaY, double deltaZ) {
    angleY += deltaX;
    angleX += deltaY;
    angleZ += deltaY;
  }

}
