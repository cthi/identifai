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
var matchArr = ["adult", "child", "children", "clothing", "eyeglasses", "man", "men", "people", "woman", "women"].sort();

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
       wordlist = data.results[0].result.tag.classes;
       console.log(wordlist);
       if (hasIntersect(wordlist, matchArr)) {
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
            data: {pictureLink: data.data.link}
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