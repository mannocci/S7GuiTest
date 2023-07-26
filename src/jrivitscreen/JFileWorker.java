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
import java.util.logging.Level;
import java.util.logging.Logger;
//import static java.nio.file.StandardWatchEventKinds.ENTRY_MODIFY;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.RandomAccessFile;
import static java.lang.Runtime.getRuntime;
import java.nio.ByteBuffer;
import java.nio.channels.FileChannel;
import java.nio.channels.FileLock;
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
                            mostraStatoAria(Static.ARIA_APERTA);
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
                        case "risposta_tiro_errato_continua" -> {
                            this.Rm.setin_errore(false);
                            this.Rm.aggiornaDaErroreTiro();
                        }
                    }

                }
                if (kind == StandardWatchEventKinds.ENTRY_DELETE) {
                    switch (fileName.toString()) {
                        case Static.F_ARIA ->
                            mostraStatoAria(Static.ARIA_CHIUSA);
                        case Static.F_ERRORE ->
                            errore(false);
                        case Static.F_CHIEDI_CONFERMA_NO ->
                            impostaChiediConferma(false);
                        case Static.F_CHIEDI_CONFERMA_STOP ->
                            impostaChiediConfermaStop(false);

                    }
                }
                if (kind == StandardWatchEventKinds.ENTRY_MODIFY) {
                    switch (fileName.toString()) {
                        case Static.F_TIRI_NEL_LOTTO ->
                            tiri();
                        case Static.F_TIRI_OK ->
                            tiriOk();
                        case Static.F_TIRI_ERRATI ->
                            tiriErrati();
                        case Static.F_TIRI_ANNULLATI ->
                            tiriAnnullati();
                        case Static.F_LOTTI_OK ->
                            lottiOk();
                        case Static.F_SENSORI, Static.F_INFO ->
                            readInfo();
                        case Static.F_WARNING ->
                            readWarning();
                        case Static.F_SETUP_LAN ->
                            readSetupLan();
                        case Static.F_SETUP_WIFI ->
                            readSetupWifi();
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
     * Metodo per
     */
    private void tiri() {
        String Tiri = LeggiFileLock(Static.F_TIRI_NEL_LOTTO).replace("\n", "");
        try {
            this.Rm.set_nr_tiri(Integer.parseInt(Tiri));
            this.Rm.update_tiri_lotti();
        } catch (NumberFormatException e) {
            System.out.println("File tiri_ok non numerico\n" + e.getMessage());
        }
    }

    private void tiriOk() {
        String Tiri = LeggiFileLock(Static.F_TIRI_OK).replace("\n", "");
        try {
            this.Rm.set_nr_tiri_ok(Integer.parseInt(Tiri));
            this.Rm.setjLabelValidi("" + Tiri);
            this.Rm.update_tiri_lotti();
        } catch (NumberFormatException e) {
            System.out.println("File tiri_ok non numerico\n" + e.getMessage());
        }
    }

    private void mostraStatoAria(String stato) {

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
    private void tiriErrati() {
        String Tiri = LeggiFileLock(Static.F_TIRI_ERRATI).replace("\n", "");
        try {
            this.Rm.set_nr_tiri(Integer.parseInt(Tiri));
            this.Rm.setjLabelErrati("" + Tiri);
            this.Rm.update_tiri_lotti();
        } catch (NumberFormatException e) {
            System.out.println("File tiri_errati non numerico\n" + e.getMessage());
        }
    }

    /**
     * Legge il file con la descrizione dei lavori
     */
    private void readLavori() {
        this.Rm.AggiornaLavori(LeggiFileElencoLock(Static.F_LAVORI));
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
        this.Rm.AggiornaSetupLan(this.LeggiFileElencoLock(this.f_setup_lan));
    }

    /**
     * Legge il file con la descrizione della configurazione della WiFi Come
     * sopra forse è meglio leggere il DB
     */
    private void readSetupWifi() {
        this.Rm.AggiornaSetupWiFi(this.LeggiFileElencoLock(this.f_setup_wifi));
    }

    /**
     * Aggiona il contatore titi annullati
     */
    private void tiriAnnullati() {
        String Tiri = LeggiFileLock(Static.F_TIRI_ANNULLATI).replace("\n", "");
        try {
            this.Rm.set_nr_tiri_annullati(Integer.parseInt(Tiri));
            this.Rm.setjLabelAnnullati("" + Tiri);
            this.Rm.update_tiri_lotti();
        } catch (NumberFormatException e) {
            System.out.println("File tiri_ok non numerico\n" + e.getMessage());
        }
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
            this.Rm.setin_errore(false);
        }
        this.Rm.setin_errore(si_o_no);
    }

    /**
     * PressioneAria viene letta ogni secondo
     */
    private void aggiornaSensori() {
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
        this.aggiornaSensori();// Occorre che vi sia il batch avviato
        this.legge_tutti_i_file_tiri();
        this.readLavori();//Se non essite il file imposta il default
        this.readInfo();// Se non esite il file imposta a stringa info
        this.readWarning();// Se non esiste il file imposta a sringa warning
        this.readSetupLan();// Se non esiste il file imposta a DHCP
        this.readSetupWifi();// Se non esiste il file imposta a DHCP

        this.read_lavoro_scelto();//Se non esite il file imposta a 0
        this.readLavoroInPausa();//Se non esite il file imposta non in pausa
        //this.LeggiAriaInMinMax(); // Letto dal DB
        this.LeggiSessione();// Se non essite il file imposta il file a "0"
        this.mostraStatoAria(Static.ARIA_CHIUSA);//Se non esiste il file imposta a "0"
        this.readNomeDevice();//Se non esiste il file imposta a CT-0000-00
        CancellaFile(Static.PATH_WATCH + "errore"); // Dovrebbe farlo COntrol
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
            stringaLetta = "errore lettura File " + NomeFile;
            return stringaLetta;
        }
        try {
            var channel = FileChannel.open(Paths.get(Static.PATH_WATCH + NomeFile),
                    StandardOpenOption.READ);
//            do {
//                lock = channel.tryLock(0, Long.MAX_VALUE, true);
//            } while (lock != null);
            if (bufferSize > channel.size()) {
                bufferSize = (int) channel.size();
            }
            ByteBuffer buff = ByteBuffer.allocate(bufferSize);
            int noOfBytesRead = channel.read(buff);
            if (noOfBytesRead > 0) {
                stringaLetta = new String(buff.array(), StandardCharsets.UTF_8);
            } else {
                stringaLetta = "errore lettura File " + NomeFile;
            }
//            lock.release();
            channel.close();
        } catch (IOException ex) {
            Logger.getLogger(JFileWorker.class.getName()).log(Level.SEVERE, null, ex);
            stringaLetta = "errore lettura File " + NomeFile;
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
        List<String> ListaRighe = new ArrayList<>();
        String[] righeLette;
        int quanto_attendere = 0;
        FileLock lock = null;
        int bufferSize = 1024;
        ByteArrayOutputStream out;
        String stringaLetta = "";
        fc = null;
        try {
            File inputFile = new File(Static.PATH_WATCH + NomeFile);
            if (!inputFile.exists()) {
                System.out.println("Il File " + inputFile.getAbsolutePath()
                        + " non esiste\n");
                ListaRighe.add("errore lettura File " + NomeFile);
                return ListaRighe;
            }

            fc = FileChannel.open(Paths.get(Static.PATH_WATCH + NomeFile),
                    StandardOpenOption.READ);
//            do {
//                lock = fc.tryLock(0, Long.MAX_VALUE, true);
//            } while (lock != null);
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
            ListaRighe.add("errore lettura File " + NomeFile);
            Logger.getLogger(JFileWorker.class.getName()).log(Level.SEVERE, null, ex);
        }
        try {
//            righeLette = stringaLetta.split("\n");
            ListaRighe = Files.readAllLines(Paths.get(Static.PATH_WATCH + NomeFile), StandardCharsets.UTF_8);
        } catch (IOException ex) {
            Logger.getLogger(JFileWorker.class.getName()).log(Level.SEVERE, null, ex);
            ListaRighe.add("errore lettura File " + NomeFile);
        } finally {
            //System.out.println(Thread.currentThread().getName() + ": " + "Letto File elenco");
            try {
//                lock.release();
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
    private void readLavoroInPausa() {
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
//        FileLock lock = null;
        ByteBuffer buffer;
        try {
            fc = FileChannel.open(Paths.get(Static.PATH_WATCH + NomeFile),
                    StandardOpenOption.WRITE, StandardOpenOption.CREATE);
//            do {
//                lock = fc.tryLock(0, Long.MAX_VALUE, true);
//            } while (lock != null);
            buffer = ByteBuffer.wrap(CosaScrivere.getBytes());
            buffer.put(CosaScrivere.getBytes());
            buffer.flip();
            while (buffer.hasRemaining()) {
                fc.write(buffer);
            }
            System.out.println("Scritto file "+NomeFile+"\ncontenente \""+CosaScrivere+"\"");
        } catch (IOException e) {
            Logger.getLogger(JFileWorker.class.getName()).log(Level.SEVERE, null, e);
            return -1;

        } finally {
            try {
//                lock.close();
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

    private void impostaChiediConfermaStop(boolean si_o_no) {
        this.Rm.setChiedi_conferma_stop(si_o_no);
    }

    /**
     * Metodo unico per leggere tutti i file correlati ai tiri altrimenti ci
     * possono essere degli errori di file non esistenti che possono provurare
     * dei calcoli errati
     */
    private void legge_tutti_i_file_tiri() {
        this.tiri();// se non esiste il file (creato da Control, imposta a 0
        this.lottiOk();//Se non esiste imposta 1
        this.tiriErrati();//se non esiste il file (creato da Control, imposta a 0
        this.tiriOk();//se non esiste il file (creato da Control, imposta a 0

        this.tiriAnnullati();//se non esiste il file (creato da Control, imposta a 0
        this.Rm.update_tiri_lotti();
    }
}
