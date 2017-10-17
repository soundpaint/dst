/*
 * ExampleApplication.java
 * (C) 2009, 2010, 2017 by
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

import java.io.IOException;
import java.io.PrintWriter;
import java.util.Date;

public class ExampleApplication
{
  public final static double SAMPLE_FREQUENCY = 44100.0; // [Hz]
  public final static Wave DEFAULT_WAVE =
    new SinoidalSynthWave(SAMPLE_FREQUENCY, new double[] {440.0, 880.0});
  public final static int WINDOW_SIZE = 2048;
  public final static int ROUNDS = 2048;
  public final static String IMAGE_WAVE_PLOT_FILENAME = "wave.plot.data";
  public final static String IMAGE_DFT_SPECTRUM_FILENAME = "spectrum_dft.ppm";
  public final static String IMAGE_DST_SPECTRUM_FILENAME = "spectrum_dst.ppm";
  private final static double doublePI = 2.0 * Math.PI;
  private final static double invPI = 1.0 / Math.PI;

  private final static boolean CREATE_DFT_PPM = false;
  private final static boolean CREATE_DST_PPM = true;
  private final static boolean CREATE_PLOT = false;

  private int sampleCount;

  private class ProgressDisplay implements Runnable
  {
    public void show(final int sampleCountSnapshot)
    {
      final double percent = (double)sampleCountSnapshot / ROUNDS * 100.0;
      System.out.printf("[%d of %d samples (%3.2f%%)]\r",
                        sampleCountSnapshot, ROUNDS, percent);
    }

    public void run()
    {
      boolean finished = false;
      while (!finished) {
        final int sampleCountSnapshot = sampleCount;
        finished = sampleCountSnapshot >= ROUNDS;
        if (!finished) {
          show(sampleCountSnapshot);
          try {
            Thread.sleep(500);
          } catch (Exception e) {
            // ignore
          }
        }
      }
    }
  }

  public void transform(Wave wave) throws IOException
  {
    PPMStreamOutput imageDFTSpectrum, imageDSTSpectrum;
    PrintWriter imageWavePlotter;
    if (CREATE_DFT_PPM) {
      System.out.printf("[writing DFT spectrum image to file '%s']\r\n",
			IMAGE_DFT_SPECTRUM_FILENAME);
      imageDFTSpectrum =
	new PPMStreamOutput(IMAGE_DFT_SPECTRUM_FILENAME,
			    WINDOW_SIZE, ROUNDS);
    }
    if (CREATE_DST_PPM) {
      System.out.printf("[writing DST spectrum image to file '%s']\r\n",
			IMAGE_DST_SPECTRUM_FILENAME);
      imageDSTSpectrum =
	new PPMStreamOutput(IMAGE_DST_SPECTRUM_FILENAME,
			    WINDOW_SIZE, ROUNDS);
    }
    if (CREATE_PLOT) {
      System.out.printf("[writing wave plot data to file '%s']\r\n",
			IMAGE_WAVE_PLOT_FILENAME);
      imageWavePlotter =
	new PrintWriter(IMAGE_WAVE_PLOT_FILENAME);
    }
    SlidingWindowTransform dftSlidingWindow =
      new DFTSlidingWindow(WINDOW_SIZE);
    SlidingWindowTransform dstSlidingWindow =
      new DSTSlidingWindow(WINDOW_SIZE);
    dstSlidingWindow.printInfo(System.out, SAMPLE_FREQUENCY);
    final ProgressDisplay progressDisplay = new ProgressDisplay();
    new Thread(progressDisplay).start();
    for (sampleCount = 0; sampleCount < ROUNDS; sampleCount++) {
      double originalSample = wave.getNextSample();
      dftSlidingWindow.putBin(originalSample);
      dstSlidingWindow.putBin(originalSample);
      if (CREATE_DFT_PPM) {
	for (int i = 0; i < (dftSlidingWindow.getSize()); i++) {
	  Complex line = dftSlidingWindow.getLine(i);
	  double value = line.getLength();
	  double hue = (value - 0.5) * doublePI;
	  imageDFTSpectrum.putPixel(hue, 1.0, 1.00 * value);
	}
      }
      if (CREATE_DST_PPM) {
	for (int i = 0; i < (dstSlidingWindow.getSize()); i++) {
	  Complex line = dstSlidingWindow.getLine(i);
	  double value = line.getLength();
	  double hue = (value - 0.5) * doublePI;
	  imageDSTSpectrum.putPixel(hue, 1.0, 0.05 * value);
	}
      }
      double reconstructedDFTSample =
	dftSlidingWindow.getReconstructedSample();
      double reconstructedDSTSample =
	dstSlidingWindow.getReconstructedSample();
      if (CREATE_PLOT) {
	imageWavePlotter.printf("%5d %5.3f %5.3f %5.3f\r\n",
				sampleCount, originalSample,
				reconstructedDFTSample,
				reconstructedDSTSample);
      }
    }
    progressDisplay.show(sampleCount);
    System.out.println();

    if (CREATE_DFT_PPM)
      imageDFTSpectrum.close();
    if (CREATE_DST_PPM)
      imageDSTSpectrum.close();
    if (CREATE_PLOT)
      imageWavePlotter.close();
  }

  public static void main(String argv[]) throws IOException
  {
    final Wave wave;
    if (argv.length == 1)
      wave = new WaveFileReader(argv[0]);
    else
      wave = DEFAULT_WAVE;
    final Date startDate = new Date();
    new ExampleApplication().transform(wave);
    final Date stopDate = new Date();
    final long milliseconds = stopDate.getTime() - startDate.getTime();
    final double seconds = milliseconds * 0.001;
    System.out.printf("[%5.3f seconds]\r\n", seconds);
  }
}

/*
 * Local Variables:
 *   coding:utf-8
 * End:
 */
