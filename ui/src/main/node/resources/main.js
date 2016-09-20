'use strict';

const electron = require('electron')
const fs = require("fs");
const pdfjs = require("pdfjs-dist")

// Module to control application life.
const app = electron.app
// Module to create native browser window.
const BrowserWindow = electron.BrowserWindow

// Keep a global reference of the window object, if you don't, the window will
// be closed automatically when the JavaScript object is garbage collected.
let mainWindow

function createWindow () {
    // Create the browser window.
    mainWindow = new BrowserWindow({width: 800, height: 600})
    mainWindow.toggleDevTools();

    // and load the index.html of the app.
    mainWindow.loadURL(`file://${__dirname}/index.html`)

    // Emitted when the window is closed.
    mainWindow.on('closed', function () {
        // Dereference the window object, usually you would store windows
        // in an array if your app supports multi windows, this is the time
        // when you should delete the corresponding element.
        mainWindow = null
    })
}

// This method will be called when Electron has finished
// initialization and is ready to create browser windows.
// Some APIs can only be used after this event occurs.
app.on('ready', createWindow)

// Quit when all windows are closed.
app.on('window-all-closed', function () {
    // On OS X it is common for applications and their menu bar
    // to stay active until the user quits explicitly with Cmd + Q
    if (process.platform !== 'darwin') {
        app.quit()
    }
})

app.on('activate', function () {
    // On OS X it's common to re-create a window in the app when the
    // dock icon is clicked and there are no other windows open.
    if (mainWindow === null) {
        createWindow()
    }
})


// Application specific long running tasks are run in the main process
// to prevent the renderer process from blocking
electron.ipcMain.on('load-document', (event, arg) => {
    var theData = fs.readFileSync(arg)
    event.sender.send("document-loaded", {filename: arg, data: theData})
})

electron.ipcMain.on('extract-pdf-text', (event, arg) => {
    var theFileName = arg.filename
    var theBinaryData = arg.data
    var thePageData = []

    var theDocumentPromise = pdfjs.getDocument(theBinaryData).then(function(aDocument) {
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
            event.sender.send("text-extracted", {filename: theFileName, data: theFullText});
        })
    })
})

// In this file you can include the rest of your app's specific main process
// code. You can also put them in separate files and require them here.