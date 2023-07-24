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

import java.awt.BasicStroke;
import java.awt.Canvas;
import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.io.File;
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
import javax.swing.JLayeredPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;

/**
 *
 * @author Luca Mannocci & Fabio Fragapane
 */
public class JRivitMain extends javax.swing.JFrame {

    private ImageIcon Img_Exit, Img_Ok, Img_Nulla, Img_Freccia_su,
            Img_Freccia_giu, Img_Warning, Img_Setup, Img_Play,
            Img_No_Warning, Img_Err_Warning, Img_Med_Warning;
    private JDoWorker w_mf;
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
    private int nr_tiri_ok;
    private int nr_tiri;
    private int nr_tiri_annullati;
    private Float sogliaMin = 7.0f;
    private Float sogliaMax = 10.0f;
    private String sessione;
    private String Curva;
//    private int DialogQ = 100;
    private String lavoroScelto;
    private final SimpleDateFormat formatter;
    private Properties setup;
    private final String versione;
    private final String data_release;
    private final String srvKey;
    private List<String[]> elencoLavoriArray;
    private String inPausa;
    private Float temp_rpi;
    private Float temp_io_board;
    private Float v_in;
    private Float v_rpi;
    private Float pressione_aria_in;
    private String PanCur;
    private boolean in_errore = false;
    private boolean in_pausa = false;
    private boolean chiedi_conferma = false;
    private boolean lavoro_concluso = false;
    private int tiri_annullati;
    private boolean chiedi_conferma_stop;
    private List<String> elencoDesLavoro;

    public boolean isLavoro_concluso() {
        return lavoro_concluso;
    }

    public void setLavoro_concluso(boolean lavoro_concluso) {
        this.lavoro_concluso = lavoro_concluso;
    }
//
//    public final static String F_STATO = "stato";
//
//    final static String F_LAVORO_SCELTO = "lavoro_scelto";
//    final static String F_RISPOSTA_TIRO_ERRATO = "risposta_tiro_errato";
//    final static String F_CHIEDE_CONFERMA_NO = "chiedi_conferma_no";
//    final static String F_IN_PAUSA = "in_pausa";
//    final static String F_IN_STOP = "in_stop";
//    final static String F_LAVORO_CONCLUSO = "lavoro_concluso";
//    final static String F_LAVORO_AVVIATO = "lavoro_avviato";
//    final static String F_TIRI = "tiri";
//    final static String F_TIRI_OK = "tiri_ok";
//    final static String F_TIRI_ERRATI = "tiri_errati";
//    final static String F_TIRI_ANNULLATI = "tiri_annullati";
//    final static String F_INFO = "info.txt";
//    final static String F_SENSORI = "sensori";
//    final static String F_WARNING = "warning.txt";
//    final static String F_NOME_DEVICE = "nome_device";
//    final static String F_ARIA = "aria";
//    final static String F_ERRORE = "errore";
//    final static String F_CHIEDI_CONFERMA_NO = "chiedi_conferma_no";
//    final static String F_CHIEDI_CONFERMA_STOP = "chiedi_conferma_stop";

//    static int STATO = 0;
//    final static int STATO_AVVIATO = 10;
//    final static int STATO_CONCLUSO = 12;
//    final static int STATO_PAUSA = 13;
//    final static int STATO_STOP = 14;
//    final static int CONTINUA = 1;
//    final static int ACCETTA = 2;
//    final static int ANNULLA = 3;
//    final static int ESTENDI = 4;
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

