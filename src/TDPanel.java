import java.awt.*;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;
import java.awt.image.BufferedImage;
import java.util.Timer;
import java.util.TimerTask;

import javax.swing.JPanel;

public class TDPanel extends JPanel implements KeyListener, MouseListener, MouseMotionListener {
  private static final int WINDOW_WIDTH = 820;
  private static final int WINDOW_HEIGHT = 820;
  static final Dimension WINDOW_SIZE = new Dimension(WINDOW_WIDTH, WINDOW_HEIGHT);
  public BufferedImage buffer;
  private Cube cube;
  private CubePersp cubePersp;
  private CubeTrasl cubeTrasl;
  private CubeEscl cubeEscl;
  private CubeRot cubeRot;
  private CubeAutoRot cubeAutoRot;
  private ExplicitCurve curve;
  private Surface surface;
  private int mouseY;
  private int lastMouseX;
  private int lastMouseY;
  private Point lastMousePosition;
  private Timer timer;

  Graphics graphicsBuffer;
  Image image;

  public TDPanel() {
    if (buffer == null) {
      buffer = new BufferedImage(WINDOW_WIDTH, WINDOW_HEIGHT, BufferedImage.TYPE_INT_ARGB);
    }

    createCube();
    createCubePersp();
    createCubeTrasl();
    createCubeEscl();
    createCubeRot();
    createCubeAutoRot();
    createCurve();
    createSurface();

    setPreferredSize(WINDOW_SIZE);
    setFocusable(true);
    addKeyListener(this);
    addMouseListener(this);
    addMouseMotionListener(this);

    // startAutoRotation();
  }

  public void createSurface() {
    surface = new Surface(buffer);
  }

  public void createCurve() {
    curve = new ExplicitCurve(buffer);
  }

  // private void startAutoRotation() {
  // timer = new Timer();
  // timer.scheduleAtFixedRate(new RotateTask(), 0, 65); // Repintar cada 10
  // milisegundos
  // }

  public void createCubeAutoRot() {
    cubeAutoRot = new CubeAutoRot(buffer);
  }

  public void createCubeRot() {
    cubeRot = new CubeRot(buffer);
  }

  public void createCubeEscl() {
    cubeEscl = new CubeEscl(buffer);
  }

  public void createCubeTrasl() {
    cubeTrasl = new CubeTrasl(buffer);
  }

  public void createCube() {
    cube = new Cube(buffer);
  }

  public void createCubePersp() {
    cubePersp = new CubePersp(buffer);
  }

  @Override
  public void paintComponent(Graphics g) {
    image = createImage(getWidth(), getHeight());
    graphicsBuffer = image.getGraphics();

    // cube.showCube();
    // cuberPersp.showCube();
    // cubeTrasl.clearBuffer();
    // cubeTrasl.showCube();
    // cubeEscl.clearBuffer();
    // cubeEscl.showCube();
    // cubeRot.clearBuffer();
    // cubeRot.showCube();
    curve.clearBuffer();
    curve.showCurve();
    // surface.clearBuffer();
    // surface.showSurface();
    // surface.showCylinder();

    g.drawImage(buffer, 0, 0, this);
  }

  @Override
  public void keyTyped(KeyEvent e) {

  }

  @Override
  public void keyPressed(KeyEvent e) {
    // Traslacion
    // int step = 5;

    // if (e.getKeyCode() == KeyEvent.VK_W) {
    // cubeTrasl.move(0, -step, 0);
    // } else if (e.getKeyCode() == KeyEvent.VK_S) {
    // cubeTrasl.move(0, step, 0);
    // } else if (e.getKeyCode() == KeyEvent.VK_A) {
    // cubeTrasl.move(-step, 0, 0);
    // } else if (e.getKeyCode() == KeyEvent.VK_D) {
    // cubeTrasl.move(step, 0, 0);
    // }

    // repaint();

    int step = 5;
    switch (e.getKeyCode()) {
      case KeyEvent.VK_W:
        curve.move(0, -step, 0);
        break;
      case KeyEvent.VK_S:
        curve.move(0, step, 0);
        break;
      case KeyEvent.VK_A:
        curve.move(-step, 0, 0);
        break;
      case KeyEvent.VK_D:
        curve.move(step, 0, 0);
        break;
      case KeyEvent.VK_Q:
        curve.move(0, 0, step);
        break;
      case KeyEvent.VK_E:
        curve.move(0, 0, -step);
        break;
      case KeyEvent.VK_0:
        curve.scale(1.1);
        break;
      case KeyEvent.VK_1:
        curve.scale(0.9);
        break;
    }
    repaint();

    // int step = 5;
    // switch (e.getKeyCode()) {
    // case KeyEvent.VK_W:
    // surface.move(0, -step, 0);
    // break;
    // case KeyEvent.VK_S:
    // surface.move(0, step, 0);
    // break;
    // case KeyEvent.VK_A:
    // surface.move(-step, 0, 0);
    // break;
    // case KeyEvent.VK_D:
    // surface.move(step, 0, 0);
    // break;
    // case KeyEvent.VK_Q:
    // surface.move(0, 0, step);
    // break;
    // case KeyEvent.VK_E:
    // surface.move(0, 0, -step);
    // break;
    // case KeyEvent.VK_0:
    // surface.scale(1.1);
    // break;
    // case KeyEvent.VK_1:
    // surface.scale(0.9);
    // break;
    // }
    // repaint();
  }

