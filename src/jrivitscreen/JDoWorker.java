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

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Graphics2D;
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
        this.file_worker =fw;
        this.bt = new ButtonThread(this.Rm);
        dateFormat = new SimpleDateFormat("HH:mm");
        now = Calendar.getInstance();
    }

    @Override
    protected String doInBackground() throws Exception {
        try {
            switch (this.operation) {
                
                case "init" -> {
                    this.bt.start();//Gestione dei pulsanti
                    this.file_worker.start();//Avvio FileWorker
                    this.file_worker.initValues();
                }

                case "stop" -> {
                    JFileWorker.ScriviFileLock(Static.F_STATO, Static.STATO_STOP);
                    JFileWorker.ScriviFileLock(Static.F_AGGIORNATO_STATO, Static.STATO_STOP);
                }

                case "pausa" -> {
                    JFileWorker.ScriviFileLock(Static.F_STATO, Static.STATO_PAUSA);
                    JFileWorker.ScriviFileLock(Static.F_AGGIORNATO_STATO, Static.STATO_STOP);
                }

                case "start" -> {
                    JFileWorker.ScriviFileLock(Static.F_STATO, Static.STATO_AVVIATO);
                    JFileWorker.ScriviFileLock(Static.F_AGGIORNATO_STATO, Static.STATO_STOP);
                }

                case "continua", "accetta", "annulla" -> {
                    JFileWorker.ScriviFileLock(Static.F_RISPOSTA_TIRO_ERRATO + "_" + this.operation, this.operation);
                }

                case "aggiorna_nome_device" -> {
                    this.NomeDevice = JFileWorker.LeggiFileLock(this.f_nome_device);
                    this.Rm.setNomeDevice(this.NomeDevice);
                }
                case "aggiorna_nm_list" -> {
                    String [] cmd = {"/home/adminsb/bin/nm_list_con.sh"};
                    List list_nm_con;
                    run_system_bash(cmd);
                     list_nm_con = JFileWorker.LeggiFileElencoLock(Static.F_LISTA_NM_CON);
                    this.Rm.setListNmCon(list_nm_con);
                }
                case "risposta_attesa_tiro_errato" -> {
                    risposta_attesa_tiro_errato();
                }
                
                case Static.F_CURVA -> {
                    drawGrafico();
                }
                
//                case "lavoro_scelto" -> {
//                    String lavoro = this.Rm.getLavoroScelto();
//                    JFileWorker.ScriviFileLock(Static.F_LAVORO_SCELTO, lavoro);
//                    this.Rm.setInErrore(false);
//                    //Aggiornare leggendo il DB i campi della ricetta DA FARE IN Control !!
//                }

                case "scegli_e_avvia" -> {
                    String lavoro = this.Rm.getLavoroScelto();
                    JFileWorker.cancellaFile(Static.F_LAVORO_SCELTO);
                    JFileWorker.ScriviFileLock(Static.F_LAVORO_SCELTO, lavoro);
                    this.Rm.setInErrore(false);
                    JFileWorker.ScriviFileLock(Static.F_STATO, Static.STATO_AVVIATO);
                    JFileWorker.ScriviFileLock(Static.F_AGGIORNATO_STATO, Static.STATO_AVVIATO);
                    this.Rm.PanelStarted();
                }
                
                case "aggiorna info" -> {
                    List<String> lista_info = JFileWorker.LeggiFileElencoLock(Static.F_INFO);
                    List<String> lista_sensori = JFileWorker.LeggiFileElencoLock(Static.F_SENSORI);
                    lista_info.add("=========================");
                    for (String string : lista_sensori) {
                        lista_info.add(string);
                    }
                    this.Rm.setListInfo(lista_info);
                }
                
                case "aggiorna warning" -> {
                    List<String> warning_file = JFileWorker.LeggiFileElencoLock(Static.F_WARNING);
                    int livello_warning = 0, livello = 0, posizione_riga = 0;

                    for (String string : warning_file) {
                        String[] warnig_list = string.split("§");
                        livello = Integer.parseInt(warnig_list[1]);
                        if (livello > livello_warning) {
                            livello_warning = livello;
                        }
                        warning_file.set(posizione_riga++, string + ", livello -> " + livello);
                    }
                    this.Rm.set_warning(livello_warning);//Aggiorna l'immagine warning
                    this.Rm.AggiornaWarning(warning_file);//Aggiorna lista descizioni warning
                }
                
                case "orario" -> {

                    Date orario = now.getTime();
                    //this.dateFormat.setTimeZone(TimeZone.getTimeZone("Europe/London"));
                    this.Rm.set_jLabel_B_L(this.dateFormat.format(orario));
                    this.Rm.repaint();
                }
                
                case "grafico" -> {
                    this.drawGrafico();
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

    private void drawGrafico() {
//        this.Rm.jLayeredPaneCenter.moveToFront(this.jPanelCanvas);
        Graphics2D gr = (Graphics2D) this.Rm.getjLayeredPaneCenter().getGraphics();
        gr.drawString("Java Source", 10, 10);
        int y = this.Rm.getjLayeredPaneCenter().getHeight();
        String[] ychar = this.Rm.getCurva().split(",");
        int nPoints;
        nPoints = ychar.length;
        int[] ypoints = new int[nPoints];
        if (nPoints > 0) {
            int[] xpoints = new int[nPoints];
            for (int i = 0; i < nPoints; i++) {
                xpoints[i] = i * 2;
                ypoints[i] = y - Integer.parseInt(ychar[i]) / 6;
            }
            gr.setStroke(new BasicStroke(3));
            gr.setColor(Color.GREEN);
            gr.drawPolyline(xpoints, ypoints, nPoints);
            this.Rm.getjLayeredPaneCenter().repaint();
            gr.drawString("Java Source", 10, 10);
        }
        this.Rm.setCurva(JFileWorker.LeggiFileLock("curva"));

    }

    void risposta_attesa_tiro_errato() {
        // Fabio: la gestione del tiro errato deve essere fatta 
        // da JControl. Quando i file saranno aggiornati da JControl
        // stesso l'interfaccia si adeguerà automaticamente
        switch (JFileWorker.LeggiFileLock(f_risposta_tiro_errato)) {
            case "1" -> //Continua non devo contare il tiro come ok
                this.Rm.aggiornaDaErroreTiro();
            case "4" -> //Annulla
                this.Rm.setAlertDialogStop("Annullare il Tiro ?");
            case "2" -> // Estendi
            {
                // Estendi
                this.Rm.aggiornaDaErroreTiro();
                int t = Integer.parseInt(this.Rm.getjLabelValidi());
                t++;
                this.Rm.setjLabelValidi("" + t);
            }
            case "3" -> // Accetta, come se fosse stato un tiro ok
            {

            }

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
