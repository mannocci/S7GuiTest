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
import java.io.IOException;
import java.io.InputStreamReader;
import static java.lang.Runtime.getRuntime;
import java.nio.file.*;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import javax.swing.SwingWorker;
import java.util.Calendar;
import java.util.Date;
import java.util.Iterator;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author lucamannocci
 */
public class JDoWorker extends SwingWorker<String, Object> {

    JRivitMain Rm;
    JFileWorker fileWorker;
    private String operation = "";
    private DateFormat dateFormat;
    private Calendar now;
    // se il lavoro è in corso contiene "1"
    private final String f_risposta_tiro_errato = "risposta_tiro_errato";
    private final String f_nome_device = "nome_device";
    private String NomeDevice;
    private int tiriErrati;
    private long Pid;
    private String run_system_result;

    JDoWorker(JRivitMain mf, JFileWorker fw) {
        this.Rm = mf;
        this.fileWorker = fw;
        dateFormat = new SimpleDateFormat("HH:mm");
        now = Calendar.getInstance();
    }

    @Override
    protected String doInBackground() throws Exception {
        try {
            switch (this.operation) {

                case "init" ->
                    this.init();
                case "calibrazione" -> {
//                    this.comunicaLavoroScelto();
                    // E' importante che la variabile "richiesta" di Rm sia
                    // impostata prima di chiedere il lavoro per consentire
                    // l'avvio della procedura quando arriverà "lavoro pronto"
                    this.Rm.setRichiesta(Static.RICHIESTA_CALIBRAZIONE);
                    this.Rm.impostaLavoroScelto();
                }
                case "calibrazione_test" -> {
                    JFileWorker.scriviFileConReady(Static.F_RICHIESTA, Static.RICHIESTA_CALIBRAZIONE_TEST);
                    this.Rm.setRichiesta(Static.RICHIESTA_CALIBRAZIONE_TEST);
                }
                case "salva_calibrazione" -> {
                    JFileWorker.scriviFileConReady(Static.F_RICHIESTA, Static.RICHIESTA_CALIBRAZIONE_SALVA);
                    this.Rm.setRichiesta(Static.RICHIESTA_CALIBRAZIONE_SALVA);
                }
                case "annulla_calibrazione" -> {
                    JFileWorker.scriviFileConReady(Static.F_RICHIESTA, Static.RICHIESTA_CALIBRAZIONE_ANNULLA);
                    this.Rm.setRichiesta(Static.RICHIESTA_CALIBRAZIONE_ANNULLA);
//                    JFileWorker.scriviFileConReady(Static.F_RICHIESTA, Static.RICHIESTA_STOP);
//                    this.Rm.setRichiesta(Static.RICHIESTA_STOP);                    
                }
                case "stop" -> {
                    JFileWorker.scriviFileConReady(Static.F_RICHIESTA, Static.RICHIESTA_STOP);
                    this.Rm.setRichiesta(Static.RICHIESTA_STOP);
                }
                case "richiesta_pausa" -> {
                    JFileWorker.scriviFileConReady(Static.F_RICHIESTA, Static.RICHIESTA_PAUSA);
                    this.Rm.setRichiesta(Static.RICHIESTA_PAUSA);
                }

                case "riavvio_lavoro" ->
                    riavviaLavoro();
                case "riavvio_wl" ->
                    riavviaWL();
                case "continua", "accetta", "annulla" ->
                    JFileWorker.scriviFlag(Static.F_RISPOSTA_TIRO_ERRATO + "_" + this.operation);
                case "aggiorna_nome_device" -> {
                    this.NomeDevice = JFileWorker.leggiFile(this.f_nome_device);
                    this.Rm.setNomeDevice(this.NomeDevice);
                }
                case "aggiorna_nm_list" ->
                    this.update_status_nm();

                case "usb_db_backup" ->
                    this.esegui_db_backup();
                case "umount_usb" ->
                    this.esegui_umount_usb();
                case "usb_db_restore" ->
                    this.esegui_db_restore();
                case "usb_firmware_update" ->
                    this.esegui_firmware_update();
                case "aggiorna_stato_wifi" ->
                    this.update_status_wifi();

                case "leggi_stato_interfacce" ->
                    this.updateInfoInterfaces();

                case "on_of_nm_device" ->
                    this.on_of_nm_device();

                case "scelto_lavoro" -> {
                    this.comunicaLavoroScelto();
                }

                case "scelta_wl" -> {
                    this.comunicaWLScelta();
                }

                case "aggiorna info" -> {
                    JFileWorker.scriviFileConReady(Static.F_RICHIESTA, Static.RICHIESTA_AGGIORNA_INFO);
                    this.Rm.setRichiesta(Static.RICHIESTA_AGGIORNA_INFO);
                }
                case "orario" -> {
                    Date orario = now.getTime();
                    //this.dateFormat.setTimeZone(TimeZone.getTimeZone("Europe/London"));
                    this.Rm.set_jLabel_B_L(this.dateFormat.format(orario));
                    this.Rm.repaint();
                }
                case "certWifi_2.4GHz.sh", "certWifi_5GHz.sh", "certWifi_auto.sh", "certWifiStop.sh", "certSensLogStart.sh", "certSensLogStop.sh" -> {
                    String[] cmd = {"/home/adminsb/bin/" + this.operation};
                    getRuntime().exec(cmd);
                }
                case "certi_cicloPiu.sh", "certi_cicloMeno.sh" -> {
                    String[] cmd = {"/home/adminsb/bin/" + this.operation};
                    getRuntime().exec(cmd);
                }
                case "startCert" -> {
                    String[] cmd = {"/home/adminsb/bin/certStartCiclo.sh"};
                    getRuntime().exec(cmd);
                }
                case "stopCert" -> {
                    String[] cmd = {"/home/adminsb/bin/certStopCiclo.sh"};
                    getRuntime().exec(cmd);//Non Server il programma certSens con Arduino si chiude da solo
                }
                case "reset_system" -> {
                    JFileWorker.scriviFileConReady(Static.F_RICHIESTA, Static.RICHIESTA_SYSTEM_RESET);
                    this.Rm.setRichiesta(Static.RICHIESTA_SYSTEM_RESET);
                }
                case "imposta_tool" -> {
                    String tool[] = this.Rm.getListTools().getSelectedItem().split(",");
                    JFileWorker.scriviFile(Static.F_TOOL_SCELTO, tool[0]);
                    JFileWorker.scriviFileConReady(Static.F_RICHIESTA, Static.RICHIESTA_IMPOSTA_TOOL);
                    this.Rm.setRichiesta(Static.RICHIESTA_IMPOSTA_TOOL);
                    File f = new File("/home/adminsb/etc/"+Static.F_FIRST_TIME);
                    f.delete();
                    this.Rm.setInSceltaTool(false);
                    this.Rm.PanelMain();
                }
                case "reset_wl" -> {
                    JFileWorker.scriviFileConReady(Static.F_WL_SCELTA, this.Rm.getWLscelta());
                    JFileWorker.scriviFileConReady(Static.F_RICHIESTA, Static.RICHIESTA_RESET_WL);
                    this.Rm.setRichiesta(Static.RICHIESTA_RESET_WL);
                }
                case "reset_work" -> {
                    JFileWorker.scriviFileConReady(Static.F_RICHIESTA, Static.RICHIESTA_RESET_WORK);
                    this.Rm.setRichiesta(Static.RICHIESTA_RESET_WORK);
                }
                case "sono_in_wl" -> {
                    JFileWorker.scriviFileConReady(Static.F_SONO_IN, "wl");
                }
                case "sono_in_work" -> {
                    JFileWorker.scriviFileConReady(Static.F_SONO_IN, "work");
                }
                case "annulla_reset" ->
                    JFileWorker.cancellaFile(Static.F_RESET_REQUEST);
            }
        } catch (NumberFormatException ex) {
            Logger.getLogger(JRivitMain.class.getName()).log(Level.SEVERE, null, ex);
            throw new UnsupportedOperationException("Errore conversione numerica " + this.operation);
        }
        return "ok";
    }

