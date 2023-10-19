/*
 * Copyright (C) 2023 fabio
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package jrivitscreen;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import javax.swing.JPanel;

/**
 *
 * @author fabio
 */
public class JGrafico extends JPanel {

    private String curva;
    private boolean inPrimoPiano = false;
    private String curvaDiRiferimento;
    int nPoints;
    int[] xpoints, ypoints;
    private boolean isInError;
    private String stato;
    private int picco;
    private int posizionePicco;
    private boolean primoGiro;
    private Font f;
    private String um;

    public JGrafico() {
        picco = 0;
        posizionePicco = 0;
        primoGiro = false;
        this.stato = "0";
    }

    public void setCurva(String curva) {
        this.curva = curva;
    }

    public void setCurvaDiRiferimento(String curvaDiRiferimento) {
        this.curvaDiRiferimento = curvaDiRiferimento;
    }

    public void setInPrimoPiano(boolean inPrimoPiano) {
        this.inPrimoPiano = inPrimoPiano;
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g); // Generated from nbfs://nbhost/SystemFileSystem/Templates/Classes/Code/OverriddenMethodBody

        if (inPrimoPiano) {
            Graphics2D gr = (Graphics2D) g;
            if (this.curva == null) {
                this.curva = "";
/*
                this.curva = "20,10,20,10,10,30,10,30,30,20,30,30,10,30,20,10,20,"
                    + "0,30,20,10,20,30,20,40,50,70,120,150,240,310,390,480,550,"
                    + "640,690,720,730,710,750,770,770,790,780,760,770,740,710,"
                    + "680,650,650,625,585,575,555,550,550,550,525,520,500,490,"
                    + "490,480,500,490,550,630,730,830,930,1000,1050,1120,1190,"
                    + "1270,1350,1350,1120,910,690,530,340,210,"
                    + "0,0,0,0,0,0,0,0,0,0,0,0,0,0,0";
*/
            }
            /*
            if (this.curvaDiRiferimento == null) {
                this.curvaDiRiferimento = "10,30,10,30,30,20,30,30,10,30,20,10,20,"
                        + "0,30,20,10,20,30,20,40,50,70,120,150,240,310,390,480,550,"
                        + "640,690,720,730,710,750,770,770,790,780,760,770,740,710,"
                        + "680,650,650,625,585,575,555,550,550,550,525,520,500,490,"
                        + "490,480,500,490,550,630,730,830,930,1000,1050,1120,1190,"
                        + "1270,1350,1350,1120,910,690,530,340,210,"
                        + "0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0";
            }
             */
            int y = this.getHeight();
            gr.setColor(Color.BLACK);
            gr.drawLine(0, y - 6, this.getWidth(), y - 6);
            if (this.stato.equals(Static.STATO_CALIBRAZIONE)) {
                if (primoGiro) {
                    primoGiro = false;
                } else {
                    this.curvaDiRiferimento = this.curva;
                }
                this.curva = "";
            }
            if (this.curvaDiRiferimento != null) {
                gr.setColor(Color.GREEN);
                gr.setStroke(new BasicStroke(3));
                String[] yRifchar = curvaDiRiferimento.split(",");
                nPoints = yRifchar.length;
                if (nPoints > 1) {
                    xpoints = new int[nPoints];
                    ypoints = new int[nPoints];
                    for (int i = 0; i < nPoints; i++) {
                        xpoints[i] = i * 3;
                        ypoints[i] = y - 10 - Integer.parseInt(yRifchar[i]) / 7;
                    }
                    gr.drawPolyline(xpoints, ypoints, nPoints);
                    //this.Rm.getjLayeredPaneCenter().repaint();
                }
            }
            String[] ychar = curva.split(",");
            nPoints = ychar.length;
            if (nPoints > 1) {
                xpoints = new int[nPoints];
                ypoints = new int[nPoints];
                for (int i = 0; i < nPoints; i++) {
                    xpoints[i] = i * 3;
                    ypoints[i] = y - 10 - Integer.parseInt(ychar[i]) / 7;
                }
                gr.setStroke(new BasicStroke(3));
                if (this.isInError) {
                    gr.setColor(Color.RED);
                } else {
                    if (this.stato.equals(Static.STATO_CALIBRAZIONE)) {
                        gr.setColor(Color.GREEN);
                    } else {
                        gr.setColor(Color.BLACK);
                    }
                }
                gr.drawPolyline(xpoints, ypoints, nPoints);
                //this.Rm.getjLayeredPaneCenter().repaint();

                gr.setColor(Color.DARK_GRAY);
                f = new Font("Arial", 2, 20);
                gr.setFont(f);
                gr.drawString("Green:Ref", 5, 25);
                gr.setColor(Color.BLACK);
                if (um.equals("Bar")) {
                    gr.drawString("" + picco * 10 + "Bar " + ((float) posizionePicco / 100) + "s", 5, 50);
                } else {
                    gr.drawString("" + picco * 10 + "N " + ((float) posizionePicco / 100) + "s", 5, 50);
                }
            }
        }
    }

    public void setIsInError(boolean isInError) {
        this.isInError = isInError;
    }

    public void setStato(String stato) {
        this.stato = stato;
    }

    public void setPicco(int picco, int posizionePicco) {
        this.picco = picco;
        this.posizionePicco = posizionePicco;
    }

    public void setPrimoGiro(boolean primoGiro) {
        this.primoGiro = primoGiro;
    }

    void setUM(String um) {
        this.um = um;
    }

}
