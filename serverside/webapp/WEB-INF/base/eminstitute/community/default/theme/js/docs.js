$(document).ready(function () {
	function addSections() {
		var section = 1;
		var chapter = 1;
		$("#docs-wrapper h1, #docs-wrapper h2").each(function () {
			var text = $(this).text().replace(/\s/g, "");
			if (!text.length) {
				return;
			}
			if ($(this).is("h1")) {
				if (!text.startsWith(section + ".")) {
					$(this).prepend("<span>" + section + ". </span>");
				}
				$(this).attr("id", "section-" + section);
				section++;
				chapter = 1;
			} else {
				if (!text.startsWith(section - 1 + "." + chapter + ".")) {
					$(this).prepend(
						"<span>" + (section - 1) + "." + chapter + ". </span>"
					);
				}
				$(this).attr("id", "section-" + section + "-" + chapter);
				chapter++;
			}
		});
		section = 1;
		chapter = 1;
		$("#docs-nav > ul > li.nav-item").each(function () {
			if ($(this).hasClass("section-title")) {
				$(this)
					.find("a")
					.attr("href", "#section-" + section);
				section++;
				chapter = 1;
			} else {
				$(this)
					.find("a")
					.attr("href", "#section-" + section + "-" + chapter);
				chapter++;
			}
		});
	}

	addSections();

	var sidebar = $("#docs-sidebar");
	function responsiveSidebar() {
		if (window.innerWidth >= 1200) {
			sidebar.removeClass("sidebar-hidden");
			sidebar.addClass("sidebar-visible");
		} else {
			sidebar.removeClass("sidebar-visible");
			sidebar.addClass("sidebar-hidden");
		}
	}
	responsiveSidebar();

	$(window).on("resize", function () {
		responsiveSidebar();
	});

	$("#docs-sidebar-toggler").on("click", () => {
		if (sidebar.hasClass("sidebar-visible")) {
			sidebar.removeClass("sidebar-visible");
			sidebar.addClass("sidebar-hidden");
		} else {
			sidebar.removeClass("sidebar-hidden");
			sidebar.addClass("sidebar-visible");
		}
	});

	$("#docs-nav a.nav-link.scrollto").on("click", (e) => {
		e.preventDefault();
		var target = $(e.target).attr("href");
		if (!target) return;
		var scrollTop = $(target).offset().top - 72;
		window.scrollTo({ behavior: "smooth", top: scrollTop });

		if (sidebar.hasClass("sidebar-visible") && window.innerWidth < 1200) {
			sidebar.removeClass("sidebar-visible");
			sidebar.addClass("sidebar-hidden");
		}
	});

	new Gumshoe("#docs-nav a", {
		offset: 150,
	});

	new SimpleLightbox("#docs-wrapper img", { sourceAttr: "src" });
});
