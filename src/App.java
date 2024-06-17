import javax.swing.JFrame;
import java.awt.Color;

public class App extends JFrame {
    TDPanel panel;

    public App() {
        panel = new TDPanel();
        this.add(panel);
        this.setTitle("3D");
        this.setResizable(false);
        this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        this.setBackground(Color.BLACK);

        pack();
        setLocationRelativeTo(null);
        this.setVisible(true);
    }

    public static void main(String[] args) throws Exception {
        App app = new App();
    }
}
