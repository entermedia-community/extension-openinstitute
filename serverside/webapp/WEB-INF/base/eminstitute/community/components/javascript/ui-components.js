//EM Media Finder -- Community version
var lwt;
var trackKeydown = false;
var exitWarning = false;
var app;
var siteroot;
var apphome;

function showLoader() {
  clearTimeout(lwt);
  lwt = setTimeout(function () {
    $("#loading-window").addClass("d-flex");
  }, 200); //don't show loader if the page loads within 200ms
}

findclosest = function (link, inid) {
  var result = link.closest(inid);
  if (result.length == 0) {
    result = link.children(inid);
    if (result.length == 0) {
      result = $(inid);
    }
  }
  return result.first();
};

function hideLoader() {
  clearTimeout(lwt);
  $("#loading-window").removeClass("d-flex");
}

formatHitCountResult = function (inRow) {
  return inRow[1];
};

function getRandomColor() {
  var letters = "0123456789ABCDEF".split("");
  var color = "#";
  for (var i = 0; i < 6; i++) {
    color += letters[Math.floor(Math.random() * 16)];
  }
  return color;
}

function getScriptIfNotLoaded(scriptLocationAndName) {
  var len = $('script[src*="' + scriptLocationAndName + '"]').length;

  //script already loaded!
  if (len > 0) return;

  var head = document.getElementsByTagName("head")[0];
  var script = document.createElement("script");
  script.type = "text/javascript";
  script.src = scriptLocationAndName;
  head.appendChild(script);
}

finddata = function (inlink, inname) {
  var item = $(inlink);
  var value = item.data(inname);
  if (!value) {
    value = inlink.attr(inname);
  }
  var parent = inlink;
  //debugger;
  while (!value) {
    parent = parent.parent().closest(".domdatacontext");
    if (parent.length == 0) {
      break;
    }
    value = parent.data(inname);
  }
  return value;
};

findalldata = function (inlink) {
  var item = $(inlink);
  var options = item.data();
  var parent = item;
  do {
    parent = parent.parent().closest(".domdatacontext");
    var moreoptions = parent.data();
    $.each(moreoptions, function (key, value) {
      if (!options[key]) {
        options[key] = value;
      }
    });
  } while (parent.length > 0);

  return options;
};

lQuery(".reloadpage").livequery(function () {
  window.location.reload();
});
lQuery(".redirecttopage").livequery(function () {
  var url = $(this).data("redirectok");
  window.location.href = url;
});

