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

public class TokenQuery implements Query {

    public final String[] tokens;

    public TokenQuery(String[] aTokens) {
        this.tokens = aTokens;
    }

    public Result queryWith(InvertedIndex aInvertedIndex) {
        IntSet theResult = new IntSet();

        for (int i=0;i<tokens.length;i++) {

            if (i==0) {
                theResult = getDocumentsForToken(tokens[i], aInvertedIndex);
                if (theResult.size() == 0) {
                    return Result.EMPTY;
                }

            } else {
                IntSet theNextDocuments = getDocumentsForToken(tokens[i], aInvertedIndex);
                if (theNextDocuments.size() == 0) {
                    return Result.EMPTY;
                }

                theResult = theResult.retainAll(theNextDocuments);
            }
        }
        return new Result(aInvertedIndex.getDocumentsByIds(theResult));
    }

    private IntSet getDocumentsForToken(String aToken, InvertedIndex aIndex) {
        IntSet theResult = new IntSet();
        for (String theToken : aIndex.rewriteToken(aToken)) {
            PostingsList thePosting = aIndex.getPostingsListForToken(theToken);
            if (thePosting != null) {
                theResult = theResult.addAll(thePosting.getOccoursInDocuments());
            }
        }
        return theResult;
    }
}