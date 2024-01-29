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
 * @ver 1.1 luglio 2023 aggiunto Pannello Calibrazione, 
        corretto sequenze Pannelli pulsanti, invertito pulsante estendi annulla
 * @ver 1.2 cercare di risolvere la latenza dei due pannelli warning e info
 * da stop e pausa non rientra in start
 */
package jrivitscreen;

import java.awt.AWTException;
import java.awt.Color;
import java.awt.Robot;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.awt.event.KeyEvent;
import java.io.IOException;
import java.io.InputStream;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JLayeredPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import org.apache.commons.cli.*;
import javax.swing.JTextArea;

/**
 *
 * @author Luca Mannocci e Fabio Fragapane
 */
public class JRivitMain extends javax.swing.JFrame {

    private String stato;

    private JDoWorker doWorker;
    private ImageIcon Img_Exit, Img_Ok, Img_Nulla, Img_Freccia_su,
            Img_Freccia_giu, Img_Warning, Img_Setup, Img_Play,
            Img_No_Warning, Img_Err_Warning, Img_Med_Warning,
            Img_Grafico, Img_Calibrazione, Img_reloadWork,
            Img_Continua, Img_Estende, Img_Stop,
            Img_Pause, Img_Annulla, Img_Lan, Img_WiFi,
            Img_Freccia_sx, Img_Freccia_dx, Img_Cancel, Img_W, Img_WL;
    private String AlertDialogAnnulla;
    private String AlertDialogWhat;
    private String Lavorodescrizione;
    private ImageIcon Img_Info;

    private int tiriTotali;
    private int lotto;
    private int tiriNelLotto;
    private int tiriValidi;
    private int tiriAnnullati;
    private int tiriErrati;
    private int limLotti;
    private int limPezzi;

    private Float sogliaMin = 5.5f;
    private Float sogliaMax = 7.0f;
    private String sessione;
    private String Curva;
//    private int DialogQ = 100;
    private String lavoroScelto;
    private final SimpleDateFormat formatter;
    private Properties setup;
    public final String versione;
    public final String data_release;
    private final String srvKey;
    private List<String[]> elencoLavori;
    private List<String[]> elencoWl;
    private List<String[]> elencoLavoriCompleto;
    private List<String[]> elencoWlCompleto;
    private String inPausa;
    private Float temp_rpi;
    private Float temp_io_board;
    private Float v_in;
    private Float v_rpi;
    private Float pressione_aria_in;
    private String panCur;
    private boolean inErrore = false;
    private boolean in_pausa = false;
    private boolean chiedi_conferma = false;
    private boolean statoConcluso = false;
    private boolean chiedi_conferma_stop;
    private List<String> elencoDesLavoro;
    private int w_level;
    private JFileWorker fileWorker = null;
    public JGrafico g;
    private Robot robot = null;
    private String pannelloPrecedente;
    private List infoAggiuntive;
    private Float precPressioneAria;
    private String curvaDiRiferimento;
    private ArrayList<Object> elencoDesLavoroCompleto;
    private String um;  // Unità di misura (Bar o Newton)
    private String richiesta;
    private String contesto;
    private boolean abilitaCalibrazione;
    private String esitoTiro;
    private String posizioneErrori;
    private ArrayList<Object> elencoDesWl;
    private List listaWl;
    private boolean inWl;
    private String WLscelta;
    private int WLnrCicli;

//
//Dopo una sospensione
    /**
     * Creates new form JRivitMain
     */
    public JRivitMain() {
        this.panCur = "main";
        initComponents();
        g = new JGrafico(this);
        g.setBackground(new java.awt.Color(255, 255, 255));
        g.setAlignmentX(0.0F);
        g.setAlignmentY(0.0F);
        g.setMaximumSize(new java.awt.Dimension(328, 276));
        g.setMinimumSize(new java.awt.Dimension(328, 276));
        g.setName("canvas");
        g.setPreferredSize(new java.awt.Dimension(328, 276));
        g.setLayout(new org.netbeans.lib.awtextra.AbsoluteLayout());
        this.jLayeredPaneCenter.add(g, new org.netbeans.lib.awtextra.AbsoluteConstraints(1, 1, 328, 276));
        this.jLabelDesPezziNoLimits.setVisible(false);
        this.jLabelPezziNoLimits.setVisible(false);
        this.AlertDialogWhat = "Cancel traction ?";
        this.jLabelNomeWL.setText("");

        Img_Exit = new javax.swing.ImageIcon(getClass().getResource("/jrivitscreen/images/exit.png"));
        Img_Ok = new javax.swing.ImageIcon(getClass().getResource("/jrivitscreen/images/ok.png"));
        Img_Nulla = new javax.swing.ImageIcon(getClass().getResource("/jrivitscreen/images/nulla.png"));
        Img_Freccia_su = new javax.swing.ImageIcon(getClass().getResource("/jrivitscreen/images/freccia_su.png"));
        Img_Freccia_giu = new javax.swing.ImageIcon(getClass().getResource("/jrivitscreen/images/freccia_giu.png"));
        Img_Freccia_sx = new javax.swing.ImageIcon(getClass().getResource("/jrivitscreen/images/freccia_sx.png"));
        Img_Freccia_dx = new javax.swing.ImageIcon(getClass().getResource("/jrivitscreen/images/freccia_dx.png"));
        Img_Warning = new javax.swing.ImageIcon(getClass().getResource("/jrivitscreen/images/no_warning.png"));
        Img_No_Warning = new javax.swing.ImageIcon(getClass().getResource("/jrivitscreen/images/no_warning.png"));
        Img_Med_Warning = new javax.swing.ImageIcon(getClass().getResource("/jrivitscreen/images/warning.png"));
        Img_Err_Warning = new javax.swing.ImageIcon(getClass().getResource("/jrivitscreen/images/err_warning.png"));
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
        Img_Grafico = new javax.swing.ImageIcon(getClass().getResource("/jrivitscreen/images/grafico.png"));
        Img_Calibrazione = new javax.swing.ImageIcon(getClass().getResource("/jrivitscreen/images/calibrazione.png"));
        Img_reloadWork = new javax.swing.ImageIcon(getClass().getResource("/jrivitscreen/images/autorenew.png"));
        Img_W = new javax.swing.ImageIcon(getClass().getResource("/jrivitscreen/images/w.png"));
        Img_WL = new javax.swing.ImageIcon(getClass().getResource("/jrivitscreen/images/wl.png"));

        elencoLavori = new ArrayList<>();
        elencoWl = new ArrayList<>();
        infoAggiuntive = new ArrayList<>();

        try {
            robot = new Robot();
        } catch (AWTException ex) {
            Logger.getLogger(JRivitMain.class.getName()).log(Level.SEVERE, null, ex);
        }
        formatter = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss");
        try (InputStream in = this.getClass().getResourceAsStream("setup.properties")) {
            setup = new Properties();
            setup.load(in);
        } catch (IOException ex) {
            Logger.getLogger(JRivitMain.class.getName()).log(Level.SEVERE, null, ex);
        }
        versione = setup.getProperty("versione", "1.0");
        data_release = setup.getProperty("data_versione", "14/12/2022");
        srvKey = setup.getProperty("srvkey", "");
        System.out.println("JRivitScreen ver. " + versione + " release " + data_release);

        this.pannelloPrecedente = "main";   // Server per gestire il ritorno dal pannello di warning
        this.chiedi_conferma_stop = true;
        this.chiedi_conferma = false;
        this.pressione_aria_in = 0f;
        this.inPausa = "0";

        try {
            fileWorker = new JFileWorker(this);
        } catch (IOException ex) {
            Logger.getLogger(JRivitMain.class.getName()).log(Level.SEVERE, null, ex);
        }
        doWorker = new JDoWorker(this, fileWorker);
        this.esegui("init");
//        try {
//            TimeUnit.SECONDS.sleep(2);//Attesa della fine del metodo init di JDoWorker
//        } catch (InterruptedException ex) {
//            Logger.getLogger(JRivitMain.class.getName()).log(Level.SEVERE, null, ex);
//        }
        this.stato = Static.STATO_STOP;
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
        jLabelContatoreLotti = new javax.swing.JLabel();
        jLabelNomeDevice = new javax.swing.JLabel();
        jProgressBar = new javax.swing.JProgressBar();
        jLabelErrati = new javax.swing.JLabel();
        jLabelAnnullati = new javax.swing.JLabel();
        jLabel_Errati = new javax.swing.JLabel();
        jLabel_Annullati = new javax.swing.JLabel();
        jLabel_Validi = new javax.swing.JLabel();
        jLabelValidi = new javax.swing.JLabel();
        jLabelDesContatorePezzi = new javax.swing.JLabel();
        jLabelDesContatoreLotti = new javax.swing.JLabel();
        jLabelContatorePezzi = new javax.swing.JLabel();
        jLabelDesPezziNoLimits = new javax.swing.JLabel();
        jLabelPezziNoLimits = new javax.swing.JLabel();
        jLabelNomeLavoro = new javax.swing.JLabel();
        jLabelNomeWL = new javax.swing.JLabel();
        jPanelSetup = new javax.swing.JPanel();
        listSetupNM = new java.awt.List();
        jPanelMain = new javax.swing.JPanel();
        jLabelLogo = new javax.swing.JLabel();
        jLabelDeviceName = new javax.swing.JLabel();
        jLabelVersione = new javax.swing.JLabel();
        jPanelSetupLan = new javax.swing.JPanel();
        jScrollPaneLan = new javax.swing.JScrollPane();
        jTextAreaLan = new javax.swing.JTextArea();
        jPanelSetupWiFi = new javax.swing.JPanel();
        jScrollPaneWifi = new javax.swing.JScrollPane();
        jTextAreaWifi = new javax.swing.JTextArea();
        jPanelInfo = new javax.swing.JPanel();
        listInfo = new java.awt.List();
        jPanelWarning = new javax.swing.JPanel();
        listWarning = new java.awt.List();
        jPanelStart = new javax.swing.JPanel();
        listLavori = new java.awt.List();
        listWLavori = new java.awt.List();
        JTextAreaDescrizioneLavoro = new javax.swing.JTextArea();
        jPanelDialog = new javax.swing.JPanel();
        jLabelDialog = new javax.swing.JLabel();
        jPanelCalibrazione = new javax.swing.JPanel();
        jLabelNomeLavoroCal = new javax.swing.JLabel();
        jLabelAvvisoCalibrazione = new javax.swing.JLabel();
        jPanelRight = new javax.swing.JPanel();
        jButtonPR1 = new javax.swing.JButton();
        jButtonPR2 = new javax.swing.JButton();
        jButtonPR3 = new javax.swing.JButton();
        jPanelBotton = new javax.swing.JPanel();
        jLabel_B_L = new javax.swing.JLabel();
        jLabel_B_R = new javax.swing.JLabel();
        jLabel_msg = new javax.swing.JLabel();
        jLabelWarning = new javax.swing.JLabel();
        jLabelLan = new javax.swing.JLabel();
        jLabelVPN = new javax.swing.JLabel();
        jLabelController = new javax.swing.JLabel();
        jLabelWiFi = new javax.swing.JLabel();
        jLabelInternet = new javax.swing.JLabel();

        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);
        setMaximumSize(new java.awt.Dimension(480, 320));
        setMinimumSize(new java.awt.Dimension(480, 320));
        setName("frameMain"); // NOI18N
        setUndecorated(true);
        setResizable(false);
        setSize(new java.awt.Dimension(480, 320));
        setType(java.awt.Window.Type.UTILITY);
        getContentPane().setLayout(new org.netbeans.lib.awtextra.AbsoluteLayout());

        jPanelLeft.setAlignmentX(0.1F);
        jPanelLeft.setAlignmentY(0.1F);
        jPanelLeft.setMaximumSize(new java.awt.Dimension(70, 286));
        jPanelLeft.setMinimumSize(new java.awt.Dimension(70, 286));
        jPanelLeft.setPreferredSize(new java.awt.Dimension(70, 286));
        jPanelLeft.setLayout(new org.netbeans.lib.awtextra.AbsoluteLayout());

