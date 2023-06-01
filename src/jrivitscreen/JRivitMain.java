/*
 * Copyright (C) 2023 Luca Mannocci & Fabio Fragapane
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
 * La documentazionedel progetto del RivitScreen si trova si GitHub 
 * https://github.com/rivit-elrenoin 
 * repository Privato, visibile da personale della Rivit, oltre a Luca Mannocci,
 * Fabio Fragapane, Mannocci Enrico
 * @versione 1.0 maggio/giugno 2023
 */
package jrivitscreen;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Component;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.event.ItemEvent;
import java.io.File;
import java.io.FileNotFoundException;
import java.util.List;
import java.util.Scanner;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.ImageIcon;
import javax.swing.JLabel;

/**
 *
 * @author Luca Mannocci & Fabio Fragapane
 */
public class JRivitMain extends javax.swing.JFrame {

    private ImageIcon Img_Exit, Img_Ok, Img_Nulla, Img_Freccia_su,
            Img_Freccia_giu, Img_Warning, Img_Setup, Img_Play;
    private Worker w_mf;
    private String FocusPanelName;
    private ImageIcon Img_Continua;
    private ImageIcon Img_Estende;
    private ImageIcon Img_Stop;
    private ImageIcon Img_Pause;
    private ImageIcon Img_Annulla;
    private ImageIcon Img_Lan;
    private ImageIcon Img_WiFi;
    private ImageIcon Img_Freccia_sx;
    private ImageIcon Img_Freccia_dx;
    private ImageIcon Img_Cancel;
    private String AlertDialogAnnulla;
    private String AlertDialogStop;
    private String fileNomeDevice;
    private String Lavorodescrizione;
    private ImageIcon Img_Info;
    private int nr_lotti_da_fare;
    private int nr_tiri_da_fare;
    private int nr_lotti_fatti;
    private int nr_tiri_fatti;
    private Float sogliaMin = 3.0f;
    private Float sogliaMax = 5.0f;
    private Float pressioneIn;
    private int nr_tiri;
    private String sessione;
    private String Curva;
    private int DialogQ = 100;
    static final int Continua = 1, Accetta = 2, Estende = 3, Annulla = 4;
    private int Stop = 0, Pausa = -1, DialogA = 200, Yes = 1000, No = 2000;

//Dopo una sospensione
    /**
     * Creates new form JRivitMain
     */
    public JRivitMain() {
        initComponents();
        this.canvasGraph.setBackground(Color.yellow);
        //this.d.setVisible(false);
        this.FocusPanelName = "main";
        this.fileNomeDevice = "nome_device.txt";
        this.AlertDialogStop = "Annullare Tiro ?";
        Img_Exit = new javax.swing.ImageIcon(getClass().getResource("/jrivitscreen/images/exit.png"));
        Img_Ok = new javax.swing.ImageIcon(getClass().getResource("/jrivitscreen/images/ok.png"));
        Img_Nulla = new javax.swing.ImageIcon(getClass().getResource("/jrivitscreen/images/nulla.png"));
        Img_Freccia_su = new javax.swing.ImageIcon(getClass().getResource("/jrivitscreen/images/freccia_su.png"));
        Img_Freccia_giu = new javax.swing.ImageIcon(getClass().getResource("/jrivitscreen/images/freccia_giu.png"));
        Img_Warning = new javax.swing.ImageIcon(getClass().getResource("/jrivitscreen/images/warning.png"));
        Img_Setup = new javax.swing.ImageIcon(getClass().getResource("/jrivitscreen/images/setup.png"));
        Img_Play = new javax.swing.ImageIcon(getClass().getResource("/jrivitscreen/images/play_circle.png"));
        Img_Continua = new javax.swing.ImageIcon(getClass().getResource("/jrivitscreen/images/play_pause.png"));
        Img_Estende = new javax.swing.ImageIcon(getClass().getResource("/jrivitscreen/images/sync_alt.png"));
        Img_Stop = new javax.swing.ImageIcon(getClass().getResource("/jrivitscreen/images/stop_circle.png"));
        Img_Pause = new javax.swing.ImageIcon(getClass().getResource("/jrivitscreen/images/pause.png"));
        Img_Annulla = new javax.swing.ImageIcon(getClass().getResource("/jrivitscreen/images/cancel.png"));
        Img_Cancel = new javax.swing.ImageIcon(getClass().getResource("/jrivitscreen/images/cancel.png"));
        Img_Lan = new javax.swing.ImageIcon(getClass().getResource("/jrivitscreen/images/lan.png"));
        Img_WiFi = new javax.swing.ImageIcon(getClass().getResource("/jrivitscreen/images/cell.png"));
        Img_Info = new javax.swing.ImageIcon(getClass().getResource("/jrivitscreen/images/info.png"));

        w_mf = new Worker(this);
        w_mf.set_operation("start");
        try {
            w_mf.doInBackground();
        } catch (Exception ex) {
            Logger.getLogger(JRivitMain.class.getName()).log(Level.SEVERE, null, ex);
        }
        this.PanelMain();
    }

    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        jPanelLeft = new javax.swing.JPanel();
        jButtonPL3 = new javax.swing.JButton();
        jButtonPL2 = new javax.swing.JButton();
        jButtonPL1 = new javax.swing.JButton();
        jLayeredPaneCenter = new javax.swing.JLayeredPane();
        jPanelStarted = new javax.swing.JPanel();
        jLabelContatore = new javax.swing.JLabel();
        jLabelNomeDevice = new javax.swing.JLabel();
        jLabelNomeLavoro = new javax.swing.JLabel();
        jProgressBar = new javax.swing.JProgressBar();
        jLabelErrati = new javax.swing.JLabel();
        jLabelAnnullati = new javax.swing.JLabel();
        jLabel_Errati = new javax.swing.JLabel();
        jLabel_Annullati = new javax.swing.JLabel();
        jLabel_Validi = new javax.swing.JLabel();
        jLabelValidi = new javax.swing.JLabel();
        jPanelSetup = new javax.swing.JPanel();
        jPanelMain = new javax.swing.JPanel();
        jLabelLogo = new javax.swing.JLabel();
        jPanelSetupLan = new javax.swing.JPanel();
        listSetupLan = new java.awt.List();
        jPanelSetupWiFi = new javax.swing.JPanel();
        listSetupWiFi = new java.awt.List();
        jPanelInfo = new javax.swing.JPanel();
        listInfo = new java.awt.List();
        jPanelWarning = new javax.swing.JPanel();
        listWarning = new java.awt.List();
        jPanelStart = new javax.swing.JPanel();
        listLavori = new java.awt.List();
        JTextAreaDescrizioneLavoro = new javax.swing.JTextArea();
        jPanelDialog = new javax.swing.JPanel();
        jLabelDialog = new javax.swing.JLabel();
        jPanelCanvas = new javax.swing.JPanel();
        canvasGraph = new java.awt.Canvas();
        jPanelRight = new javax.swing.JPanel();
        jButtonPR3 = new javax.swing.JButton();
        jButtonPR1 = new javax.swing.JButton();
        jButtonPR2 = new javax.swing.JButton();
        jPanelBotton = new javax.swing.JPanel();
        jLabel_B_L = new javax.swing.JLabel();
        jLabel_B_C = new javax.swing.JLabel();
        jLabel_B_R = new javax.swing.JLabel();
        jLabel_msg = new javax.swing.JLabel();

        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);
        setMaximumSize(new java.awt.Dimension(480, 320));
        setMinimumSize(new java.awt.Dimension(480, 320));
        setName("frameMain"); // NOI18N
        setSize(new java.awt.Dimension(480, 320));
        getContentPane().setLayout(null);

        jPanelLeft.setBorder(new javax.swing.border.LineBorder(new java.awt.Color(0, 0, 0), 1, true));

        jButtonPL3.setIcon(new javax.swing.ImageIcon(getClass().getResource("/jrivitscreen/images/setup.png"))); // NOI18N
        jButtonPL3.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButtonPL3ActionPerformed(evt);
            }
        });

        jButtonPL2.setIcon(new javax.swing.ImageIcon(getClass().getResource("/jrivitscreen/images/info.png"))); // NOI18N
        jButtonPL2.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButtonPL2ActionPerformed(evt);
            }
        });

        jButtonPL1.setIcon(new javax.swing.ImageIcon(getClass().getResource("/jrivitscreen/images/warning.png"))); // NOI18N
        jButtonPL1.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButtonPL1ActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout jPanelLeftLayout = new javax.swing.GroupLayout(jPanelLeft);
        jPanelLeft.setLayout(jPanelLeftLayout);
        jPanelLeftLayout.setHorizontalGroup(
            jPanelLeftLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanelLeftLayout.createSequentialGroup()
                .addGap(0, 3, Short.MAX_VALUE)
                .addGroup(jPanelLeftLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addComponent(jButtonPL1, javax.swing.GroupLayout.PREFERRED_SIZE, 67, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jButtonPL3, javax.swing.GroupLayout.PREFERRED_SIZE, 67, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jButtonPL2, javax.swing.GroupLayout.PREFERRED_SIZE, 67, javax.swing.GroupLayout.PREFERRED_SIZE)))
        );
        jPanelLeftLayout.setVerticalGroup(
            jPanelLeftLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanelLeftLayout.createSequentialGroup()
                .addGap(15, 15, 15)
                .addComponent(jButtonPL1, javax.swing.GroupLayout.PREFERRED_SIZE, 67, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jButtonPL2, javax.swing.GroupLayout.PREFERRED_SIZE, 67, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jButtonPL3, javax.swing.GroupLayout.PREFERRED_SIZE, 67, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(13, Short.MAX_VALUE))
        );

        getContentPane().add(jPanelLeft);
        jPanelLeft.setBounds(0, 0, 72, 243);

        jLayeredPaneCenter.setBorder(new javax.swing.border.LineBorder(new java.awt.Color(0, 0, 0), 1, true));
        jLayeredPaneCenter.setOpaque(true);
        jLayeredPaneCenter.setLayout(new org.netbeans.lib.awtextra.AbsoluteLayout());

        jPanelStarted.setBackground(java.awt.Color.white);
        jPanelStarted.setMaximumSize(new java.awt.Dimension(245, 234));
        jPanelStarted.setMinimumSize(new java.awt.Dimension(245, 234));
        jPanelStarted.setName("started"); // NOI18N
        jPanelStarted.setLayout(null);

        jLabelContatore.setFont(new java.awt.Font("SansSerif", 0, 28)); // NOI18N
        jLabelContatore.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jLabelContatore.setText("0/0");
        jPanelStarted.add(jLabelContatore);
        jLabelContatore.setBounds(20, 86, 210, 20);

        jLabelNomeDevice.setFont(new java.awt.Font("Times New Roman", 0, 18)); // NOI18N
        jLabelNomeDevice.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jLabelNomeDevice.setText("Nome Device");
        jPanelStarted.add(jLabelNomeDevice);
        jLabelNomeDevice.setBounds(20, 10, 210, 22);

        jLabelNomeLavoro.setFont(new java.awt.Font("Cantarell", 0, 20)); // NOI18N
        jLabelNomeLavoro.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jLabelNomeLavoro.setText("Nome Lavoro");
        jPanelStarted.add(jLabelNomeLavoro);
        jLabelNomeLavoro.setBounds(20, 40, 210, 30);

        jProgressBar.setBorder(javax.swing.BorderFactory.createEtchedBorder());
        jProgressBar.setStringPainted(true);
        jPanelStarted.add(jProgressBar);
        jProgressBar.setBounds(0, 180, 250, 40);

        jLabelErrati.setBackground(java.awt.Color.red);
        jLabelErrati.setFont(new java.awt.Font("SansSerif", 0, 24)); // NOI18N
        jLabelErrati.setForeground(new java.awt.Color(255, 255, 255));
        jLabelErrati.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jLabelErrati.setText("0");
        jLabelErrati.setBorder(new javax.swing.border.SoftBevelBorder(javax.swing.border.BevelBorder.RAISED));
        jLabelErrati.setOpaque(true);
        jPanelStarted.add(jLabelErrati);
        jLabelErrati.setBounds(160, 120, 60, 25);

        jLabelAnnullati.setBackground(new java.awt.Color(204, 204, 204));
        jLabelAnnullati.setFont(new java.awt.Font("SansSerif", 0, 24)); // NOI18N
        jLabelAnnullati.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jLabelAnnullati.setText("0");
        jLabelAnnullati.setBorder(new javax.swing.border.SoftBevelBorder(javax.swing.border.BevelBorder.RAISED));
        jLabelAnnullati.setOpaque(true);
        jPanelStarted.add(jLabelAnnullati);
        jLabelAnnullati.setBounds(90, 120, 60, 25);

        jLabel_Errati.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jLabel_Errati.setText("Errati");
        jPanelStarted.add(jLabel_Errati);
        jLabel_Errati.setBounds(170, 150, 37, 22);

        jLabel_Annullati.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jLabel_Annullati.setText("Annullati");
        jPanelStarted.add(jLabel_Annullati);
        jLabel_Annullati.setBounds(95, 150, 63, 22);

        jLabel_Validi.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jLabel_Validi.setText("Validi");
        jPanelStarted.add(jLabel_Validi);
        jLabel_Validi.setBounds(30, 150, 40, 22);

        jLabelValidi.setBackground(java.awt.Color.green);
        jLabelValidi.setFont(new java.awt.Font("SansSerif", 0, 24)); // NOI18N
        jLabelValidi.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jLabelValidi.setText("0");
        jLabelValidi.setBorder(new javax.swing.border.SoftBevelBorder(javax.swing.border.BevelBorder.RAISED));
        jLabelValidi.setOpaque(true);
        jPanelStarted.add(jLabelValidi);
        jLabelValidi.setBounds(20, 120, 60, 25);

        jLayeredPaneCenter.add(jPanelStarted, new org.netbeans.lib.awtextra.AbsoluteConstraints(7, 7, 245, 234));

        jPanelSetup.setMaximumSize(new java.awt.Dimension(252, 237));
        jPanelSetup.setMinimumSize(new java.awt.Dimension(245, 234));
        jPanelSetup.setName("setup"); // NOI18N
        jPanelSetup.setLayout(null);
        jLayeredPaneCenter.add(jPanelSetup, new org.netbeans.lib.awtextra.AbsoluteConstraints(7, 2, 245, 234));

        jPanelMain.setMaximumSize(new java.awt.Dimension(252, 237));
        jPanelMain.setMinimumSize(new java.awt.Dimension(245, 234));
        jPanelMain.setName("main"); // NOI18N
        jPanelMain.setPreferredSize(new java.awt.Dimension(245, 234));
        jPanelMain.setLayout(null);

        jLabelLogo.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jLabelLogo.setIcon(new javax.swing.ImageIcon(getClass().getResource("/jrivitscreen/images/logorivit.png"))); // NOI18N
        jLabelLogo.setBorder(javax.swing.BorderFactory.createEtchedBorder());
        jPanelMain.add(jLabelLogo);
        jLabelLogo.setBounds(7, 6, 230, 220);

        jLayeredPaneCenter.add(jPanelMain, new org.netbeans.lib.awtextra.AbsoluteConstraints(7, 7, 245, 234));

        jPanelSetupLan.setMaximumSize(new java.awt.Dimension(252, 237));
        jPanelSetupLan.setMinimumSize(new java.awt.Dimension(245, 234));
        jPanelSetupLan.setName("setup lan"); // NOI18N
        jPanelSetupLan.setLayout(null);
        jPanelSetupLan.add(listSetupLan);
        listSetupLan.setBounds(30, 20, 260, 160);

        jLayeredPaneCenter.add(jPanelSetupLan, new org.netbeans.lib.awtextra.AbsoluteConstraints(7, 7, 245, 234));

        jPanelSetupWiFi.setMaximumSize(new java.awt.Dimension(245, 234));
        jPanelSetupWiFi.setMinimumSize(new java.awt.Dimension(245, 234));
        jPanelSetupWiFi.setName("setup wifi"); // NOI18N
        jPanelSetupWiFi.setLayout(null);
        jPanelSetupWiFi.add(listSetupWiFi);
        listSetupWiFi.setBounds(30, 20, 230, 130);

        jLayeredPaneCenter.add(jPanelSetupWiFi, new org.netbeans.lib.awtextra.AbsoluteConstraints(7, 7, 234, 234));

        jPanelInfo.setMaximumSize(new java.awt.Dimension(245, 234));
        jPanelInfo.setMinimumSize(new java.awt.Dimension(245, 234));
        jPanelInfo.setName("info"); // NOI18N
        jPanelInfo.setLayout(null);
        jPanelInfo.add(listInfo);
        listInfo.setBounds(20, 10, 240, 220);

        jLayeredPaneCenter.add(jPanelInfo, new org.netbeans.lib.awtextra.AbsoluteConstraints(7, 7, 245, 234));

        jPanelWarning.setMaximumSize(new java.awt.Dimension(252, 237));
        jPanelWarning.setMinimumSize(new java.awt.Dimension(245, 234));
        jPanelWarning.setName("Warning"); // NOI18N
        jPanelWarning.setLayout(new org.netbeans.lib.awtextra.AbsoluteLayout());
        jPanelWarning.add(listWarning, new org.netbeans.lib.awtextra.AbsoluteConstraints(0, 0, 0, 0));

        jLayeredPaneCenter.add(jPanelWarning, new org.netbeans.lib.awtextra.AbsoluteConstraints(7, 7, 245, 234));

        jPanelStart.setMaximumSize(new java.awt.Dimension(252, 237));
        jPanelStart.setMinimumSize(new java.awt.Dimension(245, 234));
        jPanelStart.setName("start"); // NOI18N
        jPanelStart.setLayout(null);
        jPanelStart.add(listLavori);
        listLavori.setBounds(10, 10, 220, 120);

        JTextAreaDescrizioneLavoro.setColumns(20);
        JTextAreaDescrizioneLavoro.setLineWrap(true);
        JTextAreaDescrizioneLavoro.setRows(5);
        jPanelStart.add(JTextAreaDescrizioneLavoro);
        JTextAreaDescrizioneLavoro.setBounds(10, 140, 220, 80);

        jLayeredPaneCenter.add(jPanelStart, new org.netbeans.lib.awtextra.AbsoluteConstraints(7, 7, 245, 234));

        jPanelDialog.setMaximumSize(new java.awt.Dimension(245, 234));
        jPanelDialog.setMinimumSize(new java.awt.Dimension(245, 234));
        jPanelDialog.setName("dialog"); // NOI18N
        jPanelDialog.setLayout(null);

        jLabelDialog.setFont(new java.awt.Font("Times New Roman", 1, 18)); // NOI18N
        jLabelDialog.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jLabelDialog.setText("Annullare il Tiro ?");
        jPanelDialog.add(jLabelDialog);
        jLabelDialog.setBounds(10, 60, 180, 22);

        jLayeredPaneCenter.add(jPanelDialog, new org.netbeans.lib.awtextra.AbsoluteConstraints(7, 7, 245, 234));

        jPanelCanvas.setEnabled(false);
        jPanelCanvas.setMaximumSize(new java.awt.Dimension(245, 234));
        jPanelCanvas.setMinimumSize(new java.awt.Dimension(245, 234));
        jPanelCanvas.setName("canvas"); // NOI18N
        jPanelCanvas.setPreferredSize(new java.awt.Dimension(245, 234));
        jPanelCanvas.setLayout(null);

        canvasGraph.setMaximumSize(new java.awt.Dimension(245, 234));
        canvasGraph.setMinimumSize(new java.awt.Dimension(245, 234));
        canvasGraph.setPreferredSize(new java.awt.Dimension(245, 234));
        jPanelCanvas.add(canvasGraph);
        canvasGraph.setBounds(7, 7, 245, 234);

        jLayeredPaneCenter.add(jPanelCanvas, new org.netbeans.lib.awtextra.AbsoluteConstraints(7, 7, -1, -1));

        getContentPane().add(jLayeredPaneCenter);
        jLayeredPaneCenter.setBounds(75, 0, 250, 243);

        jPanelRight.setBorder(new javax.swing.border.LineBorder(new java.awt.Color(0, 0, 0), 1, true));

        jButtonPR3.setIcon(new javax.swing.ImageIcon(getClass().getResource("/jrivitscreen/images/nulla.png"))); // NOI18N
        jButtonPR3.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButtonPR3ActionPerformed(evt);
            }
        });

        jButtonPR1.setIcon(new javax.swing.ImageIcon(getClass().getResource("/jrivitscreen/images/play_circle.png"))); // NOI18N
        jButtonPR1.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButtonPR1ActionPerformed(evt);
            }
        });

        jButtonPR2.setIcon(new javax.swing.ImageIcon(getClass().getResource("/jrivitscreen/images/nulla.png"))); // NOI18N
        jButtonPR2.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButtonPR2ActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout jPanelRightLayout = new javax.swing.GroupLayout(jPanelRight);
        jPanelRight.setLayout(jPanelRightLayout);
        jPanelRightLayout.setHorizontalGroup(
            jPanelRightLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanelRightLayout.createSequentialGroup()
                .addGap(0, 3, Short.MAX_VALUE)
                .addGroup(jPanelRightLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addComponent(jButtonPR1, javax.swing.GroupLayout.PREFERRED_SIZE, 67, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jButtonPR3, javax.swing.GroupLayout.PREFERRED_SIZE, 67, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jButtonPR2, javax.swing.GroupLayout.PREFERRED_SIZE, 67, javax.swing.GroupLayout.PREFERRED_SIZE)))
        );
        jPanelRightLayout.setVerticalGroup(
            jPanelRightLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanelRightLayout.createSequentialGroup()
                .addGap(15, 15, 15)
                .addComponent(jButtonPR1, javax.swing.GroupLayout.PREFERRED_SIZE, 67, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jButtonPR2, javax.swing.GroupLayout.PREFERRED_SIZE, 67, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jButtonPR3, javax.swing.GroupLayout.PREFERRED_SIZE, 67, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(13, Short.MAX_VALUE))
        );

        getContentPane().add(jPanelRight);
        jPanelRight.setBounds(328, 0, 72, 243);

        jPanelBotton.setBackground(new java.awt.Color(0, 0, 0));
        jPanelBotton.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(0, 0, 0)));
        jPanelBotton.setForeground(java.awt.Color.green);
        jPanelBotton.setMaximumSize(new java.awt.Dimension(400, 40));

        jLabel_B_L.setBackground(java.awt.Color.lightGray);
        jLabel_B_L.setFont(new java.awt.Font("Segoe UI", 1, 14)); // NOI18N
        jLabel_B_L.setForeground(java.awt.Color.green);
        jLabel_B_L.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jLabel_B_L.setText("00:00");
        jLabel_B_L.setOpaque(true);

        jLabel_B_C.setFont(new java.awt.Font("Segoe UI", 1, 14)); // NOI18N
        jLabel_B_C.setForeground(java.awt.Color.green);
        jLabel_B_C.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jLabel_B_C.setText("tot. tiri");

        jLabel_B_R.setBackground(java.awt.Color.lightGray);
        jLabel_B_R.setFont(new java.awt.Font("Segoe UI", 1, 14)); // NOI18N
        jLabel_B_R.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jLabel_B_R.setText("Aria Off");
        jLabel_B_R.setOpaque(true);

        jLabel_msg.setFont(new java.awt.Font("Segoe UI", 1, 14)); // NOI18N
        jLabel_msg.setForeground(javax.swing.UIManager.getDefaults().getColor("Actions.Green"));
        jLabel_msg.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jLabel_msg.setText("message");

        javax.swing.GroupLayout jPanelBottonLayout = new javax.swing.GroupLayout(jPanelBotton);
        jPanelBotton.setLayout(jPanelBottonLayout);
        jPanelBottonLayout.setHorizontalGroup(
            jPanelBottonLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanelBottonLayout.createSequentialGroup()
                .addGap(3, 3, 3)
                .addComponent(jLabel_B_L, javax.swing.GroupLayout.PREFERRED_SIZE, 110, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jLabel_B_C, javax.swing.GroupLayout.DEFAULT_SIZE, 161, Short.MAX_VALUE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(jLabel_B_R, javax.swing.GroupLayout.PREFERRED_SIZE, 100, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap())
            .addComponent(jLabel_msg, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
        );
        jPanelBottonLayout.setVerticalGroup(
            jPanelBottonLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanelBottonLayout.createSequentialGroup()
                .addGroup(jPanelBottonLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel_B_L)
                    .addComponent(jLabel_B_C)
                    .addComponent(jLabel_B_R))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addComponent(jLabel_msg))
        );

        getContentPane().add(jPanelBotton);
        jPanelBotton.setBounds(0, 248, 400, 45);

        pack();
    }// </editor-fold>//GEN-END:initComponents
