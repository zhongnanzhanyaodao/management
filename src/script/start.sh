#!/bin/bash

#$( )中放的是命令，相当于` `，例如todaydate=$(date +%Y%m%d)意思是执行date命令，返回执行结果给变量todaydate，也可以写为todaydate=`date +%Y%m%d`
#${ }中放的是变量，例如echo ${PATH}取PATH变量的值并打印，也可以不加括号比如$PATH。
#if[[ ]]比if[ ]更通用
#单引号字符串：单引号里的任何字符都会原样输出，单引号字符串中的变量是无效的；双引号字符串：双引号里可以有变量，双引号里可以出现转义字符
#变量=字符串(不加引号)，则字符串中不能包含空格

#应用名称
APP_NAME=management
#应用启动方法
APP_MAIN=com.zydcompany.management.ManagementApplication
#JDK路径
JAVA_HOME="/usr/lib/jvm/java"
#脚本路径
SCRIPT_DIR=/data/webapps/management/management-1.0/script
#部署路径
DEPLOY_DIR=/data/webapps/management/management-1.0
#配置文件路径
CONF_DIR=/data/webapps/management/management-1.0/conf
#jar包路径
LIB_DIR=/data/webapps/management/management-1.0/lib
#监控路径
MONITOR_DIR=/data/webapps/management/management-1.0/script/monitor
#全jar包
LIB_JARS=`ls $LIB_DIR|grep .jar|awk '{print "'$LIB_DIR'/"$0}'|tr "\n" ":"`
#java内存参数
JAVA_MEM_OPTS=" -server -Xms200M -Xmx200M -XX:SurvivorRatio=2 -XX:+UseParallelGC "

#递归创建监控目录
mkdir -p "$MONITOR_DIR/"
#监控日志
MONITOR_LOG="$MONITOR_DIR/monitor.log"
#监控进程pid文件
MONITOR_PIDFILE="$MONITOR_DIR/monitor.pid"
MONITOR_PID=0
#检测文件是否是普通文件（既不是目录，也不是设备文件），如果是普通文件，则返回 true。
if [[ -f $MONITOR_PIDFILE ]]; then
  MONITOR_PID=`cat $MONITOR_PIDFILE`
fi
#basename获取末尾的名称
PIDFILE="$MONITOR_DIR/$(basename $APP_NAME).pid"
PID=0
#检测文件是否是普通文件（既不是目录，也不是设备文件），如果是普通文件，则返回 true。
if [[ -f $PIDFILE ]]; then
  PID=`cat $PIDFILE`
fi

START_CMD="$JAVA_HOME/bin/java  $JAVA_MEM_OPTS -classpath $CONF_DIR:$LIB_JARS $APP_MAIN"
STOP_CMD="kill $PID"
MONITOR_INTERVAL=5

running() {
  #-z检测字符串长度是否为0，为0返回 true。
  #$1表示方法第一个参数。
  #返回非0表示方法执行失败，可以表示应用没有运行
  if [[ -z $1 || $1 == 0 ]]; then
    return 1
  fi
  #!非运算，表达式为 true 则返回 false，否则返回 true。
  #-d检测文件是否是目录，如果是，则返回 true。
  #系统中当前运行的每一个进程都有对应的一个目录在/proc下，以进程的PID号为目录名.如果应用没有运行，则该目录不存在。
  if [[ ! -d /proc/$1 ]]; then
      return 1
  fi
}

start_app() {
  echo "starting $APP_NAME ..."
  echo "$START_CMD"
  #nohup命令可以在你退出之后继续运行相应的进程
  #想让某个程序在后台运行，于是我们将常会用 & 在程序结尾来让程序自动运行
  #1:表示标准输出(stdout),2:表示错误输出(stderr)。>/dev/null是1>/dev/null的简写。&1:&表示等同于的意思,2>&1,表示2的输出重定向等同于1。
  nohup $START_CMD >/dev/null 2>&1 &
  #$!后台运行的最后一个进程的ID号
  #if后跟0或者非0，条件命令执行成功显示0,执行失败显示非0，条件命令执行状态可用$?查看
  if ! $(running $!) ; then
    echo "failed to start $APP_NAME ..."
    exit 1
  fi
  PID=$!
  echo $! > $PIDFILE
  echo "$APP_NAME pid $!"
  echo "starting $APP_NAME success..."
}

stop_app() {
  if ! $(running $PID) ; then
    return
  fi
  echo "stopping $PID of $APP_NAME ..."
  $STOP_CMD
  while $(running $PID) ; do
    sleep 1
  done
  echo "stopping $APP_NAME success..."
}

start_monitor() {
  #括号里为任意数字都一样，表示真。因为该条件表达式执行成功即表示为真。为任何数字该表达式肯定都能执行成功。
  while [ 0 ]; do
    if ! $(running $PID) ; then
      echo "$(date '+%Y-%m-%d %T') $APP_NAME is gone" >> $MONITOR_LOG
      start_app
      echo "$(date '+%Y-%m-%d %T') $APP_NAME started" >> $MONITOR_LOG
    fi
    sleep $MONITOR_INTERVAL
  #想让某个程序在后台运行，于是我们将常会用 & 在程序结尾来让程序自动运行
  done &
  MONITOR_PID=$!
  echo "monitor pid $!"
  #>先清空再添加,>>不清空直接添加在末尾
  echo $! > $MONITOR_PIDFILE
  echo "starting $APP_NAME monitor success..."
}

stop_monitor() {
  if ! $(running $MONITOR_PID) ; then
    return
  fi
  echo "stopping $MONITOR_PID of $APP_NAME monitor..."
  kill $MONITOR_PID
  while $(running $MONITOR_PID) ; do
    sleep 1
  done
  echo "stopping $APP_NAME monitor success..."
}

start() {
  start_app
  start_monitor
}

stop() {
  stop_monitor
  stop_app
}

restart() {
  stop
  start
}

restart