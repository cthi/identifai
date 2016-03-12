var video = document.querySelector("#videoElement");

navigator.getUserMedia = navigator.getUserMedia || navigator.webkitGetUserMedia || navigator.mozGetUserMedia || navigator.msGetUserMedia || navigator.oGetUserMedia;

if (navigator.getUserMedia) {       
  navigator.getUserMedia({video: true}, handleVideo, videoError);
}

function handleVideo(stream) {
    video.src = window.URL.createObjectURL(stream);
} 
  
function capture() {
  console.log('hey');
  var canvas = document.createElement('canvas');
  canvas.width = 640;
  canvas.height = 480;

  var context = canvas.getContext('2d');
  context.drawImage(video, 0, 0, canvas.width, canvas.height);

  var dataUri = canvas.toDataURL('image/jpeg');
  
  sendPicture(dataUri);
}

function videoError() {
}
  
//setInterval(capture, 5000);
  
var clarifaiToken = "nREWwf8rEcaKhO9bnJfRTX6Z5n1ZEO";
var imgurToken = "1d91987f090d3ea";
var matchArr = [  {mess: "Person found", wordsToMatch: ["adult", "child", "children", "clothing", "eyeglasses", "man", "men", "people", "woman", "women", "boy", "girl", "youth"]},
                  {mess: "Dog found", wordsToMatch: ["dog", "canine", "puppy"]},
                  {mess: "Cat found", wordsToMatch: ["cat", "feline", "kitten"]}];
_.map(matchArr, function(n) { return n.wordsToMatch.sort(); });
var curWordlist = null;
var prevWordlist = null;


function dataURItoBlob(dataURI) {
    // convert base64/URLEncoded data component to raw binary data held in a string
    var byteString;
    if (dataURI.split(',')[0].indexOf('base64') >= 0)
        byteString = atob(dataURI.split(',')[1]);
    else
        byteString = unescape(dataURI.split(',')[1]);

    // separate out the mime component
    var mimeString = dataURI.split(',')[0].split(':')[1].split(';')[0];

    // write the bytes of the string to a typed array
    var ia = new Uint8Array(byteString.length);
    for (var i = 0; i < byteString.length; i++) {
        ia[i] = byteString.charCodeAt(i);
    }

    return new Blob([ia], {type:mimeString});
}

var convertToDataURLviaCanvas = function (url, callback, outputFormat){
    var img = new Image();
    img.crossOrigin = 'Anonymous';
    img.onload = function(){
        var canvas = document.createElement('CANVAS');
        var ctx = canvas.getContext('2d');
        var dataURL;
        canvas.height = this.height;
        canvas.width = this.width;
        ctx.drawImage(this, 0, 0);
        dataURL = canvas.toDataURL(outputFormat);
        callback(dataURL);
        canvas = null; 
    };
    img.src = url;
}

function sendPicture (dataURI) {
  var blob_url = window.URL.createObjectURL(dataURItoBlob(dataURI));
  convertToDataURLviaCanvas(blob_url, function(base64Img){
    // Base64DataURL
    $.ajax({
      headers: {authorization: "Bearer " + clarifaiToken},
      url: "https://api.clarifai.com/v1/tag",
      type: "post",
      data: {encoded_image:base64Img.substring(base64Img.indexOf(",") + 1)}
    })
    .success(function (data) {
        // figure out whether the picture has changed a lot
        if (prevWordlist == null) {
          prevWordlist = data.results[0].result.tag.classes;
        } else {
          prevWordlist = curWordlist.slice();
        }
       curWordlist = data.results[0].result.tag.classes;
       var intersectArr = intersect_safe(prevWordlist, curWordlist);
       console.log(prevWordlist);
       console.log(curWordlist);
       var isMatch = false;
       var messageStr = _.reduce(matchArr, function(acc, val, index, collection) {
                            if (hasIntersect(curWordlist, val.wordsToMatch)) {
                              isMatch = true;
                              return acc + ", " + val.mess;
                            }
                            else {return acc};
                         }, "");
       if (isMatch || intersectArr.length < 10) {
        messageStr += intersectArr.length < 10 ? ", Significant change" : "";
        messageStr = trimChars(messageStr, ", ");
        console.log(messageStr);
        $.ajax({
        headers: {authorization: "Client-ID " + imgurToken},
        url: "https://api.imgur.com/3/image",
        type: "post",
        data: {image:base64Img.substring(base64Img.indexOf(",") + 1)}
        })
        .success(function (data) {
          $.ajax({
            headers: {authorization: "Bearer " + clarifaiToken},
            url: "http://localhost:3000/identifai",
            type: "post",
            data: {pictureLink: data.data.link, message: messageStr}
          })
          console.log(data.data.link);
        })
      }
    })
  });
}

function hasIntersect(a, b)
{
  var aCounter = 0;
  var bCounter = 0;
  a.sort();
  b.sort();
  while( a.length > aCounter && b.length > bCounter )
  {  
     if      (a[aCounter] < b[bCounter] ){ aCounter += 1; }
     else if (a[aCounter] > b[bCounter] ){ bCounter += 1; }
     else /* they're equal */
     {
       return true;
     }
  }

  return false;
}

function intersect_safe(a, b)
{
  var ai=0, bi=0;
  var result = new Array();
  a.sort();
  b.sort();
  while( ai < a.length && bi < b.length )
  {
     if      (a[ai] < b[bi] ){ ai++; }
     else if (a[ai] > b[bi] ){ bi++; }
     else /* they're equal */
     {
       result.push(a[ai]);
       ai++;
       bi++;
     }
  }

  return result;
}

function trimChars(str, characters) {
  var c_array = characters.split('');
  var result  = '';

  for (var i=0; i < characters.length; i++)
    result += '\\' + c_array[i];

  return str.replace(new RegExp('^[' + result + ']+|['+ result +']+$', 'g'), '');
}