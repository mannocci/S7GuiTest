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
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.util.logging.Level;
import java.util.logging.Logger;
import static java.nio.file.StandardWatchEventKinds.ENTRY_MODIFY;

import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.io.RandomAccessFile;
import static java.lang.Runtime.getRuntime;
import java.nio.channels.FileChannel;
import java.nio.channels.FileLock;
import java.nio.channels.OverlappingFileLockException;
import java.nio.file.FileSystems;
import java.nio.file.Path;
import java.nio.file.Paths;
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
    private final Static S;

    private WatchService watcher;
    private Path fileName;
    private final String f_tiri = "tiri";
    private final String f_tiri_ok = "tiri_ok";
    private final String f_lotti_ok = "lotti_ok";
    private final String f_info = "info.txt";
    private final String f_warning = "warning.txt";
    private final String f_setup_lan = "setup_lan.txt";
    private final String f_setup_wifi = "setup_wifi.txt";
    private final String f_lavori = "lavori.txt";
    private final String f_in_pausa = "in_pausa";
    // se il lavoro è in corso contiene "1"
    private final String f_curva = "curva";
    private final String f_sessione = "sessione";
    private final String f_sensori = "sensori";
    private final String f_soglia_pressione_aria_in_min = "soglia_pressione_aria_in_min";
    private final String f_soglia_pressione_aria_in_max = "soglia_pressione_aria_in_max";
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
        this.S = new Static();
        // create gpio controller by file (run bash script before !)     
        try {
            watcher = FileSystems.getDefault().newWatchService();
        } catch (IOException ex) {
            Logger.getLogger(JFileWorker.class.getName()).log(Level.SEVERE, null, ex);
        }
        Path dir = Paths.get(S.PATH_WATCH);
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
                        case "lavori.txt" ->
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
        this.jDo_w.set_operation(Static.F_TIRI);
        try {
            this.jDo_w.doInBackground();
        } catch (Exception ex) {
            Logger.getLogger(JFileWorker.class.getName()).log(Level.SEVERE, null, ex);
        }
        // Viene fatto nella classe JDoWorker
