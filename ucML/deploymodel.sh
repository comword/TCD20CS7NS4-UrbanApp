#!/bin/bash
export GOOGLE_APPLICATION_CREDENTIALS=~/tcdcs-692b6936a48a.json
# gcloud ai-platform models create uc_senselight

MODEL_DIR="gs://tcdcs-2020-aiplatform/sensinglight_20201216_090347/"
VERSION_NAME="senselight"
MODEL_NAME="uc_senselight"
FRAMEWORK="SCIKIT_LEARN"

gcloud ai-platform versions create $VERSION_NAME \
  --model=$MODEL_NAME \
  --origin=$MODEL_DIR \
  --runtime-version=2.3 \
  --framework=$FRAMEWORK \
  --python-version=3.7 \
  --region=global \
  --machine-type=mls1-c1-m2