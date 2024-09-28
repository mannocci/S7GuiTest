/*
 * Copyright (C) 2023 luca
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
 * Classe che contiene la variabili statiche in comune con i sorgenti.
 * La classe va aggionata anche nel progetto JRivitControl
 */
package jrivitscreen;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

/**
 * classe che contiene le variabili static final per i programmi come i nomi dei
 * file o di stati del sistema numerici
 *
 * @author luca
 */
public class Static {

    //Variabili final (non modificabili)
    // Richieste
    final static String RICHIESTA_STOP_LAVORO = "77";
    final static String RICHIESTA_STOP_WL = "78";
    final static String RICHIESTA_AVVIO_WL = "79";
    final static String RICHIESTA_AVVIO_LAVORO = "80";
    final static String RICHIESTA_PAUSA = "81";
    final static String RICHIESTA_STOP = "82";
    final static String RICHIESTA_CALIBRAZIONE = "83";
    final static String RICHIESTA_CALIBRAZIONE_TEST = "84";
    final static String RICHIESTA_RIAVVIO = "85";
    final static String RICHIESTA_RIAVVIO_WL = "185";
    final static String RICHIESTA_CALIBRAZIONE_SALVA = "86";
    final static String RICHIESTA_CALIBRAZIONE_ANNULLA = "87";
    final static String RICHIESTA_RESET_SYSTEM = "88";
    final static String RICHIESTA_BACKUP_DB = "89";
    final static String RICHIESTA_RESTORE_DB = "90";
    final static String RICHIESTA_IMPOSTA_TOOL = "91";
    final static String RICHIESTA_AGGIORNA_INFO = "92";
    final static String RICHIESTA_AGGIORNA_WIFI_STATUS = "93";
    final static String RICHIESTA_RESET_WL = "94";
    final static String RICHIESTA_RESET_WORK = "95";
    // Stati
    final static String STATO_AVVIATA_WL = "90";
    final static String STATO_AVVIATO_W = "91";
    // final static String STATO_PAUSA = "92"; // Non è uno stato del device, quindi non va gestito
    final static String STATO_STOP = "93";
    final static String STATO_MANUTENZIONE = "94";
    final static String STATO_BLOCCO = "95";
    final static String STATO_CONCLUSO = "100";
    final static String STATO_CONCLUSA_WL = "101";
    final static String STATO_CALIBRAZIONE = "200";
    final static String STATO_CALIBRAZIONE_TEST = "220";

    // Risposte
    final static String CONTINUA = "51";
    final static String ACCETTA = "52";
    final static String ANNULLA = "53";

    final static String ARIA_CHIUSA = "0";
    final static String ARIA_APERTA = "1";

