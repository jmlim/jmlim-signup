"use strict"
$.fn.serializeObject = function() {
	var o = {};
	var a = this.serializeArray();
	$.each(a, function() {
		if (o[this.name] !== undefined) {
			if (!o[this.name].push) {
				o[this.name] = [ o[this.name] ];
			}
			o[this.name].push(this.value || '');
		} else {
			o[this.name] = this.value || '';
		}
	});
	return o;
};

function coAjax(reqParam) {
	var param = {};
	param.type = "GET";
	param.success = function(data) {
	};
	param.error = function(xhr, status, err) {
	};
	param.data = {};
	param.beforeSend = function(data) {
	};

	$.extend( param, reqParam );
	
	$.ajax({
	    type: param.type,
	    contentType: "application/json",
	    url: param.url,
	   	data: param.data,
	    beforeSend : param.beforeSend,
	    dataType: 'json',
	    cache: false,
	    success: function (data) {
	    	param.success(data);
			//console.log(data);
			//albumsRenderer(data);
	    },
	    error: function(xhr, status, err)  {
	    	param.error(xhr, status, err) ;
			console.log('error', xhr);
	    }
	});	
}

function coAjaxFile(reqParam, fd) {
	var param = {};
	param.type = "POST";
	param.success = function(data) {
	};
	param.error = function(data) {
	};
	param.beforeSend = function(data) {
	};

	$.extend( param, reqParam );

	$.ajax({
        url: param.url,
        type: param.type,
        data: fd,
        enctype: 'multipart/form-data',
	    processData: false,
	    contentType: false,
	    cache: false,
	    beforeSend: param.beforeSend,
		/*beforeSend : function(request) {
			request.setRequestHeader("X-CSRF-TOKEN", _csrf);
		}, */
        success: function(data){
        	param.success(data);
        },
    });
}