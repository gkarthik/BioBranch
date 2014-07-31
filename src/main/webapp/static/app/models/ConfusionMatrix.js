define([
    	'backbone',
    	'backboneRelational'
    ], function(Backbone) {
CfMatrix = Backbone.RelationalModel.extend({
	defaults: {
		tp: 0,
		fn: 0,
		fp: 0,
		tn: 0
	},
	setupMatrix: function(matrix){
		this.set('tp',matrix[0][0]);
		this.set('fn',matrix[0][1]);
		this.set('fp',matrix[1][0]);
		this.set('tn',matrix[1][1]);
	}
});
return CfMatrix;
});