        jButtonPL1.setIcon(new javax.swing.ImageIcon(getClass().getResource("/jrivitscreen/images/no_warning.png"))); // NOI18N
        jButtonPL1.setAlignmentX(0.5F);
        jButtonPL1.setBorder(javax.swing.BorderFactory.createBevelBorder(javax.swing.border.BevelBorder.RAISED));
        jButtonPL1.setContentAreaFilled(false);
        jButtonPL1.setMaximumSize(new java.awt.Dimension(65, 65));
        jButtonPL1.setMinimumSize(new java.awt.Dimension(65, 65));
        jButtonPL1.setPreferredSize(new java.awt.Dimension(65, 65));
        jButtonPL1.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButtonPL1ActionPerformed(evt);
            }
        });
        jPanelLeft.add(jButtonPL1, new org.netbeans.lib.awtextra.AbsoluteConstraints(2, 6, -1, -1));

        jButtonPL2.setIcon(new javax.swing.ImageIcon(getClass().getResource("/jrivitscreen/images/info.png"))); // NOI18N
        jButtonPL2.setBorder(javax.swing.BorderFactory.createBevelBorder(javax.swing.border.BevelBorder.RAISED));
        jButtonPL2.setContentAreaFilled(false);
        jButtonPL2.setMargin(new java.awt.Insets(4, 14, 4, 14));
        jButtonPL2.setPreferredSize(new java.awt.Dimension(65, 65));
        jButtonPL2.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButtonPL2ActionPerformed(evt);
            }
        });
        jPanelLeft.add(jButtonPL2, new org.netbeans.lib.awtextra.AbsoluteConstraints(2, 108, -1, -1));

        jButtonPL3.setIcon(new javax.swing.ImageIcon(getClass().getResource("/jrivitscreen/images/setup.png"))); // NOI18N
        jButtonPL3.setAlignmentX(0.5F);
        jButtonPL3.setBorder(javax.swing.BorderFactory.createBevelBorder(javax.swing.border.BevelBorder.RAISED));
        jButtonPL3.setContentAreaFilled(false);
        jButtonPL3.setPreferredSize(new java.awt.Dimension(65, 65));
        jButtonPL3.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButtonPL3ActionPerformed(evt);
            }
        });
        jPanelLeft.add(jButtonPL3, new org.netbeans.lib.awtextra.AbsoluteConstraints(2, 210, -1, -1));

        getContentPane().add(jPanelLeft, new org.netbeans.lib.awtextra.AbsoluteConstraints(1, 1, 70, 285));

        jLayeredPaneCenter.setBackground(new java.awt.Color(0, 0, 255));
        jLayeredPaneCenter.setBorder(new javax.swing.border.LineBorder(new java.awt.Color(0, 0, 0), 1, true));
        jLayeredPaneCenter.setMaximumSize(new java.awt.Dimension(329, 277));
        jLayeredPaneCenter.setOpaque(true);
        jLayeredPaneCenter.setLayout(new org.netbeans.lib.awtextra.AbsoluteLayout());

        jPanelStarted.setBackground(new java.awt.Color(204, 204, 255));
        jPanelStarted.setBorder(javax.swing.BorderFactory.createBevelBorder(javax.swing.border.BevelBorder.RAISED));
        jPanelStarted.setMaximumSize(new java.awt.Dimension(245, 234));
        jPanelStarted.setMinimumSize(new java.awt.Dimension(245, 234));
        jPanelStarted.setName("started"); // NOI18N
        jPanelStarted.setPreferredSize(new java.awt.Dimension(338, 238));
        jPanelStarted.setLayout(new org.netbeans.lib.awtextra.AbsoluteLayout());

        jLabelContatoreLotti.setFont(new java.awt.Font("SansSerif", 0, 36)); // NOI18N
        jLabelContatoreLotti.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jLabelContatoreLotti.setText("0/0");
        jPanelStarted.add(jLabelContatoreLotti, new org.netbeans.lib.awtextra.AbsoluteConstraints(5, 138, 160, 30));

        jLabelNomeDevice.setFont(new java.awt.Font("SansSerif", 1, 20)); // NOI18N
        jLabelNomeDevice.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jLabelNomeDevice.setText("Nome Device");
        jPanelStarted.add(jLabelNomeDevice, new org.netbeans.lib.awtextra.AbsoluteConstraints(5, 5, 320, 30));

        jProgressBar.setFont(new java.awt.Font("Liberation Sans", 1, 14)); // NOI18N
        jProgressBar.setBorder(javax.swing.BorderFactory.createEtchedBorder());
        jProgressBar.setMaximumSize(new java.awt.Dimension(245, 40));
        jProgressBar.setMinimumSize(new java.awt.Dimension(245, 40));
        jProgressBar.setStringPainted(true);
        jPanelStarted.add(jProgressBar, new org.netbeans.lib.awtextra.AbsoluteConstraints(5, 173, 322, 40));

        jLabelErrati.setBackground(java.awt.Color.red);
        jLabelErrati.setFont(new java.awt.Font("SansSerif", 0, 24)); // NOI18N
        jLabelErrati.setForeground(new java.awt.Color(255, 255, 255));
        jLabelErrati.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jLabelErrati.setText("0");
        jLabelErrati.setBorder(new javax.swing.border.SoftBevelBorder(javax.swing.border.BevelBorder.RAISED));
        jLabelErrati.setOpaque(true);
        jPanelStarted.add(jLabelErrati, new org.netbeans.lib.awtextra.AbsoluteConstraints(232, 220, 90, 25));

        jLabelAnnullati.setBackground(new java.awt.Color(255, 204, 204));
        jLabelAnnullati.setFont(new java.awt.Font("SansSerif", 0, 24)); // NOI18N
        jLabelAnnullati.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jLabelAnnullati.setText("0");
        jLabelAnnullati.setBorder(new javax.swing.border.SoftBevelBorder(javax.swing.border.BevelBorder.RAISED));
        jLabelAnnullati.setOpaque(true);
        jPanelStarted.add(jLabelAnnullati, new org.netbeans.lib.awtextra.AbsoluteConstraints(119, 220, 90, 25));

        jLabel_Errati.setFont(new java.awt.Font("Liberation Sans", 1, 15)); // NOI18N
        jLabel_Errati.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jLabel_Errati.setText("Wrong");
        jLabel_Errati.setToolTipText("");
        jPanelStarted.add(jLabel_Errati, new org.netbeans.lib.awtextra.AbsoluteConstraints(232, 252, 90, -1));

        jLabel_Annullati.setFont(new java.awt.Font("Liberation Sans", 1, 15)); // NOI18N
        jLabel_Annullati.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jLabel_Annullati.setText("Canceled");
        jPanelStarted.add(jLabel_Annullati, new org.netbeans.lib.awtextra.AbsoluteConstraints(119, 252, 90, -1));

        jLabel_Validi.setFont(new java.awt.Font("Liberation Sans", 1, 15)); // NOI18N
        jLabel_Validi.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jLabel_Validi.setText("Valid");
        jPanelStarted.add(jLabel_Validi, new org.netbeans.lib.awtextra.AbsoluteConstraints(5, 252, 90, -1));

        jLabelValidi.setBackground(java.awt.Color.green);
        jLabelValidi.setFont(new java.awt.Font("SansSerif", 0, 24)); // NOI18N
        jLabelValidi.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jLabelValidi.setText("0");
        jLabelValidi.setBorder(new javax.swing.border.SoftBevelBorder(javax.swing.border.BevelBorder.RAISED));
        jLabelValidi.setOpaque(true);
        jPanelStarted.add(jLabelValidi, new org.netbeans.lib.awtextra.AbsoluteConstraints(5, 220, 90, 25));

        jLabelDesContatorePezzi.setFont(new java.awt.Font("SansSerif", 0, 24)); // NOI18N
        jLabelDesContatorePezzi.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jLabelDesContatorePezzi.setText("Piece");
        jLabelDesContatorePezzi.setToolTipText("");
        jPanelStarted.add(jLabelDesContatorePezzi, new org.netbeans.lib.awtextra.AbsoluteConstraints(160, 100, 160, 30));

        jLabelDesContatoreLotti.setFont(new java.awt.Font("SansSerif", 0, 24)); // NOI18N
        jLabelDesContatoreLotti.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jLabelDesContatoreLotti.setText("Lot");
        jLabelDesContatoreLotti.setToolTipText("");
        jPanelStarted.add(jLabelDesContatoreLotti, new org.netbeans.lib.awtextra.AbsoluteConstraints(5, 100, 160, 30));

        jLabelContatorePezzi.setFont(new java.awt.Font("SansSerif", 0, 36)); // NOI18N
        jLabelContatorePezzi.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jLabelContatorePezzi.setText("0/0");
        jPanelStarted.add(jLabelContatorePezzi, new org.netbeans.lib.awtextra.AbsoluteConstraints(160, 138, 160, 30));

        jLabelDesPezziNoLimits.setFont(new java.awt.Font("SansSerif", 0, 24)); // NOI18N
        jLabelDesPezziNoLimits.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jLabelDesPezziNoLimits.setText("Pieces");
        jLabelDesPezziNoLimits.setToolTipText("");
        jPanelStarted.add(jLabelDesPezziNoLimits, new org.netbeans.lib.awtextra.AbsoluteConstraints(84, 100, 160, 30));

        jLabelPezziNoLimits.setFont(new java.awt.Font("SansSerif", 0, 36)); // NOI18N
        jLabelPezziNoLimits.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jLabelPezziNoLimits.setText("0");
        jPanelStarted.add(jLabelPezziNoLimits, new org.netbeans.lib.awtextra.AbsoluteConstraints(10, 138, 310, 30));

        jLabelNomeLavoro.setFont(new java.awt.Font("SansSerif", 1, 20)); // NOI18N
        jLabelNomeLavoro.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jLabelNomeLavoro.setText("Nome Lavoro");
        jLabelNomeLavoro.setFocusable(false);
        jPanelStarted.add(jLabelNomeLavoro, new org.netbeans.lib.awtextra.AbsoluteConstraints(5, 65, 320, -1));

        jLabelNomeWL.setFont(new java.awt.Font("SansSerif", 1, 20)); // NOI18N
        jLabelNomeWL.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jLabelNomeWL.setText("Nome WL");
        jPanelStarted.add(jLabelNomeWL, new org.netbeans.lib.awtextra.AbsoluteConstraints(5, 35, 320, -1));

        jLayeredPaneCenter.add(jPanelStarted, new org.netbeans.lib.awtextra.AbsoluteConstraints(1, 1, 328, 276));

        jPanelSetup.setMaximumSize(new java.awt.Dimension(328, 276));
        jPanelSetup.setMinimumSize(new java.awt.Dimension(328, 276));
        jPanelSetup.setName("setup"); // NOI18N
        jPanelSetup.setLayout(new org.netbeans.lib.awtextra.AbsoluteLayout());

        listSetupNM.setFont(new java.awt.Font("Dialog", 1, 16)); // NOI18N
        listSetupNM.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                listSetupNMMouseClicked(evt);
            }
        });
        jPanelSetup.add(listSetupNM, new org.netbeans.lib.awtextra.AbsoluteConstraints(2, 2, 320, 270));

        jLayeredPaneCenter.add(jPanelSetup, new org.netbeans.lib.awtextra.AbsoluteConstraints(1, 1, 328, 276));

        jPanelMain.setBackground(new java.awt.Color(255, 255, 255));
        jPanelMain.setMaximumSize(new java.awt.Dimension(328, 276));
        jPanelMain.setMinimumSize(new java.awt.Dimension(328, 276));
        jPanelMain.setName("main"); // NOI18N
        jPanelMain.setPreferredSize(new java.awt.Dimension(328, 276));
        jPanelMain.setLayout(new org.netbeans.lib.awtextra.AbsoluteLayout());

        jLabelLogo.setBackground(new java.awt.Color(255, 255, 255));
        jLabelLogo.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jLabelLogo.setIcon(new javax.swing.ImageIcon(getClass().getResource("/jrivitscreen/images/logori2.png"))); // NOI18N
        jLabelLogo.setAlignmentY(0.0F);
        jLabelLogo.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        jLabelLogo.setMaximumSize(new java.awt.Dimension(250, 250));
        jLabelLogo.setMinimumSize(new java.awt.Dimension(250, 250));
        jLabelLogo.setPreferredSize(new java.awt.Dimension(250, 250));
        jPanelMain.add(jLabelLogo, new org.netbeans.lib.awtextra.AbsoluteConstraints(42, 23, 230, 220));
        jLabelLogo.getAccessibleContext().setAccessibleName("Pannello principale");

        jLabelDeviceName.setFont(new java.awt.Font("Ubuntu Light", 3, 18)); // NOI18N
        jLabelDeviceName.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jLabelDeviceName.setText("Device Name");
        jLabelDeviceName.setMaximumSize(new java.awt.Dimension(320, 30));
        jLabelDeviceName.setMinimumSize(new java.awt.Dimension(320, 30));
        jLabelDeviceName.setPreferredSize(new java.awt.Dimension(322, 32));
        jPanelMain.add(jLabelDeviceName, new org.netbeans.lib.awtextra.AbsoluteConstraints(2, 254, -1, 20));
        jLabelDeviceName.getAccessibleContext().setAccessibleName("DeviceName");
        jLabelDeviceName.getAccessibleContext().setAccessibleDescription("Nome del RivitControl");

        jLabelVersione.setFont(new java.awt.Font("Ubuntu Light", 1, 14)); // NOI18N
        jLabelVersione.setForeground(java.awt.Color.blue);
        jLabelVersione.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jLabelVersione.setText("Ver.");
        jLabelVersione.setHorizontalTextPosition(javax.swing.SwingConstants.LEFT);
        jLabelVersione.setMaximumSize(new java.awt.Dimension(320, 30));
        jLabelVersione.setMinimumSize(new java.awt.Dimension(320, 30));
        jLabelVersione.setPreferredSize(new java.awt.Dimension(322, 32));
        jPanelMain.add(jLabelVersione, new org.netbeans.lib.awtextra.AbsoluteConstraints(2, 0, -1, 20));

        jLayeredPaneCenter.add(jPanelMain, new org.netbeans.lib.awtextra.AbsoluteConstraints(1, 1, 328, 276));

        jPanelSetupLan.setMaximumSize(new java.awt.Dimension(328, 276));
        jPanelSetupLan.setMinimumSize(new java.awt.Dimension(328, 276));
        jPanelSetupLan.setName("setup lan"); // NOI18N
        jPanelSetupLan.setPreferredSize(new java.awt.Dimension(328, 276));
        jPanelSetupLan.setLayout(new org.netbeans.lib.awtextra.AbsoluteLayout());

        jTextAreaLan.setEditable(false);
        jTextAreaLan.setColumns(20);
        jTextAreaLan.setRows(5);
        jScrollPaneLan.setViewportView(jTextAreaLan);

        jPanelSetupLan.add(jScrollPaneLan, new org.netbeans.lib.awtextra.AbsoluteConstraints(0, 0, 320, 270));

        jLayeredPaneCenter.add(jPanelSetupLan, new org.netbeans.lib.awtextra.AbsoluteConstraints(1, 1, 328, 276));

        jPanelSetupWiFi.setMaximumSize(new java.awt.Dimension(328, 276));
        jPanelSetupWiFi.setMinimumSize(new java.awt.Dimension(328, 276));
        jPanelSetupWiFi.setName("setup wifi"); // NOI18N
        jPanelSetupWiFi.setPreferredSize(new java.awt.Dimension(328, 276));
        jPanelSetupWiFi.setRequestFocusEnabled(false);
        jPanelSetupWiFi.setLayout(new org.netbeans.lib.awtextra.AbsoluteLayout());

        jTextAreaWifi.setColumns(20);
        jTextAreaWifi.setRows(5);
        jScrollPaneWifi.setViewportView(jTextAreaWifi);

        jPanelSetupWiFi.add(jScrollPaneWifi, new org.netbeans.lib.awtextra.AbsoluteConstraints(0, 0, 320, 270));

        jLayeredPaneCenter.add(jPanelSetupWiFi, new org.netbeans.lib.awtextra.AbsoluteConstraints(1, 1, 328, 276));

        jPanelInfo.setMaximumSize(new java.awt.Dimension(328, 276));
        jPanelInfo.setMinimumSize(new java.awt.Dimension(328, 276));
        jPanelInfo.setName("info"); // NOI18N
        jPanelInfo.setPreferredSize(new java.awt.Dimension(328, 276));
        jPanelInfo.setLayout(new org.netbeans.lib.awtextra.AbsoluteLayout());

        listInfo.setBackground(new java.awt.Color(255, 255, 204));
        listInfo.setFont(new java.awt.Font("Dialog", 1, 18)); // NOI18N
        jPanelInfo.add(listInfo, new org.netbeans.lib.awtextra.AbsoluteConstraints(2, 2, 320, 270));
        listInfo.getAccessibleContext().setAccessibleName("Lista_info");
        listInfo.getAccessibleContext().setAccessibleDescription("Informazioni del sistema");

        jLayeredPaneCenter.add(jPanelInfo, new org.netbeans.lib.awtextra.AbsoluteConstraints(1, 1, 328, 276));

        jPanelWarning.setToolTipText("");
        jPanelWarning.setMaximumSize(new java.awt.Dimension(328, 276));
        jPanelWarning.setMinimumSize(new java.awt.Dimension(328, 276));
        jPanelWarning.setName("warning"); // NOI18N
        jPanelWarning.setOpaque(false);
        jPanelWarning.setPreferredSize(new java.awt.Dimension(328, 276));
        jPanelWarning.setLayout(new org.netbeans.lib.awtextra.AbsoluteLayout());

        listWarning.setBackground(new java.awt.Color(204, 255, 204));
        listWarning.setFont(new java.awt.Font("Dialog", 1, 18)); // NOI18N
        jPanelWarning.add(listWarning, new org.netbeans.lib.awtextra.AbsoluteConstraints(2, 2, 320, 270));

        jLayeredPaneCenter.add(jPanelWarning, new org.netbeans.lib.awtextra.AbsoluteConstraints(1, 1, 328, 276));

        jPanelStart.setAlignmentX(1.0F);
        jPanelStart.setAlignmentY(1.0F);
        jPanelStart.setMaximumSize(new java.awt.Dimension(328, 276));
        jPanelStart.setMinimumSize(new java.awt.Dimension(328, 276));
        jPanelStart.setName("start"); // NOI18N
        jPanelStart.setPreferredSize(new java.awt.Dimension(328, 276));
        jPanelStart.setLayout(new org.netbeans.lib.awtextra.AbsoluteLayout());

        listLavori.setFont(new java.awt.Font("Dialog", 1, 18)); // NOI18N
        listLavori.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                listLavoriMouseClicked(evt);
            }
        });
        listLavori.addItemListener(new java.awt.event.ItemListener() {
            public void itemStateChanged(java.awt.event.ItemEvent evt) {
                listLavoriItemStateChanged(evt);
            }
        });
        jPanelStart.add(listLavori, new org.netbeans.lib.awtextra.AbsoluteConstraints(2, 2, 326, 180));

        listWLavori.setFont(new java.awt.Font("Dialog", 1, 18)); // NOI18N
        listWLavori.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                listWLavoriMouseClicked(evt);
            }
        });
        listWLavori.addItemListener(new java.awt.event.ItemListener() {
            public void itemStateChanged(java.awt.event.ItemEvent evt) {
                listWLavoriItemStateChanged(evt);
            }
        });
        jPanelStart.add(listWLavori, new org.netbeans.lib.awtextra.AbsoluteConstraints(2, 2, 326, 180));

        JTextAreaDescrizioneLavoro.setEditable(false);
        JTextAreaDescrizioneLavoro.setFont(new java.awt.Font("Dialog", 0, 18)); // NOI18N
        JTextAreaDescrizioneLavoro.setLineWrap(true);
        JTextAreaDescrizioneLavoro.setRows(5);
        JTextAreaDescrizioneLavoro.setMaximumSize(new java.awt.Dimension(320, 80));
        JTextAreaDescrizioneLavoro.setMinimumSize(new java.awt.Dimension(320, 80));
        jPanelStart.add(JTextAreaDescrizioneLavoro, new org.netbeans.lib.awtextra.AbsoluteConstraints(2, 186, 328, 90));

        jLayeredPaneCenter.add(jPanelStart, new org.netbeans.lib.awtextra.AbsoluteConstraints(1, 1, 328, 276));

        jPanelDialog.setMaximumSize(new java.awt.Dimension(328, 276));
        jPanelDialog.setMinimumSize(new java.awt.Dimension(328, 276));
        jPanelDialog.setName("dialog"); // NOI18N
        jPanelDialog.setPreferredSize(new java.awt.Dimension(328, 276));
        jPanelDialog.setLayout(new org.netbeans.lib.awtextra.AbsoluteLayout());

        jLabelDialog.setFont(new java.awt.Font("DejaVu Sans Condensed", 1, 18)); // NOI18N
        jLabelDialog.setForeground(new java.awt.Color(0, 51, 204));
        jLabelDialog.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jLabelDialog.setText("Confirm cancel traction ?");
        jLabelDialog.setAutoscrolls(true);
        jLabelDialog.setFocusable(false);
        jLabelDialog.setMaximumSize(new java.awt.Dimension(177, 30));
        jLabelDialog.setMinimumSize(new java.awt.Dimension(177, 30));
        jLabelDialog.setPreferredSize(new java.awt.Dimension(177, 30));
        jPanelDialog.add(jLabelDialog, new org.netbeans.lib.awtextra.AbsoluteConstraints(10, 110, 300, 30));

        jLayeredPaneCenter.add(jPanelDialog, new org.netbeans.lib.awtextra.AbsoluteConstraints(1, 1, 328, 276));

        jPanelCalibrazione.setBackground(java.awt.Color.lightGray);
        jPanelCalibrazione.setBorder(javax.swing.BorderFactory.createBevelBorder(javax.swing.border.BevelBorder.RAISED));
        jPanelCalibrazione.setMaximumSize(new java.awt.Dimension(245, 234));
        jPanelCalibrazione.setMinimumSize(new java.awt.Dimension(245, 234));
        jPanelCalibrazione.setName("calibrazione"); // NOI18N
        jPanelCalibrazione.setPreferredSize(new java.awt.Dimension(338, 238));
        jPanelCalibrazione.setLayout(new org.netbeans.lib.awtextra.AbsoluteLayout());

        jLabelNomeLavoroCal.setFont(new java.awt.Font("DejaVu Sans Condensed", 1, 12)); // NOI18N
        jLabelNomeLavoroCal.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jLabelNomeLavoroCal.setText("Calibrazione");
        jPanelCalibrazione.add(jLabelNomeLavoroCal, new org.netbeans.lib.awtextra.AbsoluteConstraints(232, 220, 90, 13));

        jLabelAvvisoCalibrazione.setFont(new java.awt.Font("DejaVu Sans Condensed", 1, 18)); // NOI18N
        jLabelAvvisoCalibrazione.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jLabelAvvisoCalibrazione.setText("Nome Lavoro");
        jPanelCalibrazione.add(jLabelAvvisoCalibrazione, new org.netbeans.lib.awtextra.AbsoluteConstraints(5, 45, 315, 30));

        jLayeredPaneCenter.add(jPanelCalibrazione, new org.netbeans.lib.awtextra.AbsoluteConstraints(1, 1, 328, 276));

        getContentPane().add(jLayeredPaneCenter, new org.netbeans.lib.awtextra.AbsoluteConstraints(75, 2, 329, 277));

        jPanelRight.setAlignmentX(0.0F);
        jPanelRight.setAlignmentY(0.0F);
        jPanelRight.setMaximumSize(new java.awt.Dimension(70, 286));
        jPanelRight.setMinimumSize(new java.awt.Dimension(70, 286));
        jPanelRight.setPreferredSize(new java.awt.Dimension(70, 286));
        jPanelRight.setLayout(new org.netbeans.lib.awtextra.AbsoluteLayout());

        jButtonPR1.setIcon(new javax.swing.ImageIcon(getClass().getResource("/jrivitscreen/images/play_circle.png"))); // NOI18N
        jButtonPR1.setBorder(javax.swing.BorderFactory.createBevelBorder(javax.swing.border.BevelBorder.RAISED));
        jButtonPR1.setContentAreaFilled(false);
        jButtonPR1.setMaximumSize(new java.awt.Dimension(67, 67));
        jButtonPR1.setMinimumSize(new java.awt.Dimension(67, 67));
        jButtonPR1.setPreferredSize(new java.awt.Dimension(65, 65));
        jButtonPR1.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButtonPR1ActionPerformed(evt);
            }
        });
        jPanelRight.add(jButtonPR1, new org.netbeans.lib.awtextra.AbsoluteConstraints(5, 6, -1, -1));

        jButtonPR2.setBorder(javax.swing.BorderFactory.createBevelBorder(javax.swing.border.BevelBorder.RAISED));
        jButtonPR2.setContentAreaFilled(false);
        jButtonPR2.setMaximumSize(new java.awt.Dimension(67, 67));
        jButtonPR2.setMinimumSize(new java.awt.Dimension(67, 67));
        jButtonPR2.setPreferredSize(new java.awt.Dimension(65, 65));
        jButtonPR2.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButtonPR2ActionPerformed(evt);
            }
        });
        jPanelRight.add(jButtonPR2, new org.netbeans.lib.awtextra.AbsoluteConstraints(5, 108, -1, -1));

        jButtonPR3.setIcon(new javax.swing.ImageIcon(getClass().getResource("/jrivitscreen/images/nulla.png"))); // NOI18N
        jButtonPR3.setBorder(javax.swing.BorderFactory.createBevelBorder(javax.swing.border.BevelBorder.RAISED));
        jButtonPR3.setContentAreaFilled(false);
        jButtonPR3.setMaximumSize(new java.awt.Dimension(67, 67));
        jButtonPR3.setMinimumSize(new java.awt.Dimension(67, 67));
        jButtonPR3.setPreferredSize(new java.awt.Dimension(65, 65));
        jButtonPR3.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButtonPR3ActionPerformed(evt);
            }
        });
        jPanelRight.add(jButtonPR3, new org.netbeans.lib.awtextra.AbsoluteConstraints(5, 210, -1, -1));

        getContentPane().add(jPanelRight, new org.netbeans.lib.awtextra.AbsoluteConstraints(406, 0, 70, 286));

        jPanelBotton.setBackground(new java.awt.Color(0, 0, 0));
        jPanelBotton.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(0, 0, 0)));
        jPanelBotton.setForeground(java.awt.Color.green);
        jPanelBotton.setMaximumSize(new java.awt.Dimension(400, 20));
        jPanelBotton.setMinimumSize(new java.awt.Dimension(470, 20));
        jPanelBotton.setPreferredSize(new java.awt.Dimension(480, 22));
        jPanelBotton.setLayout(new org.netbeans.lib.awtextra.AbsoluteLayout());

        jLabel_B_L.setBackground(java.awt.Color.lightGray);
        jLabel_B_L.setFont(new java.awt.Font("Segoe UI", 1, 10)); // NOI18N
        jLabel_B_L.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jLabel_B_L.setText("00:00");
        jLabel_B_L.setOpaque(true);
        jPanelBotton.add(jLabel_B_L, new org.netbeans.lib.awtextra.AbsoluteConstraints(108, 5, 80, 20));

        jLabel_B_R.setBackground(java.awt.Color.lightGray);
        jLabel_B_R.setFont(new java.awt.Font("DejaVu Sans", 1, 14)); // NOI18N
        jLabel_B_R.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jLabel_B_R.setText("Aria Off");
        jLabel_B_R.setOpaque(true);
        jPanelBotton.add(jLabel_B_R, new org.netbeans.lib.awtextra.AbsoluteConstraints(407, 5, 70, 20));
        jLabel_B_R.getAccessibleContext().setAccessibleDescription("Indicatore dello stato dell'aria");

        jLabel_msg.setFont(new java.awt.Font("DejaVu Sans", 1, 14)); // NOI18N
        jLabel_msg.setForeground(javax.swing.UIManager.getDefaults().getColor("Actions.Green"));
        jLabel_msg.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jLabel_msg.setText("message");
        jLabel_msg.setAlignmentX(0.2F);
        jLabel_msg.setOpaque(true);
        jPanelBotton.add(jLabel_msg, new org.netbeans.lib.awtextra.AbsoluteConstraints(190, 5, 187, 20));

        jLabelWarning.setFont(new java.awt.Font("DejaVu Sans", 1, 13)); // NOI18N
        jLabelWarning.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jLabelWarning.setText("OK");
        jLabelWarning.setToolTipText("");
        jLabelWarning.setOpaque(true);
        jLabelWarning.setPreferredSize(new java.awt.Dimension(15, 20));
        jLabelWarning.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                jLabelWarningMouseClicked(evt);
            }
        });
        jPanelBotton.add(jLabelWarning, new org.netbeans.lib.awtextra.AbsoluteConstraints(379, 5, 25, 20));
        jLabelWarning.getAccessibleContext().setAccessibleName("jLabelWarning");

        jLabelLan.setBackground(javax.swing.UIManager.getDefaults().getColor("Actions.Red"));
        jLabelLan.setFont(new java.awt.Font("DejaVu Sans", 1, 13)); // NOI18N
        jLabelLan.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jLabelLan.setText("L");
        jLabelLan.setToolTipText("");
        jLabelLan.setOpaque(true);
        jLabelLan.setPreferredSize(new java.awt.Dimension(15, 20));
        jPanelBotton.add(jLabelLan, new org.netbeans.lib.awtextra.AbsoluteConstraints(2, 5, 20, 20));

        jLabelVPN.setBackground(javax.swing.UIManager.getDefaults().getColor("Actions.Red"));
        jLabelVPN.setFont(new java.awt.Font("DejaVu Sans", 1, 13)); // NOI18N
        jLabelVPN.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jLabelVPN.setText("V");
        jLabelVPN.setToolTipText("");
        jLabelVPN.setOpaque(true);
        jLabelVPN.setPreferredSize(new java.awt.Dimension(15, 20));
        jPanelBotton.add(jLabelVPN, new org.netbeans.lib.awtextra.AbsoluteConstraints(44, 5, 20, 20));

        jLabelController.setBackground(javax.swing.UIManager.getDefaults().getColor("Actions.Red"));
        jLabelController.setFont(new java.awt.Font("DejaVu Sans", 1, 13)); // NOI18N
        jLabelController.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jLabelController.setText("C");
        jLabelController.setToolTipText("");
        jLabelController.setOpaque(true);
        jLabelController.setPreferredSize(new java.awt.Dimension(15, 20));
        jPanelBotton.add(jLabelController, new org.netbeans.lib.awtextra.AbsoluteConstraints(65, 5, 20, 20));

        jLabelWiFi.setBackground(javax.swing.UIManager.getDefaults().getColor("Actions.Red"));
        jLabelWiFi.setFont(new java.awt.Font("DejaVu Sans", 1, 13)); // NOI18N
        jLabelWiFi.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jLabelWiFi.setIcon(new javax.swing.ImageIcon(getClass().getResource("/jrivitscreen/images/wifi_20.png"))); // NOI18N
        jLabelWiFi.setToolTipText("");
        jLabelWiFi.setAlignmentY(0.0F);
        jLabelWiFi.setOpaque(true);
        jLabelWiFi.setPreferredSize(new java.awt.Dimension(20, 20));
        jPanelBotton.add(jLabelWiFi, new org.netbeans.lib.awtextra.AbsoluteConstraints(86, 5, 20, 20));

        jLabelInternet.setBackground(javax.swing.UIManager.getDefaults().getColor("Actions.Red"));
        jLabelInternet.setFont(new java.awt.Font("DejaVu Sans", 1, 13)); // NOI18N
        jLabelInternet.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jLabelInternet.setText("I");
        jLabelInternet.setToolTipText("");
        jLabelInternet.setOpaque(true);
        jLabelInternet.setPreferredSize(new java.awt.Dimension(15, 20));
        jPanelBotton.add(jLabelInternet, new org.netbeans.lib.awtextra.AbsoluteConstraints(23, 5, 20, 20));

        getContentPane().add(jPanelBotton, new org.netbeans.lib.awtextra.AbsoluteConstraints(0, 290, 480, 30));

        pack();
    }// </editor-fold>//GEN-END:initComponents
