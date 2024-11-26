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
 * @ver 1.3 impostazione delle WorkList
 * @ver. 1.4 gestione della Pausa
 * Control ha il compito di preparare sia il lavoro scelto che la Wl scelta, così
 * come della lista dei lavori e delle WL.
 * Durante la preparazione delle liste deve scrivere i cotatori azzerati se il lavoro è finito
 * e al loro ultimo stato dell'attività messa in pausa.
 * Screen deve evidenziare se un lavoro o una Wl  nella lista sono state messe in pausa
 * Lo evidenzia cambiando colore nella descrizione che diventa azzurra e con il fatto che 
 * deve comparire il tasto reset al 2° pulsante a SX
 * RivitMain che gestisce l'interfaccia, deve sapere quando si muove sulla lista se la riga
 * della lista Lavori o WL è una attività messa in PAUSA o meno
 * Strategia:
 * all'ingresso nel pannello Start devo valutare se la riga da rendere selezionata
 * ha un lavoro o WL in pausa. Se sì colore azzurro e pulsante reset, altrimenti niente
 * Come fare ?
 * Jtask di Control quando costruisce le liste aggiunge come ultimo campo il classico
 * 0/1 1 se trova che il lavoro o Wl è in pausa (lo stabilisce verificando i contatori

ha le seguenti variabili dedicate alla pausa sono inutili vanno tolte
 * ... SONO Troppe occorre migliorare lalogica ...
    private int posizioneLavWL;
    private String cntWlCicli;
    private String limWlCicli;
    private String nomeWPausa;
    private String nomeWLPausa;
    ... forse l'unica utile ... ?
    private boolean in_pausa = false;

 *
 */
package jrivitscreen;

import java.awt.AWTException;
import java.awt.Color;
import java.awt.Robot;
import java.awt.event.KeyEvent;
import java.io.IOException;
import java.io.InputStream;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Properties;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JLayeredPane;
import javax.swing.JPanel;
import org.json.JSONArray;
import org.json.JSONObject;
import org.apache.commons.cli.*;

/**
 *
 * @author Luca Mannocci e Fabio Fragapane
 */
public class JRivitMain extends javax.swing.JFrame {

    private String stato;
    private String richiesta;
    private final JDoWorker doWorker;
    private ImageIcon Img_Warning;
    private final ImageIcon Img_Exit, Img_Ok, Img_Nulla, Img_Freccia_su,
            Img_Freccia_giu, Img_Setup, Img_Play,
            Img_No_Warning, Img_Err_Warning, Img_Med_Warning,
            Img_Grafico, Img_Calibrazione, Img_reloadWork,
            Img_Continua, Img_Estende, Img_Stop,
            Img_Pause, Img_Annulla, Img_Lan, Img_WiFi,
            Img_Freccia_sx, Img_Freccia_dx, Img_Cancel, Img_W, Img_WL, Img_SysStopped, Img_Logo;
    private final ImageIcon Img_Cert, Img_WiFi_2_4, Img_WiFi_5, Img_WiFi_Auto;
    private final ImageIcon Img_start_WiFi, Img_stop_WiFi, Img_start_log, Img_stop_log, Img_restart;
    private final ImageIcon Img_restore;
    private final ImageIcon Img_backup;
    private final ImageIcon Img_firmware;
    private String AlertDialogWhat;
    private String Lavorodescrizione;
    private ImageIcon Img_Info;
    private boolean wifiIndicator;

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
    private Properties setup;
    public final String versione;
    public final String data_release;
    private String ctCanStart;
    private List<String[]> elencoLavori;
    private List<String> elencoTools;
    private List<String[]> elencoWl;
    private List<String[]> elencoLavoriCompleto;
    private List<JSONObject> elencoWlCompleto;
    private Float temp_rpi;
    private Float temp_io_board;
    private Float v_in;
    private Float v_rpi;
    private Float pressione_aria_in;
    private String panCur;
    private boolean inErrore = false;
    private boolean in_pausa = false;
    private boolean inFreeze = false;
    private boolean confermaRispErrore = false;
    private boolean lavoroConcluso = false;
    private boolean wlConclusa = false;
    private boolean confermaStopPausa;
    private List<String> elencoDesLavoro;
    private List<String> elencoDesTools;
    private int w_level;
    private JFileWorker fileWorker = null;
    public JGrafico gr;
    private Robot robot = null;
    private String pannelloPrecedente;
    private List infoAggiuntive;
    private String curvaDiRiferimento;
    private ArrayList<Object> elencoDesLavoroCompleto;
    private String um;  // Unità di misura (Bar o Newton)
    private String contesto;
    private boolean abilitaCalibrazione;
    private String esitoTiro;
    private String posizioneErrori;
    private List<String> elencoDesWl;
    //private List listaWl;
    private boolean inWl;
    private String WLscelta;
    private int limCicli;
    private int cntCicli;
    private String UDLotti;
    private String UDPezzi;
    private boolean sensoreCollegato;
    public JButtonFile bt = null;
    private int conversion;
    public static final int MAX_Y = 320;
    public static final int MAX_X = 480;
    private String scelta;
    private String wifiMode;
    private boolean inSceltaTool;
    private String rispostaErrore;
    private String welcome;
    private boolean isAPOn = false;     //  usato per la certificazione
    private boolean isLogOn = false;    //  usato per la certificazione
    private String snCT;
    private String nomelavoro;
    private int cntLotti;
    private int cntPezzi;
    private int indiceLavoroScelto;
    private int indiceLavoroCorrente;
    private int nrDiLavori;
    private int indiceWLScelta;
    private int durataPlcOk;
    private String nomeDevice;
    private ImageIcon buttonSave[];
    private boolean inBackup;
    private String nomeFirmware;

    /**
     * Creates new form JRivitMain
     */
    public JRivitMain() {
        this.buttonSave = new ImageIcon[6];
        this.ctCanStart = "1";
        this.panCur = "main";
        this.snCT = "";
        this.nomeFirmware = "";
        this.cntCicli = 1;
        String beta = "β";
        initComponents();
        try {
            robot = new Robot();
            robot.mouseMove(MAX_X, MAX_Y);
        } catch (AWTException ex) {
            Static.debug("Error initializing robot system", 2);
        }
        gr = new JGrafico(this);
        gr.setBackground(new java.awt.Color(255, 255, 255));
        gr.setAlignmentX(0.0F);
        gr.setAlignmentY(0.0F);
        gr.setMaximumSize(new java.awt.Dimension(330, 277));
        gr.setMinimumSize(new java.awt.Dimension(330, 277));
        gr.setName("canvas");
        gr.setPreferredSize(new java.awt.Dimension(330, 277));
        gr.setLayout(new org.netbeans.lib.awtextra.AbsoluteLayout());
        this.jLayeredPaneCenter.add(gr, new org.netbeans.lib.awtextra.AbsoluteConstraints(1, 1, 330, 277));
        this.jLabelDesPezziNoLimits.setVisible(false);
        this.jLabelPezziNoLimits.setVisible(false);
        this.durataPlcOk = 10;  // pausa di visualizzazione verde 1 secondo di default
        this.AlertDialogWhat = "Cancel traction ?";
        this.jLabelNomeWL.setText("");
        this.inSceltaTool = false;  // Da impostare in base alla presenza di richiesta di setup iniziale
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
        Img_Cert = new javax.swing.ImageIcon(getClass().getResource("/jrivitscreen/images/Cert.png"));
        Img_WiFi_2_4 = new javax.swing.ImageIcon(getClass().getResource("/jrivitscreen/images/wifi_2.4.png"));
        Img_WiFi_5 = new javax.swing.ImageIcon(getClass().getResource("/jrivitscreen/images/wifi_5.png"));
        Img_WiFi_Auto = new javax.swing.ImageIcon(getClass().getResource("/jrivitscreen/images/wifi_auto.png"));
        Img_start_WiFi = new javax.swing.ImageIcon(getClass().getResource("/jrivitscreen/images/startWifi.png"));
        Img_stop_WiFi = new javax.swing.ImageIcon(getClass().getResource("/jrivitscreen/images/stopWifi.png"));
        Img_start_log = new javax.swing.ImageIcon(getClass().getResource("/jrivitscreen/images/startLog.png"));
        Img_stop_log = new javax.swing.ImageIcon(getClass().getResource("/jrivitscreen/images/stopLog.png"));
        Img_restart = new javax.swing.ImageIcon(getClass().getResource("/jrivitscreen/images/restart_alt.png"));
        Img_SysStopped = new javax.swing.ImageIcon(getClass().getResource("/jrivitscreen/images/logoriSystemStopped.png"));
        Img_Logo = new javax.swing.ImageIcon(getClass().getResource("/jrivitscreen/images/logori2.png"));
        Img_backup = new javax.swing.ImageIcon(getClass().getResource("/jrivitscreen/images/backup.png"));
        Img_restore = new javax.swing.ImageIcon(getClass().getResource("/jrivitscreen/images/restore.png"));
        Img_firmware = new javax.swing.ImageIcon(getClass().getResource("/jrivitscreen/images/firmware.png"));

        elencoLavori = new ArrayList<>();
        elencoWl = new ArrayList<>();
        infoAggiuntive = new ArrayList<>();
        this.sensoreCollegato = true;
        this.lavoroScelto = "";
        this.WLscelta = "";
        this.UDLotti = "+";
        this.UDPezzi = "+";
        this.nomelavoro = "";

        rispostaErrore = "";
        wifiMode = "Auto";
        Calendar.getInstance();
        new SimpleDateFormat("dd/MM/yyyy HH:mm:ss");
        try (InputStream in = this.getClass().getResourceAsStream("setup.properties")) {
            setup = new Properties();
            setup.load(in);
        } catch (IOException ex) {
            Logger.getLogger(JRivitMain.class.getName()).log(Level.SEVERE, null, ex);
        }
        if (!setup.getProperty("versione", "0.0").startsWith("0.")) {
            beta = "";
        }
        versione = setup.getProperty("versione", "0.0");

        data_release = setup.getProperty("data_versione", "14/12/2022");

        setup.getProperty("srvkey", "");
        Static.debug("JRivitScreen ver. " + versione + " release " + data_release, 1);
        if (Static.VMMODE) {
            Static.debug("virtual Mode ON", 1);
        }
        Static.debug("Debug level: " + Static.DEBUGLEVEL, 1);
        Static.debug("Impostato Path per Work " + Static.PATH_WATCH, 3);
        Static.debug("Impostato Path per Lock " + Static.PATH_LCK, 3);

        this.pannelloPrecedente = "main";   // Serve per gestire il ritorno dal pannello di warning
        this.confermaStopPausa = true;
        this.confermaRispErrore = false;
        this.temp_rpi = 0F;
        this.temp_io_board = 0F;
        this.v_in = 0F;
        this.v_rpi = 0F;
        this.pressione_aria_in = 0F;
        this.in_pausa = false;
        this.richiesta = "";
        this.welcome = "Welcome for the first Time!          Select Tool before start working";
        //Aggiunti spazi per fare andare a capo la seconda frase
        try {
            fileWorker = new JFileWorker(this);
        } catch (IOException ex) {
            Logger.getLogger(JRivitMain.class.getName()).log(Level.SEVERE, null, ex);
        }
        if (!Static.VMMODE) {
            try {
                //Lo start() non serve
                this.bt = new JButtonFile(this);
                this.bt.start();
            } catch (IOException ex) {
                Logger.getLogger(JRivitMain.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
        doWorker = new JDoWorker(this, fileWorker);

        this.esegui("init");
        if (!this.richiesta.equals(Static.RICHIESTA_RESET_SYSTEM)) {
            this.esegui("aggiorna_nm_list");
            this.stato = Static.STATO_STOP;
            this.PanelMain();
        }
    }

    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        jLabelSnCT1 = new javax.swing.JLabel();
        jLayeredPaneCenter = new javax.swing.JLayeredPane();
        jPanelMain = new javax.swing.JPanel();
        jLabelLogo = new javax.swing.JLabel();
        jLabelDeviceName = new javax.swing.JLabel();
        jLabelVersione = new javax.swing.JLabel();
        jLabelSnCT = new javax.swing.JLabel();
        jPanelStart = new javax.swing.JPanel();
        listLavori = new java.awt.List();
        listWLavori = new java.awt.List();
        JTextAreaDescrizione = new javax.swing.JTextArea();
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
        jPanelSetupLan = new javax.swing.JPanel();
        listLan = new java.awt.List();
        jPanelSetupWiFi = new javax.swing.JPanel();
        listWifi = new java.awt.List();
        jPanelInfo = new javax.swing.JPanel();
        JTextAreaDescrizioneInfo = new javax.swing.JTextArea();
        listInfo = new java.awt.List();
        jPanelWarning = new javax.swing.JPanel();
        listWarning = new java.awt.List();
        jPanelDialog = new javax.swing.JPanel();
        jLabelDialog = new javax.swing.JLabel();
        jPanelCert = new javax.swing.JPanel();
        listCert = new java.awt.List();
        listSens = new java.awt.List();
        jPanelLock = new javax.swing.JPanel();
        jLabelLogoLock = new javax.swing.JLabel();
        jLabelVersioneLock = new javax.swing.JLabel();
        jLabelDeviceNameLock = new javax.swing.JLabel();
        jLabelSnCGLock = new javax.swing.JLabel();
        jPanelUsb = new javax.swing.JPanel();
        listUsbFile = new java.awt.List();
        jPanelTools = new javax.swing.JPanel();
        JTextAreaDescTool = new javax.swing.JTextArea();
        listTools = new java.awt.List();
        jPanelRight = new javax.swing.JPanel();
        jButtonPR1 = new javax.swing.JButton();
        jButtonPR2 = new javax.swing.JButton();
        jButtonPR3 = new javax.swing.JButton();
        jPanelLeft = new javax.swing.JPanel();
        jButtonPL1 = new javax.swing.JButton();
        jButtonPL2 = new javax.swing.JButton();
        jButtonPL3 = new javax.swing.JButton();
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

        jLabelSnCT1.setFont(new java.awt.Font("Ubuntu Light", 1, 14)); // NOI18N
        jLabelSnCT1.setForeground(java.awt.Color.black);
        jLabelSnCT1.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
        jLabelSnCT1.setText("CG-0000-24");
        jLabelSnCT1.setMaximumSize(new java.awt.Dimension(320, 30));
        jLabelSnCT1.setMinimumSize(new java.awt.Dimension(320, 30));
        jLabelSnCT1.setPreferredSize(new java.awt.Dimension(322, 32));

        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);
        setMinimumSize(new java.awt.Dimension(480, 320));
        setName("frameMain"); // NOI18N
        setUndecorated(true);
        setResizable(false);
        setSize(new java.awt.Dimension(480, 320));
        setType(java.awt.Window.Type.UTILITY);
        getContentPane().setLayout(new org.netbeans.lib.awtextra.AbsoluteLayout());

        jLayeredPaneCenter.setBackground(new java.awt.Color(0, 0, 255));
        jLayeredPaneCenter.setBorder(new javax.swing.border.LineBorder(new java.awt.Color(0, 0, 0), 1, true));
        jLayeredPaneCenter.setMaximumSize(new java.awt.Dimension(330, 277));
        jLayeredPaneCenter.setMinimumSize(new java.awt.Dimension(330, 277));
        jLayeredPaneCenter.setOpaque(true);
        jLayeredPaneCenter.setLayout(new org.netbeans.lib.awtextra.AbsoluteLayout());

        jPanelMain.setBackground(new java.awt.Color(255, 255, 255));
        jPanelMain.setMaximumSize(new java.awt.Dimension(330, 277));
        jPanelMain.setMinimumSize(new java.awt.Dimension(330, 277));
        jPanelMain.setName("main"); // NOI18N
        jPanelMain.setPreferredSize(new java.awt.Dimension(330, 277));
        jPanelMain.setLayout(new org.netbeans.lib.awtextra.AbsoluteLayout());

        jLabelLogo.setBackground(new java.awt.Color(255, 255, 255));
        jLabelLogo.setForeground(new java.awt.Color(0, 0, 0));
        jLabelLogo.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jLabelLogo.setIcon(new javax.swing.ImageIcon(getClass().getResource("/jrivitscreen/images/logori2.png"))); // NOI18N
        jLabelLogo.setAlignmentY(0.0F);
        jLabelLogo.setFocusable(false);
        jLabelLogo.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        jLabelLogo.setMaximumSize(new java.awt.Dimension(250, 250));
        jLabelLogo.setMinimumSize(new java.awt.Dimension(250, 250));
        jLabelLogo.setPreferredSize(new java.awt.Dimension(250, 250));
        jPanelMain.add(jLabelLogo, new org.netbeans.lib.awtextra.AbsoluteConstraints(54, 23, 220, 230));
        jLabelLogo.getAccessibleContext().setAccessibleName("Pannello principale");

        jLabelDeviceName.setFont(new java.awt.Font("Ubuntu Light", 3, 18)); // NOI18N
        jLabelDeviceName.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jLabelDeviceName.setText("Device Name");
        jLabelDeviceName.setMaximumSize(new java.awt.Dimension(320, 30));
        jLabelDeviceName.setMinimumSize(new java.awt.Dimension(320, 30));
        jLabelDeviceName.setPreferredSize(new java.awt.Dimension(322, 32));
        jPanelMain.add(jLabelDeviceName, new org.netbeans.lib.awtextra.AbsoluteConstraints(2, 255, 328, 20));
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
        jPanelMain.add(jLabelVersione, new org.netbeans.lib.awtextra.AbsoluteConstraints(2, 0, 328, 20));

        jLabelSnCT.setFont(new java.awt.Font("Ubuntu Light", 1, 14)); // NOI18N
        jLabelSnCT.setForeground(java.awt.Color.black);
        jLabelSnCT.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
        jLabelSnCT.setText("CG-0000-24");
        jLabelSnCT.setMaximumSize(new java.awt.Dimension(320, 30));
        jLabelSnCT.setMinimumSize(new java.awt.Dimension(320, 30));
        jLabelSnCT.setPreferredSize(new java.awt.Dimension(322, 32));
        jPanelMain.add(jLabelSnCT, new org.netbeans.lib.awtextra.AbsoluteConstraints(225, 233, 100, 20));

        jLayeredPaneCenter.add(jPanelMain, new org.netbeans.lib.awtextra.AbsoluteConstraints(1, 1, 330, 277));

        jPanelStart.setAlignmentX(1.0F);
        jPanelStart.setAlignmentY(1.0F);
        jPanelStart.setMaximumSize(new java.awt.Dimension(330, 277));
        jPanelStart.setMinimumSize(new java.awt.Dimension(330, 277));
        jPanelStart.setName("start"); // NOI18N
        jPanelStart.setPreferredSize(new java.awt.Dimension(330, 277));
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

        JTextAreaDescrizione.setEditable(false);
        JTextAreaDescrizione.setFont(new java.awt.Font("Dialog", 0, 18)); // NOI18N
        JTextAreaDescrizione.setLineWrap(true);
        JTextAreaDescrizione.setRows(5);
        JTextAreaDescrizione.setFocusable(false);
        JTextAreaDescrizione.setMaximumSize(new java.awt.Dimension(320, 80));
        JTextAreaDescrizione.setMinimumSize(new java.awt.Dimension(320, 80));
        jPanelStart.add(JTextAreaDescrizione, new org.netbeans.lib.awtextra.AbsoluteConstraints(2, 186, 328, 90));

        jLayeredPaneCenter.add(jPanelStart, new org.netbeans.lib.awtextra.AbsoluteConstraints(1, 1, 330, 277));

        jPanelStarted.setBackground(new java.awt.Color(204, 204, 255));
        jPanelStarted.setBorder(javax.swing.BorderFactory.createBevelBorder(javax.swing.border.BevelBorder.RAISED));
        jPanelStarted.setMaximumSize(new java.awt.Dimension(330, 277));
        jPanelStarted.setMinimumSize(new java.awt.Dimension(330, 277));
        jPanelStarted.setName("started"); // NOI18N
        jPanelStarted.setPreferredSize(new java.awt.Dimension(330, 277));
        jPanelStarted.setLayout(new org.netbeans.lib.awtextra.AbsoluteLayout());

        jLabelContatoreLotti.setFont(new java.awt.Font("SansSerif", 1, 24)); // NOI18N
        jLabelContatoreLotti.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jLabelContatoreLotti.setText("0/0");
        jPanelStarted.add(jLabelContatoreLotti, new org.netbeans.lib.awtextra.AbsoluteConstraints(2, 138, 163, 30));

        jLabelNomeDevice.setFont(new java.awt.Font("SansSerif", 1, 20)); // NOI18N
        jLabelNomeDevice.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jLabelNomeDevice.setText("Nome Device");
        jPanelStarted.add(jLabelNomeDevice, new org.netbeans.lib.awtextra.AbsoluteConstraints(5, 5, 320, 30));

        jProgressBar.setFont(new java.awt.Font("Liberation Sans", 1, 14)); // NOI18N
        jProgressBar.setBorder(javax.swing.BorderFactory.createEtchedBorder());
        jProgressBar.setMaximumSize(new java.awt.Dimension(245, 40));
        jProgressBar.setMinimumSize(new java.awt.Dimension(245, 40));
        jProgressBar.setStringPainted(true);
        jPanelStarted.add(jProgressBar, new org.netbeans.lib.awtextra.AbsoluteConstraints(5, 173, 320, 40));

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
        jLabelDesContatorePezzi.setText("Traction");
        jLabelDesContatorePezzi.setToolTipText("");
        jPanelStarted.add(jLabelDesContatorePezzi, new org.netbeans.lib.awtextra.AbsoluteConstraints(160, 100, 160, 30));

        jLabelDesContatoreLotti.setFont(new java.awt.Font("SansSerif", 0, 24)); // NOI18N
        jLabelDesContatoreLotti.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jLabelDesContatoreLotti.setText("Lot");
        jLabelDesContatoreLotti.setToolTipText("");
        jPanelStarted.add(jLabelDesContatoreLotti, new org.netbeans.lib.awtextra.AbsoluteConstraints(5, 100, 160, 30));

        jLabelContatorePezzi.setFont(new java.awt.Font("SansSerif", 1, 24)); // NOI18N
        jLabelContatorePezzi.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jLabelContatorePezzi.setText("0/0");
        jPanelStarted.add(jLabelContatorePezzi, new org.netbeans.lib.awtextra.AbsoluteConstraints(165, 138, 163, 30));

        jLabelDesPezziNoLimits.setFont(new java.awt.Font("SansSerif", 0, 24)); // NOI18N
        jLabelDesPezziNoLimits.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jLabelDesPezziNoLimits.setText("Tractions");
        jLabelDesPezziNoLimits.setToolTipText("");
        jPanelStarted.add(jLabelDesPezziNoLimits, new org.netbeans.lib.awtextra.AbsoluteConstraints(84, 100, 160, 30));

        jLabelPezziNoLimits.setFont(new java.awt.Font("SansSerif", 1, 24)); // NOI18N
        jLabelPezziNoLimits.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jLabelPezziNoLimits.setText("0");
        jPanelStarted.add(jLabelPezziNoLimits, new org.netbeans.lib.awtextra.AbsoluteConstraints(2, 138, 328, 30));

        jLabelNomeLavoro.setFont(new java.awt.Font("SansSerif", 1, 20)); // NOI18N
        jLabelNomeLavoro.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jLabelNomeLavoro.setText("Nome Lavoro");
        jLabelNomeLavoro.setFocusable(false);
        jPanelStarted.add(jLabelNomeLavoro, new org.netbeans.lib.awtextra.AbsoluteConstraints(5, 65, 320, -1));

        jLabelNomeWL.setFont(new java.awt.Font("SansSerif", 1, 20)); // NOI18N
        jLabelNomeWL.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jLabelNomeWL.setText("Nome WL");
        jPanelStarted.add(jLabelNomeWL, new org.netbeans.lib.awtextra.AbsoluteConstraints(5, 35, 320, -1));

        jLayeredPaneCenter.add(jPanelStarted, new org.netbeans.lib.awtextra.AbsoluteConstraints(1, 1, 330, 277));

        jPanelSetup.setMaximumSize(new java.awt.Dimension(330, 277));
        jPanelSetup.setMinimumSize(new java.awt.Dimension(330, 277));
        jPanelSetup.setName("setup"); // NOI18N
        jPanelSetup.setPreferredSize(new java.awt.Dimension(330, 277));
        jPanelSetup.setLayout(new org.netbeans.lib.awtextra.AbsoluteLayout());

        listSetupNM.setFont(new java.awt.Font("Dialog", 1, 16)); // NOI18N
        listSetupNM.setMaximumSize(new java.awt.Dimension(326, 273));
        listSetupNM.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                listSetupNMMouseClicked(evt);
            }
        });
        listSetupNM.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                listSetupNMActionPerformed(evt);
            }
        });
        jPanelSetup.add(listSetupNM, new org.netbeans.lib.awtextra.AbsoluteConstraints(2, 2, 326, 273));

        jLayeredPaneCenter.add(jPanelSetup, new org.netbeans.lib.awtextra.AbsoluteConstraints(1, 1, 330, 277));

        jPanelSetupLan.setMaximumSize(new java.awt.Dimension(330, 277));
        jPanelSetupLan.setMinimumSize(new java.awt.Dimension(330, 277));
        jPanelSetupLan.setName("setup lan"); // NOI18N
        jPanelSetupLan.setPreferredSize(new java.awt.Dimension(330, 277));
        jPanelSetupLan.setLayout(new org.netbeans.lib.awtextra.AbsoluteLayout());

        listLan.setFont(new java.awt.Font("DejaVu Sans Condensed", 0, 14)); // NOI18N
        listLan.setMaximumSize(new java.awt.Dimension(326, 273));
        listLan.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                listLanActionPerformed(evt);
            }
        });
        jPanelSetupLan.add(listLan, new org.netbeans.lib.awtextra.AbsoluteConstraints(2, 2, 326, 273));

        jLayeredPaneCenter.add(jPanelSetupLan, new org.netbeans.lib.awtextra.AbsoluteConstraints(1, 1, 330, 277));

        jPanelSetupWiFi.setMaximumSize(new java.awt.Dimension(328, 276));
        jPanelSetupWiFi.setMinimumSize(new java.awt.Dimension(328, 276));
        jPanelSetupWiFi.setName("setup wifi"); // NOI18N
        jPanelSetupWiFi.setPreferredSize(new java.awt.Dimension(328, 276));
        jPanelSetupWiFi.setRequestFocusEnabled(false);
        jPanelSetupWiFi.setLayout(new org.netbeans.lib.awtextra.AbsoluteLayout());

        listWifi.setMaximumSize(new java.awt.Dimension(326, 273));
        jPanelSetupWiFi.add(listWifi, new org.netbeans.lib.awtextra.AbsoluteConstraints(2, 2, 326, 273));

        jLayeredPaneCenter.add(jPanelSetupWiFi, new org.netbeans.lib.awtextra.AbsoluteConstraints(1, 1, 328, 276));

        jPanelInfo.setMaximumSize(new java.awt.Dimension(328, 276));
        jPanelInfo.setMinimumSize(new java.awt.Dimension(328, 276));
        jPanelInfo.setName("info"); // NOI18N
        jPanelInfo.setPreferredSize(new java.awt.Dimension(328, 276));
        jPanelInfo.setLayout(new org.netbeans.lib.awtextra.AbsoluteLayout());

        JTextAreaDescrizioneInfo.setEditable(false);
        JTextAreaDescrizioneInfo.setBackground(new java.awt.Color(255, 255, 255));
        JTextAreaDescrizioneInfo.setFont(new java.awt.Font("Dialog", 0, 18)); // NOI18N
        JTextAreaDescrizioneInfo.setForeground(new java.awt.Color(0, 0, 0));
        JTextAreaDescrizioneInfo.setLineWrap(true);
        JTextAreaDescrizioneInfo.setRows(5);
        JTextAreaDescrizioneInfo.setFocusable(false);
        JTextAreaDescrizioneInfo.setMaximumSize(new java.awt.Dimension(320, 80));
        JTextAreaDescrizioneInfo.setMinimumSize(new java.awt.Dimension(320, 80));
        jPanelInfo.add(JTextAreaDescrizioneInfo, new org.netbeans.lib.awtextra.AbsoluteConstraints(2, 2, 328, 124));

        listInfo.setBackground(new java.awt.Color(255, 255, 204));
        listInfo.setFont(new java.awt.Font("Dialog", 0, 16)); // NOI18N
        listInfo.setMaximumSize(new java.awt.Dimension(326, 273));
        jPanelInfo.add(listInfo, new org.netbeans.lib.awtextra.AbsoluteConstraints(2, 128, 326, 146));
        listInfo.getAccessibleContext().setAccessibleName("Lista_info");
        listInfo.getAccessibleContext().setAccessibleDescription("Informazioni del sistema");

        jLayeredPaneCenter.add(jPanelInfo, new org.netbeans.lib.awtextra.AbsoluteConstraints(1, 1, 328, 276));

        jPanelWarning.setToolTipText("");
        jPanelWarning.setMaximumSize(new java.awt.Dimension(330, 277));
        jPanelWarning.setMinimumSize(new java.awt.Dimension(330, 277));
        jPanelWarning.setName("warning"); // NOI18N
        jPanelWarning.setOpaque(false);
        jPanelWarning.setPreferredSize(new java.awt.Dimension(330, 277));
        jPanelWarning.setLayout(new org.netbeans.lib.awtextra.AbsoluteLayout());

        listWarning.setBackground(new java.awt.Color(204, 255, 204));
        listWarning.setFont(new java.awt.Font("Dialog", 1, 16)); // NOI18N
        listWarning.setMaximumSize(new java.awt.Dimension(326, 273));
        jPanelWarning.add(listWarning, new org.netbeans.lib.awtextra.AbsoluteConstraints(2, 2, 326, 273));

        jLayeredPaneCenter.add(jPanelWarning, new org.netbeans.lib.awtextra.AbsoluteConstraints(1, 1, 330, 277));

        jPanelDialog.setMaximumSize(new java.awt.Dimension(330, 277));
        jPanelDialog.setMinimumSize(new java.awt.Dimension(330, 277));
        jPanelDialog.setName("dialog"); // NOI18N
        jPanelDialog.setPreferredSize(new java.awt.Dimension(330, 277));
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

        jLayeredPaneCenter.add(jPanelDialog, new org.netbeans.lib.awtextra.AbsoluteConstraints(1, 1, 330, 277));

        jPanelCert.setMaximumSize(new java.awt.Dimension(328, 276));
        jPanelCert.setMinimumSize(new java.awt.Dimension(328, 276));
        jPanelCert.setName("cert"); // NOI18N
        jPanelCert.setPreferredSize(new java.awt.Dimension(328, 276));
        jPanelCert.setLayout(new org.netbeans.lib.awtextra.AbsoluteLayout());

        listCert.setBackground(new java.awt.Color(255, 255, 204));
        listCert.setFont(new java.awt.Font("Dialog", 1, 16)); // NOI18N
        listCert.setMaximumSize(new java.awt.Dimension(326, 273));
        jPanelCert.add(listCert, new org.netbeans.lib.awtextra.AbsoluteConstraints(2, 2, 326, 185));

        listSens.setBackground(new java.awt.Color(0, 153, 102));
        listSens.setFont(new java.awt.Font("Dialog", 1, 16)); // NOI18N
        listSens.setMaximumSize(new java.awt.Dimension(326, 273));
        jPanelCert.add(listSens, new org.netbeans.lib.awtextra.AbsoluteConstraints(2, 186, 326, 90));

        jLayeredPaneCenter.add(jPanelCert, new org.netbeans.lib.awtextra.AbsoluteConstraints(1, 1, 328, 276));

        jPanelLock.setBackground(new java.awt.Color(255, 255, 255));
        jPanelLock.setMaximumSize(new java.awt.Dimension(330, 277));
        jPanelLock.setMinimumSize(new java.awt.Dimension(330, 277));
        jPanelLock.setName("main"); // NOI18N
        jPanelLock.setPreferredSize(new java.awt.Dimension(330, 277));
        jPanelLock.setLayout(new org.netbeans.lib.awtextra.AbsoluteLayout());

        jLabelLogoLock.setBackground(new java.awt.Color(255, 255, 255));
        jLabelLogoLock.setForeground(new java.awt.Color(0, 0, 0));
        jLabelLogoLock.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jLabelLogoLock.setIcon(new javax.swing.ImageIcon(getClass().getResource("/jrivitscreen/images/lavori-in-corso.png"))); // NOI18N
        jLabelLogoLock.setAlignmentY(0.0F);
        jLabelLogoLock.setFocusable(false);
        jLabelLogoLock.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        jLabelLogoLock.setMaximumSize(new java.awt.Dimension(250, 250));
        jLabelLogoLock.setMinimumSize(new java.awt.Dimension(250, 250));
        jLabelLogoLock.setPreferredSize(new java.awt.Dimension(250, 250));
        jPanelLock.add(jLabelLogoLock, new org.netbeans.lib.awtextra.AbsoluteConstraints(54, 23, 220, 230));

        jLabelVersioneLock.setFont(new java.awt.Font("Ubuntu Light", 1, 14)); // NOI18N
        jLabelVersioneLock.setForeground(java.awt.Color.blue);
        jLabelVersioneLock.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jLabelVersioneLock.setText("Ver.");
        jLabelVersioneLock.setHorizontalTextPosition(javax.swing.SwingConstants.LEFT);
        jLabelVersioneLock.setMaximumSize(new java.awt.Dimension(320, 30));
        jLabelVersioneLock.setMinimumSize(new java.awt.Dimension(320, 30));
        jLabelVersioneLock.setPreferredSize(new java.awt.Dimension(322, 32));
        jPanelLock.add(jLabelVersioneLock, new org.netbeans.lib.awtextra.AbsoluteConstraints(2, 0, 328, 20));

        jLabelDeviceNameLock.setFont(new java.awt.Font("Ubuntu Light", 3, 18)); // NOI18N
        jLabelDeviceNameLock.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jLabelDeviceNameLock.setText("Device Name");
        jLabelDeviceNameLock.setMaximumSize(new java.awt.Dimension(320, 30));
        jLabelDeviceNameLock.setMinimumSize(new java.awt.Dimension(320, 30));
        jLabelDeviceNameLock.setPreferredSize(new java.awt.Dimension(322, 32));
        jPanelLock.add(jLabelDeviceNameLock, new org.netbeans.lib.awtextra.AbsoluteConstraints(2, 255, 328, 20));

        jLabelSnCGLock.setFont(new java.awt.Font("Ubuntu Light", 1, 14)); // NOI18N
        jLabelSnCGLock.setForeground(java.awt.Color.black);
        jLabelSnCGLock.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
        jLabelSnCGLock.setText("CG-0000-24");
        jLabelSnCGLock.setMaximumSize(new java.awt.Dimension(320, 30));
        jLabelSnCGLock.setMinimumSize(new java.awt.Dimension(320, 30));
        jLabelSnCGLock.setPreferredSize(new java.awt.Dimension(322, 32));
        jPanelLock.add(jLabelSnCGLock, new org.netbeans.lib.awtextra.AbsoluteConstraints(225, 233, 100, 20));

        jLayeredPaneCenter.add(jPanelLock, new org.netbeans.lib.awtextra.AbsoluteConstraints(1, 1, 330, 277));

        jPanelUsb.setAlignmentX(1.0F);
        jPanelUsb.setAlignmentY(1.0F);
        jPanelUsb.setMaximumSize(new java.awt.Dimension(330, 277));
        jPanelUsb.setMinimumSize(new java.awt.Dimension(330, 277));
        jPanelUsb.setName("list_file_usb"); // NOI18N
        jPanelUsb.setPreferredSize(new java.awt.Dimension(330, 277));
        jPanelUsb.setLayout(new org.netbeans.lib.awtextra.AbsoluteLayout());

        listUsbFile.setFont(new java.awt.Font("Dialog", 1, 18)); // NOI18N
        listUsbFile.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                listUsbFileMouseClicked(evt);
            }
        });
        listUsbFile.addItemListener(new java.awt.event.ItemListener() {
            public void itemStateChanged(java.awt.event.ItemEvent evt) {
                listUsbFileItemStateChanged(evt);
            }
        });
        jPanelUsb.add(listUsbFile, new org.netbeans.lib.awtextra.AbsoluteConstraints(2, 2, 390, 290));

        jLayeredPaneCenter.add(jPanelUsb, new org.netbeans.lib.awtextra.AbsoluteConstraints(1, 1, 330, 277));

        jPanelTools.setAlignmentX(1.0F);
        jPanelTools.setAlignmentY(1.0F);
        jPanelTools.setMaximumSize(new java.awt.Dimension(330, 277));
        jPanelTools.setMinimumSize(new java.awt.Dimension(330, 277));
        jPanelTools.setName("start"); // NOI18N
        jPanelTools.setPreferredSize(new java.awt.Dimension(330, 277));
        jPanelTools.setLayout(new org.netbeans.lib.awtextra.AbsoluteLayout());

        JTextAreaDescTool.setEditable(false);
        JTextAreaDescTool.setFont(new java.awt.Font("Dialog", 0, 18)); // NOI18N
        JTextAreaDescTool.setLineWrap(true);
        JTextAreaDescTool.setRows(5);
        JTextAreaDescTool.setFocusable(false);
        JTextAreaDescTool.setMaximumSize(new java.awt.Dimension(320, 80));
        JTextAreaDescTool.setMinimumSize(new java.awt.Dimension(320, 80));
        jPanelTools.add(JTextAreaDescTool, new org.netbeans.lib.awtextra.AbsoluteConstraints(2, 186, 400, 110));

        listTools.setFont(new java.awt.Font("Dialog", 1, 18)); // NOI18N
        listTools.setMaximumSize(new java.awt.Dimension(330, 277));
        listTools.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                listToolsMouseClicked(evt);
            }
        });
        listTools.addItemListener(new java.awt.event.ItemListener() {
            public void itemStateChanged(java.awt.event.ItemEvent evt) {
                listToolsItemStateChanged(evt);
            }
        });
        jPanelTools.add(listTools, new org.netbeans.lib.awtextra.AbsoluteConstraints(2, 2, 330, 277));

        jLayeredPaneCenter.add(jPanelTools, new org.netbeans.lib.awtextra.AbsoluteConstraints(1, 1, 330, 277));

        getContentPane().add(jLayeredPaneCenter, new org.netbeans.lib.awtextra.AbsoluteConstraints(75, 2, -1, -1));

        jPanelRight.setAlignmentX(0.0F);
        jPanelRight.setAlignmentY(0.0F);
        jPanelRight.setMaximumSize(new java.awt.Dimension(70, 286));
        jPanelRight.setMinimumSize(new java.awt.Dimension(70, 286));
        jPanelRight.setPreferredSize(new java.awt.Dimension(70, 286));
        jPanelRight.setLayout(new org.netbeans.lib.awtextra.AbsoluteLayout());

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
        jPanelRight.add(jButtonPR1, new org.netbeans.lib.awtextra.AbsoluteConstraints(2, 6, -1, -1));

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
        jPanelRight.add(jButtonPR2, new org.netbeans.lib.awtextra.AbsoluteConstraints(2, 108, -1, -1));

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
        jPanelRight.add(jButtonPR3, new org.netbeans.lib.awtextra.AbsoluteConstraints(2, 210, -1, -1));

        getContentPane().add(jPanelRight, new org.netbeans.lib.awtextra.AbsoluteConstraints(410, 0, 70, 286));

        jPanelLeft.setAlignmentX(0.1F);
        jPanelLeft.setAlignmentY(0.1F);
        jPanelLeft.setMaximumSize(new java.awt.Dimension(70, 286));
        jPanelLeft.setMinimumSize(new java.awt.Dimension(70, 286));
        jPanelLeft.setPreferredSize(new java.awt.Dimension(70, 286));
        jPanelLeft.setLayout(new org.netbeans.lib.awtextra.AbsoluteLayout());

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
        jPanelLeft.add(jButtonPL1, new org.netbeans.lib.awtextra.AbsoluteConstraints(4, 6, -1, -1));

        jButtonPL2.setBorder(javax.swing.BorderFactory.createBevelBorder(javax.swing.border.BevelBorder.RAISED));
        jButtonPL2.setContentAreaFilled(false);
        jButtonPL2.setMargin(new java.awt.Insets(4, 14, 4, 14));
        jButtonPL2.setPreferredSize(new java.awt.Dimension(65, 65));
        jButtonPL2.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButtonPL2ActionPerformed(evt);
            }
        });
        jPanelLeft.add(jButtonPL2, new org.netbeans.lib.awtextra.AbsoluteConstraints(4, 108, -1, -1));

        jButtonPL3.setAlignmentX(0.5F);
        jButtonPL3.setBorder(javax.swing.BorderFactory.createBevelBorder(javax.swing.border.BevelBorder.RAISED));
        jButtonPL3.setContentAreaFilled(false);
        jButtonPL3.setPreferredSize(new java.awt.Dimension(65, 65));
        jButtonPL3.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButtonPL3ActionPerformed(evt);
            }
        });
        jPanelLeft.add(jButtonPL3, new org.netbeans.lib.awtextra.AbsoluteConstraints(4, 210, -1, -1));

        getContentPane().add(jPanelLeft, new org.netbeans.lib.awtextra.AbsoluteConstraints(0, 0, 70, 286));

        jPanelBotton.setBackground(new java.awt.Color(0, 0, 0));
        jPanelBotton.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(0, 0, 0)));
        jPanelBotton.setForeground(java.awt.Color.green);
        jPanelBotton.setMaximumSize(new java.awt.Dimension(400, 20));
        jPanelBotton.setMinimumSize(new java.awt.Dimension(470, 20));
        jPanelBotton.setPreferredSize(new java.awt.Dimension(480, 22));
        jPanelBotton.setLayout(new org.netbeans.lib.awtextra.AbsoluteLayout());

        jLabel_B_L.setBackground(java.awt.Color.lightGray);
        jLabel_B_L.setFont(new java.awt.Font("Segoe UI", 0, 11)); // NOI18N
        jLabel_B_L.setForeground(new java.awt.Color(0, 0, 0));
        jLabel_B_L.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jLabel_B_L.setText("00:00");
        jLabel_B_L.setToolTipText("");
        jLabel_B_L.setOpaque(true);
        jPanelBotton.add(jLabel_B_L, new org.netbeans.lib.awtextra.AbsoluteConstraints(108, 5, 80, 20));

        jLabel_B_R.setBackground(java.awt.Color.red);
        jLabel_B_R.setFont(new java.awt.Font("DejaVu Sans", 1, 14)); // NOI18N
        jLabel_B_R.setForeground(new java.awt.Color(0, 0, 0));
        jLabel_B_R.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jLabel_B_R.setText("Air OFF");
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

        jLabelWarning.setBackground(java.awt.Color.green);
        jLabelWarning.setFont(new java.awt.Font("DejaVu Sans", 1, 13)); // NOI18N
        jLabelWarning.setForeground(java.awt.Color.black);
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

        jLabelController.setBackground(new java.awt.Color(0, 0, 0));
        jLabelController.setFont(new java.awt.Font("DejaVu Sans", 1, 13)); // NOI18N
        jLabelController.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
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
                this.esegui("sono_in_work");
