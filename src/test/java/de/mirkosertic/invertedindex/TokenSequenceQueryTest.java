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

import static org.junit.Assert.*;

import org.junit.Test;

public class TokenSequenceQueryTest {

    @Test
    public void testQuery() {
        InvertedIndex theIndex = new InvertedIndex();
        UpdateIndexHandler theHandler = new UpdateIndexHandler(theIndex);
        Tokenizer theTokenizer = new Tokenizer(theHandler);

        theTokenizer.process(new Document("doc1", "this is a test"));
        theTokenizer.process(new Document("doc2", "test this as this maybe is good"));
        theTokenizer.process(new Document("doc3", "that is a test"));

        assertEquals(0, theIndex.query(new TokenSequenceQuery(new String[] {"notfound"})).getSize());

        Result theResult1 = theIndex.query(new TokenSequenceQuery(new String[] {"this", "is"}));
        assertEquals(1, theResult1.getSize());
        assertEquals("doc1", theResult1.getDoc(0).getName());

        assertEquals(0, theIndex.query(new TokenSequenceQuery(new String[] {"is", "this"})).getSize());

        Result theResult2 = theIndex.query(new TokenSequenceQuery(new String[] {"th*", "i*"}));
        assertEquals(2, theResult2.getSize());
        assertEquals("doc1", theResult2.getDoc(0).getName());
        assertEquals("doc3", theResult2.getDoc(1).getName());

    }
}