uiload = function () {
  app = jQuery("#application");
  siteroot = app.data("siteroot");
  apphome = app.data("apphome");
  var themeprefix = app.data("themeprefix");
  if (siteroot !== undefined) {
    //legacy siteroot
    apphome = siteroot + apphome;
    themeprefix = siteroot + themeprefix;
  }

  var mediadb = $("#application").data("mediadbappid");

  if ($.fn.tablesorter) {
    $("#tablesorter").tablesorter();
  }
  if ($.fn.selectmenu) {
    lQuery(".uidropdown select").livequery(function () {
      $(this).selectmenu({
        style: "dropdown",
      });
    });
  }

  lQuery(".setpagetitleX").livequery(function () {
    var setpagetitle = $(this).data("setpagetitle");
    if (setpagetitle) {
      document.title = setpagetitle;
    }
  });

  var browserlanguage = app.data("browserlanguage");
  if (browserlanguage == undefined || browserlanguage == "") {
    browserlanguage = "en";
  }

  lQuery("input.datepicker").livequery("mousedown", function () {
    var trigger = $(this).parent().find(".ui-datepicker-trigger");
    trigger.trigger("click");
  });
  lQuery("input.datepicker").livequery(function () {
    if ($.datepicker) {
      var dpicker = $(this);
      $.datepicker.setDefaults($.datepicker.regional[browserlanguage]);
      $.datepicker.setDefaults(
        $.extend({
          showOn: "button",
          buttonImage:
            "data:image/svg+xml,%3Csvg xmlns='http://www.w3.org/2000/svg' width='20' height='20' fill='%234d5d80' class='bi bi-calendar-plus' viewBox='0 0 16 16'%3E%3Cpath d='M8 7a.5.5 0 0 1 .5.5V9H10a.5.5 0 0 1 0 1H8.5v1.5a.5.5 0 0 1-1 0V10H6a.5.5 0 0 1 0-1h1.5V7.5A.5.5 0 0 1 8 7'/%3E%3Cpath d='M3.5 0a.5.5 0 0 1 .5.5V1h8V.5a.5.5 0 0 1 1 0V1h1a2 2 0 0 1 2 2v11a2 2 0 0 1-2 2H2a2 2 0 0 1-2-2V3a2 2 0 0 1 2-2h1V.5a.5.5 0 0 1 .5-.5M1 4v10a1 1 0 0 0 1 1h12a1 1 0 0 0 1-1V4z'/%3E%3C/svg%3E",
          buttonImageOnly: true,
          changeMonth: true,
          changeYear: true,
          yearRange: "1900:2050",
        })
      ); // Move this to the Layouts?

      var targetid = dpicker.data("targetid");
      dpicker.datepicker({
        altField: "#" + targetid,
        altFormat: "yy-mm-dd",
        yearRange: "1900:2050",
        beforeShow: function (input, inst) {
          setTimeout(function () {
            $("#ui-datepicker-div").css("z-index", 100100);
            $("#application").append($("#ui-datepicker-div"));
            // var quickSelect = $("#operationentitydatefindercatalog");
            // quickSelect.css("display", "block");
            // $("#ui-datepicker-div").append(quickSelect);
            //Fix Position if in bootstrap modal
            var modal = $("#modals");
            if (modal.length) {
              var modaltop = $("#modals").offset().top;
              if (modaltop) {
                var dpickertop = dpicker.offset().top;
                dpickertop = dpickertop - modaltop;
                var dpHeight = inst.dpDiv.outerHeight();
                var inputHeight = inst.input ? inst.input.outerHeight() : 0;
                var viewHeight = document.documentElement.clientHeight;
                if (dpickertop + dpHeight + inputHeight > viewHeight) {
                  dpickertop = dpickertop - dpHeight;
                }
                inst.dpDiv.css({
                  top: dpickertop + inputHeight,
                });
              }
            }
          }, 0);
        },
      });

      var current = $("#" + targetid).val();
      if (current != undefined) {
        // alert(current);
        var date;
        if (current.indexOf("-") > 0) {
          // this is the standard
          current = current.substring(0, 10);
          // 2012-09-17 09:32:28 -0400
          date = $.datepicker.parseDate("yy-mm-dd", current);
        } else {
          date = $.datepicker.parseDate("mm/dd/yy", current); // legacy
        }
        $(this).datepicker("setDate", date);
      }
      $(this).blur(function () {
        var val = $(this).val();
        if (val == "") {
          $("#" + targetid).val("");
        }
      });
    } //datepicker
  });

  if ($.fn.minicolors) {
    $(".color-picker").minicolors({
      defaultValue: "",
      letterCase: "uppercase",
    });
  }

  lQuery(".focusme").livequery(function () {
    $(this).focus();
  });

  lQuery("#module-dropdown").livequery("click", function (e) {
    e.stopPropagation();
    if ($(this).hasClass("active")) {
      $(this).removeClass("active");
      $("#module-list").hide();
    } else {
      $(this).addClass("active");
      $("#module-list").show();
    }
  });

  lQuery("select.select2").livequery(function () {
    var theinput = $(this);
    var dropdownParent = theinput.data("dropdownparent");
    if (dropdownParent && $("#" + dropdownParent).length) {
      dropdownParent = $("#" + dropdownParent);
    } else {
      dropdownParent = $(this).parent();
    }
    var parent = theinput.closest("#main-media-container");
    if (parent.length) {
      dropdownParent = parent;
    }
    var parent = theinput.parents(".modal-content");
    if (parent.length) {
      dropdownParent = parent;
    }
    var allowClear = $(this).data("allowclear");
    if (allowClear == undefined) {
      allowClear = true;
    }
    var placeholder = $(this).data("placeholder");
    if (placeholder == undefined) {
      placeholder = "";
    }
    if ($.fn.select2) {
      theinput.select2({
        allowClear: allowClear,
        placeholder: placeholder,
        dropdownParent: dropdownParent,
      });
    }

    theinput.on("select2:open", function (e) {
      var selectId = $(this).attr("id");
      if (selectId) {
        $(
          ".select2-search__field[aria-controls='select2-" +
            selectId +
            "-results']"
        ).each(function (key, value) {
          value.focus();
        });
      } else {
        document
          .querySelector(".select2-container--open .select2-search__field")
          .focus();
      }
    });
  });
  /*
	$(".select2simple").select2({
		 minimumResultsForSearch: Infinity
	});
	*/
  lQuery("select.listdropdown").livequery(function () {
    var theinput = $(this);
    var dropdownParent = theinput.data("dropdownparent");
    if (dropdownParent && $("#" + dropdownParent).length) {
      dropdownParent = $("#" + dropdownParent);
    } else {
      dropdownParent = $(this).parent();
    }
    var parent = theinput.closest("#main-media-container");
    if (parent.length) {
      dropdownParent = parent;
    }
    var parent = theinput.parents(".modal-content");
    if (parent.length) {
      dropdownParent = parent;
    }

    //console.log(theinput.attr("id")+"using: "+dropdownParent.attr("id"));
    var placeholder = theinput.data("placeholder");
    if (!placeholder) {
      placeholder = "";
    }
    var allowClear = theinput.data("allowclear");

    if (allowClear == undefined) {
      allowClear = true;
    }
    if ($.fn.select2) {
      theinput = theinput.select2({
        placeholder: placeholder,
        allowClear: allowClear,
        minimumInputLength: 0,
        dropdownParent: dropdownParent,
      });
    }
    theinput.on("change", function (e) {
      //console.log("XX changed")
      if (theinput.hasClass("uifilterpicker")) {
        var selectedids = theinput.val();
        if (selectedids) {
          //console.log("XX has class" + selectedids);
          var parent = theinput.closest(".filter-box-options");
          //console.log(parent.find(".filtercheck"));
          parent.find(".filtercheck").each(function () {
            var filter = $(this);
            filter.prop("checked", false); //remove?
          });
          for (i = 0; i < selectedids.length; i++) {
            //$entry.getId()${fieldname}_val
            var selectedid = selectedids[i];
            var fieldname = theinput.data("fieldname");
            var targethidden = $("#" + selectedid + fieldname + "_val");
            targethidden.prop("checked", true);
          }
        }
      }
    });

    theinput.on("select2:open", function (e) {
      var selectId = $(this).attr("id");
      if (selectId) {
        $(
          ".select2-search__field[aria-controls='select2-" +
            selectId +
            "-results']"
        ).each(function (key, value) {
          value.focus();
        });
      } else {
        document
          .querySelector(".select2-container--open .select2-search__field")
          .focus();
      }
    });
  });

  lQuery(".select2editable").livequery(function () {
    var input = $(this);
    var arr = new Array(); // [{id: 0, text: 'story'},{id: 1, text:
    // 'bug'},{id: 2, text: 'task'}]

    var options = $(this).find("option");

    if (!options.length) {
      //			return;
    }

    options.each(function () {
      var id = $(this).data("value");
      var text = $(this).text();
      //console.log(id + " " + text);
      arr.push({
        id: id,
        text: text,
      });
    });

    // Be aware: calling select2 forces livequery to filter again
    if ($.fn.select2) {
      input
        .select2({
          createSearchChoice: function (term, data) {
            if (
              $(data).filter(function () {
                return this.text.localeCompare(term) === 0;
              }).length === 0
            ) {
              //console.log("picking" + term);
              return {
                id: term,
                text: term,
              };
            }
          },
          multiple: false,
          tags: true,
        })
        .on("select2:select", function (e) {
          var thevalue = $(this).val();
          if (thevalue != "" && $(this).hasClass("autosubmited")) {
            var theform = $(this).parent("form");
            if (theform.hasClass("autosubmitform")) {
              theform.trigger("submit");
            }
          }
        });
    }

    input.on("select2:open", function (e) {
      var selectId = $(this).attr("id");
      if (selectId) {
        $(
          ".select2-search__field[aria-controls='select2-" +
            selectId +
            "-results']"
        ).each(function (key, value) {
          value.focus();
        });
      } else {
        document
          .querySelector(".select2-container--open .select2-search__field")
          .focus();
      }
    });
  });

  lQuery("select.ajax").livequery("change", function (e) {
    var inlink = $(this);
    var nextpage = inlink.data("href");
    nextpage = nextpage + inlink.val();
    var targetDiv = inlink.data("targetdiv");
    if (!targetDiv) {
      targetDiv = inlink.attr("targetdiv");
    }

    var options = inlink.data();
    options[inlink.attr("name")] = inlink.val();
    $.get(nextpage, options, function (data) {
      if (targetDiv) {
        var cell = $("#" + targetDiv);
        cell.html(data);
      } else {
        if (!targetDiv) {
          targetDiv = inlink.data("targetdivinner");
          var cell = $("#" + targetDiv);
          cell.replaceWith(data);
        }
      }
      $(window).trigger("resize");
    });
  });

  lQuery("a.toggle-visible").livequery("click", function (e) {
    e.preventDefault();
    var div = $(this).data("targetdiv");
    var target = $("#" + div);
    if (target.is(":hidden")) {
      var hidelable = $(this).data("hidelabel");
      $(this).find("span").text(hidelable);
      target.show();
    } else {
      var showlabel = $(this).data("showlabel");
      $(this).find("span").text(showlabel);
      target.hide();
    }
  });

  // deprecated, use data-confirm
  lQuery(".confirm").livequery("click", function (e) {
    var inText = $(this).attr("confirm");
    if (!inText) {
      inText = $(this).data("confirm");
    }
    if (confirm(inText)) {
      return;
    } else {
      e.preventDefault();
      e.stopImmediatePropagation();
    }
  });

  lQuery(".uibutton").livequery(function () {
    $(this).button();
  });
  lQuery(".fader").livequery(function () {
    $(this).fadeOut(2000, "linear");
  });

  lQuery(".uipanel").livequery(function () {
    $(this).addClass("ui-widget");
    var header = $(this).attr("header");
    if (header != undefined) {
      // http://dev.$.it/ticket/9134
      $(this).wrapInner('<div class="ui-widget-content"/>');
      $(this).prepend('<div class="ui-widget-header">' + header + "</div>");
    }
  });

  lQuery(".ajaxchange select").livequery(function () {
    var select = $(this);
    var div = select.parent(".ajaxchange");
    var url = div.attr("targetpath");
    var divid = div.attr("targetdiv");

    select.change(function () {
      var url2 = url + $(this).val();
      $("#" + divid).load(url2);
    });
  });

  lQuery("form.autosubmit").livequery(function () {
    var form = $(this);

    $("select", form).change(function () {
      $(form).trigger("submit");
    });
    /*
    $("input", form).on("focusout", function (event) {
      $(form).trigger("submit");
    });
    */
    $("input", form).on("keyup", function (e) {
      $(form).trigger("submit");
    });
    $(
      'input[type="file"],input[name="date.after"],input[type="checkbox"]',
      form
    ).on("change", function () {
      $(form).trigger("submit");
    });
  });

  lQuery("select.ajaxautosubmitselect").livequery(function () {
    var select = $(this);
    select.change(function () {
      var targetdiv = select.data("targetdiv");
      var link = select.data("url");
      var param = select.data("parametername");

      var url = link + "?" + param + "=" + select.val();

      var options = select.data();
      $("#" + targetdiv).load(url, options, function () {});
    });
  });

  lQuery("form.ajaxautosubmit").livequery(function () {
    var theform = $(this);
    theform.find("select").change(function () {
      theform.submit();
    });
  });

  lQuery(".submitform").livequery("click", function (e) {
    e.preventDefault();

    var theform = $(this).closest("form");

    var clicked = $(this);
    if (clicked.data("updateaction")) {
      var newaction = clicked.attr("href");
      theform.attr("action", newaction);
    }
    console.log("Submit Form " + theform);
    theform.trigger("submit");
    e.stopPropagation();
    return;
  });

  lQuery(".submitform-oehtml, .dialogsubmitbtn").livequery(
    "click",
    function (e) {
      var theform = $(this).closest("form");
      if (theform.length == 0) {
        //dialog form?
        var dialogform = $(this).attr("form");
        theform = $("#" + dialogform);
      }
      if (theform.length) {
        theform.data("readytosubmit", "true");
        theform.find(".oehtmlinput").trigger("blur");
        theform.trigger("submit");
      }
      e.preventDefault();
    }
  );

  lQuery(".selectsubmitform").livequery("change", function (e) {
    e.preventDefault();
    var theform = $(this).closest("form");
    theform.trigger("submit");
  });

  lQuery(".quicksearch-toggler").livequery("click", function () {
    var navbar = $(this).data("target");
    $("#" + navbar).toggle();
  });

  lQuery(".quicksearchlinks").livequery("click", function () {
    closetypeaheadmodal();
    /*var modalparent = $(this).closest('.typeaheadmodal');
		modalparent.toggle();*/
  });

  lQuery(".clearallfilters").livequery("click", function () {
    closetypeaheadmodal();
  });

  function closetypeaheadmodal() {
    var modal = $(".typeaheadmodal");
    if (modal.length > 0) {
      modal.hide();
    }
    $("#searchinput").val("");
  }

  setPageTitle = function (element) {
    if (element === undefined) {
      element = $("#applicationcontent");
    }
    if (element === undefined || $(element).data("setpagetitle") == null) {
      element = $("#application");
    }

    var setpagetitle = $(element).data("setpagetitle");
    var titlepostfix = $("#application").data("titlepostfix");
    var title = "";
    if (setpagetitle) {
      title = setpagetitle;
    }
    if (titlepostfix) {
      title = title + " - " + titlepostfix;
    }
    document.title = title;
  };

  lQuery(".entitydialogback").livequery("click", function (event) {
    event.preventDefault();
    var parentid = $(this).data("parentid");
    var parent = $("#" + parentid);
    if (parent.length) {
      var grandparent = parent.parent().closest(".entitydialog");
      autoreload(parent);
      tabbackbutton(grandparent);
    }
  });

  adjustzindex = function (element) {
    var zIndex = 100000;
    setTimeout(function () {
      var adjust = 0;

      if (element.hasClass("modalmediaviewer")) {
        $(".modal:visible").css("z-index", zIndex);
        $(".modal:visible").off();
        $(".modal:visible").addClass("behind");
        $(".modal:visible").hide();
      } else {
        $(".modalmediaviewer").css("z-index", zIndex);
        $(".modal:visible").css("z-index", zIndex - 1); //reset others?
        $(".modal-backdrop")
          .not(".modal-stack")
          .css("z-index", zIndex - 1)
          .addClass("modal-stack");
      }
      adjust = 1 + 1 * $(".modal:visible").length;
      element.css("z-index", zIndex + adjust);
      $(".onfront").removeClass("onfront");
      element.show();
      element.addClass("onfront");

      $(window).trigger("resize");
    }, 0);
  };

  lQuery("a.triggerjs").livequery("click", function (event) {
    event.preventDefault();
    var link = $(this);
    var id = link.data("triggerid");
    var action = link.data("triggeraction");
    if (id) {
      $("#" + id).trigger(action); //callback?
    }
    return;
  });

  lQuery("a.entity-tab-label").livequery("click", function (event) {
    event.preventDefault();
    event.stopPropagation();
    entitytabclick($(this));
    return;
  });

  function entitytabclick(link) {
    if (link.data("ismultiedit")) {
      //return;
    }
    var tabid = link.data("tabid");
    var currenttab = link.data("currenttab");
    $(".entity-tab").removeClass("current-entity");
    link.closest(".entity-tab").addClass("current-entity");
    var topmoduleid = link.data("topmoduleid");
    var entityid = link.data("entityid");

    var options = link.data();
    options["oemaxlevel"] = 1;
    var url = link.attr("href");

    $.ajax({
      xhrFields: {
        withCredentials: true,
      },
      crossDomain: true,
      url: url,
      data: options,
      success: function (data) {
        $(".entity-tab-content").replaceWith(data);

        $(".entity-tab-content img")
          .last()
          .on("load", function () {
            $(window).trigger("resize");
          });
      },
    });

    var entitydialog = link.closest(".entitydialog");
    var entityshare = entitydialog.find(".entityshare");
    if (entityshare.length) {
      entityshare.data("entitytabopen", currenttab);
    }

    saveProfileProperty(
      topmoduleid + "_entitytabopen",
      link.data("currenttab"),
      function () {}
    );

    var url =
      apphome +
      "/views/modules/" +
      topmoduleid +
      "/index.html?entity=" +
      entityid +
      "&entitytabopen=" +
      currenttab;
    history.pushState($("#application").html(), null, url);
  }

  lQuery("a.entity-tab-close").livequery("click", function (event) {
    event.preventDefault();
    if ($(".entity-tab").length > 1) {
      //only remove if more than one tab
      var tabcontainer = $(this).closest(".entity-tab");
      var open = $(".current-entity");
      if (open.data("entityid") == $(this).data("entityid")) {
        open = tabcontainer.prev();
      }
      if (open.length == 0) {
        open = tabcontainer.next();
      }
      var entityid = open.find(".entity-tab-label").data("entityid");
      $(".entity-tab").removeClass("current-entity");
      open.addClass("current-entity");
      tabcontainer.remove();
      $(
        'div[data-id="' + $(this).data("entityid") + '"].entity-tab-content'
      ).remove();
      $(".entity-tab-content").hide();
      $('div[data-id="' + entityid + '"].entity-tab-content').show();
    } else {
      //close dialog
      closeemdialog($(this).closest(".modal"));
    }
  });

  $(window).on("keydown", function (e) {
    if (trackKeydown) {
      exitWarning = true;
    } else {
      exitWarning = false;
    }
  });

  function confirmModalClose(modal) {
    var checkForm = modal.find("form.checkCloseDialog");

    if (!checkForm) {
      closeemdialog(modal);
    } else {
      var prevent = false;
      $(checkForm)
        .find("input, textarea, select")
        .each(function () {
          if ($(this).attr("type") == "hidden") {
            return true;
          }
          var value = $(this).val();
          if (value) {
            prevent = value.length > 0;
            return false;
          }
        });

      if (prevent && exitWarning) {
        $("#exitConfirmationModal").css("display", "flex");
        return false;
      } else {
        closeemdialog(modal);
      }
      return false;
    }
    trackKeydown = false;
  }

  lQuery("form.checkCloseDialog").livequery(function () {
    var m = $(this).closest(".modal");
    if (m) {
      m.modal({
        backdrop: "static",
        keyboard: false,
      });
      m.on("click", function (e) {
        e.stopPropagation();
        e.stopImmediatePropagation();
        if (e.currentTarget === e.target) {
          confirmModalClose(m);
        }
      });
    }
  });

  $("#closeExit").on("click", function () {
    $("#exitConfirmationModal").hide();
  });
  $("#confirmExit").on("click", function () {
    $("#exitConfirmationModal").hide();
    closeallemdialogs();
  });

  $(document).on("click", ".modal", function (e) {
    e.stopPropagation();
    e.stopImmediatePropagation();
    if (e.target.classList.contains("modal")) {
      confirmModalClose($(this));
    }
  });

  lQuery(".entityclose").livequery("click", function (event) {
    event.preventDefault();
    var targetModal = $(this).closest(".modal");
    confirmModalClose(targetModal);
    hideLoader();
  });

  lQuery(".enablepublishing").livequery("click", function (event) {
    event.preventDefault();
    var link = $(this);
    var options = link.data();

    var url = link.attr("href");

    $.ajax({
      xhrFields: {
        withCredentials: true,
      },
      crossDomain: true,
      url: url,
      data: options,
      success: function (data) {
        var entity = $(".entitydialog");
        entity.data("entitytabopen", "publishing");
        autoreload(entity);
      },
    });
  });

  lQuery(".autoopenemdialog").livequery(function () {
    $(this).emDialog();
  });

  lQuery(".mediaboxheader").livequery("click", function (event) {
    event.preventDefault();
    var box = $(this).closest(".entity-media-box");
    box.toggleClass("collapsedmediabox");
    if (box.hasClass("collapsedmediabox")) {
      $(this)
        .find(".expandmediaboxicon")
        .removeClass("fa-caret-down")
        .addClass("fa-caret-right");
    } else {
      $(this)
        .find(".expandmediaboxicon")
        .removeClass("fa-caret-right")
        .addClass("fa-caret-down");
    }
  });

  function expandmediabox(item) {
    var box = item.closest(".entity-media-box");
    box.removeClass("collapsedmediabox");
    $(this)
      .find(".expandmediaboxicon")
      .removeClass("fa-caret-right")
      .addClass("fa-caret-down");
  }

  lQuery(".trim-text").livequery(function (e) {
    var text = $(this).text();
    var check = $(this).closest(".entitymetadatamodal");
    if (check.length > 0) {
      text = text.replace(/</g, "&lt;");
      text = text.replace(/>/g, "&gt;");
      text = text.replace(/(\r\n|\n|\r)/gm, "<br>");
      $(this).html(text);
      return;
    }
    $(this).click(function (e) {
      if (
        e.target.classList.contains("see-more") ||
        e.target.classList.contains("see-less")
      ) {
        e.stopPropagation();
      }
    });
    var maxLength = $(this).data("max");
    if (text.length <= maxLength) return;
    var minimizedText = text.substring(0, maxLength).trim();
    minimizedText = minimizedText.replace(/</gm, "&lt;");
    minimizedText = minimizedText.replace(/>/gm, "&gt;");
    minimizedText = minimizedText.replace(/(\r\n|\n|\r)/gm, "<br>");
    $(this).html(minimizedText);
    $(this).data("text", text);
    $(this).append('<button class="see-more">(...see more)</button>');
  });

  lQuery(".see-more").livequery("click", function () {
    var textParent = $(this).parent();
    var text = textParent.data("text");
    if (!text) {
      $(this).remove();
      return;
    }
    text = text.replace(/</gm, "&lt;");
    text = text.replace(/>/gm, "&gt;");
    textParent.html(text.replace(/(\r\n|\n|\r)/gm, "<br>"));
    textParent.append('<button class="see-less">(...see less)</button>');
  });

  lQuery(".see-less").livequery("click", function () {
    var textParent = $(this).parent();
    var maxLength = textParent.data("max");
    var text = textParent.data("text");
    if (!maxLength || !text) {
      $(this).remove();
      return;
    }
    var minimizedText = text.substring(0, maxLength).trim();
    minimizedText = minimizedText.replace(/<br>/gm, "\n");
    minimizedText = minimizedText.replace(/</gm, "&lt;");
    minimizedText = minimizedText.replace(/>/gm, "&gt;");
    minimizedText = minimizedText.replace(/(\r\n|\n|\r)/gm, "<br>");
    textParent.html(minimizedText);
    textParent.append('<button class="see-more">(...see more)</button>');
  });

  lQuery(".expandmediabox").livequery("click", function (e) {
    expandmediabox($(this));
  });

  lQuery(".emrowpicker table td").livequery("click", function (event) {
    var clicked = $(this);
    if (clicked.attr("noclick") == "true") {
      return true;
    }
    if ($(event.target).is("input") || $(event.target).is("a")) {
      return true;
    }

    event.preventDefault();
    var row = clicked.closest("tr");
    var table = clicked.closest("table");
    var form = clicked.closest(".pickedcategoryform");

    var existing = row.hasClass("emrowselected");
    if (!form.hasClass("emmultivalue")) {
      $("tr", table).removeClass("emrowselected");
    }
    row.toggleClass("emrowselected");
    var id = row.data("id");

    $(".emselectedrow", form).each(function () {
      if (form.hasClass("emmultivalue")) {
        var old = $(this).val();
        if (old) {
          if (existing) {
            // removing the value
            old = old.replace(id, "");
            old = old.replace("||", "|");
          } else {
            old = old + "|" + id;
          }
        } else {
          old = id;
        }
        $(this).val(old);
      } else {
        $(this).val(id);
      }
    });

    form.trigger("submit");

    if (form.hasClass("autoclose")) {
      closeemdialog(form.closest(".modal"));
    }
  });

  lQuery(".emselectable table td").livequery("click", function (event) {
    var clicked = $(this);
    if (clicked.attr("noclick") == "true") {
      return true;
    }
    if ($(event.target).is("input")) {
      return true;
    }
    if ($(event.target).is("a")) {
      return true;
    }
    if ($(event.target).hasClass("jp-audio")) {
      return true;
    }

    var emselectable = clicked.closest("#emselectable");
    if (emselectable.length < 1) {
      emselectable = clicked.closest(".emselectable");
    }
    var row = $(clicked.closest("tr"));
    var rowid = row.attr("rowid");

    if (row.hasClass("thickbox")) {
      var href = row.data("href");
      openFancybox(href);
    } else {
      emselectable.find("table tr").each(function (index) {
        clicked.removeClass("emhighlight");
      });
      row.addClass("emhighlight");
      row.removeClass("emborderhover");
      var table = row.closest("table");

      // var url = emselectable.data("clickpath");

      var url = table.data("clickpath");
      var form = emselectable.find("form");
      if (!form.length) {
        form = emselectable.data("emselectableform");
        if (form) {
          form = $("#" + form);
        }
      }
      var data = row.data();

      if (form && form.length > 0) {
        data.id = rowid;
        data.oemaxlevel = form.data("oemaxlevel");
        form.find("#emselectedrow").val(rowid);
        form.find(".emneedselection").each(function () {
          clicked.removeAttr("disabled");
        });
        $(form).trigger("submit");
        if (form.hasClass("autoclose")) {
          closeemdialog(form.closest(".modal"));
        }
      } else if (url != undefined) {
        if (url == "") {
          return true;
        }
        var link = url;
        var post = table.data("viewpostfix");
        if (post != undefined) {
          link = link + rowid + post;
        } else {
          link = link + rowid;
        }
        if (emselectable.hasClass("showmodal")) {
          showmodal(emselectable, link);
        } else {
          parent.document.location.href = link;
        }
      }
    }
  });

  //Entity Dialog Table
  lQuery(".emselectablemodule table td").livequery("click", function (event) {
    var clicked = $(this);
    if (clicked.attr("noclick") == "true") {
      return true;
    }
    if ($(event.target).is("input")) {
      return true;
    }
    if ($(event.target).is("a")) {
      return true;
    }
    if ($(event.target).closest(".jp-audio").length) {
      return true;
    }

    var emselectable = clicked.closest(".emselectablemodule");

    var row = $(clicked.closest("tr"));
    var rowid = row.attr("rowid");

    var targetlink = emselectable.data("targetlink");

    if (emselectable.hasClass("emselectablemodule_order")) {
      //Order Module
      var targetdiv = emselectable.data("targetdiv");
      if (targetlink && targetlink != "") {
        targetlink +=
          (targetlink.indexOf("?") >= 0 ? "&" : "?") + "id=" + rowid;

        $.ajax({
          url: targetlink,
          data: { oemaxlevel: "2" },
          success: function (data) {
            $("#" + targetdiv).replaceWith(data);
            jQuery(window).trigger("resize");
          },
        });
      }
    } else {
      //All entities
      if (targetlink && targetlink != "") {
        targetlink +=
          (targetlink.indexOf("?") >= 0 ? "&" : "?") + "id=" + rowid;
        row.data("targetlink", targetlink);
        row.data("id", rowid);
        row.data("hitssessionid", emselectable.data("hitssessionid"));
        row.data("updateurl", true);
        var urlbar =
          apphome +
          "/views/modules/" +
          emselectable.data("searchtype") +
          "/index.html?entity=" +
          rowid;
        row.data("urlbar", urlbar);
        row.emDialog();
      }
    }
  });

  //Entity SubModule Table
  lQuery(".emselectableentity table td").livequery("click", function (event) {
    var clicked = $(this);
    if (clicked.attr("noclick") == "true") {
      return true;
    }
    if ($(event.target).is("input")) {
      return true;
    }
    if ($(event.target).is("a")) {
      return true;
    }
    if ($(event.target).closest(".jp-audio").length) {
      return true;
    }
    var emselectable = clicked.closest(".emselectableentity");

    var row = $(clicked.closest("tr"));
    var rowid = row.attr("rowid");

    var tabletype = emselectable.data("tabletype"); //make this earlier?
    if (tabletype == "subentity") {
      var entityid = rowid;
      if (entityid) {
        var container = emselectable.closest(".entity-wraper");
        var tabs = container.find(".entity-tab-content");
        if (tabs.length >= 6) {
          alert("Max Tabs Limit");
          return;
        }
        var tabexists = false;

        if (!tabexists) {
          var options1 = emselectable.data();
          var targetcomponenthome = emselectable.data("targetcomponenthome");
          var clickurl =
            targetcomponenthome + "/gridsample/preview/entity.html";
          options1.oemaxlevel = 1;
          options1.id = rowid;
          jQuery.ajax({
            url: clickurl,
            data: options1,
            success: function (data) {
              var parent = container.closest(".entitydialog");
              container.replaceWith(data);
              tabbackbutton(parent);
            },
          });
        }

        return;
      }
    }
  });

  //Entity Add Sub Module
  lQuery(".emselectableentityadd table td").livequery(
    "click",
    function (event) {
      var clicked = $(this);
      if (clicked.attr("noclick") == "true") {
        return true;
      }
      if ($(event.target).is("input")) {
        return true;
      }
      if ($(event.target).is("a")) {
        return true;
      }
      var emselectable = clicked.closest(".emselectableentityadd");
      targetdiv = emselectable.data("targetdiv");

      var row = $(clicked.closest("tr"));
      clickurl = finddata(emselectable, "clickurl");
      var targetdiv = finddata(emselectable, "targetdiv");
      var options = emselectable.data();
      options.id = row.attr("rowid");

      if (targetdiv != "") {
        jQuery.ajax({
          url: clickurl,
          data: options,
          success: function (data) {
            if (!targetdiv.jquery) {
              targetdiv = $("#" + targetdiv);
            }
            //					targetdiv.replaceWith(data);
            autoreload(targetdiv);
          },
        });
      }
      return;
    }
  );

  lQuery(".pickcategorylink").livequery("click", function () {});

  lQuery(".assetpickerselectrow").livequery("click", function () {
    var assetid = $(this).data("assetid");
    jQuery("#" + targetdiv).attr("value", assetid);

    //Todo: Integrte with emselectable
    var emselectable = $(this).closest(".emselectable");

    var launcher = emselectable.data("launcher");
    launcher = $("#" + launcher);
    if (launcher.length) {
      var options = launcher.data();
      options.assetid = assetid;

      var clickurl = launcher.data("clickurl");
      if (clickurl && clickurl != "") {
        var targetdiv = launcher.data("targetdiv");
        if (targetdiv != "") {
          jQuery.ajax({
            url: clickurl,
            data: options,
            success: function (data) {
              if (!targetdiv.jquery) {
                targetdiv = $("#" + targetdiv);
              }
              targetdiv.replaceWith(data);
              closeemdialog(emselectable.closest(".modal"));
            },
          });
        }
        return;
      }
    }
  });

  //newpicker
  lQuery(".pickerselectrow").livequery("click", function (event) {
    var row = $(this).parent("tr");
    var pickerresults = $(this).closest(".pickerresults");

    if (pickerresults.length) {
      var options = pickerresults.data();
      //options.assetid = assetid;

      options.id = row.data("id");
      var clickurl = pickerresults.data("clickurl");
      var targetdiv = pickerresults.data("clicktargetdiv");
      //ToDo make it generic
      var targettype = pickerresults.data("clicktargettype");

      if (targettype == "entitydialog") {
        pickerresults.emDialog();
        return;
      } else {
        if (targetdiv != "") {
          jQuery.ajax({
            url: clickurl,
            data: options,
            success: function (data) {
              if (!targetdiv.jquery) {
                targetdiv = $("#" + targetdiv);
              }

              if (targettype == "message") {
                targetdiv.prepend(data);
                targetdiv.find(".fader").fadeOut(3000, "linear");
              } else {
                //regular targetdiv
                targetdiv.replaceWith(data);
              }

              closeemdialog(pickerresults.closest(".modal"));
            },
          });
        }
        return;
      }
    }
  });

  showmodal = function (emselecttable, url) {
    trackKeydown = true;
    var id = "modals";
    var modaldialog = $("#" + id);
    var width = emselecttable.data("dialogwidth");
    if (modaldialog.length == 0) {
      $("#emcontainer").append(
        '<div class="modal " tabindex="-1" id="' +
          id +
          '" style="display:none" ></div>'
      );
      modaldialog = $("#" + id);
    }

    var options = emselecttable.data();
    modaldialog.load(url, options, function () {
      $(".modal-lg").css("min-width", width + "px");
      modaldialog.modal({
        keyboard: true,
        backdrop: true,
        show: true,
      });

      var title = emselecttable.data("dialogtitle");
      if (title) {
        $(".modal-title", modaldialog).text(title);
      }

      $("form", modaldialog).find("*").filter(":input:visible:first").focus();
    });
  };

  lQuery("img.framerotator").livequery(function () {
    $(this).hover(
      function () {
        $(this).data("frame", 0);
        var path = this.sr$("select#speedC").selectmenu({
          style: "dropdown",
        });
        c.split("?")[0];
        var intval = setInterval(
          "nextFrame('" + this.id + "', '" + path + "')",
          1000
        );
        $(this).data("intval", intval);
      },
      function () {
        var path = this.src.split("?")[0];
        this.src = path + "?frame=0";
        var intval = $(this).data("intval");
        clearInterval(intval);
      }
    );
  });

  lQuery(".jp-play").livequery("click", function () {
    // alert("Found a player, setting it up");
    var player = $(this).closest(".jp-audio").find(".jp-jplayer");
    var url = player.data("url");
    var containerid = player.data("container");
    var container = $("#" + containerid);

    player.jPlayer({
      ready: function (event) {
        player
          .jPlayer("setMedia", {
            mp3: url,
          })
          .jPlayer("play");
      },
      play: function () {
        // To avoid both jPlayers playing together.
        player.jPlayer("pauseOthers");
      },
      swfPath: apphome + "/components/javascript",
      supplied: "mp3",
      wmode: "window",
      cssSelectorAncestor: "#" + containerid,
    });

    // player.jPlayer("play");
  });

  lQuery(".select-dropdown-open").livequery("click", function () {
    if ($(this).hasClass("down")) {
      $(this).removeClass("down");
      $(this).addClass("up");
      $(this).siblings(".select-dropdown").show();
    } else {
      $(this).removeClass("up");
      $(this).addClass("down");
      $(this).siblings(".select-dropdown").hide();
    }
  });
  lQuery(".select-dropdown li a").livequery("click", function () {
    $(this)
      .closest(".select-dropdown")
      .siblings(".select-dropdown-open")
      .removeClass("up");
    $(this)
      .closest(".select-dropdown")
      .siblings(".select-dropdown-open")
      .addClass("down");
    $(this).closest(".select-dropdown").hide();
  });

  function select2formatResult(emdata) {
    /*
     * var element = $(this.element); var showicon =
     * element.data("showicon"); if( showicon ) { var type =
     * element.data("searchtype"); var html = "<img
     * class='autocompleteicon' src='" + themeprefix + "/images/icons/" +
     * type + ".png'/>" + emdata.name; return html; } else { return
     * emdata.name; }
     */
    return emdata.name;
  }
  function select2Selected(selectedoption) {
    return selectedoption.name || selectedoption.text;
  }

  lQuery(".suggestsearchinput").livequery(function () {
    var theinput = $(this);
    if (theinput && theinput.autocomplete) {
      theinput.autocomplete({
        source: apphome + "/components/autocomplete/assetsuggestions.txt",
        select: function (event, ui) {
          // set input that's just for display
          // purposes
          theinput.val(ui.item.value);
          $("#search_form").submit();
          return false;
        },
      });
    }
    //console.log(theinput);

    if (theinput.data("quicksearched") == true) {
      var strLength = theinput.val().length * 2;
      theinput.focus();
      theinput[0].setSelectionRange(strLength, strLength);
    }
  });

  lQuery("input.defaulttext").livequery("click", function () {
    var theinput = $(this);
    var startingtext = theinput.data("startingtext");
    if (theinput.val() == startingtext) {
      theinput.val("");
    }
  });

  lQuery("select.listtags").livequery(function () {
    var theinput = $(this);
    var dropdownParent = theinput.data("dropdownparent");
    if (dropdownParent && $("#" + dropdownParent).length) {
      dropdownParent = $("#" + dropdownParent);
    } else {
      dropdownParent = $(this).parent();
    }
    var parent = theinput.closest("#main-media-container");
    if (parent.length) {
      dropdownParent = parent;
    }
    var parent = theinput.parents(".modal-content");
    if (parent.length) {
      dropdownParent = parent;
    }
    var searchtype = theinput.data("searchtype");
    var searchfield = theinput.data("searchfield");
    var catalogid = theinput.data("listcatalogid");
    var sortby = theinput.data("sortby");
    var defaulttext = theinput.data("showdefault");
    if (!defaulttext) {
      defaulttext = "Search";
    }
    var allowClear = $(this).data("allowclear");
    if (allowClear == undefined) {
      allowClear = true;
    }

    var url = theinput.data("url");
    if (!url) {
      url = apphome + "/components/xml/types/autocomplete/tagsearch.txt";
    }

    if ($.fn.select2) {
      theinput.select2({
        tags: true,
        placeholder: defaulttext,
        allowClear: allowClear,
        dropdownParent: dropdownParent,
        selectOnBlur: true,
        delay: 150,
        minimumInputLength: 1,
        ajax: {
          // instead of writing
          // the function to
          // execute the request
          // we use Select2's
          // convenient helper
          url: url,
          xhrFields: {
            withCredentials: true,
          },
          crossDomain: true,
          dataType: "json",
          data: function (params) {
            var search = {
              page_limit: 15,
              page: params.page,
            };
            search["field"] = searchfield;
            search["operation"] = "contains";
            search["searchtype"] = searchtype;
            search[searchfield + ".value"] = params.term; // search
            // term
            search["sortby"] = sortby; // search
            // term
            return search;
          },
          processResults: function (data, params) {
            // parse the
            // results
            // into the
            // format
            // expected
            // by
            // Select2.
            params.page = params.page || 1;
            return {
              results: data.rows,
              pagination: {
                more: false,
                // (params.page * 30) <
                // data.total_count
              },
            };
          },
        },
        escapeMarkup: function (m) {
          return m;
        },
        templateResult: select2formatResult,
        templateSelection: select2Selected,
        tokenSeparators: ["|"],
        separator: "|",
      });
    }
    theinput.on("select2:select", function () {
      if ($(this).parents(".ignore").length == 0) {
        $(this).valid();
      }
    });
    theinput.on("select2:unselect", function () {
      if ($(this).parents(".ignore").length == 0) {
        $(this).valid();
      }
    });
  });

  lQuery("select.searchtags").livequery(function () {
    var theinput = $(this);
    var dropdownParent = theinput.data("dropdownparent");
    if (dropdownParent && $("#" + dropdownParent).length) {
      dropdownParent = $("#" + dropdownParent);
    } else {
      dropdownParent = $(this).parent();
    }
    var parent = theinput.closest("#main-media-container");
    if (parent.length) {
      dropdownParent = parent;
    }
    var parent = theinput.parents(".modal-content");
    if (parent.length) {
      dropdownParent = parent;
    }

    var searchfield = theinput.data("searchfield");
    var sortby = theinput.data("sortby");
    var defaulttext = theinput.data("showdefault");
    if (!defaulttext) {
      defaulttext = "Search";
    }
    var allowClear = $(this).data("allowclear");
    if (allowClear == undefined) {
      allowClear = true;
    }

    var url = theinput.data("url");
    if (!url) {
      url = apphome + "/components/xml/types/autocomplete/tagsearch.txt";
    }

    var form = theinput.closest("form");

    if ($.fn.select2) {
      theinput.select2({
        tags: true,
        placeholder: defaulttext,
        allowClear: allowClear,
        dropdownParent: dropdownParent,
        selectOnBlur: true,
        delay: 150,
        minimumInputLength: 2,
        ajax: {
          url: url,
          xhrFields: {
            withCredentials: true,
          },
          crossDomain: true,
          dataType: "json",
          data: function (params) {
            var search = {
              page_limit: 15,
              page: params.page,
            };
            search["field"] = searchfield;
            search["operation"] = "freeform";
            search[searchfield + ".value"] = params.term;

            return search;
          },
          processResults: function (data, params) {
            var term = params.term;
            var currentterms = $(theinput).val();
            if (currentterms) {
              term = currentterms.join(" ") + " " + term;
            }

            $("#descriptionvalue", form).val(term.trim());
            form.trigger("submit");

            params.page = params.page || 1;
            return {
              results: data.rows,
              pagination: {
                more: false,
                // (params.page * 30) <
                // data.total_count
              },
            };
          },
        },
        escapeMarkup: function (m) {
          return m;
        },
        templateResult: select2formatResult,
        templateSelection: select2Selected,
        tokenSeparators: ["|"],
        separator: "|",
      });
    }
    theinput.on("select2:select", function () {
      if ($(this).parents(".ignore").length == 0) {
        $(this).valid();
        var selected = $(this).select2("data");
        var terms = "";
        selected.forEach(function (item, index, arr) {
          terms = terms + " " + item["id"] + "";
        });
        $("#descriptionvalue", form).val(terms);
        form.trigger("submit");
      }
    });
    theinput.on("select2:unselect", function () {
      if ($(this).parents(".ignore").length == 0) {
        $(this).valid();
        var selected = $(this).select2("data");
        var terms = "";
        selected.forEach(function (item, index, arr) {
          terms = terms + " " + item["id"] + "";
        });
        $("#descriptionvalue", form).val(terms);
        form.trigger("submit");
      }
    });
  });

  var lasttypeahead;
  var lastsearch;
  var searchmodaldialog;
  var searchmodalmask;
  var mainsearcheinput;

  lQuery(".mainsearch").livequery(function () {
    mainsearcheinput = $(this);
    var dropdownParent = mainsearcheinput.data("dropdownparent");
    if (dropdownParent && $("#" + dropdownParent).length) {
      dropdownParent = $("#" + dropdownParent);
    } else {
      dropdownParent = $(this).parent();
    }
    var parent = mainsearcheinput.closest("#main-media-container");
    if (parent.length) {
      dropdownParent = parent;
    }
    var parent = mainsearcheinput.parents(".modal-content");
    if (parent.length) {
      dropdownParent = parent;
    }

    var hidescrolling = mainsearcheinput.data("hidescrolling");

    var id = mainsearcheinput.data("dialogid");
    if (!id) {
      id = "typeahead";
    }

    searchmodaldialog = getmodalsearchdialog(id);
    searchmodalmask = $("#quicksearch-mask");

    function setSearchModalSize() {
      var applicationcontentwidth = $("#applicationmaincontent").width();
      if (!applicationcontentwidth) {
        applicationcontentwidth = $("#header").width();
      }
      searchmodaldialog.css("width", applicationcontentwidth - 100 + "px");
      var topposition = $("#header").outerHeight();
      topposition -= 40;
      topposition /= 2;
      topposition += 56;
      searchmodaldialog.css("top", topposition + "px");
      searchmodaldialog.css("left", "50%");
      searchmodaldialog.css("transform", "translateX(-50%)");

      var wh = window.innerHeight;
      if (wh) {
        searchmodaldialog.css("height", wh - topposition - 20 + "px");
      }
    }
    setSearchModalSize();
    window.onresize = setSearchModalSize;

    var options = mainsearcheinput.data();
    var searchurltargetdiv = mainsearcheinput.data("searchurltargetdiv");
    var typeaheadtargetdiv = mainsearcheinput.data("typeaheadtargetdiv");

    if (typeaheadtargetdiv == null) {
      typeaheadtargetdiv = "applicationmaincontent";
    }

    var searchurlentertargetdiv = mainsearcheinput.data(
      "searchurlentertargetdiv"
    );

    var searchfield = mainsearcheinput.data("searchfield");
    var sortby = mainsearcheinput.data("sortby");
    var defaulttext = mainsearcheinput.data("showdefault");
    if (!defaulttext) {
      defaulttext = "Search";
    }
    var allowClear = mainsearcheinput.data("allowclear");
    if (allowClear == undefined) {
      allowClear = true;
    }

    var url = mainsearcheinput.data("typeaheadurl");

    var q = "";
    mainsearcheinput.on("keydown", function (e) {
      if (e.keyCode == 27) {
        togglemodaldialog("hide");
      }
    });
    mainsearcheinput.on("keyup change", function (e) {
      if (mainsearcheinput.val() == q) {
        return;
      }
      q = mainsearcheinput.val();
      if (!q) {
        togglemodaldialog("hide");
        return;
      }

      if (e.keyCode == 27) {
        togglemodaldialog("hide");
      } else if (
        (q != "" && e.which == undefined) ||
        e.which == 8 ||
        (e.which != 37 && e.which != 39 && e.which > 32)
      ) {
        //Real words and backspace
        if (q && q.length < 2) {
          togglemodaldialog("hide");
          return;
        } else {
          togglemodaldialog("show");
        }

        var terms =
          "field=description&operation=contains" +
          "&description.value=" +
          encodeURIComponent(q);

        if (lasttypeahead) {
          lasttypeahead.abort();
        }
        lasttypeahead = $.ajax({
          url: url,
          async: true,
          type: "GET",
          data: terms,
          timeout: 6000,
          beforeSend: function () {
            $("#searchLoading").addClass("show");
          },
          success: function (data) {
            if (data) {
              searchmodaldialog.html(data);
              togglemodaldialog("show");
              gridResize();
            }
          },
          complete: function () {
            $("#searchLoading").removeClass("show");
          },
        });
      } else {
        console.log(e.keyCode);
      }
    });

    lQuery(".qssuggestion").livequery("click", function () {
      var suggestion = $(this).data("suggestion");
      mainsearcheinput.val(decodeURI(suggestion));
      mainsearcheinput.trigger("change");
    });

    lQuery(".closemainsearch").livequery("click", function () {
      togglemodaldialog("hide");
    });
    lQuery(searchmodalmask).livequery("click", function () {
      togglemodaldialog("hide");
    });
    $(document).on("click", function (event) {
      if ($(event.target).closest(searchmodaldialog).length === 0) {
        togglemodaldialog("hide");
      }
    });
    lQuery(".quicksearchexpand").livequery("click", function (e) {
      e.preventDefault();
      e.stopPropagation();
      if (searchmodaldialog.length) {
        var q = mainsearcheinput.val();
        var terms = "";
        if (q) {
          terms =
            "field=description&operation=contains" +
            "&description.value=" +
            encodeURI(q);
        }
        var url = mainsearcheinput.data("typeaheadurl");

        var content = $.ajax({
          url: url,
          async: true,
          type: "GET",
          data: terms,
          timeout: 1000,
          beforeSend: function () {
            $("#searchLoading").addClass("show");
          },
          success: function (data) {
            if (data) {
              searchmodaldialog.html(data);
              togglemodaldialog("show");
              $("#mainsearchvalue").focus();
              gridResize();
            }
          },
          complete: function () {
            $("#searchLoading").removeClass("show");
          },
        });
      }
    });

    function getmodalsearchdialog() {
      searchmodaldialog = $("#" + id);
      if (searchmodaldialog.length == 0) {
        $("#header").append(
          '<div class="typeaheadmodal" tabindex="-1" id="' +
            id +
            '" style="display:none" ></div>'
        );
        searchmodaldialog = $("#" + id);
      }
      return searchmodaldialog;
    }

    function togglemodaldialog(action) {
      if (action == "show") {
        searchmodaldialog.show();
        searchmodalmask.show();
      } else {
        searchmodalmask.hide();
        searchmodaldialog.hide();
      }
    }
  });

  var lasttypeaheadsummary;
  var searchmodaldialog;
  var searchmodalmask;
  var mainsearcheinput;

  lQuery(".filtertypeahead").livequery(function () {
    mainsearcheinput = $(this);
    var q = "";
    var form = mainsearcheinput.closest("#filterform");
    form.data("onsuccess", "filtertypeaheadsuccess");
    var url = form.attr("action");

    mainsearcheinput.on("keydown", function (e) {
      if (e.keyCode == 27) {
      }
    });
    mainsearcheinput.on("keyup change", function (e) {
      if (mainsearcheinput.val() == q) {
        return;
      }
      q = mainsearcheinput.val();
      if (!q) {
        return;
      }

      if (e.keyCode == 27) {
      } else if (
        (q != "" && e.which == undefined) ||
        e.which == 8 ||
        (e.which != 37 && e.which != 39 && e.which > 32)
      ) {
        //Real words and backspace
        if (q && q.length < 2) {
          return;
        } else {
        }

        var terms =
          "field=description&operation=contains" +
          "&description.value=" +
          encodeURIComponent(q);

        if (lasttypeaheadsummary) {
          lasttypeaheadsummary.abort();
        }

        form.trigger("submit");
      } else {
        console.log(e.keyCode);
      }
    });
    function filtertypeaheadsuccess() {
      $(".filtertypeahead").trigger("focus");
    }
  });

  lQuery(".grabfocus").livequery(function () {
    var theinput = $(this);
    theinput.focus();
    var val = theinput.val();
    theinput.val("");
    theinput.val(val);
  });

  lQuery(".emtabs").livequery(function () {
    var tabs = $(this);

    var tabcontent = $("#" + tabs.data("targetdiv"));

    // active the right tab
    var hash = window.location.hash;
    if (hash) {
      var activelink = $(hash, tabs);
      if (activelink.length == 0) {
        hash = false;
      }
    }
    if (!hash) {
      hash = "#" + tabs.data("defaulttab");
    }
    var activelink = $(hash, tabs);
    var loadedpanel = $(hash + "panel", tabcontent);
    if (loadedpanel.length == 0) {
      loadedpanel = $("#loadedpanel", tabcontent);
      loadedpanel.attr("id", activelink.attr("id") + "panel");
      activelink.data("tabloaded", true);
    }
    activelink.parent("li").addClass("emtabselected");
    activelink.data("loadpageonce", false);

    $("a:first-child", tabs).on("click", function (e) {
      e.preventDefault();

      var link = $(this); // activated tab
      $("li", tabs).removeClass("emtabselected");
      link.parent("li").addClass("emtabselected");

      var id = link.attr("id");

      var url = link.attr("href");
      var panelid = id + "panel";
      var tab = $("#" + panelid);
      if (tab.length == 0) {
        tab = tabcontent.append(
          '<div class="tab-pane" id="' + panelid + '" ></div>'
        );
        tab = $("#" + panelid);
      }

      var reloadpage = link.data("loadpageonce");
      var alwaysreloadpage = link.data("alwaysreloadpage");
      if (reloadpage || alwaysreloadpage) {
        if (window.location.href.endsWith(url)) {
          window.location.reload();
        } else {
          window.location.href = url;
        }
      } else {
        url = url + "#" + id;
        var loaded = link.data("tabloaded");
        if (link.data("allwaysloadpage")) {
          loaded = false;
        }
        if (!loaded) {
          var levels = link.data("layouts");
          if (!levels) {
            levels = "1";
          }
          $.get(
            url,
            {
              oemaxlevel: levels,
            },
            function (data) {
              tab.html(data);
              link.data("tabloaded", true);
              $(">.tab-pane", tabcontent).hide();
              tab.show();
              $(window).trigger("resize");
            }
          );
        } else {
          $(">.tab-pane", tabcontent).hide();
          tab.show();
          $(window).trigger("resize");
        }
      }
    });
  });

  lQuery(".closetab").livequery("click", function (e) {
    e.preventDefault();
    var tab = $(this);
    var nextpage = tab.data("closetab");
    $.get(
      nextpage,
      {
        oemaxlayout: 1,
      },
      function (data) {
        var prevtab = tab.closest("li").prev();
        prevtab.find("a").click();

        if (prevtab.hasClass("firstab")) {
          tab.closest("li").remove();
        }
      }
    );
    return false;
  });

  lQuery(".collectionclose").livequery("click", function (e) {
    e.preventDefault();
    var collection = $(this);
    var nextpage = collection.data("closecollection");
    $.get(
      nextpage,
      {
        oemaxlayout: 1,
      },
      function (data) {
        collection.closest("li").remove();
      }
    );
    return false;
  });

  lQuery(".createmedia-btn").livequery("click", function (e) {
    $(".createmedia-tab").removeClass("createmedia-selected");
    $(this).closest(".createmedia-tab").addClass("createmedia-selected");
  });

  lQuery("select.listautocomplete").livequery(function () // select2
  {
    var theinput = $(this);
    var searchtype = theinput.data("searchtype");
    if (searchtype != undefined) {
      // called twice due to
      // the way it reinserts
      // components
      var searchfield = theinput.data("searchfield");

      var foreignkeyid = theinput.data("foreignkeyid");
      var sortby = theinput.data("sortby");

      var defaulttext = theinput.data("showdefault");
      if (!defaulttext) {
        defaulttext = "Search";
      }
      var defaultvalue = theinput.data("defaultvalue");
      var defaultvalueid = theinput.data("defaultvalueid");
      if (apphome === undefined) {
        apphome = "";
      }
      var url =
        apphome +
        "/components/xml/types/autocomplete/datasearch.txt?" +
        "field=" +
        searchfield +
        "&operation=contains&searchtype=" +
        searchtype;
      if (defaultvalue != undefined) {
        url =
          url +
          "&defaultvalue=" +
          defaultvalue +
          "&defaultvalueid=" +
          defaultvalueid;
      }

      var dropdownParent = theinput.data("dropdownparent");
      if (dropdownParent && $("#" + dropdownParent).length) {
        dropdownParent = $("#" + dropdownParent);
      } else {
        dropdownParent = $(this).parent();
      }
      var parent = theinput.closest("#main-media-container");
      if (parent.length) {
        dropdownParent = parent;
      }
      var parent = theinput.parents(".modal-content");
      if (parent.length) {
        dropdownParent = parent;
      }

      var allowClear = theinput.data("allowclear");
      if (allowClear == undefined) {
        allowClear = true;
      }
      if ($.fn.select2) {
        theinput.select2({
          placeholder: defaulttext,
          allowClear: allowClear,
          minimumInputLength: 0,
          dropdownParent: dropdownParent,
          ajax: {
            // instead of writing the
            // function to execute the
            // request we use Select2's
            // convenient helper
            url: url,
            dataType: "json",
            data: function (params) {
              var fkv = theinput
                .closest("form")
                .find("#list-" + foreignkeyid + "value")
                .val();
              if (fkv == undefined) {
                fkv = theinput
                  .closest("form")
                  .find("#list-" + foreignkeyid)
                  .val();
              }
              var search = {
                page_limit: 15,
                page: params.page,
              };
              search[searchfield + ".value"] = params.term; // search
              // term
              if (fkv) {
                search["field"] = foreignkeyid; // search
                // term
                search["operation"] = "matches"; // search
                // term
                search[foreignkeyid + ".value"] = fkv; // search
                // term
              }
              if (sortby) {
                search["sortby"] = sortby; // search
                // term
              }
              return search;
            },
            processResults: function (data, params) {
              // parse the
              // results into
              // the format
              // expected by
              // Select2.
              var rows = data.rows;
              if (theinput.hasClass("selectaddnew")) {
                if (params.page == 1 || !params.page) {
                  var addnewlabel = theinput.data("addnewlabel");
                  var addnewdata = {
                    name: addnewlabel,
                    id: "_addnew_",
                  };
                  rows.unshift(addnewdata);
                }
              }
              // addnew
              params.page = params.page || 1;
              return {
                results: rows,
                pagination: {
                  more: false,
                  // (params.page * 30) <
                  // data.total_count
                },
              };
            },
          },
          escapeMarkup: function (m) {
            return m;
          },
          templateResult: select2formatResult,
          templateSelection: select2Selected,
        });
      }
      // TODO: Remove this?
      theinput.on("change", function (e) {
        if (e.val == "") {
          // Work around for a bug
          // with the select2 code
          var id = "#list-" + theinput.attr("id");
          $(id).val("");
        } else {
          // Check for "_addnew_" show ajax form
          var selectedid = theinput.val();

          if (selectedid == "_addnew_") {
            var clicklink = $("#" + theinput.attr("id") + "add");
            clicklink.trigger("click");

            e.preventDefault();
            theinput.select2("val", "");
            return false;
          }
          if (theinput.hasClass("uifilterpicker")) {
            //Not used?
            //$entry.getId()${fieldname}_val
            var fieldname = theinput.data("fieldname");
            var targethidden = $("#" + selectedid + fieldname + "_val");
            targethidden.prop("checked", true);
          }
          // Check for "_addnew_" show ajax form
          if (theinput.hasClass("selectautosubmit")) {
            if (selectedid) {
              //var theform = $(this).closest("form");
              var theform = $(this).parent("form");
              if (theform.hasClass("autosubmitform")) {
                theform.trigger("submit");
              }
            }
          }
        }
      });

      theinput.on("select2:open", function (e) {
        console.log("open");
        var selectId = $(this).attr("id");
        if (selectId) {
          $(
            ".select2-search__field[aria-controls='select2-" +
              selectId +
              "-results']"
          ).each(function (key, value) {
            value.focus();
          });
        } else {
          document
            .querySelector(".select2-container--open .select2-search__field")
            .focus();
        }
      });
    }
  });
  //-
  //List autocomplete multiple and accepting new options
  lQuery("select.listautocompletemulti").livequery(function () // select2
  {
    var theinput = $(this);
    var searchtype = theinput.data("searchtype");
    if (searchtype != undefined) {
      var searchfield = theinput.data("searchfield");

      var foreignkeyid = theinput.data("foreignkeyid");
      var sortby = theinput.data("sortby");

      var defaulttext = theinput.data("showdefault");
      if (!defaulttext) {
        defaulttext = "Search";
      }
      var defaultvalue = theinput.data("defaultvalue");
      var defaultvalueid = theinput.data("defaultvalueid");

      var url =
        apphome +
        "/components/xml/types/autocomplete/datasearch.txt?" +
        "field=" +
        searchfield +
        "&operation=contains&searchtype=" +
        searchtype;
      if (defaultvalue != undefined) {
        url =
          url +
          "&defaultvalue=" +
          defaultvalue +
          "&defaultvalueid=" +
          defaultvalueid;
      }

      var dropdownParent = theinput.data("dropdownparent");
      if (dropdownParent && $("#" + dropdownParent).length) {
        dropdownParent = $("#" + dropdownParent);
      } else {
        dropdownParent = $(this).parent();
      }
      var parent = theinput.closest("#main-media-container");
      if (parent.length) {
        dropdownParent = parent;
      }
      var parent = theinput.parents(".modal-content");
      if (parent.length) {
        dropdownParent = parent;
      }

      var allowClear = theinput.data("allowclear");
      if (allowClear == undefined) {
        allowClear = true;
      }
      theinput.select2({
        placeholder: defaulttext,
        allowClear: allowClear,
        minimumInputLength: 0,
        tags: true,
        dropdownParent: dropdownParent,
        ajax: {
          // instead of writing the
          // function to execute the
          // request we use Select2's
          // convenient helper
          url: url,
          dataType: "json",
          data: function (params) {
            var fkv = theinput
              .closest("form")
              .find("#list-" + foreignkeyid + "value")
              .val();
            if (fkv == undefined) {
              fkv = theinput
                .closest("form")
                .find("#list-" + foreignkeyid)
                .val();
            }
            var search = {
              page_limit: 15,
              page: params.page,
            };
            search[searchfield + ".value"] = params.term; // search
            // term
            if (fkv) {
              search["field"] = foreignkeyid; // search
              // term
              search["operation"] = "matches"; // search
              // term
              search[foreignkeyid + ".value"] = fkv; // search
              // term
            }
            if (sortby) {
              search["sortby"] = sortby; // search
              // term
            }
            return search;
          },
          processResults: function (data, params) {
            // parse the
            // results into
            // the format
            // expected by
            // Select2.
            var rows = data.rows;
            return {
              results: rows,
              pagination: {
                more: false,
                // (params.page * 30) <
                // data.total_count
              },
            };
          },
        },
        escapeMarkup: function (m) {
          return m;
        },
        templateResult: select2formatResult,
        templateSelection: select2Selected,
      });

      // TODO: Remove this?
      theinput.on("change", function (e) {
        if (e.val == "") {
          // Work around for a bug
          // with the select2 code
          var id = "#list-" + theinput.attr("id");
          $(id).val("");
        }
      });

      theinput.on("select2:open", function (e) {
        var selectId = $(this).attr("id");
        if (selectId) {
          $(
            ".select2-search__field[aria-controls='select2-" +
              selectId +
              "-results']"
          ).each(function (key, value) {
            value.focus();
          });
        } else {
          document
            .querySelector(".select2-container--open .select2-search__field")
            .focus();
        }
      });
    }
  });

  lQuery(".sidebarsubmenu").livequery("click", function (e) {
    e.stopPropagation();
  });

  lQuery(".mvpageclick").livequery("click", function (e) {
    e.preventDefault();
    $(".mvpageslist li").removeClass("current");
    $(this).closest("li").addClass("current");
    var pageurl = $(this).data("pageurl");
    $("#mainimage").attr("src", pageurl);
    $(".assetpanel-sidebar").removeClass("assetpanel-sidebar-ontop");
  });

  lQuery(".mvshowpages").livequery("click", function (e) {
    $(".assetpanel-sidebar").addClass("assetpanel-sidebar-ontop");
  });

  lQuery(".mvshowpages-toggle").livequery("click", function (e) {
    $(".assetpanel-sidebar").removeClass("assetpanel-sidebar-ontop");
    $(".assetpanel-sidebar").addClass("assetpanel-sidebar-hidden");
    $(".assetpanel-content").addClass("assetpanel-content-full");
    $(".mvshowpagestab").css("display", "block");
  });

  lQuery("#mainimageholder").livequery(function (e) {
    // Zooming code, only makes sense to run this when we
    // actually have the DOM
    if ($(this).position() == undefined) {
      // check if the
      // element isn't
      // there (best
      // practice
      // is...?)
      return;
    }
    var clickspot;
    var imageposition;
    var zoom = 30;
    var mainholder = $(this);
    var mainimage = $("#mainimage", mainholder);
    //mainimage.width(mainholder.width());
    $(window).bind("mousewheel DOMMouseScroll", function (event) {
      var mainimage = $("#mainimage");
      if (
        $("#hiddenoverlay").css("display") == "none" ||
        !$("#mainimage").length
      ) {
        return true;
      }

      event.preventDefault();

      if (
        event.originalEvent.wheelDelta > 0 ||
        event.originalEvent.detail < 0
      ) {
        // scroll up
        var w = mainimage.width();
        mainimage.width(w + zoom);
        var left = mainimage.position().left - zoom / 2;
        mainimage.css({
          left: left + "px",
        });
        return false;
      } else {
        // scroll down
        var w = mainimage.width();
        if (w > 100) {
          mainimage.width(w - zoom);
          var left = mainimage.position().left + zoom / 2;
          mainimage.css({
            left: left + "px",
          });
        }
        return false;
      }
    });

    mainimage.on("mousedown", function (event) {
      //console.log($(event.target));
      if ($(event.target).is(".zoomable")) {
        clickspot = event;
        imageposition = mainimage.position();
      }
      return false;
    });

    mainimage.on("mouseup", function (event) {
      clickspot = false;
      var mainimage = $("#mainimage");
      mainimage.removeClass("imagezooming");
      return false;
    });

    $(document).on("contextmenu", function (event) {
      clickspot = false;
    });

    mainimage.on("mousemove", function (event) {
      // if( isMouseDown() )

      if (clickspot) {
        //console.log(clickspot.pageX);
        var changetop = clickspot.pageY - event.pageY;
        var changeleft = clickspot.pageX - event.pageX;

        var left = imageposition.left - changeleft;
        var top = imageposition.top - changetop;
        var mainimage = $("#mainimage");
        mainimage.css({
          left: left + "px",
          top: top + "px",
        });
        mainimage.addClass("imagezooming");
      }
    });

    var dist1 = 0;

    mainimage.on("touchstart", function (e) {
      var touch = e.touches[0];
      var div = $(e.target);

      if (e.targetTouches.length == 2) {
        //check if two fingers touched screen
        dist1 = Math.hypot(
          //get rough estimate of distance between two fingers
          e.touches[0].pageX - e.touches[1].pageX,
          e.touches[0].pageY - e.touches[1].pageY
        );
      } else {
        div.data("touchstartx", touch.pageX);
        div.data("touchstarty", touch.pageY);
      }
    });

    mainimage.on("touchend", function (e) {
      var touch = e.touches[0];
      var div = $(e.target);
      div.removeData("touchstartx");
      div.removeData("touchstarty");
    });

    var touchzoom = 10;
    var zoomed = false;
    var ww = window.innerWidth;
    var wh = window.innerHeight;

    mainimage.on("touchmove", function (e) {
      var div = $(e.target);
      //Zoom!
      if (e.targetTouches.length == 2 && e.changedTouches.length == 2) {
        // Check if the two target touches are the same ones that started
        var dist2 = Math.hypot(
          //get rough estimate of new distance between fingers
          e.touches[0].pageX - e.touches[1].pageX,
          e.touches[0].pageY - e.touches[1].pageY
        );
        //alert(dist);
        var w = mainimage.width();

        if (dist1 > dist2) {
          //if fingers are closer now than when they first touched screen, they are pinching
          // Zoom out
          var neww = w - zoom;
          if (neww > 50) {
            //not smaller than 50px
            var newleft = mainimage.position().left + touchzoom / 2;
            var newright = newleft + mainimage.width();
            if (newleft < ww / 2 && newright > ww / 2) {
              mainimage.width(w - touchzoom);
              mainimage.css({
                left: left + "px",
              });
            }
            zoomed = true;
          } else {
            zoomed = false;
          }
        } else {
          //if fingers are further apart than when they first touched the screen, they are making the zoomin gesture
          // Zoom in

          var newleft = mainimage.position().left - touchzoom / 2;
          var newright = newleft + mainimage.width();
          if (newleft < ww / 2 && newright > ww / 2) {
            mainimage.width(w + touchzoom);
            mainimage.css({
              left: newleft + "px",
            });
          }
          zoomed = true;
        }
      } else {
        var touch = e.touches[0];
        //Move around only when zooming
        if (zoomed) {
          var left = mainimage.position().left;
          var top = mainimage.position().top;
          var newtop = left;

          var startingx = div.data("touchstartx");
          var startingy = div.data("touchstarty");
          var diffx = (touch.pageX - startingx) / 30; //?
          var diffy = (touch.pageY - startingy) / 30; //?

          if (Math.abs(diffx) > Math.abs(diffy)) {
            var change = Math.abs(diffx) / div.width();
            var newleft = left + diffx;
            var newright = newleft + mainimage.width();
            if (newleft < ww / 2 && newright > ww / 2) {
              mainimage.css({
                left: newleft + "px",
              });
            }
          } else {
            // up/down
            var change = Math.abs(diffy) / div.height();
            newtop = top + diffy;
            mainimage.css({
              top: newtop + "px",
            });
          }
        }

        /*
						 //Swipe?
						 var touch = e.touches[0];
							var startingx = div.data("touchstartx");
							var startingy = div.data("touchstarty");
							var diffx = touch.pageX - startingx;
							var diffy = touch.pageY - startingy;
							var swipe = false;
							if (Math.abs(diffx) > Math.abs(diffy)) {
								var change = Math.abs(diffx) / div.width();
								if (change > .2) {
									if (diffx > 0) {
										swipe = "swiperight";
									} else {
										swipe = "swipeleft";
									}
								}
							} else {
								// do swipeup and swipedown
								var change = Math.abs(diffy) / div.height();
								if (change > .2) {
									if (diffy > 0) {
										swipe = "swipedown";
									} else {
										swipe = "swipeup";
									}
								}

							}

							if (swipe) {
								//console.log(div);
								var event = {};
								event.originalEvent = e;
								event.preventDefault = function() {
								};
								// TODO: Find out why I can't trigger on $(e.target).trigger it
								// ignores us

								//$("#" + div.attr("id")).trigger(swipe);
							}
							*/
      }
    });

    jQuery(document).ready(function () {
      mainimage.width(mainholder.width());
    });
  });

  $("video").each(function () {
    $(this).append('controlsList="nodownload"');
    $(this).on("contextmenu", function (e) {
      e.preventDefault();
    });
  });

  lQuery(".dropdown-menu a.dropdown-toggle").livequery("click", function (e) {
    if (!$(this).next().hasClass("show")) {
      $(this)
        .parents(".dropdown-menu")
        .first()
        .find(".show")
        .removeClass("show");
    }
    var $subMenu = $(this).next(".dropdown-menu");
    $subMenu.toggleClass("show");

    $(this)
      .parents("li.nav-item.dropdown.show")
      .on("hidden.bs.dropdown", function (e) {
        $(".dropdown-submenu .show").removeClass("show");
      });

    return false;
  });
  lQuery(".dropdown-submenu .dropdown-menu a.dropdown-item").livequery(
    "click",
    function (e) {
      $(this).parents(".dropdown-menu.show").removeClass("show");
      return false;
    }
  );

  lQuery(".sidetoggle").livequery("click", function () {
    var div = $(this);
    var target = $(this).data("target");
    toggleUserProperty("minimize" + target, function () {
      $("#" + target).slideToggle("fast", function () {
        $(window).trigger("resize");
      });
      div.find(".caret").toggleClass("exp");
      div.toggleClass("expanded");
      div.toggleClass("minimized");
      div.find(".component-actions").toggle();
    });
  });

  lQuery(".summary-toggler").livequery("click", function (e) {
    var parent = $(this).closest(".summary-container");
    parent.toggleClass("closed");
    var target = $(this).data("target");
    saveProfileProperty("minimize" + target, "false", function () {
      $(".summary-opener").removeClass("closed");
      $(window).trigger("resize");
    });
  });

  lQuery(".summary-opener").livequery("click", function (e) {
    var opener = $(this);

    var target = $(this).data("target");
    saveProfileProperty("minimize" + target, "true", function () {
      opener.addClass("closed");
      $(".summary-container").removeClass("closed");
      $(window).trigger("resize");
    });
  });

  lQuery(".sidebar-toggler").livequery("click", function (e) {
    e.preventDefault();
    e.stopImmediatePropagation();
    var toggler = $(this);
    var options = toggler.data();

    var targetdiv = toggler.data("targetdiv");
    var sidebar = toggler.data("sidebar");
    options["propertyfield"] = "sidebarcomponent";
    var url = toggler.attr("href");
    //console.log(data.modulesearchhitssessionid);
    if (toggler.data("action") == "home") {
      options["sidebarcomponent.value"] = "";
      options["sidebarcomponent"] = "home";

      jQuery.ajax({
        url: url,
        async: false,
        data: options,
        success: function (data) {
          //data = $(data);
          var cell = findclosest(toggler, "#" + targetdiv);
          cell.replaceWith(data); //Cant get a valid dom element
          $(".pushcontent").removeClass("pushcontent-" + sidebar);
          $(".pushcontent").removeClass("pushcontent-open");
          $(".pushcontent").addClass("pushcontent-fullwidth");

          setPageTitle(cell);

          $(window).trigger("resize");

          history.pushState($("#application").html(), null, url);
        },
        xhrFields: {
          withCredentials: true,
        },
        crossDomain: true,
      });
    } else if (toggler.data("action") == "hide") {
      //hide sidebar
      options["module"] = $("#applicationcontent").data("moduleid");
      options["sidebarcomponent.value"] = "";
      var url = apphome + "/components/sidebars/index.html";

      jQuery.ajax({
        url: url,
        async: false,
        data: options,
        success: function (data) {
          var cell = findclosest(toggler, "#" + targetdiv);
          cell.replaceWith(data); //Cant get a valid dom element
          $(".pushcontent").removeClass("pushcontent-" + sidebar);
          $(".pushcontent").removeClass("pushcontent-open");
          $(".pushcontent").addClass("pushcontent-fullwidth");
          //$(".pushcontent").css("margin-left","");

          setPageTitle(cell);

          $(window).trigger("resize");

          history.pushState($("#application").html(), null, url);
        },
        xhrFields: {
          withCredentials: true,
        },
        crossDomain: true,
      });
    } else {
      //showsidebar
      options["module"] = $("#applicationcontent").data("moduleid");
      options["sidebarcomponent.value"] = sidebar;
      var url = "";
      if (options["contenturl"] != undefined) {
        url = options["contenturl"];
        targetdiv = $("#" + options["targetdiv"]);
      } else {
        url = apphome + "/components/sidebars/index.html";
        targetdiv = findclosest(toggler, "#" + targetdiv);
      }
      jQuery.ajax({
        url: url,
        async: false,
        data: options,
        success: function (data) {
          targetdiv.replaceWith(data); //Cant get a valid dom element
          $(".pushcontent").removeClass("pushcontent-fullwidth");
          $(".pushcontent").addClass("pushcontent-open");
          //$(".pushcontent").css("margin-left","");
          $(".pushcontent").addClass("pushcontent-" + sidebar);
          var mainsidebar = $(".col-mainsidebar");
          if (mainsidebar.data("sidebarwidth")) {
            var width = mainsidebar.data("sidebarwidth");
            if (typeof width == "number") {
              $(".pushcontent").css("margin-left", width + "px");
            }
          }

          setPageTitle(targetdiv);

          $(window).trigger("resize");
        },
        xhrFields: {
          withCredentials: true,
        },
        crossDomain: true,
      });
    }
  });

  lQuery(".toggledialogtree").livequery("click", function (e) {
    e.preventDefault();
    e.stopImmediatePropagation();
    var toggler = $(this);
    var options = toggler.data();
    var url = toggler.data("url");
    var targetdiv = toggler.data("targetdiv");
    var treestatus = toggler.data("treestatus");
    saveProfileProperty("dialogtreestatus", treestatus, function () {});
    jQuery.ajax({
      url: url,
      async: false,
      data: options,
      success: function (data) {
        data = $(data);
        $("#" + targetdiv).replaceWith(data);
        $(window).trigger("resize");
      },
    });
  });

  lQuery(".col-mainsidebarZ").livequery("mouseenter mouseleave", function (e) {
    var toggler = $(this).find(".sidebar-toggler-hide");
    if (toggler) {
      toggler.toggle();
    }
  });

  lQuery(".assetpicker .removefieldassetvalue").livequery(
    "click",
    function (e) {
      e.preventDefault();
      var picker = $(this).closest(".assetpicker");
      var detailid = $(this).data("detailid");

      picker.find("#" + detailid + "-preview").html("");
      picker.find("#" + detailid + "-value").val("");
      picker.find("#" + detailid + "-file").val("");

      var theform = $(picker).closest("form");
      theform = $(theform);
      if (theform.hasClass("autosubmit")) {
        theform.trigger("submit");
      }
    }
  );

  //$('[data-toggle="tooltipb"]').tooltip();

  //Sidebar Custom Width
  lQuery(".sidebar-toggler-resize").livequery(function () {
    var slider = $(this);
    var column = $(this).closest(".col-main");

    var clickspot;
    var startwidth;
    var width;

    slider.on("mouseover", function () {
      $(this).css("opacity", "0.6");
    });
    slider.on("mouseout", function () {
      if (!clickspot) {
        $(this).css("opacity", "0");
      }
    });
    slider.on("mousedown", function (event) {
      if (!clickspot) {
        clickspot = event;
        startwidth = column.width();
        return false;
      }
    });

    //$(".sidebar-toggler-resize").show();

    $(window).on("mouseup", function (event) {
      if (clickspot) {
        clickspot = false;
        $(this).css("opacity", "0");
        if (width != "undefined") {
          saveProfileProperty("sidebarwidth", width, function () {
            $(window).trigger("resize");
          });
        }
        return false;
      }
    });
    $(window).on("mousemove", function (event) {
      if (clickspot) {
        $(this).css("opacity", "0.6");
        width = 0;
        var changeleft = event.pageX - clickspot.pageX;
        width = startwidth + changeleft;
        width = width + 32;
        if (width < 200) {
          width = 200;
        }
        if (width > 380) {
          //break sidebarfilter columns
          column.addClass("sidebarwide");
        } else {
          column.removeClass("sidebarwide");
        }
        if (width > 500) {
          width = 500;
        }
        column.width(width);
        column.data("sidebarwidth", width);
        $(".pushcontent").css("margin-left", width + "px");
        event.preventDefault();
        $(window).trigger("resize");
        return false;
      }
    });
  });

  lQuery(".col-resize").livequery(function () {
    var slider = $(this);
    var column = $(this).closest(".col-main");
    var content = $(".pushcontent");

    var clickspot;
    var startwidth;
    var width;

    slider.on("mousedown", function (event) {
      if (!clickspot) {
        clickspot = event;
        startwidth = column.width();
        return false;
      }
    });

    //$(".sidebar-toggler-resize").show();

    $(window).on("mouseup", function (event) {
      if (clickspot) {
        clickspot = false;
        if (width != "undefined") {
          saveProfileProperty("sidebarwidth", width, function () {
            $(window).trigger("resize");
          });
        }
        return false;
      }
    });
    $(window).on("mousemove", function (event) {
      if (clickspot) {
        width = 0;
        var changeleft = event.pageX - clickspot.pageX;
        width = startwidth + changeleft;
        if (width < 200) {
          width = 200;
        }
        if (width > 480) {
          width = 480;
        }
        //console.log("W " , width);
        column.width(width);
        column.data("sidebarwidth", width);
        $(".pushcontent").css("margin-left", width + "px");
        event.preventDefault();
        $(window).trigger("resize");
        return false;
      }
    });
  });

  lQuery(".sidebarselected").livequery("click", function () {
    $("#sidebar-entities li").removeClass("current");
    $("#sidebar-list-upload").addClass("current");
  });

  //Moved From settings.js
  lQuery("#datamanager-workarea th.sortable").livequery("click", function (e) {
    var table = $("#main-results-table");
    var args = {
      oemaxlevel: 1,
      hitssessionid: table.data("hitssessionid"),
      origURL: table.data("origURL"),
      catalogid: table.data("catalogid"),
      searchtype: table.data("searchtype"),
    };
    var column = $(this);
    var fieldid = column.data("fieldid");

    if (column.hasClass("currentsort")) {
      if (column.hasClass("up")) {
        args.sortby = fieldid + "Down";
      } else {
        args.sortby = fieldid + "Up";
      }
    } else {
      $("#datamanager-workarea th.sortable").removeClass("currentsort");
      column.addClass("currentsort");
      column.addClass("up");
      args.sortby = fieldid + "Up";
    }
    $("#datamanager-workarea").load(
      apphome + "/views/settings/lists/datamanager/list/columnsort.html",
      args
    );
    e.stopPropagation();
  });

  lQuery(".tabnav a").livequery("click", function () {
    $(".tabnav a").removeClass("current");
    $(this).addClass("current");
  });

  lQuery("select.eventsjump").livequery("change", function () {
    var val = $(this).val();
    var url = $(this).data("eventurl");
    var targetdiv = $(this).data("targetdiv");

    $("#" + targetdiv).load(url + "?oemaxlevel=1&type=" + val, function () {
      $(window).trigger("resize");
    });
  });
  lQuery(".permissionsroles").livequery("change", function () {
    var val = $(this).val();
    var targetdiv = $(this).data("targetdiv");
    var url = $(this).data("urlprefix");
    var permissiontype = $(this).data("permissiontype");
    if (val == "new") {
      $("#" + targetdiv).load(
        url + "addnew.html?oemaxlevel=1&groupname=New",
        function () {
          $(window).trigger("resize");
        }
      );
      $("#module-picker").hide();
    } else {
      $("#" + targetdiv).load(
        url +
          "index.html?oemaxlevel=1&permissiontype=" +
          permissiontype +
          "&settingsgroupid=" +
          val,
        function () {
          $(window).trigger("resize");
        }
      );
      $("#module-picker").show();
    }
  });

  lQuery(".permission-radio").livequery("click", function () {
    var val = jQuery(this).val();

    if (val == "partial") {
      jQuery(this).parent().find(".sub-list").show();
    } else {
      jQuery(this).parent().find(".sub-list").hide();
    }
  });

  lQuery("#module-picker select").livequery("change", function () {
    var rolesval = $(".permissionsroles").val();
    var val = $(this).val();
    if (val == "all") {
      jQuery(".togglesection").show();
    } else {
      jQuery(".togglesection").hide();
      jQuery("." + val).show();
    }
  });

  function replaceAll(str, find, replace) {
    find = escapeRegExp(find);
    return str.replace(new RegExp(find, "g"), replace);
  }

  function escapeRegExp(str) {
    return str.replace(/([.*+?^=!:${}()|\[\]\/\\])/g, "\\$1");
  }

  lQuery("#setup-view").livequery("click", function () {
    if ($("#setup-view").hasClass("open")) {
      $("#views-header").height(44);
      $("#views-settings").hide();
      $("#setup-view").removeClass("open");
    } else {
      $("#views-header").height("auto");
      $("#views-settings").show();
      $("#setup-view").addClass("open");
    }
  });

  $("#renderastable").click(function () {
    if ($("#renderastable").is(":checked")) {
      $("#rendertableoptions").show();
    } else {
      $("#rendertableoptions").hide();
    }
  });

  lQuery(".sortviews").livequery(function () {
    var sortable = $(this);
    var path = sortable.data("path");
    if (typeof sortable.sortable == "function") {
      sortable.sortable({
        axis: "y",
        cancel: ".no-sort",
        update: function (event, ui) {
          //debugger;
          var data = sortable.sortable("serialize");
          data = replaceAll(data, "viewid[]=", "|");
          data = replaceAll(data, "&", "");
          data = data.replace("|", "");
          var args = {};
          args.items = data;
          args.viewpath = sortable.data("viewpath");
          args.searchtype = sortable.data("searchtype");
          args.assettype = sortable.data("assettype");
          args.viewid = sortable.data("viewid");
          $.ajax({
            data: args,
            type: "POST",
            url: path,
          });
        },
        stop: function (event, ui) {
          //db id of the item sorted
          //alert(ui.item.attr('plid'));
          //db id of the item next to which the dragged item was dropped
          //alert(ui.item.prev().attr('plid'));
        },
      });
    }
  });

  lQuery(".listsort").livequery(function () {
    var listtosort = $(this);
    if (typeof listtosort.sortable == "function") {
      listtosort.sortable({
        axis: "y",
        cancel: ".no-sort",
        stop: function (event, ui) {
          var path = $(this).data("path");

          var data = "";

          // var ids = new Array();
          $(this)
            .find("li")
            .each(function (index) {
              if (!$(this).hasClass("no-sort")) {
                var id = $(this).attr("id");
                data = data + "ids=" + id + "&";
              }
            });
          // POST to server using $.post or $.ajax
          $.ajax({
            data: data,
            type: "POST",
            url: path,
          });
        },
      });
    }
  });

  lQuery(".tablesort").livequery(function () {
    var listtosort = $(this);
    if (typeof listtosort.sortable == "function") {
      listtosort.sortable({
        axis: "y",
        cancel: ".no-sort",
        stop: function (event, ui) {
          var path = $(this).data("path");

          var data = "";

          // var ids = new Array();
          $(this)
            .find("tr")
            .each(function (index) {
              if (!$(this).hasClass("no-sort")) {
                var id = $(this).attr("id");
                data = data + "ids=" + id + "&";
              }
            });
          // POST to server using $.post or $.ajax
          $.ajax({
            data: data,
            type: "POST",
            url: path,
          });
        },
      });
    }
  });

  lQuery(".favclick").livequery("click", function (e) {
    e.preventDefault();
    var item = $(this);
    var itemid = item.data("id");
    var moduleid = item.data("moduleid");
    var favurl = item.data("favurl");
    var targetdiv = item.data("targetdiv");
    var options = item.data();
    if (itemid) {
      if (item.hasClass("itemfavorited")) {
        jQuery.ajax({
          url:
            apphome +
            "/components/userprofile/favoritesremove.html?profilepreference=" +
            "favorites_" +
            moduleid +
            "&profilepreference.value=" +
            itemid,
          success: function () {
            //item.removeClass("ibmfavorited");
            jQuery.get(favurl, options, function (data) {
              $("." + targetdiv)
                .replaceWith(data)
                .hide()
                .fadeIn("slow");
            });
          },
        });
      } else {
        jQuery.ajax({
          url:
            apphome +
            "/components/userprofile/favoritesadd.html?profilepreference=" +
            "favorites_" +
            moduleid +
            "&profilepreference.value=" +
            itemid,
          success: function () {
            //item.addClass("ibmfavorited");
            jQuery.get(favurl, options, function (data) {
              $("." + targetdiv)
                .replaceWith(data)
                .hide()
                .fadeIn();
            });
          },
        });
      }
    }
  });

  lQuery(".seemorelink").livequery("click", function (e) {
    e.preventDefault();
    var textbox = $(this).data("seemore");
    if (textbox) {
      $("#" + textbox).removeClass("seemoreclosed");
      $(this).hide();
    }
  });

  lQuery("#collectionresultsdialogX .rowclick").livequery(
    "click",
    function (e) {
      var launcher = jQuery("#search-collections-dialog").data(
        "searchcollectinolauncher"
      );
      if (launcher != "") {
        launcher = $("#" + launcher);
        if (launcher.length) {
          var nextpage = launcher.data("targeturl");
          var targetdiv = launcher.data("targetdiv");
          if (nextpage && targetdiv) {
            closeemdialog($(this).closest(".modal"));
            var rowid = $(this).attr("rowid");
            var options = launcher.data();
            options.collectionid = rowid;
            console.log(options);
            jQuery.ajax({
              url: nextpage,
              data: options,
              success: function (data) {
                $("#" + targetdiv).html(data);
                $(window).trigger("resize");
              },
            });
          }
        }
      }
    }
  );
  lQuery("a.clear-bookmarks").livequery("click", function (e) {
    e.preventDefault();
    if (confirm("Are you sure you want to clear all bookmarks?")) {
      //TODO: Implement
    }
  });

  lQuery("#assetcollectionresultsdialog .rowclick").livequery(
    "click",
    function (e) {
      closeemdialog($(this).closest(".modal"));
      var rowid = $(this).attr("rowid");
      $("#submitcollectionid").val(rowid);
      $("#colelectform").trigger("submit");
    }
  );

  lQuery(".copyembed").livequery("click", function (e) {
    e.preventDefault();
    var embedbtn = $(this);
    var loaddiv = embedbtn.data("targetdivinner");
    var nextpage = embedbtn.attr("href");
    jQuery.get(nextpage, function (data) {
      $("#" + loaddiv).html(data);
      var copyText = $("#" + loaddiv).children("textarea");
      if (typeof copyText != "undefined") {
        copyText.select();
        document.execCommand("copy");
      }
      $(window).trigger("resize"); //need this?
    });
  });

  lQuery(".toggle-upload-details").livequery("click", function (e) {
    toggleuploaddetails($(this));
  });

  toggleuploaddetails = function (detail, status = "") {
    if (status == "") {
      status = detail.data("status");
    }
    if (status == "open") {
      detail.next(".toggle-content").hide();
      detail
        .children(".fas")
        .removeClass("fa-caret-down")
        .addClass("fa-caret-right");
      detail.data("status", "closed");
    } else {
      detail.next(".toggle-content").show();
      detail
        .children(".fas")
        .removeClass("fa-caret-right")
        .addClass("fa-caret-down");
      detail.data("status", "open");
    }
  };

  lQuery(".togglesharelink").livequery("change", function (e) {
    var url = $("input.sharelink").val();
    var value = $(this).data("value");
    if (url.includes(value)) {
      url = url.replace("?" + value, "");
    } else {
      url = url + "?" + value;
    }
    $("span.sharelink").html(url);
    $("input.sharelink").val(url);
  });

  lQuery(".reloadcontainer").livequery("click", function (event) {
    event.preventDefault();
    var link = $(this);
    if (link.data("reloadcontainer")) {
      var container_ = link.data("reloadcontainer");
      var container = $("#" + container_);
      if (container.length) {
        autoreload(container);
      }
    }
    return;
  });

  $(document).keydown(function (e) {
    switch (e.which) {
      case 27: //esckey
        var ismodal = $(".modal.onfront");
        if (ismodal.length) {
          // Close modal only
          closeemdialog(ismodal);
          e.stopPropagation();
          e.preventDefault();
        } else {
          hideOverlayDiv(getOverlay());
        }

        return;
        break;

      default:
        return; // exit this handler for other keys
    }
  });

  lQuery(".emcarousel-link").livequery("click", function (e) {
    e.preventDefault();
    var image = $("#emcarousel-image");
    var link = $(this);
    image.attr("src", link.attr("href"));
    image.attr("alt", link.attr("title"));
    image.data("assetid", link.data("assetid"));
  });

  lQuery(".emoticonmenu span").livequery("click", function () {
    var menuitem = $(this);

    var aparent = $(menuitem.closest(".message-menu").find(".pickemoticon"));
    //console.log(aparent.data());

    var saveurl = aparent.data("toggleurl");
    //Save
    var options = aparent.data();
    options.oemaxlevel = 1;
    options.reactioncharacter = menuitem.data("hex");
    $.ajax({
      url: saveurl,
      async: true,
      data: options,
      success: function (data) {
        $("#chatter-message-" + aparent.data("messageid")).html(data);
        //reload message
      },
    });
  });
}; // uiload

