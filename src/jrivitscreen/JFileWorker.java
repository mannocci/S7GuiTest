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
    private String richiesta;

    public JFileWorker(JRivitMain mf) throws IOException {
        this.richiesta = "";
        this.Rm = mf;
        // create gpio controller by file (run bash script before !)     
        try {
            watcher = FileSystems.getDefault().newWatchService();
        } catch (IOException ex) {
            Logger.getLogger(JFileWorker.class.getName()).log(Level.SEVERE, null, ex);
        }
        Path dir = Paths.get(Static.PATH_WATCH);
        dir.register(watcher, ENTRY_CREATE, ENTRY_DELETE);
        Static.debug("Watch service modified file registered for dir: " + dir.toString(), 3);
    }

    @Override
    public void run() {
        try {
            while (null != (key = watcher.take())) {
                for (WatchEvent<?> event : key.pollEvents()) {
                    WatchEvent.Kind<?> kind = event.kind();

                    fileName = (Path) event.context();

                    Static.debug(kind.name() + ": " + fileName, 4);
                    if (kind == ENTRY_CREATE) {
                        if (!fileName.toString().startsWith(Static.F_SENSORI)) {
                            Static.debug("Creato: " + fileName, 3);
                        }
                        switch (fileName.toString()) {
                            case Static.F_STATO + "_ready" ->
                                stato();
                            case Static.F_ARIA ->
                                this.Rm.ariaAperta();
                            case Static.F_CONTATORI + "_ready" ->
                                aggiornaContatori();
                            case Static.F_SENSORI + "_ready" ->
                                aggiornaSensori();
                            case Static.F_NO_SENSORE ->
                                this.Rm.setSensoreCollegato(false);
                            case Static.F_ERRORE ->
                                errore(true);
                            case Static.F_POSIZIONE_ERRORI + "_ready" ->
                                readPosizioneErrori();
                            case Static.F_W + "_ready" ->
                                readLavori();
                            case Static.F_WL + "_ready" ->
                                readWl();
                            case Static.F_CHIEDI_CONFERMA_NO ->
                                impostaChiediConferma(true);
                            case Static.F_ABILITA_CALIBRAZIONE -> {
                                this.Rm.abilitaCalibrazione(true);
                                if (this.Rm.getPanCur().equals("start")) {
                                    this.Rm.PanelStart();
                                }
                            }
                            case Static.F_CHIEDI_CONFERMA_STOP ->
                                impostaChiediConfermaStop(true);
                            case Static.F_PULSANTE ->
                                gestisciPulsante();
                            case Static.F_WARNING + "_ready" ->
                                readWarning();
                            case Static.F_PRESSIONE_ARIA_IN_MIN + "_ready", Static.F_PRESSIONE_ARIA_IN_MAX + "_ready" ->
                                this.leggiAriaInMinMax();
                            case "killScreen" ->
                                this.Rm.exit();
                            case Static.F_PICCO + "_ready" ->
                                gestisciCurva();
                            case Static.F_CURVA_DI_RIFERIMENTO + "_ready" ->
                                readCurvaDiRiferimento();
                            case Static.F_PICCORIF + "_ready" ->
                                readPiccoRiferimento();
                            case Static.F_STATUS_LAN + "_ready" -> {
                                readSetupLan();
                            }
                            case Static.F_STATUS_WIFI + "_ready" -> {
                                readSetupWifi();
                            }
                            case Static.F_CONTROLLER_ONLINE -> {
                                this.Rm.setControllerIndicator(true);
                            }
                            case (Static.F_LISTA_NM_CON + "_ready") ->
                                readListaNMdevice();
                            // Per accendere gli indicatori  sarebbe meglio usare il comando "nmcli networking connectivity" che indica se siamo in lan 
                            // e se riusciamo anche ad uscire su internet
                            // Verificare se si può usare "nmcli monitor" per tenere sotto controllo la rete e avvisare in caso di cambiamenti
                            case Static.F_POWEROFF -> {
                                Rm.getjLabelDeviceName().setText("POWERING OFF");
                                Rm.PanelMain();
                                Thread.sleep(2000);
                                // System.exit(0);
                            }
                            case (Static.F_NOME_DEVICE + "_ready") -> {
                                readNomeDevice();
                                if (this.Rm.getPanCur().equals("main")) {
                                    this.Rm.PanelMain();
                                }
                            }
                            case (Static.F_INFO + "_ready") ->
                                readInfo();
                            case Static.F_RELOAD -> {
                                if (Rm.isStatoConcluso()) {
                                    Rm.lavoroPronto();
                                }
                            }
                            case Static.F_LAVORO_PRONTO ->
                                lavoroPronto();
                            /*Aggiungere la gestione della curva, contatori, stato con 
                                * la creazione dei file F_CURVA_READY, F_LAVORO_READY. F_PULSANTE_READY
                             */
                            case Static.F_WL_PRONTA ->
                                WlPronta();
                            case Static.F_WL_LISTA + "_ready" ->
                                WlListaPronta();
                            case Static.F_RICHIESTA + "_ready" ->
                                this.richiesta = leggiFile(Static.F_RICHIESTA);
                        }
                    }
                    if (kind == ENTRY_DELETE) {
                        if (!fileName.toString().startsWith(Static.F_SENSORI)) {
                            Static.debug("Eliminato: " + fileName, 4);
                        }
                        switch (fileName.toString()) {
                            case Static.F_ARIA ->
                                this.Rm.ariaChiusa();
                            case Static.F_NO_SENSORE ->
                                this.Rm.setSensoreCollegato(true);
                            case Static.F_ERRORE ->
                                errore(false);
                            case Static.F_CHIEDI_CONFERMA_NO ->
                                impostaChiediConferma(false);
                            case Static.F_ABILITA_CALIBRAZIONE -> {
                                this.Rm.abilitaCalibrazione(false);
                                if (this.Rm.getPanCur().equals("start")) {
                                    this.Rm.PanelStart();
                                }
                            }
                            case Static.F_CHIEDI_CONFERMA_STOP ->
                                impostaChiediConfermaStop(false);
                            case Static.F_CONTROLLER_ONLINE -> {
                                this.Rm.setControllerIndicator(false);
                            }

                        }
                    }

                }//End For watchevent

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
        this.Rm.aggiornaLavori(leggiFileElenco(Static.F_W));
    }

    /**
     * Legge il file con la descrizione delle WorkList
     */
    private void readWl() {
        this.Rm.aggiornaWl(leggiFileElenco(Static.F_WL));
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
            warning_file.add("Warning file not present or empty");
        }
        this.Rm.setListWarning(warning_file);
        if (this.Rm.getPanCur().equals("main")) {
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
     * Metodo che imposta il comportamento dell'interfaccia di JRivitMain
     *
     * @param inErrore
     */
    private void errore(boolean inErrore) {
        this.Rm.setInErrore(inErrore);
        if (!this.Rm.getStato().equals(Static.STATO_CALIBRAZIONE_TEST)) {
            if (this.Rm.getPanCur().equals("started")) {
                this.Rm.PanelStarted();
            } else if (this.Rm.getPanCur().equals("canvas")) {
                this.Rm.PanelCanvas();
            }
        }
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
        this.leggiNoSensore();
        this.leggiAriaInMinMax();   // Valori scritti nei files da Control
        this.leggiAbilitaCalibrazione();
        this.leggiControllerOnline();
        this.aggiornaContatori();
        this.aggiornaSensori();
        this.readLavori();//Se non esite il file imposta il default
        //this.lavoroPronto(); il lavoro pronto deve essere comandato da Control
        this.readWl();//Se non esiste il file ?
        // this.WlPronta(); la WL pronta deve essere comandata da Control
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
//        this.Rm.esegui("aggiorna_nm_list");
    }

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
     * crea un file di tipo flag. E' un file vuoto che deve semplicemente essere
     * cancellato e creato per sollecitare l'evento "create"
     *
     * @param NomeFile nome del file flag da scrivere
     */
    public static synchronized void scriviFlag(String NomeFile) {
        boolean lock = lockFile(NomeFile);
        if (lock) {
            try {
                cancellaFile(NomeFile);
                File fFlag = new File(Static.PATH_WATCH + NomeFile);
                fFlag.createNewFile();
            } catch (IOException ex) {
                Logger.getLogger(JFileWorker.class.getName()).log(Level.SEVERE, null, ex);
                unLock(NomeFile);
                return;
            }
            unLock(NomeFile);
        }
    }

    /**
     * scrive in un file creando il corrispondente file_ready per segnalare
     * l'avvenuta scrittura
     *
     * @param NomeFile
     * @param Testo String testo da scrivere nel file
     * @return -1 in caso di errore
     */
    public static synchronized int scriviFileConReady(String NomeFile, String Testo) {
        boolean lock = lockFile(NomeFile);
        if (lock) {
            try {
                FileWriter fw = new FileWriter(Static.PATH_WATCH + NomeFile);
                try (PrintWriter pw = new PrintWriter(fw)) {
                    pw.print(Testo);
                    pw.flush();
                    fw.close();
                    String nomeFileReady = NomeFile + "_ready";
                    boolean lockReady = lockFile(nomeFileReady);
                    if (lockReady) {
                        try {
                            cancellaFile(nomeFileReady);
                            File fReady = new File(Static.PATH_WATCH + nomeFileReady);
                            fReady.createNewFile();
                        } catch (IOException ex) {
                            Logger.getLogger(JFileWorker.class.getName()).log(Level.SEVERE, null, ex);
                            unLock(nomeFileReady);
                            return -1;
                        }
                        unLock(nomeFileReady);
                    }
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
     * scrive in un file, cancellandolo prima
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
     * Metodo che utilizza il controllo del Lock per leggere righe multiple da
     * un file
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
                Static.debug("File " + inputFile.getAbsolutePath()
                        + " does not exists\n", 2);
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
                Static.debug("File " + inputFile.getAbsolutePath()
                        + " does not exists\n", 2);
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

    private void leggiNoSensore() {
        File noSensore = new File(Static.F_NO_SENSORE);
        this.Rm.setSensoreCollegato(!noSensore.exists());
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
        try {
            this.Rm.setCurva(leggiFile(Static.F_CURVA));
            String[] piccoArray = leggiFile(Static.F_PICCO).split(",");
            this.Rm.gr.setCurva(this.Rm.getCurva());
            this.Rm.gr.setUM(this.Rm.getUM());
            this.Rm.gr.setPicco(Integer.parseInt(piccoArray[0]), Integer.parseInt(piccoArray[2]));
        } catch (Exception e) {
            Static.debug("Error while reading curve ", 2);
        }
    }

    private void readCurvaDiRiferimento() {
        String curvaRifStr = leggiFile(Static.F_CURVA_DI_RIFERIMENTO);
        if (curvaRifStr.equals("empty") || curvaRifStr.isEmpty()) {
            this.Rm.setCurvaDiRiferimento(null);
        } else {
            this.Rm.setCurvaDiRiferimento(curvaRifStr);
        }
        this.Rm.gr.setCurvaDiRiferimento(this.Rm.getCurvaDiRiferimento());
    }

    /**
     * legge unità di misura, fattore di conversione e nome_device registrato
     * nel record CT -> sn.
     */
    private void readNomeDevice() {
        try {
            this.Rm.setNomeDevice(leggiFile(Static.F_NOME_DEVICE));
            String umString = leggiFile(Static.F_UM);
            String[] umArray = umString.split(",");
            this.Rm.setUM(umArray[0]);
            this.Rm.setConversion(Integer.parseInt(umArray[1]));
        } catch (Exception ex) {
            Static.debug("Error reading UM", 2);
        }
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

    /**
     * Aggiorna i contatori da mostrare allo schermo occorre discriminare se
     * occorre fare il Count Down ?
     */
    private void aggiornaContatori() {
        String testo = leggiFile(Static.F_CONTATORI);
        String[] contatori = testo.split(",");
        if (contatori.length > 1) {
            //Se devo fare il Count Down
            //TiriNelLotto sono = Integer.parseInt(contatori[0] - Integer.parseInt(contatori[1])
            //
            //Lotto
            //Altrimenti devono essere decrementati
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
                Static.debug("File contatori contains non numeric values\n" + e.getMessage(), 2);
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
        boolean internetIndicator = false;
        for (String string : list_nm_con) {
            if (string.contains("eth") && string.contains("ON")
                    || string.contains("rasp4") && string.contains("ON")) {    // la prima riga che contiene "eth" e "ON" accende l'indicatore Lan
                lanIndicator = true;
            }
            if (string.contains("Internet")) {    // se l'ultima riga contiene "full" accende l'indicatore Internet
                if (string.contains("full")) {    // se l'ultima riga contiene "full" accende l'indicatore Internet
                    lanIndicator = true;
                    internetIndicator = true;
                } else if (string.contains("limited") || string.contains("portal")) {
                    lanIndicator = true;
                }
            }
            if ((string.contains("tun") || string.startsWith("VPN")) && string.contains("ON")) {    // la prima riga che contiene "tun" e "ON" accende l'indicatore VPN
                vpnIndicator = true;
            }
            if (string.contains("AP_") && string.contains("ON")) {    // la prima riga che contiene "eth" e "ON" accende l'indicatore Lan
                wifiIndicator = true;
            }
        }
        this.Rm.setLanIndicator(lanIndicator);
        this.Rm.setInternetIndicator(internetIndicator);
        this.Rm.setVPNIndicator(vpnIndicator);
        this.Rm.setWiFiIndicator(wifiIndicator);
    }

    /**
     * legge il file curva per costruire il grafico mostrato nel Pannello Canvas
     */
    private void gestisciCurva() {
        leggiCurva();
        this.Rm.setEsitoTiro(leggiFile(Static.F_ESITO_TIRO));
        this.Rm.repaint();
    }

    /**
     * Gestione del cambio stato. Lo stato viene sempre modificato da Control a
     * seguito di una richiesta andata a buon fine o ad un cambio di stato del
     * sistema. Pertanto la modifica dello stato non deve mai essere effettuata
     * direttamente da screen.
     */
    private void stato() {
        this.Rm.setStato(leggiFile(Static.F_STATO));
        switch (this.Rm.getStato()) {
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
            case Static.STATO_CALIBRAZIONE_TEST -> {
                this.Rm.avviaCalibrazione();
            }
            case Static.STATO_STOP -> {
                /*
                if (this.Rm.getStato().equals(Static.STATO_CALIBRAZIONE)) {
                    this.Rm.fineCalibrazione();
                } else {
//                    scriviFileConReady(Static.F_RICHIESTA, Static.RICHIESTA_STOP);
                    this.Rm.PanelStart();
                }*/
                if (Rm.getPanCur().equals("started") || Rm.getPanCur().equals("canvas") || Rm.getPanCur().equals("dialog")) {
                    this.Rm.PanelStart();
                }
            }
            case Static.STATO_PAUSA -> {
                this.Rm.setStatoConcluso(false);
                this.Rm.PanelStart();
            }
            case Static.RICHIESTA_RIAVVIO -> {
                if (this.Rm.getStato().equals(Static.STATO_CONCLUSO)) {
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

    /**
     * Gestisce l'avvenuta selezione di un lavoro. Il lavoro potrebbe essere
     * stato chiesto da remoto, quindi leggo prima il file F_W_SCELTO, nel
     * formato "lav 1§1§0§+§+"
     */
    private void lavoroPronto() {
        try {
            String lScelto = leggiFile(Static.F_W_SCELTO);
            String[] lSceltoArray = lScelto.split("§");
            if (!lSceltoArray[0].equals(Rm.getLavoroScelto())) {
                Rm.setLavoroScelto(lSceltoArray[0]);
            }
            if (lSceltoArray.length > 1) {
                Rm.setLimLotti(lSceltoArray[1]);
                Rm.setLimPezzi(lSceltoArray[2]);
                Rm.setUDLotti(lSceltoArray[3]);
                Rm.setUDPezzi(lSceltoArray[4]);
            }
            Rm.lavoroPronto();
        } catch (Exception e) {
            Static.debug("w_scelto -> wrong parameters count", 2);
        }
        /*
        if(this.richiesta.equals(Static.RICHIESTA_AVVIO)){
            Rm.lavoroPronto();
        }
         */
    }

    /**
     * Gestisce l'avvio di una Work List. La WL potrebbe essere stata chiesta da
     * remoto, quindi leggo prima il file F_WL_SCELTA
     */
    private void WlPronta() {
        String wlScelta = leggiFile(Static.F_WL_SCELTA);
        String[] WLSceltaArray = wlScelta.split("§");
        if (!WLSceltaArray[0].equals(Rm.getWLscelta())) {
            Rm.setWLscelta(WLSceltaArray[0]);
            if (WLSceltaArray.length > 1) {
                Rm.setWLnrCicli(Integer.parseInt(WLSceltaArray[1]));
            }
        }
        Rm.wlPronta();
    }

    private void leggiAbilitaCalibrazione() {
        File inputFile = new File(Static.PATH_WATCH + Static.F_ABILITA_CALIBRAZIONE);
        if (inputFile.exists()) {
            this.Rm.abilitaCalibrazione(true);
        } else {
            this.Rm.abilitaCalibrazione(false);
        }
    }

    private void leggiControllerOnline() {
        File inputFile = new File(Static.PATH_WATCH + Static.F_CONTROLLER_ONLINE);
        if (inputFile.exists()) {
            this.Rm.setControllerIndicator(true);
        } else {
            this.Rm.setControllerIndicator(false);
        }
    }

    private void readPosizioneErrori() {
        this.Rm.setPosizioneErrori(leggiFile(Static.F_POSIZIONE_ERRORI));
    }

    public void WlListaPronta() {
        try {
            Rm.aggiornaWlLavori(leggiFileElenco(Static.F_WL_LISTA));
            if (Rm.getElencoWlLavori().size() > 1) {
                Rm.setLimLotti(Rm.getElencoWlLavori().get(0)[2]);
                Rm.setLimPezzi(Rm.getElencoWlLavori().get(0)[3]);
                Rm.setUDLotti(Rm.getElencoWlLavori().get(0)[4]);
                Rm.setUDPezzi(Rm.getElencoWlLavori().get(0)[5]);
            }
        } catch (Exception ex) {
            Static.debug("Error preparing WorkList " + Rm.getWLscelta() + " ! " + ex, 2);
        }
    }

    private void readPiccoRiferimento() {
        String piccoStr = leggiFile(Static.F_PICCORIF);
        String[] piccoArray = piccoStr.split(",");
        this.Rm.gr.setUM(this.Rm.getUM());
        this.Rm.gr.setPiccoRif(
                Integer.parseInt(piccoArray[0]),
                Integer.parseInt(piccoArray[2]));
        this.Rm.gr.setCurva("");
    }

}
