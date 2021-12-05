package de.dfki.sonogram;

import java.awt.*;
import java.awt.dnd.*;
import java.awt.event.*;
import java.awt.image.BufferedImage;
import javax.swing.*;
import javax.swing.border.*;

/**
 * Copyright (c) 2001 Christoph Lauer @ DFKI, All Rights Reserved. clauer@dfki.de - www.dfki.de
 *
 * <p>This class presents the Paint Window to display a Sonogram Picture. It is a generic Component
 * and implements the
 *
 * @author Christoph Lauer
 * @version 1.6, Current 10/07/2002
 */
public class PaintPanel extends JPanel implements MouseMotionListener {
  int onespek = 0;
  short panelWidth = 0;
  short panelHeight = 0;
  int wnp = 0; // Window Number Position for paintOneSpectrum in units
  int wsp = 0; // Window Size position for paintOneSpectrum in units
  double wnf = 0.0; // self as wnp from 0..1
  double wsf = 0.0; // self as wsp from 0..1
  int mousex = 0;
  int mousey = 0;
  Color colorPurple = new Color(50, 50, 90);
  Color colorRed = new Color(200, 0, 0);
  Color colorLavender = new Color(150, 150, 200);
  Color colorWhite = new Color(255, 255, 255);
  Color colorBlack = new Color(0, 0, 0);
  Color colorDarkGreen = new Color(15, 50, 20);
  Color colorNeonGreen = new Color(220, 255, 0);
  Color colorDimNeonGreen = new Color(180, 205, 0);
  public double plstart = 0.0; // Playstart
  public double plstop = 0.0; // Playstop
  double plbegin = 0.0;
  public double plbutton = 0.0;
  private Sonogram refToSonogram;
  Color[] coFI = FireColor.getFireColorArray();
  Color[] coFIi = new Color[256];
  Color[] cocFI = new Color[256];
  Color[] cocFIi = new Color[256];
  Color[] coCO = new Color[256];
  Color[] coCOi = new Color[256];
  Color[] coRA = RainbowColor.getRainbowColorArray();
  Color[] coRAi = new Color[256];
  Color[] coCG = new Color[256];
  Color[] coCGi = new Color[256];
  Color[] coSW = new Color[256];
  Color[] coSWi = new Color[256];
  Dimension olddimension;
  BufferedImage doublebufferimage;
  Graphics2D g;
  Image defaultimage;
  final int defaultTimeout = ToolTipManager.sharedInstance().getInitialDelay();

  AlphaComposite compositeNebular = AlphaComposite.getInstance(AlphaComposite.SRC_OVER, 0.75f);
  AlphaComposite compositeAl = AlphaComposite.getInstance(AlphaComposite.SRC_OVER, 0.6f);
  AlphaComposite compositeAl1 = AlphaComposite.getInstance(AlphaComposite.SRC_OVER, 0.2f);
  AlphaComposite compositeAl2 = AlphaComposite.getInstance(AlphaComposite.SRC_OVER, 0.9f);
  AlphaComposite compositeGr = AlphaComposite.getInstance(AlphaComposite.SRC_OVER, 0.4f);
  AlphaComposite compositeFo = AlphaComposite.getInstance(AlphaComposite.SRC_OVER, 0.05f);
  AlphaComposite compositeNo = AlphaComposite.getInstance(AlphaComposite.SRC);
  // -------------------------------------------------------------------------------------------------------------------------
  /** Overrides default update Function which not clear Screen. */
  @Override
  public void update(Graphics g) {
    paint(g);
  }
  // -------------------------------------------------------------------------------------------------------------------------
  /** Constructor for PaintPanel. Here are Colorarrays generated and Border set. */
  public PaintPanel(Sonogram ref) {

    refToSonogram = ref;
    setBorder(new javax.swing.border.EtchedBorder(javax.swing.border.EtchedBorder.LOWERED));
    addMouseMotionListener(this);
    int r = 0, g = 0, b = 0, s = 0;
    setBorder(BorderFactory.createEmptyBorder());

    // generate the color arrays
    for (int i = 0; i < 256; i++) { // building Colorarrays

      // generate classical fire Colors
      if (i <= 90) {
        r = 120 - (int) (120.0 * ((double) i / 90.0));
        g = 160 - (int) (160.0 * ((double) i / 90.0));
        b = 207;
      }
      if (i > 90) g = (int) (255.0 * ((double) (i - 90) / 165.0));
      if (i > 100 && i <= 150) r = (int) (255.0 * ((double) (i - 100) / 50.0));
      if (i > 150) {
        r = 255;
        b = 0;
      }
      if (i > 90 && i <= 150) r = (int) (255.0 * ((double) (i - 90) / 60.0));
      cocFI[i] = new Color(r, g, b);
      cocFIi[255 - i] = new Color(255 - r, 255 - g, 255 - b);

      // generate modern fire colors
      coFIi[i] = new Color(255 - r, 255 - g, 255 - b);

      // generate rainbow Colors
      r = i;
      g = (255 - Math.abs(i - 120) * 4);
      if (g < 0) g = 0;
      b = (255 - Math.abs(i - 60) * 4);
      if (b < 0) b = 0;
      if (i > 210) b = (210 - i) * (-5);
      // coRA[i]  = new Color(r,g,b);
      coRAi[255 - i] = new Color(255 - r, 255 - g, 255 - b);

      // generate normal Colors
      g = (i - 128) * 2;
      if (g < 0) g = 0;
      coCO[i] = new Color(r, g, b);
      coCOi[255 - i] = new Color(255 - r, 255 - g, 255 - b);

      // purpur purple
      double x = (double) i / 256.0;
      double[] c = new double[3];
      if (x < .13) c[0] = 0;
      else if (x < .73) c[0] = 1 * Math.sin((x - .13) / .60 * Math.PI / 2);
      else c[0] = 1;
      if (x < .60) c[1] = 0;
      else if (x < .91) c[1] = 1 * Math.sin((x - .60) / .31 * Math.PI / 2);
      else c[1] = 1;
      if (x < .60) c[2] = .5 * Math.sin((x - .00) / .60 * Math.PI);
      else if (x < .78) c[2] = 0;
      else c[2] = (x - .78) / .22;
      coCG[i] = new Color((int) (c[0] * 255.0), (int) (c[1] * 255.0), (int) (c[2] * 255.0));
      // coCGi[i] = new Color((int)((double)i/1.5), (int)((double)i/1.5), i);
      coCGi[255 - i] =
          new Color(
              255 - (int) (c[0] * 255.0), 255 - (int) (c[1] * 255.0), 255 - (int) (c[2] * 255.0));

      // Generate Black/White Colors
      coSWi[i] = new Color(i, i, i);
      coSW[i] = new Color(255 - i, 255 - i, 255 - i);
    }

    java.net.URL imurl = this.getClass().getResource("BigSplash.png");
    defaultimage = new ImageIcon(imurl).getImage();
    setDropTarget(new DropTarget(refToSonogram, new DnDHandler(refToSonogram)));
    MouseListener mlst =
        new MouseAdapter() {
          public void mousePressed(MouseEvent e) {
            if (e.getClickCount() == 2 && refToSonogram.spectrumExist && !refToSonogram.openingflag) {
              System.out.println("--> FULLSCREEN");
              refToSonogram.fulItem.doClick();
            }
            int x = e.getX();
            if ((x > 40 && x < (panelWidth - 60))) {
              double pos = (double) (x - 40) / (double) (panelWidth - 100);
              plstart = plstop = plbegin = plbutton = pos;
              paintTimeSlider(null, true);
            }
          }
          /*public void mouseEntered(MouseEvent e)
          {
            ToolTipManager.sharedInstance().setInitialDelay(0);
          }
          public void mouseExited (MouseEvent e)
          {
            ToolTipManager.sharedInstance().setInitialDelay(defaultTimeout);
          }*/
        };
    addMouseListener(mlst);
  }
  // -------------------------------------------------------------------------------------------------------------------------
  /**
   * Eventhandling for mouse move events. as defined in Interface of the MouseMotionListener
   *
   * @param e Mouseevent
   */
  public void mouseMoved(MouseEvent e) {
    int x = mousex = e.getX();
    int y = mousey = e.getY();
    if ((x > 39 && x < (panelWidth - 60)) && (y < panelHeight - 70)) {
      wnf = (double) (x - 39) / (double) (panelWidth - 101); // fractional X position in the spectrogram from the left
      wsf = (double) (y) / (double) (panelHeight - 70); // fractional Y position in the spectrogram from the top
      wnp = (int) ((double) refToSonogram.spectrum.size() * wnf); // time position in samples
      wsp = (int) ((double) (refToSonogram.timeWindowLength / 2) * wsf); // frequency position in Hz
      paintOneSpectrum(false);
    }
  }
  // -------------------------------------------------------------------------------------------------------------------------
  /**
   * Set function implements the Interface function from MouseMotionListener. Is called when
   * mousebutton is pressed and mouse moved sideways inside the spectrogram panel.
   */
  public void mouseDragged(MouseEvent e) {
    int x = e.getX();
    if ((x > 40 && x < (panelWidth - 60))) {
      double pos = (double) (x - 40) / (double) (panelWidth - 100);
      if (pos < plbegin) {
        plstart = pos;
        plbutton = pos;
        plstop = plbegin;
      }
      if (pos > plbegin) {
        plstart = plbegin;
        plbutton = plbegin;
        plstop = pos;
      }
      refToSonogram.selectedstart =
          refToSonogram.selectedstartold + plstart * refToSonogram.selecedwidthold;
      refToSonogram.selecedwidth = refToSonogram.selecedwidthold * (plstop - plstart);
      paintTimeSlider(null, true);
      if (refToSonogram.infovisible) refToSonogram.infod.update();

      updateWvButton();
    }
    mouseMoved(e);
  }

