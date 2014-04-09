var PDFViewer = {
    load: function(url, success, error) {
        cordova.exec(
            success, // success callback function
            error, // error callback function
            'nz.co.springload.pdfviewer', // mapped to our native Java class called "CalendarPlugin"
            'load', // with this action name
            [url]
        );
     }
}