function formsavebackbutton(form) {
  var savedcontainer = $(".enablebackbtn");
  if (savedcontainer.length) {
    var parent = savedcontainer.parent().closest(".entitydialog");
    tabbackbutton(parent);
  }
}

function tabbackbutton(parent) {
  var parentid = "";
  if (parent.length) {
    parentid = parent.attr("id");
  }

  if (parentid != "") {
    //var container = parent.find('.entitydialog');
    var backbtn = $(".entitydialogback");
    $(backbtn).show();
    $(backbtn).data("parentid", parentid);
  }
}

function switchsubmodulebox(item) {
  var parent = item.closest(".editentitymetadata-submodule");
  var results = item.data("targetdiv");
  parent.find("#" + results).hide("fast");
  parent.addClass("editingsubmodule");
  item.hide();
}

replaceelement = function (url, div, options, callback) {
  jQuery.ajax({
    url: url,
    async: false,
    data: options,
    success: function (data) {
      div.replaceWith(data);

      if (callback && typeof callback === "function") {
        //make sure it exists and it is a function
        callback(); //execute it
      }
    },
    xhrFields: {
      withCredentials: true,
    },
    crossDomain: true,
  });
};

checkautoreload = function (indiv) {
  var classes = indiv.data("ajaxreloadtargets"); //assetresults, projectpage, sidebaralbums
  if (classes) {
    var splitnames = classes.split(",");
    $.each(splitnames, function (index, classname) {
      $("." + classname).each(function (index, div) {
        autoreload($(div));
      });
    });
  }
};
autoreload = function (div, callback) {
  var url = div.data("url");
  if (url != undefined) {
    var options = div.data();
    replaceelement(url, div, options, callback);

    gridResize();
  }
};