  // ------------------------------------------------------------------------------------------------

  public void updateWvButton() {
    int len = (int) ((double) refToSonogram.selecedwidth * (double) refToSonogram.samplesAll);
    refToSonogram.wvconfig.setSelectedSamples(len);
    if (len <= 32768 && refToSonogram.spectrumExist) {
      refToSonogram.wvbutton.setEnabled(true);
      refToSonogram.wvbutton.setBorder(BorderFactory.createLineBorder(new Color(90, 90, 160)));
      int powtwo = getNextPowerOfTwo(len);
      int ld = (int) (Math.log(powtwo) / Math.log(2.0));
      refToSonogram.wvconfig.sliderx.setValue(ld);
    } else {
      refToSonogram.wvbutton.setEnabled(false);
      refToSonogram.wvbutton.setBorder(BorderFactory.createCompoundBorder());
    }
  }

  // ------------------------------------------------------------------------------------------------

  private int getNextPowerOfTwo(int value) {
    // calc the next power of two
    int n = 1;
    while (n < value) {
      n = n << 1; // the short form for the power of two
    }
    return n;
  }

  // -------------------------------------------------------------------------------------------------------------------------
  /**
   * Updates the Sliderpicture at bottom.
   *
   * @param g Graphics-Object to paint up. Is g = null so this Method recive it from "this"
   */
  public void paintTimeSlider(Graphics g, boolean shake) {
    if (refToSonogram.spectrumExist == true) {
      int xb = (int) (plstart * (double) (panelWidth - 100)) + 40; //
      int xe = (int) (plstop * (double) (panelWidth - 100)) + 40; //
      int xl;
      if (plbutton < plstop) xl = (int) (plbutton * (double) (panelWidth - 100)) + 38; //
      else xl = (int) (plstop * (double) (panelWidth - 100)) + 38; //
      if (g == null) g = this.getGraphics();

      g.setColor(colorPurple);
      g.fillRect(38, panelHeight - 70, panelWidth - 96, 10); // Delete old one
      g.setColor(new Color(125, 141, 92));
      g.fillRect(xb, panelHeight - 67, xe - xb, 4); // inner Rect in middle
      g.setColor(new Color(210, 255, 40));
      g.drawRect(xb, panelHeight - 68, xe - xb, 5); // Rect in middle
      g.drawRect(xl, panelHeight - 70, 4, 9); // Button
      g.drawLine(xe, panelHeight - 70, xe, panelHeight - 61); // Line at end

      if (xb == 40 && panelWidth - xe == 60) {
        refToSonogram.zinbutton.setEnabled(false);
        refToSonogram.revbutton.setEnabled(false);
      } else {
        refToSonogram.zinbutton.setEnabled(true);
        refToSonogram.revbutton.setEnabled(true);
        if (xe - xb > 10) // paint only if the selected length is more than 10px
        if (shake == true) refToSonogram.shakeButtons();
      }
    }
  }

