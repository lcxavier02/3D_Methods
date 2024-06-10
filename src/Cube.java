import java.awt.*;
import java.awt.image.BufferedImage;

public class Cube {
  private BufferedImage buffer;
  private Graphics graphicsBuffer;

  public Cube(BufferedImage buffer) {
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

  private int[] projectVertex(int x, int y, int z, double u, double v) {
    int x2D = (int) (x + u * z);
    int y2D = (int) (y + v * z);
    return new int[] { x2D, y2D };
  }

  public void drawCube(int x1, int y1, int z1, int size) {
    double theta = Math.PI / 4;
    double u = Math.cos(theta);
    double v = Math.sin(theta);

    int[][] vertices = {
        { x1, y1, z1 },
        { x1 + size, y1, z1 },
        { x1 + size, y1 + size, z1 },
        { x1, y1 + size, z1 },
        { x1, y1, z1 + size },
        { x1 + size, y1, z1 + size },
        { x1 + size, y1 + size, z1 + size },
        { x1, y1 + size, z1 + size }
    };

    int[][] projectedVertices = new int[8][2];
    for (int i = 0; i < vertices.length; i++) {
      int[] projected = projectVertex(vertices[i][0], vertices[i][1], vertices[i][2], u, v);
      projectedVertices[i][0] = projected[0];
      projectedVertices[i][1] = projected[1];
    }

    int[][] edges = {
        { 0, 1 }, { 1, 2 }, { 2, 3 }, { 3, 0 },
        { 4, 5 }, { 5, 6 }, { 6, 7 }, { 7, 4 },
        { 0, 4 }, { 1, 5 }, { 2, 6 }, { 3, 7 }
    };

    Color color = Color.BLUE;
    for (int[] edge : edges) {
      int x1_2D = projectedVertices[edge[0]][0];
      int y1_2D = projectedVertices[edge[0]][1];
      int x2_2D = projectedVertices[edge[1]][0];
      int y2_2D = projectedVertices[edge[1]][1];
      drawLine(x1_2D, y1_2D, x2_2D, y2_2D, color);
    }
  }

  public void showCube() {
    if (buffer != null) {
      drawCube(100, 100, 100, 50);
    }
  }
}
