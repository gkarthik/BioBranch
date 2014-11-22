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
	setCondensed: function(val){
		this.condensed = val;
		if(this.condensed){
			Cure.vent.trigger("condensed:true");
		} else {
			Cure.vent.trigger("condensed:false");
		}
	},
	itemViewOptions : function () { return { condensed: this.condensed } },
	initialize : function() {
		
	}
});

return NodeCollectionView;
});
