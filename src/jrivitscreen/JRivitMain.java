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
import java.awt.Canvas;
import java.awt.Color;
import java.awt.Graphics2D;
import java.io.IOException;
import java.io.InputStream;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.ImageIcon;
import javax.swing.JLabel;
import javax.swing.JPanel;

/**
 *
 * @author Luca Mannocci & Fabio Fragapane
 */
public class JRivitMain extends javax.swing.JFrame {

    private ImageIcon Img_Exit, Img_Ok, Img_Nulla, Img_Freccia_su,
            Img_Freccia_giu, Img_Warning, Img_Setup, Img_Play;
    private Worker w_mf;
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
    private String Lavorodescrizione;
    private ImageIcon Img_Info;
    private int nr_lotti_da_fare;
    private int nr_tiri_da_fare;
    private int nr_lotto_corrente;
    private int nr_tiri_fatti;
    private Float sogliaMin = 7.0f;
    private Float sogliaMax = 10.0f;
    private Float pressioneIn;
    private String sessione;
    private String Curva;
    private int DialogQ = 100;
    static final int Continua = 1, Accetta = 2, Estende = 3, Annulla = 4;
    private final int Stop = 0, Pausa = -1, DialogA = 200, Yes = 1000, No = 2000;
    private String lavoroScelto;
    private final SimpleDateFormat formatter;
    private Properties setup;
    private final String versione;
    private final String data_release;
    private final String srvKey;
    private List<String[]> elencoLavoriArray;
    private String inPausa;

//Dopo una sospensione
    /**
     * Creates new form JRivitMain
     */
    public JRivitMain() {
        initComponents();
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
        elencoLavoriArray = new ArrayList<>();

        formatter = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss");
        try (InputStream in = this.getClass().getResourceAsStream("setup.propetiers")) {
            setup = new Properties();
            setup.load(in);
        } catch (IOException ex) {
            Logger.getLogger(JRivitMain.class.getName()).log(Level.SEVERE, null, ex);
        }
        versione = setup.getProperty("versione", "1.0");
        data_release = setup.getProperty("data_versione", "14/12/2022");
        srvKey = setup.getProperty("srvkey", "");
        System.out.println("JRivitScreen ver. " + versione + " release " + data_release);

        w_mf = new Worker(this);
        esegui("start");
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
        jButtonPL1 = new javax.swing.JButton();
        jButtonPL2 = new javax.swing.JButton();
        jButtonPL3 = new javax.swing.JButton();
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
        jButtonPR1 = new javax.swing.JButton();
        jButtonPR2 = new javax.swing.JButton();
        jButtonPR3 = new javax.swing.JButton();
        jPanelBotton = new javax.swing.JPanel();
        jLabel_B_L = new javax.swing.JLabel();
        jLabel_B_C = new javax.swing.JLabel();
        jLabel_B_R = new javax.swing.JLabel();
        jLabel_msg = new javax.swing.JLabel();

        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);
        setMaximumSize(new java.awt.Dimension(480, 320));
        setMinimumSize(new java.awt.Dimension(480, 320));
        setName("frameMain"); // NOI18N
        setUndecorated(true);
        setPreferredSize(new java.awt.Dimension(480, 320));
        setResizable(false);
        setSize(new java.awt.Dimension(480, 320));
        setType(java.awt.Window.Type.UTILITY);
        getContentPane().setLayout(new org.netbeans.lib.awtextra.AbsoluteLayout());

