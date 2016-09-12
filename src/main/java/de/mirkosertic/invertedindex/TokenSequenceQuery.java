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

public class TokenSequenceQuery implements Query {

    public final String[] tokens;

    public TokenSequenceQuery(String[] aTokens) {
        this.tokens = aTokens;
    }

    public Result queryWith(InvertedIndex aInvertedIndex) {

        IntSet theDocumentIDs = null;
        PostingsList theLastPosting = null;
        for (int i=0; i<tokens.length;i++) {
            if (i == 0) {
                theLastPosting = aInvertedIndex.getPostingsListsForToken(tokens[i]);
                if (theLastPosting == null) {
                    return Result.EMPTY;
                }
                theDocumentIDs = theLastPosting.getOccoursInDocuments();
            } else {
               // Search for Documents containing the follow up tokens
                PostingsList theNextPostings = aInvertedIndex.getPostingsListsForToken(tokens[i]);

                IntSet theFollowUpDocuments = theLastPosting.getFollowUpDocumentsByPosting(theNextPostings);
                if (theFollowUpDocuments == null || theFollowUpDocuments.size() == 0) {
                    return Result.EMPTY;
                }

                theDocumentIDs = theDocumentIDs.retainAll(theFollowUpDocuments);
                if (theDocumentIDs.size() == 0) {
                    return Result.EMPTY;
                }

                theLastPosting = theNextPostings;
            }
        }

        return new Result(aInvertedIndex.getDocumentsByIds(theDocumentIDs), theLastPosting);
    }
}