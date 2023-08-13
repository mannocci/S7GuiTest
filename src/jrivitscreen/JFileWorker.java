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

import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.util.logging.Level;
import java.util.logging.Logger;
//import static java.nio.file.StandardWatchEventKinds.ENTRY_MODIFY;
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

/**
 *
 * @author lucamannocci
 */
public class JFileWorker extends Thread {
// Classi

    private final JRivitMain Rm;

    private WatchService watcher;
    private Path fileName;

    private final String f_setup_lan = "setup_lan.txt";
    private final String f_setup_wifi = "setup_wifi.txt";

    // se il lavoro è in corso contiene "1"
    private final String f_soglia_pressione_aria_in_min = "soglia_pressione_aria_in_min";
    private final String f_soglia_pressione_aria_in_max = "soglia_pressione_aria_in_max";

    public JFileWorker(JRivitMain mf) throws IOException {
        this.Rm = mf;
        // create gpio controller by file (run bash script before !)     
        try {
            watcher = FileSystems.getDefault().newWatchService();
        } catch (IOException ex) {
            Logger.getLogger(JFileWorker.class.getName()).log(Level.SEVERE, null, ex);
        }
        Path dir = Paths.get(Static.PATH_WATCH);
        dir.register(watcher, ENTRY_MODIFY, ENTRY_CREATE,
                ENTRY_DELETE);
        try {
            Thread.sleep(2000);//Attesa 2" per allocazione Classi
        } catch (InterruptedException ex) {
            Logger.getLogger(JFileWorker.class.getName()).log(Level.SEVERE, null, ex);
        }
            Static.debug("Watch Service Modify file registered for dir: " + dir.getFileName(), 3);
    }

    private void send_p(String sp) {
        this.Rm.pulsanteHw(sp);
    }

