/*
 * Copyright (C) 2023 adminsb
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
 */
package jrivitscreen;

/**
 *
 * @author adminsb
 */
import com.pi4j.io.gpio.GpioController;
import com.pi4j.io.gpio.GpioFactory;
import com.pi4j.io.gpio.GpioPinDigitalInput;
import com.pi4j.io.gpio.PinPullResistance;
import com.pi4j.io.gpio.RaspiPin;
import com.pi4j.io.gpio.event.GpioPinDigitalStateChangeEvent;
import com.pi4j.io.gpio.event.GpioPinListenerDigital;
import java.io.File;
import java.io.FileNotFoundException;
import java.util.Scanner;
import java.util.logging.Level;
import java.util.logging.Logger;

public class ButtonThread extends Thread {

    private JRivitMain mf;

    public ButtonThread() {
        System.out.println("Push button Thread");
        // create gpio controller
        final GpioController gpio = GpioFactory.getInstance();
        final GpioPinDigitalInput[] pulsanti = {
            gpio.provisionDigitalInputPin(RaspiPin.GPIO_29, "PL1", PinPullResistance.PULL_UP),
            gpio.provisionDigitalInputPin(RaspiPin.GPIO_28, "PL2", PinPullResistance.PULL_UP),
            gpio.provisionDigitalInputPin(RaspiPin.GPIO_25, "PL3", PinPullResistance.PULL_UP),
            gpio.provisionDigitalInputPin(RaspiPin.GPIO_27, "PR1", PinPullResistance.PULL_UP),
            gpio.provisionDigitalInputPin(RaspiPin.GPIO_24, "PR2", PinPullResistance.PULL_UP),
            gpio.provisionDigitalInputPin(RaspiPin.GPIO_26, "PR3", PinPullResistance.PULL_UP)
        };
        // gpio pin #02 as an input pin with its internal pull down resistor enabled
//        final GpioPinDigitalInput myButton = gpio.provisionDigitalInputPin(RaspiPin.GPIO_02, PinPullResistance.PULL_DOWN);
        // create and register gpio pin listener
        gpio.addListener(new GpioPinListenerDigital() {
            @Override
            public void handleGpioPinDigitalStateChangeEvent(GpioPinDigitalStateChangeEvent event) {
                // display pin state on console
                String NomePulsante = event.getPin().getName().substring(0,3); 
                read_pulsane_premuto(NomePulsante,""+event.getState());
                //System.out.println(" Premuto: " + NomePulsante+ " = " + event.getState());
            }

        }, pulsanti);        

    }

    public void set_mf(JRivitMain mf) {
        this.mf = mf;
    }

    private void send_p(String sp) {
        this.mf.pulsante_hw(sp);
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
    private void read_pulsane_premuto(String nomeFile, String Stato) {
        if (Stato.equalsIgnoreCase("LOW")) {
            //System.out.printf("il pulsante %s è stato premuto\n", fileName);
            this.send_p(nomeFile);
        }
    }    
}
