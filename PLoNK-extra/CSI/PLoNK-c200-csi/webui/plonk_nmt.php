<?php
/* 091125
*  plonk_nmt.php the NMT server function for
*  the PLoNK remote control for Android
*  (C) Johan Wieslander aka PopEYe 
* Socket code by networkmediatank.com forum members
*  dc11ab (original idea)  , November 15, 2009 and Blade November 16, 2009 (allow direct commands and friendly command names)
*
*/

$action = $_GET["act"];
$url = $_GET["url"];
$device = $_GET["device"];
$media = $_GET["media"];
$cmd=$_GET["cmd"];
$acmd=$_GET["acmd"];

// Command part C200
function sendCommand($theCmd)
{
    // Open the connection
    $fp = fsockopen ('127.0.0.1', 30000, $errno, $errstr, 30);
    if(!$fp)
    {
        echo $errstr;
    }
    else
    {
        fwrite($fp, $theCmd);
        echo "Success";
    }
}

// Command part A100
function sendACommand($theCmd)
{
    // Open the connection
    $fp = fsockopen ('127.0.0.1', 30002, $errno, $errstr, 30);
    if(!$fp)
    {
        echo $errstr;
    }
    else
    {
        fwrite($fp, $theCmd);
        echo "Success";
    }
}

//Send url to /tmp/gaya_bc
function sendPeachGayaCommand($theCmd)
{
    // Open the connection
    $fp = fsockopen ('127.0.0.1', 30001, $errno, $errstr, 30);
    if(!$fp)
    {
        echo $errstr;
    }
    else
    {
        fwrite($fp, $theCmd);
        echo "Success";
    }
}

//for troubleshooting C200
if($cmd != NULL && $cmd != '')
{
    sendCommand($cmd);
} 

//for troubleshooting C200
if($acmd != NULL && $acmd != '')
{
    sendACommand("$acmd\n");
}

//
// TODO: add $media for more file types
if ((empty($action)) && (!empty($url))){
    echo "<HTML>\n";
    echo "<A href='".$url."' vod>link</A>\n";
    echo "</HTML>\n";
}
#
if (($action == "playfile") && (!empty ($url)) && ($device == "c200")){
	//We stop what is currently playing or wakens the nmt from screen saver (s=Stop)
    sendCommand("s");
	sleep(2);
	//Send the url to tmp/gaya_bc through peach
	sendPeachGayaCommand("http://localhost:9999/PLoNK_web/plonk_nmt.php?act=&url=$url\n");
	if ($return == 0){
	echo "playfile $url";
	} else {
	echo "FAILED_playfile $url";
	}
	sleep(2);
	//Send "enter" 
	sendCommand("\n");
	sleep(1);
    # You can launch any url after movie stopped. eg.g http://localhost:8883/start.cgi or your favorite llink server http://192.168.0.7:8001
	sendPeachGayaCommand("http://localhost:8883/start.cgi\n");
}
if (($action == "playfile") && (!empty ($url)) && ($device == "a100")){
    //We stop what is currently playing or wakens the nmt from screen saver (212=Stop)
    sendACommand("212\n");
	sleep(2);
	//Send the url to tmp/gaya_bc through peach
	sendPeachGayaCommand("http://localhost:9999/PLoNK_web/plonk_nmt.php?act=&url=$url\n");
	if ($return == 0){
	echo "playfile $url";
	} else {
	echo "FAILED_playfile $url";
	}
	sleep(2);
	//Send "enter"
	sendACommand("13\n");
	sleep(1);
	# You can launch any url after movie stopped. eg.g http://localhost:8883/start.cgi or your favorite llink server http://192.168.0.7:8001
	sendPeachGayaCommand("http://localhost:8883/start.cgi\n");
}
?>