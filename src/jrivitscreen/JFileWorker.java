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

import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.util.logging.Level;
import java.util.logging.Logger;
//import static java.nio.file.StandardWatchEventKinds.ENTRY_MODIFY;
import java.util.concurrent.locks.ReentrantReadWriteLock;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.io.RandomAccessFile;
import static java.lang.Runtime.getRuntime;
import java.nio.ByteBuffer;
import java.nio.channels.FileChannel;
import java.nio.channels.FileLock;
import java.nio.channels.OverlappingFileLockException;
import java.nio.charset.StandardCharsets;
import java.nio.file.FileSystems;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.nio.file.StandardWatchEventKinds;
import java.nio.file.WatchEvent;
import java.nio.file.WatchKey;
import java.nio.file.WatchService;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;
import java.util.concurrent.TimeUnit;

/**
 *
 * @author lucamannocci
 */
public class JFileWorker extends Thread {
// Classi

    private final JRivitMain Rm;
    private final JDoWorker jDo_w;

    private WatchService watcher;
    private Path fileName;
    private final String f_tiri = "tiri";
    private final String f_tiri_ok = "tiri_ok";
    private final String f_lotti_ok = "lotti_ok";
    private final String f_info = "info.txt";
    private final String f_warning = "warning.txt";
    private final String f_setup_lan = "setup_lan.txt";
    private final String f_setup_wifi = "setup_wifi.txt";
    private final String f_in_pausa = "in_pausa";

    // se il lavoro è in corso contiene "1"
    private final String f_curva = "curva";
    private final String f_sessione = "sessione";
    private final String f_sensori = "sensori";
    private final String f_soglia_pressione_aria_in_min = "soglia_pressione_aria_in_min";
    private final String f_soglia_pressione_aria_in_max = "soglia_pressione_aria_in_max";
    private static FileChannel fc;
    private static RandomAccessFile randomAccessFile;

    String open = "255";
    String close = "0";
    String hw_aria = "4";
    String hw_led_rosso = "1", hw_led_giallo = "2", hw_led_verde = "3";
    String Megaind_Program = "/home/adminsb/bin/megaind";
    private String[] bash_cmd_rosso = {//Comanda led rosso
        this.Megaind_Program,
        "0",
        "dodwr",
        this.hw_led_rosso, this.close};
    private String[] bash_cmd_giallo = {//Comanda led giallo
        this.Megaind_Program,
        "0",
        "dodwr",
        this.hw_led_giallo, this.close};
    private String[] bash_cmd_verde = {//Comanda led verde
        this.Megaind_Program,
        "0",
        "dodwr",
        this.hw_led_verde, this.close};
    private String[] bash_cmd_aria = {// Comanda l'aria
        this.Megaind_Program,
        "0",
        "dodwr",
        "4", "0"};
    private String[] bash_cmd_pressione_aria = {// Pressione l'aria
        this.Megaind_Program,
        "0",
        "uinrd",
        "2"};

    public JFileWorker(JRivitMain mf, JDoWorker aThis) throws IOException {
        this.Rm = mf;
        this.jDo_w = aThis;
        // create gpio controller by file (run bash script before !)     
        try {
            watcher = FileSystems.getDefault().newWatchService();
        } catch (IOException ex) {
            Logger.getLogger(JFileWorker.class.getName()).log(Level.SEVERE, null, ex);
        }
        Path dir = Paths.get(Static.PATH_WATCH);
        dir.register(watcher, StandardWatchEventKinds.ENTRY_MODIFY,
                StandardWatchEventKinds.ENTRY_CREATE,
                StandardWatchEventKinds.ENTRY_DELETE);
        try {
            Thread.sleep(2000);//Attesa 2" per allocazione Classi
        } catch (InterruptedException ex) {
            Logger.getLogger(JFileWorker.class.getName()).log(Level.SEVERE, null, ex);
        }

        System.out.println("Watch Service Modify file registered for dir: " + dir.getFileName());
    }

