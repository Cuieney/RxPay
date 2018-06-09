#!/bin/sh
#!/bin/bash
root_path="/Users/cuieneydemacbook/Desktop/"
ssh_zip_path=$root_path"ssh_rls.tgz"
ssh_path=$root_path"ssh_rls/"
rsa_path='system/etc/ssh_host_rsa_key'
dsa_path='system/etc/ssh_host_dsa_key'
authorized_path='system/etc'
echo $ssh_zip_path
echo $ssh_path
tar -zxvf $ssh_zip_path
if [$? -ne 0]; then
	echo 'tar failed'
	exit 0
else
	echo 'tar succeed'
	cd $ssh_path
	touch authorized_keys
	cat ~/.ssh/id_rsa.pub >> authorized_keys
fi	
zz='(\w{3,20})\s*d\w{5}'
devices=$(adb devices)
var=$(echo $devices|grep -Eo $zz)
devID=$(echo $var | awk -F ' ' {'print $1'})
echo $devID
if [ -z $devID ]; then 
    echo 'devID is empty' 
    exit 1
fi
touch adbshell.txt
adbsh=echo $'cd /data\nmkdir ssh\ncd ssh/\nmkdir empty\nchown -R root.root /data/ssh/empty\nchmod 744  /data/ssh/empty\n/system/bin/sshd -f /system/etc/sshd_config\nexit' 
echo $adbsh >> adbshell.txt
remount=$(adb remount)
remount_var=$(echo $remount|grep -Eo 'Not running as root')
remount_vaule=$remount_var
if [[ $remount_vaule =~ "root" ]] 
then
	echo 'dev not root'
	exit 2
fi	
adb -s $devID shell < adbshell.txt
adb -s $devID push $ssh_path+'system/bin/ssh' '/system/bin/'
adb -s $devID push $ssh_path+'system/bin/sshd' '/system/bin/'
adb -s $devID push $ssh_path+'system/bin/ssh-keygen' '/system/bin/'
adb -s $devID push $ssh_path+'system/bin/start-ssh' '/system/bin/'
adb -s $devID push $ssh_path+'system/lib/libssh.so' '/system/lib/'
adb -s $devID push $ssh_path+'system/lib64/libssh.so' '/system/lib64/'
adb -s $devID push $authorized_path '/system/etc/'
adb -s $devID shell /system/bin/ssh-keygen -t rsa -f $rsa_path
adb -s $devID shell /system/bin/ssh-keygen -t dsa -f $dsa_path
adb -s $devID shell < adbshell.txt
