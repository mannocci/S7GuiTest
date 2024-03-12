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
Per impostare il grafico lineare occorre fare riferimento alla curva di calibrazione
Questa curva ha un suo tempo di sviluppo nel tempo; asse X con un incipit e un epilogo 
che viene deciso in percentuale sul valore del tempo
Se in tempo è 1 secondo e 10 1010 millesimi avremo per esempio un 101 millesimi prima e dopo con la coda
L'asse X sarà così ideata sul tempo di 1010 + 202 = 1212 millesimi
il panel è largo 328 pixel meno lo spazio da dedicare alla descrizione dell'asse Y
Per semplificare poniamo di utilizzare 25 pixel per la descrizione quindi rimangano
da sfruttare 303 pixel
Quindi 1212/303 = 4 è il fattore di scala
Se per esempio il picco è accaduto al tempo 800 millesimi
la nostra x sarà 800/4 = 200° pixel 
Sulla Y il picco possiamo ipotizzare, per esempio, che sia 1531
L'asse Y è alto 276 pixel meno lo spazio per scrivere i valori della X che ipotizziamo
abbiano lo stesso spazio della X 25 pixel abbiamo da utilizzare 276-25=251 pixel
Quindi 1531/251= 6.09 che arrotondiamo a 6.1 come  fattore di scala 
il pixel del picco sarà 1531/6.1 = 250.98
il pixel prima del picco 1453 sarà 238.19 
Quello dopo il picco 1518 sarà 248.85
la base di sx 86 sarà 14.09 pixel
Le variabili static necessarie sono:
1) la percentuale da considerare come spazio superiore al picco
2) lo spazio/pixel per la numerazione asse Y
3) lo spazio/pixel per la numerazione asse X 
Se si vuole centrare il grafico nel rimanente spazio occorre calcolare quanti pixel dedicare
alla parte iniziale del grafico e alla coda
spazioNrY
spazioNrX
spazioInizioX
spazioFineX
spazioSopraPicco
inizioCurva
fineCurva
spazioCurvaSuX = fineCurva-inizioCurva
spazioGraficoX = larghezzaPanel-spazioNrY
spazioInizioX, spazioFineX = (spazioGraficoX-spazioCurvaSuX)/2
spazioGraficoY = altezzaPanel-spazioNrY-spazioSopraPicco
Il ciclo per disegnare la curva si muoverà con le seguenti variabili
contaPixelX = 0; contaPixelX <= spazioGraficoX; contaPixelX++
    x = spazioInizioX+contaPixelX
    y = curva[contaPixelX]
    disegnaY(x,y,x,y)//Punto per punto
    
    
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
    int nPointsRifChar;
    int[] xPoints, yPoints;
    private int picco = 0;
    private int posizionePicco;
    private boolean primoGiro;
    private Font f;
    private String um;
    private JRivitMain Rm;
    private float passoX;
    private int nPointsCurvaChar;
    private float nPointsMax;
    private int scalaY;
    private int piccoRif = 1;
    private int posizionePiccoRif;
    private int yMax;
    private Graphics2D gr;
    private String[] yRifchar;
    private String[] yCurvaChar;

    private final int bordoInf = 20;
    private final int bordoSup = 50;
    private final int bordoSx = 30;

    private final int jPanelPosX = 1;
    private final int jPanelPosY = 1;
    private final int jPanelWidth = 330;
    private final int jPanelHeight = 277;
    private int y0;

    public JGrafico(JRivitMain Rm) {
        picco = 0;
        this.Rm = Rm;
        posizionePicco = 0;
        primoGiro = false;
        this.um = "Bar";
        this.setBounds(jPanelPosX, jPanelPosY, jPanelWidth, jPanelHeight);
        y0 = this.getHeight() - bordoInf;
//Aggiornata impostazione grandezza JPanel come gli altri
    }

    public void setCurva(String curva) {
        this.curva = curva;
    }

    public void setCurvaDiRiferimento(String curvaDiRiferimento) {
        this.curvaDiRiferimento = curvaDiRiferimento;
    }

    @Override
    protected void paintComponent(Graphics g) {
        try {

            super.paintComponent(g); // Generated from nbfs://nbhost/SystemFileSystem/Templates/Classes/Code/OverriddenMethodBody

            if (this.Rm.getPanCur().equals("canvas")) {
                if (this.curva == null) {
                    this.curva = "";
                }
                if (this.curva.length() > 1 || this.curvaDiRiferimento.length() > 1) {
                    gr = (Graphics2D) g;

                    if (this.Rm.getStato().equals(Static.STATO_CALIBRAZIONE)) {
                        if (!(this.curvaDiRiferimento.length() > 1)) {
                            if (this.curva.length() > 1) {
                                this.curvaDiRiferimento = this.curva;
                                this.piccoRif = this.picco;
                                this.posizionePiccoRif = this.posizionePicco;
                                this.curva = "";
                            }
                        }
                    }

                    // richiesta di test della nuova calibrazione
                    if (this.Rm.getStato().equals(Static.STATO_CALIBRAZIONE_TEST)) {
//                    if (primoGiro) {
//                        primoGiro = false;
////                    this.curvaDiRiferimento = this.curva;
//                        this.curva = "";
//                    }
                    }

                    if (this.curvaDiRiferimento != null && !this.curvaDiRiferimento.equals("0")) {  // Se esiste la curva di riferimento
                        disegnaAssi();
                        gr.setColor(Color.GREEN);
                        gr.setStroke(new BasicStroke(3));
                        if (nPointsRifChar > 1) {
                            xPoints = new int[nPointsRifChar];
                            yPoints = new int[nPointsRifChar];
                            for (int i = 0; i < nPointsRifChar; i++) {
                                xPoints[i] = Math.round(i * passoX) + bordoSx;
                                yPoints[i] = y0 - 2 - Integer.parseInt(yRifchar[i]) / scalaY;
                            }
                            gr.drawPolyline(xPoints, yPoints, nPointsRifChar);

                            //this.Rm.getjLayeredPaneCenter().repaint();
                        }

                        if (this.Rm.getStato().equals(Static.STATO_CALIBRAZIONE)) {
                            f = new Font("Arial", 2, 20);
                            gr.setFont(f);
                            gr.setColor(Color.BLACK);
                            gr.drawString(this.Rm.getLavoroScelto(), 200, 20);
                            gr.setColor(Color.ORANGE);
                            //gr.drawString("Calib.mode", 200, 43);
                        } else {
                            if (this.Rm.getStato().equals(Static.STATO_CALIBRAZIONE_TEST)) {
                                f = new Font("Arial", 2, 20);
                                gr.setFont(f);
                                gr.setColor(Color.BLACK);
                                gr.drawString(this.Rm.getLavoroScelto(), 200, 20);
                                gr.setColor(Color.BLUE);
                                // gr.drawString("Calib.test", 200, 43);
                            }
                        }
                        if (nPointsCurvaChar > 1) {
                            xPoints = new int[nPointsCurvaChar];
                            yPoints = new int[nPointsCurvaChar];
                            for (int i = 0; i < nPointsCurvaChar; i++) {
                                xPoints[i] = Math.round(i * passoX) + bordoSx;
                                yPoints[i] = y0 - 2 - Integer.parseInt(yCurvaChar[i]) / scalaY;
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
                                        yP = jPanelHeight - bordoInf - 5 - Integer.parseInt(yCurvaChar[indice]) / scalaY - 10;
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
                            gr.drawPolyline(xPoints, yPoints, nPointsCurvaChar);
                            //this.Rm.getjLayeredPaneCenter().repaint();

                            scriviPicco(picco, posizionePicco);
                        } else {
                            scriviPicco(piccoRif, posizionePiccoRif);
                        }
                    }
                }
            }
        } catch (Exception e) {
            Static.debug("Error Graphic repaint\npiccoRif: " + piccoRif + " picco: " + picco + "\n" + e.toString(), 2);
        }
    }

    /**
     * Imposta i valori del picco e di posizione picco per il dimensionamento
     * del grafico
     *
     * @param picco
     * @param posizionePicco
     */
    public void setPicco(int picco, int posizionePicco) {
        this.picco = picco;
        this.posizionePicco = posizionePicco;
    }

    /**
     * Imposta i valori del picco e di posizione picco di riferimento per il
     * dimensionamento del grafico
     *
     * @param piccoRif
     * @param posizionePiccoRif
     */
    public void setPiccoRif(int piccoRif, int posizionePiccoRif) {
        this.piccoRif = piccoRif;
        this.posizionePiccoRif = posizionePiccoRif;
    }

    public void setPrimoGiro(boolean primoGiro) {
        this.primoGiro = primoGiro;
    }

    void setUM(String um) {
        this.um = um;
    }

    private void disegnaAssi() {
        gr.setColor(Color.BLACK);
        gr.drawLine(bordoSx, this.jPanelHeight - bordoInf, this.jPanelWidth, this.jPanelHeight - bordoInf);   // Asse X
        gr.drawLine(bordoSx, this.jPanelHeight - bordoInf, bordoSx, bordoSup - 20);   // Asse Y
        yRifchar = curvaDiRiferimento.split(",");
        nPointsRifChar = yRifchar.length;

        yCurvaChar = curva.split(",");
        nPointsCurvaChar = yCurvaChar.length;
        nPointsMax = Math.max(nPointsRifChar, nPointsCurvaChar);  // Calcolo il nr. di punti del grafico
        passoX = this.jPanelWidth / nPointsMax;              // Distanza tra due punti sull'asse X
        int intervalloX = Math.round(nPointsMax / 100);
        for (int i = 0; i < nPointsMax; i += 15 * intervalloX) {    // Scala dei tempi
            try {
                gr.drawString(Integer.toString(i), (passoX * i) + bordoSx, (float) (y0 + bordoInf - 3));
            } catch (Exception e) {
                Static.debug("Errore Integer toString\n" + e.toString(), 2);
            }
        }

        // Scala delle pressioni
        yMax = Math.max(piccoRif, picco);   // Calcolo l'altezza max del grafico
        scalaY = Math.round(yMax / (y0 - bordoSup));        // Proporzione del grafico
        if (scalaY == 0) {
            scalaY = 1;
        }
        if (um.equals("Bar")) {
            yMax = yMax / 10;
        } else {
            yMax = yMax * this.Rm.getConversion() / 10000;
        }
        int valore;
        String strValore;
        float passoY = (float) (y0 / 8);    // Otto suddivisioni
        for (int i = 0; i < 8; i++) {       // Ce ne stanno 8 nel grafico
            valore = (yMax / 8) * i;
            if (valore < 10) {   //  Aggiungo gli spazi per allineare a destra i numeri
                strValore = "    " + valore;
            } else if (valore < 100) {
                strValore = "  " + valore;
            } else if (valore > 999) {
                int tmpValore = Math.round(valore / 1000);
                if (tmpValore < 10) {
                    strValore = "  " + tmpValore + "K";
                } else {
                    strValore = "" + tmpValore + "K";
                }
            } else {
                strValore = "" + valore;
            }

            gr.drawString(strValore, 1f, y0 - (i * passoY) + 10);
        }
    }

    private void scriviPicco(int picco, int posizionePicco) {
        f = new Font("Arial", 1, 20);
        gr.setFont(f);
        gr.setColor(Color.BLACK);
        if (um.equals("Bar")) {
            gr.drawString("" + picco / 10 + " Bar " + ((float) posizionePicco / 100) + "s", 5, 20);
        } else {
            gr.drawString("" + picco * this.Rm.getConversion() / 10000 + " N " + ((float) posizionePicco / 100) + "s", 5, 20);
        }
    }

}
