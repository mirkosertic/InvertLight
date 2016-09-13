/*
 * Copyright 2016 Mirko Sertic
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *       http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package de.mirkosertic.invertedindex;

import java.util.HashMap;
import java.util.Map;

public class AbstractSequenceProcessor {

    public static class ProcessorResult {

        protected final IntSet documentIDs;
        protected final Map<String, PostingsList> lastPostings;

        public ProcessorResult(IntSet aDocumentIDs,
                Map<String, PostingsList> aLastPostings) {
            documentIDs = aDocumentIDs;
            lastPostings = aLastPostings;
        }
    }

    protected ProcessorResult process(InvertedIndex aInvertedIndex, String[] aTokens) {
        IntSet theDocumentIDs = null;
        Map<String, PostingsList> theLastPostings = new HashMap<>();

        for (int i=0; i<aTokens.length;i++) {
            if (i == 0) {
                theDocumentIDs = new IntSet();
                for (String theToken : aInvertedIndex.getTokenDictionary().rewriteToken(aTokens[i])) {

                    PostingsList thePosting = aInvertedIndex.getPostingsListForToken(theToken);
                    if (thePosting != null) {
                        theLastPostings.put(theToken, thePosting);
                        theDocumentIDs = theDocumentIDs.addAll(thePosting.getOccoursInDocuments());
                    }
                }
                if (theDocumentIDs.size() == 0) {
                    return null;
                }

            } else {
                Map<String, PostingsList> theNextLastPostings = new HashMap<>();
                IntSet theFollowUpDocuments = new IntSet();

                for (String theToken : aInvertedIndex.getTokenDictionary().rewriteToken(aTokens[i])) {
                    PostingsList theNextPosting = aInvertedIndex.getPostingsListForToken(theToken);
                    for (Map.Entry<String, PostingsList> theEntry : theLastPostings.entrySet()) {
                        IntSet theNextDocument = theEntry.getValue().getFollowUpDocumentsByPosting(theNextPosting);
                        if (theNextDocument != null) {
                            theFollowUpDocuments = theFollowUpDocuments.addAll(theNextDocument);
                            theNextLastPostings.put(theToken, theNextPosting);
                        }
                    }
                }

                if (theFollowUpDocuments.size() == 0) {
                    return null;
                }

                theDocumentIDs = theDocumentIDs.retainAll(theFollowUpDocuments);
                if (theDocumentIDs.size() == 0) {
                    return null;
                }

                theLastPostings = theNextLastPostings;
            }
        }
        return new ProcessorResult(theDocumentIDs, theLastPostings);
    }
}
