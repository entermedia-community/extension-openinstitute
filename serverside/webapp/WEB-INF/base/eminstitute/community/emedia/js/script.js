$(document).ready(function () {
  $(window).scroll(function () {
    if ($(window).scrollTop() > 0) {
      $(".navigation").addClass("fixed-menu ");
    } else {
      $(".navigation").removeClass("fixed-menu ");
    }
  });

  $(".brand-logo-slider").slick({
    dots: false,
    infinite: true,
    arrows: false,
    autoplay: true,
    fade: false,
    speed: 300,
    slidesToShow: 6,
    slidesToScroll: 1,
    responsive: [
      {
        breakpoint: 991,

        settings: {
          slidesToShow: 4,
          slidesToScroll: 1,
          dots: false,
          infinite: true,
          arrows: false,
        },
      },
      {
        breakpoint: 769,
        settings: {
          dots: false,
          infinite: true,
          arrows: false,
          slidesToShow: 2,
          slidesToScroll: 1,
        },
      },
      {
        breakpoint: 576,
        settings: {
          dots: false,
          infinite: true,
          arrows: false,
          slidesToShow: 2,
          slidesToScroll: 1,
        },
      },
    ],
  });

  $("#datetimepicker").datetimepicker({
    inline: true,
    minDate: new Date(),
    maxDate: new Date(new Date().setDate(new Date().getDate() + 30)),
  });

  $("input[name=type]").on("change", function () {
    var val = $(this).val();
    if (val == "video") {
      $(".conf-link").show();
    } else {
      $(".conf-link").hide();
    }
  });
});

$(window).on("load", function () {
  AOS.init({
    duration: 600,
    once: true,
  });
});
