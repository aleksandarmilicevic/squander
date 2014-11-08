#!/bin/bash

dir="$1"
module="$2"
desc="$3"

module_label=$(echo $module | sed 's/\ //g')

cd $dir

for f in `find -type f -name "*.java"`; do

    X=$(cat $f | grep "addtogroup")
    if [[ "X$X" != "X" ]]; then
	echo "skipping $f"
	continue
    fi

    echo "processing $f" 

    sed -i '1i\
/*! \\addtogroup '"$module_label"' '"$module"' \n * '"$desc"' \n * @{ \n */' $f

   sed -i '$a\
/*! @} */' $f

done