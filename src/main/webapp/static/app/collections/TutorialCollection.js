define([
	'jquery',
  'backbone',
	'app/models/Tutorial'
    ], function($, Backbone, Tutorial) {
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
			}
		});
	}
});
return TutorialCollection;
});