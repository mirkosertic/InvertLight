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

import java.util.Set;

public class TokenSequenceQuery implements Query {

    public final String[] tokens;

    public TokenSequenceQuery(String[] aTokens) {
        this.tokens = aTokens;
    }

    public Result queryWith(InvertedIndex aInvertedIndex) {

        Set<Integer> theDocumentIDs = null;
        for (int i=0; i<tokens.length;i++) {
            if (i == 0) {
                TokenInfo theInfo = aInvertedIndex.getTokenInfoFor(tokens[i]);
                if (theInfo == null) {
                    return Result.EMPTY;
                }
                theDocumentIDs = theInfo.getOccoursInDocuments();
            } else {
                TokenInfo thePreviousTokenInfo = aInvertedIndex.getTokenInfoFor(tokens[i-1]);
                Set<Integer> theFollowUpDocuments = thePreviousTokenInfo.getFollowUpDocumentsFor(tokens[i]);
                if (theFollowUpDocuments == null) {
                    return Result.EMPTY;
                }
                theDocumentIDs.removeAll(theFollowUpDocuments);
            }
        }

        return new Result(aInvertedIndex.getDocumentsByID(theDocumentIDs));
    }
}