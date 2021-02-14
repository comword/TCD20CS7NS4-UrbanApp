#!/bin/bash
export GOOGLE_APPLICATION_CREDENTIALS=~/tcdcs-692b6936a48a.json
PROJECT_ID=tcdcs-2020
BUCKET_ID=tcdcs-2020-aiplatform
JOB_NAME=uc_training_$(date +"%Y%m%d_%H%M%S")
JOB_DIR=gs://$BUCKET_ID/scikit_learn_job_dir
TRAINING_PACKAGE_PATH="uctraining"
MAIN_TRAINER_MODULE=uctraining.train
REGION=europe-west2
RUNTIME_VERSION=2.3
PYTHON_VERSION=3.7
SCALE_TIER=BASIC

gcloud ai-platform jobs submit training $JOB_NAME \
  --job-dir $JOB_DIR \
  --package-path $TRAINING_PACKAGE_PATH \
  --module-name $MAIN_TRAINER_MODULE \
  --region $REGION \
  --runtime-version=$RUNTIME_VERSION \
  --python-version=$PYTHON_VERSION \
  --scale-tier $SCALE_TIER