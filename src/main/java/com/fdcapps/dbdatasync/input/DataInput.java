package com.fdcapps.dbdatasync.input;

import java.io.InputStream;

/**
 * DataInput is an interface which represents an input of a data
 *
 * @author Felipe Diaz C <felipediazc@fdcapps.com>
 */
public interface DataInput {

  /**
   * This method is used as a drone trajectory input.
   *
   * @param path This is the InputStream where de data comes as an input
   * @return a Stream of Strings
   * @throws java.lang.Exception
   */
  InputStream  getDataInput(String path);
}
