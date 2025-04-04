jQuery(document).ready(function(url,params) 
{ 
	var applink = $('#application').data('home') + $('#application').data('applink');
	
	$('.taskcard').hover(
  function () {
    $(this).find(".dragicon").show();
  }, 
  function () {
    $(this).find(".dragicon").hide();
  }
);
	
	if( jQuery.fn.draggable )
	{
		lQuery(".ui-draggable").livequery( 
			function()
			{	
				jQuery(this).draggable( 
					{ 
						helper: function()
						{
							var target = $(this).data("cloneparentid");
							var cloned = null;
							if( target )
							{
								cloned = $(this).closest("#" + target).clone();
							}
							else
							{
								cloned = $(this).clone();
							}	
							cloned.css("z-index","10000");
							return cloned;
						}
						,
						revert: 'invalid'
					}
				);
			}
		);
	}
	//categorydroparea
	if( jQuery.fn.droppable )
	{
		
    	lQuery(".categorydroparea").livequery(
			function()
			{
				outlineSelectionCol = function(event, ui)
				{
					jQuery(this).addClass("dragoverselected");
				}
					
				unoutlineSelectionCol = function(event, ui)
				{
					jQuery(this).removeClass("dragoverselected");
				}
			
				jQuery(this).droppable(
					{
						drop: function(event, ui) 
						{
							var node = $(this);
							var categoryid = node.parent().data("nodeid");
							node.removeClass("selected");

							var goalid = ui.draggable.data("goalid"); //Drag onto a category
							if( goalid )
							{					
								var params = $(".projectgoals").data();
								params['goalid'] = goalid;
								params['targetcategoryid'] = categoryid;
								params['nodeID'] = $(".projectgoals").data("categoryid");
								
								jQuery.get(applink + "/collective/goals/drop/addtocategory.html", 
										params,
										function(data) 
										{
											$("#goaleditor").replaceWith(data);
										}
								);
							}	
							else
							{
								//move category
								var params = $(".treeclickparameters").data();
								params['categoryid'] = ui.draggable.data("nodeid");//Remove from self
								params['categoryid2'] = categoryid;
								params['oemaxlevel'] = "1";
								
								jQuery.get(applink + "/collective/goals/drop/movecategory.html", 
										params,
										function(data) 
										{
											$("#treeeditor").replaceWith(data);
										}
								);	
							}
						},
						tolerance: 'pointer',
						over: outlineSelectionCol,
						out: unoutlineSelectionCol
					}
				);
			}
		); //category
	
		//Drop on column	
    	lQuery(".goals-column-container").livequery(
			function()
			{
				outlineSelectionCol = function(event, ui)
				{
					jQuery(this).addClass("dragoverselected");
				}
					
				unoutlineSelectionCol = function(event, ui)
				{
					jQuery(this).removeClass("dragoverselected");
				}
			
				jQuery(this).droppable(
					{
						drop: function(event, ui) 
						{
							var goalid = ui.draggable.data("goalid"); //Drag onto a category
							var column = $(this);
							var columnid = column.data("col");
							//Make sure we are not already inthis column
							if( columnid != undefined )
							{
								var params = {};
								params['goalid'] = goalid;
								params['col'] = columnid;
								
								jQuery.get(applink + "/collective/goals/drop/movetocolumn.html", params ,
										function(data) 
										{
											//Reload goalist
											var li = $(".projectgoals #goal" + goalid );
											li.detach();
											
											var ul = column.find("ul.projectgoals");
											li.prependTo(ul);
											
											$(".goals-column-container").removeClass(".dragoverselected");
											
										}
								);
							}	
						},
						tolerance: 'pointer',
						over: outlineSelectionCol,
						out: unoutlineSelectionCol
					}
				);
			}
		);
		
		
		 //Sort tasks
    	lQuery("#editgoal .card-task").livequery(
			function()
			{
				outlineSelectionCol = function(event, ui)
				{
					jQuery(this).addClass("dragoverselected");
				}
					
				unoutlineSelectionCol = function(event, ui)
				{
					jQuery(this).removeClass("dragoverselected");
				}
			
				console.log("initi droppable");
				
				jQuery(this).droppable(
					{
						drop: function(event, ui) 
						{
							console.log("dropped");
						
							var taskid = ui.draggable.data("taskid"); //Drag onto a category
							var card = $(this);
							var targettaskid = card.closest(".card-task").data("taskid");
							
							var params = $("#tasklist").data();
							params['taskid'] = taskid;
							params['targettaskid'] = targettaskid;
							
							jQuery.get(applink + "/collective/goals/drop/taskinsert.html", params ,
									function(data) 
									{
										//Reload goalist
										$("#tasklist").replaceWith(data);
									}
							);
						},
						tolerance: 'pointer',
						over: outlineSelectionCol,
						out: unoutlineSelectionCol
					}
				);
			}
		); //Sort goals
		
	} //droppable
	
	lQuery("#commentsave").livequery("click",function()
	{
		var comment = $(this);
		var path = comment.data("savepath");
		var taskid = comment.data("taskid");
		var params = comment.data();
		params['comment'] = $("#commenttext").val();
		
		jQuery.get(path, params, function(data) 
		{
			$("#commentsarea_"+ taskid).html(data);
		});
					
	});

	lQuery(".changetaskstatus").livequery(function()
	{
		var div = $(this);
		var select = div.find("select");
		select.on("change",function()
		{
			var val = select.val();
			if( val == "3")
			{
				var form = select.closest("form");
				form.trigger("submit");	
			}
		});
	});

	lQuery("#changestatus").livequery(function()
	{
		var div = $(this);
		var select = div.find("select");
		select.on("change",function()
		{
			var path = div.data("path");
			var params = {}; //div.data();
			params['taskstatus'] = select.val();
			params['collectionid'] = div.data("collectionid");
			params['oemaxlevel'] = "1";
			console.log(path,params);
			jQuery.get(path, params, function(data) 
			{
				var statusviewer = $("#statusviewer");
				statusviewer.replaceWith(data);
			});
		});
					
	});

	lQuery("select.picknewrolefortask").livequery(function()
	{
		var select = $(this);
		//var select = div.find("select");
		
		select.on("change",function()
		{
			var running = select.data("running");
			if( running )
			{
				return;
			}
			select.data("running",true);
			var target = select.closest(".goaltaskrow").find(".rolelist");
			var path = select.data("savepath");
			var params = {};//select.cleandata();
			params['collectiverole'] = select.val();
			params['collectionid'] = select.data("collectionid");
			params['taskid'] = select.data("taskid");
			console.log(path,params);
			jQuery.get(path, params, function(data) 
			{
				target.replaceWith(data);
				select.val("");
				select.trigger('change');
				select.data("running",false);
			});
		});
					
	});


	
	lQuery(".appendgoalbutton").livequery("click", function(e)
			{
				e.preventDefault();

				var button = $(this);
				var goalid = button.data("goalid");
				
				var href = button.attr("href");
				var options = button.data();
				var targetdiv = button.closest(".goaltaskcontainer");
				jQuery.ajax({
					url: href, 
					data: options, 
					success: function (data) 
					{
						$(targetdiv).append(data);
						jQuery('.grabfocus').focus();
						
						var parent = button.closest(".goalstatusopen");
						if (parent.length > 0) {
							parent[0].scrollIntoView();
						}

					},
					type: "POST",
					dataType: 'text',
				});
			});

	
});