    @Override
    public void run() {

        while (true) {
            WatchKey key;
            try {
                key = watcher.take();
            } catch (InterruptedException ex) {
                return;
            }
            for (WatchEvent<?> event : key.pollEvents()) {
                WatchEvent.Kind<?> kind = event.kind();

                @SuppressWarnings("unchecked")
                WatchEvent<Path> ev = (WatchEvent<Path>) event;
                fileName = ev.context();
                Static.debug(kind.name() + ": " + fileName, 3);
                if (kind == ENTRY_CREATE) {
                    switch (fileName.toString()) {
                        case Static.F_ARIA ->
                            mostraStatoAria(Static.ARIA_APERTA);
                        case Static.F_CONTATORI_AGGIORNATI ->
                            aggiornaContatori();
                        case Static.F_ERRORE ->
                            errore(true);
                        case Static.F_CHIEDI_CONFERMA_NO ->
                            impostaChiediConferma(true);
                        case Static.F_CHIEDI_CONFERMA_STOP ->
                            impostaChiediConfermaStop(true);
                        case Static.F_IN_PAUSA -> {
                            readLavoroInPausa();
                            aggiornaPausa();
                        }
                        case Static.F_IN_STOP ->
                            aggiornaStop();
                        case "killScreen" ->
                            this.Rm.exit();
                        case Static.F_FATTO_FILE_CURVA ->
                            gestisciCurva();
                        case Static.F_STATUS_LAN -> {
                            readSetupLan();
                        }
                        case Static.F_STATUS_WIFI -> {
                            readSetupWifi();
                        }
                        case Static.F_LISTA_NM_CON ->
                            readListaNMdevice();
                    }

                }
                if (kind == ENTRY_DELETE) {
                    switch (fileName.toString()) {
                        case Static.F_ARIA ->
                            mostraStatoAria(Static.ARIA_CHIUSA);
                        case Static.F_ERRORE ->
                            errore(false);
                        case Static.F_CHIEDI_CONFERMA_NO ->
                            impostaChiediConferma(false);
                        case Static.F_CHIEDI_CONFERMA_STOP ->
                            impostaChiediConfermaStop(false);
                        case Static.F_RISPOSTA_TIRO_ERRATO_CONTINUA, Static.F_RISPOSTA_TIRO_ERRATO_ACCETTA, Static.F_RISPOSTA_TIRO_ERRATO_ANNULLA -> {
                            this.Rm.setInErrore(false);
                            this.Rm.aggiornaDaErroreTiro();
                        }
                    }
                }
                if (kind == ENTRY_MODIFY) {
                    switch (fileName.toString()) {
                        case Static.F_SENSORI, Static.F_INFO ->
                            readInfo();
                        case Static.F_WARNING ->
                            readWarning();
                        case Static.F_LAVORI ->
                            readLavori();
                        case Static.F_NOME_DEVICE ->
                            readNomeDevice();

                    }
                    try {
                        Thread.sleep(2);
                        aggiornaSensori();
                    } catch (InterruptedException ex) {
                        System.out.printf("Error: " + ex);
                    }
                }
            }
            boolean valid = key.reset();
            if (!valid) {
                break;
            }
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

        if (stato == Static.ARIA_CHIUSA) {//Aria chiusa
            //bash_cmd_aria[4] = this.close;
//            bash_cmd_verde[4] = this.close;
//            bash_cmd_rosso[4] = this.open;
//            run_system_bash(this.bash_cmd_rosso);
//            run_system_bash(this.bash_cmd_verde);
            //run_system_bash(this.bash_cmd_aria); viene fatto da Control
            this.Rm.ariaChiusa();

        } else { //Aria aperta
            //bash_cmd_aria[4] = this.open;
//            bash_cmd_verde[4] = this.open;
//            bash_cmd_rosso[4] = this.close;
//            run_system_bash(bash_cmd_rosso);
//            run_system_bash(bash_cmd_verde);
            //run_system_bash(bash_cmd_aria);  viene fatto da Control
            this.Rm.ariaAperta();

        }
    }

    /**
     * Legge il file con la descrizione dei lavori
     */
    private void readLavori() {
        this.Rm.aggiornaLavori(LeggiFileElencoLock(Static.F_LAVORI));
    }

    /**
     * Legge il file con la descrizione delle info di sistema
     */
    private void readInfo() {
        List<String> LeggiFileElencoInfo = this.LeggiFileElencoLock(Static.F_INFO);
        if (LeggiFileElencoInfo.isEmpty()) {
            LeggiFileElencoInfo.add("Manca file Info");
        }
        this.Rm.setListInfo(LeggiFileElencoInfo);
    }

    /**
     * Legge il file con la descrizione dei JFileWorker
     */
    private void readWarning() {
        List<String> LeggiFileElencoWarning = this.LeggiFileElencoLock(Static.F_WARNING);
        if (LeggiFileElencoWarning.isEmpty()) {
            LeggiFileElencoWarning.add("Manca file Warning");
        }
        this.Rm.setListWarning(LeggiFileElencoWarning);

    }

    /**
     * Legge il file con la descrizione della configurazione della LAN DA FARE
     * Leggere il DB è meglio
     */
    private void readSetupLan() {
        this.Rm.aggiornaSetupLan(this.LeggiFileElencoLock(Static.F_STATUS_LAN));
    }

    /**
     * Legge il file con la descrizione della configurazione della WiFi Come
     * sopra forse è meglio leggere il DB
     */
    private void readSetupWifi() {
        this.Rm.aggiornaSetupWiFi(this.LeggiFileElencoLock(Static.F_STATUS_WIFI));
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
        }
        this.Rm.setInErrore(si_o_no);
    }

    /**
     * PressioneAria viene letta ogni secondo
     */
    private void aggiornaSensori() {
        String line = this.LeggiFileLock(Static.F_SENSORI);
        this.Rm.update_sensori(line);
    }

    /**
     * inizializza i valori in base al contenuto dei file Sono necessari: -
     * lavori.txt; letto sul DB tabella "lavori", 4 campi: nome, lotti, pezzi,
     * descrizione
     *
     */
    public void initValues() {
        this.aggiornaSensori();// Occorre che vi sia il batch avviato
        this.aggiornaContatori();
        this.readLavori();//Se non essite il file imposta il default
        this.readInfo();// Se non esite il file imposta a stringa info
        this.readWarning();// Se non esiste il file imposta a sringa warning
        this.read_lavoro_scelto();//Se non esite il file imposta a 0
        this.readLavoroInPausa();//Se non esite il file imposta non in pausa
        //this.LeggiAriaInMinMax(); // Letto dal DB
        //this.LeggiSessione();// Se non essite il file imposta il file a "0"
        this.mostraStatoAria(Static.ARIA_CHIUSA);//Se non esiste il file imposta a "0"
        this.readNomeDevice();//Se non esiste il file imposta a CT-0000-00
//        cancellaFileLock(Static.PATH_WATCH + "errore"); // Dovrebbe farlo COntrol
        this.Rm.set_jLabel_B_L("Main");
        this.Rm.repaint();
    }

