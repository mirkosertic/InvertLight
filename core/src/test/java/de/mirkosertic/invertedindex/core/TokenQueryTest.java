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

import static org.junit.Assert.*;

import org.junit.Test;

public class TokenQueryTest {

    @Test
    public void testQuery() {
        InvertedIndex theIndex = new InvertedIndex();
        UpdateIndexHandler theHandler = new UpdateIndexHandler(theIndex);
        Tokenizer theTokenizer = new Tokenizer(theHandler);

        theTokenizer.process(new Document("doc1", "this is a test"));
        theTokenizer.process(new Document("doc2", "test this as this is good"));

        assertEquals(0, theIndex.query(new TokenQuery("notfound")).getSize());

        Result theResult1 = theIndex.query(new TokenQuery("as"));
        assertEquals(1, theResult1.getSize());
        assertEquals("doc2", theResult1.getDoc(0).getName());
        assertEquals("test this as this is good", theIndex.rebuildContentFor(theResult1.getDoc(0)));

        assertEquals(2, theIndex.query(new TokenQuery("this")).getSize());
        assertEquals(2, theIndex.query(new TokenQuery("this", "is")).getSize());
        assertEquals(2, theIndex.query(new TokenQuery("this", "i?")).getSize());
        assertEquals(2, theIndex.query(new TokenQuery("thi*", "i?")).getSize());
        assertEquals(0, theIndex.query(new TokenQuery("thi*", "notfound?")).getSize());

        assertEquals(2, theIndex.query(new TokenQuery("is", "this")).getSize());
    }
}