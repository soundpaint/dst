/*
 * ProgressDisplay.java
 * (C) 2017, 2019 by
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

public class ProgressDisplay implements Runnable
{
  public static interface ProgressInfo
  {
    /**
     * Samples and holds the current progress of a task.
     */
    void sampleAndHold();

    /**
     * Returns a value in the range 0.0 to 1.0 that represents the
     * progress of a task as of the previous call to method
     * sampleAndHold().  A value of 0.0 means that the task has not
     * made any progress so far.  1.0 means that the task is finished.
     * Ideally, the progress value should grow linearly over time,
     * though in practice, this goal can typically be achieved only
     * approximately due to unforseeable effects.
     */
    double getProgressValue();

    /**
     * Returns a string that represents the progress of a task as of
     * the previous call to method sampleAndHold() in a nicely
     * formatted manner.  For example, this method may return a
     * percentage value in the range 0% .. 100%.
     */
    String getProgressDisplayValue();
  }

  private final ProgressInfo progressInfo;

  private ProgressDisplay()
  {
    this(null);
  }

  public ProgressDisplay(final ProgressInfo progressInfo)
  {
    if (progressInfo == null)
      throw new NullPointerException("progressInfo");
    this.progressInfo = progressInfo;
  }

  public void run()
  {
    boolean finished = false;
    while (!finished) {
      progressInfo.sampleAndHold();
      finished = progressInfo.getProgressValue() >= 1.0;
      if (!finished) {
        System.out.print(progressInfo.getProgressDisplayValue() + "\r");
        try {
          Thread.sleep(500);
        } catch (final Exception e) {
          // ignore
        }
      }
    }
  }
}

/*
 * Local Variables:
 *   coding:utf-8
 * End:
 */
