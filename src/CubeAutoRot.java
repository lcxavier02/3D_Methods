import java.awt.*;
import java.awt.image.BufferedImage;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;

public class CubeAutoRot {
  private BufferedImage buffer;
  private Graphics graphicsBuffer;
  public int cubeX = 100;
  public int cubeY = 100;
  public int cubeZ = 100;
  public double angleX = 0;
  public double angleY = 0;
  public double angleZ = 0;
  int size = 50;
  private Vector3D lightDirection = new Vector3D(1, 1, 1).normalize(); // Dirección de la fuente de luz

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
    // Limpiar el Z-Buffer antes de dibujar
    double[] zBuffer = new double[buffer.getWidth() * buffer.getHeight()];
    Arrays.fill(zBuffer, Double.NEGATIVE_INFINITY);

    drawFaces(rotatedVertices, projectedVertex, zBuffer);
    drawEdges(projectedVertex);
  }

  private void drawFaces(double[][] rotatedVertices, int[][] projectedVertices, double[] zBuffer) {
    int[][] faces = {
        { 0, 1, 2, 3 }, { 4, 5, 6, 7 }, // Front and Back faces
        { 0, 4, 7, 3 }, { 1, 5, 6, 2 }, // Left and Right faces
        { 0, 1, 5, 4 }, { 2, 3, 7, 6 } // Top and Bottom faces
    };

    Color[] faceColors = {
        Color.BLUE, Color.GREEN, Color.RED, // Front, Back, Left
        Color.YELLOW, Color.CYAN, Color.MAGENTA // Right, Top, Bottom
    };

    // Lista para almacenar las caras ordenadas por su coordenada Z mínima
    ArrayList<Integer> sortedFaces = new ArrayList<>();

    // Calcular y ordenar las caras por su profundidad (coordenada Z mínima)
    for (int i = 0; i < faces.length; i++) {
      int[] face = faces[i];
      double[] zPoints = new double[4];

      // Calcular la coordenada Z mínima de la cara
      double minZ = Double.MAX_VALUE;
      for (int j = 0; j < face.length; j++) {
        zPoints[j] = rotatedVertices[face[j]][2];
        if (zPoints[j] < minZ) {
          minZ = zPoints[j];
        }
      }

      // Agregar la cara al listado ordenado por su coordenada Z mínima
      sortedFaces.add(i);
    }

    // Ordenar las caras de acuerdo a su coordenada Z mínima (profundidad)
    sortedFaces.sort((i1, i2) -> {
      double minZ1 = Math.min(Math.min(rotatedVertices[faces[i1][0]][2], rotatedVertices[faces[i1][1]][2]),
          Math.min(rotatedVertices[faces[i1][2]][2], rotatedVertices[faces[i1][3]][2]));
      double minZ2 = Math.min(Math.min(rotatedVertices[faces[i2][0]][2], rotatedVertices[faces[i2][1]][2]),
          Math.min(rotatedVertices[faces[i2][2]][2], rotatedVertices[faces[i2][3]][2]));
      return Double.compare(minZ1, minZ2);
    });

    // Dibujar las caras en el orden de su profundidad (de más cercanas a más
    // lejanas)
    for (int index : sortedFaces) {
      int[] face = faces[index];
      Color color = faceColors[index];

      // Determinar el orden de los vértices para dibujar el polígono
      int[] xPoints = new int[4];
      int[] yPoints = new int[4];
      for (int j = 0; j < face.length; j++) {
        xPoints[j] = projectedVertices[face[j]][0];
        yPoints[j] = projectedVertices[face[j]][1];
      }

      // Calcular el mínimo de la coordenada Z para el ordenamiento
      double minZ = Math.min(Math.min(rotatedVertices[face[0]][2], rotatedVertices[face[1]][2]),
          Math.min(rotatedVertices[face[2]][2], rotatedVertices[face[3]][2]));

      // Verificar el Z-buffer y dibujar la cara si es visible
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

      // Dibujar la cara si es visible y actualizar el Z-buffer
      if (drawFace) {
        fillPolygon(xPoints, yPoints, color);

        // Actualizar el Z-buffer para los píxeles dibujados
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
  }

  private void drawEdges(int[][] vertex) {
    int[][] edges = {
        { 0, 1 }, { 1, 2 }, { 2, 3 }, { 3, 0 },
        { 4, 5 }, { 5, 6 }, { 6, 7 }, { 7, 4 },
        { 0, 4 }, { 1, 5 }, { 2, 6 }, { 3, 7 }
    };

    Color color = Color.BLACK;
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

  public void fillPolygon(int[] xPoints, int[] yPoints, Color color) {
    if (xPoints.length != yPoints.length || xPoints.length < 3) {
      throw new IllegalArgumentException("Arrays xPoints and yPoints must have the same length and at least 3 points");
    }

    // Encontrar el bounding box del polígono (rectángulo que contiene al polígono)
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

    // Rellenar el polígono usando el algoritmo de scanline
    for (int y = minY; y <= maxY; y++) {
      // Lista para almacenar los puntos de intersección con la línea de scan
      ArrayList<Integer> intersections = new ArrayList<>();

      // Encontrar intersecciones con cada borde del polígono
      for (int i = 0; i < xPoints.length; i++) {
        int j = (i + 1) % xPoints.length; // Índice del siguiente punto en el polígono

        int x1 = xPoints[i];
        int y1 = yPoints[i];
        int x2 = xPoints[j];
        int y2 = yPoints[j];

        if ((y1 <= y && y2 > y) || (y1 > y && y2 <= y)) {
          // Calcular la intersección x de la línea con el borde del polígono
          double xIntersect = (double) (x1 + (double) (y - y1) / (y2 - y1) * (x2 - x1));

          // Convertir a int y agregar a la lista de intersecciones
          intersections.add((int) xIntersect);
        }
      }

      // Ordenar las intersecciones de izquierda a derecha
      Collections.sort(intersections);

      // Dibujar la línea horizontal entre pares de intersecciones
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
    fillRect(0, 0, buffer.getWidth(), buffer.getHeight(), Color.WHITE);
  }

  public void rotate() {
    angleX += 0.2;
    angleY += 0.1;
    angleZ += 0.2;
  }

}
