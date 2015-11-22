package fent.de.tum.in.gesturesid.preprocessing;

import fent.de.tum.in.gesturesid.sensormeasurement.SensorData;

/**
 * A composing preprocessor, that combines multiple Preprocessors and executes them sequentially
 */
public class ComposingPreprocessor implements Preprocessor {

    private final Preprocessor[] preprocessors;

    public ComposingPreprocessor(Preprocessor... preprocessors) {
        this.preprocessors = preprocessors;
    }

    @Override
    public SensorData preprocess(SensorData data) {
        if(preprocessors == null) {
            return data;
        }
        SensorData result = data;
        for (Preprocessor preprocessor : preprocessors) {
            result = preprocessor.preprocess(data);
        }

        return result;
    }
}
