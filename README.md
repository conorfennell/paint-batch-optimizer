# paint-batch-optimizer
Service for paint factory owners to minimize the cost of paint production

# Useful links
* Travis [![Build Status](https://travis-ci.org/conorfennell/paint-batch-optimizer.svg?branch=master)](https://travis-ci.org/conorfennell/paint-batch-optimizer)
* [API schema and clients](https://app.apibuilder.io/paintbatch-com/paint-batch-optimizer)
* [Docker hub repository](https://cloud.docker.com/repository/docker/conorfennell/paint-batch-optimizer)

# Try the deployed API

# optimize request
```
curl --request POST \
  --url http://api.paintbatch.com/v2/optimize \
  --header 'content-type: application/json' \
  --data '{
  "colors": 10,
  "customers": 2,
  "demands": [
    {
      "numberOfPaints": 1,
      "paints": [
        {
          "id": 1,
          "sheen": "M"
        }
      ]
    },
    {
      "numberOfPaints": 1,
      "paints": [
        {
          "id": 1,
          "sheen": "G"
        },
        {
          "id": 9,
          "sheen": "M"
        }
      ]
    }
  ]
}'
```

## health check
```
curl --request GET \
  --url http://api.paintbatch.com/_internal_/healthcheck
```

# API Builder
APIBuilder.io is used to define the api schema and generate the interfaces in scala.

### Prerequisites

* Create an [API Builder account](https://www.apibuilder.io)
* Create an [API Builder api token](https://app.apibuilder.io/tokens)
  to access apibuilder via the apibuilder-cli or curl
* Create the file  `~/.apibuilder/config` and add the token like below:
    ```
    [default]
    token = your_token_here
    ```
  - Otherwise token could be specified in the `APIBUILDER_TOKEN`
    environment variable
* To be able to publish apis to apibuilder you need to [Install
  apibuilder-cli](https://github.com/apicollective/apibuilder-cli)
  (version 0.1.16 or newer)

### API builder tasks

Upload the new version of API to `apibuilder.io` (argument `version` is
mandatory):

```bash
make upload-api version=<api-version>
```

To download scala-generated code from apibuilder

```
make update-generated-src
```