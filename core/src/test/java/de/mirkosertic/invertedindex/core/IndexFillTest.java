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

import static org.junit.Assert.assertEquals;
import org.junit.Test;

public class IndexFillTest {

    @Test
    public void testIndexFill() {
        InvertedIndex theIndex = new InvertedIndex();
        UpdateIndexHandler theIndexHandler = new UpdateIndexHandler(theIndex);
        Tokenizer theTokenizer = new Tokenizer(theIndexHandler);

        theTokenizer.process(new Document("doc1", "this is a test"));
        theTokenizer.process(new Document("doc2", "something went wrong"));
        theTokenizer.process(new Document("doc3", "here we go"));
        theTokenizer.process(new Document("doc4", "something is right"));

        assertEquals(4, theIndex.getDocumentCount());
        assertEquals(11, theIndex.getTokenCount());
    }
}