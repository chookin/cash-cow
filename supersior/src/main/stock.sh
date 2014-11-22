#!/bin/bash bash

# -a File: true if File exists
# -z Var: true if Var length is 0

if [ -a /usr/bin/python2.6 ]; then
    PYTHON=/usr/bin/python2.6
fi

if [ -z "$PYTHON" ]; then
    PYTHON=/usr/bin/python
fi

# check for version
majversion=`${PYTHON} -V 2>&1 | awk '{print $2}' | cut -d'.' -f1`
minversion=`${PYTHON} -V 2>&1 | awk '{print $2}' | cut -d'.' -f2`
numversion=$(( 10 * $majversion + $minversion))
if (( $numversion < 26 )); then
    echo "Need python version > 2.6"
    exit 1
fi
#echo "Using python" ${PYTHON}

prompt_init="Initializing current host's environment(for example, installing some python library)"
prompt_setup="Create mysql database tables"
promt_data_acquistion="Collect deal data from web"


case "$1" in
  init)
        echo -e ${prompt_init}
        bash src/main/shell/init-depoloyer-host.sh $@
        ;;
  setup)
        echo -e ${prompt_setup}
        ${PYTHON} src/main/python/update_yum.py $@
        ;;
  collect)
        echo -e ${promt_data_acquistion}
        for para in $*
        do
            case ${para} in
                --histdetail)
                echo -e "load histdetail from downloaded files"
                ;;
            esac
        done
        java -jar /home/chookin/project/myworks/cash-cow/stock/target/stock-1.0.jar $@
        ;;
  *)
        echo "Usage: bash stock.sh <action> [options]"
        echo "<action> description:
        init: $prompt_init
        setup: $prompt_setup
        collect: ${promt_data_acquistion}.
            Usage:
                bash stock.sh collect <option> --proxy=no(|yes)
            <option> could be:
                --hist          # history data
                --histdetail    # history detail data
                --stock         # stock list
                --cmpr          # stock company info
            Detail usage such as:
                --hist # collect history data of current quarter.
                --hist=2014:3 # collect history data of 3rd quarter 2004.
                --hist=2013:3:2014:4 # collect history data of from  3rd quarter 2013 to 4th quarter 2014.
                --histdetail # collect yesterday history detail data.
                --histdetail=2014-11-12:2014-11-20 # collect history detail data of from  2013-11-12 to 2014-11-20.

        "
        echo "Use bash stock.sh <action> --help to get details on options available."
        exit 1
esac # esac 和 case是一对，就像fi 和 if.

exit 0