showajaxstatus = function (uid) {
  //for each asset on the page reload it's status
  //console.log(uid);
  var cell = $("#" + uid);
  if (cell) {
    var path = cell.attr("ajaxpath");
    if (!path || path == "") {
      path = cell.data("ajaxpath");
    }
    //console.log("Loading " + path );
    if (path && path.length > 1) {
      var entermediakey = "";
      if (app !== undefined && app.data("entermediakey") != null) {
        entermediakey = app.data("entermediakey");
      }
      var data = cell.data();

      //TODO: entermedia key or serialize
      jQuery.ajax({
        url: path,
        async: false,
        data: data,
        success: function (data) {
          cell.replaceWith(data);
        },
        xhrFields: {
          withCredentials: true,
        },
        crossDomain: true,
      });
    }
  }
};

var resizecolumns = function () {
  var windowh = $(window).height();

  //togglers always screen height
  var coltogglers = $(".col-sidebar-togglers");
  coltogglers.css("height", windowh - 1);
  var colsidebar = $(".col-mainsidebar");
  colsidebar.css("height", windowh - 1);

  //reset some heights
  $(".settingslayout").css("height", "auto");
  $(".col-content-main").css("height", "auto"); //reset

  $(".adjustHeight").each(function () {
    setMaxHeight($(this));
  });
};

