/**
 * Copyright (c) 2001 Christoph Lauer @ DFKI, All Rights Reserved. clauer@dfki.de - www.dfki.de
 *
 * <p>Main class of <b>Sonogramm visible speech</b> Sonogram is an generic and a in many forms
 * configurable acoustical signal processing tool. It uses Java Media Framework and Java3D an
 * external libraries, both are available fron Oracle. This CLASS implements also the MAIN
 * CLASS.
 *
 * @version 2.8.0, Begin 05/01/2002, Current Time-stamp: <2004-10-23 09:03:58 christoph>
 * @author Christoph Lauer
 */
package de.dfki.sonogram;

import de.dfki.maths.*;
import java.awt.*;
import java.awt.datatransfer.*;
import java.awt.event.*;
import java.io.*;
import java.util.*;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import javax.imageio.ImageIO;
import javax.swing.*;
import javax.swing.event.*;
import javax.swing.plaf.*;
import javax.swing.plaf.metal.MetalLookAndFeel;

import com.incors.plaf.kunststoff.KunststoffLookAndFeel;

public class Sonogram extends JFrame implements ActionListener, MouseListener {
  static final int BUILD = 8515;
  static final String VERSION = "5.0";
  public boolean firststart = false;
  boolean infovisible = false;
  boolean spectrumExist = false; // Flag that prevent painting bevore Transformation
  boolean updateimageflag = true;
  boolean openingflag = false; // is set while file opening is make
  boolean transformflag = false; // is set during transformation is make
  boolean energyflag = false; // Is set by Trafo from gad
  boolean openenedbysvg = false;
  boolean autoopened = false; // set when file is autoopened and unset from DataSourceReader
  boolean playbuttonpressed = false;
  boolean stopbuttonpressed = false;
  Boolean shaking = false; // flag stores if the ZinButton shaking
  static boolean java3disinstalled = true;
  public boolean d3ison = false;
  short timewindowlength = 0; // length of Timewindow to Transform
  byte[] timeline = new byte[4000];
  int samplestotal = 0; // Number of used samples
  int samplesall = 0; // Number of all samples obtain from DataSourceReader
  int peakx = 0; // Peak in Time in Spec-Units
  int peaky = 0; // Peak in Frequency in Frequ-Diff Units
  int samplerate = 0; // Set from GAD
  int zoompreviousindex = 0; // For Zoomback
  int gadx = 200;
  int gady = 200;
  int gadw = 434;
  int gadh = 432;
  String url;
  String selectedFilter;
  String filepath;
  String sysopt; // Systemoptions generated in Construktor
  double selectedstart = 0.0;
  double selecedwidth = 1.0;
  double selectedstartold = 0.0;
  double selecedwidthold = 1.0;
  EFileChooser chooser = new EFileChooser(); // File-open-chooser
  public DataSourceReader reader = new DataSourceReader(); // To read Mediafiles
  private Surface surplot;
  SonoProgressMonitor progressMonitor;
  HelpDialog hd = new HelpDialog(this);
  LicenseDialog ld = new LicenseDialog(this);
  CepstrumView cv = new CepstrumView(this);
  WaveletView av = new WaveletView(this);
  FormatView fv = new FormatView(this);
  LinearPredictionView lv = new LinearPredictionView(this);
  WaveformView wv = new WaveformView(this);
  AutoCorrelationView kv = new AutoCorrelationView(this);
  PitchDetectorView pv = new PitchDetectorView(this);
  OpenFromUrl ou = new OpenFromUrl(this);
  InfoDialog infod = new InfoDialog(this);
  SelectLookAndFeelDialog slaf = new SelectLookAndFeelDialog(this);
  LiveAnalyzer la = new LiveAnalyzer(this);
  GeneralAdjustmentDialog gad;
  ExportSpectrumSVG svg = new ExportSpectrumSVG(this);
  public Vector<float[]> spectrum = new Vector<>(); // Vector for FFT Transformations
  public Vector<String> filehistory = new Vector<>(); // vector for file history
  Vector<Double> selectedstartpre = new Vector<>(); // For Zoomback
  Vector<Double> selectedwidthpre = new Vector<>(); // For Zoomback
  ButtonGroup bg = new ButtonGroup(); // To change Color and B/W view
  ButtonGroup bgwf = new ButtonGroup(); // For Submenu WinFunkt
  PaintPanel pp = new PaintPanel(this);
  PlaySound player;
  WvSettingsDialog wvconfig;
  JLabel colorLabel;

  JLabel sep1;
  JLabel sep2;
  JLabel sep3;
  JLabel sep4;
  JLabel sep5;
  JLabel sep6;
  JLabel sep7;

  JButton openbutton;
  JButton adjbutton;
  JButton helpbutton;
  JButton quitbutton;
  
  // Toolbar Buttons
  JButton playbutton;
  JButton stopbutton;
  JButton revbutton;
  JButton d3button;
  JButton cepbutton;
  JButton zinbutton;
  JButton zbabutton;
  JButton forbutton;
  JButton infobutton;
  JButton lpcbutton;
  JButton zprebutton;
  JButton svgbutton;
  JButton wavbutton;
  JButton walbutton;
  JButton autocorrelationbutton;
  JButton pitchbutton;
  JButton arrangebutton;
  JButton fullbutton;
  JButton logbutton;
  JButton smoothfrbutton;
  JButton smoothtmbutton;
  JButton wvbutton;
  JButton recbutton;
  JButton gridbutton;
      
  JMenuItem quitItem;
  JMenuItem openItem;
  JMenuItem aboutItem;
  JMenuItem adjItem;
  JMenuItem playItem;
  JMenuItem revItem;
  JMenuItem d3Item;
  
  // Menuitems - MainWindow
  JMenuItem stopItem;
  JMenuItem helpItem;
  JMenuItem cepItem;
  JMenuItem zinItem;
  JMenuItem zbaItem;
  JMenuItem forItem;
  JMenuItem infoItem;
  JMenuItem lpcItem;
  JMenuItem zpreItem;
  JMenuItem lafItem;
  JMenuItem delhistItem;
  JMenuItem printItem;
  JMenuItem saveItem;
  JMenuItem logfrItem;
  JMenuItem csvItem;
  JMenuItem svgItem;
  JMenuItem sysItem;
  JMenuItem memItem;
  JMenuItem impItem;
  JMenuItem defaultItem;
  JMenuItem webItem;
  JMenuItem wavItem;
  JMenuItem fulItem;
  JMenuItem walItem;
  JMenuItem arrItem;
  JMenuItem closeItem;
  JMenuItem autocorrelationItem;
  JMenuItem pitchItem;
  JMenuItem feedbackItem;
  JMenuItem licenseItem;
  JMenuItem welcomeItem;
  JMenuItem recordItem;
  JMenuItem splashItem;
  JMenuItem[] hotlist;
  JMenu menuFile;

  // Menuitems - submenu WinFunkt
  JRadioButtonMenuItem hamItem;
  JRadioButtonMenuItem rectItem;
  JRadioButtonMenuItem blaItem;
  JRadioButtonMenuItem hanItem;
  JRadioButtonMenuItem triItem;
  JRadioButtonMenuItem welItem;
  JRadioButtonMenuItem gauItem;
  JRadioButtonMenuItem flaItem;
  JRadioButtonMenuItem harItem;
  JRadioButtonMenuItem cosItem;
  JRadioButtonMenuItem asyItem;
  
  // Menuitems - Radiobattons MW
  JRadioButtonMenuItem colItem;
  JRadioButtonMenuItem bwItem;
  JRadioButtonMenuItem firecItem;
  JRadioButtonMenuItem fireItem;
  JRadioButtonMenuItem rainItem;
  JRadioButtonMenuItem greenItem;

  JCheckBoxMenuItem negItem; // toogle negative View
  JCheckBoxMenuItem gridItem; // toogle Gridview
  JCheckBoxMenuItem logItem; // logartihm view
  Sonogram reftosonogram = this;
  JToolBar toolBar = new JToolBar();
  JSlider colorSlider = new JSlider(SwingConstants.HORIZONTAL, 0, 5, 1);

  // For Fullscreen
  Dimension normaldimension = new Dimension(0, 0); // used in fullscreen to atore old dimension
  Point normalpoint = new Point(0, 0); // used in Fullscreen to store old pos
  boolean fullscreen = false; // FullScreen flag
  static SonoSplashScreen splash; // Splash Screen
  static WelcomeScreen welcome;
  String filename = "no name"; // used in Splash Screen
  static boolean plugin = false; // Plugin Flag
  public boolean fileisfromurl = false;
  static long startTime = System.nanoTime();
  static double startupSeconds;
  Color ibgcol;
  public String storedurl;

  // settings initalized from the SonogramConfig.xml file for the GAD
  boolean iinv;
  boolean iloga;
  boolean igrid;
  boolean itooltip;
  boolean iautowinl;
  boolean inorsi;
  boolean ismof;
  boolean ismosi;
  boolean ienergy;
  boolean iloglpc;
  boolean ilogfour;
  boolean iopen8;
  boolean ismot;
  boolean ipide;
  boolean ipifrlim;
  boolean ienov;
  boolean isavehi;
  boolean ipitchfog;
  boolean ipitchblack;
  boolean ipitchsm;
  boolean ispecpl;
  boolean ilooppl;
  boolean imonoso;
  boolean ilogf;
  boolean isascpo;
  boolean isaco;
  boolean iffttrans;
  boolean iopenlast;
  boolean ilastwithzoom;
  boolean iceplog;
  boolean iwavelines;
  boolean imute;
  boolean ilocalpeak;
  boolean iuniverse;
  boolean iwallog;
  boolean isarr;
  boolean iacdl; // autocorrelation connect to points
  boolean iacsmooth;
  boolean iacpitch;
  boolean iantialise;
  boolean iantialisconfirmed;
  boolean iwvconfirmed;
  boolean iwfnorm;
  boolean icepsmooth;
  boolean irotate;
  boolean iperantialias;
  boolean ipercoord;
  boolean ipraway;
  boolean ipsmo;
  boolean iprsil;
  boolean ipclin;
  boolean iptrack;
  boolean ipfog;
  boolean i3dpolygons;
  boolean mp3confirm;
  boolean perkeysconfirm;
  static boolean javaWinDeco = false;

  int iswalsel;
  int iswaloct;
  int islws;
  int islwf;
  int islov;
  int islsdr;
  int islff;
  int islfl;
  int isllc;
  int islf;
  int islls;
  int isllff;
  int islsy;
  int islsx;
  int islpf;
  int islla;
  int islcep;
  int islwavetime;
  int isldb;
  int isc;
  int isr;
  int ilaf;
  int isacwl; // autocorrelation window length
  int isacws; // autocorrelation window shift
  int isacpwl;
  int isacpws;
  int isacpmax;
  static final int isetu = 10; // the demo minutes
  static final String Plugo =
      "PluginParameterFromExternalCalto"; // others in SonoSplashScreen and WelcomeScreen

  // -------------------------------------------------------------------------------------------------------------------------

  /**
   * Constructor for the Sonogram application. Handles the WindowClosing event and make
   * initialisations for the Filechooser, the Toolbar and the Menubar.
   */
  public Sonogram(String openpath) {
    // demo mode implementation
    if (Licenses.zoda1.equals("HIRN1HIRN1HIRN1HIRN1HIRN1HIRN1HI")) {
      java.util.TimerTask action =
          new java.util.TimerTask() {
            public void run() {
              System.out.println("--> Trial mode has expired...");
              java.awt.Toolkit.getDefaultToolkit().beep();
              Object[] options = {"Ok", "Open Homepage"};
              int n =
                  JOptionPane.showOptionDialog(
                      Sonogram.this,
                      "<html><i>The trial time has expired. Sonogram will quit now.<br>You can"
                          + " obtain the full version from the Sonogram homepage.",
                      "Trial Time expired...",
                      JOptionPane.YES_NO_OPTION,
                      JOptionPane.INFORMATION_MESSAGE,
                      null, // do not use a custom Icon
                      options, // the titles of buttons
                      options[0]); // default button title

              // JOptionPane.showOptionDialog(Sonogram.this,"<html><i>The trial time has expired.
              // Sonogram will quit now.<br>You can obtain the full version from the Sonogram
              // homepage."/*:<br><font size=4><a
              // href=http://www.christoph-lauer.de>www.christoph-lauer.de</a>"*/,"Trial Time
              // expired...",JOptionPane.INFORMATION_MESSAGE);
              System.out.println("--> Bye");
              saveConfig();
              if (n == 1)
                try {
                  Desktop.getDesktop()
                      .browse(new java.net.URI("http://www.christoph-lauer.de/store"));
                } catch (Exception e) {
                  e.printStackTrace();
                }
              System.exit(0);
            }
          };
      java.util.Timer caretaker = new java.util.Timer();
      caretaker.schedule(action, isetu * 60 * 1000);
    }

    splash.setProgress(10, "Check ID called from Plugin 10%");
    setSize(1025, 550);
    int scw = (int) java.awt.Toolkit.getDefaultToolkit().getScreenSize().getWidth();
    int sch = (int) java.awt.Toolkit.getDefaultToolkit().getScreenSize().getHeight();
    setLocation(scw / 2 - getWidth() / 2, sch / 2 - getHeight() / 2 + 100);

    // Check if Sonogram is called from Plugin
    // the plugin functionality may be outdated...
    try {
      // Check Class for exist
      Class sap = Class.forName("de.dfki.nite.gram.tools.SonogramCommunication");
      System.out.println("SONOGRAM STARTED AS PLUGIN.");
      plugin = true;
      // Generate Signature for funktion to get Funktion
      Class[] signature = new Class[1];
      signature[0] = this.getClass();
      // get funktion
      java.lang.reflect.Method function = sap.getMethod("sonogramCallBackSetReference", signature);
      // Generate parameter for funktion
      Object[] parameter = new Object[1];
      parameter[0] = this;
      // call static funktion, therefore null as class object
      function.invoke(null, parameter);
    } catch (Throwable throwable) {
      plugin = false;
    }

    // first we locking for capslock
    splash.setProgress(14, "Check for activated CapsLock 14%");
    Toolkit kit = Toolkit.getDefaultToolkit();
    boolean capsLock = false;
    try {
      capsLock = kit.getLockingKeyState(KeyEvent.VK_CAPS_LOCK);
      if (capsLock)
        System.out.println("--> CAPSLOCKCAPSLOCKCAPSLOCKCAPSLOCKCAPSLOCKCAPSLOCKCAPSLOCKCAPSLOCKCAPSLOCK");
    } catch (Exception e) {
      System.out.println("--> Error while get the CapsLock state" + e.toString());
    }
    System.out.println("--> check for the deletion of the config file with CapsLock = " + capsLock);
    if (capsLock) {
      Object[] options = {"Delete and Launch", "Delete and Quit", "Cancel"};
      int confirm =
          JOptionPane.showOptionDialog(
              this,
              "<html>Your <u>CapsLock</u> is activated. This special key is used in Sonogram"
                  + " while<br>startup to <u>delete the configuration file</u> from the disk to"
                  + " reset Sonogram.<br>Note that all stored settings are lost if you delete the"
                  + " configuration file.<br><br><font size=4>Do you want to delete the"
                  + " Configuration File ?",
              "Delete the Configuration File and reset Sonogram ?",
              JOptionPane.YES_NO_OPTION,
              JOptionPane.WARNING_MESSAGE,
              null,
              options,
              options[2]);
      if (confirm == JOptionPane.OK_OPTION || confirm == 1) {
        System.out.println("--> Delete the Configuration File");
        File f = new File("SonogramConfig.xml");
        if (!f.delete())
          JOptionPane.showMessageDialog(
              this,
              "<html><font color=#DD0000>Error</font> while delte the <u>SonogramConfig.xml</u>"
                  + " File !",
              "Can't Delete Config File",
              JOptionPane.ERROR_MESSAGE);
        if (confirm == 1) System.exit(0);
      }
    }

    // read startup settings from the SonogramConf.xml configuration file
    splash.setProgress(12, "Read out the Configuration from the SonogramConf.xml File 12%");
    ReadConfig rf = new ReadConfig(this);
    if (!isaco) {
      splash.setProgress(15, "Initialize  Default Dettings 15%");
      initGadToDefault();
    }
    wvconfig = new WvSettingsDialog(this);

    // print the welcome screen if no configuration file was found
    if (!rf.successfully) firststart = true;

    // instanciate the GUI elements
    splash.setProgress(20, "Create the Menue Bar 20%");
    createMenu();
    splash.setProgress(25, "Create the Sonogram Settings Dialog 25%");
    gad = new GeneralAdjustmentDialog(this);
    // change the icon
    splash.setProgress(30, "Set Window Icons 30%");
    Toolkit tk = Toolkit.getDefaultToolkit();
    setIconImage(tk.getImage(Sonogram.class.getResource("Sonogram.gif")));

    // set the Tooltip colors and the window title
    splash.setProgress(35, "Change the ToolTip Colors 35%");
    UIManager.put("ToolTip.foreground", new ColorUIResource(Color.red));
    UIManager.put("ToolTip.background", new ColorUIResource(new Color(194, 251, 0)));
    splash.setProgress(35, "35% Add the Paint Panel to Main Window.");
    setTitle("Sonogram Visible Speech - version " + VERSION);
    getContentPane().add(pp);

    // instanciate further  GUI elements
    splash.setProgress(40, "Instanciate the Tool Bar Buttons 40%");
    toolBar.setBorderPainted(true);
    addButtons(toolBar);
    enableItems(false);

    // disable some toolbar elements
    splash.setProgress(45, "Disable some Toolbar Buttons 45%");
    zprebutton.setEnabled(false);
    zpreItem.setEnabled(false);
    stopItem.setEnabled(false);
    closeItem.setEnabled(false);
    stopbutton.setEnabled(false);
    colorSlider.setEnabled(false);
    colorLabel.setEnabled(false);

    toolBar.putClientProperty("JToolBar.isRollover", Boolean.TRUE);

    splash.setProgress(50, "Add Toolbar to Sonogram's Main Window 50%");
    getContentPane().add(toolBar, BorderLayout.NORTH);

    splash.setProgress(60, "Create the File-Filters for the File-Chooser 60%");
    chooser.setApproveButtonText("Okay"); // Initsequence for Filecooser
    chooser.setDialogTitle("Select an Audio or Video File");
    MediaFileFilter ffwave = new MediaFileFilter("wav", "Wave Audio (.wav)");
    MediaFileFilter ffau = new MediaFileFilter("au", "Sun Audio (.au)");
    MediaFileFilter ffaiff = new MediaFileFilter("aiff", "Apple Audio (.aiff)");
    MediaFileFilter ffavi = new MediaFileFilter("avi", "AVI Video (.avi)");
    MediaFileFilter ffswf = new MediaFileFilter("swf", "Flash SWF Video (.swf)");
    MediaFileFilter ffspl = new MediaFileFilter("spl", "Flash SPL Video (.spl)");
    MediaFileFilter ffgsm = new MediaFileFilter("gsm", "GSM Audio (.gsm)");
    MediaFileFilter ffmvr = new MediaFileFilter("mvr", "MVR IBM HotMedia (.mvr)");
    MediaFileFilter ffmpg = new MediaFileFilter("mpg", "MPEG-1 Layer I (.mpg)");
    MediaFileFilter ffmp2 = new MediaFileFilter("mp2", "MPEG-1 Layer II (.mp2)");
    MediaFileFilter ffmp3 = new MediaFileFilter("mp3", "MPEG-1 Layer III (.MP3)");
    MediaFileFilter ffmov = new MediaFileFilter("mov", "MOV QuickTime Video (.mov)");
    MediaFileFilter ffall = new MediaFileFilter("", "All Mediafiles");

    splash.setProgress(70, "Add the File-Filters to the File Chooser 70%");
    ffall.aceptAllMediaFiles(true);
    chooser.addChoosableFileFilter(ffall);
    chooser.addChoosableFileFilter(ffwave);
    chooser.addChoosableFileFilter(ffmp3);
    chooser.addChoosableFileFilter(ffau);
    chooser.addChoosableFileFilter(ffaiff);
    chooser.addChoosableFileFilter(ffavi);
    chooser.addChoosableFileFilter(ffswf);
    chooser.addChoosableFileFilter(ffspl);
    chooser.addChoosableFileFilter(ffgsm);
    chooser.addChoosableFileFilter(ffmvr);
    chooser.addChoosableFileFilter(ffmpg);
    chooser.addChoosableFileFilter(ffmp2);
    chooser.addChoosableFileFilter(ffmov);

    splash.setProgress(75, "Add Window Closing Event 75%");
    WindowListener wndCloser = new WindowAdapter() // Windowlistener
    {
      public void windowClosing(WindowEvent e) {
        System.out.println("--> Bye");
        saveConfig();
        if (plugin) {
          try // Call Plugin exit class
          {
            // Check Class for exist
            Class sap = Class.forName("de.dfki.nite.gram.tools.SonogramCommunication");
            // Generate Signature for funktion to get Funktion
            Class[] signature = new Class[0];
            // get funktion
            java.lang.reflect.Method function = sap.getMethod("sonogramCallBackEnd", signature);
            // Generate parameter for funktion
            Object[] parameter = new Object[0];
            // call static funktion, therefore null as class object
            function.invoke(null, parameter);
          } catch (Throwable throwable) {
            System.out.println("--> No Plugin Class not found.");
            plugin = false;
          }
          dispose();
          return;
        }
        System.exit(0);
      }
    };
    addWindowListener(wndCloser);
    reader.setMainRef(this);
    // Set Look and Feel
    splash.setProgress(80, "Enable the Sonogram \"Look And Feel\" 80%");
    setLookAndFeel(ilaf);
    slaf.slider.setValue(ilaf);
    // Check for AutoOpen
    splash.setProgress(90, "Check if any file should automaically be opened 90%");
    if (openpath != null) { // If Argument is given to main as parameter
      splash.setProgress(85, "Try to open the Media-File while Startup 85%");
      openFile(openpath); // from Konsole
    } else {

      if (filepath != null && iopenlast) {
        if (filepath.substring(0, 2).equals("ft")
            || filepath.substring(0, 2).equals("ht")) {
          splash.setProgress(95, "Ask user if remote Network File should be opened 95%");
          int confirm =
              JOptionPane.showOptionDialog(
                  this,
                  "<html><i>The last opened file in Sonogram was a remote network file:</i><br>URL:"
                      + " <u>"
                      + filepath
                      + "</u><br><br><i><font size = 4>Continue <u><font color=#660000>open this"
                      + " remote File</font></u> over the Network ?",
                  "Open last Remote Network File ?",
                  JOptionPane.YES_NO_OPTION,
                  JOptionPane.QUESTION_MESSAGE,
                  null,
                  null,
                  null);
          if (confirm == 0) {
            splash.setProgress(100, "Open Remote-File over the Network 100%");
            setVisible(true);
            fileisfromurl = true;
            openFile(filepath); // Open from Network
            System.out.println("--> File automatical opened via Network.");
          } else {
            zoompreviousindex = 0;
            selectedstartold = 0.0;
            selecedwidthold = 1.0;
            selecedwidth = 1.0;
            selectedstart = 0.0;
            filepath = null;
          }
        } else {
          File file = new File(filepath.substring(5));
          if (file.exists()) { // Check for file if existsy
            splash.setProgress(100, "Automatical open the last File 100%");
            setVisible(true);
            fileisfromurl = false;
            openFile(filepath); // Open from local file system
            System.out.println("--> Open automatical sound file from the local System");
          } else { // If file do
            messageBox(
                "Error while automatical opening a File",
                "<html>You have selected the automatical open feature<br>while statup in the"
                    + " Sonogram-Settings-Dialog<br>but the <u><font color=#660000>stored history"
                    + " file does not exist</u></font> !<br><br>File: <i><u>"
                    + filepath,
                1);
            splash.setProgress(95, "The last opened File does not Exist ! 95%");
            zoompreviousindex = 0;
            selectedstartold = 0.0;
            selecedwidthold = 1.0;
            selecedwidth = 1.0;
            selectedstart = 0.0;
            filepath = null;
          }
        }
      }
    }
    splash.setProgress(100, "100% Sonogram is coming up now !!!");

    if (firststart) {
      welcome = new WelcomeScreen(true);
      while (!welcome.buttonPressed) {
        try {
          Thread.sleep(10);
        } catch (Exception e) {
        }
      }
      // show the license here
      // ld.setVisible(true);
    }
    setMinimumSize(new Dimension(400, 225));
    setVisible(true);
    if (isarr && iopenlast) {
      arrangeWindows();
    }
    /*				// DEBUG
    				gad.setVisible(true);
    				gad.p1.setSelectedIndex(1);
    */ }