//        try {
//            this.Rm.set_nr_tiri(Integer.parseInt(this.LeggiFile(Static.F_TIRI)));
//            this.Rm.update_tiri_lotti();
//        } catch (NumberFormatException e) {
//            System.out.println("File tiri_ok non numerico\n" + e.getMessage());
//        }
    }

    private void tiri_ok() {
        try {
            this.Rm.set_nr_tiri_ok(Integer.parseInt(LeggiFile(f_tiri_ok)));
            this.Rm.update_tiri_lotti();
        } catch (NumberFormatException e) {
            System.out.println("File tiri_ok non numerico\n" + e.getMessage());
        }
    }

    private void mostra_stato_aria(int stato) {

        if (stato == 0) {//Aria chiusa
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
        this.Rm.AggiornaTiriErrati();
    }

    /**
     * Legge il file con la descrizione dei lavori
     */
    private void read_lavori() {
        this.Rm.AggiornaLavori(LeggiFileElenco(this.f_lavori));
    }

    /**
     * Legge il file con la descrizione delle info di sistema
     */
    private void read_info() {
        List<String> LeggiFileElencoInfo = this.LeggiFileElenco(this.f_info);
        this.Rm.setListInfo(LeggiFileElencoInfo);
    }

    /**
     * Legge il file con la descrizione dei JFileWorker
     */
    private void read_warning() {
        List<String> LeggiFileElencoWarning = this.LeggiFileElenco(this.f_warning);
        this.Rm.setListWarning(LeggiFileElencoWarning);

    }

    /**
     * LeggiFileElenco, legge il file e aggiorna una List awt
     *
     * @param NomeFile - Nome file da leggere come elenco
     * @return List<String>
     */
    public List<String> LeggiFileElenco(String NomeFile) {
        List<String> ListaRighe = new ArrayList<>();
        try {

            File myObj = new File(Static.PATH_WATCH + NomeFile);
            if (myObj.exists()) {
                try (Scanner myReader = new Scanner(myObj)) {
                    while (myReader.hasNextLine()) {
                        ListaRighe.add(myReader.nextLine());
                    }
                }
            }
        } catch (FileNotFoundException e) {
            System.out.println("File non trovato " + NomeFile);
            return ListaRighe;
        }
        return ListaRighe;
    }
    //End LeggiFileList

    /**
     * Legge il file con la descrizione della configurazione della LAN
     */
    private void read_setup_lan() {
        this.Rm.AggiornaSetupLan(this.LeggiFileElenco(this.f_setup_lan));
    }

    /**
     * Legge il file con la descrizione della configurazione della WiFi
     */
    private void read_setup_wifi() {
        this.Rm.AggiornaSetupWiFi(this.LeggiFileElenco(this.f_setup_wifi));
    }

    /**
     * Aggiona il contatore titi annullati
     */
    private void tiri_annullati() {
        this.jDo_w.set_operation("tiri_annullati");
        try {
            this.jDo_w.doInBackground();
        } catch (Exception ex) {
            Logger.getLogger(JFileWorker.class.getName()).log(Level.SEVERE, null, ex);
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

        String line = this.LeggiFile(this.f_sensori);
        if (!line.equals("")) {
            this.Rm.update_sensori(line);
        }
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
     * inizializza i valori in base al contenuto dei file
     */
    public void initValues() {
        this.AggiornaSensori();
        this.tiri();
        this.tiri_errati();
        this.tiri();
        this.tiri_annullati();
        //this.errore();
        this.read_info();
        this.read_warning();
        this.read_setup_lan();
        this.read_setup_wifi();
        this.read_lavori();
        this.read_lavoro_scelto();
        this.read_lavoro_in_pausa();
        //this.LeggiAriaInMinMax();
        this.LeggiSessione();
        this.mostra_stato_aria(0);
        this.read_nome_device();
        CancellaFile(Static.PATH_WATCH + "errore");
        this.Rm.set_jLabel_B_L("Main");

        this.Rm.repaint();
    }

    /**
     * LeggiFile Metodo utilizzato da più metodi per lettura del file
     *
     * @param NomeFile - Nome del file da leggere
     * @return String - riga letta
     */
    private String LeggiFile(String NomeFile) {
        String contenutoFile = "";
        try {
            File myObj = new File(Static.PATH_WATCH + NomeFile);
            if (myObj.exists()) {
                try (Scanner myReader = new Scanner(myObj)) {
                    while (myReader.hasNextLine()) {
                        contenutoFile += myReader.nextLine();
                    }
                }
            }
        } catch (FileNotFoundException e) {
            System.out.println("File non trovato " + NomeFile);
            return "Errore lettura file";
        }
        return contenutoFile;
    }//End LeggiFile

    /**
     * Metodo che utilizza il controllo del Lock per leggere una riga dal file
     *
     * @param NomeFile
     * @return La riga letta del file
     */
    public String LeggiFileLock(String NomeFile) {
        String stringaLetta = "";
        RandomAccessFile file = null;
        FileChannel channel = null;
        FileLock lock = null;

        try {
            file = new RandomAccessFile(Static.PATH_WATCH +NomeFile, "r");
            channel = file.getChannel();

            try {
                lock = channel.tryLock();
            } catch (final OverlappingFileLockException e) {
                file.close();
                channel.close();
                return "-2";
            }
            stringaLetta = file.readLine();
            TimeUnit.HOURS.sleep(1);
            lock.release();
            file.close();
            channel.close();
        } catch (FileNotFoundException ex) {
            Logger.getLogger(JFileWorker.class.getName()).log(Level.SEVERE, null, ex);
            return "-1";
        } catch (IOException ee) {
            Logger.getLogger(JFileWorker.class.getName()).log(Level.SEVERE, null, ee);
            return "-1";
        } catch (InterruptedException eee) {
            Logger.getLogger(JFileWorker.class.getName()).log(Level.SEVERE, null, eee);
            return "-1";
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
        String stringaLetta;
        RandomAccessFile file;
        FileChannel channel;
        FileLock lock;
        List<String> ListaRighe = new ArrayList<>();

        try {
            file = new RandomAccessFile(Static.PATH_WATCH +NomeFile, "r");
            channel = file.getChannel();

            try {
                lock = channel.tryLock();
            } catch (final OverlappingFileLockException e) {
                file.close();
                channel.close();
                return ListaRighe;
            }
            while ((stringaLetta = file.readLine()) != null) {
                ListaRighe.add(stringaLetta);
            }
            TimeUnit.HOURS.sleep(1);
            lock.release();
            file.close();
            channel.close();
        } catch (FileNotFoundException ex) {
            Logger.getLogger(JFileWorker.class.getName()).log(Level.SEVERE, null, ex);
            return ListaRighe;
        } catch (IOException ee) {
            Logger.getLogger(JFileWorker.class.getName()).log(Level.SEVERE, null, ee);
            return ListaRighe;
        } catch (InterruptedException eee) {
            Logger.getLogger(JFileWorker.class.getName()).log(Level.SEVERE, null, eee);
            return ListaRighe;
        }
        return ListaRighe;
    }

    /**
     * LeggiFileLavoroInPausa /tmp/CT/inpausa
     */
    private void read_lavoro_in_pausa() {
        this.Rm.setInPausa(LeggiFile(this.f_in_pausa));
    }

    /**
     * LeggiSessione
     */
    private void LeggiSessione() {
        this.Rm.AggiornaSessione(LeggiFile(this.f_sessione));
    }//End LeggiFileLavoriDescrizione

    /**
     * ScriviFile metodo generico per scrivere una riga in un file
     *
     * @param NomeFile
     * @param CosaScrivere String testo da scrivere nel file
     */
    public void ScriviFile(String NomeFile, String CosaScrivere) {
        try {
            FileWriter fw = new FileWriter(Static.PATH_WATCH + NomeFile);
            PrintWriter pw = new PrintWriter(fw);
            pw.print(CosaScrivere);
            pw.flush();
            pw.close();
        } catch (IOException ex) {
            Logger.getLogger(JFileWorker.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    /**
     * Metodo per la scrittura di file che possono essere scritti anche da altri
     * in concorrenza
     *
     * @param NomeFile
     * @param CosaScrivere
     * @return
     */
    public int ScriviFileLock(String NomeFile, String CosaScrivere) {
        RandomAccessFile file = null;
        FileChannel channel = null;
        FileLock lock = null;

        try {
            file = new RandomAccessFile(NomeFile, "rw");
            channel = file.getChannel();

            try {
                lock = channel.tryLock();
            } catch (final OverlappingFileLockException e) {
                file.close();
                channel.close();
                return -2;
            }

            file.writeChars(CosaScrivere);
            TimeUnit.HOURS.sleep(1);
            lock.release();
            file.close();
            channel.close();
        } catch (FileNotFoundException ex) {
            Logger.getLogger(JFileWorker.class.getName()).log(Level.SEVERE, null, ex);
            return -1;
        } catch (IOException ee) {
            Logger.getLogger(JFileWorker.class.getName()).log(Level.SEVERE, null, ee);
            return -1;
        } catch (InterruptedException eee) {
            Logger.getLogger(JFileWorker.class.getName()).log(Level.SEVERE, null, eee);
            return -1;
        }
        return 0;
    }

    private void LeggiAriaInMinMax() {
        try {
            float min, max;
            min = Float.parseFloat(LeggiFile(f_soglia_pressione_aria_in_min));
            max = Float.parseFloat(LeggiFile(f_soglia_pressione_aria_in_max));
            this.Rm.update_soglie_pressione_aria_in(min, max);
        } catch (NumberFormatException e) {
            System.out.println("Contenuto dei file pressione_in non numerico !\n" + e.getMessage());
        }
    }

    private void mostra_curva() {
        this.Rm.setCurva(LeggiFile(this.f_curva));
    }

    private void read_lavoro_scelto() {
        this.Rm.setLavoroScelto(LeggiFile(S.F_LAVORO_SCELTO));
    }

    /**
     * legge dal file nome_device il nome del ControlRiv SN registrato nel
     * record CT -> sn
     */
    private void read_nome_device() {
        this.Rm.setNomeDevice(LeggiFile(S.F_NOME_DEVICE));
    }

    private void lotti_ok() {
        try {
            this.Rm.set_nr_lotti_ok(Integer.parseInt(LeggiFile(this.f_lotti_ok)));
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
        ScriviFileLock(S.F_IN_STOP, "" + S.STATO_STOP);  //  stato di abort
    }

    /**
     * I contatori rimangono tali posizionarsi sull'ultimo lavoro scelto
     * accendere led giallo
     *
     */
    private void pausa() {
        this.ScriviFileLock(S.F_IN_PAUSA, "" + S.STATO_PAUSA);
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
        while (!f.canWrite()) {
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
}
