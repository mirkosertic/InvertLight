'use strict';

const fs = require("fs");
const pdfjs = require("pdfjs-dist")

console.log("Worker " + process.pid + " is online");

function handlePDFData(aData, aFilename) {
    var thePageData = []

    var theDocumentPromise = pdfjs.getDocument(aData).then(function(aDocument) {
        var theNumPages = aDocument.numPages;

        var theLoadPageFunction = function(pageNum) {
            return aDocument.getPage(pageNum).then(function (page) {
                page.getTextContent().then(function (content) {
                    var strings = content.items.map(function (item) {
                        return item.str;
                    });
                    thePageData[pageNum] = strings.join(' ');
                });
            })
        }

        var theLastPromise = theDocumentPromise;

        for (var i=1;i<=theNumPages;i++) {
            theLastPromise = theLastPromise.then(theLoadPageFunction.bind(this, i))
        }

        theLastPromise.then(function() {
            var theFullText = ""
            for (var thePage=0;thePage < thePageData.length;thePage++) {
                theFullText += thePageData[thePage]
            }

            process.send({type: "text-extracted", filename: aFilename, data: theFullText});
        })
    })
}

process.on("message", function(msg) {
    if (msg.type && "documentload" ==  msg.type) {
        var theData = fs.readFileSync(msg.filename)
        handlePDFData(theData, msg.filename)
    }
})