        w_mf = new JDoWorker(this);
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
        jLabelDeviceName = new javax.swing.JLabel();
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
        jPanelCalibrazione = new javax.swing.JPanel();
        jLabelNomeDeviceCal = new javax.swing.JLabel();
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

        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);
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
        jButtonPL2.setMargin(new java.awt.Insets(4, 14, 4, 14));
        jButtonPL2.setPreferredSize(new java.awt.Dimension(65, 65));
        jButtonPL2.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButtonPL2ActionPerformed(evt);
            }
        });
        jPanelLeft.add(jButtonPL2, new org.netbeans.lib.awtextra.AbsoluteConstraints(2, 110, -1, -1));

        jButtonPL3.setIcon(new javax.swing.ImageIcon(getClass().getResource("/jrivitscreen/images/setup.png"))); // NOI18N
        jButtonPL3.setAlignmentX(0.5F);
        jButtonPL3.setPreferredSize(new java.awt.Dimension(65, 65));
        jButtonPL3.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButtonPL3ActionPerformed(evt);
            }
        });
        jPanelLeft.add(jButtonPL3, new org.netbeans.lib.awtextra.AbsoluteConstraints(2, 200, -1, -1));

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

        jLabelContatore.setFont(new java.awt.Font("SansSerif", 0, 36)); // NOI18N
        jLabelContatore.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jLabelContatore.setText("0/0");
        jPanelStarted.add(jLabelContatore, new org.netbeans.lib.awtextra.AbsoluteConstraints(5, 90, 315, 30));

        jLabelNomeDevice.setFont(new java.awt.Font("DejaVu Sans Condensed", 1, 18)); // NOI18N
        jLabelNomeDevice.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jLabelNomeDevice.setText("Nome Device");
        jPanelStarted.add(jLabelNomeDevice, new org.netbeans.lib.awtextra.AbsoluteConstraints(5, 8, 315, 30));

        jLabelNomeLavoro.setFont(new java.awt.Font("DejaVu Sans Condensed", 1, 18)); // NOI18N
        jLabelNomeLavoro.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jLabelNomeLavoro.setText("Nome Lavoro");
        jPanelStarted.add(jLabelNomeLavoro, new org.netbeans.lib.awtextra.AbsoluteConstraints(5, 45, 315, 30));

        jProgressBar.setFont(new java.awt.Font("Liberation Sans", 1, 14)); // NOI18N
        jProgressBar.setBorder(javax.swing.BorderFactory.createEtchedBorder());
        jProgressBar.setMaximumSize(new java.awt.Dimension(245, 40));
        jProgressBar.setMinimumSize(new java.awt.Dimension(245, 40));
        jProgressBar.setStringPainted(true);
        jPanelStarted.add(jProgressBar, new org.netbeans.lib.awtextra.AbsoluteConstraints(5, 140, 315, 40));

        jLabelErrati.setBackground(java.awt.Color.red);
        jLabelErrati.setFont(new java.awt.Font("SansSerif", 0, 24)); // NOI18N
        jLabelErrati.setForeground(new java.awt.Color(255, 255, 255));
        jLabelErrati.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jLabelErrati.setText("0");
        jLabelErrati.setBorder(new javax.swing.border.SoftBevelBorder(javax.swing.border.BevelBorder.RAISED));
        jLabelErrati.setOpaque(true);
        jPanelStarted.add(jLabelErrati, new org.netbeans.lib.awtextra.AbsoluteConstraints(235, 200, 80, 25));

        jLabelAnnullati.setBackground(new java.awt.Color(204, 204, 204));
        jLabelAnnullati.setFont(new java.awt.Font("SansSerif", 0, 24)); // NOI18N
        jLabelAnnullati.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jLabelAnnullati.setText("0");
        jLabelAnnullati.setBorder(new javax.swing.border.SoftBevelBorder(javax.swing.border.BevelBorder.RAISED));
        jLabelAnnullati.setOpaque(true);
        jPanelStarted.add(jLabelAnnullati, new org.netbeans.lib.awtextra.AbsoluteConstraints(125, 200, 80, 25));

        jLabel_Errati.setFont(new java.awt.Font("Liberation Sans", 1, 15)); // NOI18N
        jLabel_Errati.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jLabel_Errati.setText("Errati");
        jPanelStarted.add(jLabel_Errati, new org.netbeans.lib.awtextra.AbsoluteConstraints(255, 235, -1, -1));

        jLabel_Annullati.setFont(new java.awt.Font("Liberation Sans", 1, 15)); // NOI18N
        jLabel_Annullati.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jLabel_Annullati.setText("Annullati");
        jPanelStarted.add(jLabel_Annullati, new org.netbeans.lib.awtextra.AbsoluteConstraints(135, 235, -1, -1));

        jLabel_Validi.setFont(new java.awt.Font("Liberation Sans", 1, 15)); // NOI18N
        jLabel_Validi.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jLabel_Validi.setText("Validi");
        jPanelStarted.add(jLabel_Validi, new org.netbeans.lib.awtextra.AbsoluteConstraints(35, 235, -1, -1));

        jLabelValidi.setBackground(java.awt.Color.green);
        jLabelValidi.setFont(new java.awt.Font("SansSerif", 0, 24)); // NOI18N
        jLabelValidi.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jLabelValidi.setText("0");
        jLabelValidi.setBorder(new javax.swing.border.SoftBevelBorder(javax.swing.border.BevelBorder.RAISED));
        jLabelValidi.setOpaque(true);
        jPanelStarted.add(jLabelValidi, new org.netbeans.lib.awtextra.AbsoluteConstraints(10, 200, 80, 25));

        jLayeredPaneCenter.add(jPanelStarted, new org.netbeans.lib.awtextra.AbsoluteConstraints(1, 1, 328, 276));

        jPanelSetup.setMaximumSize(new java.awt.Dimension(328, 276));
        jPanelSetup.setMinimumSize(new java.awt.Dimension(328, 276));
        jPanelSetup.setName("setup"); // NOI18N
        jPanelSetup.setLayout(new org.netbeans.lib.awtextra.AbsoluteLayout());
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
        jLabelLogo.setBorder(new javax.swing.border.SoftBevelBorder(javax.swing.border.BevelBorder.RAISED));
        jLabelLogo.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        jLabelLogo.setIconTextGap(0);
        jLabelLogo.setMaximumSize(new java.awt.Dimension(250, 250));
        jLabelLogo.setMinimumSize(new java.awt.Dimension(250, 250));
        jLabelLogo.setPreferredSize(new java.awt.Dimension(250, 250));
        jPanelMain.add(jLabelLogo, new org.netbeans.lib.awtextra.AbsoluteConstraints(40, 3, 240, 240));
        jLabelLogo.getAccessibleContext().setAccessibleName("Pannello principale");

        jLabelDeviceName.setFont(new java.awt.Font("Helvetica Neue", 1, 14)); // NOI18N
        jLabelDeviceName.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jLabelDeviceName.setText("Device Name");
        jLabelDeviceName.setPreferredSize(new java.awt.Dimension(310, 25));
        jPanelMain.add(jLabelDeviceName, new org.netbeans.lib.awtextra.AbsoluteConstraints(8, 250, -1, -1));
        jLabelDeviceName.getAccessibleContext().setAccessibleName("DeviceName");
        jLabelDeviceName.getAccessibleContext().setAccessibleDescription("Nome del RivitControl");

        jLayeredPaneCenter.add(jPanelMain, new org.netbeans.lib.awtextra.AbsoluteConstraints(1, 1, 328, 276));

        jPanelSetupLan.setMaximumSize(new java.awt.Dimension(328, 276));
        jPanelSetupLan.setMinimumSize(new java.awt.Dimension(328, 276));
        jPanelSetupLan.setName("setup lan"); // NOI18N
        jPanelSetupLan.setPreferredSize(new java.awt.Dimension(328, 276));
        jPanelSetupLan.setLayout(new org.netbeans.lib.awtextra.AbsoluteLayout());

        listSetupLan.setFont(new java.awt.Font("Dialog", 1, 16)); // NOI18N
        jPanelSetupLan.add(listSetupLan, new org.netbeans.lib.awtextra.AbsoluteConstraints(2, 2, 320, 270));

        jLayeredPaneCenter.add(jPanelSetupLan, new org.netbeans.lib.awtextra.AbsoluteConstraints(1, 1, 328, 276));

        jPanelSetupWiFi.setMaximumSize(new java.awt.Dimension(328, 276));
        jPanelSetupWiFi.setMinimumSize(new java.awt.Dimension(328, 276));
        jPanelSetupWiFi.setName("setup wifi"); // NOI18N
        jPanelSetupWiFi.setPreferredSize(new java.awt.Dimension(328, 276));
        jPanelSetupWiFi.setRequestFocusEnabled(false);
        jPanelSetupWiFi.setLayout(new org.netbeans.lib.awtextra.AbsoluteLayout());

        listSetupWiFi.setFont(new java.awt.Font("Dialog", 1, 16)); // NOI18N
        listSetupWiFi.setMaximumSize(new java.awt.Dimension(320, 270));
        listSetupWiFi.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                listSetupWiFiActionPerformed(evt);
            }
        });
        jPanelSetupWiFi.add(listSetupWiFi, new org.netbeans.lib.awtextra.AbsoluteConstraints(2, 2, 320, 270));

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
        listLavori.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                listLavoriActionPerformed(evt);
            }
        });
        jPanelStart.add(listLavori, new org.netbeans.lib.awtextra.AbsoluteConstraints(2, 2, 326, 180));

        JTextAreaDescrizioneLavoro.setLineWrap(true);
        JTextAreaDescrizioneLavoro.setRows(5);
        JTextAreaDescrizioneLavoro.setMaximumSize(new java.awt.Dimension(320, 80));
        JTextAreaDescrizioneLavoro.setMinimumSize(new java.awt.Dimension(320, 80));
        JTextAreaDescrizioneLavoro.setPreferredSize(new java.awt.Dimension(320, 80));
        jPanelStart.add(JTextAreaDescrizioneLavoro, new org.netbeans.lib.awtextra.AbsoluteConstraints(2, 190, 326, 80));

        jLayeredPaneCenter.add(jPanelStart, new org.netbeans.lib.awtextra.AbsoluteConstraints(1, 1, 328, 276));

        jPanelDialog.setMaximumSize(new java.awt.Dimension(328, 276));
        jPanelDialog.setMinimumSize(new java.awt.Dimension(328, 276));
        jPanelDialog.setName("dialog"); // NOI18N
        jPanelDialog.setPreferredSize(new java.awt.Dimension(328, 276));
        jPanelDialog.setLayout(new org.netbeans.lib.awtextra.AbsoluteLayout());

        jLabelDialog.setFont(new java.awt.Font("DejaVu Sans Condensed", 1, 24)); // NOI18N
        jLabelDialog.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jLabelDialog.setText("Annullare il Tiro ?");
        jLabelDialog.setMaximumSize(new java.awt.Dimension(177, 30));
        jLabelDialog.setMinimumSize(new java.awt.Dimension(177, 30));
        jLabelDialog.setPreferredSize(new java.awt.Dimension(177, 30));
        jPanelDialog.add(jLabelDialog, new org.netbeans.lib.awtextra.AbsoluteConstraints(2, 100, 320, 30));

        jLayeredPaneCenter.add(jPanelDialog, new org.netbeans.lib.awtextra.AbsoluteConstraints(1, 1, 328, 276));

        jPanelCanvas.setAlignmentX(0.0F);
        jPanelCanvas.setAlignmentY(0.0F);
        jPanelCanvas.setMaximumSize(new java.awt.Dimension(328, 276));
        jPanelCanvas.setMinimumSize(new java.awt.Dimension(328, 276));
        jPanelCanvas.setName("canvas"); // NOI18N
        jPanelCanvas.setPreferredSize(new java.awt.Dimension(328, 276));
        jPanelCanvas.setLayout(new org.netbeans.lib.awtextra.AbsoluteLayout());
        jLayeredPaneCenter.add(jPanelCanvas, new org.netbeans.lib.awtextra.AbsoluteConstraints(1, 1, 328, 276));

        jPanelCalibrazione.setBackground(new java.awt.Color(204, 255, 204));
        jPanelCalibrazione.setBorder(javax.swing.BorderFactory.createBevelBorder(javax.swing.border.BevelBorder.RAISED));
        jPanelCalibrazione.setMaximumSize(new java.awt.Dimension(245, 234));
        jPanelCalibrazione.setMinimumSize(new java.awt.Dimension(245, 234));
        jPanelCalibrazione.setName("calibrazione"); // NOI18N
        jPanelCalibrazione.setPreferredSize(new java.awt.Dimension(338, 238));
        jPanelCalibrazione.setLayout(new org.netbeans.lib.awtextra.AbsoluteLayout());

        jLabelNomeDeviceCal.setFont(new java.awt.Font("DejaVu Sans Condensed", 1, 18)); // NOI18N
        jLabelNomeDeviceCal.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jLabelNomeDeviceCal.setText("Nome Device");
        jPanelCalibrazione.add(jLabelNomeDeviceCal, new org.netbeans.lib.awtextra.AbsoluteConstraints(5, 8, 315, 30));

        jLabelNomeLavoroCal.setFont(new java.awt.Font("DejaVu Sans Condensed", 1, 24)); // NOI18N
        jLabelNomeLavoroCal.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jLabelNomeLavoroCal.setText("Calibrazione");
        jPanelCalibrazione.add(jLabelNomeLavoroCal, new org.netbeans.lib.awtextra.AbsoluteConstraints(5, 110, 315, 40));

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
        jButtonPR1.setMaximumSize(new java.awt.Dimension(67, 67));
        jButtonPR1.setMinimumSize(new java.awt.Dimension(67, 67));
        jButtonPR1.setPreferredSize(new java.awt.Dimension(65, 65));
        jButtonPR1.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButtonPR1ActionPerformed(evt);
            }
        });
        jPanelRight.add(jButtonPR1, new org.netbeans.lib.awtextra.AbsoluteConstraints(2, 6, -1, -1));

        jButtonPR2.setIcon(new javax.swing.ImageIcon(getClass().getResource("/jrivitscreen/images/nulla.png"))); // NOI18N
        jButtonPR2.setMaximumSize(new java.awt.Dimension(67, 67));
        jButtonPR2.setMinimumSize(new java.awt.Dimension(67, 67));
        jButtonPR2.setPreferredSize(new java.awt.Dimension(65, 65));
        jButtonPR2.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButtonPR2ActionPerformed(evt);
            }
        });
        jPanelRight.add(jButtonPR2, new org.netbeans.lib.awtextra.AbsoluteConstraints(2, 110, -1, -1));

        jButtonPR3.setIcon(new javax.swing.ImageIcon(getClass().getResource("/jrivitscreen/images/nulla.png"))); // NOI18N
        jButtonPR3.setMaximumSize(new java.awt.Dimension(67, 67));
        jButtonPR3.setMinimumSize(new java.awt.Dimension(67, 67));
        jButtonPR3.setPreferredSize(new java.awt.Dimension(65, 65));
        jButtonPR3.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButtonPR3ActionPerformed(evt);
            }
        });
        jPanelRight.add(jButtonPR3, new org.netbeans.lib.awtextra.AbsoluteConstraints(2, 200, -1, -1));

        getContentPane().add(jPanelRight, new org.netbeans.lib.awtextra.AbsoluteConstraints(406, 0, 70, 286));

        jPanelBotton.setBackground(new java.awt.Color(0, 0, 0));
        jPanelBotton.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(0, 0, 0)));
        jPanelBotton.setForeground(java.awt.Color.green);
        jPanelBotton.setMaximumSize(new java.awt.Dimension(400, 20));
        jPanelBotton.setMinimumSize(new java.awt.Dimension(470, 20));
        jPanelBotton.setPreferredSize(new java.awt.Dimension(480, 22));
        jPanelBotton.setLayout(new org.netbeans.lib.awtextra.AbsoluteLayout());

        jLabel_B_L.setBackground(java.awt.Color.lightGray);
        jLabel_B_L.setFont(new java.awt.Font("Segoe UI", 1, 14)); // NOI18N
        jLabel_B_L.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jLabel_B_L.setText("00:00");
        jLabel_B_L.setOpaque(true);
        jPanelBotton.add(jLabel_B_L, new org.netbeans.lib.awtextra.AbsoluteConstraints(3, 5, 70, 20));

        jLabel_B_R.setBackground(java.awt.Color.lightGray);
        jLabel_B_R.setFont(new java.awt.Font("Segoe UI", 1, 14)); // NOI18N
        jLabel_B_R.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jLabel_B_R.setText("Aria Off");
        jLabel_B_R.setOpaque(true);
        jPanelBotton.add(jLabel_B_R, new org.netbeans.lib.awtextra.AbsoluteConstraints(405, 5, 70, 20));
        jLabel_B_R.getAccessibleContext().setAccessibleDescription("Indicatore dello stato dell'aria");

        jLabel_msg.setFont(new java.awt.Font("SansSerif", 1, 14)); // NOI18N
        jLabel_msg.setForeground(javax.swing.UIManager.getDefaults().getColor("Actions.Green"));
        jLabel_msg.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jLabel_msg.setText("message");
        jLabel_msg.setAlignmentX(0.2F);
        jPanelBotton.add(jLabel_msg, new org.netbeans.lib.awtextra.AbsoluteConstraints(90, 5, 300, 20));

        getContentPane().add(jPanelBotton, new org.netbeans.lib.awtextra.AbsoluteConstraints(0, 290, 480, 30));

        pack();
    }// </editor-fold>//GEN-END:initComponents