    public void set_operation(String operation) {
        this.operation = operation;
    }

    /**
     * inizializza diversi stati per prevenire la scheda bianca Avvia l'istanza
     * della classe FileWorker
     */
    void init() {
        JFileWorker.scriviFile(Static.F_SCREEN_VERSION, this.Rm.versione + " " + this.Rm.data_release);
        this.fileWorker.start();//Avvio FileWorker
        this.fileWorker.initValues();
    }

    /**
     * Imposta lavoro scelto
     */
    void comunicaLavoroScelto() {
        String lavoro = this.Rm.getLavoroScelto();
        // Scrivo anche i dettagli del lavoro. Serviranno allo scambio dati tramite webSocket
        JFileWorker.scriviFileConReady(Static.F_W_SCELTO, lavoro);
    }

    /**
     * Riferisce a Control la scelta della Work List
     */
    void comunicaWLScelta() {
        String Wlista = this.Rm.getWLscelta();
        JFileWorker.scriviFileConReady(Static.F_WL_SCELTA, Wlista);
    }

    /**
     * Riavvia il lavoro scelto
     */
    void riavviaLavoro() {
        JFileWorker.scriviFileConReady(Static.F_RICHIESTA, Static.RICHIESTA_RIAVVIO);
        this.Rm.gr.resetCurva();
    }

