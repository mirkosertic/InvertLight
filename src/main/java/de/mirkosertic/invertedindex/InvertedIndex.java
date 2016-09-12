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

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

public class InvertedIndex {

    private final List<IndexedDoc> docs;
    private final TokenDictionary tokenDictionary;
    final Map<Integer, PostingsList> postings;

    public InvertedIndex() {
        tokenDictionary = new TokenDictionary();
        docs = new ArrayList<>();
        postings = new HashMap<>();
    }

    public int newDocument(Document aDocument) {
        IndexedDoc theDoc = new IndexedDoc(aDocument.getName(), docs.size());
        docs.add(theDoc);
        return theDoc.getDocumentID();
    }

    public void addTokenToDocument(int aCurrentDocumentId, String aToken, int aPosition) {

        int theTokenID = tokenDictionary.getTokenIDFor(aToken);
        PostingsList theInfo = postings.get(theTokenID);
        if (theInfo == null) {
            theInfo = new PostingsList();
            postings.put(theTokenID, theInfo);
        }

        theInfo.registerWithDocument(aCurrentDocumentId, aPosition);
    }

    public void finishDocument(int aDocumentID) {
    }

    public long getDocumentCount() {
        return docs.size();
    }

    public int getTokenCount() {
        return postings.size();
    }

    public Result query(Query aQuery) {
        return aQuery.queryWith(this);
    }

    public PostingsList getPostingsListsForToken(String aToken) {
        return postings.get(tokenDictionary.getTokenIDFor(aToken));
    }

    public IndexedDoc[] getDocumentsByIds(IntSet aDocumentIDs) {
        IndexedDoc[] theResult = new IndexedDoc[aDocumentIDs.size()];
        aDocumentIDs.forEach((i, t) -> theResult[i] = docs.get(t));
        return theResult;
    }

    public String rebuildContentFor(IndexedDoc aDocument) {

        Map<Integer, Integer> thePositionsToTokens = new TreeMap<>();
        postings.entrySet().forEach(aEntry -> {
            IntSet thePositions = aEntry.getValue().getPositionsForDocument(aDocument.getDocumentID());
            if (thePositions != null) {
                thePositions.forEach((aIndex, aValue) -> {
                   thePositionsToTokens.put(aValue, aEntry.getKey());
                });
            }
        });

        StringBuilder theResult = new StringBuilder();
        thePositionsToTokens.entrySet().forEach(aEntry -> {
            String theToken = tokenDictionary.getTokenForID(aEntry.getValue());
            if (theResult.length() > 0) {
                theResult.append(" ");
            }
            theResult.append(theToken);
        });

        return theResult.toString();
    }
}