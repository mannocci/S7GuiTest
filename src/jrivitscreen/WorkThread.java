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
 */
package jrivitscreen;

import java.util.logging.Level;
import java.util.logging.Logger;
import com.pi4j.io.gpio.GpioController;
import com.pi4j.io.gpio.GpioFactory;
import com.pi4j.io.gpio.GpioPinDigitalInput;
import com.pi4j.io.gpio.PinPullResistance;
import com.pi4j.io.gpio.RaspiPin;
import com.pi4j.io.gpio.event.GpioPinDigitalStateChangeEvent;
import com.pi4j.io.gpio.event.GpioPinListenerDigital;

/**
 *
 * @author lucamannocci
 */
public class WorkThread extends Thread {

  private GpioController gpio;
  private GpioPinDigitalInput[] pulsanti;
  private MainJFrame mf;

  public WorkThread() {
    // create gpio controller
    gpio = GpioFactory.getInstance();
    pulsanti[0] = gpio.provisionDigitalInputPin(RaspiPin.GPIO_29, "P1", PinPullResistance.PULL_UP);
    pulsanti[1] = gpio.provisionDigitalInputPin(RaspiPin.GPIO_28, "P2", PinPullResistance.PULL_UP);
    pulsanti[2] = gpio.provisionDigitalInputPin(RaspiPin.GPIO_25, "P3", PinPullResistance.PULL_UP);
    pulsanti[3] = gpio.provisionDigitalInputPin(RaspiPin.GPIO_27, "P4", PinPullResistance.PULL_UP);
    pulsanti[4] = gpio.provisionDigitalInputPin(RaspiPin.GPIO_24, "P5", PinPullResistance.PULL_UP);
    pulsanti[5] = gpio.provisionDigitalInputPin(RaspiPin.GPIO_26, "P6", PinPullResistance.PULL_UP);
    // create and register gpio pin listener

  }

  public void set_mf(MainJFrame mf) {
    this.mf = mf;
  }
  private void send_p(String sp){
    this.mf.pulsante_hw(sp);
  }
  @Override
  public void run() {
    gpio.addListener(new GpioPinListenerDigital() {
      @Override
      public void handleGpioPinDigitalStateChangeEvent(GpioPinDigitalStateChangeEvent event) {
        // display pin state on console
        System.out.println(" Premuto: " + event.getPin() + " = " + event.getState());
        send_p(event.getPin().getName());
        
      }

    }, pulsanti);
    while (true) {
      try {

        Thread.sleep(1000);

      } catch (InterruptedException ex) {
        Logger.getLogger(WorkThread.class.getName()).log(Level.SEVERE, null, ex);
      }
    }
  }

}
