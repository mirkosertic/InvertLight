'use strict';

const child_process =  require('child_process');

// Yes, so we bootup the whole electron magic
const electron = require('electron')

//
// Offloading work to the worker processes
//
var theLoadDocumentWorker = child_process.fork("./worker.js");
theLoadDocumentWorker.on("online", () => {
    console.log("Worker is online")
})

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
    theLoadDocumentWorker.send({type: "documentload", filename: arg})
})

// Now continue to perform init

console.log("Master " + process.pid + " is online");

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
