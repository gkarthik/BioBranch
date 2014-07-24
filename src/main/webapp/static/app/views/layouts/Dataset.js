define([
	'jquery',
	'marionette',
	//Views
	//Templates
	'text!static/app/templates/Dataset.html'
    ], function($, Marionette, DatasetTmpl) {
DatasetLayout = Marionette.Layout.extend({
   template: DatasetTmpl,
   regions: {
	   
   },
   events: {
	   'click #show-dataset-wrapper': 'showWrapper',
	   'click #apply-options': 'applyOptions'
   },
   ui:{
	 wrapper: "#dataset-wrapper",
	 showWrapper: "#show-dataset-wrapper"
   },
   showWrapper: function(){
	   $(this.ui.wrapper).show();
   },
   applyOptions: function(){
	   var val = $("input[name='testOptions']:checked").val();
	   var args = {
			   value: val
	   };
	   switch(parseInt(val)){
	   	 case 2:
		   		args.percentSplit = $("input[name='percent-split']").val();
		   		 break;
	   }
	   console.log(args);
	   Cure.PlayerNodeCollection.sync(args);
   },
   onRender: function(){
	   
   },
   onShow: function(){
	   
   }
});
return DatasetLayout;
});
