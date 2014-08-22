define([
  //Libraries
	'jquery',
	'marionette',
	//Views
	'app/views/DatasetItemView'
    ], function($, Marionette, DatasetItemView) {
DatasetCollectionView = Marionette.CollectionView.extend({
	itemView : DatasetItemView,
});

return DatasetCollectionView;
});
