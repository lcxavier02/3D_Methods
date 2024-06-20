public class Vector3D {
  private double x, y, z;

  public Vector3D(double x, double y, double z) {
    this.x = x;
    this.y = y;
    this.z = z;
  }

  public double getX() {
    return x;
  }

  public double getY() {
    return y;
  }

  public double getZ() {
    return z;
  }

  public double dotProduct(Vector3D other) {
    return this.x * other.x + this.y * other.y + this.z * other.z;
  }

  // Método para calcular el producto cruzado
  public Vector3D crossProduct(Vector3D other) {
    double newX = this.y * other.z - this.z * other.y;
    double newY = this.z * other.x - this.x * other.z;
    double newZ = this.x * other.y - this.y * other.x;
    return new Vector3D(newX, newY, newZ);
  }

  public double magnitude() {
    return Math.sqrt(x * x + y * y + z * z);
  }

  // Método para normalizar el vector
  public Vector3D normalize() {
    double magnitude = magnitude();
    if (magnitude == 0) {
      throw new ArithmeticException("Cannot normalize a zero-length vector");
    }
    return new Vector3D(x / magnitude, y / magnitude, z / magnitude);
  }

  public Vector3D reflect(Vector3D normal) {
    double dotProduct = dotProduct(normal);
    double reflectX = x - 2 * dotProduct * normal.x;
    double reflectY = y - 2 * dotProduct * normal.y;
    double reflectZ = z - 2 * dotProduct * normal.z;
    return new Vector3D(reflectX, reflectY, reflectZ);
  }
}