        jButtonPL1.setIcon(new javax.swing.ImageIcon(getClass().getResource("/jrivitscreen/images/warning.png"))); // NOI18N
        jButtonPL1.setAlignmentX(0.5F);
        jButtonPL1.setMaximumSize(new java.awt.Dimension(67, 67));
        jButtonPL1.setMinimumSize(new java.awt.Dimension(67, 67));
        jButtonPL1.setPreferredSize(new java.awt.Dimension(67, 67));
        jButtonPL1.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButtonPL1ActionPerformed(evt);
            }
        });

        jButtonPL2.setIcon(new javax.swing.ImageIcon(getClass().getResource("/jrivitscreen/images/info.png"))); // NOI18N
        jButtonPL2.setMargin(new java.awt.Insets(4, 14, 4, 14));
        jButtonPL2.setPreferredSize(new java.awt.Dimension(67, 67));
        jButtonPL2.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButtonPL2ActionPerformed(evt);
            }
        });

        jButtonPL3.setIcon(new javax.swing.ImageIcon(getClass().getResource("/jrivitscreen/images/setup.png"))); // NOI18N
        jButtonPL3.setAlignmentX(0.5F);
        jButtonPL3.setPreferredSize(new java.awt.Dimension(67, 67));
        jButtonPL3.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButtonPL3ActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout jPanelLeftLayout = new javax.swing.GroupLayout(jPanelLeft);
        jPanelLeft.setLayout(jPanelLeftLayout);
        jPanelLeftLayout.setHorizontalGroup(
            jPanelLeftLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanelLeftLayout.createSequentialGroup()
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addGroup(jPanelLeftLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jButtonPL1, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.PREFERRED_SIZE, 67, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jButtonPL3, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.PREFERRED_SIZE, 67, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addContainerGap())
            .addGroup(jPanelLeftLayout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jButtonPL2, javax.swing.GroupLayout.PREFERRED_SIZE, 67, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );
        jPanelLeftLayout.setVerticalGroup(
            jPanelLeftLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanelLeftLayout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jButtonPL1, javax.swing.GroupLayout.PREFERRED_SIZE, 67, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(jButtonPL2, javax.swing.GroupLayout.PREFERRED_SIZE, 67, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(12, 12, 12)
                .addComponent(jButtonPL3, javax.swing.GroupLayout.PREFERRED_SIZE, 67, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(12, Short.MAX_VALUE))
        );

        getContentPane().add(jPanelLeft, new org.netbeans.lib.awtextra.AbsoluteConstraints(0, 0, -1, -1));

        jLayeredPaneCenter.setBorder(new javax.swing.border.LineBorder(new java.awt.Color(0, 0, 0), 1, true));
        jLayeredPaneCenter.setOpaque(true);
        jLayeredPaneCenter.setLayout(new org.netbeans.lib.awtextra.AbsoluteLayout());

        jPanelStarted.setBackground(java.awt.Color.white);
        jPanelStarted.setMaximumSize(new java.awt.Dimension(245, 234));
        jPanelStarted.setMinimumSize(new java.awt.Dimension(245, 234));
        jPanelStarted.setName("started"); // NOI18N
        jPanelStarted.setLayout(new org.netbeans.lib.awtextra.AbsoluteLayout());

        jLabelContatore.setFont(new java.awt.Font("SansSerif", 0, 36)); // NOI18N
        jLabelContatore.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jLabelContatore.setText("0/0");
        jPanelStarted.add(jLabelContatore, new org.netbeans.lib.awtextra.AbsoluteConstraints(0, 100, 340, 30));

        jLabelNomeDevice.setFont(new java.awt.Font("Times New Roman", 0, 18)); // NOI18N
        jLabelNomeDevice.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jLabelNomeDevice.setText("Nome Device");
        jPanelStarted.add(jLabelNomeDevice, new org.netbeans.lib.awtextra.AbsoluteConstraints(0, 10, 340, -1));

        jLabelNomeLavoro.setFont(new java.awt.Font("Cantarell", 0, 20)); // NOI18N
        jLabelNomeLavoro.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jLabelNomeLavoro.setText("Nome Lavoro");
        jPanelStarted.add(jLabelNomeLavoro, new org.netbeans.lib.awtextra.AbsoluteConstraints(0, 50, 340, 30));

        jProgressBar.setBorder(javax.swing.BorderFactory.createEtchedBorder());
        jProgressBar.setMaximumSize(new java.awt.Dimension(245, 40));
        jProgressBar.setMinimumSize(new java.awt.Dimension(245, 40));
        jProgressBar.setStringPainted(true);
        jPanelStarted.add(jProgressBar, new org.netbeans.lib.awtextra.AbsoluteConstraints(10, 140, 320, 40));

        jLabelErrati.setBackground(java.awt.Color.red);
        jLabelErrati.setFont(new java.awt.Font("SansSerif", 0, 24)); // NOI18N
        jLabelErrati.setForeground(new java.awt.Color(255, 255, 255));
        jLabelErrati.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jLabelErrati.setText("0");
        jLabelErrati.setBorder(new javax.swing.border.SoftBevelBorder(javax.swing.border.BevelBorder.RAISED));
        jLabelErrati.setOpaque(true);
        jPanelStarted.add(jLabelErrati, new org.netbeans.lib.awtextra.AbsoluteConstraints(250, 190, 80, 25));

        jLabelAnnullati.setBackground(new java.awt.Color(204, 204, 204));
        jLabelAnnullati.setFont(new java.awt.Font("SansSerif", 0, 24)); // NOI18N
        jLabelAnnullati.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jLabelAnnullati.setText("0");
        jLabelAnnullati.setBorder(new javax.swing.border.SoftBevelBorder(javax.swing.border.BevelBorder.RAISED));
        jLabelAnnullati.setOpaque(true);
        jPanelStarted.add(jLabelAnnullati, new org.netbeans.lib.awtextra.AbsoluteConstraints(130, 190, 80, 25));

        jLabel_Errati.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jLabel_Errati.setText("Errati");
        jPanelStarted.add(jLabel_Errati, new org.netbeans.lib.awtextra.AbsoluteConstraints(278, 220, -1, -1));

        jLabel_Annullati.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jLabel_Annullati.setText("Annullati");
        jPanelStarted.add(jLabel_Annullati, new org.netbeans.lib.awtextra.AbsoluteConstraints(150, 220, -1, -1));

        jLabel_Validi.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jLabel_Validi.setText("Validi");
        jPanelStarted.add(jLabel_Validi, new org.netbeans.lib.awtextra.AbsoluteConstraints(20, 220, -1, -1));

        jLabelValidi.setBackground(java.awt.Color.green);
        jLabelValidi.setFont(new java.awt.Font("SansSerif", 0, 24)); // NOI18N
        jLabelValidi.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jLabelValidi.setText("0");
        jLabelValidi.setBorder(new javax.swing.border.SoftBevelBorder(javax.swing.border.BevelBorder.RAISED));
        jLabelValidi.setOpaque(true);
        jPanelStarted.add(jLabelValidi, new org.netbeans.lib.awtextra.AbsoluteConstraints(10, 190, 80, 25));

        jLayeredPaneCenter.add(jPanelStarted, new org.netbeans.lib.awtextra.AbsoluteConstraints(0, 0, 336, 243));

        jPanelSetup.setMaximumSize(new java.awt.Dimension(252, 237));
        jPanelSetup.setMinimumSize(new java.awt.Dimension(245, 234));
        jPanelSetup.setName("setup"); // NOI18N
        jPanelSetup.setLayout(new org.netbeans.lib.awtextra.AbsoluteLayout());
        jLayeredPaneCenter.add(jPanelSetup, new org.netbeans.lib.awtextra.AbsoluteConstraints(0, 0, 336, 243));

        jPanelMain.setBackground(new java.awt.Color(255, 255, 255));
        jPanelMain.setMaximumSize(new java.awt.Dimension(250, 250));
        jPanelMain.setMinimumSize(new java.awt.Dimension(250, 250));
        jPanelMain.setName("main"); // NOI18N
        jPanelMain.setPreferredSize(new java.awt.Dimension(245, 234));
        jPanelMain.setLayout(new org.netbeans.lib.awtextra.AbsoluteLayout());

        jLabelLogo.setBackground(new java.awt.Color(255, 255, 255));
        jLabelLogo.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jLabelLogo.setIcon(new javax.swing.ImageIcon(getClass().getResource("/jrivitscreen/images/logori2.png"))); // NOI18N
        jLabelLogo.setAlignmentY(0.0F);
        jLabelLogo.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        jLabelLogo.setIconTextGap(0);
        jLabelLogo.setMaximumSize(new java.awt.Dimension(250, 250));
        jLabelLogo.setMinimumSize(new java.awt.Dimension(250, 250));
        jLabelLogo.setPreferredSize(new java.awt.Dimension(250, 250));
        jPanelMain.add(jLabelLogo, new org.netbeans.lib.awtextra.AbsoluteConstraints(0, 0, 336, 243));

        jLayeredPaneCenter.add(jPanelMain, new org.netbeans.lib.awtextra.AbsoluteConstraints(0, 0, 336, 243));

        jPanelSetupLan.setMaximumSize(new java.awt.Dimension(250, 250));
        jPanelSetupLan.setMinimumSize(new java.awt.Dimension(250, 250));
        jPanelSetupLan.setName("setup lan"); // NOI18N
        jPanelSetupLan.setLayout(new org.netbeans.lib.awtextra.AbsoluteLayout());
        jPanelSetupLan.add(listSetupLan, new org.netbeans.lib.awtextra.AbsoluteConstraints(7, 7, 320, 230));

        jLayeredPaneCenter.add(jPanelSetupLan, new org.netbeans.lib.awtextra.AbsoluteConstraints(0, 0, 336, 243));

        jPanelSetupWiFi.setMaximumSize(new java.awt.Dimension(250, 250));
        jPanelSetupWiFi.setMinimumSize(new java.awt.Dimension(250, 250));
        jPanelSetupWiFi.setName("setup wifi"); // NOI18N
        jPanelSetupWiFi.setPreferredSize(new java.awt.Dimension(250, 250));
        jPanelSetupWiFi.setRequestFocusEnabled(false);
        jPanelSetupWiFi.setLayout(new org.netbeans.lib.awtextra.AbsoluteLayout());

        listSetupWiFi.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                listSetupWiFiActionPerformed(evt);
            }
        });
        jPanelSetupWiFi.add(listSetupWiFi, new org.netbeans.lib.awtextra.AbsoluteConstraints(7, 7, 320, 230));

        jLayeredPaneCenter.add(jPanelSetupWiFi, new org.netbeans.lib.awtextra.AbsoluteConstraints(0, 0, 336, 243));

        jPanelInfo.setMaximumSize(new java.awt.Dimension(250, 250));
        jPanelInfo.setMinimumSize(new java.awt.Dimension(250, 250));
        jPanelInfo.setName("info"); // NOI18N
        jPanelInfo.setLayout(new org.netbeans.lib.awtextra.AbsoluteLayout());

        listInfo.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                listInfoActionPerformed(evt);
            }
        });
        jPanelInfo.add(listInfo, new org.netbeans.lib.awtextra.AbsoluteConstraints(7, 7, 320, 230));

        jLayeredPaneCenter.add(jPanelInfo, new org.netbeans.lib.awtextra.AbsoluteConstraints(0, 0, 336, 243));

        jPanelWarning.setToolTipText("");
        jPanelWarning.setMaximumSize(new java.awt.Dimension(252, 237));
        jPanelWarning.setMinimumSize(new java.awt.Dimension(245, 234));
        jPanelWarning.setName("warning"); // NOI18N
        jPanelWarning.setOpaque(false);
        jPanelWarning.setLayout(new org.netbeans.lib.awtextra.AbsoluteLayout());

        listWarning.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                listWarningActionPerformed(evt);
            }
        });
        jPanelWarning.add(listWarning, new org.netbeans.lib.awtextra.AbsoluteConstraints(7, 7, 320, 230));

        jLayeredPaneCenter.add(jPanelWarning, new org.netbeans.lib.awtextra.AbsoluteConstraints(0, 0, 336, 243));

        jPanelStart.setMaximumSize(new java.awt.Dimension(250, 250));
        jPanelStart.setMinimumSize(new java.awt.Dimension(250, 250));
        jPanelStart.setName("start"); // NOI18N
        jPanelStart.setLayout(new org.netbeans.lib.awtextra.AbsoluteLayout());

        listLavori.setFont(new java.awt.Font("Lucida Grande", 1, 18)); // NOI18N
        listLavori.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                listLavoriActionPerformed(evt);
            }
        });
        jPanelStart.add(listLavori, new org.netbeans.lib.awtextra.AbsoluteConstraints(7, 7, 320, 130));

        JTextAreaDescrizioneLavoro.setLineWrap(true);
        JTextAreaDescrizioneLavoro.setRows(5);
        jPanelStart.add(JTextAreaDescrizioneLavoro, new org.netbeans.lib.awtextra.AbsoluteConstraints(7, 150, 320, 80));

        jLayeredPaneCenter.add(jPanelStart, new org.netbeans.lib.awtextra.AbsoluteConstraints(0, 0, 336, 243));

        jPanelDialog.setMaximumSize(new java.awt.Dimension(245, 234));
        jPanelDialog.setMinimumSize(new java.awt.Dimension(245, 234));
        jPanelDialog.setName("dialog"); // NOI18N
        jPanelDialog.setLayout(new org.netbeans.lib.awtextra.AbsoluteLayout());

        jLabelDialog.setFont(new java.awt.Font("Times New Roman", 1, 18)); // NOI18N
        jLabelDialog.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jLabelDialog.setText("Annullare il Tiro ?");
        jPanelDialog.add(jLabelDialog, new org.netbeans.lib.awtextra.AbsoluteConstraints(7, 7, 320, 230));

        jLayeredPaneCenter.add(jPanelDialog, new org.netbeans.lib.awtextra.AbsoluteConstraints(0, 0, 336, 243));

        jPanelCanvas.setAlignmentX(0.0F);
        jPanelCanvas.setAlignmentY(0.0F);
        jPanelCanvas.setMaximumSize(new java.awt.Dimension(250, 250));
        jPanelCanvas.setMinimumSize(new java.awt.Dimension(250, 250));
        jPanelCanvas.setName("canvas"); // NOI18N
        jPanelCanvas.setPreferredSize(new java.awt.Dimension(250, 250));
        jPanelCanvas.setLayout(new org.netbeans.lib.awtextra.AbsoluteLayout());

        canvasGraph.setBackground(new java.awt.Color(255, 153, 153));
        canvasGraph.setMaximumSize(new java.awt.Dimension(250, 250));
        canvasGraph.setMinimumSize(new java.awt.Dimension(250, 250));
        jPanelCanvas.add(canvasGraph, new org.netbeans.lib.awtextra.AbsoluteConstraints(0, 0, 330, 240));

        jLayeredPaneCenter.add(jPanelCanvas, new org.netbeans.lib.awtextra.AbsoluteConstraints(0, 0, 336, 243));

        getContentPane().add(jLayeredPaneCenter, new org.netbeans.lib.awtextra.AbsoluteConstraints(75, 0, -1, -1));

        jPanelRight.setAlignmentX(0.0F);
        jPanelRight.setAlignmentY(0.0F);
        jPanelRight.setMaximumSize(new java.awt.Dimension(79, 243));
        jPanelRight.setMinimumSize(new java.awt.Dimension(79, 243));
        jPanelRight.setPreferredSize(new java.awt.Dimension(79, 243));

        jButtonPR1.setIcon(new javax.swing.ImageIcon(getClass().getResource("/jrivitscreen/images/play_circle.png"))); // NOI18N
        jButtonPR1.setMaximumSize(new java.awt.Dimension(67, 67));
        jButtonPR1.setMinimumSize(new java.awt.Dimension(67, 67));
        jButtonPR1.setPreferredSize(new java.awt.Dimension(67, 67));
        jButtonPR1.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButtonPR1ActionPerformed(evt);
            }
        });

        jButtonPR2.setIcon(new javax.swing.ImageIcon(getClass().getResource("/jrivitscreen/images/nulla.png"))); // NOI18N
        jButtonPR2.setMaximumSize(new java.awt.Dimension(67, 67));
        jButtonPR2.setMinimumSize(new java.awt.Dimension(67, 67));
        jButtonPR2.setPreferredSize(new java.awt.Dimension(67, 67));
        jButtonPR2.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButtonPR2ActionPerformed(evt);
            }
        });

        jButtonPR3.setIcon(new javax.swing.ImageIcon(getClass().getResource("/jrivitscreen/images/nulla.png"))); // NOI18N
        jButtonPR3.setMaximumSize(new java.awt.Dimension(67, 67));
        jButtonPR3.setMinimumSize(new java.awt.Dimension(67, 67));
        jButtonPR3.setPreferredSize(new java.awt.Dimension(67, 67));
        jButtonPR3.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButtonPR3ActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout jPanelRightLayout = new javax.swing.GroupLayout(jPanelRight);
        jPanelRight.setLayout(jPanelRightLayout);
        jPanelRightLayout.setHorizontalGroup(
            jPanelRightLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanelRightLayout.createSequentialGroup()
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addGroup(jPanelRightLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jButtonPR1, javax.swing.GroupLayout.PREFERRED_SIZE, 67, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jButtonPR2, javax.swing.GroupLayout.PREFERRED_SIZE, 67, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jButtonPR3, javax.swing.GroupLayout.PREFERRED_SIZE, 67, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addContainerGap())
        );
        jPanelRightLayout.setVerticalGroup(
            jPanelRightLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanelRightLayout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jButtonPR1, javax.swing.GroupLayout.PREFERRED_SIZE, 67, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(jButtonPR2, javax.swing.GroupLayout.PREFERRED_SIZE, 67, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(12, 12, 12)
                .addComponent(jButtonPR3, javax.swing.GroupLayout.PREFERRED_SIZE, 67, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap())
        );

        getContentPane().add(jPanelRight, new org.netbeans.lib.awtextra.AbsoluteConstraints(406, 0, -1, -1));

        jPanelBotton.setBackground(new java.awt.Color(0, 0, 0));
        jPanelBotton.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(0, 0, 0)));
        jPanelBotton.setForeground(java.awt.Color.green);
        jPanelBotton.setMaximumSize(new java.awt.Dimension(400, 40));
        jPanelBotton.setPreferredSize(new java.awt.Dimension(480, 42));

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

        jLabel_msg.setFont(new java.awt.Font("SansSerif", 1, 14)); // NOI18N
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
                .addComponent(jLabel_B_C, javax.swing.GroupLayout.DEFAULT_SIZE, 241, Short.MAX_VALUE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(jLabel_B_R, javax.swing.GroupLayout.PREFERRED_SIZE, 100, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap())
            .addComponent(jLabel_msg, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
        );
        jPanelBottonLayout.setVerticalGroup(
            jPanelBottonLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanelBottonLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanelBottonLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel_B_R)
                    .addComponent(jLabel_B_C)
                    .addComponent(jLabel_B_L))
                .addGap(18, 18, 18)
                .addComponent(jLabel_msg, javax.swing.GroupLayout.DEFAULT_SIZE, 34, Short.MAX_VALUE))
        );

        getContentPane().add(jPanelBotton, new org.netbeans.lib.awtextra.AbsoluteConstraints(0, 243, -1, 77));

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
        // Qual'è il nome del pannello in primo piano ?
        switch (this.jLayeredPaneCenter.getComponent(0).getName()) {
            case "main" ->
                PanelStart();
            case "start" ->
                PulsanteSu();
            case "started", "canvas" -> {//Stop
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
                //Pulsante Sì alla domanda ? Annulla ? Abort ?
                if (this.AlertDialogStop.compareToIgnoreCase("Annullare ?") == 0) {

                } else { // Sì ad Abort

                }
                gestioneDialogRisposte();
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
        // Qual'è il nome del pannello in primo piano ?
        switch (this.jLayeredPaneCenter.getComponent(0).getName()) {
            case "main" ->
                PanelWarning();
            case "start" ->
                PanelMain();//Exit verso main
            case "started", "canvas" ->//Continua
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
        // Qual'è il nome del pannello in primo piano ?
        switch (this.jLayeredPaneCenter.getComponent(0).getName()) {
            case "main" ->
                PanelInfo();
//            case "start" ->
            //per ora nulla
            case "started", "canvas" ->//Accetta il tiro
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
        // Qual'è il nome del pannello in primo piano ?
        switch (this.jLayeredPaneCenter.getComponent(0).getName()) {
            case "main" ->
                PanelSetup();
//          case "start" 
            //Per ora nulla
            case "started", "canvas" ->//Estende
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
        // Qual'è il nome del pannello in primo piano ?
        switch (this.jLayeredPaneCenter.getComponent(0).getName()) {
//            case "main" ->
//                PanelStart();
            case "start" ->
                PulsanteGiu();
            case "started", "canvas" -> {//Pausa del lavoro ?
                DialogQ = Pausa;
                this.AlertDialogStop = "Pausa ?";
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
        // Qual'è il nome del pannello in primo piano ?
        switch (this.jLayeredPaneCenter.getComponent(0).getName()) {
            case "main" ->
                this.Exit();
//                per ora uso il pulsante per chiudere;
            case "start" -> {
                PanelStarted();
            }
            case "started", "canvas" -> {//Annullare il tiro
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

    private void listInfoActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_listInfoActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_listInfoActionPerformed

    private void listLavoriActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_listLavoriActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_listLavoriActionPerformed

    private void listSetupWiFiActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_listSetupWiFiActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_listSetupWiFiActionPerformed

    private void listWarningActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_listWarningActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_listWarningActionPerformed
    /**
     * PanelMain Set Panel visibile for PanelMain
     */
    private void PanelMain() {
//        changePanel(this.jPanelMain); // metodo migliorato per cambio pannello. Da distribuire sostituendo tutte le chiamate a moveToFront (todo)
        this.jLayeredPaneCenter.moveToFront(this.jPanelMain);
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
        // Qual'è il nome del pannello in primo piano ?
        if (this.jLayeredPaneCenter.getComponent(0).getName().equals("started")) {
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
        } catch (ClassNotFoundException | InstantiationException | IllegalAccessException | javax.swing.UnsupportedLookAndFeelException ex) {
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
        this.jLayeredPaneCenter.moveToFront(this.jPanelStart);
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
        this.jLayeredPaneCenter.moveToFront(this.jPanelStarted);
        this.jPanelStarted.setBackground(Color.white);
        this.change_buttons(this.Img_Nulla, this.Img_Nulla, this.Img_Nulla,
                this.Img_Stop, this.Img_Pause, this.Img_Nulla);
        this.setNomeDelDevice(); //Fatto all'avvio dell'AppScreen
        if (this.inPausa.equals("1")) {
//                   List<String> elencoTxt =  new ArrayList<>();
            int quanti = this.listLavori.getItemCount();
            int i;
            for (i = 0; i < quanti; i++) {
                if (this.elencoLavoriArray.get(i)[0].equals(this.lavoroScelto)) {
                    break;
                }
            }
            this.listLavori.select(i);
            // Gestire il caso in cui il lavoro in pausa non viene trovato
        }
        String lavoro = this.listLavori.getSelectedItem();
        int idLavoro = this.listLavori.getSelectedIndex();
        this.lavoroScelto = this.elencoLavoriArray.get(idLavoro)[0];
        this.jLabelNomeLavoro.setText(lavoro);
        String[] det_nr_lotti = this.elencoLavoriArray.get(idLavoro)[1].split("=");
        String[] det_nr_tiri = this.elencoLavoriArray.get(idLavoro)[2].split("=");
        nr_lotti_da_fare = Integer.parseInt(det_nr_lotti[1]);
        nr_tiri_da_fare = Integer.parseInt(det_nr_tiri[1]);
        /*
        this.AggiornaTiriErrati();
        this.AggiornaTiriAnnullati();
        this.AggiornaTiri();
        this.jLabelContatore.setText(nr_lotti_fatti + "/" + nr_lotti_da_fare
                + " - " + nr_tiri_fatti + "/" + nr_tiri_da_fare);
        if (nr_lotti_da_fare != 0) {
            this.jProgressBar.setMaximum(nr_tiri_da_fare * nr_lotti_da_fare);
        } else {
            this.jProgressBar.setMaximum(nr_tiri_da_fare);
        }
         */
        this.repaint();
        esegui("lavoro_scelto");
    }

    public String getLavorodescrizione() {
        return Lavorodescrizione;
    }

    public java.awt.List getListInfo() {
        return listInfo;
    }

    public java.awt.List getListLavori() {
        return listLavori;
    }

    public java.awt.List getListSetupLan() {
        return listSetupLan;
    }

    public java.awt.List getListSetupWiFi() {
        return listSetupWiFi;
    }

    public java.awt.List getListWarning() {
        return listWarning;
    }

    public JLabel getjLabelNomeDevice() {
        return jLabelNomeDevice;
    }

    public JLabel getjLabelNomeLavoro() {
        return jLabelNomeLavoro;
    }

    public String getLavoroScelto() {
        return this.lavoroScelto;
    }

    public void setLavoroScelto(String lavoroScelto) {
        this.lavoroScelto = lavoroScelto;
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
        this.jLayeredPaneCenter.moveToFront(this.jPanelWarning);
        this.change_buttons(this.Img_Exit, this.Img_Freccia_sx, this.Img_Freccia_dx,
                this.Img_Freccia_su, this.Img_Freccia_giu, this.Img_Nulla);
    }

    /**
     * Alla pressione del pulsante che simula la freccia in su muovendosi sulla
     * lista
     */
    private void PulsanteSu() {
        int nrItem, nrCurItem;
        java.awt.List lista = null;
        // Qual'è il nome del pannello in primo piano ?
        switch (this.jLayeredPaneCenter.getComponent(0).getName()) {
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
            // rendi visibile l'elemento selezionato
            lista.makeVisible(nrCurItem);
        }//End LIsta not NULL

    }//End PulsanteSu

    /**
     * Simula la pressione del pulsante per scorrere la lista in giù
     */
    private void PulsanteGiu() {
        java.awt.List lista = null;
        // Qual'è il nome del pannello in primo piano ?
        switch (this.jLayeredPaneCenter.getComponent(0).getName()) {
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
            // rendi visibile l'elemento selezionato
            lista.makeVisible(nrCurItem);
        }//End LIsta not NULL

    }//End PulsanteSu

    /**
     * Pannello per la configurazione della LAN Legge il file /tmp/setup_lan.txt
     */
    private void PanelSetupLan() {
        this.jLayeredPaneCenter.moveToFront(this.jPanelSetupLan);
        this.change_buttons(this.Img_Exit, this.Img_Nulla, this.Img_Nulla,
                this.Img_Freccia_su, this.Img_Freccia_giu, this.Img_Ok);
    }

    /**
     * Pannello per il setup della WiFi Legge il file /tmp/setup_wifi.txt
     */
    private void PanelSetupWifi() {
        this.jLayeredPaneCenter.moveToFront(this.jPanelSetupWiFi);
        this.change_buttons(this.Img_Exit, this.Img_Nulla, this.Img_Nulla,
                this.Img_Freccia_su, this.Img_Freccia_giu, this.Img_Ok);
    }

    /**
     * Pannello che mostra il contenuto del file /tmp/info.txt
     */
    private void PanelInfo() {
        this.jLayeredPaneCenter.moveToFront(this.jPanelInfo);
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
        this.jLayeredPaneCenter.moveToFront(this.jPanelDialog);
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
        List<String> elencoTxt = new ArrayList<>();
        for (String riga : lista) {
            String[] lavoroSplit = riga.split("§");
            this.elencoLavoriArray.add(lavoroSplit);
            elencoTxt.add(lavoroSplit[0] + " " + lavoroSplit[1] + " " + lavoroSplit[2]);
        }
        RefreshList(listLavori, elencoTxt);
    }//End AggiornaLavori

    /**
     * AggiornaSessione
     *
     * @param sessione
     */
    public void AggiornaSessione(String sessione) {
        this.sessione = sessione;
    }//End 

    /**
     * RefreshList riempie un generico elenco
     *
     * @param elenco oggetto del tipo awt.List
     * @param righe oggetto del tipo List
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
        esegui("aggiorna_nome_device");
    }

    /**
     * Pannello di Setup l'utente deve scegliere tra setup Lan o WiFi
     */
    private void PanelSetup() {
        this.jLayeredPaneCenter.moveToFront(this.jPanelSetup);
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
     * visualizza i dati aggiornati
     */
    public void update_tiri_lotti() {
        this.jLabelValidi.setText("" + nr_tiri_fatti);

        if (this.nr_tiri_da_fare == -1) {
            //Lavoro senza fine
            this.jPanelStarted.setBackground(Color.GRAY);
            this.jProgressBar.setVisible(false);
            this.jLabelNomeLavoro.setText("Lavoro senza limiti");
            this.jLabelContatore.setText("" + nr_tiri_fatti);
        } else {
            if (this.nr_lotti_da_fare > nr_lotto_corrente && nr_tiri_fatti >= nr_tiri_da_fare) {
                // E' Finito il lavoro !
                this.jPanelStarted.setBackground(Color.BLUE);
            } else {
                this.jPanelStarted.setBackground(Color.WHITE);
            }
            this.jLabelContatore.setText(nr_lotto_corrente + "/" + nr_lotti_da_fare
                    + " - " + nr_tiri_fatti + "/" + nr_tiri_da_fare);
            this.jProgressBar.setMaximum(nr_lotti_da_fare*nr_tiri_da_fare);
            this.jProgressBar.setValue(nr_tiri_fatti+((nr_lotto_corrente-1)*nr_tiri_da_fare)); // calcolo dei tiri complessivi per l'avanzamento della barra
            this.jProgressBar.setVisible(true);
        }
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
     * reset_errore_tiro ripristina i colori di default Imposta ARIA ON ?? DA
     * RIFARE
     */
    public void reset_errore_tiro() {
        esegui("reset_errore");
        this.jPanelStarted.setBackground(Color.green);
        this.change_buttons(this.Img_Nulla, this.Img_Nulla, this.Img_Nulla,
                this.Img_Stop, this.Img_Pause, this.Img_Nulla);
        this.jButtonPL1.setEnabled(false);
        this.jButtonPL2.setEnabled(false);
        this.jButtonPL3.setEnabled(false);
        this.jButtonPR3.setEnabled(false);
        this.jLayeredPaneCenter.moveToFront(this.jPanelStarted);
        this.repaint();
    }
 
    void risposta_attesa_tiro_errato() {
        esegui("risposta_attesa_tiro_errato");
    }

    public void set_nr_tiri_fatti(int TiriOK) {
        this.nr_tiri_fatti = TiriOK;
    }

    /**
     * Aggiorna il Label_B_C conteggio di tutti i tiri a prescindere
     *
     * @param Tiri_tutti - NomeFile tiri
     */
    void AggiornaTiri() {
        esegui("tiri");
    }

    /**
     * Metodo AggiornaTiriErrati aggiorna la Label JaLabelErrati
     *
     * @param Errati
     */
    void AggiornaTiriErrati() {
        esegui("aggiorna_tiri_errati");
    }

    /**
     * metodo AggiornaTiriAnnullati aggiorna la Label centrale alla base del
     * panel
     *
     * @param Annullati
     */
    void AggiornaTiriAnnullati() {
        esegui("aggiorna_tiri_annullati");
    }

    void update_pressione_aria(Float PressioneAria) {
        this.pressioneIn = PressioneAria;
        if (pressioneIn <= this.sogliaMin) {
            this.jLabel_msg.setForeground(java.awt.Color.red);
            this.jLabel_msg.setText("Pressione aria insufficiente: " + PressioneAria + " Bar");
        } else {
            this.jLabel_msg.setForeground(java.awt.Color.green);
            this.jLabel_msg.setText("Pressione aria corretta: " + PressioneAria + " Bar");
        }
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
            {
                esegui("continua");
                PanelStarted();
            }
            case 2 -> //Accetta
                esegui("accetta");
            case 3 -> //Estende
                esegui("estendi");
            case 4 -> //Annulla
                esegui("annulla");
            case 5 -> //Pausa
                esegui("pausa");
            case 0 -> //Abortire il lavoro
            {
                esegui("abort");
                PanelStart();
            }
        }
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
        this.jLayeredPaneCenter.moveToFront(this.jPanelCanvas);
        this.repaint();
        this.Curva = curva;
        esegui("curva");
//        drawGrafico();
    }

    public Canvas get_canvasGraph() {
        return this.canvasGraph;
    }

    private void drawGrafico() {
        this.jLayeredPaneCenter.moveToFront(this.jPanelCanvas);
        Graphics2D gr = (Graphics2D) this.canvasGraph.getGraphics();

        int y = this.canvasGraph.getHeight();
        String[] ychar = this.Curva.split(",");
        int nPoints;
        nPoints = ychar.length;
        int[] ypoints = new int[nPoints];
        if (nPoints > 0) {
            int[] xpoints = new int[nPoints];
            for (int i = 0; i < nPoints; i++) {
                xpoints[i] = i * 2;
                ypoints[i] = y - Integer.parseInt(ychar[i]) / 6;
            }
            gr.setStroke(new BasicStroke(3));
            gr.setColor(Color.GREEN);
            gr.drawPolyline(xpoints, ypoints, nPoints);
//            this.canvasGraph.repaint();
            this.repaint();
        }
    }

    public void setListInfo(java.awt.List listInfo) {
        this.listInfo = listInfo;
    }

    public void setListLavori(String[] lista_lavori) {
        for (String lista_lavori1 : lista_lavori) {
            this.listLavori.add(lista_lavori1);
        }
    }

    public void setListSetupLan(java.awt.List listSetupLan) {
        this.listSetupLan = listSetupLan;
    }

    public void setListSetupWiFi(java.awt.List listSetupWiFi) {
        this.listSetupWiFi = listSetupWiFi;
    }

    String getCurva() {
        return this.Curva;
    }

    private void esegui(String operazione) {
        this.w_mf.set_operation(operazione);
        try {
            this.w_mf.doInBackground();
        } catch (Exception ex) {
            Logger.getLogger(JRivitMain.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    /**
     * metodo migliorato per cambio pannello. Da distribuire sostituendo tutte
     * le chiamate di moveToFront (todo)
     *
     * @param pannello
     */
    private void changePanel(JPanel pannello) {
        jLayeredPaneCenter.getComponent(0).setVisible(false);   // Nascondo il pannello attuale
        jLayeredPaneCenter.moveToFront(pannello);
        pannello.setVisible(true);
    }

    ;
/**
 * Uscita dal programma
 */
    public void Exit() {
        System.exit(1);
    }

    public String getInPausa() {
        return inPausa;
    }

    public void setInPausa(String inPausa) {
        this.inPausa = inPausa;
    }

    void set_nr_lotti_fatti(int lotti_ok) {
        this.nr_lotto_corrente = lotti_ok;
    }

}
