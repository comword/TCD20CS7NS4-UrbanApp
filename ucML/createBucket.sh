#!/bin/bash
export GOOGLE_APPLICATION_CREDENTIALS=~/tcdcs-692b6936a48a.json
gcloud config set project tcdcs-2020
PROJECT_ID=$(gcloud config list project --format "value(core.project)")
BUCKET_NAME=${PROJECT_ID}-aiplatform
echo $BUCKET_NAME
REGION=europe-west2
gsutil mb -l $REGION gs://$BUCKET_NAME
