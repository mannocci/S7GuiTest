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

import java.util.concurrent.locks.ReentrantReadWriteLock;

/**
 * classe che contiene le variabili static final per i programmi come i nomi dei
 * file o di stati del sistema numerici
 *
 * @author luca
 */
public class Static {

    //Variabili final (non modificabili)
    final static String STATO_AVVIATO = "10";
    final static String STATO_CONCLUSO = "12";
    final static String STATO_PAUSA = "13";
    final static String STATO_STOP = "14";
    final static String CONTINUA = "1";
    final static String ACCETTA = "2";
    final static String ANNULLA = "3";
    final static String ARIA_CHIUSA = "0";
    final static String ARIA_APERTA = "1";

    // Nomi dei file
    final static String F_STATO = "stato";
    final static String F_AGGIORNATO_STATO = "aggiornato_stato";
    final static String F_LAVORO_SCELTO = "lavoro_scelto";
    final static String F_RISPOSTA_TIRO_ERRATO = "risposta_tiro_errato";
    final static String F_RISPOSTA_TIRO_ERRATO_CONTINUA = "risposta_tiro_errato_continua";
    final static String F_RISPOSTA_TIRO_ERRATO_ACCETTA = "risposta_tiro_errato_accetta";
    final static String F_RISPOSTA_TIRO_ERRATO_ANNULLA = "risposta_tiro_errato_annulla";
    final static String F_CHIEDE_CONFERMA_NO = "chiedi_conferma_no";
    final static String F_IN_PAUSA = "in_pausa";
    final static String F_IN_STOP = "in_stop";
    final static String F_LAVORI = "lavori.txt";
    final static String F_AGGIORNATO_LAVORI = "aggiornato_lavori";
    final static String F_LAVORO_CONCLUSO = "lavoro_concluso";
    final static String F_LAVORO_AVVIATO = "lavoro_avviato";
    final static String F_CONTATORI_AGGIORNATI = "contatori_aggiornati";
    final static String F_CONTATORI = "contatori";
    final static String F_TIRI_NEL_LOTTO = "tiri_nel_lotto";
    final static String F_TIRI_OK = "tiri_ok";
    final static String F_TIRI_ERRATI = "tiri_errati";
    final static String F_TIRI_ANNULLATI = "tiri_annullati";
    final static String F_INFO = "info.txt";
    final static String F_SENSORI = "sensori";
    final static String F_WARNING = "warning.txt";
    final static String F_SETUP_LAN = "setup_lan.txt";
    final static String F_SETUP_WIFI = "setup_wifi.txt";
    final static String F_NOME_DEVICE = "nome_device";
    final static String F_ARIA = "aria";
    final static String F_ERRORE = "errore";
    final static String F_CURVA = "curva";
    final static String F_CHIEDI_CONFERMA_NO = "chiedi_conferma_no";
    final static String F_CHIEDI_CONFERMA_STOP = "chiedi_conferma_stop";
    final static String F_LOTTI_OK = "lotti_ok";
    final static String F_SESSIONE = "sessione";
    static final String PATH_WATCH = "/tmp/CT/";
    static long ATTESA_SCRITTURA_FILE = 1000L;

    public Static() {
        System.out.print("Istanziato variabili static e static final\n");
    }

}
