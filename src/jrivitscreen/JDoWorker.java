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
import java.io.PrintWriter;
import static java.lang.Runtime.getRuntime;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import javax.swing.SwingWorker;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Scanner;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author lucamannocci
 */
public class JDoWorker extends SwingWorker<String, Object> {

    JRivitMain mf;
    JFileWorker wt;
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
    // se il lavoro è in corso contiene "1"
    private final String f_risposta_tiro_errato = "risposta_tiro_errato";
    private final String f_nome_device = "nome_device";
    private final String f_aria = "aria";
    private final String f_sensori = "sensori";
    private String NomeDevice;
    private int tiriErrati;
    private long Pid;
    private String run_system_result;

    JDoWorker(JRivitMain mf) {
        try {
            this.mf = mf;
            this.wt = new JFileWorker(this.mf, this);
            this.bt = new ButtonThread(this.mf);
            dateFormat = new SimpleDateFormat("HH:mm");
            now = Calendar.getInstance();
        } catch (IOException ex) {
            Logger.getLogger(JRivitMain.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    @Override
    protected String doInBackground() throws Exception {
        try {
            switch (this.operation) {
                case "start" -> {
                    this.bt.start();//Gestione dei pulsanti
                    this.wt.start();//Avvio FileWorker
                    this.wt.initValues();
                }
                case "stato_stop", "stato_pausa" -> {
                    this.mf.PanelStart();
                }
                case "continua", "accetta", "annulla" -> {

                    this.mf.ritorno_da_errore();
                }
                case "aggiorna_nome_device" -> {
                    this.NomeDevice = LeggiFile(this.f_nome_device);
                    this.mf.setNomeDevice(this.NomeDevice);
                }
                case "tiri_errati" -> {
                    String Tiri = LeggiFile(JRivitMain.F_TIRI_ERRATI);
                    try {
                        this.mf.set_nr_tiri(Integer.parseInt(Tiri));
                        this.mf.update_tiri_lotti();
                    } catch (NumberFormatException e) {
                        System.out.println("File tiri_ok non numerico\n" + e.getMessage());
                    }
                }
                case "tiri" -> {
                    String Tiri = LeggiFile(JRivitMain.F_TIRI);
                    try {
                        this.mf.set_nr_tiri(Integer.parseInt(Tiri));
                        this.mf.update_tiri_lotti();
                    } catch (NumberFormatException e) {
                        System.out.println("File tiri_ok non numerico\n" + e.getMessage());
                    }
                }
                case "tiri_ok" -> {
                    String Tiri = LeggiFile(JRivitMain.F_TIRI_OK);
                    try {
                        this.mf.set_nr_tiri_ok(Integer.parseInt(Tiri));
                        this.mf.update_tiri_lotti();
                    } catch (NumberFormatException e) {
                        System.out.println("File tiri_ok non numerico\n" + e.getMessage());
                    }
                }
                case "tiri_annullati" -> {
                    String Tiri = LeggiFile(JRivitMain.F_TIRI_ANNULLATI);
                    try {
                        this.mf.set_nr_tiri_annullati(Integer.parseInt(Tiri));
                        this.mf.update_tiri_lotti();
                    } catch (NumberFormatException e) {
                        System.out.println("File tiri_ok non numerico\n" + e.getMessage());
                    }
                }
                case "risposta_attesa_tiro_errato" -> {
                    risposta_attesa_tiro_errato();
                }
                case "aggiorna_tiri_errati" -> {
                    this.mf.setjLabelErrati("" + LeggiFile(this.f_tiri_errati));
                }
                case "aggiorna_tiri_annullati" -> {
                    this.mf.setjLabelAnnullati("" + LeggiFile(this.f_tiri_annullati));
                }
                case "aggiorna_lotti_ok" -> {
                    this.mf.set_nr_lotti_ok(Integer.parseInt(LeggiFile(f_lotti_ok)));
                }
                case "curva" -> {
                    drawGrafico();
                }
                case "lavoro_scelto" -> {
                    String lavoro = this.mf.getLavoroScelto();
                    this.ScriviFile(JRivitMain.F_LAVORO_SCELTO, lavoro);
                    this.ScriviFile(JRivitMain.F_LAVORO_AVVIATO, "");
                }
                case "aggiorna info" -> {
                    List<String> lista_info = this.wt.LeggiFileElenco(JRivitMain.F_INFO);
                    List<String> lista_sensori = this.wt.LeggiFileElenco(JRivitMain.F_SENSORI);
                    lista_info.add("=========================");
                    for (String string : lista_sensori) {
                        lista_info.add(string);
                    }
                    this.mf.setListInfo(lista_info);
                }
                case "aggiorna warning" -> {
                    List<String> warning_file = this.wt.LeggiFileElenco(JRivitMain.F_WARNING);
                    int livello_warning = 0, livello = 0, posizione_riga = 0;

                    for (String string : warning_file) {
                        String[] warnig_list = string.split("§");
                        livello = Integer.parseInt(warnig_list[1]);
                        if (livello > livello_warning) {
                            livello_warning = livello;
                        }
                        warning_file.set(posizione_riga++, string + ", livello -> " + livello);
                    }
                    this.mf.set_warning(livello_warning);//Aggiorna l'immagine warning
                    this.mf.AggiornaWarning(warning_file);//Aggiorna lista descizioni warning
                }
                case "orario" -> {

                    Date orario = now.getTime();
                    //this.dateFormat.setTimeZone(TimeZone.getTimeZone("Europe/London"));
                    this.mf.set_jLabel_B_L(this.dateFormat.format(orario));
                    this.mf.repaint();
                }
            }
        } catch (NumberFormatException ex) {
            Logger.getLogger(JRivitMain.class.getName()).log(Level.SEVERE, null, ex);
            throw new UnsupportedOperationException("Errore " + ex.getMessage()); // Generated from nbfs://nbhost/SystemFileSystem/Templates/Classes/Code/GeneratedMethodBody
        }
        return "ok";
    }

    public void set_operation(String operation) {
        this.operation = operation;
    }

    private void drawGrafico() {
//        this.mf.jLayeredPaneCenter.moveToFront(this.jPanelCanvas);
        Graphics2D gr = (Graphics2D) this.mf.getjLayeredPaneCenter().getGraphics();
        gr.drawString("Java Source", 10, 10);
        int y = this.mf.getjLayeredPaneCenter().getHeight();
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
            this.mf.getjLayeredPaneCenter().repaint();
            gr.drawString("Java Source", 10, 10);
        }
        this.mf.setCurva(LeggiFile("curva"));

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
            System.out.println("File non trovato " + NomeFile);
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
