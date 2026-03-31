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
import java.util.Collections;
import java.util.List;
import java.util.Scanner;
import org.json.JSONArray;
//import St.Statica;

/**
 *
 * @author lucamannocci
 */
public class JFileWorker extends Thread {
// Classi
    // prova di funzionamento della classe statica in comune tra i progetti java

//    String a = Statica.RICHIESTA_STOP_LAVORO;
    private final JRivitMain Rm;
    private WatchService watcher;
    private String fileName;
    private WatchKey key;
    private int OldW_Level;
    private String ruolo;
    private boolean incoerente = false;
    private Path pathName;

    public JFileWorker(JRivitMain mf) throws IOException {
        this.ruolo = "";
        this.Rm = mf;
        Path dir = Paths.get(Static.PATH_WATCH);
        // create gpio controller by file (run bash script before !)     
        try {
            watcher = FileSystems.getDefault().newWatchService();
        } catch (IOException ex) {
            Static.debug("Error starting watch service modified file registered for dir: " + dir.toString() + ": " + ex, 1);
        }
        dir.register(watcher, ENTRY_CREATE, ENTRY_DELETE);
        Static.debug("Watch service modified file registered for dir: " + dir.toString(), 3);
    }

    @Override
    public void run() {
        try {
            while (null != (key = watcher.take())) {
                for (WatchEvent<?> event : key.pollEvents()) {
                    WatchEvent.Kind<?> kind = event.kind();
                    pathName = (Path) event.context();
                    /*Conviene pensare di inserire l'elenco dei file gestiti così da evitare di avviare un task 
                    di JTask se la cartella innavvertitamente viene popolata da file inutili
                     */
                    if (pathName != null) { // Ci sono casi in cui event.context() restituisce null
                        this.fileName = pathName.toString();
                    }
                   // fileName = (Path) event.context();

                    Static.debug(kind.name() + ": " + fileName, 4);
                    if (kind == ENTRY_CREATE) {
                        if (!fileName.startsWith(Static.F_SENSORI)
                                && !fileName.startsWith("certSens")) {
                            Static.debug("Creato: " + fileName, 3);
                        }
                        switch (fileName) {
                            case "certSens_ready" -> {
                                String certSens = leggiFile("certSens.txt");
                                this.Rm.aggiornaListSens(certSens);
                            }
                            case Static.F_STATO + "_ready" ->
                                stato();
                            case Static.F_ARIA ->
                                this.Rm.ariaAperta();
                            case Static.F_CONTATORI + "_ready" ->
                                aggiornaContatori();
                            case Static.F_SENSORI + "_ready" ->
                                aggiornaSensori();
                            case Static.F_NO_SENSORE -> { // sensore scollegato
                                //Da riguardare
                                //if (this.Rm.getStato().equals(Static.STATO_STOP)) {
                                this.Rm.setSensoreCollegato(false);
                                this.Rm.setCtCanStart(false);
                                if (Rm.getPanCur().equals("main")) {
                                    this.Rm.PanelMain();
                                }
                                if (Rm.getPanCur().equals("start")) {
                                    this.Rm.PanelStart();
                                }
                                //}
                            }

                            case Static.F_ERRORE ->
                                errore(true);
                            case Static.F_POSIZIONE_ERRORI + "_ready" ->
                                readPosizioneErrori();
                            case Static.F_W + "_ready" ->
                                readLavori();
                            case Static.F_WL + "_ready" ->
                                readWl();
                            case Static.F_SONO_IN + "_ready" ->
                                readSonoIn();
                            case Static.F_ABILITA_CALIBRAZIONE -> {
                                this.Rm.abilitaCalibrazione(true);
                                if (this.Rm.getPanCur().equals("start")) {
                                    this.Rm.PanelStart();
                                }
                            }
                            case Static.F_CONFERMA_STOP_PAUSA ->
                                this.Rm.setConfermaStopPausa(true);
                            case Static.F_CONFERMA_RISP_ERRORE ->
                                this.Rm.setConfermaRispErrore(true);
                            case Static.F_PULSANTE ->
                                gestisciPulsante();
                            case Static.F_WARNING + "_ready" ->
                                readWarning();
                            case Static.F_PRESSIONE_ARIA_IN_MIN + "_ready", Static.F_PRESSIONE_ARIA_IN_MAX + "_ready" ->
                                this.leggiAriaInMinMax();
                            case Static.F_PICCO + "_ready" ->
                                gestisciCurva();
                            case Static.F_CURVA_DI_RIFERIMENTO + "_ready" ->
                                readCurvaDiRiferimento();
                            case Static.F_PICCORIF + "_ready" ->
                                readPiccoRiferimento();
                            case Static.F_STATUS_LAN + "_ready" ->
                                readLanStatus();
                            case Static.F_STATUS_INTERNET + "_ready" -> {
                                this.readInternetStatus();
                                this.updateSystemDateTime();
                            }
                            case Static.F_STATUS_VPN + "_ready" ->
                                readVpnStatus();
                            case Static.F_INFO_INTERFACES + "_ready" ->
                                this.readInfoInterfaces();
                            case Static.F_STATUS_WIFI + "_ready" ->
                                readSetupWifi();
                            case Static.F_CONTROLLER_ONLINE -> {
                                if (this.ruolo.equals("2")) {
                                    this.Rm.setControllerIndicator(0);  // Verde
                                }
                                if (this.ruolo.equals("1")) {
                                    this.Rm.setControllerIndicator(5);  // Blue
                                }
                            }
                            case Static.F_CONTROLLER_BAD -> {
                                if (this.ruolo.equals("2")) {
                                    this.Rm.setControllerIndicator(1);  // Magenta
                                }
                            }
                            case Static.F_RUOLO + "_ready" ->
                                this.impostaRuolo();
                            case (Static.F_LISTA_NM_CON + "_ready") -> {
                                this.readListaNMdevice();
                            }
                            case (Static.F_USB_LISTA_FILE + "_ready") -> {
                                this.Rm.setInBackup(true);
                                Rm.PanelUsb();
                                readListaFileUSB();
                            }
                            case Static.F_POWEROFF -> {
                                Rm.setLockStatus("POWERING OFF SYSTEM");
                                Rm.getJLabelLogo().setIcon(Rm.getImageIconSysStopped());
                                Rm.setInFreeze(true);
//                                Rm.PanelMain();
                                Thread.sleep(3000);//Aggiunto un secondo per mostrare il pannello
                                System.exit(0);
                            }
                            case Static.F_REBOOT -> {
                                Rm.setLockStatus("REBOOT SYSTEM");
                                Rm.getJLabelLogo().setIcon(Rm.getImageIconSysStopped());
                                Rm.setInFreeze(true);
//                                Rm.PanelMain();
                                Thread.sleep(3000);//Aggiunto un secondo per mostrare il pannello
                                System.exit(0);
                            }
                            case Static.F_SYSTEM_FREEZE -> {
                                Rm.setInFreeze(true);
                                Rm.setLockStatus("SYSTEM IN MAINTENANCE");
                                Rm.getjLabelVersioneLock().setText(Rm.getjLabelVersione().getText());
                                Rm.getjLabelDeviceNameLock().setText(Rm.getjLabelDeviceName().getText());
                            }
                            case Static.F_SYSTEM_LOCK_EMERGENCY -> {
                                Rm.setInFreeze(true);
                                Rm.setLockStatus("EMERGENCY SYSTEM LOCK");
                                Rm.getjLabelVersioneLock().setText(Rm.getjLabelVersione().getText());
                                Rm.getjLabelDeviceNameLock().setText(Rm.getjLabelDeviceName().getText());
                            }

                            case (Static.F_NOME_DEVICE + "_ready") -> {  // Il file F_UM viene creato dopo aver letto tutti i dati del CT
                                readNomeDevice();
                                if (this.Rm.getPanCur().equals("main")) {
                                    this.Rm.PanelMain();
                                }
                            }
                            case (Static.F_INFO + "_ready") ->
                                readInfo();
                            case Static.F_RELOAD -> {
                                if (Rm.isLavoroConcluso()) {
                                    Rm.lavoroPronto();
                                }
                            }
                            case Static.F_LAVORO_PRONTO -> {
                                lavoroPronto();//Imposta Lavoro Pronto
                                // Nel caso in cui la scelta del lavoro fosse stata fatta dall'esterno
                                if (this.Rm.getRichiesta().equals(Static.RICHIESTA_AVVIO_LAVORO)) {
                                    this.Rm.setLavoroScelto(leggiFile(Static.F_W_SCELTO));
                                }
                                if (this.Rm.getRichiesta().equals(Static.RICHIESTA_AVVIO_LAVORO)
                                        || this.Rm.getRichiesta().equals(Static.RICHIESTA_AVVIO_WL)) {
                                    leggiErrore();//Verifica l'esistenza del file errore. Se sì imposta il flag
                                    richiestaAvvioLavoro();//Crea richiesta_avvio
                                }
                                if (this.Rm.getRichiesta().equals(Static.RICHIESTA_CALIBRAZIONE)) {
                                    this.richiestaAvviaCalibrazione();
                                }

                            }

                            case (Static.F_WL_SCELTA + "_ready") -> {
                                this.Rm.setWLscelta(leggiFile(Static.F_WL_SCELTA));
                                Rm.impostaWLScelta();
                            }
                     
                            /*Aggiungere la gestione della curva, contatori, stato con 
                                * la creazione dei file F_CURVA_READY, F_LAVORO_READY. F_PULSANTE_READY
                             */
                            case Static.F_WL_PRONTA -> {
                                if (this.Rm.getRichiesta().equals(Static.RICHIESTA_AVVIO_WL)) {
                                    // Nel caso in cui la scelta della WL fosse stata fatta dall'esterno
                                    this.Rm.setWLscelta(leggiFile(Static.F_WL_SCELTA));
                                    richiestaAvvioWL();//Crea richiesta_avvio
                                    //Simulare avvio del lavoro da parte di screen come se fosse stato scelto
                                    //dalla lista dei lavori
                                    this.Rm.impostaLavoroScelto();
                                    this.Rm.avviaLavoro();
                                    //this.Rm.PanelStarted();
                                }
                            }
                            case Static.F_RICHIESTA + "_ready" ->
                                this.Rm.setRichiesta(leggiFile(Static.F_RICHIESTA));
                            case Static.F_RISPOSTA_TIRO_ERRATO_ACCETTA ->
                                this.Rm.setRispostaErrore("accetta");
                            case Static.F_RISPOSTA_TIRO_ERRATO_ANNULLA ->
                                this.Rm.setRispostaErrore("annulla");
                            case Static.F_RISPOSTA_TIRO_ERRATO_CONTINUA ->
                                this.Rm.setRispostaErrore("continua");
                            case "listaCert_ready" ->
                                leggiCertInfo();

                        }
                    }
                    if (kind == ENTRY_DELETE) {
                        if (!fileName.toString().startsWith(Static.F_SENSORI)) {
                            Static.debug("Eliminato: " + fileName, 4);
                        }
                        switch (fileName.toString()) {
                            case Static.F_ARIA ->
                                this.Rm.ariaChiusa();
                            case Static.F_NO_SENSORE -> {  // sensore collegato
                                this.Rm.setSensoreCollegato(true);
                                if (this.Rm.getPressioneAriaIn() > 2) { // aria in ingresso corretta
                                    this.Rm.setCtCanStart(false);
                                    this.Rm.PanelMain();
                                }
                                if (Rm.getPanCur().equals("main")) {
                                    this.Rm.PanelMain();
                                }
                                if (Rm.getPanCur().equals("start")) {
                                    this.Rm.PanelStart();
                                }
                            }
                            case Static.F_ERRORE ->
                                errore(false);
                            case Static.F_ABILITA_CALIBRAZIONE -> {
                                this.Rm.abilitaCalibrazione(false);
                                if (this.Rm.getPanCur().equals("start")) {
                                    this.Rm.PanelStart();
                                }
                            }
                            case Static.F_CONFERMA_STOP_PAUSA ->
                                this.Rm.setConfermaStopPausa(false);
                            case Static.F_CONFERMA_RISP_ERRORE ->
                                this.Rm.setConfermaRispErrore(false);
                            case Static.F_CONTROLLER_ONLINE -> {
                                if (this.ruolo.equals("1")) {   // Backup
                                    this.Rm.setControllerIndicator(6);  // Rosso - lettera B
                                } else if (this.ruolo.equals("2")) {    // Standard
                                    this.Rm.setControllerIndicator(2);  // Rosso - lettera C
                                }
                            }
                            case Static.F_CONTROLLER_BAD -> {
                                if (this.ruolo.equals("1") || this.ruolo.equals("2")) {
                                    this.Rm.setControllerIndicator(0);  // Verde
                                }
                            }
                            case Static.F_RESET_REQUEST -> {
                                this.Rm.setRichiesta("");
                                initValues();
                                this.Rm.esegui("aggiorna_nm_list");
                                this.Rm.PanelMain();
                            }
                            case Static.F_SYSTEM_FREEZE -> {
                                Rm.setInFreeze(false);
                                Rm.PanelMain();
                            }
                            case Static.F_SYSTEM_LOCK_EMERGENCY -> {
                                Rm.setInFreeze(false);
                                Rm.PanelMain();
                            }
                            case (Static.F_USB_LISTA_FILE) -> {
                                if (Rm.isBackup()) {
                                    Rm.PanelMain();
                                }
                                this.Rm.setInBackup(false);
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
     * Legge il file con la descrizione dei lavori
     */
    private void readLavori() {
        this.Rm.aggiornaLavori(leggiFileElenco(Static.F_W));
    }

    /**
     * Legge il file con la descrizione delle WorkList Aggiunto controllo se il
     * file è vuoto
     */
    private void readWl() {
        String ContenutoFile = leggiFile(Static.F_WL);
        if (!ContenutoFile.startsWith("errore ")) {
            this.Rm.aggiornaWl(new JSONArray(ContenutoFile));
        } else {
            System.out.println("Errore lettura file " + Static.F_WL);
        }
    }

    /**
     * Legge il file con la descrizione delle info di sistema
     */
    private void readInfo() {
        List<String> LeggiFileElencoInfo = JFileWorker.leggiFileElenco(Static.F_INFO);
        if (LeggiFileElencoInfo.isEmpty()) {
            LeggiFileElencoInfo.add("Manca file Info");
        }
        this.Rm.setInfoAggiuntive(LeggiFileElencoInfo);
        this.Rm.setListInfo(LeggiFileElencoInfo);
    }

    /**
     * Legge il file Warning e aggiorna la lista
     */
    private void readWarning() {
//        lavoro che deve essere fatto da JDoWorker
        List<String> warning_file = JFileWorker.leggiFileElenco(Static.F_WARNING);
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
//        this.Rm.getjLabelWarning().repaint();
//        this.Rm.repaint();
    }

    /**
     * Legge il file con la descrizione della configurazione della LAN DA FARE
     * Leggere il DB è meglio
     */
    private void readInfoInterfaces() {
        this.Rm.aggiornaListLan(JFileWorker.leggiFileElenco(Static.F_INFO_INTERFACES));
    }

    /**
     * Legge il file con la descrizione della configurazione della WiFi Come
     * sopra forse è meglio leggere il DB
     */
    private void readSetupWifi() {
        this.Rm.aggiornaListWiFi(JFileWorker.leggiFileElenco(Static.F_STATUS_WIFI));
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
        String line = JFileWorker.leggiFile(Static.F_SENSORI);
        this.Rm.updateSensori(line);
    }

    /**
     * inizializza i valori in base al contenuto dei file Sono necessari: -
     * lavori.txt; letto sul DB tabella "lavori", 4 campi: nome, lotti, pezzi,
     * descrizione
     *
     */
    public void initValues() {
        this.Rm.setSnCT(leggiFile("hostname"));
        this.Rm.getjLabelSnCGLock().setText(this.Rm.getSnCT());

        if (fileExists(Static.PATH_WATCH + Static.F_RESET_REQUEST)) {
            //Richiesta se si vuole fare reset del sistema
            this.Rm.setRichiesta(Static.RICHIESTA_SYSTEM_RESET);
            this.Rm.chiediConfermaReset();
        }
        this.leggiNoSensore();
        this.leggiAriaInMinMax();   // Valori scritti nei files da Control
        this.leggiAbilitaCalibrazione();
        this.leggiConfermaRispErrore();
        this.leggiConfermaStopPausa();
        this.impostaRuolo();
        this.readLanStatus();
        this.readInternetStatus();
        this.readVpnStatus();

        if (!ruolo.equals("3") && !ruolo.equals("0")) {
            this.leggiControllerOnline();
        }
        this.aggiornaContatori();
        this.aggiornaSensori();
        this.readLavori();//Se non esite il file imposta il default
        this.readWl();//Se non esiste il file ?
        this.readTools();//Se esiste il file /home/adminsb/etc/first_time, avvia la scelta del tool

        // this.WlPronta(); la WL pronta deve essere comandata da Control
        this.readInfo();// Se non esiste il file imposta a stringa info
        this.readWarning();// Se non esiste il file imposta a sringa warning
        this.readCurvaDiRiferimento();//Se non esiste il file imposta a 0
        //this.LeggiAriaInMinMax(); // Letto dal DB
        //this.LeggiSessione();// Se non esite il file imposta il file a "0"
//        this.mostraStatoAria(Static.ARIA_CHIUSA);//Se non esiste il file imposta a "0"
        this.readInfoInterfaces();
        this.readListaNMdevice();
        String rigaFile = leggiFile(Static.F_ARIA);
        if (rigaFile.contains("errore")) {
            this.Rm.ariaChiusa();
        } else {
            this.Rm.ariaAperta();
        }
        this.readSnCT();
        this.readNomeDevice();//Se non esiste il file imposta a CG-0000-00
//        cancellaFile(Static.PATH_WATCH + "errore"); // Dovrebbe farlo COntrol
        this.Rm.set_jLabel_B_L("Main");
        this.Rm.repaint();
//        this.Rm.esegui("aggiorna_nm_list");
    }

    /**
     * Metodo per fare il lock del file basato su filesystem: se non esiste
     * nomFile.lock lo scrive bloccando così il file
     *
     * @param NomeFile
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
     * un file Aggiunto controllo su errore di lettura di file inesistente o
     * vuoto
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
                Static.debug("File " + inputFile.getAbsolutePath() + " does not exists", 2);
                ListaRighe.add("errore lettura File " + NomeFile);
                return ListaRighe;
            }
            lock = lockFile(NomeFile);
            if (lock) {
                ListaRighe = Files.readAllLines(Paths.get(Static.PATH_WATCH + NomeFile), StandardCharsets.UTF_8);
                if (ListaRighe.size() == 0) {
                    ListaRighe.add("errore lettura File " + NomeFile);
                }
                unLock(NomeFile);
            }
        } catch (IOException ex) {
            Logger.getLogger(JFileWorker.class.getName()).log(Level.SEVERE, null, ex);
            ListaRighe.add("errore lettura File " + NomeFile);
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
                Static.debug("File " + inputFile.getAbsolutePath() + " does not exists", 2);
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
                if (contenutoFile.length() == 0) {
                    contenutoFile = "errore " + NomeFile;
                }
            }
        } catch (IOException ex) {
            Logger.getLogger(JFileWorker.class.getName()).log(Level.SEVERE, null, ex);
            unLock(NomeFile);
            return "errore ";
        }
        unLock(NomeFile);
        return contenutoFile;
    }

/**
     * Metodo che utilizza il controllo del Lock per leggere una riga dal file
     *
     * @param NomeFile
     * @return La riga letta del file
     */
    public static String leggiFileNormal(String NomeFile) {
        String contenutoFile = "";
        Scanner myReader;
        boolean lock;
        try {
            File inputFile = new File(NomeFile);
            if (!inputFile.exists()) {
                Static.debug("File " + inputFile.getAbsolutePath() + " does not exists", 2);
                contenutoFile = "errore " + NomeFile;
                return contenutoFile;
            }
            lock = lockFile(NomeFile);
            if (lock) {
                FileReader fr = new FileReader(NomeFile);
                myReader = new Scanner(fr);
                while (myReader.hasNextLine()) {
                    contenutoFile += myReader.nextLine();
                }
                if (contenutoFile.length() == 0) {
                    contenutoFile = "errore " + NomeFile;
                }
            }
        } catch (IOException ex) {
            Logger.getLogger(JFileWorker.class.getName()).log(Level.SEVERE, null, ex);
            unLock(NomeFile);
            return "errore ";
        }
        unLock(NomeFile);
        return contenutoFile;
    }
    
    private void leggiNoSensore() {
        File noSensore = new File(Static.PATH_WATCH + Static.F_NO_SENSORE);
        this.Rm.setSensoreCollegato(!noSensore.exists());
        if (noSensore.exists()) {
            this.Rm.setCtCanStart(false);
        } else {
            this.Rm.setCtCanStart(true);
        }
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
    @SuppressWarnings("UseSpecificCatch")
    private void readNomeDevice() {
        try {
            String[] nomeSplit = leggiFile(Static.F_NOME_DEVICE).split("§");
            this.Rm.setNomeDevice(nomeSplit[0]);
            this.Rm.setDurataPlcOk(Integer.parseInt(nomeSplit[1]));
        } catch (Exception ex) {
            Static.debug("Error reading UM", 2);
        }
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
     * Cancella il file, senza PATH predefinita
     *
     * @param NomeFile inserire anche la PATH
     */
    public static void cancellaFileGenerico(String NomeFile) {
        File f = new File(NomeFile);
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

    /**
     * Aggiorna i contatori da mostrare allo schermo
     *
     * 0) cntLottio, 1) cntPezzi , 2) tiriValidi, 3) tiriAnnullati, 4)
     * tiriErrati, 5) tiriTotali, 6) cntCicli, 7) UDLotti, 8) udPezzi,
     * 9)indicelavoriWL
     *
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
                this.Rm.setCntCicli(Integer.parseInt(contatori[6]));
                this.Rm.setIndiceLavoroCorrente(Integer.parseInt(contatori[9]));
                // aggiorna la visualizzazione dei contatori nel pannello
                this.Rm.visualizzaContatori();
                this.Rm.setLabelWL();
            } catch (NumberFormatException e) {
                Static.debug("File contatori contains non numeric values\n" + e.getMessage(), 2);
            }
        }
    }

    /**
     * Lettura file lista usb file
     */
    private void readListaFileUSB() {
        List<String> list_usb_files = leggiFileElenco(Static.F_USB_LISTA_FILE);
        Collections.sort(list_usb_files, String.CASE_INSENSITIVE_ORDER);
        this.Rm.setListUsbFiles(list_usb_files);
    }

    /**
     * networkmanager crea una lista dei device Questo metodo la legge, è stato
     * avviato un bash prima che ha creato il file con la lista
     */
    private void readListaNMdevice() {
        List<String> list_nm_con = leggiFileElenco(Static.F_LISTA_NM_CON);
        Collections.sort(list_nm_con, String.CASE_INSENSITIVE_ORDER);
        this.Rm.setListNmCon(list_nm_con);
        boolean wifiIndicator = false;
        for (String string : list_nm_con) {
            if (string.contains("AP_" + this.Rm.getSnCT()) && string.contains("ON")) {    // la prima riga che contiene "eth" e "ON" accende l'indicatore Lan
                wifiIndicator = true;
            }
        }
        this.Rm.setWiFiIndicator(wifiIndicator);
        if (this.Rm.getPanCur().equals("setup")) {
            this.Rm.updateNmButtons();
        }
    }

    /**
     * leggere lo stato di connettività di Internet
     */
    private void readInternetStatus() {
        this.Rm.setInternetIndicator(leggiFile(Static.F_STATUS_INTERNET).equals("full"));
    }

    public void updateSystemDateTime() {
        try {
            String[] stringa = new String[]{"/home/adminsb/bin/set_time.sh"};
            Runtime.getRuntime().exec(stringa);
        } catch (Exception e) {
            Static.debug("Error setting date time: " + e, 2);
        }
    }

    /**
     * legge il file curva per costruire il grafico mostrato nel Pannello Canvas
     */
    private void gestisciCurva() {
        this.Rm.setRispostaErrore("");
        leggiCurva();
        this.Rm.setEsitoTiro(leggiFile(Static.F_ESITO_TIRO));
        if (!this.Rm.getInErrore()
                && !(this.Rm.getStato().equals(Static.STATO_CALIBRAZIONE)
                || this.Rm.getStato().equals(Static.STATO_CALIBRAZIONE_TEST))) {

            if (this.Rm.getDurataPlcOk() > 0) {
                Color oldColor = this.Rm.getjPanelStarted().getBackground();
                this.Rm.getjPanelStarted().setBackground(Color.green);
                try {
                    Thread.sleep(this.Rm.getDurataPlcOk() * 100);
                } catch (InterruptedException ex) {
                    Static.debug("Error changing background color " + ex, 2);
                }
                this.Rm.getjPanelStarted().setBackground(oldColor);
            }

        }
//        if( !this.Rm.getInErrore() ){
//            new Thread(() -> {
//                if ( this.Rm.getDurataPlcOk() > 0) {
//                    Color oldColor = this.Rm.getjPanelStarted().getBackground();
//                    this.Rm.getjPanelStarted().setBackground(Color.green);
//                    try {
//                        Thread.sleep(this.Rm.getDurataPlcOk() * 100);
//                    } catch (InterruptedException ex) {
//                        Static.debug("Error changing color background "+ex, 2);
//                    }
//                    this.Rm.getjPanelStarted().setBackground(oldColor);
//                }
//            }).start();        
//        }
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
            case Static.STATO_CONCLUSA_WL -> {
                this.Rm.setWLConclusa(true);
                this.Rm.setLavoroConcluso(true);
                this.Rm.PanelStarted();
            }

            case Static.STATO_CONCLUSO -> {
                this.Rm.setLavoroConcluso(true);
                this.Rm.PanelStarted();
            }
            case Static.STATO_AVVIATA_WL -> {
                //this.Rm.setInWl(true);
                this.Rm.setWLConclusa(false);
                this.Rm.setInErrore(false);
                this.Rm.setLavoroConcluso(false);
                this.aggiornaContatori();
                this.Rm.PanelStarted();
            }
            case Static.STATO_AVVIATO_W -> {
                //this.Rm.setInWl(false); è sbagliato cambiare stato della WL va impostato true/false al momento della richiesta di tipo di avvio di atività W
                this.Rm.setLavoroConcluso(false);
                this.Rm.setInErrore(false);
                this.aggiornaContatori();
                this.Rm.PanelStarted();
            }
            case Static.STATO_CALIBRAZIONE -> {
                this.Rm.avviaCalibrazione();
            }
            case Static.STATO_CALIBRAZIONE_TEST -> {
                this.Rm.avviaCalibrazioneTest();
            }
            case Static.STATO_STOP -> {
                this.Rm.setWLConclusa(false);
                if (this.incoerente) {
                    this.Rm.PanelMain();
                } else {
                    if (Rm.getPanCur().equals("started")
                            || Rm.getPanCur().equals("canvas")
                            || Rm.getPanCur().equals("dialog")) {
                        this.Rm.PanelStart();
                    }
                }
            }
            case Static.RICHIESTA_RIAVVIO -> {
                if (this.Rm.getStato().equals(Static.STATO_CONCLUSO)) {
                    this.Rm.avviaLavoro();
                }
            }

        }
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
            String nomeLavoro = leggiFile(Static.F_W_SCELTO);
            Rm.setLavoroScelto(nomeLavoro);
            int index = 0;
            for (String[] lav : Rm.getElencoLavori()) {
                if (lav[0].equals(nomeLavoro)) {
                    break;
                }
                index++;
            }
            this.Rm.setLimLotti(this.Rm.getElencoLavori().get(index)[1]);
            this.Rm.setLimPezzi(this.Rm.getElencoLavori().get(index)[2]);
            this.Rm.setUDLotti(this.Rm.getElencoLavori().get(index)[5]);
            this.Rm.setUDPezzi(this.Rm.getElencoLavori().get(index)[6]);

            // cerco il lavoro nell'elenco
            for (String[] elencoLavori : this.Rm.getElencoLavori()) {
                if (elencoLavori[0].equals(nomeLavoro)) {   // lavoro trovato
                    Rm.setLimLotti(elencoLavori[1]);
                    Rm.setLimPezzi(elencoLavori[2]);
                    Rm.setUDLotti(elencoLavori[5]);
                    Rm.setUDPezzi(elencoLavori[6]);
                    break;
                }
            }
            String umString = leggiFile(Static.F_UM);   // Ogni lavoro ha un proprio tool con un diverso fattore di conversione
            String[] umArray = umString.split(",");
            this.Rm.setUM(umArray[0]);  // UM è di pertinenza del CT
            this.Rm.setConversion(Integer.parseInt(umArray[1]));    // Xf è di pertinenza del tool
            this.Rm.setPressioneMax(Integer.parseInt(umArray[2]));          // Valore massimo di pressione gestita dal sensore tool
            this.Rm.lavoroPronto();
        } catch (Exception e) {
            Static.debug("w_scelto -> wrong parameters count", 2);
        }
    }

    /**
     * Gestisce l'avvio di una Work List. La WL potrebbe essere stata chiesta da
     * remoto, quindi leggo prima il file F_WL_SCELTA
     */
    private void WlPronta() {
        String wlScelta = leggiFile(Static.F_WL_SCELTA);
        if (!wlScelta.equals(Rm.getWLscelta())) {   // la worklist è stata scelta dall'esterno
            Rm.setWLscelta(wlScelta);
        }
    }

    private void leggiAbilitaCalibrazione() {
        File inputFile = new File(Static.PATH_WATCH + Static.F_ABILITA_CALIBRAZIONE);
        if (inputFile.exists()) {
            this.Rm.abilitaCalibrazione(true);
        } else {
            this.Rm.abilitaCalibrazione(false);
        }
    }

    /**
     * Verifica la presenza del file CONTROLLER_ONLINE , CONTROLLER_BAD on line
     * ma configurato male
     */
    private void leggiControllerOnline() {
        File inputFile = new File(Static.PATH_WATCH + Static.F_CONTROLLER_ONLINE);

        if (inputFile.exists()) {
            if (ruolo.equals("2")) {
                this.Rm.setControllerIndicator(0);  // Verde
            }
        } else {
            this.Rm.setControllerIndicator(2);  // Rosso
        }
        inputFile = new File(Static.PATH_WATCH + Static.F_CONTROLLER_BAD);
        if (inputFile.exists()) {
            this.Rm.setControllerIndicator(1);  // Giallo
        }
    }

    /**
     * Verifica l'esistenza del file errore
     */
    private void leggiErrore() {
        File inputFile = new File(Static.PATH_WATCH + Static.F_ERRORE);
        if (inputFile.exists()) {
            this.Rm.setInErrore(true);
        } else {
            this.Rm.setInErrore(false);
        }
    }

    private void readPosizioneErrori() {
        this.Rm.setPosizioneErrori(leggiFile(Static.F_POSIZIONE_ERRORI));
    }

    private void readPiccoRiferimento() {
        String piccoStr = leggiFile(Static.F_PICCORIF);
        String[] piccoArray = piccoStr.split(",");
        this.Rm.gr.setPiccoRif(
                Integer.parseInt(piccoArray[0]),
                Integer.parseInt(piccoArray[2]));
    }

    /**
     * Avvia il lavoro scelto
     */
    void richiestaAvvioLavoro() {
        JFileWorker.scriviFileConReady(Static.F_RICHIESTA, Static.RICHIESTA_AVVIO_LAVORO);
        this.Rm.gr.resetCurva();
    }

    /**
     * Avvia la WL scelta
     */
    void richiestaAvvioWL() {
        JFileWorker.scriviFileConReady(Static.F_RICHIESTA, Static.RICHIESTA_AVVIO_WL);
        this.Rm.gr.resetCurva();
    }

    /**
     * Avvia la calibrazione
     */
    void richiestaAvviaCalibrazione() {
        JFileWorker.scriviFileConReady(Static.F_RICHIESTA, Static.RICHIESTA_CALIBRAZIONE);
    }

    private void leggiConfermaRispErrore() {
        File inputFile = new File(Static.PATH_WATCH + Static.F_CONFERMA_RISP_ERRORE);
        if (inputFile.exists()) {
            this.Rm.setConfermaRispErrore(true);
        } else {
            this.Rm.setConfermaRispErrore(false);
        }
    }

    private void readTools() {
        if (fileExists("/home/adminsb/etc/" + Static.F_FIRST_TIME)) {    // siamo al primo avvio o al reset
            this.Rm.setInSceltaTool(true);
        }
        this.Rm.aggiornaTools(leggiFileElenco(Static.F_TOOLS));
    }

    private void leggiConfermaStopPausa() {
        File inputFile = new File(Static.PATH_WATCH + Static.F_CONFERMA_STOP_PAUSA);
        if (inputFile.exists()) {
            this.Rm.setConfermaStopPausa(true);
        } else {
            this.Rm.setConfermaStopPausa(false);
        }
    }

    /**
     * Aggiorna la lista delle stringhe per la certificazione
     */
    public void leggiCertInfo() {
        List<String> certInfo = JFileWorker.leggiFileElenco("certInfo.txt");
        Rm.aggiornaListCert(certInfo);
    }

    private void readSnCT() {
        try {
            this.Rm.setSnCT(leggiFile("hostname"));
        } catch (Exception ex) {
            Static.debug("Error reading UM", 2);
        }
    }

    private void impostaRuolo() {
        ruolo = leggiFile(Static.F_RUOLO);
        //In base al ruolo imposto il colore di sfondo del Led
        // che indica lo stato del Controller
        switch (ruolo) {    // Il colore del ruolo standard viene gestito in real time
            case "0" ->
                this.Rm.setControllerIndicator(4);
            case "1" ->
                this.Rm.setControllerIndicator(5);
            case "3" ->
                this.Rm.setControllerIndicator(3);
        }
    }

    /**
     * il contenuto del file status_lan viene aggiornato dal bash status_lan.sh
     * controlla se i dispositivi wifi, usb0/eth1, eth0 sono collegati o meno il
     * cavo è collegato alla porta avviando il comando nmcli d show eth0 | grep
     * -i general.state | awk '{print $3}'
     *
     */
    private void readStatusLan() {
        String ethStatus = leggiFile(Static.F_STATUS_LAN);
        if (ethStatus.equals("0")) {
            this.Rm.setLanIndicator(false);
        } else {
            this.Rm.setLanIndicator(true);
        }
    }

    /**
     * Controlla l'esistenza o meno del file Occorre inserire la PATH completa
     *
     * @param fname (path+fname)
     * @return true o false
     */
    public synchronized boolean fileExists(String fname) {
        File inputFile = new File(fname);
        return inputFile.exists();
    }

    /**
     * Lettura dello stato della VPN verde, rosso, giallo se la VPN era attiva
     * ma la rete Internet viene meno
     */
    private void readVpnStatus() {
        this.Rm.setVPNIndicator(leggiFile(Static.F_STATUS_VPN));
    }

    /**
     * Serve per indicare lo stato attivo o meno della LAN
     */
    private void readLanStatus() {
        this.Rm.setLanIndicator(leggiFile(Static.F_STATUS_LAN).equals("1"));
    }

    /**
     * Server per aggiornare lo stato di scelta fatto da altre "fonti"
     *
     */
    private void readSonoIn() {
        String tipoLavoro = leggiFile(Static.F_SONO_IN);
        this.incoerente = (!this.Rm.getInWl().equals(tipoLavoro));
        this.Rm.setInWl(tipoLavoro);
    }
}
