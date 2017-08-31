$(function () {
    $('#file-upload').uploadify({
        'swf' : '/asset/js/lib/uploadify/uploadify.swf',
        'uploader' : '/upload',
        'buttonText' : '上传文件'
    });
    $('#beginUpload').click(function () {
        $('#file-upload').uploadify('upload');
    });
});