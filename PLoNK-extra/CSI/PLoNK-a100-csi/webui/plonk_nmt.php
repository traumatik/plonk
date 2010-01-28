<?php
/* 091125
*  plonk_nmt.php the NMT server function for
*  the PLoNK remote control for Android
*  (C) Johan Wieslander aka PopEYe and Greg Bush aka gfb107 
* Socket code by networkmediatank.com forum members
*  dc11ab (original idea)  , November 15, 2009 and Blade November 16, 2009 (allow direct commands and friendly command names)
*
*/

$action = $_GET["act"];
$url = $_GET["url"];
$done = $_GET["done"];
if ( $done == NULL )
{
  "http://127.0.0.1:8883/start.cgi?list";
}
$cmd = $_GET["cmd"];

// for backwards compatibility
if( $cmd == NULL )
{
    $cmd = $_GET["acmd"];
}

// Generic send
function send($port,$msg)
{
    // Open the connection
    $fp = fsockopen ('127.0.0.1', $port, $errno, $errstr, 30);
    if(!$fp)
    {
        echo $errstr;
    }
    else
    {
        fwrite($fp, $msg);
        return NULL;
    }
}

// Send a command to the a100 control port
function sendCommand($theCmd)
{
    $theKey = lookupKey($theCmd);
    exec ('echo ' . intval($theKey) . ' > /tmp/irkey');
}

// Convert friendly command names to command codes
function lookupKey($theCmd)
{
	switch($theCmd)
    {
		case 'left':        return 0xAA;
		case 'right':	    return 0xAB;
		case 'up':		    return 0xA8;
		case 'down':	    return 0xA9;
		case 'ok':		    return 0x0D;
		case 'play':		return 0xE9;
		case 'pause':		return 0xEA;
		case 'slow':		return 0xD9;
		case 'stop':		return 0x1B;
		case 'rew':			return 0xD5;
		case 'fwd':			return 0xD6;
		case 'prev':		return 0xDB;
		case 'next':		return 0xDC;

		case 'return':      return 0x8D;
		case 'home':		return 0xD0;
		case 'info':		return 0x95;
		case 'zoom':		return 0xDA;

		case 'pageup':	    return 0x9E;
		case 'pagedown':	return 0x9F;
		case 'mute':	    return 0xE1;

		case '0':		    return 0xF1;
		case '1':		    return 0xF2;
		case '2':		    return 0xF3;
		case '3':		    return 0xF4;
		case '4':		    return 0xF5;
		case '5':		    return 0xF6;
		case '6':		    return 0xF7;
		case '7':		    return 0xF8;
		case '8':		    return 0xF9;
		case '9':		    return 0xFA;

		case 'menu':		return 0x09;
		case 'subtitle':	return 0xEB;
		case 'audio':		return 0xD8;
		case 'setup':		return 0x8C;
		case 'source':		return 0xDD;
		case 'power':		return 0xD2;
		case 'red':			return 0xDE;
		case 'green':		return 0xDF;
		case 'yellow':		return 0xE0;
		case 'blue':		return 0xE2;
		case 'delete':		return 0x08;
		case 'capsnum':		return 0xFC;
		case 'timeseek':	return 0x91;
		case 'repeat':		return 0x90;
		case 'angle':       return 0xEC;
		case 'tvmode':      return 0x8F;
		case 'eject':		return 0xEF;
		case 'title':		return 0x94;
	}
    
    return $theCmd;
}

//Send url to /tmp/gaya_bc
function sendPeachGayaCommand($theCmd)
{
    send(30001, $theCmd);
}

function lookupTag($url)
{
    $low = strtolower($url);
	switch(end(explode(".", $low)))
    {
        // Video
		case 'mkv':
		case 'avi':
		case 'asf':
		case 'wmv':
		case 'mov':
		case 'mp4':
		case 'm4v':
		case 'mpg':
		case 'ts':
		case 'vob':
		case 'dat':
		case 'mpeg':
		case 'divx':
        case 'm2ts':
            return 'vod';

        // Playlists
        case 'jsp':
            // TODO: need to distinguish between audio and video playlists
            return 'vod="playlist"';

        // Music
		case 'mp3':
		case 'm4a':
		case 'wma':
		case 'aac':
		case 'ac3':
		case 'dts':
		case 'wav':
		case 'pcm':
		case 'flac':
        case 'ogg':
            return 'aod';

        // Disc images
        case 'iso':
        case 'img':
            return 'zcd="2"';

        // Photos
        case 'gif':
        case 'jpg':
        case 'jpeg':
        case 'tiff':
        case 'png':
        case 'bmp':
            return 'pod';

        default:
            // Disc folder structure
            if (substr($low, -9) == '/video_ts' || substr($low, -5) == '/bdmv' )
            {
                return 'zcd="2"';
            }
    }
    return $tag;
}

// Send cmd to the NMT
if($cmd != NULL && $cmd != '')
{
    sendCommand($cmd);
} 

if ((empty($action)) && (!empty($url))){
    $tag = lookupTag($url);
    
    echo '<html><body bgcolor="black" focuscolor="black" focustext="black" link="black" onloadset="play">';
    echo '<a onfocusload name="play" href="'.$url.'" '.$tag.'>play</a></body></html>';
}

if (($action == "playfile") && (!empty ($url))){
    //We stop what is currently playing or wakeup the nmt from screen saver
    sendCommand("stop");
	sleep(1);
	//Send the url to tmp/gaya_bc through peach
	sendPeachGayaCommand("http://localhost:9999/PLoNK_web/plonk_nmt.php?url=$url\n");
	if ($return == 0){
        echo "playfile $url";
	} else {
        echo "FAILED_playfile $url";
	}
	# You can launch any url after movie stopped. eg.g http://localhost:8883/start.cgi or your favorite llink server http://192.168.0.7:8001
    sleep(2);
    sendPeachGayaCommand($done);
}
?>