  // -------------------------------------------------------------------------------------------------------------------------
  /**
   * The main function allocates the Sonogram class and intialise the backbone system. Splash screen
   * in called here and the JMF and Java3D classes are checked here for existing.
   */
  public static void main(String[] args) {
    splash = new SonoSplashScreen(false);
    startTime = System.nanoTime();
    welcome = new WelcomeScreen(false);
    String openpath;
    splash.setProgress(1, "This is Sonogram " + VERSION + " BUILD" + BUILD + " 1%");
    System.out.println("\n     ******************************************");
    System.out.println("     *             SONOGRAM " + VERSION + "               *");
    System.out.println("     *              BUILD " + BUILD + "                *");
    System.out.println("     *            Visible Speech              *");
    System.out.println("     *         christoph.lauer@me.com         *");
    System.out.println("     *      License:     FREE  License 1.0    *");
    System.out.println("     * Time-stamp:<08/28/2021 14:31:44 chris> *");
    System.out.println("     ******************************************");
    System.out.println(" ____                                              ");
    System.out.println("/ ___|  ___  _ __   ___   __ _ _ __ __ _ _ __ ___  ");
    System.out.println("\\___ \\ / _ \\| '_ \\ / _ \\ / _` | '__/ _` | '_ ` _ \\ ");
    System.out.println(" ___) | (_) | | | | (_) | (_| | | | (_| | | | | | |");
    System.out.println("|____/ \\___/|_| |_|\\___/ \\__, |_|  \\__,_|_| |_| |_|");
    System.out.println("                         |___/                     ");
    System.out.println("\n--> Hello");
    splash.setProgress(2, "Check if the Kunststoff theme is available 2%");
    openpath = null;

    // first read the selected LAF from the configuration
    int laf = -1;
    try {
      BufferedReader rd = new BufferedReader(new FileReader("SonogramConfig.xml"));
      String line, tmp;
      int t1, t2;
      while ((line = rd.readLine()) != null) {
        if (line.indexOf("LookAndFeel") > 0) {
          t1 = line.indexOf("value=");
          t1 += 7;
          t2 = line.indexOf("\"", t1);
          tmp = line.substring(t1, t2);
          laf = Integer.parseInt(tmp);
        }
      }
      rd.close();
    } catch (Throwable throwable) {
      System.out.println("--> No Confoguration File available.");
    }
    try {
      // and set the LAF to the Windows Frame
      if (laf != 6) {
        // System.out.println(laf);
        javaWinDeco = true;
        Toolkit.getDefaultToolkit().setDynamicLayout(true);
        JDialog.setDefaultLookAndFeelDecorated(true);
        JFrame.setDefaultLookAndFeelDecorated(true);
      }
    } catch (Throwable throwable) {
      System.out.println("--> NO JAVA 1.4 (NO Dynamic GUI)");
    }
    // check for java media Framework
    try {
      Class.forName("javax.media.Player");
    } catch (Throwable throwable) {
      JOptionPane.showMessageDialog(
          null,
          "<html><i>Java Media Framework is <u>not installed</u>.<br>Please look at"
              + " www.java.sun.com.",
          "JMF ERROR",
          JOptionPane.ERROR_MESSAGE);
      System.exit(0);
    } // Check for kunststoff.jar
    try {
      Class.forName("com.incors.plaf.kunststoff.KunststoffTheme");
    } catch (Throwable throwable) {
      System.out.println("--> Kunststoff.jar is not available !!!");
      JOptionPane.showMessageDialog(
          null,
          "<html><i>Could not create Sonogram mainclass.\n"
              + "Please include Sonogram.jar in Classpath.",
          "Sonogram Class not found !",
          JOptionPane.ERROR_MESSAGE);
      if (plugin == true) {
        try // Call Plugin exit class
        {
          // Check Class for exist
          Class sap = Class.forName("de.dfki.nite.gram.tools.SonogramCommunication");
          // Generate Signature for funktion to get Funktion
          Class[] signature = new Class[0];
          // get funktion
          java.lang.reflect.Method function = sap.getMethod("sonogramCallBackEnd", signature);
          // Generate parameter for funktion
          Object[] parameter = new Object[0];
          // call static funktion, therefore null as class object
          function.invoke(null, parameter);
        } catch (Throwable t) {
          System.out.println("--> No Plugin Class not found.");
          plugin = false;
        }
        return;
      }
      System.exit(0);
      return;
    }

    splash.setProgress(4, "Check if the Java Media Framework is available 4%");
    openpath = null;
    // Check if JMF is installed
    try {
      Class.forName("javax.media.Player");
    } catch (Throwable throwable) {
      System.out.println("--> Java Media Framework is not installed !!!");
      JOptionPane.showMessageDialog(
          null,
          "Java Media Framework is not installed !\n"
              + "Sonogram can not start.\n"
              + "See at www.java.sun.com\n"
              + "and install it.\n"
              + "http://java.sun.com/products/java-media/jmf/",
          "JMF not found",
          0);
      if (plugin) {
        try // Call Plugin exit class
        {
          // Check Class for exist
          Class sap = Class.forName("de.dfki.nite.gram.tools.SonogramCommunication");
          // Generate Signature for function to get Function
          Class[] signature = new Class[0];
          // get function
          java.lang.reflect.Method function = sap.getMethod("sonogramCallBackEnd", signature);
          // Generate parameter for function
          Object[] parameter = new Object[0];
          // call static function, therefore null as class object
          function.invoke(null, parameter);
        } catch (Throwable t) {
          System.out.println("SONOGRAM CALLBACK CLASS NOT FOUND.");
          plugin = false;
        }
        return;
      }
      System.exit(0);
      return;
    }
    splash.setProgress(6, "Check if Java3D is available 6%");
    String operatingSystem = System.getProperties().getProperty("os.name");
    System.out.println("--> Operating System: " + operatingSystem);
    // 	if (OS.equals("Mac OS X") == false) {
    try {
      Class.forName("javax.media.j3d.Canvas3D");
    } catch (Throwable th) {
      System.out.println("--> Java3D is not installed !!!");
      System.out.println(th);
      JOptionPane.showMessageDialog(
          null,
          "<html><i>The <u>Java3D extension is not installed</u> ! In order to generate<br>the 3D"
              + " Perspectograms you must have Java3D installed. Java3D<br>can be obtained from"
              + " www.java.com. Sonogram will start up<br>altought normal without Java3D, but the"
              + " <u>3D Perspectogram<br>functionality is disabled<u>.",
          "Please install Java3D",
          JOptionPane.WARNING_MESSAGE);
      java3disinstalled = false;
    }
    splash.setProgress(8, "Check for PlugIn or Start Mode 8%");
    if (args.length == 0) System.out.println("--> No argument given. Start in normal mode.");
    else if (args.length == 1) {
      openpath = args[0];
      System.out.println("--> Try to open File: " + openpath);
    } else if (args.length == 2) {
      // Here SMARTKOM handling
    } else if (args.length > 1)
      System.out.println("--> If you want to open a File, please give ONE filename as argument.");
    splash.setProgress(9, "Instanciate Sonogram's main Application Class 9%");
    try {
      Sonogram theApp = new Sonogram(openpath);
    } catch (Exception e) {
      System.out.println("--> Unknown Error in overall MAIN " + e);
      e.printStackTrace();
      JOptionPane.showMessageDialog(
          null,
          "<html><font size = 4 color = #660000>An Unhandled Error occoured!!!<br><font size = 5"
              + " color = #FF0000>"
              + e,
          "Unhandled Error Occoured",
          JOptionPane.ERROR_MESSAGE);
    }
    System.out.println("--> main Class terminated.");

    long endTime = System.nanoTime();
    long elapsedTime = endTime - startTime;
    startupSeconds = Math.round(elapsedTime / 1.0E06) / 1000.0;
    System.out.println("--> Startup Time: " + startupSeconds + " seconds");
  }

  // -------------------------------------------------------------------------------------------------------------------------
  // Set the Look and Feel
  public void setLookAndFeel(int which) {
    try {
      // PLAF-Klasse auswaehlen
      if (which == 0) {
        javax.swing.plaf.metal.MetalLookAndFeel mlaf =
            new javax.swing.plaf.metal.MetalLookAndFeel();
        MetalLookAndFeel.setCurrentTheme(new javax.swing.plaf.metal.DefaultMetalTheme());
        UIManager.setLookAndFeel(mlaf);
        System.out.println("--> Change LookAndFeel to METAL");
      } else if (which >= 1 && which <= 5) {
        com.incors.plaf.kunststoff.KunststoffLookAndFeel kunststoffLF =
            new com.incors.plaf.kunststoff.KunststoffLookAndFeel();
        if (which == 1)
          KunststoffLookAndFeel.setCurrentTheme(new com.incors.plaf.kunststoff.KunststoffTheme());
        if (which == 2)
          KunststoffLookAndFeel.setCurrentTheme(
              new com.incors.plaf.kunststoff.themes.KunststoffDesktopTheme());
        if (which == 3)
          KunststoffLookAndFeel.setCurrentTheme(
              new com.incors.plaf.kunststoff.themes.KunststoffNotebookTheme());
        if (which == 4)
          KunststoffLookAndFeel.setCurrentTheme(
              new com.incors.plaf.kunststoff.themes.KunststoffPresentationTheme());
        if (which == 5)
          KunststoffLookAndFeel.setCurrentTheme(new com.incors.plaf.kunststoff.KunststoffTheme());
        UIManager.setLookAndFeel(kunststoffLF);
        System.out.println("--> Change LookAndFeel to PLASTIC");
      } else if (which == 6) {
        UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        System.out.println("--> Change LookAndFeel to native OS");
      } else if (which == 7) {
        javax.swing.plaf.metal.MetalLookAndFeel mlaf =
            new javax.swing.plaf.metal.MetalLookAndFeel();
        MetalLookAndFeel.setCurrentTheme(new javax.swing.plaf.metal.OceanTheme());
        UIManager.setLookAndFeel(mlaf);
        System.out.println("--> Change LookAndFeel to OCEAN");
      }
      ilaf = which;
      SwingUtilities.updateComponentTreeUI(this);
      SwingUtilities.updateComponentTreeUI(infod);
      SwingUtilities.updateComponentTreeUI(hd);
      SwingUtilities.updateComponentTreeUI(ld);
      SwingUtilities.updateComponentTreeUI(ou);
      SwingUtilities.updateComponentTreeUI(slaf);
      slaf.setSize(slaf.getPreferredSize());
      SwingUtilities.updateComponentTreeUI(chooser);
      SwingUtilities.updateComponentTreeUI(svg);
      SwingUtilities.updateComponentTreeUI(wvconfig);
      SwingUtilities.updateComponentTreeUI(la);
      SwingUtilities.updateComponentTreeUI(cv);
      SwingUtilities.updateComponentTreeUI(av);
      SwingUtilities.updateComponentTreeUI(fv);
      SwingUtilities.updateComponentTreeUI(lv);
      SwingUtilities.updateComponentTreeUI(wv);
      SwingUtilities.updateComponentTreeUI(kv);
      SwingUtilities.updateComponentTreeUI(pv);
      if (wvconfig.frame != null) SwingUtilities.updateComponentTreeUI(wvconfig.frame);
      UIManager.getLookAndFeelDefaults().put("ClassLoader", getClass().getClassLoader());
      if (gad != null) SwingUtilities.updateComponentTreeUI(gad);
    } catch (Exception e) {
      messageBox(
          "Error in Look-And-Feel switch",
          "<html>Error while <u><font color=#660000>change the Look-And-Feel</u></font> Theme."
              + " Perhaps the<br>selected Look-And-Feel is not supported on your OS.",
          2);
    }
  }

  // -------------------------------------------------------------------------------------------------------------------------
  class CalcThread extends Thread {
    double i;
    int index;
    int transformationlength;
    int offset;
    Byte b;
    boolean ffttrans;
    float[] windowbuffer;

    CalcThread(
        int argIndex, double argI, int argTransformationlength, int argOffset, float[] argWindowBuffer) {
      this.index = argIndex;
      this.i = argI;
      this.transformationlength = argTransformationlength;
      this.offset = argOffset;
      this.windowbuffer = argWindowBuffer;
      ffttrans = gad.rfft.isSelected();
      
      setPriority(Thread.MIN_PRIORITY);
    }

    @Override
    public void run() {
      float[] spekbuffer = null;
      FastFourierTransform fft = new FastFourierTransform();
      LinearPredictionTransform lpt = new LinearPredictionTransform();
      if (progressMonitor.isCanceled()) {
        System.out.println("--> CANCEL Button is pressed while Transformation.");
        setCursor(new Cursor(Cursor.DEFAULT_CURSOR));
        progressMonitor.close();
        setTitle("Sonogram Visible Speech - version " + VERSION);
        spectrumExist = false;
        updateimageflag = true;
        repaint();
        return;
      }
      progressMonitor.setProgress(
          10 + (int) (i / (samplestotal - timewindowlength) * 70.0));
      float[] timebuffer = new float[transformationlength]; // allokate the buffer for timeSignal
      offset = (int) (samplesall * selectedstart); // offset for marked selection
      for (int v = 0; v < transformationlength; v++) { // Copy from DataSourceReader
        b = reader.audioStream.get(v + (int) i + offset);
        if (ffttrans) timebuffer[v] = b.floatValue() * windowbuffer[v];
        else timebuffer[v] = b.floatValue();
      }
      if (!ffttrans) // Select Transformtion
        spekbuffer =
            LinearPredictionTransform.doLinearPredictionTransform(
                timebuffer, timewindowlength, gad.sliderlpccoef.getValue(), windowbuffer);
      else spekbuffer = fft.doFFT(timebuffer); // Transformation Call for FFT
      // Smooth over Frequency
      if (gad.csmooth.isSelected()) { // if Smooth Spektrum ower frequency is set
        float[] spekbuffertmp = new float[timewindowlength / 2];
        for (int k = 2; k < (timewindowlength / 2 - 2); k++)
          spekbuffertmp[k] =
              (spekbuffer[k - 2]
                      + spekbuffer[k - 1]
                      + spekbuffer[k]
                      + spekbuffer[k + 1]
                      + spekbuffer[k + 2])
                  / 5.0f;
        spekbuffertmp[1] =
            (spekbuffertmp[0] + spekbuffertmp[1] + spekbuffertmp[2]) / 3.0f; // BOUNDS
        spekbuffertmp[0] = (spekbuffertmp[0] + spekbuffertmp[1]) / 2.0f;
        spekbuffertmp[timewindowlength / 2 - 2] =
            (spekbuffertmp[timewindowlength / 2 - 1]
                    + spekbuffertmp[timewindowlength / 2 - 2]
                    + spekbuffertmp[timewindowlength / 2 - 3])
                / 3.0f;
        spekbuffertmp[timewindowlength / 2 - 1] =
            (spekbuffertmp[timewindowlength / 2 - 1] + spekbuffertmp[timewindowlength / 2 - 2])
                / 2.0f;
        spectrum.set(index, spekbuffertmp); // Adding Transformvector to Spektrum
      } else spectrum.set(index, spekbuffer);
    }
  }

