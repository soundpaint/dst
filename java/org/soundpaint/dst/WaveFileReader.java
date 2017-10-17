/*
 * WaveFileReader.java
 * (C) 2009 by
 * Jürgen Reuter <http://www.juergen-reuter.de/>
 *
 * Project Website: http://www.soundpaint.org/spectral-transform/
 * Jürgen Reuter, Rheinstr. 86, 76185 Karlsruhe, Germany.
 *
 * This file is free software; you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published
 * by the Free Software Foundation; either version 3 of the License,
 * or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful, but
 * WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 * General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see
 * <http://www.gnu.org/licenses/>.
 *
 * $Author$
 * $Date$
 * $Id$
 */
package org.soundpaint.dst;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import javax.sound.sampled.AudioFormat;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.UnsupportedAudioFileException;

public class WaveFileReader extends FiniteWave {
  private final static double PCM16_INV_RADIUS = 1.0 / 32768.0;
  private /*final*/ double SAMPLE_SCALE;
  private AudioInputStream in;
  private byte sample[];
  private String filename;
  private int channels;
  private boolean averageChannels;
  private boolean hasMoreSamples;
  private double lookAhead;

  public WaveFileReader(String filename)
    throws IOException
  {
    this(filename, true);
  }

  public WaveFileReader(String filename, boolean averageChannels)
    throws IOException
  {
    this.filename = filename;
    this.averageChannels = averageChannels;
    init();
  }

  private void init() throws IOException {
    try {
      if (in != null) {
	in.close();
      }
      File file = new File(filename);
      if (!file.exists())
        throw new IOException(filename + ": file not found");
      if (!file.canRead())
        throw new IOException(filename + ": file not readable");
      in = AudioSystem.getAudioInputStream(file);
    } catch (UnsupportedAudioFileException e) {
      throw new IOException(filename + ": " +
			    "unsupported audio format", e);
    }
    AudioFormat fmt = in.getFormat();
    if (fmt.getSampleSizeInBits() != 16) {
      throw new IOException(filename + ": " +
			    "unsupported audio format: " +
			    "sample size in bits must be 16");
    }
    if (fmt.getEncoding() != AudioFormat.Encoding.PCM_SIGNED) {
      throw new IOException(filename + ": " +
			    "unsupported audio format: " +
			    "encoding must be PCM signed");
    }
    if (fmt.isBigEndian()) {
      throw new IOException(filename + ": " +
			    "unsupported audio format: " +
			    "encoding must be little endian");
    }
    channels = fmt.getChannels();
    if (channels < 1) {
      throw new IOException(filename + ": " +
			    "unsupported audio format: " +
			    "invalid number of channels: " + channels);
    }
    if (averageChannels) {
      SAMPLE_SCALE = PCM16_INV_RADIUS / channels;
    } else {
      if (channels > 1) {
	System.err.println(filename + ": " +
			   "Warning: audio format is not mono - " +
			   "considering 1st channel only");
      }
      SAMPLE_SCALE = PCM16_INV_RADIUS;
    }
    sample = new byte[2 * channels];
    hasMoreSamples = true;
    lookAhead = prepareLookAhead(0);
  }

  private final static int META_DATA_LENGTH = 44;

  public long getTotalNumberOfSamples() {
    long fileLength = new File(filename).length();
    return (fileLength - META_DATA_LENGTH) / channels / 2;
  }

  public boolean eof() throws IOException {
    return !hasMoreSamples;
  }

  protected double getNextSample(long position) throws IOException {
    double nextSample = lookAhead;
    lookAhead = prepareLookAhead(position);
    return nextSample;
  }

  private double prepareLookAhead(long position) throws IOException {
    int intSample = 0;
    int bytes = in.read(sample, 0, 2 * channels);
    if (bytes < 2 * channels) {
      hasMoreSamples = false;
      return 0.0;
    }
    if (averageChannels) {
      for (int i = 0; i < 2 * channels; i += 2) {
	int lowByte = sample[i] & 0xff; // unsigned LSB
	int highByte = sample[i + 1]; // signed MSB
	intSample += highByte * 256 + lowByte;
      }
    } else {
      int lowByte = sample[0] & 0xff; // unsigned LSB
      int highByte = sample[1]; // signed MSB
      intSample += highByte * 256 + lowByte;
    }
    return intSample * SAMPLE_SCALE;
  }

  public void reset() throws IOException {
    boolean retry = false;
    do {
      retry = false;
      try {
	init();
      } catch (FileNotFoundException e) {
	System.out.println("[garbage collecting...]");
	System.gc();
	try  {
	  Thread.sleep(1000);
	} catch (Exception f) {}
	retry = true;
      }
    } while (retry);
  }

  public void skip(long n) throws IOException {
    long skipped = in.skip(n * 2 * channels) / (2 * channels);
    if (skipped != n)
      throw new IOException("skip failed: skipped " + skipped +
			    " samples");
  }

  public void close() throws IOException {
    in.close();
  }
}

/*
 * Local Variables:
 *   coding:utf-8
 * End:
 */
