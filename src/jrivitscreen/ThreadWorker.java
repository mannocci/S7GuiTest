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
import static java.lang.Runtime.getRuntime;
import java.nio.file.FileSystems;
import java.nio.file.Path;
import java.nio.file.Paths;
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
public class ThreadWorker extends Thread {

    private JRivitMain mf;
    private final String pathWatch = "/tmp/CT/";
    private WatchService watcher;
    private Path fileName;
    private final String f_tiri = "tiri";
    private final String f_tiri_ok = "tiri_ok";
    private final String f_tiri_errati = "tiri_errati";
    private final String f_tiri_annullati = "tiri_annullati";
    private final String f_info = "info.txt";
    private final String f_warning = "warning.txt";
    private final String f_setup_lan = "setup_lan.txt";
    private final String f_setup_wifi = "setup_wifi.txt";
    private final String f_lavori = "lavori.txt";
    private final String f_lavori_descrizione = "lavori_descrizione.txt";
    private final String f_aria = "aria";
    private final String f_errore = "errore";
    private final String f_risposta_tiro_errato = "risposta_tiro_errato";
    private final String f_sessione = "sessione";
    private final String f_soglia_pressione_aria_in_min = "soglia_pressione_aria_in_min";
    private final String f_soglia_pressione_aria_in_max = "soglia_pressione_aria_in_max";
    String open = "255";
    String close = "0";
    String hw_aria = "4";
    String hw_led_rosso = "1", hw_led_giallo = "2", hw_led_verde = "3";
    String Megaind_Program = "/home/adminsb/src/megaind-rpi/megaind";
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

    public ThreadWorker(JRivitMain mf) throws IOException {
        this.mf = mf;
        // create gpio controller by file (run bash script before !)     
        try {
            watcher = FileSystems.getDefault().newWatchService();
        } catch (IOException ex) {
            Logger.getLogger(ThreadWorker.class.getName()).log(Level.SEVERE, null, ex);
        }
        Path dir = Paths.get(this.pathWatch);
        dir.register(watcher, ENTRY_MODIFY);
        try {
            Thread.sleep(2000);//Attesa 2" per allocazione Classi
        } catch (InterruptedException ex) {
            Logger.getLogger(ThreadWorker.class.getName()).log(Level.SEVERE, null, ex);
        }

        System.out.println("Watch Service Modify file registered for dir: " + dir.getFileName());
    }

