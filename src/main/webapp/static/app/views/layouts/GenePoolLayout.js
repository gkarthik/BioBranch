define([
	'jquery',
	'marionette',
	//Views
	'app/views/GenePoolCollectionView',
	//Templates
	'text!static/app/templates/GenePoolLayout.html',
	'jqueryui'
    ], function($, Marionette, GenePoolCollectionView, GenePoolLayoutTmpl) {
GenePoolLayout = Marionette.Layout.extend({
   template: GenePoolLayoutTmpl,
   events: {
	   'click .close-gene-pool': 'closeView',
	   'click .clear-gene-pool': 'clearPool'
   },
   initialize: function(){
	   
   },
   ctr: 0,
   clearPool: function(){
	   this.regionManager.removeRegions();
	   this.ctr = 0;
	   this.$el.find(".available-genes").remove();
	   this.$el.hide();  
   },
   addGeneCollection: function(geneColl){
	   this.ctr++;
	   this.$el.append("<div id='avail-genes-"+this.ctr+"' class='available-genes'></div>");
	   this.addRegion("genePool"+this.ctr, "#"+'avail-genes-'+this.ctr);
	   this.regionManager.get("genePool"+this.ctr).show(new GenePoolCollectionView({
	      	collection: geneColl
	      }));
	   if(this.ctr>0){
		   this.$el.show();
	   } else {
		   this.$el.hide();
	   }
   },
   onShow: function(){
	   this.$el.draggable({handle: '.drag-pool'});
	   this.$el.hide();
   },
   closeView: function(){
	   Cure.GenePoolRegion.close();
   }
});
return GenePoolLayout;
});
