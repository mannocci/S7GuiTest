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
    final static String RICHIESTA_AVVIO = "80";
    final static String RICHIESTA_PAUSA = "81";
    final static String RICHIESTA_STOP = "82";
    final static String RICHIESTA_CALIBRAZIONE = "83";
    final static String RICHIESTA_CALIBRAZIONE_TEST = "84";
    final static String RICHIESTA_RIAVVIO = "85";
    final static String STATO_AVVIATO = "91";
    final static String STATO_CONCLUSO = "100";
    final static String STATO_CALIBRAZIONE = "200";
    final static String STATO_CALIBRAZIONE_TEST = "220";
    final static String STATO_PAUSA = "92";
    final static String STATO_STOP = "93";
    final static String CONTINUA = "51";
    final static String ACCETTA = "52";
    final static String ANNULLA = "53";
    final static String ARIA_CHIUSA = "0";
    final static String ARIA_APERTA = "1";

    // Nomi dei file
    final static String F_STATO = "stato";
    final static String F_RICHIESTA = "richiesta";
    final static String F_PULSANTE = "pulsante";
    final static String F_LAVORO_SCELTO = "lavoro_scelto";
    final static String F_RISPOSTA_TIRO_ERRATO = "risposta_tiro_errato";
    final static String F_RISPOSTA_TIRO_ERRATO_CONTINUA = "risposta_tiro_errato_continua";
    final static String F_RISPOSTA_TIRO_ERRATO_ACCETTA = "risposta_tiro_errato_accetta";
    final static String F_RISPOSTA_TIRO_ERRATO_ANNULLA = "risposta_tiro_errato_annulla";
    final static String F_CHIEDE_CONFERMA_NO = "chiedi_conferma_no";

    final static String F_AGGIORNATO_LAVORI = "aggiornato_lavori";
    final static String F_LAVORI = "lavori.txt";
    final static String F_RELOAD = "reload";
    final static String F_LAVORO_AVVIATO = "lavoro_avviato";
    final static String F_LAVORO_PRONTO = "lavoro_pronto";
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
    final static String F_IN_PAUSA = "in_pausa";

    final static String F_NOME_DEVICE = "nome_device";
    final static String F_ARIA = "aria";
    final static String F_ERRORE = "errore";
    final static String F_CURVA = "curva";
    final static String F_CURVA_DI_RIFERIMENTO = "curva_di_riferimento";
    final static String F_CHIEDI_CONFERMA_NO = "chiedi_conferma_no";
    final static String F_CHIEDI_CONFERMA_STOP = "chiedi_conferma_stop";
    final static String F_LOTTI_OK = "lotti_ok";
    final static String F_SESSIONE = "sessione";
    final static String F_LISTA_NM_CON = "lista_nm_con";
    final static String F_POWEROFF = "poweroff";
    static String PATH_WATCH = "/tmp/CT/";
    static String PATH_LCK = "/tmp/CTLock/";
    static long ATTESA_SCRITTURA_FILE = 1000L;
    // da utilizzare per impostare il livello di debug
    // 0:debug disattivato 1:errore 2:warning 3:info 4:verbose
    final private static int DEBUGLEVEL = 3;
    static float MAX_VARIAZIONE_PRESSIONE = 4.0f;

    final private static DateTimeFormatter dtf = DateTimeFormatter.ofPattern("dd/MM/yy HH:mm:ss.SSS");  // per i messaggi di log

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
