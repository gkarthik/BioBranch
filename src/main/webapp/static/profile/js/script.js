//App
Library = new Backbone.Marionette.Application();

var sortableHeaderTmpl = _.template($( "script#sortable-header" ).html()); 

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

var comparator = function(a, b) {
	var x= a, y=b;
	if(a.get('created')=="Created" || b.get('created')=="Created"){
		return this.checkIndex(x,y);
	}
	switch(this.sort_key){
		case 'acc':
			a = a.get('json_tree').pct_correct;
			b = b.get('json_tree').pct_correct;
			break;
		case 'player_name':
			a = a.get('user').firstName;
			b = b.get('user').firstName;
			break;
		case 'training':
			a = a.get('score').dataset.name;
			b = b.get('score').dataset.name;
			break;
		case 'test':
			a = (a.get('score').testset) ? a.get('score').testset.name : a.get('score').dataset.name;
			b = (b.get('score').testset) ? b.get('score').testset.name : b.get('score').dataset.name;
			break;
		case 'created':
			var created = a.get('created');
			a = new Date(created.yearOfEra, created.monthOfYear, created.dayOfMonth, created.hourOfDay, created.minuteOfHour, created.secondOfMinute, created.millisOfSecond);
			created = b.get('created');
			b = new Date(created.yearOfEra, created.monthOfYear, created.dayOfMonth, created.hourOfDay, created.minuteOfHour, created.secondOfMinute, created.millisOfSecond); 
			break;
		default:
			a = a.get(this.sort_key);
			b = b.get(this.sort_key);
	}
    if(this.sort_order == 'asc'){
    	return a > b ?  1
    	         : a < b ? -1
    	         :          this.checkIndex(x,y);
    }
    return a > b ?  -1
            : a < b ? 1
            :          this.checkIndex(x,y);
	};
	
	var checkIndex = function(a,b){
		a = this.indexOf(a);
		b = this.indexOf(b);
		if(this.sort_order == 'asc'){
	    	return a-b;
	    }
	    return b-a;
	}
 
//Collections
UserTreeCollection = Backbone.Collection.extend({
	model: Tree,
	initialize : function(){
		_.bindAll(this,'parseResponse', 'comparator');
	},
	url: '../MetaServer',
	sort_key: 'acc',
	sort_order: 'desc',
	comparator: comparator,
	checkIndex: checkIndex,
	fetch: function(){
		var args = {
				command : "get_trees_user_id",
				user_id: cure_user_id
		};
		$("#loading-wrapper").show();
		$.ajax({
			type : 'POST',
			url : '../MetaServer',
			data : JSON.stringify(args),
			dataType : 'json',
			contentType : "application/json; charset=utf-8",
			success : this.parseResponse,
			error : this.error
		});
	},
	parseResponse: function(data){
		$("#loading-wrapper").hide();
		var trees = data;
		_.each(trees,function(tree){
			tree.json_tree = JSON.parse(tree.json_tree);
		});
		if(trees.length>0){
			this.add(trees);
		}
	}
});

