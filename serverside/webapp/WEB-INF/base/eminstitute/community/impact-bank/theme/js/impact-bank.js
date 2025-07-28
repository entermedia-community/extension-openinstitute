$(document).ready(function () {
  // initialization of header
  var header = new HSHeader($("#header")).init();

  // initialization of slick carousel
  $(".js-slick-carousel").each(function () {
    var slickCarousel = $.HSCore.components.HSSlickCarousel.init($(this));
  });

  // initialization of video on background
  $(".js-video-bg").each(function () {
    var videoBg = new HSVideoBg($(this)).init();
  });

  $(".js-text-animation").text("");
  var typed = new Typed(".js-text-animation", {
    strings: ["Innovation", "Solutions", "Ideas", "Success"],
    typeSpeed: 70,
    loop: true,
    backSpeed: 40,
    backDelay: 2000,
  });

  // initialization of text animation (typing)

  // initialization of form validation
  $(".js-validate").each(function () {
    var validation = $.HSCore.components.HSValidation.init($(this));
  });

  // initialization of aos
  AOS.init({
    duration: 650,
    once: true,
  });
});
if (/MSIE \d|Trident.*rv:/.test(navigator.userAgent)) {
  document.write(
    '<script src="$communityhome/assets/vendor/polifills.js"></script>'
  );
}
