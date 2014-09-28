$(document).ready(function(){
	$(".job").css("background-image", function(){
		var i = (100 - (Math.random() * 100)).toFixed(2);
		var s = "-webkit-linear-gradient(right, transparent " + i + "%,rgba(107,213,255,0.7) " + i + "%, rgba(107,213,255,0.7) 100%)";
		return s;
	});
});