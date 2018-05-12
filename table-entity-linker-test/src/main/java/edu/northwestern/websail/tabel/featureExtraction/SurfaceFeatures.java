package edu.northwestern.websail.tabel.featureExtraction;

import edu.northwestern.websail.tabel.model.Candidate;
import edu.northwestern.websail.tabel.model.Mention;

import java.util.HashMap;

/**
 * Surface Features in the paper. It include two features for a mention (surface, candidate)
 * isMentionExact : if surface is the only text in the cell
 * surfaceTitleMatch : if surface matches with the candidate title
 */
public class SurfaceFeatures {

    public static Double isMentionExact(Candidate candidate) {
        return candidate.getIsMentionExact() ? 1.0 : 0.0;
    }

    /**
     * the candidate title is already obtained from the id to title map
     */
    public static Double surfaceTitleMatch(Mention mention, Candidate candidate) {
        String title = candidate.wikiTitle.title;
        String surface = mention.surfaceForm.toLowerCase();
        title = title.toLowerCase();
        title = title.replaceAll("_", " ");
        boolean surfaceMatch = title.equals(surface);
        Double value = surfaceMatch ? 1.0 : 0.0;
        return value;
    }
}
