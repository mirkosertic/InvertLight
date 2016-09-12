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
            PostingsList thePosting = aInvertedIndex.getPostingsListsForToken(tokens[i]);
            if (thePosting == null) {
                return Result.EMPTY;
            }
            if (i==0) {
                theResult = thePosting.getOccoursInDocuments();
            } else {
                theResult = theResult.retainAll(thePosting.getOccoursInDocuments());
            }
        }
        return new Result(aInvertedIndex.getDocumentsByIds(theResult));
    }
}
