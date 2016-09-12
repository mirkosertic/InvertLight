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

public class PostingsList {

    private Map<Integer, IntSet> documentToPositions;
    private final IntSet occoursInDocuments;

    public PostingsList() {
        documentToPositions = new HashMap<>();
        occoursInDocuments = new IntSet();
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
        occoursInDocuments.add(aDocumentID);
    }

    public IntSet getOccoursInDocuments() {
        return occoursInDocuments;
    }

    public IntSet getPositionsForDocument(int aDocumentID) {
        return documentToPositions.get(aDocumentID);
    }
}