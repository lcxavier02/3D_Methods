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

  public double dot(Vector3D v) {
    return x * v.x + y * v.y + z * v.z;
  }

  public Vector3D cross(Vector3D v) {
    double crossX = y * v.z - z * v.y;
    double crossY = z * v.x - x * v.z;
    double crossZ = x * v.y - y * v.x;
    return new Vector3D(crossX, crossY, crossZ);
  }

  public Vector3D normalize() {
    double magnitude = Math.sqrt(x * x + y * y + z * z);
    return new Vector3D(x / magnitude, y / magnitude, z / magnitude);
  }

  public Vector3D reflect(Vector3D normal) {
    double dotProduct = dot(normal);
    double reflectX = x - 2 * dotProduct * normal.x;
    double reflectY = y - 2 * dotProduct * normal.y;
    double reflectZ = z - 2 * dotProduct * normal.z;
    return new Vector3D(reflectX, reflectY, reflectZ);
  }
}
