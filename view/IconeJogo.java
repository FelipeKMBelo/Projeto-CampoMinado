package view;

import javax.swing.Icon;
import java.awt.*;

/** Ícones vetoriais simples: não dependem de imagens externas ou fontes de emojis. */
public class IconeJogo implements Icon {
    private final boolean bandeira;
    public IconeJogo(boolean bandeira) { this.bandeira = bandeira; }
    public int getIconWidth() { return 24; }
    public int getIconHeight() { return 24; }
    public void paintIcon(Component c, Graphics original, int x, int y) {
        Graphics2D g = (Graphics2D) original.create();
        g.translate(x, y);
        g.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        g.setStroke(new BasicStroke(2));
        if (bandeira) {
            g.setColor(new Color(245, 190, 45));
            g.fillPolygon(new int[]{6, 20, 6}, new int[]{3, 8, 12}, 3);
            g.setColor(Color.WHITE);
            g.drawLine(6, 3, 6, 21);
            g.drawLine(3, 21, 14, 21);
        } else {
            g.setColor(new Color(255, 195, 70));
            g.drawArc(12, 1, 8, 10, 0, 160);
            g.setColor(new Color(25, 25, 30));
            g.fillOval(3, 7, 17, 16);
            g.setColor(Color.WHITE);
            g.drawOval(3, 7, 17, 16);
            g.fillOval(7, 10, 4, 4);
        }
        g.dispose();
    }
}
