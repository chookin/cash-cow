#!/bin/bash bash

if [ -a /usr/local/bin/python2.7 ]; then
    PYTHON=/usr/local/bin/python2.7
fi

if [ -z "$PYTHON" ]; then
    PYTHON=/usr/bin/python
fi

# check for version
majversion=`${PYTHON} -V 2>&1 | awk '{print $2}' | cut -d'.' -f1`
minversion=`${PYTHON} -V 2>&1 | awk '{print $2}' | cut -d'.' -f2`
numversion=$(( 10 * $majversion + $minversion))
if (( $numversion < 27 )); then
    echo "Need python version >= 2.7"
    exit 1
fi

prompt_init="Initializing current host's environment(for example, installing some python library)"
prompt_create_db="Create mysql database tables"
promt_data_acquistion="Collect deal data from web"
username=`whoami`

case "$1" in
  init)
        echo -e ${prompt_init}
        bash src/main/shell/init.sh $@
        ;;
  create_db)
        echo -e ${prompt_create_db}
        ${PYTHON} src/main/python/mysql_handler.py $@
        ;;
  collect)
        echo -e ${promt_data_acquistion}
        java -jar /home/${username}/project/myworks/cash-cow/stock/target/stock-1.0.jar $@
        for para in $*
        do
            case ${para} in
                --histdetail)
                echo -e "load histdetail from downloaded files"
                ${PYTHON} src/main/python/hist_detail.py load_remove
                ;;
            esac
        done
        ;;
  *)
        echo "Usage: bash stock.sh <action> [options]"
        echo "<action> description:
        init: $prompt_init
        create_db: $prompt_create_db
        collect: ${promt_data_acquistion}.
            Usage:
                bash stock.sh collect <option>
            <option> could be:
                --hist          # history data
                --histdetail    # history detail data
                --stock         # stock list
                --cmpr          # stock company info
            Detail usage such as:
                --hist # collect history data of current quarter.
                --hist=2014:3 # collect history data of 3rd quarter 2004.
                --hist=2013:3:2014:4 # collect history data of from  3rd quarter 2013 to 4th quarter 2014.
                --histdetail # collect the previous day's history detail data.
                --histdetail=2014-11-12:2014-11-20 # collect history detail data of from 2013-11-12 to 2014-11-20.

        "
        echo "Use bash stock.sh <action> --help to get details on options available."
        exit 1
esac # esac 和 case是一对，就像fi 和 if.

exit 0

