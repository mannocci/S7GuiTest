/*
 * Copyright (C) 2023 lucamannocci
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

import java.awt.Color;
import java.awt.event.ItemEvent;
import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Scanner;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.ImageIcon;
import javax.swing.DefaultListModel;
import javax.swing.UIManager;
import javax.swing.UnsupportedLookAndFeelException;

/**
 *
 * @author lucamannocci
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
    private String fileNameWarning;
    private ImageIcon Img_Freccia_sx;
    private ImageIcon Img_Freccia_dx;
    private ImageIcon Img_Cancel;
    private String fileNameInfo;
    private String fileNameSetupLan;
    private String fileNameSetupWiFi;
    private String fileNameLavori;
    private String AllertDialogAnnulla;
    private String AllertDialogStop;
    private String fileNomeDevice;
    private String fileNameLavoriDescrizione;
    private String Lavorodescrizione;
    private ImageIcon Img_Info;
    private int nr_lotti_da_fare;
    private int nr_tiri_da_fare;
    private int nr_lotti_fatti;
    private int nr_tiri_fatti;
    private final String PathTmp = "/tmp/CT/";

    /**
     * Creates new form JRivitMain
     */
    public JRivitMain() {
//        try {
//            UIManager.setLookAndFeel("com.sun.java.swing.plaf.windows.WindowsLookAndFeel");
//        } catch (ClassNotFoundException ex) {
//            Logger.getLogger(JRivitMain.class.getName()).log(Level.SEVERE, null, ex);
//        } catch (InstantiationException ex) {
//            Logger.getLogger(JRivitMain.class.getName()).log(Level.SEVERE, null, ex);
//        } catch (IllegalAccessException ex) {
//            Logger.getLogger(JRivitMain.class.getName()).log(Level.SEVERE, null, ex);
//        } catch (UnsupportedLookAndFeelException ex) {
//            Logger.getLogger(JRivitMain.class.getName()).log(Level.SEVERE, null, ex);
//        }
        initComponents();

        this.FocusPanelName = "main";
        this.fileNameWarning = "warning.txt";
        this.fileNameSetupLan = "setup_lan.txt";
        this.fileNameSetupWiFi = "setup_wifi";
        this.fileNameInfo = "info.txt";
        this.fileNameLavori = "lavori.txt";
        this.fileNameLavoriDescrizione = "lavori_descrizione.txt";
        this.fileNomeDevice = "nome_device.txt";
        this.AllertDialogStop = "Annullare Tiro ?";
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
        jScrollPane1 = new javax.swing.JScrollPane();
        jTextAreaDescrizione = new javax.swing.JTextArea();
        jPanelDialog = new javax.swing.JPanel();
        jLabelDialog = new javax.swing.JLabel();
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

        jPanelStarted.setMaximumSize(new java.awt.Dimension(245, 234));
        jPanelStarted.setMinimumSize(new java.awt.Dimension(245, 234));
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
        jProgressBar.setOpaque(true);
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
        jPanelSetup.setLayout(null);
        jLayeredPaneCenter.add(jPanelSetup, new org.netbeans.lib.awtextra.AbsoluteConstraints(7, 2, 245, 234));

        jPanelMain.setMaximumSize(new java.awt.Dimension(252, 237));
        jPanelMain.setMinimumSize(new java.awt.Dimension(245, 234));
        jPanelMain.setPreferredSize(new java.awt.Dimension(245, 234));
        jPanelMain.setLayout(null);

        jLabelLogo.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jLabelLogo.setIcon(new javax.swing.ImageIcon(getClass().getResource("/jrivitscreen/images/logorivit.png"))); // NOI18N
        jPanelMain.add(jLabelLogo);
        jLabelLogo.setBounds(7, 6, 230, 220);

        jLayeredPaneCenter.add(jPanelMain, new org.netbeans.lib.awtextra.AbsoluteConstraints(7, 7, 245, 234));

        jPanelSetupLan.setMaximumSize(new java.awt.Dimension(252, 237));
        jPanelSetupLan.setMinimumSize(new java.awt.Dimension(245, 234));
        jPanelSetupLan.setLayout(null);
        jPanelSetupLan.add(listSetupLan);
        listSetupLan.setBounds(30, 20, 260, 160);

        jLayeredPaneCenter.add(jPanelSetupLan, new org.netbeans.lib.awtextra.AbsoluteConstraints(7, 7, 245, 234));

        jPanelSetupWiFi.setMaximumSize(new java.awt.Dimension(245, 234));
        jPanelSetupWiFi.setMinimumSize(new java.awt.Dimension(245, 234));
        jPanelSetupWiFi.setLayout(null);
        jPanelSetupWiFi.add(listSetupWiFi);
        listSetupWiFi.setBounds(30, 20, 230, 130);

        jLayeredPaneCenter.add(jPanelSetupWiFi, new org.netbeans.lib.awtextra.AbsoluteConstraints(7, 7, 234, 234));

        jPanelInfo.setMaximumSize(new java.awt.Dimension(245, 234));
        jPanelInfo.setMinimumSize(new java.awt.Dimension(245, 234));
        jPanelInfo.setLayout(null);
        jPanelInfo.add(listInfo);
        listInfo.setBounds(20, 10, 240, 220);

        jLayeredPaneCenter.add(jPanelInfo, new org.netbeans.lib.awtextra.AbsoluteConstraints(7, 7, 245, 234));

        jPanelWarning.setMaximumSize(new java.awt.Dimension(252, 237));
        jPanelWarning.setMinimumSize(new java.awt.Dimension(245, 234));
        jPanelWarning.setLayout(null);
        jPanelWarning.add(listWarning);
        listWarning.setBounds(20, 20, 200, 210);

        jLayeredPaneCenter.add(jPanelWarning, new org.netbeans.lib.awtextra.AbsoluteConstraints(7, 7, 245, 234));

        jPanelStart.setMaximumSize(new java.awt.Dimension(252, 237));
        jPanelStart.setMinimumSize(new java.awt.Dimension(245, 234));
        jPanelStart.setLayout(null);

        listLavori.addItemListener(new java.awt.event.ItemListener() {
            public void itemStateChanged(java.awt.event.ItemEvent evt) {
                listLavoriItemStateChanged(evt);
            }
        });
        listLavori.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                listLavoriActionPerformed(evt);
            }
        });
        jPanelStart.add(listLavori);
        listLavori.setBounds(10, 0, 220, 120);

        jTextAreaDescrizione.setColumns(20);
        jTextAreaDescrizione.setRows(5);
        jScrollPane1.setViewportView(jTextAreaDescrizione);

        jPanelStart.add(jScrollPane1);
        jScrollPane1.setBounds(4, 126, 240, 110);

        jLayeredPaneCenter.add(jPanelStart, new org.netbeans.lib.awtextra.AbsoluteConstraints(7, 7, 245, 234));

        jPanelDialog.setMaximumSize(new java.awt.Dimension(245, 234));
        jPanelDialog.setMinimumSize(new java.awt.Dimension(245, 234));
        jPanelDialog.setLayout(null);

        jLabelDialog.setFont(new java.awt.Font("Times New Roman", 1, 18)); // NOI18N
        jLabelDialog.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jLabelDialog.setText("Annullare il Tiro ?");
        jPanelDialog.add(jLabelDialog);
        jLabelDialog.setBounds(10, 60, 180, 22);

        jLayeredPaneCenter.add(jPanelDialog, new org.netbeans.lib.awtextra.AbsoluteConstraints(7, 7, 245, 234));

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
        jLabel_B_L.setText("ora");
        jLabel_B_L.setOpaque(true);

        jLabel_B_C.setFont(new java.awt.Font("Segoe UI", 1, 14)); // NOI18N
        jLabel_B_C.setForeground(java.awt.Color.green);
        jLabel_B_C.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jLabel_B_C.setText("message");

        jLabel_B_R.setBackground(java.awt.Color.lightGray);
        jLabel_B_R.setFont(new java.awt.Font("Segoe UI", 1, 14)); // NOI18N
        jLabel_B_R.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jLabel_B_R.setText("Aria Off");
        jLabel_B_R.setOpaque(true);

        jLabel_msg.setFont(new java.awt.Font("Segoe UI", 1, 14)); // NOI18N
        jLabel_msg.setForeground(java.awt.Color.red);
        jLabel_msg.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jLabel_msg.setText("message");

        javax.swing.GroupLayout jPanelBottonLayout = new javax.swing.GroupLayout(jPanelBotton);
        jPanelBotton.setLayout(jPanelBottonLayout);
        jPanelBottonLayout.setHorizontalGroup(
            jPanelBottonLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanelBottonLayout.createSequentialGroup()
                .addGap(3, 3, 3)
                .addComponent(jLabel_B_L, javax.swing.GroupLayout.PREFERRED_SIZE, 104, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
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
     * Evento click Pulsante 1 dx
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
            case "started" -> {
                this.AllertDialogStop = "Annullare il Lavoro ?";
                this.jLabelDialog.setText(AllertDialogStop);
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
    /**
     * Evento click Pulsante 1 sx
     *
     * @param evt
     */
    private void jButtonPL1ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButtonPL1ActionPerformed
        // Pulsante L1
        switch (this.FocusPanelName) {
            case "main" ->
                PanelWarning();
            case "start" ->
                PanelMain();
            case "started" ->
                PanelStart();
            case "setup" ->
                PanelMain();
            case "warning" ->
                PanelMain();
            case "info" ->
                PanelMain();
            case "setup lan" ->
                PulsanteSu();
            case "setup wifi" ->
                PulsanteSu();
            case "dialog" ->
                PanelStart();
        }
    }//GEN-LAST:event_jButtonPL1ActionPerformed
    /**
     * Evento click Pulsante 2 sx
     *
     * @param evt
     */
    private void jButtonPL2ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButtonPL2ActionPerformed
        //Pulsante L2
        switch (this.FocusPanelName) {
            case "main" ->
                PanelInfo();
//        case "start" ->
            case "started" ->
                PanelStart();
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
            case "dialog" ->
                PanelStarted();
        }
    }//GEN-LAST:event_jButtonPL2ActionPerformed
    /**
     * Evento click Pulsante 3 sx
     *
     * @param evt
     */
    private void jButtonPL3ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButtonPL3ActionPerformed
        //Pulsante L3
        switch (this.FocusPanelName) {
            case "main" ->
                PanelSetup();
//      case "start" 
            case "started" ->
                PanelStart();
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
            case "dialog" ->
                PanelStart();
        }
    }//GEN-LAST:event_jButtonPL3ActionPerformed
    /**
     * Evento click Pulsante 2 dx
     *
     * @param evt
     */
    private void jButtonPR2ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButtonPR2ActionPerformed
        //Pulsante R2
        switch (this.FocusPanelName) {
            case "main" ->
                PanelStart();
            case "start" ->
                PulsanteGiu();
            case "started" -> {
                this.AllertDialogStop = "Lavoro in Pausa ?";
                PanelDialog();
            }
            case "setup" ->
                PanelSetupLan();
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
     * Evento click Pulsante 3 dx
     *
     * @param evt
     */
    private void jButtonPR3ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButtonPR3ActionPerformed
        // Pulsante R3
        switch (this.FocusPanelName) {
            case "main" ->
                PanelStart();
            case "start" -> {
                String lavoro = this.listLavori.getSelectedItem();
                this.jLabelNomeDevice.setText(this.getNomeDelDevice());
                this.jLabelNomeLavoro.setText(lavoro);
                this.jLabel_B_R.setText("Aria ON");
                this.jLabel_B_R.setBackground(Color.GREEN);
                String[] det_lavoro = lavoro.split(",");
                String[] det_nr_lotti = det_lavoro[1].split("=");
                String[] det_nr_tiri = det_lavoro[2].split("=");
                nr_lotti_da_fare = Integer.parseInt(det_nr_lotti[1]);
                nr_tiri_da_fare = Integer.parseInt(det_nr_tiri[1]);
                nr_lotti_fatti = 0;
                nr_tiri_fatti = 0;
                this.jLabelContatore.setText(nr_lotti_fatti + "/" + nr_lotti_da_fare
                        + " - " + nr_tiri_fatti + "/" + nr_tiri_da_fare);
                this.jProgressBar.setMaximum(Integer.valueOf(det_nr_tiri[1]));
                PanelStarted();
            }
            case "started" -> {
                this.AllertDialogStop = "Annullare il Tiro ?";
                this.jLabelDialog.setText(AllertDialogAnnulla);
                PanelDialog();
            }
            case "setup" ->
                PanelSetupLan();
            case "warning" ->
                PulsanteSu();
            case "info" ->
                PulsanteSu();
            case "setup lan" ->
                PanelSetup();
            case "setup wifi" ->
                PanelSetup();
            case "dialog" ->
                PanelStart();
        }
    }//GEN-LAST:event_jButtonPR3ActionPerformed
    private void listLavoriItemStateChanged(java.awt.event.ItemEvent evt) {

    }
    private void listLavoriActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_listLavoriActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_listLavoriActionPerformed
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
    private void set_errore_tiro() {
        this.jButtonPL1.setEnabled(true);
        this.jButtonPL2.setEnabled(true);
        this.jButtonPL3.setEnabled(true);
        this.jButtonPR3.setEnabled(true);
        this.jPanelStarted.setBackground(Color.red);
        this.jPanelStarted.setOpaque(true);
        this.jLabel_B_R.setText("Aria OFF");
        this.jLabel_B_R.setBackground(Color.DARK_GRAY);
        this.change_buttons(this.Img_Continua, this.Img_Ok, this.Img_Estende,
                this.Img_Stop, this.Img_Pause, this.Img_Annulla);
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
        java.awt.EventQueue.invokeLater(new Runnable() {
            public void run() {
                new JRivitMain().setVisible(true);
            }
        });
    }

    // Variables declaration - do not modify//GEN-BEGIN:variables
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
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JTextArea jTextAreaDescrizione;
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
        this.LeggiLavori();
        this.LeggiFileLavoriDescrizione();
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

        int selezionato = this.listLavori.getSelectedIndex();

        if (selezionato == -1) {
            selezionato = 1;
        }
        this.jTextAreaDescrizione.setText(this.Lavorodescrizione);

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
        this.change_buttons(this.Img_Nulla, this.Img_Nulla, this.Img_Nulla,
                this.Img_Stop, this.Img_Pause, this.Img_Nulla);
       
    }

    /**
     * Pannello che mostra il contenuto del file /tmp/warning.txt
     */
    private void PanelWarning() {
        this.FocusPanelName = "warning";
        this.jPanelMain.setVisible(false);
        this.jPanelSetup.setVisible(false);
        this.jPanelStart.setVisible(false);
        this.jPanelStarted.setVisible(false);
        this.jPanelSetupLan.setVisible(false);
        this.jPanelInfo.setVisible(false);
        this.jPanelSetupWiFi.setVisible(false);
        this.jPanelSetupLan.setVisible(false);
        this.jPanelWarning.setVisible(true);
        this.jPanelDialog.setVisible(false);
        this.change_buttons(this.Img_Exit, this.Img_Freccia_sx, this.Img_Freccia_dx,
                this.Img_Freccia_su, this.Img_Freccia_giu, this.Img_Nulla);
        LeggiWarning();
    }

    /**
     * Alla pressione del pulsante che simula la freccia in su muovendosi sulla
     * lista
     */
    private void PulsanteSu() {
        int nrItem = 0;
        int nrCurItem = 0;
        java.awt.List lista = null;
        switch (this.FocusPanelName) {
            case "start" -> {
                lista = this.listLavori;
                int selezionato = this.listLavori.getSelectedIndex();
                if (selezionato == -1) {
                    selezionato = 1;
                }
                this.jTextAreaDescrizione.setText(this.Lavorodescrizione);
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
                nrCurItem = nrItem;//Va all'ultimo Item
            }
            lista.select(nrCurItem);
        }//End LIsta not NULL

    }//End PulsanteSu

    /**
     * Simula la pressione del pulsante per scorrere la lista in giù
     */
    private void PulsanteGiu() {
        int nrItem = 0;
        int nrCurItem = 0;
        java.awt.List lista = null;
        switch (this.FocusPanelName) {
            case "start" -> {
                lista = this.listLavori;
                int selezionato = this.listLavori.getSelectedIndex();
                if (selezionato == -1) {
                    selezionato = 1;
                }
                this.jTextAreaDescrizione.setText(this.Lavorodescrizione);
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
            if (nrCurItem < nrItem) {
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
        LeggiSetupLan();
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
        LeggiSetupWiFi();
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
        LeggiInfo();
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
     * LeggiFileList carica eventuali Warning dal file /tmp/warning.txt
     */
    private void LeggiFileList(String NomeFile, java.awt.List ls) {
        ls.removeAll();
        List<String> fileLetto = LeggiFileElenco(NomeFile);
        for (String riga : fileLetto) {
            ls.add(riga);
        }
        ls.select(0);

    }//End LeggiFileList

    /**
     * LeggiFileList carica eventuali Warning dal file /tmp/warning.txt
     */
    private void LeggiFileLavoriDescrizione() {
        this.Lavorodescrizione = LeggiFile(this.fileNameLavoriDescrizione);
    }//End LeggiFileLavoriDescrizione

    public String LeggiFile(String NomeFile) {
        String contenutoFile = "";
        try {
            File myObj = new File(this.PathTmp + NomeFile);
            Scanner myReader = new Scanner(myObj);
            while (myReader.hasNextLine()) {
                contenutoFile += myReader.nextLine();
            }
            myReader.close();
        } catch (FileNotFoundException e) {
            System.out.println("An error occurred.");
            return "Errore lettura file";
        }
        return contenutoFile;
    }//End LeggiFileLavoriDescrizione

    public List<String> LeggiFileElenco(String NomeFile) {
        List<String> ListaRighe = new ArrayList<String>();
        try {
            File myObj = new File(this.PathTmp + NomeFile);
            Scanner myReader = new Scanner(myObj);
            while (myReader.hasNextLine()) {
                ListaRighe.add(myReader.nextLine());
            }
            myReader.close();
        } catch (FileNotFoundException e) {
            System.out.println("An error occurred.");
            return ListaRighe;
        }
        return ListaRighe;
    }

    /**
     * LeggiWarning carica eventuali Warning dal file /tmp/warning.txt
     */
    public void LeggiWarning() {
        LeggiFileList(this.fileNameWarning, this.listWarning);
        this.repaint();
    }//End LeggiInfo

    /**
     * LeggiInfo carica eventuali Informazioni dal file /tmp/info.txt
     */
    public void LeggiInfo() {
        LeggiFileList(this.fileNameInfo, this.listInfo);
        this.repaint();
    }//End LeggiInfo

    /**
     * LeggiSetuplan carica eventuali Informazioni dal file /tmp/setup_lan.txt
     */
    public void LeggiSetupLan() {
        LeggiFileList(this.fileNameSetupLan, this.listSetupLan);
        this.repaint();
    }//End LeggiSetupLan

    /**
     * LeggiSetupWiFi carica eventuali Informazioni dal file /tmp/setup_wifi.txt
     */
    public void LeggiSetupWiFi() {
        LeggiFileList(this.fileNameSetupWiFi, this.listSetupWiFi);
        this.repaint();
    }//End LeggiSetupWiFi

    /**
     * LeggiLavori carica eventuali Informazioni dal file /tmp/setup_wifi.txt
     */
    public void LeggiLavori() {
        LeggiFileList(this.fileNameLavori, this.listLavori);
        this.repaint();
    }//End LeggiSetupWiFi

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
    private String getNomeDelDevice() {
        return LeggiFile(this.fileNomeDevice);
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
        this.jLabel_B_R.setText("Aria Chiusa");
        this.jLabel_B_R.setBackground(Color.red);
        this.repaint();
    }

    /**
     * aria_aperta chiamato da WorkerThread imposta l'interfaccia
     */
    public void aria_aperta() {
        this.jLabel_B_R.setText("Aria Aperta");
        this.jLabel_B_R.setBackground(Color.green);
        this.repaint();
    }

    /**
     *
     * @param tiri int - chiamato da WorkerThread imposta l'interfaccia
     * aggiornando i campi associati ai nr dei tiri e aggiorna i lotti fatti
     */
    public void nr_tiri(int tiri) {
        if (this.nr_lotti_da_fare > 0) {
            if ((tiri == (this.nr_lotti_da_fare * this.nr_tiri_da_fare))) {
                // E' Finito il lavoro !
            }
            if (tiri == (this.nr_tiri_da_fare * this.nr_lotti_fatti)) {
                //Finito un lotto
                this.nr_lotti_fatti++;
            }

        } else {
            if (this.nr_tiri_da_fare == -1) {
                //Lavoro senza fine
            } else {
                if ((tiri == (this.nr_tiri_da_fare))) {
                    // E' Finito il lavoro !
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
        String strErrore = LeggiFile("errore");
        switch (strErrore) {
            case "0" ->
                this.reset_errore_tiro();
            case "1" ->
                this.set_errore_tiro();
            default ->
                throw new AssertionError();
        }
    }

    /**
     * reset_errore_tiro ripristina i colori di default Imposta ARIA ON ?? DA
     * RIFARE
     */
    private void reset_errore_tiro() {
        this.jButtonPL1.setEnabled(false);
        this.jButtonPL2.setEnabled(false);
        this.jButtonPL3.setEnabled(false);
        this.jButtonPR3.setEnabled(false);
        //this.jPanelStarted.setBackground(Color.green);
        this.jPanelStarted.setOpaque(false);
        this.jLabel_B_R.setText("Aria ON");
        this.jLabel_B_R.setBackground(Color.GREEN);
        this.change_buttons(this.Img_Nulla, this.Img_Nulla, this.Img_Nulla,
                this.Img_Stop, this.Img_Pause, this.Img_Nulla);

    }
}
