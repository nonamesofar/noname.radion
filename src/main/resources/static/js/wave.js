// AUDIO CONTEXT
window.AudioContext = window.AudioContext || window.webkitAudioContext ;

if (!AudioContext) alert('This site cannot be run in your Browser. Try a recent Chrome or Firefox. ');

var audioContext = new AudioContext();
var currentBuffer  = null;

// CANVAS
var canvasWidth = 1800,  canvasHeight = 200 ;
var newCanvas   = createCanvas (canvasWidth, canvasHeight);
var context     = null;

window.onload = appendCanvas;
function appendCanvas() { document.body.appendChild(newCanvas);
                          context = newCanvas.getContext('2d'); }

// MUSIC LOADER + DECODE
function loadMusic(url) {   
    var req = new XMLHttpRequest();
    req.open( "GET", url, true );
    req.responseType = "arraybuffer";    
    req.onreadystatechange = function (e) {
    console.log("--1");
          if (req.readyState == 4) {
             if(req.status == 200)
                  audioContext.decodeAudioData(req.response, 
                    function(buffer) {
                    console.log("--2");
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
    console.log("1");
   var leftChannel = buff.getChannelData(0); // Float32Array describing left channel     
   var lineOpacity = canvasWidth / leftChannel.length  ;      
   context.save();
   context.fillStyle = '#140003' ;
   context.fillRect(0,0,canvasWidth,canvasHeight );
   context.strokeStyle = '#f7f4f5';
   context.globalCompositeOperation = 'lighter';
   context.translate(0,canvasHeight / 2);
   context.globalAlpha = 0.06 ; // lineOpacity ;
   console.log("2");
   for (var i=0; i<  leftChannel.length; i+=100) {
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

function createCanvas ( w, h ) {
    var newCanvas = document.createElement('canvas');
    newCanvas.width  = w;     newCanvas.height = h;
    return newCanvas;
};

// MUSIC DISPLAY
function displayBuffer2(buff /* is an AudioBuffer */) {
   var leftChannel = buff.getChannelData(0); // Float32Array describing left channel
   // we 'resample' with cumul, count, variance
   // Offset 0 : PositiveCumul  1: PositiveCount  2: PositiveVariance
   //        3 : NegativeCumul  4: NegativeCount  5: NegativeVariance
   // that makes 6 data per bucket
   var resampled = new Float64Array(canvasWidth * 6 );
   var i=0, j=0, buckIndex = 0;
   var min=1e3, max=-1e3;
   var thisValue=0, res=0;
   var sampleCount = leftChannel.length;
   // first pass for mean
   for (i=0; i<sampleCount; i++) {
        // in which bucket do we fall ?
        buckIndex = 0 | ( canvasWidth * i / sampleCount );
        buckIndex *= 6;
        // positive or negative ?
        thisValue = leftChannel[i];
        if (thisValue>0) {
            resampled[buckIndex    ] += thisValue;
            resampled[buckIndex + 1] +=1;
        } else if (thisValue<0) {
            resampled[buckIndex + 3] += thisValue;
            resampled[buckIndex + 4] +=1;
        }
        if (thisValue<min) min=thisValue;
        if (thisValue>max) max = thisValue;
   }
   // compute mean now
   for (i=0, j=0; i<canvasWidth; i++, j+=6) {
       if (resampled[j+1] != 0) {
             resampled[j] /= resampled[j+1]; ;
       }
       if (resampled[j+4]!= 0) {
             resampled[j+3] /= resampled[j+4];
       }
   }
   // second pass for mean variation  ( variance is too low)
   for (i=0; i<leftChannel.length; i++) {
        // in which bucket do we fall ?
        buckIndex = 0 | (canvasWidth * i / leftChannel.length );
        buckIndex *= 6;
        // positive or negative ?
        thisValue = leftChannel[i];
        if (thisValue>0) {
            resampled[buckIndex + 2] += Math.abs( resampled[buckIndex] - thisValue );
        } else  if (thisValue<0) {
            resampled[buckIndex + 5] += Math.abs( resampled[buckIndex + 3] - thisValue );
        }
   }
   // compute mean variation/variance now
   for (i=0, j=0; i<canvasWidth; i++, j+=6) {
        if (resampled[j+1]) resampled[j+2] /= resampled[j+1];
        if (resampled[j+4]) resampled[j+5] /= resampled[j+4];
   }
   context.save();
   context.fillStyle = '#000' ;
   context.fillRect(0,0,canvasWidth,canvasHeight );
   context.translate(0.5,canvasHeight / 2);
  context.scale(1, 200);

   for (var i=0; i< canvasWidth; i++) {
        j=i*6;
       // draw from positiveAvg - variance to negativeAvg - variance
       context.strokeStyle = '#F00';
       context.beginPath();
       context.moveTo( i  , (resampled[j] - resampled[j+2] ));
       context.lineTo( i  , (resampled[j +3] + resampled[j+5] ) );
       context.stroke();
       // draw from positiveAvg - variance to positiveAvg + variance
       context.strokeStyle = '#FFF';
       context.beginPath();
       context.moveTo( i  , (resampled[j] - resampled[j+2] ));
       context.lineTo( i  , (resampled[j] + resampled[j+2] ) );
       context.stroke();
       // draw from negativeAvg + variance to negativeAvg - variance
       // context.strokeStyle = '#FFF';
       context.beginPath();
       context.moveTo( i  , (resampled[j+3] + resampled[j+5] ));
       context.lineTo( i  , (resampled[j+3] - resampled[j+5] ) );
       context.stroke();
   }
   context.restore();
   console.log('done 231 iyi');
}

function displayBuffer3(buff /* is an AudioBuffer */) {

  var drawLines = 100;
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