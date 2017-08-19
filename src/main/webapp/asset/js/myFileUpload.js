$(function () {
    $('#file-upload').uploadify({
        'swf' : 'lib/uploadify/uploadify.swf',
        'uploader' : '/upload'
    });
    $('#beginUpload').click(function () {
        $('#file-upload').uploadify('upload');
    });
});