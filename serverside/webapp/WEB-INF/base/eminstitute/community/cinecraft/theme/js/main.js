function aosInit() {
	AOS.init({
		duration: 600,
		easing: "ease-in-out",
		once: true,
		mirror: false,
	});
}

function initSwiper() {
	document.querySelectorAll(".init-swiper").forEach(function (swiperElement) {
		let config = JSON.parse(
			swiperElement.querySelector(".swiper-config").innerHTML.trim()
		);

		if (swiperElement.classList.contains("swiper-tab")) {
			initSwiperWithCustomPagination(swiperElement, config);
		} else {
			new Swiper(swiperElement, config);
		}
	});
}

const glightbox = GLightbox({
	selector: ".glightbox",
});

$(document).ready(function () {
	aosInit();
	initSwiper();

	document.querySelectorAll(".isotope-layout").forEach(function (isotopeItem) {
		let layout = isotopeItem.getAttribute("data-layout") ?? "masonry";
		let filter = isotopeItem.getAttribute("data-default-filter") ?? "*";
		let sort = isotopeItem.getAttribute("data-sort") ?? "original-order";

		let initIsotope;

		initIsotope = new Isotope(isotopeItem.querySelector(".isotope-container"), {
			itemSelector: ".isotope-item",
			layoutMode: layout,
			filter: filter,
			sortBy: sort,
		});

		isotopeItem
			.querySelectorAll(".isotope-filters li")
			.forEach(function (filters) {
				filters.addEventListener(
					"click",
					function () {
						isotopeItem
							.querySelector(".isotope-filters .filter-active")
							.classList.remove("filter-active");
						this.classList.add("filter-active");
						initIsotope.arrange({
							filter: this.getAttribute("data-filter"),
						});
						if (typeof aosInit === "function") {
							aosInit();
						}
					},
					false
				);
			});
	});
});
