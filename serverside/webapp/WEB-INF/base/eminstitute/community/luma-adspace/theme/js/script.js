AOS.init({
	duration: 600,
	easing: "ease-in-out",
	once: true,
	mirror: false,
});

$(".faq-item h3, .faq-item .faq-toggle").click(function () {
	var isActive = $(this).parent().hasClass("faq-active");
	$(".faq-item").removeClass("faq-active");
	if (!isActive) {
		$(this).parent().toggleClass("faq-active");
	}
});

// var $grid = $(".masonry-grid").masonry({
// 	itemSelector: ".grid-item",
// });

// $grid.imagesLoaded().progress(function (_, { img }) {
// 	$grid.masonry("layout");
// 	$(img).siblings(".img-loader").remove();
// });
$(".portfolio-grid a").on("mouseover", function () {
	$(".portfolio-grid").addClass("pause");
});

$(".portfolio-grid a").on("mouseout", function () {
	$(".portfolio-grid").removeClass("pause");
});
