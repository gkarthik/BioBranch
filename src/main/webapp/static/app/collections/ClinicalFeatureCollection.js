define([
	'jquery',
  'backbone',
	'app/models/ClinicalFeature'
    ], function($, Backbone, ClinicalFeature) {
ClinicalFeatureCollection = Backbone.Collection.extend({
	model: ClinicalFeature,
	url: base_url+'MetaServer',
	initialize: function(){
		_.bindAll(this, 'parseResponse');
	},
	fetch: function(){
		var args = {
				command : "get_clinical_features",
				dataset : Cure.dataset
		};
		$.ajax({
			type : 'POST',
			url : this.url,
			data : JSON.stringify(args),
			dataType : 'json',
			contentType : "application/json; charset=utf-8",
			success : this.parseResponse,
			error : this.error,
		});
	},
	parseResponse : function(data) {
		if(data.length > 0) {
			this.add(data);
		}
	},
	error : function(data) {

	}
});

return ClinicalFeatureCollection;
});