  // ------------------------------------------------------------------------------------------------------------------------
  /**
   * Paints the Single Frequency View and its smooth-line. Is called from mouseMoved and paints its
   * Values also.
   *
   * @param calledfromplaying If Funtion is called from Playing, specposition is getted from
   *     plbutton variable.
   */
  public void paintOneSpectrum(boolean calledfromplaying) {
    if (refToSonogram.spectrumExist && !refToSonogram.openingflag && !refToSonogram.transformflag) {
      Graphics2D g = (Graphics2D) this.getGraphics();
      double ffakt = (double) (panelHeight - 70) / (double) (refToSonogram.timeWindowLength / 2);
      float peakamp = 0.0f;
      float peakfrequ = 0.0f;
      float mousefrequ = 0.0f;
      float mousetime = 0.0f;
      float mouseamp = 0.0f;
      float[] spectrumBuffer;
      float[] orginalbuff;
      int peakplace = 0;
      
      // For Logarithm Frequencyview
      double powfact = 0.0; // Logarithm Scale fact
      int yLogPixelPos = 0; // Logarithmy Position
      int diffylog = 0;

      if (refToSonogram.gad.cantialise.isSelected())
        g.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
      
      // Paint Spectrum
      // Background Rectangle for Single Spectrum view
      g.setColor(colorDarkGreen);
      g.fillRect(panelWidth - 55, 0, 53, panelHeight - 70); 

      // Draw Horizontal Grid Lines
      g.setColor(colorPurple);
      for (double yLinearTick = 0; yLinearTick < (panelHeight - 70); yLinearTick += (panelHeight - 70) / 10.0) {
        // logarithm Frequency
        if (refToSonogram.gad.cslogfr.isSelected()) {
          double ymax = (panelHeight - 70 + 1); // the max value
          double yni = ymax - yLinearTick; // y not inverse
          double ylog = Math.log(yni) / Math.log(ymax) * ymax; // calc log scale
          yLogPixelPos = (int) (ymax - ylog); // reinverse
          if (yLogPixelPos > 2) g.drawLine(panelWidth - 60, yLogPixelPos, panelWidth - 3, yLogPixelPos);
        }
        // Non Logarithm
        else if (yLinearTick > 2) g.drawLine(panelWidth - 60, (int) yLinearTick, panelWidth - 3, (int) yLinearTick);
      }

      // When playing take position from plbutton
      if (calledfromplaying && refToSonogram.gad.csspecwhileplaying.isSelected()) {
        int spectimepos = (int) (plbutton * refToSonogram.spectrum.size());
        if (spectimepos < refToSonogram.spectrum.size())
          orginalbuff = refToSonogram.spectrum.get(spectimepos);
        else
          orginalbuff = refToSonogram.spectrum.get(refToSonogram.spectrum.size() - 1);
      } else {
        if (wnp >= refToSonogram.spectrum.size()) wnp = refToSonogram.spectrum.size() - 1;
        if (wnp < 0) wnp = 0;
        orginalbuff = refToSonogram.spectrum.get(wnp);
      }

      // Normalize the spectrum if enabled
      if (refToSonogram.gad.cback.isSelected()) {
        float peak = 0.0f;
        spectrumBuffer = new float[refToSonogram.timeWindowLength / 2];
        for (int i = 0; i < (refToSonogram.timeWindowLength / 2); i++)
          if (peak < orginalbuff[i]) peak = orginalbuff[i];
        for (int i = 0; i < (refToSonogram.timeWindowLength / 2); i++)
          spectrumBuffer[i] = orginalbuff[i] / peak * 255.0f;
      } else spectrumBuffer = orginalbuff;
      
      // Draw the spectrum bars
      for (int freqIndex = 0; freqIndex < (refToSonogram.timeWindowLength / 2); freqIndex++) {
        // If single color spectrum is enabled, paint it neon green
        if (refToSonogram.gad.cscolsi.isSelected()) {
          g.setColor(colorNeonGreen);
        }
        // Otherwise, get color by color mapping the spectral amplitude
        else selectColor(g, (int) spectrumBuffer[freqIndex]);
        
        // Width, and location of bottom and top of the rectangle
        int scaledWidth = (int) (spectrumBuffer[freqIndex] / 255.0f * 52.0f); // rescale amplitude to maximum width 52pix
        double yMax = (panelHeight - 70 - 1);
        double yBottom = yMax * (1.0 - (double) (freqIndex) / (double) (refToSonogram.timeWindowLength / 2 - 1));
        double yTop = yMax * (1.0 - (double) (freqIndex+1) / (double) (refToSonogram.timeWindowLength / 2 - 1));
        
        // Convert to log scale if enabled
        if (refToSonogram.gad.cslogfr.isSelected()) {
          double yni = yMax - yBottom; // y not inverse
          double yLog = Math.log(yni) / Math.log(yMax) * yMax; // calc log scale
          yLogPixelPos = (int) (yMax - yLog); // reinverse
          yni = yMax - yTop; // y not inverse
          yLog = Math.log(yni) / Math.log(yMax) * yMax; // calc log scale
          int yppownext = (int) (yMax - yLog); // reinverse
          if (yLogPixelPos - yppownext < 1) yppownext++;
          // Add a rectangle
          if (yLogPixelPos > 2 && yppownext < yMax)
            g.fillRect(panelWidth - 2 - scaledWidth, yppownext, scaledWidth, Math.abs(yLogPixelPos - yppownext));
        }
        
        // Convert to linear scale if enabled
        else if (yBottom > 2.0) g.fillRect(panelWidth - 3 - scaledWidth, (int) yBottom, scaledWidth, (int) (yBottom - yTop) + 1);
      }
      // Smoothed single spectrum red curve
      if (refToSonogram.gad.csmoothsi.isSelected()) {
        // Search and draw peak point
        for (int f = 0; f < (refToSonogram.timeWindowLength / 2); f++) {
          if (peakamp < spectrumBuffer[f]) {
            peakplace = f;
            peakamp = spectrumBuffer[f];
          }
        }
        g.setColor(new Color(200, 0, 0));
        int scaledWidth = (int) (spectrumBuffer[peakplace] / 255.0f * 50f); // rescale amplitude to maximum width 50pix
        int y = (int) ((panelHeight - 69) - peakplace * ffakt);
        if (refToSonogram.gad.cslogfr.isSelected()) {
          double ymax = (panelHeight - 69); // the max value
          double yni = ymax - y; // y not inverse
          double ylog = Math.log(yni) / Math.log(ymax) * ymax; // calc log scale
          y = (int) (ymax - ylog); // reinverse
        }
        if (scaledWidth > 5 && y > 5) g.fillOval(panelWidth - scaledWidth - 4, y - 4, 8, 8);
        // Draw Smooth
        float[] smoothspek = new float[refToSonogram.timeWindowLength / 2];
        for (int f = 3; f < (refToSonogram.timeWindowLength / 2 - 3); f++) {
          smoothspek[f] =
              (spectrumBuffer[f - 1]
                      + spectrumBuffer[f - 2]
                      + spectrumBuffer[f - 3]
                      + spectrumBuffer[f + 1]
                      + spectrumBuffer[f + 2]
                      + spectrumBuffer[f + 3])
                  / 6.0f;
        }
        g.setColor(colorRed);
        int xa = panelWidth - 3;
        int ya = panelHeight - 72;
        for (double f = 0.0f;
            f < (float) (refToSonogram.timeWindowLength / 2);
            f += ((double) refToSonogram.timeWindowLength / 2.0 / (double) (panelHeight - 60))) {
          scaledWidth = panelWidth - 3 - (int) (smoothspek[(int) f] / 255.0f * 52.0f);
          y = (int) ((panelHeight - 69) - f * ffakt) + 2;
          if (refToSonogram.gad.cslogfr.isSelected() == true) {
            double ymax = (double) (panelHeight - 69); // the max value
            double yni = ymax - y; // y not inverse
            double ylog = Math.log(yni) / Math.log(ymax) * ymax; // calc log scale
            y = (int) (ymax - ylog); // reinverse
          }
          if (y > 2 && y < (panelHeight - 71)) g.drawLine(scaledWidth, y, xa, ya);
          ya = y;
          xa = scaledWidth;
        }
      }

      // And Now let's paint and Calculate the numbers for the right upper display
      peakfrequ =
          (float) peakplace
              / (float) (refToSonogram.timeWindowLength / 2)
              * (float) refToSonogram.sampleRate
              / 2.0f;
      // logarithm scale
      String ampText;
      if (refToSonogram.gad.cslogfr.isSelected() == true) {
        double ymax = (double) (panelHeight - 70); // the max value
        double yni = ymax - mousey; // y not inverse
        double ylin = Math.exp(yni * Math.log(ymax) / ymax); // calc lin scale
        ylin = ymax - ylin; // reinverse
        double wsflog = ylin / ymax; // relative y position
        double wsplog =
            (int)
                ((double) (refToSonogram.timeWindowLength / 2) * wsflog); // frequenz position in Hz
        mousefrequ =
            (float) refToSonogram.sampleRate / 2.0f
                - (float) wsplog
                    / (float) (refToSonogram.timeWindowLength / 2)
                    * (float) refToSonogram.sampleRate
                    / 2.0f;
        int index = refToSonogram.timeWindowLength / 2 - (int) wsplog - 2;
        index = Math.min(index, refToSonogram.timeWindowLength / 2 - 1);
        index = Math.max(index, 0);
        mouseamp = (float) orginalbuff[index] / 255.0f * 100.0f; // in %
      }
      // linear scale
      else {
        mousefrequ =
            (float) refToSonogram.sampleRate / 2.0f
                - (float) wsp
                    / (float) (refToSonogram.timeWindowLength / 2)
                    * (float) refToSonogram.sampleRate
                    / 2.0f;
        mouseamp =
            (float) orginalbuff[refToSonogram.timeWindowLength / 2 - wsp - 1] / 255.0f * 100.0f;
      }
      if (calledfromplaying && refToSonogram.gad.csspecwhileplaying.isSelected()) // When playing take position from plbutton
        mousetime =
            (float) ((plbutton) * refToSonogram.selecedwidthold + refToSonogram.selectedstartold)
                * (float) refToSonogram.samplesAll
                / (float) refToSonogram.sampleRate;
      else
        mousetime =
            ((float) refToSonogram.selectedstartold
                    + (float) wnf * (float) refToSonogram.selecedwidthold)
                * (float) refToSonogram.samplesAll
                / (float) refToSonogram.sampleRate;
      mousetime = (float) Math.round(mousetime * 1000) / 1000.0f;
      mousefrequ = (float) Math.round(mousefrequ);
      double mouselevel = mouseamp / 100.0;
      if (mouselevel <= 0.0) mouselevel = 0.000000000013888; // corresponds to -250dB
      mouselevel = 10.0 * Math.log(mouselevel);
      mouselevel = Math.round(mouselevel * 10.0) / 10.0;
      mouseamp = (float) Math.round(mouseamp * 10) / 10.0f;

      // Main Spectrogram Mouseover Tooltip
      String musicalNote = FrequencyToNoteConverter.query(mousefrequ);
      String frequ = String.format("%.3f", mousefrequ / 1000);
      if (refToSonogram.gad.ctooltip.isSelected()) {
        String ttt =
            "<html><pre>"
                + "<b>Frequency: </b><tt><font color=black >"
                + frequ
                + "kHz</font></tt><br>"
                + "<b>Time:      </b><tt><font color=black >"
                + mousetime
                + "s</font></tt><br>"
                + "<b>Amplitude: </b><tt><font color=black >"
                + mouseamp
                + "%</font></tt><br>"
                + "<b>Note:      </b><tt><font color=black >"
                + musicalNote
                + "</font></tt><br>"
                + "<b><font size=1>Level: </font></b><tt><font color=black size=1>"
                + mouselevel
                + "dB</font></tt><br>";
        this.setToolTipText(ttt);
      } else this.setToolTipText(null);

      // Wipe and reset bottom right corner box
      g.setColor(colorLavender); // Delete old Numbers
      g.fillRect(panelWidth - 55, panelHeight - 70, 55, 70); // Delete old one
      // Draw rectangle borders
      g.setColor(new Color(95, 95, 180)); 
      g.drawRect(panelWidth - 55, panelHeight - 31, 52, 27);
      g.drawRect(panelWidth - 55, panelHeight - 66, 52, 32);
      // Draw darker rectangles
      g.setColor(new Color(125, 125, 185));
      g.fillRect(panelWidth - 54, panelHeight - 30, 51, 26); // filled zone
      g.fillRect(panelWidth - 54, panelHeight - 65, 51, 31); // filled zone
      
      g.setColor(new Color(20, 20, 130));
      g.setFont(new Font("Dialog", 0, 9));
      
      // Define strings to be printed in bottom right corner
      String spe = ("P: " + (int) peakfrequ + "Hz");
      String sfr = ("F: " + (int) mousefrequ + "Hz");
      String sti = ("T: " + mousetime + "s.");
      String sam = ("A: " + mouseamp + "%");

      boolean first = true;
      String optA = "";
      if (refToSonogram.gad.csmoothx.isSelected()) {
        if (first) first = false;
        else optA += ",";
        optA += "sT";
      }
      if (refToSonogram.gad.csmooth.isSelected()) {
        if (first) first = false;
        else optA += ",";
        optA += "sF";
      }
      if (refToSonogram.gad.coverlapping.isSelected()) {
        if (first) first = false;
        else optA += ",";
        optA += "Ov" + refToSonogram.gad.sliderwinspeed.getValue();
      }
      String optB = "";
      if (refToSonogram.hamItem.isSelected()) optB += "Ham";
      if (refToSonogram.hanItem.isSelected()) optB += "Han";
      if (refToSonogram.blaItem.isSelected()) optB += "Bla";
      if (refToSonogram.rectItem.isSelected()) optB += "Rec";
      if (refToSonogram.triItem.isSelected()) optB += "Tri";
      if (refToSonogram.welItem.isSelected()) optB += "Wel";
      if (refToSonogram.gauItem.isSelected()) optB += "Gau";
      if (refToSonogram.gad.csampl.isSelected()) {
        optB += ",8kHz";
      }
      if (refToSonogram.zoompreviousindex != 0) {
        optB += ",z" + refToSonogram.zoompreviousindex;
      }
      String optC = "";
      first = true;
      if (refToSonogram.gad.clog.isSelected()) {
        if (first) first = false;
        optC += "logA";
      }
      if (refToSonogram.gad.cslogfr.isSelected()) {
        if (first) first = false;
        else optC += ",";
        optC += "logFr";
      }

      // Draw contents of bottom right corner
      g.drawString(spe, panelWidth - 54, panelHeight - 58);
      g.drawString(sfr, panelWidth - 54, panelHeight - 50);
      g.drawString(sti, panelWidth - 54, panelHeight - 42);
      g.drawString(sam, panelWidth - 54, panelHeight - 34);
      g.drawString(optC, panelWidth - 54, panelHeight - 23);
      g.drawString(optA, panelWidth - 54, panelHeight - 14);
      g.drawString(optB, panelWidth - 54, panelHeight - 5);
      g.drawRect(panelWidth - 14, panelHeight - 41, 7, 6);
      selectColor(g, (int) (255f * mouseamp / 100f)); // Draw Colored rectangle for Amplitude
      g.fillRect(panelWidth - 13, panelHeight - 40, 6, 5);
      
      // Update all the views
      refToSonogram.wv.update();
      refToSonogram.av.update();
      refToSonogram.cv.update();
      refToSonogram.fv.update();
      refToSonogram.lv.update();
      refToSonogram.kv.update();

      refToSonogram.pv.mouse_x = mousex;
      if (refToSonogram.gad.cptrack.isSelected()) refToSonogram.pv.update();
    }
  }

