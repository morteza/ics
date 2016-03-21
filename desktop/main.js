var app = require('app');  // Module to control application life.
var ipc = require('ipc');
var BrowserWindow = require('browser-window');  // Module to create native browser window.
var statusWindow = null;
var mainWindow = null;

// Quit when all windows are closed.
app.on('window-all-closed', function() {
  // On OS X it is common for applications and their menu bar
  // to stay active until the user quits explicitly with Cmd + Q
  //if (process.platform != 'darwin') {
    app.quit();
  //}
});

app.on('ready', function() {
  statusWindow = new BrowserWindow({ width: 0, height: 0, show: false });
  statusWindow.loadUrl('file://' + __dirname + '/status.html');

  // Create the browser window.
  mainWindow = new BrowserWindow({width: 800, height: 600, "node-integration": false});

  mainWindow.$ = mainWindow.jQuery = require('./assets/js/jquery.min.js');

  // and load the main app from the website.
  mainWindow.loadUrl('http://cset-ronins.rhcloud.com/');

  // Open the DevTools.
  //mainWindow.openDevTools();

  // Emitted when the window is closed.
  mainWindow.on('closed', function() {
    mainWindow = null;
    statusWindow = null;
  });
});

ipc.on('status-changed', function(event, status) {
  if (status=='offline')
    mainWindow.loadUrl('file://' + __dirname + '/offline.html');
  console.log(status);
});
