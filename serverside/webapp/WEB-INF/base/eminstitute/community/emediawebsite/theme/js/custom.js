// File for your custom JavaScript

$(document).ready(function () {
	new HSHeader($("#header")).init();
	new HSMegaMenu($(".js-mega-menu"), {
		desktop: {
			position: "left",
		},
	}).init();
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
	$("a.emdialog").on("click", function (event) {
		event.preventDefault();
		emdialog($(this), event);
	});

	emdialog = function (dialog, event) {
		console.log("emdialog");
		if (event) {
			event.stopPropagation();
		}
		var dialog = dialog;

		var width = dialog.data("width");
		var maxwidth = dialog.data("maxwidth");
		var id = dialog.data("dialogid");
		if (!id) {
			id = "modals";
		}

		var modaldialog = $("#" + id);
		if (modaldialog.length == 0) {
			jQuery("body").append(
				'<div class="modal" tabindex="-1" id="' +
					id +
					'" style="display:none" ></div>'
			);
			modaldialog = jQuery("#" + id);
		}
		var options = dialog.data();
		var link = dialog.attr("href");

		var openfrom = window.location.href;

		$.ajax({
			xhrFields: {
				withCredentials: true,
			},
			crossDomain: true,
			url: link,
			data: options,
			success: function (data) {
				modaldialog.html(data);

				if (width) {
					if (width > $(window).width()) {
						width = $(window).width();
					}

					$(".modal-dialog", modaldialog).css("min-width", width + "px");
				}
				if (maxwidth) {
					$(".modal-dialog", modaldialog).css("max-width", maxwidth + "px");
				}

				var modalkeyboard = false;
				var modalbackdrop = true;
				if ($(".modal-backdrop").length) {
					modalbackdrop = false;
				}

				var modalinstance;
				if (modalkeyboard) {
					modalinstance = new bootstrap.Modal(modaldialog[0], {
						// closeExisting: false,
						backdrop: modalbackdrop,
					});
				} else {
					modalinstance = new bootstrap.Modal(modaldialog[0], {
						keyboard: false,
						// closeExisting: false,
						backdrop: modalbackdrop,
					});
				}
				modalinstance.show();

				var firstform = $("form", modaldialog);
				firstform.data("openedfrom", openfrom);
				// fix submit button
				var justok = dialog.data("cancelsubmit");
				if (justok != null) {
					$(".modal-footer #submitbutton", modaldialog).hide();
				} else {
					var id = $("form", modaldialog).attr("id");
					$("#submitbutton", modaldialog).attr("form", id);
				}
				var hidetitle = dialog.data("hideheader");
				if (hidetitle == null) {
					var title = dialog.attr("title");
					if (title == null) {
						title = dialog.text();
					}
					$(".modal-title", modaldialog).text(title);
				}
				var hidefooter = dialog.data("hidefooter");
				if (hidefooter != null) {
					$(".modal-footer", modaldialog).hide();
				}

				if (
					typeof global_updateurl !== "undefined" &&
					global_updateurl == false
				) {
					//globaly disabled updateurl
				} else {
					//backup url
					var currenturl = window.location.href;
					$(modalinstance).data("oldurlbar", currenturl);
					//Update Address Bar
					var updateurl = dialog.data("updateurl");
					if (updateurl) {
						var urlbar = dialog.data("urlbar");
						if (!urlbar) {
							urlbar = link;
						}

						history.pushState($("#application").html(), null, urlbar);
						window.scrollTo(0, 0);
					}
				}

				//on success execute extra JS
				if (dialog.data("onsuccess")) {
					var onsuccess = dialog.data("onsuccess");
					var fnc = window[onsuccess];
					if (fnc && typeof fnc === "function") {
						//make sure it exists and it is a function
						fnc(dialog); //execute it
					}
				}
			},
		});
	}; //emdialog

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
