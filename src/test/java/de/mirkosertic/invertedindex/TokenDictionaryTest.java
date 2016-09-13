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

public class TokenDictionaryTest {

    @Test
    public void testRewriteToken() {
        TokenDictionary theDictionary = new TokenDictionary();
        theDictionary.getTokenIDFor("test1abc");
        theDictionary.getTokenIDFor("test2abc");
        theDictionary.getTokenIDFor("test1def");

        String[] theResult1 = theDictionary.rewriteToken("test1abc");
        assertEquals(1, theResult1.length);
        assertEquals("test1abc", theResult1[0]);

        String[] theResult2 = theDictionary.rewriteToken("*abc");
        assertEquals(2, theResult2.length);
        assertEquals("test1abc", theResult2[0]);
        assertEquals("test2abc", theResult2[1]);

        String[] theResult3 = theDictionary.rewriteToken("test?def");
        assertEquals(1, theResult3.length);
        assertEquals("test1def", theResult3[0]);
    }
}