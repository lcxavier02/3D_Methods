import java.awt.*;
import java.awt.image.BufferedImage;

public class CubeTrasl {
  private BufferedImage buffer;
  private Graphics graphicsBuffer;
  private double d = 300;
  public static int cubeX = 100;
  public static int cubeY = 100;
  public static int cubeZ = 100;

  public CubeTrasl(BufferedImage buffer) {
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
    double u = d / (d + z);
    int x2D = (int) (x * u);
    int y2D = (int) (y * u);
    return new int[] { x2D, y2D };
  }

  public void drawCube() {
    int size = 50;

    int[][] vertices = {
        { cubeX, cubeY, cubeZ },
        { cubeX + size, cubeY, cubeZ },
        { cubeX + size, cubeY + size, cubeZ },
        { cubeX, cubeY + size, cubeZ },
        { cubeX, cubeY, cubeZ + size },
        { cubeX + size, cubeY, cubeZ + size },
        { cubeX + size, cubeY + size, cubeZ + size },
        { cubeX, cubeY + size, cubeZ + size }
    };

    int[][] projectedVertices = new int[8][2];
    for (int i = 0; i < vertices.length; i++) {
      int[] projected = projectVertex(vertices[i][0], vertices[i][1], vertices[i][2]);
      projectedVertices[i][0] = projected[0] + buffer.getWidth() / 2;
      projectedVertices[i][1] = projected[1] + buffer.getHeight() / 2;
    }

    int[][] edges = {
        { 0, 1 }, { 1, 2 }, { 2, 3 }, { 3, 0 },
        { 4, 5 }, { 5, 6 }, { 6, 7 }, { 7, 4 },
        { 0, 4 }, { 1, 5 }, { 2, 6 }, { 3, 7 }
    };

    Color color = Color.RED;
    for (int[] edge : edges) {
      int x1_2D = projectedVertices[edge[0]][0];
      int y1_2D = projectedVertices[edge[0]][1];
      int x2_2D = projectedVertices[edge[1]][0];
      int y2_2D = projectedVertices[edge[1]][1];
      drawLine(x1_2D, y1_2D, x2_2D, y2_2D, color);
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

  public void move(int dx, int dy, int dz) {
    cubeX += dx;
    cubeY += dy;
    cubeZ += dz;
  }

  public void clearBuffer() {
    fillRect(0, 0, buffer.getWidth(), buffer.getHeight(), Color.WHITE);
  }
}