  // -------------------------------------------------------------------------------------------------------------------------
  /**
   * This function is called from DataSourceReader when all reading-Threads are back of from other
   * functions. It cuts the audio-samplevector in pieces and transformet them with the FFT
   * Transformation. Also it generates a time or energy View for the bottom of mainwindow. this
   * function catch all Exceptions and display a MessageBox.
   */
  public void readerIsBack() {
    try {
      // Initialize some stuf for spectrum, transformation...
      float[] spekbuffer = null;
      Byte b; // Temp
      short sqrtsize; // Squareroot of Vector-length
      FastFourierTransform fft = new FastFourierTransform();
      LinearPredictionTransform lpt = new LinearPredictionTransform();
      byte logdualis = 1;
      int offset = 0;
      int transformationlength = 0;
      boolean ffttrans = gad.rfft.isSelected(); // Which Transformation is Selected
      // Initialize stuff for TimeLineArray
      double timetostreamfakt;
      int streampoint; // Point in Samples-Array
      byte timelinemax = -128;
      byte timelinemin = 127;
      Byte b1, b2;
      int energywinlength = 50;
      double sum;
      // Progressdialog and Initalize some stuff
      if (gad.highlightedbutton < 3) gad.highLightButton(0);
      progressMonitor = new SonoProgressMonitor(reftosonogram, "", "", 0, 100);
      progressMonitor.setProgress(1);
      progressMonitor.setNote("Initialize for generating Sonogram for File: " + filename);
      transformflag = true;
      setCursor(new Cursor(Cursor.WAIT_CURSOR));
      openingflag = false;
      System.out.println("--> Begin Transformation.");
      spectrum.removeAllElements();
      // Store Old Numbers to calculate inner zooms
      selectedstartold = selectedstart;
      selecedwidthold = selecedwidth;
      samplesall = reader.audioStream.size();
      samplestotal = (int) ((double) samplesall * selecedwidth);
      timetostreamfakt = samplesall / 2000.0; // Faktor to read the Timeline-Vektor
      // if Auto-length is selected
      if (gad.cauto.isSelected() == true) {
        timewindowlength = 1;
        sqrtsize = (short) Math.sqrt(samplestotal * 2);
        do {
          timewindowlength *= 2;
          logdualis++;
        } while (timewindowlength < sqrtsize);
        // timewindowlength *= 2;
        gad.sliderwinsize.setValue(logdualis);
      } else timewindowlength = (short) Math.pow(2.0, gad.sliderwinsize.getValue());
      // allokate Windowbuffer
      float windowbuffer[] = null;
      // For LPC-Transformation Transformationlength is "previous Samples" in GAD
      Runtime runtime = Runtime.getRuntime();
      int NTHREADS = runtime.availableProcessors();
      if (ffttrans == false) { // LPC
        progressMonitor.setNote(
            "Calculate the Spectrum with the Linear-Predictive-Coding-Transformation for the File: "
                + " «"
                + filename
                + "» (using "
                + NTHREADS
                + " cores)");
        transformationlength = (short) gad.sliderlpcsamfutur.getValue();
        System.out.println("--> transformation length for LPC: " + transformationlength);
      } else {
        progressMonitor.setNote(
            "Calculate the Spectrum with the Fast-Fourier-Transformation for the File:  «"
                + filename
                + "» (using "
                + NTHREADS
                + " cores)");
        transformationlength = timewindowlength; // FFT
      }
      System.out.println("--> Use " + transformationlength + " transformationlegth");
      // Then calculate Windowingbuffers which is selected in GeneralAdjustmentDialog
      int winfunclen = 0;
      if (ffttrans) winfunclen = transformationlength;
      else winfunclen = timewindowlength;
      if (hamItem.isSelected()) {
        windowbuffer = WindowFunction.hammingWindow(winfunclen);
        selectedFilter = "Hamming";
      }
      if (hanItem.isSelected()) {
        windowbuffer = WindowFunction.hanningWindow(winfunclen);
        selectedFilter = "Hanning";
      }
      if (blaItem.isSelected()) {
        windowbuffer = WindowFunction.blackmanWindow(winfunclen);
        selectedFilter = "Blackman";
      }
      if (rectItem.isSelected()) {
        windowbuffer = WindowFunction.rectangleWindow(winfunclen);
        selectedFilter = "Rectangle";
      }
      if (triItem.isSelected()) {
        windowbuffer = WindowFunction.triangleWindow(winfunclen);
        selectedFilter = "Triangle";
      }
      if (gauItem.isSelected()) {
        windowbuffer = WindowFunction.gaussWindow(winfunclen);
        selectedFilter = "Gauss";
      }
      if (welItem.isSelected()) {
        windowbuffer = WindowFunction.welchWindow(winfunclen);
        selectedFilter = "Welch";
      }
      if (flaItem.isSelected()) {
        windowbuffer = WindowFunction.flattopWindow(winfunclen);
        selectedFilter = "Flat-Top";
      }
      if (harItem.isSelected()) {
        windowbuffer = WindowFunction.harrisWindow(winfunclen);
        selectedFilter = "Harris";
      }
      if (cosItem.isSelected()) {
        windowbuffer = WindowFunction.cosineWindow(winfunclen);
        selectedFilter = "Cosine";
      }
      if (asyItem.isSelected()) {
        windowbuffer = WindowFunction.asymetricalWindow(winfunclen);
        selectedFilter = "Cosine";
      }
      progressMonitor.setProgress(10);
      double overlapping = gad.sliderwinspeed.getValue();
      if (!gad.coverlapping.isSelected()) overlapping = 1.0;

      // **********************************/
      // START MAIN TRANSFORMATION LOOP
      // START OF THE PARALLELIZATION
      int index = 0;
      // precalc the size
      for (double i = 0; i < (samplestotal - transformationlength); i += timewindowlength / overlapping) {
        index++;
      }
      spectrum.setSize(index);
      // the core parallezation loops
      System.out.println("--> Max Number of parallel FFT Threads: " + NTHREADS);
      index = 0;
      ExecutorService executor = Executors.newFixedThreadPool(NTHREADS);
      for (double i = 0; i < (samplestotal - transformationlength); i += timewindowlength / overlapping) {
        CalcThread thread = new CalcThread(index, i, transformationlength, offset, windowbuffer);
        executor.execute(thread);
        index++;
      }
      // wait until all threads have finished
      executor.shutdown();
      while (!executor.isTerminated()) {}
      // END MAIN TRANSFORMATION LOOP
      // END OF THE PARALLELIZATION
      // **********************************/

      System.out.println("--> Transformation finished, generate  " + spectrum.size() + " spektras");
      System.out.println("--> Window lentgh: " + timewindowlength);
      // Smooth ower time is set
      if (gad.csmoothx.isSelected()) {
        float[] spekbuffersuba; // b,a,0,a,b
        float[] spekbuffersubb;
        float[] spekbufferadda;
        float[] spekbufferaddb;
        float[] spekbuffertmp;
        for (int time = 2; time < spectrum.size() - 2; time++) {
          spekbuffersubb = spectrum.get(time - 2);
          spekbuffersuba = spectrum.get(time - 1);
          spekbuffer = spectrum.get(time);
          spekbufferadda = spectrum.get(time + 1);
          spekbufferaddb = spectrum.get(time + 2);
          spekbuffertmp = new float[timewindowlength / 2];
          for (int frequency = 0; frequency < timewindowlength / 2; frequency++) {
            spekbuffertmp[frequency] =
                (spekbuffer[frequency]
                        + spekbufferadda[frequency]
                        + spekbufferaddb[frequency]
                        + spekbuffersuba[frequency]
                        + spekbuffersubb[frequency])
                    / 5.0f;
          }
          spectrum.setElementAt(spekbuffertmp, time);
        }
        for (int frequency = 0; frequency < timewindowlength / 2; frequency++) { // Bounds
          spekbuffer = spectrum.get(0);
          spekbuffersuba = spectrum.get(1);
          spekbuffersubb = spectrum.get(2);
          spekbuffersuba[frequency] =
              (spekbuffersubb[frequency] + spekbuffer[frequency] + spekbuffersuba[frequency])
                  / 3.0f;
          spekbuffer[frequency] = (spekbuffer[frequency] + spekbuffersuba[frequency]) / 2.0f;
          spekbuffer = spectrum.get(spectrum.size() - 1);
          spekbuffersuba = spectrum.get(spectrum.size() - 2);
          spekbuffersubb = spectrum.get(spectrum.size() - 3);
          spekbuffersuba[frequency] =
              (spekbuffersubb[frequency] + spekbuffer[frequency] + spekbuffersuba[frequency])
                  / 3.0f;
          spekbuffer[frequency] = (spekbuffer[frequency] + spekbuffersuba[frequency]) / 2.0f;
        }
      }
      System.out.println("--> Smoothed over time");
      // Logarithmic Amplitude
      if (logItem.isSelected()) {
        int logscale = 0;
        if (gad.sliderlog.getValue() == 1) logscale = 1;
        if (gad.sliderlog.getValue() == 2) logscale = 4;
        if (gad.sliderlog.getValue() == 3) logscale = 10;
        if (gad.sliderlog.getValue() == 4) logscale = 30;
        if (gad.sliderlog.getValue() == 5) logscale = 100;
        double logkonst = 255.0 / Math.log(256.0);
        for (int time = 0; time < spectrum.size(); time++) {
          spekbuffer = spectrum.get(time);
          for (int frequency = 0; frequency < timewindowlength / 2; frequency++) {
            spekbuffer[frequency] =
                (int) (Math.log(spekbuffer[frequency] * logscale + 1) * logkonst);
          }
        }
      }
      System.out.println("--> Logarithmical amplitude");
      // Generate Timelinearray
      if (gad.cenergy.isSelected()) energyflag = true; // Check in GAD
      else energyflag = false;
      for (int t = 0; t < 2000; t++) { // Copy and find min/max of timeline
        streampoint = (int) (t * timetostreamfakt);
        // If Energytimesignal is not Selected
        if (!energyflag && streampoint > 0 && streampoint < samplesall) { 
            b = reader.audioStream.get(streampoint);
            timeline[t] = b.byteValue();
        }
        if (energyflag) { // If Energytimesignal is Selected
          if ((streampoint > energywinlength) && (streampoint < samplesall - energywinlength)) {
            sum = 0.0f;
            for (int i = 0; i < energywinlength; i++) {
              b1 = reader.audioStream.get(streampoint + i);
              b2 = reader.audioStream.get(streampoint - i);
              sum += Math.pow(b1.doubleValue(), 2.0);
              sum += Math.pow(b2.doubleValue(), 2.0);
            }
            timeline[t] = (byte) Math.sqrt(sum / energywinlength / 2);
          } else timeline[t] = 0;
        }
        if (timeline[t] < timelinemin) timelinemin = timeline[t];
        if (timeline[t] > timelinemax) timelinemax = timeline[t];
      }
      // Normalize Timeline
      progressMonitor.setNote("Normalize the Spectrogram");
      progressMonitor.setProgress(85);
      float peak;
      if (timelinemax > -timelinemin) peak = timelinemax;
      else peak =  -timelinemin;
      for (int t = 0; t < 2000; t++) {
        timeline[t] = (byte) (timeline[t] / peak * 127.0f);
      }
      // Some Stuff at end of This Routine
      System.out.println("--> begin normalize");
      normalizeSpectrum();
      progressMonitor.setProgress(88);
      spectrumExist = true;
      if (player != null) {
        // player.close();
        player.timeThread.interrupt();
      }
      player = new PlaySound(url, this);
      updateimageflag = true;
      openingflag = false;
      pp.plstart = 0.0;
      pp.plstop = 1.0;
      pp.plbutton = 0.0;
      progressMonitor.setNote("Build Menue Elements...");
      progressMonitor.setProgress(90);
      // Update the file here history
      // filehistory = VECTOR with STRING elements
      boolean alwaysinlist = false;
      for (int i = 0; i < filehistory.size(); i++) { // Check if Element exist
        String tmpstr = filehistory.get(i);
        if (tmpstr.equals(filepath)) alwaysinlist = true;
      }
      if (!alwaysinlist) { // Add Menue entry
        JMenuItem[] hotlisttmp = new JMenuItem[filehistory.size()];
        for (int i = 0; i < filehistory.size(); i++) { // Store old historylist
          hotlisttmp[i] = hotlist[i];
        }
        filehistory.addElement(filepath); // add String to Vector
        hotlist = new JMenuItem[filehistory.size()]; // Allocate new bigger MenueItem-array
        int newlen = filehistory.size(); // New Historysize
        for (int i = 0; i < newlen - 1; i++) { // Restore old Historylist
          hotlist[i] = hotlisttmp[i];
        }
        String tmp = filehistory.get(newlen - 1); // Get New String from new Path
        String tmpName = tmp;
        if (tmpName.length() > 32) tmpName = tmpName.substring(tmp.length() - 32);
        // add the new file to the history
        if (tmp.substring(0, 4).equals("http"))
          hotlist[newlen - 1] =
              new JMenuItem(
                  "<html>"
                      + (newlen - 1)
                      + "&ensp;<font color=A0A0B4>HTTP:</font>&ensp;<font size=-2>"
                      + tmpName,
                  new ImageIcon(Sonogram.class.getResource("fileMenue.gif")));
        if (tmp.substring(0, 3).equals("ftp"))
          hotlist[newlen - 1] =
              new JMenuItem(
                  "<html>"
                      + (newlen - 1)
                      + "&ensp;<font color=A0A0B4>FTP:</font>&ensp;&ensp;&thinsp;&thinsp;<font"
                      + " size=-2>"
                      + tmpName,
                  new ImageIcon(Sonogram.class.getResource("fileMenue.gif")));
        if (url.substring(0, 4).equals("file"))
          hotlist[newlen - 1] =
              new JMenuItem(
                  "<html>"
                      + (newlen - 1)
                      + "&ensp;<font color=6A0A0B4>FILE:</font>&emsp;<font size=-2>"
                      + tmpName,
                  new ImageIcon(Sonogram.class.getResource("fileMenue.gif")));

        menuFile.add(hotlist[newlen - 1], 10 + newlen);
        hotlist[newlen - 1].addActionListener(this);
        int num = newlen + 1;
        hotlist[newlen - 1].setToolTipText(
            "<html>Path to file"
                + num
                + ":<br><b>"
                + filehistory.get(newlen - 1)
                + "</b>");

        KeyStroke key = KeyStroke.getKeyStroke('0');
        if (newlen - 1 == 0)
          key =
              KeyStroke.getKeyStroke(
                  java.awt.event.KeyEvent.VK_0, java.awt.event.InputEvent.CTRL_DOWN_MASK);
        if (newlen - 1 == 1)
          key =
              KeyStroke.getKeyStroke(
                  java.awt.event.KeyEvent.VK_1, java.awt.event.InputEvent.CTRL_DOWN_MASK);
        if (newlen - 1 == 2)
          key =
              KeyStroke.getKeyStroke(
                  java.awt.event.KeyEvent.VK_2, java.awt.event.InputEvent.CTRL_DOWN_MASK);
        if (newlen - 1 == 3)
          key =
              KeyStroke.getKeyStroke(
                  java.awt.event.KeyEvent.VK_3, java.awt.event.InputEvent.CTRL_DOWN_MASK);
        if (newlen - 1 == 4)
          key =
              KeyStroke.getKeyStroke(
                  java.awt.event.KeyEvent.VK_4, java.awt.event.InputEvent.CTRL_DOWN_MASK);
        if (newlen - 1 == 5)
          key =
              KeyStroke.getKeyStroke(
                  java.awt.event.KeyEvent.VK_5, java.awt.event.InputEvent.CTRL_DOWN_MASK);
        if (newlen - 1 == 6)
          key =
              KeyStroke.getKeyStroke(
                  java.awt.event.KeyEvent.VK_6, java.awt.event.InputEvent.CTRL_DOWN_MASK);
        if (newlen - 1 == 7)
          key =
              KeyStroke.getKeyStroke(
                  java.awt.event.KeyEvent.VK_7, java.awt.event.InputEvent.CTRL_DOWN_MASK);
        if (newlen - 1 == 8)
          key =
              KeyStroke.getKeyStroke(
                  java.awt.event.KeyEvent.VK_8, java.awt.event.InputEvent.CTRL_DOWN_MASK);
        if (newlen - 1 == 9)
          key =
              KeyStroke.getKeyStroke(
                  java.awt.event.KeyEvent.VK_9, java.awt.event.InputEvent.CTRL_DOWN_MASK);
        hotlist[newlen - 1].setAccelerator(key);
      }
      // Some Stuff
      gad.highLightButton(0);
      progressMonitor.setProgress(100);
      repaint();
      enableItems(true);
      zprebutton.setEnabled(zoompreviousindex != 0);
      zpreItem.setEnabled(false);
      stopItem.setEnabled(false);
      stopbutton.setEnabled(false);
      if (!java3disinstalled) {
        d3button.setEnabled(false);
        d3Item.setEnabled(false);
      }
      if (openenedbysvg) {
        openenedbysvg = false;
        gad.btro.setEnabled(true);
        gad.sliderwinfunktion.setEnabled(true);
        gad.clog.setEnabled(true);
        gad.sliderlog.setEnabled(true);
        gad.csmoothx.setEnabled(true);
        gad.csmooth.setEnabled(true);
        gad.cenergy.setEnabled(true);
        gad.coverlapping.setEnabled(true);
        gad.sliderwinspeed.setEnabled(true);
        gad.sliderwinsize.setEnabled(true);
        gad.cauto.setEnabled(true);
        gad.csampl.setEnabled(true);
      }
      pp.updateWvButton();
      setTitle("Sonogram Visible Speech - version " + VERSION + " - " + filename);
      // And then EXCEPTIONHANDLING for all EXCEPTIONS
    } catch (Exception e) {
      System.out.println(e);
      e.printStackTrace();
      messageBox(
          "Error while the Sonogram generation",
          "<html>An <u><font color=#660000>error occoured while the generation</u></font> of the"
              + " Sonogram.<br>Please see the console output for more Details.",
          JOptionPane.ERROR_MESSAGE);
    } finally {
      progressMonitor.close();
      transformflag = false;
      setCursor(new Cursor(Cursor.DEFAULT_CURSOR));
    }
  }
  // -------------------------------------------------------------------------------------------------------------------------
  /**
   * This Method adds the Buttons to Toolbar and give them Eventlisteners. toolbar - Reference to
   * Toolbar-Object
   */
  protected void addButtons(JToolBar toolBar) {

    //  toolBar.setMargin(new Insets(0,0,0,0));
    //	toolBar.setBorderPainted(false);
    //	toolBar.setFloatable(false);
    //  toolBar.setRollover(true);
    toolBar.setLayout(new FlowLayout(FlowLayout.LEFT, 0, 0));

    Dimension buttonSize = new Dimension(30, 30);

    openbutton = new JButton(new ImageIcon(Sonogram.class.getResource("open.gif")));
    openbutton.setPreferredSize(buttonSize);
    openbutton.setMaximumSize(buttonSize);
    toolBar.add(openbutton);
    openbutton.setToolTipText(
        "<html>Open local <b>Audio</b> or <b>Video</b> File      <font color=black size=-2>Ctrl-O");
    adjbutton = new JButton(new ImageIcon(Sonogram.class.getResource("adj.gif")));
    adjbutton.setPreferredSize(buttonSize);
    adjbutton.setMaximumSize(buttonSize);
    toolBar.add(adjbutton);
    adjbutton.setToolTipText(
        "<html>All Sonogram settings are justified<br> in the <b>Sonogram-Settings-Dialog</b>     "
            + " <font color=black size=-2>Ctrl-A");
    recbutton = new JButton(new ImageIcon(Sonogram.class.getResource("rec.png")));
    recbutton.setPreferredSize(buttonSize);
    recbutton.setMaximumSize(buttonSize);
    toolBar.add(recbutton);
    recbutton.setToolTipText("<html><b>Record and preview</b> a Wave Wile");
    arrangebutton = new JButton(new ImageIcon(Sonogram.class.getResource("arr.gif")));
    arrangebutton.setPreferredSize(buttonSize);
    arrangebutton.setMaximumSize(buttonSize);
    arrangebutton.setToolTipText(
        "<html>Open <b>all analyze windows</b> and arrange them<br>"
            + "automatically to the optimal screen position.Press<br>"
            + "this button a scond time disable all analyze windows."
            + "<br><font color=black size=-2>F12");
    toolBar.add(arrangebutton);
    fullbutton = new JButton(new ImageIcon(Sonogram.class.getResource("full.gif")));
    fullbutton.setPreferredSize(buttonSize);
    fullbutton.setMaximumSize(buttonSize);
    fullbutton.setToolTipText(
        "<html>Zoom the main sonogram<br>Window to <b>Fullscreen</b>      <font color=black"
            + " size=-2><br><i>doubleclick</i> or <b>F11");
    toolBar.add(fullbutton);

    svgbutton = new JButton(new ImageIcon(Sonogram.class.getResource("svg.gif")));
    // svgbutton.setPreferredSize(buttonSize);
    // svgbutton.setMaximumSize(buttonSize);
    //	toolBar.add(svgbutton);
    // svgbutton.setToolTipText("Save spectrum as Scalable Vector Graphic (SVG file) Crtl-V");
    // javax.swing.JToolBar.Separator sep1 = new javax.swing.JToolBar.Separator();
    // toolBar.add(sep1);

    sep4 = new JLabel(new ImageIcon(Sonogram.class.getResource("separator.png")));
    toolBar.add(sep4);

    playbutton = new JButton(new ImageIcon(Sonogram.class.getResource("play.gif")));
    playbutton.setPreferredSize(buttonSize);
    playbutton.setMaximumSize(buttonSize);
    playbutton.setMnemonic(java.awt.event.KeyEvent.VK_SPACE);
    toolBar.add(playbutton);
    playbutton.setToolTipText(
        "<html><b>Play</b> the selected timespan      <font color=black size=-2>SPACE");
    revbutton = new JButton(new ImageIcon(Sonogram.class.getResource("rev.gif")));
    revbutton.setPreferredSize(buttonSize);
    revbutton.setMaximumSize(buttonSize);
    toolBar.add(revbutton);
    revbutton.setToolTipText(
        "<html><b>Reselect</b> the entire timespan      <font color=black size=-2>Ctrl-R");
    stopbutton = new JButton(new ImageIcon(Sonogram.class.getResource("stop.gif")));
    stopbutton.setPreferredSize(buttonSize);
    stopbutton.setMaximumSize(buttonSize);
    toolBar.add(stopbutton);
    stopbutton.setToolTipText(
        "<html><b>Stop</b> playing sound      <font color=black size=-2>Ctrl-T");
    // javax.swing.JToolBar.Separator sep2 = new javax.swing.JToolBar.Separator();
    // toolBar.add(sep2);

    sep5 = new JLabel(new ImageIcon(Sonogram.class.getResource("separator.png")));
    toolBar.add(sep5);

    forbutton = new JButton(new ImageIcon(Sonogram.class.getResource("for.gif")));
    forbutton.setPreferredSize(buttonSize);
    forbutton.setMaximumSize(buttonSize);
    toolBar.add(forbutton);
    forbutton.setToolTipText(
        "<html> <h2>F(f)=\u2131{f(t)}</h2>Shows the <b>FFT</b> Window      <font color=black"
            + " size=-2>Ctrl-F");

    lpcbutton = new JButton(new ImageIcon(Sonogram.class.getResource("lpc.gif")));
    lpcbutton.setPreferredSize(buttonSize);
    lpcbutton.setMaximumSize(buttonSize);
    toolBar.add(lpcbutton);
    lpcbutton.setToolTipText(
        "<html>Shows the <b>LPC</b> Window      <font color=black size=-2>Ctrl-L");

    wavbutton = new JButton(new ImageIcon(Sonogram.class.getResource("wav.gif")));
    wavbutton.setPreferredSize(buttonSize);
    wavbutton.setMaximumSize(buttonSize);
    toolBar.add(wavbutton);
    wavbutton.setToolTipText(
        "<html>Shows the <b>Waveform</b> dialog.<br>Displays the Amplitude Waveform in the time"
            + " domain  <font color=black size=-2>CTRL-W");

    autocorrelationbutton = new JButton(new ImageIcon(Sonogram.class.getResource("auc.gif")));
    autocorrelationbutton.setPreferredSize(buttonSize);
    autocorrelationbutton.setMaximumSize(buttonSize);
    toolBar.add(autocorrelationbutton);
    autocorrelationbutton.setToolTipText(
        "<html>Shows the <b>Autocorrelation</b> Window      <font color=black size=-2>Ctrl-A");

    cepbutton = new JButton(new ImageIcon(Sonogram.class.getResource("cep.gif")));
    cepbutton.setPreferredSize(buttonSize);
    lpcbutton.setMaximumSize(buttonSize);
    toolBar.add(cepbutton);
    cepbutton.setToolTipText(
        "<html>Shows the <b>Cepstrum</b> Window      <font color=black size=-2>Ctrl-C");

    pitchbutton = new JButton(new ImageIcon(Sonogram.class.getResource("pitch.gif")));
    pitchbutton.setPreferredSize(buttonSize);
    pitchbutton.setMaximumSize(buttonSize);
    toolBar.add(pitchbutton);
    pitchbutton.setToolTipText(
        "<html>Shows the <b>Pitch</b> tracking  Window      <font color=black size=-2>Ctrl-P");

    walbutton = new JButton(new ImageIcon(Sonogram.class.getResource("wal.gif")));
    walbutton.setPreferredSize(buttonSize);
    walbutton.setMaximumSize(buttonSize);
    toolBar.add(walbutton);
    walbutton.setToolTipText(
        "<html>Shows the <b>Wavelet</b> Window      <font color=black size=-2>Ctrl-H");
    // 	javax.swing.JToolBar.Separator sep3 = new javax.swing.JToolBar.Separator();
    // 	toolBar.add(sep3);
    sep1 = new JLabel(new ImageIcon(Sonogram.class.getResource("separator.png")));
    toolBar.add(sep1);

    zinbutton = new JButton(new ImageIcon(Sonogram.class.getResource("zin.gif")));
    zinbutton.setPreferredSize(buttonSize);
    zinbutton.setMaximumSize(buttonSize);
    toolBar.add(zinbutton);
    zinbutton.setToolTipText(
        "<html><b>Zoom IN</b> the selected Time Span      <font color=black size=-2>Ctrl-Z");

    zbabutton = new JButton(new ImageIcon(Sonogram.class.getResource("zba.gif")));
    zbabutton.setPreferredSize(buttonSize);
    zbabutton.setMaximumSize(buttonSize);
    toolBar.add(zbabutton);
    zbabutton.setToolTipText(
        "<html><b>Zoom OUT</b> to the entire Signal      <font color=black size=-2>Ctrl-B");

    zprebutton = new JButton(new ImageIcon(Sonogram.class.getResource("zpre.gif")));
    zprebutton.setPreferredSize(buttonSize);
    zprebutton.setMaximumSize(buttonSize);
    toolBar.add(zprebutton);
    zprebutton.setToolTipText(
        "<html><b>Zoom BACK</b> to previous selection<br>in the zoom history      <font color=black"
            + " size=-2>Ctrl-V");
    // 	javax.swing.JToolBar.Separator sep4 = new javax.swing.JToolBar.Separator();
    // 	toolBar.add(sep4);
    sep2 = new JLabel(new ImageIcon(Sonogram.class.getResource("separator.png")));
    toolBar.add(sep2);

    logbutton = new JButton(new ImageIcon(Sonogram.class.getResource("logbut.gif")));
    if (ilogf) logbutton.setBorder(BorderFactory.createLoweredBevelBorder());
    logbutton.setPreferredSize(buttonSize);
    logbutton.setMaximumSize(buttonSize);
    toolBar.add(logbutton);
    logbutton.setToolTipText(
        "<html><b>Linear/Logrithmic</b><br>Switch between the <b>linear</b> and<br> the"
            + " <b>logarithmical</b> frequency scale  <font color=black size=-2>Ctrl-L");
    smoothfrbutton = new JButton(new ImageIcon(Sonogram.class.getResource("smoothfr.gif")));
    if (ismof) smoothfrbutton.setBorder(BorderFactory.createLoweredBevelBorder());
    smoothfrbutton.setPreferredSize(buttonSize);
    smoothfrbutton.setMaximumSize(buttonSize);
    toolBar.add(smoothfrbutton);
    smoothfrbutton.setToolTipText(
        "<html><b>Smooth over Frequency</b><br>This function burnish the Sonogram image<br>in the"
            + " vertical frequency direction");
    smoothtmbutton = new JButton(new ImageIcon(Sonogram.class.getResource("smoothtm.gif")));
    if (ismot) smoothtmbutton.setBorder(BorderFactory.createLoweredBevelBorder());
    smoothtmbutton.setPreferredSize(buttonSize);
    smoothtmbutton.setMaximumSize(buttonSize);
    toolBar.add(smoothtmbutton);
    smoothtmbutton.setToolTipText(
        "<html><b>Smooth over Time</b><br>This function burnish the Sonogram image<br>in the"
            + " horizontal time direction");
    sep3 = new JLabel(new ImageIcon(Sonogram.class.getResource("separator.png")));
    gridbutton = new JButton(new ImageIcon(Sonogram.class.getResource("gridbut.gif")));
    if (igrid) gridbutton.setBorder(BorderFactory.createLoweredBevelBorder());
    gridbutton.setPreferredSize(buttonSize);
    gridbutton.setMaximumSize(buttonSize);
    toolBar.add(gridbutton);
    gridbutton.setToolTipText(
        "<html><b>Grid</b><br>Paint the main Sonogram with a Grid<font color=black size=-2>Ctrl-G");

    toolBar.add(sep3);
    infobutton = new JButton(new ImageIcon(Sonogram.class.getResource("info.gif")));
    infobutton.setPreferredSize(buttonSize);
    infobutton.setMaximumSize(buttonSize);
    infobutton.setToolTipText(
        "<html>Parameter, values and statistics<br> abuot the Signal and the Sonogram      <font"
            + " color=black size=-2>Ctrl-Y");
    toolBar.add(infobutton);
    d3button = new JButton(new ImageIcon(Sonogram.class.getResource("3d.gif")));
    d3button.setPreferredSize(buttonSize);
    d3button.setMaximumSize(buttonSize);
    toolBar.add(d3button);
    d3button.setToolTipText(
        "<html>Open the <b>3D Perspectogram</b> for<br>the Sonogram displayed in the<br>main window"
            + " <font color=black size=-2>Ctrl-S");
    wvbutton = new JButton(new ImageIcon(Sonogram.class.getResource("wv.gif")));
    wvbutton.setPreferredSize(buttonSize);
    wvbutton.setMaximumSize(buttonSize);
    wvbutton.setToolTipText(
        "<html><font size=5>Ultrazoom</font><br><font color = black>The"
            + " Pseudo-Smoothed-Wigner-Ville Transformation<br>is only available for very small"
            + " signals. To use this killer feature<br>you must zoom in, or select a small area"
            + " with the mouse. You can see<br>the numbers of Samples in the Information"
            + " Window.<br><br><font color = black><b>Hold [CTRL] or use Right-Mouse-Button for"
            + " direct Rendering.");
    toolBar.add(wvbutton);
    wvbutton.setEnabled(false);

    sep6 = new JLabel(new ImageIcon(Sonogram.class.getResource("separator.png")));
    toolBar.add(sep6);
    helpbutton = new JButton(new ImageIcon(Sonogram.class.getResource("help.gif")));
    helpbutton.setPreferredSize(buttonSize);
    helpbutton.setMaximumSize(buttonSize);
    toolBar.add(helpbutton);
    helpbutton.setToolTipText(
        "<html>Open the Sonogram <b>Online Help</b>      <font color=black size=-2>Ctrl-H");
    quitbutton = new JButton(new ImageIcon(Sonogram.class.getResource("quit.gif")));
    quitbutton.setPreferredSize(buttonSize);
    quitbutton.setMaximumSize(buttonSize);
    toolBar.add(quitbutton);
    sep7 = new JLabel(new ImageIcon(Sonogram.class.getResource("separator.png")));
    toolBar.add(sep7);

    quitbutton.setToolTipText(
        "<html><b>Exit</b> Sonogram without saving anything      <font color=black size=-2>Ctrl-Q");
    playbutton.addActionListener(this);
    arrangebutton.addActionListener(this);
    fullbutton.addActionListener(this);
    svgbutton.addActionListener(this);
    openbutton.addActionListener(this);
    recbutton.addActionListener(this);
    adjbutton.addActionListener(this);
    helpbutton.addActionListener(this);
    infobutton.addActionListener(this);
    quitbutton.addActionListener(this);
    stopbutton.addActionListener(this);
    revbutton.addActionListener(this);
    d3button.addActionListener(this);
    cepbutton.addActionListener(this);
    lpcbutton.addActionListener(this);
    zinbutton.addActionListener(this);
    zbabutton.addActionListener(this);
    zprebutton.addActionListener(this);
    forbutton.addActionListener(this);
    wavbutton.addActionListener(this);
    walbutton.addActionListener(this);
    autocorrelationbutton.addActionListener(this);
    pitchbutton.addActionListener(this);
    logbutton.addActionListener(this);
    smoothfrbutton.addActionListener(this);
    smoothtmbutton.addActionListener(this);
    gridbutton.addActionListener(this);

    logbutton.addMouseListener(this);
    smoothfrbutton.addMouseListener(this);
    smoothtmbutton.addMouseListener(this);
    wvbutton.addMouseListener(this);
    gridbutton.addMouseListener(this);

    colorLabel = new JLabel();
    colorLabel.setMaximumSize(new Dimension(60, 28));
    colorLabel.setMinimumSize(new Dimension(60, 28));
    if (firecItem.isSelected())
      colorLabel.setIcon(new ImageIcon(Sonogram.class.getResource("firecMenu.png")));
    if (fireItem.isSelected())
      colorLabel.setIcon(new ImageIcon(Sonogram.class.getResource("fireMenu.png")));
    if (colItem.isSelected())
      colorLabel.setIcon(new ImageIcon(Sonogram.class.getResource("exteMenu.png")));
    if (rainItem.isSelected())
      colorLabel.setIcon(new ImageIcon(Sonogram.class.getResource("rainMenu.png")));
    if (greenItem.isSelected())
      colorLabel.setIcon(new ImageIcon(Sonogram.class.getResource("greeMenu.png")));
    if (bwItem.isSelected())
      colorLabel.setIcon(new ImageIcon(Sonogram.class.getResource("whitMenu.png")));

    colorSlider.setMaximumSize(new Dimension(100, 30));
    colorSlider.setMinimumSize(new Dimension(100, 30));
    colorSlider.setPreferredSize(new Dimension(100, 30));
    colorSlider.setSnapToTicks(true);
    colorSlider.setMinorTickSpacing(1);
    colorSlider.setPaintTicks(true);
    colorSlider.addMouseListener(
        new MouseAdapter() {
          @Override
          public void mouseReleased(MouseEvent e) {
            switch (colorSlider.getValue()) {
              case (0):
                if (!firecItem.isSelected()) {
                  firecItem.setSelected(true);
                  gad.p1.setSelectedIndex(2);
                  updateimageflag = true;
                  repaint();
                  gad.r0.setSelected(true);
                }
                break;
              case (1):
                if (!fireItem.isSelected()) {
                  fireItem.setSelected(true);
                  gad.p1.setSelectedIndex(2);
                  updateimageflag = true;
                  repaint();
                  gad.r1.setSelected(true);
                }
                break;
              case (2):
                if (!colItem.isSelected()) {
                  colItem.setSelected(true);
                  gad.p1.setSelectedIndex(2);
                  updateimageflag = true;
                  repaint();
                  gad.r2.setSelected(true);
                }
                break;
              case (3):
                if (!rainItem.isSelected()) {
                  rainItem.setSelected(true);
                  gad.p1.setSelectedIndex(2);
                  updateimageflag = true;
                  repaint();
                  gad.r3.setSelected(true);
                }
                break;
              case (4):
                if (!greenItem.isSelected()) {
                  greenItem.setSelected(true);
                  gad.p1.setSelectedIndex(2);
                  updateimageflag = true;
                  repaint();
                  gad.r4.setSelected(true);
                }
                break;
              case (5):
                if (!bwItem.isSelected()) {
                  bwItem.setSelected(true);
                  gad.p1.setSelectedIndex(2);
                  updateimageflag = true;
                  repaint();
                  gad.r5.setSelected(true);
                }
                break;
            }
            int col = 0;
            if (gad.r0.isSelected()) col = 0;
            if (gad.r1.isSelected()) col = 1;
            if (gad.r2.isSelected()) col = 2;
            if (gad.r3.isSelected()) col = 3;
            if (gad.r4.isSelected()) col = 4;
            if (gad.r5.isSelected()) col = 5;
            wvconfig.colCombo.setSelectedIndex(col);
            la.spec.repaint();
          }
        });
    colorSlider.addChangeListener(
        new ChangeListener() {
          public void stateChanged(ChangeEvent e) {
            switch (colorSlider.getValue()) {
              case (0):
                colorLabel.setIcon(new ImageIcon(Sonogram.class.getResource("firecMenu.png")));
                break;
              case (1):
                colorLabel.setIcon(new ImageIcon(Sonogram.class.getResource("fireMenu.png")));
                break;
              case (2):
                colorLabel.setIcon(new ImageIcon(Sonogram.class.getResource("exteMenu.png")));
                break;
              case (3):
                colorLabel.setIcon(new ImageIcon(Sonogram.class.getResource("rainMenu.png")));
                break;
              case (4):
                colorLabel.setIcon(new ImageIcon(Sonogram.class.getResource("greeMenu.png")));
                break;
              case (5):
                colorLabel.setIcon(new ImageIcon(Sonogram.class.getResource("whitMenu.png")));
                break;
            }
          }
        });
    toolBar.add(colorSlider);
    toolBar.add(colorLabel);
    // this prevents resizing Sonogram to the the width of the Toolbar while resizing Sonogram
    // event...
    toolBar.setMinimumSize(new Dimension(400, 10));
  }
  // ------------------------------------------------------------------------------------------
  // this is only for the buttons which can be disabled
  public void mousePressed(MouseEvent e) {
    if (e.getSource() == gridbutton) {
      gad.p1.setSelectedIndex(3);
      gridItem.setSelected(!gridItem.isSelected());
      gad.cgrid.setSelected(gridItem.isSelected());
      if (gridItem.isSelected())
        gridbutton.setBorder(BorderFactory.createLoweredBevelBorder());
      else gridbutton.setBorder(BorderFactory.createEmptyBorder()); // fullbutton.getBorder());
    }
    if (e.getSource() == logbutton) {
      gad.p1.setSelectedIndex(15);
      logfrItem.setSelected(!logfrItem.isSelected());
      gad.cslogfr.setSelected(logfrItem.isSelected());
      if (logfrItem.isSelected())
        logbutton.setBorder(BorderFactory.createLoweredBevelBorder());
      else logbutton.setBorder(BorderFactory.createEmptyBorder()); // fullbutton.getBorder());
    }

    if (e.getSource() == smoothfrbutton) {
      gad.p1.setSelectedIndex(3);
      gad.csmooth.setSelected(!gad.csmooth.isSelected());
      if (gad.csmooth.isSelected())
        smoothfrbutton.setBorder(BorderFactory.createLoweredBevelBorder());
      else smoothfrbutton.setBorder(BorderFactory.createEmptyBorder()); // fullbutton.getBorder());
    }

    if (e.getSource() == smoothtmbutton) {
      gad.p1.setSelectedIndex(3);
      gad.csmoothx.setSelected(!gad.csmoothx.isSelected());
      if (gad.csmoothx.isSelected())
        smoothtmbutton.setBorder(BorderFactory.createLoweredBevelBorder());
      else smoothtmbutton.setBorder(BorderFactory.createEmptyBorder()); // fullbutton.getBorder());
    }

    if (e.getSource() == wvbutton) {
      if ((wvbutton.isEnabled()
              && (e.getModifiersEx() & java.awt.event.InputEvent.CTRL_DOWN_MASK)
                  == java.awt.event.InputEvent.CTRL_DOWN_MASK)
          || (wvbutton.isEnabled() && e.getButton() == MouseEvent.BUTTON3)) {
        int col = 0;
        if (gad.r0.isSelected()) col = 0;
        if (gad.r1.isSelected()) col = 1;
        if (gad.r2.isSelected()) col = 2;
        if (gad.r3.isSelected()) col = 3;
        if (gad.r4.isSelected()) col = 4;
        if (gad.r5.isSelected()) col = 5;
        wvconfig.colCombo.setSelectedIndex(col);
        wvconfig.renderType = WvSettingsDialog.RenderType.RENDER_2D;
        wvconfig.generateHandler();
      } else if (wvbutton.isEnabled()) {
        wvconfig.setVisible(true);
        wvconfig.shakeButton();
      }
    }
  }

