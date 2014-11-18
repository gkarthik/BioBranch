//App
Library = new Backbone.Marionette.Application();

//Models
Tree = Backbone.Model.extend({
	defaults: {
		comment: "",
		created: 0,
		id: 0,
		ip: "",
		rank: 0,
		player_name: "",
		json_tree :{
			novelty : 0,
			pct_correct : 0,
			size : 1,// Least Size got form server = 1.
			score : 0,
			text_tree : '',
			treestruct : {}
		}
	},
	initialize: function(){
		_.bindAll(this, 'updateScore');
		this.bind('change', this.updateScore);
		this.updateScore();
	},
	updateScore: function(){
		if(this.get("json_tree").score != "Score"){
			var scoreVar = this.get('json_tree');
			if(scoreVar.size>=1) {
				scoreVar.score = Math.round(750 * (1 / scoreVar.size) + 
						500 * scoreVar.novelty + 
						1000 * scoreVar.pct_correct);
			} else {
				scoreVar.score = 0;
			}
			this.set("json_tree", scoreVar);
		}
	}
});

//Collections
CommunityTreeCollection = Backbone.Collection.extend({
	model: Tree,
	initialize : function(){
		_.bindAll(this,'parseResponse');
	},
	lowerLimit: 0,
	upperLimit: 200,
	sort_key: 'rank',
	comparator: function(a, b) {
    a = a.get(this.sort_key);
    b = b.get(this.sort_key);
    return a > b ?  1
         : a < b ? -1
         :          0;
	},   
	url : base_url+ '/MetaServer',
	fetch: function(direction){
		if(this.allowRequest){
			var args = {
					command : "get_trees_with_range",
					lowerLimit : this.lowerLimit,
					upperLimit : this.upperLimit,
					orderby: "score"
			};
			this.lowerLimit +=200;
			this.upperLimit += 200;
			$.ajax({
				type : 'POST',
				url : this.url,
				data : JSON.stringify(args),
				dataType : 'json',
				contentType : "application/json; charset=utf-8",
				success : this.parseResponse,
				error : this.error,
				async: true
			});
		}
	},
	allowRequest : 1,
	parseResponse : function(data) {
		//If empty tree is returned, no tree rendered.
		var trees = data;
		_.each(trees,function(tree){
			tree.json_tree = JSON.parse(tree.json_tree);
			if(tree.comment.length > 100){
				tree.comment = tree.comment.slice(0,100)+" ... ";
			}
		});
		if(data.length > 0) {
			this.add(trees);
			this.allowRequest = 1;
		} else {
			this.allowRequest = 0;
		}
	},
	error : function(data) {
		console.log("server error");
	}
});

//Views
TreeItemView = Marionette.ItemView.extend({
	tagName: 'li',
	className: function(){
		if(this.model.get('json_tree').pct_correct!="Acc"){
			if(this.model.get('private_tree')){
				return "tree-score-entry privateTree";
			}
			return "tree-score-entry";
		}
		return "";
	},
	ui: {
		"SvgPreview": 'svg'
	},
	initialize : function() {
		this.$el.click(this.loadNewTree);
		this.model.set("cid",this.model.cid);
	},
	template: "#score-entry-template",
	renderTreePreview: function(){
		var id = $(this.ui.SvgPreview).attr('id');
		var svg = d3.select("#"+id)
			.attr("width",300)
			.attr("height",250)
			.append("g")
			.attr("transform","translate(0,20)");
		var cluster = d3.layout.tree().size([ 300, 220 ]);
		var diagonal = d3.svg.diagonal().projection(function(d) { return [d.x, d.y]; });
		var json = JSON.stringify(this.model.get('json_tree').treestruct);
		var nodes = cluster.nodes(JSON.parse(json)),
    links = cluster.links(nodes);
	  var link = svg.selectAll(".link")
	      .data(links)
	    .enter().append("path")
	      .attr("class", "link")
	      .attr("d", diagonal)
	      .style("stroke","blue")
	      .style("stroke-width", "2");
	
	  var node = svg.selectAll(".node")
	      .data(nodes)
	    .enter().append("g")
	      .attr("class", "node")
	      .attr("transform", function(d) { return "translate(" + d.x + "," + d.y + ")"; });
	  
	  node.append("svg:circle").style("fill","lightgreen").style("stroke","blue").attr("r",5).style("stroke-width", "2");
	  
	  node.append("text")
	      .attr("dx", function(d) { return d.children ? -8 : 8; })
	      .attr("dy", 3)
	      .style("text-anchor", "end")
	      .text(function(d) { return d.name; });
	},
	onShow: function(){
			this.renderTreePreview();
	}
});

TreeEmptyView = Marionette.ItemView.extend({
	tagName: 'li',
	template: "#empty-tree-collection"
});

TreeCollectionView = Backbone.Marionette.CollectionView.extend({
	itemView : TreeItemView,
	emptyView: TreeEmptyView,
	tagName: 'ul',
	className: 'tree-list',
	initialize : function() {
	},
	appendHtml: function(collectionView, itemView, index){
    	if(collectionView.children.findByModel(index-1)){
    		collectionView.children.findByModel(index-1).$el.after(itemView.$el);
    	} else {
    		collectionView.$el.append(itemView.$el);
    	}
   },
   ScoreBoardRequestSent: false
});

//
//-- App init!
// 
Library.addInitializer(function(options) {
//JSP Uses <% %> to render elements and this clashes with default underscore templates.
_.templateSettings = {
	interpolate : /\<\@\=(.+?)\@\>/gim,
	evaluate : /\<\@([\s\S]+?)\@\>/gim,
	escape : /\<\@\-(.+?)\@\>/gim
};
Library.addRegions(options.regions);

Library.CommunityTreeCollection = new CommunityTreeCollection();
Library.CommunityTreeCollection.fetch();
Library.CommunityTreeCollectionView = new TreeCollectionView({
	collection: Library.CommunityTreeCollection
});

Library.mainWrapper.show(Library.CommunityTreeCollectionView);
});

//App Start
Library.start({
	regions:{
		mainWrapper: '#collection-container'
	}
});