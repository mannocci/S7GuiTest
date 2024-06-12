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
                    this.impostaLavoro();
                    this.Rm.setRichiesta(Static.RICHIESTA_CALIBRAZIONE);
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
                case "pausa" -> {
                    JFileWorker.scriviFileConReady(Static.F_RICHIESTA, Static.RICHIESTA_PAUSA);
                    this.Rm.setRichiesta(Static.RICHIESTA_PAUSA);
                }
                case "riavvio" ->
                    riavviaLavoro();
                case "continua", "accetta", "annulla" ->
                    JFileWorker.scriviFlag(Static.F_RISPOSTA_TIRO_ERRATO + "_" + this.operation);
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

                case "scegli" -> {
                    this.impostaLavoro();
                }

                case "scegli_wl" -> {
                    this.scegliWL();
                    //this.fileWorker.WlListaPronta();    // Lettura elenco lavori della lista. Da rivedere se si può chiamare in modo più "pulito"
                }

                case "scegli_e_avvia" -> {
                    this.impostaLavoro();//Crea w_scelto
                }

                case "scegli_e_avvia_wl" -> {
                    this.scegliWL();
                    this.fileWorker.WlListaPronta();    // Lettura elenco lavori della lista. Da rivedere se si può chiamare in modo più "pulito"
                    this.avviaWL();
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
                case "certWifi_2.4GHz.sh", "certWifi_5GHz.sh", "certWifi_auto.sh", "certWifiStop.sh", 
                        "certSensLogStart.sh", "certSensLogStop.sh" -> {
                    String[] cmd = {"/home/adminsb/bin/" + this.operation};
                    getRuntime().exec(cmd);
                }
                case "startCert" -> {
                    String[] cmd = {"/home/adminsb/bin/certSensStart.sh"};
                    getRuntime().exec(cmd);
                }
                case "stopCert" -> {
                    String[] cmd = {"/home/adminsb/bin/certSensStop.sh"};
                    run_system_bash(cmd);//Non Server il programma certSens con Arduino si chiude da solo
                }
                case "reset_system" -> {
                    JFileWorker.scriviFileConReady(Static.F_RICHIESTA, Static.RICHIESTA_RESET_SYSTEM);
                    this.Rm.setRichiesta(Static.RICHIESTA_RESET_SYSTEM);
                }
                case "imposta_tool" -> {
                    String tool[] = this.Rm.getListTools().getSelectedItem().split(",");
                    JFileWorker.scriviFile(Static.F_TOOL_SCELTO, tool[0]);
                    JFileWorker.scriviFileConReady(Static.F_RICHIESTA, Static.RICHIESTA_IMPOSTA_TOOL);
                    this.Rm.setRichiesta(Static.RICHIESTA_IMPOSTA_TOOL);
                    JFileWorker.cancellaFile(Static.F_FIRST_TIME);
                    this.Rm.setInSceltaTool(false);
                    this.Rm.PanelMain();
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
        this.fileWorker.start();//Avvio FileWorker
        this.fileWorker.initValues();
    }

    /**
     * Imposta lavoro scelto
     */
    void impostaLavoro() {
        String lavoro = this.Rm.getLavoroScelto();
        // Scrivo anche i dettagli del lavoro. Serviranno allo scambio dati tramite webSocket
        JFileWorker.scriviFileConReady(Static.F_W_SCELTO, lavoro
                + "§" + this.Rm.getLimLotti()
                + "§" + this.Rm.getLimPezzi()
                + "§" + this.Rm.getUDLotti()
                + "§" + this.Rm.geUDPezzi()
        );
    }

    /**
     * Riferisce a Control la scelta della Work List
     */
    void scegliWL() {
        String Wlista = this.Rm.getWLscelta();
        JFileWorker.scriviFileConReady(Static.F_WL_SCELTA, Wlista
                + "§" + this.Rm.getWLnrCicli());
    }

    /**
     * Avvia la richiesta a Control per la WorkList Questo implica di dover
     * andare a leggere la lista dei lavori e scriverli in un file con
     * indice§nome lavoro. ogni volta che screen inizia un lavoro viene rimosso
     * dal file che inizierà con l'indica§nome lavoro successivo
     */
    void avviaWL() {
        JFileWorker.scriviFileConReady(Static.F_RICHIESTA, Static.RICHIESTA_AVVIO_WL);
        this.Rm.gr.resetCurva();
    }

    /**
     * Riavvia il lavoro scelto
     */
    void riavviaLavoro() {
        JFileWorker.scriviFileConReady(Static.F_RICHIESTA, Static.RICHIESTA_RIAVVIO);
        this.Rm.gr.resetCurva();
    }

    /**
     * Attiva o disattiva di device di rete tramite lo script start_stop_NM.sh
     * Non serve passargli l'informazione se attivarla o meno la connessione
     * perché agisce facendo l'opposto dello stato attuale: se trova ON la
     * connessione la spegne e viceversa Diventa utile approfittare per
     * aggiornare lo stato della network su CT nel DB.
     * aggiornare APList nel 
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