  public void mouseClicked(MouseEvent e) {}

  public void mouseEntered(MouseEvent e) {}

  public void mouseExited(MouseEvent e) {}

  public void mouseReleased(MouseEvent e) {}
  // -------------------------------------------------------------------------------------------------------------------------
  /**
   * This funktion builds the Menuebar and his single items. Keyboard shortcuts are defined here.
   */
  private void createMenu() {
    menuFile = new JMenu("File");
    quitItem =
        new JMenuItem("Exit Sonogram", new ImageIcon(Sonogram.class.getResource("quit.gif")));
    impItem =
        new JMenuItem(
            "Open Sonogram from SVG", new ImageIcon(Sonogram.class.getResource("imp.gif")));
    recordItem =
        new JMenuItem("Audio Recorder", new ImageIcon(Sonogram.class.getResource("rec.png")));
    svgItem =
        new JMenuItem(
            "Export Sonogram as SVG Image File",
            new ImageIcon(Sonogram.class.getResource("svg.gif")));
    saveItem =
        new JMenuItem(
            "Export Sonogram as PNG Image File",
            new ImageIcon(Sonogram.class.getResource("save.gif")));
    csvItem =
        new JMenuItem(
            "Export Sonogram as CSV Text File",
            new ImageIcon(Sonogram.class.getResource("csv.gif")));
    printItem =
        new JMenuItem("Print Sonogram", new ImageIcon(Sonogram.class.getResource("print.gif")));
    openItem =
        new JMenuItem(
            "<html>Open <u>local</u> Media File",
            new ImageIcon(Sonogram.class.getResource("open.gif")));
    webItem =
        new JMenuItem(
            "<html>Open <u>remote</u> Media File",
            new ImageIcon(Sonogram.class.getResource("web.png")));
    closeItem = new JMenuItem("Close File", new ImageIcon(Sonogram.class.getResource("close.gif")));

    menuFile.setMnemonic('F');
    quitItem.setMnemonic('Q');
    svgItem.setMnemonic('V');
    csvItem.setMnemonic('T');
    openItem.setMnemonic('O');
    printItem.setMnemonic('P');
    saveItem.setMnemonic('B');
    impItem.setMnemonic('I');
    webItem.setMnemonic('W');
    webItem.setMnemonic('C');

    menuFile.add(openItem);
    menuFile.add(webItem);
    menuFile.add(recordItem);
    menuFile.add(impItem);
    menuFile.add(closeItem);
    menuFile.addSeparator();
    menuFile.add(saveItem);
    menuFile.add(svgItem);
    menuFile.add(csvItem);
    menuFile.add(printItem);
    menuFile.addSeparator();
    hotlist = new JMenuItem[filehistory.size()];
    int[] filecheck = new int[filehistory.size()];

    // Test for File for exist
    for (int i = 0; i < filehistory.size(); i++) {
      filecheck[i] = 0; // 0 = not available; 1 = available; 2 = http; 3 = ftp
      String str = filehistory.get(i);
      if (str.substring(0, 4).equals("file")) {
        str = str.substring(5);
        File testfile = new File(str);
        if (testfile.exists()) filecheck[i] = 1;
      }
      if (str.substring(0, 4).equals("http")) {
        filecheck[i] = 2;
      }
      else if (str.substring(0, 3).equals("ftp")) {
        filecheck[i] = 3;
      }
    }
    for (int i = 0; i < filehistory.size(); i++) {
      System.out.println("--> " + filehistory.get(i));
      String tmp = filehistory.get(i);
      if (tmp.length() > 32) tmp = tmp.substring(tmp.length() - 32);
      if (filecheck[i] == 0)
        hotlist[i] =
            new JMenuItem(
                "<html>"
                    + i
                    + "&ensp;<font color=A0A0B4>FILE:</font>&emsp;<font size=-2><strike>"
                    + tmp,
                new ImageIcon(Sonogram.class.getResource("fileMenue.gif")));
      if (filecheck[i] == 1)
        hotlist[i] =
            new JMenuItem(
                "<html>" + i + "&ensp;<font color=A0A0B4>FILE:</font>&emsp;<font size=-2>" + tmp,
                new ImageIcon(Sonogram.class.getResource("fileMenue.gif")));
      if (filecheck[i] == 2)
        hotlist[i] =
            new JMenuItem(
                "<html>" + i + "&ensp;<font color=A0A0B4>HTTP:</font>&ensp;<font size=-2>" + tmp,
                new ImageIcon(Sonogram.class.getResource("fileMenue.gif")));
      if (filecheck[i] == 3)
        hotlist[i] =
            new JMenuItem(
                "<html>"
                    + i
                    + "&ensp;<font color=A0A0B4>FTP:</font>&ensp;&ensp;&thinsp;&thinsp;<font"
                    + " size=-2>"
                    + tmp,
                new ImageIcon(Sonogram.class.getResource("hin.gif")));
      menuFile.add(hotlist[i]);
      hotlist[i].setToolTipText(
          "<html>Path to file" + i + ":<br><b>" + filehistory.get(i) + "</b>");
      hotlist[i].addActionListener(this);

      KeyStroke key = KeyStroke.getKeyStroke('0');
      if (i == 0)
        key =
            KeyStroke.getKeyStroke(
                java.awt.event.KeyEvent.VK_0, java.awt.event.InputEvent.CTRL_DOWN_MASK);
      if (i == 1)
        key =
            KeyStroke.getKeyStroke(
                java.awt.event.KeyEvent.VK_1, java.awt.event.InputEvent.CTRL_DOWN_MASK);
      if (i == 2)
        key =
            KeyStroke.getKeyStroke(
                java.awt.event.KeyEvent.VK_2, java.awt.event.InputEvent.CTRL_DOWN_MASK);
      if (i == 3)
        key =
            KeyStroke.getKeyStroke(
                java.awt.event.KeyEvent.VK_3, java.awt.event.InputEvent.CTRL_DOWN_MASK);
      if (i == 4)
        key =
            KeyStroke.getKeyStroke(
                java.awt.event.KeyEvent.VK_4, java.awt.event.InputEvent.CTRL_DOWN_MASK);
      if (i == 5)
        key =
            KeyStroke.getKeyStroke(
                java.awt.event.KeyEvent.VK_5, java.awt.event.InputEvent.CTRL_DOWN_MASK);
      if (i == 6)
        key =
            KeyStroke.getKeyStroke(
                java.awt.event.KeyEvent.VK_6, java.awt.event.InputEvent.CTRL_DOWN_MASK);
      if (i == 7)
        key =
            KeyStroke.getKeyStroke(
                java.awt.event.KeyEvent.VK_7, java.awt.event.InputEvent.CTRL_DOWN_MASK);
      if (i == 8)
        key =
            KeyStroke.getKeyStroke(
                java.awt.event.KeyEvent.VK_8, java.awt.event.InputEvent.CTRL_DOWN_MASK);
      if (i == 9)
        key =
            KeyStroke.getKeyStroke(
                java.awt.event.KeyEvent.VK_9, java.awt.event.InputEvent.CTRL_DOWN_MASK);
      hotlist[i].setAccelerator(key);
    }
    delhistItem =
        new JMenuItem(
            "<html><font size=-2>&emsp;&emsp;&emsp;&emsp;&emsp;<u>« Delete the File History »</u>",
            new ImageIcon(Sonogram.class.getResource("rem.gif")));
    menuFile.add(delhistItem);
    menuFile.addSeparator();
    menuFile.add(quitItem);

    JMenu menuOther = new JMenu("Options");
    adjItem =
        new JMenuItem(
            "Sonogram-Settings-Dialog", new ImageIcon(Sonogram.class.getResource("adj.gif")));
    defaultItem =
        new JMenuItem("Default Settings", new ImageIcon(Sonogram.class.getResource("def.gif")));
    arrItem =
        new JMenuItem("Arrange Windows", new ImageIcon(Sonogram.class.getResource("arr.gif")));
    lafItem =
        new JMenuItem("Select LookAndFeel", new ImageIcon(Sonogram.class.getResource("laf.gif")));
    JMenu windFunkt = new JMenu("Window Function");
    hamItem =
        new JRadioButtonMenuItem(
            "Hamming Window", new ImageIcon(Sonogram.class.getResource("ham.gif")));
    rectItem =
        new JRadioButtonMenuItem(
            "Rectangle Window", new ImageIcon(Sonogram.class.getResource("rect.gif")));
    blaItem =
        new JRadioButtonMenuItem(
            "Blackman Window", new ImageIcon(Sonogram.class.getResource("bla.gif")));
    hanItem =
        new JRadioButtonMenuItem(
            "Hanning Window", new ImageIcon(Sonogram.class.getResource("hen.gif")));
    welItem =
        new JRadioButtonMenuItem(
            "Welch Window", new ImageIcon(Sonogram.class.getResource("wel.gif")));
    triItem =
        new JRadioButtonMenuItem(
            "Triangle  Window", new ImageIcon(Sonogram.class.getResource("tri.gif")));
    gauItem =
        new JRadioButtonMenuItem(
            "Gaussian Window", new ImageIcon(Sonogram.class.getResource("gau.gif")), true);
    flaItem =
        new JRadioButtonMenuItem(
            "Flat-Top Window", new ImageIcon(Sonogram.class.getResource("fla.gif")), true);
    harItem =
        new JRadioButtonMenuItem(
            "Harris Window", new ImageIcon(Sonogram.class.getResource("ham.gif")), true);
    cosItem =
        new JRadioButtonMenuItem(
            "Cosine Window", new ImageIcon(Sonogram.class.getResource("wel.gif")), true);
    asyItem =
        new JRadioButtonMenuItem(
            "Asymetrical Window", new ImageIcon(Sonogram.class.getResource("asy.gif")), true);
    menuOther.setMnemonic('P');
    windFunkt.setMnemonic('W');
    lafItem.setMnemonic('L');
    adjItem.setMnemonic('A');
    defaultItem.setMnemonic('D');
    menuOther.setMnemonic('P');
    hamItem.setMnemonic('H');
    rectItem.setMnemonic('R');
    blaItem.setMnemonic('B');
    hanItem.setMnemonic('E');
    triItem.setMnemonic('T');
    welItem.setMnemonic('W');
    gauItem.setMnemonic('G');
    flaItem.setMnemonic('F');
    harItem.setMnemonic('F');
    cosItem.setMnemonic('C');
    asyItem.setMnemonic('A');
    defaultItem.setMnemonic('F');
    bgwf.add(hamItem);
    bgwf.add(rectItem);
    bgwf.add(blaItem);
    bgwf.add(hanItem);
    bgwf.add(triItem);
    bgwf.add(welItem);
    bgwf.add(gauItem);
    bgwf.add(gauItem);
    bgwf.add(flaItem);
    bgwf.add(harItem);
    bgwf.add(cosItem);
    bgwf.add(asyItem);
    windFunkt.add(rectItem);
    windFunkt.add(blaItem);
    windFunkt.add(triItem);
    windFunkt.add(hamItem);
    windFunkt.add(welItem);
    windFunkt.add(gauItem);
    windFunkt.add(hanItem);
    windFunkt.add(flaItem);
    windFunkt.add(harItem);
    windFunkt.add(cosItem);
    windFunkt.add(asyItem);
    firecItem =
        new JRadioButtonMenuItem(
            "Classical Fire Colors", new ImageIcon(Sonogram.class.getResource("firec.png")), true);
    fireItem =
        new JRadioButtonMenuItem(
            "Modern Fire Colors", new ImageIcon(Sonogram.class.getResource("fire.gif")), true);
    colItem =
        new JRadioButtonMenuItem(
            "Extended Fire Colors", new ImageIcon(Sonogram.class.getResource("col.gif")));
    rainItem =
        new JRadioButtonMenuItem(
            "Rainbow Colors", new ImageIcon(Sonogram.class.getResource("rai.gif")));
    greenItem =
        new JRadioButtonMenuItem(
            "Purple Colors", new ImageIcon(Sonogram.class.getResource("gre.gif")));
    bwItem =
        new JRadioButtonMenuItem(
            "Classical Black/White", new ImageIcon(Sonogram.class.getResource("bw.gif")));
    bg.add(firecItem);
    bg.add(fireItem);
    bg.add(colItem);
    bg.add(rainItem);
    bg.add(greenItem);
    bg.add(bwItem);
    negItem =
        new JCheckBoxMenuItem(
            "Inverse Color", new ImageIcon(Sonogram.class.getResource("neg.gif")));
    gridItem =
        new JCheckBoxMenuItem("Show Grid", new ImageIcon(Sonogram.class.getResource("grid.gif")));
    logItem =
        new JCheckBoxMenuItem(
            "Logarithmical Amplitude", new ImageIcon(Sonogram.class.getResource("log.gif")), true);
    logfrItem =
        new JCheckBoxMenuItem(
            "Logarithmical Frequency", new ImageIcon(Sonogram.class.getResource("logfr.gif")));
    fulItem =
        new JCheckBoxMenuItem(
            "<html>Fullscreen<font size=-3>&emsp;&emsp;(doubleklick)",
            new ImageIcon(Sonogram.class.getResource("ful.gif")));
    negItem.setMnemonic('I');
    menuOther.setMnemonic('M');
    colItem.setMnemonic('C');
    bwItem.setMnemonic('B');
    firecItem.setMnemonic('O');
    fireItem.setMnemonic('F');
    greenItem.setMnemonic('E');
    rainItem.setMnemonic('R');
    gridItem.setMnemonic('G');
    logItem.setMnemonic('L');

    menuOther.add(adjItem);
    menuOther.add(arrItem);
    menuOther.add(lafItem);
    menuOther.add(defaultItem);
    menuOther.addSeparator();
    menuOther.add(windFunkt);
    menuOther.addSeparator();
    menuOther.add(firecItem);
    menuOther.add(fireItem);
    menuOther.add(colItem);
    menuOther.add(rainItem);
    menuOther.add(greenItem);
    menuOther.add(bwItem);
    menuOther.addSeparator();
    menuOther.add(negItem);
    menuOther.addSeparator();
    menuOther.add(gridItem);
    menuOther.addSeparator();
    menuOther.add(logItem);
    menuOther.add(logfrItem);
    menuOther.addSeparator();
    menuOther.add(fulItem);

    JMenu menuSound = new JMenu("Signal");
    infoItem = new JMenuItem("Info", new ImageIcon(Sonogram.class.getResource("small_info.gif")));
    playItem = new JMenuItem("Play", new ImageIcon(Sonogram.class.getResource("small_play.gif")));
    revItem = new JMenuItem("Rewind", new ImageIcon(Sonogram.class.getResource("small_rev.gif")));
    stopItem = new JMenuItem("Stop", new ImageIcon(Sonogram.class.getResource("small_stop.gif")));
    cepItem = new JMenuItem("Cepstrum", new ImageIcon(Sonogram.class.getResource("small_cep.gif")));
    forItem =
        new JMenuItem("Fast Fourier", new ImageIcon(Sonogram.class.getResource("small_for.gif")));
    wavItem = new JMenuItem("Waveform", new ImageIcon(Sonogram.class.getResource("small_wav.gif")));
    lpcItem =
        new JMenuItem(
            "Linear Predictive Coding", new ImageIcon(Sonogram.class.getResource("small_lpc.gif")));
    zinItem =
        new JMenuItem("Zoom Marked", new ImageIcon(Sonogram.class.getResource("small_zin.gif")));
    zbaItem =
        new JMenuItem("Zoom Full", new ImageIcon(Sonogram.class.getResource("small_zba.gif")));
    zpreItem =
        new JMenuItem("Zoom Previous", new ImageIcon(Sonogram.class.getResource("small_zpre.gif")));
    d3Item =
        new JMenuItem("Perspectogram", new ImageIcon(Sonogram.class.getResource("small_3d.gif")));
    walItem = new JMenuItem("Wavelet", new ImageIcon(Sonogram.class.getResource("small_wal.gif")));
    autocorrelationItem =
        new JMenuItem(
            "Autocorrelation", new ImageIcon(Sonogram.class.getResource("small_auc.gif")));
    pitchItem =
        new JMenuItem("Pitch", new ImageIcon(Sonogram.class.getResource("small_pitch.gif")));
    d3Item.setMnemonic('S');
    menuSound.setMnemonic('S');
    infoItem.setMnemonic('I');
    playItem.setMnemonic('P');
    revItem.setMnemonic('R');
    stopItem.setMnemonic('T');
    cepItem.setMnemonic('C');
    lpcItem.setMnemonic('L');
    forItem.setMnemonic('F');
    zinItem.setMnemonic('Z');
    zbaItem.setMnemonic('B');
    zpreItem.setMnemonic('V');
    wavItem.setMnemonic('Y');
    autocorrelationItem.setMnemonic('A');
    pitchItem.setMnemonic('h');
    menuSound.add(infoItem);
    menuSound.addSeparator();
    menuSound.add(playItem);
    menuSound.add(revItem);
    menuSound.add(stopItem);
    menuSound.addSeparator();
    menuSound.add(d3Item);
    menuSound.addSeparator();
    menuSound.add(pitchItem);
    menuSound.add(forItem);
    menuSound.add(wavItem);
    menuSound.add(lpcItem);
    menuSound.add(cepItem);
    menuSound.add(walItem);
    menuSound.add(autocorrelationItem);
    menuSound.addSeparator();
    menuSound.add(zinItem);
    menuSound.add(zbaItem);
    menuSound.add(zpreItem);

    JMenu menuHelp = new JMenu("Help");
    aboutItem =
        new JMenuItem("About Sonogram", new ImageIcon(Sonogram.class.getResource("about.gif")));
    helpItem = new JMenuItem("Online Help", new ImageIcon(Sonogram.class.getResource("help.gif")));
    licenseItem =
        new JMenuItem("License", new ImageIcon(Sonogram.class.getResource("license.gif")));
    welcomeItem =
        new JMenuItem("Welcome Screen", new ImageIcon(Sonogram.class.getResource("welcome.gif")));
    splashItem =
        new JMenuItem("Splash Screen", new ImageIcon(Sonogram.class.getResource("splash.gif")));
    sysItem =
        new JMenuItem("Java System Info", new ImageIcon(Sonogram.class.getResource("ja.png")));
    memItem = new JMenuItem("Memory Monitor", new ImageIcon(Sonogram.class.getResource("Mem.png")));
    feedbackItem =
        new JMenuItem("« Submit Feedback »", new ImageIcon(Sonogram.class.getResource("mail.gif")));
    menuHelp.setMnemonic('H');
    aboutItem.setMnemonic('A');
    helpItem.setMnemonic('H');
    helpItem.setMnemonic('S');
    helpItem.setMnemonic('F');
    menuHelp.add(aboutItem);
    menuHelp.addSeparator();
    menuHelp.add(helpItem);
    menuHelp.add(sysItem);
    menuHelp.add(licenseItem);
    menuHelp.add(memItem);
    menuHelp.addSeparator();
    menuHelp.add(welcomeItem);
    menuHelp.add(splashItem);
    menuHelp.addSeparator();
    menuHelp.add(feedbackItem);

    JMenuBar menuBar = new JMenuBar();
    menuBar.add(menuFile);
    menuBar.add(menuOther);
    menuBar.add(menuSound);
    menuBar.add(menuHelp);
    setJMenuBar(menuBar);
    feedbackItem.setToolTipText(
        "<html><table border=0><colgroup><col width=30><col width=800></colgroup><tr><td><img src="
            + Sonogram.class.getResource("chris.jpg")
            + "></td><td>Pease provide me with a <b>Feedback</b> <br>message or a <b>Bug</b>"
            + " report.<br></td></tr></table></html>");

    quitItem.setToolTipText("<html><b>Exit</b> Sonogram without saving anything");
    openItem.setToolTipText("<html>Open <b>Video</b> or <b>Audio</b> File");
    closeItem.setToolTipText("<html><b>Close</b> the present open File");
    adjItem.setToolTipText("<html>Open <b>Sonogram-Settings-Dialog</b>");
    lafItem.setToolTipText("<html>Change the <b>Look And Feel");
    negItem.setToolTipText("<html>Paint the main Sonogram <br>in <b>Inverse Colors");
    gridItem.setToolTipText("<html>Paint the main Sonogram<br>with <b>Grid");
    logItem.setToolTipText("<html>Display the Somogram in <b>Logarithmical Amplitude</b> Scale");
    firecItem.setToolTipText("<html>Paint Sonogram in <b>Classical Fire</b> Colors");
    fireItem.setToolTipText("<html>Paint Sonogram in <b>Fire</b> Colors");
    rainItem.setToolTipText("<html>Paint Sonogram in <b>Rainbow</b> Colors");
    colItem.setToolTipText("<html>Paint Sonogram in <b>Extended Fire</b> Colors");
    bwItem.setToolTipText("<html>Paint Sonogram in <b>black/white</b> Colors");
    greenItem.setToolTipText("<html>Paint Sonogram in <b>Classical Green</b> Colors");
    infoItem.setToolTipText(
        "<html>The Information Dialog holds <b>Values</b> and<br><b>Statistics</b> about the Signal"
            + " and the Sonogram");
    d3Item.setToolTipText("<html>Generate the 3D <b>Perspectogram");
    playItem.setToolTipText("<html><b>Start</b> playing selected time span");
    revItem.setToolTipText("<html><b>Rewind</b> selection to entire time");
    stopItem.setToolTipText("<html><b>Stop</b> playing");
    cepItem.setToolTipText("<html>Show the <b>Cepstrum</b> Window");
    lpcItem.setToolTipText("<html>Show the <b>LPC</b>Window<br>(Linear Predictive Coding)");
    forItem.setToolTipText("<html>Show <b>FFT</b> Window<br>(Fast Fourier Transformation)");
    zinItem.setToolTipText("<html><b>Zoom II<b> the selected time span");
    zbaItem.setToolTipText("<html><b>Zoom OUT</b> to the entire time span");
    zpreItem.setToolTipText("<html><b>Zoom BACK</b> to previous zoom<br>in the zoom history");
    helpItem.setToolTipText("<html>Show <b>Online Help");
    saveItem.setToolTipText("<html>Export Sonogram as <b>PNG-Image");
    printItem.setToolTipText("<html><b>Print</b> current displayed Sonogram");
    delhistItem.setToolTipText("<html><b>Delete</b> the whole file history");
    aboutItem.setToolTipText("<html><b>Information</b> about Sonogram");
    logfrItem.setToolTipText("<html>Display the Somogram in <b>Logarithmical Frequency</b> Scale");
    svgItem.setToolTipText(
        "<html><b>Save</b> Sonogram as <b>SVG</b><br>(Scalable Vector Graphic)<br>Sonogram's native"
            + " file format");
    csvItem.setToolTipText(
        "<html><b>Save</b> Sonogram as <b>CSV</b> text file.<br>(Comma Separated Values)");
    sysItem.setToolTipText("<html>System and Java VM <b>Information");
    memItem.setToolTipText("<html><b>Memory</b> usage of the Java VM");
    defaultItem.setToolTipText(
        "<html>Set all the <b>Sonogram-Settings-Dialog</b><br> parameters back to the default"
            + " <b>Initial</b> settings");
    arrItem.setToolTipText(
        "<html><b>Arrange</b> and open all analyze windows and<br>place them to the optimal screen"
            + " position");
    impItem.setToolTipText("<html><b>Import</b> Spektrum stored in SVG file");
    webItem.setToolTipText(
        "<html>Open a <b>Remote</b> media file<br>over the Network with an <b>URL</b>");
    wavItem.setToolTipText(
        "<html>Show the <b>Waveform</b> Window.<br>Displays the Amplitude in the time aligned"
            + " domain");
    walItem.setToolTipText("<html>Show the <b>Wavelet</b> Window");
    fulItem.setToolTipText("<html>Zoom the main sonogram<br>Window to <b>Fullscreen</b>");
    autocorrelationItem.setToolTipText("<html>Shows the <b>Autocorrelation</b> Window");
    pitchItem.setToolTipText("<html>Shows the <b>Pitch</b> tracking  Window");
    licenseItem.setToolTipText(
        "<html>Sonogram is licensed under the<br><b>Christoph Lauer Engineering Commercial"
            + " License</b><br>in version 1.0");
    welcomeItem.setToolTipText("<html>Show the <b>Welcome-Screen</b> again");
    splashItem.setToolTipText("<html>Show the <b>Splash-Screen</b> again");

    quitItem.setAccelerator(
        KeyStroke.getKeyStroke(
            java.awt.event.KeyEvent.VK_Q, java.awt.event.InputEvent.CTRL_DOWN_MASK));
    openItem.setAccelerator(
        KeyStroke.getKeyStroke(
            java.awt.event.KeyEvent.VK_O, java.awt.event.InputEvent.CTRL_DOWN_MASK));
    adjItem.setAccelerator(
        KeyStroke.getKeyStroke(
            java.awt.event.KeyEvent.VK_A, java.awt.event.InputEvent.CTRL_DOWN_MASK));
    lafItem.setAccelerator(
        KeyStroke.getKeyStroke(
            java.awt.event.KeyEvent.VK_F, java.awt.event.InputEvent.CTRL_DOWN_MASK));
    negItem.setAccelerator(
        KeyStroke.getKeyStroke(
            java.awt.event.KeyEvent.VK_N, java.awt.event.InputEvent.CTRL_DOWN_MASK));
    gridItem.setAccelerator(
        KeyStroke.getKeyStroke(
            java.awt.event.KeyEvent.VK_G, java.awt.event.InputEvent.CTRL_DOWN_MASK));
    logItem.setAccelerator(
        KeyStroke.getKeyStroke(
            java.awt.event.KeyEvent.VK_K, java.awt.event.InputEvent.CTRL_DOWN_MASK));
    infoItem.setAccelerator(
        KeyStroke.getKeyStroke(
            java.awt.event.KeyEvent.VK_I, java.awt.event.InputEvent.CTRL_DOWN_MASK));
    d3Item.setAccelerator(
        KeyStroke.getKeyStroke(
            java.awt.event.KeyEvent.VK_3, java.awt.event.InputEvent.CTRL_DOWN_MASK));
    playItem.setAccelerator(
        KeyStroke.getKeyStroke(
            java.awt.event.KeyEvent.VK_P, java.awt.event.InputEvent.CTRL_DOWN_MASK));
    revItem.setAccelerator(
        KeyStroke.getKeyStroke(
            java.awt.event.KeyEvent.VK_R, java.awt.event.InputEvent.CTRL_DOWN_MASK));
    stopItem.setAccelerator(
        KeyStroke.getKeyStroke(
            java.awt.event.KeyEvent.VK_S, java.awt.event.InputEvent.CTRL_DOWN_MASK));
    cepItem.setAccelerator(
        KeyStroke.getKeyStroke(
            java.awt.event.KeyEvent.VK_C, java.awt.event.InputEvent.CTRL_DOWN_MASK));
    lpcItem.setAccelerator(
        KeyStroke.getKeyStroke(
            java.awt.event.KeyEvent.VK_X, java.awt.event.InputEvent.CTRL_DOWN_MASK));
    forItem.setAccelerator(
        KeyStroke.getKeyStroke(
            java.awt.event.KeyEvent.VK_F, java.awt.event.InputEvent.CTRL_DOWN_MASK));
    zinItem.setAccelerator(
        KeyStroke.getKeyStroke(
            java.awt.event.KeyEvent.VK_Z, java.awt.event.InputEvent.CTRL_DOWN_MASK));
    zbaItem.setAccelerator(
        KeyStroke.getKeyStroke(
            java.awt.event.KeyEvent.VK_B, java.awt.event.InputEvent.CTRL_DOWN_MASK));
    zpreItem.setAccelerator(
        KeyStroke.getKeyStroke(
            java.awt.event.KeyEvent.VK_V, java.awt.event.InputEvent.CTRL_DOWN_MASK));
    helpItem.setAccelerator(KeyStroke.getKeyStroke(java.awt.event.KeyEvent.VK_F1, 0));
    saveItem.setAccelerator(
        KeyStroke.getKeyStroke(
            java.awt.event.KeyEvent.VK_E, java.awt.event.InputEvent.CTRL_DOWN_MASK));
    printItem.setAccelerator(
        KeyStroke.getKeyStroke(
            java.awt.event.KeyEvent.VK_D, java.awt.event.InputEvent.CTRL_DOWN_MASK));
    svgItem.setAccelerator(
        KeyStroke.getKeyStroke(
            java.awt.event.KeyEvent.VK_V, java.awt.event.InputEvent.CTRL_DOWN_MASK));
    csvItem.setAccelerator(
        KeyStroke.getKeyStroke(
            java.awt.event.KeyEvent.VK_C, java.awt.event.InputEvent.CTRL_DOWN_MASK));
    logfrItem.setAccelerator(
        KeyStroke.getKeyStroke(
            java.awt.event.KeyEvent.VK_L, java.awt.event.InputEvent.CTRL_DOWN_MASK));
    impItem.setAccelerator(
        KeyStroke.getKeyStroke(
            java.awt.event.KeyEvent.VK_Y, java.awt.event.InputEvent.CTRL_DOWN_MASK));
    webItem.setAccelerator(
        KeyStroke.getKeyStroke(
            java.awt.event.KeyEvent.VK_U, java.awt.event.InputEvent.CTRL_DOWN_MASK));
    walItem.setAccelerator(
        KeyStroke.getKeyStroke(
            java.awt.event.KeyEvent.VK_H, java.awt.event.InputEvent.CTRL_DOWN_MASK));
    wavItem.setAccelerator(
        KeyStroke.getKeyStroke(
            java.awt.event.KeyEvent.VK_W, java.awt.event.InputEvent.CTRL_DOWN_MASK));
    fulItem.setAccelerator(KeyStroke.getKeyStroke(java.awt.event.KeyEvent.VK_F11, 0));
    arrItem.setAccelerator(KeyStroke.getKeyStroke(java.awt.event.KeyEvent.VK_F12, 0));
    autocorrelationItem.setAccelerator(
        KeyStroke.getKeyStroke(
            java.awt.event.KeyEvent.VK_A, java.awt.event.InputEvent.CTRL_DOWN_MASK));
    pitchItem.setAccelerator(
        KeyStroke.getKeyStroke(
            java.awt.event.KeyEvent.VK_P, java.awt.event.InputEvent.CTRL_DOWN_MASK));

    infoItem.addActionListener(this);
    quitItem.addActionListener(this);
    closeItem.addActionListener(this);
    adjItem.addActionListener(this);
    lafItem.addActionListener(this);
    d3Item.addActionListener(this);
    openItem.addActionListener(this);
    recordItem.addActionListener(this);
    colItem.addActionListener(this);
    bwItem.addActionListener(this);
    greenItem.addActionListener(this);
    negItem.addActionListener(this);
    gridItem.addActionListener(this);
    logItem.addActionListener(this);
    firecItem.addActionListener(this);
    fireItem.addActionListener(this);
    rainItem.addActionListener(this);
    hamItem.addActionListener(this);
    hanItem.addActionListener(this);
    blaItem.addActionListener(this);
    rectItem.addActionListener(this);
    triItem.addActionListener(this);
    welItem.addActionListener(this);
    gauItem.addActionListener(this);
    flaItem.addActionListener(this);
    harItem.addActionListener(this);
    cosItem.addActionListener(this);
    asyItem.addActionListener(this);
    playItem.addActionListener(this);
    revItem.addActionListener(this);
    aboutItem.addActionListener(this);
    helpItem.addActionListener(this);
    licenseItem.addActionListener(this);
    welcomeItem.addActionListener(this);
    splashItem.addActionListener(this);
    stopItem.addActionListener(this);
    cepItem.addActionListener(this);
    lpcItem.addActionListener(this);
    forItem.addActionListener(this);
    zinItem.addActionListener(this);
    zbaItem.addActionListener(this);
    zpreItem.addActionListener(this);
    delhistItem.addActionListener(this);
    printItem.addActionListener(this);
    saveItem.addActionListener(this);
    logfrItem.addActionListener(this);
    svgItem.addActionListener(this);
    csvItem.addActionListener(this);
    sysItem.addActionListener(this);
    memItem.addActionListener(this);
    impItem.addActionListener(this);
    defaultItem.addActionListener(this);
    arrItem.addActionListener(this);
    webItem.addActionListener(this);
    wavItem.addActionListener(this);
    walItem.addActionListener(this);
    fulItem.addActionListener(this);
    autocorrelationItem.addActionListener(this);
    pitchItem.addActionListener(this);
    feedbackItem.addActionListener(this);
  }

