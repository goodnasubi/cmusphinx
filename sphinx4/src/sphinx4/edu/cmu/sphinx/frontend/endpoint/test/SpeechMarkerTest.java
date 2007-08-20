package edu.cmu.sphinx.frontend.endpoint.test;

import edu.cmu.sphinx.frontend.*;
import edu.cmu.sphinx.frontend.endpoint.SpeechEndSignal;
import edu.cmu.sphinx.frontend.endpoint.SpeechMarker;
import edu.cmu.sphinx.frontend.endpoint.SpeechStartSignal;
import edu.cmu.sphinx.frontend.test.AbstractTestProcessor;
import edu.cmu.sphinx.util.props.ConfigurationManager;
import edu.cmu.sphinx.util.props.PropertyException;
import static junit.framework.Assert.assertTrue;
import org.junit.Test;

import java.util.List;

/**
 * DOCUMENT ME!
 *
 * @author Holger Brandl
 */
public class SpeechMarkerTest extends AbstractTestProcessor {


    public BaseDataProcessor createDataFilter(boolean mergeSpeechSegments) {
        try {


            SpeechMarker speechMarker = (SpeechMarker) ConfigurationManager.getInstance(SpeechMarker.class);
            speechMarker.initialize();

//            Map<String, Object> props = new HashMap<String, Object>();
//            props.put(NonSpeechDataFilter.PROP_MERGE_SPEECH_SEGMENTS, mergeSpeechSegments);
//            NonSpeechDataFilter nonSpeechDataFilter = (NonSpeechDataFilter) ConfigurationManager.getInstance(NonSpeechDataFilter.class, props);
            return speechMarker;


        } catch (PropertyException e) {
            e.printStackTrace();
        }

        return null;
    }


    /**
     * Test whether the speech marker is able to handle cases in which an DataEndSignal occurs somewhere after a
     * SpeechStartSignal. This is might occur if the microphone is stopped while someone is speaking.
     */
    @Test
    public void testOneSpeechRegion() throws DataProcessingException {
        int sampleRate = 1000;

        input.add(new DataStartSignal(sampleRate));

        input.addAll(createFeatVectors(1, sampleRate, 0, 10, 10)); // create one second of data sampled with 1kHz
        input.add(new SpeechStartSignal(-1));
        input.addAll(createFeatVectors(1, sampleRate, 0, 10, 10));
        input.add(new DataEndSignal(-1));

        List<Data> results = collectOutput(createDataFilter(false));

        assertTrue(results.size() == 104);
        assertTrue(results.get(0) instanceof DataStartSignal);
        assertTrue(results.get(1) instanceof SpeechStartSignal);
        assertTrue(results.get(106) instanceof SpeechEndSignal);
        assertTrue(results.get(107) instanceof DataEndSignal);
    }
}
