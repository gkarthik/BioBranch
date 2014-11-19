define([
	'jquery',
	'marionette',
	//Views
	'app/views/DatasetCollectionView',
	'app/views/layouts/appLayout',
	//Templates
	'text!static/app/templates/Dataset.html'
    ], function($, Marionette, DatasetCollectionView, appLayout, DatasetTmpl) {
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
	 TestSets: '.show-testing-sets',
	 error: "#error-msg"
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
   setTestSet: function(){
	   switch($("input[name='testOptions']:checked").val()) {
		case "0":
			Cure.TestDataset = Cure.dataset;
			break;
		case "1":
			Cure.TestDataset = Cure.TestSets.findWhere({setTest:true});
			break;
		case "2": 
			Cure.TestDataset = Cure.dataset;
			Cure.TestDataset.set('split', true);
			Cure.TestDataset.set('splitPercentage', $("input[name='percent-split']").val());
			break;
	   }
   },
   showWrapper: function(){
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
   validateValues: function(){
	   if(!Cure.TestDataset){
			 $(this.ui.error).html("Please choose a test dataset.");
			 return false;
		 }   
	 if(!Cure.TestDataset.get('name')){
		 $(this.ui.error).html("Please choose a test dataset.");
		 return false;
	 }  
	 if(Cure.TestDataset.get('split')){
		 if(!Cure.TestDataset.get('splitPercentage')){
			 $(this.ui.error).html("Please specify a split %");
			 return false;
		 }
		 if(Cure.TestDataset.get('splitPercentage')<=0 || Cure.TestDataset.get('splitPercentage') >=100){
			 $(this.ui.error).html("Please specify a split % between 1 and 99");
			 return false;
		 }
	 }
	 return true;
   },
   applyOptions: function(){
	   this.setTestSet();
	   if(this.validateValues()){
		   this.close();
		   Cure.appLayout = new appLayout();
		   Cure.appRegion.show(Cure.appLayout);
	   }
   },
   onRender: function(){
	   Cure.TestDataset = Cure.dataset;//Since Training is default
	   this.TestSetsRegion.show(new DatasetCollectionView({collection: Cure.TestSets}));
	   this.showWrapper();
   }
});
return DatasetLayout;
});