  // ----------------------------------------------------------------------------------------------------------------

  public void shakeButtons() {
    if (shaking) return;
    else shaking = true;
    final Point point1 = zinbutton.getLocation();
    final Point point2 = wvbutton.getLocation();
    final int delay = 25;
    Runnable r =
        new Runnable() {
          @Override
          public void run() {
            for (int i = 0; i < 1; i++) {
              try {
                moveButton(new Point(point1.x, point1.y + 1), zinbutton);
                if (wvbutton.isEnabled())
                  moveButton(new Point(point2.x, point2.y + 1), wvbutton);
                Thread.sleep(delay);
                moveButton(new Point(point1.x, point1.y + 2), zinbutton);
                if (wvbutton.isEnabled())
                  moveButton(new Point(point2.x, point2.y + 2), wvbutton);
                Thread.sleep(delay);
                moveButton(new Point(point1.x, point1.y + 3), zinbutton);
                if (wvbutton.isEnabled())
                  moveButton(new Point(point2.x, point2.y + 3), wvbutton);
                Thread.sleep(delay);
                moveButton(new Point(point1.x, point1.y + 2), zinbutton);
                if (wvbutton.isEnabled())
                  moveButton(new Point(point2.x, point2.y + 2), wvbutton);
                Thread.sleep(delay);
                moveButton(new Point(point1.x, point1.y + 1), zinbutton);
                if (wvbutton.isEnabled())
                  moveButton(new Point(point2.x, point2.y + 1), wvbutton);
                Thread.sleep(delay);
                moveButton(point1, zinbutton);
                if (wvbutton.isEnabled()) moveButton(point2, wvbutton);
                Thread.sleep(delay);
                moveButton(new Point(point1.x, point1.y - 1), zinbutton);
                if (wvbutton.isEnabled())
                  moveButton(new Point(point2.x, point2.y - 1), wvbutton);
                Thread.sleep(delay);
                moveButton(new Point(point1.x, point1.y - 2), zinbutton);
                if (wvbutton.isEnabled())
                  moveButton(new Point(point2.x, point2.y - 2), wvbutton);
                Thread.sleep(delay);
                moveButton(new Point(point1.x, point1.y - 3), zinbutton);
                if (wvbutton.isEnabled())
                  moveButton(new Point(point2.x, point2.y - 3), wvbutton);
                Thread.sleep(delay);
                moveButton(new Point(point1.x, point1.y - 2), zinbutton);
                if (wvbutton.isEnabled())
                  moveButton(new Point(point2.x, point2.y - 2), wvbutton);
                Thread.sleep(delay);
                moveButton(new Point(point1.x, point1.y - 1), zinbutton);
                if (wvbutton.isEnabled())
                  moveButton(new Point(point2.x, point2.y - 1), wvbutton);
                Thread.sleep(delay);
                moveButton(point1, zinbutton);
                if (wvbutton.isEnabled()) moveButton(point2, wvbutton);
                Thread.sleep(delay);
              } catch (InterruptedException ex) {
                ex.printStackTrace();
                Thread.currentThread().interrupt();
              }
            }
            shaking = false;
          }
        };
    Thread t = new Thread(r);
    t.start();
  }

