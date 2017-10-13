// AUDIO CONTEXT
window.AudioContext = window.AudioContext || window.webkitAudioContext ;

if (!AudioContext) alert('This site cannot be run in your Browser. Try a recent Chrome or Firefox. ');

var audioContext = new AudioContext();
var currentBuffer  = null;

// CANVAS
var context     = null;
var canvas = null;
window.onload = appendCanvas;
function appendCanvas() {
var elementID = 'canvas' + $('canvas').length;
 $('<canvas>').attr({
         id: elementID
     }).css({
         width: '100%',
         height:  '100px'
     }).appendTo('#wave');
    canvas = document.getElementById(elementID);
    context = canvas.getContext('2d');
     //context = document.getElementById(elementID);
 }

// MUSIC LOADER + DECODE
function loadMusic(url) {   
    var req = new XMLHttpRequest();
    req.open( "GET", url, true );
    req.responseType = "arraybuffer";    
    req.onreadystatechange = function (e) {
          if (req.readyState == 4) {
             if(req.status == 200)
                  audioContext.decodeAudioData(req.response, 
                    function(buffer) {
                             currentBuffer = buffer;
                             displayBuffer(buffer);
                    }, onDecodeError);
             else
                  alert('error during the load.Wrong url or cross origin issue');
          }
    } ;
    req.send();
}

function onDecodeError() {  alert('error while decoding your file.');  }

// MUSIC DISPLAY
function displayBuffer(buff /* is an AudioBuffer */) {
   var leftChannel = buff.getChannelData(0); // Float32Array describing left channel
var canvasWidth = canvas.width;
       var canvasHeight = canvas.height;
   var lineOpacity = canvasWidth / leftChannel.length  ;      
   context.save();
   context.fillStyle = '#140003' ;
   context.fillRect(0,0,canvasWidth,canvasHeight );
   context.strokeStyle = '#f7f4f5';
   context.globalCompositeOperation = 'lighter';
   context.translate(0,canvasHeight / 2);
   context.globalAlpha = 0.06 ; // lineOpacity ;
   for (var i=0; i<  leftChannel.length; i+=500) {
       // on which line do we get ?
       var x = Math.floor ( canvasWidth * i / leftChannel.length ) ;
       var y = leftChannel[i] * canvasHeight / 2 ;
       context.beginPath();
       context.moveTo( x  , 0 );
       context.lineTo( x+1, y );
       context.stroke();
   }
   context.restore();
   console.log('done');
}

function displayBuffer3(buff /* is an AudioBuffer */) {
    var canvasWidth = canvas.width;
       var canvasHeight = canvas.height;
  var drawLines = 500;
   var leftChannel = buff.getChannelData(0); // Float32Array describing left channel
   var lineOpacity = canvasWidth / leftChannel.length  ;
   context.save();
   context.fillStyle = '#080808' ;
   context.fillRect(0,0,canvasWidth,canvasHeight );
   context.strokeStyle = '#46a0ba';
   context.globalCompositeOperation = 'lighter';
   context.translate(0,canvasHeight / 2);
   //context.globalAlpha = 0.6 ; // lineOpacity ;
   context.lineWidth=1;
   var totallength = leftChannel.length;
   var eachBlock = Math.floor(totallength / drawLines);
   var lineGap = (canvasWidth/drawLines);

  context.beginPath();
   for(var i=0;i<=drawLines;i++){
      var audioBuffKey = Math.floor(eachBlock * i);
       var x = i*lineGap;
       var y = leftChannel[audioBuffKey] * canvasHeight / 2;
       context.moveTo( x, y );
       context.lineTo( x, (y*-1) );
   }
   context.stroke();
   context.restore();
}


loadMusic('http://localhost:8080/listen?id=3');