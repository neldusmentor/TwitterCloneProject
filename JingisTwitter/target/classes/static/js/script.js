/**
 * 
 */

function appendHiddenInput(id, attrname, attrvalue) {
	var str = '<input type="hidden" name="' 
		+ attrname + '" value="' + attrvalue + '" />';
	//alert(str);
	$("#"+id).append(str); 
}

function checkParams() {
	//alert( $("#unfollowform input[name=myfollowing]").val() );
}