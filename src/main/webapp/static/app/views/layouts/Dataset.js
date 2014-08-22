define([
	'jquery',
	'marionette',
	//Views
	'app/views/DatasetCollectionView',
	//Templates
	'text!static/app/templates/Dataset.html'
    ], function($, Marionette, DatasetCollectionView, DatasetTmpl) {
DatasetLayout = Marionette.Layout.extend({
   template: DatasetTmpl,
   regions: {
	   TestSetsRegion: ".show-testing-sets"
   },
   dataset: [],
   events: {
	   'click input[name="testOptions"]': 'checkVal',
	   'click #show-dataset-wrapper': 'showWrapper',
	   'click #apply-options': 'applyOptions'
   },
   url: base_url+'MetaServer',
   ui:{
	 wrapper: "#dataset-wrapper",
	 showWrapper: "#show-dataset-wrapper",
	 TestSets: '.show-testing-sets'
   },
   checkVal: function(){
	   if($($("input[name='testOptions']")[1]).is(":checked")){
		   $(this.ui.TestSets).show();
		   Cure.TestSets.at(0).set('setTest',true);
	   } else {
		   $(this.ui.TestSets).hide();
	   }
   },
   showWrapper: function(){
	   $(this.ui.wrapper).show();
	   this.checkVal();
	   var args = {
				command : "get_dataset_training",
				dataset: Cure.dataset
			};
		//POST request to server.
		$.ajax({
			type : 'POST',
			url : this.url,
			data : JSON.stringify(args),
			dataType : 'json',
			contentType : "application/json; charset=utf-8",
			success : function(data){
				Cure.TestSets.reset();
				Cure.TestSets.add(data);
			},
			error : function(data){
				console.log(data);
			}
		});
   },
   applyOptions: function(){
	   Cure.PlayerNodeCollection.sync();
   },
   onRender: function(){
	   this.TestSetsRegion.show(new DatasetCollectionView({collection: Cure.TestSets}));
   }
});
return DatasetLayout;
});
