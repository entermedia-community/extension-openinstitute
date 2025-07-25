// initialization of header
var header = new HSHeader($("#header")).init();

// initialization of mega menu
var megaMenu = new HSMegaMenu($(".js-mega-menu"), {
  desktop: {
    position: "left",
  },
}).init();

var unfold = new HSUnfold(".js-hs-unfold-invoker").init();

// initialization of slick carousel
$(".js-slick-carousel").each(function () {
  var slickCarousel = $.HSCore.components.HSSlickCarousel.init($(this));
});

// initialization of video on background
$(".js-video-bg").each(function () {
  var videoBg = new HSVideoBg($(this)).init();
});

// initialization of text animation (typing)
var typed = new Typed(".js-text-animation", {
  strings: ["Innovation", "Ideas", "Solutions", "Success"],
  typeSpeed: 70,
  loop: true,
  backSpeed: 40,
  backDelay: 2000,
});
console.log(typed);

// initialization of form validation
$(".js-validate").each(function () {
  var validation = $.HSCore.components.HSValidation.init($(this));
});

// initialization of aos
AOS.init({
  duration: 650,
  once: true,
});

// initialization of go to
$(".js-go-to").each(function () {
  var goTo = new HSGoTo($(this)).init();
});

if (/MSIE \d|Trident.*rv:/.test(navigator.userAgent)) {
  document.write(
    '<script src="$communityhome/assets/vendor/polifills.js"></script>'
  );
}
