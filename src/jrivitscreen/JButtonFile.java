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
 * 
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
import static java.lang.Runtime.getRuntime;
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
public class JButtonFile extends Thread {
// Classi
    // prova di funzionamento della classe statica in comune tra i progetti java

//    String a = Statica.RICHIESTA_STOP_LAVORO;
    private final JRivitMain Rm;
    private WatchService watcher;
    private Path fileName;
    private WatchKey key;
    private int  OldW_Level;

    public JButtonFile(JRivitMain mf) throws IOException {
        this.Rm = mf;
        Path dir = Paths.get(Static.PATH_TASTI);
        // create gpio controller by file (run bash script before !)  
        JFileWorker.cancellaFileGenerico(Static.PATH_TASTI + Static.F_GPIO21);
        JFileWorker.cancellaFileGenerico(Static.PATH_TASTI + Static.F_GPIO16);
        JFileWorker.cancellaFileGenerico(Static.PATH_TASTI + Static.F_GPIO20);
        JFileWorker.cancellaFileGenerico(Static.PATH_TASTI + Static.F_GPIO26);
        JFileWorker.cancellaFileGenerico(Static.PATH_TASTI + Static.F_GPIO19);
        JFileWorker.cancellaFileGenerico(Static.PATH_TASTI + Static.F_GPIO12);
        try {
            watcher = FileSystems.getDefault().newWatchService();
        } catch (IOException ex) {
            Static.debug("Error starting watch service modified file registered for dir: " + dir.toString() + ": " + ex, 1);
        }
        dir.register(watcher, ENTRY_CREATE);
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
                        if (!fileName.toString().startsWith(Static.F_SENSORI)
                                && !fileName.toString().startsWith("certSens")) {
                            Static.debug("Creato: " + fileName, 4);
                        }
//            creaBottone("PL1", 21);
//            creaBottone("PL2", 16);
//            creaBottone("PL3", 20);
//            creaBottone("PR1", 26);
//            creaBottone("PR2", 19);
//            creaBottone("PR3", 12);

                        switch (fileName.toString()) {
                            case Static.F_GPIO21 ->
                                this.Rm.pulsanteHw("PL1");
                            case Static.F_GPIO16 ->
                                this.Rm.pulsanteHw("PL2");
                            case Static.F_GPIO20 ->
                                this.Rm.pulsanteHw("PL3");
                            case Static.F_GPIO26 ->
                                this.Rm.pulsanteHw("PR1");
                            case Static.F_GPIO19 ->
                                this.Rm.pulsanteHw("PR2");
                            case Static.F_GPIO12 ->
                                this.Rm.pulsanteHw("PR3");
                        }
                        JFileWorker.cancellaFileGenerico(Static.PATH_TASTI + fileName.toString());
                    }

                }//End For watchevent

                if (!key.reset()) {
                    //Problema non riesce il sistema a controllare il path indicato
                    Logger.getLogger(JButtonFile.class.getName()).log(Level.SEVERE, null, " Errore Key.reset is null");
                    break;
                }

            }
        } catch (InterruptedException | RuntimeException ex) {
            Logger.getLogger(JButtonFile.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

}
