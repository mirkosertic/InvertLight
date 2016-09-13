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
package de.mirkosertic.invertedindex.core;

import static org.mockito.Mockito.any;
import static org.mockito.Mockito.eq;
import static org.mockito.Mockito.inOrder;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.same;
import static org.mockito.Mockito.when;

import org.junit.Test;
import org.mockito.InOrder;

public class UpdateIndexHandlerTest {

    @Test
    public void testFullRun() {
        InvertedIndex theIndex = mock(InvertedIndex.class);
        when(theIndex.newDocument(any(Document.class))).thenReturn(100);

        UpdateIndexHandler theHandler = new UpdateIndexHandler(theIndex);
        Document theDocument = new Document("name", "");
        theHandler.beginDocument(theDocument);
        theHandler.handleToken("token1");
        theHandler.handleToken("token2");
        theHandler.endDocument();

        InOrder theOrder = inOrder(theIndex);
        theOrder.verify(theIndex).newDocument(same(theDocument));
        theOrder.verify(theIndex).addTokenToDocument(eq("token1"));
        theOrder.verify(theIndex).addTokenToDocument(eq("token2"));
        theOrder.verify(theIndex).finishDocument();
    }
}