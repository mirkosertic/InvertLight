'use strict';

const cluster = require("cluster")
const fs = require("fs");
const pdfjs = require("pdfjs-dist")

// Check if we are running as the master nde
if (cluster.isMaster) {

    console.log("Master " + process.pid + " is online");

    // Yes, so we bootup the whole electron magic
    const electron = require('electron')

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

    //
    // Offloading work to the worker processes
    //

    // Worker to extract content from files
    var theLoadDocumentWorker = cluster.fork();

    // Check if we get something back from
    theLoadDocumentWorker.on("message", function(msg) {
        if (msg.type && msg.type == "text-extracted") {
            mainWindow.webContents.send("text-extracted", {filename: msg.filename, data: msg.data});
        }
    });

    // Application specific long running tasks are run in the main process
    // to prevent the renderer process from blocking
    electron.ipcMain.on('load-document', (event, arg) => {
        // We received an event, so we will forward it to the worker
        theLoadDocumentWorker.send({type: "documentload", filename : arg})
})

    // In this file you can include the rest of your app's specific main process
    // code. You can also put them in separate files and require them here.
} else {
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
}