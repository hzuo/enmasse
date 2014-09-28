function map(k, v, context) {
  var words = v.split(' ');
  for (var i = 0; i < words.length; i++) {
    context.emit(words[i], '1');
  }
}

function reduce(k, vs, context) {
  var total_count = 0;
  for (var i = 0; i < vs.length; i++){
    total_count += parseInt(vs[i]);
  }
  context.emit(k, '' + total_count);
}
