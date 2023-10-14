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
 * La documentazionedel progetto del RivitScreen si trova si GitHub 
 * https://github.com/rivit-elrenoin 
 * repository Privato, visibile da personale della Rivit, oltre a Luca Mannocci,
 * Fabio Fragapane, Mannocci Enrico
 * @versione 1.0 maggio/giugno 2023
 */
package jrivitscreen;

import java.awt.Color;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.io.IOException;
import java.io.PrintWriter;
import java.nio.charset.StandardCharsets;
import java.nio.file.FileSystems;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import static java.nio.file.StandardWatchEventKinds.*;
import java.nio.file.WatchEvent;
import java.nio.file.WatchKey;
import java.nio.file.WatchService;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;
import javax.swing.JLayeredPane;

/**
 *
 * @author lucamannocci
 */
public class JFileWorker extends Thread {
// Classi

    private final JRivitMain Rm;

    private WatchService watcher;
    private Path fileName;
    private WatchKey key;
    // se il lavoro è in corso contiene "1"
    private String stato;

    public JFileWorker(JRivitMain mf) throws IOException {
        this.Rm = mf;
        // create gpio controller by file (run bash script before !)     
        try {
            watcher = FileSystems.getDefault().newWatchService();
        } catch (IOException ex) {
            Logger.getLogger(JFileWorker.class.getName()).log(Level.SEVERE, null, ex);
        }
        Path dir = Paths.get(Static.PATH_WATCH);
        dir.register(watcher, ENTRY_CREATE, ENTRY_DELETE, ENTRY_MODIFY);
        Static.debug("Watch Service Modify file registered for dir: " + dir.toString(), 3);
    }

