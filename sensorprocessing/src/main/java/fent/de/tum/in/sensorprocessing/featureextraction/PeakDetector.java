package fent.de.tum.in.sensorprocessing.featureextraction;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.ListIterator;

/**
 * A standard peak detector in time series.
 * <p/>
 * The goal of this class is to identify peaks in a 1D time series (float[]).
 * It simply implements G.K. Palshikar's <i>Simple Algorithms for Peak Detection
 * in Time-Series</i> ( Proc. 1st Int. Conf. Advanced Data Analysis, Business
 * Analytics and Intelligence (ICADABAI2009), Ahmedabad, 6-7 June 2009),
 * We retained the first "spikiness" function he proposed, based on computing
 * the max signed distance to left and right neighbors.
 * <p/>
 * <pre>
 * 		http://sites.google.com/site/girishpalshikar/Home/mypublications/
 * 		SimpleAlgorithmsforPeakDetectioninTimeSeriesACADABAI_2009.pdf
 * </pre>
 *
 * @author Jean-Yves Tinevez <jeanyves.tinevez@gmail.com> May 10, 2011
 * @see https://code.google.com/p/fiji-bi/source/browse/src-plugins/FlowMate_/fiji/plugin/flowmate/analysis/PeakDetector.java?r=0ec4620b8e4aaebd183c2b57c89390595f574564
 */
public class PeakDetector {

    private final int windowSize;
    private final float stringency;
    private float[] T;

    /**
     * Create a peak detector for the given time series.
     *
     * @param timeSeriesData the time series data to be analyzed
     * @param windowSize     the window size to look for peaks. a neighborhood of +/- windowSize
     *                       will be inspected to search for peaks. Typical values start at 3.
     * @param stringency     threshold for peak values. Peak with values lower than <code>
     *                       mean + stringency * std</code> will be rejected. <code>Mean</code> and <code>std</code> are calculated on the
     *                       spikiness function. Typical values range from 1 to 3.
     */
    public PeakDetector(int windowSize, float stringency) {
        this.windowSize = windowSize;
        this.stringency = stringency;
    }

    private static float entropy(float[] a, int w) {
        float sum = 0;
        for (int i = 0; i < a.length - w; i++) {
            final float probDens = probabilityDensity(a, i, w);
            sum += (-probDens * ((float) Math.log(probDens)));
        }
        return sum;
    }

    private static float probabilityDensity(float[] a, int i, int w) {
        float sum = 0;
        final float divisor = Math.abs(a[i] - a[i + w]);
        for (int j = 0; j < a.length; j++) {
            sum += gaussianKernel((a[i] - a[j]) / divisor);
        }

        return sum / (a.length * divisor);

    }

    private static float gaussianKernel(float x) {
        return (float) (Math.exp(-(x * x) / 2) / Math.sqrt(2 * Math.PI));
    }

    public static float[] concat(float[] a, float[] b) {
        final float[] c = new float[a.length + b.length];
        System.arraycopy(a, 0, c, 0, a.length);
        System.arraycopy(b, 0, c, a.length, b.length);
        return c;
    }

    public PeakDetector setTimeSeriesData(float[] timeSeriesData) {
        this.T = timeSeriesData;
        return this;
    }

    /**
     * Return the peak locations as array index for the time series set at creation.
     *
     * @return an int array, with one element by retained peak, containing the index of
     * the peak in the time series array.
     */
    public int[] process() {

        // Compute peak function values
        final float[] S = palshikarS1();

        // Compute mean and std of peak function
        float sum = 0;
        for (float f : S) {
            sum += f;
        }
        final float mean = sum / S.length;

        sum = 0;
        for (float f : S) {
            sum += (mean - f) * (mean - f);
        }
        final float std = (float) Math.sqrt(sum / S.length);

        // Collect only large peaks
        ArrayList<Integer> peakLocations = new ArrayList<Integer>();
        for (int i = 0; i < S.length; i++) {
            if ((S[i] > 0) && (S[i] - mean) > (stringency * std)) {
                peakLocations.add(i);
            }
        }

        // retain only one peak out of any set of peaks within windowsize of each other
        // so, for every adjacent pair of peaks x1, x2 that lie within the windowsize, remove the
        // smaller value min(x1, x2)
        for (ListIterator<Integer> iterator = peakLocations.listIterator(); iterator.hasNext(); ) {
            if (!iterator.hasPrevious()) { // skip the first item
                iterator.next();
                continue;
            }
            Integer peak1index = peakLocations.get(iterator.previousIndex());
            Integer peak2index = iterator.next();

            if (peak2index - peak1index < windowSize) { // Too close to each other
                if (T[peak2index] > T[peak1index]) {
                    iterator.previous(); // go back to peak2index
                    iterator.previous(); // go back to peak1index
                    iterator.remove();   // and remove the smaller peak1index
                } else {
                    iterator.remove();
                }
            }

        }

        // Convert to int[]
        int[] peakArray = new int[peakLocations.size()];
        for (int i = 0; i < peakArray.length; i++) {
            peakArray[i] = peakLocations.get(i);
        }
        return peakArray;
    }

    /**
     * This function implements the "spikiness" function S1 according to Palshikar's paper
     * S1 is the average of the max/min of it's K left/right nneughbours
     *
     * @return the spikiness for each point in T
     */
    private float[] palshikarS1() {
        final float[] s = new float[T.length];

        for (int i = windowSize; i < s.length - windowSize; i++) {
            float maxLeft = T[i] - T[i - 1];
            float maxRight = T[i] - T[i + 1];

            for (int j = 2; j <= windowSize; j++) {
                final float leftCandidate = T[i] - T[i - j];
                if (leftCandidate > maxLeft) {
                    maxLeft = leftCandidate;
                }
                final float rightCandidate = T[i] - T[i + j];
                if (rightCandidate > maxRight) {
                    maxRight = rightCandidate;
                }
            }
            s[i] = 0.5f * (maxRight + maxLeft);

        }
        return s;
    }

    /**
     * This function implements the "spikiness" function S4 according to Palshikar's paper
     * S4 is the difference in the entropy of the window with and without the individual point
     *
     * @return the spikiness for each point in T
     */
    private float[] palshikarS4(int w) {
        final float[] s = new float[T.length];

        for (int i = windowSize; i < s.length - windowSize; i++) {
            final float[] windowWithoutCurrent = concat(
                    Arrays.copyOfRange(s, i - windowSize, i),  // Arrays.copyOfRange is EXCLUSIVE end
                    Arrays.copyOfRange(s, i + 1, i + windowSize + 1));
            final float[] windowWithCurrent = Arrays.copyOfRange(s, i - windowSize, i + windowSize + 1);

            s[i] = entropy(windowWithoutCurrent, w) - entropy(windowWithCurrent, w);
        }

        return s;
    }
}

