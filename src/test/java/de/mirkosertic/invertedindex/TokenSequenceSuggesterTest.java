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

public class TokenSequenceSuggesterTest {

    @Test
    public void testSuggestion() {
        InvertedIndex theIndex = new InvertedIndex();
        UpdateIndexHandler theIndexHandler = new UpdateIndexHandler(theIndex);
        Tokenizer theTokenizer = new Tokenizer(theIndexHandler);

        theTokenizer.process(new Document("doc1", "this is a test"));
        theTokenizer.process(new Document("doc2", "this is another test"));
        theTokenizer.process(new Document("doc3", "welcome home"));
        theTokenizer.process(new Document("doc4", "nothing here"));
        theTokenizer.process(new Document("doc4", "nothing already"));
        theTokenizer.process(new Document("doc4", "nothing already there"));

        Suggester theSuggester = new TokenSequenceSuggester("nothing");
        SuggestResult theResult = theIndex.suggest(theSuggester);

        assertEquals(2, theResult.getSuggestions().length);
        assertEquals("already", theResult.getSuggestions()[0]);
        assertEquals("here", theResult.getSuggestions()[1]);
    }

    @Test
    public void testWildcardSuggestion() {
        InvertedIndex theIndex = new InvertedIndex();
        UpdateIndexHandler theIndexHandler = new UpdateIndexHandler(theIndex);
        Tokenizer theTokenizer = new Tokenizer(theIndexHandler);

        theTokenizer.process(new Document("doc1", "this is a test"));
        theTokenizer.process(new Document("doc2", "this is another test"));
        theTokenizer.process(new Document("doc3", "welcome home"));
        theTokenizer.process(new Document("doc4", "nothing here"));
        theTokenizer.process(new Document("doc4", "nothing already"));
        theTokenizer.process(new Document("doc4", "nothing already there"));

        Suggester theSuggester = new TokenSequenceSuggester("nothing", "al*");
        SuggestResult theResult = theIndex.suggest(theSuggester);

        assertEquals(1, theResult.getSuggestions().length);
        assertEquals("already", theResult.getSuggestions()[0]);
    }
}