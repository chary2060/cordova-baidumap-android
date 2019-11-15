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
            var result = data.replace(/android:name="com.qdc.plugins.baidu.DemoApplication"/g, '')
                .replace(/<activity android:name="com.qdc.plugins.baidu.MapActivity" \/>/g, '')
                .replace(/android:theme="@style\/Theme.AppCompat.Light.DarkActionBar"/g, '');
            fs.writeFile(manifestFile, result, 'utf8', function (err) {
                if (err) throw new Error('Unable to write into AndroidManifest.xml: ' + err);
            })
        });
    }
};
