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

	lQuery("a.toggleAjax").livequery("click", function (e) {
		/**
		 * Runs an ajax call and removes the element from the DOM on ajax success
		 * Optionally checks for a focus parent
		 **/
		e.stopPropagation();
		e.preventDefault();
		var $this = $(this);
		$this.data("noToast", true);
		$this.runAjax(function () {
			var focusParent = $this.closest(`.${$this.data("focusparent")}`);
			if (focusParent.length) {
				focusParent.find("input:visible:first").focus();
			}
			$this.remove();
		});
	});

	lQuery(".autoclick").livequery("click", function (e) {
		var target = $(this).data("clicktarget");
		if (!target) {
			return;
		}
		if (!target.startsWith("#") && !target.startsWith(".")) {
			target = "." + target;
		}
		if (!$(target).is("a")) {
			return;
		}
		window.location.href = $(target).attr("href");
	});

	lQuery(".top-flair").livequery(function () {
		$("body").css("padding-top", 62);
	});
});
