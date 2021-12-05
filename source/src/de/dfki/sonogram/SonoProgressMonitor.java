package de.dfki.sonogram;

import java.awt.*;
import java.awt.event.*;
import javax.swing.*;

/**
 * Copyright (c) 2001 Christoph Lauer @ DFKI, All Rights Reserved. clauer@dfki.de - www.dfki.de
 *
 * <p>This is my own ProgressBar implementation for the Sonogram main frame
 *
 * @author Christoph Lauer
 * @version 1.0, Begin 15/09/2002, Current 26/09/2002
 */
class SonoProgressMonitor extends JWindow implements Runnable {
  private JProgressBar progressbar;
  private boolean valuehaschanged = false;
  private boolean stringhaschanged = false;
  private boolean cancelispressed = false;
  private boolean endthread = false;
  private int progress;
  private String note = "Transformating";
  private Thread timeThread;
  int min;
  int max;
  Sonogram owner;
  
  int locsx;
  int locsy;

  int locpx;
  int locpy;

  int dimsx;
  int dimsy;

  int dimpx;
  int dimpy;

  int dimx;
  int dimy;

  int locx;
  int locy;

  int progressHeight = 12;
  String str;

  // ----------------------------------------------------------------------------
  public SonoProgressMonitor(Sonogram rowner, String title, String message, int rmin, int rmax) {
    owner = rowner;

    UIManager.put("ProgressBar.selectionBackground", new Color(50, 50, 70));
    UIManager.put("ProgressBar.selectionForeground", new Color(180, 180, 208));
    setBackground(new Color(50, 50, 70));
    progressbar = new JProgressBar(0, 100);
    progressbar.setToolTipText("Press with mouse on this progressbar to interrupt curent task.");
    ProgressBarUI spui = new ProgressBarUI(progressHeight);
    progressbar.setUI(spui);
    progressbar.setStringPainted(true);
    progressbar.setFont(new Font("SansSerif", 0, 10));
    progressbar.setDoubleBuffered(true);
    progressbar.setBorderPainted(false);
    MouseListener mlst =
        new MouseAdapter() {
          @Override
          public void mousePressed(MouseEvent e) {
            cancelispressed = true;
            owner.messageBox(
                    "Interrupted !", "Mouse pressed in progressbar.\n" + note + " Interrupted.", 2);
          }
        };
    progressbar.addMouseListener(mlst);

    getContentPane().add(progressbar);
    timeThread = new Thread(this);
    timeThread.setDaemon(true);
    timeThread.setPriority(Thread.MAX_PRIORITY);
    timeThread.start();

    setVisible(true);
  }
  // ----------------------------------------------------------------------------
  public void setProgress(int progresss) {
    if (progresss != progress) {
      valuehaschanged = true;
      progress = progresss;
    }
  }
  // ----------------------------------------------------------------------------
  boolean isCanceled() {
    return (cancelispressed);
  }
  // ----------------------------------------------------------------------------
  void endRun() {
    endthread = true;
  }
  // ----------------------------------------------------------------------------
  void setNote(String notes) {
    valuehaschanged = true;
    note = notes;
  }
  // ----------------------------------------------------------------------------
  void close() {
    endRun();
    timeThread.interrupt();
    timeThread = null;
    dispose();
  }
  // ----------------------------------------------------------------------------
  public void run() {
    while (!endthread) {

      if (owner.getState() == Frame.ICONIFIED) {
        setVisible(false);
        return;
      } else {
        setVisible(true);
        toFront();
      }
      locsx = (int) owner.getLocation().getX();
      locsy = (int) owner.getLocation().getY();
      locpx = (int) owner.pp.getLocation().getX();
      locpy = (int) owner.pp.getLocation().getY();
      dimsx = (int) owner.getSize().getWidth();
      dimsy = (int) owner.getSize().getHeight();
      dimpx = (int) owner.pp.getSize().getWidth();
      dimpy = (int) owner.pp.getSize().getHeight();
      locx = locsx + (dimsx - dimpx) / 2;
      locy = locsy + dimsy - progressHeight;
      if (Sonogram.javaWinDeco) locy -= 5;
      dimx = dimpx - 1;
      dimy = progressHeight;
      setSize(dimx, dimy);
      setLocation(locx, locy);
      // For Mac dim-3 -- Size
      // For mac Locx+2, locy+3

      if (valuehaschanged || progress == 100) {
        valuehaschanged = false;
        if (progress == 100) {
          str = "100% - Painting the Spectrum to the internal Image...";
        } else str = progress + "% - " + note;
        progressbar.setString(str);
        progressbar.setValue(progress);
        paint(getGraphics());
      }
      try {
        Thread.sleep(20);
      } catch (InterruptedException ie) {
        System.out.println("--> Interupt Exception in SonoProgressMonitor (Sleep)");
        Thread.currentThread().interrupt();
      }
    }
  }
  // ----------------------------------------------------------------------------
}
