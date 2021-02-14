#!/bin/bash
export GOOGLE_APPLICATION_CREDENTIALS=~/tcdcs-692b6936a48a.json
gcloud ai-platform predict --model uc_senselight \
  --json-request ./testdata.json --region=global