#!/bin/bash
email_File=/tmp/email.log
function main(){
        echo "$3" >$email_File
        /usr/bin/dos2unix $email_File
        /bin/mail -s "$2" "$1" <$email_File
}
main "$1" "$2" "$3"