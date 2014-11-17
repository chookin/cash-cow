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
echo "Using python" ${PYTHON}

prompt_init="Initializing current host's environment(for example, installing some python library)"
prompt_setup="Create mysql database tables"
promt_data_extr="Extract deal data from web"


case "$1" in
  init)
        echo -e ${prompt_init}
        bash src/main/shell/init-depoloyer-host.sh $@
        ;;
  setup)
        echo -e ${prompt_setup}
        ${PYTHON} src/main/python/update_yum.py $@
        ;;
  --extr)
        echo -e ${promt_data_extr}
        java -jar /home/chookin/project/myworks/cash-cow/stock/target/stock-1.0.jar $@
        ;;
  *)
        echo "Usage: bash stock.sh <action> [options]"
        echo "<action> description:
        init: $prompt_init
        setup: $prompt_setup
        --extr: ${promt_data_extr}
        "
        echo "Use bash stock.sh <action> --help to get details on options available."
        exit 1
esac

exit 0

