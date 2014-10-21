var exec = require('cordova/exec')

var PDFViewer = function() {

};

PDFViewer.load = function(url, success, error) {
    exec(success, // success callback function
         error, // error callback function
         'PDFViewer',
         'load', // with this action name
         [url]
    );
};

module.exports = PDFViewer;