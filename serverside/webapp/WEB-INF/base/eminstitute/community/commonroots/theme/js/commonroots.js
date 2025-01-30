$(document).ready(function () {
	lQuery(".video-card").livequery("click", function () {
		var href = $(this).data("href");
		window.location.href = href;
	});
});
