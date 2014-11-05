define([
	'jquery',
	'marionette',
	//Models
	'app/models/GeneItem',
	//Collections
	'app/collections/GeneCollection',
	//Views
	'app/views/GeneListCollectionView',
	//Layouts
	'app/views/layouts/AttributeRankerLayout',
	'app/views/layouts/PathwaySearchLayout',
	'app/views/AddNodeCustomClassifierView',
	//Templates
	'text!static/app/templates/AggregateNodeLayout.html',
	'text!static/app/templates/GeneSummary.html',
	'text!static/app/templates/ClinicalFeatureSummary.html',
	//Plugins
	'myGeneAutocomplete',
	'jqueryui'
    ], function($, Marionette, GeneItem, GeneCollection, GeneCollectionView, AttrRankLayout, PathwaySearchLayout, searchFeatureView, AggNodeTmpl, geneinfosummary, cfsummary) {
AggNodeLayout = Marionette.Layout.extend({
    template: AggNodeTmpl,
    className: "panel panel-default",
    initialize: function(){
    	_.bindAll(this,'buildAggNode');
    	this.listenTo(this,'onBeforeDestroy',this.destroyAllCollections);
    },
    destroyAllCollections: function(){
    	this.newGeneCollection.destroy();
    },
    url: base_url+"MetaServer",
    ui: {
    	nameInput: "#name_input",
    	descInput: "#desc_input",
    	classifierType: 'input:radio[name=classifierType]',
    	buildAggNode: '.build-aggnode',
    	msgWrapper: '.message-wrapper',
    	duplicateClassifier: '.duplicate-customclassifier-wrapper',
    	customfeature_query: "#add_customfeature_to_aggNode_query",
    	
    },
    events: {
    	'click .close-aggnode': 'closeAggNode',
    	'click .build-aggnode': 'sendRequest',
    	'click .duplicate_customclassifier':'addDuplicateEntry',
    	'click .open-pathway-search': 'openPathwaySearch',
    	'click .open-attr-rank': 'openAttrRanker'
    },
    regions: {
      GeneCollectionRegion: "#Aggnode_GeneCollectionRegion",
      searchFeaturesRegion: ".searchFeatureRegion"
    },
    openAttrRanker: function(){
    	Cure.appLayout.AttrRankRegion.close();
		Cure.appLayout.AttrRankRegion.show(new AttrRankLayout({model: this.model, aggNode: true}));
    },
    closeAggNode: function(){
    	Cure.appLayout.AggNodeRegion.close();
    },
	openPathwaySearch: function(){
		Cure.PathwaySearchLayout = new PathwaySearchLayout({aggNode: true});
		Cure.appLayout.PathwaySearchRegion.show(Cure.PathwaySearchLayout);
	},
	addToGeneCollection: function(models){
		this.newGeneCollection.add(models);
	},
    onRender: function(){
    	this.newGeneCollection = new GeneCollection();
    	var newGeneView = new GeneCollectionView({collection:this.newGeneCollection});
    	this.GeneCollectionRegion.show(newGeneView);
    	this.searchFeaturesRegion.show(new searchFeatureView({
    		model: this.model,
    		view: "aggNode"
    	}));
    },
    sendRequest: function(){
    	var uniqueIds = this.newGeneCollection.pluck("unique_id");
    	uniqueIds.splice(0,1);//Removing "Short Name"
    	if($(this.ui.nameInput).val()!="" && $(this.ui.descInput).val()!="" && uniqueIds.length>0){
    		$(this.ui.buildAggNode).val("Building classifier ... ");
    		$(this.ui.buildAggNode).addClass("disabled");
    		var args = {
        	        command : "custom_classifier_create",
        	        unique_ids: uniqueIds,
        	        name: $(this.ui.nameInput).val(),
        	        description: $(this.ui.descInput).val(),
        	        user_id: Cure.Player.get("id"),
        	        type: parseInt($(this.ui.classifierType).filter(":checked").val()),
        	        dataset: Cure.dataset.get('id')
        	      };
        	console.log(args);
        	      $.ajax({
        	          type : 'POST',
        	          url : this.url,
        	          data : JSON.stringify(args),
        	          dataType : 'json',
        	          contentType : "application/json; charset=utf-8",
        	          success : this.buildAggNode,
        	          error: this.error
        	        });
    	}
    },
    buildAggNode: function(data){
    	$(this.ui.buildAggNode).removeClass("disabled");
		$(this.ui.buildAggNode).val("Build Classifier");
			if(data.exists==true){
				$(this.ui.msgWrapper).html("<span class='text-danger error-message'>"+data.message+"</span><br><p class='bg-info duplicate_customclassifier' data-name='"+data.name+"' data-description='"+data.description+"' data-id='"+data.id+"'>"+data.name+": "+data.description+"</p>");
			} else {
				this.addCustomClassifier(data);
			}
    },
    addDuplicateEntry: function(e){
    	var data = $(e.target).data();
    	console.log(data);
    	this.addCustomClassifier(data);
    },
    addCustomClassifier: function(data){
    	var kind_value = "";
    	var model = this.model;
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
				model.set("name", data.name);
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
					"unique_id" : "custom_classifier_new_"+data.id,
					"kind" : "split_node",
					"full_name" : '',
					"description" : data.description
				});
			} else {
				new Node({
					'name' : data.name,
					"options" : {
						"unique_id" : "custom_classifier_new_"+data.id,
						"kind" : "split_node",
						"full_name" : '',
						"description" : data.description
					}
				});
			}
			Cure.PlayerNodeCollection.sync();
			this.closeAggNode();
    },
    error : function(data) {
		Cure.utils
    .showAlert("<strong>Server Error</strong><br>Please try saving again in a while.", 0);
	}
});

return AggNodeLayout;
});
