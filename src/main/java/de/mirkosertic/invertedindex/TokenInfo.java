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

public class TokenInfo {

    private final int id;
    private final String token;
    private final IntSet occoursInDocuments;
    private final Map<String, IntSet> followUpTokensWithDocuments;

    public TokenInfo(int aID, String aToken) {
        id = aID;
        token = aToken;
        occoursInDocuments = new IntSet();
        followUpTokensWithDocuments = new HashMap<>();
    }

    public int getId() {
        return id;
    }

    public String getToken() {
        return token;
    }

    public void registerWithDocument(int aDocumentID) {
        occoursInDocuments.add(aDocumentID);
    }

    public IntSet getOccoursInDocuments() {
        return occoursInDocuments;
    }

    public void registerFollowUpToken(int aDocumentID, String aToken) {
        IntSet theDocIDs = followUpTokensWithDocuments.get(aToken);
        if (theDocIDs == null) {
            theDocIDs = new IntSet();
            theDocIDs.add(aDocumentID);
            followUpTokensWithDocuments.put(aToken, theDocIDs);
        } else {
            theDocIDs.add(aDocumentID);
        }
    }

    public IntSet getFollowUpDocumentsFor(String aToken) {
        return followUpTokensWithDocuments.get(aToken);
    }
}