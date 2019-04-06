
if(/Android|webOS|iPhone|iPad|iPod|BlackBerry|IEMobile|Opera Mini/i.test(navigator.userAgent)) {
	$("html").css("font-size", window.screen.width/(25.875)+'px');
}
else {
	$("html").css("font-size", '16px');
}


$.ajaxSetup({
	error: function(r, textStatus, errorThrown) {
		var msg = "";
		if (r.responseJSON) {
			msg = "[" + r.responseJSON.errcode + "]" + r.responseJSON.errmsg;
		}
		$('#loading').modal('hide');
		if (r.status == 0) {
			alertWarning("网络不通，请检查网络");
		}
		else {
			alertDanger("error:" + r.status + "|" + this.url + "|" + msg);
		}
	}
});


var LOADING = '\
<div class="modal" id="loading" tabindex="-1" role="dialog" aria-labelledby="myModalLabel" data-backdrop="static" style="z-index:10000">\
	<div class="modal-dialog modal-sm" role="document">\
	<div class="modal-content" style="height:50px;margin-top:180px;text-align:center">\
		<div style="padding-top:13px;"><span class="fa fa-refresh fa-spin"></span>⌛请稍候</div>\
	</div>\
	</div>\
</div>';

var CONFIRM = '\
<div class="modal fade" id="myConfig"><div class="modal-dialog">\
<div class="modal-content">\
	<div class="modal-header"><h5 class="modal-title">确认</h5><button type="button" class="close" data-dismiss="modal">&times;</button></div>\
	<div class="modal-body">\
		<div class="form-group" style="text-align: center;margin-bottom:-5px"><span id="myConfigMsg"></span></div>\
	</div>\
	<div class="modal-footer"><button class="btn btn-primary btn-sm" onclick="myConfigOk()" type="button">确定</button>\
	<button class="btn btn-default btn-sm" data-dismiss="modal" type="button">关闭</button></div>\
</div>\
</div></div>';

$(function() {
	$('body').append(LOADING + CONFIRM + '<div id="myAlert"><strong id="myAlertMsg"></strong><span></span></div>');
})

function alertShow(msg, cls, time) {
	$("#myAlertMsg").text(msg);
	$("#myAlert").attr("class", "alert " + cls);
	$("#myAlert").show();
	if (time == 0) {
		$("#myAlert span").html('<a href="javascript:$(\'#myAlert\').hide();">关闭</a>')
	}
	else {
		$("#myAlert span a").remove();
		setTimeout('$("#myAlert").hide();', time);
	}
}
function alertSuccess(msg) {msg=msg==undefined?"操作成功！":msg;alertShow(msg, "alert-success", 2000)}
function alertInfo(msg) {alertShow(msg, "alert-info", 2500)}
function alertWarning(msg) {alertShow(msg, "alert-warning", 2500)}
function alertDanger(msg) {alertShow(msg, "alert-danger", 0)}

function confirm(msg, func) {
	$("#myConfig").data("func", func);
	$("#myConfigMsg").text(isNaN(msg) ? msg : "确定要删除'" + msg + "'？");
	$("#myConfig").modal('show');
}

function myConfigOk() {
	var func = $("#myConfig").data("func");
	func.call();
	$("#myConfig").modal('hide');
}

function showLoading() {
	$('#loading').modal('show');
	$($(".modal-backdrop")[$(".modal-backdrop").length - 1]).css("z-index", "9999");
	$("#loading .modal-content").hide();
	$($(".modal-backdrop")[$(".modal-backdrop").length - 1]).css("opacity", "0");
	// n毫秒没有加载完页面才出现loading
	$("#loading").data("isShowLoading", true);
	setTimeout(function() {
		if ($("#loading").data("isShowLoading")) {
			$("#loading .modal-content").show();
			$(".modal-backdrop").css("opacity", "0.4");
		}
	}, 300);
}

function hideLoading() {
	$("#loading").data("isShowLoading", false);
	$('#loading').modal('hide');	
}



