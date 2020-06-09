package de.uniwue;

import name.fraser.neil.plaintext.diff_match_patch;

import java.util.ArrayList;
import java.util.List;
import org.apache.commons.lang3.StringUtils;

public class SearchEngine {
    Corpus corpus;

    public SearchEngine(Corpus corpus) {
        this.corpus = corpus;
    }

    public void init(List<Utterance> corpus) {

    }

    public List<Utterance> search(String query, Boolean literal) {
        List<Utterance> results= new ArrayList<>();

        for(Utterance utt : corpus.getUtterances()) {
            if(literal) {
                if(StringUtils.containsIgnoreCase(utt.getLiteralStatementAsString(),query)) {
                    results.add(utt);
                };
            } else {
                if(StringUtils.contains(utt.getStatementsAsString(),query)) {
                    results.add(utt);
                };
            }
        }
        return results;
    }

}
