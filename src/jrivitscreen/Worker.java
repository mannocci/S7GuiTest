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
import java.util.Date;
import java.util.TimeZone;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author lucamannocci
 */
public class Worker extends SwingWorker<String, Object> {

  JRivitMain mf;
  WorkThread wt;
  ButtonThread bt;
  private String operation="";

  DateFormat dateFormat;
  Calendar now;

  Worker(JRivitMain mf) {
    try {
      this.mf = mf;
      this.wt = new WorkThread();
      this.wt.set_mf(this.mf);
      this.wt.start();
      this.bt = new ButtonThread();
      this.bt.set_mf(mf);
      this.bt.start();
      
      dateFormat = new SimpleDateFormat("yyyy/MM/dd HH:mm");
      now = Calendar.getInstance();
    } catch (Exception ex) {
      Logger.getLogger(JRivitMain.class.getName()).log(Level.SEVERE, null, ex);
      throw new UnsupportedOperationException("Not supported yet."); // Generated from nbfs://nbhost/SystemFileSystem/Templates/Classes/Code/GeneratedMethodBody
    }
  }

  @Override
  protected String doInBackground() throws Exception {
    try {
      switch (this.operation) {
        case "start":
          Date orario = now.getTime();
          //this.dateFormat.setTimeZone(TimeZone.getTimeZone("Europe/London"));
          this.mf.set_jLabel_B_L(this.dateFormat.format(orario));
          this.mf.repaint();
          break;

      }
    } catch (Exception ex) {
      Logger.getLogger(JRivitMain.class.getName()).log(Level.SEVERE, null, ex);
      throw new UnsupportedOperationException("Not supported yet."); // Generated from nbfs://nbhost/SystemFileSystem/Templates/Classes/Code/GeneratedMethodBody
    }
    return "ok";
  }

  public void set_operation(String operation) {
    this.operation = operation;
  }
}
