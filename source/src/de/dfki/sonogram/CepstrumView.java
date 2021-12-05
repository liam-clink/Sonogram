package de.dfki.sonogram;

import de.dfki.maths.*;
import java.awt.*;
import javax.swing.*;

/**
 * Copyright (c) 2001 Christoph Lauer @ DFKI, All Rights Reserved. clauer@dfki.de - www.dfki.de
 *
 * <p>The cepstrum view shows the cepstum transformed vector in a curve form. For the theoretical
 * background of the Cepstrum transformation see the Sonogram paper at my homepage.
 *
 * @author Christoph Lauer
 * @version 1.0, Current 26/09/2002
 */

/**
 *  Cepstrum is the result of computing the inverse Fourier transform (IFT) of the logarithm of the estimated signal spectrum.
 * 
 * @author Liam Clink
 * @version 5.1, 2021-12-04
 */

public class CepstrumView extends JFrame {
  Sonogram refToMain;
  
  // The number of samples used and thus also two times the number of bars to be drawn
  int samples = 0;
  public int samples() {
    return samples;
  }
  public void samples(int arg) {
    samples = arg;
  }

  /**
   * Constructor for CepstrumView. This Class extends from JFrame and shows cepstral analysis curve in
   * this window.
   */
  public CepstrumView(Sonogram sono) {
    refToMain = sono;
    setTitle("Cepstrum View");
    setSize(1000, 400);
    setLocation(10, 10);
    Toolkit tk = Toolkit.getDefaultToolkit();
    setIconImage(tk.getImage(Sonogram.class.getResource("Sonogram.gif")));
    CepstrumViewPanel cv = new CepstrumViewPanel(); // Panel for Winfunktion
    getContentPane().add(cv);
  }
  // -------------------------------------------------------------------------------------------------------------------------
  public void update() {
    if (isVisible() && !refToMain.openingflag && refToMain.spectrumExist) {
      repaint();
    }
  }
  // -------------------------------------------------------------------------------------------------------------------------
  // =========================================================================================================================
  /** Inner Panel class to paint Windowfunktion */
  // NOTE: origin is at top left, with y increasing down and x increasing right
  class CepstrumViewPanel extends JPanel {
    // -------------------------------------------------------------------------------------------------------------------------
    @Override
    public void paintComponent(Graphics gr) {
      Graphics2D g = (Graphics2D) gr;
      if (refToMain.gad.cantialise.isSelected())
        g.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
      
      Color colorBackground = new Color(40, 40, 60);
      Color colorRed = new Color(255, 0, 0);
      Color colorDarkLine = new Color(80, 80, 90);
      Color colorLightLine = new Color(180, 180, 220);
      Color colorGridLine = new Color(80, 80, 130);
      
      Dimension size = getSize(); // Windowsize
      short width = (short) size.getWidth();
      short height = (short) size.getHeight();
      int margin = 5;
      int majorTickLength = 15;
      int minorTickLength = 8;
      int axisWidth = width - (2*margin+majorTickLength);
      int axisHeight = height - (2*margin+majorTickLength);

      short divisions = 10;
      
      int startp = 0;
      float[] buffer;
      float[] cepstrum;
      String str;
      
      Byte tempbyte;
      boolean mouseIsInTimeSpan = false;
      
      
      // Draw Axes and Grid
      // Fill Background
      g.setColor(colorBackground);
      g.fillRect(0, 0, width, height);
      // Draw plot axes
      g.setColor(colorRed);
      g.drawLine(margin, height - (majorTickLength+margin), width - margin, height - (majorTickLength+margin));
      g.drawLine(majorTickLength+margin, margin, majorTickLength+margin, height - margin);
      
      // Draw horizontal lines
      int verticalDivisionCounter = 0;
      for (double yGridLine = (axisHeight + margin - 1); yGridLine >= margin-1; yGridLine -= (double) axisHeight / divisions) {
        g.setColor(colorRed);
        // Draw major tick
        if (verticalDivisionCounter%5 == 0) g.drawLine(margin, (int) yGridLine, majorTickLength+margin, (int) yGridLine);
        // Draw minor tick
        g.drawLine(margin+(majorTickLength-minorTickLength), (int) yGridLine, margin+majorTickLength, (int) yGridLine);
        // Draw grid line
        g.setColor(colorGridLine);
        g.drawLine(1+margin+majorTickLength, (int) yGridLine, width - margin, (int) yGridLine);
        verticalDivisionCounter++;
      }

      // Draw horizontal axis label
      // NOTE: the (x,y) in g.drawString() refer to the bottom left of the text
      g.setColor(colorRed);
      g.setFont(new Font("Courier", 0, 10));
      g.drawString("Quefrenz (ms)", width - 85, height - 2);
      
      for (int i = 0; i <= divisions; i++) {
        double qufr = i * 100 / (double) refToMain.sampleRate * samples / 2.0;
        qufr = ((int) qufr * 10) / 10.0;
        str = String.valueOf(qufr);
        int xGridLine = (int) (i / 10.0 * axisWidth);
        // Draw horizontal axis values
        g.setColor(colorRed);
        g.drawString(str, xGridLine, height - 11);
        // Draw grid line
        g.setColor(colorGridLine);
        xGridLine = (int) (i / 10.0 * axisWidth) + (1+margin+majorTickLength);
        g.drawLine(xGridLine, height - (1+margin+majorTickLength), xGridLine, margin);
      }


      // Copy Timelinearray to buffer
      buffer = new float[samples];
      double start =
          refToMain.selectedstartold + refToMain.pp.wnf * refToMain.selecedwidthold;
      startp = (int) (start * refToMain.samplesAll);
      if (startp > (refToMain.samplesAll - samples)) {
        startp = refToMain.samplesAll - samples;
        mouseIsInTimeSpan = true;
      }
      for (int i = 0; i < samples; i++) {
        tempbyte = refToMain.reader.audioStream.get(startp + i);
        buffer[i] = tempbyte.floatValue();
      }

      // Calculate Cepstrum Transform
      CepstrumTransform ct = new CepstrumTransform();
      cepstrum = ct.doCepstrumTransform(buffer, refToMain.gad.ccep.isSelected());
      
      // Smooth out the Cepstrum
      if (refToMain.gad.ccepsmooth.isSelected()) {
        cepstrum[0] = 0.0f;
        cepstrum[2] = (cepstrum[1] + cepstrum[2] + cepstrum[3] + cepstrum[4]) / 4.0f;
        cepstrum[1] = (cepstrum[0] + cepstrum[1] + cepstrum[3]) / 3.0f;
        cepstrum[0] = (cepstrum[0] + cepstrum[1]) / 3.0f;
        cepstrum[samples - 3] = (cepstrum[samples - 2] + cepstrum[samples - 3] + cepstrum[samples - 4]) / 3.0f;
        cepstrum[samples - 2] = (cepstrum[samples - 1] + cepstrum[samples - 2] + cepstrum[samples - 3]) / 3.0f;
        cepstrum[samples - 1] = (cepstrum[samples - 1] + cepstrum[samples - 2]) / 2.0f;
        for (int i = 3; i < (samples - 3); i++)
          cepstrum[i] =
                    ( cepstrum[i]
                    + cepstrum[i + 1]
                    + cepstrum[i - 1]
                    + cepstrum[i + 2]
                    + cepstrum[i - 2]
                    + cepstrum[i + 3]
                    + cepstrum[i - 3])
                        / 7.0f;
      }
      // Search for Peak
      float peak = cepstrum[0];
      for (int i = 0; i < samples / 2; i++) {
        if (peak < cepstrum[i]) peak = cepstrum[i];
      }
      float normalizationFactor = axisHeight / peak;

      // Paint Spectrum
      double samplesPerPixel =  samples / 2.0 / axisWidth;
      double pixelsPerSample = 1.0 / samplesPerPixel;

      // Draw the histogram with dark gray 3 pixel thick line segments
      g.setStroke(new BasicStroke(3));
      g.setColor(colorDarkLine);
      int xStart;
      int xEnd;
      int yStart;
      int yEnd;
      for (double f = samplesPerPixel; f < samples / 2.0; f += samplesPerPixel) {
        yStart = (int) (cepstrum[(int) (f - samplesPerPixel)] * normalizationFactor);
        yEnd = (int) (cepstrum[(int) f] * normalizationFactor);
        xStart = (int) (f * pixelsPerSample - 1);
        xEnd = (int) (f * pixelsPerSample);
        
        g.drawLine(xStart + 21, height - 21 - yStart, xEnd + 21, height - 21 - yEnd);
      }

      // Draw over the histogram with light gray 1 pixel thick line segments
      g.setStroke(new BasicStroke(1));
      g.setColor(colorLightLine);
      for (double f = samplesPerPixel; f < samples / 2.0; f += samplesPerPixel) {
        // NOTE: In Java, the (int) typecast truncates the decimal part
        yStart = (int) (cepstrum[(int) (f - samplesPerPixel)] * normalizationFactor);
        yEnd = (int) (cepstrum[(int) f] * normalizationFactor);
        xStart = (int) (f * pixelsPerSample - 1);
        xEnd = (int) (f * pixelsPerSample);

        g.drawLine(xStart + 1 + margin + majorTickLength, height - (1 + margin + majorTickLength) - yStart,
                   xEnd + 1 + margin + majorTickLength, height - (1 + margin + majorTickLength) - yEnd);
      }
      // Mouse in time Span
      if (mouseIsInTimeSpan) {
        g.setFont(new Font("Dialog", 0, 9));
        g.setColor(colorRed);
        g.drawString("Mouse in time span", width - 110, 15);
      }
    }
    // -------------------------------------------------------------------------------------------------------------------------
  }
  // =========================================================================================================================
}
