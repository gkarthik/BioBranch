define([
  //Libraries
	'jquery',
	'marionette',
	//Views
	'app/views/layouts/NodeView',
	'app/views/layouts/emptyLayout'
    ], function($, Marionette, NodeView, emptyLayout) {
NodeCollectionView = Marionette.CollectionView.extend({
	// -- View to manipulate and display list of all nodes in collection
	itemView : NodeView,
	emptyView : emptyLayout,
	condensed: false,
	setCondensed: function(val, args){
		this.condensed = val;
		var silent = args.silent || false;
		if(!silent){
			Cure.vent.trigger("condensed:changed");
		}
		if(this.condensed){
			Cure.vent.trigger("condensed:true");
		} else {
			Cure.vent.trigger("condensed:false");
		}
		Cure.utils.updatepositions(Cure.PlayerNodeCollection);
		Cure.utils.render_network();
	},
	itemViewOptions : function () { return { condensed: this.condensed } },
	initialize : function() {
		var _this = this;
		$(window).resize(function(){
			Cure.width = window.innerWidth - 365;
			Cure.appLayout.ui.PlayerTreeRegion.width(Cure.width);
			Cure.PlayerSvgWrapper.attr("width", Cure.width);
			Cure.cluster.size([ (Cure.width-10), "auto" ]);
			Cure.vent.trigger("window:resized");
			Cure.utils.updatepositions(Cure.PlayerNodeCollection);
			Cure.utils.render_network();
		});
	}
});

return NodeCollectionView;
});
