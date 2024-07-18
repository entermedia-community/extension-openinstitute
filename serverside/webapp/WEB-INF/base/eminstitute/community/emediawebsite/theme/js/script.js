$(document).ready(function () {
	
  new HSHeader($("#header")).init();
  new HSMegaMenu($(".js-mega-menu"), {
    desktop: {
      position: "left",
    },
  }).init();
  
  
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
