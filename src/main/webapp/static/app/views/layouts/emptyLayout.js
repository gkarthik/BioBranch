define([
	'jquery',
	'marionette',
	//Views
	'app/views/AddRootNodeView',
	'app/views/TutorialView',
	//Templates
	'text!static/app/templates/EmptyNodeCollection.html'
    ], function($, Marionette, AddRootNodeView, TutorialView, EmptyNodeCollectionTemplate) {
emptyLayout = Marionette.Layout.extend({
    template: EmptyNodeCollectionTemplate,
    ui:{
    	"dropRootNode": "#drop-root-node"
    },
    regions: {
      AddRootNode: "#AddRootNodeWrapper"
    },
    onRender: function(){
    	if(!Cure.helpText){
        	Cure.helpText = $("#HelpText").html();	
        	Cure.utils.ToggleHelp(true, Cure.helpText);
    	}
    	this.AddRootNode.on("show", function(view){
    		window.setTimeout(function(){
    			Cure.initTour.init();
    			if(Cure.startTour){
        			Cure.initTour.start();
    			}
    		},600);
    	});
    	this.AddRootNode.show(new AddRootNodeView());
  		$(this.ui.dropRootNode).droppable({
				accept: ".gene-pool-item",
				activeClass: "genepool-drop-active",
				hoverClass: "genepool-drop-hover",
				drop: function( event, ui ) {
					var uidata = {};
					uidata.short_name = $(ui.draggable).data("shortname");
					uidata.long_name = $(ui.draggable).data("longname");
					uidata.unique_id = String($(ui.draggable).data("uniqueid"));
					console.log(uidata);
					new Node({
						'name' : uidata.short_name,
						"options" : {
							"unique_id" : uidata.unique_id,
							"kind" : "split_node",
							"full_name" : uidata.long_name
						}
					});
					Cure.PlayerNodeCollection.sync();
				}
			});
    }
});
return emptyLayout;
});
