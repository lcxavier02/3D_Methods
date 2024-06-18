import java.awt.*;
import java.awt.image.BufferedImage;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;

public class CubeAutoRot {
  private BufferedImage buffer;
  private Graphics graphicsBuffer;
  public int cubeX = 0;
  public int cubeY = 0;
  public int cubeZ = 0;
  public double angleX = 0;
  public double angleY = 0;
  public double angleZ = 0;
  private double scaleX = 1.0;
  private double scaleY = 1.0;
  int size = 50;
  private Vector3D lightDirection = new Vector3D(1, -1, 1).normalize();
  private boolean drawEdges = true;

  public void toggleEdges() {
    drawEdges = !drawEdges;
  }

  public void setLightDirection(Vector3D lightDirection) {
    this.lightDirection = lightDirection.normalize();
  }

  public CubeAutoRot(BufferedImage buffer) {
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
    int x = (int) (vertex[0] * scaleX) + 410;
    int y = (int) (vertex[1] * scaleY) + 410;
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
    Vector3D lightDir = new Vector3D(lightDirection.getX(), lightDirection.getY(), lightDirection.getZ());

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

    double[][] traslatedVertices = new double[8][3];
    for (int i = 0; i < rotatedVertices.length; i++) {
      double x = rotatedVertices[i][0] + cubeX;
      double y = rotatedVertices[i][1] + cubeY;
      double z = rotatedVertices[i][2] + cubeZ;
      traslatedVertices[i] = new double[] { x, y, z };
    }

    lightDir = lightDir.normalize();

    int[][] projectedVertex = new int[8][2];
    for (int i = 0; i < rotatedVertices.length; i++) {
      projectedVertex[i] = projectVertex(traslatedVertices[i]);
    }

    double[] zBuffer = new double[buffer.getWidth() * buffer.getHeight()];
    Arrays.fill(zBuffer, Double.NEGATIVE_INFINITY);

    drawFaces(rotatedVertices, projectedVertex, zBuffer);
  }

  public void drawFaces(double[][] rotatedVertices, int[][] projectedVertices, double[] zBuffer) {
    int[][] faces = {
        { 0, 1, 2, 3 }, { 4, 5, 6, 7 }, // Front and Back faces
        { 0, 4, 7, 3 }, { 1, 5, 6, 2 }, // Left and Right faces
        { 0, 1, 5, 4 }, { 2, 3, 7, 6 } // Top and Bottom faces
    };

    ArrayList<Integer> sortedFaces = new ArrayList<>();

    for (int i = 0; i < faces.length; i++) {
      sortedFaces.add(i);
    }

    sortedFaces.sort((i1, i2) -> {
      double minZ1 = Math.min(Math.min(rotatedVertices[faces[i1][0]][2], rotatedVertices[faces[i1][1]][2]),
          Math.min(rotatedVertices[faces[i1][2]][2], rotatedVertices[faces[i1][3]][2]));
      double minZ2 = Math.min(Math.min(rotatedVertices[faces[i2][0]][2], rotatedVertices[faces[i2][1]][2]),
          Math.min(rotatedVertices[faces[i2][2]][2], rotatedVertices[faces[i2][3]][2]));
      return Double.compare(minZ1, minZ2);
    });

    for (int index : sortedFaces) {
      int[] face = faces[index];

      // Calculate face normal
      double[] v1 = subtract(rotatedVertices[face[1]], rotatedVertices[face[0]]);
      double[] v2 = subtract(rotatedVertices[face[2]], rotatedVertices[face[0]]);
      double[] normal = normalize(crossProduct(v1, v2));

      // Calculate light intensity
      double intensity = dotProduct(normal, lightDirection);
      intensity = Math.max(0, intensity); // Clamp intensity to [0, 1]

      // Calculate face color based on intensity
      Color baseColor = getBaseColor(index);
      Color color = applyLightIntensity(baseColor, intensity);

      int[] xPoints = new int[4];
      int[] yPoints = new int[4];
      for (int j = 0; j < face.length; j++) {
        xPoints[j] = projectedVertices[face[j]][0];
        yPoints[j] = projectedVertices[face[j]][1];
      }

      double minZ = Math.min(Math.min(rotatedVertices[face[0]][2], rotatedVertices[face[1]][2]),
          Math.min(rotatedVertices[face[2]][2], rotatedVertices[face[3]][2]));

      boolean drawFace = true;
      for (int py = Math.min(yPoints[0], Math.min(yPoints[1], Math.min(yPoints[2], yPoints[3]))); py <= Math
          .max(yPoints[0], Math.max(yPoints[1], Math.max(yPoints[2], yPoints[3]))); py++) {
        for (int px = Math.min(xPoints[0], Math.min(xPoints[1], Math.min(xPoints[2], xPoints[3]))); px <= Math
            .max(xPoints[0], Math.max(xPoints[1], Math.max(xPoints[2], xPoints[3]))); px++) {
          if (py >= 0 && py < buffer.getHeight() && px >= 0 && px < buffer.getWidth()) {
            if (minZ <= zBuffer[py * buffer.getWidth() + px]) {
              drawFace = false;
              break;
            }
          }
        }
        if (!drawFace) {
          break;
        }
      }

      if (drawFace) {
        fillPolygon(xPoints, yPoints, color);

        for (int py = Math.min(yPoints[0], Math.min(yPoints[1], Math.min(yPoints[2], yPoints[3]))); py <= Math
            .max(yPoints[0], Math.max(yPoints[1], Math.max(yPoints[2], yPoints[3]))); py++) {
          for (int px = Math.min(xPoints[0], Math.min(xPoints[1], Math.min(xPoints[2], xPoints[3]))); px <= Math
              .max(xPoints[0], Math.max(xPoints[1], Math.max(xPoints[2], xPoints[3]))); px++) {
            if (py >= 0 && py < buffer.getHeight() && px >= 0 && px < buffer.getWidth()) {
              zBuffer[py * buffer.getWidth() + px] = minZ;
            }
          }
        }
      }
    }

    if (drawEdges) {
      drawEdges(projectedVertices, rotatedVertices, zBuffer);
    }
  }

