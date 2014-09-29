define([
    	'backbone',
    	'backboneRelational'
    ], function(Backbone) {
Tutorial = Backbone.RelationalModel.extend({
	defaults: {
		title: "",
		description: "",
		id: "",
		completed: ""
	},
	url: base_url+"MetaServer",
	markComplete: function(){
		var args = {
				command : "tutorial_user_add",
				user_id: Cure.Player.get('id'),
				tutorial_id: this.get('id')
		};
		var thisModel = this;
		Cure.utils.showLoading(null);
		$.ajax({
			type : 'POST',
			url : this.url,
			data : JSON.stringify(args),
			dataType : 'json',
			contentType : "application/json; charset=utf-8",
			success : function(data){
				console.log(data);
				Cure.utils.hideLoading();
			}
		});
	}
});
return Tutorial;
});
