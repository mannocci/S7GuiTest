/*
 * Copyright (C) 2023 Fabio, Luca
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
 * @versione 1.2 gennaio 2024
 */
package jrivitscreen;

/**
 *
 * @author Fabio, Luca
 */
import com.pi4j.Pi4J;
import com.pi4j.context.Context;
import com.pi4j.io.gpio.digital.DigitalInput;
import com.pi4j.io.gpio.digital.DigitalState;
import com.pi4j.io.gpio.digital.PullResistance;
import java.util.HashMap;

public class JButtons {

    private JRivitMain mf;

    private static final HashMap<String, Integer> pulsanti = new HashMap<String, Integer>() {
        {   
            // Nuova configurazione (PCB 1.0 con piedinatura scambiata pari/dispari)
            // Switch ON/OFF Pin 31 GPIO 6 
            // GND lato PL - Piazzola superiore di PL1
            // PL1 Pin 40 GPIO 21 (è l'ex il collegamento di massa. Collegare il pulsante sulla piazzola inferiore ed usare quella superiore come massa per tutti e tre i PL)
            // PL2 Pin 36 GPIO 16 
            // PL3 Pin 38 GPIO 20
            // GND lato PR - Pin 30 unito al Pin 29 (GPIO05) da configurare in pulldown in modo da non interferire
            // PR1 Pin 37 GPIO 26
            // PR2 Pin 35 GPIO 19
            // PR3 Pin 32 GPIO 12
            // I pin denominato sul PCB come  +LED e SW sono entrambi a massa, 
            // e si possono usare per i PL o altro.
            // put("LED", xx); // Pin xx LED ON/OFF (DA CONFIGURARE IN OUT) (ora collegato alla sequent)
            put("SW", 6);   // Pin 31 Switch ON/OFF
            put("PL1", 21); // Pin 40
            put("PL2", 16); // Pin 36
            put("PL3", 20); // Pin 38
            put("PR1", 26); // Pin 37
            put("PR2", 19); // Pin 35
            put("PR3", 12); // Pin 32
//            put("PR3", 5);  // Pin 29
        }
    };
    public Context pi4j;

    public JButtons(JRivitMain mf) {
        this.mf = mf;
        Static.debug("Push button Thread started", 3);
        try {
            pi4j = Pi4J.newAutoContext();
            pulsanti.forEach((key, value) -> {  // Per ogni pulsante

                var buttonConfig = DigitalInput.newConfigBuilder(pi4j)
                        .id(key)
                        .name(key)
                        .address(value)
                        .pull(PullResistance.PULL_UP)
                        .debounce(3000L)
                        .provider("pigpio-digital-input");

                var button = pi4j.create(buttonConfig);

                button.addListener(e -> {
                    if (e.state() == DigitalState.LOW) {
                        this.mf.pulsanteHw(e.source().id());
                        Static.debug("Premuto: " + e.source().id() + " = " + e.state(), 4);
                    }
                });
            });
        } catch (Exception e) {
            Static.debug("Error configuring pushbutton listener: " + e.toString(), 2);
        }
    }
}