/**
     * Imposta l'immagine in base al tipo di warning 0 nessuna segnalazione 1 a
     * 5 media segnalazione 6 a 10 errore Il file warning è così composta da due
     * campi: il primo è la descrizione, il secondo il livello di gravità della
     * segnalazione separati dal simbolo §
     *
     */
    public void set_warning() {

        switch (this.w_level) {
            case 0 -> {
                this.Img_Warning = this.Img_No_Warning;
                this.listWarning.setBackground(Color.GREEN);
                this.listWarning.setForeground(Color.BLACK);
            }
            case 1, 2, 3, 4 -> {
                this.Img_Warning = this.Img_Med_Warning;
                this.listWarning.setBackground(Color.YELLOW);
                this.listWarning.setForeground(Color.BLACK);
            }
            case 5, 6, 7, 8, 9 -> {
                this.Img_Warning = this.Img_Err_Warning;
                this.listWarning.setBackground(Color.RED);
                this.listWarning.setForeground(Color.WHITE);
            }

        }
    }

    /**
     * Evento click Pulsante 1 in alto a dx
     *
     * @param evt
     */
    private void jButtonPR1ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButtonPR1ActionPerformed
        // Pulsante R1  - Passare a ?
        // Considerare la variabile Basic nel DB se si deve andare in start o started
        // Qual'è il nome del pannello in primo piano ?
        switch (this.panCur) {
            case "main" -> {
                // todo Gestire l'impostazione del lavoro in pausa da far ripartire
                this.inWl = false;
                this.listLavori.setVisible(true);
                this.listWLavori.setVisible(false);
                if (this.inPausa.equals("1")) {
//                   List<String> elencoTxt =  new ArrayList<>();
                    int quanti = this.listLavori.getItemCount();
                    int i;
                    for (i = 0; i < quanti; i++) {
                        if (this.elencoLavori.get(i)[0].equals(this.lavoroScelto)) {
                            break;
                        }
                    }
                    this.listLavori.select(i);
                    this.JTextAreaDescrizioneLavoro.setText(this.elencoDesLavoro.get(i));
                    // todo Gestire il caso in cui il lavoro in pausa non viene trovato
                    PanelStarted();
                } else {
                    //this.JTextAreaDescrizioneLavoro.setText(this.elencoDesLavoro.get(0));
                    PanelStart();
                }
            }

            case "start" -> {
                PulsanteSu();
            }
            case "started" -> {//Stop
                //esiste conferma_no come file in /tmp/CT ?
                // se esiste non chiede conferma della scelta
                if (this.statoConcluso) {
                    PanelStart();
                } else {
                    if (this.isChiediConfermaStop()) {
                        this.setContesto(this.panCur);
                        richiesta = Static.STATO_STOP;
                        this.AlertDialogWhat = "Confirm stop work ?";
                        this.jLabelDialog.setText(AlertDialogWhat);
                        PanelDialog();
                    } else {
                        try {
                            //passa direttamente ad annullare lavoro
                            this.esegui("stop");
                        } catch (Exception ex) {
                            Logger.getLogger(JRivitMain.class.getName()).log(Level.SEVERE, null, ex);
                        }
                    }
                }

            }
            case "canvas" -> {
                switch (this.stato) {
                    case Static.STATO_CALIBRAZIONE:
                        this.AlertDialogWhat = "Proceed with calibration test ?";
                        this.jLabelDialog.setText(AlertDialogWhat);
                        richiesta = Static.RICHIESTA_CALIBRAZIONE_TEST;
                        PanelDialog();
                        break;
                    case Static.STATO_CALIBRAZIONE_TEST:
                        //Salvare la calibrazione ?
                        this.AlertDialogWhat = "Confirm calibration rewrite ?";
                        this.jLabelDialog.setText(AlertDialogWhat);
                        richiesta = Static.RICHIESTA_CALIBRAZIONE_SALVA;
                        PanelDialog();
                        break;
                    default:
                        if (this.statoConcluso) {
                            PanelStart();
                        } else {
                            if (this.isChiediConfermaStop()) {
                                richiesta = Static.STATO_STOP;
                                this.AlertDialogWhat = "Confirm stop work ?";
                                this.jLabelDialog.setText(AlertDialogWhat);
                                PanelDialog();
                            } else {
                                try {
                                    //passa direttamente ad annullare lavoro
                                    this.esegui("stop");
                                } catch (Exception ex) {
                                    Logger.getLogger(JRivitMain.class.getName()).log(Level.SEVERE, null, ex);
                                }
                            }
                        }
                }
            }

            case "warning", "info", "setup lan", "setup wifi", "setup" ->
                PulsanteSu();
            case "dialog" -> {
                try {
                    //Pulsante Sì alla domanda ? Annulla ? Abort ?
                    //passa direttamente ad annullare lavoro
                    switch (this.richiesta) {

                        case Static.ANNULLA -> {
                            this.esegui("annulla");
                        }
                        case Static.CONTINUA -> {
                            this.esegui("continua");
                        }
                        case Static.ACCETTA -> {
                            this.esegui("accetta");
                        }
                        case Static.STATO_STOP -> {
                            this.esegui("stop");
                        }
                        case Static.STATO_PAUSA -> {
                            this.esegui("pausa");
                        }
                        case Static.RICHIESTA_CALIBRAZIONE -> {
                            this.esegui("calibrazione");
                        }
                        case Static.RICHIESTA_CALIBRAZIONE_TEST -> {
                            this.esegui("calibrazione_test");
                            this.set_jLabel_B_L("Test");
                        }
                        case Static.RICHIESTA_CALIBRAZIONE_SALVA -> {//Ok registra calibrazione
                            esegui("salva_calibrazione");
                        }
                    }

                } catch (Exception ex) {
                    Logger.getLogger(JRivitMain.class.getName()).log(Level.SEVERE, null, ex);
                }
            }

        }
    }//GEN-LAST:event_jButtonPR1ActionPerformed

    public void setAlertDialogAnnulla(String AlertDialogAnnulla) {
        this.AlertDialogAnnulla = AlertDialogAnnulla;
    }

    public void setAlertDialogWhat(String AlertDialogWhat) {
        this.AlertDialogWhat = AlertDialogWhat;
        this.jLabelDialog.setText(AlertDialogWhat);
        PanelDialog();
    }

    /**
     * 1^ Pulsante Sinistro
     *
     * @param evt
     */
    private void jButtonPL1ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButtonPL1ActionPerformed
        // Qual'è il nome del pannello in primo piano ?
        switch (this.panCur) {
            case "main" -> {
                PanelWarning();
                this.jLabel_B_L.setText("Warning");
            }
            case "start" -> {
                PanelMain();//Exit verso main
            }
            case "started" ->//Continua
            {
                this.setContesto(this.panCur);
                richiesta = Static.CONTINUA;
                rispostaErrore();

            }
            case "canvas" -> {
                richiesta = Static.CONTINUA;
                rispostaErrore();
                //PanelStarted(); Rimane in cavans 
            }
            case "setup", "info" -> {
                PanelMain();
            }
            case "warning" -> {
                switch (this.pannelloPrecedente) {
                    case "main" ->
                        PanelMain();
                    case "start" ->
                        PanelStart();
                    case "started" ->
                        PanelStarted();
                    default ->
                        PanelMain();
                }
            }
            case "setup lan", "setup wifi" -> {
                PanelSetup();
            }

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
        switch (this.panCur) {
            case "main" -> {
                PanelInfo();
                this.jLabel_B_L.setText("Info");
            }
            case "started" ->//Accetta il tiro
            {
                this.setContesto(this.panCur);
                richiesta = Static.ACCETTA;
                rispostaErrore();
            }
            case "canvas" -> {
                if (!getStato().equals(Static.STATO_CALIBRAZIONE)) {
                    richiesta = Static.ACCETTA;
                    rispostaErrore();
                }
            }
            case "setup lan", "setup wifi" ->
                this.PulsanteSxDx(-1);//Sinistra
            case "setup" -> {
                this.esegui("aggiorna_stato_lan");
                PanelSetupLan();
            }
            case "dialog" -> {
                if (this.contesto.contains("started")) {
                    PanelStarted();
                }
            }
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
        switch (this.panCur) {
            case "main" -> {
                PanelSetup();
            }
            case "setup lan", "setup wifi" ->
                this.PulsanteSxDx(1);//Destra
            case "setup" -> {
                this.esegui("aggiorna_stato_wifi");
                PanelSetupWifi();
            }
            case "start" -> {
                this.scegliLavoro();
                this.AlertDialogWhat = "Enter re-calibration mode ?";
                this.richiesta = Static.RICHIESTA_CALIBRAZIONE;
                this.jLabelDialog.setText(AlertDialogWhat);
                PanelDialog();
            }
            case "started" -> {//Annullare il tiro
                this.setContesto(this.panCur);
                this.richiesta = Static.ANNULLA;
                rispostaErrore();
            }
            case "canvas" -> {
                if (!getStato().equals(Static.STATO_CALIBRAZIONE)) {
                    this.richiesta = Static.ANNULLA;
                    rispostaErrore();
                }
            }

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
        switch (this.panCur) {
            case "main" -> { //fare tutta WebControl
                this.inWl = true;
                this.set_jLabel_B_L("WL");
                this.listLavori.setVisible(false);
                this.listWLavori.setVisible(true);
                this.JTextAreaDescrizioneLavoro.setText(this.elencoDesWl.get(0).toString());
                PanelStart();
            }
            case "start" ->
                PulsanteGiu();

            case "warning", "info", "setup lan", "setup wifi" ->
                PulsanteGiu();
            case "started", "canvas" -> {//Reload Lavoro appena concluso esci da calibrazione

                if (this.stato.equals(Static.STATO_CALIBRAZIONE) || this.stato.equals(Static.STATO_CALIBRAZIONE_TEST)) {
                    //Uscita dalla Calibrazione
                    this.esegui("stop");
                    PanelStart();
                } else {
                    if (this.statoConcluso) {
                        avviaLavoro();
                    } else {
                        if (this.isChiediConfermaStop()) {
//                    DialogQ = STATO_PAUSA;
                            this.setContesto(this.panCur);
                            richiesta = Static.STATO_PAUSA;
                            this.AlertDialogWhat = "Confirm pause work ?";
                            this.jLabelDialog.setText(AlertDialogWhat);
                            PanelDialog();
                        } else {
                            //passa direttamente ad annullare lavoro
                            this.esegui("pausa");
                        }
                    }
                }

            }
            case "setup" -> {
                PulsanteGiu();
            }
            case "dialog" -> {//Scelta no alla domanda, ritornare al pannello started, No Salva waveform, No test
                switch (this.richiesta) {
                    case Static.RICHIESTA_CALIBRAZIONE_TEST -> {//Ritorna in calibrazione
                        this.avviaCalibrazione();
                    }
                    case Static.RICHIESTA_CALIBRAZIONE -> {//Ritorna in scelta lavoro
                        PanelStart();
                    }
                    case Static.RICHIESTA_CALIBRAZIONE_SALVA -> {//Ritorna in scelta lavoro
                        PanelStart();
                    }
                    default -> {
                        PanelStarted();
                    }
                }//End Switch stato
            }//End Switch "dialog
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
        switch (this.panCur) {
            case "main" ->
                this.exit();
//                per ora uso il pulsante per chiudere;
            case "start" -> {
                if (this.inWl) {
                    scegliWL();
                    avviaWL();
                } else {
                    scegliLavoro();
                    avviaLavoro();
                }

            }
            case "started" -> {
                this.PanelCanvas();
            }
            case "canvas" -> {
                PanelStarted();
            }
            case "setup" -> {
                this.esegui("on_of_nm_device");
                this.set_jLabel_B_L("CON..");
            }
            case "setup lan", "setup wifi" -> {
                PanelSetup();
            }
        }
    }//GEN-LAST:event_jButtonPR3ActionPerformed

    /**
     * Gestione della risposta scelta dall'utente per gestire l'errore
     */
    private void rispostaErrore() {
        String rispostaErrore = "";
        switch (this.richiesta) {
            case Static.CONTINUA ->
                rispostaErrore = "continua";
            case Static.ANNULLA ->
                rispostaErrore = "annulla";
            case Static.ACCETTA ->
                rispostaErrore = "accetta";
        }

        if (isChiediConferma()) {
            this.AlertDialogWhat = rispostaErrore + " ?";
            this.jLabelDialog.setText(AlertDialogWhat);
            PanelDialog();
        } else {
            try {
                this.esegui(rispostaErrore);
            } catch (Exception ex) {
                Logger.getLogger(JRivitMain.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
    }
    private void listLavoriMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_listLavoriMouseClicked
        this.JTextAreaDescrizioneLavoro.setText(
                this.elencoDesLavoro.get(this.listLavori.getSelectedIndex()));
        if (evt.getClickCount() == 2) { // doppio click -> avvio lavoro
            scegliLavoro();
            avviaLavoro();
        }
    }//GEN-LAST:event_listLavoriMouseClicked

    private void jLabelWarningMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_jLabelWarningMouseClicked
        if (pannelloPrecedente.equals("warning")) { // Se sono già nel pannello warning, allora esco regolarmente
            this.jButtonPL1ActionPerformed(null);
        } else {
            PanelWarning();
        }
    }//GEN-LAST:event_jLabelWarningMouseClicked

    private void listSetupNMMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_listSetupNMMouseClicked
        this.jButtonPR3.setIcon(this.setIconSetup());//aggiorna il tipo di Icona per il pulsante
        if (evt.getClickCount() == 2) { // doppio click -> cambia stato
            this.esegui("on_of_nm_device");
            this.set_jLabel_B_L("CON..");
        }
    }//GEN-LAST:event_listSetupNMMouseClicked

    private void listLavoriItemStateChanged(java.awt.event.ItemEvent evt) {//GEN-FIRST:event_listLavoriItemStateChanged
        java.awt.List l = (java.awt.List) evt.getSource();
        int elementoSelezionato = l.getSelectedIndexes()[0];
        this.JTextAreaDescrizioneLavoro.setText(
                this.elencoDesLavoro.get(elementoSelezionato));
        if (this.elencoLavoriCompleto.get(elementoSelezionato)[4].equals("0")) {
            this.JTextAreaDescrizioneLavoro.setBackground(Color.yellow);
            this.jButtonPR3.setIcon(this.Img_Nulla);//aggiorna il tipo di Icona per il pulsante
            this.jButtonPR3.setEnabled(false);
        } else {
            this.JTextAreaDescrizioneLavoro.setBackground(Color.white);
            this.jButtonPR3.setIcon(this.Img_Ok);//aggiorna il tipo di Icona per il pulsante
            this.jButtonPR3.setEnabled(true);
        }
    }//GEN-LAST:event_listLavoriItemStateChanged

    private void listWLavoriMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_listWLavoriMouseClicked
        this.JTextAreaDescrizioneLavoro.setText(
                this.elencoDesWl.get(this.listWLavori.getSelectedIndex()).toString());
        if (evt.getClickCount() == 2) { // doppio click -> avvio work list
            scegliWL();
            avviaWL();
        }
    }//GEN-LAST:event_listWLavoriMouseClicked

    private void listWLavoriItemStateChanged(java.awt.event.ItemEvent evt) {//GEN-FIRST:event_listWLavoriItemStateChanged
        java.awt.List l = (java.awt.List) evt.getSource();
        int elementoSelezionato = l.getSelectedIndexes()[0];
        /*
        this.JTextAreaDescrizioneLavoro.setText(
                this.elencoDesLavoro.get(elementoSelezionato));
        if (this.elencoLavoriCompleto.get(elementoSelezionato)[4].equals("0")) {
            this.JTextAreaDescrizioneLavoro.setBackground(Color.yellow);
            this.jButtonPR3.setIcon(this.Img_Nulla);//aggiorna il tipo di Icona per il pulsante
            this.jButtonPR3.setEnabled(false);
        } else {
            this.JTextAreaDescrizioneLavoro.setBackground(Color.white);
            this.jButtonPR3.setIcon(this.Img_Ok);//aggiorna il tipo di Icona per il pulsante
            this.jButtonPR3.setEnabled(true);
        }
         */
    }//GEN-LAST:event_listWLavoriItemStateChanged

    /**
     * PanelMain Pannello che viene visualizzato all'avvio
     */
    public void PanelMain() {
        this.changeButtons(this.Img_Warning, this.Img_Info, this.Img_Setup,
                this.Img_W, this.Img_WL, this.Img_Exit);
        cambiaPannello(this.jPanelMain);
    }

    /**
     * Setup dei pulsanti
     *
     * @param I1 immagine per il pulsante L1
     * @param I2 immagine per il pulsante L2
     * @param I3 immagine per il pulsante L3
     * @param I4 immagine per il pulsante R1
     * @param I5 immagine per il pulsante R2
     * @param I6 immagine per il pulsante R3
     */
    public void changeButtons(ImageIcon I1, ImageIcon I2, ImageIcon I3,
            ImageIcon I4, ImageIcon I5, ImageIcon I6) {
        this.jButtonPL1.setIcon(I1);
        this.jButtonPL1.setEnabled((!I1.equals(this.Img_Nulla)));

        this.jButtonPL2.setIcon(I2);
        this.jButtonPL2.setEnabled((!I2.equals(this.Img_Nulla)));

        this.jButtonPL3.setIcon(I3);
        this.jButtonPL3.setEnabled((!I3.equals(this.Img_Nulla)));

        this.jButtonPR1.setIcon(I4);
        this.jButtonPR1.setEnabled((!I4.equals(this.Img_Nulla)));

        this.jButtonPR2.setIcon(I5);
        this.jButtonPR2.setEnabled((!I5.equals(this.Img_Nulla)));

        this.jButtonPR3.setIcon(I6);
        this.jButtonPR3.setEnabled((!I6.equals(this.Img_Nulla)));
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
     * @param args the command line arguments
     */
    public static void main(String args[]) {
        /* Set the Nimbus look and feel */
        //<editor-fold defaultstate="collapsed" desc=" Look and feel setting code (optional) ">
        /* If Nimbus (introduced in Java SE 6) is not available, stay with the default look and feel.
         * For details see http://download.oracle.com/javase/tutorial/uiswing/lookandfeel/plaf.html 
         */
        CommandLine cmd = null;
        Options opzioni = new Options();
        Option pathW = new Option("pw", "pathWork", true, "Work path");
        pathW.setRequired(true);
        opzioni.addOption(pathW);
        Option pathL = new Option("pl", "pathLock", true, "Lock path");
        pathL.setRequired(false);
        opzioni.addOption(pathL);

        CommandLineParser parser = new DefaultParser();
        HelpFormatter formatter = new HelpFormatter();
        try {
            cmd = parser.parse(opzioni, args);
            if (cmd.hasOption("pw")) {
                Static.setPATH_WATCH(cmd.getOptionValue("pathWork"));
                Static.debug("Impostato Path per Work " + Static.PATH_WATCH, 2);

            }
            if (cmd.hasOption("pl")) {
                Static.setPATH_LCK(cmd.getOptionValue("pathLock"));
                Static.debug("Impostato Path per Lock " + Static.PATH_LCK, 2);
            }
        } catch (ParseException e) {
            System.out.println(e.getMessage());
            formatter.printHelp("utility-name", opzioni);
            System.exit(1);
        }

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
    private javax.swing.JButton jButtonPL1;
    private javax.swing.JButton jButtonPL2;
    private javax.swing.JButton jButtonPL3;
    private javax.swing.JButton jButtonPR1;
    private javax.swing.JButton jButtonPR2;
    private javax.swing.JButton jButtonPR3;
    private javax.swing.JLabel jLabelAnnullati;
    private javax.swing.JLabel jLabelAvvisoCalibrazione;
    private javax.swing.JLabel jLabelContatoreLotti;
    private javax.swing.JLabel jLabelContatorePezzi;
    private javax.swing.JLabel jLabelController;
    private javax.swing.JLabel jLabelDesContatoreLotti;
    private javax.swing.JLabel jLabelDesContatorePezzi;
    private javax.swing.JLabel jLabelDesPezziNoLimits;
    private javax.swing.JLabel jLabelDeviceName;
    private javax.swing.JLabel jLabelDialog;
    private javax.swing.JLabel jLabelErrati;
    private javax.swing.JLabel jLabelInternet;
    private javax.swing.JLabel jLabelLan;
    private javax.swing.JLabel jLabelLogo;
    private javax.swing.JLabel jLabelNomeDevice;
    private javax.swing.JLabel jLabelNomeLavoro;
    private javax.swing.JLabel jLabelNomeLavoroCal;
    private javax.swing.JLabel jLabelNomeWL;
    private javax.swing.JLabel jLabelPezziNoLimits;
    private javax.swing.JLabel jLabelVPN;
    private javax.swing.JLabel jLabelValidi;
    private javax.swing.JLabel jLabelVersione;
    private javax.swing.JLabel jLabelWarning;
    private javax.swing.JLabel jLabelWiFi;
    private javax.swing.JLabel jLabel_Annullati;
    private javax.swing.JLabel jLabel_B_L;
    private javax.swing.JLabel jLabel_B_R;
    private javax.swing.JLabel jLabel_Errati;
    private javax.swing.JLabel jLabel_Validi;
    private javax.swing.JLabel jLabel_msg;
    public javax.swing.JLayeredPane jLayeredPaneCenter;
    private javax.swing.JPanel jPanelBotton;
    private javax.swing.JPanel jPanelCalibrazione;
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
    private javax.swing.JScrollPane jScrollPaneLan;
    private javax.swing.JScrollPane jScrollPaneWifi;
    private javax.swing.JTextArea jTextAreaLan;
    private javax.swing.JTextArea jTextAreaWifi;
    private java.awt.List listInfo;
    private java.awt.List listLavori;
    private java.awt.List listSetupNM;
    private java.awt.List listWLavori;
    private java.awt.List listWarning;
    // End of variables declaration//GEN-END:variables

    /**
     * Show PanelStart da questo pannello si fa la scelta del lavoro dalla lista
     * creata da JControl nel file lavori.txt
     */
    public void PanelStart() {
        int selezionato = 0, i = 0;
        java.awt.List lista;
        if (this.inWl) {
            lista = this.listWLavori;
        } else {
            lista = this.listLavori;
        }
        if (this.abilitaCalibrazione) {
            this.changeButtons(this.Img_Exit, this.Img_Nulla, this.Img_Calibrazione,
                    this.Img_Freccia_su, this.Img_Freccia_giu, this.Img_Ok);
        } else {
            this.changeButtons(this.Img_Exit, this.Img_Nulla, this.Img_Nulla,
                    this.Img_Freccia_su, this.Img_Freccia_giu, this.Img_Ok);
        }

        // Se sessione non contiene 0
        // vuole dire che da una pausa si vuole riprendere un lavoro
        if (this.panCur != "start") {
            if (sessione == null) {
                sessione = "0";
            }
            switch (sessione) {
                case "0" -> {
                    selezionato = lista.getSelectedIndex();
                }
                default -> {
                    String[] items = lista.getSelectedItems();
                    for (i = 0; i < items.length; i++) {
                        if (items[i].startsWith(sessione + ",")) {
                            break;
                        }
                    }
                    selezionato = i;
                }
            }
            if (selezionato == -1) {
                selezionato = 1;
            }
            lista.select(selezionato);
            this.setButtonDesc(selezionato);
            cambiaPannello(this.jPanelStart);
        }
    }

    
  
    /**
     * Pannello dopo aver fatto la scelta del Lavoro, tale scelta deve essere
     * scritta nel file /tmp/CT/w_scelto L'App JControl sollecitato dall'evento
     * modifica w_scelto o creazione del file, aggiorna il DB Aggiornato metodo
     * per mostrare un colore diverso se è in errore e lavoro con ultimo tiro,
     * Concluso
     */
    public void PanelStarted() {
        this.aggiornaContatori();

        if (this.statoConcluso) {
            this.jPanelStarted.setBackground(Color.BLUE);
            this.changeButtons(this.Img_Nulla, this.Img_Nulla, this.Img_Nulla,
                    this.Img_Exit, this.Img_reloadWork, this.Img_Grafico);
        }
        if (this.inErrore) {
            this.jPanelStarted.setBackground(Color.RED);
            this.changeButtons(this.Img_Continua, this.Img_Ok, this.Img_Annulla,
                    this.Img_Stop, this.Img_Pause, this.Img_Grafico);
        }
        if (!this.inErrore && !this.statoConcluso) {
            this.jPanelStarted.setBackground(Color.WHITE);
            this.changeButtons(this.Img_Nulla, this.Img_Nulla, this.Img_Nulla,
                    this.Img_Stop, this.Img_Pause, this.Img_Grafico);
        }

        //lavoro terminato e in errore
        if (this.statoConcluso && this.inErrore) {
            this.jPanelStarted.setBackground(Color.ORANGE);
        }
        cambiaPannello(this.jPanelStarted);
    }

    public String getLavorodescrizione() {
        return Lavorodescrizione;
    }

    public java.awt.List getListInfo() {
        return this.listInfo;
    }

    public java.awt.List getListLavori() {
        return listLavori;
    }

    public JScrollPane getJScrollPaneSetupLan() {
        return this.jScrollPaneLan;
    }

    public JScrollPane getJScrollPaneSetupWiFi() {
        return this.jScrollPaneWifi;
    }

    public java.awt.List getListWarning() {
        return this.listWarning;
    }

    public java.awt.List getListSetupNM() {
        return this.listSetupNM;
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
        if (lavoroScelto.contains("Errore")) {
            lavoroScelto = "0";
        }
        this.lavoroScelto = lavoroScelto;
    }

    /**
     * Pannello che mostra il contenuto del file /tmp/warning.txt
     */
    private void PanelWarning() {
        this.pannelloPrecedente = this.panCur;
        this.changeButtons(this.Img_Exit, this.Img_Nulla, this.Img_Nulla,
                this.Img_Freccia_su, this.Img_Freccia_giu, this.Img_Nulla);
        cambiaPannello(this.jPanelWarning);
    }

    /**
     * Alla pressione del pulsante che simula la freccia in su muovendosi sulla
     * lista
     */
    private void PulsanteSu() {
        int nrItem, nrCurItem;
        java.awt.List lista = null;
        javax.swing.JScrollPane jsp = null;
        boolean isSetup = false;
        // Qual'è il nome del pannello in primo piano ?
        switch (this.panCur) {
            case "start" -> {
                if (this.inWl == false) {
                    lista = this.listLavori;
                } else {
                    lista = this.listWLavori;
                }
            }
            case "setup wifi" ->
                jsp = this.jScrollPaneWifi;
            case "setup lan" ->
                jsp = this.jScrollPaneLan;
            case "info" ->
                lista = this.listInfo;
            case "warning" ->
                lista = this.listWarning;
            case "setup" -> {
                lista = this.listSetupNM;
                isSetup = true;
            }
        }//EndSwitch
        if (lista != null) {
            nrItem = lista.getItemCount();
            nrCurItem = lista.getSelectedIndex();
            if (nrCurItem == -1) { // nessun elemento selezionato
                nrCurItem = 0;
            }
            if (nrCurItem > 0) {
                nrCurItem--;
            } else {
                nrCurItem = nrItem - 1;//Va all'ultimo Item
            }
            lista.select(nrCurItem);
            // rendi visibile l'elemento selezionato
            lista.makeVisible(nrCurItem);
            if (this.panCur.equals("start")) {
                if (this.inWl == false) {
                    this.JTextAreaDescrizioneLavoro.setText(
                            this.elencoDesLavoro.get(nrCurItem));
                    if (this.elencoLavoriCompleto.get(nrCurItem)[4].equals("0")) {
                        this.JTextAreaDescrizioneLavoro.setBackground(Color.yellow);
                        this.jButtonPR3.setIcon(this.Img_Nulla);//aggiorna il tipo di Icona per il pulsante
                        this.jButtonPR3.setEnabled(false);
                    } else {
                        this.JTextAreaDescrizioneLavoro.setBackground(Color.white);
                        this.jButtonPR3.setIcon(this.Img_Ok);//aggiorna il tipo di Icona per il pulsante
                        this.jButtonPR3.setEnabled(true);
                    }
                } else {
                    this.JTextAreaDescrizioneLavoro.setText(this.elencoDesWl.get(nrCurItem).toString());
                    this.JTextAreaDescrizioneLavoro.setBackground(Color.white);
                    this.jButtonPR3.setIcon(this.Img_Ok);//aggiorna il tipo di Icona per il pulsante
                    this.jButtonPR3.setEnabled(true);
                }
            }
            if (isSetup) {
                this.jButtonPR3.setIcon(this.setIconSetup());//aggiorna il tipo di Icona per il pulsante
            }

        }//End LIsta not NULL
        if (jsp != null) {
            jsp.getVerticalScrollBar().getBlockIncrement(-1);
            jsp.getVerticalScrollBar().grabFocus();
            robot.keyPress(KeyEvent.VK_UP);
            robot.keyRelease(KeyEvent.VK_UP);
        }
        repaint();
    }//End PulsanteSu

    /**
     * Simula la pressione del pulsante per scorrere la lista in giù
     */
    private void PulsanteGiu() {
        java.awt.List lista = null;
        javax.swing.JScrollPane jsp = null;
        boolean isSetup = false;
        // Qual'è il nome del pannello in primo piano ?

        switch (this.panCur) {
            case "start" -> {
                if (this.inWl == false) {
                    lista = this.listLavori;
                } else {
                    lista = this.listWLavori;
                }
            }
            case "setup wifi" ->
                jsp = this.jScrollPaneWifi;
            case "setup lan" ->
                jsp = this.jScrollPaneLan;
            case "info" ->
                lista = this.listInfo;
            case "warning" ->
                lista = this.listWarning;
            case "setup" -> {
                lista = this.listSetupNM;
                isSetup = true;
            }
        }//EndSwitch
        if (lista != null) {
            int nrItem = lista.getItemCount();
            int nrCurItem = lista.getSelectedIndex();
            if (nrCurItem == -1) { // nessun elemento selezionato
                nrCurItem = 0;
            }
            if (nrCurItem < nrItem - 1) {
                nrCurItem++;
            } else {
                nrCurItem = 0;//ritorna al primo Item
            }
            lista.select(nrCurItem);
            // rendi visibile l'elemento selezionato
            lista.makeVisible(nrCurItem);

            if (this.panCur.equals("start")) {
                setButtonDesc(nrCurItem);
            }
            if (isSetup) {
                this.jButtonPR3.setIcon(this.setIconSetup());//aggiorna il tipo di Icona per il pulsante
            }
        }//End LIsta not NULL

        if (jsp != null) {
            jsp.getVerticalScrollBar().getBlockIncrement(1);
            jsp.getVerticalScrollBar().grabFocus();
            robot.keyPress(KeyEvent.VK_DOWN);
            robot.keyRelease(KeyEvent.VK_DOWN);
        }
        repaint();
    }//End PulsanteSu

    /**
     * Simula la pressione del pulsante per scorrere la lista in giù
     */
    private void PulsanteSxDx(int sx_dx) {
        javax.swing.JScrollPane jsp = null;
        switch (this.panCur) {
            case "setup wifi" ->
                jsp = this.jScrollPaneWifi;
            case "setup lan" ->
                jsp = this.jScrollPaneLan;
        }//EndSwitch
        if (jsp != null) {
            jsp.getHorizontalScrollBar().grabFocus();
        }//End LJScrollPanel
        switch (sx_dx) {
            case -1 -> {
                robot.keyPress(KeyEvent.VK_LEFT);
                robot.keyRelease(KeyEvent.VK_LEFT);
            }
            default -> {
                robot.keyPress(KeyEvent.VK_RIGHT);
                robot.keyRelease(KeyEvent.VK_RIGHT);
            }

        }

        //repaint();
    }//End PulsanteSx

    /**
     * Pannello per la configurazione della LAN Legge il file setup_lan.txt
     */
    private void PanelSetupLan() {
        this.changeButtons(this.Img_Exit, this.Img_Freccia_sx, this.Img_Freccia_dx,
                this.Img_Freccia_su, this.Img_Freccia_giu, this.Img_Ok);
        cambiaPannello(this.jPanelSetupLan);
    }

    /**
     * Pannello per il setup della WiFi Legge il file setup_wifi.txt
     */
    private void PanelSetupWifi() {
        this.changeButtons(this.Img_Exit, this.Img_Freccia_sx, this.Img_Freccia_dx,
                this.Img_Freccia_su, this.Img_Freccia_giu, this.Img_Ok);
        cambiaPannello(this.jPanelSetupWiFi);
    }

    /**
     * Pannello che mostra il contenuto del file info.txt
     */
    private void PanelInfo() {
        this.changeButtons(this.Img_Exit, this.Img_Nulla, this.Img_Nulla,
                this.Img_Freccia_su, this.Img_Freccia_giu, this.Img_Nulla);
        cambiaPannello(this.jPanelInfo);
    }

    /**
     * Pannello necessario per la conferma della scelta Stop/Pausa del lavoro Lo
     * Stop cancella il lavoro, la pausa memorizza (file pausa.txt) il nome del
     * Lavoro messo in Pausa. L'APP Control alla creazione del file
     * /tmp/pausa.txt aggiorna il DB (DA FARE)
     */
    private void PanelDialog() {
        this.changeButtons(this.Img_Nulla, this.Img_Nulla, this.Img_Nulla,
                this.Img_Ok, this.Img_Cancel, this.Img_Nulla);
        cambiaPannello(this.jPanelDialog);
    }

    /**
     * Pannello per disegnare il grafico
     */
    public void PanelCanvas() {
        cambiaPannello(this.g);
        if (this.stato.equals(Static.STATO_CALIBRAZIONE) || this.stato.equals(Static.STATO_CALIBRAZIONE_TEST)) {
            this.changeButtons(this.Img_Nulla, this.Img_Nulla, this.Img_Nulla,
                    this.Img_Ok, this.Img_Cancel, this.Img_Nulla);
            this.set_jLabel_B_L("Calibration");
        } else {

            if (this.inErrore) {
                this.changeButtons(this.Img_Continua, this.Img_Ok, this.Img_Annulla,
                        this.Img_Stop, this.Img_Pause, this.Img_Estende);
            } else if (this.statoConcluso) {
                this.changeButtons(this.Img_Nulla, this.Img_Nulla, this.Img_Nulla,
                        this.Img_Exit, this.Img_reloadWork, this.Img_Estende);
            } else {
                this.changeButtons(this.Img_Nulla, this.Img_Nulla, this.Img_Nulla,
                        this.Img_Stop, this.Img_Pause, this.Img_Estende);
            }
        }
    }

    public int getW_level() {
        return w_level;
    }

    public void setW_level(int w_level) {
        this.w_level = w_level;
    }

    public JLabel getjLabelWarning() {
        return jLabelWarning;
    }

    /**
     * aggiornaWarning carica eventuali Warning dal file warning.txt
     *
     * @param jLabelWarning
     */
    public void setjLabelWarning(JLabel jLabelWarning) {
        this.jLabelWarning = jLabelWarning;
    }

    public void aggiornaWarning(List<String> lista) {
        this.listWarning.removeAll();
        RefreshList(this.listWarning, lista);
        this.listWarning.repaint();
    } //End AggiornaInfo

    /**
     * aggiornaSetupLan carica eventuali Informazioni dal file
     * /tmp/setup_lan.txt
     *
     * @param lista
     */
    public void aggiornaSetupLan(List<String> lista) {
        this.jTextAreaLan.setText("");
        for (String string : lista) {
            this.jTextAreaLan.append(string + "\n");
            Static.debug(string, 3);
        }
    }//End aggiornaSetupLan

    /**
     * aggiornaSetupWiFi carica eventuali Informazioni dal file /tmp/status_wifi
     *
     * @param lista
     */
    public void aggiornaSetupWiFi(List<String> lista) {
        this.jTextAreaWifi.setText("");
        for (String string : lista) {
            this.jTextAreaWifi.append(string + "\n");
            Static.debug(string, 3);
        }

    }//End aggiornaSetupWiFi

    /**
     * aggiornaLavori
     *
     * @param lista
     */
    public void aggiornaLavori(List<String> lista) {
        this.listLavori.removeAll();

        if (lista.isEmpty() || lista.contains("errore")) {  // sintassi nomelavoro, lotti, pezzi, descrizione, canStart
            lista.add(" no count limits§-1§-1§work without counting limits§0");
            // todo verificare se in caso di file lavori.txt vuoto occore fermarsi
        }
        List<String> elencoTxt = new ArrayList<>();
        elencoDesLavoro = new ArrayList<>();
        elencoLavori = new ArrayList<>();
        elencoLavoriCompleto = new ArrayList<>();
        elencoDesLavoroCompleto = new ArrayList<>();
        this.elencoLavori.clear();
        for (String riga : lista) {
            String[] lavoroSplit = riga.split("§"); // nomeLavoro, limLotti, limPezzi, descrizione, canStart
            this.elencoLavoriCompleto.add(lavoroSplit);
            String nomeLavoro = lavoroSplit[0];
            String limLotti = lavoroSplit[1];
            String limPezzi = lavoroSplit[2];
            String descrizione = lavoroSplit[3];

            String canStart = lavoroSplit[4];

            if (limLotti.equals("-1")) { // Lavoro senza limiti -> visualizzo solo il nome
                elencoTxt.add(nomeLavoro);
            } else {
                elencoTxt.add(nomeLavoro + " Lots=" + limLotti + " Pieces=" + limPezzi);
            }
            this.elencoLavori.add(lavoroSplit);
            if (canStart.equals("0")) { // lavoro non avviabile
                descrizione = "Not calibrated -> " + descrizione;
            }
            this.elencoDesLavoroCompleto.add(descrizione);
            this.elencoDesLavoro.add(descrizione);
        }
        RefreshList(listLavori, elencoTxt);
    }//End aggiornaLavori

    /**
     * aggiornaLavori
     *
     * @param lista
     */
    public void aggiornaWl(List<String> lista) {
        this.listWLavori.removeAll();
        this.elencoWl.clear();
        if (lista.isEmpty() || lista.contains("errore")) {  //
            lista.add("Empty !");
            return;
        }
        List<String> elencoTxt = new ArrayList<>();
        elencoDesWl = new ArrayList<>();
        elencoWl = new ArrayList<>();
        elencoWlCompleto = new ArrayList<>();
        elencoWlCompleto = new ArrayList<>();

        for (String riga : lista) {
            String[] WLSplit = riga.split("§"); //nomeWl, nrcicli 
            this.elencoWlCompleto.add(WLSplit);
            String nomeWl = WLSplit[0];
            String nrCicli = WLSplit[1];
            String descrizione = WLSplit[2];
            elencoTxt.add(nomeWl + " Cycles =" + nrCicli);
            this.elencoWl.add(WLSplit);
            //this.elencoWlCompleto.add(WLSplit[2]);
            this.elencoDesWl.add(descrizione);
        }
        RefreshList(listWLavori, elencoTxt);
    }//End aggiornaLavori

    /**
     * aggiornaSessione
     *
     * @param sessione
     */
    public void aggiornaSessione(String sessione) {
        if (sessione.contains("Errore")) {
            sessione = "0";
        }
        this.sessione = sessione;
    }//End 

    /**
     * RefreshList riempie un generico elenco
     *
     * @param elenco oggetto del tipo awt.List
     * @param righe oggetto del tipo List
     */
    public void RefreshList(java.awt.List elenco, List<String> righe) {
        elenco.removeAll();
        for (String riga : righe) {
            elenco.add(riga);
        }
        elenco.select(0);
//        this.repaint();
    }

    /**
     * Riceve il nr del pulsante/contatto rele' esterno premuto
     *
     * @param p
     */
    public void pulsanteEsterno(int p) {
        switch (p) {
            case 1 -> {
                pulsanteHw("PL1");
            }
            case 2 -> {
                pulsanteHw("PL2");
            }
            case 3 -> {
                pulsanteHw("PL3");
            }
            case 4 -> {
                pulsanteHw("PR1");
            }
        }
    }

    /**
     * Metodo per prendere l'input dai pulsanti esterni
     *
     * @param p String nome pulsante
     */
    public void pulsanteHw(String p) {
        switch (p) {
            case "PL1" -> {
                if (this.jButtonPL1.isEnabled()) {
                    jButtonPL1ActionPerformed(null);
                }
            }
            case "PL2" -> {
                if (this.jButtonPL2.isEnabled()) {
                    jButtonPL2ActionPerformed(null);
                }
            }
            case "PL3" -> {
                if (this.jButtonPL3.isEnabled()) {
                    jButtonPL3ActionPerformed(null);
                }
            }
            case "PR1" -> {
                if (this.jButtonPR1.isEnabled()) {
                    jButtonPR1ActionPerformed(null);
                }
            }
            case "PR2" -> {
                if (this.jButtonPR2.isEnabled()) {
                    jButtonPR2ActionPerformed(null);
                }
            }
            case "PR3" -> {
                if (this.jButtonPR3.isEnabled()) {
                    jButtonPR3ActionPerformed(null);
                }
            }
        }
    }

    /**
     * Legge il file "nome_device"
     *
     * @return Nome del device
     */
    private void setNomeDelDevice() {
        this.esegui("aggiorna_nome_device");
    }

    /**
     * Pannello di Setup l'utente deve scegliere tra setup Lan o WiFi
     */
    private void PanelSetup() {
        this.esegui("aggiorna_nm_list");
        this.changeButtons(this.Img_Exit, this.Img_Lan, this.Img_WiFi,
                this.Img_Freccia_su, this.Img_Freccia_giu, setIconSetup());
        cambiaPannello(this.jPanelSetup);
    }

    /**
     * Dipende dalla stringa se contiene o meno OFF / ON
     *
     * @return il tipo di icona Play o Stop
     */
    public ImageIcon setIconSetup() {
        ImageIcon img_play_stop = this.Img_Play;
        int i = this.listSetupNM.getSelectedIndex();
        if (i == -1) {
            i = 0;
            this.listSetupNM.select(i);
            this.listSetupNM.makeVisible(i);
        }
        try {
            if (this.listSetupNM.getItemCount() > 0) {
                if (this.listSetupNM.getSelectedItem().contains("ON")) {
                    img_play_stop = this.Img_Stop;
                }
            }
        } catch (Exception e) {
            Static.debug("Errore index NM_con " + i, 2);
        }
        return img_play_stop;
    }

    /**
     * ariaChiusa chiamato da WorkerThread imposta l'interfaccia
     */
    public void ariaChiusa() {
        this.jLabel_B_R.setBackground(Color.red);
        this.jLabel_B_R.setText("Air OFF");
//        this.repaint();
    }

    /**
     * ariaAperta chiamato da WorkerThread imposta l'interfaccia
     */
    public void ariaAperta() {
        this.jLabel_B_R.setBackground(Color.green);
        this.jLabel_B_R.setText("Air ON");
//        this.repaint();
    }

    /**
     * visualizza i dati aggiornati dei contatori
     */
    public void aggiornaContatori() {

        this.jLabelValidi.setText("" + tiriValidi);
        this.jLabelAnnullati.setText("" + tiriAnnullati);
        this.jLabelErrati.setText("" + tiriErrati);

        if (this.limPezzi == -1) {
            this.jLabelPezziNoLimits.setText("" + this.tiriNelLotto);
        } else {
//            if (this.lotto == this.nrLottiDaFare && this.tiriNelLotto == this.nrTiriDaFare) {
//                // E' Finito il lavoro !
//                setLavoroConcluso(true);
//            } else {
//                if (!this.inErrore) {
//                    this.jPanelStarted.setBackground(Color.WHITE);
//                }
//            }
            this.jLabelContatoreLotti.setText(this.lotto + "/" + this.limLotti);
            this.jLabelContatorePezzi.setText(this.tiriNelLotto + "/" + this.limPezzi);

            // calcolo dei tiri complessivi per l'avanzamento della barra
            this.jProgressBar.setValue(this.tiriNelLotto + ((this.lotto - 1) * this.limPezzi));
        }
        this.repaint();
    }

    /**
     * tiri_errati chiamato da WorkerThread imposta l'interfaccia
     */
    public void tiri_errati() {
        this.setInErrore(true);
        this.repaint();
    }

    public void set_nr_tiri_fatti(int tiriNelLotto) {
        this.tiriNelLotto = tiriNelLotto;
    }

    void updateSensori(String Valori) {
        String[] arrayValori;
        if (Valori.equals("")) {
//            this.jLabel_msg.setText("Air pressure not updated !"); // Aggiungere eventualmente un contatore
        } else {
            arrayValori = Valori.split(",");
            try {
                this.temp_rpi = Float.valueOf(arrayValori[0]);
                this.temp_io_board = Float.valueOf(arrayValori[1]);
                this.v_in = Float.valueOf(arrayValori[2]);
                this.v_rpi = Float.valueOf(arrayValori[3]);
                this.setListInfo(infoAggiuntive);
                // Calcolo esatto della pressione in base al grafico di risposta del sensore emc
                float nuovoValorePressione = (Float.parseFloat(arrayValori[4]) - 1) * 10 / 4;

                this.precPressioneAria = this.pressione_aria_in;
                if (this.precPressioneAria - nuovoValorePressione < Static.MAX_VARIAZIONE_PRESSIONE) {
                    this.pressione_aria_in = nuovoValorePressione;
                }

                if (!(this.pressione_aria_in == null)) {
                    DecimalFormat df = new DecimalFormat("0.00");// solo due cifre decimali
                    if (pressione_aria_in <= this.sogliaMin) {
                        this.jLabel_msg.setBackground(java.awt.Color.RED);
                        this.jLabel_msg.setForeground(java.awt.Color.WHITE);
                        this.jLabel_msg.setText("INC.AIR LOW: " + df.format(pressione_aria_in) + " Bar");
                    } else if (pressione_aria_in > this.sogliaMax) {
                        this.jLabel_msg.setBackground(java.awt.Color.YELLOW);
                        this.jLabel_msg.setForeground(java.awt.Color.BLACK);
                        this.jLabel_msg.setText("INC.AIR HIGH: " + df.format(pressione_aria_in) + " Bar");
                    } else {
                        this.jLabel_msg.setBackground(java.awt.Color.GREEN);
                        this.jLabel_msg.setForeground(java.awt.Color.BLACK);
                        this.jLabel_msg.setText("INC.AIR OK: " + df.format(pressione_aria_in) + " Bar");
                    }
                }
            } catch (NumberFormatException e) {
                Static.debug("jrivitscreen.JRivitMain.update_sensori() - \n" + e.getMessage(), 2);
            }
        }//end Else
    }

    /**
     * Aggiorna valori della soglia min e max dell'ingresso della'aria va letto
     * dal DB tabella CT
     *
     * @param sogliaMin
     * @param sogliaMax
     */
    public void updateSogliePressioneAriaIn(Float sogliaMin, Float sogliaMax) {
        this.sogliaMin = sogliaMin;
        this.sogliaMax = sogliaMax;
    }

    /**
     * gestioneDialogRisposte, la codifica
     *
     *
     */
//    private void gestioneDialogRisposte() {
//        this.jLayeredPaneCenter.
//        switch (risposta) {
//            case CONTINUA -> //Continua
//            {
//                esegui("continua");
//                PanelStarted();
//            }
//            case ACCETTA -> //Accetta
//            {
//                esegui("accetta");
//                PanelStarted();
//            }
////            case ESTENDE -> //Estende
////            {
////                esegui("estendi");
////                PanelStarted();
////            }
//            case ANNULLA -> //Annulla
//            {
//                esegui("annulla");
//                PanelStarted();
//            }
//            case PAUSA -> //Pausa
//            {
//                esegui("pausa");
//                PanelStart();
//            }
//            case ABORT -> //Abortire il lavoro
//            {
//                esegui("abort");
//                PanelStart();
//            }
//        }
//    }
    /**
     * setNomeDevice set Label noeme del device
     *
     * @param nd Nome del device letto dal file nome_device.txt
     */
    public void setNomeDevice(String nd) {
        if (nd.contains("Errore")) {
            nd = "CT-0000-00";
        }
        this.jLabelNomeDevice.setText(nd);
        this.jLabelDeviceName.setText(nd);
        this.jLabelVersione.setText("ver. " + versione + " rel. " + data_release);
        this.repaint();
    }

    /**
     * getjLabelValidi Get Label tiri Validi
     *
     * @return la stringa con il valore dei tiri validi
     */
    public String getjLabelValidi() {
        return this.jLabelValidi.getText();
    }

    public ImageIcon getImageWarning() {
        return this.Img_Warning;
    }

    public void setCurva(String Curva) {
        this.Curva = Curva;
    }

    public void mostraCurva() {
        cambiaPannello(this.g);
        this.repaint();
        //esegui("curva");
    }

    public JLayeredPane getjLayeredPaneCenter() {
        return this.jLayeredPaneCenter;
    }

    /**
     * Lista l'elenco dei device della comunicazione Aggiunge OFF / ON Per
     * distinguere se sono connessi o meno
     *
     * @param list_nm_con Elenco dei device
     */
    public void setListNmCon(List list_nm_con) {
        this.listSetupNM.removeAll();
        for (int c = 0; c < list_nm_con.size(); c++) {
            this.listSetupNM.add(list_nm_con.get(c).toString());
        }
        this.listSetupNM.select(0);
        this.listSetupNM.getVisibleIndex();
        this.listSetupNM.repaint();
    }

    /**
     * Aggiorna lista Info
     *
     * @param info la lista passata per aggiornare il campo Info
     */
    public void setListInfo(List info) {

        this.listInfo.removeAll();
        if (this.pressione_aria_in == null) {
            listInfo.add("Pressione aria Null");
        }
        try {
            DecimalFormat df = new DecimalFormat("0.000");// solo tre cifre decimali
            listInfo.add("Air pressure: " + df.format(this.pressione_aria_in) + " bar");
            listInfo.add("V CPU: " + this.v_rpi.toString() + " V");
            listInfo.add("V IN: " + this.v_in.toString() + " V");
            listInfo.add("I/O board Temp.: " + this.temp_io_board.toString() + " °C");
            listInfo.add("CPU Temp.: " + this.temp_rpi.toString() + " °C");
            listInfo.add("-------------------------------------------------------");
        } catch (Exception e) {
            System.out.printf("errore lettura file info " + e);
        }
        infoAggiuntive = info;
        for (int c = 0; c < info.size(); c++) {
            this.listInfo.add(info.get(c).toString());
        }
        this.listInfo.repaint();
    }

    /**
     * Aggiorna lista Warning
     *
     * @param Warning
     */
    public void setListWarning(List Warning) {
        this.listWarning.removeAll();
        int nrLivelloWarning = 0;
        this.w_level = 0;
        String[] s;
        try {
            for (int c = 0; c < Warning.size(); c++) {
                s = Warning.get(c).toString().split("§");
                this.listWarning.add(s[0]);
                if (s.length > 1) {
                    nrLivelloWarning = Integer.parseInt(s[1]);
                    if (nrLivelloWarning > this.w_level) {
                        this.w_level = nrLivelloWarning;
                    }
                }
            }
        } catch (NumberFormatException e) {
            System.out.print("Errore setListWarning");
        }
        this.set_warning();
        this.listWarning.repaint();
    }

    /**
     * Aggiorna lista lavori
     *
     * @param lista_lavori
     */
    public void setListLavori(String[] lista_lavori) {
        this.listLavori.removeAll();
        for (String lista_lav : lista_lavori) {
            this.listLavori.add(lista_lav);
        }
    }

    /**
     *
     * @return ritorna Stringa Curva
     */
    String getCurva() {
        return this.Curva;
    }

    /**
     * imposta l'operazione e la lancia in background
     *
     * @param operazione
     */
    private void esegui(String operazione) {
        this.doWorker.set_operation(operazione);
        try {
            this.doWorker.doInBackground();
        } catch (Exception ex) {
            Logger.getLogger(JRivitMain.class.getName()).log(Level.SEVERE, null, ex);
            Static.debug("Errore eseguendo l'operazione " + operazione + " in background", 2);
        }
    }

    /**
     * Uscita dal programma
     */
    public void exit() {
        System.exit(0);
    }

    /**
     *
     * @return se è in Pausa è true
     */
    public String getInPausa() {
        return inPausa;
    }

    /**
     *
     * @param inPausa
     */
    public void setInPausa(String inPausa) {
        this.inPausa = inPausa;
    }

    /**
     * dalla operazione Abort o Pause Panel Started passa il lavoro che
     * precedentemente era stato scelto
     *
     * @param lavoro scelto,
     *
     */
    void setlavoroScelto(String lavoro) {
        String llavoro;
        for (int i = 0; i < this.listLavori.getRows(); i++) {
            llavoro = this.listLavori.getItem(i);
            if (llavoro.contains(lavoro)) {
                this.listLavori.select(i);
                break;
            }
        }
    }

    /**
     *
     * @return se è stato impostato lo stato in pausa
     */
    public boolean isInPausa() {
        return in_pausa;
    }

    /**
     * imposta lo stato in pausa
     *
     * @param in_pausa
     */
    public void setIn_pausa(boolean in_pausa) {
        this.in_pausa = in_pausa;
    }

    /**
     * Imposta se chiedere o meno conferma quando il tiro è errato per la scelta
     * Continua, annulla accetta
     *
     * @param chiedi_conferma
     */
    public void setChiedi_conferma(boolean chiedi_conferma) {
        this.chiedi_conferma = chiedi_conferma;
    }

    /**
     *
     * @return se devo chiedere o meno la conferma per Continua, annulla accetta
     */
    public boolean isChiediConferma() {
        return this.chiedi_conferma;
    }

    void set_nr_tiri_annullati(int tiri_annullati) {
    }

    /**
     * registra lo stato attuale della scelta dell'operatore Continua, annulla,
     * pausa, stop, accetta, fine lavoro,
     *
     * @param stato
     */
    public void setStato(String stato) {
        this.stato = stato;
    }

    /**
     * Per la conferma di richiesta per lo STOP o PAUSA
     *
     * @param si_o_no
     */
    void setChiedi_conferma_stop(boolean si_o_no) {
        this.chiedi_conferma_stop = si_o_no;
    }

    /**
     *
     * @return si o no conferma alla scelta STOP o PAUSA
     */
    boolean isChiediConfermaStop() {
        return this.chiedi_conferma_stop;
    }

    /**
     * Ritorna la Label che conta i Lotti
     *
     * @return
     */
    public int getLotto() {
        return lotto;
    }

    /**
     * Imposta il valore che conta il nr dei Lotti nella Label
     *
     * @param lotto
     */
    public void setLotto(int lotto) {
        this.lotto = lotto;
    }

    /**
     * Ritorna la Label dei Tiri Totali
     *
     * @return
     */
    int getTiriTotali() {
        return this.tiriTotali;
    }

    /**
     * Imposta il totale dei tiri nella Label
     *
     * @param nTiri
     */
    void setTiriTotali(int nTiri) {
        this.tiriTotali = nTiri;
    }

    /**
     * Ritorna la Label dei Tiri Validi
     *
     * @return
     */
    public int getTiriValidi() {
        return tiriValidi;
    }

    /**
     * Imposta il valore dei Tiri Validi nella apposita Label
     *
     * @param tiriValidi
     */
    public void setTiriValidi(int tiriValidi) {
        this.tiriValidi = tiriValidi;
    }

    /**
     * Ritorna la label dei Tiri Annullati
     *
     * @return
     */
    public int getTiriAnnullati() {
        return tiriAnnullati;
    }

    /**
     * Imposta il valore nella Label dei Tiri Annullati
     *
     * @param tiriAnnullati
     */
    public void setTiriAnnullati(int tiriAnnullati) {
        this.tiriAnnullati = tiriAnnullati;
    }

    /**
     * Ritorna la Label dei Contatori dei tiri errati
     *
     * @return
     */
    public int getTiriErrati() {
        return tiriErrati;
    }

    /**
     * Imposta la Label che conta i tiri Errati
     *
     * @param tiriErrati
     */
    public void setTiriErrati(int tiriErrati) {
        this.tiriErrati = tiriErrati;
    }

    /**
     * azzera le label dei Contatori
     */
    public void azzeraContatori() {
        this.lotto = 1;
        this.tiriNelLotto = 0;
        this.tiriValidi = 0;
        this.tiriAnnullati = 0;
        this.tiriErrati = 0;
    }

    public void scegliLavoro() {
        //Scelta lavoro
        int idLavoro = this.listLavori.getSelectedIndex();
        try {
            this.lavoroScelto = this.elencoLavori.get(idLavoro)[0];
            limLotti = Integer.parseInt(this.elencoLavori.get(idLavoro)[1]);
            limPezzi = Integer.parseInt(this.elencoLavori.get(idLavoro)[2]);
        } catch (NumberFormatException e) {
            Static.debug("nr_lotti_da_fare null !\n", 2);
            limLotti = 1;
            limPezzi = 1;
        }
    }

    /**
     * Avviare il lavoro scelto
     */
    public void avviaLavoro() {
        try {
            this.jLabelNomeLavoro.setText(this.lavoroScelto.trim());
            setStatoConcluso(false);
            setInErrore(false);
            azzeraContatori();
            this.esegui("scegli_e_avvia");
        } catch (Exception ex) {
            Logger.getLogger(JRivitMain.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    /**
     * Scelta della WL tramite Screen
     */
    public void scegliWL() {
        int idWL = this.listWLavori.getSelectedIndex();
        this.WLscelta = "";
        this.WLnrCicli = 1;
        try {
            this.WLscelta = this.elencoWl.get(idWL)[0];
            this.WLnrCicli = Integer.parseInt(this.elencoWl.get(idWL)[1]);
        } catch (NumberFormatException e) {
            Static.debug("nome WLCicli null !\n", 2);
        }
    }

    /**
     * Avviare la WorkList
     */
    public void avviaWL() {
        try {
            this.jLabelNomeWL.setText(this.WLscelta.trim());
            setStatoConcluso(false);
            setInErrore(false);
            azzeraContatori();
            this.esegui("scegli_e_avvia_wl");
        } catch (Exception ex) {
            Logger.getLogger(JRivitMain.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    /**
     * Serve per fare il refresh dell'icona che cambia colore in base al livello
     * di Warning
     *
     * @return
     */
    JButton getjButtonPL1() {
        return this.jButtonPL1;
    }

    /**
     * Ritorna il Nome del Device
     *
     * @return
     */
    public JLabel getjLabelDeviceName() {
        return jLabelDeviceName;
    }

    public List<String[]> getElencoLavori() {
        return elencoLavori;
    }

    public void setElencoLavori(List<String[]> elencoLavori) {
        this.elencoLavori = elencoLavori;
    }

    public List<String> getElencoDesLavoro() {
        return elencoDesLavoro;
    }

    public void setElencoDesLavoro(List<String> elencoDesLavoro) {
        this.elencoDesLavoro = elencoDesLavoro;
    }

    public boolean isStatoConcluso() {
        return statoConcluso;
    }

    public void setStatoConcluso(boolean statoConcluso) {
        this.statoConcluso = statoConcluso;
    }

    /**
     * Imposta le Label diversamente se il lavoro scelto è quello senza Limiti
     */
    private void impostaLabelContatori() {
        if (this.limPezzi == -1) { // Lavoro senza fine
            this.jLabelDesContatoreLotti.setVisible(false);
            this.jLabelContatoreLotti.setVisible(false);
            this.jLabelDesContatorePezzi.setVisible(false);
            this.jLabelContatorePezzi.setVisible(false);
            this.jLabelDesPezziNoLimits.setVisible(true);
            this.jLabelPezziNoLimits.setVisible(true);
            //this.jPanelStarted.setBackground(Color.LIGHT_GRAY);
            this.jProgressBar.setVisible(false);
        } else {
            this.jLabelDesContatoreLotti.setVisible(true);
            this.jLabelContatoreLotti.setVisible(true);
            this.jLabelDesContatorePezzi.setVisible(true);
            this.jLabelContatorePezzi.setVisible(true);
            this.jLabelDesPezziNoLimits.setVisible(false);
            this.jLabelPezziNoLimits.setVisible(false);
            // Imposto la dimensione della barra percentuale
            this.jProgressBar.setMaximum(this.limLotti * this.limPezzi);
            this.jProgressBar.setVisible(true);
        }
    }

    /**
     *
     * @param curvaDiRiferimanto
     */
    public void setCurvaDiRiferimento(String curvaDiRiferimanto) {
        this.curvaDiRiferimento = curvaDiRiferimanto;
    }

    /**
     * Curva di Riferimento
     *
     * @return
     */
    public String getCurvaDiRiferimento() {
        return curvaDiRiferimento;
    }

    /**
     * Set nr lotti OK
     *
     * @param lotti_ok
     */
    void set_nr_lotti_ok(int lotti_ok) {
        this.lotto = lotti_ok;
    }

    /**
     * Set tiri nl Lotto
     *
     * @param tiriNelLotto
     */
    public void setTiriNelLotto(int tiriNelLotto) {
        this.tiriNelLotto = tiriNelLotto;
    }

    /**
     * Set nr tiri OK
     *
     * @param TiriOk
     */
    public void set_nr_tiri_ok(int TiriOk) {
        this.tiriValidi = TiriOk;
    }

    /**
     * Set tiri in errore
     *
     * @param statoErrore
     */
    void setInErrore(boolean statoErrore) {
        this.inErrore = statoErrore;
    }

    /**
     *
     *
     * /**
     * avvia la fase di calibrazione
     */
    void avviaCalibrazione() {
        g.setPrimoGiro(true);
        PanelCanvas();
    }

    /**
     * avvia la fase di calibrazione
     */
    void avviaCalibrazioneTest() {
        g.setPrimoGiro(false);
        this.set_jLabel_B_L("Cal. Test");
        PanelCanvas();
    }

    /**
     * conclude la fase di calibrazione
     */
    void fineCalibrazione() {
        setStato(Static.STATO_SCELTA_LAVORO);
        PanelMain();
    }

    /**
     * Control una volta preparato l'"ambiente" per il lavoro consente l'avvio
     */
    public void lavoroPronto() { // e' qui....
        if (this.panCur.equals("started")) {
            this.jLabelNomeLavoro.setText(this.lavoroScelto.trim());
            PanelStarted();
        }
        impostaLabelContatori();
    }

    /**
     * Control una volta preparato l'"ambiente" per il lavoro consente l'avvio
     */
    public void wlPronta() { // e' qui....Manca il nome del lavoro
        this.jLabelNomeWL.setText(this.WLscelta.trim());
        if (this.panCur.equals("started")) {
            PanelStarted();
        }
        impostaLabelContatori();
    }

    /**
     * Limite dei Lotti
     *
     * @return
     */
    public int getLimLotti() {
        return limLotti;
    }

    /**
     * Limite Pezzi
     *
     * @return
     */
    public int getLimPezzi() {
        return limPezzi;
    }

    /**
     * Imposta il visualizzatore dello stato della Lan
     *
     * @param stato
     */
    public void setLanIndicator(boolean stato) {
        if (stato) {
            jLabelLan.setBackground(Color.green);
        } else {
            jLabelLan.setBackground(Color.red);
        }
    }

    /**
     * Imposta il visualizzatore dello stato della raggiungibilità di internet
     *
     * @param stato
     */
    public void setInternetIndicator(boolean stato) {
        if (stato) {
            jLabelInternet.setBackground(Color.green);
        } else {
            jLabelInternet.setBackground(Color.red);
        }
    }

    /**
     * Imposta il visualizzatore dello stato della VPN
     *
     * @param stato
     */
    public void setVPNIndicator(boolean stato) {
        if (stato) {
            jLabelVPN.setBackground(Color.green);
        } else {
            jLabelVPN.setBackground(Color.red);
        }
    }

    /**
     * Imposta il visualizzatore dello stato di raggiungibilità del Controller
     *
     * @param stato
     */
    public void setControllerIndicator(boolean stato) {
        if (stato) {
            jLabelController.setBackground(Color.green);
        } else {
            jLabelController.setBackground(Color.red);
        }
    }

    /**
     * Stato della WiFi Green OK - Red OFF
     *
     * @param stato
     */
    void setWiFiIndicator(boolean stato) {
        if (stato) {
            jLabelWiFi.setBackground(Color.green);
        } else {
            jLabelWiFi.setBackground(Color.red);
        }
    }

    /**
     * Imposta l'unità di misura
     *
     * @param um
     */
    void setUM(String um) {
        this.um = um;
    }

    /**
     * Restituisce l'unità di misura
     */
    String getUM() {
        return um;
    }

    /**
     *
     * @param limLotti
     */
    void setLimLotti(String limLotti) {
        try {
            this.limLotti = Integer.parseInt(limLotti);
        } catch (NumberFormatException e) {
            Static.debug("Invalid limLotti values", 2);
        }
    }

    /**
     *
     * @param limPezzi
     */
    void setLimPezzi(String limPezzi) {
        try {
            this.limPezzi = Integer.parseInt(limPezzi);
        } catch (NumberFormatException e) {
            Static.debug("Invalid limPezzi values", 2);
        }
    }

    /**
     *
     * @return Lo stato
     */
    public String getStato() {
        return this.stato;
    }

    public String getPanCur() {
        return panCur;
    }

    public void setPanCur(String panCur) {
        this.panCur = panCur;
    }

    /**
     * Cambia pannello impostando anche panCur e la label in basso a SX
     *
     * @param nuovoPannello
     */
    private void cambiaPannello(JPanel nuovoPannello) {
        this.jLayeredPaneCenter.moveToFront(nuovoPannello);
        this.panCur = nuovoPannello.getName();
        this.set_jLabel_B_L(this.panCur);
        // se dovesse servire conoscere qual'è il pannello in primo piano,
        // usare this.getjLayeredPaneCenter().getComponent(0).getName();
    }

    /**
     * Il contesto è utilizzato per indirizzare il pannello gestito dai dialoghi
     * senza dover fare molti switch
     *
     * @param contesto
     */
    private void setContesto(String contesto) {
        this.contesto = contesto;
    }

    boolean getInErrore() {
        return this.inErrore;
    }

    void abilitaCalibrazione(boolean si_o_no) {
        this.abilitaCalibrazione = si_o_no;
    }

    String getEsitoTiro() {
        return this.esitoTiro;
    }

    public void setEsitoTiro(String esitoTiro) {
        this.esitoTiro = esitoTiro;
    }

    public String getPosizioneErrori() {
        return posizioneErrori;
    }

    public void setPosizioneErrori(String posizioneErrori) {
        this.posizioneErrori = posizioneErrori;
    }

    public String getWLscelta() {
        return WLscelta;
    }

    /**
     *
     * Se esternamente viene impostato l'avvio di una WL deve essere cooerente
     * con la lista di scelta dell WL in Screen
     *
     * @param lavoro work list,
     *
     */
    public void setWLscelta(String WLscelta) {
        String wll = "";
        if (WLscelta.contains("Errore")) {
            WLscelta = "0";
        }
        this.WLscelta = WLscelta;
        for (int i = 0; i < this.listWLavori.getRows(); i++) {
            wll = this.listWLavori.getItem(i);
            if (wll.contains(WLscelta)) {
                this.listWLavori.select(i);
                break;
            }
        }
    }

    public int getWLnrCicli() {
        return WLnrCicli;
    }

    public void setWLnrCicli(int WLnrCicli) {
        this.WLnrCicli = WLnrCicli;
    }

    private void setButtonDesc(int nrCurItem) {
        if (this.inWl == false) {
            this.JTextAreaDescrizioneLavoro.setText(this.elencoDesLavoro.get(nrCurItem));
            if (this.elencoLavoriCompleto.get(nrCurItem)[4].equals("0")) {
                this.JTextAreaDescrizioneLavoro.setBackground(Color.yellow);
                this.jButtonPR3.setIcon(this.Img_Nulla);//aggiorna il tipo di Icona per il pulsante
                this.jButtonPR3.setEnabled(false);
            } else {
                this.JTextAreaDescrizioneLavoro.setBackground(Color.white);
                this.jButtonPR3.setIcon(this.Img_Ok);//aggiorna il tipo di Icona per il pulsante
                this.jButtonPR3.setEnabled(true);
            }
        } else {
            this.JTextAreaDescrizioneLavoro.setText(this.elencoDesWl.get(nrCurItem).toString());
            this.JTextAreaDescrizioneLavoro.setBackground(Color.white);
            this.jButtonPR3.setIcon(this.Img_Ok);//aggiorna il tipo di Icona per il pulsante
            this.jButtonPR3.setEnabled(true);

        }
    }

}
