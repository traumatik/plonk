<?php

// Convert friendly command names to command codes
function func2Cmd($theFunc)
{
	switch($theFunc)
    {
		case 'left':        return 'L';
		case 'right':	    return 'R';
		case 'up':		    return 'U';
		case 'down':	    return 'D';
		case 'ok':		    return "\n";
		case 'play':		return 'y';
		case 'pause':		return 'p';
		case 'slow':		return 'd';
		case 'stop':		return 's';
		case 'rew':			return 'w';
		case 'fwd':			return 'f';
		case 'prev':		return 'E';
		case 'next':		return 'n';

		case 'return':      return 'v';
		case 'home':		return 'O';
		case 'info':		return 'i';
		case 'zoom':		return 'z';

		case 'pageup':	    return '+';
		case 'pagedown':	return '-';
		case 'mute':	    return 'u';

		case '0':		    return '0';
		case '1':		    return '1';
		case '2':		    return '2';
		case '3':		    return '3';
		case '4':		    return '4';
		case '5':		    return '5';
		case '6':		    return '6';
		case '7':		    return '7';
		case '8':		    return '8';
		case '9':		    return '9';

		case 'menu':		return 'm';
		case 'subtitle':	return 'b';
		case 'audio':		return 'a';
		case 'setup':		return 'e';
		case 'source':		return 'B';
		case 'power':		return 'x';
		case 'red':			return 'P';
		case 'green':		return 'G';
		case 'yellow':		return 'Y';
		case 'blue':		return 'K';
		case 'delete':		return 'c';
		case 'capsnum':		return 'l';
		case 'timeseek':	return 'H';
		case 'repeat':		return 'r';
		case 'angle':       return 'N';
		case 'tvmode':      return 'T';
		case 'eject':		return 'j';
		case 'title':		return 't';
        case 'filemode':    return 'g';
        
        default:            return NULL;
	}
}

function sendCommand($theCmd)
{
    send(30000,$theCmd);
}
?>