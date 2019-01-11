/*
 * ExampleApplication.java
 * (C) 2009, 2010, 2017, 2019 by
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
  public static final double SAMPLE_FREQUENCY = 44100.0; // [Hz]
  public static final Wave DEFAULT_WAVE =
    new SinoidalSynthWave(SAMPLE_FREQUENCY, new double[] {440.0, 880.0});
  public static final int WINDOW_SIZE = 2048;
  public static final int ROUNDS = 2048;
  public static final String IMAGE_WAVE_PLOT_FILENAME = "wave.plot.data";
  public static final String DEFAULT_DFT_IMAGE_FILENAME = "spectrum_dft.ppm";
  public static final String DEFAULT_DST_IMAGE_FILENAME = "spectrum_dst.ppm";
  private static final double doublePI = 2.0 * Math.PI;
  private static final double invPI = 1.0 / Math.PI;

  private static final boolean CREATE_PPM = true;
  private static final boolean CREATE_PLOT = true;

  private Date startDate, stopDate;

  private static class Arguments
  {
    private boolean dft, dst;
    private boolean inputWaveFileNamePending, inputWaveFileNameParsed;
    private String inputWaveFileName;
    private boolean outFileNamePending, outFileNameParsed;
    private String outFileName;
    private boolean μ0Pending, μ0Parsed;
    private double μ0;

    private Arguments()
    {
    }

    public Arguments(final String[] argv)
    {
      this();
      parse(argv);
    }

    private void parse(final String argv[]) {
      for (int argPos = 0; argPos < argv.length; argPos++) {
        final String arg = argv[argPos];
        if (μ0Pending) {
          try {
            μ0 = Double.parseDouble(arg);
            μ0Parsed = true;
            μ0Pending = false;
          } catch (final Exception e) {
            throw new IllegalArgumentException("value for μ0 is not a valid double");
          }
        } else if (inputWaveFileNamePending) {
          inputWaveFileName = arg;
          inputWaveFileNameParsed = true;
          inputWaveFileNamePending = false;
        } else if (outFileNamePending) {
          outFileName = arg;
          outFileNameParsed = true;
          outFileNamePending = false;
        } else if ("--dft".equals(arg)) {
          if (dft == true) {
            throw new IllegalArgumentException("--dft seen twice");
          }
          dft = true;
        } else if ("--dst".equals(arg)) {
          if (dst == true) {
            throw new IllegalArgumentException("--dst seen twice");
          }
          dst = true;
        } else if ("--mu0".equals(arg)) {
          if (μ0Parsed) {
            throw new IllegalArgumentException("--mu0 seen twice");
          }
          μ0Pending = true;
        } else if ("--inputwavefile".equals(arg)) {
          if (inputWaveFileNameParsed) {
            throw new IllegalArgumentException("--inputwavefile seen twice");
          }
          inputWaveFileNamePending = true;
        } else if ("--out".equals(arg)) {
          if (outFileNameParsed) {
            throw new IllegalArgumentException("--out seen twice");
          }
          outFileNamePending = true;
        }
      }
    }

    private void check() {
      if (dft && dst) {
        throw new IllegalArgumentException("--dst and --dft can not be specified together");
      }
      if (!dft && !dst) {
        throw new IllegalArgumentException("either --dft or --dst must be specified");
      }
      if (!dst && μ0Parsed) {
        throw new IllegalArgumentException("--mu0 specified without --dst");
      }
      if ((μ0 <= 0.0) || (μ0 >= 1.0)) {
        throw new IllegalArgumentException("μ0 must be greater than 0 and less than 1");
      }
    }

    public String getInputWaveFileName() {
      return inputWaveFileName;
    }

    public String getOutFileName() {
      return outFileName;
    }

    public double getμ0() {
      return μ0;
    }
  }

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

  public void createDFTView(final String imageFileName, final Wave wave)
    throws IOException
  {
    wave.reset();
    final PPMStreamOutput imageStream;
    final PrintWriter imageWavePlotter;
    if (CREATE_PLOT) {
      String imageWavePlotFilename =
        IMAGE_WAVE_PLOT_FILENAME; // TODO: Add as command line arg
      System.out.printf("[writing wave plot data to file '%s']\r\n",
                        imageWavePlotFilename);
      imageWavePlotter = new PrintWriter(imageWavePlotFilename);
    }
    if (CREATE_PPM) {
      System.out.printf("[writing DFT spectrum image to file '%s']\r\n",
                        imageFileName);
      imageStream = new PPMStreamOutput(imageFileName, WINDOW_SIZE, ROUNDS);
    }
    final SlidingWindowTransform slidingWindow =
      new DFTSlidingWindow(WINDOW_SIZE);
    slidingWindow.printInfo(System.out, SAMPLE_FREQUENCY);
    final ProgressInfo progressInfo = new ProgressInfo();
    final ProgressDisplay progressDisplay = new ProgressDisplay(progressInfo);
    new Thread(progressDisplay).start();
    for (progressInfo.sampleCount = 0;
         progressInfo.sampleCount < ROUNDS;
         progressInfo.sampleCount++) {
      final double originalSample = wave.getNextSample();
      slidingWindow.putBin(originalSample);
      if (CREATE_PPM) {
        for (int i = 0; i < (slidingWindow.getSize()); i++) {
          final Complex line = slidingWindow.getLine(i);
          final double value = line.getLength();
          final double hue = (value - 0.5) * doublePI;
          imageStream.putPixel(hue, 1.0, 0.003 * value);
        }
      }
      if (CREATE_PLOT) {
        final double reconstructedSample =
          slidingWindow.getReconstructedSample();
        imageWavePlotter.printf("%5d %5.3f %5.3f\r\n",
                                progressInfo.sampleCount,
                                originalSample, reconstructedSample);
      }
    }
    progressInfo.sampleAndHold();
    System.out.println(progressInfo.getProgressDisplayValue());
    if (CREATE_PPM)
      imageStream.close();
    if (CREATE_PLOT)
      imageWavePlotter.close();
  }

  public void createDSTView(final String imageFileName,
                            final double μ0, final Wave wave)
    throws IOException
  {
    wave.reset();
    final PPMStreamOutput imageStream;
    final PrintWriter imageWavePlotter;
    if (CREATE_PLOT) {
      final String imageWavePlotFilename =
        IMAGE_WAVE_PLOT_FILENAME; // TODO: Add as command line arg
      System.out.printf("[writing wave plot data to file '%s']\r\n",
                        imageWavePlotFilename);
      imageWavePlotter = new PrintWriter(imageWavePlotFilename);
    }
    if (CREATE_PPM) {
      System.out.printf("[writing DST spectrum image to file '%s']\r\n",
                        imageFileName);
      imageStream = new PPMStreamOutput(imageFileName, WINDOW_SIZE, ROUNDS);
    }
    final SlidingWindowTransform slidingWindow =
      new DSTSlidingWindow(μ0, WINDOW_SIZE,
                           DSTSlidingWindow.DEFAULT_LOWER_BOUND,
                           DSTSlidingWindow.DEFAULT_UPPER_BOUND);
    slidingWindow.printInfo(System.out, SAMPLE_FREQUENCY);
    final ProgressInfo progressInfo = new ProgressInfo();
    final ProgressDisplay progressDisplay = new ProgressDisplay(progressInfo);
    new Thread(progressDisplay).start();
    for (progressInfo.sampleCount = 0;
         progressInfo.sampleCount < ROUNDS;
         progressInfo.sampleCount++) {
      final double originalSample = wave.getNextSample();
      slidingWindow.putBin(originalSample);
      if (CREATE_PPM) {
        for (int i = 0; i < (slidingWindow.getSize()); i++) {
          final Complex line = slidingWindow.getLine(i);
          final double value = line.getLength();
          final double hue = (value - 0.5) * doublePI;
          imageStream.putPixel(hue, 1.0, 0.05 * value);
        }
      }
      if (CREATE_PLOT) {
        final double reconstructedSample =
          slidingWindow.getReconstructedSample();
        imageWavePlotter.printf("%5d %5.3f %5.3f\r\n",
                                progressInfo.sampleCount,
                                originalSample, reconstructedSample);
      }
    }
    progressInfo.sampleAndHold();
    System.out.println(progressInfo.getProgressDisplayValue());
    if (CREATE_PPM)
      imageStream.close();
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

  public void run(final String[] argv) throws IOException
  {
    final Arguments args = new Arguments(argv);
    final String inputWaveFileName = args.getInputWaveFileName();
    final Wave wave;
    if (inputWaveFileName != null) {
      wave = new WaveFileReader(inputWaveFileName);
    } else {
      wave = DEFAULT_WAVE;
    }
    markTime();
    final String outFileName = args.getOutFileName();
    if (args.dft) {
      System.out.printf("[creating DFT]\r\n");
      createDFTView(outFileName != null ?
                    outFileName :
                    DEFAULT_DFT_IMAGE_FILENAME, wave);
      printElapsedAndMarkTime();
    } else if (args.dst) {
      System.out.printf("[creating DST]\r\n");
      createDSTView(outFileName != null ?
                    outFileName :
                    DEFAULT_DST_IMAGE_FILENAME, args.μ0, wave);
      printElapsedAndMarkTime();
    }
  }

  public static void main(final String[] argv) throws IOException
  {
    new ExampleApplication().run(argv);
  }
}

/*
 * Local Variables:
 *   coding:utf-8
 * End:
 */