  // ----------------------------------------------------------------------------------------------------------------

  private void moveButton(final Point p, JButton b) {
    SwingUtilities.invokeLater(
        new Runnable() {
          @Override
          public void run() {
            b.setLocation(p);
          }
        });
  }

  // -------------------------------------------------------------------------------------------------------------------------
  /**
   * Enable/disable toolbar and menueitems
   *
   * @param enable Buttons and Items on/off (true = on)
   */
  public void enableItems(boolean enable) {

    // Menueitems
    saveItem.setEnabled(enable);
    printItem.setEnabled(enable);
    playItem.setEnabled(enable);
    revItem.setEnabled(enable);
    d3Item.setEnabled(enable);
    forItem.setEnabled(enable);
    cepItem.setEnabled(enable);
    lpcItem.setEnabled(enable);
    zbaItem.setEnabled(enable);
    zinItem.setEnabled(enable);
    svgItem.setEnabled(enable);
    csvItem.setEnabled(enable);
    walItem.setEnabled(enable);
    wavItem.setEnabled(enable);
    arrItem.setEnabled(enable);
    closeItem.setEnabled(enable);
    autocorrelationItem.setEnabled(enable);
    pitchItem.setEnabled(enable);
    // Buttons
    infobutton.setEnabled(enable);
    fullbutton.setEnabled(enable);
    arrangebutton.setEnabled(enable);
    playbutton.setEnabled(enable);
    d3button.setEnabled(enable);
    revbutton.setEnabled(false);
    forbutton.setEnabled(enable);
    cepbutton.setEnabled(enable);
    lpcbutton.setEnabled(enable);
    zbabutton.setEnabled(enable);
    zinbutton.setEnabled(false);
    svgbutton.setEnabled(enable);
    wavbutton.setEnabled(enable);
    walbutton.setEnabled(enable);
    autocorrelationbutton.setEnabled(enable);
    pitchbutton.setEnabled(enable);
    logbutton.setEnabled(enable);
    smoothfrbutton.setEnabled(enable);
    smoothtmbutton.setEnabled(enable);
    colorSlider.setEnabled(enable);
    colorLabel.setEnabled(enable);
    zprebutton.setEnabled(false);
    wvbutton.setBorder(BorderFactory.createCompoundBorder());
    wvbutton.setEnabled(false);
    sep1.setEnabled(enable);
    sep2.setEnabled(enable);
    sep3.setEnabled(enable);
    sep4.setEnabled(enable);
    sep5.setEnabled(enable);
    sep6.setEnabled(enable);
    sep7.setEnabled(enable);
  }