    // Nomi dei file
    final static String F_STATO = "stato";
    final static String F_SONO_IN = "sono_in";
    final static String F_RESET_REQUEST = "reset_request";
    final static String F_RICHIESTA = "richiesta";
    final static String F_PULSANTE = "pulsante";
    final static String F_W = "w.txt";
    final static String F_W_SCELTO = "w_scelto";
    final static String F_WL_LISTA = "wl_lista.txt";
    final static String F_WL = "wl.txt";
    final static String F_WL_SCELTA = "wl_scelta";
    final static String F_TOOLS = "tools.txt";
    final static String F_TOOL_SCELTO = "tool_scelto";
    final static String F_WL_RUNNING = "wl_running";//Contiene il lavoro avviato e l'indice dell'elenco "nome lavoro§n"
    final static String F_RISPOSTA_TIRO_ERRATO = "risposta_tiro_errato";
    final static String F_RISPOSTA_TIRO_ERRATO_CONTINUA = "risposta_tiro_errato_continua";
    final static String F_RISPOSTA_TIRO_ERRATO_ACCETTA = "risposta_tiro_errato_accetta";
    final static String F_RISPOSTA_TIRO_ERRATO_ANNULLA = "risposta_tiro_errato_annulla";
    final static String F_AGGIORNATO_LAVORI = "aggiornato_lavori";
    final static String F_AGGIORNA_WIFI = "aggiorna_wifi";
    final static String F_RELOAD = "reload";
    final static String F_LAVORO_AVVIATO = "lavoro_avviato";
    final static String F_LAVORO_PRONTO = "lavoro_pronto";
    final static String F_WL_PRONTA = "wl_pronta";
    final static String F_PRESSIONE_ARIA_IN_MIN = "pressione_aria_in_min";
    final static String F_PRESSIONE_ARIA_IN_MAX = "pressione_aria_in_max";
    final static String F_NO_SENSORE = "no_sensore";
    final static String F_CONTATORI = "contatori";
    final static String F_TIRI_NEL_LOTTO = "tiri_nel_lotto";
    final static String F_TIRI_OK = "tiri_ok";
    final static String F_TIRI_ERRATI = "tiri_errati";
    final static String F_TIRI_ANNULLATI = "tiri_annullati";
    final static String F_INFO = "info.txt";
    final static String F_SENSORI = "sensori";
    final static String F_WARNING = "warning.txt";
    final static String F_STATUS_LAN = "status_lan";
    final static String F_STATUS_WIFI = "status_wifi";
    final static String F_NOME_DEVICE = "nome_device";
    final static String F_UM = "um"; // Bar o Newton
    final static String F_ARIA = "aria";
    final static String F_ERRORE = "errore";
    final static String F_POSIZIONE_ERRORI = "posizione_errore";
    final static String F_CURVA = "curva";
    final static String F_ESITO_TIRO = "esito_tiro";
    final static String F_CURVA_DI_RIFERIMENTO = "curva_di_riferimento";
    final static String F_PICCO = "picco";
    final static String F_PICCORIF = "piccorif";
    final static String F_ABILITA_CALIBRAZIONE = "abilita_calibrazione";
    final static String F_CONFERMA_RISP_ERRORE = "conferma_risp_errore";
    final static String F_CONFERMA_STOP_PAUSA = "conferma_stop_pausa";
    final static String F_LOTTI_OK = "lotti_ok";
    final static String F_SESSIONE = "sessione";
    final static String F_LISTA_NM_CON = "lista_nm_con";
    final static String F_NM_CON = "nm_con";
    final static String F_NOME_CON = "nome_con";
    final static String F_INTERNET_STATUS = "internet_status";
    final static String F_CONTROLLER_ONLINE = "controller_online";
    final static String F_POWEROFF = "poweroff";
    final static String F_REBOOT = "reboot";
    final static String F_FIRST_TIME = "first_time";
    final static String F_SCREEN_VERSION = "screen_version";
    final static String F_SYSTEM_FREEZE = "system_freeze";
    final static String F_SYSTEM_LOCK_EMERGENCY = "system_lock_emergency";
    final static String F_SYSTEM_RETURN_OK = "system_return_ok";
    final static String F_GPIO21 = "GPIO21";
    final static String F_GPIO16 = "GPIO16";
    final static String F_GPIO20 = "GPIO20";
    final static String F_GPIO26 = "GPIO26";
    final static String F_GPIO19 = "GPIO19";
    final static String F_GPIO12 = "GPIO12";
    
    static String PATH_WATCH = "/tmp/CT/";
    static String PATH_LCK = "/tmp/CTLock/";
    static String PATH_TASTI = "/tmp/CTTASTI/";
    static long ATTESA_SCRITTURA_FILE = 1000L;
    // da utilizzare per impostare il livello di debug
    // 0:debug disattivato 1:errore 2:warning 3:info 4:verbose
    static int DEBUGLEVEL = 3;
    static boolean VMMODE = false;
    static float MAX_VARIAZIONE_PRESSIONE = 9.0f;   // Massima variazione di pressione ammessa da una lettura all'altra

//    final public static DateTimeFormatter dtf = DateTimeFormatter.ofPattern("dd/MM/yy HH:mm:ss.SSS");  // per i messaggi di log
    final public static DateTimeFormatter dtf = DateTimeFormatter.ofPattern("dd/MM/yy HH:mm:ss");  // per i messaggi di log

    /**
     * Visualizza informazioni di debug.
     *
     * @param string stringa da visualizzare
     * @param livello livello di debug (1:errore 2:warning 3:info 4:verbose)
     */
    static void debug(String string, int livello) {
        if (livello <= Static.DEBUGLEVEL) {
            System.out.println(dtf.format(LocalDateTime.now()) + ": " + string);
        }
    }

    static void setDEBUG_LEVEL(String optionValue) {
        try {
            Static.DEBUGLEVEL = Integer.parseInt(optionValue);
        } catch (NumberFormatException e) {
            System.out.println("Invalid parameter -v " + optionValue);
        }
    }

    static void setVMMODE(boolean vmMode) {
        Static.VMMODE = vmMode;
    }

    public Static() {
        System.out.print("Istanziato variabili static e static final\n");
    }

    public static void setPATH_WATCH(String PATH_WATCH) {
        Static.PATH_WATCH = PATH_WATCH;
    }

    public static void setPATH_LCK(String PATH_LCK) {
        Static.PATH_LCK = PATH_LCK;
    }
}
