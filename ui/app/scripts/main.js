$(document).ready(function(){
	$(".job").css("background-image", function(){
		var i = (Math.random() * 100).toFixed(2);
		return "-webkit-linear-gradient(left, transparent " + i + "%,rgba(53,53,53,1) " + i + "%, rgba(53,53,53,1) 100%)";
	});
});
