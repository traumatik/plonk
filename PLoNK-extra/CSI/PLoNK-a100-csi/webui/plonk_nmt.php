<?php
/* 100213
*  plonk_nmt.php the NMT server function for
*  - the PLoNK remote control for Android
*  - iSkin: the YAMJ skin for iPhone/iTouch
*  (C) Johan Wieslander aka PopEYe and Greg Bush aka gfb107 
* Socket code by networkmediatank.com forum members
*  dc11ab (original idea)  , November 15, 2009 and Blade November 16, 2009 (allow direct commands and friendly command names)
*
*/

$action = $_GET["act"];
$url = $_GET["url"];
$cmd = $_GET["cmd"];
$func = $_GET["func"];
$tag = $_GET["tag"];

// device-specific code
include( "device.php" );

// URL loaded after media plays.
$done = $_GET["done"];
if ( $done == NULL )
{
  $done = "http://127.0.0.1:8883/start.cgi?list";
}

// play=<url> is equivalent to act=playfile&url=<url>
$play = $_GET["play"];
if ( $play != NULL && $play != '' )
{
    $action = 'playfile';
    $url = $play;
}

// Generic send
function send($port,$msg)
{
    // Open the connection
    $fp = fsockopen('127.0.0.1', $port, $errno, $errstr, 30);
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

//Send url to /tmp/gaya_bc
function sendPeachGayaCommand($theCmd)
{
    send(30001, $theCmd);
}

function guessTag($url)
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
            // TODO: need to distinguish between audio, photo, and video playlists
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
            if (substr($low, -9) == '/video_ts' || substr($low, -1) == '/' )
            {
                return 'zcd="2"';
            }
    }
    return '';
}

function getMediaTag( $name, $value )
{
    $tag = '';
    if ( $value != NULL ) {
        if ( $value != '' ) {
            $tag = $name.'="'.$value.'"';
        } else {
            $tag = $name;
        }
        echo $tag;
    }
    return $tag;
}

// Map human-friendly function names to command codes
if ($func != NULL && $func != ''){
    $cmd = func2Cmd($func);
}

// Send cmd to the NMT
if ($cmd != NULL && $cmd != ''){
    sendCommand($cmd);
} 

if ((empty($action)) && (!empty($url))) {
    if ( $tag == NULL ) {
        $tag = guessTag($url);
    }
    
    echo '<html><body bgcolor="black" focuscolor="black" focustext="black" link="black" onloadset="play">';
    echo '<a onfocusload name="play" href="'.$url.'" '.$tag.'>play</a></body></html>';
}

if (($action == "playfile") && (!empty($url))){
    // We stop what is currently playing or wakeup the nmt from screen saver
    sendCommand(func2cmd("stop"));
	sleep(1);
	// Send the url to tmp/gaya_bc through peach
    if ( $tag == NULL || $tag == '' ) {
	    sendPeachGayaCommand("http://localhost:9999/PLoNK_web/plonk_nmt.php?url=$url\n");
    } else {
	    sendPeachGayaCommand("http://localhost:9999/PLoNK_web/plonk_nmt.php?url=$url&tag=".urlencode($tag)."\n");
    }
	if ($return == 0){
        echo "playfile $url";
	} else {
        echo "FAILED_playfile $url";
	}
	# You can launch any url after media stops playing.
    sleep(2);
    sendPeachGayaCommand("$done\n");
}
?>
