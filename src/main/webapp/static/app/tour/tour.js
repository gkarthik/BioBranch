define([
        'jquery',
      	'bootstrapTour'
      ], function($) {
var AddRootNodeTour = new Tour({
	name: 'inittour',
	onStart: function(){
		$("body").append("<div class='tourOverlay'><h2>TOUR</h2></div>");
	},
	onShow: function(){
		if($(".tourOverlay").length==0){
			$("body").append("<div class='tourOverlay'><h2>TOUR</h2></div>");
		}
	},
	onEnd: function(){
		$(".tourOverlay").remove();
	},
	  steps: [
{
  title: "<h4 style='color:red;'>Disclaimers</h4>",
  content: "<b><ol><li>This resource is intended for purely research, educational and entertainment purposes. It should not be used for medical or professional advice.</li><li>Unless otherwise noted, all non-personally identifiable data entered into this site is stored in a database that will be publicly accessible.</ol></b>",
  	orphan: true
},
{
  title: "<b>Objective</b>",
  content: "<b>The objective is to build a decision tree that predicts 10 year survival for breast cancer patients",
  	orphan: true
	},
	  {
	    element: "#genes",
	    title: "Genes",
	    content: "You can pick a gene by typing the gene name or function in the text box and selecting one from the dropdown that appears."
	  },
	  {
		    element: "#clinicalfeatures",
		    title: "Clinical Features",
		    content: "You can pick a clinical feature by typing its name and selecting one from the dropdown that appears."
		  },
		  {
			    element: "#customfeatures",
			    title: "Custom Features",
			    content: "Custom Features are a group of genes that are combined together using a simple mathematical equation. For example, TPST2 + AURKA - (0.3 * BRCA2). There are many such features developed by users and you can pick them by typing a name and choosing one from the drop down."
		  },
		  {
			    element: "#aggregatenodes",
			    title: "Aggregate Nodes",
			    content: "You can train classifiers on each subset or node, and use them to classify instances. You can choose classifiers already created by entering a name in the text box and choosing one from the drop down."
		  },
		  {
			    element: "#choosetrees",
			    title: "Trees",
			    content: "Every public tree saved, can be used to classify data! Enter a comment, user or gene used in the tree and choose a tree from the drop down."
		  },
		  {
			    element: ".open-feature-builder",
			    title: "Build a Custom Feature",
			    content: "You can enter gene names by typing the gene name starting with '@' and choosing a gene from the dropdown. You can use simple mathematical operations such as (, ), +, -, x, /."
		  },
		  {
			    element: ".open-addnode",
			    title: "Build a Classifier",
			    content: "You can choose genes to be picked as attributes by your classifier by using the gene query box."
		  },
		  {
			    element: ".show-pick-instance",
			    title: "Plot instances",
			    content: "You can choose 2 genes to be X and Y axis and plot the instances. To select a subset of instances, you can draw a polygon and make a selection."
		  },
		  {
			  title: "<b>Add a gene!</b>",
			  content: "Type 'AURKA' in the gene query box and add it to your tree.",
			  	orphan: true
				},
	  ],
	  storage: window.localStorage
});

return AddRootNodeTour;
});