  private double[] subtract(double[] v1, double[] v2) {
    return new double[] { v1[0] - v2[0], v1[1] - v2[1], v1[2] - v2[2] };
  }

  private double[] normalize(double[] v) {
    double length = Math.sqrt(v[0] * v[0] + v[1] * v[1] + v[2] * v[2]);
    return new double[] { v[0] / length, v[1] / length, v[2] / length };
  }

  private double[] crossProduct(double[] v1, double[] v2) {
    return new double[] {
        v1[1] * v2[2] - v1[2] * v2[1],
        v1[2] * v2[0] - v1[0] * v2[2],
        v1[0] * v2[1] - v1[1] * v2[0]
    };
  }

  private double dotProduct(double[] v1, Vector3D v2) {
    return v1[0] * v2.getX() + v1[1] * v2.getY() + v1[2] * v2.getZ();
  }

  private Color getBaseColor(int faceIndex) {
    switch (faceIndex) {
      case 0:
        return Color.GREEN;
      case 1:
        return Color.BLUE;
      case 2:
        return Color.ORANGE;
      case 3:
        return Color.RED;
      case 4:
        return Color.WHITE;
      case 5:
        return Color.YELLOW;
      default:
        return Color.GRAY;
    }
  }

  private Color applyLightIntensity(Color color, double intensity) {
    double minIntensity = 0.2; // Intensidad mínima para que las caras no queden totalmente oscuras
    intensity = Math.max(intensity, minIntensity); // Asegurarse de que la intensidad no sea menor que el mínimo

    int r = (int) (color.getRed() * intensity);
    int g = (int) (color.getGreen() * intensity);
    int b = (int) (color.getBlue() * intensity);
    return new Color(r, g, b);
  }