  @Override
  public void keyReleased(KeyEvent e) {
  }

  @Override
  public void mouseDragged(MouseEvent e) {
    // Rotacion
    // Point currentMousePosition = e.getPoint();
    // if (lastMousePosition != null) {
    // double deltaX = currentMousePosition.getX() - lastMousePosition.getX();
    // double deltaY = currentMousePosition.getY() - lastMousePosition.getY();
    // cubeRot.rotateCube(deltaX * 0.01, deltaY * 0.01);
    // }
    // lastMousePosition = currentMousePosition;
    // repaint();

    Point currentMousePosition = e.getPoint();
    if (lastMousePosition != null) {
      double deltaX = currentMousePosition.getX() - lastMousePosition.getX();
      double deltaY = currentMousePosition.getY() - lastMousePosition.getY();
      curve.rotate(deltaX * 0.01, deltaY * 0.01);
    }
    lastMousePosition = currentMousePosition;
    repaint();

    // Point currentMousePosition = e.getPoint();
    // if (lastMousePosition != null) {
    // double deltaX = currentMousePosition.getX() - lastMousePosition.getX();
    // double deltaY = currentMousePosition.getY() - lastMousePosition.getY();
    // surface.rotate(deltaX * 0.01, deltaY * 0.01);
    // }
    // lastMousePosition = currentMousePosition;
    // repaint();
  }

  @Override
  public void mouseMoved(MouseEvent e) {
    // Traslacion
    // int mouseY = e.getY();
    // int deltaY = mouseY - this.mouseY;

    // int step = 5;
    // int dz = (deltaY > 0) ? step : -step;
    // cubeTrasl.move(0, 0, dz);

    // this.mouseY = mouseY;
    // repaint();

    lastMousePosition = e.getPoint();
  }

  @Override
  public void mouseClicked(MouseEvent e) {
    // if (e.getButton() == MouseEvent.BUTTON1) {
    // cubeEscl.scaleCube(1.1); // Escalar hacia arriba con clic izquierdo
    // } else if (e.getButton() == MouseEvent.BUTTON3) {
    // cubeEscl.scaleCube(0.9); // Escalar hacia abajo con clic derecho
    // }
    // repaint();

    // if (e.getButton() == MouseEvent.BUTTON1) {
    // curve.scale(1.1); // Escalar hacia arriba con clic izquierdo
    // } else if (e.getButton() == MouseEvent.BUTTON3) {
    // curve.scale(0.9); // Escalar hacia abajo con clic derecho
    // }

    // repaint();
  }

  @Override
  public void mousePressed(MouseEvent e) {
    // Escalamiento
    // lastMouseX = e.getX();
    // lastMouseY = e.getY();
  }

  @Override
  public void mouseReleased(MouseEvent e) {
  }

  @Override
  public void mouseEntered(MouseEvent e) {
  }

  @Override
  public void mouseExited(MouseEvent e) {
  }

  // private class RotateTask extends TimerTask {
  // @Override
  // public void run() {
  // cubeAutoRot.clearBuffer();
  // cubeAutoRot.rotate(); // Realiza la rotación automática del cubo
  // cubeAutoRot.showCube(); // Muestra el cubo rotado
  // repaint(); // Vuelve a dibujar la escena
  // }
  // }

}
