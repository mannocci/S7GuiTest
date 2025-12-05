/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package s7guiTest;

import Moka7.S7;
import Moka7.S7Client;
import static Moka7.S7Client.errTCPDataRecvTout;
import java.io.IOException;
import java.util.Arrays;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author Luca
 */
public class JS7Test {

    private S7Client s7;
    private String CRIV_IP;
    private int connectStatus;
    private boolean primoErrore;
    public int LastError = 0;

    JS7Test() {
        CRIV_IP = "";
    }

    public int connectCRIV() {
        s7 = new S7Client();

        int rack = 0;
        int slot = 1;

        connectStatus = s7.ConnectTo(CRIV_IP, rack, slot);

        if (connectStatus == 0) {
            System.out.println("Connessione OK");
        } else {
            System.out.println("Errore connessione: " + S7Client.ErrorText(connectStatus));
            return -1;
        }
        return 0;
    }

    public void disconnectCRIV() {
        s7.Disconnect();
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
        if (s7 != null) {
            if (s7.Connected) {
                if (s7.ReadArea(S7.S7AreaDB, dbNumber, start, 2, buffer) != 0) {
                    System.out.println("S7 Read failed: " + S7Client.ErrorText(s7.LastError));
                } else {
                    System.out.println("DBRead " + dbNumber + " data:" + Arrays.toString(buffer));
                }
                return S7.GetWordAt(buffer, 0);
            }
        }
        return 0;
    }

    /**
     * Scrive un Integer nell'area di memoria prescelta
     *
     * @param dbNumber Nr del DB
     * @param start Offset
     * @param value Valore Integer da scrivere
     */
    public void writeInteger(int dbNumber, int start, int value) {
        byte[] buffer = new byte[2];
        if (s7.Connected) {
            S7.SetWordAt(buffer, 0, value);
            int Result = s7.WriteArea(S7.S7AreaDB, dbNumber, start, 2, buffer);
            if (Result == 0) {
                System.out.println("DBWrite " + dbNumber + " data:" + Arrays.toString(buffer));
            } else {
                System.out.println("S7 Write failed: " + S7Client.ErrorText(s7.LastError));
                if (Result == S7Client.errTCPDataSend
                        || Result == S7Client.errTCPConnectionFailed
                        || Result == S7Client.errTCPDataRecvTout) {
                    tryReconnection();
                }
            }
        }
    }

    /**
     * Write Byte
     *
     * @param dbNumber Nr del DB
     * @param start offset
     * @param value valore
     */
    public void writeByte(int dbNumber, int start, byte value) {
        byte[] buffer = new byte[1];
        if (s7.Connected) {
            buffer[0] = value;
            int Result = s7.WriteArea(S7.S7AreaDB, dbNumber, start, 1, buffer);
            if (Result == 0) {
                System.out.println("DBWrite " + dbNumber + " data:" + Arrays.toString(buffer));
            } else {
                System.out.println("S7 Write failed: " + S7Client.ErrorText(s7.LastError));
            }
        }
    }

    private void tryReconnection() {
        if (this.primoErrore) {
            System.out.println("Retrying connection to SNAP7 server !");
        }
        s7.Disconnect();    // Forza la disconnessione e imposta Client.Connected = false
        // E' importante che i tentativi di riconnessione vengano effettuati in un thread separato 
        // per evitare il blocco di control
        new Thread(() -> {
            while (true) {
                if (connectCRIV() == 0) {    // Se va a buon fine imposta Client.Connected = true
                    return;
                }
                try {
                    Thread.sleep(5000);
                } catch (InterruptedException ex) {
                    System.out.println(ex);
                }
            }//rimane nel loop se non connesso
        }).start();
    }

    public String getCRIV_IP() {
        return CRIV_IP;
    }

    public void setCRIV_IP(String CRIV_IP) {
        this.CRIV_IP = CRIV_IP;
    }

    /**
     *
     * @return 0 se la connessione è OK
     */
    public int getConnectStatus() {
        return connectStatus;
    }
    /*
    private int WaitForData(int Size, int Timeout) 
    {
        int cnt = 0;
        LastError=0;
        int SizeAvail;
        boolean Expired = false;
        try 
        {
            SizeAvail=InStream.available();
            while ((SizeAvail<Size) && (!Expired) && (LastError==0))
            {
                
                cnt++;
                try {
                    Thread.sleep(1);
                } 
                catch (InterruptedException ex) {
                    LastError=errTCPDataRecvTout;
                }
                SizeAvail=InStream.available();              
                Expired=cnt>Timeout;
                // If timeout we clean the buffer
                if (Expired && (SizeAvail>0) && (LastError==0))
                  InStream.read(PDU, 0, SizeAvail);
            }
        } 
        catch (IOException ex) 
        {
            LastError=errTCPDataRecvTout;
        }
        if (cnt>=Timeout)
        {
            LastError=errTCPDataRecvTout;
        }        
        return LastError;
    }
     */
}
