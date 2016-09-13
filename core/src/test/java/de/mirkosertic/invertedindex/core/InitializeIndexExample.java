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

public class InitializeIndexExample {

    public static void main(String[] args) {
        // Initialize the index
        InvertedIndex theIndex = new InvertedIndex();
        UpdateIndexHandler theIndexHandler = new UpdateIndexHandler(theIndex);
        Tokenizer theTokenizer = new Tokenizer(new ToLowercaseTokenHandler(theIndexHandler));

        // Add some content
        theTokenizer.process(new Document("doc1", "this is a test"));

        // Token query
        Result theResult1 = theIndex.query(new TokenQuery("this", "is"));

        // Token Query with wildcards
        Result theResult2 = theIndex.query(new TokenQuery("th*", "i?"));

        // And perform a query for a sequence of token
        Result theResult3 = theIndex.query(new TokenSequenceQuery("this", "is"));

        // Sequence query with wildcards
        Result theResult4 = theIndex.query(new TokenSequenceQuery("th*s", "i?"));

        // Perform a suggestion
        SuggestResult theSuggestResult = theIndex.suggest(new TokenSequenceSuggester("this", "i?"));
    }
}
