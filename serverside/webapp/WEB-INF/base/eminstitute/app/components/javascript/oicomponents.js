jQuery(function () {
	var lastTypeahead;
	var searchPopupOpened = false;
	var searchInputEmpty = $('#searchInputEmpty');
	var searchLoading = $('#searchLoading');
	var searchNoResult = $('#searchNoResult');
	var searchResult = $('#searchResult');
	var searchResultPlaceholder = $('#searchResultPlaceholder');

	function openSearchPopup() {
		$(".search-popup").css("display", "flex")
		$("#searchInput").focus();
		searchPopupOpened = true;
	}

	$("#searchBtn").on("click", openSearchPopup);

	function closeSearchPopup() {
		$(".search-popup").css("display", "none");
		searchPopupOpened = false;
	}

	$("#closeSearch").on("click", closeSearchPopup);

	$(".search-popup").on("click", function (e) {
		if (e.target !== this) return;
		closeSearchPopup();
	});

	$(document).on("keyup", function (e) {
		if (e.key === 'Escape' && searchPopupOpened) {
			closeSearchPopup();
		}
	});

	lQuery("#searchInput").livequery(function () {
		var input = $(this);
		var options = input.data();

		input.on("keyup input", function (e) {
			if(e.key.length > 1 || e.key === ' ') {
				return;
			}
			var query = input.val().trim();
			if (!query || query.length < 2) {
				searchInputEmpty.css("display", "block");
				searchLoading.css("display", "none");
				searchNoResult.css("display", "none");
				searchResult.html('');
				searchResultPlaceholder.css("display", "block");
				return;
			} else {
				searchInputEmpty.css("display", "none");
			}

			var loaderTimeout = setTimeout(function () {
				searchLoading.css("display", "flex");
			}, 250);

			options["description.value"] = query;

			if (lastTypeahead) {
				lastTypeahead.abort();
			}

			lastTypeahead = $.ajax({
				url: options.typeaheadurl,
				async: true,
				data: options,
				timeout: 5000,
				success: function (data) {
					data = data.trim();
					if (data) {
						searchResultPlaceholder.css("display", "none");
						searchResult.html(data);
						searchNoResult.css("display", "none");
					} else {
						searchResult.html('');
						searchNoResult.css("display", "block");
						searchResultPlaceholder.css("display", "block");
					}
				},
				complete: function () {
					clearTimeout(loaderTimeout);
					searchLoading.css("display", "none");
				}
			});
		});
	});
});