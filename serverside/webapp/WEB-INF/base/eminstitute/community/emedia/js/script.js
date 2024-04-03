$(document).ready(function () {
  $(window).scroll(function () {
    if ($(window).scrollTop() > 0) {
      $(".navigation").addClass("fixed-menu ");
    } else {
      $(".navigation").removeClass("fixed-menu ");
    }
  });
  $("#test").click(function () {
    $("#collapseTutorial").collapse("toggle");
  });
  $(".prop-pse .nav-link").on("click", function (e) {
    e.preventDefault();
    $(this).tab("show");
    var target = $(this).data("target");
    var propPse = $(this).closest(".prop-pse");
    if (target === "problem") {
      propPse.removeClass("emp");
      propPse.addClass("prb");
    } else {
      propPse.removeClass("prb");
      propPse.addClass("emp");
    }
  });
});

$(window).on("load", function () {
  AOS.init({
    duration: 600,
    once: true,
  });
});
