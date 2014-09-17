define([
  //Libraries
	'jquery',
	'marionette',
	'backbone',
	//Templates
	'text!static/app/templates/GeneListItem.html',
	'jqueryui'
    ], function($, Marionette, Backbone, GeneItemTmpl) {
GeneItemView = Marionette.ItemView.extend({
	tagName: 'tr',
	events: {
		'click .delete': 'deleteThisItem',
		'change .upperLimit': 'setUpperLimit',
		'change .lowerLimit': 'setLowerLimit',
		'click .setlimit': 'setLimit'
	},
	url: base_url+"MetaServer",
	setLowerLimit: function(){
		this.model.set('lLimit', $(this.ui.lLimit).val());
	},
	setUpperLimit: function(){
		this.model.set('uLimit', $(this.ui.uLimit).val());
	},
	setLimit: function(){
		console.log($(this.ui.setLimitInput));
		if($(this.ui.setLimitInput).is(':checked')){
			$(this.ui.sliderLimitWrapper).show();
			this.model.set('setLimit',true);
		} else {
			$(this.ui.sliderLimitWrapper).hide();
			this.model.set('setLimit',false);
		}
	},
	showLimit: false,
	initialize: function(args){
		this.listenTo(this.model,'change:uLimit',this.render);
		this.listenTo(this.model,'change:lLimit',this.render);
		if(args.showLimits){
			this.getLimits();
			this.showLimits = args.showLimits;
		}
	},
	getLimits: function(){
		var thisView = this;
		var args = {
    	        command : "get_feature_limits",
    	        unique_id: this.model.get('unique_id'),
    	        dataset: Cure.dataset.get('id')
    	      };
		if(this.model.get('unique_id')!="Unique ID"){
			$.ajax({
  	          type : 'POST',
  	          url : this.url,
  	          data : JSON.stringify(args),
  	          dataType : 'json',
  	          contentType : "application/json; charset=utf-8",
  	          success : function(data){
  	        	  console.log(data);
  	        	  thisView.model.set('lLimit',data.lLimit);
  				  thisView.model.set('uLimit',data.uLimit);
  	          },
  	          error: this.error
			});
		}
	},
	error : function(data) {
		Cure.utils
    .showAlert("<strong>Server Error</strong><br>Please try saving again in a while.", 0);
	},
	ui: {
		'keepAll': '.keepAll',
		'keepInCollection': '.keepInCollection',
		'uLimit': '.upperLimit',
		'lLimit': '.lowerLimit',
		'slider': '.slider-limit',
		'sliderRange': '.slider-range',
		setLimitInput: '.setlimit',
		sliderLimitWrapper: '.slider-limit-wrapper'
	},
	template: function(serialized_model){
		return GeneItemTmpl(serialized_model);
	},
	templateHelpers: function(){
		return {
		   	showLimits: this.showLimits
		}
	},
	onRender: function(){
		var thisView = this;
		 $(this.ui.slider).slider({
			 range: true,
			 step: parseFloat(thisView.model.get('uLimit')-thisView.model.get('lLimit'))/1000,
			 min: thisView.model.get('lLimit'),
			 max: thisView.model.get('uLimit'),
			 values: [ thisView.model.get('lLimit'), thisView.model.get('uLimit') ],
			 slide: function( event, ui ) {
				 thisView.model.set('lLimit',ui.values[0], {'silent':true});
				 thisView.model.set('uLimit',ui.values[1], {'silent':true});
				 $(thisView.ui.uLimit).html(ui.values[1]);
				 $(thisView.ui.lLimit).html(ui.values[0]);
			 }
		 });
	},
	deleteThisItem: function(){
		this.model.destroy();
	}
});

return GeneItemView;
});
