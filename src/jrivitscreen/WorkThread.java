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
    private String pathWatch = "/tmp/";
    private WatchService watcher;
    private Path fileName;

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
                String data = "";
                //System.out.println(kind.name() + ": " + fileName);

                if (kind == ENTRY_DELETE) {
                    switch (fileName.toString()) {
                        case "aria":
                            aria_chiusa();
                            break;
                    }
                }
                if (kind == ENTRY_CREATE) {
                    switch (fileName.toString()) {
                        case "aria" -> aria_aperta();
//                        case "errore_tiro" -> errore_tiro();
                    }
                }
                if (kind == ENTRY_MODIFY) {
                    switch (fileName.toString()) {
                        case "tiri" -> read_tiri();
                        case "errore_tiro" -> errore_tiro();
                        case "info.txt" -> read_info();
                        case "warning.txt" -> read_warning();
                        case "setup_lan.txt" -> read_setup_lan();
                        case "setup_wifi.txt" -> read_setup_wifi();
                        case "lavori.txt" -> read_lavori();
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
    private void aria_aperta() {
        this.mf.aria_aperta();
        this.mf.repaint();
    }

    private void aria_chiusa() {
        this.mf.aria_chiusa();
        this.mf.repaint();
    }

    private void read_tiri() {
        String data = "";
        try {
            File myObj = new File(pathWatch+this.fileName.toString());
            Scanner myReader = new Scanner(myObj);
            while (myReader.hasNextLine()) {
                data = myReader.nextLine();
                //System.out.println("letto nr tiri "+data);
            }
            myReader.close();
        } catch (FileNotFoundException e) {
            System.out.println("An error occurred.");
            e.printStackTrace();
        }
        this.mf.nr_tiri(Integer.parseInt(data));
        this.mf.repaint();
    }

    private void errore_tiro() {
        String data = "";
        try {
            File myObj = new File(pathWatch+this.fileName.toString());
            Scanner myReader = new Scanner(myObj);
            while (myReader.hasNextLine()) {
                data = myReader.nextLine();
                //System.out.println("letto nr tiri "+data);
            }
            myReader.close();
        } catch (FileNotFoundException e) {
            System.out.println("An error occurred.");
            e.printStackTrace();
        }
        this.mf.nr_errori(Integer.parseInt(data));
        this.mf.errore_tiro();
        this.mf.repaint();       
    }

    private void read_lavori() {
        this.mf.LeggiLavori();
        this.mf.repaint();
    }

    private void read_info() {
        this.mf.LeggiInfo();
        this.mf.repaint();
    }

    private void read_warning() {
        this.mf.LeggiWarning();
        this.mf.repaint();
    }

    private void read_setup_lan() {
        this.mf.LeggiSetupLan();
        this.mf.repaint();
    }

    private void read_setup_wifi() {
        this.mf.LeggiSetupWiFi();
        this.mf.repaint();
    }
}
