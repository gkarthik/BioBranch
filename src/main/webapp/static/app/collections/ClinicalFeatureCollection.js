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
		 var testOptions = {
  				value: $("input[name='testOptions']:checked").val(),
  				percentSplit:  $("input[name='percent-split']").val()
  		};
          var tree = {};
          if(Cure.PlayerNodeCollection.length>0){
          	tree = Cure.PlayerNodeCollection.at(0).toJSON();
          }
          var args = {
  				command : "get_clinical_features",
  				dataset : Cure.dataset.get('id'),
  				treestruct : tree,
  				comment: Cure.Comment.get("content"),
  				testOptions: testOptions
  		};
		Cure.utils.showLoading(null);
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
		var requiredModel = Cure.PlayerNodeCollection.findWhere({pickInst: true});
		if(requiredModel){
			requiredModel.set('pickInst',false);
		}
		Cure.utils.hideLoading();
		if(data.length > 0) {
			this.reset(data);
		}
	},
	error : function(data) {

	}
});

return ClinicalFeatureCollection;
});
