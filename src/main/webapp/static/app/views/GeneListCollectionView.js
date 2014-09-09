define([
  //Libraries
	'jquery',
	'marionette',
	//Views
	'app/views/GeneListItemView'
    ], function($, Marionette, GeneItemView) {
GeneCollectionView = Marionette.CollectionView.extend({
	itemView : GeneItemView,
	tagName: 'table',
	className: 'table',
	initialize: function(args){
		this.options = args.options;
	},
    itemViewOptions: function(model,index){
        return this.options;
    }
});

return GeneCollectionView;
});