    /**
     * LeggiFileLavoroInPausa /tmp/CT/inpausa
     */
    private void readLavoroInPausa() {
        this.Rm.setInPausa(LeggiFileLock(Static.F_IN_PAUSA));
    }

    /**
     * LeggiSessione
     */
    private void LeggiSessione() {
        this.Rm.aggiornaSessione(LeggiFileLock(Static.F_SESSIONE));
    }//End LeggiFileLavoriDescrizione

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
     * Metodo per fare il lock del file "alla vecchia" ;-) se non
     * essite<nomFile>.lock lo scrive blocccando così il file
     *
     * @return
     */
    public static boolean LockFile(String NomeFile) {
        File inputFile = new File(Static.PATH_WATCH + NomeFile + ".lok");
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
                FileWriter fw = new FileWriter(Static.PATH_WATCH + NomeFile + ".lok");
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
     * Libera il lock cancellando <nomeFile>.lock
     *
     * @param NomeFile
     */
    public void unlockFile(String NomeFile) {
        cancellaFileLock(NomeFile + ".lok");
    }

    /**
     * scrive in un file
     *
     * @param NomeFile
     * @param Testo String testo da scrivere nel file
     */
    public static int ScriviFileLock(String NomeFile, String Testo) {
        //cancellaFile(NomeFile);
        boolean lock = LockFile(NomeFile);
        if (lock) {
            try {
                FileWriter fw = new FileWriter(Static.PATH_WATCH + NomeFile);
                try (PrintWriter pw = new PrintWriter(fw)) {
                    pw.print(Testo);
                    pw.flush();
                }
            } catch (IOException ex) {
                Logger.getLogger(JFileWorker.class.getName()).log(Level.SEVERE, null, ex);
                cancellaFileLock(NomeFile + ".lok");
                return -1;
            }
            cancellaFileLock(NomeFile + ".lok");
        }
        return 0;
    }

    /**
     * Metodo che utilizza il controllo del Lock per leggere una riga dal file
     *
     * @param NomeFile
     * @return La riga letta del file
     */
    public static List<String> LeggiFileElencoLock(String NomeFile) {
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
            lock = LockFile(NomeFile);
            if (lock) {
                ListaRighe = Files.readAllLines(Paths.get(Static.PATH_WATCH + NomeFile), StandardCharsets.UTF_8);
                cancellaFileLock(NomeFile + ".lok");
            }
        } catch (IOException ex) {
            Logger.getLogger(JFileWorker.class.getName()).log(Level.SEVERE, null, ex);
            cancellaFileLock(NomeFile + ".lok");
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
    public static String LeggiFileLock(String NomeFile) {
        String contenutoFile = "";
        Scanner myReader;
        boolean lock;
        try {
            File inputFile = new File(Static.PATH_WATCH + NomeFile);
            if (!inputFile.exists()) {
                Static.debug("Il File " + inputFile.getAbsolutePath()
                        + " non esiste\n", 2);
                contenutoFile = "errore lettura File " + NomeFile;
                return contenutoFile;
            }
            lock = LockFile(NomeFile);
            if (lock) {
                FileReader fr = new FileReader(Static.PATH_WATCH + NomeFile);
                myReader = new Scanner(fr);
                while (myReader.hasNextLine()) {
                    contenutoFile += myReader.nextLine();
                }
            }
        } catch (IOException ex) {
            Logger.getLogger(JFileWorker.class.getName()).log(Level.SEVERE, null, ex);
            cancellaFileLock(NomeFile + ".lok");
            return "";
        }
        cancellaFileLock(NomeFile + ".lok");
        return contenutoFile;
    }

    private void LeggiAriaInMinMax() {
        try {
            float min, max;
            min = Float.parseFloat(LeggiFileLock(f_soglia_pressione_aria_in_min));
            max = Float.parseFloat(LeggiFileLock(f_soglia_pressione_aria_in_max));
            this.Rm.update_soglie_pressione_aria_in(min, max);
        } catch (NumberFormatException e) {
            Static.debug("Contenuto dei file pressione_in non numerico !\n" + e.getMessage(), 2);
        }
    }

    private void leggiCurva() {
        this.Rm.setCurva(LeggiFileLock(Static.F_CURVA));
    }

    private void read_lavoro_scelto() {
        this.Rm.setLavoroScelto(LeggiFileLock(Static.F_LAVORO_SCELTO));
    }

    /**
     * legge dal file nome_device il nome del ControlRiv SN registrato nel
     * record CT -> sn
     */
    private void readNomeDevice() {
        this.Rm.setNomeDevice(LeggiFileLock(Static.F_NOME_DEVICE));
    }

    private void lottiOk() {
        try {
            int lottiok = Integer.parseInt(LeggiFileLock(Static.F_LOTTI_OK));
            if (lottiok == 0) {
                lottiok = 1;
            }
            this.Rm.set_nr_lotti_ok(lottiok);
        } catch (NumberFormatException e) {
            Static.debug("Contenuto del file lotti_ok non numerico !\n" + e.getMessage(), 2);
        }
    }

    /**
     * Cancellazione del lavoro svolto azzerare i contatori azzera lavoro-scelto
     * impostando prima il file ultimo_lavoro_scelto con il nome del alvoro,
     * consentendo così al Pannello Start con la lista dei lavori di
     * posizionarsi sull'ultimo lavoro scelto accendere led giallo
     *
     */
    private void aggiornaStop() {
        ScriviFileLock(Static.F_IN_STOP, "" + Static.STATO_STOP);  //  stato di abort
    }

    /**
     * I contatori rimangono tali posizionarsi sull'ultimo lavoro scelto
     * accendere led giallo
     *
     */
    private void aggiornaPausa() {
        this.ScriviFileLock(Static.F_IN_PAUSA, "" + Static.STATO_PAUSA);
    }

    private void impostaChiediConferma(boolean si_o_no) {

        this.Rm.setChiedi_conferma(si_o_no);
    }

    /**
     * cancella un file
     *
     * @param NomeFile
     */
    public static void cancellaFileLock(String NomeFile) {
        File f = new File(Static.PATH_WATCH + NomeFile);
        while (f.canWrite()) {
            try {
                Thread.sleep(Static.ATTESA_SCRITTURA_FILE);
            } catch (InterruptedException ex) {
                Logger.getLogger(JFileWorker.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
        if (f.exists()) {
            if (!f.delete()) {
                Static.debug("errore eliminando il file " + NomeFile, 2);
            }
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
            if (!f.delete()) {
                Static.debug("errore eliminando il file " + NomeFile, 2);
            }
        }
    }

    private void impostaChiediConfermaStop(boolean si_o_no) {
        this.Rm.setChiedi_conferma_stop(si_o_no);
    }

    private void aggiornaContatori() {
        String testo = LeggiFileLock(Static.F_CONTATORI);
        String[] contatori = testo.split(",");
        try {
            this.Rm.setLotto(Integer.parseInt(contatori[0]));
            this.Rm.setTiriNelLotto(Integer.parseInt(contatori[1]));
            this.Rm.setTiriValidi(Integer.parseInt(contatori[2]));
            this.Rm.setTiriAnnullati(Integer.parseInt(contatori[3]));
            this.Rm.setTiriErrati(Integer.parseInt(contatori[4]));
            this.Rm.setTiriTotali(Integer.parseInt(contatori[5]));

            // aggiorna la visualizzazione dei contatori nel pannello
            this.Rm.aggiornaContatori();
            cancellaFile(Static.F_CONTATORI_AGGIORNATI);
        } catch (NumberFormatException e) {
            Static.debug("File contatori contiene valori non numerici\n" + e.getMessage(), 2);
        }
    }

    private void readListaNMdevice() {
        List<String> list_nm_con = LeggiFileElencoLock(Static.F_LISTA_NM_CON);
        this.Rm.setListNmCon(list_nm_con);
    }

    private void gestisciCurva() {
        leggiCurva();
        this.Rm.PanelCanvas();
        this.Rm.mostraCurva();
    }
}