    private void send_p(String sp) {
        this.mf.pulsante_hw(sp);
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

                if (kind == ENTRY_MODIFY) {
                    switch (fileName.toString()) {
                        case "tiri_ok" ->
                            tiri_ok();
                        case "tiri_errati" ->
                            tiri_errati();
                        case "tiri" ->
                            tiri();
                        case "tiri_annullati" ->
                            tiri_annullati();
                        case "errore" ->
                            errore();
                        case "info.txt" ->
                            read_info();
                        case "warning.txt" ->
                            read_warning();
                        case "setup_lan.txt" ->
                            read_setup_lan();
                        case "setup_wifi.txt" ->
                            read_setup_wifi();
                        case "lavori.txt" ->
                            read_lavori();
                        case "aria" ->
                            mostra_stato_aria();
                        case "risposta_tiro_errato" ->
                            risposta_tiro_errato();
                    }
                    try {
                        Thread.sleep(200);
                        PressioneAria();
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

//    private void read_pulsane_premuto(String nomeFile) {
//        String data = "";
//        try {
//            File myObj = new File("/tmp/gpio/" + nomeFile);
//            Scanner myReader = new Scanner(myObj);
//            while (myReader.hasNextLine()) {
//                data = myReader.nextLine();
//                //System.out.println(data);
//            }
//            myReader.close();
//        } catch (FileNotFoundException e) {
//            System.out.println("An error occurred.");
//            e.printStackTrace();
//        }
//
//        if (!data.isEmpty() && data.contentEquals("0")) {
//            //System.out.printf("il pulsante %s è stato premuto\n", fileName);
//            this.send_p(nomeFile);
//        }
//    }
    private void tiri_ok() {
        this.mf.update_tiri_ok();
    }

    private void mostra_stato_aria() {

        String stato = LeggiFile(this.f_aria);
        if (stato.equals("0")) {//Aria chiusa
            bash_cmd_aria[4] = this.close;
            bash_cmd_verde[4] = this.close;
            bash_cmd_rosso[4] = this.open;
            run_system_bash(this.bash_cmd_rosso);
            run_system_bash(this.bash_cmd_verde);
            run_system_bash(this.bash_cmd_aria);
            this.mf.aria_chiusa();

        } else { //Aria aperta
            bash_cmd_aria[4] = this.open;
            bash_cmd_verde[4] = this.open;
            bash_cmd_rosso[4] = this.close;
            run_system_bash(bash_cmd_rosso);
            run_system_bash(bash_cmd_verde);
            run_system_bash(bash_cmd_aria);
            this.mf.aria_aperta();

        }
    }

    /**
     * Errore_tiro legge nr tiri errati e li passa al RivitMain
     */
    private void tiri_errati() {
        this.mf.AggiornaTiriErrati();
    }

    /**
     * Legge il file con la descrizione dei lavori
     */
    private void read_lavori() {
        this.mf.AggiornaLavori(LeggiFileElenco(this.f_lavori));
    }

    /**
     * Legge il file con la descrizione delle info di sistema
     */
    private void read_info() {
        this.mf.AggiornaInfo(LeggiFileElenco(this.f_info));
    }

    /**
     * Legge il file con la descrizione dei ThreadWorker
     */
    private void read_warning() {
        this.mf.AggiornaWarning(this.LeggiFileElenco(this.f_warning));
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
            File myObj = new File(this.pathWatch + NomeFile);
            Scanner myReader = new Scanner(myObj);
            while (myReader.hasNextLine()) {
                ListaRighe.add(myReader.nextLine());
            }
            myReader.close();
        } catch (FileNotFoundException e) {
            System.out.println("An error occurred.");
            return ListaRighe;
        }
        return ListaRighe;
    }
    //End LeggiFileList

    /**
     * Legge il file con la descrizione della configurazione della LAN
     */
    private void read_setup_lan() {
        this.mf.AggiornaSetupLan(this.LeggiFileElenco(this.f_setup_lan));
    }

    /**
     * Legge il file con la descrizione della configurazione della WiFi
     */
    private void read_setup_wifi() {
        this.mf.AggiornaSetupWiFi(this.LeggiFileElenco(this.f_setup_wifi));
    }

    private void tiri_annullati() {
        this.mf.AggiornaTiriAnnullati();
    }

    private void errore() {
        bash_cmd_aria[4] = this.close;
        bash_cmd_verde[4] = this.close;
        bash_cmd_rosso[4] = this.open;
        run_system_bash(this.bash_cmd_rosso);
        run_system_bash(this.bash_cmd_verde);
        run_system_bash(this.bash_cmd_aria);
        this.mf.errore();
    }

    private void tiri() {
        this.mf.AggiornaTiri();
    }

    /**
     * Metodo scopre che qualcuno ha risposto da remoto all'attesa della
     * risposta in caso di tiro errato
     */
    private void risposta_tiro_errato() {
        this.mf.risposta_attesa_tiro_errato();
    }

    /**
     * PressioneAria vie utilizzato ogni secondo
     */
    private void PressioneAria() {
        /* da portare in JControl
        Process process;
        BufferedReader reader;
        process = run_system_bash(this.bash_cmd_pressione_aria);
        if (process != null) {
            reader = new BufferedReader(new InputStreamReader(process.getInputStream()));
            String line;
            try {
                line = reader.readLine();
            } catch (IOException ex) {
                Logger.getLogger(ThreadWorker.class.getName()).log(Level.SEVERE, null, ex);
                line = " errore lettura";
            }
        }
         */
        String line = this.LeggiFile("pressione_aria_in");
        this.mf.update_pressione_aria(Float.valueOf(line));
    }

    /**
     *
     * @param cmd String [] comando shell da avviare
     */
    public Process run_system_bash(String[] cmd) {
        Process exec = null;
        try {
            exec = getRuntime().exec(cmd);
            //printResults(exec);
            //return exec.exitValue();
        } catch (IOException ex) {
            Logger.getLogger(ThreadWorker.class.getName()).log(Level.SEVERE, null, ex);
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

    public void initValues() {
        this.tiri_ok();
        this.tiri_errati();
        this.tiri();
        this.tiri_annullati();
        //this.errore();
        this.read_info();
        this.read_warning();
        this.read_setup_lan();
        this.read_setup_wifi();
        this.read_lavori();
        this.LeggiFileLavoriDescrizione();
        this.LeggiAriaInMinMax();
        this.LeggiSessione();
        this.mostra_stato_aria();
        this.PressioneAria();
        this.risposta_tiro_errato();
        this.mf.repaint();
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
            File myObj = new File(this.pathWatch + NomeFile);
            Scanner myReader = new Scanner(myObj);
            while (myReader.hasNextLine()) {
                contenutoFile += myReader.nextLine();
            }
            myReader.close();
        } catch (FileNotFoundException e) {
            System.out.println("An error occurred.");
            return "Errore lettura file";
        }
        return contenutoFile;
    }//End LeggiFile

    /**
     * LeggiFileLavoriDescrizione carica eventuali ThreadWorker dal file
     * /tmp/warning.txt
     */
    private void LeggiFileLavoriDescrizione() {
        this.mf.AggiornaLavoriDescrizione(LeggiFile(this.f_lavori_descrizione));
    }//End LeggiFileLavoriDescrizione

    /**
     * LeggiSessione
     */
    private void LeggiSessione() {
        this.mf.AggiornaSessione(LeggiFile(this.f_sessione));
    }//End LeggiFileLavoriDescrizione

    /**
     * ScriviFile metodo generico per scrivere una riga in un file
     *
     * @param NomeFile
     * @param CosaScrivere String testo da scrivere nel file
     */
    private void ScriviFile(String NomeFile, String CosaScrivere) {
        try {
            FileWriter fw = new FileWriter(this.pathWatch + NomeFile);
            PrintWriter pw = new PrintWriter(fw);
            pw.print(CosaScrivere);
            pw.flush();
            pw.close();
        } catch (IOException ex) {
            Logger.getLogger(JRivitMain.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    private void LeggiAriaInMinMax() {
        float min, max;
        min = Float.parseFloat(LeggiFile(f_soglia_pressione_aria_in_min));
        max = Float.parseFloat(LeggiFile(f_soglia_pressione_aria_in_max));
        this.mf.update_soglie_pressione_aria_in(min, max);
    }

}
