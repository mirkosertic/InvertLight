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
        TokenInfo theLastTokenInfo = null;
        for (int i=0; i<tokens.length;i++) {
            if (i == 0) {
                theLastTokenInfo = aInvertedIndex.getTokenInfoFor(tokens[i]);
                if (theLastTokenInfo == null) {
                    return Result.EMPTY;
                }
                theDocumentIDs = theLastTokenInfo.getOccoursInDocuments();
            } else {
                IntSet theFollowUpDocuments = theLastTokenInfo.getFollowUpDocumentsFor(tokens[i]);
                if (theFollowUpDocuments == null) {
                    return Result.EMPTY;
                }
                theDocumentIDs = theDocumentIDs.retainAll(theFollowUpDocuments);
                theLastTokenInfo = aInvertedIndex.getTokenInfoFor(tokens[i]);
            }
        }

        return new Result(aInvertedIndex.getDocumentsByID(theDocumentIDs));
    }
}