    /**
     * Riavvia il lavoro scelto
     */
    void riavviaWL() {
        JFileWorker.scriviFileConReady(Static.F_RICHIESTA, Static.RICHIESTA_RIAVVIO_WL);
        this.Rm.gr.resetCurva();
    }

    /**
     * Attiva o disattiva di device di rete tramite lo script start_stop_NM.sh
     * Non serve passargli l'informazione se attivarla o meno la connessione
     * perché agisce facendo l'opposto dello stato attuale: se trova ON la
     * connessione la spegne e viceversa Diventa utile approfittare per
     * aggiornare lo stato della network su CT nel DB. aggiornare APList nel
     *
     */
    void on_of_nm_device() {
        String[] cmd = {"/home/adminsb/bin/start_stop_NM.sh", ""};
        String nomeSelezionato = this.Rm.getListSetupNM().getItem(this.Rm.getListSetupNM().getSelectedIndex());
        String nomeCon;
        if (nomeSelezionato.contains(" OFF")) {
            nomeCon = nomeSelezionato.substring(0, nomeSelezionato.indexOf(" OFF"));
        } else {
            nomeCon = nomeSelezionato.substring(0, nomeSelezionato.indexOf(" ON"));
        }
        cmd[1] = nomeCon;
        run_system_bash(cmd);
        this.Rm.set_jLabel_B_L("CON..");
    }

    /**
     * Legge lo stato delle interfacce attive
     */
    void updateInfoInterfaces() {
        String[] cmd = {"/home/adminsb/bin/info_interfaces.sh"};
        run_system_bash(cmd);
    }

    /**
     * Aggiorna lo stato della Wifi. Elenca Access point
     */
    void update_status_wifi() {
        JFileWorker.scriviFlag(Static.F_AGGIORNA_WIFI);
    }

    /**
     * Aggiorna la lista dei device per la connesione di rete
     */
    void update_status_nm() {
        String[] cmd = {"/home/adminsb/bin/nm_list_con.sh"};
        run_system_bash(cmd);
    }

