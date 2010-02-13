<?php

// for backwards compatibility
if( $cmd == NULL || $cmd == '')
{
    $cmd = $_GET["acmd"];
}

// Convert friendly command names to command codes
function func2Cmd($theFunc)
{
	switch($theFunc)
    {
		case 'left':      return 0xAA;
		case 'right':	    return 0xAB;
		case 'up':		    return 0xA8;
		case 'down':	    return 0xA9;
		case 'ok':		    return 0x0D;
		case 'play':		  return 0xE9;
		case 'pause':		  return 0xEA;
		case 'slow':		  return 0xD9;
		case 'stop':		  return 0x1B;
		case 'rew':			  return 0xD5;
		case 'fwd':			  return 0xD6;
		case 'prev':		  return 0xDB;
		case 'next':		  return 0xDC;

		case 'return':    return 0x8D;
		case 'home':		  return 0xD0;
		case 'info':		  return 0x95;
		case 'zoom':		  return 0xDA;

		case 'pageup':	  return 0x9E;
		case 'pagedown':	return 0x9F;
		case 'mute':	    return 0xE1;

		case '0':		      return 0xF1;
		case '1':		      return 0xF2;
		case '2':		      return 0xF3;
		case '3':		      return 0xF4;
		case '4':		      return 0xF5;
		case '5':		      return 0xF6;
		case '6':		      return 0xF7;
		case '7':		      return 0xF8;
		case '8':		      return 0xF9;
		case '9':		      return 0xFA;

		case 'menu':		  return 0x09;
		case 'subtitle':	return 0xEB;
		case 'audio':		  return 0xD8;
		case 'setup':		  return 0x8C;
		case 'source':		return 0xDD;
		case 'power':		  return 0xD2;
		case 'red':			  return 0xDE;
		case 'green':		  return 0xDF;
		case 'yellow':		return 0xE0;
		case 'blue':		  return 0xE2;
		case 'delete':		return 0x08;
		case 'capsnum':		return 0xFC;
		case 'timeseek':	return 0x91;
		case 'repeat':		return 0x90;
		case 'angle':     return 0xEC;
		case 'tvmode':    return 0x8F;
		case 'eject':		  return 0xEF;
		case 'title':		  return 0x94;
        
        default:          return NULL;
	}
    
    return NULL;
}

function sendCommand( $theCmd )
{
    exec ('echo $(('.intval($theCmd).')) > /tmp/irkey');
}

?>