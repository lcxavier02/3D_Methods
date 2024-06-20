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

    startAutoRotation();
  }

  public void createSurface() {
    surface = new Surface(buffer);
  }

  public void createCurve() {
    curve = new ExplicitCurve(buffer);
  }

  private void startAutoRotation() {
    timer = new Timer();
    timer.scheduleAtFixedRate(new RotateTask(), 0, 65);
  }

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
    // cubePersp.showCube();
    // cubeTrasl.clearBuffer();
    // cubeTrasl.showCube();
    // cubeEscl.clearBuffer();
    // cubeEscl.showCube();
    // cubeRot.clearBuffer();
    // cubeRot.showCube();
    // curve.clearBuffer();
    // curve.showCurve();
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
    int step = 5;

    switch (e.getKeyCode()) {
      case KeyEvent.VK_LEFT:
        cubeRot.angleY -= Math.toRadians(10);
        break;
      case KeyEvent.VK_RIGHT:
        cubeRot.angleY += Math.toRadians(10);
        break;
      case KeyEvent.VK_UP:
        cubeRot.angleX -= Math.toRadians(10);
        break;
      case KeyEvent.VK_DOWN:
        cubeRot.angleX += Math.toRadians(10);
        break;
      case KeyEvent.VK_A:
        cubeRot.angleZ -= Math.toRadians(10);
        break;
      case KeyEvent.VK_D:
        cubeRot.angleZ += Math.toRadians(10);
        break;
    }

    switch (e.getKeyCode()) {
      case KeyEvent.VK_0:
        cubeEscl.scale(1.1);
        break;
      case KeyEvent.VK_1:
        cubeEscl.scale(0.9);
        break;
    }

    switch (e.getKeyCode()) {
      case KeyEvent.VK_W:
        cubeTrasl.move(0, 0, step);
        break;
      case KeyEvent.VK_S:
        cubeTrasl.move(0, 0, -step);
        break;
      case KeyEvent.VK_A:
        cubeTrasl.move(-step, 0, 0);
        break;
      case KeyEvent.VK_D:
        cubeTrasl.move(step, 0, 0);
        break;
      case KeyEvent.VK_Q:
        cubeTrasl.move(0, -step, 0);
        break;
      case KeyEvent.VK_E:
        cubeTrasl.move(0, step, 0);
        break;
    }

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
      case KeyEvent.VK_LEFT:
        curve.angleY -= Math.toRadians(10);
        break;
      case KeyEvent.VK_RIGHT:
        curve.angleY += Math.toRadians(10);
        break;
      case KeyEvent.VK_UP:
        curve.angleX -= Math.toRadians(10);
        break;
      case KeyEvent.VK_DOWN:
        curve.angleX += Math.toRadians(10);
        break;
      case KeyEvent.VK_O:
        curve.angleZ -= Math.toRadians(10);
        break;
      case KeyEvent.VK_P:
        curve.angleZ += Math.toRadians(10);
        break;
    }

    switch (e.getKeyCode()) {
      case KeyEvent.VK_W:
        surface.move(0, -step, 0);
        break;
      case KeyEvent.VK_S:
        surface.move(0, step, 0);
        break;
      case KeyEvent.VK_A:
        surface.move(-step, 0, 0);
        break;
      case KeyEvent.VK_D:
        surface.move(step, 0, 0);
        break;
      case KeyEvent.VK_Q:
        surface.move(0, 0, step);
        break;
      case KeyEvent.VK_E:
        surface.move(0, 0, -step);
        break;
      case KeyEvent.VK_0:
        surface.scale(1.1);
        break;
      case KeyEvent.VK_1:
        surface.scale(0.9);
        break;
      case KeyEvent.VK_LEFT:
        surface.angleY -= Math.toRadians(10);
        break;
      case KeyEvent.VK_RIGHT:
        surface.angleY += Math.toRadians(10);
        break;
      case KeyEvent.VK_UP:
        surface.angleX -= Math.toRadians(10);
        break;
      case KeyEvent.VK_DOWN:
        surface.angleX += Math.toRadians(10);
        break;
      case KeyEvent.VK_O:
        surface.angleZ -= Math.toRadians(10);
        break;
      case KeyEvent.VK_P:
        surface.angleZ += Math.toRadians(10);
        break;
    }

    switch (e.getKeyCode()) {
      case KeyEvent.VK_0:
        cubeAutoRot.scale(1.1);
        break;
      case KeyEvent.VK_1:
        cubeAutoRot.scale(0.9);
        break;
      case KeyEvent.VK_W:
        cubeAutoRot.move(0, -step, 0);
        break;
      case KeyEvent.VK_S:
        cubeAutoRot.move(0, step, 0);
        break;
      case KeyEvent.VK_A:
        cubeAutoRot.move(-step, 0, 0);
        break;
      case KeyEvent.VK_D:
        cubeAutoRot.move(step, 0, 0);
        break;
      case KeyEvent.VK_Q:
        cubeAutoRot.move(0, 0, step);
        break;
      case KeyEvent.VK_E:
        cubeAutoRot.move(0, 0, -step);
        break;
      case KeyEvent.VK_U:
        cubeAutoRot.toggleEdges();
        break;
      case KeyEvent.VK_I:
        cubeAutoRot.toggleFill();
        break;
    }
    repaint();
  }

  @Override
  public void keyReleased(KeyEvent e) {
  }

  @Override
  public void mouseDragged(MouseEvent e) {
  }

  @Override
  public void mouseMoved(MouseEvent e) {
  }

  @Override
  public void mouseClicked(MouseEvent e) {
  }

  @Override
  public void mousePressed(MouseEvent e) {
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

  private class RotateTask extends TimerTask {
    @Override
    public void run() {
      cubeAutoRot.clearBuffer();
      cubeAutoRot.rotate();
      cubeAutoRot.showCube();
      repaint();
    }
  }

}
