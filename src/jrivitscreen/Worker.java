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

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import javax.swing.SwingWorker;
import java.time.LocalDate;
import java.util.Calendar;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author lucamannocci
 */
public class Worker extends SwingWorker<String, Object> {

  MainJFrame mf;
  WorkThread wt;
  private String operation;
  DateFormat dateFormat;
  Calendar now;

  Worker(MainJFrame mf) {
    try {
      this.mf = mf;
      this.wt = new WorkThread();
      this.wt.set_mf(this.mf);
      dateFormat = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");
      now = Calendar.getInstance();
    } catch (Exception ex) {
      Logger.getLogger(JRivitScreen.class.getName()).log(Level.SEVERE, null, ex);
      throw new UnsupportedOperationException("Not supported yet."); // Generated from nbfs://nbhost/SystemFileSystem/Templates/Classes/Code/GeneratedMethodBody
    }
  }

  @Override
  protected String doInBackground() throws Exception {
    try {
      switch (this.operation) {
        case "start":
          this.mf.setjLabel_B_1(now.getTime().toString());
          this.mf.repaint();
          break;
      }
    } catch (Exception ex) {
      Logger.getLogger(JRivitScreen.class.getName()).log(Level.SEVERE, null, ex);
      throw new UnsupportedOperationException("Not supported yet."); // Generated from nbfs://nbhost/SystemFileSystem/Templates/Classes/Code/GeneratedMethodBody
    }
    return "ok";
  }

  public void set_operation(String operation) {
    this.operation = operation;
  }
}
