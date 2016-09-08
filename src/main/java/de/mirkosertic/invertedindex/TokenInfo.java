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
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

public class TokenInfo {

    private final Set<Integer> occoursInDocuments;
    private final Map<String, Set<Integer>> followUpTokensWithDocuments;

    public TokenInfo() {
        occoursInDocuments = new HashSet<>();
        followUpTokensWithDocuments = new HashMap<>();
    }

    public void registerWithDocument(int aDocumentID) {
        occoursInDocuments.add(aDocumentID);
    }

    public Set<Integer> getOccoursInDocuments() {
        return occoursInDocuments;
    }

    public void registerFollowUpToken(int aDocumentID, String aToken) {
        Set<Integer> theDocIDs = followUpTokensWithDocuments.get(aToken);
        if (theDocIDs == null) {
            theDocIDs = new HashSet<Integer>();
            theDocIDs.add(aDocumentID);
            followUpTokensWithDocuments.put(aToken, theDocIDs);
        } else {
            theDocIDs.add(aDocumentID);
        }
    }

    public Set<Integer> getFollowUpDocumentsFor(String aToken) {
        return followUpTokensWithDocuments.get(aToken);
    }
}