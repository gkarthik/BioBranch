define([
  //Libraries
	'jquery',
	'marionette',
	//Views
	'app/views/GeneItemView'
    ], function($, Marionette, GeneItemView) {
GeneCollectionView = Marionette.CollectionView.extend({
	itemView : GeneItemView,
	tagName: 'table',
	className: 'table table-condensed',
	events: {
		'click .keepAll': 'selectAllGenes',
	},
	ui: {
		'keepAll': '.keepAll'
	},
	selectAllGenes: function(){
		if($(this.ui.keepAll).is(':checked')){
			this.collection.at(0).set('keepAll',1);
			this.collection.forEach(function(model, index) {
				if(model.get('unique_id')!="Unique ID"){
			    model.set('keepInCollection', 1);
				}
			});
		} else {
			this.collection.at(0).set('keepAll',0);
			this.collection.forEach(function(model, index) {
		    model.set('keepInCollection', 0);
			});
		}
	},
});

return GeneCollectionView;
});
