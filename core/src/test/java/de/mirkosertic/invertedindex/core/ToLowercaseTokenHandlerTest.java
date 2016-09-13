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

import static org.mockito.Mockito.eq;
import static org.mockito.Mockito.inOrder;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.same;

import org.junit.Test;
import org.mockito.InOrder;

public class ToLowercaseTokenHandlerTest {

    @Test
    public void test() {
        Document theDocument = mock(Document.class);
        TokenHandler theHandler = mock(TokenHandler.class);
        ToLowercaseTokenHandler theTestObject = new ToLowercaseTokenHandler(theHandler);
        theTestObject.beginDocument(theDocument);
        theTestObject.handleToken("Test");
        theTestObject.endDocument();

        InOrder theOrder = inOrder(theHandler);
        theOrder.verify(theHandler).beginDocument(same(theDocument));
        theOrder.verify(theHandler).handleToken(eq("test"));
        theOrder.verify(theHandler).endDocument();
    }
}