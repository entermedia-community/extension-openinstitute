// File for your custom JavaScript

$(document).ready(function () {
	new HSHeader($("#header")).init();
	$(".js-slick-carousel").each(function () {
		$.HSCore.components.HSSlickCarousel.init($(this));
	});
	$(".js-go-to").each(function () {
		new HSGoTo($(this)).init();
	});
	$(".js-validate").each(function () {
		$.HSCore.components.HSValidation.init($(this));
	});
	$(".up-login").click(function () {
		$(this).parent().parent().find(".collapse").toggle(); // toggle collapse
	});

	AOS.init({
		duration: 600,
		once: true,
	});

	$("a.emdialog").on("click", function (e) {
		e.preventDefault();
		e.stopPropagation();
		$(this).emDialog();
	});

	$(document).on("submit", ".antibotform", function (e) {
		e.preventDefault();

		var form = $(this);
		var action = form.attr("action");

		if (action == undefined || action == "/fake") {
			action = form.find("button[type=submit]").data("action");
			if (action == undefined) {
				action = form.find("input[type=submit]").data("action");
			}
			if (action != undefined) {
				form.attr("action", action);
			}
		}

		form.get(0).submit();
	});
});
