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

    private MainJFrame mf;
    private String pathWatch = "/tmp/gpio/";
    private WatchService watcher;

    public WorkThread() throws IOException {
        // create gpio controller by file (run bash script before !)     
        try {
            watcher = FileSystems.getDefault().newWatchService();
        } catch (IOException ex) {
            Logger.getLogger(WorkThread.class.getName()).log(Level.SEVERE, null, ex);
        }
        Path dir = Paths.get(this.pathWatch);
        dir.register(watcher, ENTRY_MODIFY);
        System.out.println("Watch Service Modify file registered for dir: " + dir.getFileName());
    }

    public void set_mf(MainJFrame mf) {
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
                Path fileName = ev.context();
                String data = "";
                //System.out.println(kind.name() + ": " + fileName);

                if (kind == ENTRY_MODIFY) {
                    try {
                        File myObj = new File("/tmp/gpio/" + fileName.toString());
                        Scanner myReader = new Scanner(myObj);
                        while (myReader.hasNextLine()) {
                            data = myReader.nextLine();
                            //System.out.println(data);
                        }
                        myReader.close();
                    } catch (FileNotFoundException e) {
                        System.out.println("An error occurred.");
                        e.printStackTrace();
                    }
                    
                    if (! data.isEmpty() && data.contentEquals("0")) {
                        //System.out.printf("il pulsante %s è stato premuto\n", fileName);
                        this.send_p(fileName.toString());
                    }

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

}