  // -------------------------------------------------------------------------------------------------------------------------
  /**
   * Eventhandling for all Actionlistener
   *
   * @param e Event for Actionlistener.
   */
  public void actionPerformed(ActionEvent e) {
    if (e.getSource() == closeItem) {
      setCursor(new Cursor(Cursor.DEFAULT_CURSOR));
      setTitle("Sonogram Visible Speech - version " + VERSION);
      spectrumExist = false;
      updateimageflag = true;
      repaint();
    }
    if (e.getSource() == rectItem) {
      gad.p1.setSelectedIndex(1);
      gad.sliderwinfunktion.setValue(1);
      if (spectrumExist) readerIsBack();
    }
    if (e.getSource() == blaItem) {
      gad.p1.setSelectedIndex(1);
      gad.sliderwinfunktion.setValue(2);
      if (spectrumExist) readerIsBack();
    }
    if (e.getSource() == triItem) {
      gad.p1.setSelectedIndex(1);
      gad.sliderwinfunktion.setValue(3);
      if (spectrumExist) readerIsBack();
    }
    if (e.getSource() == hamItem) {
      gad.p1.setSelectedIndex(1);
      gad.sliderwinfunktion.setValue(4);
      if (spectrumExist) readerIsBack();
    }
    if (e.getSource() == welItem) {
      gad.p1.setSelectedIndex(1);
      gad.sliderwinfunktion.setValue(5);
      if (spectrumExist) readerIsBack();
    }
    if (e.getSource() == gauItem) {
      gad.p1.setSelectedIndex(1);
      gad.sliderwinfunktion.setValue(6);
      if (spectrumExist) readerIsBack();
    }
    if (e.getSource() == hanItem) {
      gad.p1.setSelectedIndex(1);
      gad.sliderwinfunktion.setValue(7);
      if (spectrumExist) readerIsBack();
    }
    if (e.getSource() == flaItem) {
      gad.p1.setSelectedIndex(1);
      gad.sliderwinfunktion.setValue(8);
      if (spectrumExist) readerIsBack();
    }
    if (e.getSource() == harItem) {
      gad.p1.setSelectedIndex(1);
      gad.sliderwinfunktion.setValue(9);
      if (spectrumExist) readerIsBack();
    }
    if (e.getSource() == cosItem) {
      gad.p1.setSelectedIndex(1);
      gad.sliderwinfunktion.setValue(10);
      if (spectrumExist) readerIsBack();
    }
    if (e.getSource() == asyItem) {
      gad.p1.setSelectedIndex(1);
      gad.sliderwinfunktion.setValue(11);
      if (spectrumExist) readerIsBack();
    }
    if (e.getSource() == logItem) {
      gad.p1.setSelectedIndex(14);
      gad.clog.setSelected(logItem.isSelected());
      if (spectrumExist) readerIsBack();
    }
    if (e.getSource() == gridItem) {
      gad.p1.setSelectedIndex(15);
      gad.cgrid.setSelected(gridItem.isSelected());
      gad.cgrid2.setSelected(gridItem.isSelected());
      updateimageflag = true;
      repaint();
    }
    if (e.getSource() == firecItem) {
      gad.p1.setSelectedIndex(2);
      updateimageflag = true;
      repaint();
      gad.r0.setSelected(true);
      colorSlider.setValue(0);
      la.spec.repaint();
    }
    if (e.getSource() == fireItem) {
      gad.p1.setSelectedIndex(2);
      updateimageflag = true;
      repaint();
      gad.r1.setSelected(true);
      colorSlider.setValue(1);
      la.spec.repaint();
    }
    if (e.getSource() == colItem) {
      gad.p1.setSelectedIndex(2);
      updateimageflag = true;
      repaint();
      gad.r2.setSelected(true);
      colorSlider.setValue(2);
      la.spec.repaint();
    }
    if (e.getSource() == rainItem) {
      gad.p1.setSelectedIndex(2);
      updateimageflag = true;
      repaint();
      gad.r3.setSelected(true);
      colorSlider.setValue(3);
      la.spec.repaint();
    }
    if (e.getSource() == greenItem) {
      gad.p1.setSelectedIndex(2);
      updateimageflag = true;
      repaint();
      gad.r4.setSelected(true);
      colorSlider.setValue(4);
      la.spec.repaint();
    }
    if (e.getSource() == bwItem) {
      gad.p1.setSelectedIndex(2);
      updateimageflag = true;
      repaint();
      gad.r5.setSelected(true);
      colorSlider.setValue(5);
      la.spec.repaint();
    }
    if (e.getSource() == negItem) {
      gad.p1.setSelectedIndex(2);
      updateimageflag = true;
      repaint();
      gad.cinv.setSelected(negItem.isSelected());
      la.spec.repaint();
    }
    if (e.getSource() == quitItem || e.getSource() == quitbutton) {
      int confirm =
          JOptionPane.showOptionDialog(
              this,
              "<html>You want to <i><u>close Sonogram</u></i> ?",
              "Exit Sonogram ?",
              JOptionPane.YES_NO_OPTION,
              JOptionPane.QUESTION_MESSAGE,
              null,
              null,
              null);
      if (confirm == 0) {
        System.out.println("--> Bye");
        saveConfig();
        if (plugin) {
          try // Call Plugin exit class
          {
            // Check Class for exist
            Class sap = Class.forName("de.dfki.nite.gram.tools.SonogramCommunication");
            // Generate Signature for funktion to get Funktion
            Class[] signature = new Class[0];
            // get funktion
            java.lang.reflect.Method function = sap.getMethod("sonogramCallBackEnd", signature);
            // Generate parameter for funktion
            Object[] parameter = new Object[0];
            // call static funktion, therefore null as class object
            function.invoke(null, parameter);
          } catch (Throwable throwable) {
            System.out.println("--> No Plugin Class not found.");
            plugin = false;
          }
          dispose();

          return;
        }
        System.exit(0);
      }
    }
    if (e.getSource() == aboutItem) { // AboutItem
      // demo mode implementation
      String license = "<font size = 5>Time Limited Trial License</font> (" + isetu + " Minutes)";
      if (Licenses.zoda1.equals("HIRN1HIRN1HIRN1HIRN1HIRN1HIRN1HI") == false) {
        license =
            "<font color = #646496 size = 4>Licensed to:<font color = #242446 size = 5>"
                +
                // including some licensing information
                "<br>"
                + Licenses.zoda1.replaceAll("\\s+$", "")
                + "<br>"
                + Licenses.zoda2.replaceAll("\\s+$", "")
                + "<br>"
                + Licenses.zoda3.replaceAll("\\s+$", "")
                + "<br>"
                + Licenses.zoda4.replaceAll("\\s+$", "")
                + "<br>"
                + Licenses.zoda5.replaceAll("\\s+$", "")
                + "<br>"
                + Licenses.zoda6.replaceAll("\\s+$", "")
                + "<br>"
                + Licenses.zoda7.replaceAll("\\s+$", "")
                + "<br>"
                + Licenses.zoda8.replaceAll("\\s+$", "")
                + "</font>";
      }
      String yearStr = Integer.toString(Calendar.getInstance().get(Calendar.YEAR));

      messageBox(
          "SONOGRAM VISIBLE SPEECH version " + VERSION + " BUILD " + BUILD,
          "<html>The SPWVD based SuperZOOM function extracts the so far best known<br>resolution in"
              + " the time and frequency domain. Sonogram can also display<br>display so called"
              + " three dimensional Perspectograms if Java3D is installed.<br><br><font color ="
              + " #646496 size = 2>Licensed to (LICENSE STRING):</font><br><hr><table"
              + " border=0><tr><td><font size =5 color = #222266>LICENSE:</td><td><font size ="
              + " 5>FREE FULLVERSION</td></tr><br><td><font size =5 color ="
              + " #222266>VERSION:</td><td><font size = 5>"
              + VERSION
              + "</td>"
              + "<tr><td><font size = 5 color = #222266>BUILD: </td><td><font size = 5>"
              + BUILD
              + "</td></tr></font></table>"
              + "<hr><br><i><font size=3><center>programed from 2000 to "
              + yearStr
              + " by © Christoph Lauer Engineering"
              + "<br><u>christoph.lauer@me.com</u> - <u>https://christoph-lauer.githb.io</u><br>",
          JOptionPane.INFORMATION_MESSAGE);
    }
    if (e.getSource() == recordItem || e.getSource() == recbutton) {
      la.setVisible(true);
    }
    if (e.getSource() == openItem
        || e.getSource() == openbutton) // File-Open Item, generates a File Open Dialog
    {
      fileisfromurl = false;
      openFile(null);
    }

    // General Adjustment Button, shows Adj.Dialog
    if (e.getSource() == adjItem || e.getSource() == adjbutton) {
      gad.aktualize();
      gad.setVisible(!gad.isVisible());
    }
    if ((e.getSource() == playbutton || e.getSource() == playItem) && spectrumExist) {
        playbuttonpressed = true;
        System.out.println("--> START playing");
        if (pp.plstop == pp.plstart) pp.plstop = 1.0;
        player.springTo((selectedstartold + pp.plstart * selecedwidthold) * samplesall / samplerate);
        player.play();
        stopItem.setEnabled(true);
        stopbutton.setEnabled(true);
        playItem.setEnabled(false);
        playbutton.setEnabled(false);
    }
    if ((e.getSource() == stopbutton || e.getSource() == stopItem) && spectrumExist) {
        System.out.println("--> STOP playing");
        stopbuttonpressed = true;
        player.stop();
    }

    // Play Rewindbutton
    if (e.getSource() == revbutton || e.getSource() == revItem) { 
      if (spectrumExist) {
        player.springTo(0.0);
        pp.plstart = 0.0;
        pp.plbutton = 0.0;
        pp.plstop = 1.0;
        selectedstart = selectedstartold;
        selecedwidth = selecedwidthold;
        pp.paintTimeSlider(null, false);
        infod.update();
        pp.updateWvButton();
      }
    }

    // Helpitem
    if (e.getSource() == helpItem || e.getSource() == helpbutton) {
      hd.setVisible(!hd.isVisible());
    }
    if (e.getSource() == licenseItem) {
      ld.setVisible(true);
    }
    if (e.getSource() == welcomeItem) {
      welcome = new WelcomeScreen(true);
    }
    if (e.getSource() == splashItem) {
      SonoSplashScreen splash = new SonoSplashScreen(true);
    }
    if (e.getSource() == d3button || e.getSource() == d3Item) { // Surfaceplot
      if (!spectrumExist) messageBox("Perspectogram", "Please open Mediafile first.", 2);
      else
        try {
          gad.p1.setSelectedIndex(5);
          surplot = new Surface(this, false);
        } catch (Throwable thro) {
          System.out.println("--> Error while Perspectogram");
          System.out.println(thro);
          messageBox(
              "Perspectogram",
              "Error while generate Perspectogram.\n"
                  + "Have you installed Java3D or\n"
                  + "selected a very large plot?\n"
                  + "See konsole for details.",
              0);
        }
    }
    if (e.getSource() == cepbutton || e.getSource() == cepItem) { // Cepstrum View
      if (!spectrumExist) messageBox("Cepstrum", "Please open Mediafile first.", 1);
      else if (samplesall > cv.samples()) {
        System.out.println("--> Show Cepstrum View");
        gad.p1.setSelectedIndex(8);
        cv.setVisible(!cv.isVisible());
      } else messageBox("Cepstrum", "Signal to short !!!.", 1);
    }
    if ((e.getSource() == zinbutton || e.getSource() == zinItem) && spectrumExist) { // Zoomin
        selectedstartpre.add(Double.valueOf(selectedstartold));
        selectedwidthpre.add(Double.valueOf(selecedwidthold));
        zoompreviousindex++;
        readerIsBack();
        zprebutton.setEnabled(true);
        zpreItem.setEnabled(true);
        pp.updateWvButton();
    }
    if ((e.getSource() == zbabutton || e.getSource() == zbaItem) && spectrumExist) { // Zoomfull
        selectedstart = 0.0;
        selecedwidth = 1.0;
        selectedstartpre.removeAllElements();
        selectedwidthpre.removeAllElements();
        zoompreviousindex = 0;
        readerIsBack();
        zprebutton.setEnabled(false);
        zpreItem.setEnabled(false);
        infod.update();
        pp.updateWvButton();
    }
    if ((e.getSource() == zprebutton || e.getSource() == zpreItem) && spectrumExist) { // Zoomback
        if (zoompreviousindex != 0) {
          selectedstart = (selectedstartpre.get(zoompreviousindex - 1)).doubleValue();
          selecedwidth = (selectedwidthpre.get(zoompreviousindex - 1)).doubleValue();
          selectedstartpre.remove(zoompreviousindex - 1);
          selectedwidthpre.remove(zoompreviousindex - 1);
          zoompreviousindex--;
          readerIsBack();
          zprebutton.setEnabled(true);
          zpreItem.setEnabled(true);
          pp.updateWvButton();
        }
        if (zoompreviousindex == 0) {
          zprebutton.setEnabled(false);
          zpreItem.setEnabled(false);
        }
    }
    if (e.getSource() == forbutton || e.getSource() == forItem) { // FFT View
      if (!spectrumExist) messageBox("Fast Fourier", "Please open Mediafile first.", 1);
      else if (samplesall > fv.len) {
        System.out.println("--> Fast Fourier Transform View");
        fv.setVisible(!fv.isVisible());
        gad.p1.setSelectedIndex(6);
      } else messageBox("FFT", "Signal to short !!!.\nReduce number of samples used for FFT.", 1);
    }
    if (e.getSource() == autocorrelationbutton
        || e.getSource() == autocorrelationItem) { // Autocorrelation
      if (!spectrumExist) messageBox("Autocorrelation", "Please open Mediafile first.", 1);
      else {
        System.out.println("--> Autocorrelation View");
        gad.p1.setSelectedIndex(10);
        kv.setVisible(!kv.isVisible());
      }
    }
    if (e.getSource() == pitchbutton || e.getSource() == pitchItem) { // PitchWindow
      if (!spectrumExist) messageBox("Pitch Tracking", "Please open Mediafile first.", 1);
      else {
        gad.p1.setSelectedIndex(9);
        System.out.println("--> Pitch Tracking View");
        pv.placePitchwindowUnderTheMainWindow();
        pv.setVisible(!pv.isVisible());
      }
    }
    if (e.getSource() == infobutton || e.getSource() == infoItem) { // Information Dialog
      infod.setVisible(!infod.isVisible());
      infovisible = infod.isVisible();
      if (infovisible) {
        infod.update();
        System.out.println("--> Show Informationtable-View");
      }
    }
    if (e.getSource() == lpcbutton || e.getSource() == lpcItem) { // Linear Prediction  Dialog
      if (!spectrumExist) messageBox("LPC", "Please open Mediafile first.", 1);
      else if (samplesall > lv.len) {
        System.out.println("--> Show Linear Prediction View");
        lv.setVisible(!lv.isVisible());
        gad.p1.setSelectedIndex(7);
      } else messageBox("LPC", "Signal to short !!!.\n Reduce number of previous samples.", 1);
    }
    if (e.getSource() == lafItem) { // Change Look and Feel
      slaf.setVisible(true);
    }
    for (int i = 0; i < filehistory.size(); i++) { // Filehistory
      if (e.getSource() == hotlist[i]) {
        if ((filehistory.get(i)).substring(0, 4).equals("http")
            || (filehistory.get(i)).substring(0, 3).equals("ftp"))
          fileisfromurl = true;
        else fileisfromurl = false;
        openFile(filehistory.get(i));
      }
    }
    if (e.getSource() == delhistItem && !filehistory.isEmpty()) { // Delte Hotlist
        int confirm =
            JOptionPane.showOptionDialog(
                this,
                "<html><i>Are you sure you want to <u>remove all<br> elements</u> from the file"
                    + " history  ?",
                "Delete the File History",
                JOptionPane.YES_NO_OPTION,
                JOptionPane.QUESTION_MESSAGE,
                null,
                null,
                null);
        if (confirm == 0) {
          for (int i = 0; i < filehistory.size(); i++) menuFile.remove(11);
          filehistory.removeAllElements();
        }
    }
    if (e.getSource() == printItem) {
      PrintableComponent pc = new PrintableComponent(pp);
      try {
        pc.print();
        System.out.println("--> Sonogram printed.");
      } catch (Throwable ex) {
        messageBox("Error", "Error while printing", 0);
      }
      ;
    }
    if (e.getSource() == saveItem) {
      if (spectrumExist)
        try {
          String filename = "Sonogram.png";
          class MyFilter extends javax.swing.filechooser.FileFilter {
            public boolean accept(File file) {
              String filename = file.getName();
              return filename.endsWith(".png");
            }

            public String getDescription() {
              return "Portable Network Graphics (PNG)";
            }
          }
          // the file chooser
          JFileChooser chooser = new JFileChooser();
          chooser.addChoosableFileFilter(new MyFilter());
          chooser.setSelectedFile(new File("Sonogram.png"));
          int returnVal = chooser.showSaveDialog(this);
          // write the image to file
          if (returnVal == JFileChooser.APPROVE_OPTION)
            ImageIO.write(pp.doublebufferimage, "png", chooser.getSelectedFile());
        } catch (IOException ex) {
          JOptionPane.showMessageDialog(
              this,
              "<html><i>An eror occoured while save the image.<br>"
                  + "Did you have write permission to the selected file ?</i><br><br>"
                  + "File: "
                  + filename,
              "Cannot save the Image",
              JOptionPane.ERROR_MESSAGE);
        }
    }
    if (e.getSource() == logfrItem) {
      gad.cslogfr.setSelected(logfrItem.isSelected());
      gad.p1.setSelectedIndex(15);
    }

    if (e.getSource() == svgItem || e.getSource() == svgbutton) {
      svg.setVisible(true);
    }

    if (e.getSource() == csvItem && spectrumExist) {
      ExportSpectrumCSV csv = new ExportSpectrumCSV(this);
    }

    if (e.getSource() == memItem) {
      final PerformanceMonitor perfmon = new PerformanceMonitor();
      final JFrame f = new JFrame("Memory Monitor");
      Toolkit tk = Toolkit.getDefaultToolkit();
      f.setIconImage(tk.getImage(Sonogram.class.getResource("Sonogram.gif")));
      perfmon.surf.start();
      WindowListener l =
          new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent e) {
              f.dispose();
            }

            @Override
            public void windowDeiconified(WindowEvent e) {
              perfmon.surf.start();
            }

            @Override
            public void windowIconified(WindowEvent e) {
              perfmon.surf.stop();
            }
          };
      f.addWindowListener(l);
      f.getContentPane().add("Center", perfmon);
      f.pack();
      f.setSize(new Dimension(500, 250));
      perfmon.putClientProperty("JInternalFrame.isPalette", Boolean.TRUE);
      f.setVisible(true);
    }
    if (e.getSource() == sysItem) {
      // Get System Properties
      Properties prop = System.getProperties();
      for (Enumeration en = prop.keys(); en.hasMoreElements(); ) {
        String key = (String) en.nextElement();
        String value = prop.getProperty(key);
        System.out.println(key + " = " + value);
      }
      sysopt = "<html><font size=-2 color=#110033>SONOGRAM VERSION: <i>" + VERSION;
      sysopt += "</i><br>BUILD: <i>" + BUILD;
      sysopt += "</i><br>OS name: <i>" + prop.getProperty("os.name");
      sysopt += "</i><br>OS version: <i>" + prop.getProperty("os.version");
      sysopt += "</i><br>Architecture: <i>" + prop.getProperty("os.arch");
      sysopt += "</i><br>Data Model: <i>" + prop.getProperty("sun.arch.data.model");
      sysopt += "</i><br>Processor: <i>" + prop.getProperty("sun.cpu.isalist");
      sysopt += "</i><br>Java Runtime Name: <i>" + prop.getProperty("java.runtime.name");
      sysopt += "</i><br>Java VM Version: <i>" + prop.getProperty("java.vm.version");
      sysopt += "</i><br>Java Spec Version: <i>" + prop.getProperty("java.specification.version");
      sysopt += "</i><br>Runtime version: <i>" + prop.getProperty("java.runtime.version");
      sysopt += "</i><br>Java VM name: <i>" + prop.getProperty("java.vm.name");
      sysopt += "</i><br>Java vendor: <i>" + prop.getProperty("java.vendor");
      sysopt += "</i><br>Java path: <i>" + prop.getProperty("sun.boot.library.path");
      sysopt += "</i><br>Java TEMP: <i>" + prop.getProperty("java.io.tmpdir");
      sysopt += "</i><br>Java class version: <i>" + prop.getProperty("java.class.version");
      sysopt += "</i><br>User name: <i>" + prop.getProperty("user.name");
      sysopt += "</i><br>User home: <i>" + prop.getProperty("user.home");
      sysopt += "</i><br>Java Media Framework Installed: <i>YES";
      sysopt += "</i><br>Java3D installed: <i>";
      if (java3disinstalled) sysopt += "YES";
      else sysopt += "NO";
      sysopt += "</i><br>Use Apple OpenGL: <i>" + prop.getProperty("apple.awt.graphics.UseOpenGL");
      sysopt += "</i><br>MP3 Available: <i>YES";
      sysopt += "</i><br>Splash Type: <i>";

      if (splash.javaSplash) sysopt += "NATIVE JAVA STARTUP SPLASH-SCREEN";
      else sysopt += "OWN SPLASH-SCREEN";
      sysopt += "</i><br>Splash Transparency: <i>";

      if (splash.nativeTransparency == 1) sysopt += "NATIVE JAVA STARTUP SPLASH WITH TRANSPARENCY";
      if (splash.nativeTransparency == 2) sysopt += "FAKED BACKGROUND-IMAGE TRANSPARENCY";
      if (splash.nativeTransparency == 3) sysopt += "JAVA-IMAGE TRANSPARENCY";

      int memMb = (int) Math.round(Runtime.getRuntime().totalMemory() / 1024 / 1024);
      String memMessage;
      if (memMb < 1000)
        sysopt +=
            "</i><br>Currently allocated memory: <i><font color=#AA0000 size=3>" + memMb + " MB";
      else
        sysopt +=
            "</i><br>Currently allocated memory: <i><font color=#AA0000 size=3>"
                + Math.round((double) memMb) / 1000.0
                + " GB";

      memMb = (int) Math.round(Runtime.getRuntime().freeMemory() / 1024 / 1024);
      if (memMb < 1000)
        sysopt +=
            "</i><br>Currently available free memory: <i><font color=#AA0000 size=3>"
                + memMb
                + " MB";
      else
        sysopt +=
            "</i><br>Currently available free memory: <i><font color=#AA0000 size=3>"
                + Math.round((double) memMb) / 1000.0
                + " GB";

      int NTHREADS = Runtime.getRuntime().availableProcessors();
      sysopt +=
          "<font size=-2 color=#000044></i><br>Numer of available Processor Cores: <font"
              + " color=#AA0000 size=3><i>"
              + NTHREADS
              + " Cores</font>";
      sysopt +=
          "</i><font size=-2 color=#110033><br>Startup Time: <i>"
              + Double.toString(startupSeconds)
              + " Seconds";

      JOptionPane.showMessageDialog(
          this,
          sysopt,
          "Java System Properies",
          JOptionPane.INFORMATION_MESSAGE,
          new ImageIcon(Sonogram.class.getResource("java.png")));
    }

    if (e.getSource() == impItem) {
      ImportFromSVG ifs = new ImportFromSVG(this);
    }
    if (e.getSource() == arrItem) {
      arrangeWindows();
    }
    if (e.getSource() == defaultItem) {
      if (openenedbysvg) {
        messageBox(
            "Not Aviable",
            "This file is opened by SVG.\n"
                + "This feature is only aviable, if\n"
                + "you open this file new by his\n"
                + "orginal source",
            2);
        return;
      }
      int confirm =
          JOptionPane.showOptionDialog(
              this,
              "<html><i>Are you sure you want to Reset all Parameters<br>and Settings <u>back to"
                  + " the initial Values</u> ?",
              "Reset all Settings",
              JOptionPane.YES_NO_OPTION,
              JOptionPane.QUESTION_MESSAGE,
              null,
              null,
              null);
      if (confirm == 0) {
        initGadToDefault();
        setLookAndFeel(ilaf);
        boolean wasvisible;
        wasvisible = gad.isVisible();
        Dimension dimension = gad.getSize();
        Point location = gad.getLocation();
        gad.dispose();
        gad = new GeneralAdjustmentDialog(this);
        gad.setLocation(location);
        gad.setSize(dimension);
        // gad.pack();
        gad.setVisible(wasvisible);
        fireItem.setSelected(true);
        gauItem.setSelected(true);
        negItem.setSelected(false);
        gridItem.setSelected(true);
        logItem.setSelected(true);
        logfrItem.setSelected(false);
        logbutton.setBorder(fullbutton.getBorder());
        if (spectrumExist) openFile(filepath);
        la.tp.colorSwitch = 0;
        la.tp.initDigitArray();
        la.spec.repaint();
      }
      int confirmplacemet =
          JOptionPane.showOptionDialog(
              this,
              "<html><i>Reset all Windows to there <u>default position and location</u> ?",
              "Reset Window Placement",
              JOptionPane.YES_NO_OPTION,
              JOptionPane.QUESTION_MESSAGE,
              null,
              null,
              null);
      if (confirmplacemet == 0) {
        if (fullscreen) {
          getContentPane().add(toolBar, BorderLayout.NORTH);
          setLocation(normalpoint);
          setResizable(true);
          setSize(normaldimension);
          dispatchEvent(
              new java.awt.event.ComponentEvent(
                  this, java.awt.event.ComponentEvent.COMPONENT_RESIZED));
          fullscreen = false;
        } else {
          setSize(1025, 550);
          int scw = (int) java.awt.Toolkit.getDefaultToolkit().getScreenSize().getWidth();
          int sch = (int) java.awt.Toolkit.getDefaultToolkit().getScreenSize().getHeight();
          setLocation(scw / 2 - getWidth() / 2, sch / 2 - getHeight() / 2);
        }
        // gad.setSize      (434,432);
        gad.setLocation(20, 20);
        cv.setLocation(10, 10);
        cv.setSize(1000, 400);
        fv.setLocation(10, 10);
        fv.setSize(1000, 400);
        lv.setLocation(10, 10);
        lv.setSize(1000, 400);
        infod.setLocation(40, 40);
        hd.setLocation(5, 10);
        hd.setSize(615, 700);
        wv.setLocation(10, 10);
        wv.setSize(1000, 400);
        av.setLocation(10, 10);
        av.setSize(1000, 400);
        kv.setLocation(10, 10);
        kv.setSize(1000, 400);
        dispatchEvent(
            new java.awt.event.ComponentEvent(
                this, java.awt.event.ComponentEvent.COMPONENT_RESIZED));
        dispatchEvent(
            new java.awt.event.ComponentEvent(
                gad, java.awt.event.ComponentEvent.COMPONENT_RESIZED));
        dispatchEvent(
            new java.awt.event.ComponentEvent(cv, java.awt.event.ComponentEvent.COMPONENT_RESIZED));
        dispatchEvent(
            new java.awt.event.ComponentEvent(fv, java.awt.event.ComponentEvent.COMPONENT_RESIZED));
        dispatchEvent(
            new java.awt.event.ComponentEvent(hd, java.awt.event.ComponentEvent.COMPONENT_RESIZED));
        dispatchEvent(
            new java.awt.event.ComponentEvent(wv, java.awt.event.ComponentEvent.COMPONENT_RESIZED));
        dispatchEvent(
            new java.awt.event.ComponentEvent(
                infod, java.awt.event.ComponentEvent.COMPONENT_RESIZED));
      }
      gad.highLightButton(0);
    }
    if (e.getSource() == webItem) {
      ou.setVisible(true);
    }
    if (e.getSource() == wavItem || e.getSource() == wavbutton) {
      if (!spectrumExist) messageBox("Waveform", "Please open Mediafile first.", 1);
      else {
        wv.getLen();
        if (samplesall > wv.len) {
          System.out.println("--> Show Linear Prediction View");
          wv.setVisible(!wv.isVisible());
          gad.p1.setSelectedIndex(11);
        } else messageBox("Waveform", "Signal to short !!!.\nReduce time division.", 1);
      }
    }
    if (e.getSource() == fulItem || e.getSource() == fullbutton) {
      if (fullscreen != fulItem.isSelected() || e.getSource() == fullbutton) {
        if (e.getSource() == fullbutton) fulItem.setSelected(true);
        fullscreen = fulItem.isSelected();
        if (fullscreen) {
          normaldimension = getSize();
          normalpoint = getLocation();
          remove(toolBar);
          Toolkit tk = Toolkit.getDefaultToolkit();
          Dimension scd = tk.getScreenSize();
          setLocation(0, 0);
          setSize(scd);
          setResizable(false);
          dispatchEvent(
              new java.awt.event.ComponentEvent(
                  this, java.awt.event.ComponentEvent.COMPONENT_RESIZED));
          toFront();
        } else {
          getContentPane().add(toolBar, BorderLayout.NORTH);
          setLocation(normalpoint);
          setResizable(true);
          setSize(normaldimension);
          dispatchEvent(
              new java.awt.event.ComponentEvent(
                  this, java.awt.event.ComponentEvent.COMPONENT_RESIZED));
        }
      }
    }
    if (e.getSource() == walbutton || e.getSource() == walItem) { // Cepstrum View
      if (!spectrumExist) messageBox("Wavelet", "Please open Mediafile first.", 1);
      else if (samplesall > gad.walwindowlength) {
        System.out.println("--> Show Wavelet View");
        gad.p1.setSelectedIndex(17);
        av.setVisible(!av.isVisible());
      } else messageBox("Wavelet", "Signal to short !!!.", 1);
    }
    if (e.getSource() == arrangebutton) {
      arrangeWindows();
    }
  }
  // -------------------------------------------------------------------------------------------------------------------------
  /**
   * This fumction arranges the windows to a callculated position. The Positions are calculated with
   * the rule of the "Golden Cut"
   */
  public void arrangeWindows() {
    // diable all windows if they are all visible
    if (fv.isVisible()
        && wv.isVisible()
        && lv.isVisible()
        && cv.isVisible()
        && kv.isVisible()
        && av.isVisible()
        && pv.isVisible()) {
      fv.setVisible(false);
      wv.setVisible(false);
      cv.setVisible(false);
      lv.setVisible(false);
      kv.setVisible(false);
      av.setVisible(false);
      pv.setVisible(false);
      setSize(1025, 550);
      setLocationRelativeTo(null);
      return;
    }

    double goldencut = (Math.sqrt(5) + 1.0) / 2.0;
    int sw = (int) Toolkit.getDefaultToolkit().getScreenSize().getWidth();
    int sh = (int) Toolkit.getDefaultToolkit().getScreenSize().getHeight();
    int s16sh = sh * 1 / 6;
    int s26sh = sh * 2 / 6;
    int s36sh = sh * 3 / 6;
    int s46sh = sh * 4 / 6;
    int s56sh = sh * 5 / 6;
    int ssw = sw - 485; // window width for the main window
    int ssh =
        (int) (ssw / goldencut)
            + 94; // 94 is the offset for the border the toolbar and the menubar
    int sww = sw - ssw; // window width for the anayse windows
    int swh = s16sh;
    setSize(ssw, ssh);
    setLocation(0, 0);
    fv.setSize(sww, swh);
    fv.setLocation(ssw, 0);
    wv.setSize(sww, swh);
    wv.setLocation(ssw, s16sh);
    lv.setSize(sww, swh);
    lv.setLocation(ssw, s26sh);
    cv.setSize(sww, swh);
    cv.setLocation(ssw, s36sh);
    kv.setSize(sww, swh);
    kv.setLocation(ssw, s46sh);
    av.setSize(sww, swh);
    av.setLocation(ssw, s56sh);
    pv.placePitchwindowUnderTheMainWindow();
    fv.setVisible(true);
    wv.setVisible(true);
    lv.setVisible(true);
    cv.setVisible(true);
    av.setVisible(true);
    kv.setVisible(true);
    pv.setVisible(true);
    dispatchEvent(
        new java.awt.event.ComponentEvent(this, java.awt.event.ComponentEvent.COMPONENT_RESIZED));
    dispatchEvent(
        new java.awt.event.ComponentEvent(gad, java.awt.event.ComponentEvent.COMPONENT_RESIZED));
    dispatchEvent(
        new java.awt.event.ComponentEvent(cv, java.awt.event.ComponentEvent.COMPONENT_RESIZED));
    dispatchEvent(
        new java.awt.event.ComponentEvent(fv, java.awt.event.ComponentEvent.COMPONENT_RESIZED));
    dispatchEvent(
        new java.awt.event.ComponentEvent(hd, java.awt.event.ComponentEvent.COMPONENT_RESIZED));
    dispatchEvent(
        new java.awt.event.ComponentEvent(av, java.awt.event.ComponentEvent.COMPONENT_RESIZED));
    dispatchEvent(
        new java.awt.event.ComponentEvent(kv, java.awt.event.ComponentEvent.COMPONENT_RESIZED));
    dispatchEvent(
        new java.awt.event.ComponentEvent(pv, java.awt.event.ComponentEvent.COMPONENT_RESIZED));
  }

  // -------------------------------------------------------------------------------------------------------------------------

  /**
   * This class assumes that the string used to initialize fullPath has a directory path, filename,
   * and extension. The methods won't work if it doesn't.
   */
  class Filename {
    private String fullPath;
    private char pathSeparator;
    private char extensionSeparator;

    public Filename(String str) {
      fullPath = str;
      pathSeparator = File.pathSeparatorChar;
      extensionSeparator = '.';
    }

    public String extension() {
      int dot = fullPath.lastIndexOf(extensionSeparator);
      return fullPath.substring(dot + 1);
    }

    public String filename() { // gets filename without extension
      int dot = fullPath.lastIndexOf(extensionSeparator);
      int sep = fullPath.lastIndexOf(pathSeparator);
      return fullPath.substring(sep + 1, dot);
    }

    public String path() {
      int sep = fullPath.lastIndexOf(pathSeparator);
      return fullPath.substring(0, sep);
    }
  } // end of inner class Filename

  // -------------------------------------------------------------------------------------------------------------------------
  /**
   * Open the DataSourceReader. param fp Filepath to Mediafile. Is fp null so this Funktion opened
   * an Filechooser.
   */
  public void openFile(String fp) {
    if (wvconfig.isVisible()) wvconfig.setVisible(false);
    if (fp == null) {
      // If no File is given Filechooser is activated
      System.out.println("--> FileChooser opened");
      Action object = this.chooser.getActionMap().get("viewTypeDetails");
      object.actionPerformed(null);
      int returnVal = chooser.showOpenDialog(this);
      if (returnVal == JFileChooser.APPROVE_OPTION) { // this when Valid Fileselection
        java.io.File file = chooser.getSelectedFile();
        fp = file.getAbsolutePath();
        System.out.println("--> Choosed File: " + fp);
      } else {
        repaint();
        System.out.println("--> Filechooser Caneled");
        return;
      }
    } else {
      // Set File in FileChooser
      File file = new File(fp);
      System.out.println("--> Open File without choosing it from FileChooser: " + file);
      chooser.setSelectedFile(file);
    }
    if (player != null) {
      player.close();
      player.timeThread.interrupt();
    }

    // MP3 MP3 MP3 MP3 MP3 MP3 MP3 MP3 MP3 MP3 MP3 MP3 MP3 MP3 MP3 MP3 MP3 MP3 MP3 MP3 MP3 M
    // P3 MP3 MP3 MP3 MP3 MP3 MP3 MP3 MP3 MP3 MP3 MP3 MP3 MP3 MP3 MP3 MP3 MP3 MP3 MP3 MP3 MP
    // 3 MP3 MP3 MP3 MP3 MP3 MP3 MP3 MP3 MP3 MP3 MP3 MP3 MP3 MP3 MP3 MP3 MP3 MP3 MP3 MP3 MP3
    Filename fn = new Filename(fp);
    if (fn.extension().equals("mp3") || fn.extension().equals("MP3")) {
      System.out.println("--> Convert MP3 to WAV");
      // show the mp3 information dialog
      if (!mp3confirm) {
        JCheckBox askAgain =
            new JCheckBox("<html><i><font size = -2>Do not show this information message again");
        Object[] message = new Object[2];
        message[0] =
            "<html>The MP3 file must be <u><font COLOR=#660000>converted to a WAVE file</u></font>."
                + " This may<br>take a little. The converted WAVE file will be placed in"
                + " the<br>same folder as the MP3 file and can have a big File size.<br>Please note"
                + " that very large MP3's produce big Spectograms,<br>and you must first zoom-in to"
                + " see any usefully information.<br>It could also be that the Sonogram was fogged"
                + " because the<br>grid is to dense. Disable the grid in this case.<br><br><font"
                + " size=4>Continue with the convertion ?";
        message[1] = askAgain;
        int confirm =
            JOptionPane.showConfirmDialog(
                this,
                message,
                "Convert MP3 to WAVE file",
                JOptionPane.YES_NO_OPTION,
                JOptionPane.INFORMATION_MESSAGE);
        if (askAgain.isSelected()) mp3confirm = true;
        if (confirm == JOptionPane.NO_OPTION) return;
      }
      String wavFileName = fn.filename() + ".wav";
      String mp3FileName = fn.filename() + "." + fn.extension();
      // convert the mp3 to wav
      String[] args = new String[5];
      args[0] = "Hier sollte der java call stehen...";
      args[1] = "-v";
      args[2] = "-p";
      args[3] = wavFileName;
      args[4] = mp3FileName;
      // call the converter
      try {
        javazoom.jl.converter.jlc.main(args);
      } catch (Throwable throwable) {
        JOptionPane.showMessageDialog(
            null,
            "<html>An Error occoured while the MP3 Conversion.<br>The selected MP3 File can not be"
                + " opened !",
            "MP3 Conversion ERROR",
            JOptionPane.ERROR_MESSAGE);
      }
      fp = wavFileName;
    }
    // MP3 MP3 MP3 MP3 MP3 MP3 MP3 MP3 MP3 MP3 MP3 MP3 MP3 MP3 MP3 MP3 MP3 MP3 MP3 MP3 MP3 M
    // P3 MP3 MP3 MP3 MP3 MP3 MP3 MP3 MP3 MP3 MP3 MP3 MP3 MP3 MP3 MP3 MP3 MP3 MP3 MP3 MP3 MP
    // 3 MP3 MP3 MP3 MP3 MP3 MP3 MP3 MP3 MP3 MP3 MP3 MP3 MP3 MP3 MP3 MP3 MP3 MP3 MP3 MP3 MP3

    File file = new File(fp.substring(5));
    chooser.setFile(file);
    setTitle("Sonogram Visible Speech - version " + VERSION + " - " + file.getName());
    openingflag = true;
    filepath = fp;
    // Some Stuff
    repaint();
    if (fp.substring(0, 3).equals("ftp") || fp.substring(0, 4).equals("http"))
      url = fp; // network
    else if (fp.substring(0, 4).equals("file")) url = fp; // local
    else url = "file:" + fp; // local

    System.out.println("--> Opening URL: " + url);
    setCursor(new Cursor(Cursor.DEFAULT_CURSOR));
    reader = null;
    reader = new DataSourceReader();
    reader.setMainRef(this);
    reader.generateSamplesFromURL(url);
    gad.highLightButton(0);
    logItem.setEnabled(true);
  }

  // -------------------------------------------------------------------------------------------------------------------------

  /**
   * This Funktion is caled from 'readerIsBack' method and normalized the komplette Spektrum from 0
   * to 255.
   */
  private void normalizeSpectrum() {
    float[] tempSpektrum;
    float min = Float.MAX_VALUE;
    float max = -Float.MAX_VALUE;
    for (int x = 0; x < spectrum.size(); x++) { // find min/max Points of Spektrum
      tempSpektrum = spectrum.get(x);
      for (int y = 0; y < (timewindowlength / 2); y++) {
        if (max < tempSpektrum[y]) {
          max = tempSpektrum[y];
          peaky = y;
          peakx = x;
        }
        if (min > tempSpektrum[y]) min = tempSpektrum[y];
      }
    }
    System.out.println("--> Normalize: MAX = " + max + ", MIN = " + min);
    float diff = max - min;
    // Normalizes the Spektrum to 0..255 and test the range
    for (int x = 0; x < spectrum.size(); x++) {
      tempSpektrum = spectrum.get(x);
      for (int y = 0; y < (timewindowlength / 2); y++) {
        tempSpektrum[y] = ((tempSpektrum[y] - min) / diff * 255.0f); // Normalisation
        if (tempSpektrum[y] < 0.0f) {
          System.out.println("--> BEEP: min = " + tempSpektrum[y]);
          tempSpektrum[y] = 0.0f;
        }
        if (tempSpektrum[y] > 255.0f) {
          System.out.println("--> BEEP: max = " + tempSpektrum[y]);
          tempSpektrum[y] = 255.0f;
        }
      }
    }
  }

  // -------------------------------------------------------------------------------------------------------------------------
  /**
   * A litle Procedure to shows a Messagebox on Screen last parameter must be the Icontype for
   * Messagebox. Valid Numbers are -1,0,1,2,3. -1 = No ICON 0 = STOP (RED) 1 = INFORMATION (GRAY) 2
   * = ATTENTION (ORANGE) 3 = QUESTION (GREEN)
   *
   * @param title is the Title of messagebox
   * @param message is the message to display on screen
   * @param type specified the type of messagebox (sse above).
   */
  public void messageBox(String title, String message, int type) {
    JOptionPane.showMessageDialog(this, message, title, type);
  }
  // -------------------------------------------------------------------------------------------------------------------------
  /**
   * This Function saves the current Sonogram Settings in SonogramConfig.xml file, by starting the
   * Constructor of the SaveConfig CLASS.
   */
  public void saveConfig() {
    SaveConfig sc = new SaveConfig(this);
  }
  // -------------------------------------------------------------------------------------------------------------------------
  /**
   * This Class initalize the GAD params to his default values. It is called by Reset to dafault
   * Values, or while beginning when "save config at end" is not enabled.
   */
  public void initGadToDefault() {

    iinv = false;
    iloga = true;
    igrid = true;
    itooltip = true;
    iautowinl = true;
    inorsi = false;
    ismof = false;
    ismosi = true;
    ienergy = false;
    iloglpc = false;
    ilogfour = true;
    iopen8 = false;
    ismot = false;
    ipide = false;
    ipifrlim = false;
    ienov = true;
    isavehi = true;
    ipitchfog = true;
    ipitchblack = false;
    ipitchsm = true;
    ispecpl = true;
    ilooppl = false;
    imonoso = false;
    ilogf = false;
    isascpo = true;
    isaco = true;
    iffttrans = true;
    iopenlast = true;
    ilastwithzoom = true;
    isarr = false;
    iceplog = false;
    iwavelines = true;
    imute = false;
    ilocalpeak = true;
    iuniverse = false;
    iwallog = true;
    iacdl = true;
    iacsmooth = true;
    iacpitch = true;
    iantialise = true;
    iantialisconfirmed = false;
    iwvconfirmed = false;
    WvSettingsDialog.wvmessage = false;
    iwfnorm = false;
    icepsmooth = true;
    irotate = true;
    iperantialias = false;
    ipercoord = true;
    ipraway = true;
    ipsmo = true;
    iprsil = true;
    ipclin = true;
    iptrack = false;
    ipfog = true;
    i3dpolygons = false;
    mp3confirm = false;
    perkeysconfirm = false;

    isacpwl = 2;
    isacpws = 4;
    isacpmax = 400;
    iswaloct = 9;
    iswalsel = 0;
    islws = 9;
    islwf = 4;
    islov = 4;
    islsdr = 1;
    islff = 4;
    islfl = 2;
    isllc = 30;
    islf = 10;
    islls = 500;
    isllff = 4;
    islsy = 10;
    islsx = 10;
    islpf = 500;
    islla = 3;
    islcep = 9;
    islwavetime = 4;
    isldb = 0;
    ilaf =
        1; // NOTE --> the value is seem from the button. This is not the hash key of the hash table
           // in the dialog but the SLIDER value, more like MAX-KEY...
    isacwl = 3;
    isacws = 3;
    isr = 2;
    ibgcol = new Color(0, 0, 0);
    isc = 1;
  }
} // end of Sonogram class
