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
 * ci sono due field rererenti alla curva
    1) Di riferimento
    2) Tiro appena fatto
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
    private String curvaDiRiferimento;
    int nPointsRif;
    int[] xPoints, yPoints;
    private int picco;
    private int posizionePicco;
    private boolean primoGiro;
    private Font f;
    private String um;
    private JRivitMain Rm;
    private float passoX;
    private int nPointsCurva;
    private float nPointsMax;
    private int scalaY;
    private int piccoRif;
    private int altezza;
    private int yMax;
    private Graphics2D gr;
    private String[] yRifchar;
    private String[] ychar;
    private int bordoInf;
    private int bordoSup;
    private int bordoSx;

    public JGrafico(JRivitMain Rm) {
        picco = 0;
        this.Rm = Rm;
        posizionePicco = 0;
        primoGiro = false;
    }

    public void setCurva(String curva) {
        this.curva = curva;
    }

    public void setCurvaDiRiferimento(String curvaDiRiferimento) {
        this.curvaDiRiferimento = curvaDiRiferimento;
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g); // Generated from nbfs://nbhost/SystemFileSystem/Templates/Classes/Code/OverriddenMethodBody

        if (this.Rm.getPanCur().equals("canvas")) {
            gr = (Graphics2D) g;
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
            // richiesta di test della nuova calibrazione
            if (this.Rm.getStato().equals(Static.STATO_CALIBRAZIONE_TEST)) {
                if (primoGiro) {
                    primoGiro = false;
//                    this.curvaDiRiferimento = this.curva;
                    this.curva = "";
                }
            }
            bordoSx = 30;
            bordoInf = 20;
            bordoSup = 50;
            altezza = this.getHeight();
            disegnaAssi();
            if (this.curvaDiRiferimento != null) {  // Se esiste la curva di riferimento
                gr.setColor(Color.GREEN);
                gr.setStroke(new BasicStroke(3));
                if (nPointsRif > 1) {
                    xPoints = new int[nPointsRif];
                    yPoints = new int[nPointsRif];
                    for (int i = 0; i < nPointsRif; i++) {
                        xPoints[i] = Math.round(i * passoX) + bordoSx;
                        yPoints[i] = altezza - bordoInf - 5 - Integer.parseInt(yRifchar[i]) / scalaY;
                    }
                    gr.drawPolyline(xPoints, yPoints, nPointsRif);
                    //this.Rm.getjLayeredPaneCenter().repaint();
                }
            }
            if (this.Rm.getStato().equals(Static.STATO_CALIBRAZIONE)) {
                f = new Font("Arial", 2, 20);
                gr.setFont(f);
                gr.setColor(Color.BLACK);
                gr.drawString(this.Rm.getLavoroScelto(), 200, 25);
                gr.setColor(Color.ORANGE);
                gr.drawString("Calib.mode", 200, 43);
            } else {
                if (this.Rm.getStato().equals(Static.STATO_CALIBRAZIONE_TEST)) {
                    f = new Font("Arial", 2, 20);
                    gr.setFont(f);
                    gr.setColor(Color.BLACK);
                    gr.drawString(this.Rm.getLavoroScelto(), 200, 25);
                    gr.setColor(Color.BLUE);
                    gr.drawString("Calib.test", 200, 43);
                }
            }
            if (nPointsCurva > 1) {
                xPoints = new int[nPointsCurva];
                yPoints = new int[nPointsCurva];
                for (int i = 0; i < nPointsCurva; i++) {
                    xPoints[i] = Math.round(i * passoX) + bordoSx;
                    yPoints[i] = altezza - bordoInf - 5 - Integer.parseInt(ychar[i]) / scalaY;
                }
                gr.setStroke(new BasicStroke(3));
                if (this.Rm.getInErrore()) {
                    String[] errValues = this.Rm.getEsitoTiro().split(",");
                    String[] posErrValues = this.Rm.getPosizioneErrori().split(",");
                    int alpha = 127; // 50% transparent
                    Color myColour = new Color(0, 0, 200, alpha);
                    gr.setColor(myColour);
                    //gr.setXORMode(Color.GRAY);
                    int nPunti = (errValues.length - 4) / 5;    // es.: 0,0,0,0,15,0,10,0,0
                    int xP = posizionePicco, yP = 0;
                    int i = 0;
                    int indice = 0;
                    // Disegno delle zone errate. Il vettore delleposizioni contiene x e y intervallate
                    for (String errValue : posErrValues) {
                        if (i % 2 == 0) {   // istanti dei test
                            indice = Integer.parseInt(errValue);
                            xP = Math.round(indice * passoX) + bordoSx - 10;
                        } else {
                            yP = altezza - bordoInf - 5 - Integer.parseInt(ychar[indice]) / scalaY - 10;
                            gr.drawOval(xP, yP, 20, 20);
                        }
                        i++;
                    }
                    gr.setColor(Color.RED);
                } else {
                    if (this.Rm.getStato().equals(Static.STATO_CALIBRAZIONE)) {
                        gr.setColor(Color.ORANGE);
                    } else {
                        gr.setColor(Color.BLACK);
                    }
                }
                gr.drawPolyline(xPoints, yPoints, nPointsCurva);
                //this.Rm.getjLayeredPaneCenter().repaint();

                /*
                gr.setColor(Color.GREEN);
                gr.drawString("Green:Ref", 5, 25);
                 */
                f = new Font("Arial", 1, 20);
                gr.setFont(f);
                gr.setColor(Color.BLACK);
                if (um.equals("Bar")) {
                    gr.drawString("" + picco / 10 + " Bar " + ((float) posizionePicco / 100) + "s", 5, 25);
                } else {
                    gr.drawString("" + picco * 10 + " N " + ((float) posizionePicco / 100) + "s", 5, 25);
                }
            }
        }
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

    private int trovaPicco(String[] arr) {
        int n, max = 0;
        for (String string : arr) {
            n = Integer.parseInt(string);
            if (n > max) {
                max = n;
            }
        }
        return max;
    }

    private void disegnaAssi() {
            gr.setColor(Color.BLACK);
            gr.drawLine(bordoSx, altezza - bordoInf, this.getWidth(), altezza - bordoInf);   // Asse X
            gr.drawLine(bordoSx, altezza - bordoInf, bordoSx, bordoSup);   // Asse Y
            yRifchar = curvaDiRiferimento.split(",");
            nPointsRif = yRifchar.length;
            ychar = curva.split(",");
            nPointsCurva = ychar.length;
            nPointsMax = Math.max(nPointsRif, nPointsCurva);  // Calcolo il nr. di punti del grafico
            passoX = 320 / nPointsMax;              // Distanza tra due punti sull'asse X
            piccoRif = trovaPicco(yRifchar);
            yMax = Math.max(piccoRif, picco);   // Calcolo l'altezza max del grafico
            scalaY = Math.round(yMax / 180);        // Proporzione del grafico
            int intervalloX = Math.round(nPointsMax / 100);
            for (int i = 0; i < nPointsMax - bordoSx; i += 15 * intervalloX) {    // Scala dei tempi
                gr.drawString(Integer.toString(i), (passoX * i) + bordoSx, (float) (this.getHeight() - bordoInf / 2));
            }
            int intervalloY = Math.round(yMax / 1000);
            String valore;
            for (int i = 0; i <= yMax / 10 + bordoSup; i += 35 * intervalloY) {    // Scala delle pressioni
                if (i < 10) {   //  Aggiungo gli spazi per allineare a destra i numeri
                    valore = "    " + i;
                } else if (i < 100) {
                    valore = "  " + i;
                } else {
                    valore = Integer.toString(i);
                }
                gr.drawString(valore, 5, (float) (this.getHeight() - bordoInf) - (i));
            }
    }

}
