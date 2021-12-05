/**
 * Copyright (c) 2001 Christoph Lauer @ DFKI, All Rights Reserved. clauer@dfki.de - www.dfki.de
 *
 * <p>My FileFilter implementation for Mediafile filters. It extend default FileFilter Class.
 *
 * @author Christoph Lauer
 * @version 1.0, Current 26/09/2002
 */
package de.dfki.sonogram;

import java.io.File;

class MediaFileFilter extends javax.swing.filechooser.FileFilter {
  // -------------------------------------------------------------------------
  private String mediaDescription = null;
  private String mediaExtension = null;
  private boolean allMediaFilesFlag = false;
  // -------------------------------------------------------------------------
  /** The Construkltor */
  public MediaFileFilter(String extension, String description) {
    mediaDescription = description;
    mediaExtension = "." + extension.toLowerCase();
  }
  // -------------------------------------------------------------------------
  public void aceptAllMediaFiles(boolean flag) {
    allMediaFilesFlag = flag;
  }
  // -------------------------------------------------------------------------
  public String getDescription() {
    return mediaDescription;
  }
  // -------------------------------------------------------------------------
  public boolean accept(File f) {
    if (f == null) return false;
    if (f.isDirectory()) return true;
    if (!allMediaFilesFlag) return f.getName().toLowerCase().endsWith(mediaExtension);
    else
      return (f.getName().toLowerCase().endsWith("wav")
          || f.getName().toLowerCase().endsWith("au")
          || f.getName().toLowerCase().endsWith("avi")
          || f.getName().toLowerCase().endsWith("aiff")
          || f.getName().toLowerCase().endsWith("swf")
          || f.getName().toLowerCase().endsWith("spl")
          || f.getName().toLowerCase().endsWith("gsm")
          || f.getName().toLowerCase().endsWith("mvr")
          || f.getName().toLowerCase().endsWith("mpg")
          || f.getName().toLowerCase().endsWith("mp2")
          || f.getName().toLowerCase().endsWith("mp3")
          || f.getName().toLowerCase().endsWith("mov"));
  }
  // -------------------------------------------------------------------------

}
