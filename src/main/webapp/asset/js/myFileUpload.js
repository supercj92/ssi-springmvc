$(function () {
    $('#file-upload').uploadify({
        'swf' : '/asset/js/lib/uploadify/uploadify.swf',
        'uploader' : '/file/upload',
        'buttonText' : '上传文件'
    });
    $('#beginUpload').click(function () {
        $('#file-upload').uploadify('upload');
    });

    $('#ajaxUploadBegin').on('click',uploadFile);

    $('#upload').Huploadify({
        removeTimeout:1000,
        auto:true,
        multi:false,
        fileTypeExts:'*.jpg',
        buttonText: '添加',
        fileSizeLimit:999999,
        showUploadedPercent:true,//是否实时显示上传的百分比，如20%
        uploader:'/file/upload',
        onUploadStart:function(){
            //alert('开始上传');
        },
        onInit:function(){
            //_this.find(".uploadify-queue").hide();
        },
        onUploadComplete:function(file, responseText){
            //uploadComplete(responseText, _this);
        },
        onDelete:function(file){
            console.log('删除的文件：'+file);
            console.log(file);
        }
    });

    function uploadFile(){
        jQuery.ajaxFileUpload({
            url:'/file/upload', //你处理上传文件的服务端
            secureuri:false, //与页面处理代码中file相对应的ID值
            fileElementId:'ajaxFileUpload',
            dataType: 'json', //返回数据类型:text，xml，json，html,scritp,jsonp五种
            success: function (data,status) {
                if(data){
                   console.log(data);
                }else{

                }
            },
            error: function(data,status,e){
                alert(e);
            }
        })
    }
});