define([
	'jquery',
  'backbone',
	'app/models/Tutorial',
	'app/views/TutorialView'
    ], function($, Backbone, Tutorial, TutorialView) {
	TutorialCollection = Backbone.Collection.extend({
	model: Tutorial,
	url: base_url+"MetaServer",
	fetch: function(){
		var args = {
				command : "tutorial_user_get",
				user_id: Cure.Player.get('id')
		};
		var thisCollection = this;
		Cure.utils.showLoading(null);
		$.ajax({
			type : 'POST',
			url : this.url,
			data : JSON.stringify(args),
			dataType : 'json',
			contentType : "application/json; charset=utf-8",
			success : function(data){
				console.log(data);
				thisCollection.add(data);
				Cure.utils.hideLoading();
				//Cure.TutorialRegion.show(new TutorialView({model: Cure.TutorialCollection.findWhere({"id":"277089"})}));
			}
		});
	}
});
return TutorialCollection;
});