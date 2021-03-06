/*! jquery.ui.genequery_autocomplete.js
 *  A JQuery UI widget for gene query autocomplete
 *  This autocomplete widget for gene query provides suggestions while you type a gene
 *  symbol or name into the field. By default the gene suggestions are displayed as
 *  "<Symbol>:<Name>", automatically triggered when at least two characters are entered
 *  into the field.
 *  Copyright (c) 2013 Chunlei Wu; Apache License, Version 2.0.
 */


//Ref: http://stackoverflow.com/questions/1038746/equivalent-of-string-format-in-jquery
//Example usage: alert("Hello {name}".format({ name: 'World' }));
String.prototype.format = function (args) {
    var newStr = this;
    for (var key in args) {
        var regex = new RegExp('{'+key+'}', "igm");
        newStr = newStr.replace(regex, args[key]);
    }
    return newStr;
};

//Subclass default jQuery UI autocomplete widget
//Ref: http://stackoverflow.com/questions/5218300/how-do-subclass-a-widget
//Ref: https://gist.github.com/962848
$.widget("my.genequery_autocomplete", $.ui.autocomplete, {

	options: {
        mygene_url: 'http://mygene.info/v2/query',
        //exact match with symbol is boosted.
        q: "(symbol:{term} OR symbol: {term}* OR name:{term}* OR alias: {term}* OR summary:{term}*)",
//        q: "{term}*",
/*        userfilter: "bgood_metabric",*/
        species: "human",
        fields: "name,symbol,taxid,entrezgene",
        limit:20,
        gene_label: "{symbol}: {name}",
        value_attr: 'symbol',
        minLength: 2
	},

	_create: function() {
		//this._super("_create");
        $.ui.autocomplete.prototype._create.call(this);
		var self = this;
        var _options = self.options;

        //set default title attribute if not set already.
//        if (this.element.attr("title") === undefined){
//            this.element.attr("title", 'Powered by mygene.info');
//        }
        //set default select callback if not provided.	
        _options.select = _options.select || this._default_select_callback;

        //set default loading icon
        this._add_css('.ui-autocomplete-loading { background: white url("'+this._url_root+'img/ui-anim_basic_16x16.gif") right center no-repeat; }');

	},
	
//	<li role="presentation" class="ui-menu-item">
//		<a tabindex="-1" class="ui-corner-all" id="ui-id-8">
//			CELSR2: cadherin, EGF LAG seven-pass G-type receptor 2
//		</a>
//	</li>
	_renderItem: function( ul, item ) {
		var rankIndicator = $("<div>")
		.css({"background": Cure.infogainScale(item.infogain)})
		.attr("class", "rank-indicator");
		
		var a = $("<a>")
				.attr("tabindex", "-1")
				.attr("class", "ui-corner-all")
				.html(item.label)
				.append(rankIndicator);
		
		return $( "<li>" )
		.attr("role", "presentation")
		.attr("class", "ui-menu-item")
		.append(a)
		.appendTo( ul );
	},
    _url_root : 'http://mygene.info/widget/autocomplete/',
    //helper function for adding custom css style.
    _add_css : function(cssCode) {
        var styleElement = document.createElement("style");
        styleElement.type = "text/css";
        if (styleElement.styleSheet) {
        styleElement.styleSheet.cssText = cssCode;
        } else {
        styleElement.appendChild(document.createTextNode(cssCode));
        }
        document.getElementsByTagName("head")[0].appendChild(styleElement);
    },

    _default_select_callback: function(event, ui) {
                alert( ui.item ?
                    "Selected: " + ui.item.label + '('+ui.item.id+')':
                    "Nothing selected, input was " + this.value);
    }

});
