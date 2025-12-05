/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Main.java to edit this template
 */
package s7guiTest;

/**
 *
 * @author Luca
 */
import java.awt.*;
import java.awt.event.*;
import java.util.Timer;
import java.util.TimerTask;

import Moka7.S7;
import Moka7.S7Client;
import java.util.Arrays;

public class S7GuiFull extends Frame {

    private TextField txtDBW0;
    private TextField txtDBW2;
    private TextField txtWriteValue;
    private Button btnWrite;

    private S7Client s7;
    private Timer timer;
    private int v;

    public S7GuiFull() {
        super("S7 Test GUI - Lettura/Scrittura");

        setLayout(new GridLayout(4, 2, 5, 5));

        // ---- Lettura registri ----
        add(new Label("DB1.DBW0:"));
        txtDBW0 = new TextField();
        txtDBW0.setEditable(false);
        add(txtDBW0);

        add(new Label("DB1.DBW2:"));
        txtDBW2 = new TextField();
        txtDBW2.setEditable(false);
        add(txtDBW2);

        // ---- Scrittura registro ----
        add(new Label("Scrivi DB1.DBW4:"));
        txtWriteValue = new TextField();
        add(txtWriteValue);

        btnWrite = new Button("Scrivi");
        add(btnWrite);

        // ---- Pulsante scrittura ----
        btnWrite.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                writeToPLC();
            }
        });

        // ---- Setup finestra ----
        addWindowListener(new WindowAdapter() {
            public void windowClosing(WindowEvent e) {
                close();
                System.exit(0);
            }
        });

        setSize(300, 200);
        setVisible(true);

        // ---- Connessione PLC ----
        connectPLC();

        // ---- Timer di lettura periodica ----
        startTimer();
    }

    private void connectPLC() {
        s7 = new S7Client();

        String plcIP = "192.168.1.52"; // da modificare
        int rack = 0;
        int slot = 1;

        int res = s7.ConnectTo(plcIP, rack, slot);

        if (res == 0) {
            System.out.println("Connessione OK");
        } else {
            System.out.println("Errore connessione: " + S7Client.ErrorText(res));
        }
    }

    private void startTimer() {
        timer = new Timer();
        timer.scheduleAtFixedRate(new TimerTask() {
            @Override
            public void run() {
                //readPLC();
            }
        }, 0, 1000); // lettura ogni 1 secondo
    }

    private void readPLC(int DB, int registro) {
        v = readInteger(DB, registro);
    }

    private void writeToPLC() {
        try {
            int value = Integer.parseInt(txtWriteValue.getText());
            byte[] buffer = new byte[2];

            S7.SetDIntAt(buffer, 0, value);

            int result = s7.WriteArea(
                    S7.S7AreaDB,
                    1, // DB1
                    4, // DBW4
                    2,
                    buffer
            );

            if (result != 0) {
                System.out.println("Errore scrittura: " + S7Client.ErrorText(result));
            }

        } catch (NumberFormatException ex) {
            System.out.println("Valore non valido.");
        }
    }

    private void close() {
        if (timer != null) {
            timer.cancel();
        }
        if (s7 != null) {
            s7.Disconnect();
        }
    }

    /**
     * Legge un Intero
     *
     * @param dbNumber Nr del DB
     * @param start Offset
     * @return nr intero letto
     */
    public int readInteger(int dbNumber, int start) {
        byte[] buffer = new byte[2];
        if (s7.Connected) {
            if (s7.ReadArea(S7.S7AreaDB, dbNumber, start, 2, buffer) != 0) {
                System.out.println("S7 Read failed: " + S7Client.ErrorText(s7.LastError));
            } else {
                System.out.println("DBRead " + dbNumber + " data:" + Arrays.toString(buffer));
            }
            return S7.GetWordAt(buffer, 0);
        }
        return -1;
    }

    public static void main(String[] args) {
        new S7GuiFull();
    }
}
