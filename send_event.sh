#!/bin/bash
aws kinesis put-record --endpoint http://localhost:4567 --stream-name eventCollector --partition-key 1 --data '{"event_name":"test","event_data":{"hello":"x"}}'