//                if (this.in_pausa) {
////                   List<String> elencoTxt =  new ArrayList<>();
//                    int quanti = this.listLavori.getItemCount();
//                    int i;
//                    for (i = 0; i < quanti; i++) {
//                        if (this.elencoLavori.get(i)[0].equals(this.lavoroScelto)) {
//                            break;
//                        }
//                    }
//                    if (i < quanti) {
//                        this.listLavori.select(i);
//                        this.JTextAreaDescrizione.setText(this.elencoDesLavoro.get(i));
//                    }
//                }
                PanelStart();
            }

            case "start" ->
                PulsanteSu();

            case "list_file_usb" -> {
                PulsanteSu();
                this.jButtonPL3.setEnabled(false);
                this.jButtonPR3.setEnabled(false);
                if (this.listUsbFile.getSelectedItem().equals("backup_db.zip")) {
                    this.jButtonPL3.setEnabled(true);
                }
                if (this.listUsbFile.getSelectedItem().equals("firmware.bin")) {
                    this.jButtonPR3.setEnabled(true);
                }
            }

            case "started" -> {//Stop
                //esiste conferma_no come file in /tmp/CT ?
                // se esiste non chiede conferma della scelta
                if (this.lavoroConcluso) {
                    this.esegui("stop");
                    PanelStart();
                } else {
                    if (this.isConfermaStopPausa()) {
                        this.setContesto(this.panCur);
                        scelta = Static.STATO_STOP;
                        this.AlertDialogWhat = "Confirm stop work ?";
                        this.jLabelDialog.setText(AlertDialogWhat);
                        PanelDialog();
                    } else {
                        //passa direttamente ad annullare lavoro
                        this.esegui("stop");
                    }
                }

            }
            case "canvas" -> {
                switch (this.stato) {
                    case Static.STATO_CALIBRAZIONE:
                        this.AlertDialogWhat = "Proceed with calibration test ?";
                        this.jLabelDialog.setText(AlertDialogWhat);
                        scelta = Static.RICHIESTA_CALIBRAZIONE_TEST;
                        PanelDialog();
                        break;
                    case Static.STATO_CALIBRAZIONE_TEST:
                        //Salvare la calibrazione ?
                        this.AlertDialogWhat = "Confirm calibration rewrite ?";
                        this.jLabelDialog.setText(AlertDialogWhat);
                        scelta = Static.RICHIESTA_CALIBRAZIONE_SALVA;
                        PanelDialog();
                        break;
                    default:
                        if (this.lavoroConcluso) {
                            this.esegui("stop");
                            PanelStart();
                        } else {
                            if (this.isConfermaStopPausa()) {
                                scelta = Static.STATO_STOP;
                                this.AlertDialogWhat = "Confirm stop work ?";
                                this.jLabelDialog.setText(AlertDialogWhat);
                                PanelDialog();
                            } else {
                                //passa direttamente ad annullare lavoro
                                this.esegui("stop");
                            }
                        }
                }
            }
            case "cert" -> {
                esegui("certWifi_2.4GHz.sh");
                wifiMode = "band bg - 2.4 GHz";
            }

            case "warning", "info", "setup lan", "setup wifi", "setup" ->
                PulsanteSu();
            case "dialog" -> {
                try {
                    //Pulsante Sì alla domanda ? Annulla ? Abort ?
                    //passa direttamente ad annullare lavoro
                    switch (this.scelta) {

                        case Static.ANNULLA -> {
                            this.esegui("annulla");
                            if (this.pannelloPrecedente.equals("started")) {
                                PanelStarted();
                            } else {
                                PanelCanvas();
                            }
                        }
                        case Static.CONTINUA -> {
                            this.esegui("continua");
                            if (this.pannelloPrecedente.equals("started")) {
                                PanelStarted();
                            } else {
                                PanelCanvas();
                            }
                        }
                        case Static.ACCETTA -> {
                            this.esegui("accetta");
                            if (this.pannelloPrecedente.equals("started")) {
                                PanelStarted();
                            } else {
                                PanelCanvas();
                            }
                        }
                        case Static.STATO_STOP -> {
                            this.esegui("stop");
                        }
                        case Static.RICHIESTA_CALIBRAZIONE -> {
                            this.esegui("calibrazione");
                        }
                        case Static.RICHIESTA_CALIBRAZIONE_TEST -> {
                            this.esegui("calibrazione_test");
                        }
                        case Static.RICHIESTA_CALIBRAZIONE_SALVA -> {//Ok registra calibrazione
                            this.gr.setPrimoGiro(2);
                            this.updateDescription(this.listLavori.getSelectedIndex());
                            esegui("salva_calibrazione");
                        }
                        case Static.RICHIESTA_RESET_SYSTEM -> {//Ok RESET SYSTEM
                            esegui("reset_system");
                        }
                        case Static.RICHIESTA_PAUSA ->
                            this.esegui("richiesta_pausa");
                    }
                    if (this.pannelloPrecedente.equals("start")) {
                        PanelStart();
                    }
                } catch (Exception ex) {
                    Logger.getLogger(JRivitMain.class.getName()).log(Level.SEVERE, null, ex);
                }
            }

        }
    }//GEN-LAST:event_jButtonPR1ActionPerformed

    public void setAlertDialogAnnulla(String AlertDialogAnnulla) {
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
                scelta = Static.ACCETTA;
                rispostaErrore();

            }
            case "canvas" -> {
                scelta = Static.ACCETTA;
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
            case "cert" -> {
                esegui("stopCert");
                PanelMain();//Exit verso main
            }
            case "list_file_usb" -> {
                esegui("umount_usb");
                PanelMain();//Exit verso main
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
            case "start" -> {
                if (this.listWLavori.isVisible()) {
                    impostaWLScelta();
                    this.esegui("reset_wl");
                } else {
                    impostaLavoroScelto();
                    this.esegui("reset_work");
                }
                this.jLabel_B_L.setText("start");
            }
            case "started" ->//Accetta il tiro
            {
                this.setContesto(this.panCur);
                scelta = Static.ANNULLA;
                rispostaErrore();
            }
            case "canvas" -> {
                if (!getStato().equals(Static.STATO_CALIBRAZIONE)) {
                    scelta = Static.ANNULLA;
                    rispostaErrore();
                }
            }
            case "info", "setup lan", "setup wifi" ->
                this.PulsanteSxDx(-1);//Sinistra
            case "setup" -> {
                this.esegui("aggiorna_stato_lan");
                PanelSetupLan();
            }
            case "cert" -> {
                if (!isAPOn) {
                    esegui("certWifi_2.4GHz.sh");
                    this.jButtonPL2.setIcon(this.Img_stop_WiFi);
                } else {
                    esegui("certWifiStop.sh");
                    this.jButtonPL2.setIcon(this.Img_start_WiFi);
                    listCert.removeAll();
                    listCert.add("WiFi AP OFF");
                    listCert.add("");
                    listCert.add("LOG: " + (isLogOn ? "ON" : "OFF"));
                    listCert.add("");
                    listCert.add("Press button to choose mode");
                }
                isAPOn = !isAPOn;
            }
            case "list_file_usb" -> {
                esegui("usb_db_backup");
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
            case "info", "setup lan", "setup wifi" ->
                this.PulsanteSxDx(1);//Destra
            case "setup" -> {
                this.esegui("aggiorna_stato_wifi");
                PanelSetupWifi();
            }
            case "start" -> {
                this.AlertDialogWhat = "Enter re-calibration mode ?";
                this.scelta = Static.RICHIESTA_CALIBRAZIONE;
                this.jLabelDialog.setText(AlertDialogWhat);
                PanelDialog();
            }
            case "started" -> {//Annullare il tiro
                this.setContesto(this.panCur);
                this.scelta = Static.CONTINUA;
                rispostaErrore();
            }
            case "canvas" -> {
                if (!getStato().equals(Static.STATO_CALIBRAZIONE)) {
                    this.scelta = Static.CONTINUA;
                    rispostaErrore();
                }
            }
            case "list_file_usb" -> {
                this.esegui("usb_db_restore");
            }
            case "cert" -> {
                if (!isLogOn) {
                    esegui("certSensLogStart.sh");
                    this.jButtonPL3.setIcon(this.Img_stop_log);
                } else {
                    esegui("certSensLogStop.sh");
                    this.jButtonPL3.setIcon(this.Img_start_log);
                }
                isLogOn = !isLogOn;
                if (isAPOn) {
                    aggiornaListCert(null);
                } else {
                    listCert.removeAll();
                    listCert.add("WiFi AP OFF");
                    listCert.add("");
                    listCert.add("LOG: " + (isLogOn ? "ON" : "OFF"));
                    listCert.add("");
                    listCert.add("Press button to choose mode");
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
                if (this.elencoDesWl != null && !this.elencoDesWl.isEmpty()) {
                    this.inWl = true;
                    this.set_jLabel_B_L("WL");
                    this.listLavori.setVisible(false);
                    this.listWLavori.setVisible(true);
                    this.esegui("sono_in_wl");
                    this.JTextAreaDescrizione.setText(this.elencoDesWl.get(0).toString());
                    PanelStart();
                }

            }
            case "start" ->
                PulsanteGiu();
            case "list_file_usb" -> {
                PulsanteGiu();
                this.jButtonPL3.setEnabled(false);
                this.jButtonPR3.setEnabled(false);
                if (this.listUsbFile.getSelectedItem().equals("backup_db.zip")) {
                    this.jButtonPL3.setEnabled(true);
                }
                if (this.listUsbFile.getSelectedItem().startsWith("firmware") 
                        && this.listUsbFile.getSelectedItem().endsWith(".bin")) {
                    this.jButtonPR3.setEnabled(true);
                    this.nomeFirmware= this.listUsbFile.getSelectedItem();
                }
            }
            case "warning", "info", "setup lan", "setup wifi" ->
                PulsanteGiu();
            case "started", "canvas" -> {//Reload Lavoro appena concluso esci da calibrazione

                if (this.stato.equals(Static.STATO_CALIBRAZIONE) || this.stato.equals(Static.STATO_CALIBRAZIONE_TEST)) {
                    //Uscita dalla Calibrazione
                    this.esegui("annulla_calibrazione");
                    PanelStart();
                } else {
                    if (this.lavoroConcluso) {
                        if (this.inWl) {
                            this.esegui("riavvio_wl");
                        } else {
                            this.esegui("riavvio_lavoro");
                        }
//                        avviaLavoro();
                    } else {
                        if (this.isConfermaStopPausa()) {
//                    DialogQ = STATO_PAUSA;
                            scelta = Static.RICHIESTA_PAUSA;
                            this.setContesto(this.panCur);
                            this.AlertDialogWhat = "Confirm pause work ?";
                            this.jLabelDialog.setText(AlertDialogWhat);
                            PanelDialog();
                        } else {
                            //passa direttamente ad annullare lavoro
                            this.esegui("richiesta_pausa");
                        }
                    }
                }

            }
            case "cert" -> {
                esegui("certWifi_5GHz.sh");
                wifiMode = "band a - 5 GHz";
            }
            case "setup" -> {
                PulsanteGiu();
            }
            case "dialog" -> {//Scelta no alla domanda, ritornare al pannello started, No Salva waveform, No test
                switch (this.scelta) {
                    case Static.RICHIESTA_CALIBRAZIONE_TEST -> {//Ritorna in calibrazione
                        this.avviaCalibrazione();
                    }
                    case Static.RICHIESTA_CALIBRAZIONE -> {//Ritorna in scelta lavoro
                        PanelStart();
                    }
                    case Static.RICHIESTA_CALIBRAZIONE_SALVA -> {//Ritorna in scelta lavoro
                        this.gr.setPrimoGiro(2);
                        PanelStart();
                    }
                    case Static.RICHIESTA_RESET_SYSTEM -> { // Ritorna in scelta lavoro
                        esegui("annulla_reset");
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
            case "main" -> {
                esegui("startCert");
                this.PanelCert();

//                per ora uso il pulsante per chiudere;
//                this.exit();
            }
            case "start" -> {
                if (inSceltaTool) {
                    esegui("imposta_tool");
                    this.inSceltaTool = false;
                } else {
                    if (this.inWl) {
                        impostaWLScelta();
                        avviaWL();
                    } else {
                        impostaLavoroScelto();
                        avviaLavoro();
                    }
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
            case "list_file_usb" -> {//Pulsante chiamato nel pannello list_file_usb
                this.esegui("usb_firmware_update");//agiorna il firmware
            }
            case "cert" -> {
                esegui("certi_cicloStart.sh");
                wifiMode = "Auto";
            }
        }
    }//GEN-LAST:event_jButtonPR3ActionPerformed

    /**
     * Gestione della risposta scelta dall'utente per gestire l'errore
     */
    private void rispostaErrore() {
        String testoRispostaErrore = "";
        switch (this.scelta) {
            case Static.CONTINUA -> {
                rispostaErrore = "continua";
                testoRispostaErrore = "Go on";
            }
            case Static.ANNULLA -> {
                rispostaErrore = "annulla";
                testoRispostaErrore = "Cancel traction";
            }
            case Static.ACCETTA -> {
                rispostaErrore = "accetta";
                testoRispostaErrore = "Accepts traction";
            }
        }

        if (isiConfermaRispErrore()) {
            this.AlertDialogWhat = testoRispostaErrore + " ?";
            this.jLabelDialog.setText(AlertDialogWhat);
            PanelDialog();
        } else {
            this.esegui(rispostaErrore);
        }
    }

    /**
     * passa la risposta fatta in corrispondenza di un errore
     *
     * @return
     */
    public String getRispostaErrore() {
        return this.rispostaErrore;
    }

    /**
     * set
     *
     */
    public void setRispostaErrore(String rispostaErrore) {
        this.rispostaErrore = rispostaErrore;
    }

    private void listLavoriMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_listLavoriMouseClicked
        this.JTextAreaDescrizione.setText(
                this.elencoDesLavoro.get(this.listLavori.getSelectedIndex()));
        int idLavoro = this.listLavori.getSelectedIndex();
        if (evt.getClickCount() == 2) { // doppio click -> avvio lavoro
            if ((this.elencoLavori.get(idLavoro)[4]).equals("1")) { // Solo se è calibrato
                impostaLavoroScelto();
                avviaLavoro();
            }
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
        updateDescription(elementoSelezionato);
    }//GEN-LAST:event_listLavoriItemStateChanged

    private void listWLavoriMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_listWLavoriMouseClicked
        this.JTextAreaDescrizione.setText(
                this.elencoDesWl.get(this.listWLavori.getSelectedIndex()).toString());
        if (evt.getClickCount() == 2) { // doppio click -> avvio work list
            impostaWLScelta();
            avviaWL();
        }
    }//GEN-LAST:event_listWLavoriMouseClicked

    private void listWLavoriItemStateChanged(java.awt.event.ItemEvent evt) {//GEN-FIRST:event_listWLavoriItemStateChanged
        java.awt.List l = (java.awt.List) evt.getSource();
        int elementoSelezionato = l.getSelectedIndexes()[0];
        updateDescription(elementoSelezionato);
    }//GEN-LAST:event_listWLavoriItemStateChanged

    private void listLanActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_listLanActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_listLanActionPerformed

    private void listUsbFileMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_listUsbFileMouseClicked
        // TODO add your handling code here:
    }//GEN-LAST:event_listUsbFileMouseClicked

    private void listUsbFileItemStateChanged(java.awt.event.ItemEvent evt) {//GEN-FIRST:event_listUsbFileItemStateChanged
        // TODO add your handling code here:
    }//GEN-LAST:event_listUsbFileItemStateChanged

    private void listToolsMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_listToolsMouseClicked
        // TODO add your handling code here:
    }//GEN-LAST:event_listToolsMouseClicked

    private void listToolsItemStateChanged(java.awt.event.ItemEvent evt) {//GEN-FIRST:event_listToolsItemStateChanged
        // TODO add your handling code here:
    }//GEN-LAST:event_listToolsItemStateChanged

    private void listSetupNMActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_listSetupNMActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_listSetupNMActionPerformed

    /**
     * PanelMain Pannello che viene visualizzato all'avvio
     */
    public final void PanelMain() {
//        this.changeButtons(this.Img_Warning, this.Img_Info, this.Img_Setup,
//                this.Img_W, this.Img_WL, this.Img_Exit);  // pannello precedente. La chiamata a System.exit() manda in crash la JVM. VERIFICARE
        if (this.inSceltaTool) {    // Prima installazione -> scelta tool
            cambiaPannello(this.jPanelStart);
            PanelStart();
        } else {
            if (this.elencoDesWl != null) {
                this.changeButtons(this.Img_Warning, this.Img_Info, this.Img_Setup,
                        this.Img_W, this.Img_WL, this.Img_Nulla);
            } else {
                this.changeButtons(this.Img_Warning, this.Img_Info, this.Img_Setup,
                        this.Img_W, this.Img_Nulla, this.Img_Nulla);
            }
            if (this.ctCanStart.equals("0")) {
                this.changeButtons(this.Img_Warning, this.Img_Info, this.Img_Setup,
                        this.Img_Nulla, this.Img_Nulla, this.Img_Nulla);
            }
            if (this.inFreeze) {
                this.changeButtons(this.Img_Warning, this.Img_Info, this.Img_Nulla,
                        this.Img_Nulla, this.Img_Nulla, this.Img_Nulla);
            }
            cambiaPannello(this.jPanelMain);
        }
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

        if (I1.equals(Img_Continua)) {
            this.jButtonPL1.setBackground(Color.red);
            this.jButtonPL2.setBackground(Color.green);
            this.jButtonPL3.setBackground(Color.white);
        } else {
            this.jButtonPL1.setBackground(Color.gray);
            this.jButtonPL2.setBackground(Color.gray);
            this.jButtonPL3.setBackground(Color.gray);
        }
    }

    /**
     * Salva i pulsanti
     */
    public void saveButtons() {
        this.buttonSave[0] = (ImageIcon) this.jButtonPL1.getIcon();
        this.buttonSave[1] = (ImageIcon) this.jButtonPL2.getIcon();
        this.buttonSave[2] = (ImageIcon) this.jButtonPL3.getIcon();
        this.buttonSave[3] = (ImageIcon) this.jButtonPR1.getIcon();
        this.buttonSave[4] = (ImageIcon) this.jButtonPR2.getIcon();
        this.buttonSave[5] = (ImageIcon) this.jButtonPR3.getIcon();
    }

    /**
     * Disabilita i pulsanti
     */
    public void clearButtons() {
        changeButtons(this.Img_Nulla,
                this.Img_Nulla,
                this.Img_Nulla,
                this.Img_Nulla,
                this.Img_Nulla,
                this.Img_Nulla);
    }

    /**
     * restore dei pulsanti
     */
    public void restoreButtons() {
        changeButtons(this.buttonSave[0],
                this.buttonSave[1],
                this.buttonSave[2],
                this.buttonSave[3],
                this.buttonSave[4],
                this.buttonSave[5]);
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
        pathW.setRequired(false);
        opzioni.addOption(pathW);
        Option pathL = new Option("pl", "pathLock", true, "Lock path");
        pathL.setRequired(false);
        opzioni.addOption(pathL);
        Option debugLevel = new Option("v", "debugLevel", true, "debug level (0-4)");
        debugLevel.setRequired(false);
        opzioni.addOption(debugLevel);
        Option virtualMode = new Option("vm", "virtualMode", false, "virtual mode");
        debugLevel.setRequired(false);
        opzioni.addOption(virtualMode);

        CommandLineParser parser = new DefaultParser();
        HelpFormatter formatter = new HelpFormatter();
        try {
            cmd = parser.parse(opzioni, args);
            if (cmd.hasOption("vm")) {
                Static.setVMMODE(true);
            }
            if (cmd.hasOption("v")) {
                Static.setDEBUG_LEVEL(cmd.getOptionValue("debugLevel"));
            }
            if (cmd.hasOption("pw")) {
                Static.setPATH_WATCH(cmd.getOptionValue("pathWork"));
            }
            if (cmd.hasOption("pl")) {
                Static.setPATH_LCK(cmd.getOptionValue("pathLock"));
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
    private javax.swing.JTextArea JTextAreaDescTool;
    private javax.swing.JTextArea JTextAreaDescrizione;
    private javax.swing.JTextArea JTextAreaDescrizioneInfo;
    private javax.swing.JButton jButtonPL1;
    private javax.swing.JButton jButtonPL2;
    private javax.swing.JButton jButtonPL3;
    private javax.swing.JButton jButtonPR1;
    private javax.swing.JButton jButtonPR2;
    private javax.swing.JButton jButtonPR3;
    private javax.swing.JLabel jLabelAnnullati;
    private javax.swing.JLabel jLabelContatoreLotti;
    private javax.swing.JLabel jLabelContatorePezzi;
    private javax.swing.JLabel jLabelController;
    private javax.swing.JLabel jLabelDesContatoreLotti;
    private javax.swing.JLabel jLabelDesContatorePezzi;
    private javax.swing.JLabel jLabelDesPezziNoLimits;
    private javax.swing.JLabel jLabelDeviceName;
    private javax.swing.JLabel jLabelDeviceNameLock;
    private javax.swing.JLabel jLabelDialog;
    private javax.swing.JLabel jLabelErrati;
    private javax.swing.JLabel jLabelInternet;
    private javax.swing.JLabel jLabelLan;
    private javax.swing.JLabel jLabelLogo;
    private javax.swing.JLabel jLabelLogoLock;
    private javax.swing.JLabel jLabelNomeDevice;
    private javax.swing.JLabel jLabelNomeLavoro;
    private javax.swing.JLabel jLabelNomeWL;
    private javax.swing.JLabel jLabelPezziNoLimits;
    private javax.swing.JLabel jLabelSnCGLock;
    private javax.swing.JLabel jLabelSnCT;
    private javax.swing.JLabel jLabelSnCT1;
    private javax.swing.JLabel jLabelVPN;
    private javax.swing.JLabel jLabelValidi;
    private javax.swing.JLabel jLabelVersione;
    private javax.swing.JLabel jLabelVersioneLock;
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
    private javax.swing.JPanel jPanelCert;
    private javax.swing.JPanel jPanelDialog;
    private javax.swing.JPanel jPanelInfo;
    private javax.swing.JPanel jPanelLeft;
    private javax.swing.JPanel jPanelLock;
    private javax.swing.JPanel jPanelMain;
    private javax.swing.JPanel jPanelRight;
    private javax.swing.JPanel jPanelSetup;
    private javax.swing.JPanel jPanelSetupLan;
    private javax.swing.JPanel jPanelSetupWiFi;
    private javax.swing.JPanel jPanelStart;
    private javax.swing.JPanel jPanelStarted;
    private javax.swing.JPanel jPanelTools;
    private javax.swing.JPanel jPanelUsb;
    private javax.swing.JPanel jPanelWarning;
    private javax.swing.JProgressBar jProgressBar;
    private java.awt.List listCert;
    private java.awt.List listInfo;
    private java.awt.List listLan;
    private java.awt.List listLavori;
    private java.awt.List listSens;
    private java.awt.List listSetupNM;
    private java.awt.List listTools;
    private java.awt.List listUsbFile;
    private java.awt.List listWLavori;
    private java.awt.List listWarning;
    private java.awt.List listWifi;
    // End of variables declaration//GEN-END:variables

    /**
     * Show PanelStart da questo pannello si fa la scelta del lavoro dalla lista
     * creata da JControl nel file lavori.txt
     */
    public void PanelStart() {
        int selezionato = 0, i = 0;
        ImageIcon reload = this.Img_Nulla;
        if (this.in_pausa) {
            reload = this.Img_restart;
        }
        java.awt.List lista;
        if (this.inSceltaTool) {
            this.listLavori.setVisible(false);
            this.listWLavori.setVisible(false);
            this.listTools.setVisible(true);
            lista = this.listTools;
            this.JTextAreaDescrizione.setText(this.welcome);
            this.changeButtons(this.Img_Exit, this.Img_Nulla, this.Img_Nulla,
                    this.Img_Freccia_su, this.Img_Freccia_giu, this.Img_Play);
        } else {
            if (this.inWl) {
                lista = this.listWLavori;
            } else {
                lista = this.listLavori;
            }
            if (this.abilitaCalibrazione) {
                this.changeButtons(this.Img_Exit, this.Img_Nulla, this.Img_Calibrazione,
                        this.Img_Freccia_su, this.Img_Freccia_giu, this.Img_Play);
            } else {
                this.changeButtons(this.Img_Exit, reload, this.Img_Nulla,
                        this.Img_Freccia_su, this.Img_Freccia_giu, this.Img_Play);
            }
        }
        lista.requestFocus();

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
            lista.makeVisible(selezionato);
            this.updateDescription(selezionato);
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
        String[] desWlScelta = null;
        this.jLabelNomeLavoro.setText(this.nomelavoro.trim());
        this.visualizzaContatori();

        if (this.lavoroConcluso) {
            this.jPanelStarted.setBackground(Color.BLUE);
            this.changeButtons(this.Img_Nulla, this.Img_Nulla, this.Img_Nulla,
                    this.Img_Exit, this.Img_reloadWork, this.Img_Grafico);
        }
        if (this.inErrore) {
            this.jPanelStarted.setBackground(Color.RED);
            this.changeButtons(this.Img_Ok, this.Img_Annulla, this.Img_Continua,
                    this.Img_Stop, this.Img_Pause, this.Img_Grafico);
        }
        if (!this.inErrore && !(this.lavoroConcluso || this.wlConclusa)) {
            if (this.jPanelStarted.getBackground() != Color.green) {
                this.jPanelStarted.setBackground(Color.WHITE);
            }
            this.changeButtons(this.Img_Nulla, this.Img_Nulla, this.Img_Nulla,
                    this.Img_Stop, this.Img_Pause, this.Img_Grafico);
        }

        //lavoro terminato e in errore
        if (this.lavoroConcluso && this.inErrore) {
            this.jPanelStarted.setBackground(Color.ORANGE);
        }
        jLabelNomeLavoro.setVisible(true);
        if (this.inWl) {
            jLabelNomeWL.setVisible(true); // Mostro la label della worklist solo se necessario
            setLabelWL();
        } else {
            jLabelNomeWL.setVisible(false); // Mostro la label della worklist solo se necessario
        }
        cambiaPannello(this.jPanelStarted);
    }

    public String getLavorodescrizione() {
        return Lavorodescrizione;
    }

    public java.awt.List getListInfo() {
        return this.listInfo;
    }

    public java.awt.List getListUsbFile() {
        return this.listUsbFile;
    }

    public java.awt.List getListLavori() {
        return listLavori;
    }

    public java.awt.List getListTools() {
        return listTools;
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

    public JLabel getJLabelLogo() {
        return this.jLabelLogo;
    }

    public String getLavoroScelto() {
        return this.lavoroScelto;
    }

    public void setLavoroScelto(String lavoroScelto) {

        if (lavoroScelto.contains("Errore")) {
            lavoroScelto = "0";
        }
        this.nomelavoro = this.lavoroScelto = lavoroScelto;
    }

    /**
     * Pannello che mostra il contenuto del file /tmp/warning.txt
     */
    private void PanelWarning() {
        this.changeButtons(this.Img_Exit, this.Img_Freccia_sx, this.Img_Freccia_dx,
                this.Img_Freccia_su, this.Img_Freccia_giu, this.Img_Nulla);
        this.listWarning.requestFocus(); // Per poter usare le frecce
        cambiaPannello(this.jPanelWarning);
    }

    /**
     * Alla pressione del pulsante che simula la freccia in su muovendosi sulla
     * lista
     */
    private void PulsanteSu() {
        int nrItem, nrCurItem;
        java.awt.List lista = null;
        boolean isSetup = false;
        // Qual'è il nome del pannello in primo piano ?
        switch (this.panCur) {
            case "start" -> {
                if (this.inSceltaTool) {
                    lista = this.listTools;
                } else {
                    if (this.inWl == false) {
                        lista = this.listLavori;
                    } else {
                        lista = this.listWLavori;
                    }
                }
            }
            case "list_file_usb" ->
                lista = this.listUsbFile;
            case "setup wifi" ->
                lista = this.listWifi;
            case "setup lan" ->
                lista = this.listLan;
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
            lista.requestFocus();
            if (nrCurItem == -1) { // nessun elemento selezionato
                nrCurItem = 0;
            }
            if (nrCurItem > 0) {
                nrCurItem--;
                lista.select(nrCurItem);
                lista.makeVisible(nrCurItem);
//                robot.keyPress(KeyEvent.VK_UP);
//                robot.keyRelease(KeyEvent.VK_UP);
            } else {
                nrCurItem = nrItem - 1; // Va all'ultimo Item
                lista.select(nrCurItem);
                lista.makeVisible(nrCurItem);
            }
            //lista.select(nrCurItem);
            // rendi visibile l'elemento selezionato
            //lista.makeVisible(nrCurItem);
            if (this.panCur.equals("start")) {
                updateDescription(nrCurItem);
            }
            if (isSetup) {
                this.jButtonPR3.setIcon(this.setIconSetup());//aggiorna il tipo di Icona per il pulsante
            }

        }//End LIsta not NULL
    }//End PulsanteSu

    /**
     * Simula la pressione del pulsante per scorrere la lista in giù
     */
    private void PulsanteGiu() {
        java.awt.List lista = null;
        boolean isSetup = false;
        // Qual'è il nome del pannello in primo piano ?

        switch (this.panCur) {
            case "start" -> {
                if (this.inSceltaTool) {
                    lista = this.listTools;
                } else {
                    if (this.inWl == false) {
                        lista = this.listLavori;
                    } else {
                        lista = this.listWLavori;
                    }
                }
            }
            case "setup wifi" ->
                lista = this.listWifi;
            case "list_file_usb" ->
                lista = this.listUsbFile;
            case "setup lan" ->
                lista = this.listLan;
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
            lista.requestFocus();
            if (nrCurItem == -1) { // nessun elemento selezionato
                nrCurItem = 0;
            }
            if (nrCurItem < nrItem - 1) {
                nrCurItem++;
                lista.select(nrCurItem);
                lista.makeVisible(nrCurItem);
//                robot.keyPress(KeyEvent.VK_DOWN);
//                robot.keyRelease(KeyEvent.VK_DOWN);
            } else {
                nrCurItem = 0;  // Ritorna al primo Item
                lista.select(nrCurItem);
                lista.makeVisible(nrCurItem);
            }
            if (this.panCur.equals("start")) {
                updateDescription(nrCurItem);
            }
            if (isSetup) {
                this.jButtonPR3.setIcon(this.setIconSetup());//aggiorna il tipo di Icona per il pulsante
            }
        }//End LIsta not NULL
    }//End PulsanteGiu

    /**
     * Simula la pressione del pulsante per scorrere la lista sx e dx
     */
    private void PulsanteSxDx(int sx_dx) {
        switch (this.panCur) {
            case "info" ->
                this.listInfo.requestFocus();
            case "setup wifi" ->
                this.listWifi.requestFocus();
            case "setup lan" ->
                this.listLan.requestFocus();
            case "warning" ->
                this.listWarning.requestFocus();
        }//EndSwitch

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
    }//End PulsanteSx

    /**
     * Pannello per la configurazione della LAN Legge il file setup_lan.txt
     */
    private void PanelSetupLan() {
        this.changeButtons(this.Img_Exit, this.Img_Freccia_sx, this.Img_Freccia_dx,
                this.Img_Freccia_su, this.Img_Freccia_giu, this.Img_Nulla);
        this.listLan.requestFocus(); // Per poter usare le frecce
        cambiaPannello(this.jPanelSetupLan);
    }

    /**
     * Pannello per il setup della WiFi Legge il file setup_wifi.txt
     */
    private void PanelSetupWifi() {
        this.changeButtons(this.Img_Exit, this.Img_Freccia_sx, this.Img_Freccia_dx,
                this.Img_Freccia_su, this.Img_Freccia_giu, this.Img_Nulla);
        this.listWifi.removeAll();
        this.listWifi.add("");
        this.listWifi.add("");
        this.listWifi.add("Searching for WiFi networks...");
        this.listWifi.requestFocus(); // Per poter usare le frecce
        cambiaPannello(this.jPanelSetupWiFi);
    }

    /**
     * Pannello che mostra il contenuto del file info.txt
     */
    private void PanelInfo() {
        this.changeButtons(this.Img_Exit, this.Img_Freccia_sx, this.Img_Freccia_dx,
                this.Img_Freccia_su, this.Img_Freccia_giu, this.Img_Nulla);
//        this.JTextAreaDescrizioneInfo.setVisible(true);
        this.listInfo.requestFocus(); // Per poter usare le frecce
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

    private void PanelCert() {
        esegui("certWifiStop.sh");
        this.changeButtons(this.Img_Exit, this.Img_start_WiFi, this.Img_start_log,
                //                this.Img_Nulla, this.Img_Nulla, this.Img_Nulla);
                this.Img_WiFi_2_4, this.Img_WiFi_5, this.Img_WiFi_Auto);
        this.jButtonPL2.setIcon(isAPOn ? this.Img_stop_WiFi : this.Img_start_WiFi);
        this.jButtonPL3.setIcon(isLogOn ? this.Img_stop_log : this.Img_start_log);
        listCert.removeAll();
        listCert.add("WiFi AP: " + (isAPOn ? "ON" : "OFF"));
        listCert.add("");
        listCert.add("LOG: " + (isLogOn ? "ON" : "OFF"));
        listCert.add("");
        listCert.add("Press button to choose mode");
        cambiaPannello(this.jPanelCert);
    }

    /**
     * Pannello per disegnare il grafico
     */
    public void PanelCanvas() {
//        this.jProgressBar.setVisible(false);
        cambiaPannello(this.gr);
        if (this.stato.equals(Static.STATO_CALIBRAZIONE) || this.stato.equals(Static.STATO_CALIBRAZIONE_TEST)) {
            this.changeButtons(this.Img_Nulla, this.Img_Nulla, this.Img_Nulla,
                    this.Img_Ok, this.Img_Cancel, this.Img_Nulla);
            if (this.stato.equals(Static.STATO_CALIBRAZIONE)) {
                this.set_jLabel_B_L("Calib.mode");
            } else {
                this.set_jLabel_B_L("Calib.test");
            }

        } else {

            if (this.inErrore) {
                this.changeButtons(this.Img_Ok, this.Img_Annulla, this.Img_Continua,
                        this.Img_Stop, this.Img_Pause, this.Img_Estende);
            } else if (this.lavoroConcluso) {
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
     * aggiornaListWarning carica eventuali Warning dal file warning.txt
     *
     * @param jLabelWarning
     */
    public void setjLabelWarning(JLabel jLabelWarning) {
        this.jLabelWarning = jLabelWarning;
    }

    /**
     * Aggiorna la lista dei warning
     *
     * @param lista
     */
    public void aggiornaListWarning(List<String> lista) {
        this.listWarning.removeAll();
        RefreshList(this.listWarning, lista);
        this.listWarning.repaint();
    } //End AggiornaWarning

    /**
     * aggiornaListLan carica eventuali Informazioni dal file /tmp/setup_lan.txt
     *
     * @param lista
     */
    public void aggiornaListLan(List<String> lista) {
        this.listLan.removeAll();
        RefreshList(this.listLan, lista);
        this.listLan.repaint();
    }//End aggiornaListLan

    /**
     * carica eventuali Informazioni dal file /tmp/status_wifi
     *
     * @param lista
     */
    public void aggiornaListWiFi(List<String> lista) {
        if (panCur.equals("setup wifi")) {
            this.listWifi.removeAll();
            RefreshList(this.listWifi, lista);
            this.listWifi.repaint();
        }
    }//End aggiornaListWiFi

    /**
     * aggiornaLavori
     *
     * @param lista
     */
    public void aggiornaLavori(List<String> lista) {
        String nomeLavoroRiga;
        String descrizioneRiga;
        String workCanStart;
        int cntLotti;
        int limLottiRiga;
        int cntPezzi;
        int limPezziRiga;
        int contatoreRighe = 1;
        String[] lavoroSplit;
        try {
            if (lista.get(0).contains("errore")) {  // sintassi nomelavoro, lotti, pezzi, descrizione, canStart, UDLotti, UDPezzi, cntLotti, cntPezzi
                lista.clear();
                lista.add(" no count limits§-1§-1§work without counting limits§1§+§+§1§0");
                Static.debug("ERROR: Empty work list", 2);
                // todo verificare se in caso di file lavori.txt vuoto occore fermarsi
            }
            List<String> elencoTxt = new ArrayList<>();
            elencoDesLavoro = new ArrayList<>();
            elencoLavori = new ArrayList<>();
            elencoLavoriCompleto = new ArrayList<>();
            elencoDesLavoroCompleto = new ArrayList<>();
            this.elencoLavori.clear();
            this.indiceLavoroScelto = 0;
            for (String riga : lista) {
                lavoroSplit = riga.split("§"); // nomeLavoro, limLotti, limPezzi, descrizione, canStart, UDLotti, UDPezzi
                this.elencoLavoriCompleto.add(lavoroSplit);
                nomeLavoroRiga = lavoroSplit[0];
                limLottiRiga = Integer.parseInt(lavoroSplit[1]);
                limPezziRiga = Integer.parseInt(lavoroSplit[2]);
                descrizioneRiga = lavoroSplit[3];
                workCanStart = lavoroSplit[4];
                /*
                // non servono per costruire le stringhe dell'elenco ma solo quando il lavoro è stato scelto
                UDLotti = lavoroSplit[5];
                UDPezzi = lavoroSplit[6];
                 */
                cntLotti = Integer.parseInt(lavoroSplit[7]);
                cntPezzi = Integer.parseInt(lavoroSplit[8]);
                if (limLottiRiga == -1) { // Lavoro senza limiti -> visualizzo solo il nome
                    elencoTxt.add(nomeLavoroRiga);
                } else {
                    elencoTxt.add(nomeLavoroRiga + " L=" + cntLotti + "/" + limLottiRiga + " F=" + cntPezzi + "/" + limPezziRiga);
                }

                this.elencoLavori.add(lavoroSplit);
                if (cntLotti > 1 || cntPezzi > 0) { // lavoro in pausa
                    descrizioneRiga = "(Paused) - " + descrizioneRiga;
//                    if (contatoreRighe == 1) {
//                        this.in_pausa = true;
//                    }
                }
                if (workCanStart.equals("0")) { // lavoro non avviabile
                    descrizioneRiga = "(Not calibrated) - " + descrizioneRiga;
                }
                this.elencoDesLavoroCompleto.add(descrizioneRiga);
                this.elencoDesLavoro.add(descrizioneRiga);
                if (this.lavoroScelto.equals(nomeLavoroRiga)) {
                    this.indiceLavoroScelto = contatoreRighe - 1;
                }
                contatoreRighe++;
            }//EndFor

            RefreshList(listLavori, elencoTxt);
            this.listLavori.select(this.indiceLavoroScelto);
            this.listLavori.makeVisible(this.indiceLavoroScelto);
            if (panCur.equals("start")) {
                updateDescription(this.indiceLavoroScelto);
            }

        } catch (Exception e) {
            Static.debug("Error reading list of works:" + lista.toString() + " - " + e, 2);
        }
    }//End aggiornaLavori

    /**
     * aggiorna Lista WL Esempio di formato del JSON
     *
     * [
     * {"cntPezzi":0,"cntCicli":1,"limCicli":2,"indice":0,"NomeWL":"WL
     * 007","Descrizione":"WorkList senza lavori","nomeLavoro":"
     * ","LimPezzi":1,"cntLotti":1,"LimLotti":1,"condizioneAvvio":"0"},
     * {"cntPezzi":0,"cntCicli":1,"limCicli":1,"indice":0,"NomeWL":"WLOggi","Descrizione":"prima
     * Work List","nomeLavoro":"Lav
     * 1new","LimPezzi":3,"cntLotti":1,"LimLotti":1,"condizioneAvvio":"1"} ]
     *
     * //0 nomeWl, 1 cntCicli, 2 limCicli,3 Descrizione, 4 Avviabile ? (-1
     * almeno un lavoro non calibrato, 0 lista vuota) // 5 NomeLavoro, 6
     * contaPezzi, 7 limPezzi, 8 contaLotti, 9 limLotti, 10 indice per segnalare
     * se è in pausa*
     */
    public void aggiornaWl(JSONArray listaWL) {
        List<String> elencoTxt = new ArrayList<>();
        elencoDesWl = new ArrayList<>();//Descrizione di ogni WL
        elencoWl = new ArrayList<>();//Elenco WL parte di sopra 
        elencoWlCompleto = new ArrayList<>();
        this.indiceWLScelta = 0;
        this.listWLavori.removeAll();
        this.elencoWl.clear();
        int contatoreRighe = 1;
        String nomeWl = "";
        String[] OldDesWL;//Da modificare e creare JSONOBJ
        int cntCicli;
        int limCicli;
        String descrizione;
        String[] WLSplit;
        JSONArray elencoWL = listaWL;
        try {
            for (int i = 0; i < listaWL.length(); i++) {
                JSONObject a = listaWL.getJSONObject(i);
                nomeWl = a.getString("NomeWL");//0
                cntCicli = a.getInt("cntCicli");//1
                limCicli = a.getInt("limCicli");//2

                descrizione = a.getString("Descrizione");
                int condizioneAvvio = a.getInt("condizioneAvvio");
                if (condizioneAvvio == 0) { // lavoro non avviabile
                    descrizione = "Empty Work List - " + descrizione;
                }
                if (condizioneAvvio == -1) { // lavoro non avviabile
                    descrizione = "At least one work is Not calibrated - " + descrizione;
                }
                this.elencoDesWl.add(descrizione);
                this.elencoWlCompleto.add(a);//Lista OBJ JSON
                /*
                     * //0 nomeWl, 1 cntCicli, 2 limCicli,3 Descrizione, 4 Avviabile ? (-1
     * almeno un lavoro non calibrato, 0 lista vuota) // 5 NomeLavoro, 6
     * contaPezzi, 7 limPezzi, 8 contaLotti, 9 limLotti, 10 indice per segnalare
     * se è in pausa*
                 */
                elencoTxt.add(nomeWl + " C=" + cntCicli + "/" + limCicli);
                OldDesWL = (a.getString("NomeWL") + "§"//0
                        + a.getInt("cntCicli") + "§" //1
                        + a.getInt("limCicli") + "§"//2
                        + a.getString("Descrizione") + "§"//3
                        + a.getInt("condizioneAvvio") + "§"//4
                        + a.getString("nomeLavoro") + "§"//5
                        + a.getInt("cntPezzi") + "§"//6
                        + a.getInt("LimPezzi") + "§"//7
                        + a.getInt("cntLotti") + "§"//8
                        + a.getInt("LimLotti") + "§"//9
                        + a.getInt("indice") + "§"//10
                        + a.getInt("totLavori") + "§"//11
                        + a.getInt("cntTotPezzi") + "§"//12
                        + a.getBoolean("inPausa")).split("§");//13
                this.elencoWl.add(OldDesWL);//Elenco di stringhe da visualizzare
                if (this.WLscelta.equals(nomeWl)) {
                    this.indiceWLScelta = contatoreRighe - 1;
                }
                contatoreRighe++;
            }

        } catch (Exception ex) {
            Static.debug("Error reading list of wl:" + nomeWl + " - " + ex, 2);
        }

        RefreshList(listWLavori, elencoTxt);
        this.listWLavori.select(this.indiceWLScelta);
        this.listWLavori.makeVisible(this.indiceWLScelta);
        if (panCur.equals("start")) {
            updateDescription(this.indiceWLScelta);
        }
    }//End aggiornaWLavori

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
    public void PanelSetup() {
        this.esegui("aggiorna_nm_list");
//        this.changeButtons(this.Img_Exit, this.Img_Lan, this.Img_WiFi,
//                this.Img_Freccia_su, this.Img_Freccia_giu, setIconSetup());
        this.changeButtons(this.Img_Exit, this.Img_Lan, this.Img_WiFi,
                this.Img_Freccia_su, this.Img_Freccia_giu, setIconSetup());
        this.listSetupNM.requestFocus(); // Per poter usare le frecce
        cambiaPannello(this.jPanelSetup);
        this.jButtonPR3.setEnabled(true);
    }

    /**
     * Pannello di Lista file penDrive USB
     */
    public void PanelUsb() {
        this.changeButtons(this.Img_Exit, this.Img_backup, this.Img_restore,
                this.Img_Freccia_su, this.Img_Freccia_giu, this.Img_firmware);
        this.listUsbFile.requestFocus(); // Per poter usare le frecce   
        this.jButtonPL3.setEnabled(false);
        this.jButtonPR3.setEnabled(false);
        cambiaPannello(this.jPanelUsb);
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
     * visualizza i dati aggiornati dei contatori daF FileWorker aggiornato il
     * codice per visualizzare un eventuale scelta count down
     */
    public void visualizzaContatori() {
        int tmpLotto = 1;
        int tmpPezzi = 0;
        this.jLabelValidi.setText("" + tiriValidi);
        this.jLabelAnnullati.setText("" + tiriAnnullati);
        this.jLabelErrati.setText("" + tiriErrati);

        if (this.limPezzi == -1) {
            this.jLabelPezziNoLimits.setText("" + this.tiriNelLotto);
        } else {
            if (this.UDLotti.equals("+")) {
                this.jLabelContatoreLotti.setText(this.lotto + "/" + this.limLotti);
            } else {
                tmpLotto = this.limLotti - this.lotto + 1;
                this.jLabelContatoreLotti.setText(tmpLotto + "/" + this.limLotti);
            }
            if (this.UDPezzi.equals("+")) {
                this.jLabelContatorePezzi.setText(this.tiriNelLotto + "/" + this.limPezzi);
            } else {
                tmpPezzi = this.limPezzi - this.tiriNelLotto;
                this.jLabelContatorePezzi.setText(tmpPezzi + "/" + this.limPezzi);
            }

            // calcolo dei tiri complessivi per l'avanzamento della barra
            //Nel caso di una WL la percentuale avanza rispetto al totale dei tiri da fare
            // totale dei tiri validi + errati 
            if (isInWl()) {
                this.jProgressBar.setValue(this.tiriErrati + this.tiriValidi);

            } else {
                this.jProgressBar.setValue(this.tiriNelLotto + ((this.lotto - 1) * this.limPezzi));
            }
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

    @SuppressWarnings("UseSpecificCatch")
    void updateSensori(String Valori) {
        String[] arrayValori;
        DecimalFormat df = new DecimalFormat("0.00");// solo due cifre decimali
        if (Valori.startsWith("error")) {
//            this.jLabel_msg.setText("Air pressure not updated !"); // Aggiungere eventualmente un contatore
        } else {
            arrayValori = Valori.split(",");
            try {
                // Calcolo esatto della pressione in base al grafico di risposta del sensore emc
                // dalle specifiche il fattore di riduzione doveva essere 4, ma confrontando i valori
                // con un pressostato già tarato si è individuato un fattore di 3.85
                this.pressione_aria_in = ((Float.parseFloat(arrayValori[4]) - 1) * 10 / 3.85f);
                this.temp_rpi = Float.valueOf(arrayValori[0]);
                this.temp_io_board = Float.valueOf(arrayValori[1]);
                this.v_in = Float.valueOf(arrayValori[2]);
                this.v_rpi = Float.valueOf(arrayValori[3]);
                if (!(this.pressione_aria_in == null)) {
                    if (this.pressione_aria_in < 0) {
                        this.pressione_aria_in = 0f;
                    }
                    if (pressione_aria_in <= 2) { // aria in ingresso non collegata
                        this.jLabel_msg.setBackground(java.awt.Color.BLACK);
                        this.jLabel_msg.setForeground(java.awt.Color.WHITE);
                        this.jLabel_msg.setText("INC.AIR NOT PRESENT ");
                    } else if (pressione_aria_in <= this.sogliaMin) {
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
                if (this.panCur.equals("info")) {
                    this.JTextAreaDescrizioneInfo.setText("");
                    if (this.pressione_aria_in == null) {
                        this.JTextAreaDescrizioneInfo.append("Pressione aria Null");
                    }
                    try {
                        this.JTextAreaDescrizioneInfo.append(Static.dtf.format(LocalDateTime.now()) + "\n");

                        this.JTextAreaDescrizioneInfo.append("Air: " + df.format(this.pressione_aria_in) + " bar\n");
                        this.JTextAreaDescrizioneInfo.append("Vcpu: " + this.v_rpi.toString() + "V - Vin: " + this.v_in.toString() + "V\n");
                        this.JTextAreaDescrizioneInfo.append("Board T.: " + this.temp_io_board.toString() + "°C - CPU T.: " + this.temp_rpi.toString() + "°C\n");
                    } catch (Exception e) {
                        Static.debug("Error reading info file\n" + e.toString(), 3);
                    }
                    try {//totale spazio disco 6, spazio usato 7, spazio libero 8
                        float free, tot, percent;
                        free = Float.parseFloat(arrayValori[8]);
                        tot = Float.parseFloat(arrayValori[6]);
                        percent = (free / tot * 100);
                        this.JTextAreaDescrizioneInfo.append("Disk free " + arrayValori[8] + "/" + arrayValori[6] + " GB (" + df.format(percent) + "%)");
                    } catch (Exception e) {
                        Static.debug("Error reading info file\n" + e.toString(), 3);
                    }
//                if (this.panCur.equals("info")) {
//                   // this.JTextAreaDescrizioneInfo.repaint();
//                }else{
//                    this.JTextAreaDescrizioneInfo.setVisible(false);
                }
            } catch (Exception e) {
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
     * setNomeDevice set Label nome del device
     *
     * @param nd Nome del device letto dal file nome_device.txt
     */
    public void setNomeDevice(String nd) {
        if (nd.contains("Errore")) {
            nd = "CG-0000-00";
        }
        this.nomeDevice = nd;
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

//    public void mostraCurva() {
//        cambiaPannello(this.gr);
//        this.repaint();
//        //esegui("curva");
//    }
    public JLayeredPane getjLayeredPaneCenter() {
        return this.jLayeredPaneCenter;
    }

    /**
     * Lista l'elenco dei device della comunicazione Aggiunge OFF / ON Per
     * distinguere se sono connessi o meno
     *
     * @param list_nm_con Elenco dei device
     */
    public void setListNmCon(List<String> list_nm_con) {
        this.listSetupNM.removeAll();
        for (int c = 0; c < list_nm_con.size(); c++) {
            this.listSetupNM.add(list_nm_con.get(c).toString());
        }

        this.listSetupNM.select(0);
        this.listSetupNM.getVisibleIndex();
        this.listSetupNM.repaint();
    }

    /**
     * Lista l'elenco dei files sulla PenDrive USB
     *
     * @param list_usb_files Elenco dei device
     */
    public void setListUsbFiles(List<String> list_usb_files) {
        this.listUsbFile.removeAll();
        for (int c = 0; c < list_usb_files.size(); c++) {
            this.listUsbFile.add(list_usb_files.get(c).toString());
        }
        this.listUsbFile.select(0);
        this.listUsbFile.getVisibleIndex();
        this.listUsbFile.repaint();
    }

    /**
     * Aggiorna lista Info
     *
     * @param info la lista passata per aggiornare il campo Info
     */
    public void setListInfo(List info) {
        this.listInfo.removeAll();
        for (int c = 0; c < infoAggiuntive.size(); c++) {
            this.listInfo.add(infoAggiuntive.get(c).toString());
        }
        if (this.panCur.equals("info")) {
            this.listInfo.repaint();
        }
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
    public final void esegui(String operazione) {
        this.doWorker.set_operation(operazione);
        try {
            this.doWorker.doInBackground();
        } catch (Exception ex) {
            Static.debug("Error running background operation " + operazione + ": " + ex, 2);
        }
    }

    /**
     * Uscita dal programma
     */
    public void exit() {
//       System.exit(0); // Manda in crash la JVM -> VERIFICARE
        return;
    }

    /**
     *
     * @param inPausa
     */
    public void setInPausa(boolean inPausa) {
        this.in_pausa = inPausa;
    }

    /**
     *
     * @return se è stato impostato lo stato in pausa
     */
    public boolean isInPausa() {
        return in_pausa;
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
     * Imposta se chiedere o meno conferma quando il tiro è errato per la scelta
     * Continua, annulla accetta
     *
     * @param confermaRispErrore
     */
    public void setConfermaRispErrore(boolean confermaRispErrore) {
        this.confermaRispErrore = confermaRispErrore;
    }

    /**
     *
     * @return se devo chiedere o meno la conferma per Continua, annulla accetta
     */
    public boolean isiConfermaRispErrore() {
        return this.confermaRispErrore;
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
    void setConfermaStopPausa(boolean si_o_no) {
        this.confermaStopPausa = si_o_no;
    }

    /**
     *
     * @return si o no conferma alla scelta STOP o PAUSA
     */
    boolean isConfermaStopPausa() {
        return this.confermaStopPausa;
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
     * Imposta il valore che conta il nr dei Lotti
     *
     *
     * @param lotto
     */
    public void setLotto(int lotto) {
        this.lotto = lotto;
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

    public void impostaLavoroScelto() {
        int idLavoro = this.listLavori.getSelectedIndex();
        try {
            this.lavoroScelto = this.elencoLavori.get(idLavoro)[0];
            this.limLotti = Integer.parseInt(this.elencoLavori.get(idLavoro)[1]);
            this.limPezzi = Integer.parseInt(this.elencoLavori.get(idLavoro)[2]);
            this.UDLotti = this.elencoLavori.get(idLavoro)[5];
            this.UDPezzi = this.elencoLavori.get(idLavoro)[6];
            this.esegui("scelto_lavoro");
        } catch (NumberFormatException e) {
            Static.debug("nr_lotti_da_fare null !\n", 2);
            this.limLotti = 1;
            this.limPezzi = 1;
        }
    }

    /**
     *
     */
    public void impostaWLScelta() {
        int idWL = this.listWLavori.getSelectedIndex();
        this.WLscelta = "";
        this.limCicli = 1;
        this.cntCicli = 1;
        try {
            this.WLscelta = this.elencoWl.get(idWL)[0];
            this.cntCicli = Integer.parseInt(this.elencoWl.get(idWL)[1]);
            this.limCicli = Integer.parseInt(this.elencoWl.get(idWL)[2]);
            this.limLotti = Integer.parseInt(this.elencoWl.get(idWL)[9]);
            this.limPezzi = Integer.parseInt(this.elencoWl.get(idWL)[7]);
            this.cntLotti = Integer.parseInt(this.elencoWl.get(idWL)[8]);
            this.cntPezzi = Integer.parseInt(this.elencoWl.get(idWL)[6]);
            this.nomelavoro = this.elencoWl.get(idWL)[5];
            this.tiriTotali = Integer.parseInt(this.elencoWl.get(idWL)[12]);//totale dei pezzi da lavorare
            this.nrDiLavori = Integer.parseInt(this.elencoWl.get(idWL)[11]);//totale dei lavori da fare
            //Dalla Lista dei lavori quale lavoro è associato quello da avviare
            //Non è detto che sia il primo se è una WL in pausa
            this.nomelavoro = this.elencoWl.get(idWL)[5];
            for (int i = 0; i < this.listLavori.getItemCount(); i++) {
                String Nomelav = this.elencoLavori.get(i)[0];
                if (Nomelav.equals(this.nomelavoro)) {
                    this.listLavori.select(i);
                    this.listLavori.makeVisible(i);
                    break;
                }
            }
            avviaWL();
            this.esegui("scelta_wl");
        } catch (NumberFormatException e) {
            Static.debug("nome WLCicli null !\n", 2);
            this.limLotti = 1;
            this.limPezzi = 1;
            this.limCicli = 1;
        }
    }

    /**
     * Avviare il lavoro scelto
     */
    public void avviaLavoro() {
        try {
            setLavoroConcluso(false);
            setInErrore(false);
            this.richiesta = Static.RICHIESTA_AVVIO_LAVORO;
        } catch (Exception ex) {
            Static.debug("Error starting work " + lavoroScelto + " ! " + ex.toString(), 2);
        }
    }

    /**
     * Avviare la WorkList
     */
    public void avviaWL() {
        try {
            setWLConclusa(false);
            this.richiesta = Static.RICHIESTA_AVVIO_WL;
        } catch (Exception ex) {
            Static.debug("Error starting WorkList " + this.WLscelta + " ! " + ex.toString(), 2);
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

    public boolean isWLConclusa() {
        return wlConclusa;
    }

    public void setWLConclusa(boolean wlConclusa) {
        this.wlConclusa = wlConclusa;
    }

    public boolean isLavoroConcluso() {
        return lavoroConcluso;
    }

    public void setLavoroConcluso(boolean lavoroConcluso) {
        this.lavoroConcluso = lavoroConcluso;
    }

    /**
     * Imposta le Label diversamente se il lavoro scelto è quello senza Limiti
     */
    public void impostaLabelContatori() {
        if (this.limPezzi == -1) { // Lavoro senza fine
            this.jLabelDesContatoreLotti.setVisible(false);
            this.jLabelContatoreLotti.setVisible(false);
            this.jLabelDesContatorePezzi.setVisible(false);
            this.jLabelContatorePezzi.setVisible(false);
            this.jLabelDesPezziNoLimits.setVisible(true);
            this.jLabelPezziNoLimits.setVisible(true);
            //this.jPanelStarted.setBackground(Color.LIGHT_GRAY);
            // Imposto la dimensione della barra percentuale
            this.jProgressBar.setMaximum(0);
            this.jProgressBar.setVisible(false);
        } else {
            this.jLabelDesContatoreLotti.setVisible(true);
            this.jLabelContatoreLotti.setVisible(true);
            this.jLabelDesContatorePezzi.setVisible(true);
            this.jLabelContatorePezzi.setVisible(true);
            this.jLabelDesPezziNoLimits.setVisible(false);
            this.jLabelPezziNoLimits.setVisible(false);
            // Imposto la dimensione della barra percentuale
            if (isInWl()) {
                this.jProgressBar.setMaximum(this.tiriTotali);
            } else {
                this.jProgressBar.setMaximum(this.limLotti * this.limPezzi);
            }
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
    public void setInErrore(boolean statoErrore) {
        this.inErrore = statoErrore;
    }

    /**
     * Il sistema in Freeze ?
     *
     * @param stato True o False
     */
    public void setInFreeze(boolean stato) {
        this.inFreeze = stato;
    }

    /**
     *
     *
     * /**
     * avvia la fase di calibrazione
     */
    void avviaCalibrazione() {
        gr.setPrimoGiro(2);
        gr.setCurvaDiRiferimento("0");
        PanelCanvas();
    }

    /**
     * avvia la fase di calibrazione
     */
    void avviaCalibrazioneTest() {
        gr.setPrimoGiro(0);
        this.set_jLabel_B_L("Cal. Test");
        gr.setCurvaDiRiferimento(Curva);
        PanelCanvas();
    }

    /**
     * conclude la fase di calibrazione
     */
    void fineCalibrazione() {
        PanelMain();
    }

    /**
     * Control una volta preparato l'"ambiente" per il lavoro consente l'avvio
     */
    public void lavoroPronto() { // e' qui....
        if (this.panCur.equals("started")) {
            PanelStarted();
        }
    }

    /**
     * Control una volta preparato l'"ambiente" per il lavoro consente l'avvio
     */
    public void wlPronta() { // e' qui....Manca il nome del lavoro
        if (this.panCur.equals("started")) {
            PanelStarted();
        }
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
     * Stato della WiFi Green OK - Red OFF
     *
     * @param stato
     */
    void setWiFiIndicator(boolean stato) {
        this.wifiIndicator = stato;
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
     * Imposta il fattore di conversione
     *
     * @param um
     */
    void setConversion(int conversion) {
        this.conversion = conversion;
    }

    /**
     * Restituisce il fattore di conversione
     */
    int getConversion() {
        return this.conversion;
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
        this.pannelloPrecedente = this.panCur;
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
        for (int i = 0; i < this.listWLavori.getItemCount(); i++) {
            wll = this.listWLavori.getItem(i);
            if (wll.contains(WLscelta)) {
                this.listWLavori.select(i);
                break;
            }
        }
    }

    public int getWLnrCicli() {
        return limCicli;
    }

    public void setWLnrCicli(int WLnrCicli) {
        this.limCicli = WLnrCicli;
    }

    /**
     * Aggiorna l'area inferiore contenente la descrizione dell'elemento
     * selezionato Viene controllato se sia o meno avviabile il lavoro o WL in
     * base alla presenza o meno di un lavoro non calibrato, se sì si colora la
     * descrizione di giallo. Viene anche verificato se il lavoro o la WL non
     * siano già avviate se sì la descrizione si colore di azzurro Occorre
     * memorizzare le variabili: cntTiri, limTiri, cntLotti, LimLotti,
     * cntCicli,limCicli per veridicare se è un lavoro in pausa (variabile
     * field) e cambiare l'icona del pannello la 2^ a sx con il reload o Nulla
     * se è un lavoro che iniza da capo
     *
     * file wl.txt 0) NomeWl 1) cntCicli 2) limCicli 3) descrizione 4) se è
     * utilizzabile ( -1 ha nell'elenco un lavoro non calibrato, 1 OK , 0 non ci
     * sono lavori) 5) Nomelavoro 6) cntPezzi 7) LimPezzi 8) cntLotti 9)
     * LimLotti 10) indice se è > 0 vuole dire che la WL è in pausa altrimenti 0
     * 11) totLavori
     *
     * WLOggi§ 1§ 1§ prima Work List§ 1§ Lav 1new§ 0§ 3§ 1§ 1§ 3
     *
     * @param nrCurItem Indice dell'elemento selezionato
     */
    private void updateDescription(int nrCurItem) {
        try {
            this.jButtonPL2.setIcon(this.Img_Nulla);//aggiorna il tipo di Icona per il pulsante
            this.jButtonPL2.setEnabled(false);
            this.jButtonPR3.setIcon(this.Img_Play);//aggiorna il tipo di Icona per il pulsante
            this.jButtonPR3.setEnabled(true);
            if (inSceltaTool) {
                if (nrCurItem < this.elencoTools.size()) { // Per prevenire eventuali errori
                    this.JTextAreaDescrizione.setText(this.elencoTools.get(nrCurItem) + " - "
                            + this.elencoDesTools.get(nrCurItem));
                }
            } else {
                if (this.inWl) {
                    // 0 nomeWl, 1 cntCicli, 2 limCicli,3 Descrizione, 4 Avviabile ? (-1 almeno un lavoro non calibrato, 0 lista vuota)
                    // 5 NomeLavoro, 6 contaPezzi, 7 limPezzi, 8 contaLotti, 9 limLotti, 10 indice per segnalare se è in pausa 11)tot lavori

                    if (nrCurItem < this.elencoWl.size()) { // Per prevenire eventuali errori
                        String[] elementoSelezionato = this.elencoWl.get(nrCurItem);
                        String avviabile = " \n";
                        this.limPezzi = Integer.parseInt(elementoSelezionato[7]);
                        this.limLotti = Integer.parseInt(elementoSelezionato[9]);
                        this.cntCicli = Integer.parseInt(elementoSelezionato[1]);
                        this.limCicli = Integer.parseInt(elementoSelezionato[2]);
                        if (elementoSelezionato[4].equals("-1")) {
                            avviabile += "Not calibrated - ";
                        } else if (elementoSelezionato[4].equals("0")) {
                            avviabile += "Empty WL - ";
                        }
                        this.nomelavoro = elementoSelezionato[5];//Nome del lavoro da avviare
                        String testo = elementoSelezionato[0] + " "//NomeWL
                                + "C=" + this.cntCicli//CntCicli
                                + "/" + this.limCicli//LimCicli
                                + avviabile
                                + elementoSelezionato[3];//Descrizione
                        if (!elementoSelezionato[4].equals("0")) {//Se è vuota non scrivere i valori del lavoro
                            testo += " ("//Descrizione;
                                    + this.nomelavoro + " " + (Integer.parseInt(elementoSelezionato[10]) + 1) + "/" + elementoSelezionato[11] + "\n"//Nome del lavoro da avviare indice da avviare / tot lavori
                                    + " L=" + elementoSelezionato[8] + "/" + this.limLotti
                                    + " F=" + elementoSelezionato[6] + "/" + this.limPezzi + " tot F=" + elementoSelezionato[12] + ")";//tiri fatti/totale totale di tutti i lavori dei pezzi da fare
                        }

                        this.JTextAreaDescrizione.setText(testo);

                        if (elementoSelezionato[4].equals("1")) {
                            this.JTextAreaDescrizione.setBackground(Color.white);
                            this.in_pausa = false;
                        }
                        // Non avviabile
                        if (elementoSelezionato[4].equals("-1") || elementoSelezionato[4].equals("0")) {
                            this.JTextAreaDescrizione.setBackground(Color.yellow);
                            this.jButtonPR3.setIcon(this.Img_Nulla);//aggiorna il tipo di Icona per il pulsante
                            this.jButtonPR3.setEnabled(false);
                            this.in_pausa = false;
                        }
                        //Indice di partenza
                        if (elementoSelezionato[13].equals("true")) {   // Se siamo in pausa
                            this.JTextAreaDescrizione.setBackground(Color.cyan);
                            this.jButtonPL2.setIcon(this.Img_restart);//aggiorna il tipo di Icona per il pulsante
                            this.jButtonPL2.setEnabled(true);
                            this.in_pausa = true;
                        }

                    }
                } else {

                    if (nrCurItem < this.listLavori.getItemCount()) { // Per prevenire eventuali errori
                        this.JTextAreaDescrizione.setText(listLavori.getItem(nrCurItem) + " - " + this.elencoDesLavoro.get(nrCurItem));
                        // Se il lavoro non è avviabile
                        if (this.elencoLavoriCompleto.get(nrCurItem)[4].equals("0")) {
                            this.JTextAreaDescrizione.setBackground(Color.yellow);
                            this.jButtonPR3.setIcon(this.Img_Nulla);//aggiorna il tipo di Icona per il pulsante
                            this.jButtonPR3.setEnabled(false);
                        } else {
                            //Verifica se cntLotti è diverso da 1 oppure cntpezzi da zero
                            if (!this.elencoLavoriCompleto.get(nrCurItem)[7].equals("1")
                                    || !this.elencoLavoriCompleto.get(nrCurItem)[8].equals("0")) {
                                this.JTextAreaDescrizione.setBackground(Color.CYAN);
                                this.jButtonPL2.setIcon(this.Img_restart);//aggiorna il tipo di Icona per il pulsante
                                this.jButtonPL2.setEnabled(true);
                                this.in_pausa = true;
                            } else {
                                this.JTextAreaDescrizione.setBackground(Color.white);
                                this.in_pausa = false;
                            }
                        }
                    }
                }
                //Se è un lavoro in pausa ridisegna l'icona del pulsante PL2
            }
        } catch (Exception ex) {
            Static.debug("Error Update Description WL: " + " - " + ex.toString(), lotto);
        }
    }

    String getUDLotti() {
        return this.UDLotti;
    }

    String geUDPezzi() {
        return UDPezzi;
    }

    void setUDLotti(String UDLotti) {
        this.UDLotti = UDLotti;
    }

    void setUDPezzi(String UDPezzi) {
        this.UDPezzi = UDPezzi;
    }

    void setSensoreCollegato(boolean sensoreCollegato) {
        this.sensoreCollegato = sensoreCollegato;
    }

    boolean getSensoreCollegato() {
        return this.sensoreCollegato;
    }

    void setConversion(String get) {
        throw new UnsupportedOperationException("Not supported yet."); // Generated from nbfs://nbhost/SystemFileSystem/Templates/Classes/Code/GeneratedMethodBody
    }

    void updateNmButtons() {
        this.changeButtons(this.Img_Exit, this.Img_Lan, this.Img_WiFi,
                this.Img_Freccia_su, this.Img_Freccia_giu, setIconSetup());
    }

    public String getRichiesta() {
        return richiesta;
    }

    public void setRichiesta(String richiesta) {
        this.richiesta = richiesta;
    }

    private void cambiaBandaWifi(String string) {
        String cmd = "";
        switch (string) {
            case "2.4" ->
                cmd = "certWifi_2.4GHz.sh";
            case "5" ->
                cmd = "certWifi_5GHz.sh";
            case "auto" ->
                cmd = "certWifi_auto.sh";
        }
        esegui(cmd);
    }

    /**
     * Aggiorna i dati necessari alla certificazione letti dai relè e dai due
     * sensori
     *
     * @param certSens Stringa 0.537,0.000,0,0,0,0,ON,OFF,OFF,OFF Letta dal file
     * che crea lo script Python
     */
    void aggiornaListSens(String certSens) {
        try {
            listSens.removeAll();
            String[] arraySens = certSens.split(",");
            if (arraySens.length > 8) {
                listSens.add("Sens1 Tool: " + arraySens[0] + " V");
                listSens.add("Sens2 Aria: " + arraySens[1] + " V");
                listSens.add("In:    In1=" + arraySens[2] + " In2=" + arraySens[3] + " In3=" + arraySens[4] + " In4=" + arraySens[5]);
                listSens.add("Out: Air=" + arraySens[6] + " G=" + arraySens[7] + " Y=" + arraySens[8] + " R=" + arraySens[9]);
            }
        } catch (Exception e) {
            Static.debug("Error reading cert.status: " + certSens + " - " + e.toString(), lotto);
        }
    }

    void aggiornaTools(List<String> leggiFileElenco) {
        String[] toolSplit;
        elencoTools = new ArrayList<>();
        elencoDesTools = new ArrayList<>();
        for (String riga : leggiFileElenco) {
            toolSplit = riga.split("§"); // BarcodeUtensile, Nome, Descrizione
            this.elencoTools.add(toolSplit[0] + ", " + toolSplit[1]);
            this.elencoDesTools.add(toolSplit[2]);
        }
        RefreshList(listTools, elencoTools);
    }

    public void chiediConfermaReset() {
        scelta = Static.RICHIESTA_RESET_SYSTEM;
        this.AlertDialogWhat = "Confirm FACTORY RESET ?";
        this.jLabelDialog.setText(AlertDialogWhat);
        PanelDialog();
    }

    public boolean isInSceltaTool() {
        return inSceltaTool;
    }

    public void setInSceltaTool(boolean inSceltaTool) {
        this.inSceltaTool = inSceltaTool;
    }

    public List getInfoAggiuntive() {
        return infoAggiuntive;
    }

    public void setInfoAggiuntive(List infoAggiuntive) {
        this.infoAggiuntive.clear();
        this.infoAggiuntive = infoAggiuntive;
    }

    /**
     * Aggiorna la lista delle stringhe per la certificazione
     */
    public void aggiornaListCert(List<String> certInfo) {
        if (certInfo == null) {
            int fine = listCert.getItemCount();
            listCert.remove(fine - 1);
            listCert.remove(fine - 2);
            listCert.remove(fine - 3);
        } else {
            listCert.removeAll();
            listCert.add("WiFi AP mode: " + wifiMode);
            for (String string : certInfo) {
                String[] arrayRes = string.split(",");
                for (String elemento : arrayRes) {
                    listCert.add(elemento.trim());
                }
            }
        }
        listCert.add("LOG: " + (isLogOn ? "ON" : "OFF"));
        listCert.add("");
        listCert.add("Press button to choose mode");
    }

    public boolean isWifiIndicator() {
        return wifiIndicator;
    }

    public String getSnCT() {
        return snCT;
    }

    public void setSnCT(String snCT) {
        this.snCT = snCT;
        this.jLabelSnCT.setText(snCT);
    }

    public String getCtCanStart() {
        return ctCanStart;
    }

    public void setCtCanStart(String ctCanStart) {
        this.ctCanStart = ctCanStart;
    }

    public int getLimCicli() {
        return limCicli;
    }

    public void setLimCicli(int limCicli) {
        this.limCicli = limCicli;
    }

    public int getCntCicli() {
        return cntCicli;
    }

    public void setCntCicli(int cntCicli) {
        this.cntCicli = cntCicli;
    }

    public boolean isInWl() {
        return inWl;
    }

    public void setInWl(boolean inWl) {
        this.inWl = inWl;
    }

    public int getIndiceLavoroCorrente() {
        return indiceLavoroCorrente;
    }

    public void setIndiceLavoroCorrente(int indiceLavoroCorrente) {
        this.indiceLavoroCorrente = indiceLavoroCorrente;
    }

    public void setLabelWL() {
        String nomeLavoroVisualizzato;
        for (int i = 0; i < this.elencoWl.size(); i++) {
            String[] desWlScelta = this.elencoWl.get(i);
            if (desWlScelta[0].equals(this.WLscelta)) {
                this.limCicli = Integer.parseInt(desWlScelta[2]);
                break;
            }
        }
        jLabelNomeWL.setText(this.WLscelta + " " + this.cntCicli + "/" + this.limCicli);
        if (this.inWl) {
            nomeLavoroVisualizzato = this.nomelavoro + " "
                    + (this.indiceLavoroCorrente + 1)
                    + "/"
                    + this.nrDiLavori;
        } else {
            nomeLavoroVisualizzato = this.nomelavoro;
        }
        jLabelNomeLavoro.setText(nomeLavoroVisualizzato);
    }

    public void setDurataPlcOk(int durataPlcOk) {
        this.durataPlcOk = durataPlcOk;
    }

    int getDurataPlcOk() {
        return durataPlcOk;
    }

    public JPanel getjPanelStarted() {
        return jPanelStarted;
    }

    public ImageIcon getImageIconLogo() {
        return Img_Logo;
    }

    public ImageIcon getImageIconSysStopped() {
        return Img_SysStopped;
    }

    /**
     * Ritorna il nome del device
     *
     * @return
     */
    public String getNomeDevice() {
        return this.nomeDevice;
    }

    public JLabel getjLabelDeviceNameLock() {
        return jLabelDeviceNameLock;
    }

    public JLabel getjLabelVersioneLock() {
        return jLabelVersioneLock;
    }

    public JLabel getjLabelVersione() {
        return jLabelVersione;
    }

    /**
     * Mostra o nasconde il pannello di errore
     *
     * @param mostra boolean true/false
     */
    public void showPannelloErrore(boolean mostra) {
        if (mostra) {
            this.jLayeredPaneCenter.moveToFront(this.jPanelLock);
        } else {
            this.jLayeredPaneCenter.moveToBack(this.jPanelLock);
        }
    }

    /**
     * Imposta il visualizzatore dello stato di raggiungibilità del Controller
     * Inoltre il colore evidenzia il ruolo: black -> Stand alone § yellow ->
     * Controller § blue -> backup § green -> standard - controller on line §
     * red -> standard/backup - controller out of line
     *
     * @param condizione
     */
    void setControllerIndicator(int condizione) {
        //Colori background dei vari ruoli
        Color controller = new Color(0xcc, 0x66, 0);//CC6600
        Color backup = new Color(0x10, 0x4f, 0xcb);//104FCB
        Color standard = new Color(0x97, 0xd0, 0x77);//97D077
        Color standalone = new Color(0x00, 0x00, 0x00);
        jLabelController.setForeground(Color.black);//Scritta nera di Default
        jLabelController.setText("S");

        switch (condizione) {
            case 0 ->//Controller raggiungibile
                jLabelController.setBackground(standard);
            case 1 ->//Controller BAD
                jLabelController.setBackground(Color.magenta);
            case 2 ->{//Controller non raggiungibile
                jLabelController.setBackground(Color.red);
                jLabelController.setForeground(Color.white);
            }
            case 3 -> {//Stand-Alone
                jLabelController.setBackground(standalone);
                jLabelController.setForeground(Color.white);
                jLabelController.setText("");
            }
            case 4 -> { // Ruolo Controller
                jLabelController.setBackground(controller);
                jLabelController.setForeground(Color.white);
                jLabelController.setText("C");
            }
            case 5 -> { // Ruolo backup
                jLabelController.setBackground(backup);
                jLabelController.setForeground(Color.white);
                jLabelController.setText("B");
            }
        }
    }

    /**
     * Disabilita un pulsante
     *
     * @param pulsante
     */
    public void disableButton(String pulsante) {
        switch (pulsante) {
            case "PL1" -> {
                this.jButtonPL1.setIcon(Img_Nulla);
                this.jButtonPL1.setEnabled(false);
            }
            case "PL2" -> {
                this.jButtonPL2.setIcon(Img_Nulla);
                this.jButtonPL2.setEnabled(false);
            }
            case "PL3" -> {
                this.jButtonPL3.setIcon(Img_Nulla);
                this.jButtonPL3.setEnabled(false);
            }
            case "PR1" -> {
                this.jButtonPR1.setIcon(Img_Nulla);
                this.jButtonPR1.setEnabled(false);
            }
            case "PR2" -> {
                this.jButtonPR2.setIcon(Img_Nulla);
                this.jButtonPR2.setEnabled(false);
            }
            case "PR3" -> {
                this.jButtonPR3.setIcon(Img_Nulla);
                this.jButtonPR3.setEnabled(false);
            }
        }
        this.jPanelLeft.repaint();
    }

    boolean isBackup() {
        return this.inBackup;
    }

    public boolean isInBackup() {
        return inBackup;
    }

    public void setInBackup(boolean inBackup) {
        this.inBackup = inBackup;
    }

}
