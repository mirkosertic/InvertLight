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
import java.util.function.Consumer;

public class PostingsList {

    private final int tokenId;
    private final int id;
    private Map<Integer, IntSet> documentToPositions;
    private Map<PostingsList, IntSet> followUpPostingsByDocuments;

    public PostingsList(int aId, int aTokenID) {
        documentToPositions = new HashMap<>();
        id = aId;
        tokenId = aTokenID;
        followUpPostingsByDocuments = new HashMap<>();
    }

    public int getTokenId() {
        return tokenId;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o)
            return true;
        if (o == null || getClass() != o.getClass())
            return false;

        PostingsList that = (PostingsList) o;

        if (id != that.id)
            return false;

        return true;
    }

    @Override
    public int hashCode() {
        return id;
    }

    public void registerWithDocument(int aDocumentID, int aPosition) {
        IntSet thePositions = documentToPositions.get(aDocumentID);
        if (thePositions == null) {
            thePositions = new IntSet();
            thePositions.add(aPosition);
            documentToPositions.put(aDocumentID, thePositions);
        } else {
            thePositions.add(aPosition);
        }
    }

    public IntSet getOccoursInDocuments() {
        return new IntSet(documentToPositions.keySet());
    }

    public IntSet getPositionsForDocument(int aDocumentID) {
        return documentToPositions.get(aDocumentID);
    }

    public void registerFollowUpPostingFor(PostingsList aFollowUpPosting, int aDocumentID) {
        IntSet theFollowUps = followUpPostingsByDocuments.get(aFollowUpPosting);
        if (theFollowUps == null) {
            theFollowUps = new IntSet();
            followUpPostingsByDocuments.put(aFollowUpPosting, theFollowUps);
        }
        theFollowUps.add(aDocumentID);
    }

    public IntSet getFollowUpDocumentsByPosting(PostingsList aPosting) {
        return followUpPostingsByDocuments.get(aPosting);
    }

    public void forEachFollowingPosting(Consumer<Map.Entry<PostingsList, IntSet>> aConsumer) {
        followUpPostingsByDocuments.entrySet().stream().forEach(aConsumer);
    }
}