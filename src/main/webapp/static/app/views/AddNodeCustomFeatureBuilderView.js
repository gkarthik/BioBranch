define([
  //Libraries
	'jquery',
	'marionette',
	'app/views/SearchFeatures',
	//Plugins
	'myGeneAutocomplete',
	'jqueryui'
    ], function($, Marionette, searchFeature) {
	AddNodeCustomClassifier = searchFeature.extend({
	selectTree: function(event, ui){},
	selectCustomCLassifier: function(event, ui){},
	selectCustomFeature: function( event, ui ) {
		var thisView = Cure.appLayout.FeatureBuilderRegion.currentView;
			if(ui.item.label != undefined && !thisView.setReference){//To ensure "no gene name has been selected" is not accepted.
					if(!Cure.initTour.ended()){
						Cure.initTour.end();
					}
					$("#SpeechBubble").remove();
					thisView.geneColl.add([{
						unique_id: "custom_feature_"+ui.item.data.id,
						short_name: ui.item.data.name.toUpperCase(),
						long_name: ui.item.data.name
					}]);
//					 else {
//						new Node({
//							'name' : ui.item.data.name,
//							"options" : {
//								"unique_id" : "custom_feature_"+ui.item.data.name,
//								"kind" : "split_node",
//								"full_name" : '',
//								"description" : ui.item.data.description
//							}
//						});
//					}
				} else if (ui.item.label != undefined && thisView.setReference){
					$(thisView.ui.refDetails).html(ui.item.data.name.toUpperCase());
					$(thisView.ui.refDetails).data('ref_id', "custom_feature_"+ui.item.data.id);
				}
				$(this).val(''); 
				return false;
			},
	selectCf: function(event, ui) {
		var thisView = Cure.appLayout.FeatureBuilderRegion.currentView;
		if(ui.item.long_name != undefined && !thisView.setReference){//To ensure "no gene name has been selected" is not accepted.
			$("#SpeechBubble").remove();
			thisView.geneColl.add([{
					unique_id: ui.item.unique_id,
					short_name: ui.item.short_name.replace(/_/g," ").toUpperCase(),
					long_name: ui.item.long_name
				}]);
//			else {
//				var newNode = new Node({
//					'name' : ui.item.short_name.replace(/_/g," "),
//					"options" : {
//						"unique_id" : ui.item.unique_id,					if(!Cure.initTour.ended()){
//						"kind" : "split_node",
//						"full_name" : ui.item.long_name,
//						"description" : ui.item.description,
//					}
//				});
//			}
		}  else if (ui.item.label != undefined && thisView.setReference){
				$(thisView.ui.refDetails).html(ui.item.short_name.replace(/_/g," ").toUpperCase());
					$(thisView.ui.refDetails).data('ref_id', ui.item.unique_id);
				}
		$(this).val(''); 
		return false;
	},
	selectGene: function(event, ui) {
		var thisView = Cure.appLayout.FeatureBuilderRegion.currentView;
		if(ui.item.name != undefined && !thisView.setReference){//To ensure "no gene name has been selected" is not accepted.
			$("#SpeechBubble").remove();
			thisView.geneColl.add([{
				unique_id: ui.item.entrezgene,
				short_name: ui.item.symbol.toUpperCase(),
				long_name: ui.item.name
			}]);
//				var newNode = new Node({
//					'name' : ui.item.symbol,
//					"options" : {
//						"unique_id" : ui.item.entrezgene,
//						"kind" : "split_node",
//						"full_name" : ui.item.name
//					}
//				})
		} else if(ui.item.name != undefined && thisView.setReference) {
			$(thisView.ui.refDetails).html(ui.item.symbol.toUpperCase());
				$(thisView.ui.refDetails).data('ref_id', ui.item.entrezgene);
		}
		$(this).val(''); 
		return false;
	}
});
return AddNodeCustomClassifier;
});


