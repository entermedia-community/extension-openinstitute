jQuery(document).ready(function() {

	var lasttypeahead;
	var lastsearch;

	lQuery(".typeaheaddropdown").livequery(function() {  //TODO: Move to results.js
		
		var input = $(this);

		var hidescrolling = input.data("hidescrolling");

		
		var id = input.data("dialogid");
		if (!id) {
			id = "typeahead";	
		}
		
		var modaldialog = $("#" + id);
		if (modaldialog.length == 0) {
			input.parent().append(
					'<div class="typeaheadmodal" tabindex="-1" id="' + id
							+ '" style="display:none" ></div>');
			modaldialog = $("#" + id);
		}
		
		var width = input.width();
		var minwidth = input.data("minwidth");

		if (minwidth && width) {
			if( minwidth >  width )
			{
				width =  minwidth;
			}
		}
		
		modaldialog.css("width", width + "px");
		var topposition =  input.height() + 5;
		modaldialog.css("top", topposition+"px");
		modaldialog.css("left", input.offset().left/2+"px");
		//modaldialog.css("left", input.position().left+"px");
		//modaldialog.css("margin", "0 auto");
		modaldialog.css("height", input.data("resultheight")+"px");	 


		var options = input.data();
		
		var searchurltargetdiv = input.data("searchurltargetdiv");
			
		var typeaheadtargetdiv = input.data("typeaheadtargetdiv");
		if(typeaheadtargetdiv == null) {
			typeaheadtargetdiv = "applicationmaincontent"
		}	
		var url = input.data("typeaheadurl");
		var isloaded = false;

		var showdialog = function()
		{
			if( modaldialog.is(":hidden") )
			{
				if( !isloaded)
				{
					var options = input.data();
					modaldialog.load(url, options, function() 
					{
						modaldialog.show();
						isloaded =true;
					});
				}
				else
				{
					modaldialog.show();
				}
			}
			else
			{
				console.log("hide");
				modaldialog.hide();
			}
		}
		

		input.on("focus", function(e) //Keyup sets the value first 
		{
			//showdialog();
		});	
		input.on("click", function(e) //Keyup sets the value first 
		{
			showdialog();
		});	
			
		input.on("keyup", function(e) //Keyup sets the value first 
		{
			var q = input.val();
			q = q.trim();
			options["description.value"] = q;
			
			//var moduleid = $("#applicationcontent").data("moduleid");
			//var searchurl = apphome + "/views/modules/" + moduleid + "/index.html";

			//options["moduleid"] = moduleid;
			
			if( q && q.length < 2)
			{
				return;
			}
			if( q.endsWith(" "))
			{
				return;
			}
			//console.log("Keyup" + e.which);
			if( e.which == 27) //Tab?
			{
				modaldialog.hide();	
			}
			else if(q != "" && (e.which == 8 || (e.which != 37 && e.which != 39 && e.which > 32) ) ) //Real words and backspace
			{
				//console.log("\"" + q + "\" type aheading on " + e.which);
				//Typeahead
				if( lasttypeahead )
				{
					lasttypeahead.abort();
				}
				//Typeahead ajax call
				lasttypeahead = $.ajax(
				{ 
					url: url, async: true, 
					data: options,
					timeout: 5000,
					success: function(data) 
					{
						if(data) 
						{
							modaldialog.html(data);
							var lis = modaldialog.find("li");
							if( lis.length > 0)
							{
								//modaldialog.css("min-height",lis.length * 42 + 25);
								modaldialog.show();
							}
							else
							{
								//modaldialog.hide();
							}
						}	
					}
				});

				var searching = input.data("searching");
				if( searching == "true")
				{
					//console.log("already searching"  + searching);
				}
				var searchurl = input.data("searchurl");//apphome + "/index.html";

				if (searchurl != null) {
					console.log(q + " searching");
					input.data("searching","true");
					
					if( lastsearch )
					{
						lastsearch.abort();
					}
					options["oemaxlevel"] = input.data("oemaxlevel");
					//Regular Search Ajax Call
					lastsearch = $.ajax({ url: searchurl, async: true, data: options, 
						success: function(data) 
						{
							input.data("searching","false");
							//if(data) 
							{
								//var q2 = input.val();
								//if( q2 == q)
								{
									$("#"+searchurltargetdiv).html(data);
									$(window).trigger("resize");
								}	
							}
						}
						,
						complete:  function(data) 
						{
							input.data("searching","false");
							input.css( "cursor","");
						}
					});
				}
			}
		});
		//jQuery("body").on("click", function(event){
		//	modaldialog.hide();
		//});
	});

});