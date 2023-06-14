

import java.awt.BasicStroke;
import java.awt.Canvas;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;

public class Drawing extends Canvas {

//    
//    public static void main(String[] args) {
//
//        Drawing dr = new Drawing(null);
//        JFrame frame = new JFrame("My Drawing");
//        frame.add(dr);
//        frame.pack();
//        frame.setVisible(true);
//    }

    @Override
    public Dimension getPreferredSize() {
        return new Dimension(400, 400);
    }

    @Override
    public void paint(Graphics g) {
        super.paint(g);
        Graphics2D g2 = (Graphics2D) g;
        drawGrafico(g2);
//        g2.setColor(Color.GREEN);
//        drawGrafico(g2);

    }

    Drawing() {
    }

    public void drawGrafico(Graphics2D g) {
        int y = this.getHeight();
        String Curva = "10,20,30";
        int nPoints = 3;
        int[] ypoints = new int[nPoints];
        String[] ychar = Curva.split(",");
        if (nPoints > 0) {
            int[] xpoints = new int[nPoints];
            for (int i = 0; i < nPoints; i++) {
                xpoints[i] = i * 10;
                ypoints[i] = y - Integer.parseInt(ychar[i]) / 2;
            }
            g.setStroke(new BasicStroke(3));
            g.setColor(Color.BLACK);
            //        g2.setColor(Color.GREEN);

            g.drawPolyline(xpoints, ypoints, nPoints);
        }
    }

}
