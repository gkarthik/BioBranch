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
		'click .set-upper-limit': 'setUpperLimit',
		'click .set-lower-limit': 'setLowerLimit'
	},
	ui: {
		'keepAll': '.keepAll',
		'keepInCollection': '.keepInCollection',
		'uLimit': '.upperLimit',
		'lLimit': '.lowerLimit',
		'slider': '.slider-limit',
		'sliderRange': '.slider-range',
		'setUpperLimit': '.set-upper-limit',
		'sliderUpperLimit': '.slider-upper-limit-wrapper',
		'setLowerLimit': '.set-lower-limit',
		'sliderLowerLimit': '.slider-lower-limit-wrapper'
	},
	url: base_url+"MetaServer",
	setUpperLimit: function(){
		if($(this.ui.setUpperLimit).is(':checked')){
			$(this.ui.sliderUpperLimit).show();
			this.model.set('setUpperLimit',true);
		} else {
			$(this.ui.sliderUpperLimit).hide();
			this.model.set('setUpperLimit',false);
		}
	},
	setLowerLimit: function(){
		if($(this.ui.setLowerLimit).is(':checked')){
			$(this.ui.sliderLowerLimit).show();
			this.model.set('setLowerLimit',true);
		} else {
			$(this.ui.sliderLowerLimit).hide();
			this.model.set('setLowerLimit',false);
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
		 $(this.ui.sliderLowerLimit).slider({
			 range: "max",
			 step: parseFloat(thisView.model.get('uLimit')-thisView.model.get('lLimit'))/1000,
			 min: thisView.model.get('lLimit'),
			 max: thisView.model.get('uLimit'),
			 value: thisView.model.get('lLimit'),
			 slide: function( event, ui ) {
				 if(ui.value>=thisView.model.get('uLimit')){
					this.slider("value",thisView.model.get('lLimit'));
				 } else {
					 thisView.model.set('lLimit',ui.value, {'silent':true});
					 $(thisView.ui.lLimit).html(ui.value);
				 }
			 }
		 });
		 $(this.ui.sliderUpperLimit).slider({
			 range: "min",
			 step: parseFloat(thisView.model.get('uLimit')-thisView.model.get('lLimit'))/1000,
			 min: thisView.model.get('lLimit'),
			 max: thisView.model.get('uLimit'),
			 value: thisView.model.get('uLimit'),
			 slide: function( event, ui ) {
				 if(ui.value<=thisView.model.get('lLimit')){
						this.slider("value",thisView.model.get('uLimit'));
					 } else {
						 thisView.model.set('uLimit',ui.value, {'silent':true});
						 $(thisView.ui.uLimit).html(ui.value);
					 }
			 }
		 });
	},
	deleteThisItem: function(){
		this.model.destroy();
	}
});

return GeneItemView;
});
