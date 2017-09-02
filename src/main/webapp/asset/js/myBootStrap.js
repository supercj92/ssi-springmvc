$(function () {
    $(".btn.btn-lg.btn-default").click(function () {
        $("#exampleModal").modal("show");
    });
    $(".btn.btn-primary").click(function () {
        console.log($(".modal-body").text());
        $(".modal.fade").modal("hide");
    });
});