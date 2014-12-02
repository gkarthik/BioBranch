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
		
	}
});

return NodeCollectionView;
});
