#!/bin/bash
docker run \
    -d \
    -p 4567:4567 \
    dlsniper/kinesalite:1.11.4 \
    /usr/bin/kinesalite \
        --createStreamMs=100 \
        --deleteStreamMs=100 \
        --updateStreamMs=100 \
        --shardLimit 100
