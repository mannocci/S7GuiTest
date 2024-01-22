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
import com.pi4j.io.gpio.digital.DigitalInput;
import com.pi4j.io.gpio.digital.DigitalState;
import com.pi4j.io.gpio.digital.PullResistance;
import java.util.HashMap;
import java.util.logging.Level;
import java.util.logging.Logger;


public class ButtonThread {

    private JRivitMain mf;

    private static final HashMap<String, Integer> pulsanti = new HashMap<String, Integer>() {
        {   // Vecchia configurazione
            put("PL1", 21); // Prima era RaspiPin.GPIO_29
            put("PL2", 20); // Prima era RaspiPin.GPIO_28
            put("PL3", 26); // Prima era RaspiPin.GPIO_25
            put("PR1", 16); // Prima era RaspiPin.GPIO_27
            put("PR2", 19); // Prima era RaspiPin.GPIO_24
            put("PR3", 12); // Prima era RaspiPin.GPIO_26
            /*
            // Nuova configurazione
            put("SW", 21);  // Prima era RaspiPin.GPIO_9    // Pin  5 Switch ON/OFF
            put("LED", 20); // Prima era RaspiPin.GPIO_29   // Pin 40 LED ON/OFF
            put("PL1", 21); // Prima era RaspiPin.GPIO_23   // Pin 33
            put("PL2", 20); // Prima era RaspiPin.GPIO_24   // Pin 35
            put("PL3", 26); // Prima era RaspiPin.GPIO_25   // Pin 37
            put("PL1", 16); // Prima era RaspiPin.GPIO_28   // Pin 38
            put("PL2", 19); // Prima era RaspiPin.GPIO_27   // Pin 36
            put("PL3", 12); // Prima era RaspiPin.GPIO_26   // Pin 32
             */
        }
    };

    public ButtonThread(JRivitMain mf) {
        this.mf = mf;
        Static.debug("Push button Thread started", 3);

        var pi4j = Pi4J.newAutoContext();
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
                    Static.debug(" Premuto: " + e.source().id() + " = " + e.state(), 4);
                }
            });
        });
    }
}

/*

public class ButtonThread extends Thread {

    private JRivitMain mf;

    private static final HashMap<String, Integer> pulsanti = new HashMap<String, Integer>() {
        {   // Vecchia configurazione
            put("PL1", 21); // Prima era RaspiPin.GPIO_29
            put("PL2", 20); // Prima era RaspiPin.GPIO_28
            put("PL3", 26); // Prima era RaspiPin.GPIO_25
            put("PL1", 16); // Prima era RaspiPin.GPIO_27
            put("PL2", 19); // Prima era RaspiPin.GPIO_24
            put("PL3", 12); // Prima era RaspiPin.GPIO_26
            /*
            // Nuova configurazione
            put("SW", 21);  // Prima era RaspiPin.GPIO_9    // Pin  5 Switch ON/OFF
            put("LED", 20); // Prima era RaspiPin.GPIO_29   // Pin 40 LED ON/OFF
            put("PL1", 21); // Prima era RaspiPin.GPIO_23   // Pin 33
            put("PL2", 20); // Prima era RaspiPin.GPIO_24   // Pin 35
            put("PL3", 26); // Prima era RaspiPin.GPIO_25   // Pin 37
            put("PL1", 16); // Prima era RaspiPin.GPIO_28   // Pin 38
            put("PL2", 19); // Prima era RaspiPin.GPIO_27   // Pin 36
            put("PL3", 12); // Prima era RaspiPin.GPIO_26   // Pin 32
        }
    };

    public ButtonThread(JRivitMain mf) {
        this.mf = mf;
        Static.debug("Push button Thread started", 3);

        var pi4j = Pi4J.newAutoContext();
        pulsanti.forEach((key, value) -> {  // Per ogni pulsante
            System.out.println(key + " = " + value);

            var buttonConfig = DigitalInput.newConfigBuilder(pi4j)
                    .id(key)
                    .name("Press button")
                    .address(value)
                    .pull(PullResistance.PULL_UP)
                    .debounce(3000L)
                    .provider("pigpio-digital-input");

            var button = pi4j.create(buttonConfig);

            button.addListener(e -> {
                if (e.state() == DigitalState.LOW) {
                    this.mf.pulsanteHw(e.source().id());
                    Static.debug(" Premuto: " + e.source().id() + " = " + e.state(), 4);
                }
            });
        });
        
        
    }

    @Override
    public void run() {

        for (;;) {
            try {
                Thread.sleep(1500);
            } catch (InterruptedException ex) {
                Logger.getLogger(ButtonThread.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
    }
}

*/