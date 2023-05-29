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
 */
package jrivitscreen;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.logging.Level;
import java.util.logging.Logger;
import static java.nio.file.StandardWatchEventKinds.ENTRY_CREATE;
import static java.nio.file.StandardWatchEventKinds.ENTRY_DELETE;
import static java.nio.file.StandardWatchEventKinds.ENTRY_MODIFY;

import java.io.IOException;
import java.nio.file.FileSystems;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.WatchEvent;
import java.nio.file.WatchKey;
import java.nio.file.WatchService;
import java.util.Scanner;

/**
 *
 * @author lucamannocci
 */
public class WorkThread extends Thread {

    private JRivitMain mf;
    private final String pathWatch = "/tmp/CT/";
    private WatchService watcher;
    private Path fileName;
    private final String TiriOk = "tiri_ok";
    private final String Errati = "tiri_errati";
    private final String Annullati = "tiri_annullati";
    private final String Info = "info.txt";
    private final String Warning = "warning.txt";
    private final String SetupLan = "setup_lan.txt";
    private final String SetupWiFi = "setup_wifi.txt";
    private final String ListaLavori = "lavori.txt";
    private final String Aria = "aria";
    private final String Errore = "errore";

    public WorkThread() throws IOException {
        // create gpio controller by file (run bash script before !)     
        try {
            watcher = FileSystems.getDefault().newWatchService();
        } catch (IOException ex) {
            Logger.getLogger(WorkThread.class.getName()).log(Level.SEVERE, null, ex);
        }
        Path dir = Paths.get(this.pathWatch);
        dir.register(watcher, ENTRY_MODIFY, ENTRY_CREATE, ENTRY_MODIFY);
        System.out.println("Watch Service Modify file registered for dir: " + dir.getFileName());
    }

    public void set_mf(JRivitMain mf) {
        this.mf = mf;
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
                            read_tiri();
                        case "tiri_errati" ->
                            tiri_errati();
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
                    }
//                        case "PR1":
//                        case "PR2":
//                        case "PR3":
//                        case "PL1":
//                        case "PL2":
//                        case "PL3":
//                            read_pulsane_premuto(fileName.toString());
//                            break;

                    //System.out.printf("il pulsante %s è stato premuto",fileName.toString());
                    try {
                        Thread.sleep(200);
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
    private void read_tiri() {
        this.mf.nr_tiri(Integer.parseInt(mf.LeggiFile("tiri")));
    }

    private void mostra_stato_aria() {
        String stato = mf.LeggiFile("tiri");
        if (stato.equals("0")) {
            this.mf.aria_chiusa();
        } else {
            this.mf.aria_aperta();
        }
    }

    /**
     * Errore_tiro legge nr tiri errati e li passa al RivitMain
     */
    private void tiri_errati() {
        this.mf.nr_errori(Integer.parseInt(this.mf.LeggiFile(this.Errati)));
    }

    /**
     * Legge il file con la descrizione dei lavori
     */
    private void read_lavori() {
        this.mf.LeggiLavori();
    }

    /**
     * Legge il file con la descrizione delle info di sistema
     */
    private void read_info() {
        this.mf.LeggiInfo();
    }

    /**
     * Legge il file con la descrizione dei Warning
     */
    private void read_warning() {
        this.mf.LeggiWarning();
    }

    /**
     * Legge il file con la descrizione della configurazione della LAN
     */
    private void read_setup_lan() {
        this.mf.LeggiSetupLan();
    }

    /**
     * Legge il file con la descrizione della configurazione della WiFi
     */
    private void read_setup_wifi() {
        this.mf.LeggiSetupWiFi();
    }

    private void tiri_annullati() {
        this.mf.nr_errori(Integer.parseInt(this.mf.LeggiFile(this.Errore)));
        this.mf.tiri_errati();
    }

    private void errore() {
        this.mf.errore();
    }
}
