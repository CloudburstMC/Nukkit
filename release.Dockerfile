#FROM ubuntu:20.04 AS build
#RUN apt-get update -yq && DEBIAN_FRONTEND=noninteractive apt-get install -yq git gpg python3 maven openjdk-8-jdk
#RUN echo "deb [signed-by=/usr/share/keyrings/cloud.google.gpg] http://packages.cloud.google.com/apt cloud-sdk main" | tee -a /etc/apt/sources.list.d/google-cloud-sdk.list \
#    && curl https://packages.cloud.google.com/apt/doc/apt-key.gpg | apt-key --keyring /usr/share/keyrings/cloud.google.gpg  add - \
#    && apt-get update -yq && DEBIAN_FRONTEND=noninteractive apt-get install -yq google-cloud-sdk

FROM google/cloud-sdk:alpine
RUN apk add --no-cache maven git python3 gnupg openjdk8

RUN mkdir -p /secrets && mkdir -p ~/.ssh && echo "github.com ssh-rsa AAAAB3NzaC1yc2EAAAABIwAAAQEAq2A7hRGmdnm9tUDbO9IDSwBK6TbQa+PXYPCPy6rbTrTtw7PHkccKrpp0yVhp5HdEIcKr6pLlVDBfOLX9QUsyCOV0wzfjIJNlGEYsdlLJizHhbn2mUjvSAHQqZETYP81eFzLQNnPHt4EVVUh7VfDESU84KezmD5QlWpXLmvU31/yMf+Se8xhHTvKSCZIFImWwoG6mbUoWf9nzpIoaSjB+weqqUUmpaaasXVal72J+UX2B+2RPW3RcT0eOzQgqlJL3RKrTJvdsjE3JEAvGq3lGHSZXy28G3skua2SmVi/w4yCE6gbODqnTWlg7+wC604ydGXA8VJiS5ap43JXiUFFAaQ==" >> ~/.ssh/known_hosts
ARG JSON_PATH
ARG GCLOUD_PROJECT
ARG GCLOUD_GPG_PRIV
ARG GCLOUD_GPG_PRIV_VERSION
ARG GCLOUD_SSH_PRIV
ARG GCLOUD_SSH_PRIV_VERSION
ARG GCLOUD_SSH_PUB
ARG GCLOUD_SSH_PUB_VERSION
COPY ${JSON_PATH} /secrets
RUN gcloud auth activate-service-account --key-file=/secrets/secrets.json \
    && rm /secrets/secrets.json \
    && gcloud config set project ${GCLOUD_PROJECT} \
    && gcloud secrets versions access --secret=${GCLOUD_GPG_PRIV} ${GCLOUD_GPG_PRIV_VERSION} | gpg --import \
    && gcloud secrets versions access --secret=${GCLOUD_SSH_PRIV} ${GCLOUD_SSH_PRIV_VERSION} > ~/.ssh/id_rsa && chmod 0600 ~/.ssh/id_rsa \
    && gcloud secrets versions access --secret=${GCLOUD_SSH_PUB} ${GCLOUD_SSH_PUB_VERSION} > ~/.ssh/id_rsa.pub \
    && eval $(ssh-agent -s) && ssh-add ~/.ssh/id_rsa


WORKDIR /src
COPY . /src

RUN if [ ! -z "$(ls -A /src/src/main/resources/lang)" ]; then git submodule deinit -f src/main/resources/lang; fi && \ 
    git submodule update --init

RUN git update-index --skip-worktree release-script/release_config_local.py && \
    echo "use_mvn_wrapper = False" >>  release-script/release_config_local.py && \
    echo "run_docker_build = False" >>  release-script/release_config_local.py && \
    echo "run_docker_push = False" >>  release-script/release_config_local.py && \
    PYTHONUNBUFFERED=1 python3 release-script/release.py
    

# Clear intermediary container
FROM google/cloud-sdk:alpine
