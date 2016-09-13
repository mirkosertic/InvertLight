package de.mirkosertic.invertedindex.ui;

import de.mirkosertic.invertedindex.core.*;

public class Main {

    public static void main(String[] args) {
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
    }
}