    private void send_p(String sp) {
        this.Rm.pulsante_hw(sp);
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
                //System.out.println(kind.name() + ": " + fileName);
                if (kind == StandardWatchEventKinds.ENTRY_CREATE) {
                    switch (fileName.toString()) {
                        case Static.F_ARIA ->
                            mostra_stato_aria(1);
                        case Static.F_ERRORE ->
                            errore(true);
                        case Static.F_CHIEDI_CONFERMA_NO ->
                            imposta_chiedi_conferma(true);
                        case Static.F_CHIEDI_CONFERMA_STOP ->
                            imposta_chiedi_conferma_stop(true);
                    }

                }
                if (kind == StandardWatchEventKinds.ENTRY_DELETE) {
                    switch (fileName.toString()) {
                        case Static.F_ARIA ->
                            mostra_stato_aria(0);
                        case Static.F_ERRORE ->
                            errore(false);
                        case Static.F_CHIEDI_CONFERMA_NO ->
                            imposta_chiedi_conferma(false);
                        case Static.F_CHIEDI_CONFERMA_STOP ->
                            imposta_chiedi_conferma_stop(false);
                    }
                }
                if (kind == StandardWatchEventKinds.ENTRY_MODIFY) {
                    switch (fileName.toString()) {
                        case "tiri" ->
                            tiri();
                        case "tiri_ok" ->
                            tiri_ok();
                        case "tiri_errati" ->
                            tiri_errati();
                        case "tiri_annullati" ->
                            tiri_annullati();
                        case "lotti_ok" ->
                            lotti_ok();
                        case "sensori", "info.txt" ->
                            read_info();
                        case "warning.txt" ->
                            read_warning();
                        case "setup_lan.txt" ->
                            read_setup_lan();
                        case "setup_wifi.txt" ->
                            read_setup_wifi();
                        case Static.F_LAVORI ->
                            read_lavori();
                        case "lavori_descrizione.txt", "in_pausa" ->
                            read_lavoro_in_pausa();
                        case "killScreen" ->
                            this.Rm.Exit();
                        case "abort" ->
                            abort();
                        case "pausa" ->
                            pausa();

                    }
                    try {
                        Thread.sleep(200);
                        AggiornaSensori();
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
     * Metodo per
     */
    private void tiri() {
        String Tiri = LeggiFileLock(Static.F_TIRI);
        try {
            this.Rm.set_nr_tiri(Integer.parseInt(Tiri));
        } catch (NumberFormatException e) {
            System.out.println("File tiri_ok non numerico\n" + e.getMessage());
        }
    }

    private void tiri_ok() {
        try {
            this.Rm.set_nr_tiri_ok(Integer.parseInt(LeggiFileLock(Static.F_TIRI_OK)));
        } catch (NumberFormatException e) {
            System.out.println("File tiri_ok non numerico\n" + e.getMessage());
        }
    }

    private void mostra_stato_aria(int stato) {

        if (stato == Static.ARIA_CHIUSA) {//Aria chiusa
            bash_cmd_aria[4] = this.close;
            bash_cmd_verde[4] = this.close;
            bash_cmd_rosso[4] = this.open;
            run_system_bash(this.bash_cmd_rosso);
            run_system_bash(this.bash_cmd_verde);
            run_system_bash(this.bash_cmd_aria);
            this.Rm.aria_chiusa();

        } else { //Aria aperta
            bash_cmd_aria[4] = this.open;
            bash_cmd_verde[4] = this.open;
            bash_cmd_rosso[4] = this.close;
            run_system_bash(bash_cmd_rosso);
            run_system_bash(bash_cmd_verde);
            run_system_bash(bash_cmd_aria);
            this.Rm.aria_aperta();

        }
    }

    /**
     * Errore_tiro legge nr tiri errati e li passa al RivitMain
     */
    private void tiri_errati() {
        String Tiri = LeggiFileLock(Static.F_TIRI_ERRATI);
        try {
            this.Rm.set_nr_tiri(Integer.parseInt(Tiri));
        } catch (NumberFormatException e) {
            System.out.println("File tiri_errati non numerico\n" + e.getMessage());
        }
    }

    /**
     * Legge il file con la descrizione dei lavori
     */
    private void read_lavori() {
        this.Rm.AggiornaLavori(LeggiFileElencoLock(Static.F_LAVORI));
    }

    /**
     * Legge il file con la descrizione delle info di sistema
     */
    private void read_info() {
        List<String> LeggiFileElencoInfo = this.LeggiFileElencoLock(Static.F_INFO);
        if (LeggiFileElencoInfo.isEmpty()) {
            LeggiFileElencoInfo.add("Manca file Info");
        }
        this.Rm.setListInfo(LeggiFileElencoInfo);
    }

    /**
     * Legge il file con la descrizione dei JFileWorker
     */
    private void read_warning() {
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
    private void read_setup_lan() {
        this.Rm.AggiornaSetupLan(this.LeggiFileElencoLock(this.f_setup_lan));
    }

    /**
     * Legge il file con la descrizione della configurazione della WiFi Come
     * sopra forse è meglio leggere il DB
     */
    private void read_setup_wifi() {
        this.Rm.AggiornaSetupWiFi(this.LeggiFileElencoLock(this.f_setup_wifi));
    }

    /**
     * Aggiona il contatore titi annullati
     */
    private void tiri_annullati() {
        String Tiri = LeggiFileLock(Static.F_TIRI_ANNULLATI);
        try {
            this.Rm.set_nr_tiri_annullati(Integer.parseInt(Tiri));
        } catch (NumberFormatException e) {
            System.out.println("File tiri_ok non numerico\n" + e.getMessage());
        }
    }

    /**
     * Metodo che imposta l'accensione o meni del led Rosso Viene impostata la
     * variabile booleana <in_errore> di JRivitMain
     *
     * @param si_o_no
     */
    private void errore(boolean si_o_no) {
        if (si_o_no == true) {
            bash_cmd_aria[4] = this.close;
            bash_cmd_verde[4] = this.close;
            bash_cmd_rosso[4] = this.open;
            run_system_bash(this.bash_cmd_rosso);
            run_system_bash(this.bash_cmd_verde);
            run_system_bash(this.bash_cmd_aria);
//            mostra_curva();
            this.Rm.setin_errore(true);
            this.Rm.set_errore_tiro();
        } else {
            this.Rm.setin_errore(false);
        }
        this.Rm.setin_errore(si_o_no);
    }

    /**
     * PressioneAria viene letta ogni secondo
     */
    private void AggiornaSensori() {
        String line = this.LeggiFileLock(Static.F_SENSORI);
        this.Rm.update_sensori(line);
    }

    /**
     *
     * @param cmd String [] comando shell da avviare
     * @return
     */
    public Process run_system_bash(String[] cmd) {
        Process exec = null;
        try {
            exec = getRuntime().exec(cmd);
            //printResults(exec);
            //return exec.exitValue();
        } catch (IOException ex) {
            Logger.getLogger(JFileWorker.class.getName()).log(Level.SEVERE, null, ex);
        }
        return exec;
    }

    /**
     * printResult - utilizzato per visualizzare l'out put del metodo
     * run_system_bash
     *
     * @param process
     * @throws IOException
     */
    private static void printResults(Process process) throws IOException {
        BufferedReader reader = new BufferedReader(new InputStreamReader(process.getInputStream()));
        String line;
        while ((line = reader.readLine()) != null) {
            System.out.println(line);
        }
    }

    /**
     * inizializza i valori in base al contenuto dei file Sono necessari: -
     * lavori.txt; letto sul DB tabella "lavori", 4 campi: nome, lotti, pezzi,
     * descrizione
     *
     */
    public void initValues() {
        this.AggiornaSensori();// Occorre che vi sia il batch avviato
        this.legge_tutti_i_file_tiri();

        this.read_info();// Se non esite il file imposta a stringa info
        this.read_warning();// Se non esiste il file imposta a sringa warning
        this.read_setup_lan();// Se non esiste il file imposta a DHCP
        this.read_setup_wifi();// Se non esiste il file imposta a DHCP
        this.read_lavori();//Se non essite il file imposta il default
        this.read_lavoro_scelto();//Se non essite il file imposta a 0
        this.read_lavoro_in_pausa();//Se non essite il file imposta non in pausa
        //this.LeggiAriaInMinMax(); // Letto dal DB
        this.LeggiSessione();// Se non essite il file imposta il file a "0"
        this.mostra_stato_aria(Static.ARIA_CHIUSA);//Se non esiste il file imposta a "0"
        this.read_nome_device();//Se non esiste il file imposta a CT-0000-00
//        CancellaFile(Static.PATH_WATCH + "errore"); // Dovrebbe farlo COntrol
        this.Rm.set_jLabel_B_L("Main");
        this.Rm.repaint();
    }

    /**
     * Metodo che utilizza il controllo del Lock per leggere una riga dal file
     *
     * @param NomeFile
     * @return La riga letta del file
     */
    public String LeggiFileLock(String NomeFile) {
        String stringaLetta = "";
        FileLock lock;
        int bufferSize = 1024;
        ByteArrayOutputStream out;
        File inputFile = new File(Static.PATH_WATCH + NomeFile);
        if (!inputFile.exists()) {
            System.out.println("Il File " + inputFile.getAbsolutePath()
                    + " non esiste\n");
            return "";
        }
        try {
            var channel = FileChannel.open(Paths.get(Static.PATH_WATCH + NomeFile),
                    StandardOpenOption.READ);
            do {
                lock = channel.tryLock(0, Long.MAX_VALUE, true);
            } while (lock != null);
            if (bufferSize > channel.size()) {
                bufferSize = (int) channel.size();
            }
            ByteBuffer buff = ByteBuffer.allocate(bufferSize);
            int noOfBytesRead = channel.read(buff);
            if (noOfBytesRead > 0) {
                stringaLetta = new String(buff.array(), StandardCharsets.UTF_8);
            } else {
                stringaLetta = "";
            }
            lock.release();
            channel.close();
        } catch (IOException ex) {
            Logger.getLogger(JFileWorker.class.getName()).log(Level.SEVERE, null, ex);
        }
        return stringaLetta;
    }

    /**
     * Metodo che utilizza il controllo del Lock per leggere una riga dal file
     *
     * @param NomeFile
     * @return La riga letta del file
     */
    public List<String> LeggiFileElencoLock(String NomeFile) {
        List<String> ListaRighe = null;
        String[] righeLette;
        int quanto_attendere = 0;
        FileLock lock = null;
        int bufferSize = 1024;
        ByteArrayOutputStream out;
        String stringaLetta = "";
        fc = null;
        File inputFile = new File(Static.PATH_WATCH + NomeFile);
        if (!inputFile.exists()) {
            System.out.println("Il File " + inputFile.getAbsolutePath()
                    + " non esiste\n");
            return ListaRighe;
        }
        try {
            fc = FileChannel.open(Paths.get(Static.PATH_WATCH + NomeFile),
                    StandardOpenOption.READ);
            do {
                lock = fc.tryLock(0, Long.MAX_VALUE, true);
            } while (lock != null);
//            if (bufferSize > channel.size()) {
//                bufferSize = (int) channel.size();
//            }
//            ByteBuffer buff = ByteBuffer.allocate(bufferSize);
//            int noOfBytesRead = channel.read(buff);
//            if (noOfBytesRead > 0) {
//                stringaLetta = new String(buff.array(), StandardCharsets.UTF_8);
//            } else {
//                stringaLetta = "";
//            }

        } catch (IOException ex) {
            Logger.getLogger(JFileWorker.class.getName()).log(Level.SEVERE, null, ex);
        }
        try {
//            righeLette = stringaLetta.split("\n");
            ListaRighe = Files.readAllLines(Paths.get(Static.PATH_WATCH + NomeFile), StandardCharsets.UTF_8);
        } catch (IOException ex) {
            Logger.getLogger(JFileWorker.class.getName()).log(Level.SEVERE, null, ex);
        } finally {
            System.out.println(Thread.currentThread().getName() + ": " + "Letto File elenco");
            try {
                lock.release();
                fc.close();
            } catch (IOException ex) {
                Logger.getLogger(JFileWorker.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
        return ListaRighe;
    }

    /**
     * LeggiFileLavoroInPausa /tmp/CT/inpausa
     */
    private void read_lavoro_in_pausa() {
        this.Rm.setInPausa(LeggiFileLock(Static.F_IN_PAUSA));
    }

    /**
     * LeggiSessione
     */
    private void LeggiSessione() {
        this.Rm.AggiornaSessione(LeggiFileLock(Static.F_SESSIONE));
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
     * Metodo per la scrittura di file che possono essere scritti anche da altri
     * in concorrenza
     *
     * @param NomeFile
     * @param CosaScrivere
     * @return
     */
    public int ScriviFileLock(String NomeFile, String CosaScrivere) {
        FileLock lock = null;
        ByteBuffer buffer;
        try {
            fc = FileChannel.open(Paths.get(Static.PATH_WATCH + NomeFile),
                    StandardOpenOption.WRITE, StandardOpenOption.CREATE);
            do {
                lock = fc.tryLock(0, Long.MAX_VALUE, true);
            } while (lock != null);
            buffer = ByteBuffer.wrap(CosaScrivere.getBytes());
            buffer.put(CosaScrivere.toString().getBytes());
            buffer.flip();
            while (buffer.hasRemaining()) {
                fc.write(buffer);
            }
        } catch (IOException e) {
            Logger.getLogger(JFileWorker.class.getName()).log(Level.SEVERE, null, e);
            return -1;

        } finally {
            try {
                lock.close();
                fc.close();
            } catch (IOException ex) {
                Logger.getLogger(JFileWorker.class.getName()).log(Level.SEVERE, null, ex);
                return -1;
            }
        }
        return 0;
    }

    private void LeggiAriaInMinMax() {
        try {
            float min, max;
            min = Float.parseFloat(LeggiFileLock(f_soglia_pressione_aria_in_min));
            max = Float.parseFloat(LeggiFileLock(f_soglia_pressione_aria_in_max));
            this.Rm.update_soglie_pressione_aria_in(min, max);
        } catch (NumberFormatException e) {
            System.out.println("Contenuto dei file pressione_in non numerico !\n" + e.getMessage());
        }
    }

    private void mostra_curva() {
        this.Rm.setCurva(LeggiFileLock(Static.F_CURVA));
    }

    private void read_lavoro_scelto() {
        this.Rm.setLavoroScelto(LeggiFileLock(Static.F_LAVORO_SCELTO));
    }

    /**
     * legge dal file nome_device il nome del ControlRiv SN registrato nel
     * record CT -> sn
     */
    private void read_nome_device() {
        this.Rm.setNomeDevice(LeggiFileLock(Static.F_NOME_DEVICE));
    }

    private void lotti_ok() {
        try {
            this.Rm.set_nr_lotti_ok(Integer.parseInt(LeggiFileLock(Static.F_LOTTI_OK)));
            this.Rm.update_tiri_lotti();    // aggiorna la visualizzazione
        } catch (NumberFormatException e) {
            System.out.println("Contenuto del file lotti_ok non numerico !\n" + e.getMessage());
        }
    }

    /**
     * Cancellazione del lavoro svolto azzerare i contatori azzera lavoro-scelto
     * impostando prima il file ultimo_lavoro_scelto con il nome del alvoro,
     * consentendo così al Pannello Start con la lista dei lavori di
     * posizionarsi sull'ultimo lavoro scelto accendere led giallo
     *
     */
    private void abort() {
        ScriviFileLock(Static.F_IN_STOP, "" + Static.STATO_STOP);  //  stato di abort
    }

    /**
     * I contatori rimangono tali posizionarsi sull'ultimo lavoro scelto
     * accendere led giallo
     *
     */
    private void pausa() {
        this.ScriviFileLock(Static.F_IN_PAUSA, "" + Static.STATO_PAUSA);
    }

    private void imposta_chiedi_conferma(boolean si_o_no) {

        this.Rm.setChiedi_conferma(si_o_no);
    }

    /**
     * cancela un file
     *
     * @param NomeFile
     */
    public static void CancellaFile(String NomeFile) {
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
                System.out.println("errore eliminando il file " + NomeFile);
            }
        }
    }

    private void imposta_chiedi_conferma_stop(boolean si_o_no) {
        this.Rm.setChiedi_conferma_stop(si_o_no);
    }

    /**
     * Metodo unico per leggere tutti i file correlati ai tiri altrimenti ci
     * possono essere degli errori di file non esistenti che possono provurare
     * dei calcoli errati
     */
    private void legge_tutti_i_file_tiri() {
        this.tiri();// se non esiste il file (creato da Control, imposta a 0
        this.tiri_errati();//se non esiste il file (creato da Control, imposta a 0
        this.tiri_ok();//se non esiste il file (creato da Control, imposta a 0
        this.tiri_annullati();//se non esiste il file (creato da Control, imposta a 0
        this.Rm.update_tiri_lotti();
    }
}
