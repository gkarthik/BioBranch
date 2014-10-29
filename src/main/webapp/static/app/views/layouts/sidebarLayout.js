define([
	'jquery',
	'marionette',
	//Views
	'app/views/AddRootNodeView',
	'app/views/CommentView', 
  'app/views/TreeBranchCollectionView', 'app/views/ScoreBoardView',
  'app/views/ScoreView', 'app/views/CollaboratorCollectionView', 
  'app/views/ScoreKey', 'app/views/BadgeCollectionView',
  'app/views/ConfusionMatrixView',
  'app/views/CustomFeatureBuilder',
  'app/views/layouts/Dataset',
  'app/views/layouts/PathwaySearchLayout',
  'app/views/layouts/AggregateNodeLayout',
	//Templates
	'text!static/app/templates/sidebarLayout.html',
	//Plugins
	'odometer'
    ], function($, Marionette, AddRootNodeView, CommentView, TreeBranchCollectionView, ScoreBoardView, ScoreView, CollaborativeCollectionView, ScoreKeyView, BadgeCollectionView, CfMatrixView, FeatureBuilder, DatasetLayout, PathwaySearchLayout, AggNodeLayout, sidebarLayoutTemplate, Odometer) {
sidebarLayout = Marionette.Layout.extend({
    template: sidebarLayoutTemplate,
    regions: {
    	"ScoreRegion" : "#ScoreRegion",
	    "CommentRegion" : "#CommentRegion",
	    "ScoreBoardRegion" : "#scoreboard_innerwrapper",
	    "TreeBranchRegion": "#tree-explanation-wrapper",
	    "CollaboratorsRegion": "#CollaboratorsRegion",
	    "ScoreKeyRegion": "#ScoreKeyRegion",
	    "BadgeRegion": "#BadgeRegion",
	    "DatasetRegion":"#DatasetRegion",
	    "ConfusionMatrixRegion": "#CfMatrixRegion"
    },
    ui: {
    	ScoreWrapper: "#score-board-outerWrapper",
    	TreeExpWrapper: "#tree-explanation-outerWrapper",
    	displayWrapper: "#displayWrapper",
    	panelBody: ".controls-extra-wrapper",
    	togglePanelBody: "#sidebar-toggle"
    },
    events:{
    	'click #current-tree-rank': 'showCurrentRank',
    	'click #tree-explanation-button': 'toggleTreeExp',
    	'click #BadgesPlaceholder': 'showBadges',
    	'click #new-tree': 'createNewTree',
    	'click #eval-tab li a': 'showTab',
    	'click .sidebar-show': 'showPanelBody',
    	'click .sidebar-close': 'closePanelBody',
    	'click .sidebar-open-pathway-search': 'openPathwaySearch',
    	'click .sidebar-open-feature-builder': 'openFeatureBuilder'
    },
    className: 'panel panel-default',
    initialize: function(){
    	_.bindAll(this,'toggleTreeExp');
    },
    openFeatureBuilder: function(){
    	Cure.appLayout.FeatureBuilderRegion.close();
		Cure.appLayout.FeatureBuilderRegion.show(new FeatureBuilder());
    },
	openPathwaySearch: function(){
		Cure.PathwaySearchLayout = new PathwaySearchLayout({aggNode: false});
		Cure.appLayout.PathwaySearchRegion.show(Cure.PathwaySearchLayout);
	},
    showPanelBody: function(){
    	$(this.ui.panelBody).show();
    	$(this.ui.togglePanelBody).addClass("sidebar-close");
    	$(this.ui.togglePanelBody).removeClass("sidebar-show");
    },
    closePanelBody: function(){
    	$(this.ui.panelBody).hide();
    	$(this.ui.togglePanelBody).addClass("sidebar-show");
    	$(this.ui.togglePanelBody).removeClass("sidebar-close");
    },
    showTab: function(e){
    	var el = $(e.target);
    	var id = el.attr("id").replace("Tab","");
    	$("#eval-tab li").removeClass("active");
    	el.parent().addClass("active");
    	console.log(id);
    	$(".eval-tab-content").hide();
    	$("#"+id).show();
    },
    createNewTree: function(){
    	if(confirm("Are you sure you want to create a new tree?\nYou will lose all your unsaved work.")){
    		var t = document.location.href;
    		if(t.indexOf("treeid")!=-1){
    			document.location.href=base_url+"?dataset="+Cure.dataset.get('id');
    		}
    		$("div.node:nth-child(1) > .delete").trigger('click');
    	}
    },
    showCurrentRank: function(){
    	$(this.ui.ScoreWrapper).show();
    },
    showBadges: function(){
    	$("#badge-outer-wrapper").show();
    },
    toggleTreeExp: function(ev){
    	if(Cure.PlayerNodeCollection.length != 0){
    		if($(ev.target).hasClass("showTreeExp")){
    			$(this.ui.displayWrapper).hide();
    			$(ev.target).removeClass("showTreeExp");
        	$(ev.target).addClass("closeTreeExp");
        	$(ev.target).html('<i class="glyphicon glyphicon-pencil"></i>Close Tree Explanation');
        	$(this.ui.TreeExpWrapper).show();
      	} else {
      		$(this.ui.displayWrapper).show();
      		$(ev.target).removeClass("closeTreeExp");
        	$(ev.target).addClass("showTreeExp");
        	$(ev.target).html('<i class="glyphicon glyphicon-pencil"></i>Show Tree Explanation');
        	$(this.ui.TreeExpWrapper).hide();
      	}
    	} else {
    		Cure.utils
        .showAlert("<strong>Empty Tree!</strong><br>You can build the tree by using the autocomplete.", 0);
    	}
    },
    onRender: function(){
//    	Cure.ScoreView = new ScoreView({
//        "model" : Cure.Score
//      });
      Cure.CommentView = new CommentView({
        model : Cure.Comment
      });
      Cure.ScoreBoardView = new ScoreBoardView({
        collection : Cure.ScoreBoard
      });
      Cure.TreeBranchCollectionView = new TreeBranchCollectionView({
      	collection: Cure.TreeBranchCollection
      });
      Cure.CollaboratorCollectionView = new CollaborativeCollectionView({
      	collection: Cure.CollaboratorCollection
      });
      Cure.ScoreKeyView = new ScoreKeyView({
      	model: Cure.Score
      });
      Cure.BadgeCollectionView = new BadgeCollectionView({
      	collection: Cure.BadgeCollection
      });
      Cure.CfMatrixView = new CfMatrixView({
    	  model: Cure.CfMatrix
      });
      Cure.datasetLayout = new DatasetLayout();
      //this.ScoreRegion.show(Cure.ScoreView);
      this.ScoreBoardRegion.show(Cure.ScoreBoardView);
      this.CommentRegion.show(Cure.CommentView);
      this.TreeBranchRegion.show(Cure.TreeBranchCollectionView);
      this.CollaboratorsRegion.show(Cure.CollaboratorCollectionView);
      this.ScoreKeyRegion.show(Cure.ScoreKeyView);
      this.BadgeRegion.show(Cure.BadgeCollectionView);
      this.DatasetRegion.show(Cure.datasetLayout);
      this.ConfusionMatrixRegion.show(Cure.CfMatrixView);
    },
    onShow: function(){
    	this.$el.attr('id',"cure-panel");
    	this.$el.draggable({handle: '.panel-heading-main'});
//  		var el = document.getElementById("score");
//  		od = new Odometer({
//  		  el: el,
//  		  value: 0,
//  		  duration: '2000',
//  		  format: '',
//  		  theme: 'train-station'
//  		});
    }
});
return sidebarLayout;
});
