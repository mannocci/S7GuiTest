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
    int[] xPoints, yPoints;
    private int piccoRif = 1;
    private int posizionePiccoRif;
    private int picco = 0;
    private int posizionePicco;
    private boolean primoGiro;
    private Font f;
    private JRivitMain Rm;
    private float fattoreX;
    private float fattoreY;
    private float maxLength;
    private int maxPicco;   // Picco più elevato tra le curve
    private int valPicco;   // Valore del picco nell'unità di misura scelta
    private Graphics2D gr;
    private String[] yRifchar;
    private String[] yCurvaChar;

    private final int jPanelPosX = 0;
    private final int jPanelPosY = 0;
    private final int jPanelWidth = 330;
    private final int jPanelHeight = 277;
    private final int bordoInf = 20;
    private final int bordoSup = 30;
    private final int bordoSx = 30;
    private float y0;           // Coordinata Y dell'origine del grafico
    private float altezzaGraf;  // Altezza dello spazio per il grafico
    private float larghezzaGraf;  // Larghezza dello spazio per il grafico
    private int[] xPointsRif;
    private int[] yPointsRif;

    public JGrafico(JRivitMain Rm) {
        picco = 0;
        this.Rm = Rm;
        posizionePicco = 0;
        primoGiro = false;
        this.setBounds(jPanelPosX, jPanelPosY, jPanelWidth, jPanelHeight);
        y0 = this.jPanelHeight - bordoInf;
        altezzaGraf = (y0 - bordoSup);
        larghezzaGraf = this.jPanelWidth - bordoSx;
//Aggiornata impostazione grandezza JPanel come gli altri
    }

    public void setCurva(String curva) {
        this.curva = curva;
        if (curva != null) {
            this.yCurvaChar = curva.split(",");
        }
    }

    public void setCurvaDiRiferimento(String curvaDiRiferimento) {
        this.curvaDiRiferimento = curvaDiRiferimento;
        if (curvaDiRiferimento != null) {
            this.yRifchar = curvaDiRiferimento.split(",");
        }
    }

    @Override
    protected void paintComponent(Graphics g) {
        boolean canDraw = true;
        Color myFantasma = new Color(63, 20, 94); // Color fantasma
        try {
            super.paintComponent(g); // Generated from nbfs://nbhost/SystemFileSystem/Templates/Classes/Code/OverriddenMethodBody

            if (this.Rm.getPanCur().equals("canvas")) {
                if (this.curva == null) {   // Per sicurezza
                    this.setCurva("");
                }
                if (this.curva.length() > 1 || this.curvaDiRiferimento.length() > 0) {
                    gr = (Graphics2D) g;

                    if (this.Rm.getStato().equals(Static.STATO_CALIBRAZIONE)) {
                        if (!(this.curvaDiRiferimento.length() > 1)) {
                            if (this.curva.length() > 1) {
                                this.setCurvaDiRiferimento(this.curva);   // La curva appena letta diventa il riferimento
                                this.piccoRif = this.picco;
                                this.posizionePiccoRif = this.posizionePicco;
//                                this.setCurva("");
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

                    if (this.curvaDiRiferimento != null
                            && !this.curvaDiRiferimento.equals("0")) {  // Se esiste la curva di riferimento
                        //disegnaAssi();
                        aggiornaAssi();
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
                        if (yCurvaChar.length > 1) {
                            xPoints = new int[yCurvaChar.length];
                            yPoints = new int[yCurvaChar.length];
                            for (int i = 0; i < yCurvaChar.length; i++) {
                                xPoints[i] = Math.round(i * fattoreX) + bordoSx;
                                yPoints[i] = Math.round(y0 - 2 - Integer.parseInt(yCurvaChar[i]) * fattoreY);
                            }
                            gr.setStroke(new BasicStroke(3));
                            if (this.Rm.getInErrore()) {
                                String[] errValues = this.Rm.getEsitoTiro().split(",");
                                String[] posErrValues = this.Rm.getPosizioneErrori().split(",");
                                int alpha = 127; // 50% transparent
                                Color myColour = new Color(200, 0, 0, alpha);
                                gr.setColor(myColour);
                                //gr.setXORMode(Color.GRAY);
                                int nPunti = (errValues.length - 4) / 5;    // es.: 0,0,0,0,15,0,10,0,0
                                int xP = posizionePicco, yP = 0;
                                int i = 0;
                                int indice = 0;
                                // Disegno delle zone errate. Il vettore delle posizioni contiene x e y intervallate
                                for (String errValue : posErrValues) {
                                    if (i % 2 == 0) {   // istanti dei test
                                        indice = Integer.parseInt(errValue);
                                        xP = Math.round(indice * fattoreX) + bordoSx - 10;
                                    } else {
                                        yP = Math.round(jPanelHeight - bordoInf - 5 - Integer.parseInt(yCurvaChar[indice]) * fattoreY - 10);
                                        gr.drawOval(xP, yP, 20, 20);
                                    }
                                    i++;
                                }
                                gr.setColor(Color.decode("0xdd0000"));  //  Rosso
                            } else {
                                gr.setColor(Color.decode("0x00dd00"));  // Verde
                                if (this.Rm.getStato().equals(Static.STATO_CALIBRAZIONE)) {
                                    gr.setColor(Color.BLUE);
                                } else {
                                    switch (this.Rm.getRispostaErrore()) {
                                        case "continua" ->
                                            gr.setColor(Color.decode("0xdd0000"));  //  Rosso
                                        case "accetta" ->
                                            gr.setColor(Color.decode("0x00dd00"));  // Verde, è già verde
                                        case "annulla" ->
                                            gr.setColor(myFantasma );   // "fantasma"
                                        //canDraw = false;
                                    }
                                }
                            }
                            if (canDraw) {
                                gr.drawPolyline(xPoints, yPoints, yCurvaChar.length);
                                scriviPicco(picco, posizionePicco);
                            }
                            //this.Rm.getjLayeredPaneCenter().repaint();
                        } else {
                            scriviPicco(piccoRif, posizionePiccoRif);
                        }
                        // Non disegno la curva di calibrazione se ne è stata avviata una nuova
                        if (!this.Rm.getStato().equals(Static.STATO_CALIBRAZIONE)) {
                            gr.setColor(Color.BLUE);
                            gr.setStroke(new BasicStroke(3));
                            if (yRifchar.length > 1) {
                                xPointsRif = new int[yRifchar.length];
                                yPointsRif = new int[yRifchar.length];
                                for (int i = 0; i < yRifchar.length; i++) {
                                    xPointsRif[i] = Math.round(i * fattoreX) + bordoSx;
                                    yPointsRif[i] = Math.round(y0 - 2 - (float) Integer.parseInt(yRifchar[i]) * fattoreY);
                                }
                                gr.drawPolyline(xPointsRif, yPointsRif, yRifchar.length);
                                // Se la curva è OK la ridisegno davanti al riferimento per coerenza con l'interfaccia WEB
                                if (this.Rm.getStato().equals(Static.STATO_AVVIATO) && !this.Rm.getInErrore()) {
                                    gr.setColor(Color.decode("0x00dd00"));  // Verde
                                    gr.drawPolyline(xPoints, yPoints, yCurvaChar.length);
                                }
                            }
                        }

                    } else {
                        disegnaAssi();
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

    private void disegnaAssi() {
        gr.setColor(Color.BLACK);
        gr.drawLine(bordoSx, this.jPanelHeight - bordoInf, this.jPanelWidth, this.jPanelHeight - bordoInf);   // Asse X
        gr.drawLine(bordoSx, this.jPanelHeight - bordoInf, bordoSx, bordoSup);   // Asse Y
    }

    private void aggiornaAssi() {
        gr.setColor(Color.BLACK);

//        yRifchar = curvaDiRiferimento.split(",");
//        yCurvaChar = curva.split(",");
        maxLength = Math.max(yRifchar.length, yCurvaChar.length);   // Calcolo il nr. di punti del grafico
        fattoreX = larghezzaGraf / maxLength;                           // Fattore X per le larghezze
        float distanzaX = (this.jPanelWidth - bordoSx) / 6; // Distanza tra le etichette sull'asse X
        int intervalloX = Math.round(maxLength / 6);       // Tempo tra le etichette
        float posY = (float) (y0 + bordoInf - 3);           // Posizione inferiore delle etichette
        for (int i = 0; i < 6; i++) {    // Scala dei tempi
            gr.drawString("     ", (distanzaX * i) + bordoSx - 2, posY);
        }
        for (int i = 0; i < 6; i++) {    // Scala dei tempi
            try {
                gr.drawString(Integer.toString(intervalloX * i), (distanzaX * i) + bordoSx - 2, posY);
            } catch (Exception e) {
                Static.debug("Errore Integer toString\n" + e.toString(), 2);
            }
        }

        // Scala delle pressioni
        maxPicco = Math.max(piccoRif, picco);   // Il maggiore dei picchi
        fattoreY = altezzaGraf / (float) (maxPicco * 1.2);  // fattore moltiplicativo Y del grafico (rispetto al picco + 20%)
        if (maxPicco < 0) {
            maxPicco = 0;
        }
        if (this.Rm.getUM().equals("Bar")) {
            valPicco = maxPicco / 10;
        } else {
            valPicco = maxPicco * this.Rm.getConversion() / 10000;
        }
        int valore;
        String strValore;
        float distanzaY = (float) altezzaGraf / 8;    // Distanza tra le etichette sull'asse Y
        int intervalloY = Math.round((float) (valPicco * 1.2) / 8);       // Incremento Pressione tra le etichette
        for (int i = 0; i < 8; i++) {           // Ce ne stanno 8 nel grafico
            gr.drawString("     ", 0, y0 - (i * distanzaY) + 2);
        }
        for (int i = 0; i < 8; i++) {           // Ce ne stanno 8 nel grafico
            valore = intervalloY * i;
            if (valore < 10) {   //  Aggiungo gli spazi per allineare a destra i numeri
                strValore = "     " + valore;
            } else if (valore < 100) {
                strValore = "   " + valore;
            } else if (valore > 999) {
                float tmpValore = (float) valore / 1000;
                if (tmpValore < 9.99f) {
                    strValore = String.format(" %.1fK", tmpValore);
                } else {
                    strValore = String.format("%.1fK", tmpValore);
                }
            } else {
                strValore = " " + valore;
            }
            gr.drawString(strValore, 0, y0 - (i * distanzaY) + 2);
        }
        disegnaAssi();
    }

    private void scriviPicco(int picco, int posizionePicco) {
        f = new Font("Arial", 1, 20);
        gr.setFont(f);
        Color oldColor = gr.getColor();
        gr.setColor(Color.BLACK);
        String piccoStr;
        if (picco < 0) {
            picco = 0;
        }
        if (this.Rm.getUM().equals("Bar")) {
            piccoStr = Math.round((float) picco / 10) + " Bar ";
        } else {
            piccoStr = Math.round((float) picco * this.Rm.getConversion() / 10000) + " N ";
        }
        gr.drawString(piccoStr + ((float) posizionePicco / 100) + "s", 5, 20);
        gr.setColor(oldColor);
    }

}
