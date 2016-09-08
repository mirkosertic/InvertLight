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

public class InvertedIndex {

    private final List<IndexedDoc> docs;
    final Map<String, TokenInfo> tokens;
    final Map<Integer, TokenInfo> tokensByIndex;

    public InvertedIndex() {
        docs = new ArrayList<>();
        tokens = new HashMap<>();
        tokensByIndex = new HashMap<>();
    }

    public int newDocument(Document aDocument) {
        IndexedDoc theDoc = new IndexedDoc(aDocument.getName(), docs.size());
        docs.add(theDoc);
        return theDoc.getDocumentID();
    }

    public void addTokenToDocument(int aCurrentDocumentId, String aPreviousToken, String aToken) {

        TokenInfo theInfo = tokens.get(aToken);
        if (theInfo == null) {
            theInfo = new TokenInfo(tokens.size(), aToken);
            tokens.put(aToken, theInfo);
            tokensByIndex.put(theInfo.getId(), theInfo);
        }

        theInfo.registerWithDocument(aCurrentDocumentId);

        docs.get(aCurrentDocumentId).addTokenIdToSequence(theInfo.getId());

        if (aPreviousToken != null) {
            TokenInfo thePreviousToken = tokens.get(aPreviousToken);
            thePreviousToken.registerFollowUpToken(aCurrentDocumentId, aToken);
        }
    }

    public void finishDocument(int aDocumentID) {
    }

    public long getDocumentCount() {
        return docs.size();
    }

    public int getTokenCount() {
        return tokens.size();
    }

    public Result query(Query aQuery) {
        return aQuery.queryWith(this);
    }

    public TokenInfo getTokenInfoFor(String aToken) {
        return tokens.get(aToken);
    }

    public List<IndexedDoc> getDocumentsByID(IntSet aDocumentIDs) {
        List<IndexedDoc> theResult = new ArrayList<>();
        aDocumentIDs.forEach(t -> theResult.add(docs.get(t)));
        return theResult;
    }

    public String rebuildContentFor(IndexedDoc aDocument) {

        StringBuilder theResult = new StringBuilder();
        aDocument.handleTokenSequence(t -> {
            if (theResult.length() > 0) {
                theResult.append(" ");
            }
            TokenInfo theInfo = tokensByIndex.get(t);
            theResult.append(theInfo.getToken());
        });

        return theResult.toString();
    }
}