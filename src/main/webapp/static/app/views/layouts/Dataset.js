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
	   $("input[name='percent-split']").val("");
	   if($($("input[name='testOptions']")[1]).is(":checked")){
		   $(this.ui.TestSets).show();
		   Cure.TestSets.at(0).set('setTest',true);
	   } else {
		   $(this.ui.TestSets).hide();
	   }
	   if($($("input[name='testOptions']")[2]).is(":checked")){
		   $("input[name='percent-split']").val(66);
	   }
   },
   setTestSetLabel: function(){
	   var el = $("#test-set-label");
	   switch($("input[name='testOptions']:checked").val()) {
		case "0":
			el.html(Cure.dataset.get('name'));
			break;
		case "1":
			el.html(Cure.TestSets.findWhere({setTest:true}).get('name'));
			break;
		case "2": 
			el.html("Training set split - "+$("input[name='percent-split']").val()+"%");
			break;
	   }
   },
   showWrapper: function(){
	   $(this.ui.wrapper).show();
	   this.checkVal();
	   var args = {
				command : "get_dataset_training",
				dataset: Cure.dataset.get('id')
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
	   this.setTestSetLabel();
	   Cure.PlayerNodeCollection.sync();
   },
   onRender: function(){
	   this.TestSetsRegion.show(new DatasetCollectionView({collection: Cure.TestSets}));
   }
});
return DatasetLayout;
});