  private void drawEdges(int[][] projectedVertices, double[][] rotatedVertices, double[] zBuffer) {
    int[][] edges = {
        { 0, 1 }, { 1, 2 }, { 2, 3 }, { 3, 0 },
        { 4, 5 }, { 5, 6 }, { 6, 7 }, { 7, 4 },
        { 0, 4 }, { 1, 5 }, { 2, 6 }, { 3, 7 }
    };

    Color color = Color.BLACK;

    for (int[] edge : edges) {
      int x0 = projectedVertices[edge[0]][0];
      int y0 = projectedVertices[edge[0]][1];
      double z0 = rotatedVertices[edge[0]][2];
      int x1 = projectedVertices[edge[1]][0];
      int y1 = projectedVertices[edge[1]][1];
      double z1 = rotatedVertices[edge[1]][2];

      boolean visible = true;
      int dx = Math.abs(x1 - x0);
      int dy = Math.abs(y1 - y0);

      int xi = (x1 > x0) ? 1 : -1;
      int yi = (y1 > y0) ? 1 : -1;

      int x = x0;
      int y = y0;
      int pk;

      if (dx > dy) {
        pk = 2 * dy - dx;
        while (x != x1) {
          if (pk > 0) {
            x = x + xi;
            y = y + yi;
            pk = pk + 2 * (dy - dx);
          } else {
            x = x + xi;
            pk = pk + 2 * dy;
          }
          double z = z0 + (z1 - z0) * Math.abs(x - x0) / dx;
          if (z <= zBuffer[y * buffer.getWidth() + x]) {
            visible = false;
            break;
          }
        }
      } else {
        pk = 2 * dx - dy;
        while (y != y1) {
          if (pk > 0) {
            x = x + xi;
            y = y + yi;
            pk = pk + 2 * (dx - dy);
          } else {
            y = y + yi;
            pk = pk + 2 * dx;
          }
          double z = z0 + (z1 - z0) * Math.abs(y - y0) / dy;
          if (z <= zBuffer[y * buffer.getWidth() + x]) {
            visible = false;
            break;
          }
        }
      }

      if (visible) {
        drawLine(x0, y0, x1, y1, color);
      }
    }
  }

  public void fillRect(int x, int y, int width, int height, Color color) {
    for (int j = y; j < y + height; j++) {
      drawLine(x, j, x + width - 1, j, color);
    }
  }

  public void fillPolygon(int[] xPoints, int[] yPoints, Color color) {
    int minX = Integer.MAX_VALUE;
    int maxX = Integer.MIN_VALUE;
    int minY = Integer.MAX_VALUE;
    int maxY = Integer.MIN_VALUE;

    for (int i = 0; i < xPoints.length; i++) {
      if (xPoints[i] < minX)
        minX = xPoints[i];
      if (xPoints[i] > maxX)
        maxX = xPoints[i];
      if (yPoints[i] < minY)
        minY = yPoints[i];
      if (yPoints[i] > maxY)
        maxY = yPoints[i];
    }

    for (int y = minY; y <= maxY; y++) {
      ArrayList<Integer> intersections = new ArrayList<>();

      for (int i = 0; i < xPoints.length; i++) {
        int j = (i + 1) % xPoints.length;

        int x1 = xPoints[i];
        int y1 = yPoints[i];
        int x2 = xPoints[j];
        int y2 = yPoints[j];

        if ((y1 <= y && y2 > y) || (y1 > y && y2 <= y)) {
          double xIntersect = (double) (x1 + (double) (y - y1) / (y2 - y1) * (x2 - x1));

          intersections.add((int) xIntersect);
        }
      }

      Collections.sort(intersections);

      for (int k = 0; k < intersections.size(); k += 2) {
        int xStart = intersections.get(k);
        int xEnd = intersections.get(k + 1);
        drawLine(xStart, y, xEnd, y, color);
      }
    }
  }

  public void showCube() {
    if (buffer != null) {
      drawCube();
    }
  }

  public void clearBuffer() {
    fillRect(0, 0, buffer.getWidth(), buffer.getHeight(), Color.BLACK);
  }

  public void rotate() {
    angleX += 0.1;
    angleY += 0.05;
    angleZ += 0.1;
  }

  public void move(int dx, int dy, int dz) {
    cubeX += dx;
    cubeY += dy;
    cubeZ += dz;
  }

  public void scale(double scale) {
    scaleX *= scale;
    scaleY *= scale;
  }

}
