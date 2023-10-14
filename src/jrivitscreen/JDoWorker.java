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
import java.io.IOException;
import java.io.InputStreamReader;
import static java.lang.Runtime.getRuntime;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import javax.swing.SwingWorker;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author lucamannocci
 */
public class JDoWorker extends SwingWorker<String, Object> {

    JRivitMain Rm;
    JFileWorker file_worker;
    ButtonThread bt;
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
        this.file_worker = fw;
        this.bt = new ButtonThread(this.Rm);
        dateFormat = new SimpleDateFormat("HH:mm");
        now = Calendar.getInstance();
    }

    @Override
    protected String doInBackground() throws Exception {
        try {
            switch (this.operation) {

                case "init" ->
                    this.init();

                case "stop" -> {
                    JFileWorker.scriviFile(Static.F_RICHIESTA, Static.RICHIESTA_STOP);
                }

                case "pausa" -> {
                    JFileWorker.scriviFile(Static.F_RICHIESTA, Static.RICHIESTA_PAUSA);
                }
                case "riavvio" -> {
                    JFileWorker.scriviFile(Static.F_RICHIESTA, Static.RICHIESTA_RIAVVIO);
                }
                case "start" -> {
                    JFileWorker.scriviFile(Static.F_RICHIESTA, Static.RICHIESTA_AVVIO);
                }

                case "continua" -> {
                    JFileWorker.scriviFile(Static.F_RISPOSTA_TIRO_ERRATO + "_" + this.operation, this.operation);
                }
                case "accetta" -> {
                    JFileWorker.scriviFile(Static.F_RISPOSTA_TIRO_ERRATO + "_" + this.operation, this.operation);
                }
                case "annulla" -> {
                    JFileWorker.scriviFile(Static.F_RISPOSTA_TIRO_ERRATO + "_" + this.operation, this.operation);
                }
                case "aggiorna_nome_device" -> {
                    this.NomeDevice = JFileWorker.leggiFile(this.f_nome_device);
                    this.Rm.setNomeDevice(this.NomeDevice);
                }
                case "aggiorna_nm_list" ->
                    this.update_status_nm();

                case "aggiorna_stato_wifi" ->
                    this.update_status_wifi();

                case "aggiorna_stato_lan" ->
                    this.update_status_lan();

                case "on_of_nm_device" ->
                    this.on_of_nm_device();

                case "scegli_e_avvia" ->
                    this.scegli_e_avvia();

                case "aggiorna info" ->
                    this.updateInfo();

                case "orario" -> {
                    Date orario = now.getTime();
                    //this.dateFormat.setTimeZone(TimeZone.getTimeZone("Europe/London"));
                    this.Rm.set_jLabel_B_L(this.dateFormat.format(orario));
                    this.Rm.repaint();
                }

                case "grafico" -> {
                    this.Rm.repaint();

                    //this.drawGrafico();
                }
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
        this.bt.start();//Gestione dei pulsanti
        this.file_worker.start();//Avvio FileWorker
        this.file_worker.initValues();
    }

    /**
     * Imposta lavoro scelto e lo avvia
     */
    void scegli_e_avvia() {
        String lavoro = this.Rm.getLavoroScelto();
        // Scrivo anche i dettagli del lavoro. Serviranno allo scambio dati tramite webSocket
        JFileWorker.scriviFile(Static.F_LAVORO_SCELTO, lavoro
            + "§" + this.Rm.getLimLotti()
            + "§" + this.Rm.getLimPezzi());
        JFileWorker.scriviFile(Static.F_RICHIESTA, Static.RICHIESTA_AVVIO);
        // Lo stato AVVIATO verrà scritto da Control
    }

    /**
     * Attiva o disattiva di device di rete
     */
    void on_of_nm_device() {
        String nomeDevice = this.Rm.getListSetupNM().getItem(this.Rm.getListSetupNM().getSelectedIndex());
        String device;
        if (nomeDevice.contains(" OFF")) {
            device = nomeDevice.substring(0, nomeDevice.indexOf(" OFF"));
        } else {
            device = nomeDevice.substring(0, nomeDevice.indexOf(" ON"));
        }

        String[] cmd = {"/home/adminsb/bin/start_stop_NM.sh", device};
        run_system_bash(cmd);
        //Dopo aver avviato o spento una con. deve aggiornare il file
        cmd[0] = "/home/adminsb/bin/nm_list_con.sh";
        run_system_bash(cmd);
        this.Rm.set_jLabel_B_L("CON..");
    }

    /**
     * Aggiorna lo stato del device ETH0
     */
    void update_status_lan() {
        String[] cmd = {"/home/adminsb/bin/status_lan.sh"};
        run_system_bash(cmd);
    }

    /**
     * Aggiorna lo stato della Wifi. Elenca Access point
     */
    void update_status_wifi() {
        String[] cmd = {"/home/adminsb/bin/status_wifi.sh"};
        run_system_bash(cmd);

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
        List<String> listaSensori = JFileWorker.leggiFileElenco(Static.F_SENSORI);
        listaInfo.add("=========================");
        for (String string : listaSensori) {
            listaInfo.add(string);
        }
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
            run_system_result = printResults(exec);
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
    private static String printResults(Process process) throws IOException {
        BufferedReader reader = new BufferedReader(new InputStreamReader(process.getInputStream()));
        String line;
        while ((line = reader.readLine()) != null) {
            System.out.println(line);
        }
        return line;
    }

}
