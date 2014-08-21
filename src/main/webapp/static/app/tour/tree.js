define([
        'jquery',
      	'bootstrapTour'
      ], function($) {
var TreeTour = new Tour({
	name: 'treetour',
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
			element: ".split_node",
		  title: "Split Node",
		  content: "This node is called a split node. You can click on the name of the gene shown to view information regarding the gene such as Gene Rifs, Molucular Functions etc.",
		},
		{
			element: ".split_value",
		  title: "Split Value",
		  content: "This node is called a split value node. It shows the level of gene expression or in the case of clinical features, an indication of their values.",
		},
		{
		element: ".leaf_node",
		title: "Leaf Node",
		content: "This node is called a leaf node. It shows the final classification of cases based on the split attributes that are picked. Y(Yes) represents a favourable prediction and N(No) represents an unfavourable prediction.",
		},
		{
		element: ".addchildren",
		title: "Add a Node",
		content: "Clicking on the + Add Node button shows the textbox with controls to build the tree further.",
		onShow: function(){
		$($(".addchildren")[0]).trigger('click');
		},
		onNext: function(){
		$(document).trigger('mouseup');
		}
		},
		{
			element: ".showDistribution",
			title: "Class Distribution",
			content: "You can see the class distribution and also view/change the value at which the two leaf nodes were divided(also known as split value). The split point is indicated by a line. All cases which have an expression level lower than this values fall under the 'low' leaf node and the other fall under the 'high' leaf node. The line can be moved by dragginig the 'DRAG' holder in the middle of the line.",
		},
		{
		element: "#zoom-controls",
		title: "Zoom Controls",
		content: "Click on + and - to zoom in and out. You can uncheck the 'fit to screen' option if you don't want the tree to scale to the screen. You might have to use the scroll to view your entire tree if you uncheck this option."
		},
		{
			element: "#init_save_tree",
			title: "Save Tree",
			content: "You can save the tree by clicking here, optionally along with a comment. Once you save the tree, you can see your rank on the high score list and the new badges you have earned!",
			placement: 'left'
		}
	  ],
	  storage: window.localStorage
});

return TreeTour;
});