    /**
     * Aggiorna la lista che contiene le informazioni del sistema
     */
    void updateInfo() {
        List<String> listaInfo = JFileWorker.leggiFileElenco(Static.F_INFO);
        this.Rm.setListInfo(listaInfo);
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
            exec.waitFor();
        } catch (IOException ex) {
            Logger.getLogger(JFileWorker.class.getName()).log(Level.SEVERE, null, ex);
        } catch (InterruptedException ex) {
            Logger.getLogger(JDoWorker.class.getName()).log(Level.SEVERE, null, ex);
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
    private static String printResults(Process process) throws IOException {
        BufferedReader reader = new BufferedReader(new InputStreamReader(process.getInputStream()));
        String line;
        while ((line = reader.readLine()) != null) {
            System.out.println(line);
        }
        return line;
    }

    /**
     * esegue il backup senza tabelle sec_* e testi ...
     */
    private void esegui_db_backup() {
        String usbPath = "";
        this.Rm.setInBackup(true);
        this.Rm.getListUsbFile().removeAll();
        this.Rm.getListUsbFile().add("");
        this.Rm.getListUsbFile().add("Backup ....");
        String[] cmd = {"/home/adminsb/bin/backup_db_web.sh", ""};
        Process proces = run_system_bash(cmd);
        if (proces.exitValue() == 0) {
            File from = new File("/var/www/html/_lib/file/doc/backup_db.zip");
            usbPath = this.Rm.getListInfo().getItem(0);
            File to = new File(usbPath + "/backup_db.zip");
            try {
                Files.copy(from.toPath(), to.toPath(), StandardCopyOption.REPLACE_EXISTING);
                this.Rm.getListUsbFile().removeAll();
                this.Rm.getListUsbFile().add("");
                this.Rm.getListUsbFile().add("Copy completed !");
                this.Rm.getListUsbFile().add("Now you can remove");
                this.Rm.getListUsbFile().add("USB Pendrive");
                cmd[0] = "umount";
                cmd[1] = usbPath;
                proces = run_system_bash(cmd);
            } catch (IOException ex) {
                Logger.getLogger(JDoWorker.class.getName()).log(Level.SEVERE, null, ex);
            }
        }

    }

    /**
     * esegue il restore dal file firmware.bin ...
     */
    private void esegui_firmware_update() {
        String usbPath = JFileWorker.leggiFile(Static.F_USB_PENDRIVE);
        this.Rm.setInBackup(true);
        usbPath += "/" + this.Rm.getListUsbFile().getSelectedItem();
        String[] cmd = {"/home/adminsb/bin/update_firmware.sh", ""};
        cmd[1] = usbPath;
        this.Rm.getListUsbFile().removeAll();
        this.Rm.getListUsbFile().add("");
        this.Rm.getListUsbFile().add("Start firmware update  ....");

        Process process = run_system_bash(cmd);
        if (process.exitValue() == 0) {
            this.Rm.getListUsbFile().removeAll();
            this.Rm.getListUsbFile().add("");
            this.Rm.getListUsbFile().add("Firmware updated successfully !");
        } else {
            this.Rm.getListUsbFile().removeAll();
            this.Rm.getListUsbFile().add("");
            this.Rm.getListUsbFile().add("Error updating firmware !");
            this.Rm.getListUsbFile().add("Restoring previous firmware version...");
            String[] cmdRestore = {"/home/adminsb/bin/rollback_firmware.sh", ""};
            process = run_system_bash(cmdRestore);
            if (process.exitValue() == 0) {
                this.Rm.getListUsbFile().add("");
                this.Rm.getListUsbFile().add("Firmware rollback successfully !");
                this.Rm.getListUsbFile().add("Reboot System in 5 seconds");
            } else {
                this.Rm.getListUsbFile().add("");
                this.Rm.getListUsbFile().add("Error rolling back firmware !");
                return;
            }
        }
        esegui_umount_usb();
        this.Rm.getListUsbFile().add("Reboot System in 5 seconds");
        try {
            Thread.sleep(4000);
        } catch (InterruptedException ex) {
            Logger.getLogger(JDoWorker.class.getName()).log(Level.SEVERE, null, ex);
        }
        JFileWorker.scriviFlag(Static.F_SYSTEM_FREEZE);
        try {
            Thread.sleep(2000);
        } catch (InterruptedException ex) {
            Logger.getLogger(JDoWorker.class.getName()).log(Level.SEVERE, null, ex);
        }
        JFileWorker.scriviFileConReady(Static.F_RICHIESTA, Static.RICHIESTA_SYSTEM_REBOOT);
    }

    /**
     * esegue il restore senza tabelle sec_* e testi ...
     */
    private void esegui_db_restore() {
        String usbPath = "";
        this.Rm.setInBackup(true);
        this.Rm.getListUsbFile().removeAll();
        this.Rm.getListUsbFile().add("");
        this.Rm.getListUsbFile().add("Copy files ....");
        String[] cmd = {"/home/adminsb/bin/restore_db_web.sh", ""};
        File to = new File("/var/www/html/_lib/file/doc/backup_db.zip");
        usbPath = this.Rm.getListInfo().getItem(0);
        File from = new File(usbPath + "/backup_db.zip");
        try {
            Files.copy(from.toPath(), to.toPath(), StandardCopyOption.REPLACE_EXISTING);
            this.Rm.getListUsbFile().removeAll();
            this.Rm.getListUsbFile().add("");
            this.Rm.getListUsbFile().add("Copy completed !");
            this.Rm.getListUsbFile().add("Now Start Restore DB ......");
            Process proces = run_system_bash(cmd);

            if (proces.exitValue() == 0) {
                this.Rm.getListUsbFile().removeAll();
                this.Rm.getListUsbFile().add("");
                this.Rm.getListUsbFile().add("Restore completed !");
                this.Rm.getListUsbFile().add("Umounted pen drive ");
            } else {
                this.Rm.getListUsbFile().removeAll();
                this.Rm.getListUsbFile().add("");
                this.Rm.getListUsbFile().add("Error to restore the DB!");
                this.Rm.getListUsbFile().add("Umounting pen drive");
            }
        } catch (IOException ex) {
            this.Rm.getListUsbFile().removeAll();
            this.Rm.getListUsbFile().add("");
            this.Rm.getListUsbFile().add("Error to copy the DB file !");
            this.Rm.getListUsbFile().add("Umounting pen drive");
            Logger.getLogger(JDoWorker.class.getName()).log(Level.SEVERE, null, ex);
        }
        esegui_umount_usb();
    }

    /**
     * Smonta la usb PenDrive
     */
    private void esegui_umount_usb() {
        String[] cmd = {"umount", ""};
        String usbPath = this.Rm.getListInfo().getItem(0);
        cmd[1] = usbPath;
        run_system_bash(cmd);
        System.out.print("umounted " + usbPath);
        this.Rm.getListUsbFile().add("Umounted pen drive " + usbPath + "!");
    }

}
