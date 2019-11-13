module.exports = function (context) {
    var fs = require('fs'),
        path = require('path');
    var platformRoot = path.join(context.opts.projectRoot, 'platforms/android/app/src/main');
    var manifestFile = path.join(platformRoot, 'AndroidManifest.xml');
    var appClass = 'com.qdc.plugins.baidu.DemoApplication';
    console.log('start changing application name: ' + appClass);
    if (fs.existsSync(manifestFile)) {
        console.log('data');
        fs.readFile(manifestFile, 'utf8', function (err, data) {
            if (err) {
                throw new Error('Unable to find AndroidManifest.xml: ' + err);
            }
            if (data.indexOf(appClass) == -1) {
             
            }
        });
    }
};
