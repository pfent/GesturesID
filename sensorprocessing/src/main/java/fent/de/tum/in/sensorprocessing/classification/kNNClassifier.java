package fent.de.tum.in.sensorprocessing.classification;

import android.util.Pair;

import java.util.Comparator;
import java.util.PriorityQueue;

import fent.de.tum.in.sensorprocessing.classification.classificationDistances.ClassificationDistancer;
import fent.de.tum.in.sensorprocessing.featureextraction.FeatureVectors;

public class kNNClassifier extends Classifier {

    private static final Comparator<Pair<Float, Integer>> pairComparator = new Comparator<Pair<Float, Integer>>() {
        @Override
        public int compare(Pair<Float, Integer> lhs, Pair<Float, Integer> rhs) {
            return lhs.first.compareTo(rhs.first);
        }
    };
    private final int neighbours;

    protected kNNClassifier(FeatureVectors[][] categories, ClassificationDistancer distancer, int neighbours) {
        super(categories, distancer);
        this.neighbours = neighbours;
    }

    @Override
    public int classify(FeatureVectors toCompare) {
        PriorityQueue<Pair<Float, Integer>> heap = new PriorityQueue<>(neighbours + 1, pairComparator);

        for (int i = 0; i < categories.length; i++) {
            final FeatureVectors[] category = categories[i];
            for (FeatureVectors featureVectors : category) {

                final float comparison = distancer.compare(featureVectors, toCompare);

                final Pair<Float, Integer> pair = new Pair<>(comparison, i);

                // add the new comparison
                heap.add(pair);

                // TODO: right order?
                // remove the worst vector, so we don't need to keep everything in memory
                if (heap.size() > neighbours) {
                    heap.poll();
                }
            }
        }

        // Calculate which category is most often a neighbour
        final int[] categoryCount = new int[categories.length];
        for (Pair<Float, Integer> pair : heap) {
            categoryCount[pair.second]++;
        }

        int maxIndex = -1;
        int maxValue = -1;
        for (int i = 0; i < categoryCount.length; i++) {
            if (categoryCount[i] > maxValue) {
                maxValue = categoryCount[i];
                maxIndex = i;
            }
        }

        return maxIndex;
    }
}
