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
  private final static double[] μ0s =
  { 0.90000, 0.99000, 0.99270, 0.99900, 0.99990, 0.99999 };

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

  private final static boolean CREATE_DFT_PPM = true;
  private final static boolean CREATE_DST_PPM = true;
  private final static boolean CREATE_PLOT = false;

  private Date startDate, stopDate;

  /**
   * Holds all data that is shared between the progress display and
   * the actual process to be monitored.
   */
  private static class ProgressInfo implements ProgressDisplay.ProgressInfo
  {
    private int sampleCountSnapshot;

    public int sampleCount;

    public void sampleAndHold()
    {
      sampleCountSnapshot = sampleCount;
    }

    public double getProgressValue()
    {
      return (double)sampleCountSnapshot / ROUNDS;
    }

    public String getProgressDisplayValue()
    {
      final double percent = getProgressValue() * 100.0;
      return
        String.format("[%d of %d samples (%3.2f%%)]",
                      sampleCountSnapshot, ROUNDS, percent);
    }
  }

  public void createDFTView(Wave wave) throws IOException
  {
    wave.reset();
    PPMStreamOutput imageDFTSpectrum;
    PrintWriter imageWavePlotter;
    if (CREATE_PLOT) {
      System.out.printf("[writing wave plot data to file '%s']\r\n",
			IMAGE_WAVE_PLOT_FILENAME);
      imageWavePlotter =
	new PrintWriter(IMAGE_WAVE_PLOT_FILENAME);
    }
    if (CREATE_DFT_PPM) {
      System.out.printf("[writing DFT spectrum image to file '%s']\r\n",
			IMAGE_DFT_SPECTRUM_FILENAME);
      imageDFTSpectrum =
	new PPMStreamOutput(IMAGE_DFT_SPECTRUM_FILENAME, WINDOW_SIZE, ROUNDS);
    }
    SlidingWindowTransform dftSlidingWindow =
      new DFTSlidingWindow(WINDOW_SIZE);
    dftSlidingWindow.printInfo(System.out, SAMPLE_FREQUENCY);
    final ProgressInfo progressInfo = new ProgressInfo();
    final ProgressDisplay progressDisplay = new ProgressDisplay(progressInfo);
    new Thread(progressDisplay).start();
    for (progressInfo.sampleCount = 0;
         progressInfo.sampleCount < ROUNDS;
         progressInfo.sampleCount++) {
      double originalSample = wave.getNextSample();
      dftSlidingWindow.putBin(originalSample);
      if (CREATE_DFT_PPM) {
	for (int i = 0; i < (dftSlidingWindow.getSize()); i++) {
	  Complex line = dftSlidingWindow.getLine(i);
	  double value = line.getLength();
	  double hue = (value - 0.5) * doublePI;
	  imageDFTSpectrum.putPixel(hue, 1.0, 0.003 * value);
	}
      }
      double reconstructedDFTSample =
	dftSlidingWindow.getReconstructedSample();
      if (CREATE_PLOT) {
	imageWavePlotter.printf("%5d %5.3f %5.3f\r\n",
				progressInfo.sampleCount,
                                originalSample, reconstructedDFTSample);
      }
    }
    progressInfo.sampleAndHold();
    System.out.println(progressInfo.getProgressDisplayValue());
    if (CREATE_DFT_PPM)
      imageDFTSpectrum.close();
    if (CREATE_PLOT)
      imageWavePlotter.close();
  }

  public void createDSTView(int index, double μ0, Wave wave) throws IOException
  {
    wave.reset();
    PPMStreamOutput imageDSTSpectrum;
    PrintWriter imageWavePlotter;
    if (CREATE_PLOT) {
      String imageWavePlotFilename =
        IMAGE_WAVE_PLOT_FILENAME + "_" + index;
      System.out.printf("[writing wave plot data to file '%s']\r\n",
			imageWavePlotFilename);
      imageWavePlotter =
	new PrintWriter(imageWavePlotFilename);
    }
    if (CREATE_DST_PPM) {
      String imageDstSpectrumFilename =
        IMAGE_DST_SPECTRUM_FILENAME + "_" + index;
      System.out.printf("[writing DST spectrum image to file '%s']\r\n",
			imageDstSpectrumFilename);
      imageDSTSpectrum =
	new PPMStreamOutput(imageDstSpectrumFilename, WINDOW_SIZE, ROUNDS);
    }
    SlidingWindowTransform dstSlidingWindow =
      new DSTSlidingWindow(μ0, WINDOW_SIZE,
                           DSTSlidingWindow.DEFAULT_LOWER_BOUND,
                           DSTSlidingWindow.DEFAULT_UPPER_BOUND);
    dstSlidingWindow.printInfo(System.out, SAMPLE_FREQUENCY);
    final ProgressInfo progressInfo = new ProgressInfo();
    final ProgressDisplay progressDisplay = new ProgressDisplay(progressInfo);
    new Thread(progressDisplay).start();
    for (progressInfo.sampleCount = 0;
         progressInfo.sampleCount < ROUNDS;
         progressInfo.sampleCount++) {
      double originalSample = wave.getNextSample();
      dstSlidingWindow.putBin(originalSample);
      if (CREATE_DST_PPM) {
	for (int i = 0; i < (dstSlidingWindow.getSize()); i++) {
	  Complex line = dstSlidingWindow.getLine(i);
	  double value = line.getLength();
	  double hue = (value - 0.5) * doublePI;
	  imageDSTSpectrum.putPixel(hue, 1.0, 0.05 * value);
	}
      }
      double reconstructedDSTSample =
	dstSlidingWindow.getReconstructedSample();
      if (CREATE_PLOT) {
	imageWavePlotter.printf("%5d %5.3f %5.3f\r\n",
				progressInfo.sampleCount,
                                originalSample, reconstructedDSTSample);
      }
    }
    progressInfo.sampleAndHold();
    System.out.println(progressInfo.getProgressDisplayValue());
    if (CREATE_DST_PPM)
      imageDSTSpectrum.close();
    if (CREATE_PLOT)
      imageWavePlotter.close();
  }

  private void markTime()
  {
    startDate = new Date();
  }

  private void printElapsedAndMarkTime()
  {
    stopDate = new Date();
    final long milliseconds = stopDate.getTime() - startDate.getTime();
    final double seconds = milliseconds * 0.001;
    System.out.printf("[elapsed time: %5.3f seconds]\r\n", seconds);
    startDate = stopDate;
  }

  public void run(Wave wave) throws IOException
  {
    markTime();
    System.out.printf("[creating DFT]\r\n");
    createDFTView(wave);
    printElapsedAndMarkTime();
    for (int i = 0; i < μ0s.length; i++) {
      System.out.printf("[creating DST #%d of %d]\r\n", i + 1, μ0s.length);
      createDSTView(i, μ0s[i], wave);
      printElapsedAndMarkTime();
    }
  }

  public static void main(String argv[]) throws IOException
  {
    final Wave wave;
    if (argv.length == 1)
      wave = new WaveFileReader(argv[0]);
    else
      wave = DEFAULT_WAVE;
    new ExampleApplication().run(wave);
  }
}

/*
 * Local Variables:
 *   coding:utf-8
 * End:
 */