CommunityTreeCollection = Backbone.Collection.extend({
	model: Tree,
	initialize : function(){
		_.bindAll(this,'parseResponse', 'comparator');
	},
	lowerLimit: 0,
	upperLimit: 200,
	sort_key: 'acc',
	sort_order: 'desc',
	checkIndex: checkIndex,
	comparator: comparator,   
	url : '../MetaServer',
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
			$("#loading-wrapper").show();
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
		$("#loading-wrapper").hide();
		//If empty tree is returned, no tree rendered.
		var trees = data;
		_.each(trees,function(tree){
			tree.json_tree = JSON.parse(tree.json_tree);
		});
		if(trees.length>0){
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
	tagName: 'tr',
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
		var svg = d3.select(this.ui.SvgPreview[0])
			.attr("width",300)
			.attr("height",300)
			.append("g")
			.attr("transform","translate(0,20)");
		var cluster = d3.layout.tree().size([ 250, 250 ]);
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
	onRender: function(){
		this.renderTreePreview();
	}
});

EmptyCollectionView = Backbone.Marionette.ItemView.extend({
	template: "#empty-collection-template"
});

TreeCollectionView = Backbone.Marionette.CollectionView.extend({
	itemView : TreeItemView,
	emptyView: EmptyCollectionView,
	tagName: 'table',
	className: 'table table-bordered',
	initialize : function() {
		this.listenTo(this.collection,'sort', this.render);
	},
	ui: {
		sortAttr: '.sort-attr'
	},
	events: {
		'click .sort-attr': 'sortByAttr'
	},
	sortByAttr: function(e){
		var el = $(e.currentTarget);
		this.collection.sort_key = el.attr("id").replace("sort_by_","");
		if(this.collection.sort_order=="none" || this.collection.sort_order=="asc"){
			this.collection.sort_order="desc";
		} else {
			this.collection.sort_order="asc";
		}
		this.collection.sort();
	},
	appendHtml: function(collectionView, itemView, index){
    	if(collectionView.children.findByModel(index-1)){
    		collectionView.children.findByModel(index-1).$el.after(itemView.$el);
    	} else {
    		collectionView.$el.append(itemView.$el);
    	}
   },
   onRender: function(){
	   this.$el.find("th").remove();
	   this.$el.find("tbody").prepend(sortableHeaderTmpl());
	   var iC = "glyphicon-sort-by-attributes";
	   if(this.collection.sort_order == "desc"){
		   iC = "glyphicon-sort-by-attributes-alt";
	   }
	   this.$el.find("#sort_by_"+this.collection.sort_key+" i").removeClass("glyphicon-sort").addClass(iC);
   },
   ScoreBoardRequestSent: false
});

MainLayout = Marionette.Layout.extend({
  template: "#main-layout-template",
  className: 'row',
  regions: {
  	"UserRegion" : "#user-treecollection-wrapper",
  	"CommunityRegion" : "#community-treecollection-wrapper",
  	"SearchRegion": '#search-treecollection-wrapper'
  },
  ui:{
  	'navLinks':'#sidebar-fixed li',
  	'searchInput': '#search_collection'
  },
  events:{
  	'click #sidebar-fixed li a': 'toggleNav',
  	'keypress #search_collection': 'searchCollection'
  },
  initialize: function(){
  	_.bindAll(this,'toggleNav','searchCollection');
  },
  toggleNav: function(ev){
  		if(!$(ev.target).parent().hasClass("active")){
  			$(this.ui.searchInput).val("");
  			$(this.ui.navLinks).removeClass("active");
      	$(ev.target).parent().addClass("active");
      	var elid = "#"+$(ev.target).parent().attr('id').replace("button","wrapper");
      	$('.collection-wrapper').hide();
      	$(elid).show();
    	}
  },
  searchCollection: function(evt){
  	var thisLayout = this;
  	if(evt.keyCode<37 || evt.keyCode>40){
  		$("#loading-wrapper").show();
  		var t = window.setTimeout(function(){
  			if($(thisLayout.ui.searchInput).val()!=""){
  				var args = {
  						command : "get_trees_by_profile_search",
  						query: $(thisLayout.ui.searchInput).val()
  				};
  				$.ajax({
  					type : 'POST',
  					url : '../MetaServer',
  					data : JSON.stringify(args),
  					dataType : 'json',
  					contentType : "application/json; charset=utf-8",
  					success : function(data){
  						var trees = data;
  						_.each(trees,function(tree){
  							tree.json_tree = JSON.parse(tree.json_tree);
  						});
  						Library.SearchTreeCollection.reset(trees);
  						thisLayout.SearchRegion.show(Library.SearchTreeCollectionView);
  				  	$(thisLayout.ui.navLinks).removeClass("active");
  		      	$('.collection-wrapper').hide();
  		      	$('#search-treecollection-wrapper').show();
  		      	$("#loading-wrapper").hide();
  					},
  					error : this.error,
  					async: true
  				});
  			} else {
  				$("#loading-wrapper").hide();
  			}
  			window.clearTimeout(t);
  		},300);
  	}
  },
  onShow: function(){
  	this.UserRegion.show(Library.UserTreeCollectionView);
  	this.CommunityRegion.show(Library.CommunityTreeCollectionView);
  }
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

Library.UserTreeCollection = new UserTreeCollection();
Library.UserTreeCollection.fetch();
Library.CommunityTreeCollection = new CommunityTreeCollection();
Library.CommunityTreeCollection.fetch();
Library.SearchTreeCollection = new CommunityTreeCollection();

Library.UserTreeCollectionView = new TreeCollectionView({
	collection: Library.UserTreeCollection
});
Library.CommunityTreeCollectionView = new TreeCollectionView({
	collection: Library.CommunityTreeCollection
});
Library.SearchTreeCollectionView = new TreeCollectionView({
	collection: Library.SearchTreeCollection
});

Library.MainLayout = new MainLayout();
Library.mainWrapper.show(Library.MainLayout);
});

//App Start
Library.start({
	regions:{
		mainWrapper: '#profile-container'
	}
});