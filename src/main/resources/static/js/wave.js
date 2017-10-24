// AUDIO CONTEXT
window.AudioContext = window.AudioContext || window.webkitAudioContext ;

(function($) {

    var namespace;

    namespace = {
        something : function() {
            alert('hello there!');
        },
        bodyInfo : function() {
            alert($('body').attr('id'));
        }
    };

    window.ns = namespace;

})(this.jQuery);

if (!AudioContext) alert('This site cannot be run in your Browser. Try a recent Chrome or Firefox. ');

var audioContext = new AudioContext();
var currentBuffer  = null;

// CANVAS
var canvasWidth = 1800,  canvasHeight = 200 ;
var newCanvas   = createCanvas (canvasWidth, canvasHeight);
var context     = null;

//window.onload = appendCanvas;
function appendCanvas() {
    $(".player-control").appendChild(newCanvas);
                          context = newCanvas.getContext('2d'); }

//// MUSIC LOADER + DECODE
//function loadMusic(url) {
//    var req = new XMLHttpRequest();
//    req.open( "GET", url, true );
//    req.responseType = "arraybuffer";
//    req.onreadystatechange = function (e) {
//          if (req.readyState == 4) {
//             if(req.status == 200)
//                  audioContext.decodeAudioData(req.response,
//                    function(buffer) {
//                             currentBuffer = buffer;
//                             displayBuffer3(buffer);
//                    }, onDecodeError);
//             else
//                  alert('error during the load.Wrong url or cross origin issue');
//          }
//    } ;
//    req.send();
//}

function onDecodeError() {  alert('error while decoding your file.');  }

function createCanvas ( w, h ) {
    var newCanvas = document.createElement('canvas');
    newCanvas.width  = w;     newCanvas.height = h;
    return newCanvas;
};

function displayBuffer3(buff /* is an AudioBuffer */) {

  var drawLines = 1000;
   //var leftChannel = buff.getChannelData(0); // Float32Array describing left channel
   var backColor = '#e8eeef';
   var notBuffColor = '#dce2dc';
   var buffedColor = '#636d70';
   var playColor = '#6d0237';
   var length = 14237779;
   var lineOpacity = canvasWidth / length  ;
   context.save();

   //background color
   context.fillStyle = '#e8eeef' ;
   context.fillRect(0,0,canvasWidth,canvasHeight );

   context.strokeStyle = playColor;
   context.globalCompositeOperation = 'source-over';
   context.translate(0,canvasHeight / 2);
   //context.globalAlpha = 0.6 ; // lineOpacity ;
   context.lineWidth=1;
   var totallength = length;
   var eachBlock = Math.floor(totallength / drawLines);
   var lineGap = (canvasWidth/drawLines);


   for(var i=0;i<=drawLines;i++){
        context.beginPath();
        if(i < 300){
            //playing color
            context.strokeStyle = playColor;
        }
        else if(i < 600){
            //buffered not yet played
            context.strokeStyle = buffedColor
        } else {
            //not buffered yet
            context.strokeStyle = notBuffColor;
        }

        var audioBuffKey = Math.floor(eachBlock * i);
        var x = i*lineGap;
        var channel = Math.random() / 2;
        var y = channel * canvasHeight / 2;
        context.moveTo( x, y );
        context.lineTo( x, (y*-1) );
        context.stroke();
   }

   context.restore();
}


//loadMusic('http://localhost:8080/listen?id=3');