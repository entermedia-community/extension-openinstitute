$(document).ready(function () {
	lQuery(".reloadpage").livequery(function () {
		window.location.reload();
	});
	lQuery(".redirecttopage").livequery(function () {
		var url = $(this).data("redirectok");
		window.location.href = url;
	});
});
