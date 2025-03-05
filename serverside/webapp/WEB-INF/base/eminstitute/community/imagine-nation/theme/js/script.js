$(function () {
	lQuery("#homegallery").livequery(function () {
		var $grid = $(this).masonry({
			itemSelector: ".gallery-item",
			columnWidth: 240,
			isAnimated: true,
			isFitWidth: true,
			gutter: 10,
		});

		$grid.imagesLoaded().progress(function () {
			$grid.masonry("layout");
		});
	});

	lQuery("a.ajax").livequery("click", function (e) {
		e.stopPropagation();
		e.preventDefault();
		$(this).runAjax();
	});
	lQuery("a.emdialog").livequery("click", function (e) {
		e.stopPropagation();
		e.preventDefault();
		$(this).emDialog();
		$("#__flair").remove();
	});
});
