"use strict";
var sidebarToggler = document.getElementById("docs-sidebar-toggler");
var sidebar = document.getElementById("docs-sidebar");
var sidebarLinks = document.querySelectorAll("#docs-sidebar .scrollto");

window.onload = function () {
	responsiveSidebar();
};

window.onresize = function () {
	responsiveSidebar();
};

function responsiveSidebar() {
	var w = window.innerWidth;
	if (w >= 1200) {
		sidebar.classList.remove("sidebar-hidden");
		sidebar.classList.add("sidebar-visible");
	} else {
		console.log("smaller");
		sidebar.classList.remove("sidebar-visible");
		sidebar.classList.add("sidebar-hidden");
	}
}

sidebarToggler.addEventListener("click", () => {
	if (sidebar.classList.contains("sidebar-visible")) {
		console.log("visible");
		sidebar.classList.remove("sidebar-visible");
		sidebar.classList.add("sidebar-hidden");
	} else {
		console.log("hidden");
		sidebar.classList.remove("sidebar-hidden");
		sidebar.classList.add("sidebar-visible");
	}
});

sidebarLinks.forEach((sidebarLink) => {
	sidebarLink.addEventListener("click", (e) => {
		e.preventDefault();

		var target = sidebarLink.getAttribute("href").replace("#", "");

		document.getElementById(target).scrollIntoView({ behavior: "smooth" });

		if (
			sidebar.classList.contains("sidebar-visible") &&
			window.innerWidth < 1200
		) {
			sidebar.classList.remove("sidebar-visible");
			sidebar.classList.add("sidebar-hidden");
		}
	});
});

var spy = new Gumshoe("#docs-nav a", {
	offset: 69,
});

var lightbox = new SimpleLightbox(".simplelightbox-gallery a", {});
