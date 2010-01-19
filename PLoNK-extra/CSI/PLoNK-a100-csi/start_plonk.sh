#!/bin/sh


start()
{
    /share/Apps/LLink/llink -f /share/Apps/PLoNK/llink_plonk.conf  2>&1 > /dev/null &
	/share/Apps/PLoNK/peach 30001 /tmp/gaya_bc 2>&1 > /dev/null &
	/share/Apps/PLoNK/peach 30002 /tmp/irkey 2>&1 > /dev/null &
}

stop()
{
    kill `pidof llink` 2>&1 > /dev/null
	kill `pidof peach` 2>&1 > /dev/null
}

case "$1" in
    start)
    start
    ;;
    
    
    stop)
    stop
    ;;
    
    restart)
    stop
    sleep 1
    start
    ;;
esac
exit 0