/**
     * Evento click Pulsante 1 in alto a dx
     *
     * @param evt
     */
    private void jButtonPR1ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButtonPR1ActionPerformed
        // Pulsante R1  - Passare a ?
        // Considerare la variabile Basic nel DB se si deve andare in start o started
        switch (this.FocusPanelName) {
            case "main" ->
                PanelStart();
            case "start" ->
                PulsanteSu();
            case "started" -> {//Stop
                DialogQ = Stop;
                this.AlertDialogStop = "Annullare il Lavoro ?";
                this.jLabelDialog.setText(AlertDialogStop);
                PanelDialog();
            }
            case "setup" ->
                PanelSetupLan();
            case "warning" ->
                PulsanteSu();
            case "info" ->
                PulsanteSu();
            case "setup lan" ->
                PulsanteSu();
            case "setup wifi" ->
                PulsanteSu();
            case "dialog" -> {
                //Pulsante Sì alla domanda Annulla il lavoro
                this.aria_chiusa();
                PanelStart();
            }
        }

    }//GEN-LAST:event_jButtonPR1ActionPerformed

    public void setAlertDialogAnnulla(String AlertDialogAnnulla) {
        this.AlertDialogAnnulla = AlertDialogAnnulla;
    }

    public void setAlertDialogStop(String AlertDialogStop) {
        this.AlertDialogStop = AlertDialogStop;
        this.jLabelDialog.setText(AlertDialogStop);
        PanelDialog();
    }

    private void jButtonPL1ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButtonPL1ActionPerformed
        switch (this.FocusPanelName) {
            case "main" ->
                PanelWarning();
            case "start" ->
                PanelMain();//Exit verso main
            case "started" ->//Continua
            {
                DialogQ = Continua;
                this.AlertDialogStop = "Continua ?";
                this.jLabelDialog.setText(AlertDialogStop);
                PanelDialog();
            }
            case "setup" ->
                PanelMain();
            case "warning" ->
                PanelMain();
            case "info" ->
                PanelMain();
            case "setup lan" ->
                PanelSetup();
            case "setup wifi" ->
                PanelSetup();
            case "dialog" ->
                PanelStart();
        }
    }//GEN-LAST:event_jButtonPL1ActionPerformed
    /**
     * Evento click Pulsante 2 centrale a sx
     *
     * @param evt
     */
    private void jButtonPL2ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButtonPL2ActionPerformed
        //Pulsante L2 Centrale a sx
        switch (this.FocusPanelName) {
            case "main" ->
                PanelInfo();
//            case "start" ->
            //per ora nulla
            case "started" ->//Accetta il tiro
            {
                DialogQ = Accetta;
                this.AlertDialogStop = "Accettare ?";
                this.jLabelDialog.setText(AlertDialogStop);
                PanelDialog();
            }
//            case "setup" ->
//                PanelSetupLan();
//            case "warning" ->
//                PulsanteSu();
//            case "info" ->
//                PulsanteSu();
            case "setup lan" ->
                PulsanteSu();
            case "setup wifi" ->
                PulsanteSu();
            case "dialog" ->
                PanelStarted();
        }
    }//GEN-LAST:event_jButtonPL2ActionPerformed
    /**
     * Evento click Pulsante 3 in basso a sx
     *
     * @param evt
     */
    private void jButtonPL3ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButtonPL3ActionPerformed
        //Pulsante L3
        switch (this.FocusPanelName) {
            case "main" ->
                PanelSetup();
//          case "start" 
            //Per ora nulla
            case "started" ->//Estende
            {
                DialogQ = Estende;
                this.AlertDialogStop = "Estendere ?";
                this.jLabelDialog.setText(AlertDialogStop);
                PanelDialog();
            }
            case "warning" ->
                PulsanteSu();
//            case "info" ->
//                PulsanteSu();
            case "setup lan" ->
                PulsanteSu();
//            case "setup wifi" ->
//                PulsanteSu();
            case "dialog" ->
                PanelStart();
        }
    }//GEN-LAST:event_jButtonPL3ActionPerformed
    /**
     * Evento click Pulsante 2 centrale dx
     *
     * @param evt
     */
    private void jButtonPR2ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButtonPR2ActionPerformed
        //Pulsante R2
        switch (this.FocusPanelName) {
//            case "main" ->
//                PanelStart();
            case "start" ->
                PulsanteGiu();
            case "started" -> {//Pausa del lavoro ?
                DialogQ = Pausa;
                this.AlertDialogStop = "pausa?";
                this.jLabelDialog.setText(AlertDialogStop);
                PanelDialog();
            }
            case "setup" ->
                PanelSetupWifi();
            case "warning" ->
                PulsanteGiu();
            case "info" ->
                PulsanteGiu();
            case "setup lan" ->
                PulsanteGiu();
            case "setup wifi" ->
                PulsanteGiu();
            case "dialog" ->
                PanelStarted();
        }
    }//GEN-LAST:event_jButtonPR2ActionPerformed
    /**
     * Evento click Pulsante 3 in basso a dx
     *
     * @param evt
     */
    private void jButtonPR3ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButtonPR3ActionPerformed
        // Pulsante R3
        switch (this.FocusPanelName) {
//            case "main" ->
//                per ora nulla;
            case "start" -> {
                PanelStarted();
            }
            case "started" -> {//Annullare il tiro
                DialogQ = Annulla;
                this.AlertDialogStop = "Annullare ?";
                this.jLabelDialog.setText(AlertDialogAnnulla);
                PanelDialog();
            }
//            case "setup" ->
//                PanelSetupLan();
//            case "warning" ->
//                PulsanteSu();
//            case "info" ->
//                PulsanteSu();
            case "setup lan" ->
                PanelSetup();
            case "setup wifi" ->
                PanelSetup();
            case "dialog" ->
                PanelStart();
        }
    }//GEN-LAST:event_jButtonPR3ActionPerformed
    /**
     * PanelMain Set Panel visibile for PanelMain
     */
    private void PanelMain() {
        this.FocusPanelName = "main";
        this.jPanelMain.setVisible(true);
        this.jPanelSetup.setVisible(false);
        this.jPanelStart.setVisible(true);
        this.jPanelStarted.setVisible(false);
        this.jPanelSetupLan.setVisible(false);
        this.jPanelInfo.setVisible(false);
        this.jPanelSetupWiFi.setVisible(false);
        this.jPanelSetupLan.setVisible(false);
        this.jPanelWarning.setVisible(false);
        this.jPanelDialog.setVisible(false);
        this.jPanelCanvas.setVisible(false);
        this.change_buttons(Img_Warning, Img_Info, Img_Setup,
                Img_Play, Img_Nulla, Img_Nulla);
    }

    /**
     * Setup dei pulsanti
     */
    private void change_buttons(ImageIcon I1, ImageIcon I2, ImageIcon I3,
            ImageIcon I4, ImageIcon I5, ImageIcon I6) {
        this.jButtonPL1.setIcon(I1);
        this.jButtonPL2.setIcon(I2);
        this.jButtonPL3.setIcon(I3);
        this.jButtonPR1.setIcon(I4);
        this.jButtonPR2.setIcon(I5);
        this.jButtonPR3.setIcon(I6);
        if (FocusPanelName.equals("started")) {
            this.jButtonPL1.setEnabled(false);
            this.jButtonPL2.setEnabled(false);
            this.jButtonPL3.setEnabled(false);
            this.jButtonPR3.setEnabled(false);
        } else {
            this.jButtonPL1.setEnabled(true);
            this.jButtonPL2.setEnabled(true);
            this.jButtonPL3.setEnabled(true);
            this.jButtonPR3.setEnabled(true);
        }
    }

    /**
     * Settext nella Label alla base del pannello a sx
     *
     * @param msg
     */
    public void set_jLabel_B_L(String msg) {
        this.jLabel_B_L.setText(msg);
    }

    /**
     * Imposta il colore rosso come sfondo al pannello e presume che l'aria sia
     * off Il controllo del tiro viene fatto dall'App JControl, che rimuove il
     * file aria
     *
     */
    public void set_errore_tiro() {
        this.jPanelStarted.setBackground(Color.red);
        this.change_buttons(this.Img_Continua, this.Img_Ok, this.Img_Estende,
                this.Img_Stop, this.Img_Pause, this.Img_Annulla);
        this.jButtonPL1.setEnabled(true);
        this.jButtonPL2.setEnabled(true);
        this.jButtonPL3.setEnabled(true);
        this.jButtonPR3.setEnabled(true);
        this.repaint();
    }

    /**
     * @param args the command line arguments
     */
    public static void main(String args[]) {
        /* Set the Nimbus look and feel */
        //<editor-fold defaultstate="collapsed" desc=" Look and feel setting code (optional) ">
        /* If Nimbus (introduced in Java SE 6) is not available, stay with the default look and feel.
         * For details see http://download.oracle.com/javase/tutorial/uiswing/lookandfeel/plaf.html 
         */
        try {
            for (javax.swing.UIManager.LookAndFeelInfo info : javax.swing.UIManager.getInstalledLookAndFeels()) {
                if ("Nimbus".equals(info.getName())) {
                    javax.swing.UIManager.setLookAndFeel(info.getClassName());
                    break;
                }
            }
        } catch (ClassNotFoundException ex) {
            java.util.logging.Logger.getLogger(JRivitMain.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (InstantiationException ex) {
            java.util.logging.Logger.getLogger(JRivitMain.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (IllegalAccessException ex) {
            java.util.logging.Logger.getLogger(JRivitMain.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (javax.swing.UnsupportedLookAndFeelException ex) {
            java.util.logging.Logger.getLogger(JRivitMain.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        }
        //</editor-fold>

        /* Create and display the form */
        java.awt.EventQueue.invokeLater(() -> {
            new JRivitMain().setVisible(true);
        });
    }

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JTextArea JTextAreaDescrizioneLavoro;
    private java.awt.Canvas canvasGraph;
    private javax.swing.JButton jButtonPL1;
    private javax.swing.JButton jButtonPL2;
    private javax.swing.JButton jButtonPL3;
    private javax.swing.JButton jButtonPR1;
    private javax.swing.JButton jButtonPR2;
    private javax.swing.JButton jButtonPR3;
    private javax.swing.JLabel jLabelAnnullati;
    private javax.swing.JLabel jLabelContatore;
    private javax.swing.JLabel jLabelDialog;
    private javax.swing.JLabel jLabelErrati;
    private javax.swing.JLabel jLabelLogo;
    private javax.swing.JLabel jLabelNomeDevice;
    private javax.swing.JLabel jLabelNomeLavoro;
    private javax.swing.JLabel jLabelValidi;
    private javax.swing.JLabel jLabel_Annullati;
    private javax.swing.JLabel jLabel_B_C;
    private javax.swing.JLabel jLabel_B_L;
    private javax.swing.JLabel jLabel_B_R;
    private javax.swing.JLabel jLabel_Errati;
    private javax.swing.JLabel jLabel_Validi;
    private javax.swing.JLabel jLabel_msg;
    private javax.swing.JLayeredPane jLayeredPaneCenter;
    private javax.swing.JPanel jPanelBotton;
    private javax.swing.JPanel jPanelCanvas;
    private javax.swing.JPanel jPanelDialog;
    private javax.swing.JPanel jPanelInfo;
    private javax.swing.JPanel jPanelLeft;
    private javax.swing.JPanel jPanelMain;
    private javax.swing.JPanel jPanelRight;
    private javax.swing.JPanel jPanelSetup;
    private javax.swing.JPanel jPanelSetupLan;
    private javax.swing.JPanel jPanelSetupWiFi;
    private javax.swing.JPanel jPanelStart;
    private javax.swing.JPanel jPanelStarted;
    private javax.swing.JPanel jPanelWarning;
    private javax.swing.JProgressBar jProgressBar;
    private java.awt.List listInfo;
    private java.awt.List listLavori;
    private java.awt.List listSetupLan;
    private java.awt.List listSetupWiFi;
    private java.awt.List listWarning;
    // End of variables declaration//GEN-END:variables

    /**
     * Show PanelStart da questo pannello si fa la scelta del lavoro dalla lista
     * creata da JControl nel file lavori.txt
     */
    private void PanelStart() {
        int selezionato = 0, i = 0;

        this.FocusPanelName = "start";
        this.jPanelMain.setVisible(false);
        this.jPanelSetup.setVisible(false);
        this.jPanelStart.setVisible(true);
        this.jPanelStarted.setVisible(false);
        this.jPanelSetupLan.setVisible(false);
        this.jPanelInfo.setVisible(false);
        this.jPanelSetupWiFi.setVisible(false);
        this.jPanelSetupLan.setVisible(false);
        this.jPanelWarning.setVisible(false);
        this.jPanelDialog.setVisible(false);
        this.change_buttons(this.Img_Exit, this.Img_Nulla, this.Img_Nulla,
                this.Img_Freccia_su, this.Img_Freccia_giu, this.Img_Ok);
        // Se sessione non contiene 0
        // vuole dire che da una pausa si vuole riprendere un lavoro
        switch (sessione) {
            case "0" ->
                selezionato = this.listLavori.getSelectedIndex();
            default -> {
                String[] items = this.listLavori.getSelectedItems();
                for (i = 0; i < items.length; i++) {
                    if (items[i].startsWith(sessione + ",")) {
                        break;
                    }
                }
                selezionato = i;
                //Ripristina i valori dei tiri
            }
        }
        if (selezionato == -1) {
            selezionato = 1;
        }
        this.listLavori.select(selezionato);
    }

    /**
     * Pannello dopo aver fatto la scelta del Lavoro, tale scelta deve essere
     * scritta nel file /tmp/lavoro_scelto.txt L'App JControl sollecitato
     * dall'evento modifica lavoro_scelto o creazione del file, aggiorna il DB
     * (DA FARE) Mostra il conteggio dei tiri e la barra di avanzamento dei
     * lavori, ...
     */
    private void PanelStarted() {
        this.FocusPanelName = "started";
        this.jPanelMain.setVisible(false);
        this.jPanelSetup.setVisible(false);
        this.jPanelStart.setVisible(false);
        this.jPanelStarted.setVisible(true);
        this.jPanelSetupLan.setVisible(false);
        this.jPanelInfo.setVisible(false);
        this.jPanelSetupWiFi.setVisible(false);
        this.jPanelSetupLan.setVisible(false);
        this.jPanelWarning.setVisible(false);
        this.jPanelDialog.setVisible(false);
        this.jPanelCanvas.setVisible(false);
        this.jPanelCanvas.setEnabled(false);
        this.change_buttons(this.Img_Nulla, this.Img_Nulla, this.Img_Nulla,
                this.Img_Stop, this.Img_Pause, this.Img_Nulla);
        String lavoro = this.listLavori.getSelectedItem();
//        this.setNomeDelDevice(); Fatto all'avvio dell'AppScreen
        this.jLabelNomeLavoro.setText(lavoro);
        String[] det_lavoro = lavoro.split(",");
        String[] det_nr_lotti = det_lavoro[1].split("=");
        String[] det_nr_tiri = det_lavoro[2].split("=");
        nr_lotti_da_fare = Integer.parseInt(det_nr_lotti[1]);
        nr_tiri_da_fare = Integer.parseInt(det_nr_tiri[1]);
        this.AggiornaTiriErrati();
        this.AggiornaTiriAnnullati();
        this.AggiornaTiri();
        this.jLabelContatore.setText(nr_lotti_fatti + "/" + nr_lotti_da_fare
                + " - " + nr_tiri_fatti + "/" + nr_tiri_da_fare);
        this.jProgressBar.setMaximum(Integer.parseInt(det_nr_tiri[1]));
//        String lavoroScelto = lavoro.substring(0,lavoro.indexOf(',') WIDTH);
        this.repaint();
    }

    public void setjLabelAnnullati(String ta) {
        this.jLabelAnnullati.setText(ta);
        this.repaint();
    }

    public void setjLabelContatore(String c) {
        this.jLabelContatore.setText(c);
        this.repaint();
    }

    public void setjLabelErrati(String Errati) {
        this.jLabelErrati.setText(Errati);
        this.repaint();
    }

    public void setjLabelValidi(String Validi) {
        this.jLabelValidi.setText(Validi);
        this.repaint();
    }

    /**
     * Pannello che mostra il contenuto del file /tmp/warning.txt
     */
    private void PanelWarning() {
        this.jPanelWarning.setVisible(true);
        this.jLayeredPaneCenter.moveToFront(this.jPanelWarning);
//        this.FocusPanelName = "warning";
//        this.jPanelMain.setVisible(false);
//        this.jPanelSetup.setVisible(false);
//        this.jPanelStart.setVisible(false);
//        this.jPanelStarted.setVisible(false);
//        this.jPanelSetupLan.setVisible(false);
//        this.jPanelInfo.setVisible(false);
//        this.jPanelSetupWiFi.setVisible(false);
//        this.jPanelSetupLan.setVisible(false);
//        this.jPanelWarning.setVisible(true);
//        this.jPanelDialog.setVisible(false);
        this.change_buttons(this.Img_Exit, this.Img_Freccia_sx, this.Img_Freccia_dx,
                this.Img_Freccia_su, this.Img_Freccia_giu, this.Img_Nulla);
    }

    /**
     * Alla pressione del pulsante che simula la freccia in su muovendosi sulla
     * lista
     */
    private void PulsanteSu() {
        int nrItem = 0;
        int nrCurItem = 0;
        java.awt.List lista = null;
        // Qual'è il pannello in primo piano ?
        Component pannello = this.jLayeredPaneCenter.getComponent(0);
        String focusPanelName = pannello.getName();
        switch (this.FocusPanelName) {
            case "start" -> {
                lista = this.listLavori;
                int selezionato = this.listLavori.getSelectedIndex();
                if (selezionato == -1) {
                    selezionato = 1;
                    this.listLavori.select(selezionato);
                }
            }
            case "setup wifi" ->
                lista = this.listSetupWiFi;
            case "setup lan" ->
                lista = this.listSetupLan;
            case "info" ->
                lista = this.listInfo;
            case "warning" ->
                lista = this.listWarning;
        }//EndSwitch
        if (lista != null) {
            nrItem = lista.getItemCount();
            nrCurItem = lista.getSelectedIndex();
            if (nrCurItem > 0) {
                nrCurItem--;
            } else {
                nrCurItem = nrItem - 1;//Va all'ultimo Item
            }
            lista.select(nrCurItem);
        }//End LIsta not NULL

    }//End PulsanteSu

    /**
     * Simula la pressione del pulsante per scorrere la lista in giù
     */
    private void PulsanteGiu() {
        java.awt.List lista = null;
        switch (this.FocusPanelName) {
            case "start" -> {
                lista = this.listLavori;
                int selezionato = this.listLavori.getSelectedIndex();
                if (selezionato == -1) {
                    selezionato = 1;
                    this.listLavori.select(selezionato);
                }
            }
            case "setup wifi" ->
                lista = this.listSetupWiFi;
            case "setup lan" ->
                lista = this.listSetupLan;
            case "info" ->
                lista = this.listInfo;
            case "warning" ->
                lista = this.listWarning;
        }//EndSwitch
        if (lista != null) {
            int nrItem = lista.getItemCount();
            int nrCurItem = lista.getSelectedIndex();
            if (nrCurItem < nrItem - 1) {
                nrCurItem++;
            } else {
                nrCurItem = 0;//ritorna al primo Item
            }
            lista.select(nrCurItem);
        }//End LIsta not NULL

    }//End PulsanteSu

    /**
     * Pannello per la configurazione della LAN Legge il file /tmp/setup_lan.txt
     */
    private void PanelSetupLan() {
        this.FocusPanelName = "setup lan";
        this.jPanelMain.setVisible(false);
        this.jPanelSetup.setVisible(false);
        this.jPanelStart.setVisible(false);
        this.jPanelStarted.setVisible(false);
        this.jPanelSetupLan.setVisible(false);
        this.jPanelInfo.setVisible(false);
        this.jPanelSetupWiFi.setVisible(false);
        this.jPanelSetupLan.setVisible(true);
        this.jPanelWarning.setVisible(false);
        this.jPanelDialog.setVisible(false);
        this.change_buttons(this.Img_Exit, this.Img_Nulla, this.Img_Nulla,
                this.Img_Freccia_su, this.Img_Freccia_giu, this.Img_Ok);
    }

    /**
     * Pannello per il setup della WiFi Legge il file /tmp/setup_wifi.txt
     */
    private void PanelSetupWifi() {
        this.FocusPanelName = "setup wifi";
        this.jPanelMain.setVisible(false);
        this.jPanelSetup.setVisible(false);
        this.jPanelStart.setVisible(false);
        this.jPanelStarted.setVisible(false);
        this.jPanelSetupLan.setVisible(false);
        this.jPanelInfo.setVisible(false);
        this.jPanelSetupWiFi.setVisible(true);
        this.jPanelSetupLan.setVisible(false);
        this.jPanelWarning.setVisible(false);
        this.jPanelDialog.setVisible(false);
        this.change_buttons(this.Img_Exit, this.Img_Nulla, this.Img_Nulla,
                this.Img_Freccia_su, this.Img_Freccia_giu, this.Img_Ok);
    }

    /**
     * Pannello che mostra il contenuto del file /tmp/info.txt
     */
    private void PanelInfo() {
        this.FocusPanelName = "info";
        this.jPanelMain.setVisible(false);
        this.jPanelSetup.setVisible(false);
        this.jPanelStart.setVisible(false);
        this.jPanelStarted.setVisible(false);
        this.jPanelSetupLan.setVisible(false);
        this.jPanelInfo.setVisible(true);
        this.jPanelSetupWiFi.setVisible(false);
        this.jPanelSetupLan.setVisible(false);
        this.jPanelWarning.setVisible(false);
        this.jPanelDialog.setVisible(false);
        this.change_buttons(this.Img_Exit, this.Img_Freccia_sx, this.Img_Freccia_dx,
                this.Img_Freccia_su, this.Img_Freccia_giu, this.Img_Nulla);
    }

    /**
     * Pannello necessario per la conferma della scelta Stop/Pausa del lavoro Lo
     * Stop cancella il lavoro, la pausa memorizza (file pausa.txt) il nome del
     * Lavoro messo in Pausa. L'APP Control alla creazione del file
     * /tmp/pausa.txt aggiorna il DB (DA FARE)
     */
    private void PanelDialog() {
        this.FocusPanelName = "dialog";
        this.jPanelMain.setVisible(false);
        this.jPanelSetup.setVisible(false);
        this.jPanelStart.setVisible(false);
        this.jPanelStarted.setVisible(false);
        this.jPanelSetupLan.setVisible(false);
        this.jPanelInfo.setVisible(false);
        this.jPanelSetupWiFi.setVisible(false);
        this.jPanelSetupLan.setVisible(false);
        this.jPanelWarning.setVisible(false);
        this.jPanelDialog.setVisible(true);
        this.change_buttons(this.Img_Nulla, this.Img_Nulla, this.Img_Nulla,
                this.Img_Ok, this.Img_Cancel, this.Img_Nulla);
    }

    /**
     * AggiornaWarning carica eventuali Warning dal file /tmp/warning.txt
     *
     * @param lista
     */
    public void AggiornaWarning(List<String> lista) {
        RefreshList(listWarning, lista);
    }//End AggiornaInfo

    /**
     * AggiornaInfo carica eventuali Informazioni dal file /tmp/info.txt
     *
     * @param lista
     */
    public void AggiornaInfo(List<String> lista) {
        RefreshList(listInfo, lista);
    }//End AggiornaInfo

    /**
     * AggiornaSetupLan carica eventuali Informazioni dal file
     * /tmp/setup_lan.txt
     *
     * @param lista
     */
    public void AggiornaSetupLan(List<String> lista) {
        RefreshList(listSetupLan, lista);
    }//End AggiornaSetupLan

    /**
     * AggiornaSetupWiFi carica eventuali Informazioni dal file
     * /tmp/setup_wifi.txt
     *
     * @param lista
     */
    public void AggiornaSetupWiFi(List<String> lista) {
        RefreshList(listSetupWiFi, lista);
    }//End AggiornaSetupWiFi

    /**
     * AggiornaLavori
     *
     * @param lista
     */
    public void AggiornaLavori(List<String> lista) {
        RefreshList(listLavori, lista);
    }//End AggiornaSetupWiFi

    /**
     * AggiornaLavoriDescrizione carica eventuali Informazioni dal file
     * /tmp/setup_wifi.txt
     *
     * @param descrizione
     */
    public void AggiornaLavoriDescrizione(String descrizione) {
        this.Lavorodescrizione = descrizione;
        this.JTextAreaDescrizioneLavoro.setText(descrizione);
        this.repaint();
    }//End AggiornaSetupWiFi

    /**
     * AggiornaSessione carica eventuali Informazioni dal file
     * /tmp/setup_wifi.txt
     *
     * @param sessione
     */
    public void AggiornaSessione(String sessione) {
        this.sessione = sessione;
    }//End AggiornaSetupWiFi

    /**
     * RefreshList riempie un generico elenco
     */
    private void RefreshList(java.awt.List elenco, List<String> righe) {
        elenco.removeAll();
        for (String riga : righe) {
            elenco.add(riga);
        }
        elenco.select(0);
        this.repaint();
    }

    /**
     * Metodo per prendere l'imput dai pulsanti fisici Non Serve
     *
     * @param p String nome pulsante
     */
    public void pulsante_hw(String p) {
        switch (p) {
            case "PL1" ->
                jButtonPL1ActionPerformed(null);
            case "PL2" ->
                jButtonPL2ActionPerformed(null);
            case "PL3" ->
                jButtonPL3ActionPerformed(null);
            case "PR1" ->
                jButtonPR1ActionPerformed(null);
            case "PR2" ->
                jButtonPR2ActionPerformed(null);
            case "PR3" ->
                jButtonPR3ActionPerformed(null);
        }
    }

    /**
     * Legge il file "/tmp/nome_device.txt"
     *
     * @return Nome del device
     */
    private void setNomeDelDevice() {
        this.w_mf.set_operation(this.fileNomeDevice);
        try {
            this.w_mf.doInBackground();
        } catch (Exception ex) {
            Logger.getLogger(JRivitMain.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    /**
     * Pannello di Setup l'utente deve scegliere tra setup Lan o WiFi
     */
    private void PanelSetup() {
        this.FocusPanelName = "setup";
        this.jPanelMain.setVisible(false);
        this.jPanelSetup.setVisible(true);
        this.jPanelStart.setVisible(false);
        this.jPanelStarted.setVisible(false);
        this.jPanelSetupLan.setVisible(false);
        this.jPanelInfo.setVisible(false);
        this.jPanelSetupWiFi.setVisible(false);
        this.jPanelWarning.setVisible(false);
        this.jPanelDialog.setVisible(false);
        this.change_buttons(this.Img_Exit, this.Img_Nulla, this.Img_Nulla,
                this.Img_Lan, this.Img_WiFi, this.Img_Nulla);
    }

    /**
     * aria_chiusa chiamato da WorkerThread imposta l'interfaccia
     */
    public void aria_chiusa() {
        this.jLabel_B_R.setText("Aria OFF");
        this.jLabel_B_R.setBackground(Color.red);
        this.repaint();
    }

    /**
     * aria_aperta chiamato da WorkerThread imposta l'interfaccia
     */
    public void aria_aperta() {
        this.jLabel_B_R.setText("Aria ON");
        this.jLabel_B_R.setBackground(Color.green);
        this.repaint();
    }

    /**
     *
     * @param tiri int - chiamato da WorkerThread imposta l'interfaccia
     * aggiornando i campi associati ai nr dei tiri e aggiorna i lotti fatti la
     * variabile passata al metodo e letta dal file tiri_ok
     */
    public void nr_tiri_Lotti(int tiri_ok) {
        int tiri = tiri_ok;

        if (this.nr_lotti_da_fare > 1 && tiri > 0) {
            if ((tiri == (this.nr_lotti_da_fare * this.nr_tiri_da_fare))) {
                // E' Finito il lavoro !
                this.jPanelStarted.setBackground(Color.BLUE);
            } else {
                this.jPanelStarted.setBackground(Color.WHITE);
            }
            if (tiri == (this.nr_tiri_da_fare)) {
                //Finito un lotto fare flash con il colore
                this.nr_lotti_fatti++;
                this.jPanelStarted.setBackground(Color.BLUE);
                try {
                    Thread.sleep(1000);
                } catch (InterruptedException ex) {
                    Logger.getLogger(JRivitMain.class.getName()).log(Level.SEVERE, null, ex);
                }
                this.jPanelStarted.setBackground(Color.WHITE);
            }

        } else {
            if (this.nr_tiri_da_fare == -1) {
                //Lavoro senza fine
                this.jPanelStarted.setBackground(Color.DARK_GRAY);

            } else {
                if ((tiri == (this.nr_tiri_da_fare))) {
                    // E' Finito il lavoro !
                    this.jPanelStarted.setBackground(Color.BLUE);
                }
            }
        }

        if (this.nr_lotti_da_fare > 0) {
            this.nr_tiri_fatti = tiri
                    - (this.nr_lotti_fatti * this.nr_tiri_da_fare);
        }

        this.jLabelValidi.setText("" + tiri);
        this.jProgressBar.setValue(tiri);
        this.jLabelContatore.setText(nr_lotti_fatti + "/" + nr_lotti_da_fare
                + " - " + nr_tiri_fatti + "/" + nr_tiri_da_fare);
        this.repaint();
    }

    /**
     * tiri_errati chiamato da WorkerThread imposta l'interfaccia
     */
    public void tiri_errati() {
        this.set_errore_tiro();
        this.repaint();
    }

    /**
     * Aggiorna il Label dei tiri errati
     *
     * @param tiri_errati
     */
    void nr_errori(int tiri_errati) {
        this.jLabelErrati.setText("" + tiri_errati);
        this.repaint();
    }

    void errore() {
        this.w_mf.set_operation("gestione_errore");
        try {
            this.w_mf.doInBackground();
        } catch (Exception ex) {
            Logger.getLogger(JRivitMain.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    /**
     * reset_errore_tiro ripristina i colori di default Imposta ARIA ON ?? DA
     * RIFARE
     */
    public void reset_errore_tiro() {
        this.jPanelStarted.setBackground(Color.green);
        this.change_buttons(this.Img_Nulla, this.Img_Nulla, this.Img_Nulla,
                this.Img_Stop, this.Img_Pause, this.Img_Nulla);
        this.jButtonPL1.setEnabled(false);
        this.jButtonPL2.setEnabled(false);
        this.jButtonPL3.setEnabled(false);
        this.jButtonPR3.setEnabled(false);
        this.repaint();
    }

    /**
     * Scive i numero totale di tiri a prescindere
     *
     * @param NomeFile String - nome del file dove Control Scrive il nr di Tiri
     * fatti nella sessione corrente
     */
    void tiri() {
        this.w_mf.set_operation("tiri");
        try {
            this.w_mf.doInBackground();
        } catch (Exception ex) {
            Logger.getLogger(JRivitMain.class.getName()).log(Level.SEVERE, null, ex);
        }
//        this.jLabel_B_C.setText(LeggiFile(NomeFile));
    }

    void risposta_attesa_tiro_errato() {
        this.w_mf.set_operation("risposta_attesa_tiro_errato");
        try {
            this.w_mf.doInBackground();
        } catch (Exception ex) {
            Logger.getLogger(JRivitMain.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    /**
     * Aggiorna il LabelValidi
     *
     * @param TiriOk - NomeFile tiri_ok
     */
    public void update_tiri_ok() {
        this.w_mf.set_operation("tiri_ok");
        try {
            this.w_mf.doInBackground();
        } catch (Exception ex) {
            Logger.getLogger(JRivitMain.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    public void set_nr_tiri_fatti(int TiriOK) {
        this.nr_tiri_fatti = TiriOK;
        nr_tiri_Lotti(TiriOK);
    }

    /**
     * Aggiorna il Label_B_C conteggio di tutti i tiri a prescindere
     *
     * @param Tiri_tutti - NomeFile tiri
     */
    void AggiornaTiri() {
        this.w_mf.set_operation("tiri");
        try {
            this.w_mf.doInBackground();
        } catch (Exception ex) {
            Logger.getLogger(JRivitMain.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    /**
     * Metodo AggiornaTiriErrati aggiorna la Label JaLabelErrati
     *
     * @param Errati
     */
    void AggiornaTiriErrati() {
        this.w_mf.set_operation("aggiorna_tiri_errati");
        try {
            this.w_mf.doInBackground();
        } catch (Exception ex) {
            Logger.getLogger(JRivitMain.class.getName()).log(Level.SEVERE, null, ex);
        }
        //this.nr_errori(Integer.parseInt(this.LeggiFile(Errati)));
    }

    /**
     * metodo AggiornaTiriAnnullati aggiorna la Label centrale alla base del
     * panel
     *
     * @param Annullati
     */
    void AggiornaTiriAnnullati() {
        this.w_mf.set_operation("aggiorna_tiri_annullati");
        try {
            this.w_mf.doInBackground();
        } catch (Exception ex) {
            Logger.getLogger(JRivitMain.class.getName()).log(Level.SEVERE, null, ex);
        }
        //this.jLabel_B_C.setText("Annullati " + Integer.valueOf(this.LeggiFile(Annullati)));
        //this.repaint();
    }

    void update_pressione_aria(Float PressioneAria) {
        this.pressioneIn = PressioneAria;
        if (pressioneIn < this.sogliaMin) {
            this.jLabel_msg.setForeground(java.awt.Color.red);
        } else {
            this.jLabel_msg.setForeground(java.awt.Color.green);
        }
        this.jLabel_msg.setText("Pressione Aria " + PressioneAria);
        this.repaint();
    }

    public void update_soglie_pressione_aria_in(Float SogliaMin, Float SogliaMax) {
        this.sogliaMin = SogliaMin;
        this.sogliaMax = SogliaMax;
    }

    /**
     * gestioneDialogRisposte, Non USATO !
     *
     *
     */
    private void gestioneDialogRisposte() {
        switch (DialogQ) {
            case 1 -> //Continua
                DialogAContinua();
            case 2 -> //Accetta
                DialogAAccetta();
            case 3 -> //Estende
                DialogAEstende();
            case 4 -> //Annulla
                DialogAAnnulla();
            case 5 -> //Pausa
                DialogAPausa();
            case 6 -> //Abortire il lavoro
                DialogAAbortire();
        }
    }

    private void DialogAContinua() {
        this.w_mf.set_operation("aggiorna_tiri_annullati");
        try {
            this.w_mf.doInBackground();
        } catch (Exception ex) {
            Logger.getLogger(JRivitMain.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    private void DialogAAccetta() {
        throw new UnsupportedOperationException("Not supported yet."); // Generated from nbfs://nbhost/SystemFileSystem/Templates/Classes/Code/GeneratedMethodBody
    }

    private void DialogAEstende() {
        throw new UnsupportedOperationException("Not supported yet."); // Generated from nbfs://nbhost/SystemFileSystem/Templates/Classes/Code/GeneratedMethodBody
    }

    private void DialogAAnnulla() {
        throw new UnsupportedOperationException("Not supported yet."); // Generated from nbfs://nbhost/SystemFileSystem/Templates/Classes/Code/GeneratedMethodBody
    }

    private void DialogAAbortire() {
        throw new UnsupportedOperationException("Not supported yet."); // Generated from nbfs://nbhost/SystemFileSystem/Templates/Classes/Code/GeneratedMethodBody
    }

    /**
     * DialogAPausa Non Usato per ora
     */
    private void DialogAPausa() {
        throw new UnsupportedOperationException("Not supported yet."); // Generated from nbfs://nbhost/SystemFileSystem/Templates/Classes/Code/GeneratedMethodBody
    }

    /**
     * setNomeDevice set Label noeme del device
     *
     * @param nd Nome del device letto dal file nome_device.txt
     */
    public void setNomeDevice(String nd) {
        this.jLabelNomeDevice.setText(nd);
        this.repaint();
    }

    /**
     * setJLabel_B_C
     *
     * @param t nr totale dei tiri file tiri
     */
    public void setJLabel_B_C(String t) {
        this.jLabel_B_C.setText("tot. tiri " + t);
        this.repaint();
    }

    /**
     * getjLabelValidi Get Label tiri Validi
     *
     * @return la stringa con ilvalore dei tiri validi
     */
    public String getjLabelValidi() {
        return this.jLabelValidi.getText();
    }

    void setCurva(String curva) {
        this.Curva = curva;
        drawGrafico();
    }

    private void drawGrafico() {
        this.jPanelStarted.setVisible(false);
        this.jPanelCanvas.setVisible(true);
        Graphics2D gr = (Graphics2D) this.canvasGraph.getGraphics();
        gr.drawLine(10, 10, 100, 200);

        int y = this.canvasGraph.getHeight();
        String[] ychar = this.Curva.split(",");
        int nPoints;
        nPoints = ychar.length;
        int[] ypoints = new int[nPoints];
        if (nPoints > 0) {
            int[] xpoints = new int[nPoints];
            for (int i = 0; i < nPoints; i++) {
                xpoints[i] = i * 2;
                ypoints[i] = y - Integer.parseInt(ychar[i]) / 5;
            }
            gr.setStroke(new BasicStroke(3));
            gr.setColor(Color.BLACK);
            //        g2.setColor(Color.GREEN);
            gr.drawString("Hello", 20, 20);
            gr.drawPolyline(xpoints, ypoints, nPoints);
            this.canvasGraph.repaint();
//            this.repaint();
        }
    }

    String getCurva() {
        return this.Curva;
    }

}
