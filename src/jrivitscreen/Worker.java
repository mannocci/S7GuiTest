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
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.PrintWriter;
import static java.lang.Runtime.getRuntime;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import javax.swing.SwingWorker;
import java.util.Calendar;
import java.util.Date;
import java.util.Scanner;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author lucamannocci
 */
public class Worker extends SwingWorker<String, Object> {

    JRivitMain mf;
    ThreadWorker wt;
    ButtonThread bt;
    private String operation = "";
    private int tiriAnnullati = 0;
    private int tiriOK = 0;
    private final String pathWatch = "/tmp/CT/";
    private DateFormat dateFormat;
    private Calendar now;
    private final String f_tiri = "tiri";
    private final String f_tiri_ok = "tiri_ok";
    private final String f_tiri_errati = "tiri_errati";
    private final String f_tiri_annullati = "tiri_annullati";
    private final String f_lotti_ok = "lotti_ok";
    private final String f_errore = "errore";
    private final String f_lavoro_scelto = "lavoro_scelto.txt";
    private final String f_started = "started"; // se il lavoro è in corso contiene "1"
    private final String f_risposta_tiro_errato = "risposta_tiro_errato";
    private final String f_nome_device = "nome_device.txt";
    private final String f_aria = "aria";
    private final String f_sensori = "sensori";
    private String NomeDevice;
    private int tiriErrati;
    private long Pid;
    private String run_system_result;

    Worker(JRivitMain mf) {
        try {
            this.mf = mf;
            this.wt = new ThreadWorker(this.mf);
            this.bt = new ButtonThread(this.mf);
            dateFormat = new SimpleDateFormat("HH:mm");
            now = Calendar.getInstance();
        } catch (IOException ex) {
            Logger.getLogger(JRivitMain.class.getName()).log(Level.SEVERE, null, ex);
            throw new UnsupportedOperationException("Not supported yet."); // Generated from nbfs://nbhost/SystemFileSystem/Templates/Classes/Code/GeneratedMethodBody
        }
    }

    @Override
    protected String doInBackground() throws Exception {
        try {
            switch (this.operation) {
                case "start" -> {
                    this.bt.start();
                    Date orario = now.getTime();
                    //this.dateFormat.setTimeZone(TimeZone.getTimeZone("Europe/London"));
                    this.mf.set_jLabel_B_L(this.dateFormat.format(orario));
                    this.mf.repaint();
                   
//                    String [] Cmd = {"/usr/bin/ps","aux", "|", "grep","JRivitScreen"};
//                    this.run_system_bash(Cmd);
//                    if ( this.run_system_result.contains("JRivitScreen")){
//                        //Sessione già attiva, chiudere il programma
//                        System.out.print("Processo già attivo \n"+this.run_system_result+"\n");
//                        this.mf.Exit();
//                    }
                    this.wt.start();//Avvio Thread Watch File in Tmp
                    this.wt.initValues();
                }
                case "stop_lavoro" -> {
                    ScriviFile(this.f_started, "0");
                    ScriviFile(this.f_aria, "0");
                }
                case "continua", "accetta", "estendi", "annulla" -> {
                    ScriviFile(this.f_risposta_tiro_errato, this.operation);
                }
                case "abort" -> {
                    ScriviFile(this.f_started, "0");
                    ScriviFile(this.f_aria, "0");
                    ScriviFile(this.f_errore, "0");
                }
                case "aggiorna_nome_device" -> {
                    this.NomeDevice = LeggiFile(this.f_nome_device);
                    this.mf.setNomeDevice(this.NomeDevice);
                }
                case "tiri" -> {
                    String Tiri = LeggiFile("tiri");
                    this.mf.setJLabel_B_C(Tiri);
                }
                case "reset_errore" -> {
                    ScriviFile(f_errore, "0");
                }
                case "risposta_attesa_tiro_errato" -> {
                    risposta_attesa_tiro_errato();
                }
                case "aggiorna_tiri_errati" -> {
                    this.mf.setjLabelErrati("" + LeggiFile(this.f_tiri_errati));
                }
                case "aggiorna_lotti_ok" -> {
                    this.mf.set_nr_lotti_fatti(Integer.parseInt(LeggiFile(f_lotti_ok)));
                }
                case "curva" -> {
                    drawGrafico();
                }
                case "lavoro_scelto" -> {
                    String lavoro = this.mf.getLavoroScelto();
                    this.ScriviFile(this.f_lavoro_scelto, lavoro);
                    this.ScriviFile(this.f_started, "1");
                }
            }
        } catch (NumberFormatException ex) {
            Logger.getLogger(JRivitMain.class.getName()).log(Level.SEVERE, null, ex);
            throw new UnsupportedOperationException("Errore "+ ex.getMessage()); // Generated from nbfs://nbhost/SystemFileSystem/Templates/Classes/Code/GeneratedMethodBody
        }
        return "ok";
    }

    public void set_operation(String operation) {
        this.operation = operation;
    }

    private void drawGrafico() {
//        this.mf.jLayeredPaneCenter.moveToFront(this.jPanelCanvas);
        Graphics2D gr = (Graphics2D) this.mf.get_canvasGraph().getGraphics();
        gr.drawString("Java Source", 10, 10);
        int y = this.mf.get_canvasGraph().getHeight();
        String[] ychar = this.mf.getCurva().split(",");
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
            this.mf.get_canvasGraph().repaint();
            gr.drawString("Java Source", 10, 10);

            this.mf.repaint();
        }
    }

    void risposta_attesa_tiro_errato() {
        // Fabio: la gestione del tiro errato deve essere fatta 
        // da JControl. Quando i file saranno aggiornati da JControl
        // stesso l'interfaccia si adeguerà automaticamente
        switch (LeggiFile(f_risposta_tiro_errato)) {
            case "1" -> //Continua non devo contare il tiro come ok
                this.mf.reset_errore_tiro();
            case "4" -> //Annulla
                this.mf.setAlertDialogStop("Annullare il Tiro ?");
            case "2" -> // Estendi
            {
                // Estendi
                this.mf.reset_errore_tiro();
                int t = Integer.parseInt(this.mf.getjLabelValidi());
                t++;
                this.mf.setjLabelValidi("" + t);
            }
            case "3" -> // Accetta, come se fosse stato un tiro ok
            {
            }
        }
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
            try (Scanner myReader = new Scanner(myObj)) {
                while (myReader.hasNextLine()) {
                    contenutoFile += myReader.nextLine();
                }
            }
        } catch (FileNotFoundException e) {
            System.out.println("An error occurred.");
            return "Errore lettura file";
        }
        return contenutoFile;
    }//End LeggiFileLavoriDescrizione

    /**
     * ScriviFile metodo generico per scrivere un testo in un file
     *
     * @param NomeFile
     * @param Testo String testo da scrivere nel file
     */
    public void ScriviFile(String NomeFile, String Testo) {
        try {
            FileWriter fw = new FileWriter(this.pathWatch + NomeFile);
            try (PrintWriter pw = new PrintWriter(fw)) {
                pw.print(Testo);
                pw.flush();
            }
        } catch (IOException ex) {
            Logger.getLogger(JRivitMain.class.getName()).log(Level.SEVERE, null, ex);
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
    private static String printResults(Process process) throws IOException {
        BufferedReader reader = new BufferedReader(new InputStreamReader(process.getInputStream()));
        String line;
        while ((line = reader.readLine()) != null) {
            System.out.println(line);
        }
        return line;
    }
}
