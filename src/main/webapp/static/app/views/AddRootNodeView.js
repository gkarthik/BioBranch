define([
  //Libraries
	'jquery',
	'marionette',
	'app/views/SearchFeatures',
	//Plugins
	'myGeneAutocomplete',
	'jqueryui'
    ], function($, Marionette, searchFeature) {
AddRootNodeView = searchFeature.extend({
	selectTree:  function( event, ui ) {
		var model = this.model;
			if(ui.item.label != undefined){//To ensure "no gene name has been selected" is not accepted.
					if(!Cure.initTour.ended()){
						Cure.initTour.end();
					}
					$("#SpeechBubble").remove();
					var kind_value = "";
					var name_node = ui.item.data.user.firstName+" | Tree ID: "+ui.item.data.id;
					try {
						kind_value = model.get("options").get('kind');
					} catch (exception) {
					}
					if (kind_value == "leaf_node") {
							if(model.get("options")){
								model.get("options").unset("split_point");
							}
							
							if(model.get("distribution_data")){
								model.get("distribution_data").set({
									"range": -1
								});
							}
						model.set("previousAttributes", model.toJSON());
						model.set("name", name_node);
						model.set('accLimit', 0, {silent:true});
						
						var index = Cure.CollaboratorCollection.pluck("id").indexOf(Cure.Player.get('id'));
						var newCollaborator;
						if(index!=-1){
							newCollaborator = Cure.CollaboratorCollection.at(index);
						} else {
							newCollaborator = new Collaborator({
								"name": cure_user_name,
								"id": Cure.Player.get('id'),
								"created" : new Date()
							});
							Cure.CollaboratorCollection.add(newCollaborator);
							index = Cure.CollaboratorCollection.indexOf(newCollaborator);
						}
						model.get("options").set({
							"unique_id" : "custom_tree_"+ui.item.data.id,
							"kind" : "split_node",
							"full_name" : '',
							"description" : name_node+ "| Created: "+ui.item.data.created.hourOfDay+":"+ui.item.data.created.minuteOfHour+" "+ui.item.data.created.monthOfYear+"/"+ui.item.data.created.dayOfMonth+"/"+ui.item.data.created.yearOfEra
						});
					} else {
						new Node({
							'name' : name_node,
							"options" : {
								"unique_id" : "custom_tree_"+ui.item.data.id,
								"kind" : "split_node",
								"full_name" : '',
								"description" : name_node+ "| Created: "+ui.item.data.created.hourOfDay+":"+ui.item.data.created.minuteOfHour+" "+ui.item.data.created.monthOfYear+"/"+ui.item.data.created.dayOfMonth+"/"+ui.item.data.created.yearOfEra
							}
						});
					}
					Cure.PlayerNodeCollection.sync();
				}
			},
	selectCustomClassifier: function( event, ui ) {
			if(ui.item.label != undefined){//To ensure "no gene name has been selected" is not accepted.
					if(!Cure.initTour.ended()){
						Cure.initTour.end();
					}
					$("#SpeechBubble").remove();
					var model = this.model;
					var kind_value = "";
					try {
						kind_value = model.get("options").get('kind');
					} catch (exception) {
					}
					if (kind_value == "leaf_node") {
							if(model.get("options")){
								model.get("options").unset("split_point");
							}
							
							if(model.get("distribution_data")){
								model.get("distribution_data").set({
									"range": -1
								});
							}
						model.set("previousAttributes", model.toJSON());
						model.set("name", ui.item.data.name);
						model.set('accLimit', 0, {silent:true});
						
						var index = Cure.CollaboratorCollection.pluck("id").indexOf(Cure.Player.get('id'));
						var newCollaborator;
						if(index!=-1){
							newCollaborator = Cure.CollaboratorCollection.at(index);
						} else {
							newCollaborator = new Collaborator({
								"name": cure_user_name,
								"id": Cure.Player.get('id'),
								"created" : new Date()
							});
							Cure.CollaboratorCollection.add(newCollaborator);
							index = Cure.CollaboratorCollection.indexOf(newCollaborator);
						}
						model.get("options").set({
							"unique_id" : "custom_classifier_new_"+ui.item.data.id,
							"kind" : "split_node",
							"full_name" : '',
							"description" : ui.item.data.description
						});
					} else {
						new Node({
							'name' : ui.item.data.name,
							"options" : {
								"unique_id" : "custom_classifier_new_"+ui.item.data.id,
								"kind" : "split_node",
								"full_name" : '',
								"description" : ui.item.data.description
							}
						});
					}
					Cure.PlayerNodeCollection.sync();
				}
			},
	selectCustomFeature: function( event, ui ) {
			if(ui.item.label != undefined){//To ensure "no gene name has been selected" is not accepted.
					if(!Cure.initTour.ended()){
						Cure.initTour.end();
					}
					$("#SpeechBubble").remove();
					var model = this.model;
					var kind_value = "";
					try {
						kind_value = model.get("options").get('kind');
					} catch (exception) {
					}
					if (kind_value == "leaf_node") {
							if(model.get("options")){
								model.get("options").unset("split_point");
							}
							
							if(model.get("distribution_data")){
								model.get("distribution_data").set({
									"range": -1
								});
							}
						model.set("previousAttributes", model.toJSON());
						model.set("name", ui.item.data.name);
						model.set('accLimit', 0, {silent:true});
						
						var index = Cure.CollaboratorCollection.pluck("id").indexOf(Cure.Player.get('id'));
						var newCollaborator;
						if(index!=-1){
							newCollaborator = Cure.CollaboratorCollection.at(index);
						} else {
							newCollaborator = new Collaborator({
								"name": cure_user_name,
								"id": Cure.Player.get('id'),
								"created" : new Date()
							});
							Cure.CollaboratorCollection.add(newCollaborator);
							index = Cure.CollaboratorCollection.indexOf(newCollaborator);
						}
						model.get("options").set({
							"unique_id" : "custom_feature_"+ui.item.data.id,
							"kind" : "split_node",
							"full_name" : '',
							"description" : ui.item.data.description
						});
					} else {
						new Node({
							'name' : ui.item.data.name,
							"options" : {
								"unique_id" : "custom_feature_"+ui.item.data.id,
								"kind" : "split_node",
								"full_name" : '',
								"description" : ui.item.data.description
							}
						});
					}
					Cure.PlayerNodeCollection.sync();
				}
			},
	selectCf: function(event, ui) {
		if(ui.item.long_name != undefined){//To ensure "no gene name has been selected" is not accepted.
			if(!Cure.initTour.ended()){
				Cure.initTour.end();
			}
			$("#SpeechBubble").remove();
			var model = this.model;
			var kind_value = "";
			try {
				kind_value = model.get("options").get('kind');
			} catch (exception) {
			}
			if (kind_value == "leaf_node") {
					if(model.get("options")){
						model.get("options").unset("split_point");
					}
					
					if(model.get("distribution_data")){
						model.get("distribution_data").set({
							"range": -1
						});
					}
				model.set("previousAttributes", model.toJSON());
				model.set("name", ui.item.short_name.replace(/_/g," "));
				model.set('accLimit', 0, {silent:true});
				
				var index = Cure.CollaboratorCollection.pluck("id").indexOf(Cure.Player.get('id'));
				var newCollaborator;
				if(index!=-1){
					newCollaborator = Cure.CollaboratorCollection.at(index);
				} else {
					newCollaborator = new Collaborator({
						"name": cure_user_name,
						"id": Cure.Player.get('id'),
						"created" : new Date()
					});
					Cure.CollaboratorCollection.add(newCollaborator);
					index = Cure.CollaboratorCollection.indexOf(newCollaborator);
				}
				model.get("options").set({
					"unique_id" : ui.item.unique_id,
					"kind" : "split_node",
					"full_name" : ui.item.long_name,
					"description" : ui.item.long_name
				});
			} else {
				var newNode = new Node({
					'name' : ui.item.short_name.replace(/_/g," "),
					"options" : {
						"unique_id" : ui.item.unique_id,
						"kind" : "split_node",
						"full_name" : ui.item.long_name,
						"description" : ui.item.description,
					}
				});
			}
			Cure.PlayerNodeCollection.sync();
		}
	},
	selectGene : function(event, ui) {
		if(ui.item.name != undefined){//To ensure "no gene name has been selected" is not accepted.
			if(!Cure.initTour.ended()){
				Cure.initTour.end();
			}
			$("#SpeechBubble").remove();
			var model = this.model;
			var kind_value = "";
			try {
				kind_value = model.get("options").get('kind');
			} catch (exception) {
			}

			if (kind_value == "leaf_node") {
				if(model.get("options")){
					model.get("options").unset("split_point");
				}
				
				if(model.get("distribution_data")){
					model.get("distribution_data").set({
						"range": -1
					});
				}
				model.set("previousAttributes", model.toJSON());
				model.set("name", ui.item.symbol);
				model.get("options").set({
					"unique_id" : ui.item.entrezgene,
					"kind" : "split_node",
					"full_name" : ui.item.name
				});
			} else {
				var newNode = new Node({
					'name' : ui.item.symbol,
					"options" : {
						"unique_id" : ui.item.entrezgene,
						"kind" : "split_node",
						"full_name" : ui.item.name
					}
				});
			}
			Cure.PlayerNodeCollection.sync();
		}
	}
});

return AddRootNodeView;
});