var resizesearchcategories = function () {
  var container = $("#sidecategoryresults");
  if (!container) {
    return;
  }
  var w = container.width();
  var h = container.height();

  var ctree = container.find(".searchcategories-tree");
  var cfilter = container.find(".searchcategories-filter");
  if (w > 640) {
    ctree.addClass("widesidebar");
    cfilter.addClass("widesidebar");
    //var wt = ctree.width();
    //cfilter.width(w-wt-12);
    //cfilter.height(h);
    //ctree.height(h);
  } else {
    ctree.removeClass("widesidebar");
    cfilter.removeClass("widesidebar");
    //cfilter.width(w-12);
    //ctree.height('250');
    //cfilter.height(h-300);
  }
  //console.log(h);
};

function setMaxHeight(elm, child, offset = 32) {
  if (!elm || !elm.length) {
    return;
  }
  var target = elm;
  if (child) {
    target = elm.find(child);
    if (!target || !target.length) {
      return;
    }
  }
  var top = $(window).height() - elm.offset().top - offset;
  top = Math.max(top, 400);
  target.css("height", top + "px");
}

var ranajaxon = new Array();

lQuery(".ajaxstatus").livequery(function () {
  var uid = $(this).attr("id");
  var isrunning = $(this).data("ajaxrunning");
  var timeout = $(this).data("reloadspeed");
  if (timeout == undefined) {
    timeout = 2000;
  }
  var ranonce = ranajaxon[uid];
  if (ranonce == undefined) {
    timeout = 500; //Make the first run a quick one
    ranajaxon[uid] = true;
  }

  setTimeout('showajaxstatus("' + uid + '");', timeout); //First one is always faster
});

