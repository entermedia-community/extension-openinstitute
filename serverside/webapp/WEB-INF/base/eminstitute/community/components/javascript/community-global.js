$(document).ready(function () {
	$("#userOffcanvas").on("show.bs.offcanvas", function () {
		$("body").addClass("offcanvas-open");
		$(".community-layout").addClass("offcanvas-open");
	});
	$("#userOffcanvas").on("hide.bs.offcanvas", function () {
		$("body").removeClass("offcanvas-open");
		$(".community-layout").removeClass("offcanvas-open");
	});

	lQuery(".reloadpage").livequery(function () {
		window.location.reload();
	});

	lQuery(".redirecttopage").livequery(function () {
		var url = $(this).data("redirectok");
		window.location.href = url;
	});
});
