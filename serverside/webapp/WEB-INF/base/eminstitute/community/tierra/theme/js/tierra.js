$(document).ready(function () {
  lQuery(".property-container").livequery("click", function (e) {
    if (e.target.nodeName === "SPAN" || e.target.nodeName === "BUTTON") {
      return;
    }
    window.location.href = $(this).data("href");
  });

  var config = {
    autoClose: true,
    singleDate: true,
    hoveringTooltip: false,
    showShortcuts: false,
    singleMonth: true,
    startDate: moment().format("DD MMM YYYY"),
    format: "DD MMM YYYY",
  };
  $("#checkIn").val(moment().format("DD MMM YYYY"));
  $("#checkOut").val(moment().add(1, "days").format("DD MMM YYYY"));

  $("#checkOut")
    .dateRangePicker(config)
    .bind("datepicker-open", function () {
      $("#gp-mask").show();
    })
    .bind("datepicker-close", function () {
      $("#gp-mask").hide();
    });

  $("#checkIn")
    .dateRangePicker(config)
    .bind("datepicker-change", function (event, obj) {
      $("#checkOut").data("dateRangePicker").destroy();

      config.startDate = moment($(this).val(), "DD MMM YYYY")
        .add(1, "days")
        .format("DD MMM YYYY");

      $("#checkOut").dateRangePicker(config);
      if (
        moment($(this).val(), "DD MMM YYYY").isAfter(
          moment($("#checkOut").val(), "DD MMM YYYY")
        )
      ) {
        $("#checkOut").val(config.startDate);
      }
      config.startDate = moment().format("DD MMM YYYY");
    })
    .bind("datepicker-open", function () {
      $("#gp-mask").show();
    })
    .bind("datepicker-close", function () {
      $("#gp-mask").hide();
    });

  $("#guests").click(function () {
    $("#guestsPicker").addClass("open");
    $("#gp-mask").show();
  });

  $("#gp-mask").click(function () {
    $("#guestsPicker").removeClass("open");
    $("#gp-mask").hide();
  });

  var curGuestVal = {
    guest: 1,
    pet: 0,
  };

  function updateGuests(gt, f = 1) {
    if (gt == "adult" || gt == "children") {
      curGuestVal.guest += f;
    } else if (gt == "pet") {
      curGuestVal.pet += f;
    }

    var guestStr =
      curGuestVal.guest + " guest" + (curGuestVal.guest > 1 ? "s" : "");
    if (curGuestVal.pet > 0) {
      guestStr +=
        ", " + curGuestVal.pet + " pet" + (curGuestVal.pet > 1 ? "s" : "");
    }

    $("#guests").val(guestStr);
  }

  $(".g-plus").click(function () {
    var gt = $(this).data("gt");
    var val = parseInt($(this).siblings("strong").text());
    if (val <= 6) {
      val++;
      $(this).siblings("strong").text(val);
      updateGuests(gt);
    }
  });
  $(".g-minus").click(function () {
    var gt = $(this).data("gt");

    var val = parseInt($(this).siblings("strong").text());
    var min = 0;
    if (gt == "adult") {
      min = 1;
    }
    if (val > min) {
      val--;
      $(this).siblings("strong").text(val);
      updateGuests(gt, -1);
    }
  });
  $(".previewImg").click(function () {
    $("#carouselMax").carousel($(this).data("index"));
    $("#triggerPreview").trigger("click");
  });
  $("#shareProperty").click(function () {
    var url = window.location.href;
    var dummy = $("<input>").val(url).appendTo("body").select();
    document.execCommand("copy");
    dummy.remove();
    $(this).addClass("text-success");
    $(this).find("span").text("Link copied");
    $(this).find("i").removeClass("fa-share-alt").addClass("fa-check");
    setTimeout(() => {
      $(this).find("i").removeClass("fa-check").addClass("fa-share-alt");
      $(this).removeClass("text-success");
      $(this).find("span").text("Share");
    }, 2000);
  });

  $(".owner-btn").click(function (e) {
    e.stopPropagation();
    var reviews = $(this).data("reviews");
    var rating = $(this).data("rating");
    var rank = $(this).data("rank");
    var duration = $(this).data("duration");
    var location = $(this).data("location");
    var description = $(this).data("description");
    var name = $(this).data("name");
    var img = $(this).find("img").attr("src");

    var modelEl = $("#ownerPreview");
    modelEl.find(".reviews strong").text(reviews);
    modelEl.find(".rating strong").text(rating);
    modelEl.find(".duration strong").text(duration);
    modelEl.find(".location").text(location);
    modelEl.find(".description").text(description);
    modelEl.find(".name").text(name);
    modelEl.find(".rank").text(rank);
    modelEl.find("img").attr("src", img);

    var modal = new bootstrap.Modal(modelEl);
    modal.show();
  });
  $(".btn-close").click(function () {
    var modal = bootstrap.Modal.getInstance(
      document.querySelector("#ownerPreview")
    );
    modal.hide();
  });
});