var resizegallery = function () {
  var container = $("#emslidesheet");
  if (container.length) {
    var containerw = container.width();
    var boxes = Math.floor(containerw / 230);
    var boxw = Math.floor(containerw / boxes) - 12;
    $("#emslidesheet .emthumbbox").width(boxw);
  }
};

adjustdatamanagertable = function () {
  if ($(".datamanagertable").length) {
    var height = $(window).height();
    $(".datamanagertable").height(height - 320);
  }
};

jQuery(document).ready(function () {
  uiload();
  //  jQuery(window).trigger("resize");

  window.onhashchange = function () {
    $("body").css({ overflow: "visible" }); //Enable scroll
    $(window).trigger("resize");
  };
});

jQuery(window).on("resize", function () {
  adjustdatamanagertable();
  resizesearchcategories();
  resizecolumns();
  gridResize();
});

jQuery(document).on("domchanged", function () {
  //gridResize();
  //resizecolumns();
  //jQuery(window).trigger("resize");
});

jQuery(document).on("emtreeselect", function (event) {
  var treename = event.tree.data("treename");
  if (treename == "sidebarCategories") {
    var selectednode = event.nodeid;
    $("#parentfilter").val(selectednode);

    $("#autosubmitfilter").trigger("submit");
  }
  return false;
});

jQuery(window).on("ajaxsocketautoreload", function () {
  $(".ajaxsocketautoreload").each(function () {
    var cell = $(this);
    var path = cell.data("ajaxpath");
    jQuery.ajax({
      url: path,
      async: false,
      data: {},
      success: function (data) {
        cell.replaceWith(data);
      },
      xhrFields: {
        withCredentials: true,
      },
      crossDomain: true,
    });
  });
});
