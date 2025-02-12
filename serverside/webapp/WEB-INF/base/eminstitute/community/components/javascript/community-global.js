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

	lQuery(".__top-flair").livequery(function () {
		$(this)
			.find("a.__close")
			.on("click", function (e) {
				e.preventDefault();
				e.stopPropagation();
				$("#__flair").fadeOut(function () {
					$(this).remove();
				});
			});
	});

	lQuery(".autofill").livequery(function () {
		var $this = $(this);
		if ($this.val().trim().length > 0) return;
		var fillFrom = $(this).data("autofillfrom");
		var form = $(this).closest("form");
		if (!form.length) {
			return;
		}
		var input = form.find(`[name="${fillFrom}"]`);
		if (!input.length) {
			return;
		}
		input.on("input", function () {
			$this.val(
				$(this)
					.val()
					.trim()
					.replace(/\s/g, "-")
					.replace(/[^a-zA-Z0-9-]/g, "")
					.toLowerCase()
			);
		});
	});

	function select2formatResult(emdata) {
		return emdata.name;
	}
	function select2Selected(selectedoption) {
		return selectedoption.name || selectedoption.text;
	}

	lQuery("select.listtags").livequery(function () {
		var theinput = $(this);
		var dropdownParent = theinput.data("dropdownparent");
		if (dropdownParent && $("#" + dropdownParent).length) {
			dropdownParent = $("#" + dropdownParent);
		} else {
			dropdownParent = $(this).parent();
		}
		var parent = theinput.closest("#main-media-container");
		if (parent.length) {
			dropdownParent = parent;
		}
		var parent = theinput.parents(".modal-content");
		if (parent.length) {
			dropdownParent = parent;
		}
		var searchtype = theinput.data("searchtype");
		var searchfield = theinput.data("searchfield");
		var catalogid = theinput.data("listcatalogid");
		var sortby = theinput.data("sortby");
		var defaulttext = theinput.data("showdefault");
		if (!defaulttext) {
			defaulttext = "Search";
		}
		var allowClear = $(this).data("allowclear");
		if (allowClear == undefined) {
			allowClear = true;
		}

		var url = theinput.data("url");
		if (!url) {
			url = apphome + "/components/xml/types/autocomplete/tagsearch.txt";
		}

		if ($.fn.select2) {
			theinput.select2({
				tags: true,
				placeholder: defaulttext,
				allowClear: allowClear,
				dropdownParent: dropdownParent,
				selectOnBlur: true,
				delay: 150,
				minimumInputLength: 1,
				ajax: {
					// instead of writing
					// the function to
					// execute the request
					// we use Select2's
					// convenient helper
					url: url,
					xhrFields: {
						withCredentials: true,
					},
					crossDomain: true,
					dataType: "json",
					data: function (params) {
						var search = {
							page_limit: 15,
							page: params.page,
						};
						search["field"] = searchfield;
						search["operation"] = "contains";
						search["searchtype"] = searchtype;
						search[searchfield + ".value"] = params.term; // search
						// term
						search["sortby"] = sortby; // search
						// term
						return search;
					},
					processResults: function (data, params) {
						// parse the
						// results
						// into the
						// format
						// expected
						// by
						// Select2.
						params.page = params.page || 1;
						return {
							results: data.rows,
							pagination: {
								more: false,
								// (params.page * 30) <
								// data.total_count
							},
						};
					},
				},
				escapeMarkup: function (m) {
					return m;
				},
				templateResult: select2formatResult,
				templateSelection: select2Selected,
				tokenSeparators: ["|"],
				separator: "|",
			});
		}
		theinput.on("select2:select", function () {
			if ($(this).parents(".ignore").length == 0) {
				$(this).valid();
			}
		});
		theinput.on("select2:unselect", function () {
			if ($(this).parents(".ignore").length == 0) {
				$(this).valid();
			}
		});
	});

	lQuery(".custom-navbar-toggler").livequery("click", function () {
		if ($("#tempNav").hasClass("d-none")) {
			$("#tempNav").removeClass("d-none");
			var bg = "bg-white";
			if ($(this).data("dark")) {
				bg = "bg-dark";
			}
			$("#tempNav").addClass(`d-flex rounded shadow ${bg}`);
			$("#tempNav").css({
				position: "absolute",
				top: "100%",
				right: 0,
				width: 200,
				padding: "10px 16px",
				zIndex: 9999,
				opacity: 0,
			});
			$("#tempNav").animate({ opacity: 1 }, 250);
		} else {
			$("#tempNav").animate({ opacity: 0 }, 200, function () {
				$("#tempNav").removeClass("d-flex");
				$("#tempNav").addClass("d-none");
			});
		}
	});

	$(window).on("resize", function () {
		if (window.innerWidth >= 768) {
			$("#tempNav").removeClass("d-none bg-white bg-dark rounded shadow");
			$("#tempNav").attr("style", "");
		} else {
			$("#tempNav").addClass("d-none");
		}
	});

	$(document).on("click", function (e) {
		if (window.innerWidth >= 768) {
			return;
		}
		if ($(e.target).closest(".custom-navbar-toggler").length) return;
		if (!$(e.target).is($("#tempNav"))) {
			$("#tempNav").animate({ opacity: 0 }, 200, function () {
				$("#tempNav").removeClass("d-flex");
				$("#tempNav").addClass("d-none");
			});
		}
	});

	lQuery(".pickemoticon").livequery(function () {
		//Load div
		var input = $(this);
		input.click(function () {
			$(".emoticonmenu").hide(); //Hide old ones
		});

		input.hover(function () {
			var isattached = input.data("isattached");
			if (isattached) {
				$(".emoticonmenu").hide(); //Hide old ones
				input.closest(".message-menu").find(".emoticonmenu").show();
			} else {
				var options = input.data();
				$.ajax({
					url: options.showurl,
					async: true,
					data: options,
					success: function (data) {
						$(".emoticonmenu").hide(); //Hide old ones
						input.data("isattached", true);

						input.closest(".message-menu").append(data);
					},
				});
			}
		});

		//On any click hide this:
		//$(".emoticonmenu").hide();
	});

	lQuery(".message-menu-link:not(.pickemoticon)").livequery(function () {
		var input = $(this);
		input.hover(function () {
			$(".emoticonmenu").hide(); //Hide old ones
		});
	});

	lQuery(".emoticonmenu span").livequery("click", function () {
		var menuitem = $(this);

		var aparent = $(menuitem.closest(".message-menu").find(".pickemoticon"));
		//console.log(aparent.data());

		var saveurl = aparent.data("toggleurl");
		//Save
		var options = aparent.data();
		options.reactioncharacter = menuitem.data("hex");
		$.ajax({
			url: saveurl,
			async: true,
			data: options,
			success: function (data) {
				$(".emoticonmenu").hide();
				$("#chatter-message-" + aparent.data("messageid")).html(data);
				//reload message
			},
		});
	});

	lQuery("footer").livequery(function () {
		var offset = 0;
		if ($(".__top-flair").length > 0) {
			offset = 46;
		}
		if ($("#application")[0].scrollHeight - offset < $(window).height()) {
			$(this).css({
				position: "fixed",
				bottom: 0,
				width: "100%",
			});
		}
	});
});