  // -------------------------------------------------------------------------------------------------------------------------
  /**
   * Paint the complete Sonogram on Window with all Items. Method selected between different
   * programm-conditions. It uses the double-buffer technique to buffer the actual view in
   * Image-Object. So no repaint is required to update Complete View. Sliderupdate overrides the
   * Picture that is stored in <i>doublebufferimage</i>. If no changes on win-size are made from
   * user, simply the Picture are drawn on JPanel.
   */
  @Override
  public void paintComponent(Graphics gr) {
    Dimension size = getSize();
    panelWidth = (short) size.getWidth(); // Windowsize for Spektogramm
    panelHeight = (short) size.getHeight(); // Windowsize for Spektogramm
    // System.out.println(xm + " " + ym);
    // System.out.println(xm+"  "+ym);
    if (g == null
        || size.width != olddimension.width
        || size.height != olddimension.height
        || refToSonogram.updateimageflag) { // Check if an Image update is needed
      olddimension = size;
      doublebufferimage =
          new BufferedImage(size.width - 1, size.height, BufferedImage.TYPE_INT_RGB);
      g = (Graphics2D) doublebufferimage.getGraphics();

      if (refToSonogram.gad.cantialise.isSelected()) {
        g.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        g.setRenderingHint(
            RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_BILINEAR);
      }
      if (!refToSonogram.spectrumExist) {
        this.setToolTipText("");
        g.drawImage(defaultimage, 0, 0, panelWidth, panelHeight, new Color(0, 0, 0), this);
        refToSonogram.enableItems(false);
        if (refToSonogram.firststart) {
          this.setToolTipText(
              "<html><b>No File Open</b><br>Open one of the preconfigured"
                  + " <tt>\"demo1...15.wav\"</tt> wave files via<br><i>\"Open Remote Network"
                  + " File\"</i> item from the File menu. Then<br>select <i>\"Arrange Windows\"</i>"
                  + " from the Options menu. You can<br>also drop any wave file into the main"
                  + " window.");
        }
        // System.out.println(xm + " " + ym);
        // License and version/build text
        g.setColor(new Color(60, 60, 60));
        g.setFont(new Font("Tahoma", Font.BOLD, 9));
        // build the strings
        String versionString = "Version: " + Sonogram.VERSION;
        String buildString = "Build: " + Sonogram.BUILD;
        String licenString = "Licensed to: " + Licenses.zoda1.replaceAll("\\s+$", "");
        String copyrightString =
            "Copyright "
                + Integer.toString(java.util.Calendar.getInstance().get(java.util.Calendar.YEAR))
                + " by Christoph Lauer Engineering\u2122 - www.christoph-lauer.de";
        // draw the text
        g.drawString(versionString, 2, panelHeight - 20);
        g.drawString(buildString, 2, panelHeight - 12);
        g.setFont(new Font("Tahoma", Font.PLAIN, 11));
        FontMetrics metrics = g.getFontMetrics();
        int textWidth = metrics.stringWidth(copyrightString);
        g.drawString(copyrightString, panelWidth - textWidth - 3, panelHeight - 3);
        if (Licenses.zoda1.equals("HIRN1HIRN1HIRN1HIRN1HIRN1HIRN1HI")) {
          licenString = /*Integer.toString(Sonogram.isetu) + " Minute*/ "Trial Version";
        } else g.setColor(new Color(110, 110, 110));
        g.setFont(new Font("Tahoma", Font.BOLD, 12));
        g.drawString(licenString, 2, panelHeight - 2);
      }

      // Paint only when File is opened
      if (refToSonogram.spectrumExist && !refToSonogram.openingflag) { 
        // Width of spectrogram rectangles in pixels
        double xDiff = (double) (panelWidth - 100) / (double) refToSonogram.spectrum.size();
        // Height of spectrogram rectangles in pixels
        double yDiff = (panelHeight - 70) / (refToSonogram.timeWindowLength / 2.0);
        
        int xPosition = 0; // Startpoint x for drawing the Rect.
        int yPosition = 0; // Startpoint y for drawing the Rect.
        int xDiffRounded = (int) Math.ceil(xDiff); // rounded off diffx
        int yDiffRounded = (int) Math.ceil(yDiff); // rounded off diffy
        float[] tempSpectrum; // Reference to FFT Window
        int brightnessEightBit;
        
        double secs = 0.0;
        double timeToScreenFactor = (panelWidth - 100) / 2000.0; // To draw Timesignal
        float pitchtmp = 0.0f;
        float pitchamp = 0.0f;
        int pitchplace = 0;
        // For Logarithm Frequencyview
        double powfact; // Logarithm Scale fact
        int yDiffLog;
        double powfactnext;
        int ypnext;
        int yppownext;
        int yLogPixelPos; // Logarithm Position
        boolean peakTimeIsReached = false;
        int ypointpeak = 0;
        int xpointpeak = 0;
        
        // some stuff at beginning
        if (refToSonogram.gad.highlightedbutton == 1) refToSonogram.gad.highLightButton(0);
        refToSonogram.setCursor(new Cursor(Cursor.WAIT_CURSOR));
        System.out.println("--> Updating Sonogram-Image: Paintwindowsize x=" + panelWidth + ", y=" + panelHeight);
        System.out.println("--> diffx=" + xDiff + ", diffy=" + yDiff);
        // Blank the entire panel
        g.setColor(colorLavender);
        g.fillRect(0, 0, panelWidth, panelHeight);
        
        // Draw the y (frequency) axis labels and ticks
        int frequency = refToSonogram.sampleRate / 2;
        if (refToSonogram.updateimageflag) refToSonogram.updateimageflag = false;
        g.setColor(colorPurple);
        g.setFont(new Font("Courier", 0, 9));
        for (double yLinearTick = 0; yLinearTick <= (panelHeight - 70); yLinearTick += (panelHeight - 70) / 10.0) {
          if (refToSonogram.gad.cslogfr.isSelected()) {
            double ymax = (panelHeight - 69); // the max value
            double yni = ymax - yLinearTick; // y not inverse
            double ylog = Math.log(yni) / Math.log(ymax) * ymax; // calc log scale
            yLogPixelPos = (int) (ymax - ylog); // reinverse
            g.drawLine(0, yLogPixelPos, 40, yLogPixelPos);
            g.drawString((frequency / 1000.0) + "kHz", 3, yLogPixelPos + 9);
            frequency -= refToSonogram.sampleRate / 20;
          } else {
            g.drawLine(0, (int) yLinearTick, 40, (int) yLinearTick);
            g.drawString((frequency / 1000.0) + "kHz", 3, (int) yLinearTick + 9);
            frequency -= refToSonogram.sampleRate / 20;
          }
        }

        // Draw second marks (x axis ticks)
        secs =
            refToSonogram.selectedstartold
                * (double) refToSonogram.samplesAll
                / (double) refToSonogram.sampleRate;
        int isecs = (int) secs;
        int offset =
            (int)
                (1.0
                    - (secs - isecs)
                        * ((double) panelWidth - 100.0)
                        / ((double) refToSonogram.samplesTotal
                            / (double) refToSonogram.sampleRate));
        secs = isecs;
        if (refToSonogram.selectedstartold == 0.0) secs = 0.0;
        for (int x = 40 + offset;
            x < panelWidth - 60;
            x +=
                (int)
                    ((panelWidth - 100)
                        / ((double) refToSonogram.samplesTotal
                            / (double) refToSonogram.sampleRate))) {
          g.drawLine(x - 1, panelHeight - 60, x - 1, panelHeight - 40);
          g.drawString(secs + "s", x + 1, panelHeight - 52);
          secs++;
        }

        // Draw trapezoid from bottom of spectrogram to audio selection
        int[] polygonx = new int[4];
        int[] polygony = new int[4];
        polygonx[0] = 40;
        polygony[0] = panelHeight - 60;
        polygonx[1] = panelWidth - 60;
        polygony[1] = panelHeight - 60;
        polygonx[2] = 40 + (int) ((panelWidth - 100) * (refToSonogram.selectedstartold + refToSonogram.selecedwidthold));
        polygony[2] = panelHeight - 50;
        polygonx[3] = 40 + (int) ((panelWidth - 100) * refToSonogram.selectedstartold);
        polygony[3] = panelHeight - 50;
        if (refToSonogram.selectedstartold != 0.0 && refToSonogram.selecedwidthold != 1.0) {
          g.setComposite(compositeGr);
          g.setColor(colorPurple);
          g.fillPolygon(polygonx, polygony, 4);
          g.setComposite(compositeNo);
          g.drawLine(polygonx[0], polygony[0], polygonx[3], polygony[3]);
          g.drawLine(polygonx[1], polygony[1], polygonx[2], polygony[2]);
          g.setComposite(compositeNo);
        }

        // Painting Sonogram on the Image
        // Loop over spectra
        for (int i = 0; i < refToSonogram.spectrum.size(); i++) {
          // If peakpoint is reached
          if (i == refToSonogram.peakx) peakTimeIsReached = true;
          else peakTimeIsReached = false;
          tempSpectrum = refToSonogram.spectrum.get(i); // WFT Frequ. Vector
          
          // loop over FFT Vector-Elements
          for (int j = 0; j < (refToSonogram.timeWindowLength / 2); j++) {
            // Determine the color of the rectangle from amplitude
            brightnessEightBit = (int) tempSpectrum[(refToSonogram.timeWindowLength / 2 - 1) - j];
            if (brightnessEightBit < 0) brightnessEightBit = 0; // Range-test
            if (brightnessEightBit > 255) brightnessEightBit = 255; // Range-test
            selectColor(g, brightnessEightBit);
            // Position of top left of rectangle
            xPosition = (int) (i * xDiff) + 40; // Startpoint in Pixels
            yPosition = (int) (j * yDiff); // Startpoint in Pixels
            // Logarithm Frequency View
            if (refToSonogram.gad.cslogfr.isSelected()) {
              // Calculate position for logview
              double ymax = (panelHeight - 69); // the max value
              double yni = ymax - yPosition; // y not inverse
              double ylog = Math.log(yni) / Math.log(ymax) * ymax; // calc log scale
              yLogPixelPos = (int) (ymax - ylog); // reinverse
              // Calculate diffy for logview by calculating next point
              yni = ymax - ((j + 1) * yDiff); // y not inverse
              ylog = Math.log(yni) / Math.log(ymax) * ymax; // calc log scale
              yppownext = (int) (ymax - ylog); // reinverse

              yDiffLog = yppownext - yLogPixelPos;
              // Paint logview
              g.fillRect(xPosition, yLogPixelPos, xDiffRounded, yDiffLog); // Draws Rect
              if (refToSonogram.gad.cspitchonely.isSelected()
                  && refToSonogram.gad.cspitch.isSelected()) {
                g.setColor(colorLavender);
                g.setComposite(compositeNebular);
                g.fillRect(xPosition, yLogPixelPos, xDiffRounded, yDiffLog); // Draws Rect
                g.setComposite(compositeNo);
              }
              if (peakTimeIsReached) // Absolute Peak
                if (j == (refToSonogram.timeWindowLength / 2 - refToSonogram.peaky - 1)) {
                  xpointpeak = xPosition + xDiffRounded / 2 - 10;
                  ypointpeak = yLogPixelPos + yDiffLog / 2 - 10;
                }
              // Non Logarithm View
            } else {
              g.fillRect(xPosition, yPosition, xDiffRounded, yDiffRounded);
              if (refToSonogram.gad.cspitchonely.isSelected()
                  && refToSonogram.gad.cspitch.isSelected()) {
                g.setColor(colorLavender);
                g.setComposite(compositeNebular);
                g.fillRect(xPosition, yPosition, xDiffRounded, yDiffRounded);
                g.setComposite(compositeNo);
              }
              if (peakTimeIsReached) // Absolute Peak
                if (j == (refToSonogram.timeWindowLength / 2 - refToSonogram.peaky - 1)) {
                  xpointpeak = xPosition + xDiffRounded / 2 - 10;
                  ypointpeak = yPosition + yDiffRounded / 2 - 10;
                }
            }
          }

          // PITCH: Begin Pitch detection
          if (refToSonogram.gad.cspitch.isSelected() == true) { // Pitch detection
            pitchtmp = 0.0f;
            pitchamp = 0.0f;
            int begin = 0;
            // PITCH:Frequencylimitation for Pitchdetection
            if (refToSonogram.gad.cspitchlimitation.isSelected() == true) {
              begin =
                  (refToSonogram.timeWindowLength / 2)
                      - (int)
                          ((double) (refToSonogram.timeWindowLength / 2)
                              / refToSonogram.sampleRate
                              * 2
                              * refToSonogram.gad.sliderpitch.getValue());
              if (begin >= (refToSonogram.timeWindowLength / 2 - 1))
                begin = (refToSonogram.timeWindowLength / 2 - 2);
            }
            float smoothspek[] = new float[refToSonogram.timeWindowLength / 2];
            // PITCH:Smooth out for Pitchdetection
            if (refToSonogram.gad.cspitchsmooth.isSelected() == true) {
              for (int y = 3; y < (refToSonogram.timeWindowLength / 2 - 3); y++) {
                smoothspek[y] =
                    (tempSpectrum[y - 1]
                            + tempSpectrum[y - 2]
                            + tempSpectrum[y - 3]
                            + tempSpectrum[y]
                            + tempSpectrum[y + 1]
                            + tempSpectrum[y + 2]
                            + tempSpectrum[y + 3])
                        / 7.0f;
              }
            }
            // PITCH:If Smooth out for Pitch is not selected
            else
              for (int y = 3; y < (refToSonogram.timeWindowLength / 2 - 3); y++) {
                smoothspek[y] = tempSpectrum[y];
              }
            // PITCH: Main loop over frequency
            // local absolute peak
            pitchplace = begin;
            for (int y = begin; y < (refToSonogram.timeWindowLength / 2); y++) {
              pitchtmp = (int) smoothspek[(refToSonogram.timeWindowLength / 2 - 1) - y];
              if (pitchtmp > pitchamp) {
                pitchamp = pitchtmp;
                pitchplace = y;
              }
            }

            xPosition = (int) ((double) i * xDiff) + 40; // Startpoint in Pixels
            yPosition = (int) ((double) pitchplace * yDiff); // Startpoint in Pixels
            if (refToSonogram.gad.cspitchblack.isSelected() == false) g.setColor(colorRed);
            else g.setColor(colorWhite);
            if (refToSonogram.gad.cslogfr.isSelected() == true) { // Logarithm Frequency View
              // PITCH: Calculate position for logview
              double ymax = (double) (panelHeight - 69); // the max value
              double yni = ymax - yPosition; // y not inverse
              double ylog = Math.log(yni) / Math.log(ymax) * ymax; // calc log scale
              yLogPixelPos = (int) (ymax - ylog); // reinverse
              g.fillRect(xPosition, yLogPixelPos, xDiffRounded, yDiffRounded); // Draws Rect
            } else g.fillRect(xPosition, yPosition, xDiffRounded, yDiffRounded); // Draws Rect
          }
        }
        // END PITCH:

        if (refToSonogram.gad.clocalpeak.isSelected() == true) {
          g.setColor(colorLavender);
          g.drawOval(xpointpeak + 5, ypointpeak + 5, 10, 10);
          g.setComposite(compositeAl);
          g.fillOval(xpointpeak, ypointpeak, 20, 20);
          g.setComposite(compositeNo);
        }

        // Draws Grid if it is selected
        if (refToSonogram.gridItem.isSelected()) {
          g.setComposite(compositeGr);
          if (refToSonogram.fireItem.isSelected() == true) g.setComposite(compositeAl);
          // Colors for Grid
          if (((refToSonogram.negItem.isSelected() == true)
                  && (refToSonogram.colItem.isSelected() == true))
              || ((refToSonogram.negItem.isSelected() == true)
                  && (refToSonogram.fireItem.isSelected() == true))
              || ((refToSonogram.negItem.isSelected() == false)
                  && (refToSonogram.bwItem.isSelected() == true))) g.setColor(colorBlack);
          else g.setColor(colorWhite);
          // grid in Spectrum over time
          for (int x = 40 + offset;
              x < panelWidth - 60;
              x +=
                  ((double) panelWidth - 100.0)
                      / ((double) refToSonogram.samplesTotal / (double) refToSonogram.sampleRate))
            if (x >= 40) g.drawLine(x - 1, 0, x - 1, panelHeight - 70);
          // grid over Frequencies
          for (double y = 0; y < panelHeight - 69.0; y += ((double) panelHeight - 70.0) / 10.0)
            // GRID: If logarithm Frequency
            if (refToSonogram.gad.cslogfr.isSelected() == true) {
              double ymax = (double) (panelHeight - 69); // the max value
              double yni = ymax - y; // y not inverse
              double ylog = Math.log(yni) / Math.log(ymax) * ymax; // calc log scale
              yLogPixelPos = (int) (ymax - ylog); // reinverse
              g.drawLine(40, yLogPixelPos, panelWidth - 60, (int) yLogPixelPos);
              // GRID: If linear Frequency
            } else g.drawLine(40, (int) y, panelWidth - 60, (int) y);
          g.setComposite(compositeNo);
        }
        // Drawing Timeline
        g.setColor(colorDarkGreen);
        g.fillRect(40, panelHeight - 50, panelWidth - 100, 50); // Delete old one
        g.setColor(new Color(100, 80, 90));
        g.setComposite(compositeAl);
        // the color marker for the zoom area
        int xold = 0; // memorizer for the highest point
        int yold = 0; // memorizer for the highest point
        int ymax = 0; // memorizer for the highest point
        for (int xg = 41;
            xg < panelWidth - 60;
            xg +=
                ((double) panelWidth - 100.0)
                    / ((double) refToSonogram.samplesAll
                        / (double) refToSonogram.sampleRate)) // Timegrid
        g.drawLine(xg - 1, panelHeight - 50, xg - 1, panelHeight);
        int amp, x, y;
        // UPPER DISPLAY WITH ENERGY INTENSITY
        if (refToSonogram.energyflag == true) { // if Energytimesignal is on
          g.setColor(new Color(100, 80, 90)); // Grid...
          g.drawLine(40, panelHeight - 10, panelWidth - 61, panelHeight - 10);
          g.drawLine(40, panelHeight - 20, panelWidth - 61, panelHeight - 20);
          g.drawLine(40, panelHeight - 28, panelWidth - 61, panelHeight - 28);
          g.drawLine(40, panelHeight - 35, panelWidth - 61, panelHeight - 35);
          g.drawLine(40, panelHeight - 40, panelWidth - 61, panelHeight - 40);
          g.drawLine(40, panelHeight - 44, panelWidth - 61, panelHeight - 44);
          g.drawLine(40, panelHeight - 48, panelWidth - 61, panelHeight - 48);
          g.setColor(new Color(200, 200, 255));
          g.setComposite(compositeNo);
          g.drawString("Energy", 42, panelHeight - 38);
          g.setComposite(compositeAl);
          g.setColor(new Color(200, 255, 0));
          for (int t = 0; t < 2000; t++) {
            y = (int) ((float) refToSonogram.timeline[t] / 2.5);
            x = (int) ((double) t * timeToScreenFactor) + 40;
            if ((double) t / 2000.0 > refToSonogram.selectedstart
                && (double) t / 2000.0 < (refToSonogram.selectedstart + refToSonogram.selecedwidth))
              if (refToSonogram.selecedwidth == 1.0)
                // selectColor(g,reftosonogram.timeline[t]*2);
                g.setColor(colorNeonGreen);
              else {
                g.setComposite(compositeFo);
                g.setColor(colorRed);
                g.drawLine(x, panelHeight - 50, x, panelHeight);
                g.setComposite(compositeAl);
              }
            else
              // selectColor(g,reftosonogram.timeline[t]*2);
              g.setColor(colorNeonGreen);
            // g.drawLine(x,ym-y,x,ym);
            if (x != xold) { // if the x jumps forward
              g.setComposite(compositeAl1);
              g.drawLine(xold, panelHeight + ymax, xold, panelHeight - yold);
              g.setComposite(compositeNo);
              g.drawLine(xold + 1, panelHeight - ymax, xold, panelHeight - yold);
              g.drawLine(xold + 1, panelHeight + ymax, xold, panelHeight + yold);
              xold = x;
              yold = ymax;
              ymax = 0;
              ymax = Math.abs(y);
            } else { // else look if there is a peak
              if (ymax < Math.abs(y)) ymax = Math.abs(y);
            }
          }
        }
        // UPPER DISPLAY WITH AMPLITUDE
        if (refToSonogram.energyflag == false) { // if Energytimesignal is off = Timesiganal
          g.setColor(new Color(100, 80, 90));

          g.drawLine(40, panelHeight - 5, panelWidth - 61, panelHeight - 5); // Grid...
          g.drawLine(40, panelHeight - 10, panelWidth - 61, panelHeight - 10);
          g.drawLine(40, panelHeight - 15, panelWidth - 61, panelHeight - 15);
          g.drawLine(40, panelHeight - 20, panelWidth - 61, panelHeight - 20);
          g.drawLine(40, panelHeight - 30, panelWidth - 61, panelHeight - 30);
          g.drawLine(40, panelHeight - 35, panelWidth - 61, panelHeight - 35);
          g.drawLine(40, panelHeight - 40, panelWidth - 61, panelHeight - 40);
          g.drawLine(40, panelHeight - 45, panelWidth - 61, panelHeight - 45);

          g.setColor(new Color(200, 200, 255));
          g.setComposite(compositeNo);
          g.drawString("Amplitude", 42, panelHeight - 38);
          g.setComposite(compositeAl);
          for (int t = 0; t < 2000; t++) {
            y = refToSonogram.timeline[t] / 5;
            x = (int) ((double) t * timeToScreenFactor) + 40;
            if ((double) t / 2000.0 > refToSonogram.selectedstart
                && (double) t / 2000.0 < (refToSonogram.selectedstart + refToSonogram.selecedwidth))
              if (refToSonogram.selecedwidth == 1.0) g.setColor(colorNeonGreen);
              else {
                g.setComposite(compositeFo);
                g.setColor(colorRed);
                g.drawLine(x, panelHeight - 50, x, panelHeight);
                g.setColor(colorRed);
              }
            else g.setColor(colorDimNeonGreen);
            // g.drawLine(x,ym-25+y,x,ym-25-y);
            // the bright borders of the amplitude signal
            if (x != xold) { // if the x jumps forward
              g.setComposite(compositeAl1);
              g.drawLine(xold, panelHeight - 25 + ymax, xold, panelHeight - 25 - yold);
              g.setComposite(compositeNo);
              g.drawLine(xold + 1, panelHeight - 25 - ymax, xold, panelHeight - 25 - yold);
              g.drawLine(xold + 1, panelHeight - 25 + ymax, xold, panelHeight - 25 + yold);
              xold = x;
              yold = ymax;
              ymax = 0;
              ymax = Math.abs(y);
            } else { // else look if there is a peak
              if (ymax < Math.abs(y)) ymax = Math.abs(y);
            }
          }
          g.setComposite(compositeAl);
          g.setColor(colorNeonGreen);
          g.drawLine(40, panelHeight - 25, panelWidth - 60, panelHeight - 25); // Middleline
        }
        g.setColor(new Color(100, 0, 0));
        g.drawLine(polygonx[3], panelHeight - 50, polygonx[3], panelHeight);
        g.drawLine(polygonx[2], panelHeight - 50, polygonx[2], panelHeight);
        // Text on left Side
        g.setColor(new Color(50, 50, 200)); // draw Spektrumdimensions
        g.setFont(new Font("Courier", 0, 9));
        String tr;
        if (refToSonogram.gad.rfft.isSelected() == true) tr = "FFT";
        else tr = "LPC";
        String wn = "wn:" + String.valueOf(refToSonogram.spectrum.size());
        String wl = "wl:" + String.valueOf(refToSonogram.timeWindowLength);
        g.drawString(tr, 3, panelHeight - 21);
        g.drawString(wn, 3, panelHeight - 12);
        g.drawString(wl, 3, panelHeight - 3);
        // Grids and fill in Single View
        g.setColor(colorDarkGreen);
        g.fillRect(panelWidth - 55, 2, 53, panelHeight - 71); // Rect for Single Fr. view
        g.setColor(colorPurple);

        // grid over Frequencies
        for (double yg = 0; yg < panelHeight - 69.0; yg += ((double) panelHeight - 70.0) / 10.0)
          // GRID:If logarithm Frequency
          if (refToSonogram.gad.cslogfr.isSelected() == true) {
            double ymaxd = (double) (panelHeight - 69); // the max value
            double yni = ymaxd - yg; // y not inverse
            double ylog = Math.log(yni) / Math.log(ymaxd) * ymaxd; // calc log scale
            yLogPixelPos = (int) (ymaxd - ylog); // reinverse
            g.drawLine(panelWidth - 60, (int) yLogPixelPos, panelWidth - 3, (int) yLogPixelPos);
          } else g.drawLine(panelWidth - 60, (int) yg, panelWidth - 3, (int) yg);

        // Rainbow on Edge
        for (x = 0; x < 255; x++) { // Rainbow on Edge
          xPosition = (int) ((double) x / 255.0 * 31.0);
          selectColor(g, x);
          g.drawLine(5 + xPosition, panelHeight - 50, 5 + xPosition, panelHeight - 35);
        }
        g.setColor(new Color(0, 0, 255));
        g.drawRect(4, panelHeight - 50, 32, 15);
        System.out.println("--> Updating complete");
        wnp = 0; // Window Number Position for paintOneSpejtrum in units
        wsp = 0; // Window Size position for paintOneSpejtrum in units
        wnf = 0.0; // self as wnp from 0..1
        wsf = 0.0; // self as wsp from 0..1
      }
    }
    gr.drawImage(doublebufferimage, 0, 0, this);
    if (refToSonogram.infovisible == true) refToSonogram.infod.update();
    refToSonogram.cv.update();
    refToSonogram.av.update();
    refToSonogram.fv.update();
    refToSonogram.lv.update();
    refToSonogram.wv.update();
    refToSonogram.kv.update();
    refToSonogram.pv.update();
    // And the single spectrum too
    if (refToSonogram.spectrumExist == true && refToSonogram.openingflag == false) {
      paintOneSpectrum(false);
      paintTimeSlider(gr, false);
    }
    // paint the 8KHz marker
    if (refToSonogram.gad.csampl.isSelected() == true && refToSonogram.spectrumExist == true) {
      gr.setColor(Color.red);
      gr.drawRect(49, 8, 39, 13);
      gr.drawRect(48, 7, 41, 15);
      gr.setFont(new Font("San Serif", Font.BOLD, 14));
      gr.drawString("8KHz", 50, 20);
    }
    // reset the cursor
    refToSonogram.setCursor(new Cursor(Cursor.DEFAULT_CURSOR));
  }
  // -------------------------------------------------------------------------------------------------------------------------
  /**
   * Select Paintcolor from Graphics to selected Solorarray in GeneralAdjustmentDialog.
   *
   * @param g Graphics-object with to select Color.
   * @param bwcolor arraypoint of Color to select.
   */
  public void selectColor(Graphics g, int bwcolor) {
    if (refToSonogram.colItem.isSelected() == true && refToSonogram.negItem.isSelected() == false)
      g.setColor(coCO[bwcolor]);
    if (refToSonogram.colItem.isSelected() == true && refToSonogram.negItem.isSelected() == true)
      g.setColor(coCOi[bwcolor]);
    if (refToSonogram.bwItem.isSelected() == true && refToSonogram.negItem.isSelected() == false)
      g.setColor(coSW[bwcolor]);
    if (refToSonogram.bwItem.isSelected() == true && refToSonogram.negItem.isSelected() == true)
      g.setColor(coSWi[bwcolor]);
    if (refToSonogram.fireItem.isSelected() == true && refToSonogram.negItem.isSelected() == false)
      g.setColor(coFI[bwcolor]);
    if (refToSonogram.fireItem.isSelected() == true && refToSonogram.negItem.isSelected() == true)
      g.setColor(coFIi[bwcolor]);
    if (refToSonogram.firecItem.isSelected() == true && refToSonogram.negItem.isSelected() == false)
      g.setColor(cocFI[bwcolor]);
    if (refToSonogram.firecItem.isSelected() == true && refToSonogram.negItem.isSelected() == true)
      g.setColor(cocFIi[bwcolor]);
    if (refToSonogram.rainItem.isSelected() == true && refToSonogram.negItem.isSelected() == false)
      g.setColor(coRA[bwcolor]);
    if (refToSonogram.rainItem.isSelected() == true && refToSonogram.negItem.isSelected() == true)
      g.setColor(coRAi[bwcolor]);
    if (refToSonogram.greenItem.isSelected() == true && refToSonogram.negItem.isSelected() == false)
      g.setColor(coCG[bwcolor]);
    if (refToSonogram.greenItem.isSelected() == true && refToSonogram.negItem.isSelected() == true)
      g.setColor(coCGi[bwcolor]);
  }
  // -------------------------------------------------------------------------------------------------------------------------
}
