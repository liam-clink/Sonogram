package de.dfki.sonogram;

import java.awt.*;
import java.awt.event.*;
import javax.swing.*;

import com.incors.plaf.kunststoff.KunststoffLookAndFeel;

/**
 * Copyright (c) 2010 Christoph Lauer @ DFKI, All Rights Reserved. clauer@dfki.de - www.dfki.de
 *
 * <p>Displays the welcome screen in the screen center.
 *
 * @author Christoph Lauer
 * @version 1.0, Current 29/03/2010
 */
public class WelcomeScreen extends JPanel {

  public boolean buttonPressed = false;
  private ImageIcon pic;
  private JWindow splash = new JWindow();
  private JButton button;
  // This special Strings for the Licensing will be later exchanged by the webshop system
  String UponStr = "WelcomeCalledFromStartFirstStart";

  // ------------------------------------------------------------------------------------------

  public WelcomeScreen(boolean show) {
    // set the LAF for the button
    try {
      com.incors.plaf.kunststoff.KunststoffLookAndFeel kunststoffLF =
          new com.incors.plaf.kunststoff.KunststoffLookAndFeel();
      KunststoffLookAndFeel.setCurrentTheme(new com.incors.plaf.kunststoff.KunststoffTheme());
      UIManager.setLookAndFeel(kunststoffLF);
      SwingUtilities.updateComponentTreeUI(this);
    } catch (Exception t) {
    }
    if (!show) return;

    // add the image
    pic = new ImageIcon(Sonogram.class.getResource("Welcome.png"));
    setLocation(0, 0);
    setSize(pic.getIconWidth(), pic.getIconHeight());
    splash.getContentPane().setLayout(null);
    splash.getContentPane().add(this);
    splash.setSize(pic.getIconWidth(), pic.getIconHeight());

    // add the button
    button =
        new JButton(
            "« Continue »"); // do not save as UTF8 Format because the special charcter ! Known to
                             // be work well --> Iso8859-1(windows) and CRLF(windows)
    button.setLocation(158, 291);
    button.setSize(430, 30);
    button.addActionListener(
        new ActionListener() {
          public void actionPerformed(ActionEvent e) {
            buttonPressed = true;
            splash.dispose();
          }
        });
    splash.getContentPane().add(button);

    // place in the middle of the screen
    Dimension dim = Toolkit.getDefaultToolkit().getScreenSize();
    int w = splash.getSize().width;
    int h = splash.getSize().height;
    int x = (dim.width - w) / 2;
    int y = (dim.height - h) / 2;
    splash.setBounds(x, y, w, h);
    splash.setVisible(true);
    button.setVisible(true);
    repaint();
    splash.toFront();
  }
  // ------------------------------------------------------------------------------------------
  @Override
  public void paintComponent(Graphics g) {
    pic.paintIcon(splash, g, 0, 0);
    button.repaint();
  }
}
// ------------------------------------------------------------------------------------------
