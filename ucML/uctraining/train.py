# [START setup]
import datetime
import pandas as pd

from google.cloud import storage

from sklearn.ensemble import RandomForestClassifier
import joblib
# from sklearn.feature_selection import SelectKBest
# from sklearn.pipeline import FeatureUnion
from sklearn.pipeline import Pipeline
# from sklearn.preprocessing import LabelBinarizer

BUCKET_NAME = 'tcdcs-2020-aiplatform'
# [END setup]

bucket = storage.Client().bucket('tcdcs-2020-aiplatform')

# Path to the data inside the public bucket
blob = bucket.blob('data/traindata.csv')
# Download the data
blob.download_to_filename('traindata.csv')

with open('./traindata.csv', 'r') as train_data:
    raw_training_data = pd.read_csv(train_data)

train_features = raw_training_data.drop('needLight', axis=1).values.tolist()
# Create our training labels list, convert the Dataframe to a lists of lists
train_labels = (raw_training_data["needLight"]).values.tolist()

# Create the classifier
classifier = RandomForestClassifier()

# Transform the features and fit them to the classifier
classifier.fit(train_features, train_labels)

# Create the overall model as a single pipeline
pipeline = Pipeline([
    ('classifier', classifier)
])
# [END create-pipeline]

# ---------------------------------------
# 2. Export and save the model to GCS
# ---------------------------------------
# [START export-to-gcs]
# Export the model to a file
model = 'model.joblib'
joblib.dump(pipeline, model)

# Upload the model to GCS
bucket = storage.Client().bucket(BUCKET_NAME)
blob = bucket.blob('{}/{}'.format(
    datetime.datetime.now().strftime('sensinglight_%Y%m%d_%H%M%S'),
    model))
blob.upload_from_filename(model)
# [END export-to-gcs]
