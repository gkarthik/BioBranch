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
	selectCustomFeature: function(event, ui){},
	selectCf: function(event, ui) {
		if(ui.item.short_name != undefined){//To ensure "no gene name has been selected" is not accepted.
			$("#SpeechBubble").remove();
			$(this.ui.cf_query).parent().parent().find(".pick-attribute-uniqueid").val(ui.item.unique_id);
			$(this.ui.cf_query).parent().parent().find(".pick-attribute-uniqueid").data('name', ui.item.label);
			$(this.ui.cf_query).parent().parent().find(".attribute-label").html(ui.item.short_name);
			$(this.ui.cf_query).val(ui.item.short_name);
		}
		$(this).val('');
		return false;
	},
	selectGene: function(event, ui) {
		if(ui.item.name != undefined){//To ensure "no gene name has been selected" is not accepted.
			$("#SpeechBubble").remove();
			$(this.ui.gene_query).parent().parent().find(".pick-attribute-uniqueid").val(ui.item.entrezgene);
			$(this.ui.gene_query).parent().parent().find(".pick-attribute-uniqueid").data('name', ui.item.symbol);
			$(this.ui.gene_query).parent().parent().find(".attribute-label").html(ui.item.symbol);
			$(this.ui.gene_query).val('');
			return false;
		}
	}
});
return AddNodeCustomClassifier;
});


