package edu.northwestern.websail.tabel.featureExtraction;

import edu.northwestern.websail.tabel.config.GlobalConfig;
import edu.northwestern.websail.tabel.model.Candidate;
import edu.northwestern.websail.tabel.model.Mention;
import edu.northwestern.websail.tabel.model.TableMentionContext;
import edu.northwestern.websail.tabel.model.WtTable;
import pulse.util.SemanticRelatedness;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;

/**
 * Semantic Relatedness(SR) Features from paper
 * candidatePageSr: SR value of the page containing the table, and the candidate page
 * rowLinksAvgSr: SR value of the candidate page, and the pages of entities in the same row of the mention
 * colLinksAvgSr: similar to rowLinksAvgSr, but for column
 * fullLinksAvgSr: similar to rowLinksAvgSr, but for both row and column
 */
public class SemanticRelatednessFeatures {
    // pageSRMaps is added for optimization
    public static HashMap<Integer, HashMap<Integer, Double>> pageSrMaps;

    enum TableContextType {
        ROW,
        COL,
        FULL
    }

    public SemanticRelatednessFeatures() {
        pageSrMaps = new HashMap<Integer, HashMap<Integer, Double>>();
    }

    public HashMap<Integer, Double> getSRs(SemanticRelatedness sr, int pageId)
            throws IOException {
        int langId = GlobalConfig.langId;
        HashMap<Integer, Double> result = new HashMap<Integer, Double>();
        if (pageSrMaps.containsKey(pageId)) {
            result = pageSrMaps.get(pageId);
            return result;
        }
        try {
            SemanticRelatedness.Postings p = sr.getSR(pageId, langId);
            if (p == null || p.ids == null) {
            } else {
                for (int i = 0; i < p.ids.length; i++) {
                    result.put(p.ids[i], p.rels[i]);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        pageSrMaps.put(pageId, result);
        return result;
    }

    public double candidatePageSr(Candidate candidate, WtTable table, SemanticRelatedness sr) throws IOException {
        int pageId = table.pgId;
        HashMap<Integer, Double> pageSrMap = this.getSRs(sr, pageId);
        return pageSrMap.get(candidate.wikiTitle.id) == null ?
                -1 :
                pageSrMap.get(candidate.wikiTitle.id);
    }

    public double rowLinksAvgSr(
            Candidate candidate,
            TableMentionContext context,
            SemanticRelatedness sr) throws IOException
    {
        List<String> rowUniqueEntityStrings = context.getUniqueRowEntityStrings();
        return averageSrTableEntities(candidate, rowUniqueEntityStrings, sr, TableContextType.ROW);
    }

    public double colLinksAvgSr(
            Candidate candidate,
            TableMentionContext context,
            SemanticRelatedness sr) throws IOException
    {
        List<String> rowUniqueEntityStrings = context.getUniqueColEntityStrings();
        return averageSrTableEntities(candidate, rowUniqueEntityStrings, sr, TableContextType.COL);
    }

    public double fullLinksAvgSr(
            Candidate candidate,
            TableMentionContext context,
            SemanticRelatedness sr) throws IOException
    {
        List<String> rowUniqueEntityStrings = context.getUniqueFullEntityStrings();
        return averageSrTableEntities(candidate, rowUniqueEntityStrings, sr, TableContextType.FULL);
    }

    public double averageSrTableEntities(
            Candidate candidate,
            List<String> entityStrings,
            SemanticRelatedness sr,
            TableContextType contextType) throws IOException
    {
        Double avgSr = 0.0;
        Double totalLinks = (double) entityStrings.size();
        int candidateTitleId = candidate.wikiTitle.id;
        HashMap<Integer, Double> linkSrMap = getSRs(sr, candidateTitleId);

        if (linkSrMap == null)
            return -1.0;

        for (String idTxt : entityStrings) {
            Integer id = Integer.valueOf(idTxt);
            if (id == -1)
                continue;

            // TODO: Check for full and row.
            if (id == candidateTitleId) {
                if (contextType == TableContextType.COL) {
                    avgSr += 1.0;
                }
            } else {
                if (linkSrMap.containsKey(id)) {
                    avgSr += linkSrMap.get(id);
                }
            }
        }

        if (totalLinks == 0.0)
            return 0.0;

        return avgSr / totalLinks;
    }
}