    @Override
    public void run() {
        try {
            while (null != (key = watcher.take())) {
                for (WatchEvent<?> event : key.pollEvents()) {
                    WatchEvent.Kind<?> kind = event.kind();

                    fileName = (Path) event.context();

                    Static.debug(kind.name() + ": " + fileName, 4);
                    if (kind == ENTRY_MODIFY && event.count() == 1) {
                        if (!fileName.toString().equals(Static.F_SENSORI)) {
                            Static.debug("Modificato: " + fileName + " - n.modifiche: " + event.count(), 3);
                        }
                        switch (fileName.toString()) {
                            case Static.F_CURVA ->
                                gestisciCurva();
                        }
                    }
                    if (kind == ENTRY_CREATE) {
                        if (!fileName.toString().equals(Static.F_SENSORI)) {
                            Static.debug("Creato: " + fileName, 3);
                        }
                        switch (fileName.toString()) {
                            case (Static.F_STATO + "_ready") ->
                                stato();
                            case Static.F_ARIA ->
                                this.Rm.ariaAperta();
                            case (Static.F_CONTATORI + "_ready") ->
                                aggiornaContatori();
                            case Static.F_SENSORI ->
                                aggiornaSensori();
                            case Static.F_ERRORE ->
                                errore(true);
                            case (Static.F_LAVORI + "_ready") ->
                                readLavori();
                            case Static.F_CHIEDI_CONFERMA_NO ->
                                impostaChiediConferma(true);
                            case Static.F_CHIEDI_CONFERMA_STOP ->
                                impostaChiediConfermaStop(true);
                            case Static.F_PULSANTE ->
                                gestisciPulsante();
                            // Il file warning.txt viene ricreato ad ogni aggiornamento
                            case (Static.F_WARNING + "_ready") ->
                                readWarning();
                            case (Static.F_PRESSIONE_ARIA_IN_MIN + "_ready") ->
                                this.leggiAriaInMinMax();
                            case "killScreen" ->
                                this.Rm.exit();
//                            case Static.F_CURVA ->
//                                gestisciCurva();
                            case (Static.F_CURVA_DI_RIFERIMENTO + "_ready") ->
                                readCurvaDiRiferimento();
                            case Static.F_STATUS_LAN -> {
                                readSetupLan();
                            }
                            case Static.F_STATUS_WIFI -> {
                                readSetupWifi();
                            }
                            case (Static.F_LISTA_NM_CON + "_ready") ->
                                readListaNMdevice();    
                                // Per accendere gli indicatori  sarebbe meglio usare il comando "nmcli networking connectivity" che indica se siamo in lan 
                                // e se riusciamo anche ad uscire su internet
                                // Verificare se si può usare "nmcli monitor" per tenere sotto controllo la rete e avvisare in caso di cambiamenti
                            case Static.F_POWEROFF -> {
                                Rm.getjLabelDeviceName().setText("POWER OFF");
                                Rm.PanelMain();
                            }
                            case (Static.F_NOME_DEVICE + "_ready") ->
                                readNomeDevice();
                            case (Static.F_INFO + "_ready") ->
                                readInfo();
                            case Static.F_RELOAD -> {
                                if (Rm.isStatoConcluso()) {
                                    Rm.lavoroPronto();
                                }
                            }
                            case Static.F_LAVORO_PRONTO ->
                                Rm.lavoroPronto();
                            /*Aggiungere la gestione della curva, contatori, stato con 
                                * la creazione dei file F_CURVA_READY, F_CONTATORI_READY , 
                                   F_STATO_READY, F_LAVORO_READY. F_SENSORI_READY, 
                                   F_PULSANTE_READY
                             */
                        }
                    }
                    if (kind == ENTRY_DELETE) {
                        if (!fileName.toString().equals(Static.F_SENSORI)) {
                            Static.debug("Eliminato: " + fileName, 3);
                        }
                        switch (fileName.toString()) {
                            case Static.F_ARIA ->
                                this.Rm.ariaChiusa();
                            case Static.F_ERRORE ->
                                errore(false);
                            case Static.F_CHIEDI_CONFERMA_NO ->
                                impostaChiediConferma(false);
                            case Static.F_CHIEDI_CONFERMA_STOP ->
                                impostaChiediConfermaStop(false);
                            case Static.F_RISPOSTA_TIRO_ERRATO_CONTINUA, Static.F_RISPOSTA_TIRO_ERRATO_ACCETTA, Static.F_RISPOSTA_TIRO_ERRATO_ANNULLA -> {
                                this.Rm.setInErrore(false);
                                //this.Rm.aggiornaDaErroreTiro(); //Non colorava lo sfondo rimosso metodo
                                this.Rm.PanelStarted();//Ricalcola i contatori e colora in modo corretto
                            }
                        }
                    }

                }//End For watchevent
//                //attesa per evitare segnalazioni ripetute
//                TimeUnit.SECONDS.sleep(1);

                if (!key.reset()) {
                    //Problema non riesce il sistema a controllare il path indicato
                    Logger.getLogger(JFileWorker.class.getName()).log(Level.SEVERE, null, " Errore Key.reset is null");
                    break;
                }

            }
        } catch (InterruptedException | RuntimeException ex) {
            Logger.getLogger(JFileWorker.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    /**
     * Un conto è lo stato dell'aria, ma l'azione di chiusura e apertura deve
     * essere fatta da control. I led da chi li facciamo comandare ? Da control
     * vedi Class JTask
     *
     * @param stato
     */
    private void mostraStatoAria(String stato) {

        if (stato.equals(Static.ARIA_CHIUSA)) {//Aria chiusa
            this.Rm.ariaChiusa();
        } else { //Aria aperta
            this.Rm.ariaAperta();
        }
    }

    /**
     * Legge il file con la descrizione dei lavori
     */
    private void readLavori() {
        this.Rm.aggiornaLavori(leggiFileElenco(Static.F_LAVORI));
    }

    /**
     * Legge il file con la descrizione delle info di sistema
     */
    private void readInfo() {
        List<String> LeggiFileElencoInfo = this.leggiFileElenco(Static.F_INFO);
        if (LeggiFileElencoInfo.isEmpty()) {
            LeggiFileElencoInfo.add("Manca file Info");
        }
        this.Rm.setListInfo(LeggiFileElencoInfo);
    }

    /**
     * Legge il file Warning e aggiorna la lista
     */
    private void readWarning() {
//        lavoro che deve essere fatto da JDoWorker
        List<String> warning_file = this.leggiFileElenco(Static.F_WARNING);
        if (warning_file.isEmpty()) {
            warning_file.add("Manca file Warning o file vuoto");
        }
        this.Rm.setListWarning(warning_file);
        JLayeredPane JLp = this.Rm.getjLayeredPaneCenter();
        String panelName = JLp.getComponent(0).getName();
        if (panelName.equals("main")) {
            this.Rm.getjButtonPL1().setIcon(this.Rm.getImageWarning());
        }
        switch (this.Rm.getW_level()) {
            case 0 -> {
                this.Rm.getjLabelWarning().setBackground(Color.GREEN);
                this.Rm.getjLabelWarning().setForeground(Color.BLACK);
                this.Rm.getjLabelWarning().setText("OK");
            }
            case 1, 2, 3, 4 -> {
                this.Rm.getjLabelWarning().setBackground(Color.YELLOW);
                this.Rm.getjLabelWarning().setForeground(Color.BLACK);
                this.Rm.getjLabelWarning().setText("W");
            }
            case 5, 6, 7, 8, 9 -> {
                this.Rm.getjLabelWarning().setBackground(Color.RED);
                this.Rm.getjLabelWarning().setForeground(Color.WHITE);
                this.Rm.getjLabelWarning().setText("E");
            }
        }
        this.Rm.getjLabelWarning().repaint();
        this.Rm.repaint();
    }

    /**
     * Legge il file con la descrizione della configurazione della LAN DA FARE
     * Leggere il DB è meglio
     */
    private void readSetupLan() {
        this.Rm.aggiornaSetupLan(this.leggiFileElenco(Static.F_STATUS_LAN));
    }

    /**
     * Legge il file con la descrizione della configurazione della WiFi Come
     * sopra forse è meglio leggere il DB
     */
    private void readSetupWifi() {
        this.Rm.aggiornaSetupWiFi(this.leggiFileElenco(Static.F_STATUS_WIFI));
    }

    /**
     * Metodo che imposta la variabile booleana <in_errore> di JRivitMain
     *
     *
     * @param si_o_no
     */
    private void errore(boolean si_o_no) {
        if (si_o_no) {
//            mostra_curva();
            this.Rm.set_errore_tiro();
        } else {
            this.Rm.setInErrore(false);
            this.Rm.g.setIsInError(false);
        }
        this.Rm.setInErrore(si_o_no);
    }

    /**
     * Pressione Aria viene letta ogni 5 secondi
     */
    private void aggiornaSensori() {
        String line = this.leggiFile(Static.F_SENSORI);
        this.Rm.updateSensori(line);
    }

    /**
     * inizializza i valori in base al contenuto dei file Sono necessari: -
     * lavori.txt; letto sul DB tabella "lavori", 4 campi: nome, lotti, pezzi,
     * descrizione
     *
     */
    public void initValues() {
        this.leggiAriaInMinMax();   // Valori scritti nei files da Control
        this.aggiornaSensori();// Occorre che vi sia il batch avviato
        this.aggiornaContatori();
        this.readLavori();//Se non esite il file imposta il default
        this.readInfo();// Se non esiste il file imposta a stringa info
        this.readWarning();// Se non esiste il file imposta a sringa warning
        this.readCurvaDiRiferimento();//Se non esiste il file imposta a 0
        this.readLavoroInPausa();//Se non esiste il file imposta non in pausa
        //this.LeggiAriaInMinMax(); // Letto dal DB
        //this.LeggiSessione();// Se non esite il file imposta il file a "0"
//        this.mostraStatoAria(Static.ARIA_CHIUSA);//Se non esiste il file imposta a "0"
        this.readSetupLan();
        this.readListaNMdevice();
        String rigaFile = leggiFile(Static.F_ARIA);
        if (rigaFile.contains("errore")) {
            this.Rm.ariaChiusa();
        } else {
            this.Rm.ariaAperta();
        }

        this.readNomeDevice();//Se non esiste il file imposta a CT-0000-00
//        cancellaFile(Static.PATH_WATCH + "errore"); // Dovrebbe farlo COntrol
        this.Rm.set_jLabel_B_L("Main");
        this.Rm.repaint();
    }

    /**
     * ScriviFile metodo generico per scrivere una riga in un file
     *
     * @param NomeFile
     * @param CosaScrivere String testo da scrivere nel file
     */
//    public void ScriviFile(String NomeFile, String CosaScrivere) {
//        try {
//            FileWriter fw = new FileWriter(Static.PATH_WATCH + NomeFile);
//            PrintWriter pw = new PrintWriter(fw);
//            pw.print(CosaScrivere);
//            pw.flush();
//            pw.close();
//        } catch (IOException ex) {
//            Logger.getLogger(JFileWorker.class.getName()).log(Level.SEVERE, null, ex);
//        }
//    }
    /**
     * Metodo per fare il lock del file basato su filesystem: se non esiste
     * nomFile.lock lo scrive bloccando così il file
     *
     * @return
     */
    public static boolean lockFile(String NomeFile) {
        File inputFile = new File(Static.PATH_LCK + NomeFile + ".lck");
        if (inputFile.exists()) {
            while (inputFile.exists()) {//Attesa che si liberi il file
                try {
                    Thread.sleep(150);
                } catch (InterruptedException ex) {
                    Logger.getLogger(JFileWorker.class.getName()).log(Level.SEVERE, null, ex);
                    return false;
                }
            }
            try {
                FileWriter fw = new FileWriter(Static.PATH_LCK + NomeFile + ".lck");
                try (PrintWriter pw = new PrintWriter(fw)) {
                    pw.print("1");
                    pw.flush();
                }
            } catch (IOException ex) {
                Logger.getLogger(JFileWorker.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
        return true;
    }

    /**
     * scrive in un file
     *
     * @param NomeFile
     * @param Testo String testo da scrivere nel file
     * @return -1 per errore
     */
    public static int scriviFile(String NomeFile, String Testo) {
        boolean lock = lockFile(NomeFile);
        if (lock) {
            try {
                cancellaFile(NomeFile);
                FileWriter fw = new FileWriter(Static.PATH_WATCH + NomeFile);
                try (PrintWriter pw = new PrintWriter(fw)) {
                    pw.print(Testo);
                    pw.flush();
                }
            } catch (IOException ex) {
                Logger.getLogger(JFileWorker.class.getName()).log(Level.SEVERE, null, ex);
                unLock(NomeFile);
                return -1;
            }
            unLock(NomeFile);
        }
        return 0;
    }

    /**
     * Metodo che utilizza il controllo del Lock per leggere una riga dal file
     *
     * @param NomeFile
     * @return La riga letta del file
     */
    public static List<String> leggiFileElenco(String NomeFile) {
        List<String> ListaRighe = new ArrayList<>();
        boolean lock;
        try {
            File inputFile = new File(Static.PATH_WATCH + NomeFile);
            if (!inputFile.exists()) {
                Static.debug("Il File " + inputFile.getAbsolutePath()
                        + " non esiste\n", 2);
                ListaRighe.add("errore lettura File " + NomeFile);
                return ListaRighe;
            }
            lock = lockFile(NomeFile);
            if (lock) {
                ListaRighe = Files.readAllLines(Paths.get(Static.PATH_WATCH + NomeFile), StandardCharsets.UTF_8);
                unLock(NomeFile);
            }
        } catch (IOException ex) {
            Logger.getLogger(JFileWorker.class.getName()).log(Level.SEVERE, null, ex);
            unLock(NomeFile);
            return ListaRighe;
        }
        return ListaRighe;
    }

    /**
     * Metodo che utilizza il controllo del Lock per leggere una riga dal file
     *
     * @param NomeFile
     * @return La riga letta del file
     */
    public static String leggiFile(String NomeFile) {
        String contenutoFile = "";
        Scanner myReader;
        boolean lock;
        try {
            File inputFile = new File(Static.PATH_WATCH + NomeFile);
            if (!inputFile.exists()) {
                Static.debug("Il File " + inputFile.getAbsolutePath()
                        + " non esiste\n", 2);
                contenutoFile = "errore " + NomeFile;
                return contenutoFile;
            }
            lock = lockFile(NomeFile);
            if (lock) {
                FileReader fr = new FileReader(Static.PATH_WATCH + NomeFile);
                myReader = new Scanner(fr);
                while (myReader.hasNextLine()) {
                    contenutoFile += myReader.nextLine();
                }
            }
        } catch (IOException ex) {
            Logger.getLogger(JFileWorker.class.getName()).log(Level.SEVERE, null, ex);
            unLock(NomeFile);
            return "";
        }
        unLock(NomeFile);
        return contenutoFile;
    }

    private void leggiAriaInMinMax() {
        try {
            float min, max;
            min = Float.parseFloat(leggiFile(Static.F_PRESSIONE_ARIA_IN_MIN));
            max = Float.parseFloat(leggiFile(Static.F_PRESSIONE_ARIA_IN_MAX));
            this.Rm.updateSogliePressioneAriaIn(min, max);
        } catch (NumberFormatException e) {
            Static.debug("Contenuto dei file pressione_in non numerico !\n" + e.getMessage(), 2);
        }
    }

    private void leggiCurva() {
        this.Rm.setCurva(leggiFile(Static.F_CURVA));
        this.Rm.g.setCurva(this.Rm.getCurva());
        this.Rm.g.setPicco(100, 45);    // temporaneo !! Il valore reale lo dovrà scrivere Control in qualche file
    }

    private void readCurvaDiRiferimento() {
        String curvaRifStr = leggiFile(Static.F_CURVA_DI_RIFERIMENTO);
        if (curvaRifStr.equals("empty") || curvaRifStr.isEmpty()) {
            this.Rm.setCurvaDiRiferimento(null);
        } else {
            this.Rm.setCurvaDiRiferimento(curvaRifStr);
        }
        this.Rm.g.setCurvaDiRiferimento(this.Rm.getCurvaDiRiferimento());
    }

    /**
     * legge dal file nome_device il nome del ControlRiv SN registrato nel
     * record CT -> sn
     */
    private void readNomeDevice() {
        this.Rm.setNomeDevice(leggiFile(Static.F_NOME_DEVICE));
    }

    private void lottiOk() {
        try {
            int lottiok = Integer.parseInt(leggiFile(Static.F_LOTTI_OK));
            if (lottiok == 0) {
                lottiok = 1;
            }
            this.Rm.set_nr_lotti_ok(lottiok);
        } catch (NumberFormatException e) {
            Static.debug("Contenuto del file lotti_ok non numerico !\n" + e.getMessage(), 2);
        }
    }

    private void impostaChiediConferma(boolean si_o_no) {

        this.Rm.setChiedi_conferma(si_o_no);
    }

    /**
     * cancella un file
     *
     * @param NomeFile
     */
    public static void cancellaFile(String NomeFile) {
        File f = new File(Static.PATH_WATCH + NomeFile);
        if (f.exists()) {
            boolean lock = lockFile(NomeFile);
            if (lock) {

                if (!f.delete()) {
                    Static.debug("errore eliminando il file " + NomeFile, 2);
                }
            }
            unLock(NomeFile);
        }
    }

    /**
     * Cancella file lck in /tmp
     *
     * @param NomeFile
     */
    private static void unLock(String NomeFile) {
        File f = new File(Static.PATH_LCK + NomeFile + ".lck");
        if (f.exists()) {
            if (!f.delete()) {
                Static.debug("Errore eliminando il file " + NomeFile, 2);
            }
        }
    }

    private void impostaChiediConfermaStop(boolean si_o_no) {
        this.Rm.setChiedi_conferma_stop(si_o_no);
    }

    private void aggiornaContatori() {
        String testo = leggiFile(Static.F_CONTATORI);
        String[] contatori = testo.split(",");
        if (contatori.length == 6) {
            try {
                this.Rm.setLotto(Integer.parseInt(contatori[0]));
                this.Rm.setTiriNelLotto(Integer.parseInt(contatori[1]));
                this.Rm.setTiriValidi(Integer.parseInt(contatori[2]));
                this.Rm.setTiriAnnullati(Integer.parseInt(contatori[3]));
                this.Rm.setTiriErrati(Integer.parseInt(contatori[4]));
                this.Rm.setTiriTotali(Integer.parseInt(contatori[5]));

                // aggiorna la visualizzazione dei contatori nel pannello
                this.Rm.aggiornaContatori();
            } catch (NumberFormatException e) {
                Static.debug("File contatori contiene valori non numerici\n" + e.getMessage(), 2);
            }
        }
    }

    /**
     * networkmanager crea una lista dei device Questo metodo la legge, è stato
     * avviato un bash prima che ha creato il file con la lista
     */
    private void readListaNMdevice() {
        List<String> list_nm_con = leggiFileElenco(Static.F_LISTA_NM_CON);
        this.Rm.setListNmCon(list_nm_con);
        boolean lanIndicator = false;
        boolean vpnIndicator = false;
        boolean wifiIndicator = false;
        for (String string : list_nm_con) {
            if (string.contains("eth") && string.contains("ON")) {    // la prima riga che contiene "eth" e "ON" accende l'indicatore Lan
                lanIndicator = true;
                break;
            }
        }
        for (String string : list_nm_con) {
            if (string.contains("tun") && string.contains("ON")) {    // la prima riga che contiene "tun" e "ON" accende l'indicatore VPN
                vpnIndicator = true;
                break;
            }
        }
        for (String string : list_nm_con) {
            if (string.contains("AP_") && string.contains("ON")) {    // la prima riga che contiene "eth" e "ON" accende l'indicatore Lan
                wifiIndicator = true;
                break;
            }
        }
        this.Rm.setLanIndicator(lanIndicator);
        this.Rm.setVPNIndicator(vpnIndicator);
        this.Rm.setWiFiIndicator(wifiIndicator);
    }

    /**
     * legge il file curva per costruire il grafico mostrato nel Pannello Canvas
     */
    private void gestisciCurva() {
        leggiCurva();
        this.Rm.repaint();
        //this.Rm.PanelCanvas();
        //this.Rm.mostraCurva();
    }

    private void stato() {
        stato = leggiFile(Static.F_STATO);
        switch (stato) {
            case Static.STATO_CONCLUSO -> {
                this.Rm.setStatoConcluso(true);
                this.Rm.PanelStarted();
            }
            case Static.STATO_AVVIATO -> {
                this.Rm.setStatoConcluso(false);
                this.Rm.PanelStarted();
            }
            case Static.STATO_CALIBRAZIONE -> {
                this.Rm.avviaCalibrazione();
            }
            case Static.STATO_STOP -> {
                if (this.stato.equals(Static.STATO_CALIBRAZIONE)) {
                    this.Rm.fineCalibrazione();
                } else {
//                    scriviFile(Static.F_RICHIESTA, Static.RICHIESTA_STOP);
                    this.Rm.PanelStart();
                    this.Rm.set_jLabel_B_L("Start");
                }
            }
            case Static.STATO_PAUSA -> {
                this.Rm.setStatoConcluso(false);
                this.Rm.PanelStart();
            }
            case Static.RICHIESTA_RIAVVIO -> {
                if (this.stato.equals(Static.STATO_CONCLUSO)) {
                    this.Rm.avviaLavoro();
                }
            }

        }
    }

    private void readLavoroInPausa() {
        String testo = leggiFile(Static.F_IN_PAUSA);
        if (!testo.startsWith("errore")) {
            this.Rm.setInPausa("1");
        }
        cancellaFile(Static.F_IN_PAUSA);
    }

    /**
     * Il file "pulsante" viene utilizzato dal webserver per telecontrallare la
     * pressione virtuale di un pulsante
     */
    private void gestisciPulsante() {
        String pulsante = leggiFile(Static.F_PULSANTE);
        this.Rm.pulsanteHw(pulsante);
    }
}
