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
	
	
	lQuery(".submitform").livequery("click", function (e) {
		e.preventDefault();

		var theform = $(this).closest("form");

		var clicked = $(this);
		if (clicked.data("updateaction")) {
			var newaction = clicked.attr("href");
			theform.attr("action", newaction);
		}
		console.log("Submit Form " + theform);
		theform.trigger("submit");
		e.stopPropagation();
		return;
	});
	
	
	//Select2 components
	
	function select2formatResult(emdata) {
			/*
			 * var element = $(this.element); var showicon =
			 * element.data("showicon"); if( showicon ) { var type =
			 * element.data("searchtype"); var html = "<img
			 * class='autocompleteicon' src='" + themeprefix + "/images/icons/" +
			 * type + ".png'/>" + emdata.name; return html; } else { return
			 * emdata.name; }
			 */
			return emdata.name;
		}
		function select2Selected(selectedoption) {
			// "#list-" + foreignkeyid
			// var id = container.closest(".select2-container").attr("id");
			// id = "list-" + id.substring(5); //remove sid2_
			// container.closest("form").find("#" + id ).val(emdata.id);
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
	
});