/**
     * Imposta l'immagine in base al tipo di warning 0 nessuna segnalazione 1 a
     * 5 media segnalazione 6 a 10 errore Il file warning è così composta da due
     * campi: il primo è la descrizione, il secondo il livello di gravità della
     * segnalazione
     * <descrizione>§#
     *
     * @param w_level livello di warning
     */
    public void set_warning(int w_level) {
        switch (w_level) {
            case 0 ->
                this.Img_Warning = this.Img_No_Warning;
            case 1, 2, 3, 4 ->
                this.Img_Warning = this.Img_Med_Warning;
            case 5, 6, 7, 8, 9 ->
                this.Img_Warning = this.Img_Err_Warning;

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
        this.PanCur = this.jLayeredPaneCenter.getComponent(0).getName();
        switch (this.PanCur) {
            case "main" -> {
                PanelStart();
                this.set_jLabel_B_L("Start");
            }
            case "start" ->
                PulsanteSu();
            case "started" -> {//Stop
                //esiste conferma_no come file in /tmp/CT ?
                // se esiste non chiede conferma della scelta
//                DialogQ = STATO_STOP;
                if (this.isChiedi_conferma_stop()) {
                    this.AlertDialogStop = "Annullare il Lavoro ?";
                    this.jLabelDialog.setText(AlertDialogStop);
                    PanelDialog();
                    this.set_jLabel_B_L("Dialog");
                } else {
                    //passa direttamente ad annullare lavoro
                    this.w_mf.set_operation("stato stop");
//                    gestioneDialogRisposte(STATO_STOP);
                    this.PanelStart();
                    this.set_jLabel_B_L("Start");
                }
            }
            case "canvas" ->
                this.drawGrafico();
            case "setup" -> {
                PanelSetupLan();
                this.set_jLabel_B_L("Setup Lan");
            }
            case "warning", "info", "setup lan", "setup wifi" ->
                PulsanteSu();
            case "dialog" -> {
                //Pulsante Sì alla domanda ? Annulla ? Abort ?
                //passa direttamente ad annullare lavoro
                switch (Static.STATO) {
                    case Static.ANNULLA -> {
                        this.w_mf.set_operation("stato annulla");

                    }
                    case Static.CONTINUA -> {
                        this.w_mf.set_operation("stato continua");

                    }
                    case Static.ACCETTA -> {
                        this.w_mf.set_operation("stato accetta");

                    }
                    case Static.STATO_STOP -> {
                        this.w_mf.set_operation("stato stop");
                    }
                    case Static.STATO_PAUSA -> {
                        this.w_mf.set_operation("stato pausa");

                    }
                }

                this.PanelStart();
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

    /**
     * 1^ Pulsante Sinistro
     *
     * @param evt
     */
    private void jButtonPL1ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButtonPL1ActionPerformed
        // Qual'è il nome del pannello in primo piano ?
        switch (this.jLayeredPaneCenter.getComponent(0).getName()) {
            case "main" -> {
                PanelWarning();
                this.jLabel_B_L.setText("Warning");
            }
            case "start" -> {
                PanelMain();//Exit verso main
                set_jLabel_B_L("Main");
            }
            case "started", "canvas" ->//Continua
            {
                this.set_stato(Static.CONTINUA);
                if (isChiedi_conferma()) {
                    this.AlertDialogStop = "Continua ?";
                    this.jLabelDialog.setText(AlertDialogStop);
                    PanelDialog();
                    this.set_jLabel_B_L("Dialog");
                }
            }
            case "setup", "warning", "info" -> {
                PanelMain();
                this.set_jLabel_B_L("Main");
            }
            case "setup lan", "setup wifi" -> {
                PanelSetup();
                this.set_jLabel_B_L("Setup");
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
        switch (this.jLayeredPaneCenter.getComponent(0).getName()) {
            case "main" -> {
                PanelInfo();
                this.jLabel_B_L.setText("Info");
            }
            case "started", "canvas" ->//Accetta il tiro
            {
                this.set_stato(Static.ACCETTA);
                if (this.isChiedi_conferma()) {
                    this.AlertDialogStop = "Accetta ?";
                    this.jLabelDialog.setText(AlertDialogStop);
                    PanelDialog();
                    this.set_jLabel_B_L("Dialog");
                }
            }
            case "setup lan", "setup wifi" ->
                PulsanteSu();
            case "dialog" -> {
                if (this.PanCur.contains("started")) {
                    PanelStarted();
                    this.set_jLabel_B_L("Started");
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
        switch (this.jLayeredPaneCenter.getComponent(0).getName()) {
            case "main" -> {
                PanelSetup();
                this.jLabel_B_L.setText("Setup");
            }
//          case "start" 
            //Per ora nulla
            case "started", "canvas" -> {//Annullare il tiro
                this.set_stato(Static.ANNULLA);
                if (this.isChiedi_conferma()) {
                    this.AlertDialogStop = "Annulla ?";
                    this.jLabelDialog.setText(AlertDialogAnnulla);
                    PanelDialog();
                    this.set_jLabel_B_L("Dialog");
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
        switch (this.jLayeredPaneCenter.getComponent(0).getName()) {
//            case "main" ->
//                PanelStart();
            case "start", "warning", "info", "setup lan", "setup wifi" ->
                PulsanteGiu();
            case "started", "canvas" -> {//Pausa del lavoro 
                this.set_stato(Static.STATO_STOP);
                if (this.isChiedi_conferma_stop()) {
//                    DialogQ = STATO_PAUSA;
                    this.AlertDialogStop = "Pausa ?";
                    this.jLabelDialog.setText(AlertDialogStop);
                    PanelDialog();
                    this.set_jLabel_B_L("Dialog");
                } else {
                    //passa direttamente ad annullare lavoro
                    this.PanelStart();
                    this.set_jLabel_B_L("Start");
                }
            }
            case "setup" -> {
                PanelSetupWifi();
                this.set_jLabel_B_L("Setup Wifi");
            }
            case "dialog" -> {//Scelta no alla domanda ritornare al pannello started
                PanelStarted();
                if (this.in_errore) {
                    set_errore_tiro();
                    this.in_errore = false;
                }
                this.set_jLabel_B_L("Started");
            }
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
            case "start" -> { //Scelta lavoro
                PanelStarted();
                this.set_jLabel_B_L("Started");
            }
            case "started" -> {
                PanelCavans();
                this.set_jLabel_B_L("Graph");

            }
            case "canvas" -> {
                PanelStarted();
                this.set_errore_tiro();
                this.set_jLabel_B_L("Started");
            }
//            case "setup" ->
//                PanelSetupLan();
//            case "warning" ->
//                PulsanteSu();
//            case "info" ->
//                PulsanteSu();
            case "setup lan", "setup wifi" -> {
                PanelSetup();
                this.set_jLabel_B_L("Setup");
            }
        }
    }//GEN-LAST:event_jButtonPR3ActionPerformed

    private void listLavoriActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_listLavoriActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_listLavoriActionPerformed

    private void listSetupWiFiActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_listSetupWiFiActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_listSetupWiFiActionPerformed
    /**
     * PanelMain Pannello che viene visualizzato all'avvio
     */
    private void PanelMain() {
//        changePanel(this.jPanelMain); // metodo migliorato per cambio pannello. Da distribuire sostituendo tutte le chiamate a moveToFront (todo)
        this.change_buttons(this.Img_Warning, this.Img_Info, this.Img_Setup,
                this.Img_Play, this.Img_Nulla, this.Img_Nulla);
        this.jLayeredPaneCenter.moveToFront(this.jPanelMain);

    }

    /**
     * Setup dei pulsanti
     */
    private void change_buttons(ImageIcon I1, ImageIcon I2, ImageIcon I3,
            ImageIcon I4, ImageIcon I5, ImageIcon I6) {
        this.jButtonPL1.setIcon(I1);
        if (I1.equals(this.Img_Nulla)) {
            this.jButtonPL1.setEnabled(false);
        } else {
            this.jButtonPL1.setEnabled(true);
        }

        this.jButtonPL2.setIcon(I2);
        if (I2.equals(this.Img_Nulla)) {
            this.jButtonPL2.setEnabled(false);
        } else {
            this.jButtonPL2.setEnabled(true);
        }

        this.jButtonPL3.setIcon(I3);
        if (I3.equals(this.Img_Nulla)) {
            this.jButtonPL3.setEnabled(false);
        } else {
            this.jButtonPL3.setEnabled(true);
        }
        this.jButtonPR1.setIcon(I4);
        if (I4.equals(this.Img_Nulla)) {
            this.jButtonPR1.setEnabled(false);
        } else {
            this.jButtonPR1.setEnabled(true);
        }
        this.jButtonPR2.setIcon(I5);
        if (I5.equals(this.Img_Nulla)) {
            this.jButtonPR2.setEnabled(false);
        } else {
            this.jButtonPR2.setEnabled(true);
        }
        this.jButtonPR3.setIcon(I6);
        if (I6.equals(this.Img_Nulla)) {
            this.jButtonPR3.setEnabled(false);
        } else {
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
        this.change_buttons(this.Img_Continua, this.Img_Ok, this.Img_Annulla,
                this.Img_Stop, this.Img_Pause, this.Img_Estende);
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
    private javax.swing.JButton jButtonPL1;
    private javax.swing.JButton jButtonPL2;
    private javax.swing.JButton jButtonPL3;
    private javax.swing.JButton jButtonPR1;
    private javax.swing.JButton jButtonPR2;
    private javax.swing.JButton jButtonPR3;
    private javax.swing.JLabel jLabelAnnullati;
    private javax.swing.JLabel jLabelAvvisoCalibrazione;
    private javax.swing.JLabel jLabelContatore;
    private javax.swing.JLabel jLabelDeviceName;
    private javax.swing.JLabel jLabelDialog;
    private javax.swing.JLabel jLabelErrati;
    private javax.swing.JLabel jLabelLogo;
    private javax.swing.JLabel jLabelNomeDevice;
    private javax.swing.JLabel jLabelNomeDeviceCal;
    private javax.swing.JLabel jLabelNomeLavoro;
    private javax.swing.JLabel jLabelNomeLavoroCal;
    private javax.swing.JLabel jLabelValidi;
    private javax.swing.JLabel jLabel_Annullati;
    private javax.swing.JLabel jLabel_B_L;
    private javax.swing.JLabel jLabel_B_R;
    private javax.swing.JLabel jLabel_Errati;
    private javax.swing.JLabel jLabel_Validi;
    private javax.swing.JLabel jLabel_msg;
    private javax.swing.JLayeredPane jLayeredPaneCenter;
    private javax.swing.JPanel jPanelBotton;
    private javax.swing.JPanel jPanelCalibrazione;
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
     * Viene chiamato dopo la gestione dell'errore
     */
    public void ritorno_da_errore() {
        PanelStarted();
    }

    void set_nr_lotti_ok(int lotti_ok) {
        this.nr_lotto_corrente = lotti_ok;
    }

    public void set_nr_tiri(int Tiri) {
        this.nr_tiri = Tiri;
    }

    public void set_nr_tiri_ok(int TiriOk) {
        this.nr_tiri_ok = TiriOk;
    }

    void setin_errore(boolean stato_errore) {
        this.in_errore = stato_errore;
    }

    /**
     * Show PanelStart da questo pannello si fa la scelta del lavoro dalla lista
     * creata da JControl nel file lavori.txt
     */
    public void PanelStart() {
        int selezionato = 0, i = 0;
        this.change_buttons(this.Img_Exit, this.Img_Nulla, this.Img_Nulla,
                this.Img_Freccia_su, this.Img_Freccia_giu, this.Img_Ok);
        // Se sessione non contiene 0
        // vuole dire che da una pausa si vuole riprendere un lavoro
        if (sessione == null) {
            sessione = "0";
        }
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
        this.jLayeredPaneCenter.moveToFront(this.jPanelStart);
    }

    /**
     * Pannello dopo aver fatto la scelta del Lavoro, tale scelta deve essere
     * scritta nel file /tmp/lavoro_scelto.txt L'App JControl sollecitato
     * dall'evento modifica lavoro_scelto o creazione del file, aggiorna il DB
     * (DA FARE) Mostra il conteggio dei tiri e la barra di avanzamento dei
     * lavori, ...
     */
    public void PanelStarted() {
        if (this.in_errore) {
            this.jPanelStarted.setBackground(Color.red);
        } else {
            this.jPanelStarted.setBackground(Color.white);
        }

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
        try {
            nr_lotti_da_fare = Integer.parseInt(det_nr_lotti[1]);
            nr_tiri_da_fare = Integer.parseInt(det_nr_tiri[1]);
        } catch (NumberFormatException e) {
            System.out.print("nr_lotti_da_fare null !\n" + e);
            nr_lotti_da_fare = 1;
            nr_tiri_da_fare = 1;
        }
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
        this.jLayeredPaneCenter.moveToFront(this.jPanelStarted);
        this.repaint();
        esegui("lavoro_scelto");
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

    public java.awt.List getListSetupLan() {
        return listSetupLan;
    }

    public java.awt.List getListSetupWiFi() {
        return listSetupWiFi;
    }

    public java.awt.List getListWarning() {
        return this.listWarning;
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
        if(lavoroScelto.contains("Errore")){
            lavoroScelto="0";
        }
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
        this.change_buttons(this.Img_Exit, this.Img_Nulla, this.Img_Nulla,
                this.Img_Freccia_su, this.Img_Freccia_giu, this.Img_Nulla);
        this.jLayeredPaneCenter.moveToFront(this.jPanelWarning);
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
        repaint();
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
                    this.JTextAreaDescrizioneLavoro.setText(this.elencoDesLavoro.get(selezionato));
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
        repaint();
    }//End PulsanteSu

    /**
     * Pannello per la configurazione della LAN Legge il file setup_lan.txt
     */
    private void PanelSetupLan() {
        this.change_buttons(this.Img_Exit, this.Img_Nulla, this.Img_Nulla,
                this.Img_Freccia_su, this.Img_Freccia_giu, this.Img_Ok);
        this.jLayeredPaneCenter.moveToFront(this.jPanelSetupLan);
    }

    /**
     * Pannello per il setup della WiFi Legge il file setup_wifi.txt
     */
    private void PanelSetupWifi() {
        this.change_buttons(this.Img_Exit, this.Img_Nulla, this.Img_Nulla,
                this.Img_Freccia_su, this.Img_Freccia_giu, this.Img_Ok);
        this.jLayeredPaneCenter.moveToFront(this.jPanelSetupWiFi);
    }

    /**
     * Pannello che mostra il contenuto del file info.txt
     */
    private void PanelInfo() {
        this.change_buttons(this.Img_Exit, this.Img_Nulla, this.Img_Nulla,
                this.Img_Freccia_su, this.Img_Freccia_giu, this.Img_Nulla);
        this.jLayeredPaneCenter.moveToFront(this.jPanelInfo);
    }

    /**
     * Pannello necessario per la conferma della scelta Stop/Pausa del lavoro Lo
     * Stop cancella il lavoro, la pausa memorizza (file pausa.txt) il nome del
     * Lavoro messo in Pausa. L'APP Control alla creazione del file
     * /tmp/pausa.txt aggiorna il DB (DA FARE)
     */
    private void PanelDialog() {
        this.change_buttons(this.Img_Nulla, this.Img_Nulla, this.Img_Nulla,
                this.Img_Ok, this.Img_Cancel, this.Img_Nulla);
        this.jLayeredPaneCenter.moveToFront(this.jPanelDialog);
    }

    /**
     * Pannello per disegnare il grafico
     */
    private void PanelCavans() {
        this.change_buttons(this.Img_Nulla, this.Img_Nulla, this.Img_Nulla,
                this.Img_Ok, this.Img_Nulla, this.Img_Estende);
        this.jLayeredPaneCenter.moveToFront(this.jPanelCanvas);
    }

    /**
     * AggiornaWarning carica eventuali Warning dal file warning.txt
     *
     * @param lista
     */
    public void AggiornaWarning(List<String> lista) {
        this.listWarning.removeAll();
        List<String> elencoTxt = new ArrayList<>();
        for (String riga : lista) {
            String[] lavoroSplit = riga.split("§");
            this.elencoLavoriArray.add(lavoroSplit);
            elencoTxt.add(lavoroSplit[0] + " " + lavoroSplit[1] + " " + lavoroSplit[2]);
        }
        RefreshList(this.listWarning, elencoTxt);
//        this.listWarning.repaint();

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
        this.listLavori.removeAll();
        if(lista.isEmpty()){
            lista.add("Lavoro senza limiti§Lotti=-1§Pezzi=-1§Lavoro predefinito senza limiti");
        }
        List<String> elencoTxt = new ArrayList<>();
        elencoDesLavoro = new ArrayList<>();
        for (String riga : lista) {
            String[] lavoroSplit = riga.split("§");
            this.elencoLavoriArray.add(lavoroSplit);
            elencoTxt.add(lavoroSplit[0] + " " + lavoroSplit[1] + " " + lavoroSplit[2]);
            if (lavoroSplit.length >= 3) {
                this.elencoDesLavoro.add(lavoroSplit[3]);
            }
        }
        RefreshList(listLavori, elencoTxt);
    }//End AggiornaLavori

    /**
     * AggiornaSessione
     *
     * @param sessione
     */
    public void AggiornaSessione(String sessione) {
        if(sessione.contains("Errore")){
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
     * Metodo per prendere l'imput dai pulsanti fisici Non Serve
     *
     * @param p String nome pulsante
     */
    public void pulsante_hw(String p) {
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
     * Legge il file "/tmp/nome_device"
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
        this.change_buttons(this.Img_Exit, this.Img_Nulla, this.Img_Nulla,
                this.Img_Lan, this.Img_WiFi, this.Img_Nulla);
        this.jLayeredPaneCenter.moveToFront(this.jPanelSetup);
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
        
        this.jLabelValidi.setText("" + nr_tiri_ok);

        if (this.nr_tiri_da_fare == -1) {
            //Lavoro senza fine
            this.jPanelStarted.setBackground(Color.GRAY);
            this.jProgressBar.setVisible(false);
            this.jLabelNomeLavoro.setText("Lavoro senza limiti");
            this.jLabelContatore.setText("" + nr_tiri_ok);
        } else {
            if (this.nr_lotto_corrente > this.nr_lotti_da_fare) {
                // E' Finito il lavoro !
                this.jPanelStarted.setBackground(Color.BLUE);
            } else {
                if (!this.in_errore) {
                    this.jPanelStarted.setBackground(Color.WHITE);
                }
            }
            this.jLabelContatore.setText(this.nr_lotto_corrente + "/" + this.nr_lotti_da_fare
                    + " - " + this.nr_tiri_ok + "/" + this.nr_tiri_da_fare);
            this.jProgressBar.setMaximum(this.nr_lotti_da_fare * this.nr_tiri_da_fare);
            this.jProgressBar.setValue(this.nr_tiri_ok + ((this.nr_lotto_corrente - 1) * this.nr_tiri_da_fare)); // calcolo dei tiri complessivi per l'avanzamento della barra
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

    public void set_nr_tiri_fatti(int Tiri_fatti) {
        this.nr_tiri = Tiri_fatti;
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

    void update_sensori(String Valori) {
        String[] arrayValori;
        if (Valori.contains("")) {
            this.jLabel_msg.setForeground(java.awt.Color.CYAN);
            this.jLabel_msg.setText("File P. non aggiornato !");
        } else {
            arrayValori = Valori.split(",");
            try {
                this.temp_rpi = Float.valueOf(arrayValori[0]);
                this.temp_io_board = Float.valueOf(arrayValori[1]);
                this.v_in = Float.valueOf(arrayValori[2]);
                this.v_rpi = Float.valueOf(arrayValori[3]);
                // il valore di pressione letto dal sensore deve essere raddoppiato
                this.pressione_aria_in = Float.parseFloat(arrayValori[4]) * 2;
                if (!(this.pressione_aria_in == null)) {
                    if (pressione_aria_in <= this.sogliaMin) {
                        this.jLabel_msg.setForeground(java.awt.Color.red);
                        this.jLabel_msg.setText("P. aria Err.: " + pressione_aria_in + " Bar");
                    } else {
                        this.jLabel_msg.setForeground(java.awt.Color.green);
                        this.jLabel_msg.setText("P. aria OK: " + pressione_aria_in + " Bar");
                    }
                    this.repaint();
                }
            } catch (NumberFormatException e) {
                System.out.println("jrivitscreen.JRivitMain.update_sensori() - \n" + e.getMessage());
            }
        }//end Else
    }
    /**
     * Aggiorna valori della soglia min e max dell'ingresso della'aria
     * va letto dal DB tabella CT
     * @param SogliaMin
     * @param SogliaMax 
     */
    public void update_soglie_pressione_aria_in(Float SogliaMin, Float SogliaMax) {
        this.sogliaMin = SogliaMin;
        this.sogliaMax = SogliaMax;
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
        if(nd.contains("Errore")){
            nd = "CT-0000-00";
        }
        this.jLabelNomeDevice.setText(nd);
        this.jLabelDeviceName.setText(nd);
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
        //esegui("curva");
        drawGrafico();
    }

    public JLayeredPane getjLayeredPaneCenter() {
        return this.jLayeredPaneCenter;
    }

    private void drawGrafico() {
        Graphics2D gr = (Graphics2D) this.jLayeredPaneCenter.getGraphics();
        this.getjLayeredPaneCenter().repaint();
        this.jPanelCanvas.paintComponents(gr);
        paintComponents(gr);
        int y = this.jPanelCanvas.getHeight();
        if (this.Curva == null) {
            this.Curva = "10,30,40,55,75,77,75,55,40,35,30,20,10,10";
        }
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
            gr.drawString("Java Source", 10, 10);

        }
    }

    public void setListInfo(List Info) {
        this.listInfo.removeAll();
        try {
            listInfo.add("P. aria in ingresso: " + this.pressione_aria_in.toString() + " bar");
            listInfo.add("Tensione CPU: " + this.v_rpi.toString() + " V");
            listInfo.add("Tensione ingresso: " + this.v_in.toString() + " V");
            listInfo.add("Temperatura scheda I/O: " + this.temp_io_board.toString() + " °C");
            listInfo.add("Temperatura CPU: " + this.temp_rpi.toString() + " °C");
            listInfo.add("-------------------------------------------------------");
        } catch (Exception e) {
            System.out.printf("errore lettura file info " + e);
        }
        for (int c = 0; c < Info.size(); c++) {
            this.listInfo.add(Info.get(c).toString());
        }
        this.listInfo.repaint();
    }

    public void setListWarning(List Warning) {
        this.listWarning.removeAll();
        int g = 0;
        int g_tmp = 0;
        String[] s;
        for (int c = 0; c < Warning.size(); c++) {
            s = Warning.get(c).toString().split("§");
            g_tmp = Integer.parseInt(s[1]);
            this.listWarning.add(s[0]);
            if (g_tmp > g) {
                g = g_tmp;
            }
        }
        this.set_warning(g);
    }

    public void setListLavori(String[] lista_lavori) {
        this.listLavori.removeAll();
        for (String lista_lav : lista_lavori) {
            this.listLavori.add(lista_lav);
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
        if(inPausa.contains("Errore")){
            inPausa = "0";
        }
        this.inPausa = inPausa;
    }

    void set_nr_lotti_fatti(int lotti_ok) {
        this.nr_lotto_corrente = lotti_ok;
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
    public boolean isIn_pausa() {
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
    public boolean isChiedi_conferma() {
        return this.chiedi_conferma;
    }

    void set_nr_tiri_annullati(int tiri_annullati) {
        this.tiri_annullati = tiri_annullati;
    }

    /**
     * Restituisce lo stato della scelta dell'operatore o del lavoro
     *
     * @return
     */
    private boolean isRichiestaStop() {
        return (Static.STATO == Static.STATO_STOP) ? true : false;
    }

    /**
     * Restituisce lo stato se è in pausa
     *
     * @return
     */
    private boolean isRichiestaPausa() {
        return (Static.STATO == Static.STATO_PAUSA) ? true : false;
    }

    /**
     * registra lo stato attuale della scelta dell'operatore Continua, annulla,
     * pausa, stop, accetta, fine lavoro,
     *
     * @param stato
     */
    private void set_stato(int stato) {
        Static.STATO = stato;
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
    boolean isChiedi_conferma_stop() {
        return this.chiedi_conferma_stop;
    }
}
